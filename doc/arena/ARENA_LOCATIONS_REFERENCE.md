# BioBuzz Arena Locations Reference

This document is the field-location guide for BioBuzz opmode implementation.
Use these locations as the canonical reference when naming path targets, pose constants,
and alliance-specific shooting or parking positions.

## Red Alliance

- Shooting position `C2` — near garden
- Shooting position `C5` — near loading zone
- Loading / parking zone `A5`

## Blue Alliance

- Shooting position `D2` — near loading zone
- Shooting position `D5` — garden
- Loading / parking zone `F2`

## Implementation Notes

- Keep alliance-specific constants aligned with this guide when adding or updating Pedro paths.
- Prefer descriptive constant names in code, such as `RED_SIDE_SHOOT_POSE` or `BLUE_CLEARANCE_WAYPOINT_POSE`.
- If the field map changes, update this document first, then update the opmode constants and path logic.
- For driver-facing documentation, reference this file instead of repeating the positions in multiple places.

