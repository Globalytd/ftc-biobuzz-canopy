package org.firstinspires.ftc.teamcode.gytd.hardware;

import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.MotorType;

import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

@MotorType(
        ticksPerRev = 28,
        gearing = 1,
        maxRPM = 6000,
        orientation = Rotation.CW
)
@DeviceProperties(
        xmlTag = "RevHdHexOneToOneMotor",
        name = "REV HD Hex 1:1 Launcher"
)
public interface RevHdHexOneToOneMotor {
}