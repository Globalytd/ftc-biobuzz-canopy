package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Tiny tile-mapping helper for field planning.
 * Home starts at A1 and drivers can step around the grid with D-pad presses.
 *
 * Configuration required:
 * - None. This OpMode does not use any hardware devices.
 *
 * Controls:
 * - D-pad: move one tile at a time
 * - A: reset to A1
 *
 * Safety notes:
 * - No drivetrain or actuators are driven by this OpMode; it is telemetry-only and safe to run
 *   without the robot on the field.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "Test: Arena Mapper", group = "Test")
public class TestArenaMapper extends LinearOpMode {
    private static final int MAX_COLUMNS = 6; // A-F
    private static final int MAX_ROWS = 6;    // 1-6

    @Override
    public void runOpMode() {
        int col = 0; // A
        int row = 0; // 1

        boolean lastUp = false;
        boolean lastDown = false;
        boolean lastLeft = false;
        boolean lastRight = false;
        boolean lastA = false;

        telemetry.addLine("Arena mapper ready");
        telemetry.addLine("Home tile: A1");
        telemetry.addLine("D-pad moves one tile, A resets");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean up = gamepad1.dpad_up;
            boolean down = gamepad1.dpad_down;
            boolean left = gamepad1.dpad_left;
            boolean right = gamepad1.dpad_right;
            boolean a = gamepad1.a;

            if (up && !lastUp) {
                row = Math.min(MAX_ROWS - 1, row + 1);
            }
            if (down && !lastDown) {
                row = Math.max(0, row - 1);
            }
            if (right && !lastRight) {
                col = Math.min(MAX_COLUMNS - 1, col + 1);
            }
            if (left && !lastLeft) {
                col = Math.max(0, col - 1);
            }
            if (a && !lastA) {
                col = 0;
                row = 0;
            }

            lastUp = up;
            lastDown = down;
            lastLeft = left;
            lastRight = right;
            lastA = a;

            char colLetter = (char) ('A' + col);
            int rowNumber = row + 1;

            telemetry.addData("Current Tile", "%c%d", colLetter, rowNumber);
            telemetry.addData("Home Tile", "A1");
            telemetry.addData("Grid", "A1 to F6");
            telemetry.addLine("Use D-pad to map, A to reset");
            telemetry.update();
            idle();
        }
    }
}

