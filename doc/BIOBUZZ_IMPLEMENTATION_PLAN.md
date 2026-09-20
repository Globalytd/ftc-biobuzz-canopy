# FTC BioBuzz Implementation Plan (GYTD Baseline)

Date: 2026-09-15
Project: `BioBuzz`
TeamCode base package: `org.firstinspires.ftc.teamcode.gytd`

This plan captures the current project inspection, what to reuse, and the phased implementation path. It is intentionally incremental so the project remains compilable after each phase.

---

## 1) Existing Components and Reuse Decisions

| Existing Component | Purpose | Reuse / Modify / Replace | Recommended Change |
|---|---|---|---|
| `hardware/RobotHardware.java` | Central hardware init, motor directions, IMU init/reset, bulk caching | MODIFY | Keep as core hardware container; extend with shooter/magazine/flower hardware as names are confirmed. |
| `hardware/HardwareConstants.java` | Hardware map names for drive/intake/IMU | MODIFY | Keep existing names; add constants for new mechanisms only after confirmed Control Hub config names. |
| `subsystems/DriveSubsystem.java` | Mecanum robot-centric + field-centric drive, speed scaling | REUSE + MODIFY | Reuse current drivetrain math; add heading-hold, rotate-to-heading, and move-to-coordinate hooks in later phases. |
| `subsystems/IntakeSubsystem.java` | Placeholder | REPLACE (implementation only) | Implement intake state machine and commands (`intake`, `reverse`, `stop`, `setPower`, `hasGamePiece`). |
| `subsystems/VisionSubsystem.java` | Generic vision lifecycle shell | MODIFY | Preserve shell; integrate FTC `VisionPortal` + `AprilTagProcessor` and expose target telemetry getters. |
| `vision/VisionPipeline.java` | Generic OpenCV-style pipeline base | REUSE (optional) | Keep for now; may coexist with VisionPortal-based AprilTag flow. |
| `opmodes/teleop/RobotCentricTeleOp.java` | Working robot-centric drive test teleop | REUSE | Keep as reference and fallback test mode. |
| `opmodes/teleop/FieldCentricTeleOp.java` | Working field-centric drive with IMU yaw reset | REUSE | Keep and continue using for drive validation. |
| `opmodes/autonomous/AutonomousBasic.java` | Autonomous scaffold | MODIFY | Keep minimal; transition to reusable action-based autonomous coordinator. |
| `test/HardwareTest.java` | Per-motor and IMU validation | REUSE + MODIFY | Expand to include all new mechanisms/sensors for pit diagnostics. |
| `Util.java` | Utility placeholder | MODIFY | Keep lightweight utility behavior only; avoid putting subsystem logic here. |

---

## 2) Proposed Architecture (Integrated with Current Project)

Use the current `gytd` package and extend it instead of replacing it.

```text
org.firstinspires.ftc.teamcode.gytd
├── hardware/
│   ├── HardwareConstants.java
│   └── RobotHardware.java
├── robot/
│   └── Robot.java
├── subsystems/
│   ├── DriveSubsystem.java
│   ├── LocalizationSubsystem.java
│   ├── IntakeSubsystem.java
│   ├── MagazineSubsystem.java
│   ├── ShooterSubsystem.java
│   ├── FlowerSubsystem.java
│   └── VisionSubsystem.java
├── navigation/
│   ├── Pose2d.java
│   └── FieldNavigator.java
├── commands/
│   ├── HiveTargeting.java
│   └── ShootingSequence.java
├── opmodes/
│   ├── teleop/
│   │   ├── BioBuzzTeleOp.java
│   │   ├── RobotCentricTeleOp.java
│   │   └── FieldCentricTeleOp.java
│   ├── autonomous/
│   │   ├── AutonomousBasic.java
│   │   └── (new reusable autos)
│   └── test/
│       ├── HardwareTest.java
│       ├── TestMecanumDrive.java
│       ├── TestLocalization.java
│       ├── TestIntake.java
│       ├── TestMagazine.java
│       ├── TestShooter.java
│       ├── TestVision.java
│       ├── TestHiveAlignment.java
│       └── TestFlower.java
└── vision/
    └── VisionPipeline.java
```

Design rule: OpModes orchestrate subsystems; subsystem logic does not live directly in OpModes.

---

## 3) Gradle / Dependency Status

Current status from project files:
- FTC SDK artifacts: `12.0.0` (`Inspection`, `Blocks`, `RobotCore`, `RobotServer`, `OnBotJava`, `Hardware`, `FtcCommon`, `Vision`)
- `TeamCode` depends only on `:FtcRobotController`
- No third-party libraries currently required

