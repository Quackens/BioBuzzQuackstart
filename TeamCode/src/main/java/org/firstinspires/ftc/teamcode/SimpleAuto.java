package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
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

import static com.pedropathing.follower.Follower.Mode.FOLLOW;
import static com.pedropathing.follower.Follower.Mode.IDLE;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name="Use this Drive Coach. Please! Otherwise it's not my fault, it's yours.", preselectTeleOp = "Drive")
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
    public static double xPos;
    public static double yPos;
    public static double robotHeading;

    // Poses!
    private final Pose startPose = p.of(58.75, 8.5, 90);
    private final Pose shootPose = p.of(58.75, 10, 90);
    private final Pose controlPose = p.of(15, 15, 90);
    private final Pose endPose = p.of(10, 95, 90);

    // Paths!
    private Path movePath;

    public void updatePos() {
        robotHeading = follower.localizer.pose().heading();
        xPos = follower.localizer.pose().x();
        yPos = follower.localizer.pose().y();
    }

    @Override
    public void init() {
//        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
//        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//        intakeCtl = (DcMotorControllerEx)intakeMotor.getController();
//
//        turret = new Turret(hardwareMap);
        movePath = Paths.curve(startPose, shootPose, controlPose, endPose).tangent();

        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        follower.update();
        follower.follow(movePath);
    }

    @Override
    public void loop() {

        joinedTelemetry.addData("x", follower.localizer.pose().x());
        joinedTelemetry.addData("y", follower.localizer.pose().y());
        joinedTelemetry.addData("Heading", follower.localizer.pose().heading());

        joinedTelemetry.update();
        Scheduler.execute();
        follower.update();
        updatePos();
    }
}