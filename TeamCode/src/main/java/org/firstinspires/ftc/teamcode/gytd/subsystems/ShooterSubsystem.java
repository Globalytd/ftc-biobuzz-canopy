package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Controls the shooter wheel speed.
 * This uses target velocity (speed) instead of only raw motor power.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class ShooterSubsystem {
    public enum ShooterState {
        OFF,
        SPINNING_UP,
        READY,
        FIRING
    }

    // Tune these during testing.
    private static final double DEFAULT_TARGET_VELOCITY = 0.0;
    private static final double DEFAULT_VELOCITY_TOLERANCE = 50.0;

    private final DcMotorEx shooterMotor;
    private ShooterState state = ShooterState.OFF;
    private double targetVelocity = DEFAULT_TARGET_VELOCITY;
    private double velocityTolerance = DEFAULT_VELOCITY_TOLERANCE;

    public ShooterSubsystem() {
        this(null);
    }

    public ShooterSubsystem(DcMotorEx shooterMotor) {
        this.shooterMotor = shooterMotor;
    }

    public void setTargetVelocity(double ticksPerSecond) {
        targetVelocity = Math.max(0.0, ticksPerSecond);

        // If shooter is already running, apply new speed right away.
        if (state != ShooterState.OFF) {
            applyVelocityTarget();
        }
    }

    public void setVelocityTolerance(double toleranceTicksPerSecond) {
        velocityTolerance = Math.max(0.0, toleranceTicksPerSecond);
    }

    public void startShooter() {
        state = ShooterState.SPINNING_UP;
        applyVelocityTarget();
        updateState();
    }

    public void stopShooter() {
        state = ShooterState.OFF;
        if (shooterMotor != null) {
            shooterMotor.setPower(0.0);
        }
    }

    public double getVelocity() {
        if (shooterMotor == null) {
            return 0.0;
        }
        return shooterMotor.getVelocity(AngleUnit.RADIANS);
    }

    public boolean atTargetVelocity() {
        // "Ready" means current speed is close enough to target speed.
        return Math.abs(targetVelocity - getVelocity()) <= velocityTolerance;
    }

    public void markFiring() {
        state = ShooterState.FIRING;
    }

    public void updateState() {
        if (state == ShooterState.OFF || state == ShooterState.FIRING) {
            return;
        }
        state = atTargetVelocity() ? ShooterState.READY : ShooterState.SPINNING_UP;
    }

    public ShooterState getState() {
        return state;
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    public double getVelocityError() {
        return targetVelocity - getVelocity();
    }

    public boolean isConfigured() {
        return shooterMotor != null;
    }

    public String getStatus() {
        return isConfigured() ? state.name().toLowerCase() : "unconfigured";
    }

    private void applyVelocityTarget() {
        if (shooterMotor != null) {
            shooterMotor.setVelocity(targetVelocity, AngleUnit.RADIANS);
        }
    }
}