Decision:
- Keep existing dependencies unchanged for now.
- Use SDK-native `VisionPortal` and `AprilTagProcessor` already available via FTC Vision dependency.
- If any dependency change becomes necessary, document rationale before modification.

---

## 4) Missing Hardware Information (Needed Before Full Mechanism Wiring)

Do not guess names; confirm from Control Hub config first.

Required confirmations:
1. Magazine/indexer motor/servo names
2. Shooter motor names (and whether 1 or 2 motors)
3. Flower mechanism hardware type and names (servo/motor/slide/conveyor)
4. Sensor names for game-piece detection (`hasGamePiece`, load detection, feed confirmation)
5. Camera name for VisionPortal (for AprilTag)
6. Any existing odometry/encoder conventions and dimensions (if localization uses custom math)

---

## 5) Recommended Implementation Order

### Phase 1 - Framework and Safe Skeletons (first coding phase)
1. Add central `robot/Robot.java` to initialize and expose subsystems.
2. Keep current drivetrain behavior intact and route access through `Robot`.
3. Add subsystem skeletons with explicit states and safe `stop()` behavior:
   - `IntakeSubsystem`
   - `MagazineSubsystem`
   - `ShooterSubsystem` (velocity-control API + tunable constants)
   - `FlowerSubsystem`
4. Extend `VisionSubsystem` with AprilTag-ready API surface (no unverified formulas).
5. Add mechanism-specific test OpModes with telemetry-first outputs.
6. Ensure project compiles after each incremental edit.

Phase 1 acceptance criteria:
- Project compiles after each incremental change.
- No existing working drive OpModes are broken.
- Subsystems provide safe `stop()` behavior.
- Shooter test telemetry includes: target velocity, actual velocity, error, ready state.
- Vision test telemetry includes: tag id, range, bearing, yaw when visible.

### Phase 2 - Mechanism Foundations
1. Implement full intake behavior and state transitions (`OFF`, `INTAKING`, `REVERSING`).
2. Implement `MagazineSubsystem` loading/feeding flow with placeholders for future sensors.
3. Implement `ShooterSubsystem` velocity control and readiness checks (`atTargetVelocity`).
4. Implement `FlowerSubsystem` safe placeholder operations (`deploy`, `score`, `retract`, `stop`).
5. Expand `HardwareTest` to validate new actuators one-by-one.

### Phase 3 - Navigation and Vision Core
1. Add `navigation/Pose2d.java` and `subsystems/LocalizationSubsystem.java`.
2. Add `navigation/FieldNavigator.java` for rotate/move primitives.
3. Add AprilTag integration in `VisionSubsystem` using FTC `VisionPortal` and `AprilTagProcessor`.
4. Add `commands/HiveTargeting.java` scaffold with calibration-table interface.
5. Preserve existing drive behavior while layering new navigation APIs.

### Phase 4 - TeleOp Integration
1. Create `opmodes/teleop/BioBuzzTeleOp.java` as the main subsystem-oriented TeleOp.
2. Keep gamepad mapping modular (driver vs operator separation).
3. Add automated shooting state flow hooks: `ALIGN -> SPIN_UP -> READY -> FEED -> FIRE -> RESET`.
4. Keep `RobotCentricTeleOp` and `FieldCentricTeleOp` as fallback/diagnostic modes.

### Phase 5 - Autonomous Composition
1. Build reusable autonomous actions instead of duplicated movement logic.
2. Create coordinator flow: `Navigate -> Intake -> Navigate -> Align -> Spin -> Shoot -> Repeat -> Park`.
3. Add `commands/ShootingSequence.java` to coordinate shooter+magazine+targeting.
4. Keep autonomous routines short, composable, and testable.

### Phase 6 - Validation, Tuning, and Reliability
1. Add/finish targeted test OpModes (`TestIntake`, `TestMagazine`, `TestShooter`, `TestVision`, `TestHiveAlignment`, `TestFlower`, `TestLocalization`).
2. Tune shooter velocity constants and targeting calibration table with field data.
3. Verify fail-safe behavior (`stop()` everywhere, no conflicting motor ownership).
4. Freeze stable interfaces for competition use.
5. Document final known-good hardware names and operating procedures.

---

## 6) Implementation Rules for This Project

- Do not delete working code.
- Do not rename hardware map names unless required and validated.
- Keep classes beginner-readable and avoid giant files.
- Prefer reuse of existing drivetrain/IMU/teleop foundations.
- Keep subsystem responsibilities separated.
- Use telemetry heavily in all test OpModes.
- Keep each phase compilable before moving forward.

