package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.revhub.localizers.CustomIMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import static com.pedropathing.follower.Follower.Mode.FOLLOW;
import static com.pedropathing.follower.Follower.Mode.IDLE;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class Drive extends OpMode {

    enum DriveMode {
        Robot, Field
    }

    enum HoldState {
        NoHold,
        HoldWait,
        Holding
    }

    HoldState holdState = HoldState.NoHold;
    long holdStart = 0;
    private DriveMode driveMode = DriveMode.Robot;
    private Pose savedPose;
    private boolean manualEnabled = true;
    private double savedMaxPositionCorrectionIn;
    private double savedMaxHeadingCorrectionRad;
    private JoinedTelemetry joinedTelemetry;
    private CustomIMU imu;
    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    public Turret turret;
    public double velocity = 200;
    private DcMotorEx intakeMotor;
    private double intakePower = 1.0;
    private DcMotorControllerEx intakeCtl;
    private Servo slideServo;
    LED redLED;
    LED greenLED;

    GoBildaPrismDriver prism;

    private double Pollen = 0.0;
    private double Nectar = 1.0;

    // Poses!
    private final Pose endPose = p.of(138, 138, 0);
    private final Pose controlOne = p.of(72, 48, 45);
    private final Pose controlTwo = p.of(72, 96, 45);
    private final Pose startPose = p.of(SimpleAuto.xPos, SimpleAuto.yPos, SimpleAuto.robotHeading);

    public int counter = 0;

    // Paths!
    private Path curvePath;

    PrismAnimations.Solid solid;
    // PrismAnimations.RainbowSnakes rainbowSnakes = new PrismAnimations.RainbowSnakes();


    @Override
    public void init() {

        // LED Strip Set
        prism = hardwareMap.get(GoBildaPrismDriver.class,"prism");
        prism.clearAllAnimations();

        if (gamepad1.b){
            solid = new PrismAnimations.Solid(Color.RED);
        }
        else {
            solid = new PrismAnimations.Solid(Color.BLUE);
        }
        solid.setBrightness(50);
        solid.setStartIndex(0);
        solid.setStopIndex(23);

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, solid);
        // prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1,rainbowSnakes);

        //greenLED = hardwareMap.get(LED.class, "front_led_green");
        //redLED = hardwareMap.get(LED.class, "front_led_red");
        // intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        // intakeCtl = (DcMotorControllerEx)intakeMotor.getController();


        // slideServo = hardwareMap.get(Servo.class, "slide");

        turret = new Turret(hardwareMap);
        // curvePath = Paths.through(startPose, controlOne, controlTwo, endPose).tangent();

        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);

        if (gamepad1.dpadUpWasPressed()) {
            driveMode = DriveMode.Field;
            manualEnabled = true;
        } else if (gamepad1.dpadDownWasPressed()) {
            driveMode = DriveMode.Robot;
            manualEnabled = true;
        }
    }

    @Override
    public void start() {
        follower.update();
        savedPose = follower.localizer.pose();

        // turret.enable();
        // intakeMotor.setPower(0.85);

        // slideServo.setPosition(0.0);
    }

    @Override
    public void loop() {

        //joinedTelemetry.addData("Heading", Math.toDegrees(follower.localizer.pose().heading()));
        joinedTelemetry.addData("x", follower.localizer.pose().x());
        joinedTelemetry.addData("y", follower.localizer.pose().y());
        //joinedTelemetry.addData("A Was Pressed", gamepad1.a);
        //joinedTelemetry.addData("Manual Enabled?", manualEnabled);

//        if (gamepad1.yWasPressed()) {
//            follower.setPose(new Pose (0, 0, 90));
//        }
//        if (gamepad1.aWasPressed()) {
//            manualEnabled = true;
//            follower.update();
//        }


        if (gamepad2.right_trigger_pressed) {
            // turret.enable();
            turret.enableKick();
        }
        if (gamepad2.left_trigger_pressed) {
            // turret.disable();
            turret.enableIntake();
        }

        if (gamepad2.right_bumper) {
            // intakePower = 1.0;
            // intakeMotor.setPower(intakePower);
        }
        if (gamepad2.left_bumper) {
            // intakePower = -1.0;
            // intakeMotor.setPower(intakePower);
            turret.enableOutake();
        }

        if (gamepad2.yWasPressed()) {
            // pollen
            turret.setPollen();
        }
        else if (gamepad2.xWasPressed()) {
            // nectar
            turret.setNectar();
        }

        if (gamepad2.bWasPressed()){
            // Close and slow down
            turret.setClose();
        }

        if (gamepad1.aWasPressed() || gamepad2.aWasPressed()){
            // Align turret with robot "RESET"
            turret.targetHeading = Math.toDegrees(follower.localizer.pose().heading()) + 90;
        }

        turret.targetHeading += gamepad2.right_stick_x * 4.0;
        turret.targetHeading = AngleUnit.normalizeDegrees(turret.targetHeading);

//        if (gamepad1.bWasPressed()) {
//            follower.follow(curvePath);
//            follower.update();
//        }

        if (gamepad1.rightTriggerWasPressed()) {

        }
        else if (gamepad1.leftTriggerWasPressed()) {

        }

        if (gamepad2.dpadUpWasPressed()) {
            velocity += 10.0;
            turret.setVelocity(velocity);
        }
        else if (gamepad2.dpadDownWasPressed()) {
            velocity -= 10.0;
            turret.setVelocity(velocity);
        }



        if (follower.mode() == FOLLOW) {
            //redLED.on();
            //greenLED.on();
        }
        else if (follower.mode() == IDLE) {
            //redLED.off();
            //greenLED.off();
        }

        if (manualEnabled) {
            if (driveMode == DriveMode.Robot) {
                follower.manual(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x,
                        -gamepad1.right_stick_x
                );
            }
            else {
                DrivePowers powers = ManualDrive.fieldCentric(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x,
                        -gamepad1.right_stick_x,
                        follower.pose().heading()
                );
                follower.manual(powers);
            }

            if (gamepad1.left_stick_x > 0.01 || gamepad1.left_stick_x < -0.01
                || gamepad1.left_stick_y > 0.01 || gamepad1.left_stick_y < -0.01
                || gamepad1.right_stick_x > 0.01 || gamepad1.right_stick_x < -0.01)
            {
                holdState = HoldState.NoHold;
            }
            else {
                // When there is no controller input, wait 250ms before saving the position, then
                // begin holding position.
                switch (holdState) {
                    case NoHold:
                        holdStart = System.currentTimeMillis();
                        holdState = HoldState.HoldWait;
                        break;
                    case HoldWait:
                        if (System.currentTimeMillis() - holdStart > 250) {
                            savedPose = follower.localizer.pose();
                            holdState = HoldState.Holding;
                        }
                        break;
                    case Holding:
                        follower.hold(savedPose);
                        break;
                }
            }
            telemetry.addData("HoldState", holdState);
        }



        // Adjustments to the slide on turret

        /*
        if (gamepad2.dpadUpWasPressed()) {

            turret.slidePause += 0.05;
            counter += 1;


        } else if (gamepad2.dpadDownWasPressed()) {

            turret.slidePause -= 0.05;
            counter -= 1;

        }
        */


        telemetry.addData("Counter", counter);

        // telemetry.addData("intake mA", intakeCtl.getMotorCurrent(2, CurrentUnit.MILLIAMPS));
        turret.setHeading(Math.toDegrees(follower.localizer.pose().heading()));
        // double heading = turretImu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        joinedTelemetry.addData("Heading", Math.toDegrees(follower.localizer.pose().heading()));
        turret.update();
        turret.debug(telemetry);
        joinedTelemetry.addData("Drive Mode", driveMode);
        joinedTelemetry.update();
        Scheduler.execute();
        follower.update();
    }
}