package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class ImuTurretTest extends OpMode {
    private JoinedTelemetry joinedTelemetry;
    private Follower follower;
    public Turret turret;

    @Override
    public void init() {
        turret = new Turret(hardwareMap);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {
        follower.update();
        turret.enable();
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) {
            turret.servoPos += 0.05;
        } else if (gamepad1.dpadDownWasPressed()) {
            turret.servoPos -= 0.05;
        }

        if (gamepad1.rightTriggerWasPressed()) {
            turret.enable();
        } else if (gamepad1.leftTriggerWasPressed()) {
            turret.disable();
        }

        joinedTelemetry.addData("Error", turret.error);
        joinedTelemetry.addData("IMU yaw", turret.yaw);
        joinedTelemetry.addData("Servo pos", turret.servoPos);
        joinedTelemetry.update();
        follower.update();
        turret.update();
    }
}