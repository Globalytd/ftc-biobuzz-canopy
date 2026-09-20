package org.firstinspires.ftc.teamcode.gytd.subsystems;

/**
 * Placeholder for the Flower scoring mechanism.
 * We can plug in servo/motor commands later without changing OpModes.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class FlowerSubsystem {
    public enum FlowerState {
        STOWED,
        DEPLOYED,
        SCORING
    }

    private FlowerState state = FlowerState.STOWED;

    public void deploy() {
        state = FlowerState.DEPLOYED;
        // TODO: Add hardware command when mechanism is selected.
    }

    public void score() {
        state = FlowerState.SCORING;
        // TODO: Add hardware command when mechanism is selected.
    }

    public void retract() {
        state = FlowerState.STOWED;
        // TODO: Add hardware command when mechanism is selected.
    }

    public void stop() {
        // Safe default: do not move until we know the real mechanism.
    }

    public FlowerState getState() {
        return state;
    }

    public String getStatus() {
        return state.name().toLowerCase();
    }
}
