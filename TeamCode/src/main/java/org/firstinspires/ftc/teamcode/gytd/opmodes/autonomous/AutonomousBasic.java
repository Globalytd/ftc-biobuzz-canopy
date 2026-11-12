package org.firstinspires.ftc.teamcode.gytd.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherSubsystem;

/**
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@Autonomous(name = "Auto Basic", group = "AutoOp")
public class AutonomousBasic extends LinearOpMode {
    private static final double WHEEL_DIAMETER_INCHES = 3.78;
    private static final double TICKS_PER_MOTOR_REV = 537.7;
    private static final double DRIVE_GEAR_RATIO = 1.0;
    private static final double DRIVE_POWER = 0.40;
    private static final double DRIVE_TIMEOUT_SECONDS = 4.0;

    private static final double LAUNCHER_TARGET_RAD_PER_SEC = 220.0;
    private static final long LAUNCHER_SPIN_UP_MS = 1500;
    private static final long LAUNCHER_FIRE_MS = 400;

    private Robot robot;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private LauncherSubsystem launcher;

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        intake = robot.getIntake();
        launcher = robot.getLauncher();


        telemetry.addLine("Autonomous initialized");
        telemetry.addData("Drive", drive.getStatus());
        telemetry.addData("Intake", intake.getStatus());
        telemetry.addData("Launcher", launcher.getStatus());
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            robot.shutdown();
            return;
        }

        if (opModeIsActive()) {
            telemetry.addLine("Driving forward 24 inches");
            telemetry.update();
            driveForwardInches(24.0, DRIVE_POWER, DRIVE_TIMEOUT_SECONDS);

            if (opModeIsActive()) {
                telemetry.addLine("Triggering launcher");
                telemetry.update();
                triggerLauncher();
            }
        }

        robot.shutdown();
    }

    private void driveForwardInches(double inches, double power, double timeoutSeconds) {
        DcMotorEx frontLeft = robot.getHardware().getFrontLeftDrive();
        DcMotorEx frontRight = robot.getHardware().getFrontRightDrive();
        DcMotorEx backLeft = robot.getHardware().getBackLeftDrive();
        DcMotorEx backRight = robot.getHardware().getBackRightDrive();

        DcMotorEx[] driveMotors = new DcMotorEx[]{frontLeft, frontRight, backLeft, backRight};

        int ticksToTravel = inchesToTicks(inches);

        setDriveMode(driveMotors, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(driveMotors, DcMotor.RunMode.RUN_USING_ENCODER);

        frontLeft.setTargetPosition(ticksToTravel);
        frontRight.setTargetPosition(ticksToTravel);
        backLeft.setTargetPosition(ticksToTravel);
        backRight.setTargetPosition(ticksToTravel);

        setDriveMode(driveMotors, DcMotor.RunMode.RUN_TO_POSITION);

        double clippedPower = Math.max(0.0, Math.min(1.0, Math.abs(power)));
        for (DcMotorEx motor : driveMotors) {
            motor.setPower(clippedPower);
        }

        long deadlineMs = System.currentTimeMillis() + (long) (timeoutSeconds * 1000.0);
        while (opModeIsActive()
                && System.currentTimeMillis() < deadlineMs
                && (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
            telemetry.addData("Drive ticks", "%d / %d", frontLeft.getCurrentPosition(), ticksToTravel);
            telemetry.update();
            idle();
        }

        drive.stop();
        setDriveMode(driveMotors, DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void triggerLauncher() {
        if (!launcher.isConfigured()) {
            telemetry.addLine("Launcher not configured; skipping trigger");
            telemetry.update();
            return;
        }

        launcher.setTargetVelocity(LAUNCHER_TARGET_RAD_PER_SEC);
        launcher.startLauncher();

        long spinUpDeadlineMs = System.currentTimeMillis() + LAUNCHER_SPIN_UP_MS;
        while (opModeIsActive() && System.currentTimeMillis() < spinUpDeadlineMs) {
            launcher.updateState();
            telemetry.addData("Launcher state", launcher.getState());
            telemetry.addData("Launcher velocity", "%.1f", launcher.getVelocity());
            telemetry.update();
            idle();
        }

        if (opModeIsActive()) {
            launcher.markFiring();
            sleep(LAUNCHER_FIRE_MS);
        }

        launcher.stopLauncher();
    }

    private int inchesToTicks(double inches) {
        double wheelCircumference = Math.PI * WHEEL_DIAMETER_INCHES;
        double motorRevolutions = inches / wheelCircumference;
        return (int) Math.round(motorRevolutions * TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO);
    }

    private void setDriveMode(DcMotorEx[] motors, DcMotor.RunMode runMode) {
        for (DcMotorEx motor : motors) {
            motor.setMode(runMode);
        }
    }
}
