package org.firstinspires.ftc.teamcode.gytd.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.FlowerSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.VisionSubsystem;

/**
 * Think of this class like the robot's "main control box".
 * OpModes ask this class for subsystems instead of creating everything themselves.
 *
 * NOTE (2026-11-08): the magazine/indexer subsystem was removed. The team is using a passive
 * gravity-fed chute from intake straight to the launcher wheel instead of a powered indexer motor,
 * so there is no "load"/"feed one"/"reverse magazine" step anymore - game pieces simply roll down
 * to the launcher wheel on their own once the intake has pulled them in.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class Robot {
    private final RobotHardware hardware;
    private final DriveSubsystem drive;
    private final IntakeSubsystem intake;
    private final LauncherSubsystem launcher;
    private final FlowerSubsystem flower;
    private final VisionSubsystem vision;

    public Robot(HardwareMap hardwareMap) {
        this.hardware = new RobotHardware(hardwareMap);

        // Drive + intake are connected to hardware that already exists.
        this.drive = new DriveSubsystem(hardware);
        this.intake = new IntakeSubsystem(hardware.getIntakeMotor());
        this.vision = new VisionSubsystem(hardwareMap);

        // These are safe placeholders until final hardware names are provided.
        this.launcher = new LauncherSubsystem(hardware.getLaunchMotor());
        this.flower = new FlowerSubsystem();
    }

    public RobotHardware getHardware() {
        return hardware;
    }

    public DriveSubsystem getDrive() {
        return drive;
    }

    public IntakeSubsystem getIntake() {
        return intake;
    }

    public LauncherSubsystem getLauncher() {
        return launcher;
    }

    public FlowerSubsystem getFlower() {
        return flower;
    }

    public VisionSubsystem getVision() {
        return vision;
    }

    public void stopAll() {
        // Emergency-safe stop for every mechanism in one call.
        drive.stop();
        intake.stop();
        launcher.stopLauncher();
        flower.stop();
    }

    public void shutdown() {
        // End-of-OpMode cleanup.
        stopAll();
        vision.shutdown();
        hardware.shutdown();
    }
}
