package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.VisionSubsystem;

@TeleOp(name = "GYTD Main TeleOp", group = "GYTD")
public class MainTeleOp extends LinearOpMode {
    private RobotHardware hardware;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private VisionSubsystem vision;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware(hardwareMap);
        drive = new DriveSubsystem();
        intake = new IntakeSubsystem();
        vision = new VisionSubsystem(hardwareMap);

        telemetry.addLine("GYTD TeleOp initialized");
        telemetry.addData("Drive", drive.getStatus());
        telemetry.addData("Intake", intake.getStatus());
        telemetry.addData("Vision", vision.getStatus());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Barebones project ready for incremental build-out");
            telemetry.addData("Drive", drive.getStatus());
            telemetry.addData("Intake", intake.getStatus());
            telemetry.addData("Vision", vision.getStatus());
            telemetry.update();
            idle();
        }

        vision.shutdown();
        hardware.shutdown();
    }
}




