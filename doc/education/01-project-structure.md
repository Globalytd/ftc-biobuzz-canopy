# Lesson 1: Project Structure (Simple)

Goal: Know where to put code.

---

## Big Picture

- `TeamCode` = your team code
- `FtcRobotController` = SDK app code
- We mostly work in `TeamCode`

---

## Main Folders in TeamCode

### 1) Hardware names

Path:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/hardware/HardwareConstants.java`

What goes here:
- Exact Control Hub config names
- Example: `front_left_drive`, `imu`, `intake_motor`

Why:
- One place for names
- Fewer typing mistakes

---

### 2) Robot hardware setup

Path:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/hardware/RobotHardware.java`

What goes here:
- Get motors, sensors, IMU from `hardwareMap`
- Set motor direction
- Set brake mode
- Set hub bulk caching

Why:
- One place to wire robot code to real hardware

---

### 3) Robot container

Path:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/robot/Robot.java`

What goes here:
- Build all subsystems in one object
- Give OpModes easy access to subsystems

Why:
- OpModes stay clean
- Startup and shutdown are consistent

---

### 4) Subsystems

Folder:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/subsystems/`

Examples:
- `DriveSubsystem.java`
- `IntakeSubsystem.java`
- `MagazineSubsystem.java`
- `ShooterSubsystem.java`
- `FlowerSubsystem.java`
- `VisionSubsystem.java`

What goes here:
- Logic for one mechanism
- States and commands
- Safety stop behavior

Why:
- Small code blocks
- Easy to test one part at a time

---

### 5) OpModes (what Driver Station runs)

Folders:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/teleop/`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/opmodes/autonomous/`

What goes here:
- Match code
- Driver controls
- Auto sequences

Why:
- These are the programs students select on Driver Station

---

### 6) Utilities

Paths:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/Util.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/vision/VisionPipeline.java`

What goes here:
- Helper methods
- Shared utility code
- Vision base classes

---

### 7) Tests

Folder:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/`

Examples:
- `HardwareTest.java`
- `TestMecanumDrive.java`
- `TestIntake.java`
- `TestMagazine.java`
- `TestShooter.java`
- `TestVision.java`
- `TestFlower.java`

What goes here:
- One test per mechanism
- Safe checks before match code

Why:
- Find problems early

---

## Quick Rule for Students

- New motor name? Edit `HardwareConstants.java`
- New hardware wiring code? Edit `RobotHardware.java`
- New mechanism behavior? Edit a file in `subsystems/`
- New match mode? Add file in `opmodes/`
- New pit test? Add file in `test/`

