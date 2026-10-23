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
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        c.xPodOffset.set(-5.2550320362481555);
        c.yPodOffset.set(-6.496516550619771);  // sign flipped by hand: tuner gave +6.4965, but the strafe pod is measured ~6.5 in BEHIND center
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);  // flipped by hand: Pose Test read y = -32 for a push LEFT (tuner said FORWARD)
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    // From AutoTune Foresight Tuner, 2026-09-25. Conditions: battery 12.46 V; unused third swingarm pod still mounted (drag).
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(1.1998508715688647);
                Controller secondaryTranslationalForward = Controller.proportional(0.44331283643893854);
                Controller primaryTranslationalLateral = Controller.proportional(1.9049721514539277);
                Controller secondaryTranslationalLateral = Controller.proportional(0.7038363081688676);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018368115417001965));
                c.brake.set(Controller.proportionalFeedforward(0.01561289810445167));

                c.headingFeedback.set(Controller.proportional(4.596844429638475));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04218781000101513, 0.01613080633335909));

                c.linearBrakeCoefficients.set(Matrix.diag(0.058416982797864364, 0.11570183144397427));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0033602010087923338, 0.0010356649240153));

                c.maxAchievableForwardVelocity.set(50.43126216359433);
                c.maxAchievableStrafeVelocity.set(35.36683020082209);
                c.naturalForwardDeceleration.set(33.586018221594536);
                c.naturalStrafeDeceleration.set(44.77963675001058);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(new PinpointLocalizer(h, localizerConfig), new Mecanum(h, drivetrainConfig), new Foresight(foresightConfig));
    }
}