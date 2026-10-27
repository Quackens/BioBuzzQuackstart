package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Turret {

    public double heading = 0.0;
    public boolean enabled = false;
    public boolean changed = false;
    public DcMotorEx hsM;
    public DcMotorEx lsM;

    private double velocity = 100.0;

    public Servo headingServo;
    DcMotorControllerEx hsMController;
    DcMotorControllerEx lsMController;

    public Turret(HardwareMap hardwareMap) {
        hsM = hardwareMap.get(DcMotorEx.class, "hsM");
        lsM = hardwareMap.get(DcMotorEx.class, "lsM");
        headingServo = hardwareMap.get(Servo.class, "headingServo");

        hsM.setDirection(DcMotorSimple.Direction.REVERSE);

        hsMController = (DcMotorControllerEx)hsM.getController();
        lsMController = (DcMotorControllerEx)lsM.getController();

        lsM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setVelocity(double v) {
        changed = true;
        velocity = v;
    }

    public void enable() {
        changed = true;
        enabled = true;
    }

    public void disable() {
        changed = true;
        enabled = false;
    }

    public void update(double robotHeading) {
        if (enabled) {
            hsM.setVelocity(velocity / 3.0, AngleUnit.DEGREES);
            lsM.setVelocity(velocity, AngleUnit.DEGREES);
        }
        else {
            hsM.setVelocity(0, AngleUnit.DEGREES);
            lsM.setVelocity(0, AngleUnit.DEGREES);
        }
        headingServo.setPosition(0.0);
    }

    public void setHeading(double heading) {
        this.heading = heading;

    }

    public void debug(Telemetry telemetry) {
        telemetry.addData("hsm power", hsM.getPower());
        telemetry.addData("hsm pct", hsMController.getMotorPower(0) * 100.0);
        telemetry.addData("hsm mA", hsMController.getMotorCurrent(0, CurrentUnit.MILLIAMPS));
        //telemetry.addData("hsm mode", hsMController.getMotorMode(0));
        //telemetry.addData("hsm mode", lsMController.getMotorZeroPowerBehavior(0));

        telemetry.addData("lsm pct", lsMController.getMotorPower(1) * 100.0);
        telemetry.addData("lsm mA", lsMController.getMotorCurrent(1, CurrentUnit.MILLIAMPS));
        //telemetry.addData("lsm mode", lsMController.getMotorMode(1));
        //telemetry.addData("lsm mode", lsMController.getMotorZeroPowerBehavior(1));

        telemetry.addData("hsM RPM", (hsM.getVelocity() / 28.0) * 60.0);
        telemetry.addData("lsM RPM", (lsM.getVelocity() / 28.0) * 60.0);
    }
}
