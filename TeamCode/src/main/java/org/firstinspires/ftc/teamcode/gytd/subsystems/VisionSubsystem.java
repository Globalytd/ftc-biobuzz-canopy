package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.vision.VisionPipeline;

/**
 * Central vision subsystem for the GYTD robot.
 * Manages camera initialization and vision processing tasks.
 */
public class VisionSubsystem {
    private final HardwareMap hardwareMap;
    private VisionPipeline pipeline;
    private boolean isEnabled;

    public VisionSubsystem(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        this.isEnabled = false;
    }

    public void setPipeline(VisionPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public VisionPipeline getPipeline() {
        return pipeline;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getStatus() {
        if (!isEnabled) {
            return "disabled";
        }
        return pipeline != null ? "active" : "ready";
    }

    public void shutdown() {
        if (pipeline != null) {
            pipeline.releaseResources();
        }
    }
}

