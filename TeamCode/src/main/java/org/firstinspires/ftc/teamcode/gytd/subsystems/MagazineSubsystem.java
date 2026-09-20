package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * Controls storage and feeding of game pieces between intake and shooter.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class MagazineSubsystem {
    // Simple state list so one command is active at a time.
    public enum MagazineState {
        IDLE,
        LOADING,
        FEEDING,
        REVERSING
    }

    // Starter powers for tuning.
    private static final double LOAD_POWER = 0.6;
    private static final double FEED_POWER = 0.8;
    private static final double REVERSE_POWER = -0.6;

    private final DcMotorEx magazineMotor;
    private MagazineState state = MagazineState.IDLE;

    // Approximate count until real sensors are added.
    private int ballCount = 0;

    public MagazineSubsystem() {
        this(null);
    }

    public MagazineSubsystem(DcMotorEx magazineMotor) {
        this.magazineMotor = magazineMotor;
    }

    public void load() {
        state = MagazineState.LOADING;
        setPower(LOAD_POWER);

        // Placeholder counting logic.
        if (ballCount < 5) {
            ballCount++;
        }
    }

    public void feedOne() {
        state = MagazineState.FEEDING;
        setPower(FEED_POWER);

        // Placeholder counting logic.
        if (ballCount > 0) {
            ballCount--;
        }
    }

    public void reverse() {
        state = MagazineState.REVERSING;
        setPower(REVERSE_POWER);
    }

    public void stop() {
        state = MagazineState.IDLE;
        setPower(0.0);
    }

    public boolean isLoaded() {
        return ballCount > 0;
    }

    public int getBallCount() {
        return ballCount;
    }

    public MagazineState getState() {
        return state;
    }

    public boolean isConfigured() {
        return magazineMotor != null;
    }

    public String getStatus() {
        return isConfigured() ? state.name().toLowerCase() : "unconfigured";
    }

    private void setPower(double power) {
        // Safe when hardware is still a placeholder.
        if (magazineMotor != null) {
            magazineMotor.setPower(power);
        }
    }
}
