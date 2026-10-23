package org.firstinspires.ftc.teamcode.pedro;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class PathTest extends OpMode {

    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final Pose startPose = poseFactory.of(0, 0, 90);
    private Path testPath() {
        return line(startPose, new Pose(24, 48, 90)).linear(startPose, new Pose(24, 48, 90));
    }

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {
        follower.setPose(startPose);
        //schedule(follow(follower, testPath()));
    }

    @Override
    public void loop() {

        if (gamepad1.aWasPressed()) {
            follower.hold(new Pose(140, 140, 90));
        }

        telemetry.addData("Heading", Math.toDegrees(follower.localizer.pose().heading()));

        follower.update();
        Scheduler.execute();
        telemetry.update();
    }
}
