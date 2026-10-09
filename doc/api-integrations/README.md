# API Integrations

This folder stores repeatable integration playbooks.

Current playbooks:
- `PEDRO_PATHING_INTEGRATION_PLAYBOOK.md`

Tracking files:
- `PEDRO_PATHING_SESSION_LOG.md`

Use each playbook as a step-by-step skill:
1. Follow phases in order
2. Pass the gate for each phase
3. Record results before moving on

Daily logging flow:
1. Open `PEDRO_PATHING_INTEGRATION_PLAYBOOK.md`
2. Pick current phase and planned step
3. Make small changes
4. Log `Files touched`, `Symbols touched`, and `Behavior change type`
5. Record gate evidence in `PEDRO_PATHING_SESSION_LOG.md`
6. If any button control changed, update `doc/controls/BUTTON_CONTROLS_REFERENCE.md`

Drive backend selection:
- The active drive backend is chosen in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/pathing/PathingConfig.java`
- `PathingConfig.usePedroPathing()` is the switch point
- It starts on legacy drive by default
- On Driver Station before pressing Start:
  - `gamepad1.x` = Legacy Drive
  - `gamepad1.b` = Pedro Pathing
- The selected mode shows in telemetry before the OpMode starts

Button controls source:
- The central button map lives at `../controls/BUTTON_CONTROLS_REFERENCE.md`
- Update that file whenever any gamepad1 or gamepad2 button control is added, removed, or changed

