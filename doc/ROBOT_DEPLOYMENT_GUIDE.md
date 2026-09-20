# FTC BioBuzz Robot Deployment Guide

Date: 2026-09-16
Purpose: Standardized steps to build, install, and validate the BioBuzz robot code on the competition robot.

---

## 1) Project status

The project is currently set up as an FTC Android Studio project and has been verified to compile successfully.

Build command used successfully:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
```

The generated APK is here:

```text
C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz\TeamCode\build\outputs\apk\debug\TeamCode-debug.apk
```

---

## 2) Required tools

- Android Studio (Narwhal 3 Feature Drop or later recommended)
- USB cable to the robot controller device, or ADB over Wi-Fi if already configured
- Robot Controller device (Control Hub or Android phone)
- Driver Station device
- FTC Robot Controller app installed and working on the robot controller device

---

## 3) Build the project

From the project root:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
```

Expected result:
- Gradle finishes with `BUILD SUCCESSFUL`
- APK created under `TeamCode/build/outputs/apk/debug/`

If the build fails:
- check the Android SDK is installed in Android Studio
- confirm the project has not been opened with an outdated Gradle/AGP version
- re-run `gradlew` from the project root

---

## 4) Install to the robot controller

### Option A: Install from Android Studio (recommended)

1. Connect the robot controller device to the laptop with USB.
2. Enable Developer Options and USB debugging on the controller device.
3. Open the project in Android Studio.
4. Make sure the robot controller device appears in the device selector.
5. Select the target device and click Run / Debug.
6. Choose the `TeamCode` app module as the deployment target.
7. Wait for Gradle to finish building and Android Studio to push the APK.

This is the easiest and most reliable method for everyday deployment.

### Option B: Install directly with ADB

If the controller is connected and recognized by ADB:

```powershell
adb devices
adb install -r "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz\TeamCode\build\outputs\apk\debug\TeamCode-debug.apk"
```

If the device is not detected:
- confirm USB debugging is enabled
- reconnect the cable
- confirm the device trusts the computer
- check `adb devices` again

---

## 5) Start the robot controller and driver station

1. Power on the Control Hub / robot controller device.
2. Wait for the FTC Robot Controller app to finish starting.
3. Open the Driver Station app on the driver station phone/tablet.
4. Confirm the robot controller and driver station are connected to the same team number.
5. Ensure the correct robot configuration is configured in the FTC settings.
6. Verify the device sees the motors, servos, IMU, and sensors under the configured hardware map.

---

## 6) Verify the robot code before match use

Run the test OpModes in this order:

- `TestMecanumDrive`
- `TestIntake`
- `TestMagazine`
- `TestShooter`
- `TestVision`
- `TestFlower`

Check the following:
- all motors spin in the correct direction
- no motor names are mismatched in the Robot Controller config
- all sensors respond normally
- the robot does not drift unexpectedly
- no OpMode crashes on init or start

---

## 7) Daily deployment checklist

Before each session:

- [ ] Project still builds with `:TeamCode:assembleDebug`
- [ ] Robot controller is connected to the laptop or ADB
- [ ] Team number is correct
- [ ] Robot config names match the actual hardware map
- [ ] Test OpModes are run before teleop/autonomous use
- [ ] Battery is healthy and connected

---

## 8) Troubleshooting

### Build errors
- Re-run the Gradle command from project root
- Make sure Android Studio is using the correct JDK/SDK setup
- Try a Gradle sync if the IDE is showing stale project state

### Device not detected
- Enable USB debugging
- Check cable/port
- Run `adb kill-server` then `adb start-server`
- Reconnect the device

### App installs but robot still behaves incorrectly
- Confirm configured hardware names match real robot config
- Re-check motor direction and servo ranges
- Ensure the correct OpMode is selected in the Driver Station

---

## 9) Recommended future workflow

1. Build in the project root with Gradle
2. Install to robot controller via Android Studio or ADB
3. Run the hardware test OpModes
4. Only then move to teleop or autonomous validation
5. Keep each subsystem change incremental and re-test after major edits

This keeps the robot stable and reduces the chance of deploying an untested change.

---

## 10) Quick command summary

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
adb devices
adb install -r "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz\TeamCode\build\outputs\apk\debug\TeamCode-debug.apk"
```

