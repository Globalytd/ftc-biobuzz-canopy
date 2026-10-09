package org.firstinspires.ftc.teamcode.gytd.pathing;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;

/**
 * Pedro seam backend for Phase 2.
 * This keeps the same safe drive behavior for now, while giving us a clean place to wire Pedro logic later.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class PedroDriveBackend extends AbstractMecanumDriveBackend {
    public PedroDriveBackend(RobotHardware hardware) {
        super(hardware);
    }

    @Override
    public String getBackendName() {
        return "pedro-seam";
    }
}

