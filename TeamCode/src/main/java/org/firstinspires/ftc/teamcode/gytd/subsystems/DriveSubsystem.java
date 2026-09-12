package org.firstinspires.ftc.teamcode.gytd.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.gytd.hardware.RobotHardware;

public class DriveSubsystem {
    private final RobotHardware hardware;
    private final DcMotorEx frontLeft;
    private final DcMotorEx frontRight;
    private final DcMotorEx backLeft;
    private final DcMotorEx backRight;
    private double speedScale = 1.0;

    public DriveSubsystem(RobotHardware hardware) {
        this.hardware = hardware;
        this.frontLeft = hardware.getFrontLeftDrive();
        this.frontRight = hardware.getFrontRightDrive();
        this.backLeft = hardware.getBackLeftDrive();
        this.backRight = hardware.getBackRightDrive();
    }

    public void clearCache() {
        hardware.clearBulkCache();
    }

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

    public void driveFieldCentric(double axial, double lateral, double yaw, double headingRadians) {
        double cos = Math.cos(-headingRadians);
        double sin = Math.sin(-headingRadians);

        double robotLateral = (lateral * cos) - (axial * sin);
        double robotAxial = (lateral * sin) + (axial * cos);

        driveRobotCentric(robotAxial, robotLateral, yaw);
    }

    public void setSpeedScale(double speedScale) {
        this.speedScale = Math.max(0.0, Math.min(1.0, speedScale));
    }

    public double getSpeedScale() {
        return speedScale;
    }

    public void stop() {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
    }

    public String getMotorPowers() {
        return String.format(
                "FL: %.2f FR: %.2f BL: %.2f BR: %.2f",
                frontLeft.getPower(),
                frontRight.getPower(),
                backLeft.getPower(),
                backRight.getPower());
    }

    public String getStatus() {
        return "mecanum ready";
    }
}

