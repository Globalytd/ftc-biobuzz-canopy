package org.firstinspires.ftc.teamcode.gytd.pathing;

/**
 * Calibration constants for {@link MecanumEncoderIMULocalizer}.
 *
 * TICKS_TO_INCHES below is a rough starting estimate (goBILDA 5203-series 312 RPM motor,
 * ~537.7 ticks/rev, with a common 96mm/3.78in mecanum wheel diameter: (pi * 3.78) / 537.7).
 *
 * THIS MUST BE RE-MEASURED ON THE PHYSICAL ROBOT before trusting Pedro shoot-assist paths:
 *   1. Record assist.getPose() at a known starting tile.
 *   2. Drive manually (legacy/field-centric) in a straight line a measured distance
 *      (e.g. exactly 48in using a tape measure on the field).
 *   3. Compare the commanded distance to the Pedro pose delta (position error telemetry already
 *      added to RedTeamPedroTeleOp).
 *   4. Scale TICKS_TO_INCHES by (actualDistance / reportedDistance) and repeat until the reported
 *      distance matches the measured distance within ~1 inch over several feet of travel.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public final class PedroMecanumLocalizerConstants {
    private PedroMecanumLocalizerConstants() {
        // Utility class - no instantiation
    }

    public static final double TICKS_TO_INCHES = 0.0221;
}

