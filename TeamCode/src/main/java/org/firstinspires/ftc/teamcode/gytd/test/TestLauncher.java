package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherSubsystem;

/**
 * Launcher spin-up and velocity tuning test (formerly "Test: Shooter").
 * Use this OpMode to adjust target speed and confirm launcher readiness logic.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Launcher motor must be wired to the robot and mapped in code when available
 *
 * Controls:
 * - gamepad1 dpad_up: increase target velocity by 2.0
 * - gamepad1 dpad_down: decrease target velocity by 2.0 (minimum 0)
 * - gamepad1 a: start launcher
 * - gamepad1 b: stop launcher
 *
 * Notes:
 * - Telemetry "Configured" is true only after a real launcher motor is passed into LauncherSubsystem
 * - Launcher velocity methods currently use AngleUnit.RADIANS internally; tune with consistent units
 * - Once you find a good value here, copy it into LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC
 *   so real TeleOps start the launcher at the right speed automatically.
 *
 * Safety notes:
 * - Keep game pieces out of the launcher until spin direction and speed are verified
 * - Start with low target values and increase gradually
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Launcher", group = "Test")
public class TestLauncher extends LinearOpMode {
    // Small step so speed can be tuned gradually.
    private static final double VELOCITY_STEP = 2.0;

    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        LauncherSubsystem launcher = robot.getLauncher();

        double targetVelocity = 0.0;
        boolean lastUp = false;
        boolean lastDown = false;

        telemetry.addLine("Launcher test ready");
        telemetry.addLine("D-pad up/down: target velocity, A: start, B: stop");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean upPressed = gamepad1.dpad_up;
            boolean downPressed = gamepad1.dpad_down;

            // Edge detect so each tap changes speed only once.
            if (upPressed && !lastUp) {
                targetVelocity += VELOCITY_STEP;
                launcher.setTargetVelocity(targetVelocity);
            }
            if (downPressed && !lastDown) {
                targetVelocity = Math.max(0.0, targetVelocity - VELOCITY_STEP);
                launcher.setTargetVelocity(targetVelocity);
            }
            lastUp = upPressed;
            lastDown = downPressed;

            if (gamepad1.a) {
                launcher.startLauncher();
            } else if (gamepad1.b) {
                launcher.stopLauncher();
            }

            launcher.updateState();

            telemetry.addData("Configured", launcher.isConfigured());
            telemetry.addData("Launcher State", launcher.getState());
            telemetry.addData("Target Velocity", launcher.getTargetVelocity());
            telemetry.addData("Actual Velocity", launcher.getVelocity());
            telemetry.addData("Velocity Error", launcher.getVelocityError());
            telemetry.addData("Launcher Ready", launcher.atTargetVelocity());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}


