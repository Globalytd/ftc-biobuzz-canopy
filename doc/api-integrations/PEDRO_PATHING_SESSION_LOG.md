# Pedro Pathing Session Log

Use one entry per work session.
Keep entries short and factual.

---

## Session 001

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 0 - Baseline freeze
Branch: `integration/pedro-pathing`
Goal for today: Start integration safely and create rollback point.

Files touched:
- `doc/api-integrations/PEDRO_PATHING_INTEGRATION_PLAYBOOK.md`
- `doc/api-integrations/README.md`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - `Test: Hardware`
  - `Test: Mecanum Drive`
  - baseline TeleOp smoke test

What passed:
- Baseline build gate passed.
- Rollback tag created: `pre-pedro-baseline`.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Next action:
- Start Phase 1 (dependency-only) in small commit.
- Keep runtime behavior unchanged.

---

## Session 002

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 1 - Dependency only
Branch: `integration/pedro-pathing`
Goal for today: Add Pedro dependencies with zero runtime code changes.

Files touched:
- `build.dependencies.gradle`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

What changed:
- Added repository: `https://repo.dairy.foundation/releases/`
- Added dependencies:
  - `com.pedropathing:revhub:3.0.1`
  - `com.pedropathing:tuning:1.0.1`

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Driver Station OpMode list smoke check
  - `Test: Mecanum Drive`
  - baseline TeleOp smoke test

What passed:
- Dependency sync/build gate passed.
- No runtime wiring added yet.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Next action:
- Start Phase 2 adapter seam behind feature flag.
- Keep legacy drive path as default.

---

## Session 003

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 2 - Adapter seam
Branch: `integration/pedro-pathing`
Goal for today: Add a backend seam so legacy drive stays default and Pedro can be enabled later.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/DriveBackend.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/AbstractMecanumDriveBackend.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/LegacyDriveBackend.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PedroDriveBackend.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PathingConfig.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/DriveSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/BasicTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RobotCentricTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/FieldCentricTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestMecanumDrive.java`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Symbols touched:
- `DriveSubsystem`
- `DriveBackend`
- `AbstractMecanumDriveBackend`
- `LegacyDriveBackend`
- `PedroDriveBackend`
- `PathingConfig.usePedroPathing()`
- `DriveSubsystem.getBackendName()`

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

What changed:
- Added a backend interface and two backends.
- Added a compile-time flag that keeps legacy drive selected by default.
- Added backend telemetry to drive OpModes and mecanum test.
- Kept current drive behavior unchanged because the flag is OFF.

Behavior change type:
- behind flag

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Driver Station OpMode list smoke check
  - `Test: Mecanum Drive`
  - `Robot-Centric TeleOp`
  - `Field-Centric TeleOp`

What passed:
- Build passed.
- Phase 2 tag created: `pedro-phase-2-pass`.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- `pedro-phase-2-pass` created

Next action:
- Run robot smoke checks with the flag OFF.
- If stable, continue to Phase 3 pilot test OpMode.

---

## Session 004

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 2 - Driver Station selection update
Branch: `integration/pedro-pathing`
Goal for today: Let the driver choose legacy or Pedro drive on gamepad1 before pressing Start.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PathingConfig.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/BasicTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RobotCentricTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/FieldCentricTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestMecanumDrive.java`
- `doc/api-integrations/PEDRO_PATHING_INTEGRATION_PLAYBOOK.md`
- `doc/api-integrations/README.md`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Symbols touched:
- `PathingConfig.applyDriverStationSelection(Gamepad)`
- `PathingConfig.getSelectionInstructions()`
- `PathingConfig.getActiveBackendName()`
- `BasicTeleOp.runOpMode()`
- `RobotCentricTeleOp.runOpMode()`
- `FieldCentricTeleOp.runOpMode()`
- `TestMecanumDrive.runOpMode()`

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

What changed:
- Added gamepad1 pre-start selection for legacy vs Pedro.
- X selects Legacy Drive.
- B selects Pedro Pathing.
- The chosen mode is shown in telemetry before Start.

Behavior change type:
- behind flag

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Confirm X selects Legacy Drive
  - Confirm B selects Pedro Pathing
  - `Test: Mecanum Drive`
  - `Robot-Centric TeleOp`
  - `Field-Centric TeleOp`

