package org.firstinspires.ftc.teamcode.gytd.test;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.pathing.PedroFollowerFactory;
import org.firstinspires.ftc.teamcode.gytd.pathing.PedroTwoWheelLocalizerConstants;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

/**
 * Lightweight manual tuning OpMode for Pedro two-wheel localization.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 * - Two-wheel odometry pods wired per PedroTwoWheelLocalizerConstants
 *
 * Controls:
 * - A: reset localizer pose and reference pose to (0, 0, 0)
 * - X: capture current pose as reference
 * - B: compute closure error from captured reference to current pose
 * - Left stick: field-centric translation
 * - Right stick X: rotation
 * - Left bumper: precision speed
 *
 * Safety notes:
 * - Put the robot on blocks for the first run.
 * - Keep the area around the robot clear while manually driving for tuning.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "Test: Pedro TwoWheel Telemetry Tuner", group = "Test")
public class TestPedroTwoWheelTelemetryTuner extends LinearOpMode {
    private Robot robot;
    private DriveSubsystem drive;
    private Follower follower;

    private Pose referencePose = Pose.zero();
    private double lastClosureDistanceIn = 0.0;
    private double lastClosureHeadingDeg = 0.0;

    private boolean lastResetButton;
    private boolean lastCaptureButton;
    private boolean lastMeasureButton;

    @Override
    public void runOpMode() {
        PathingConfig.setUsePedroPathing(true);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("Pedro TwoWheel Telemetry Tuner ready");
            telemetry.addLine("A=reset pose  X=capture ref  B=measure closure");
            telemetry.addLine("Left stick=drive  Right stick X=rotate  LB=slow mode");
            telemetry.addData("Pedro backend", PathingConfig.getActiveBackendName());
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            return;
        }

        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        follower = PedroFollowerFactory.createTwoWheelFollower(hardwareMap);

        follower.setPose(Pose.zero());
        referencePose = follower.pose();

        waitForStart();

        while (opModeIsActive()) {
            updateButtons();

            follower.update();
            updateManualDrive();

            Pose currentPose = follower.pose();
            double deltaX = currentPose.x() - referencePose.x();
            double deltaY = currentPose.y() - referencePose.y();
            double deltaHeadingDeg = Math.toDegrees(angleWrap(currentPose.heading() - referencePose.heading()));

            telemetry.addLine("Pedro TwoWheel Telemetry Tuner");
            telemetry.addData("Pose", "(%.2f, %.2f, %.1f deg)", currentPose.x(), currentPose.y(), Math.toDegrees(currentPose.heading()));
            telemetry.addData("Ref Pose", "(%.2f, %.2f, %.1f deg)", referencePose.x(), referencePose.y(), Math.toDegrees(referencePose.heading()));
            telemetry.addData("Delta from Ref", "dx=%.2f dy=%.2f dh=%.1f deg", deltaX, deltaY, deltaHeadingDeg);
            telemetry.addData("Closure (last B)", "dist=%.2f in, dh=%.1f deg", lastClosureDistanceIn, lastClosureHeadingDeg);

            telemetry.addLine("TwoWheel constants");
            telemetry.addData("xPodOffset", PedroTwoWheelLocalizerConstants.X_POD_OFFSET_IN);
            telemetry.addData("yPodOffset", PedroTwoWheelLocalizerConstants.Y_POD_OFFSET_IN);
            telemetry.addData("forwardTicksToInches", PedroTwoWheelLocalizerConstants.FORWARD_TICKS_TO_INCHES);
            telemetry.addData("strafeTicksToInches", PedroTwoWheelLocalizerConstants.STRAFE_TICKS_TO_INCHES);
            telemetry.addData("xPodDirection", PedroTwoWheelLocalizerConstants.X_POD_DIRECTION);
            telemetry.addData("yPodDirection", PedroTwoWheelLocalizerConstants.Y_POD_DIRECTION);
            telemetry.update();

            idle();
        }

        robot.shutdown();
    }

    private void updateButtons() {
        boolean resetPressed = gamepad1.a && !lastResetButton;
        boolean capturePressed = gamepad1.x && !lastCaptureButton;
        boolean measurePressed = gamepad1.b && !lastMeasureButton;

        if (resetPressed) {
            follower.setPose(Pose.zero());
            referencePose = follower.pose();
            lastClosureDistanceIn = 0.0;
            lastClosureHeadingDeg = 0.0;
        }

        if (capturePressed) {
            referencePose = follower.pose();
        }

        if (measurePressed) {
            Pose currentPose = follower.pose();
            lastClosureDistanceIn = Math.hypot(currentPose.x() - referencePose.x(), currentPose.y() - referencePose.y());
            lastClosureHeadingDeg = Math.toDegrees(angleWrap(currentPose.heading() - referencePose.heading()));
        }

        lastResetButton = gamepad1.a;
        lastCaptureButton = gamepad1.x;
        lastMeasureButton = gamepad1.b;
    }

    private void updateManualDrive() {
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

    private double angleWrap(double angle) {
        while (angle > Math.PI) {
            angle -= 2.0 * Math.PI;
        }
        while (angle < -Math.PI) {
            angle += 2.0 * Math.PI;
        }
        return angle;
    }
}

