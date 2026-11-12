# IMU Localizer Tuner Guide

This guide explains how to use `TestImuLocalizerTuner` to tune Pedro two-wheel localization.

## OpMode
- Name: `IMU Localizer Tuner`
- Source: `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestImuLocalizerTuner.java`

## Controls
- `A`: stationary drift test (10s)
- `B`: rotate closure test (CW then CCW)
- `X`: forward/back closure test
- `Y`: strafe/back closure test
- `DPad Down`: reset localizer pose to `(0,0,0)` and reset yaw
- `Left bumper`: cancel running test
- `Left stick` + `Right stick X`: manual drive when no test is active

## Pass Targets (from code)
- Stationary drift <= `1.0 in`
- Stationary heading drift <= `1.5 deg`
- Closure XY error <= `2.0 in`
- Closure heading error <= `4.0 deg`

## Setup Checklist
- Place robot on flat tile.
- Mark a start location on the field.
- Ensure battery is reasonably consistent between runs.
- Start OpMode and press `DPad Down` before each test set.
- Run each test at least 3 times.

## Test Order (recommended)
1. `A` Stationary drift
2. `B` Rotate closure
3. `X` Forward/back closure
4. `Y` Strafe/back closure

Run this full sequence, adjust one parameter group, then repeat.

## Telemetry to Watch
- `Mode`
- `Pose`
- `IMU Heading`
- `Pose-IMU heading diff`
- `Delta from mode start`
- `Last result`

## How to Tune Based on Results

### 1) Fix direction signs first
If pose moves opposite expected direction during manual drive/tests, correct:
- `c.xPodDirection.set(...)`
- `c.yPodDirection.set(...)`

Do this before changing scale/offset values.

### 2) Tune distance scale
Use closure tests:
- Forward/back error mostly high: tune `c.forwardTicksToInches.set(...)`
- Strafe/back error mostly high: tune `c.strafeTicksToInches.set(...)`

Rule of thumb:
- If reported travel is too small, increase ticks-to-inches.
- If reported travel is too large, decrease ticks-to-inches.

### 3) Tune rotational coupling offsets
If rotate closure (`B`) returns heading but XY drifts, tune:
- `c.xPodOffset.set(...)`
- `c.yPodOffset.set(...)`

Make small changes, then rerun full sequence.

### 4) Verify heading consistency
If `Pose-IMU heading diff` grows a lot, verify:
- IMU orientation config in `HardwareConstants`
- consistent yaw reset usage (`DPad Down` reset)

## Where to Apply Final Tuned Constants
Update these fields in every active Pedro `TwoWheelConfig` block:
- `xPodOffset`
- `yPodOffset`
- `forwardTicksToInches`
- `strafeTicksToInches`
- `xPodDirection`
- `yPodDirection`

Apply to these files:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/LinearNearAndFarPedroTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/RedTeamPedroTeleOpBKP.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestImuLocalizerTuner.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestArenaMapperPedro.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestPedroPathingPilot.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestRedTeamPedroIMUTeleOp.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/TestRedPedroShootRecovery.java`

## Quick Troubleshooting
- Pose jumps or huge drift: re-check pod directions and reset pose/yaw.
- Same-point returns to very different coordinates: scale factors and offsets are not tuned yet.
- Inconsistent runs: lower power, reduce wheel slip, keep battery level steady.

