# Lesson Progress Tracker

Use this after every class.
Keep notes short.

---

## Current Lesson Status

- Current lesson file:
- Last section taught:
- Date:
- Teacher:

---

## Where We Stopped

- Last thing students completed:
- Last demo done:
- Last robot test run:
- Open questions from students:

---

## What Changed Since Last Class

| Date | File Changed | What Changed (short) | Why |
|---|---|---|---|
| 2026-09-21 | doc/education/01-project-structure.md | Added project structure lesson | Start middle school curriculum |
| 2026-09-21 | doc/education/02-driver-station-map.md | Added Driver Station mapping lesson | Help students connect code to DS |
| 2026-09-21 | doc/education/03-run-and-deploy.md | Added run/deploy lesson | Teach safe robot start flow |
| 2026-09-21 | doc/education/README.md | Added lesson index | Navigation |
| 2026-11-08 | gytd/pathing/MecanumEncoderIMULocalizer.java, PedroFollowerFactory.java | Replaced drive-motor-encoder misuse of TwoWheelLocalizer with a correct mecanum+IMU localizer | Fixed Pedro shoot-assist oscillation (see Pedro_Pathing_Explained.md section 2, Localizer) |
| 2026-11-08 | gytd/test/TestArenaMapperPedro.java | Fixed HOME_POSE heading from 0 deg to 90 deg | Matched project's "90 deg = facing downfield" convention; fixed swapped row/column movement (see Pedro_Pathing_Explained.md section 6b) |

Add new rows each class.

---

## Next Class Plan

- [ ] Review last lesson for 5 minutes
- [ ] Demo one new idea
- [ ] Hands-on student practice
- [ ] Run one test OpMode
- [ ] Record what changed in this file

---

## Student Understanding Check (quick)

- Can student find `HardwareConstants.java`?
- Can student explain what `RobotHardware.java` does?
- Can student find a subsystem file?
- Can student pick the right OpMode on DS?
- Can student explain INIT vs PLAY?

---

## Parking Lot (teach later)

- Quiz ideas
- Hard words to define
- New controls to teach
- New subsystem lessons to add

