package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.Rev9AxisImu;
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
// Carter Was Here
// Kellen Was Here
// GeekInGreen Was Here
    public double heading = 0.0;
    public boolean enabled = false;
    public boolean changed = false;
//    public DcMotorEx hsM;
    public DcMotorEx lsM;
    public double yaw = 0.0;
    public double servoPos = 0.5;
    private double velocity = 150.0;
    public double targetHeading = 90.0;
    public double error = 0.0;

    public double slidePause = 0.5;

    private final Rev9AxisImu turretImu;
    public Servo headingServo;

    public Servo slideServo;

    public Drive driveVariables;
    public DcMotor kicker;
    public DcMotorEx intake;

    private boolean kick = false;

    private final double IntakeForce = -1.0;
    private final double OutakeForce = 0.6;

    private double intakeForce = 0;

    public final double KickForce = 0.8;

    private final double NectarPose = 0.55;
    private final double PollenPose = 0.35;

    private double pollenV = 250.0;
    private double nectarV = 280.0;

    private final double PollenClose = 0.3 ;
    DcMotorControllerEx hsMController;
    DcMotorControllerEx lsMController;

    public Turret(HardwareMap hardwareMap) {
        turretImu = hardwareMap.get(Rev9AxisImu.class, "tIMU");
//        hsM = hardwareMap.get(DcMotorEx.class, "hsM");
        lsM = hardwareMap.get(DcMotorEx.class, "lsM");
        headingServo = hardwareMap.get(Servo.class, "turretR");
        slideServo = hardwareMap.get(Servo.class, "slide");
        kicker = hardwareMap.get(DcMotor.class, "kickM");
        intake = hardwareMap.get(DcMotorEx.class, "iM");

//        hsM.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        hsMController = (DcMotorControllerEx)hsM.getController();
        lsMController = (DcMotorControllerEx)lsM.getController();

        //slideServo.setPosition(0.5);
        slidePause = (PollenClose);

        lsM.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        double heading = turretImu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
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

    public void setPollen(){
        slidePause = PollenPose;
        enabled = true;
        velocity = pollenV;
    }

    public void setNectar(){
        slidePause = NectarPose;
        enabled = true;
        velocity = nectarV;
    }

    public void enableKick(){
        kick = true;
    }

    public void enableIntake(){
        intakeForce = IntakeForce;
    }

    public void enableOutake(){
        intakeForce = OutakeForce;
    }

    public void setClose(){
        slidePause = (PollenClose);
        velocity = 50;
    }

    public void update() {
        if (enabled) {
//            hsM.setVelocity(velocity / 3.0, AngleUnit.DEGREES);
            lsM.setVelocity(velocity, AngleUnit.DEGREES);
        }
        else {
//            hsM.setVelocity(0, AngleUnit.DEGREES);
            lsM.setVelocity(0, AngleUnit.DEGREES);
        }
        /*
        yaw = turretImu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) + 90.0;
        error = targetHeading - yaw;
        servoPos = (1.0 / 180.0) * (error * 0.1);
         */

        servoPos = (1.0 / 180.0) * AngleUnit.normalizeDegrees(targetHeading - heading);

        headingServo.setPosition((servoPos * 2.0 + 1.0) / 2.0);

        slideServo.setPosition(slidePause); // Don't change unless needed

        if (kick){
            kicker.setPower(KickForce);
            kick = false;
        } else {
            kicker.setPower(0);
        }

        intake.setPower(intakeForce);

//        setVelocity(driveVariables.distanceToHive);

        intakeForce = 0.0;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void debug(Telemetry telemetry) {

        //telemetry.addData("Error:", error);
        telemetry.addData("Target Heading:", targetHeading);
        telemetry.addData("Servo Pose", servoPos);
        //telemetry.addData("Yaw:", yaw);
//        telemetry.addData("hsm power", hsM.getPower());
        // telemetry.addData("hsm pct", hsMController.getMotorPower(0) * 100.0);
        // telemetry.addData("hsm mA", hsMController.getMotorCurrent(0, CurrentUnit.MILLIAMPS));
        //telemetry.addData("hsm mode", hsMController.getMotorMode(0));
        //telemetry.addData("hsm mode", lsMController.getMotorZeroPowerBehavior(0));

        telemetry.addData("lsm pct", lsMController.getMotorPower(1) * 100.0);
        telemetry.addData("lsm mA", lsMController.getMotorCurrent(0, CurrentUnit.MILLIAMPS));
        //telemetry.addData("lsm mode", lsMController.getMotorMode(1));
        //telemetry.addData("lsm mode", lsMController.getMotorZeroPowerBehavior(1));

//        telemetry.addData("hsM RPM", (hsM.getVelocity() / 28.0) * 60.0);
        telemetry.addData("lsM RPM", (lsM.getVelocity() / 28.0) * 60.0);
        telemetry.addData("lsM velocity", velocity);



    }
}
