package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/*
 * Robot configuration (Pedro Pathing 3.0.1). All three tuners complete as of 2026-09-25.
 *
 * ConfigVar validation is LAZY: a required value that is never set does not fail here,
 * it throws IllegalStateException("Config variable has not been set") the first time
 * something reads it on the robot.
 *
 * Fill in the TODOs by pasting the Java code the tuners print:
 *   Mecanum Tuner   -> motor directions
 *   Pinpoint Tuner  -> pod offsets, pod directions, pod type
 *   Foresight Tuner -> the whole ForesightConfig (stage 2; not declared yet)
 */
public class Constants {
    // From AutoTune Mecanum Tuner, 2026-09-25.
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("fl");
        c.frontRightName.set("fr");
        c.backLeftName.set("bl");
        c.backRightName.set("br");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });

    // From AutoTune Pinpoint Tuner, 2026-09-25.
    // goBILDA convention: xPodOffset = sideways distance of the forward pod from the tracking center (left +, right -);
    // yPodOffset = forward distance of the strafe pod from the tracking center (forward +, back -).
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("odo");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-5.377155814583846);
        c.yPodOffset.set(1.1819974643977609);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    // From AutoTune Foresight Tuner, 2026-09-25. Conditions: battery 12.46 V; unused third swingarm pod still mounted (drag).
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.2281848926505584);
                Controller secondaryTranslationalForward = Controller.proportional(0.08430822062175573);
                Controller primaryTranslationalLateral = Controller.proportional(0.2528138955936328);
                Controller secondaryTranslationalLateral = Controller.proportional(0.09340797911014273);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.012713583370992878));
                c.brake.set(Controller.proportionalFeedforward(0.010806545865343945));

                c.headingFeedback.set(Controller.proportional(3.1336515053164176));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.046446246722901645, 0.005883933672762462));

                c.linearBrakeCoefficients.set(Matrix.diag(0.06806511001114263, 0.022457154166842023));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.001094671753520018, 0.0020515459492595903));

                c.maxAchievableForwardVelocity.set(69.8841320969751);
                c.maxAchievableStrafeVelocity.set(59.56914817100791);
                c.naturalForwardDeceleration.set(47.911636349829884);
                c.naturalStrafeDeceleration.set(68.58485553215003);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(new PinpointLocalizer(h, localizerConfig), new Mecanum(h, drivetrainConfig), new Foresight(foresightConfig));
    }
}