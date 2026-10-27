package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class IntakeTest extends OpMode {
    public DcMotorEx intakeMotor;

    public CRServo ir;
    public CRServo il;

    @Override
    public void init() {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        ir = hardwareMap.get(CRServo.class, "ir");
        il = hardwareMap.get(CRServo.class, "il");
        //intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        ir.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        intakeMotor.setPower(gamepad1.left_stick_x);

        if (gamepad1.a) {
            ir.setPower(1.0);
            il.setPower(1.0);
        }
        else {
            ir.setPower(0.0);
            il.setPower(0.0);
        }
    }
}