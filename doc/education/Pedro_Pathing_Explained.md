# Pedro Pathing Explained for the RED Team Pedro TeleOp

This note explains the main Pedro Pathing pieces used in the RED team assist OpMode and how they fit together.

## 1. Why Pedro Pathing is useful

Pedro Pathing lets the robot follow a planned path on the field instead of relying only on raw driver sticks.

For this OpMode, we use it to:
- let the driver press one button and move the robot to a safe assist position;
- keep the robot centered around the hive area;
- hand driving control back to the driver when the assist ends or is cancelled.

This is useful because the robot is moving relative to the field, not just relative to itself.

## 2. The main Pedro components in this project

### Follower

The `Follower` is the robot controller that follows a path.

In this OpMode, the follower is created in `createFollower()`.

It does the most important work:
- it knows the robot pose;
- it receives a path to follow;
- it updates the drivetrain in real time;
- it can stop when the path is done or cancelled.

Key calls:
- `follower.setPose(...)`
- `follower.follow(path)`
- `follower.update()`
- `follower.stop()`

This is the main object that "drives the robot along the path."

### Path

A `Path` is the route itself.

In this OpMode we create a path by calling:

```java
activePath = line(startPose, targetPose).constant(targetPose.heading());
```

That means:
- start at the current robot pose;
- move toward the final target pose;
- keep the end heading set to the target heading.

The OpMode currently uses a simple direct line path to a waypoint. That is a safe first version. Later, you can replace it with multiple waypoints to go around the hive or under the hive when the center is blocked.

### Pose

A `Pose` is a robot position and heading.

Pedro stores this as:
- x position
- y position
- heading angle

Example:

```java
new Pose(24.0, 18.0, Math.toRadians(180.0));
```

This means:
- x = 24 inches
- y = 18 inches
- heading = 180 degrees

Important: in Pedro, heading is usually stored in radians.

### Localizer

The localizer tells Pedro where the robot really is on the field.

**Update (2026-11-08): this project does NOT use dead-wheel odometry pods.** Earlier versions of
this doc (and an earlier version of the code) assumed a `TwoWheelLocalizer` fed by dedicated,
free-spinning "dead wheel" pods. That is the normal/best way to do it, but this robot doesn't have
those pods installed yet, and the code used to make a dangerous mistake: it fed Pedro's
`TwoWheelLocalizer` with ticks from two of the **powered drive motors** instead.

Why that was a bug: `TwoWheelLocalizer` assumes its wheels spin freely and never slip. Mecanum
drive wheels are the opposite of that - their rollers are *designed* to slide sideways so the
robot can strafe. Feeding slipping-wheel data into a "this wheel never slips" localizer meant
Pedro's idea of where the robot was could drift far from reality any time the robot turned or
strafed - which is exactly when the one-touch shoot-assist paths run. Pedro would then try to
"correct" toward a target it was mis-judging the distance/angle to, which is what looked like the
robot shaking/oscillating when you pressed `A` or `B`.

**The fix:** this project now uses a custom localizer, `MecanumEncoderIMULocalizer`
(`gytd/pathing/MecanumEncoderIMULocalizer.java`), which:
- reads all four drive motor encoders (not just two) and combines them with the correct mecanum
  math, so it at least accounts for how all four wheels actually move together; and
- trusts the REV Hub IMU for heading, since the IMU doesn't care about wheel slip at all.

It is still not as accurate as true dead-wheel pods (powered wheels can still slip a little under
hard acceleration or when bumped by another robot), but it is far more consistent than the old
2-motor-encoder setup.

`PedroFollowerFactory` picks the localizer automatically:

```java
Localizer localizer = PedroTwoWheelLocalizerConstants.HAS_DEAD_WHEEL_PODS
        ? createDeadWheelLocalizer(hardwareMap)
        : new MecanumEncoderIMULocalizer(hardwareMap);
```

