package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Controls the launcher wheel speed (formerly "shooter").
 * This uses target velocity (speed) instead of only raw motor power.
 *
 * IMPORTANT: {@link #DEFAULT_TARGET_VELOCITY} starts at 0.0 on purpose - a brand new
 * LauncherSubsystem will not spin until something calls {@link #setTargetVelocity(double)} with a
 * real, non-zero value. See {@code LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC} for the
 * value TeleOps should apply at init time, and {@code Test: Launcher} for tuning it on the robot.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class LauncherSubsystem {
    public enum LauncherState {
        OFF,
        SPINNING_UP,
        READY,
        FIRING,
        REVERSING
    }

    // Tune these during testing.
    private static final double DEFAULT_TARGET_VELOCITY = 0.0;
    private static final double DEFAULT_VELOCITY_TOLERANCE = 50.0;
    // Scale applied to the forward target velocity when manually reversing the launcher wheel
    // (e.g. to clear a jammed game piece). Negative so the motor spins backward; magnitude is
    // reduced because reversing does not need to be as fast as launching.
    private static final double REVERSE_VELOCITY_SCALE = -0.5;

    private final DcMotorEx launcherMotor;
    private LauncherState state = LauncherState.OFF;
    private double targetVelocity = DEFAULT_TARGET_VELOCITY;
    private double velocityTolerance = DEFAULT_VELOCITY_TOLERANCE;

    public LauncherSubsystem() {
        this(null);
    }

    public LauncherSubsystem(DcMotorEx launcherMotor) {
        this.launcherMotor = launcherMotor;
    }

    public void setTargetVelocity(double ticksPerSecond) {
        targetVelocity = Math.max(0.0, ticksPerSecond);

        // If launcher is already running, apply new speed right away.
        if (state != LauncherState.OFF) {
            applyVelocityTarget();
        }
    }

    public void setVelocityTolerance(double toleranceTicksPerSecond) {
        velocityTolerance = Math.max(0.0, toleranceTicksPerSecond);
    }

    public void startLauncher() {
        state = LauncherState.SPINNING_UP;
        applyVelocityTarget();
        updateState();
    }

    public void stopLauncher() {
        state = LauncherState.OFF;
        if (launcherMotor != null) {
            launcherMotor.setPower(0.0);
        }
    }

    /**
     * Spins the launcher wheel backward at a reduced speed. Useful for clearing a jammed game
     * piece out of the gravity-fed chute without having to fully launch it.
     */
    public void reverseLauncher() {
        state = LauncherState.REVERSING;
        if (launcherMotor != null) {
            double reverseVelocity = targetVelocity > 0.0
                    ? targetVelocity * REVERSE_VELOCITY_SCALE
                    : LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC * REVERSE_VELOCITY_SCALE;
            launcherMotor.setVelocity(reverseVelocity, AngleUnit.RADIANS);
        }
    }

    public double getVelocity() {
        if (launcherMotor == null) {
            return 0.0;
        }
        return launcherMotor.getVelocity(AngleUnit.RADIANS);
    }

    public boolean atTargetVelocity() {
        // "Ready" means current speed is close enough to target speed.
        return Math.abs(targetVelocity - getVelocity()) <= velocityTolerance;
    }

    public void markFiring() {
        state = LauncherState.FIRING;
    }

    public void updateState() {
        if (state == LauncherState.OFF || state == LauncherState.FIRING || state == LauncherState.REVERSING) {
            return;
        }
        state = atTargetVelocity() ? LauncherState.READY : LauncherState.SPINNING_UP;
    }

    public LauncherState getState() {
        return state;
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    public double getVelocityError() {
        return targetVelocity - getVelocity();
    }

    public boolean isConfigured() {
        return launcherMotor != null;
    }

    public String getStatus() {
        return isConfigured() ? state.name().toLowerCase() : "unconfigured";
    }

    private void applyVelocityTarget() {
        if (launcherMotor != null) {
            launcherMotor.setVelocity(targetVelocity, AngleUnit.RADIANS);
        }
    }
}


