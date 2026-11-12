package org.firstinspires.ftc.teamcode.gytd.test;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util.TeleOpConstants;
import org.firstinspires.ftc.teamcode.gytd.pathing.PedroFollowerFactory;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Integration-style practice OpMode for red one-touch Pedro shooting recovery.
 *
 * This is the easiest way to "simulate" the behavior on FTC hardware without a full desktop
 * physics simulator: the test can intentionally nudge the pose estimate in software and watch the
 * robot recover using Pedro hold mode.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 *
 * Controls:
 * - gamepad1.b: start the red one-touch path from the current Pedro pose
 * - gamepad1.a: simulate a nudge while holding at the shooting pose
 * - gamepad1.x: cancel immediately and return drive control to Gamepad 1
 * - gamepad1.y: reset the Pedro pose to the red home reference
 *
 * Safety notes:
 * - Put the robot on blocks for the first run.
 * - If the test uses a simulated nudge, the robot is not physically moved; only the pose estimate is shifted.
 *
 * Note: follower creation now goes through PedroFollowerFactory so this recovery test stays in
 * sync with the shared two-wheel localizer/drivetrain configuration used by other Pedro OpModes.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "Test: Red Pedro Shoot Recovery", group = "Test")
public class TestRedPedroShootRecovery extends LinearOpMode {
    private enum TestState {
        IDLE,
        DRIVING_TO_SHOOT_POSE,
        HOLDING_SHOOT_POSE,
        RECOVERING_SHOOT_POSE,
        COMPLETE,
        CANCELLED
    }

    private static final double SHOOT_POSE_POSITION_TOLERANCE_IN = 2.0;
    private static final double SHOOT_POSE_HEADING_TOLERANCE_RAD = Math.toRadians(6.0);
    private static final double SHOOT_POSE_SETTLE_TIME_S = 0.30;
    private static final double SIMULATED_NUDGE_X_IN = 3.0;
    private static final double SIMULATED_NUDGE_Y_IN = 2.0;
    private static final double SIMULATED_NUDGE_HEADING_RAD = Math.toRadians(8.0);

    private Robot robot;
    private DriveSubsystem drive;
    private Follower follower;

    private TestState state = TestState.IDLE;
    private boolean shootingAllowed = false;
    private boolean shootingPausedForPoseRecovery = false;
    private double stableStartTimeS = -1.0;
    private boolean lastB = false;
    private boolean lastA = false;
    private boolean lastX = false;

    private final List<Path> route = new ArrayList<>();
    private int routeIndex = 0;
    private Pose shootPose = TeleOpConstants.RED_SIDE_SHOOT_POSE;

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        follower = createFollower();
        follower.setPose(TeleOpConstants.RED_HOME_POSE);

        telemetry.addLine("Red Pedro shoot-recovery test ready");
        telemetry.addLine("B=start, A=simulate bump, X=cancel, Y=reset to home");
        telemetry.addLine("This is a pose-level simulation, not a full physics simulator.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (state != TestState.DRIVING_TO_SHOOT_POSE) {
                follower.update();
            }

            handleButtons();
            updateStateMachine();
            updateManualDrive();

