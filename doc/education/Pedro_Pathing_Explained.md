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

This project uses a `TwoWheelLocalizer` with:
- wheel pod data;
- an IMU;
- wheel tick to inch conversion.

This is important because even a small offset can cause the robot to drift or think it is somewhere else.

The localizer is configured in `createFollower()`:

```java
TwoWheelConfig localizerConfig = new TwoWheelConfig(c -> {
    c.xPodName.set(HardwareConstants.FRONT_LEFT_DRIVE);
    c.yPodName.set(HardwareConstants.FRONT_RIGHT_DRIVE);
    c.imuName.set(HardwareConstants.IMU_NAME);
    ...
});
```

If the robot is not driving to the right place, this is one of the first areas to tune.

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