What passed:
- Build passed.
- Drive backend can now be selected on Driver Station before Start.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- `pedro-phase-2-pass` remains the phase tag for the seam; no new rollback tag needed for this logging update.

Next action:
- Run robot smoke checks with X and B selection.
- If stable, continue to Phase 3 pilot test OpMode.

---

## Session 005

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 3 - Test OpMode pilot start
Branch: `integration/pedro-pathing`
Goal for today: Add an isolated Pedro pilot test OpMode with safe start/abort controls.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestPedroPathingPilot.java`
- `doc/controls/BUTTON_CONTROLS_REFERENCE.md`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Symbols touched:
- `TestPedroPathingPilot`
- `PilotState`
- `runPilotStateMachine(...)`
- `PathingConfig.applyDriverStationSelection(...)`

What changed (short bullets):
- Added `Test: Pedro Pathing Pilot` OpMode.
- Added pre-start backend selection and in-run pilot controls.
- Added pilot button mappings to the central button controls doc.

Button controls changed?
- yes
- Updated `doc/controls/BUTTON_CONTROLS_REFERENCE.md`

Behavior change type:
- behind flag

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Select mode pre-start (`X` legacy, `B` pedro)
  - `A` start pilot sequence
  - `X` abort pilot sequence
  - `Y` yaw reset

What passed:
- Build passed with pilot OpMode added.
- Documentation updated in same change for button controls.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- `pedro-phase-2-pass` remains latest phase tag.

Next action:
- Run pilot test on robot and capture telemetry/video evidence.
- If stable, continue Phase 3 path-error telemetry improvements.

---

## Session 006

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 3 - Pilot telemetry metrics
Branch: `integration/pedro-pathing`
Goal for today: Add phase-3 telemetry metrics (segment timing and heading target/error) to the pilot test.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestPedroPathingPilot.java`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Symbols touched:
- `TURN_TARGET_DELTA_RAD`
- `transitionTo(...)`
- `getHeadingErrorRadians()`
- `normalizeRadians(...)`
- `getPilotRuntime()`

What changed (short bullets):
- Added planned-vs-actual timing telemetry for forward, strafe, and turn segments.
- Added target heading and heading error telemetry.
- Added previous-state and full pilot runtime telemetry for easier diagnostics.

Button controls changed?
- no

Behavior change type:
- behind flag

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Run `Test: Pedro Pathing Pilot`
  - Record heading error in each segment
  - Record actual vs planned segment time

What passed:
- Build passed with new telemetry metrics.
- Pilot safety controls unchanged.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- `pedro-phase-2-pass` remains latest rollback tag.

Next action:
- Collect robot run data for heading error and segment timing.
- Tune turn target delta and durations from measured telemetry.

---

## Session 007

