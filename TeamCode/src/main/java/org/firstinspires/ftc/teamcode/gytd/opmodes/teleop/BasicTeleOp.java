package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;

@TeleOp(name = "Barebones TeleOp", group = "TeleOp")
public class BasicTeleOp extends LinearOpMode {
    private RobotHardware hardware;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware(hardwareMap);
        drive = new DriveSubsystem(hardware);
        intake = new IntakeSubsystem();

        telemetry.addLine("Barebones TeleOp scaffold initialized");
        telemetry.addData("Drive", drive.getStatus());
        telemetry.addData("Intake", intake.getStatus());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Barebones project ready for incremental build-out");
            telemetry.addData("Drive", drive.getStatus());
            telemetry.addData("Intake", intake.getStatus());
            telemetry.update();
            idle();
        }

        drive.stop();
        hardware.shutdown();
    }
}

