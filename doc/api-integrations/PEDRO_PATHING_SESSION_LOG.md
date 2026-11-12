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

## Session 009

Date: 2026-11-08
Driver/Tester: TODO
Current Phase: Phase 4 - Localizer root-cause fix for shoot-assist oscillation
Branch: `integration/pedro-pathing`
Goal for today: Diagnose and fix erratic/oscillating robot movement whenever Pedro shoot-assist (gamepad1 A/B) took control.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/MecanumEncoderIMULocalizer.java` (new)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PedroMecanumLocalizerConstants.java` (new)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PedroTwoWheelLocalizerConstants.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PedroFollowerFactory.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestArenaMapperPedro.java`
- `doc/api-integrations/PEDRO_PATHING_SESSION_LOG.md`
- `doc/education/Pedro_Pathing_Explained.md`
- `doc/education/LESSON_PROGRESS_TRACKER.md`

Symbols touched:
- `MecanumEncoderIMULocalizer` (implements `Localizer`)
- `PedroMecanumLocalizerConstants.TICKS_TO_INCHES`
- `PedroTwoWheelLocalizerConstants.HAS_DEAD_WHEEL_PODS`
- `PedroTwoWheelLocalizerConstants.X_POD_NAME` / `Y_POD_NAME`
- `PedroFollowerFactory.createTwoWheelFollower(...)`
- `PedroFollowerFactory.createDeadWheelLocalizer(...)`
- `TestArenaMapperPedro.HOME_POSE`

What changed (short bullets):
- **Root cause found:** this robot has no dead-wheel odometry pods, but the old code fed two
  **powered mecanum drive-motor encoders** (`front_left_drive`, `front_right_drive`) directly into
  Pedro's `TwoWheelLocalizer`, which is only designed for passive, free-spinning dead-wheel pods.
  Mecanum drive wheels slip sideways by design, so the localizer's pose estimate diverged from the
  real robot pose any time the assist path strafed or turned - and the Foresight path controller
  then "corrected" toward that bad estimate, which is what looked like oscillation/erratic motion.
- Added `MecanumEncoderIMULocalizer`: a custom `Localizer` that uses all four existing drive
  encoders (correct mecanum forward kinematics) for translation, and the REV Hub IMU (not affected
  by wheel slip) for heading. This is the new default localizer since this robot has no dead wheels.
- Added `PedroMecanumLocalizerConstants.TICKS_TO_INCHES` - a rough starting estimate that **must be
  field-calibrated** (drive a known distance, compare to reported pose delta, scale accordingly).
- Added `PedroTwoWheelLocalizerConstants.HAS_DEAD_WHEEL_PODS` flag (default `false`). If the team
  installs real dead-wheel pods later, flip this to `true`, set `X_POD_NAME`/`Y_POD_NAME` to the
  pods' actual encoder port names, and `PedroFollowerFactory` will automatically build Pedro's
  built-in (more accurate) `TwoWheelLocalizer` instead - no other code changes needed.
- Fixed a second, separate bug found while testing: `TestArenaMapperPedro.HOME_POSE` declared
  heading `0.0`, but every other pose in this project (see `TeleOpConstants.RED_HOME_POSE`) uses
  heading `90°` to mean "robot physically facing downfield/away from the driver." Because the
  robot is actually placed facing downfield at A1 (matching real match setup), the mismatched 0°
  heading caused columns (A1->B1, meant to be sideways) to drive the robot forward instead, and
  rows (A1->A2, meant to be forward) to strafe it sideways instead. Fixed by changing `HOME_POSE`
  to `new Pose(12.0, 12.0, Math.toRadians(90.0))` to match the project's field-heading convention.

Why this matters for drivers/students (plain language):
- The robot's self-reported position was never accurate once dead wheels were ruled out and the
  old setup kept reading two drive motors as if they were free-spinning odometry wheels. Think of
  it like a car's speedometer reading tire spin while the tires are sliding on ice - the number on
  the dash stops matching how far the car actually went. Pedro believed that wrong number and kept
  "correcting" toward a target it was mis-estimating distance/angle to, causing the shaking/erratic
  motion you saw when pressing A/B.
- Separately, `HOME_POSE`'s heading is like telling the GPS app "you're facing north" when you're
  actually facing east - every direction it then gives you ("turn right") will be rotated 90° from
  what you expect, even though the GPS math itself is working correctly. That is exactly what the
  `TestArenaMapperPedro` symptom was: not a math bug, a "which way is the robot actually pointed at
  the start?" bug.

Button controls changed?
- no

Behavior change type:
- default path changed (localizer swap affects both `RedTeamPedroTeleOp` and
  `TestArenaMapperPedro`, since both go through `PedroFollowerFactory`)

Build result:
- `:TeamCode:compileDebugJavaWithJavac` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Stationary stability: power on, don't touch sticks, confirm `Pose` telemetry stays essentially
    fixed for ~10s.
  - Straight-line calibration: drive a measured distance (e.g. 48in) and compare to the reported
    pose delta; adjust `PedroMecanumLocalizerConstants.TICKS_TO_INCHES` until they match within
    ~1in over several feet.
  - Strafe test: confirm strafe direction/magnitude matches physical movement.
  - Rotation test: confirm heading telemetry matches physical turn direction/amount.
  - `RED team Pedro`: confirm `A` (loading side) and `B` (garden side) assist now converge smoothly
    instead of oscillating.
  - `Test: Arena Mapper Pedro`: place robot at A1 facing downfield (away from driver), confirm
    selecting `B1` now moves sideways and `A2` now moves forward/away.

What passed:
- Software build passed with the new localizer, the dead-wheel-pod swap flag, and the
  `TestArenaMapperPedro` heading fix.

What failed:
- No software compile failures. Physical robot validation is still pending - a clean compile does
  not prove the real robot behavior is correct; the tests above must be run on the physical robot.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- No new rollback tag created in this session.

Next action:
- Run the physical validation checklist above and record results in the Phase 3 pilot run data
  table or a new table in this log.
- Calibrate `PedroMecanumLocalizerConstants.TICKS_TO_INCHES` from real measured-distance tests.
- If/when dead-wheel pods are installed: wire them to unused encoder ports, update
  `PedroTwoWheelLocalizerConstants.X_POD_NAME`/`Y_POD_NAME`, flip `HAS_DEAD_WHEEL_PODS` to `true`,
  and re-calibrate using Pedro's `com.pedropathing:tuning` OpModes.

---

## Session 010

Date: 2026-11-08
Driver/Tester: TODO
Current Phase: Phase 4 - Launcher/intake control fixes
Branch: `integration/pedro-pathing`
Goal for today: Fix gamepad2 A/B not firing the launcher, and the intake having no controls wired at all in the real TeleOps.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/LauncherSubsystem.java` (renamed from `ShooterSubsystem.java`)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/LauncherConstants.java` (new)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestLauncher.java` (renamed from `TestShooter.java`)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/robot/Robot.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/LinearNearAndFarPedroTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOpBKP.java` (mechanical rename only, dead/unused backup OpMode)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/autonomous/AutonomousBasic.java`
- `doc/controls/BUTTON_CONTROLS_REFERENCE.md`

