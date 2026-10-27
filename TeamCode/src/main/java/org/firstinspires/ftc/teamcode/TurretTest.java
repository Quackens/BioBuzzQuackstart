package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@TeleOp
public class TurretTest extends OpMode {

    public Turret turret;
    public double velocity = 100.0;

    @Override
    public void init() {
        turret = new Turret(hardwareMap);
    }

    @Override
    public void loop() {
        double v = velocity;

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

        turret.update();
        turret.debug(telemetry);
        telemetry.addData("velocity", velocity);
    }
}