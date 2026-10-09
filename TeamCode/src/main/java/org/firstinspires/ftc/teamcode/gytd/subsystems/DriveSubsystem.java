package org.firstinspires.ftc.teamcode.gytd.subsystems;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.gytd.pathing.DriveBackend;
import org.firstinspires.ftc.teamcode.gytd.pathing.LegacyDriveBackend;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.pathing.PedroDriveBackend;

/**
 * Drive subsystem facade for the robot.
 * It now selects a backend so we can switch between legacy drive and Pedro Pathing later.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class DriveSubsystem {
    private final RobotHardware hardware;
    private final DriveBackend backend;

    public DriveSubsystem(RobotHardware hardware) {
        this.hardware = hardware;
        this.backend = PathingConfig.usePedroPathing()
                ? new PedroDriveBackend(hardware)
                : new LegacyDriveBackend(hardware);
    }

    public void clearCache() {
        hardware.clearBulkCache();
    }

    public void driveRobotCentric(double axial, double lateral, double yaw) {
        backend.driveRobotCentric(axial, lateral, yaw);
    }

    public void driveFieldCentric(double axial, double lateral, double yaw, double headingRadians) {
        backend.driveFieldCentric(axial, lateral, yaw, headingRadians);
    }

    public void setSpeedScale(double speedScale) {
        backend.setSpeedScale(speedScale);
    }

    public double getSpeedScale() {
        return backend.getSpeedScale();
    }

    public void stop() {
        backend.stop();
    }

    public String getMotorPowers() {
        return backend.getMotorPowers();
    }

    public String getStatus() {
        return backend.getStatus();
    }

    public String getBackendName() {
        return backend.getBackendName();
    }
}

