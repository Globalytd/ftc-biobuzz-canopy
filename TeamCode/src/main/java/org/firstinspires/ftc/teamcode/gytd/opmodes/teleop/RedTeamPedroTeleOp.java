package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.paths.Path;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.revhub.localizers.TwoWheelConfig;
import com.pedropathing.revhub.localizers.TwoWheelLocalizer;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.gytd.hardware.HardwareConstants;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.MagazineSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.ShooterSubsystem;

import java.util.List;
import java.util.ArrayList;

/**
 * RED team Pedro assist TeleOp.
 *
 * This is intentionally small and testable. It preserves normal field-centric drive and adds
 * two one-touch assist buttons that move the robot to a safe pose near the hive before the robot
 * is re-enabled for driver control.
 *
 * TODO: confirm the exact blue-side and red-side target poses, the final button map, and the
 * match-safe route around the hive. For now the route is a safe placeholder path that keeps the
 * robot clear of the center hive area until the field geometry is finalized.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "RED team Pedro", group = "TeleOp")
public class RedTeamPedroTeleOp extends LinearOpMode {
    private enum AssistState {
        IDLE,
        FOLLOWING_PATH,
        HOLDING_SHOOTING_POSE,
        COMPLETE,
        CANCELLED
    }

    private static final Pose BLUE_SIDE_SHOOT_POSE = new Pose(30.0, 40.0, Math.toRadians(180.0));
    private static final Pose BLUE_CLEARANCE_WAYPOINT_POSE = new Pose(40.0, 0.0, Math.toRadians(90.0));
    private static final Pose RED_CURVE_WAYPOINT_POSE = new Pose(40.0, 30.0, Math.toRadians(120.0));
    private static final double SHOOT_POSE_POSITION_TOLERANCE_IN = 2.0;
    private static final double SHOOT_POSE_HEADING_TOLERANCE_RAD = Math.toRadians(6.0);
    private static final double SHOOT_POSE_SETTLE_TIME_S = 0.30;
    private static final double MID_PATH_DEVIATION_REPLAN_IN = 8.0;
    private static final double MID_PATH_REPLAN_COOLDOWN_S = 0.35;

    private Robot robot;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private MagazineSubsystem magazine;
    private ShooterSubsystem shooter;
    private Follower follower;
    private AssistState assistState = AssistState.IDLE;
    private String activeSide = "none";
    private Pose assistTargetPose = TeleOpConstants.RED_HOME_POSE;
    private final List<Path> activePathSegments = new ArrayList<>();
    private final List<Pose> activeSegmentStarts = new ArrayList<>();
    private final List<Pose> activeSegmentEnds = new ArrayList<>();
    private int activePathSegmentIndex = 0;
    private double lastReplanTimeS = -10.0;
    private boolean shootingPausedForPoseRecovery = false;
    private boolean shootingAllowed = false;
    private double shootPoseStableStartTimeS = -1.0;
    private boolean pendingShotRequest = false;
    private boolean lastShootButton = false;
    private boolean lastStopShooterButton = false;
    private boolean lastLoadButton = false;
    private boolean lastStopAllButton = false;

    private boolean lastBlueAssist = false;
    private boolean lastRedAssist = false;
    private boolean lastCancel = false;

    @Override
    public void runOpMode() {
        PathingConfig.setUsePedroPathing(true);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("RED team Pedro ready");
            telemetry.addLine("A = Blue-side assist, B = Red-side assist, X = cancel");
            telemetry.addLine("Left stick = drive, right stick = rotate");
            telemetry.addData("Pedro backend", PathingConfig.getActiveBackendName());
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            return;
        }

        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        intake = robot.getIntake();
        magazine = robot.getMagazine();
        shooter = robot.getShooter();
        follower = createFollower();
        follower.setPose(TeleOpConstants.RED_HOME_POSE);

        telemetry.addLine("RED team Pedro initialized");
        telemetry.addData("Drive backend", drive.getBackendName());
        telemetry.addData("Drive status", drive.getStatus());
        telemetry.addData("Intake status", intake.getStatus());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Keep pose estimate fresh for telemetry.
            // When Pedro is actively following, the state machine updates it.
            if (!isPedroControllingDrive()) {
                follower.update();
            }
            updateAssistButtons();
            updateAssistStateMachine();
            updateShooterControls();
            updateManualDrive();

            telemetry.addLine("RED team Pedro");
            telemetry.addData("Assist state", assistState);
            telemetry.addData("Active side", activeSide);
            telemetry.addData("Path Segment", "%d/%d", Math.min(activePathSegmentIndex + 1, Math.max(activePathSegments.size(), 1)), Math.max(activePathSegments.size(), 1));
            telemetry.addData("Path Deviation (in)", "%.2f", getCurrentSegmentDeviationInches());
            telemetry.addData("Field heading (rad)", "%.3f", robot.getHardware().getHeadingRadians());
            telemetry.addData("Pose", "(%.2f, %.2f, %.3f)", follower.pose().x(), follower.pose().y(), follower.pose().heading());
            telemetry.addData("Assist Target", "(%.2f, %.2f, %.3f)", assistTargetPose.x(), assistTargetPose.y(), assistTargetPose.heading());
            telemetry.addData("Shoot Position Error (in)", "%.2f", getShootPositionErrorInches());
            telemetry.addData("Shoot Heading Error (deg)", "%.1f", Math.toDegrees(getShootHeadingErrorRadians()));
            telemetry.addData("Shooting Allowed", shootingAllowed);
            telemetry.addData("Shot Pause Reason", shootingPausedForPoseRecovery ? "Re-centering at shoot pose" : "none");
            telemetry.addData("Drive backend", drive.getBackendName());
            telemetry.addData("Speed scale", drive.getSpeedScale());
            telemetry.addData("Shooter", shooter.getStatus());
            telemetry.addData("Shooter Velocity", "%.2f / %.2f", shooter.getVelocity(), shooter.getTargetVelocity());
            telemetry.addData("Magazine", magazine.getStatus());
            telemetry.addData("Magazine Count", magazine.getBallCount());
            telemetry.addData("Pending Shot", pendingShotRequest);
            telemetry.addData("Load Ready", magazine.isLoaded());
            telemetry.addData("Intake", intake.getStatus());
            if (follower != null) {
                telemetry.addData("Pedro progress", "%.2f", follower.completion());
                telemetry.addData("Busy", follower.isBusy());
            }
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }

    private void updateAssistButtons() {
        boolean bluePressed = gamepad1.a && !lastBlueAssist;
        boolean redPressed = gamepad1.b && !lastRedAssist;
        boolean cancelPressed = gamepad1.x && !lastCancel;
        boolean resetHomePressed = gamepad1.y;

        if (bluePressed && !isPedroControllingDrive()) {
            startAssist("blue", BLUE_SIDE_SHOOT_POSE);
        }

        if (redPressed && !isPedroControllingDrive()) {
            startAssist("red", TeleOpConstants.RED_SIDE_SHOOT_POSE);
        }

        if (cancelPressed) {
            cancelAssist();
        }

        if (resetHomePressed && !isPedroControllingDrive()) {
            follower.setPose(TeleOpConstants.RED_HOME_POSE);
        }

        lastBlueAssist = gamepad1.a;
        lastRedAssist = gamepad1.b;
        lastCancel = gamepad1.x;
    }

    private List<Path> buildSafeRouteForSide(String side, Pose startPose) {
        // Build Pedro path segments that start from the robot's ACTUAL current pose.
        //
        // Red one-touch requirement:
        //   currentPose -> RED_SHOOTING_POSE at (60, 12, 123.7 degrees)
        //
        // Blue keeps a simple two-segment route for now:
        //   currentPose -> blue clearance waypoint -> blue target
        //
        // Red now uses a curved two-segment route:
        //   currentPose -> red curve waypoint -> red shooting pose
        ArrayList<Path> route = new ArrayList<>();
        activeSegmentStarts.clear();
        activeSegmentEnds.clear();

        if (side.equalsIgnoreCase("blue")) {
            addRouteSegment(route, startPose, BLUE_CLEARANCE_WAYPOINT_POSE);
            addRouteSegment(route, BLUE_CLEARANCE_WAYPOINT_POSE, BLUE_SIDE_SHOOT_POSE);
        } else {
            addRouteSegment(route, startPose, RED_CURVE_WAYPOINT_POSE);
            addRouteSegment(route, RED_CURVE_WAYPOINT_POSE, TeleOpConstants.RED_SIDE_SHOOT_POSE);
        }

        return route;
    }

    private void addRouteSegment(List<Path> route, Pose segmentStart, Pose segmentEnd) {
        route.add(line(segmentStart, segmentEnd)
                .linear(segmentStart.heading(), segmentEnd.heading()));
        activeSegmentStarts.add(segmentStart);
        activeSegmentEnds.add(segmentEnd);
    }

    private void startAssist(String side, Pose targetPose) {
        if (follower == null) {
            return;
        }

        Pose currentPose = follower.pose();
        assistState = AssistState.FOLLOWING_PATH;
        activeSide = side;
        assistTargetPose = targetPose;
        shootingPausedForPoseRecovery = false;
        shootingAllowed = false;
        shootPoseStableStartTimeS = -1.0;
        activePathSegments.clear();
        activePathSegments.addAll(buildSafeRouteForSide(side, currentPose));
        activePathSegmentIndex = 0;
        lastReplanTimeS = -10.0;

        if (!activePathSegments.isEmpty()) {
            follower.follow(activePathSegments.get(activePathSegmentIndex));
        }
    }

    private void cancelAssist() {
        stopShootingMechanisms();
        follower.stop();
        drive.stop();
        activePathSegments.clear();
        activeSegmentStarts.clear();
        activeSegmentEnds.clear();
        activePathSegmentIndex = 0;
        shootingPausedForPoseRecovery = false;
        shootingAllowed = false;
        shootPoseStableStartTimeS = -1.0;
        activeSide = "cancelled";
        assistState = AssistState.CANCELLED;
    }

    private void updateAssistStateMachine() {
        if (follower == null) {
            return;
        }

        switch (assistState) {
            case FOLLOWING_PATH:
                follower.update();

                if (activePathSegmentIndex < activeSegmentStarts.size() &&
                        getCurrentSegmentDeviationInches() > MID_PATH_DEVIATION_REPLAN_IN &&
                        (getRuntime() - lastReplanTimeS) >= MID_PATH_REPLAN_COOLDOWN_S) {
                    // If we get bumped off-route while driving, rebuild from the live pose.
                    replanFromCurrentPose();
                    break;
                }

                if (activePathSegmentIndex >= activePathSegments.size()) {
                    finishAssistPath();
                    return;
                }

                if (follower.atParametricEnd()) {
                    activePathSegmentIndex++;
                    if (activePathSegmentIndex >= activePathSegments.size()) {
                        finishAssistPath();
                    } else {
                        follower.follow(activePathSegments.get(activePathSegmentIndex));
                    }
                }
                break;

            case HOLDING_SHOOTING_POSE:
                follower.update();

                // Pedro's hold mode keeps correcting back to the shooting pose.
                if (!follower.holding()) {
                    follower.hold(TeleOpConstants.RED_SIDE_SHOOT_POSE);
                }

                if (!isAtShootPose()) {
                    // Another robot bumped us. Pause shooting until we are stable again.
                    shootingPausedForPoseRecovery = true;
                    shootingAllowed = false;
                    shootPoseStableStartTimeS = -1.0;
                    return;
                }

                if (shootPoseStableStartTimeS < 0.0) {
                    shootPoseStableStartTimeS = getRuntime();
                }

                if ((getRuntime() - shootPoseStableStartTimeS) >= SHOOT_POSE_SETTLE_TIME_S) {
                    shootingPausedForPoseRecovery = false;
                    shootingAllowed = true;
                }
                break;

            case IDLE:
            case COMPLETE:
            case CANCELLED:
                break;
        }
    }

    private void updateManualDrive() {
        // Driver control is active whenever the assist sequence is not following a path.
        if (isPedroControllingDrive()) {
            return;
        }

        if (gamepad1.left_bumper) {
            drive.setSpeedScale(0.45);
        } else {
            drive.setSpeedScale(1.0);
        }

        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        // Keep the same field-centric convention as the existing TeleOp.
        drive.driveFieldCentric(axial, lateral, yaw, robot.getHardware().getHeadingRadians());
    }

    private void updateShooterControls() {
        boolean shootPressed = gamepad2.a && !lastShootButton;
        boolean stopShooterPressed = gamepad2.b && !lastStopShooterButton;
        boolean loadPressed = gamepad2.y && !lastLoadButton;
        boolean stopAllPressed = gamepad2.x && !lastStopAllButton;
        boolean spinUpRequested = gamepad2.right_bumper;
        boolean reverseRequested = gamepad2.left_bumper;

        // Keep the shooter available for manual use, but never feed while pose recovery is active.
        if (stopShooterPressed || stopAllPressed) {
            stopShootingMechanisms();
        } else if (loadPressed) {
            magazine.load();
        } else {
            if (spinUpRequested) {
                shooter.startShooter();
            } else if (shooter.getState() != ShooterSubsystem.ShooterState.OFF) {
                shooter.stopShooter();
            }

            if (reverseRequested) {
                if (!shootingPausedForPoseRecovery) {
                    magazine.reverse();
                } else {
                    magazine.stop();
                }
            } else if (!shootingPausedForPoseRecovery) {
                magazine.stop();
            }
        }

        if (shootPressed) {
            pendingShotRequest = true;
        }

        // If we're bumped out of pose, pause the feed and wait for Pedro to recover.
        if (shootingPausedForPoseRecovery) {
            magazine.stop();
        }

        // Fire only when all safety gates are open.
        if (pendingShotRequest && shootingAllowed && !shootingPausedForPoseRecovery) {
            if (shooter.atTargetVelocity() && magazine.isLoaded()) {
                shooter.startShooter();
                magazine.feedOne();
                pendingShotRequest = false;
            }
        }

        lastShootButton = gamepad2.a;
        lastStopShooterButton = gamepad2.b;
        lastLoadButton = gamepad2.y;
        lastStopAllButton = gamepad2.x;
    }

    private void stopShootingMechanisms() {
        pendingShotRequest = false;
        magazine.stop();
        shooter.stopShooter();
    }

    private boolean isPedroControllingDrive() {
        return assistState == AssistState.FOLLOWING_PATH
                || assistState == AssistState.HOLDING_SHOOTING_POSE;
    }

    private void finishAssistPath() {
        if ("red".equalsIgnoreCase(activeSide)) {
            // At the red shooting position, switch into Pedro hold mode.
            // This lets the robot correct itself if another bot bumps it.
            follower.hold(TeleOpConstants.RED_SIDE_SHOOT_POSE);
            assistTargetPose = TeleOpConstants.RED_SIDE_SHOOT_POSE;
            assistState = AssistState.HOLDING_SHOOTING_POSE;
            shootingPausedForPoseRecovery = true;
            shootingAllowed = false;
            shootPoseStableStartTimeS = -1.0;
            return;
        }

        follower.stop();
        drive.stop();
        shootingPausedForPoseRecovery = false;
        shootingAllowed = false;
        shootPoseStableStartTimeS = -1.0;
        assistState = AssistState.COMPLETE;
        activeSide = "complete";
    }

    private boolean isAtShootPose() {
        return getShootPositionErrorInches() <= SHOOT_POSE_POSITION_TOLERANCE_IN
                && Math.abs(getShootHeadingErrorRadians()) <= SHOOT_POSE_HEADING_TOLERANCE_RAD;
    }

    private double getCurrentSegmentDeviationInches() {
        if (activePathSegmentIndex < 0 || activePathSegmentIndex >= activeSegmentStarts.size()) {
            return 0.0;
        }
        Pose currentPose = follower.pose();
        Pose segmentStart = activeSegmentStarts.get(activePathSegmentIndex);
        Pose segmentEnd = activeSegmentEnds.get(activePathSegmentIndex);
        return pointToSegmentDistance(currentPose.x(), currentPose.y(), segmentStart.x(), segmentStart.y(), segmentEnd.x(), segmentEnd.y());
    }

    private void replanFromCurrentPose() {
        if (activePathSegmentIndex < 0 || activePathSegmentIndex >= activeSegmentEnds.size()) {
            return;
        }

        Pose currentPose = follower.pose();
        int previousSegmentIndex = activePathSegmentIndex;
        Pose currentSegmentEnd = activeSegmentEnds.get(previousSegmentIndex);

        ArrayList<Path> replannedPaths = new ArrayList<>();
        ArrayList<Pose> replannedStarts = new ArrayList<>();
        ArrayList<Pose> replannedEnds = new ArrayList<>();

        // First, recover back to the segment endpoint we were heading toward.
        replannedPaths.add(line(currentPose, currentSegmentEnd)
                .linear(currentPose.heading(), currentSegmentEnd.heading()));
        replannedStarts.add(currentPose);
        replannedEnds.add(currentSegmentEnd);

        // Then continue any remaining planned segments.
        for (int i = previousSegmentIndex + 1; i < activeSegmentStarts.size(); i++) {
            Pose segmentStart = activeSegmentStarts.get(i);
            Pose segmentEnd = activeSegmentEnds.get(i);
            replannedPaths.add(line(segmentStart, segmentEnd)
                    .linear(segmentStart.heading(), segmentEnd.heading()));
            replannedStarts.add(segmentStart);
            replannedEnds.add(segmentEnd);
        }

        activePathSegments.clear();
        activePathSegments.addAll(replannedPaths);
        activeSegmentStarts.clear();
        activeSegmentStarts.addAll(replannedStarts);
        activeSegmentEnds.clear();
        activeSegmentEnds.addAll(replannedEnds);
        activePathSegmentIndex = 0;
        lastReplanTimeS = getRuntime();

        if (!activePathSegments.isEmpty()) {
            follower.follow(activePathSegments.get(activePathSegmentIndex));
        }
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

    private double getShootPositionErrorInches() {
        Pose currentPose = follower.pose();
        double deltaX = TeleOpConstants.RED_SIDE_SHOOT_POSE.x() - currentPose.x();
        double deltaY = TeleOpConstants.RED_SIDE_SHOOT_POSE.y() - currentPose.y();
        return Math.hypot(deltaX, deltaY);
    }

    private double getShootHeadingErrorRadians() {
        Pose currentPose = follower.pose();
        double headingError = TeleOpConstants.RED_SIDE_SHOOT_POSE.heading() - currentPose.heading();

        while (headingError > Math.PI) {
            headingError -= 2.0 * Math.PI;
        }
        while (headingError < -Math.PI) {
            headingError += 2.0 * Math.PI;
        }
        return headingError;
    }


    private Follower createFollower() {
        MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
            c.frontLeftName.set(HardwareConstants.FRONT_LEFT_DRIVE);
            c.frontRightName.set(HardwareConstants.FRONT_RIGHT_DRIVE);
            c.backLeftName.set(HardwareConstants.BACK_LEFT_DRIVE);
            c.backRightName.set(HardwareConstants.BACK_RIGHT_DRIVE);

            c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
            c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
            c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
            c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);

            c.manualBrakeMode.set(true);
        });

        TwoWheelConfig localizerConfig = new TwoWheelConfig(c -> {
            c.xPodName.set(HardwareConstants.FRONT_LEFT_DRIVE);
            c.yPodName.set(HardwareConstants.FRONT_RIGHT_DRIVE);
            c.imuName.set(HardwareConstants.IMU_NAME);
            c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
                    RevHubOrientationOnRobot.UsbFacingDirection.LEFT)));

            c.xPodOffset.set(0.0);
            c.yPodOffset.set(0.0);
            c.forwardTicksToInches.set(0.0100);
            c.strafeTicksToInches.set(0.0100);
            c.xPodDirection.set(Encoder.FORWARD);
            c.yPodDirection.set(Encoder.FORWARD);
        });

        Localizer localizer = new TwoWheelLocalizer(hardwareMap, localizerConfig);
        Drivetrain drivetrain = new Mecanum(hardwareMap, drivetrainConfig);

        ForesightConfig algorithmConfig = new ForesightConfig(c -> {
            c.forwardTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
            c.strafeTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
            c.coast.set(Controller.proportionalFeedforward(0.0));
            c.brake.set(Controller.proportionalFeedforward(0.0));

            c.headingFeedback.set(Controller.proportional(0.015));
            c.headingBrakeCoefficients.set(Vector2D.cartesian(0.0, 0.0));
            c.linearBrakeCoefficients.set(Matrix.diag(0.0, 0.0));
            c.quadraticBrakeCoefficients.set(Matrix.diag(0.0, 0.0));

            c.maxAchievableForwardVelocity.set(18.0);
            c.maxAchievableStrafeVelocity.set(18.0);
            c.naturalForwardDeceleration.set(18.0);
            c.naturalStrafeDeceleration.set(18.0);
        });

        return new Follower(localizer, drivetrain, new Foresight(algorithmConfig));
    }
}
