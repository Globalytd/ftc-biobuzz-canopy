package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util;

import com.pedropathing.math.Pose;
/**
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

public class TeleOpConstants {

    // Field reference tile center for Red Alliance starting tile C1.
    // This is the known, fixed starting pose for the RED Pedro assist OpModes.
    public static final Pose RED_HOME_POSE = new Pose(60.0, 12.0, Math.toRadians(90.0));

    // One-touch Red shooting pose at C1.
    // 123.7° points from C1 back toward A4.
    public static final Pose RED_SIDE_SHOOT_POSE = new Pose(60.0, 12.0, Math.toRadians(123.7));

    // BioBuzz arena location references from doc/arena/ARENA_LOCATIONS_REFERENCE.md.
    // These are FIXED, absolute tile-center poses. Once the robot knows where it is on the
    // field (via the Pedro localizer), these targets are valid from anywhere in the arena -
    // they are not computed relative to the robot's current pose.
    // C2 keeps the same heading as C1 (90 deg) since C1 -> C2 is a straight 24in forward move.
    public static final Pose RED_C2_GARDEN_SHOOT_POSE = new Pose(60.0, 36.0, Math.toRadians(90.0));
    public static final Pose RED_C5_LOADING_SHOOT_POSE = new Pose(60.0, 108.0, Math.toRadians(0.0));
    public static final Pose RED_A5_PARK_POSE = new Pose(12.0, 108.0, Math.toRadians(0.0));
    public static final Pose RED_B3_ROUTE_WAYPOINT_POSE = new Pose(24.0, 60.0, Math.toRadians(0.0));

    public static final Pose BLUE_D2_SHOOT_POSE = new Pose(84.0, 36.0, Math.toRadians(90.0));
    public static final Pose BLUE_D5_SHOOT_POSE = new Pose(84.0, 108.0, Math.toRadians(270.0));
    public static final Pose BLUE_F2_PARK_POSE = new Pose(132.0, 36.0, Math.toRadians(0.0));

    // Current Red-team Pedro placeholder routes.
    // These are temporary until the C1/C6 (red) and D1/D6 (blue) routes are finalized.
    public static final Pose LOADING_SIDE_SHOOT_POSE = new Pose(30.0, 40.0, Math.toRadians(180.0));
    public static final Pose LOADING_SIDE_CLEARANCE_WAYPOINT_POSE = new Pose(40.0, 0.0, Math.toRadians(90.0));
    public static final Pose GARDEN_SIDE_WAYPOINT_POSE = new Pose(40.0, 30.0, Math.toRadians(120.0));

    public static final double SHOOT_POSE_POSITION_TOLERANCE_IN = 2.0;
    public static final double SHOOT_POSE_HEADING_TOLERANCE_RAD = Math.toRadians(6.0);
    public static final double SHOOT_POSE_SETTLE_TIME_S = 0.30;
    public static final double ROUTE_WAYPOINT_SKIP_TOLERANCE_IN = 2.0;
    public static final double MID_PATH_DEVIATION_REPLAN_IN = 8.0;
    public static final double MID_PATH_REPLAN_COOLDOWN_S = 0.35;

    // If the robot is already within one tile-span of the target (e.g. C1 -> C2), drive straight
    // there instead of detouring through the B3 waypoint. Anything farther routes via B3 to stay
    // clear of the center hive.
    public static final double DIRECT_ROUTE_MAX_DISTANCE_IN = 30.0;

}
