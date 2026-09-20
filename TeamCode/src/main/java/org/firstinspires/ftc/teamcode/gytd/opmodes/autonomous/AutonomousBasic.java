package org.firstinspires.ftc.teamcode.gytd.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;

/**
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

@Autonomous(name = "Auto Basic", group = "AutoOp")
public class AutonomousBasic extends LinearOpMode {
    private Robot robot;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        intake = robot.getIntake();

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

        robot.shutdown();
    }
}
