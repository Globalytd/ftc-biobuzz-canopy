# FTC BioBuzz Resume Pack

Date: 2026-09-15
Purpose: Quick restart guide after pausing at the end of Phase 1.

---

## 1) Current Status Snapshot

- Project phase: **Phase 1 complete** (framework + safe subsystem skeletons + test OpModes).
- Architecture anchor: `org.firstinspires.ftc.teamcode.gytd.robot.Robot` now wires subsystems.
- Build status at last check: `:TeamCode:assembleDebug` succeeded.
- Next target: **Phase 2 - Mechanism Foundations**.

---

## 2) What Is Already Done

### Core structure
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/robot/Robot.java`
- `Robot` exposes: drive, intake, magazine, shooter, flower, vision, and safe `stopAll()/shutdown()`.

### Subsystems (Phase 1 skeletons)
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/IntakeSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/MagazineSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/ShooterSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/FlowerSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/VisionSubsystem.java`

### Existing OpModes updated to use Robot
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/BasicTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RobotCentricTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/FieldCentricTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/autonomous/AutonomousBasic.java`

### Test OpModes added
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestMecanumDrive.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestIntake.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestMagazine.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestShooter.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestVision.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestFlower.java`

---

## 3) Hardware Info Template (Fill This Before Phase 2 Wiring)

> Do not guess names. Use exact Control Hub config names.

| Mechanism | Device Type | Config Name | Notes |
|---|---|---|---|
| Magazine / Indexer | Motor/Servo | TODO | |
| Shooter Main | DcMotorEx | TODO | |
| Shooter Secondary (if any) | DcMotorEx | TODO | |
| Flower mechanism | Servo/Motor | TODO | |
| Intake sensor (has game piece) | Distance/Touch/Color | TODO | |
| Magazine sensor (loaded) | Distance/Touch/Color | TODO | |
| Feed confirmation sensor | Distance/Touch/Color | TODO | |
| Vision camera | Webcam | TODO | |

Also confirm:
- Shooter target speed unit preference (ticks/sec or RPM)
- Shooter acceptable speed tolerance
- Any mechanism travel limits (hard stop / safe range)

---

## 4) Next Session Startup Checklist

- [ ] Open `doc/BIOBUZZ_IMPLEMENTATION_PLAN.md` and `doc/FTC_RESUME_PACK.md`
- [ ] Read `doc/ROBOT_DEPLOYMENT_GUIDE.md` before installing to the robot
- [ ] Fill the hardware table above with exact config names
- [ ] Verify robot still compiles
- [ ] Run baseline test OpModes on robot
- [ ] Start Phase 2 coding in the order below

Quick compile command:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
```

---

## 5) First Phase 2 Coding Order (Recommended)

1. **Intake real behavior**
   - Keep current states (`OFF`, `INTAKING`, `REVERSING`)
   - Wire optional sensor into `hasGamePiece()`
2. **Magazine real behavior**
   - Wire motor/servo hardware
   - Replace placeholder count with sensor-aware logic where possible
3. **Shooter real behavior**
   - Wire real `DcMotorEx`
   - Tune target velocity + tolerance from test data
4. **Flower mechanism wiring**
   - Implement actual servo/motor commands behind existing methods
5. **HardwareTest update**
   - Add new hardware checks so pit debugging remains easy

Done criteria for Phase 2 entry:
- Each mechanism runs from its own test OpMode
- Each subsystem has safe `stop()` behavior
- No existing drive OpMode regressions

---

## 6) Fast Re-orientation Notes for Future Us

- `Robot` is now the main entry point. New OpModes should use it.
- `VisionSubsystem` telemetry values are placeholders until AprilTag integration phase.
- `ShooterSubsystem` already exposes target velocity / actual velocity / error / ready fields.
- Keep changes incremental and compile after each major edit.
