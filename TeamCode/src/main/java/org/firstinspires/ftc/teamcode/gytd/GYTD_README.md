# GYTD Robot Code Documentation

## Project Overview

GYTD is a barebones FTC (FIRST Tech Challenge) robot project built on the FTC SDK. This documentation covers all packages, subsystems, OpModes, and development guidelines.

The FTC SDK wiring is intentionally left mostly stock so the project can be built out incrementally.

---

## Project Structure

```
gytd/
├── autonomous/          - Autonomous period OpModes
├── drive/              - Drive subsystem (motor control, movement logic)
├── hardware/           - Centralized hardware initialization and management
├── intake/             - Intake subsystem
├── opmodes/            - OpMode organization (teleop, autonomous)
├── test/               - Hardware testing and calibration OpModes
└── vision/             - Vision processing and camera pipelines
```

---

## Packages and Components

### Hardware Package

**Purpose:** Centralized hardware configuration and initialization.

**Files:**
- `RobotHardware` - Main hardware container that holds references to all robot devices
- `HardwareConstants` - Device names and configuration constants (update to match your robot config)

**Key Device Names (update in HardwareConstants):**
- Drive motors: `front_left_drive`, `front_right_drive`, `back_left_drive`, `back_right_drive`
- Intake: `intake_motor`

---

### Drive Subsystem

**Purpose:** Robot drive and movement control.

**Location:** `drive/DriveSubsystem.java`

**Current State:** Barebones placeholder ready for motor initialization and movement logic.

---

### Intake Subsystem

**Purpose:** Robot intake mechanism control.

**Location:** `intake/IntakeSubsystem.java`

**Current State:** Barebones placeholder ready for motor/servo initialization and control logic.

---

### Vision Subsystem

**Purpose:** Vision processing and camera pipeline management.

**Files:**
- `VisionSubsystem` - Main vision controller with enable/disable and pipeline management
- `VisionPipeline` - Abstract base class for custom vision processing pipelines

**Usage:**
```java
VisionSubsystem vision = new VisionSubsystem(hardwareMap);
vision.setPipeline(new YourCustomPipeline());
vision.enable();
// ... vision.getStatus() and vision.isEnabled() available
vision.shutdown(); // cleanup
```

**Extending VisionPipeline:**
Create custom pipelines by extending `VisionPipeline` and implementing the `process(Mat input)` method.

---

## OpModes

All OpModes are organized into packages matching their game mode type and are registered in the Driver Station.

### TeleOp Mode

**Package:** `opmodes/teleop/`

**Available OpModes:**
- `MainTeleOp` - Primary driver-controlled operation mode with drive, intake, and vision integration

**Purpose:** Driver-controlled robot operation during TeleOp period.

---

### Autonomous Mode

**Package:** `opmodes/autonomous/`

**Available OpModes:**
- `AutonomousBasic` - Barebones autonomous routine ready for implementation

**Purpose:** Robot autonomous operation during Autonomous period.

---

### Test Mode

**Package:** `test/`

**Available OpModes:**
- `HardwareTest` - For hardware testing, calibration, and individual subsystem validation

**Purpose:** Debug and validate hardware and subsystem functionality.

---

## Development Workflow

### Adding a New Subsystem

1. Create a new package under `gytd/` (e.g., `gytd/shooter/`)
2. Create the subsystem class (e.g., `ShooterSubsystem.java`)
3. Add device initialization to `RobotHardware.init()`
4. Add device names to `HardwareConstants`
5. Import and instantiate in relevant OpModes
6. Update this central README with the new subsystem details

### Adding a New OpMode

1. Choose the appropriate package: `opmodes/teleop/`, `opmodes/autonomous/`, or `test/`
2. Create a new class extending `LinearOpMode`
3. Add `@TeleOp`, `@Autonomous`, or custom annotations
4. Initialize required subsystems in `runOpMode()`
5. Implement your operation logic
6. Call `shutdown()` on all subsystems before exiting

### Testing

- Use `HardwareTest` OpMode for initial hardware validation
- Create specific test OpModes in the `test/` package for subsystem validation
- Verify telemetry output for debugging

---

## Building and Deploying

### Gradle Build

From the project root:
```powershell
.\gradlew.bat :TeamCode:assembleDebug
```

### Syncing in Android Studio

File → Sync Now (or Ctrl+Alt+Y)

---

## Configuration

### Robot Hardware Configuration

Update device names in `HardwareConstants.java` to match your robot's configuration file on the Control Hub.

Example:
```java
public static final String FRONT_LEFT_DRIVE = "front_left_drive";
public static final String INTAKE_MOTOR = "intake_motor";
```

These names must exactly match the names in your Control Hub hardware configuration.

---

## Common Tasks

### Accessing Hardware in a Subsystem

```java
private DcMotor motor;

public void init(HardwareMap hardwareMap) {
    motor = hardwareMap.get(DcMotor.class, HardwareConstants.INTAKE_MOTOR);
}
```

### Creating a Vision Pipeline

```java
public class MyVisionPipeline extends VisionPipeline {
    @Override
    public void process(Mat input) {
        // Your OpenCV processing logic
        output = input.clone();
    }
}
```

### Using Vision in an OpMode

```java
vision = new VisionSubsystem(hardwareMap);
vision.setPipeline(new MyVisionPipeline());
vision.enable();

// In loop:
if (vision.isEnabled()) {
    // Use vision results
}

vision.shutdown(); // At end
```

---

## Future Enhancements

As the project develops, add more subsystems and OpModes following the established patterns:
- Additional drive modes (field-centric, etc.)
- Shooter/launcher subsystem
- Climber subsystem
- Advanced vision pipelines
- Multi-stage autonomous routines

---

## Support

- FTC SDK Documentation: https://ftc-docs.firstinspires.org/
- FTC Community Forum: https://ftc-community.firstinspires.org/
- OpenCV Documentation: https://docs.opencv.org/

---

**Last Updated:** September 2026

