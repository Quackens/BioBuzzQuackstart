package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class Carter_test extends OpMode {

    DcMotorEx[] motors = new DcMotorEx[4];
    int [] turnMatrix = {
            1, -1,
            1, -1,
    };

    int [] strafeMatrix = {
            1, -1,
            -1, 1,
    };

    public void init(){

        motors[0] = hardwareMap.get(DcMotorEx.class, "fl");
        motors[1] = hardwareMap.get(DcMotorEx.class, "fr");
        motors[2] = hardwareMap.get(DcMotorEx.class, "bl");
        motors[3] = hardwareMap.get(DcMotorEx.class, "br");

        motors[0].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[2].setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void start(){

    }

    public void loop(){

        for(int i = 0; i < 4; ++i){

            double p = -gamepad1.left_stick_y;
            p += gamepad1.right_stick_x * turnMatrix[i];
            p += gamepad1.left_stick_x * strafeMatrix[i];

            motors[i].setPower(p);
        }
    }
}
