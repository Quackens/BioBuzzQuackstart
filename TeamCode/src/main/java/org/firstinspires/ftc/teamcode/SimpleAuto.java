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

@Autonomous
public class SimpleAuto extends OpMode {

    private JoinedTelemetry joinedTelemetry;
    private CustomIMU imu;
    private Follower follower;
    private final PoseFactory p = PoseFactory.degrees();
    public Turret turret;
    public double velocity = 200;
    private DcMotorEx intakeMotor;
    private double intakePower = 1.0;
    private DcMotorControllerEx intakeCtl;

    // Poses!
    private final Pose endPose = p.of(0, 6, 0);
    private final Pose controlOne = p.of(72, 48, 45);
    private final Pose controlTwo = p.of(72, 96, 45);
    private final Pose startPose = p.of(0, 0, 0);

    // Paths!
    private Path move;

    @Override
    public void init() {
        //greenLED = hardwareMap.get(LED.class, "front_led_green");
        //redLED = hardwareMap.get(LED.class, "front_led_red");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeCtl = (DcMotorControllerEx)intakeMotor.getController();

        turret = new Turret(hardwareMap);
        move = Paths.line(startPose, endPose).linear(startPose, endPose);

        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        follower.update();
        follower.follow(move);
    }

    @Override
    public void loop() {

        joinedTelemetry.addData("x", follower.localizer.pose().x());
        joinedTelemetry.addData("y", follower.localizer.pose().y());

        joinedTelemetry.update();
        Scheduler.execute();
        follower.update();
    }
}