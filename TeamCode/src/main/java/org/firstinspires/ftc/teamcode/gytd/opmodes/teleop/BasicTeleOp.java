package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;

/**
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@TeleOp(name = "Barebones TeleOp", group = "TeleOp")
public class BasicTeleOp extends LinearOpMode {
    private Robot robot;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        intake = robot.getIntake();

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

        robot.shutdown();
    }
}
