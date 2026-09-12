package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.VisionSubsystem;

@TeleOp(name = "GYTD Field-Centric TeleOp", group = "GYTD")
public class FieldCentricTeleOp extends LinearOpMode {
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

        telemetry.addLine("GYTD Field-Centric initialized");
        telemetry.addLine("Press Y to reset heading");
        telemetry.update();

        waitForStart();

        boolean lastY = false;

        while (opModeIsActive()) {
            boolean yPressed = gamepad1.y;
            if (yPressed && !lastY) {
                hardware.resetYaw();
            }
            lastY = yPressed;

            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;
            double heading = hardware.getHeadingRadians();

            if (gamepad1.left_bumper) {
                drive.setSpeedScale(0.45);
            } else if (gamepad1.right_bumper) {
                drive.setSpeedScale(1.0);
            }

            drive.driveFieldCentric(axial, lateral, yaw, heading);

            telemetry.addLine("Field-centric drive active");
            telemetry.addData("Heading (rad)", heading);
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

