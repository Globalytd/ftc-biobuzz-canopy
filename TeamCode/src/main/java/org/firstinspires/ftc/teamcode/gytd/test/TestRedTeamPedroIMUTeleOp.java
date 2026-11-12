package org.firstinspires.ftc.teamcode.gytd.test;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util.TeleOpConstants;
import org.firstinspires.ftc.teamcode.gytd.pathing.PedroFollowerFactory;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

/**
 * Minimal RED Pedro path test.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 *
 * Controls:
 * - A: current pose -> RED C2 garden shoot pose
 * - B: current pose -> RED home pose
 * - X: cancel active path
 * - Left stick = drive, right stick = rotate when no path is active
 *
 * Safety notes:
 * - Put the robot on blocks for the first run.
 * - Keep the area around the robot clear while a path is following.
 * - Use X immediately if motion is not expected or looks unsafe.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "RED team Pedro Test IMU", group = "Test")
public class TestRedTeamPedroIMUTeleOp extends LinearOpMode {
    private enum AssistState {
        IDLE,
        FOLLOWING_PATH
    }

    private Robot robot;
    private DriveSubsystem drive;
    private Follower follower;
    private AssistState assistState = AssistState.IDLE;

    private Pose assistTargetPose = TeleOpConstants.RED_HOME_POSE;
    private Path activePath;

    private boolean lastGoToC2Button = false;
    private boolean lastGoHomeButton = false;
    private boolean lastCancelButton = false;

    // Breadcrumbs for localization diagnostics.
    private Pose lastCommandStartPose = TeleOpConstants.RED_HOME_POSE;
    private Pose lastCommandTargetPose = TeleOpConstants.RED_HOME_POSE;
    private double lastCommandTimeS = -1.0;

    @Override
    public void runOpMode() {
        PathingConfig.setUsePedroPathing(true);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("RED team Pedro pose test");
            telemetry.addLine("A = go to RED C2 garden shoot pose");
            telemetry.addLine("B = go to RED home pose");
            telemetry.addLine("X = cancel active path");
            telemetry.addLine("Left stick = drive, right stick = rotate");
            telemetry.addData("Pedro backend", PathingConfig.getActiveBackendName());
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            return;
        }

        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        follower = createFollower();
        follower.setPose(TeleOpConstants.RED_HOME_POSE);

        waitForStart();

        while (opModeIsActive()) {
            if (!isPedroControllingDrive()) {
                follower.update();
            }

            updateAssistButtons();
            updateAssistStateMachine();
            updateManualDrive();

            Pose currentPose = follower.pose();
            telemetry.addLine("RED team Pedro pose test");
            telemetry.addData("Assist state", assistState);
            telemetry.addData("Field heading (rad)", "%.3f", robot.getHardware().getHeadingRadians());
            telemetry.addData("Pose", "(%.2f, %.2f, %.3f)", currentPose.x(), currentPose.y(), currentPose.heading());
            telemetry.addData("Target", "(%.2f, %.2f, %.3f)", assistTargetPose.x(), assistTargetPose.y(), assistTargetPose.heading());
            telemetry.addData("Position error (in)", "%.2f", positionErrorInches(currentPose, assistTargetPose));
            telemetry.addData("Last cmd start", "(%.2f, %.2f, %.3f)", lastCommandStartPose.x(), lastCommandStartPose.y(), lastCommandStartPose.heading());
            telemetry.addData("Last cmd target", "(%.2f, %.2f, %.3f)", lastCommandTargetPose.x(), lastCommandTargetPose.y(), lastCommandTargetPose.heading());
            telemetry.addData("Last cmd age (s)", "%.2f", lastCommandTimeS < 0.0 ? -1.0 : (getRuntime() - lastCommandTimeS));
            telemetry.addData("Pedro progress", follower != null ? "%.2f" : "n/a", follower != null ? follower.completion() : 0.0);
            telemetry.addData("Busy", follower != null && follower.isBusy());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }

    private void updateAssistButtons() {
        boolean goToC2Pressed = gamepad1.a && !lastGoToC2Button;
        boolean goHomePressed = gamepad1.b && !lastGoHomeButton;
        boolean cancelPressed = gamepad1.x && !lastCancelButton;

        if (goToC2Pressed && !isPedroControllingDrive()) {
            startPathTo(TeleOpConstants.RED_C2_GARDEN_SHOOT_POSE);
        }

        if (goHomePressed && !isPedroControllingDrive()) {
            startPathTo(TeleOpConstants.RED_HOME_POSE);
        }

        if (cancelPressed) {
            cancelPath();
        }

        lastGoToC2Button = gamepad1.a;
        lastGoHomeButton = gamepad1.b;
        lastCancelButton = gamepad1.x;
    }

    private void startPathTo(Pose targetPose) {
        if (follower == null) {
            return;
        }

        Pose currentPose = follower.pose();
        assistTargetPose = targetPose;
        lastCommandStartPose = currentPose;
        lastCommandTargetPose = targetPose;
        lastCommandTimeS = getRuntime();
        activePath = line(currentPose, targetPose)
                .linear(currentPose.heading(), targetPose.heading());
        assistState = AssistState.FOLLOWING_PATH;
        follower.follow(activePath);
    }

    private void cancelPath() {
        follower.stop();
        drive.stop();
        activePath = null;
        assistState = AssistState.IDLE;
    }

    private void updateAssistStateMachine() {
        if (follower == null) {
            return;
        }

        if (assistState == AssistState.FOLLOWING_PATH) {
            follower.update();

            if (follower.atParametricEnd()) {
                follower.stop();
                activePath = null;
                assistState = AssistState.IDLE;
            }
        }
    }

    private void updateManualDrive() {
        if (isPedroControllingDrive()) {
            return;
        }

        if (gamepad1.left_bumper) {
            drive.setSpeedScale(0.45);
        } else {
            drive.setSpeedScale(1.0);
        }

        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        drive.driveFieldCentric(axial, lateral, yaw, robot.getHardware().getHeadingRadians());
    }

    private boolean isPedroControllingDrive() {
        return assistState == AssistState.FOLLOWING_PATH;
    }

    private double positionErrorInches(Pose currentPose, Pose targetPose) {
        double deltaX = targetPose.x() - currentPose.x();
        double deltaY = targetPose.y() - currentPose.y();
        return Math.hypot(deltaX, deltaY);
    }


    private Follower createFollower() {
        return PedroFollowerFactory.createTwoWheelFollower(hardwareMap);
    }
}
