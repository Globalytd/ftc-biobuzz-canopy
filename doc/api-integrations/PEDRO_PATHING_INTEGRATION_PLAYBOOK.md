# Pedro Pathing Integration Playbook

Date: 2026-09-23
Purpose: Add Pedro Pathing safely, in small steps, with no regressions.

---

## 1) How to use this playbook

Treat this as a team skill.

Rules:
- Do one phase at a time.
- Do not skip gates.
- If gate fails, stop and fix.
- Keep legacy behavior available until final promotion.

---

## 2) Safety principles

- Working code is the default.
- Pedro is opt-in during rollout.
- Every phase ends with build + robot test.
- Keep rollback tags for each phase.
- Never change TeleOp and Autonomous in the same phase.

---

## 3) Branch and tag strategy

Create and keep one main integration branch:
- `integration/pedro-pathing`

Use milestone tags:
- `pre-pedro-baseline`
- `pedro-phase-1-pass`
- `pedro-phase-2-pass`
- `pedro-phase-3-pass`
- `pedro-phase-4-pass`

Suggested setup:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
git checkout -b integration/pedro-pathing
git tag pre-pedro-baseline
```

### Phase change record format (use every step)

For each phase/session, always record:
- `Phase ID` (example: Phase 2)
- `Files modified` (full paths)
- `Symbols modified` (class/method names)
- `Behavior change` (`none`, `behind flag`, or `default path changed`)
- `Why changed` (one line)
- `Gate evidence` (build/test result)
- `Rollback tag` (created or pending)

---

## 4) Phase plan with gates

### Phase 0 - Baseline freeze

Goal:
- Capture current behavior before adding Pedro.

Actions:
- Build project.
- Run baseline tests: drive + core test OpModes.
- Save telemetry screenshots/videos for reference.

What must be documented as modified:
- Baseline branch/tag creation only
- Documentation/log files updated
- No runtime code behavior changes

Gate to pass:
- `:TeamCode:assembleDebug` passes.
- Current TeleOp and test OpModes behave as expected.

---

### Phase 1 - Dependency only

Goal:
- Add Pedro library with no runtime behavior changes.

Actions:
- Add Pedro dependency only.
- Do not route any driving logic through Pedro yet.

What must be documented as modified:
- Gradle repository/dependency file changes
- Exact dependency coordinates and versions
- Confirmation that runtime behavior is unchanged

Gate to pass:
- Build passes.
- OpMode list unchanged on Driver Station.
- Robot behavior unchanged.

---

### Phase 2 - Adapter seam

Goal:
- Add an interface/wrapper so code can choose legacy drive or Pedro drive.

Drive selection point:
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PathingConfig.java`
- `PathingConfig.usePedroPathing()` controls which backend is used
- Before pressing Start on Driver Station:
  - `gamepad1.x` selects Legacy Drive
  - `gamepad1.b` selects Pedro Pathing

Actions:
- Keep legacy path as default.
- Add a single default-off pathing flag in code (`PathingConfig`) to switch paths.
- Keep the seam additive so the existing OpMode calls do not change.
- Add telemetry line that shows active path.

Phase 2 note:
- The current implementation uses a runtime drive selection held in `PathingConfig`.
- The driver picks the mode on gamepad1 before the OpMode starts.
- A future Driver Station settings toggle can still be added later if needed.

What must be documented as modified:
- New adapter interface/wrapper files
- Feature flag location and default value
- Any TeleOp/test telemetry additions

Gate to pass:
- With flag OFF, behavior matches baseline.
- With flag ON, app still stable (even if minimal behavior initially).

---

### Phase 3 - Test OpMode pilot only

Goal:
- Validate Pedro in an isolated test OpMode.

Actions:
- Create a dedicated pilot test (example: `TestPedroPathing`).
- Run simple paths first (short line, then turn, then combined).
- Record path error, heading error, stop behavior.

What must be documented as modified:
- New test OpMode file(s)
- Any tuning/constants files changed
- Telemetry fields added for validation

