package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.ShooterSubsystem;

/**
 * Shooter spin-up and velocity tuning test.
 * Use this OpMode to adjust target speed and confirm shooter readiness logic.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Shooter motor must be wired to the robot and mapped in code when available
 *
 * Controls:
 * - gamepad1 dpad_up: increase target velocity by 2.0
 * - gamepad1 dpad_down: decrease target velocity by 2.0 (minimum 0)
 * - gamepad1 a: start shooter
 * - gamepad1 b: stop shooter
 *
 * Notes:
 * - Telemetry "Configured" is true only after a real shooter motor is passed into ShooterSubsystem
 * - Shooter velocity methods currently use AngleUnit.RADIANS internally; tune with consistent units
 *
 * Safety notes:
 * - Keep game pieces out of the shooter until spin direction and speed are verified
 * - Start with low target values and increase gradually
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Shooter", group = "Test")
public class TestShooter extends LinearOpMode {
    // Small step so speed can be tuned gradually.
    private static final double VELOCITY_STEP = 2.0;

    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        ShooterSubsystem shooter = robot.getShooter();

        double targetVelocity = 0.0;
        boolean lastUp = false;
        boolean lastDown = false;

        telemetry.addLine("Shooter test ready");
        telemetry.addLine("D-pad up/down: target velocity, A: start, B: stop");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean upPressed = gamepad1.dpad_up;
            boolean downPressed = gamepad1.dpad_down;

            // Edge detect so each tap changes speed only once.
            if (upPressed && !lastUp) {
                targetVelocity += VELOCITY_STEP;
                shooter.setTargetVelocity(targetVelocity);
            }
            if (downPressed && !lastDown) {
                targetVelocity = Math.max(0.0, targetVelocity - VELOCITY_STEP);
                shooter.setTargetVelocity(targetVelocity);
            }
            lastUp = upPressed;
            lastDown = downPressed;

            if (gamepad1.a) {
                shooter.startShooter();
            } else if (gamepad1.b) {
                shooter.stopShooter();
            }

            shooter.updateState();

            telemetry.addData("Configured", shooter.isConfigured());
            telemetry.addData("Shooter State", shooter.getState());
            telemetry.addData("Target Velocity", shooter.getTargetVelocity());
            telemetry.addData("Actual Velocity", shooter.getVelocity());
            telemetry.addData("Velocity Error", shooter.getVelocityError());
            telemetry.addData("Shooter Ready", shooter.atTargetVelocity());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}