Date: 2026-09-23
Driver/Tester: TODO
Current Phase: Phase 3 - Real first Pedro path conversion
Branch: `integration/pedro-pathing`
Goal for today: Convert the pilot from timed drive commands to real Pedro follower/path execution.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestPedroPathingPilot.java`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Symbols touched:
- `setupPedroFollowerAndPaths()`
- `createFollower()`
- `runPedroPilotStateMachine()`
- `runLegacyPilotStateMachine()`
- `Follower.follow(...)`

What changed (short bullets):
- Added real Pedro `Follower` setup with `Mecanum` drivetrain + `TwoWheelLocalizer`.
- Replaced Pedro mode motion from timed powers to `Path` following.
- Added a 3-segment Pedro path: forward -> strafe -> turn.
- Kept all existing safety controls (`A` start, `X` abort, `Y` yaw reset).
- Kept legacy timed path fallback when Pedro mode is not selected.

Button controls changed?
- no

Behavior change type:
- behind flag

Build result:
- `:TeamCode:assembleDebug` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - `Test: Pedro Pathing Pilot` in Legacy mode (`X` pre-start)
  - `Test: Pedro Pathing Pilot` in Pedro mode (`B` pre-start)
  - Verify abort stops immediately in both modes
  - Record heading error and completion telemetry

What passed:
- Build passed with real Pedro path flow.
- Telemetry and safety flow remain available.

What failed:
- None in software build gate.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- `pedro-phase-2-pass` remains latest rollback tag.

Next action:
- Run robot validation and tune localizer conversion values (`forwardTicksToInches`, `strafeTicksToInches`).
- If path behavior is stable, proceed with additional Phase 3 pilot paths.

---

## Session 008

Date: 2026-10-07
Driver/Tester: TODO
Current Phase: Phase 4 - Red alliance one-touch TeleOp path + shoot-pose recovery
Branch: `integration/pedro-pathing`
Goal for today: Add the Red one-touch Pedro path to the existing TeleOp, start the path from the robot's current Pedro pose, and hold/recover at the red shooting pose when bumped.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/TeleOpConstants.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOp.java`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`

Symbols touched:
- `TeleOpConstants.RED_HOME_POSE`
- `TeleOpConstants.RED_SIDE_SHOOT_POSE`
- `RedTeamPedroTeleOp.buildSafeRouteForSide(...)`
- `RedTeamPedroTeleOp.startAssist(...)`
- `RedTeamPedroTeleOp.updateAssistStateMachine()`
- `RedTeamPedroTeleOp.finishAssistPath()`
- `RedTeamPedroTeleOp.isAtShootPose()`
- `RedTeamPedroTeleOp.isPedroControllingDrive()`

What changed (short bullets):
- Red one-touch path now starts from `follower.pose()` instead of assuming the robot is exactly at home.
- Pressing `gamepad1.b` builds a Pedro path from the current estimated pose to `TeleOpConstants.RED_SIDE_SHOOT_POSE` at `(60, 12, 123.7°)`.
- Red assist now switches into Pedro `hold(...)` at the shooting pose so the robot keeps correcting back to target if another robot bumps it.
- Added shooting-pose monitoring with position and heading tolerances plus settle time before shooting is allowed again.
- Added telemetry for assist target pose, shoot position error, shoot heading error, Pedro busy/progress, and whether shooting is paused for pose recovery.
- Blue assist still uses a simple two-segment Pedro route through the blue clearance waypoint.

Button controls changed?
- no new buttons
- Existing mappings used in this TeleOp:
  - `gamepad1.a` = blue-side assist
  - `gamepad1.b` = red-side assist
  - `gamepad1.x` = cancel assist
  - `gamepad1.y` = reset Pedro pose to red home reference

Behavior change type:
- default path changed

Build result:
- `:TeamCode:compileDebugJavaWithJavac` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Start `RED team Pedro` and verify normal field-centric drive still works.
  - Press `B` after manually moving away from home and confirm the red path starts from the current Pedro pose.
  - Confirm the robot finishes near `(60, 12, 123.7°)`.
  - Push or nudge the robot at the red shooting pose and confirm Pedro re-centers it.
  - Confirm `X` cancels path/hold immediately and returns drive control to Gamepad 1.

What passed:
- Software build passed after converting Red assist to a real Pedro follower path.
- Software build passed after adding shoot-pose hold/recovery monitoring.
- Cancel behavior remains available in code path for active Pedro motion.

What failed:
- No software compile failures.
- Real shooter/magazine pause-resume behavior is not wired yet; only the pose-gating flags are present.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- No new rollback tag created in this session.

Next action:
- Connect `shootingPausedForPoseRecovery` to the real shooter/indexer feed stop path.
- Resume firing only after `shootingAllowed` becomes true again.
- Decide whether pose recovery should pause feeder only or also spin-down/spin-up the shooter wheel.
- Add driver-facing telemetry note that `Y` should only be used when the robot is physically at the red home reference pose.

---

## Session Template

### Phase 3 pilot run data table

| Run | Backend (Legacy/Pedro) | Forward actual (s) | Strafe actual (s) | Turn actual (s) | Heading target (rad) | Heading error (rad) | Abort used (Y/N) | Notes |
|---|---|---:|---:|---:|---:|---:|---|---|
| 1 |  |  |  |  |  |  |  |  |
| 2 |  |  |  |  |  |  |  |  |
| 3 |  |  |  |  |  |  |  |  |

Date:
Driver/Tester:
Current Phase:
Branch:
Goal for today:

Files touched:
Symbols touched:

What changed (short bullets):
- 

Button controls changed?
- yes / no
- If yes, update `doc/controls/BUTTON_CONTROLS_REFERENCE.md`

Behavior change type:
- none
- behind flag
- default path changed

Build result:
Robot tests run:

What passed:
What failed:

Regression checks status:
Rollback tag status:

Next action:
