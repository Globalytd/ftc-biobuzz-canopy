# Button Controls Reference

This is the single source of truth for button controls.

Rule:
- If a button control is added, removed, or changed, update this file in the same change.
- The same button can appear more than once if it does different things in different modes or OpModes.
- If the same button would confuse drivers in the same mode, choose a clearer mapping.

Scope:
- Button, bumper, dpad, and stick-button controls only.
- Stick movement values are not listed here, EXCEPT where a stick is used as a non-drive mechanism
  trigger (push-forward/push-back to run a mechanism) instead of a drive input - those are
  functionally buttons with a deadzone, not drive controls, so they are documented below.

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
| `A` | `Test: Flower` | Deploy | Move flower out |
| `B` | `Test: Flower` | Score | Use scoring position |
| `X` | `Test: Flower` | Retract | Stow flower |
| `A` | `Test: Launcher` | Start launcher | Spin up to target velocity (formerly `Test: Shooter`) |
| `B` | `Test: Launcher` | Stop launcher | Safe stop |
| D-pad up | `Test: Launcher` | Increase target velocity | Step up tuning |
| D-pad down | `Test: Launcher` | Decrease target velocity | Step down tuning |

Note: `Test: Magazine` was removed 2026-11-08. The team switched to a passive gravity-fed chute
(intake -> launcher directly), so there is no indexer/magazine motor or test OpMode anymore.

---

## Gamepad 2

Current status:
- `RED team Pedro`, `RED team Pedro Linear motion`:
  - Left stick pushed forward (hold, past 0.2 deadzone) = intake forward
  - Left stick pulled back (hold, past 0.2 deadzone) = intake reverse (clear a jam)
  - Right stick pushed forward (hold, past 0.2 deadzone) = spin up launcher
  - `gamepad2.a` = request a shot (marks "firing" once launcher is at target velocity and all Pedro
    safety gates are open; with the gravity-fed chute there is no separate feed step - a game piece
    already sitting against the spinning wheel launches on its own)
  - `gamepad2.b` = stop launcher (and cancel pending shot)
  - `gamepad2.x` = stop all launcher mechanisms (and cancel pending shot)
  - (2026-11-08: moved intake/launcher from gamepad2 bumpers/D-pad to gamepad2 sticks - driver
    preferred not having to move thumbs off the sticks to reach bumpers/D-pad)

Rule for future changes:
- When a gamepad 2 button is added, update this file immediately.

---

## Update checklist

Before merging any control change, confirm:
- [ ] This file was updated
- [ ] The matching OpMode or subsystem was updated
- [ ] The session log mentions the control change
- [ ] The change is reflected in student-facing docs