Symbols touched:
- `LauncherSubsystem` (renamed from `ShooterSubsystem`), `LauncherSubsystem.LauncherState`
- `LauncherSubsystem.startLauncher()` / `stopLauncher()` (renamed from `startShooter()`/`stopShooter()`)
- `LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC`
- `Robot.getLauncher()` (renamed from `getShooter()`)
- `RedTeamPedroTeleOp.updateLauncherControls()` / `updateIntakeControls()` (new)
- `LinearNearAndFarPedroTeleOp.updateLauncherControls()` / `updateIntakeControls()` (new)

What changed (short bullets):
- **Root cause 1 (why A/B did nothing on the launcher):** `LauncherSubsystem` (then
  `ShooterSubsystem`) defaults its target velocity to `0.0`, and nothing in either real TeleOp ever
  called `setTargetVelocity(...)` with a real value. Pressing "spin up" called `startLauncher()`,
  which commanded the motor to hold a target velocity of **0** - i.e. effectively "stay off," even
  though the button logic itself was correct. Fixed by adding `LauncherConstants` with a tunable
  `DEFAULT_TARGET_VELOCITY_RAD_PER_SEC`, applied once right after `Robot` is constructed in both
  `RedTeamPedroTeleOp` and `LinearNearAndFarPedroTeleOp`. **This placeholder value still needs to be
  tuned on the real robot using `Test: Launcher`** (see that OpMode's Javadoc).
- **Root cause 2 (why nothing triggered the intake):** `RedTeamPedroTeleOp` and
  `LinearNearAndFarPedroTeleOp` both created an `IntakeSubsystem` and displayed its status in
  telemetry, but never called `intake()`/`reverse()`/`stop()` from any gamepad button - there was no
  `updateIntakeControls()` method at all. Added one, mapped to gamepad2 D-pad up (forward) and D-pad
  down (reverse), matching the project's existing "hold while active" pattern used elsewhere.
- Renamed `ShooterSubsystem` -> `LauncherSubsystem` (and `ShooterState` -> `LauncherState`,
  `startShooter`/`stopShooter` -> `startLauncher`/`stopLauncher`, `Robot.getShooter()` ->
  `Robot.getLauncher()`) per driver/team preference for "launcher" terminology. This also now
  matches the existing hardware config name `launch_motor` in `HardwareConstants`, which was already
  using launcher terminology even before this rename.
- Renamed `Test: Shooter` OpMode to `Test: Launcher` (same file moved, same controls/behavior).
- `RedTeamPedroTeleOpBKP.java` (an older, unused/dead backup OpMode) was given the same mechanical
  symbol renames only, just to keep the module compiling - it was not otherwise touched or fixed.

Why this matters for drivers/students (plain language):
- The launcher bug is like a car with cruise control set to "0 mph": pressing the gas pedal
  (gamepad2 right bumper) correctly told the cruise control to engage, but the target speed it was
  told to hold was zero the whole time, so the engine never revved. Setting a real default target
  speed at startup is like setting cruise control to an actual speed before engaging it.
- The intake bug was simpler: the code to make the intake spin already existed and already worked
  (see `Test: Intake`), it just was never connected to a button inside the real driving OpModes -
  like a working light switch that was never wired to the light fixture.

Button controls changed?
- yes
- Updated `doc/controls/BUTTON_CONTROLS_REFERENCE.md` (new Gamepad 2 section for
  `RedTeamPedroTeleOp`/`LinearNearAndFarPedroTeleOp`, renamed `Test: Shooter` rows to `Test: Launcher`)

Behavior change type:
- default path changed (launcher now actually spins when commanded; intake now actually responds to
  gamepad2 D-pad)

Build result:
- `:TeamCode:compileDebugJavaWithJavac` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - `Test: Launcher`: confirm D-pad up/down tuning still works, find a good real target velocity.
  - Put the tuned value into `LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC`.
  - `RED team Pedro`: confirm gamepad2 right bumper spins the launcher, `A` fires once loaded/ready,
    `B`/`X` stop it, D-pad up/down on gamepad2 run the intake forward/reverse.
  - Repeat on `RED team Pedro Linear motion`.
  - Confirm `Test: Intake` and `Test: Magazine` are unaffected (not touched this session).

What passed:
- Software build passed after the full rename and control wiring across all affected OpModes.

What failed:
- No software compile failures. `LauncherConstants.DEFAULT_TARGET_VELOCITY_RAD_PER_SEC` is still an
  unverified placeholder and must be tuned on the physical robot before competition use.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- No new rollback tag created in this session.

Next action:
- Tune and record the real launcher target velocity from `Test: Launcher`.
- Decide if gamepad2 D-pad is the final intake control mapping, or if the team wants intake moved to
  bumpers/triggers instead.

---

## Session 011

Date: 2026-11-08
Driver/Tester: TODO
Current Phase: Phase 4 - Remove magazine, remap gamepad2 to bumpers
Branch: `integration/pedro-pathing`
Goal for today: Remove the magazine/indexer subsystem (team is using a passive gravity-fed chute
instead) and move intake/launcher controls from D-pad to gamepad2 bumpers.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/MagazineSubsystem.java` (deleted)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestMagazine.java` (deleted)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOpBKP.java` (deleted - dead/unused backup, not worth maintaining through this change)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/robot/Robot.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/LinearNearAndFarPedroTeleOp.java`
- `doc/controls/BUTTON_CONTROLS_REFERENCE.md`

Symbols touched:
- `Robot.getMagazine()` (removed)
- `RedTeamPedroTeleOp.updateIntakeControls()` / `updateLauncherControls()`
- `LinearNearAndFarPedroTeleOp.updateIntakeControls()` / `updateLauncherControls()`

What changed (short bullets):
- Deleted `MagazineSubsystem` and its `Test: Magazine` OpMode entirely. The team is using a passive
  gravity-fed chute from intake straight to the launcher wheel instead of a powered indexer motor -
  there is no "load"/"feed one"/"reverse magazine" step to control anymore.
- `Robot` no longer constructs or exposes a magazine subsystem; `stopAll()` no longer stops it.
- Remapped gamepad2 controls in both `RedTeamPedroTeleOp` and `LinearNearAndFarPedroTeleOp`:
  - Left bumper (hold) now runs intake forward (previously D-pad up).
  - Right bumper (hold) still spins up the launcher (unchanged).
  - D-pad down (hold) still reverses intake to clear jams.
  - `gamepad2.y` (previously magazine load) and the previous left-bumper magazine-reverse action are
    gone - there is nothing left for the magazine to do.
  - `gamepad2.a` ("request shot") still exists as a safety gate: once the launcher reaches target
    velocity and Pedro's shoot-pose/pose-recovery gates are open, it calls `launcher.markFiring()`
    for telemetry/state tracking. There is no feed-motor call anymore (gravity does the feeding),
    so this is now purely a "state/telemetry" marker, not a physical action - flagged here in case a
    future sensor-based gate needs to hook in at this exact point.

Why this matters for drivers/students (plain language):
- Think of the old magazine like a vending machine with a motorized shelf that pushed one item
  forward at a time when you pressed a button. The new gravity-fed chute is like a gumball machine
  dispenser slide - once a ball is intaken, it just rolls downhill into the launcher wheel on its
  own. There is nothing left to "push," so all the magazine code and its buttons were deleted rather
  than left around doing nothing.
- Controls moved from D-pad to bumpers because holding a bumper is more natural for a continuous
  "run while held" action (intake, launcher) than D-pad, which the team wanted to reserve for other
  uses later.

Button controls changed?
- yes
- Updated `doc/controls/BUTTON_CONTROLS_REFERENCE.md` (removed `Test: Magazine` rows, rewrote the
  Gamepad 2 section for `RedTeamPedroTeleOp`/`LinearNearAndFarPedroTeleOp`)

Behavior change type:
- default path changed (magazine removed entirely; intake/launcher gamepad2 mapping changed from
  D-pad to bumpers)

Build result:
- `:TeamCode:compileDebugJavaWithJavac` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - `RED team Pedro`: confirm gamepad2 left bumper runs intake forward, right bumper spins up the
    launcher, D-pad down reverses intake, `A`/`B`/`X` behave as described above.
  - Repeat on `RED team Pedro Linear motion`.
  - Confirm a game piece actually flows intake -> chute -> launcher wheel under gravity alone with
    no powered assist, and confirm it launches reliably once the wheel is at target speed.

What passed:
- Software build passed after removing `MagazineSubsystem`/`TestMagazine` and remapping gamepad2.

What failed:
- No software compile failures. Physical gravity-feed reliability (does the piece always reach the
  wheel without a stuck/jam point) has not been validated on the robot yet.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- No new rollback tag created in this session.

Next action:
- Validate gravity-feed reliability on the physical chute; if pieces jam, consider whether a passive
  deflector/ramp angle change is needed (software can't fix a mechanical jam).
- Decide the final meaning of `gamepad2.a`/"request shot" now that there's no feed motor - keep it as
  a pure safety/telemetry gate, or remove it if the team decides any spinning wheel should fire
  automatically.

---

## Session 012

Date: 2026-11-08
Driver/Tester: TODO
Current Phase: Phase 4 - Intake/launcher moved from bumpers to joysticks
Branch: `integration/pedro-pathing`
Goal for today: Driver preference - replace gamepad2 bumper/D-pad controls for intake and launcher
with joystick push gestures so both thumbs can stay on the sticks.

Files touched:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/LinearNearAndFarPedroTeleOp.java`
- `doc/controls/BUTTON_CONTROLS_REFERENCE.md`

Symbols touched:
- `RedTeamPedroTeleOp.GAMEPAD2_STICK_DEADZONE` (new)
- `RedTeamPedroTeleOp.updateIntakeControls()` / `updateLauncherControls()`
- `LinearNearAndFarPedroTeleOp.GAMEPAD2_STICK_DEADZONE` (new)
- `LinearNearAndFarPedroTeleOp.updateIntakeControls()` / `updateLauncherControls()`

What changed (short bullets):
- Replaced gamepad2 left bumper (intake forward) / D-pad down (intake reverse) with: push the left
  stick forward (away from the driver) for intake forward, pull it back for intake reverse. A
  `GAMEPAD2_STICK_DEADZONE` of `0.2` ignores small stick drift/noise near center.
- Replaced gamepad2 right bumper (spin up launcher) with: push the right stick forward past the
  same deadzone.
- `gamepad2.a`/`b`/`x` (request shot / stop launcher / stop all) are unchanged.
- Applied identically to both `RedTeamPedroTeleOp` and `LinearNearAndFarPedroTeleOp`.

Why this matters for drivers/students (plain language):
- This is purely an ergonomics change, not a logic change - the same "is this input past a small
  threshold" check that used to read `gamepad2.left_bumper` (a true/false button) now reads
  `-gamepad2.left_stick_y > 0.2` (a continuous stick position turned into a true/false check). The
  driver's thumb never has to leave the stick to reach a bumper or D-pad anymore.

Button controls changed?
- yes
- Updated `doc/controls/BUTTON_CONTROLS_REFERENCE.md` (Gamepad 2 section rewritten; scope note
  updated to explain why stick positions are documented here as an exception to the normal
  "stick movement values are not listed" rule)

Behavior change type:
- default path changed (intake/launcher gamepad2 input source changed from digital bumpers/D-pad to
  analog stick-push-as-trigger)

Build result:
- `:TeamCode:compileDebugJavaWithJavac` -> BUILD SUCCESSFUL

Robot tests run:
- Not run in this terminal session (perform on robot):
  - Push gamepad2 left stick forward -> intake runs forward; pull back -> intake reverses; center ->
    intake stops.
  - Push gamepad2 right stick forward -> launcher spins up; release/center -> launcher stops.
  - Confirm the 0.2 deadzone feels right (no accidental intake/launcher activation from a stick that
    is merely resting near center but not perfectly at zero).

What passed:
- Software build passed after the control remap.

What failed:
- No software compile failures. Deadzone value (0.2) is a reasonable starting guess, not yet
  confirmed comfortable by an actual driver on the physical controller.

Regression checks status:
- Software compile check: PASS
- Driver Station/robot runtime checks: PENDING (next robot session)

Rollback tag status:
- No new rollback tag created in this session.

Next action:
- Get driver feedback on the 0.2 deadzone and adjust `GAMEPAD2_STICK_DEADZONE` if it feels too
  sensitive or too sluggish.

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
