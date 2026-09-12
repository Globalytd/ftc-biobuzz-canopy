package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.VisionSubsystem;

@TeleOp(name = "Robot-Centric TeleOp", group = "TeleOp")
public class RobotCentricTeleOp extends LinearOpMode {
    private RobotHardware hardware;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private VisionSubsystem vision;

    @Override
    public void runOpMode() {
        hardware = new RobotHardware(hardwareMap);
        drive = new DriveSubsystem(hardware);
        intake = new IntakeSubsystem();
        vision = new VisionSubsystem(hardwareMap);

        telemetry.addLine("Robot-Centric TeleOp initialized");
        telemetry.addData("Drive", drive.getStatus());
        telemetry.addData("Intake", intake.getStatus());
        telemetry.addData("Vision", vision.getStatus());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            if (gamepad1.left_bumper) {
                drive.setSpeedScale(0.45);
            } else if (gamepad1.right_bumper) {
                drive.setSpeedScale(1.0);
            }

            drive.driveRobotCentric(axial, lateral, yaw);

            telemetry.addLine("Mecanum drive active");
            telemetry.addData("Drive", drive.getStatus());
            telemetry.addData("Scale", drive.getSpeedScale());
            telemetry.addData("MotorPowers", drive.getMotorPowers());
            telemetry.addData("Intake", intake.getStatus());
            telemetry.addData("Vision", vision.getStatus());
            telemetry.update();
            idle();
        }

        drive.stop();
        vision.shutdown();
        hardware.shutdown();
    }
}





