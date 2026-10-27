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
    public DcMotorEx hsM;
    public DcMotorEx lsM;
    public DcMotorEx dM;

    private double velocity = 100.0;

    boolean hsmEnabled = false;
    boolean lsmEnabled = false;

    DcMotorControllerEx hsMController;
    DcMotorControllerEx lsMController;

    @Override
    public void init() {
        hsM = hardwareMap.get(DcMotorEx.class, "hsM");
        lsM = hardwareMap.get(DcMotorEx.class, "lsM");
        dM = hardwareMap.get(DcMotorEx.class, "dM");

        hsM.setDirection(DcMotorSimple.Direction.REVERSE);

        hsMController = (DcMotorControllerEx)hsM.getController();
        lsMController = (DcMotorControllerEx)lsM.getController();

        lsM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        dM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        dM.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed())
            velocity += 1.0;
        else if (gamepad1.dpadDownWasPressed())
            velocity -= 1.0;

        if (gamepad1.xWasPressed())
            velocity = 500.0;
        else if (gamepad1.yWasPressed())
            velocity = 350.0;

        if (velocity < 0.0)
            velocity = 0.0;

        if (gamepad1.aWasPressed()) hsmEnabled = !hsmEnabled;
        if (gamepad1.bWasPressed()) lsmEnabled = !lsmEnabled;

        if (hsmEnabled)
            hsM.setVelocity(velocity / 3.0, AngleUnit.DEGREES);
        else
            hsM.setVelocity(0.0);

        if (lsmEnabled)
            lsM.setVelocity(velocity, AngleUnit.DEGREES);
        else
            lsM.setVelocity(0.0);

        dM.setPower(-gamepad1.left_stick_x * 0.4);

        telemetry.addData("hsm power", hsM.getPower());
        telemetry.addData("hsm pct", hsMController.getMotorPower(0) * 100.0);
        telemetry.addData("hsm mA", hsMController.getMotorCurrent(0, CurrentUnit.MILLIAMPS));
        telemetry.addData("hsm mode", hsMController.getMotorMode(0));
        telemetry.addData("hsm mode", lsMController.getMotorZeroPowerBehavior(0));

        telemetry.addData("lsm pct", lsMController.getMotorPower(1) * 100.0);
        telemetry.addData("lsm mA", lsMController.getMotorCurrent(1, CurrentUnit.MILLIAMPS));
        telemetry.addData("lsm mode", lsMController.getMotorMode(1));
        telemetry.addData("lsm mode", lsMController.getMotorZeroPowerBehavior(1));

        telemetry.addData("hsm", hsmEnabled);
        telemetry.addData("lsm", lsmEnabled);
        telemetry.addData("velocity", velocity);
    }

}