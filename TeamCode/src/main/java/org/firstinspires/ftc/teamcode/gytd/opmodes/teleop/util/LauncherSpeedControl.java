package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop.util;

import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherConstants;
import org.firstinspires.ftc.teamcode.gytd.subsystems.LauncherSubsystem;

/** Adjusts launcher target speed once per bumper press. */
public class LauncherSpeedControl {
    private boolean lastDecrease;
    private boolean lastIncrease;

    public void update(LauncherSubsystem launcher, boolean decrease, boolean increase) {
        boolean decreasePressed = decrease && !lastDecrease;
        boolean increasePressed = increase && !lastIncrease;
        lastDecrease = decrease;
        lastIncrease = increase;

        if (decreasePressed != increasePressed) {
            double step = increasePressed
                    ? LauncherConstants.VELOCITY_STEP_RAD_PER_SEC
                    : -LauncherConstants.VELOCITY_STEP_RAD_PER_SEC;
            launcher.setTargetVelocity(launcher.getTargetVelocity() + step);
        }
    }
}