Gate to pass:
- Test runs repeatably without hangs.
- Stop behavior is safe and immediate.
- Telemetry is understandable by drivers and programmers.

---

### Phase 4 - Autonomous pilot only

Goal:
- Use Pedro in a separate autonomous pilot OpMode.

Actions:
- Keep current autonomous path unchanged.
- Create a second auto for Pedro pilot.
- Validate init, play, stop, and end state safety.

What must be documented as modified:
- New autonomous pilot OpMode and related path files
- Any robot wiring/init code touched for pilot
- Explicit list of unchanged legacy autonomous files

Gate to pass:
- Pilot auto completes reliably across repeated runs.
- No regressions in existing autonomous mode.

---

### Phase 5 - Controlled promotion

Goal:
- Promote Pedro to main autonomous flow.

Actions:
- Migrate one routine at a time.
- Keep fallback option for at least one full practice session.

What must be documented as modified:
- Which routine moved to Pedro in this step
- Which fallback remains available
- Any default behavior switches made

Gate to pass:
- Team accepts reliability and repeatability.
- Fallback not needed in normal practice.

---

## 5) Regression test checklist (run every phase)

Build:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
.\gradlew.bat :TeamCode:assembleDebug --console=plain
```

Driver Station checks:
- OpMode names still appear correctly.
- Existing Test OpModes still run.
- Existing TeleOp still drives normally.
- Existing autonomous still starts/stops safely.

Robot checks:
- IMU yaw reset still works.
- Motors stop at end of OpMode.
- No unexpected drift/spin at init/play.

---

## 6) Rollback procedure

If a gate fails:
1. Stop merging.
2. Revert latest phase commit(s) on branch.
3. Re-test against prior phase tag.
4. Fix in a new small commit.
5. Re-run phase gate.

Fast rollback example:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
git checkout integration/pedro-pathing
git reset --hard pedro-phase-2-pass
```

---

## 7) Per-session working script

Use this at start of each Pedro session:

- Read this file.
- Read `doc/FTC_RESUME_PACK.md`.
- Confirm current phase.
- Run build.
- Run only today\'s planned tests.
- Write session notes before ending.

---

## 8) Session log template

### Phase 3 pilot data capture table (robot runs)

Use this table during `Test: Pedro Pathing Pilot` runs.

| Run | Backend (Legacy/Pedro) | Forward actual (s) | Strafe actual (s) | Turn actual (s) | Heading target (rad) | Heading error (rad) | Abort used (Y/N) | Notes |
|---|---|---:|---:|---:|---:|---:|---|---|
| 1 |  |  |  |  |  |  |  |  |
| 2 |  |  |  |  |  |  |  |  |
| 3 |  |  |  |  |  |  |  |  |

Recommended checks:
- Keep abort easy and immediate in every run.
- Compare Legacy vs Pedro on the same floor and battery condition.
- Tune one value at a time, then rerun all three rows.

Copy this block into your daily notes:

```text
Date:
Driver/Tester:
Current Phase:
Goal for today:
Files touched:
Symbols touched:
Behavior change type (none/behind flag/default changed):
Why changed:
Build result:
Robot tests run:
What passed:
What failed:
Regression checks status:
Rollback tag status:
Next action:
```

---

## 9) Merge policy

A Pedro phase is merge-ready only when all are true:
- Phase gate passed
- Regression checklist passed
- Session log updated
- Rollback tag created

---

## 10) Scope guardrails

During Pedro integration, do not mix in unrelated changes:
- no subsystem refactors not needed for Pedro
- no hardware naming changes unless required
- no test cleanup unrelated to phase objective

Small focused PRs are safer and easier to debug.

---

## 11) Button control rule

If a Pedro-related change adds, removes, or changes a button control:
- update `doc/controls/BUTTON_CONTROLS_REFERENCE.md` in the same change
- update the matching OpMode or test code
- record the control change in `PEDRO_PATHING_SESSION_LOG.md`

This keeps the button map as the single source of truth for gamepad1 and gamepad2.
