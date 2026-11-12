package org.firstinspires.ftc.teamcode.gytd.pathing;

import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.MotionState;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.gytd.hardware.HardwareConstants;

/**
 * Mecanum drive-encoder + REV Hub IMU localizer.
 *
 * This robot has no dedicated dead-wheel odometry pods. Pedro's built-in {@code TwoWheelLocalizer}
 * is designed for passive, free-spinning pods and produces unreliable pose estimates when fed
 * ticks from powered mecanum drive wheels (which slip laterally by design, especially during
 * strafing/rotation) - that mismatch was the root cause of the shoot-assist oscillation.
 *
 * This localizer instead:
 *  - Trusts the REV Hub IMU for heading (no wheel-slip error on rotation).
 *  - Uses standard mecanum forward kinematics across all four drive encoders to estimate
 *    robot-centric translation each loop (same axial/lateral sign convention as
 *    {@link AbstractMecanumDriveBackend}), then rotates that into the field frame using the
 *    current IMU heading before accumulating position.
 *
 * This is still less accurate than true dead-wheel/Pinpoint/OTOS localization - wheel slip during
 * heavy acceleration or defense contact will still introduce some translational drift - but it is
 * far more consistent than feeding only 2 of 4 powered wheels into a localizer that assumes no
 * kinematic coupling between them, with no heading compensation at all.
 *
 * IMPORTANT: {@link PedroMecanumLocalizerConstants#TICKS_TO_INCHES} below is a rough default and
 * MUST be re-measured on the physical robot (drive a known straight-line distance, compare
 * commanded distance vs. the Pedro pose delta, and adjust) before trusting shoot-assist paths.
 *
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public class MecanumEncoderIMULocalizer implements Localizer {
    private final Encoder frontLeft;
    private final Encoder frontRight;
    private final Encoder backLeft;
    private final Encoder backRight;
    private final RevHubIMU imu;
    private final Timer timer;

    private Pose pose = Pose.zero();
    private Velocity velocity = Velocity.zero();
    private double headingOffsetRad = 0.0;
    private double previousHeadingRad = 0.0;

    public MecanumEncoderIMULocalizer(HardwareMap hardwareMap) {
        frontLeft = new Encoder(hardwareMap.get(DcMotorEx.class, HardwareConstants.FRONT_LEFT_DRIVE));
        frontRight = new Encoder(hardwareMap.get(DcMotorEx.class, HardwareConstants.FRONT_RIGHT_DRIVE));
        backLeft = new Encoder(hardwareMap.get(DcMotorEx.class, HardwareConstants.BACK_LEFT_DRIVE));
        backRight = new Encoder(hardwareMap.get(DcMotorEx.class, HardwareConstants.BACK_RIGHT_DRIVE));

        // Match the physical motor directions from RobotHardware so encoder sign agrees with
        // commanded drive power sign (FL/FR/BL forward, BR reversed).
        frontLeft.setDirection(Encoder.FORWARD);
        frontRight.setDirection(Encoder.FORWARD);
        backLeft.setDirection(Encoder.FORWARD);
        backRight.setDirection(Encoder.REVERSE);

        imu = new RevHubIMU(HardwareConstants.IMU_ORIENTATION_ON_ROBOT);
        imu.initialize(hardwareMap, HardwareConstants.IMU_NAME);

        timer = new Timer();
        reset();
    }

    @Override
    public void setPose(Pose newPose) {
        pose = newPose;
        previousHeadingRad = newPose.heading();
        headingOffsetRad = normalize(newPose.heading() - imu.getHeading());
    }

    @Override
    public void update() {
        double dt = timer.seconds();
        timer.reset();
        if (dt <= 1e-6) {
            dt = 1e-6;
        }

        frontLeft.update();
        frontRight.update();
        backLeft.update();
        backRight.update();

        double ticksToInches = PedroMecanumLocalizerConstants.TICKS_TO_INCHES;
        double flIn = frontLeft.getDeltaPosition() * ticksToInches;
        double frIn = frontRight.getDeltaPosition() * ticksToInches;
        double blIn = backLeft.getDeltaPosition() * ticksToInches;
        double brIn = backRight.getDeltaPosition() * ticksToInches;

        // Standard mecanum forward kinematics (robot-centric), matching the axial/lateral sign
        // convention used by AbstractMecanumDriveBackend.driveRobotCentric():
        //   frontLeftPower  = axial + lateral + yaw
        //   frontRightPower = axial - lateral - yaw
        //   backLeftPower   = axial - lateral + yaw
        //   backRightPower  = axial + lateral - yaw
        // Solving the inverse (ignoring yaw, since heading comes from the IMU instead):
        double forwardIn = (flIn + frIn + blIn + brIn) / 4.0;
        double strafeRightIn = (flIn - frIn - blIn + brIn) / 4.0;

        // Trust the IMU for heading - it does not suffer from mecanum wheel-slip error.
        double headingRad = normalize(imu.getHeading() + headingOffsetRad);
        double headingDeltaRad = normalize(headingRad - previousHeadingRad);
        previousHeadingRad = headingRad;

        // Rotate the robot-centric translation into the field frame using the current heading.
        double cosH = Math.cos(headingRad);
        double sinH = Math.sin(headingRad);
        double fieldDx = (forwardIn * cosH) + (strafeRightIn * sinH);
        double fieldDy = (forwardIn * sinH) - (strafeRightIn * cosH);

        pose = new Pose(pose.x() + fieldDx, pose.y() + fieldDy, headingRad);
        velocity = new Velocity(fieldDx / dt, fieldDy / dt, headingDeltaRad / dt);
    }

    @Override
    public MotionState state() {
        return MotionState.ofVelocity(pose, velocity);
    }

    @Override
    public void reset() {
        frontLeft.reset();
        frontRight.reset();
        backLeft.reset();
        backRight.reset();
        imu.resetYaw();
        headingOffsetRad = 0.0;
        previousHeadingRad = pose.heading();
        velocity = Velocity.zero();
        timer.reset();
    }

    private static double normalize(double angleRad) {
        double a = angleRad;
        while (a > Math.PI) {
            a -= 2.0 * Math.PI;
        }
        while (a < -Math.PI) {
            a += 2.0 * Math.PI;
        }
        return a;
    }
}

