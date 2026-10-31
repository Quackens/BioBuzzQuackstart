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

import static com.pedropathing.follower.Follower.Mode.FOLLOW;
import static com.pedropathing.follower.Follower.Mode.IDLE;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class ExampleAuto extends OpMode {

    enum DriveMode {
        Robot, Field
    }
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

    private double Polin = 0.0;
    private double Nectar = 1.0;

    // Poses!
    private final Pose endPose = p.of(138, 138, 0);
    private final Pose controlOne = p.of(72, 48, 45);
    private final Pose controlTwo = p.of(72, 96, 45);
    private final Pose startPose = p.of(0, 0, 90);

    // Paths!
    private Path curvePath;

    @Override
    public void init() {
        //greenLED = hardwareMap.get(LED.class, "front_led_green");
        //redLED = hardwareMap.get(LED.class, "front_led_red");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeCtl = (DcMotorControllerEx)intakeMotor.getController();


        slideServo = hardwareMap.get(Servo.class, "slide");

        turret = new Turret(hardwareMap);
        curvePath = Paths.through(startPose, controlOne, controlTwo, endPose).tangent();

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

        turret.enable();
        intakeMotor.setPower(0.85);

        slideServo.setPosition(0.0);
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

        if (gamepad2.rightTriggerWasPressed()) {
            turret.enable();
        }
        else if (gamepad2.leftTriggerWasPressed()) {
            turret.disable();
        }

        if (gamepad2.rightBumperWasPressed()) {
            intakePower = 1.0;
            intakeMotor.setPower(intakePower);
        }
        else if (gamepad2.leftBumperWasPressed()) {
            intakePower = -0.2;
            intakeMotor.setPower(intakePower);
        }

        if (gamepad2.yWasPressed()) {
            // polin
            //slideServo.setPosition(Polin);
        }
        else if (gamepad2.xWasPressed()) {
            // nectar
            //slideServo.setPosition(Nectar);
        }

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
//            if (gamepad1.left_stick_x > 0.01 || gamepad1.left_stick_x < -0.01
//                || gamepad1.left_stick_y > 0.01 || gamepad1.left_stick_y < -0.01
//                || gamepad1.right_stick_x > 0.01 || gamepad1.right_stick_x < -0.01)
            {
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
                savedPose = follower.localizer.pose();

            }
//            else {
//                //follower.hold(savedPose);
//            }
        }
        telemetry.addData("intake mA", intakeCtl.getMotorCurrent(2, CurrentUnit.MILLIAMPS));
        turret.setHeading(gamepad2.right_stick_x);
        turret.update(0);
        turret.debug(telemetry);
        joinedTelemetry.addData("Drive Mode", driveMode);
        joinedTelemetry.update();
        Scheduler.execute();
        follower.update();
    }
}