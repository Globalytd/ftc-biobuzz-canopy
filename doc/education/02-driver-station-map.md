# Lesson 2: Code to Driver Station Map

Goal: Connect Java files to what students see on Driver Station.

---

## What Driver Station Shows

Driver Station shows OpModes.

OpModes come from classes with annotations:
- `@TeleOp(...)`
- `@Autonomous(...)`

---

## Where the Names Come From

Example from code:
- `@TeleOp(name = "Test: Mecanum Drive", group = "Test")`

What students see:
- Name in list: `Test: Mecanum Drive`
- Group/menu: `Test`

So:
- `name` = title on Driver Station
- `group` = menu section

---

## Current Menu Map (This Project)

### Test group

From `gytd/test/`:
- `Test: Hardware`
- `Test: Mecanum Drive`
- `Test: Intake`
- `Test: Magazine`
- `Test: Shooter`
- `Test: Vision`
- `Test: Flower`

### TeleOp group

From `gytd/opmodes/teleop/`:
- `Barebones TeleOp`
- `RobotCentricTeleOp` entry name (from annotation in file)
- `FieldCentricTeleOp` entry name (from annotation in file)

### Autonomous group

From `gytd/opmodes/autonomous/`:
- `AutonomousBasic` entry name (from annotation in file)

Note:
- Final shown names depend on each file's annotation values.

---

## What Telemetry Means for Students

In each OpMode loop, code calls telemetry.

Example lines:
- `telemetry.addData(...)`
- `telemetry.update()`

Students should read telemetry to answer:
- Is hardware configured?
- What state is subsystem in?
- Is value changing when controls are used?

Button control source:
- See `../controls/BUTTON_CONTROLS_REFERENCE.md` for the current gamepad1 and gamepad2 button map.
- Update that file whenever a button control changes.

---

## Student Checklist Before Pressing Play

- Correct OpMode selected?
- Correct group/menu?
- Robot on blocks if testing motors?
- Driver knows button map?
- Telemetry visible?

