package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;

/**
 * Manual intake behavior test for forward, reverse, and stop commands.
 * Use this OpMode to verify button mapping and intake motor direction.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Intake motor named intake_motor
 *
 * Controls:
 * - gamepad1 a: intake (pull in game piece)
 * - gamepad1 b: reverse (eject)
 * - gamepad1 x: stop
 *
 * Notes:
 * - Telemetry field "Configured" should be true when intake motor is mapped correctly
 * - "Has Game Piece" is placeholder logic and remains false until sensor wiring is added
 *
 * Safety notes:
 * - Keep fingers and loose cable ends away from rollers before pressing A/B
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Intake", group = "Test")
public class TestIntake extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        IntakeSubsystem intake = robot.getIntake();

        telemetry.addLine("Intake test ready");
        telemetry.addLine("A: intake, B: reverse, X: stop");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Simple button mapping so students can test one action at a time.
            if (gamepad1.a) {
                intake.intake();
            } else if (gamepad1.b) {
                intake.reverse();
            } else if (gamepad1.x) {
                intake.stop();
            }

            // Telemetry explains what the subsystem thinks is happening.
            telemetry.addData("Configured", intake.isConfigured());
            telemetry.addData("State", intake.getState());
            telemetry.addData("Has Game Piece", intake.hasGamePiece());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}
