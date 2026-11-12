package org.firstinspires.ftc.teamcode.gytd.subsystems;

/**
 * Tunable defaults for {@link LauncherSubsystem}.
 *
 * DEFAULT_TARGET_VELOCITY_RAD_PER_SEC is a conservative placeholder, NOT a measured value.
 * Before trusting this on the field:
 *   1. Run `Test: Launcher`.
 *   2. Use D-pad up/down to find the velocity that gives a good, repeatable shot.
 *   3. Put that measured value here so every TeleOp starts the launcher at the right speed.
 *
 * Why this file exists: previously nothing in the real TeleOp ever called
 * {@link LauncherSubsystem#setTargetVelocity(double)}, so the launcher's target velocity stayed at
 * its built-in default of 0.0 forever - meaning pressing the "spin up" button commanded the motor
 * to hold 0 velocity (effectively off) instead of actually launching. Applying this constant once
 * at OpMode init fixes that.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public final class LauncherConstants {
    private LauncherConstants() {
        // Utility class - no instantiation
    }

    // TODO: Replace with the real tuned value from Test: Launcher before competition use.
    public static final double DEFAULT_TARGET_VELOCITY_RAD_PER_SEC = 300.0;
}

