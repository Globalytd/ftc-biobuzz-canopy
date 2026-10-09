package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;

/**
 * Combined driver-control and motor smoke test for the existing real motors on the robot.
 *
 * This OpMode exercises real driver-style drive control plus the drive subsystem methods:
 * - driveRobotCentric()
 * - driveFieldCentric()
 * - setSpeedScale()
 * - intake()
 * - reverse()
 * - stop()
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - Intake motor named intake_motor
 * - IMU named imu
 *
 * Controls:
 * - left stick: drive, strafe, and turn
 * - back: toggle robot-centric / field-centric drive
 * - left bumper: slow speed scale
 * - right bumper: full speed scale
 * - A: intake forward
 * - B: reverse intake
 * - X: stop intake
 * - Y: reset yaw for field-centric testing
 *
 * Safety notes:
 * - Put the robot on blocks before trying the motor tests
 * - Start at low speed first
 * - Use X to stop the intake immediately
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "Test: Motor Operations", group = "Test")
public class TestMotorOperations extends LinearOpMode {
    private enum DriveMode {
        ROBOT_CENTRIC,
        FIELD_CENTRIC
    }

    private Robot robot;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;

    private DriveMode driveMode = DriveMode.ROBOT_CENTRIC;
    private boolean lastBack;
    private boolean lastA;
    private boolean lastB;
    private boolean lastX;
    private boolean lastY;

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        intake = robot.getIntake();

        telemetry.addLine("Motor operations test ready");
        telemetry.addLine("Left stick: drive/strafe/turn");
        telemetry.addLine("Back: toggle robot-centric / field-centric");
        telemetry.addLine("A: intake, B: reverse intake, X: stop intake, Y: reset yaw");
        telemetry.addLine("LB: slow speed, RB: full speed");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            handleDriveModeToggle();
            handleSpeedScale();
            handleIntakeCommands();
            applyDriveControl();
            publishTelemetry();
            idle();
        }

        robot.shutdown();
    }

    private void handleDriveModeToggle() {
        if (gamepad1.back && !lastBack) {
            driveMode = (driveMode == DriveMode.ROBOT_CENTRIC)
                    ? DriveMode.FIELD_CENTRIC
                    : DriveMode.ROBOT_CENTRIC;
        }

        if (gamepad1.y && !lastY) {
            robot.getHardware().resetYaw();
        }

        lastBack = gamepad1.back;
        lastY = gamepad1.y;
    }

    private void handleSpeedScale() {
        if (gamepad1.left_bumper) {
            drive.setSpeedScale(0.45);
        } else if (gamepad1.right_bumper) {
            drive.setSpeedScale(1.0);
        }
    }

    private void handleIntakeCommands() {
        if (gamepad1.a && !lastA) {
            intake.intake();
        } else if (gamepad1.b && !lastB) {
            intake.reverse();
        } else if (gamepad1.x && !lastX) {
            intake.stop();
        }

        lastA = gamepad1.a;
        lastB = gamepad1.b;
        lastX = gamepad1.x;
    }

    private void applyDriveControl() {
        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;
        double heading = robot.getHardware().getHeadingRadians();

        if (Math.abs(axial) < 0.05 && Math.abs(lateral) < 0.05 && Math.abs(yaw) < 0.05) {
            drive.stop();
            return;
        }

        switch (driveMode) {
            case ROBOT_CENTRIC:
                drive.driveRobotCentric(axial, lateral, yaw);
                break;
            case FIELD_CENTRIC:
                drive.driveFieldCentric(axial, lateral, yaw, heading);
                break;
        }
    }

    private void publishTelemetry() {
        telemetry.addData("Drive Mode", driveMode);
        telemetry.addData("Left Stick", "axial=%.2f lateral=%.2f yaw=%.2f",
                -gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        telemetry.addData("Drive Backend", drive.getBackendName());
        telemetry.addData("Drive Speed Scale", "%.2f", drive.getSpeedScale());
        telemetry.addData("Heading (rad)", "%.3f", robot.getHardware().getHeadingRadians());
        telemetry.addData("Motor Powers", drive.getMotorPowers());
        telemetry.addData("Intake Configured", intake.isConfigured());
        telemetry.addData("Intake State", intake.getState());
        telemetry.addData("Intake Status", intake.getStatus());
        telemetry.update();
    }
}

