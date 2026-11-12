package org.firstinspires.ftc.teamcode.gytd.pathing;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.drivetrain.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.revhub.localizers.TwoWheelConfig;
import com.pedropathing.revhub.localizers.TwoWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.hardware.HardwareConstants;

/**
 * Creates Pedro follower instances using shared, centralized drivetrain/localizer/controller defaults.
 *
 * Keep OpMode behavior in each OpMode, but keep robot identity and baseline tuning here.
 *
 * LOCALIZER SELECTION: {@link PedroTwoWheelLocalizerConstants#HAS_DEAD_WHEEL_PODS} controls which
 * localizer backs the follower:
 *  - {@code false} (no dead wheels yet): {@link MecanumEncoderIMULocalizer} - drive-motor encoders
 *    + REV Hub IMU. Less accurate (mecanum wheel slip), but works with existing hardware.
 *  - {@code true} (dead-wheel pods installed): Pedro's built-in {@code TwoWheelLocalizer} using
 *    the dedicated, non-powered pods - the accurate, Pedro-intended path. Prefer this once the
 *    pods are physically installed; see {@link PedroTwoWheelLocalizerConstants} for wiring/calibration
 *    steps.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public final class PedroFollowerFactory {
    private PedroFollowerFactory() {
        // Utility class - no instantiation
    }

    public static Follower createTwoWheelFollower(HardwareMap hardwareMap) {
        MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
            c.frontLeftName.set(HardwareConstants.FRONT_LEFT_DRIVE);
            c.frontRightName.set(HardwareConstants.FRONT_RIGHT_DRIVE);
            c.backLeftName.set(HardwareConstants.BACK_LEFT_DRIVE);
            c.backRightName.set(HardwareConstants.BACK_RIGHT_DRIVE);

            c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
            c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
            c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
            c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);

            c.manualBrakeMode.set(true);
        });

        Localizer localizer = PedroTwoWheelLocalizerConstants.HAS_DEAD_WHEEL_PODS
                ? createDeadWheelLocalizer(hardwareMap)
                : new MecanumEncoderIMULocalizer(hardwareMap);
        Drivetrain drivetrain = new Mecanum(hardwareMap, drivetrainConfig);

        ForesightConfig algorithmConfig = new ForesightConfig(c -> {
            c.forwardTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
            c.strafeTranslational.set(Controller.piecewise(Controller.proportional(0.04)).put(2.5, Controller.proportional(0.08)));
            c.coast.set(Controller.proportionalFeedforward(0.0));
            c.brake.set(Controller.proportionalFeedforward(0.0));

            c.headingFeedback.set(Controller.proportional(0.015));
            c.headingBrakeCoefficients.set(Vector2D.cartesian(0.0, 0.0));
            c.linearBrakeCoefficients.set(Matrix.diag(0.0, 0.0));
            c.quadraticBrakeCoefficients.set(Matrix.diag(0.0, 0.0));

            c.maxAchievableForwardVelocity.set(18.0);
            c.maxAchievableStrafeVelocity.set(18.0);
            c.naturalForwardDeceleration.set(18.0);
            c.naturalStrafeDeceleration.set(18.0);
        });

        return new Follower(localizer, drivetrain, new Foresight(algorithmConfig));
    }

    private static Localizer createDeadWheelLocalizer(HardwareMap hardwareMap) {
        TwoWheelConfig localizerConfig = new TwoWheelConfig(c -> {
            c.xPodName.set(PedroTwoWheelLocalizerConstants.X_POD_NAME);
            c.yPodName.set(PedroTwoWheelLocalizerConstants.Y_POD_NAME);
            c.imuName.set(HardwareConstants.IMU_NAME);
            c.imu.set(new RevHubIMU(HardwareConstants.IMU_ORIENTATION_ON_ROBOT));

            c.xPodOffset.set(PedroTwoWheelLocalizerConstants.X_POD_OFFSET_IN);
            c.yPodOffset.set(PedroTwoWheelLocalizerConstants.Y_POD_OFFSET_IN);
            c.forwardTicksToInches.set(PedroTwoWheelLocalizerConstants.FORWARD_TICKS_TO_INCHES);
            c.strafeTicksToInches.set(PedroTwoWheelLocalizerConstants.STRAFE_TICKS_TO_INCHES);
            c.xPodDirection.set(PedroTwoWheelLocalizerConstants.X_POD_DIRECTION);
            c.yPodDirection.set(PedroTwoWheelLocalizerConstants.Y_POD_DIRECTION);
        });

        return new TwoWheelLocalizer(hardwareMap, localizerConfig);
    }
}

