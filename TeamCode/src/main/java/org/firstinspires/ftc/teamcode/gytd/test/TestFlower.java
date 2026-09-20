package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.FlowerSubsystem;

/**
 * Flower mechanism state test for deploy, score, and retract commands.
 * Use this OpMode to validate operator controls before final mechanism wiring.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Flower servo/motor hardware must be selected and mapped in FlowerSubsystem when implemented
 *
 * Controls:
 * - gamepad1 a: deploy
 * - gamepad1 b: score
 * - gamepad1 x: retract/stow
 *
 * Notes:
 * - Current FlowerSubsystem is state-only placeholder logic in Phase 1/2
 * - Telemetry reports intended state changes even before hardware commands are added
 *
 * Safety notes:
 * - After wiring real hardware, verify travel limits before running full-range motion
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Flower", group = "Test")
public class TestFlower extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        FlowerSubsystem flower = robot.getFlower();

        telemetry.addLine("Flower test ready");
        telemetry.addLine("A: deploy, B: score, X: retract");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // One button = one clear action.
            if (gamepad1.a) {
                flower.deploy();
            } else if (gamepad1.b) {
                flower.score();
            } else if (gamepad1.x) {
                flower.retract();
            }

            telemetry.addData("State", flower.getState());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}