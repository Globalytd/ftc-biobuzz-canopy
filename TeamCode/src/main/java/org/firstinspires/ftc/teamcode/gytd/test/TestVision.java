package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.VisionSubsystem;

/**
 * Vision subsystem bring-up test focused on enable/telemetry flow.
 * Use this OpMode to confirm the vision subsystem lifecycle while AprilTag processing is still pending.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - For current placeholder telemetry: no camera is required
 * - For future AprilTag integration: add a webcam with the exact name used by VisionSubsystem wiring
 *
 * Controls:
 * - No gamepad controls are required for this test
 *
 * Notes:
 * - Telemetry values for tag ID/range/bearing/yaw are placeholders until AprilTag wiring is implemented
 * - This test still verifies that the subsystem can be enabled, updated, and shutdown safely
 *
 * Safety notes:
 * - Keep this test disabled during a match; it is for pit diagnostics and bring-up
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Test: Vision", group = "Test")
public class TestVision extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);
        VisionSubsystem vision = robot.getVision();

        vision.enable();

        telemetry.addLine("Vision test ready");
        telemetry.addLine("AprilTag integration is a Phase 3 task");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // These values are placeholders now and will become real after AprilTag wiring.
            telemetry.addData("Vision Status", vision.getStatus());
            telemetry.addData("Tag ID", vision.getTargetTag());
            telemetry.addData("Range", vision.getTargetRange());
            telemetry.addData("Bearing", vision.getTargetBearing());
            telemetry.addData("Yaw", vision.getTargetYaw());
            telemetry.addData("Visible", vision.isTagVisible());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }
}
