# Copilot Instructions: Pedro Pathing Assisted TeleOp

## Purpose

Help us create a **Java FTC TeleOp OpMode** that combines normal driver control with reusable, button-triggered automated actions using Pedro Pathing.

This code is for a middle-school FTC team. Favor code that students can read, test, explain, and maintain. Use small classes, descriptive names, constants, comments that explain *why*, and telemetry that shows what the robot is doing.

## Before Writing Code

First inspect the existing FTC project. Do not guess class names, motor names, button mappings, package names, or Pedro Pathing API methods.

Identify and report:

1. The existing basic-drive TeleOp class and its left/right stick behavior.
2. The installed Pedro Pathing version and its correct TeleOp/manual-drive API.
3. The hardware-map names and direction settings for the drivetrain, intake, turret, launcher, magazine/indexer, LEDs, sensors, camera, and servos.
4. Existing subsystems or helper classes that should be reused.
5. The robot coordinate convention, starting-pose convention, and field units.
6. The AprilTag camera and processor already used by the team.
7. Any missing hardware or unanswered configuration questions.

Present a short implementation plan before making broad changes. Compile after each small stage.

## Required Behavior

### 1. Normal Driver Control

- Mark the OpMode with `@TeleOp` and implement it as an iterative, non-blocking OpMode unless the existing project architecture requires another supported pattern.
- Preserve the same left-stick and right-stick controls used by the team's basic-drive OpMode.
- Use Pedro Pathing's supported TeleOp/manual-drive features when appropriate.
- Keep normal driving available whenever no assisted action is active.
- Add a clearly named speed mode, such as normal/precision, only if it matches the existing controls.
- Apply the same field-centric or robot-centric setting used by the basic-drive code. Do not silently change the driving convention.

#### Gamepad Responsibilities

- **Gamepad 1 is the driver controller.** It owns drivetrain movement during normal TeleOp driving.
- While the one-button assisted sequence is following a path, the automation temporarily controls the drivetrain.
- If the sequence is cancelled, completed, or enters a fault, drivetrain control must return immediately and predictably to Gamepad 1.
- On cancellation, explicitly cancel Pedro path following, command zero drive power, and enable Gamepad 1 manual drive on the next control-loop update.
- **Gamepad 2 is the mechanism/operator controller.** Provide clearly documented controls for collecting pollen, reversing the intake, preparing the launcher, safely aiming the turret when supported, feeding/firing one pollen into the hive, and stopping mechanism actions.
- Gamepad 2 manual shooting must use the same safety interlocks as assisted shooting. It must not bypass launcher-speed, turret-limit, artifact-available, jam, or other readiness checks.
- Use edge detection for one-time actions such as firing. Use hold controls only where holding is intentional, such as manual intake or outtake.
- Do not assign actual buttons until the existing team control map has been inspected. Keep the final mappings in one easy-to-find code section and display a short reminder in telemetry.

### 2. One-Button Assisted Shooting Sequence (one for the blue side of the hive and another for the red side of the hive the shooting positions will be different)

Assign one configurable button to begin an assisted shooting sequence. A single press—not holding the button—must start the sequence.

Implement button edge detection so one press creates only one request. Model the sequence as a state machine, command sequence, or action scheduler. **Do not use long `sleep()` calls or blocking `while` loops.**

Suggested states:

1. `IDLE`
2. `DRIVING_TO_SHOOTING_POSITION`
3. `SEARCHING_FOR_TAG`
4. `AIMING_TURRET`
5. `WAITING_FOR_READY`
6. `FIRING`
7. `COMPLETE`
8. `CANCELLED` or `FAULT`

Sequence requirements:

1. Start from the robot's current estimated Pedro pose.
2. Generate or follow a path from that current pose to a configurable `SHOOTING_POSE`.
3. Stop within configurable position and heading tolerances.
4. Find the required AprilTag and reject observations that are missing, stale, low-confidence, or the wrong ID.
5. Interpret the team's “OK-to-shoot height” as a named, configurable measurement with units and tolerance. Confirm whether this means image pixel Y, camera-relative vertical offset, range/elevation, or another measurement; do not assume.
6. Calculate the turret target using the tag observation and turn the turret toward the target.
7. Confirm every readiness condition before firing:
   - robot is at the shooting pose;
   - drivetrain is settled;
   - required tag is valid;
   - tag height/alignment is within tolerance;
   - turret is within its angle tolerance;
   - launcher has reached its requested velocity;
   - at least one artifact is available;
   - no timeout or safety fault is active.
8. Feed and launch the pollen/nectar into the hive.
9. Decrement the tracked artifact count only after a sensor or reliable mechanism event confirms that an artifact left the robot.
10. Return to `IDLE`, leaving normal TeleOp driving active.

If a readiness condition fails, do not fire. Show the specific reason in telemetry and allow the driver to retry or cancel.

### 3. Driver Override and Safety

