package org.firstinspires.ftc.teamcode.gytd.hardware;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.List;

/**
 * Central hardware container for the GYTD robot.
 * Holds references to all hardware devices and manages their lifecycle.
 */
public class RobotHardware {
    private final HardwareMap hardwareMap;
    private DcMotorEx frontLeftDrive;
    private DcMotorEx frontRightDrive;
    private DcMotorEx backLeftDrive;
    private DcMotorEx backRightDrive;
    private IMU imu;
    private List<LynxModule> lynxModules;

    public RobotHardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        init();
    }

    private void init() {
        frontLeftDrive = hardwareMap.get(DcMotorEx.class, HardwareConstants.FRONT_LEFT_DRIVE);
        frontRightDrive = hardwareMap.get(DcMotorEx.class, HardwareConstants.FRONT_RIGHT_DRIVE);
        backLeftDrive = hardwareMap.get(DcMotorEx.class, HardwareConstants.BACK_LEFT_DRIVE);
        backRightDrive = hardwareMap.get(DcMotorEx.class, HardwareConstants.BACK_RIGHT_DRIVE);

        // Positive axial input should move the robot forward.
        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(IMU.class, HardwareConstants.IMU_NAME);
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);
        imu.resetYaw();

        lynxModules = hardwareMap.getAll(LynxModule.class);
        for (LynxModule module : lynxModules) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    public HardwareMap getHardwareMap() {
        return hardwareMap;
    }

    public DcMotorEx getFrontLeftDrive() {
        return frontLeftDrive;
    }

    public DcMotorEx getFrontRightDrive() {
        return frontRightDrive;
    }

    public DcMotorEx getBackLeftDrive() {
        return backLeftDrive;
    }

    public DcMotorEx getBackRightDrive() {
        return backRightDrive;
    }

    public double getHeadingRadians() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    public void clearBulkCache() {
        for (LynxModule module : lynxModules) {
            if (module.getBulkCachingMode() == LynxModule.BulkCachingMode.MANUAL) {
                module.clearBulkCache();
            }
        }
    }

    public void shutdown() {
        frontLeftDrive.setPower(0.0);
        frontRightDrive.setPower(0.0);
        backLeftDrive.setPower(0.0);
        backRightDrive.setPower(0.0);
    }
}

