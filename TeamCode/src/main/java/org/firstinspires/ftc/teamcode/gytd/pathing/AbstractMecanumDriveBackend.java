package org.firstinspires.ftc.teamcode.gytd.pathing;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;

/**
 * Shared mecanum math used by both the legacy drive path and the Pedro seam.
 * @author Daniel Musigire
 * @author Katriel Nakiberu
 */
public abstract class AbstractMecanumDriveBackend implements DriveBackend {
    private final DcMotorEx frontLeft;
    private final DcMotorEx frontRight;
    private final DcMotorEx backLeft;
    private final DcMotorEx backRight;
    private double speedScale = 1.0;

    protected AbstractMecanumDriveBackend(RobotHardware hardware) {
        this.frontLeft = hardware.getFrontLeftDrive();
        this.frontRight = hardware.getFrontRightDrive();
        this.backLeft = hardware.getBackLeftDrive();
        this.backRight = hardware.getBackRightDrive();
    }

    @Override
    public void driveRobotCentric(double axial, double lateral, double yaw) {
        double frontLeftPower = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower = axial - lateral + yaw;
        double backRightPower = axial + lateral - yaw;

        double max = Math.max(
                1.0,
                Math.max(
                        Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                        Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

        frontLeft.setPower((frontLeftPower / max) * speedScale);
        frontRight.setPower((frontRightPower / max) * speedScale);
        backLeft.setPower((backLeftPower / max) * speedScale);
        backRight.setPower((backRightPower / max) * speedScale);
    }

    @Override
    public void driveFieldCentric(double axial, double lateral, double yaw, double headingRadians) {
        // Field-centric to robot-centric coordinate transformation.
        //
        // The driver's input (axial, lateral) is in field-space: "forward" always means
        // toward the far end of the field, regardless of robot heading.
        // Motors need robot-space commands: what the robot must do *relative to its own front*.
        //
        // Solution: rotate the field input by the negative of the robot's heading.
        // This is a 2D rotation matrix applied to the input vector.
        //
        // Math:
        //   robotLateral = lateral * cos(heading) + axial * sin(heading)
        //   robotAxial   = lateral * (-sin(heading)) + axial * cos(heading)
        //
        // Example: if robot heading is 90° (facing left in field):
        //   Driver pulls stick forward (axial=1, lateral=0)
        //   → rotated command tells robot to move left (robot-space)
        //   → robot appears to move forward on the field ✓
        //
        // If heading is 0° (facing field-forward): no rotation, field = robot.
        //
        double cos = Math.cos(-headingRadians);
        double sin = Math.sin(-headingRadians);

        double robotLateral = (lateral * cos) - (axial * sin);
        double robotAxial = (lateral * sin) + (axial * cos);

        driveRobotCentric(robotAxial, robotLateral, yaw);
    }

    @Override
    public void setSpeedScale(double speedScale) {
        this.speedScale = Math.max(0.0, Math.min(1.0, speedScale));
    }

    @Override
    public double getSpeedScale() {
        return speedScale;
    }

    @Override
    public void stop() {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
    }

    @Override
    public String getMotorPowers() {
        return String.format(
                "FL: %.2f FR: %.2f BL: %.2f BR: %.2f",
                frontLeft.getPower(),
                frontRight.getPower(),
                backLeft.getPower(),
                backRight.getPower());
    }

    @Override
    public String getStatus() {
        return "mecanum ready (" + getBackendName() + ")";
    }
}
