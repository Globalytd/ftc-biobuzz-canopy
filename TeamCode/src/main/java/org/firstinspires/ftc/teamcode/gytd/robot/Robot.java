package org.firstinspires.ftc.teamcode.gytd.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.FlowerSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.MagazineSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.VisionSubsystem;

/**
 * Think of this class like the robot's "main control box".
 * OpModes ask this class for subsystems instead of creating everything themselves.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class Robot {
    private final RobotHardware hardware;
    private final DriveSubsystem drive;
    private final IntakeSubsystem intake;
    private final MagazineSubsystem magazine;
    private final ShooterSubsystem shooter;
    private final FlowerSubsystem flower;
    private final VisionSubsystem vision;

    public Robot(HardwareMap hardwareMap) {
        this.hardware = new RobotHardware(hardwareMap);

        // Drive + intake are connected to hardware that already exists.
        this.drive = new DriveSubsystem(hardware);
        this.intake = new IntakeSubsystem(hardware.getIntakeMotor());
        this.vision = new VisionSubsystem(hardwareMap);

        // These are safe placeholders until final hardware names are provided.
        this.magazine = new MagazineSubsystem();
        this.shooter = new ShooterSubsystem();
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

    public MagazineSubsystem getMagazine() {
        return magazine;
    }

    public ShooterSubsystem getShooter() {
        return shooter;
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
        magazine.stop();
        shooter.stopShooter();
        flower.stop();
    }

    public void shutdown() {
        // End-of-OpMode cleanup.
        stopAll();
        vision.shutdown();
        hardware.shutdown();
    }
}
