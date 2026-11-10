package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.revhub.localizers.CustomIMU;
import com.qualcomm.hardware.rev.Rev9AxisImu;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class ImuTurretTest extends OpMode {
    private JoinedTelemetry joinedTelemetry;
    private Follower follower;
    public Turret turret;
    public double velocity = 200;
    public double yes = 0.2;

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
        turret.setHeading(yes * 3.2);

        if (gamepad1.rightTriggerWasPressed()) {
            turret.enable();
        }
        else if (gamepad1.leftTriggerWasPressed()) {
            turret.disable();
        }

        if (gamepad1.dpadUpWasPressed()) {
            yes += 0.1;
        }
        else if (gamepad1.dpadDownWasPressed()) {
            yes -= 0.1;
        }

        joinedTelemetry.addData("Turret Heading", turret.heading);
        joinedTelemetry.addData("Yes", yes);
        joinedTelemetry.update();
        follower.update();
        turret.update();
    }
}
