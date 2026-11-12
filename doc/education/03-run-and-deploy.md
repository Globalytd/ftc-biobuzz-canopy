# Lesson 3: How to Run the Robot

Goal: Build code, send code, run OpMode safely.

---

## Part A: Build the app (Laptop)

1. Open project in Android Studio.
2. Open terminal in project root.
3. Run:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
```

4. Check for `BUILD SUCCESSFUL`.

If build fails:
- Read first error line
- Fix that file first
- Build again

---

## Part B: Connect to Control Hub

1. Turn on REV Control Hub.
2. Connect USB to laptop.
3. Verify device:

```powershell
adb devices
```

4. If missing, restart adb:

```powershell
adb kill-server
adb start-server
adb devices
```

---

## Part C: Install app to robot controller

Option 1 (Android Studio):
- Choose Control Hub device
- Click Run for `TeamCode`

Option 2 (ADB):

```powershell
adb install -r "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz\TeamCode\build\outputs\apk\debug\TeamCode-debug.apk"
```

---

## Part D: Run from Driver Station

1. Open Robot Controller app on Control Hub.
2. Open Driver Station app on DS device.
3. Connect DS to robot.
4. Select OpMode from menu.
5. Press `INIT`.
6. Check telemetry.
7. Press `PLAY`.
8. Press `STOP` when done.

---

## Part E: First tests students should run

Run in this order:
1. `Test: Hardware`
2. `Test: Mecanum Drive`
3. `Test: Intake`
4. `Test: Magazine`
5. `Test: Shooter`
6. `Test: Vision`
7. `Test: Flower`

Why this order:
- Hardware first
- Drive second
- Mechanisms after

---

## Safety Rules (Always)

- Wheels up on blocks for first motor test
- Keep fingers clear of moving parts
- One driver talks, one driver controls
- Stop immediately if motion is unexpected

---

## Quick Teacher Script

Say this:
- "Pick the right OpMode."
- "Init first. Check telemetry."
- "Play only when safe."
- "Stop when test goal is done."

