# Red Pedro Shoot Recovery Test

Audience: Middle school students
Goal: Practice the Red one-touch Pedro path and watch the robot recover if it gets bumped out of place
Style: Short steps, simple words

---

## What this test is for

This test helps you check one important behavior:

- the robot drives to the Red shooting pose
- the robot holds that pose
- if another robot bumps it, the robot notices
- Pedro drives it back to the shooting pose
- shooting is only allowed again after the robot is back in position

This is a practice test, not a match mode.

---

## Where the test lives

Open this OpMode in the Driver Station list:

- `Test: Red Pedro Shoot Recovery`

The code file is:

- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestRedPedroShootRecovery.java`

---

## Buttons

Use Gamepad 1:

- `B` = start the red one-touch path
- `A` = simulate a bump while holding at the shooting pose
- `X` = cancel immediately
- `Y` = reset the Pedro pose back to the red home reference

---

## What the test does

### Step 1: Start
Press `B`.

The robot:
- reads its current Pedro pose
- builds a path to the red shooting pose
- drives there automatically

### Step 2: Hold
When it reaches the shooting pose, Pedro switches to hold mode.

That means:
- the robot keeps trying to stay at the correct spot
- if it drifts, Pedro corrects it

### Step 3: Simulate a bump
Press `A`.

This test does not use a full physics simulator.
Instead, it changes the pose estimate in software to imitate a bump.

That lets you test:
- what happens if the robot gets nudged
- whether the robot pauses shooting
- whether the robot re-centers itself
- whether shooting becomes allowed again only after it is stable

### Step 4: Cancel
Press `X`.

The test stops Pedro and gives the driver control back right away.

---

## What to watch in telemetry

Look for these telemetry lines:

- `Test State`
- `Pose`
- `Shoot Pose`
- `Pos Error (in)`
- `Heading Error (deg)`
- `Shooting Allowed`
- `Paused For Recovery`
- `Pedro Busy`
- `Pedro Holding`

### What the values mean

- **Pos Error**: how far the robot is from the shooting spot
- **Heading Error**: how far the robot is rotated away from the correct angle
- **Shooting Allowed**: tells you if the robot is lined up well enough to shoot
- **Paused For Recovery**: tells you if the robot was bumped and is re-centering

---

## What counts as a good run

A good run looks like this:

1. Press `B`
2. Robot drives to the red shooting pose
3. Robot enters hold mode
4. Press `A` to simulate a bump
5. Robot notices it is out of place
6. Robot corrects itself
7. Telemetry shows shooting is allowed again after it settles

---

## Important note about simulation

This is **not** a full desktop robot simulator.

Instead, it is a **pose-level practice test**.

That means:
- the code still runs on FTC hardware
- the test can pretend the robot got bumped by changing the pose estimate
- this is the easiest way for students to test recovery behavior in this project

If you want a real physics simulator later, that would be a separate project.

---

## Safety reminder

- Put the robot on blocks for the first run
- Keep hands away from wheels and mechanisms
- Start slowly and watch telemetry
- Use `X` if the robot does something unexpected

---

## Teacher note

This test is useful before match practice because it teaches students three ideas:

- how Pedro follows a path
- how the robot knows when it is lined up
- how the robot recovers when it gets bumped