- A clearly documented cancel button must immediately stop the path and automated mechanism actions, then return drivetrain control to Gamepad 1.
- Significant manual stick input should optionally cancel assisted driving. Put the threshold in a named constant.
- After cancellation, Gamepad 2's manual intake and shooting controls must remain available unless a safety fault specifically disables the affected mechanism.
- Define priority explicitly: emergency stop/cancel, safety interlocks, active assisted sequence, Gamepad 2 manual mechanisms, and Gamepad 1 manual drive whenever path following is inactive.
- When the OpMode stops, set all motors and servos to safe states.
- Add timeouts for path following, tag search, turret aiming, launcher spin-up, and feeding.
- Respect turret mechanical limits and prevent cable wrapping. Prefer an absolute or homed turret position; do not rely on an unknown encoder zero.
- Do not fire if the launcher is below speed or the turret/robot is not aligned.
- Prevent the assisted sequence from starting twice while it is already active.
- Define what happens if the robot starts with an inaccurate pose. Provide an easy pose-reset/localization procedure for the driver.

### 4. Automatic Pollen Intake — Phase 1

For the first version, detect and collect **yellow pollen only**. Design the detector interface so color-coded nectar can be added later without rewriting the OpMode.

- Use a forward-facing vision or proximity system to decide that pollen is close enough to collect.
- Use a separate intake/magazine sensor to confirm that the pollen actually entered the robot. Vision proximity alone must not increase the count.
- Run the intake automatically only when:
  - yellow pollen is confidently detected inside a configurable collection zone;
  - the robot has fewer than four artifacts;
  - the robot is not firing or in an unsafe/conflicting state.
- Stop the intake when the object is collected, the capacity is full, detection is lost for a configured time, a jam is detected, or the driver cancels it.
- Debounce sensor readings so one pollen is not counted several times.
- Add a manual intake, outtake, and jam-clear override for testing and match recovery.
- Put those manual controls on Gamepad 2. Automatic intake must yield immediately to an intentional Gamepad 2 command, subject to capacity and safety rules.

Recommended architecture:

- `ArtifactDetector` interface: reports candidate type, confidence, and relative location/distance.
- `IntakeSubsystem`: starts/stops/reverses intake and detects collection/jams.
- Later add nectar classification as another detector result without changing the TeleOp state logic.

### 5. Gamepad 2 Manual Hive Shooting

Gamepad 2 must be able to collect pollen and shoot into the hive even when the one-button sequence is not being used or has been cancelled.

- Provide a deliberate control to spin up or prepare the launcher.
- Provide a separate, edge-detected control to feed/fire one artifact so holding a button cannot cause repeated shots.
- Use AprilTag-assisted turret aiming when a valid target is visible. Provide limited manual aiming only if the team needs it and the hardware supports it safely.
- Do not automatically move the drivetrain during a Gamepad 2 manual shot. Gamepad 1 retains driving control unless the driver intentionally starts the assisted sequence.
- Make any Gamepad 1 speed limit during aiming/firing configurable and show when it is active in telemetry.
- Reject a shot with a visible reason if the turret is unsafe, launcher is not ready, no artifact is available, the magazine is jammed, or another required condition is false.
- A confirmed shot must decrement the artifact count exactly once.
- Releasing controls, cancelling, timing out, or stopping the OpMode must leave all mechanisms in defined safe states.

### 6. Four-Artifact Capacity Control

Do not rely only on a software integer. The preferred solution is physical confirmation in the storage/indexing mechanism.

Recommended sensor choices, in order of preference:

1. A beam-break or break-beam sensor at the magazine entrance to count artifacts entering.
2. Another beam-break sensor at the exit/feed point to confirm artifacts leaving.
3. If the magazine has four fixed slots, one occupancy sensor per slot gives the most reliable full/not-full status.
4. A color/distance sensor can be used if beam-break packaging is difficult, but readings must be calibrated and debounced.

Counting rules:

- Keep `artifactCount` between 0 and 4.
- Increment only on a debounced empty-to-blocked-to-empty entry event that represents one completed intake.
- Decrement only after a confirmed exit event.
- Do not run automatic intake when count is 4.
- Provide a guarded driver correction/reset control because jams, preload setup, or missed sensor events can make the software count wrong.
- If sensor readings disagree, stop automatic handling, report a fault, and require driver action.

LED behavior:

- `0–3` artifacts: configurable normal/team color.
- `4` artifacts: bright, unmistakable full indication (for example solid green).
- Fault or jam: a different flashing pattern.
- Never use the full-capacity color for a fault.

### 7. Telemetry and Student Learning

Show concise Driver Station telemetry for:

- robot pose `(x, y, heading)`;
- drive mode;
- current automation state;
- active path and target shooting pose;
- AprilTag ID, age, confidence, and relevant alignment/height measurement;
- turret current/target angle;
- launcher current/target velocity;
- artifact count and entrance/exit sensor states;
- intake state;
- readiness checks and the exact reason firing is blocked;
- timeout, jam, or sensor fault messages.
- current Gamepad 1/Gamepad 2 control ownership and a short button map.

Use comments to explain each state transition and sensor event. Avoid commenting obvious Java syntax.

## Suggested Code Organization

Use the project's existing architecture when it is already clear and reusable. Otherwise prefer:

