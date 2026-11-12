package org.firstinspires.ftc.teamcode.gytd.test;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.pathing.PedroFollowerFactory;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;

/**
 * Pedro-backed tile mapper that starts at A1 and logs every requested path.
 *
 * Configuration required on the Control Hub Robot Configuration:
 * - Drive motors named front_left_drive, front_right_drive, back_left_drive, back_right_drive
 * - IMU named imu
 *
 * Controls:
 * - D-pad: select target tile
 * - B: follow Pedro path from current pose to selected tile
 * - A: reset selection and Pedro pose to A1 (when idle)
 * - X: cancel active path
 *
 * Safety notes:
 * - Put the robot on blocks for the first run of any new path.
 * - Keep the area around the robot clear while a path is following.
 * - Use X immediately if motion is not expected or looks unsafe.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "Test: Arena Mapper Pedro", group = "Test")
public class TestArenaMapperPedro extends LinearOpMode {
    private enum MapperState {
        IDLE,
        FOLLOWING_PATH
    }

    private enum ControlMode {
        MAPPER,
        MANUAL_DRIVE
    }

    private static final int MAX_COLUMNS = 6; // A-F
    private static final int MAX_ROWS = 6;    // 1-6
    private static final double TILE_SIZE_IN = 24.0;
    private static final double MIN_PATH_DISTANCE_IN = 0.25;
    // Heading 90deg matches this project's established field convention (see
    // TeleOpConstants.RED_HOME_POSE): heading 90deg = robot physically facing downfield/away from
    // the driver, with rows (Y) representing downfield distance and columns (X) representing
    // lateral position. HOME_POSE must match how the robot is actually placed on the field at A1,
    // otherwise the localizer's heading offset gets miscalibrated and column/row movement gets
    // swapped with forward/strafe movement (exactly the "A1->B1 moves away, A1->A2 strafes"
    // symptom this fixes).
    private static final Pose HOME_POSE = new Pose(12.0, 12.0, Math.toRadians(90.0));

    private Robot robot;
    private DriveSubsystem drive;
    private Follower follower;

    private MapperState mapperState = MapperState.IDLE;
    private Path activePath;

    private int selectedCol = 0;
    private int selectedRow = 0;
    private int lastReachedCol = 0;
    private int lastReachedRow = 0;

    private boolean lastUp;
    private boolean lastDown;
    private boolean lastLeft;
    private boolean lastRight;
    private boolean lastA;
    private boolean lastB;
    private boolean lastX;
    private boolean lastY;

    private final StringBuilder pathLog = new StringBuilder("A1");
    private Pose lastPathStart = HOME_POSE;
    private Pose lastPathTarget = HOME_POSE;
    private double lastCommandDistanceIn = 0.0;
    private String lastCommandStatus = "none";
    private ControlMode controlMode = ControlMode.MAPPER;

    @Override
    public void runOpMode() {
        PathingConfig.setUsePedroPathing(true);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("Pedro Arena Mapper");
            telemetry.addLine("Home tile: A1");
            telemetry.addLine("D-pad select, B follow path, A reset, X cancel");
            telemetry.addData("Backend", PathingConfig.getActiveBackendName());
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            return;
        }

        robot = new Robot(hardwareMap);
        drive = robot.getDrive();
        follower = createFollower();
        follower.setPose(HOME_POSE);

        waitForStart();

        while (opModeIsActive()) {
            updateSelection();
            updateButtons();
            updateFollowerState();
            updateManualDrive();

            Pose pose = follower.pose();
            telemetry.addLine("Pedro Arena Mapper");
            telemetry.addData("Control Mode", controlMode);
            telemetry.addData("State", mapperState);
            telemetry.addData("Current Pose", "(%.1f, %.1f, %.2f)", pose.x(), pose.y(), pose.heading());
            telemetry.addData("Selected Tile", tileLabel(selectedCol, selectedRow));
            telemetry.addData("Last Reached Tile", tileLabel(lastReachedCol, lastReachedRow));
            telemetry.addData("Last Path Start", "(%.1f, %.1f)", lastPathStart.x(), lastPathStart.y());
            telemetry.addData("Last Path Target", "(%.1f, %.1f)", lastPathTarget.x(), lastPathTarget.y());
            telemetry.addData("Last Cmd Distance (in)", "%.2f", lastCommandDistanceIn);
            telemetry.addData("Last Cmd Status", lastCommandStatus);
            telemetry.addData("Pedro Progress", "%.2f", follower.completion());
            telemetry.addData("Pedro Busy", follower.isBusy());
            telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(robot.getHardware().getHeadingRadians()));
            telemetry.addData("Speed Scale", "%.2f", drive.getSpeedScale());
            telemetry.addLine("Y toggles mapper/manual, LB slow mode in manual");
            telemetry.addData("Path Log", pathLog.toString());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }

    private void updateSelection() {
        if (controlMode != ControlMode.MAPPER || mapperState != MapperState.IDLE) {
            return;
        }

        boolean up = gamepad1.dpad_up;
        boolean down = gamepad1.dpad_down;
        boolean left = gamepad1.dpad_left;
        boolean right = gamepad1.dpad_right;

        if (up && !lastUp) {
            selectedRow = Math.min(MAX_ROWS - 1, selectedRow + 1);
        }
        if (down && !lastDown) {
            selectedRow = Math.max(0, selectedRow - 1);
        }
        if (right && !lastRight) {
            selectedCol = Math.min(MAX_COLUMNS - 1, selectedCol + 1);
        }
        if (left && !lastLeft) {
            selectedCol = Math.max(0, selectedCol - 1);
        }

        lastUp = up;
        lastDown = down;
        lastLeft = left;
        lastRight = right;
    }

    private void updateButtons() {
        boolean aPressed = gamepad1.a && !lastA;
        boolean bPressed = gamepad1.b && !lastB;
        boolean xPressed = gamepad1.x && !lastX;
        boolean yPressed = gamepad1.y && !lastY;

        if (yPressed && mapperState == MapperState.IDLE) {
            controlMode = (controlMode == ControlMode.MAPPER)
                    ? ControlMode.MANUAL_DRIVE
                    : ControlMode.MAPPER;
            drive.stop();
        }

        if (controlMode == ControlMode.MAPPER && aPressed && mapperState == MapperState.IDLE) {
            selectedCol = 0;
            selectedRow = 0;
            lastReachedCol = 0;
            lastReachedRow = 0;
            follower.stop();
            follower.setPose(HOME_POSE);
            pathLog.setLength(0);
            pathLog.append("A1");
        }

        if (controlMode == ControlMode.MAPPER && bPressed && mapperState == MapperState.IDLE) {
            startPathToSelectedTile();
        }

        if (xPressed) {
            cancelPath();
        }

        lastA = gamepad1.a;
        lastB = gamepad1.b;
        lastX = gamepad1.x;
        lastY = gamepad1.y;
    }

    private void updateManualDrive() {
        if (controlMode != ControlMode.MANUAL_DRIVE || mapperState != MapperState.IDLE) {
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
        drive.driveFieldCentric(axial, lateral, yaw, robot.getHardware().getHeadingRadians());
    }

    private void startPathToSelectedTile() {
        Pose startPose = follower.pose();
        Pose targetPose = tileToPose(selectedCol, selectedRow, startPose.heading());
        double targetDistanceIn = distanceBetween(startPose, targetPose);

        lastPathStart = startPose;
        lastPathTarget = targetPose;
        lastCommandDistanceIn = targetDistanceIn;

        if (targetDistanceIn <= MIN_PATH_DISTANCE_IN) {
            lastCommandStatus = "noop (target too close)";
            pathLog.append(" [noop]");
            return;
        }

        activePath = line(startPose, targetPose).linear(startPose.heading(), targetPose.heading());
        follower.follow(activePath);
        mapperState = MapperState.FOLLOWING_PATH;
        lastCommandStatus = "following";

        appendPathSegment(tileLabel(selectedCol, selectedRow));
    }

    private void updateFollowerState() {
        if (mapperState != MapperState.FOLLOWING_PATH) {
            follower.update();
            return;
        }

        follower.update();
        if (follower.atParametricEnd()) {
            follower.stop();
            activePath = null;
            mapperState = MapperState.IDLE;
            lastReachedCol = selectedCol;
            lastReachedRow = selectedRow;
            lastCommandStatus = "complete";
        }
    }

    private void cancelPath() {
        follower.stop();
        drive.stop();
        activePath = null;
        mapperState = MapperState.IDLE;
        lastCommandStatus = "cancelled";
        pathLog.append(" [cancel]");
    }

    private double distanceBetween(Pose first, Pose second) {
        return Math.hypot(second.x() - first.x(), second.y() - first.y());
    }

    private void appendPathSegment(String nextTile) {
        if (pathLog.length() > 0) {
            pathLog.append(" -> ");
        }
        pathLog.append(nextTile);
    }

    private Pose tileToPose(int col, int row, double heading) {
        double x = 12.0 + (col * TILE_SIZE_IN);
        double y = 12.0 + (row * TILE_SIZE_IN);
        return new Pose(x, y, heading);
    }

    private String tileLabel(int col, int row) {
        char colLetter = (char) ('A' + col);
        int rowNumber = row + 1;
        return String.format("%c%d", colLetter, rowNumber);
    }

    private Follower createFollower() {
        return PedroFollowerFactory.createTwoWheelFollower(hardwareMap);
    }
}

