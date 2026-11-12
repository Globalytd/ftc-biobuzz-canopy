package org.firstinspires.ftc.teamcode.gytd.pathing;

/**
 * Shared defaults for Pedro two-wheel dead-wheel odometry configuration.
 *
 * This is ONLY used when {@link #HAS_DEAD_WHEEL_PODS} is {@code true} (see
 * {@link PedroFollowerFactory}). Until physical dead-wheel pods are installed, the follower uses
 * {@link MecanumEncoderIMULocalizer} instead - see that class's Javadoc for why drive-motor
 * encoders cannot safely be fed into this dead-wheel-only localizer.
 *
 * Once dead-wheel pods are installed:
 *   1. Plug each pod's quadrature encoder into an unused motor port on the Control/Expansion Hub.
 *   2. Update X_POD_NAME / Y_POD_NAME below to those port names (do NOT reuse drive motor names).
 *   3. Flip HAS_DEAD_WHEEL_PODS to true.
 *   4. Re-calibrate FORWARD_TICKS_TO_INCHES / STRAFE_TICKS_TO_INCHES and the pod offsets using
 *      Pedro's tuning OpModes (see com.pedropathing:tuning dependency) or a manual measured-distance
 *      test, exactly like the drive-encoder localizer's calibration procedure.
 *
 * Tune these values in one place so all Pedro followers consume the same calibration.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public final class PedroTwoWheelLocalizerConstants {
	private PedroTwoWheelLocalizerConstants() {
		// Utility class - no instantiation
	}

	// Flip to true once dead-wheel pods are physically installed and wired to the names below.
	public static final boolean HAS_DEAD_WHEEL_PODS = false;

	// Hardware config names for the dead-wheel pod encoders (NOT the drive motors).
	public static final String X_POD_NAME = "odo_x_pod";
	public static final String Y_POD_NAME = "odo_y_pod";

	// Offsets from robot center to odometry pods in inches.
	public static final double X_POD_OFFSET_IN = 0.0;
	public static final double Y_POD_OFFSET_IN = 0.0;

	// Conversion from encoder ticks to traveled inches.
	public static final double FORWARD_TICKS_TO_INCHES = 0.0100;
	public static final double STRAFE_TICKS_TO_INCHES = 0.0100;

	public static final double X_POD_DIRECTION = com.pedropathing.revhub.localizers.Encoder.FORWARD;
	public static final double Y_POD_DIRECTION = com.pedropathing.revhub.localizers.Encoder.FORWARD;
}


