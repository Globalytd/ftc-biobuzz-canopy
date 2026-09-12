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
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Name", group = "Group")
@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Name", group = "Group")
public class ExampleOpMode extends com.qualcomm.robotcore.eventloop.opmode.LinearOpMode {
    @Override
    public void runOpMode() {
    }
}
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

## FTC SDK Update / Upstream Sync

### Skill: Keep the project aligned with the latest official FTC SDK release from FIRST Tech Challenge.

**Why:** The FTC robot controller project evolves over time with SDK updates and release candidates. This project should be able to absorb those upstream improvements without losing custom team code.

**Implementation:**

1. Keep the fork as `origin` and add the official FTC repo as `upstream`:
   ```powershell
   git remote add upstream https://github.com/FIRST-Tech-Challenge/FTCRobotController.git
   ```

2. Fetch the latest FTC changes and tags:
   ```powershell
   git fetch upstream --prune --tags
   ```

3. Create a safety backup before updating:
   ```powershell
   git branch backup/pre-upstream-sync
   ```

4. Merge the official FTC release into the current branch. Use the branch or tag you want to sync to:
   ```powershell
   git checkout <your-branch>
   git merge --no-ff upstream/master
   ```

   For a specific release tag:
   ```powershell
   git merge --no-ff v12.0
   ```

5. Resolve any merge conflicts in files such as:
   - `build.dependencies.gradle`
   - `FtcRobotController/src/main/AndroidManifest.xml`
   - `TeamCode/` package files and custom robot code

6. Validate the project after the merge:
   ```powershell
   .\gradlew.bat :TeamCode:assembleDebug --console=plain
   ```

7. If the build succeeds, push the merged branch:
   ```powershell
   git push origin <your-branch>
   ```

**Typical Git flow used in this repo:**
```powershell
git remote add upstream https://github.com/FIRST-Tech-Challenge/FTCRobotController.git
git fetch upstream --prune --tags
git branch backup/pre-upstream-v12.0
git merge --no-ff upstream/master -m "Merge official FTC SDK v12.0 from upstream"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
git push origin GYTD-FTC-4-Update-SDK-from-ddecode-to-biobuzz
```

**Best practice:**
- Keep all custom code under `TeamCode/`
- Prefer a normal merge rather than force-resetting
- Always validate with a Gradle build after the upstream update
- Use a backup branch before any major SDK upgrade

---

## FTC SDK Quick Start Sync

### Skill: Fast path to update this project from the latest official FTC release.

**Use when:** You want the shortest safe workflow to pull the newest FTC code from the official repo and keep your team code intact.

**Quick commands:**
```powershell
git remote add upstream https://github.com/FIRST-Tech-Challenge/FTCRobotController.git
git fetch upstream --prune --tags
git branch backup/pre-upstream-sync
git checkout <your-branch>
git merge --no-ff upstream/master -m "Merge official FTC SDK update"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
git push origin <your-branch>
```

**If you are syncing to a tagged release instead:**
```powershell
git fetch upstream --prune --tags
git merge --no-ff v12.0 -m "Merge official FTC SDK v12.0"
```

**Keep in mind:**
- Make a backup branch before merging
- Resolve conflicts in `build.dependencies.gradle`, `FtcRobotController/src/main/AndroidManifest.xml`, and `TeamCode/` if needed
- Run the Gradle build before pushing

---

**Last Updated:** September 2026

