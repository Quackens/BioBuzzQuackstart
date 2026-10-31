package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMUNew;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Axis;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp
@Disabled
public class TurretTest extends OpMode {

    public IMU imu;
    public Turret turret;
    public double velocity = 100.0;

    @Override
    public void init() {
        turret = new Turret(hardwareMap);
        imu = hardwareMap.get(BNO055IMUNew.class, "imu");
        IMU.Parameters p = new IMU.Parameters(
                new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.LEFT)
        );
        imu.initialize(p);
    }

    @Override
    public void loop() {

        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        double v = velocity;

        turret.setHeading(gamepad1.right_stick_x);

        if (gamepad1.dpadUpWasPressed()) {
            velocity += 1.0;
        }
        else if (gamepad1.dpadDownWasPressed()) {
            velocity -= 1.0;
        }

        if (gamepad1.xWasPressed())
            velocity = 500.0;
        else if (gamepad1.yWasPressed())
            velocity = 350.0;

        if (velocity < 0.0)
            velocity = 0.0;

        if (v != velocity) {
            turret.setVelocity(velocity);
        }

        if (gamepad1.aWasPressed()) {
            turret.enable();
        }
        else if (gamepad1.bWasPressed()) {
            turret.disable();
        }

        //dM.setPower(-gamepad1.left_stick_x * 0.4);

        turret.update(orientation.getYaw(AngleUnit.DEGREES));
        turret.debug(telemetry);
        telemetry.addData("velocity", velocity);
    }
}