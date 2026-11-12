package org.firstinspires.ftc.teamcode.gytd.test;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.revhub.localizers.TwoWheelConfig;
import com.pedropathing.revhub.localizers.TwoWheelLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.gytd.hardware.HardwareConstants;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

/**
 * IMU + localizer tuning aid.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 * - Odometry pods wired per PedroTwoWheelLocalizerConstants
 *
 * Controls:
 * A = stationary drift test (10s)
 * B = rotate closure test (CW then CCW)
 * X = forward/back closure test
 * Y = strafe/back closure test
 * DPad Down = reset localizer pose to origin
 * Left bumper = cancel running test
 * Left stick/right stick = manual drive when no test is running
 *
 * Safety notes:
 * - Put the robot on blocks for the first run of each closure test.
 * - Keep the area around the robot clear during drift/closure tests.
 * - Use the left bumper immediately to cancel if motion looks unsafe.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "IMU Localizer Tuner", group = "Test")
public class TestImuLocalizerTuner extends LinearOpMode {
    private enum TestMode {
        NONE,
        STATIONARY_DRIFT,
        ROTATE_CLOSURE,
        FORWARD_BACK_CLOSURE,
        STRAFE_BACK_CLOSURE
    }

    private static final double STATIONARY_TEST_SECONDS = 10.0;
    private static final double SEGMENT_SECONDS = 2.5;
    private static final double ROTATE_SEGMENT_SECONDS = 3.0;
    private static final double DRIVE_POWER = 0.25;
    private static final double ROTATE_POWER = 0.25;

    private static final double MAX_STATIONARY_DRIFT_IN = 1.0;
    private static final double MAX_STATIONARY_HEADING_DRIFT_DEG = 1.5;
    private static final double MAX_CLOSURE_ERROR_IN = 2.0;
    private static final double MAX_CLOSURE_HEADING_ERROR_DEG = 4.0;

    private Robot robot;
    private DriveSubsystem drive;
    private Follower follower;

    private TestMode mode = TestMode.NONE;
    private Pose modeStartPose = new Pose(0.0, 0.0, 0.0);
    private double modeStartImuHeading = 0.0;
    private double modeStartTimeS = 0.0;
    private Pose modeMidPose = null;
    private double modeMidImuHeading = 0.0;
    private boolean modeMidCaptured = false;
    private String phaseLabel = "idle";
    private String tuningHint = "Run A/B/X/Y tests";

    private boolean lastA = false;
    private boolean lastB = false;
    private boolean lastX = false;
    private boolean lastY = false;
    private boolean lastDpadDown = false;

    private String lastResult = "No test yet";

    @Override
    public void runOpMode() {
        PathingConfig.setUsePedroPathing(true);

        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        follower = createFollower();
        follower.setPose(new Pose(0.0, 0.0, 0.0));

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("IMU + Localizer Tuner ready");
            telemetry.addLine("A=stationary, B=rotate, X=fwd/back, Y=strafe/back");
            telemetry.addLine("DPad Down=reset pose, Left bumper=cancel test");
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            robot.shutdown();
            return;
        }

        waitForStart();

        while (opModeIsActive()) {
            follower.update();

            handleButtons();
            runActiveTestOrManualDrive();

            Pose pose = follower.pose();
            double imuHeading = robot.getHardware().getHeadingRadians();

            telemetry.addLine("IMU + Localizer Tuner");
            telemetry.addData("Mode", mode);
            telemetry.addData("Phase", phaseLabel);
            telemetry.addData("Pose", "(%.2f, %.2f, %.1f deg)", pose.x(), pose.y(), Math.toDegrees(pose.heading()));
            telemetry.addData("Start Pose", "(%.2f, %.2f)", modeStartPose.x(), modeStartPose.y());
            if (modeMidCaptured && modeMidPose != null) {
                telemetry.addData("Mid Pose", "(%.2f, %.2f)", modeMidPose.x(), modeMidPose.y());
            }
            telemetry.addData("IMU Heading", "%.1f deg", Math.toDegrees(imuHeading));
            telemetry.addData("Pose-IMU heading diff", "%.2f deg", Math.toDegrees(wrapRadians(pose.heading() - imuHeading)));
            telemetry.addData("Delta from mode start", "dx=%.2f dy=%.2f dH=%.1f deg", pose.x() - modeStartPose.x(), pose.y() - modeStartPose.y(), Math.toDegrees(wrapRadians(pose.heading() - modeStartPose.heading())));
            telemetry.addData("XY Drift Mag", "%.2f in", distanceBetween(modeStartPose, pose));
            if (modeMidCaptured && modeMidPose != null) {
                telemetry.addData("Outbound Leg", "dx=%.2f dy=%.2f", modeMidPose.x() - modeStartPose.x(), modeMidPose.y() - modeStartPose.y());
                telemetry.addData("Return Leg", "dx=%.2f dy=%.2f", pose.x() - modeMidPose.x(), pose.y() - modeMidPose.y());
            }
            telemetry.addData("Tuning Hint", tuningHint);
            telemetry.addData("Last result", lastResult);
            telemetry.addLine("Tip: run each test 3-5 times before tuning constants");
            telemetry.update();

            idle();
        }

        robot.shutdown();
    }

    private void handleButtons() {
        boolean aPressed = gamepad1.a && !lastA;
        boolean bPressed = gamepad1.b && !lastB;
        boolean xPressed = gamepad1.x && !lastX;
        boolean yPressed = gamepad1.y && !lastY;
        boolean dpadDownPressed = gamepad1.dpad_down && !lastDpadDown;

        if (gamepad1.left_bumper && mode != TestMode.NONE) {
            cancelTest("Cancelled");
        }

        if (dpadDownPressed && mode == TestMode.NONE) {
            follower.setPose(new Pose(0.0, 0.0, 0.0));
            robot.getHardware().resetYaw();
            lastResult = "Pose + yaw reset";
        }

        if (mode == TestMode.NONE) {
            if (aPressed) {
                beginTest(TestMode.STATIONARY_DRIFT);
            } else if (bPressed) {
                beginTest(TestMode.ROTATE_CLOSURE);
            } else if (xPressed) {
                beginTest(TestMode.FORWARD_BACK_CLOSURE);
            } else if (yPressed) {
                beginTest(TestMode.STRAFE_BACK_CLOSURE);
            }
        }

        lastA = gamepad1.a;
        lastB = gamepad1.b;
        lastX = gamepad1.x;
        lastY = gamepad1.y;
        lastDpadDown = gamepad1.dpad_down;
    }

    private void beginTest(TestMode newMode) {
        mode = newMode;
        modeStartPose = follower.pose();
        modeStartImuHeading = robot.getHardware().getHeadingRadians();
        modeStartTimeS = getRuntime();
        modeMidPose = null;
        modeMidImuHeading = modeStartImuHeading;
        modeMidCaptured = false;
        phaseLabel = "phase-1";
        tuningHint = "Running " + mode;
        lastResult = "Running " + mode;
    }

    private void cancelTest(String reason) {
        drive.stop();
        mode = TestMode.NONE;
        phaseLabel = "idle";
        lastResult = reason;
    }

    private void runActiveTestOrManualDrive() {
        if (mode == TestMode.NONE) {
            manualDrive();
            return;
        }

        double elapsed = getRuntime() - modeStartTimeS;

        switch (mode) {
            case STATIONARY_DRIFT:
                phaseLabel = "hold";
                drive.stop();
                if (elapsed >= STATIONARY_TEST_SECONDS) {
                    completeStationaryTest(elapsed);
                }
                break;

            case ROTATE_CLOSURE:
                if (elapsed < ROTATE_SEGMENT_SECONDS) {
                    phaseLabel = "cw";
                    drive.driveRobotCentric(0.0, 0.0, ROTATE_POWER);
                } else if (elapsed < (2.0 * ROTATE_SEGMENT_SECONDS)) {
                    captureMidIfNeeded();
                    phaseLabel = "ccw";
                    drive.driveRobotCentric(0.0, 0.0, -ROTATE_POWER);
                } else {
                    drive.stop();
                    completeClosureTest("rotate");
                }
                break;

            case FORWARD_BACK_CLOSURE:
                if (elapsed < SEGMENT_SECONDS) {
                    phaseLabel = "forward";
                    drive.driveRobotCentric(DRIVE_POWER, 0.0, 0.0);
                } else if (elapsed < (2.0 * SEGMENT_SECONDS)) {
                    captureMidIfNeeded();
                    phaseLabel = "backward";
                    drive.driveRobotCentric(-DRIVE_POWER, 0.0, 0.0);
                } else {
                    drive.stop();
                    completeClosureTest("forward/back");
                }
                break;

            case STRAFE_BACK_CLOSURE:
                if (elapsed < SEGMENT_SECONDS) {
                    phaseLabel = "strafe";
                    drive.driveRobotCentric(0.0, DRIVE_POWER, 0.0);
                } else if (elapsed < (2.0 * SEGMENT_SECONDS)) {
                    captureMidIfNeeded();
                    phaseLabel = "counter-strafe";
                    drive.driveRobotCentric(0.0, -DRIVE_POWER, 0.0);
                } else {
                    drive.stop();
                    completeClosureTest("strafe/back");
                }
                break;

            case NONE:
                break;
        }
    }

    private void manualDrive() {
        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        drive.driveFieldCentric(axial, lateral, yaw, robot.getHardware().getHeadingRadians());
    }

    private void completeStationaryTest(double elapsedSeconds) {
        Pose pose = follower.pose();
        double imuHeading = robot.getHardware().getHeadingRadians();

        double dx = pose.x() - modeStartPose.x();
        double dy = pose.y() - modeStartPose.y();
        double driftInches = distanceBetween(modeStartPose, pose);
        double headingDriftDeg = Math.abs(Math.toDegrees(wrapRadians(imuHeading - modeStartImuHeading)));
        double driftRate = driftInches / Math.max(elapsedSeconds, 1e-6);
        double headingRate = headingDriftDeg / Math.max(elapsedSeconds, 1e-6);

        boolean pass = driftInches <= MAX_STATIONARY_DRIFT_IN
                && headingDriftDeg <= MAX_STATIONARY_HEADING_DRIFT_DEG;

        lastResult = String.format("Stationary %s | dx=%.2f dy=%.2f drift=%.2fin heading=%.2fdeg",
                pass ? "PASS" : "FAIL", dx, dy, driftInches, headingDriftDeg);
        tuningHint = String.format("Stationary drift rate=%.3f in/s, heading rate=%.3f deg/s", driftRate, headingRate);

        mode = TestMode.NONE;
        phaseLabel = "idle";
    }

    private void completeClosureTest(String label) {
        Pose pose = follower.pose();
        double imuHeading = robot.getHardware().getHeadingRadians();

        double dx = pose.x() - modeStartPose.x();
        double dy = pose.y() - modeStartPose.y();
        double closureErrorInches = distanceBetween(modeStartPose, pose);
        double localizerHeadingClosureDeg = Math.abs(Math.toDegrees(wrapRadians(pose.heading() - modeStartPose.heading())));
        double imuHeadingClosureDeg = Math.abs(Math.toDegrees(wrapRadians(imuHeading - modeStartImuHeading)));

        double outDx = 0.0;
        double outDy = 0.0;
        if (modeMidCaptured && modeMidPose != null) {
            outDx = modeMidPose.x() - modeStartPose.x();
            outDy = modeMidPose.y() - modeStartPose.y();
        }

        boolean pass = closureErrorInches <= MAX_CLOSURE_ERROR_IN
                && imuHeadingClosureDeg <= MAX_CLOSURE_HEADING_ERROR_DEG;

        lastResult = String.format("%s %s | end dx=%.2f dy=%.2f | XY=%.2fin IMU=%.2fdeg PoseH=%.2fdeg",
                label, pass ? "PASS" : "FAIL", dx, dy, closureErrorInches, imuHeadingClosureDeg, localizerHeadingClosureDeg);

        if ("forward/back".equals(label)) {
            tuningHint = String.format("Forward leg dx=%.2f dy=%.2f; tune forwardTicksToInches if forward distance is off", outDx, outDy);
        } else if ("strafe/back".equals(label)) {
            tuningHint = String.format("Strafe leg dx=%.2f dy=%.2f; tune strafeTicksToInches if strafe distance is off", outDx, outDy);
        } else {
            tuningHint = String.format("Rotate drift dx=%.2f dy=%.2f; tune xPodOffset/yPodOffset", dx, dy);
        }

        mode = TestMode.NONE;
        phaseLabel = "idle";
    }

    private void captureMidIfNeeded() {
        if (modeMidCaptured) {
            return;
        }

        modeMidPose = follower.pose();
        modeMidImuHeading = robot.getHardware().getHeadingRadians();
        modeMidCaptured = true;
    }

    private double distanceBetween(Pose first, Pose second) {
        return Math.hypot(first.x() - second.x(), first.y() - second.y());
    }

    private double wrapRadians(double radians) {
        while (radians > Math.PI) {
            radians -= 2.0 * Math.PI;
        }
        while (radians < -Math.PI) {
            radians += 2.0 * Math.PI;
        }
        return radians;
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
            c.imu.set(new RevHubIMU(HardwareConstants.IMU_ORIENTATION_ON_ROBOT));

            c.xPodOffset.set(-0.005);
            c.yPodOffset.set(+0.005

            );
            c.forwardTicksToInches.set(0.0100);
            c.strafeTicksToInches.set(0.0100);
            c.xPodDirection.set(Encoder.FORWARD);
            c.yPodDirection.set(Encoder.FORWARD);
        });

        Localizer localizer = new TwoWheelLocalizer(hardwareMap, localizerConfig);
        Drivetrain drivetrain = new Mecanum(hardwareMap, drivetrainConfig);

        ForesightConfig algorithmConfig = new ForesightConfig(c -> {
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
}
