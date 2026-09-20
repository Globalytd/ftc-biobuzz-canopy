package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * Controls the intake mechanism that pulls game pieces into the robot.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class IntakeSubsystem {
    // These are the only three intake modes we allow.
    public enum IntakeState {
        OFF,
        INTAKING,
        REVERSING
    }

    // Starter powers for testing. Tune later on the field.
    private static final double DEFAULT_INTAKE_POWER = 0.9;
    private static final double DEFAULT_REVERSE_POWER = -0.7;

    private final DcMotorEx intakeMotor;
    private IntakeState state = IntakeState.OFF;

    public IntakeSubsystem() {
        this(null);
    }

    public IntakeSubsystem(DcMotorEx intakeMotor) {
        this.intakeMotor = intakeMotor;
    }

    /**
     * Set the intake to pull in a game piece.
     */
    public void intake() {
        state = IntakeState.INTAKING;
        setPower(DEFAULT_INTAKE_POWER);
    }

    /**
     * Set the intake to push out a game piece.
     */
    public void reverse() {
        state = IntakeState.REVERSING;
        setPower(DEFAULT_REVERSE_POWER);
    }

    /**
     * Turn off the intake.
     */
    public void stop() {
        state = IntakeState.OFF;
        setPower(0.0);
    }

    public void setPower(double power) {
        // Safe when no hardware is assigned yet.
        if (intakeMotor != null) {
            intakeMotor.setPower(power);
        }
    }

    public boolean hasGamePiece() {
        // TODO: Replace with real sensor logic once sensor is configured.
        return false;
    }

    public IntakeState getState() {
        return state;
    }

    public boolean isConfigured() {
        return intakeMotor != null;
    }

    public String getStatus() {
        return isConfigured() ? state.name().toLowerCase() : "unconfigured";
    }
}
