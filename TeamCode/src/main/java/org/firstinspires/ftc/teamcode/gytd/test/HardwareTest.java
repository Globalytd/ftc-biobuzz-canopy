package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;

@TeleOp(name = "Test: Hardware", group = "Test")
public class HardwareTest extends LinearOpMode {
    private RobotHardware hardware;
    private int testStage = 0;
    private static final double TEST_POWER = 0.3;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware(hardwareMap);

        telemetry.addLine("=== HARDWARE TEST MODE ===");
        telemetry.addLine("Drive Motors: Front Left, Front Right, Back Left, Back Right");
        telemetry.addLine("Intake Motor: Intake mechanism control");
        telemetry.addLine("IMU Sensor: Yaw angle reading");
        telemetry.addLine("Press Play to start testing each motor");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            displayTestStatus();
            handleMotorTests();
            telemetry.update();
            idle();
        }

        hardware.shutdown();
    }

    private void displayTestStatus() {
        telemetry.addLine("\n=== MOTOR & SENSOR STATUS ===");

        // Drive motor power readings
        telemetry.addData("FL Power", String.format("%.2f", hardware.getFrontLeftDrive().getPower()));
        telemetry.addData("FR Power", String.format("%.2f", hardware.getFrontRightDrive().getPower()));
        telemetry.addData("BL Power", String.format("%.2f", hardware.getBackLeftDrive().getPower()));
        telemetry.addData("BR Power", String.format("%.2f", hardware.getBackRightDrive().getPower()));

        // Intake motor power reading
        telemetry.addData("Intake Power", String.format("%.2f", hardware.getIntakeMotor().getPower()));

        // IMU heading
        double yawDegrees = hardware.getHeadingRadians() * 180 / Math.PI;
        telemetry.addData("IMU Yaw (deg)", String.format("%.2f", yawDegrees));

        // Test information
        telemetry.addLine("\n=== TEST STAGE ===");
        switch (testStage) {
            case 0:
                telemetry.addLine("Stage 0: Idle - all motors at 0 power");
                break;
            case 1:
                telemetry.addLine("Stage 1: Testing FRONT LEFT motor");
                break;
            case 2:
                telemetry.addLine("Stage 2: Testing FRONT RIGHT motor");
                break;
            case 3:
                telemetry.addLine("Stage 3: Testing BACK LEFT motor");
                break;
            case 4:
                telemetry.addLine("Stage 4: Testing BACK RIGHT motor");
                break;
            case 5:
                telemetry.addLine("Stage 5: Testing ALL DRIVE motors together");
                break;
            case 6:
                telemetry.addLine("Stage 6: Testing INTAKE motor");
                break;
            case 7:
                telemetry.addLine("Stage 7: Idle - all motors stopped");
                break;
        }
        telemetry.addLine("Press dpad up/down to cycle through stages");
    }

    private void handleMotorTests() {
        // Cycle through test stages with gamepad input
        if (gamepad1.dpad_up) {
            testStage++;
            if (testStage > 7) {
                testStage = 7;
            }
            sleep(200); // Debounce
        } else if (gamepad1.dpad_down) {
            testStage--;
            if (testStage < 0) {
                testStage = 0;
            }
            sleep(200); // Debounce
        }

        // Execute test based on current stage
        switch (testStage) {
            case 0:
                // Idle - do nothing
                hardware.getFrontLeftDrive().setPower(0.0);
                hardware.getFrontRightDrive().setPower(0.0);
                hardware.getBackLeftDrive().setPower(0.0);
                hardware.getBackRightDrive().setPower(0.0);
                hardware.getIntakeMotor().setPower(0.0);
                break;
            case 1:
                // Test front left
                hardware.getFrontLeftDrive().setPower(TEST_POWER);
                hardware.getFrontRightDrive().setPower(0.0);
                hardware.getBackLeftDrive().setPower(0.0);
                hardware.getBackRightDrive().setPower(0.0);
                hardware.getIntakeMotor().setPower(0.0);
                break;
            case 2:
                // Test front right
                hardware.getFrontLeftDrive().setPower(0.0);
                hardware.getFrontRightDrive().setPower(TEST_POWER);
                hardware.getBackLeftDrive().setPower(0.0);
                hardware.getBackRightDrive().setPower(0.0);
                hardware.getIntakeMotor().setPower(0.0);
                break;
            case 3:
                // Test back left
                hardware.getFrontLeftDrive().setPower(0.0);
                hardware.getFrontRightDrive().setPower(0.0);
                hardware.getBackLeftDrive().setPower(TEST_POWER);
                hardware.getBackRightDrive().setPower(0.0);
                hardware.getIntakeMotor().setPower(0.0);
                break;
            case 4:
                // Test back right
                hardware.getFrontLeftDrive().setPower(0.0);
                hardware.getFrontRightDrive().setPower(0.0);
                hardware.getBackLeftDrive().setPower(0.0);
                hardware.getBackRightDrive().setPower(TEST_POWER);
                hardware.getIntakeMotor().setPower(0.0);
                break;
            case 5:
                // Test all drive motors
                hardware.getFrontLeftDrive().setPower(TEST_POWER);
                hardware.getFrontRightDrive().setPower(TEST_POWER);
                hardware.getBackLeftDrive().setPower(TEST_POWER);
                hardware.getBackRightDrive().setPower(TEST_POWER);
                hardware.getIntakeMotor().setPower(0.0);
                break;
            case 6:
                // Test intake motor
                hardware.getFrontLeftDrive().setPower(0.0);
                hardware.getFrontRightDrive().setPower(0.0);
                hardware.getBackLeftDrive().setPower(0.0);
                hardware.getBackRightDrive().setPower(0.0);
                hardware.getIntakeMotor().setPower(TEST_POWER);
                break;
            case 7:
                // Stop all motors
                hardware.getFrontLeftDrive().setPower(0.0);
                hardware.getFrontRightDrive().setPower(0.0);
                hardware.getBackLeftDrive().setPower(0.0);
                hardware.getBackRightDrive().setPower(0.0);
                hardware.getIntakeMotor().setPower(0.0);
                break;
        }
    }
}

