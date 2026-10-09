package org.firstinspires.ftc.teamcode.gytd.opmodes.teleop;

import com.pedropathing.math.Pose;
/**
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */

public class TeleOpConstants {

    // Field reference tile center for Red Alliance home area (A4).
    // This is a reference/reset pose for drivers, not the hard-coded start of one-touch paths.
    public static final Pose RED_HOME_POSE = new Pose(12.0, 84.0, Math.toRadians(0.0));

    // One-touch Red shooting pose at C1.
    // 123.7° points from C1 back toward A4.
    public static final Pose RED_SIDE_SHOOT_POSE = new Pose(60.0, 12.0, Math.toRadians(123.7));

}
