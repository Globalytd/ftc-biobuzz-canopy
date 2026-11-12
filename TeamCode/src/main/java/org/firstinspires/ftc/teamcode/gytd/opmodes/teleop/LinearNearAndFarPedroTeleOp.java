package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util.ShootingSide;
import org.firstinspires.ftc.teamcode.gytd.pathing.PedroShootAssist;
import org.firstinspires.ftc.teamcode.gytd.pathing.PathingConfig;
import org.firstinspires.ftc.teamcode.gytd.robot.Robot;
import org.firstinspires.ftc.teamcode.gytd.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherConstants;
import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherSubsystem;

/**
 * RED team Pedro assist TeleOp (linear-motion variant).
 *
 * Reuses the same {@link PedroShootAssist} controller as {@link RedTeamPedroTeleOp} - this is the
 * payoff of extracting the assist logic: both OpModes share identical, tested routing/state-machine
 * behavior and only differ in OpMode-level wiring (name/group/telemetry wording, if any).
 *
 * NOTE (2026-11-08): there is no magazine/indexer anymore - the team switched to a passive
 * gravity-fed chute from intake straight to the launcher wheel.
 *
 * NOTE (2026-11-08, later same day): intake/launcher moved from gamepad2 bumpers to gamepad2
 * joysticks - pushing the left stick forward/back runs intake forward/reverse, and pushing the
 * right stick forward spins up the launcher.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
@TeleOp(name = "RED team Pedro Linear motion", group = "TeleOp")
public class LinearNearAndFarPedroTeleOp extends LinearOpMode {
    // Joystick push past this magnitude counts as a deliberate command, not stick drift/noise.
    private static final double GAMEPAD2_STICK_DEADZONE = 0.2;

    private Robot robot;
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private LauncherSubsystem launcher;
    private PedroShootAssist assist;

    private boolean pendingShotRequest = false;
    private boolean lastShootButton = false;
    private boolean lastStopLauncherButton = false;
    private boolean lastStopAllButton = false;

    private boolean lastLoadingSideAssist = false;
    private boolean lastGardenSideAssist = false;
    private boolean lastCancel = false;

    @Override
    public void runOpMode() {
        PathingConfig.setUsePedroPathing(true);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("RED team Pedro ready");
            telemetry.addLine("A = loading-side assist, B = garden-side assist, X = cancel");
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
        launcher = robot.getLauncher();
        assist = PedroShootAssist.createRedAssist(hardwareMap);

        // See LauncherConstants Javadoc: without this, the launcher's target velocity stays at its
        // default of 0.0 and the "spin up" button would command 0 velocity (effectively off).
        launcher.setTargetVelocity(LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC);

        telemetry.addLine("RED team Pedro initialized");
        telemetry.addData("Drive backend", drive.getBackendName());
        telemetry.addData("Drive status", drive.getStatus());
        telemetry.addData("Intake status", intake.getStatus());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            assist.update(getRuntime());
            updateAssistButtons();
            updateLauncherControls();
            updateIntakeControls();
            updateManualDrive();

            Pose pose = assist.getPose();
            Pose targetPose = assist.getTargetPose();

            telemetry.addLine("RED team Pedro");
            telemetry.addData("Assist state", assist.getState());
            telemetry.addData("Active side", assist.getActiveSide());
            telemetry.addData("Path Segment", "%d/%d",
                    Math.min(assist.getSegmentIndex() + 1, Math.max(assist.getSegmentCount(), 1)),
                    Math.max(assist.getSegmentCount(), 1));
            telemetry.addData("Path Deviation (in)", "%.2f", assist.getCurrentSegmentDeviationInches());
            telemetry.addData("Field heading (rad)", "%.3f", robot.getHardware().getHeadingRadians());
            telemetry.addData("Pose", "(%.2f, %.2f, %.3f)", pose.x(), pose.y(), pose.heading());
            telemetry.addData("Assist Target", "(%.2f, %.2f, %.3f)", targetPose.x(), targetPose.y(), targetPose.heading());
            telemetry.addData("Shoot Position Error (in)", "%.2f", assist.getPositionErrorInches());
            telemetry.addData("Shoot Heading Error (deg)", "%.1f", Math.toDegrees(assist.getHeadingErrorRadians()));
            telemetry.addData("Shooting Allowed", assist.isShootingAllowed());
            telemetry.addData("Shot Pause Reason", assist.isPausedForPoseRecovery() ? "Re-centering at shoot pose" : "none");
            telemetry.addData("Drive backend", drive.getBackendName());
            telemetry.addData("Speed scale", drive.getSpeedScale());
            telemetry.addData("Launcher", launcher.getStatus());
            telemetry.addData("Launcher Velocity", "%.2f / %.2f", launcher.getVelocity(), launcher.getTargetVelocity());
            telemetry.addData("Pending Shot", pendingShotRequest);
            telemetry.addData("Intake", intake.getStatus());
            telemetry.addData("Pedro progress", "%.2f", assist.getFollower().completion());
            telemetry.addData("Busy", assist.getFollower().isBusy());
            telemetry.update();
            idle();
        }

        robot.shutdown();
    }

    private void updateAssistButtons() {
        boolean loadingSideLaunchPressed = gamepad1.a && !lastLoadingSideAssist;
        boolean gardenSideLaunchPressed = gamepad1.b && !lastGardenSideAssist;
        boolean cancelPressed = gamepad1.x && !lastCancel;
        boolean resetHomePressed = gamepad1.y;

        if (loadingSideLaunchPressed && !assist.isActive()) {
            assist.start(ShootingSide.LOADING_SIDE);
        }

        if (gardenSideLaunchPressed && !assist.isActive()) {
            assist.start(ShootingSide.GARDEN_SIDE);
        }

        if (cancelPressed) {
            stopLaunchingMechanisms();
            assist.cancel();
            drive.stop();
        }

        if (resetHomePressed && !assist.isActive()) {
            assist.resetToHomePose();
        }

        lastLoadingSideAssist = gamepad1.a;
        lastGardenSideAssist = gamepad1.b;
        lastCancel = gamepad1.x;
    }

    private void updateManualDrive() {
        // Driver control is active whenever the assist sequence is not following a path.
        if (assist.isActive()) {
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

    private void updateLauncherControls() {
        boolean shootPressed = gamepad2.a && !lastShootButton;
        boolean stopLauncherPressed = gamepad2.b && !lastStopLauncherButton;
        boolean stopAllPressed = gamepad2.x && !lastStopAllButton;
        // Push the right stick forward (away from the driver) to spin up the launcher.
        boolean spinUpRequested = -gamepad2.right_stick_y > GAMEPAD2_STICK_DEADZONE;

        boolean pausedForPoseRecovery = assist.isPausedForPoseRecovery();

        // Keep the launcher available for manual use at any time.
        if (stopLauncherPressed || stopAllPressed) {
            stopLaunchingMechanisms();
        } else if (spinUpRequested) {
            launcher.startLauncher();
        } else if (launcher.getState() != LauncherSubsystem.LauncherState.OFF) {
            launcher.stopLauncher();
        }

        if (shootPressed) {
            pendingShotRequest = true;
        }

        // With a gravity-fed chute there is no feed motor to trigger - once the launcher is at
        // target speed and every safety gate (Pedro pose recovery, shoot-pose hold) is open, a game
        // piece already sitting against the spinning wheel will launch on its own. We still track
        // "firing" for telemetry/future sensor-gating, we just no longer call a feed method.
        if (pendingShotRequest && assist.isShootingAllowed() && !pausedForPoseRecovery) {
            if (launcher.atTargetVelocity()) {
                launcher.markFiring();
                pendingShotRequest = false;
            }
        }

        lastShootButton = gamepad2.a;
        lastStopLauncherButton = gamepad2.b;
        lastStopAllButton = gamepad2.x;
    }

    private void updateIntakeControls() {
        // Push the left stick forward (away from the driver) for intake, pull it back to reverse.
        double intakeStick = -gamepad2.left_stick_y;
        if (intakeStick > GAMEPAD2_STICK_DEADZONE) {
            intake.intake();
        } else if (intakeStick < -GAMEPAD2_STICK_DEADZONE) {
            intake.reverse();
        } else {
            intake.stop();
        }
    }

    private void stopLaunchingMechanisms() {
        pendingShotRequest = false;
        launcher.stopLauncher();
    }
}

