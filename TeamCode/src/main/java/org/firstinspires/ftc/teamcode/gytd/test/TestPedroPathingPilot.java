package org.firstinspires.ftc.teamcode.gytd.test;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Matrix;
import com.pedropathing.paths.Path;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.revhub.localizers.TwoWheelConfig;
import com.pedropathing.revhub.localizers.TwoWheelLocalizer;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.gytd.hardware.HardwareConstants;
import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

/**
 * Phase 3 pilot OpMode for Pedro pathing integration.
 * This test runs a short scripted sequence in an isolated test flow.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 *
 * Controls:
 * - pre-start: gamepad1 b selects Pedro, gamepad1 x selects Legacy
 * - gamepad1 a: start the scripted pilot path
 * - gamepad1 x: abort path and stop all drive motors
 * - gamepad1 y: reset yaw heading
 *
 * Safety notes:
 * - Put robot on blocks for first run
 * - Start at low speed and keep clear area around robot
 * - Use X immediately if motion is not expected
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "Test: Pedro Pathing Pilot", group = "Test")
public class TestPedroPathingPilot extends LinearOpMode {
    private enum PilotState {
        IDLE,
        FORWARD,
        STRAFE,
        TURN,
        COMPLETE,
        ABORTED
    }

    private static final double FORWARD_POWER = 0.35;
    private static final double STRAFE_POWER = 0.35;
    private static final double TURN_POWER = 0.35;

    private static final double FORWARD_TIME_S = 1.20;
    private static final double STRAFE_TIME_S = 1.00;
    private static final double TURN_TIME_S = 0.80;

    // First real Pedro path in inches/radians.
    private static final Pose START_POSE = Pose.zero();
    private static final Pose FORWARD_END_POSE = new Pose(18.0, 0.0, 0.0);
    private static final Pose STRAFE_END_POSE = new Pose(18.0, 12.0, 0.0);
    private static final Pose TURN_END_POSE = new Pose(18.10, 12.0, Math.toRadians(35.0));

    private PilotState state = PilotState.IDLE;
    private double stateStartTime = 0.0;
    private PilotState previousState = PilotState.IDLE;

    private double pilotStartTime = 0.0;
    private double targetHeading = 0.0;

    private double forwardActualTimeS = 0.0;
    private double strafeActualTimeS = 0.0;
    private double turnActualTimeS = 0.0;

    private boolean usePedroPilot = false;
    private Follower follower;
    private Path forwardPath;
    private Path strafePath;
    private Path turnPath;

    private DriveSubsystem drive;
    private RobotHardware hardware;

    @Override
    public void runOpMode() {
        while (!isStarted() && !isStopRequested()) {
            PathingConfig.applyDriverStationSelection(gamepad1);
            telemetry.addLine("Select drive mode before Start");
            telemetry.addLine(PathingConfig.getSelectionInstructions());
            telemetry.addData("Selected", PathingConfig.getActiveBackendName());
            telemetry.addLine("Use B for Pedro for this pilot test");
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            return;
        }

        Robot robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        hardware = robot.getHardware();

        usePedroPilot = PathingConfig.usePedroPathing();
        if (usePedroPilot) {
            setupPedroFollowerAndPaths();
        }

        telemetry.addLine("Pedro pilot ready");
        telemetry.addData("Pilot Mode", usePedroPilot ? "Pedro Path Following" : "Legacy Timed Sequence");
        telemetry.addData("Drive Backend", drive.getBackendName());
        telemetry.addLine("A: start pilot, X: abort, Y: reset yaw");
        telemetry.update();

        waitForStart();

        boolean lastA = false;
        boolean lastY = false;

        while (opModeIsActive()) {
            boolean aPressed = gamepad1.a;
            boolean yPressed = gamepad1.y;

            if (yPressed && !lastY) {
                hardware.resetYaw();
            }
            lastY = yPressed;

            if (gamepad1.x) {
                transitionTo(PilotState.ABORTED);
                if (follower != null) {
                    follower.stop();
                }
                drive.stop();
            }

            if (aPressed && !lastA && state == PilotState.IDLE) {
                pilotStartTime = getRuntime();
                if (usePedroPilot && follower != null) {
                    follower.setPose(START_POSE);
                    follower.update();
                    follower.follow(forwardPath);
                }
                transitionTo(PilotState.FORWARD);
            }
            lastA = aPressed;

            runPilotStateMachine();

            telemetry.addData("Selected Backend", PathingConfig.getActiveBackendName());
            telemetry.addData("Pilot Mode", usePedroPilot ? "Pedro Path Following" : "Legacy Timed Sequence");
            telemetry.addData("Drive Backend", drive.getBackendName());
            telemetry.addData("Pilot State", state);
            telemetry.addData("Previous State", previousState);
            telemetry.addData("State Time (s)", "%.2f", getStateElapsedTime());
            telemetry.addData("Pilot Runtime (s)", "%.2f", getPilotRuntime());
            telemetry.addData("Heading (rad)", "%.3f", hardware.getHeadingRadians());
            telemetry.addData("Target Heading (rad)", "%.3f", targetHeading);
            telemetry.addData("Heading Error (rad)", "%.3f", getHeadingErrorRadians());
            telemetry.addData("Forward Time (actual/plan)", "%.2f / %.2f", forwardActualTimeS, FORWARD_TIME_S);
            telemetry.addData("Strafe Time (actual/plan)", "%.2f / %.2f", strafeActualTimeS, STRAFE_TIME_S);
            telemetry.addData("Turn Time (actual/plan)", "%.2f / %.2f", turnActualTimeS, TURN_TIME_S);
            if (follower != null) {
                Pose pedroPose = follower.pose();
                telemetry.addData("Pedro Pose", "(%.2f, %.2f, %.3f)", pedroPose.x(), pedroPose.y(), pedroPose.heading());
                telemetry.addData("Pedro Completion", "%.2f", follower.completion());
                telemetry.addData("Pedro Busy", follower.isBusy());
                telemetry.addData("Pedro Param End", follower.atParametricEnd());
            }
            telemetry.addData("Motor Powers", drive.getMotorPowers());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }

    private void setupPedroFollowerAndPaths() {
        follower = createFollower();

        forwardPath = line(START_POSE, FORWARD_END_POSE).constant(START_POSE.heading());
        strafePath = line(FORWARD_END_POSE, STRAFE_END_POSE).constant(STRAFE_END_POSE.heading());
        turnPath = line(STRAFE_END_POSE, TURN_END_POSE).linear(STRAFE_END_POSE.heading(), TURN_END_POSE.heading());
    }

    private Follower createFollower() {
        MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
            c.frontLeftName.set(HardwareConstants.FRONT_LEFT_DRIVE);
            c.frontRightName.set(HardwareConstants.FRONT_RIGHT_DRIVE);
            c.backLeftName.set(HardwareConstants.BACK_LEFT_DRIVE);
            c.backRightName.set(HardwareConstants.BACK_RIGHT_DRIVE);

            c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
            c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
            c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
            c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);

            c.manualBrakeMode.set(true);
        });

        TwoWheelConfig localizerConfig = new TwoWheelConfig(c -> {
            c.xPodName.set(HardwareConstants.FRONT_LEFT_DRIVE);
            c.yPodName.set(HardwareConstants.FRONT_RIGHT_DRIVE);
            c.imuName.set(HardwareConstants.IMU_NAME);
            c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.LEFT)));

            // Starter values for first path testing. Replace with tuned values from Pedro localizer tuning.
            c.xPodOffset.set(0.0);
            c.yPodOffset.set(0.0);
            c.forwardTicksToInches.set(0.0100);
            c.strafeTicksToInches.set(0.0100);
            c.xPodDirection.set(Encoder.FORWARD);
            c.yPodDirection.set(Encoder.FORWARD);
        });

        Localizer localizer = new TwoWheelLocalizer(hardwareMap, localizerConfig);
        Drivetrain drivetrain = new Mecanum(hardwareMap, drivetrainConfig);

        ForesightConfig algorithmConfig = new ForesightConfig(c -> {
            // Conservative first-run values to keep motion smooth and reduce overshoot.
            c.forwardTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
            c.strafeTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
            c.coast.set(Controller.proportionalFeedforward(0.0));
            c.brake.set(Controller.proportionalFeedforward(0.0));

            c.headingFeedback.set(Controller.proportional(0.015));
            c.headingBrakeCoefficients.set(Vector2D.cartesian(0.0, 0.0));

            c.linearBrakeCoefficients.set(Matrix.diag(0.0, 0.0));
            c.quadraticBrakeCoefficients.set(Matrix.diag(0.0, 0.0));

            c.maxAchievableForwardVelocity.set(18.0);
            c.maxAchievableStrafeVelocity.set(18.0);
            c.naturalForwardDeceleration.set(18.0);
            c.naturalStrafeDeceleration.set(18.0);
        });

        return new Follower(localizer, drivetrain, new Foresight(algorithmConfig));
    }

    private void runPilotStateMachine() {
        if (usePedroPilot && follower != null) {
            runPedroPilotStateMachine();
            return;
        }

        runLegacyPilotStateMachine();
    }

    private void runLegacyPilotStateMachine() {
        double elapsed = getStateElapsedTime();

        switch (state) {
            case IDLE:
                drive.stop();
                break;
            case FORWARD:
                drive.driveRobotCentric(FORWARD_POWER, 0.0, 0.0);
                if (elapsed >= FORWARD_TIME_S) {
                    forwardActualTimeS = elapsed;
                    transitionTo(PilotState.STRAFE);
                }
                break;
            case STRAFE:
                drive.driveRobotCentric(0.0, STRAFE_POWER, 0.0);
                if (elapsed >= STRAFE_TIME_S) {
                    strafeActualTimeS = elapsed;
                    transitionTo(PilotState.TURN);
                }
                break;
            case TURN:
                drive.driveRobotCentric(0.0, 0.0, TURN_POWER);
                if (elapsed >= TURN_TIME_S) {
                    turnActualTimeS = elapsed;
                    transitionTo(PilotState.COMPLETE);
                    drive.stop();
                }
                break;
            case COMPLETE:
                drive.stop();
                break;
            case ABORTED:
                drive.stop();
                break;
        }
    }

    private void runPedroPilotStateMachine() {
        double elapsed = getStateElapsedTime();

        switch (state) {
            case IDLE:
                follower.stop();
                break;
            case FORWARD:
                follower.update();
                if (follower.atParametricEnd()) {
                    forwardActualTimeS = elapsed;
                    follower.follow(strafePath);
                    transitionTo(PilotState.STRAFE);
                }
                break;
            case STRAFE:
                follower.update();
                if (follower.atParametricEnd()) {
                    strafeActualTimeS = elapsed;
                    follower.follow(turnPath);
                    transitionTo(PilotState.TURN);
                }
                break;
            case TURN:
                follower.update();
                if (follower.atParametricEnd()) {
                    turnActualTimeS = elapsed;
                    follower.hold(TURN_END_POSE);
                    transitionTo(PilotState.COMPLETE);
                }
                break;
            case COMPLETE:
                follower.update();
                if (!follower.holding()) {
                    follower.hold(TURN_END_POSE);
                }
                break;
            case ABORTED:
                follower.stop();
                break;
        }
    }

    private void transitionTo(PilotState newState) {
        previousState = state;
        state = newState;
        stateStartTime = getRuntime();

        if (newState == PilotState.FORWARD) {
            targetHeading = FORWARD_END_POSE.heading();
        } else if (newState == PilotState.STRAFE) {
            targetHeading = STRAFE_END_POSE.heading();
        } else if (newState == PilotState.TURN || newState == PilotState.COMPLETE) {
            targetHeading = TURN_END_POSE.heading();
        } else if (newState == PilotState.ABORTED) {
            targetHeading = hardware != null ? hardware.getHeadingRadians() : targetHeading;
        } else {
            targetHeading = hardware != null ? hardware.getHeadingRadians() : targetHeading;
        }
    }

    private double getStateElapsedTime() {
        return getRuntime() - stateStartTime;
    }

    private double getPilotRuntime() {
        if (pilotStartTime <= 0.0) {
            return 0.0;
        }
        return getRuntime() - pilotStartTime;
    }

    private double getHeadingErrorRadians() {
        if (hardware == null) {
            return 0.0;
        }
        return normalizeRadians(targetHeading - hardware.getHeadingRadians());
    }

    private double normalizeRadians(double angle) {
        while (angle > Math.PI) {
            angle -= (2.0 * Math.PI);
        }
        while (angle < -Math.PI) {
            angle += (2.0 * Math.PI);
        }
        return angle;
    }
}

