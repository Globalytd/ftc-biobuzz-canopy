package org.firstinspires.ftc.teamcode.gytd.pathing;

/**
 * Shared drive backend contract for legacy drive and Pedro Pathing.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public interface DriveBackend {
    void driveRobotCentric(double axial, double lateral, double yaw);

    void driveFieldCentric(double axial, double lateral, double yaw, double headingRadians);

    void setSpeedScale(double speedScale);

    double getSpeedScale();

    void stop();

    String getMotorPowers();

    String getStatus();

    String getBackendName();
}

