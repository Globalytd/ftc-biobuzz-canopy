# GYTD Development Skills and Standards

## Documentation Management

### Central README Documentation

**Skill:** All README documentation should be consolidated into a single central README file at the root of the GYTD package. This central document serves as the single source of truth for all project documentation.

**Implementation:**
- Maintain one comprehensive README (`GYTD_README.md`) located at `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/`
- Update this central README as new packages, subsystems, OpModes, and features are added
- Individual package README files should be removed to avoid documentation duplication and inconsistency
- The central README contains:
  - Project overview and structure
  - Detailed package and component descriptions
  - OpMode documentation and availability
  - Development workflow and guidelines
  - Configuration instructions
  - Common tasks and code examples

**Benefits:**
- Single source of truth prevents outdated or conflicting documentation
- Easier to maintain consistency across the project
- New team members have one clear reference document
- Reduces navigation between multiple README files

**When to Update:**
- Adding new subsystems
- Creating new OpModes
- Changing package structure
- Adding new hardware devices
- Modifying configuration requirements
- Documenting new patterns or best practices

---

## Code Organization Standards

### Package Structure

All code should be organized into logical packages:
- `autonomous/` - Autonomous OpModes
- `drive/` - Drive subsystem
- `hardware/` - Hardware initialization and configuration
- `intake/` - Intake subsystem
- `opmodes/` - Organized OpMode directory
- `test/` - Testing and diagnostic OpModes
- `vision/` - Vision processing

### OpMode Annotations

All OpModes must have appropriate annotations:
```java
@TeleOp(name = "Name", group = "Group")
@Autonomous(name = "Name", group = "Group")
```

### Subsystem Pattern

Subsystems should:
1. Have a clear responsibility (drive, intake, vision, etc.)
2. Provide status reporting via `getStatus()` method
3. Have initialization and shutdown methods
4. Be importable and instantiable in OpModes

---

## Incremental Development

The GYTD project is built incrementally:
- Start with barebones placeholders
- Add functionality incrementally
- Test each component before adding more
- Update central documentation with each addition
- Keep build compiling at all times

---

**Last Updated:** September 2026

