package org.firstinspires.ftc.teamcode.gytd.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;

@Autonomous(name = "Auto Basic", group = "AutoOp")
public class AutonomousBasic extends LinearOpMode {
    private RobotHardware hardware;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware(hardwareMap);
        drive = new DriveSubsystem(hardware);
        intake = new IntakeSubsystem();

        telemetry.addLine("Autonomous initialized");
        telemetry.addData("Drive", drive.getStatus());
        telemetry.addData("Intake", intake.getStatus());
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            telemetry.addLine("Autonomous routine ready for implementation");
            telemetry.update();
            idle();
        }

        drive.stop();
        hardware.shutdown();
    }
}

