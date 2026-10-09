package org.firstinspires.ftc.teamcode.gytd.pathing;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;

/**
 * Legacy mecanum drive backend.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class LegacyDriveBackend extends AbstractMecanumDriveBackend {
    public LegacyDriveBackend(RobotHardware hardware) {
        super(hardware);
    }

    @Override
    public String getBackendName() {
        return "legacy";
    }
}

