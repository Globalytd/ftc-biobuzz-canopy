package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

/**
 * Verifies robot-centric mecanum drive mapping and IMU heading reset behavior.
 * Use this test to confirm wheel direction, strafing, turning, and speed scaling.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 *
 * Controls:
 * - gamepad1 left stick: translation (forward/back and strafe)
 * - gamepad1 right stick x: rotate (yaw)
 * - gamepad1 left bumper: slow mode (speed scale 0.45)
 * - gamepad1 right bumper: full mode (speed scale 1.0)
 * - gamepad1 y: reset IMU yaw heading
 *
 * Safety notes:
 * - Start with wheels off the ground to validate motor direction
 * - Verify heading reset before doing floor drive checks
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Mecanum Drive", group = "Test")
public class TestMecanumDrive extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        DriveSubsystem drive = robot.getDrive();
        RobotHardware hardware = robot.getHardware();

        telemetry.addLine("Mecanum test ready");
        telemetry.addLine("Y: reset heading, bumpers: speed scale");
        telemetry.update();

        waitForStart();

        boolean lastY = false;

        while (opModeIsActive()) {
            boolean yPressed = gamepad1.y;
            if (yPressed && !lastY) {
                hardware.resetYaw();
            }
            lastY = yPressed;

            // Left stick = move, right stick X = rotate.
            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            if (gamepad1.left_bumper) {
                drive.setSpeedScale(0.45);
            } else if (gamepad1.right_bumper) {
                drive.setSpeedScale(1.0);
            }

            drive.driveRobotCentric(axial, lateral, yaw);

            telemetry.addData("Heading (rad)", hardware.getHeadingRadians());
            telemetry.addData("Speed Scale", drive.getSpeedScale());
            telemetry.addData("Motor Powers", drive.getMotorPowers());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}