- `AssistedTeleOp.java` — reads controls, coordinates subsystems, and owns the high-level state machine.
- `DriveSubsystem.java` — manual drive, pose access, path start/update/cancel, and settled check.
- `TurretSubsystem.java` — target angle, limits, homing, and at-target check.
- `LauncherSubsystem.java` — velocity control, at-speed check, feed action, and safe stop.
- `IntakeSubsystem.java` — automatic/manual intake and jam handling.
- `ArtifactCounter.java` — debounced entrance/exit events and capacity rules.
- `VisionSubsystem.java` — AprilTag and yellow-pollen observations.
- `LedSubsystem.java` — normal, full, active, and fault indications.
- `RobotConstants.java` — named values with units and explanations.

Do not create unnecessary abstractions if equivalent team classes already exist.

## Configuration Constants

Put tunable values in one clearly labeled class. Include units in every name or comment:

- shooting pose X, Y, and heading;
- path end position and heading tolerances;
- drivetrain settle velocity and settle time;
- required AprilTag ID;
- tag observation maximum age;
- OK-to-shoot height/alignment target and tolerance;
- turret zero, direction, gear ratio, minimum/maximum angle, and tolerance;
- launcher target ticks/second or RPM and tolerance;
- yellow-pollen color/vision thresholds and collection zone;
- intake and feed power;
- beam-break polarity and debounce times;
- manual-cancel stick threshold;
- every state timeout;
- maximum artifact count of 4.

Do not bury unexplained “magic numbers” inside control logic.

## Incremental Build Plan

Implement and test in this order:

1. Preserve basic manual driving in the new TeleOp.
2. Add the automation state enum, button edge detection, cancel behavior, and telemetry with no mechanisms moving.
3. Verify that cancellation immediately returns driving to Gamepad 1.
4. Add Gamepad 2 manual intake, outtake, and safe-stop controls.
5. Add current-pose-to-shooting-pose path following and test at low speed.
6. Add AprilTag validation and alignment telemetry without firing.
7. Add turret aiming with mechanical limits.
8. Add launcher velocity control and a dry-run readiness checklist.
9. Add Gamepad 2 single-shot control with all safety interlocks.
10. Enable one controlled assisted firing cycle.
11. Add entrance/exit sensors and validate artifact counting manually.
12. Add yellow-pollen detection and automatic intake.
13. Add full-capacity LEDs and fault patterns.
14. Only after pollen collection is reliable, add nectar classification.

For each stage, provide a short student test checklist and expected telemetry. Keep the robot on blocks whenever a mechanism could move unexpectedly.

## Acceptance Tests

The work is complete only when these tests pass:

1. Manual driving matches the basic-drive OpMode.
2. One button press starts exactly one shooting sequence.
3. The path begins at the current estimated pose and ends within tolerance.
4. The driver can cancel from every automation state.
5. Cancelling, completing, or faulting the sequence immediately returns driving to Gamepad 1.
6. Gamepad 2 can collect pollen and command one safe hive shot without taking drivetrain ownership from Gamepad 1.
7. Holding the Gamepad 2 fire button does not cause repeated unintended shots.
8. Missing, wrong, stale, or misaligned AprilTags prevent firing and show a reason when tag alignment is required.
9. Turret limits and launcher-speed checks prevent unsafe firing in both assisted and Gamepad 2 modes.
10. No long blocking loops or sleeps freeze the OpMode.
11. One pollen entering increments the count once; one leaving decrements it once.
12. The count cannot exceed 4 or fall below 0.
13. Intake stops at 4 artifacts and the full LED indication turns on.
14. A jam or sensor disagreement stops automatic handling and reports a fault.
15. Stopping the OpMode leaves all hardware in a safe state.

## Required Output From Copilot

Before editing, list the files/classes found and the missing information. Then:

1. Propose the smallest set of code changes.
2. Implement one stage at a time.
3. Explain each changed file in student-friendly language.
4. Show all assumptions explicitly.
5. Compile after every stage and fix errors before continuing.
6. Do not replace working team code or invent hardware/API names to make the example compile.

## Questions the Team Must Answer

Leave clear TODOs until these are confirmed:

1. Which button starts assisted shooting, and which button cancels it?
2. What are the final Gamepad 1 driving controls and Gamepad 2 intake/shooting controls?
3. Should Gamepad 1 driving be speed-limited while Gamepad 2 is aiming or firing?
4. What are the exact field coordinates and heading of `SHOOTING_POSE`?
5. Which AprilTag ID identifies the hive target?
6. What exactly is “AprilTag at the OK-to-shoot height,” and what are its units and tolerance?
7. Does the turret have an absolute encoder, limit switch, or homing sensor?
8. What launcher velocity produces a reliable shot?
9. What camera/sensor will detect yellow pollen, and where is it mounted?
10. What sensors exist at the magazine entrance, storage slots, and exit?
11. How is the four-artifact magazine/indexer mechanically organized?
12. Does the basic drive use field-centric or robot-centric control?
13. How will the driver reset or verify the robot pose before a match?
