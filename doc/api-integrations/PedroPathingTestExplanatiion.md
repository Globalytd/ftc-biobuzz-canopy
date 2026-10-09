# TestPedroPathingPilot Explanation

## What This Test Does

This is a **Phase 3 pilot test** for integrating the Pedro Path Following library into your FTC robot. It's designed to validate that Pedro pathing works correctly before running full autonomous routines.

### Test Flow

The test runs a **simple scripted 3-step movement sequence**:

1. **FORWARD**: Moves robot 18 inches forward
2. **STRAFE**: Moves 12 inches to the right while maintaining heading
3. **TURN**: Rotates to 35 degrees while moving slightly forward

### Two Operating Modes

The test can run in **two different backends**:

#### Mode 1: Legacy Timed Sequence
- Uses fixed time intervals (1.20s forward, 1.00s strafe, 0.80s turn)
- No path following - just timed motor power commands
- Good for basic motor verification
- Selected by pressing `B` pre-start (actually selects Pedro, `X` selects Legacy based on code)

#### Mode 2: Pedro Path Following (Recommended for this test)
- Uses the Pedro Follower with path following algorithm
- Creates paths and follows them using localizer feedback
- More accurate and smoother than timed sequences
- Shows real-time pose tracking

### Controls

**Pre-Start:**
- Press `B` to select Pedro Path Following mode
- Press `X` to select Legacy timed sequence mode

**During Test:**
- Press `A` to start the scripted pilot path
- Press `X` to abort and stop all motors
- Press `Y` to reset yaw heading

### Telemetry Data Displayed

The test displays:
- Current pilot state (IDLE, FORWARD, STRAFE, TURN, COMPLETE, ABORTED)
- Actual vs planned time for each movement segment
- Current heading and target heading
- **Pedro-specific data** (if using Pedro mode):
    - Current pose from localizer (x, y, heading)
    - Path completion percentage
    - Whether follower is busy or at parametric end
    - Motor power commands

---

## About Your Localizer Configuration

Your code currently has **placeholder starter values** for the TwoWheelLocalizer:

```java
c.xPodOffset.set(0.0);
c.yPodOffset.set(0.0);
c.forwardTicksToInches.set(0.0100);      // ❌ Needs tuning
c.strafeTicksToInches.set(0.0100);       // ❌ Needs tuning
```

These are **NOT accurate** and will cause positioning errors. You need to tune these values.

---

## How to Obtain Tuned Localizer Values

### Overview of Parameters

1. **Encoder Offsets** (`xPodOffset`, `yPodOffset`)
    - Physical offset from IMU center to each encoder wheel (in inches)
    - Affects turning accuracy

2. **Ticks-to-Inches Conversion** (`forwardTicksToInches`, `strafeTicksToInches`)
    - How many inches the robot moves per encoder tick
    - Critical for accurate distance measurement

### Step-by-Step Tuning Process

#### Step 1: Measure Your Encoder Wheels
1. Count the ticks per full motor rotation (typically 537.6 for REV MotorMax)
2. Measure your wheel diameter (in inches)
3. Calculate: `ticksToInches = (wheel_diameter × π) / ticks_per_rotation`

   **Example:**
    - Wheel diameter: 3.78 inches
    - Ticks per rotation: 537.6
    - ticksToInches = (3.78 × 3.14159) / 537.6 ≈ 0.0221

#### Step 2: Test Forward Movement
1. Create a simple test OpMode that moves the robot forward a **known distance** (e.g., 24 inches)
2. Record the actual encoder ticks received
3. Adjust `forwardTicksToInches` so the calculated distance matches reality

   `adjusted_value = (encoder_ticks_read × current_value) / actual_distance_moved`

#### Step 3: Test Strafe Movement
- Repeat Step 2 for strafing left/right
- This gives you `strafeTicksToInches`
- Note: Strafe ticks-to-inches may differ from forward due to wheel wear

#### Step 4: Measure Encoder Offsets
1. Manually move your robot's center to a known position (e.g., center of mat)
2. Record both encoder readings
3. The offsets are the **horizontal and vertical distance from IMU to each encoder wheel**

   **For your 2-wheel localizer:**
    - `xPodOffset`: distance from IMU to X-encoder (forward/back pod)
    - `yPodOffset`: distance from IMU to Y-encoder (left/right pod)

#### Step 5: Use Pedro's Built-in Tuning Tool (RECOMMENDED)
Pedro has an **interactive tuning mode** if available in your Pedro version:
- Look for `LocalizerTuner` or similar in Pedro documentation
- Or create a simple OpMode that:
    1. Starts at origin (0, 0)
    2. Moves exact distances (24", 48", etc.)
    3. Prints localizer pose vs actual pose
    4. Calculates correction factors

---

## Important: Fix Your IMU Orientation

⚠️ **Your Current Configuration:**
```java
new RevHubOrientationOnRobot(
    RevHubOrientationOnRobot.LogoFacingDirection.UP,
    RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)
```

**You said your Control Hub faces LEFT. This means:**
- USB port points LEFT (not forward)
- Logo points UP (this part is correct)

**Correct Configuration:**
```java
new RevHubOrientationOnRobot(
    RevHubOrientationOnRobot.LogoFacingDirection.UP,
    RevHubOrientationOnRobot.UsbFacingDirection.LEFT)
```

This affects heading calculations - incorrect orientation = wrong turn angles!

---

## Quick Start: Running This Test

1. **Set correct IMU orientation** ✓ (see above)
2. **Place robot on blocks** (for safety during first run)
3. **Select Backend**: Press B (Pedro mode) before Start
4. **Press Start**
5. **Press A** when ready to run path
6. Watch telemetry:
    - If using Pedro: Check if "Pedro Completion" reaches 100%
    - If movements are jerky/inaccurate: You need localizer tuning
7. **Press X** to emergency stop at any time

---

## Next Steps

1. Fix the IMU orientation for your left-facing Control Hub
2. Measure your encoder wheel diameter
3. Calculate initial `ticksToInches` values
4. Run this test and observe how accurately it tracks
5. Refine the `forwardTicksToInches` and `strafeTicksToInches` through iteration
