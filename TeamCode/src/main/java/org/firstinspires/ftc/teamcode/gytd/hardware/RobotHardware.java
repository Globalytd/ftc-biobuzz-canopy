package org.firstinspires.ftc.teamcode.gytd.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Central hardware container for the GYTD robot.
 * Holds references to all hardware devices and manages their lifecycle.
 */
public class RobotHardware {
    private final HardwareMap hardwareMap;

    public RobotHardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        init();
    }

    private void init() {
        // Hardware initialization will be added here incrementally
    }

    public HardwareMap getHardwareMap() {
        return hardwareMap;
    }

    public void shutdown() {
        // Cleanup logic will be added as hardware is added
    }
}

