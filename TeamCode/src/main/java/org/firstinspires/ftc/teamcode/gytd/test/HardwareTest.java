package org.firstinspires.ftc.teamcode.gytd.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;

@TeleOp(name = "Test: Hardware", group = "Test")
public class HardwareTest extends LinearOpMode {
    private RobotHardware hardware;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware(hardwareMap);

        telemetry.addLine("Hardware Test Mode");
        telemetry.addLine("Add hardware tests here incrementally");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Hardware test running...");
            telemetry.update();
            idle();
        }

        hardware.shutdown();
    }
}