If the team installs real dead-wheel pods later: wire each pod's encoder into an **unused** motor
port (never reuse a drive motor's name/port), update `X_POD_NAME`/`Y_POD_NAME` in
`PedroTwoWheelLocalizerConstants` to match, and flip `HAS_DEAD_WHEEL_PODS` to `true`. No other code
needs to change - Pedro's built-in `TwoWheelLocalizer` will take over and should be noticeably more
accurate than the encoder+IMU fallback.

This is important because even a small offset can cause the robot to drift or think it is
somewhere else.

### Drivetrain

The drivetrain tells Pedro how the robot physically moves.

This project uses a mecanum drivetrain:

```java
Drivetrain drivetrain = new Mecanum(hardwareMap, drivetrainConfig);
```

The drivetrain config includes:
- motor names;
- motor directions;
- brake mode.

If a motor is reversed or the robot moves backwards when it should move forward, this section is where the fix goes.

### Foresight / Controller gains

`Foresight` is the path-following controller.

It decides how strongly the robot tries to correct its path.

This is the part you tune for:
- smooth motion;
- not overshooting the target;
- not wobbling around the final pose;
- getting to the path without too much jitter.

Example from the code:

```java
c.forwardTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
c.headingFeedback.set(Controller.proportional(0.015));
```

These numbers control how hard the robot pushes toward the route and heading target.

## 3. How the OpMode uses Pedro

This OpMode follows a simple pattern:

1. Create the follower and pathing config.
2. Press a button to start an assist.
3. Set the assist state to `FOLLOWING_PATH`.
4. Call `follower.follow(activePath)`.
5. In each loop, call `follower.update()`.
6. When the robot is close enough to the target, stop the path and return driver control.
7. Press `X` to cancel the route immediately.

The main assist control flow is:

```java
if (bluePressed && assistState != AssistState.FOLLOWING_PATH) {
    startAssist("blue", BLUE_SIDE_SHOOT_POSE);
}
```

Then:

```java
follower.update();
```

And when close enough:

```java
drive.stop();
assistState = AssistState.COMPLETE;
```

## 4. How to modify the route

Right now the code uses one direct line path. To change the route, edit the Pose constants near the top:

```java
private static final Pose BLUE_SIDE_SHOOT_POSE = new Pose(24.0, 18.0, Math.toRadians(180.0));
private static final Pose RED_SIDE_SHOOT_POSE = new Pose(24.0, -18.0, Math.toRadians(0.0));
```

These are the target points the robot tries to reach.

If you want the robot to go around the hive instead of directly toward it, the next step is to create a route with multiple points, such as:
- safe start waypoint;
- clearance waypoint;
- final approach waypoint;
- end target pose.

This is safer than a single straight line when the center area may be blocked.

## 5. How to modify the path behavior

If the robot:
- goes too fast, reduce max velocity;
- overshoots the target, reduce translational gain;
- spins too much, reduce heading gain;
- drifts, adjust wheel tick scaling and localizer offsets.

This is where the real tuning happens.

## 6. How to think about the robot movement

Pedro paths are based on the field coordinate system.

That means the robot is trying to move to a place on the field, not just move relative to its front bumper.

This is why field-centric control matches Pedro better than robot-centric control.

If the driver wants to drive "to a field position," field-centric is easier and more predictable.

## 6b. The "which way am I actually facing?" trap (important!)

Every starting `Pose` in this project has an `x`, a `y`, **and a heading**. The heading isn't just
decoration - it tells Pedro which physical direction on the field counts as "facing forward" at
that pose. If the heading you write in code doesn't match the direction the robot is *actually*
physically pointed when you call `follower.setPose(...)`, every path Pedro drives afterward will be
rotated by that same mismatch - even though all the math inside Pedro is working perfectly.

This project's convention (see `TeleOpConstants.RED_HOME_POSE`) is:
- **heading 90° = robot physically facing downfield, away from the driver** (the normal way a
  robot is placed at the start of a match).
- Under this convention, Y position = downfield distance, X position = side-to-side position.

A real bug this exact mistake caused: `TestArenaMapperPedro.HOME_POSE` was originally written as
`new Pose(12.0, 12.0, 0.0)` - heading **0°**, not 90°. But the robot is still placed facing
downfield at A1 during testing (matching real match setup). That mismatch meant:
- Selecting tile **B1** (one column over, meant to be a sideways move) made the robot drive
  **forward/away** instead.
- Selecting tile **A2** (one row forward, meant to be a forward move) made the robot **strafe
  sideways** instead.

The fix was one line: change the heading in `HOME_POSE` from `0.0` to `Math.toRadians(90.0)` so it
matches how the robot is actually placed on the field. Nothing about the localizer or path math
needed to change.

**Lesson for drivers/maintainers:** whenever you add a new starting `Pose` (a new home tile, a new
test OpMode, a new alliance color), ask "if I stand the robot at this spot exactly the way we do at
a real match, which way is its front bumper pointing?" and make sure the heading number matches
that - using this project's 90°-is-downfield convention - not just `0.0` as a lazy default.

## 7. Good next steps for the team

For a middle-school team, the best next learning steps are:
1. change the Pose target values;
2. test one assist button at a time;
3. watch telemetry for robot pose and path progress;
4. adjust the localizer and gains slowly;
5. only after the path is stable, add the full shooting logic.

## 8. Summary

The essential Pedro pieces are:
- `Follower` = follows the route
- `Path` = the route itself
- `Pose` = position + heading
- `Localizer` = where the robot thinks it is
- `Drivetrain` = hardware movement setup
- `Foresight` = tuning of speed and steering corrections

In this RED team OpMode, these pieces combine to let the driver press a button and let Pedro move the robot to a safe assist pose. That is the foundation for the more advanced shooting, aiming, and hive sequence later.

## 9. Important team reminder

The current route is still a test route. The team should replace placeholder values with measured field coordinates before match use.

Until then, this is a safe starter pathing setup for learning and testing.

