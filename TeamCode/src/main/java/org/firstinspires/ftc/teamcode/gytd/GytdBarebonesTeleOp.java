package org.firstinspires.ftc.teamcode.gytd;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.intake.IntakeSubsystem;

@TeleOp(name = "GYTD Barebones TeleOp", group = "GYTD")
public class GytdBarebonesTeleOp extends LinearOpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;

    @Override
    public void runOpMode() {
        drive = new DriveSubsystem();
        intake = new IntakeSubsystem();

        telemetry.addLine("GYTD scaffold initialized");
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
    }
}

