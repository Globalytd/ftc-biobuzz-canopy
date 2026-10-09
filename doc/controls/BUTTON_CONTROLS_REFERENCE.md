# Button Controls Reference

This is the single source of truth for button controls.

Rule:
- If a button control is added, removed, or changed, update this file in the same change.
- The same button can appear more than once if it does different things in different modes or OpModes.
- If the same button would confuse drivers in the same mode, choose a clearer mapping.

Scope:
- Button, bumper, dpad, and stick-button controls only.
- Stick movement values are not listed here.

Note:
- Duplicate button labels are okay when the context is different.
- Example: `X` can mean "Legacy Drive" before Start and "Stop intake" inside `Test: Intake`.

---

## Gamepad 1

### Driver selection before Start

| Control | Where it works | Action | Notes |
|---|---|---|---|
| `X` | TeleOp/Test pre-start | Select Legacy Drive | Default choice; used for baseline comparison |
| `B` | TeleOp/Test pre-start | Select Pedro Pathing | Phase 2+ selection; recommended for pilot testing |

### Drive tests

| Control | Where it works | Action | Notes |
|---|---|---|---|
| `Y` | `Test: Mecanum Drive` | Reset yaw heading | Use before floor driving |
| Left bumper | `Test: Mecanum Drive` | Slow mode | Speed scale 0.45 |
| Right bumper | `Test: Mecanum Drive` | Full speed mode | Speed scale 1.0 |
| `A` | `Test: Pedro Pathing Pilot` | Start pilot sequence | Runs forward/strafe/turn script |
| `X` | `Test: Pedro Pathing Pilot` | Abort pilot sequence | Stops drive motors immediately |
| `Y` | `Test: Pedro Pathing Pilot` | Reset yaw heading | Re-zero heading during pilot |

### TeleOp drive

| Control | Where it works | Action | Notes |
|---|---|---|---|
| Left bumper | `Robot-Centric TeleOp`, `Field-Centric TeleOp`, `Barebones TeleOp` | Slow mode | Speed scale 0.45 |
| Right bumper | `Robot-Centric TeleOp`, `Field-Centric TeleOp`, `Barebones TeleOp` | Full speed mode | Speed scale 1.0 |
| `Y` | `Field-Centric TeleOp` | Reset yaw heading | Helps re-center driving |

### Mechanism tests

| Control | Where it works | Action | Notes |
|---|---|---|---|
| `A` | `Test: Intake` | Intake forward | Pull game piece in |
| `B` | `Test: Intake` | Reverse intake | Push game piece out |
| `X` | `Test: Intake` | Stop intake | Safe stop |
| `A` | `Test: Magazine` | Load | Placeholder count increases |
| `B` | `Test: Magazine` | Feed one | Placeholder count decreases |
| `Y` | `Test: Magazine` | Reverse | Run magazine backward |
| `X` | `Test: Magazine` | Stop | Safe stop |
| `A` | `Test: Shooter` | Start shooter | Spin up to target velocity |
| `B` | `Test: Shooter` | Stop shooter | Safe stop |
| D-pad up | `Test: Shooter` | Increase target velocity | Step up tuning |
| D-pad down | `Test: Shooter` | Decrease target velocity | Step down tuning |
| `A` | `Test: Flower` | Deploy | Move flower out |
| `B` | `Test: Flower` | Score | Use scoring position |
| `X` | `Test: Flower` | Retract | Stow flower |

---

## Gamepad 2

Current status:
- No gamepad 2 button controls are assigned yet.

Rule for future changes:
- When a gamepad 2 button is added, update this file immediately.

---

## Update checklist

Before merging any control change, confirm:
- [ ] This file was updated
- [ ] The matching OpMode or subsystem was updated
- [ ] The session log mentions the control change
- [ ] The change is reflected in student-facing docs

