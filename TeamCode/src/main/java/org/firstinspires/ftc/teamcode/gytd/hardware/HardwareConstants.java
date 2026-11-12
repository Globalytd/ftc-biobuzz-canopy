package org.firstinspires.ftc.teamcode.gytd.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

/**
 * Hardware device names and configuration constants.
 * Update these to match your robot's hardware configuration.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class HardwareConstants {
    // Drive motor names
    public static final String FRONT_LEFT_DRIVE = "front_left_drive";
    public static final String FRONT_RIGHT_DRIVE = "front_right_drive";
    public static final String BACK_LEFT_DRIVE = "back_left_drive";
    public static final String BACK_RIGHT_DRIVE = "back_right_drive";

    // Intake device names (placeholders)
    public static final String INTAKE_MOTOR = "intake_motor";
    public static final String LAUNCH_MOTOR = "launch_motor";

    // Control Hub IMU name in Robot Configuration
    public static final String IMU_NAME = "imu";

    // Shared Control Hub orientation on robot (single source of truth across the project).
    public static final RevHubOrientationOnRobot.LogoFacingDirection IMU_LOGO_FACING_DIRECTION =
            RevHubOrientationOnRobot.LogoFacingDirection.UP;
    public static final RevHubOrientationOnRobot.UsbFacingDirection IMU_USB_FACING_DIRECTION =
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
    public static final RevHubOrientationOnRobot IMU_ORIENTATION_ON_ROBOT =
            new RevHubOrientationOnRobot(IMU_LOGO_FACING_DIRECTION, IMU_USB_FACING_DIRECTION);

    private HardwareConstants() {
        // Utility class - no instantiation
    }
}
