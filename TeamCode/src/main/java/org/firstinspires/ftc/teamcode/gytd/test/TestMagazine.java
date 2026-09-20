package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.MagazineSubsystem;

/**
 * Manual magazine state test for load, feed, reverse, and stop actions.
 * Use this OpMode to validate gamepad mapping while magazine hardware is tuned.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Magazine/indexer motor must be wired to the robot and mapped in code when available
 *
 * Controls:
 * - gamepad1 a: load state
 * - gamepad1 b: feed one state
 * - gamepad1 y: reverse state
 * - gamepad1 x: stop
 *
 * Notes:
 * - "Configured" is true only after a real magazine motor is passed into MagazineSubsystem
 * - "Approx Count" is placeholder software counting and not sensor-confirmed yet
 *
 * Safety notes:
 * - Run without game pieces first to verify direction before full feed testing
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Magazine", group = "Test")
public class TestMagazine extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        MagazineSubsystem magazine = robot.getMagazine();

        telemetry.addLine("Magazine test ready");
        telemetry.addLine("A: load, B: feed one, Y: reverse, X: stop");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Run one magazine action at a time.
            if (gamepad1.a) {
                magazine.load();
            } else if (gamepad1.b) {
                magazine.feedOne();
            } else if (gamepad1.y) {
                magazine.reverse();
            } else if (gamepad1.x) {
                magazine.stop();
            }

            // Show both mode and estimated piece count.
            telemetry.addData("Configured", magazine.isConfigured());
            telemetry.addData("State", magazine.getState());
            telemetry.addData("Loaded", magazine.isLoaded());
            telemetry.addData("Approx Count", magazine.getBallCount());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}