            telemetry.addData("Test State", state);
            telemetry.addData("Pose", "(%.2f, %.2f, %.3f)", follower.pose().x(), follower.pose().y(), follower.pose().heading());
            telemetry.addData("Shoot Pose", "(%.2f, %.2f, %.3f)", shootPose.x(), shootPose.y(), shootPose.heading());
            telemetry.addData("Pos Error (in)", "%.2f", getShootPositionErrorInches());
            telemetry.addData("Heading Error (deg)", "%.1f", Math.toDegrees(getShootHeadingErrorRadians()));
            telemetry.addData("Shooting Allowed", shootingAllowed);
            telemetry.addData("Paused For Recovery", shootingPausedForPoseRecovery);
            telemetry.addData("Pedro Busy", follower.isBusy());
            telemetry.addData("Pedro Holding", follower.holding());
            telemetry.addData("Drive Backend", drive.getBackendName());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }

    private void handleButtons() {
        boolean bPressed = gamepad1.b && !lastB;
        boolean aPressed = gamepad1.a && !lastA;
        boolean xPressed = gamepad1.x && !lastX;
        boolean yPressed = gamepad1.y;

        if (bPressed && state == TestState.IDLE) {
            startRedAssist();
        }

        if (aPressed && (state == TestState.HOLDING_SHOOT_POSE || state == TestState.RECOVERING_SHOOT_POSE)) {
            applySimulatedNudge();
        }

        if (xPressed) {
            cancelAssist();
        }

        if (yPressed && state == TestState.IDLE) {
            follower.setPose(TeleOpConstants.RED_HOME_POSE);
        }

        lastB = gamepad1.b;
        lastA = gamepad1.a;
        lastX = gamepad1.x;
    }

    private void startRedAssist() {
        Pose currentPose = follower.pose();
        route.clear();
        route.add(line(currentPose, TeleOpConstants.RED_SIDE_SHOOT_POSE)
                .linear(currentPose.heading(), TeleOpConstants.RED_SIDE_SHOOT_POSE.heading()));
        routeIndex = 0;
        shootPose = TeleOpConstants.RED_SIDE_SHOOT_POSE;
        shootingAllowed = false;
        shootingPausedForPoseRecovery = false;
        stableStartTimeS = -1.0;
        state = TestState.DRIVING_TO_SHOOT_POSE;
        follower.follow(route.get(routeIndex));
    }

    private void applySimulatedNudge() {
        // Move the pose estimate on purpose to imitate another robot bumping us.
        Pose nudgedPose = new Pose(
                follower.pose().x() + SIMULATED_NUDGE_X_IN,
                follower.pose().y() + SIMULATED_NUDGE_Y_IN,
                follower.pose().heading() + SIMULATED_NUDGE_HEADING_RAD);
        follower.setPose(nudgedPose);
        shootingPausedForPoseRecovery = true;
        shootingAllowed = false;
        stableStartTimeS = -1.0;
        state = TestState.RECOVERING_SHOOT_POSE;
    }

    private void cancelAssist() {
        follower.stop();
        drive.stop();
        route.clear();
        routeIndex = 0;
        shootingAllowed = false;
        shootingPausedForPoseRecovery = false;
        stableStartTimeS = -1.0;
        state = TestState.CANCELLED;
    }

    private void updateStateMachine() {
        switch (state) {
            case DRIVING_TO_SHOOT_POSE:
                follower.update();
                if (follower.atParametricEnd()) {
                    follower.hold(TeleOpConstants.RED_SIDE_SHOOT_POSE);
                    state = TestState.HOLDING_SHOOT_POSE;
                    shootingPausedForPoseRecovery = true;
                    stableStartTimeS = -1.0;
                }
                break;

            case HOLDING_SHOOT_POSE:
            case RECOVERING_SHOOT_POSE:
                follower.update();
                if (!follower.holding()) {
                    follower.hold(TeleOpConstants.RED_SIDE_SHOOT_POSE);
                }

                if (isAtShootPose()) {
                    if (stableStartTimeS < 0.0) {
                        stableStartTimeS = getRuntime();
                    }
                    if ((getRuntime() - stableStartTimeS) >= SHOOT_POSE_SETTLE_TIME_S) {
                        shootingAllowed = true;
                        shootingPausedForPoseRecovery = false;
                        state = TestState.HOLDING_SHOOT_POSE;
                    }
                } else {
                    shootingAllowed = false;
                    shootingPausedForPoseRecovery = true;
                    stableStartTimeS = -1.0;
                    state = TestState.RECOVERING_SHOOT_POSE;
                }
                break;

            case IDLE:
            case COMPLETE:
            case CANCELLED:
                break;
        }
    }

    private void updateManualDrive() {
        if (state == TestState.DRIVING_TO_SHOOT_POSE || state == TestState.HOLDING_SHOOT_POSE || state == TestState.RECOVERING_SHOOT_POSE) {
            return;
        }

        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;
        drive.driveFieldCentric(axial, lateral, yaw, robot.getHardware().getHeadingRadians());
    }

    private boolean isAtShootPose() {
        return getShootPositionErrorInches() <= SHOOT_POSE_POSITION_TOLERANCE_IN
                && Math.abs(getShootHeadingErrorRadians()) <= SHOOT_POSE_HEADING_TOLERANCE_RAD;
    }

    private double getShootPositionErrorInches() {
        Pose currentPose = follower.pose();
        return Math.hypot(shootPose.x() - currentPose.x(), shootPose.y() - currentPose.y());
    }

    private double getShootHeadingErrorRadians() {
        double error = shootPose.heading() - follower.pose().heading();
        while (error > Math.PI) {
            error -= 2.0 * Math.PI;
        }
        while (error < -Math.PI) {
            error += 2.0 * Math.PI;
        }
        return error;
    }

    private Follower createFollower() {
        return PedroFollowerFactory.createTwoWheelFollower(hardwareMap);
    }
}

