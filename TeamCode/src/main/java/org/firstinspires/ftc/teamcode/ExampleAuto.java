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
import com.qualcomm.robotcore.hardware.Servo;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
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
    private Servo turretServo;

    // Poses!
    private final Pose endPose = p.of(138, 138, 0);
    private final Pose controlOne = p.of(72, 48, 45);
    private final Pose controlTwo = p.of(72, 96, 45);
    private final Pose startPose = p.of(0, 0, 90);

    // Paths!
    private Path curvePath;

    @Override
    public void init() {

        curvePath = Paths.through(startPose, controlOne, controlTwo, endPose).tangent();

        turretServo = hardwareMap.get(Servo.class, "turret");
        turretServo.setPosition(0);

        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
    }

    @Override
    public void start() {
        //follower.follow(curvePath);
        follower.update();
        savedPose = follower.localizer.pose();
    }

    @Override
    public void loop() {

        //joinedTelemetry.addData("Heading", Math.toDegrees(follower.localizer.pose().heading()));
        joinedTelemetry.addData("x", follower.localizer.pose().x());
        joinedTelemetry.addData("y", follower.localizer.pose().y());
        //joinedTelemetry.addData("A Was Pressed", gamepad1.a);
        //joinedTelemetry.addData("Manual Enabled?", manualEnabled);

        if (gamepad1.yWasPressed()) {
            follower.setPose(new Pose (0, 0, 90));
        }
        if (gamepad1.aWasPressed()) {
            manualEnabled = true;
            follower.update();
        }

        if (gamepad1.rightTriggerWasPressed()) {
            turretServo.setPosition(1);
        }
        else if (gamepad1.leftTriggerWasPressed()) {
            turretServo.setPosition(-1);
        }

        if (gamepad1.rightBumperWasPressed()) {
            manualEnabled = true;
        }

        if (gamepad1.dpadUpWasPressed()) {
            driveMode = DriveMode.Field;
            manualEnabled = true;
        } else if (gamepad1.dpadDownWasPressed()) {
            driveMode = DriveMode.Robot;
            manualEnabled = true;
        }

        if (manualEnabled) {
            if (gamepad1.left_stick_x > 0.01 || gamepad1.left_stick_x < -0.01
                || gamepad1.left_stick_y > 0.01 || gamepad1.left_stick_y < -0.01
                || gamepad1.right_stick_x > 0.01 || gamepad1.right_stick_x < -0.01)
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
            else {
                follower.hold(savedPose);
            }
        }

        joinedTelemetry.addData("Drive Mode", driveMode);
        joinedTelemetry.update();
        Scheduler.execute();
        follower.update();
    }
}