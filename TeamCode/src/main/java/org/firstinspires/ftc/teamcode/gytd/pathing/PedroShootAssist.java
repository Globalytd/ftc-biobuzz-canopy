package org.firstinspires.ftc.teamcode.gytd.pathing;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util.ShootingSide;
import org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util.TeleOpConstants;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Reusable one-touch "drive to shooting tile" assist built on top of a Pedro {@link Follower}.
 *
 * This encapsulates everything that used to live directly inside RedTeamPedroTeleOp:
 * - knowing the FIXED, absolute tile poses for each {@link ShootingSide}
 * - picking a direct straight-line route when already close to the target tile
 * - routing through a known waypoint tile (e.g. B3) when farther away, to avoid field hazards
 * - mid-path replanning if the robot gets bumped off its route
 * - holding the final shooting pose and reporting when it is safe to shoot
 *
 * Any field-centric TeleOp can own one instance of this class, call {@link #update(double)} once
 * per loop, and call {@link #start(ShootingSide)} / {@link #cancel()} from its own gamepad handling.
 * The OpMode stays responsible for drive/shooter/magazine control; this class only owns the
 * Pedro follower and the assist state machine.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class PedroShootAssist {

    public enum State {
        IDLE,
        FOLLOWING_PATH,
        HOLDING_SHOOTING_POSE,
        CANCELLED
    }

    private final Follower follower;
    private final Pose homePose;
    private final Pose routeWaypointPose;
    private final Map<ShootingSide, Pose> targetPosesBySide;

    private State state = State.IDLE;
    private ShootingSide activeSide;
    private Pose targetPose;

    private final List<Path> activeSegments = new ArrayList<>();
    private final List<Pose> activeSegmentStarts = new ArrayList<>();
    private final List<Pose> activeSegmentEnds = new ArrayList<>();
    private int activeSegmentIndex = 0;
    private double lastReplanTimeS = -10.0;

    private boolean pausedForPoseRecovery = false;
    private boolean shootingAllowed = false;
    private double poseStableStartTimeS = -1.0;

    public PedroShootAssist(HardwareMap hardwareMap, Pose homePose, Pose routeWaypointPose,
                             Map<ShootingSide, Pose> targetPosesBySide) {
        this.follower = PedroFollowerFactory.createTwoWheelFollower(hardwareMap);
        this.homePose = homePose;
        this.routeWaypointPose = routeWaypointPose;
        this.targetPosesBySide = new EnumMap<>(targetPosesBySide);
        this.targetPose = homePose;

        follower.setPose(homePose);
    }

    /** Convenience factory for the Red alliance Garden/Loading assist poses used today. */
    public static PedroShootAssist createRedAssist(HardwareMap hardwareMap) {
        Map<ShootingSide, Pose> targets = new EnumMap<>(ShootingSide.class);
        targets.put(ShootingSide.LOADING_SIDE, TeleOpConstants.RED_C5_LOADING_SHOOT_POSE);
        targets.put(ShootingSide.GARDEN_SIDE, TeleOpConstants.RED_C2_GARDEN_SHOOT_POSE);
        return new PedroShootAssist(hardwareMap, TeleOpConstants.RED_HOME_POSE,
                TeleOpConstants.RED_B3_ROUTE_WAYPOINT_POSE, targets);
    }

    /** Call once per OpMode loop, regardless of assist state. */
    public void update(double runtimeS) {
        if (!isActive()) {
            // Not currently driving - still keep the pose estimate fresh for telemetry/manual drive.
            follower.update();
            return;
        }

        if (state == State.FOLLOWING_PATH) {
            updateFollowingPath(runtimeS);
        } else if (state == State.HOLDING_SHOOTING_POSE) {
            updateHoldingPose(runtimeS);
        }
    }

    /** Start driving to the fixed, known tile pose for the given side, from wherever we are now. */
    public void start(ShootingSide side) {
        Pose target = targetPosesBySide.get(side);
        if (target == null) {
            return;
        }

        Pose currentPose = follower.pose();
        state = State.FOLLOWING_PATH;
        activeSide = side;
        targetPose = target;
        pausedForPoseRecovery = false;
        shootingAllowed = false;
        poseStableStartTimeS = -1.0;

        activeSegments.clear();
        activeSegments.addAll(buildSafeRoute(currentPose, target));
        activeSegmentIndex = 0;
        lastReplanTimeS = -10.0;

        if (!activeSegments.isEmpty()) {
            follower.follow(activeSegments.get(activeSegmentIndex));
        }
    }

    /** Immediately stop path following/holding and release Pedro control of the drive. */
    public void cancel() {
        follower.stop();
        activeSegments.clear();
        activeSegmentStarts.clear();
        activeSegmentEnds.clear();
        activeSegmentIndex = 0;
        pausedForPoseRecovery = false;
        shootingAllowed = false;
        poseStableStartTimeS = -1.0;
        state = State.CANCELLED;
    }

    /** Reset the localizer back to the known home/start tile pose (e.g. C1). */
    public void resetToHomePose() {
        follower.setPose(homePose);
    }

    public boolean isActive() {
        return state == State.FOLLOWING_PATH || state == State.HOLDING_SHOOTING_POSE;
    }

    public boolean isShootingAllowed() {
        return shootingAllowed;
    }

    public boolean isPausedForPoseRecovery() {
        return pausedForPoseRecovery;
    }

    public State getState() {
        return state;
    }

    public ShootingSide getActiveSide() {
        return activeSide;
    }

    public Pose getPose() {
        return follower.pose();
    }

    public Pose getTargetPose() {
        return targetPose;
    }

    public Follower getFollower() {
        return follower;
    }

    public int getSegmentIndex() {
        return activeSegmentIndex;
    }

    public int getSegmentCount() {
        return activeSegments.size();
    }

    public double getCurrentSegmentDeviationInches() {
        if (activeSegmentIndex < 0 || activeSegmentIndex >= activeSegmentStarts.size()) {
            return 0.0;
        }
        Pose currentPose = follower.pose();
        Pose segmentStart = activeSegmentStarts.get(activeSegmentIndex);
        Pose segmentEnd = activeSegmentEnds.get(activeSegmentIndex);
        return pointToSegmentDistance(currentPose.x(), currentPose.y(), segmentStart.x(), segmentStart.y(),
                segmentEnd.x(), segmentEnd.y());
    }

    public double getPositionErrorInches() {
        Pose currentPose = follower.pose();
        double deltaX = targetPose.x() - currentPose.x();
        double deltaY = targetPose.y() - currentPose.y();
        return Math.hypot(deltaX, deltaY);
    }

    public double getHeadingErrorRadians() {
        Pose currentPose = follower.pose();
        double headingError = targetPose.heading() - currentPose.heading();
        while (headingError > Math.PI) {
            headingError -= 2.0 * Math.PI;
        }
        while (headingError < -Math.PI) {
            headingError += 2.0 * Math.PI;
        }
        return headingError;
    }

    // ---- internal state machine ----

    private void updateFollowingPath(double runtimeS) {
        follower.update();

        if (activeSegmentIndex < activeSegmentStarts.size()
                && getCurrentSegmentDeviationInches() > TeleOpConstants.MID_PATH_DEVIATION_REPLAN_IN
                && (runtimeS - lastReplanTimeS) >= TeleOpConstants.MID_PATH_REPLAN_COOLDOWN_S) {
            replanFromCurrentPose(runtimeS);
            return;
        }

        if (activeSegmentIndex >= activeSegments.size()) {
            finishPath();
            return;
        }

        if (follower.atParametricEnd()) {
            activeSegmentIndex++;
            if (activeSegmentIndex >= activeSegments.size()) {
                finishPath();
            } else {
                follower.follow(activeSegments.get(activeSegmentIndex));
            }
        }
    }

    private void updateHoldingPose(double runtimeS) {
        follower.update();

        if (!follower.holding()) {
            follower.hold(targetPose);
        }

        if (!isAtShootPose()) {
            pausedForPoseRecovery = true;
            shootingAllowed = false;
            poseStableStartTimeS = -1.0;
            return;
        }

        if (poseStableStartTimeS < 0.0) {
            poseStableStartTimeS = runtimeS;
        }

        if ((runtimeS - poseStableStartTimeS) >= TeleOpConstants.SHOOT_POSE_SETTLE_TIME_S) {
            pausedForPoseRecovery = false;
            shootingAllowed = true;
        }
    }

    private void finishPath() {
        follower.hold(targetPose);
        state = State.HOLDING_SHOOTING_POSE;
        pausedForPoseRecovery = true;
        shootingAllowed = false;
        poseStableStartTimeS = -1.0;
    }

    private boolean isAtShootPose() {
        return getPositionErrorInches() <= TeleOpConstants.SHOOT_POSE_POSITION_TOLERANCE_IN
                && Math.abs(getHeadingErrorRadians()) <= TeleOpConstants.SHOOT_POSE_HEADING_TOLERANCE_RAD;
    }

    private List<Path> buildSafeRoute(Pose startPose, Pose target) {
        ArrayList<Path> route = new ArrayList<>();
        activeSegmentStarts.clear();
        activeSegmentEnds.clear();

        if (distanceBetweenPoses(startPose, target) <= TeleOpConstants.DIRECT_ROUTE_MAX_DISTANCE_IN) {
            // Already adjacent to the target tile (e.g. C1 -> C2): go straight there.
            if (distanceBetweenPoses(startPose, target) > 1e-6) {
                addSmartRouteSegment(route, startPose, target);
            }
            return route;
        }

        // Farther away: route through the known waypoint tile to stay clear of field hazards.
        Pose waypointPose = new Pose(routeWaypointPose.x(), routeWaypointPose.y(), target.heading());

        Pose segmentStart = startPose;
        if (distanceBetweenPoses(startPose, waypointPose) > TeleOpConstants.ROUTE_WAYPOINT_SKIP_TOLERANCE_IN) {
            addSmartRouteSegment(route, startPose, waypointPose);
            segmentStart = waypointPose;
        }

        if (distanceBetweenPoses(segmentStart, target) > 1e-6) {
            addSmartRouteSegment(route, segmentStart, target);
        }

        return route;
    }

    private void replanFromCurrentPose(double runtimeS) {
        if (activeSegmentIndex < 0 || activeSegmentIndex >= activeSegmentEnds.size()) {
            return;
        }

        Pose currentPose = follower.pose();
        int previousSegmentIndex = activeSegmentIndex;
        Pose currentSegmentEnd = activeSegmentEnds.get(previousSegmentIndex);

        ArrayList<Path> replannedPaths = new ArrayList<>();
        ArrayList<Pose> replannedStarts = new ArrayList<>();
        ArrayList<Pose> replannedEnds = new ArrayList<>();

        addPathTo(replannedPaths, currentPose, currentSegmentEnd);
        replannedStarts.add(currentPose);
        replannedEnds.add(currentSegmentEnd);

        for (int i = previousSegmentIndex + 1; i < activeSegmentStarts.size(); i++) {
            Pose segmentStart = activeSegmentStarts.get(i);
            Pose segmentEnd = activeSegmentEnds.get(i);
            addPathTo(replannedPaths, segmentStart, segmentEnd);
            replannedStarts.add(segmentStart);
            replannedEnds.add(segmentEnd);
        }

        activeSegments.clear();
        activeSegments.addAll(replannedPaths);
        activeSegmentStarts.clear();
        activeSegmentStarts.addAll(replannedStarts);
        activeSegmentEnds.clear();
        activeSegmentEnds.addAll(replannedEnds);
        activeSegmentIndex = 0;
        lastReplanTimeS = runtimeS;

        if (!activeSegments.isEmpty()) {
            follower.follow(activeSegments.get(activeSegmentIndex));
        }
    }

    private void addPathTo(List<Path> paths, Pose start, Pose end) {
        paths.add(Math.abs(end.heading() - start.heading()) < 1e-6
                ? line(start, end).constant(start.heading())
                : line(start, end).linear(start.heading(), end.heading()));
    }

    private void addSmartRouteSegment(List<Path> route, Pose segmentStart, Pose segmentEnd) {
        // .constant() when heading doesn't change (matches Pedro's known-good pattern for straight
        // legs); .linear() only when heading actually needs to rotate across the segment.
        addPathTo(route, segmentStart, segmentEnd);
        activeSegmentStarts.add(segmentStart);
        activeSegmentEnds.add(segmentEnd);
    }

    private double pointToSegmentDistance(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        double segmentLengthSquared = (dx * dx) + (dy * dy);

        if (segmentLengthSquared <= 1e-9) {
            return Math.hypot(px - ax, py - ay);
        }

        double t = ((px - ax) * dx + (py - ay) * dy) / segmentLengthSquared;
        t = Math.max(0.0, Math.min(1.0, t));

        double closestX = ax + (t * dx);
        double closestY = ay + (t * dy);
        return Math.hypot(px - closestX, py - closestY);
    }

    private double distanceBetweenPoses(Pose first, Pose second) {
        return Math.hypot(first.x() - second.x(), first.y() - second.y());
    }
}


