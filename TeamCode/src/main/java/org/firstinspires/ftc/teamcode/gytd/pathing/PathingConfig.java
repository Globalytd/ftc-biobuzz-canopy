package org.firstinspires.ftc.teamcode.gytd.pathing;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Phase 2 switch for Pedro Pathing.
 * Keep this false until the Pedro pathing backend is fully proven.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public final class PathingConfig {
    private static boolean usePedroPathingEnabled = false;

    private PathingConfig() {
        // Utility class - no instantiation.
    }

    public static boolean usePedroPathing() {
        return usePedroPathingEnabled;
    }

    public static void setUsePedroPathing(boolean enabled) {
        usePedroPathingEnabled = enabled;
    }

    public static void resetToLegacyDrive() {
        usePedroPathingEnabled = false;
    }

    public static void applyDriverStationSelection(Gamepad gamepad) {
        if (gamepad == null) {
            return;
        }

        if (gamepad.x) {
            usePedroPathingEnabled = false;
        } else if (gamepad.b) {
            usePedroPathingEnabled = true;
        }
    }

    public static String getActiveBackendName() {
        return usePedroPathingEnabled ? "Pedro Pathing" : "Legacy Drive";
    }

    public static String getSelectionInstructions() {
        return "X = Legacy Drive, B = Pedro Pathing";
    }
}

