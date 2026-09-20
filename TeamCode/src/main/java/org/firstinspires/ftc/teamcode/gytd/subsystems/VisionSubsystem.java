package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.vision.VisionPipeline;

/**
 * Handles camera/vision behavior for the robot.
 * In Phase 1 this stores placeholder target values for test telemetry.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class VisionSubsystem {
    private final HardwareMap hardwareMap;
    private VisionPipeline pipeline;
    private boolean isEnabled;

    // Placeholder target data until VisionPortal + AprilTagProcessor is wired.
    private int targetTagId = -1;
    private boolean tagVisible = false;
    private double targetBearing = 0.0;
    private double targetRange = 0.0;
    private double targetYaw = 0.0;

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

    public void setTargetTagId(int targetTagId) {
        this.targetTagId = targetTagId;
    }

    public int getTargetTag() {
        return targetTagId;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isTagVisible() {
        return tagVisible;
    }

    public double getTargetBearing() {
        return targetBearing;
    }

    public double getTargetRange() {
        return targetRange;
    }

    public double getTargetYaw() {
        return targetYaw;
    }

    public void clearTargetData() {
        // Reset to "no target" values.
        tagVisible = false;
        targetBearing = 0.0;
        targetRange = 0.0;
        targetYaw = 0.0;
    }

    public String getStatus() {
        if (!isEnabled) {
            return "disabled";
        }
        return pipeline != null ? "active" : "ready";
    }

    public void shutdown() {
        clearTargetData();
        if (pipeline != null) {
            pipeline.releaseResources();
        }
        isEnabled = false;
    }
}
