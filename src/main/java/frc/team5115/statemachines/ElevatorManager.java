package frc.team5115.statemachines;

import frc.team5115.Constants;
import frc.team5115.PID;
import frc.team5115.robot.InputManager;
import frc.team5115.robot.Robot;

public class ElevatorManager extends StateMachineBase {

    public static final int STOP = 0;
    public static final int MOVING_TO = 1;

    PID movement;
    double targetAngle;
    double dAngle;
    double angle;
    double output;

    public ElevatorManager(){
        movement = new PID( Constants.ARM_KP,	//readability!
                Constants.ARM_KI,	//because this line was too thicc
                Constants.ARM_KD,
                Constants.ELEVATOR_SPEED_SCALE);
    }

    public void setTarget(double target) {
        if (target > Constants.ELEVATOR_MAX) {
            targetAngle = Constants.ELEVATOR_MAX;
        } else if (target < Constants.ELEVATOR_MIN) {
            targetAngle = Constants.ELEVATOR_MIN;
        } else {
            targetAngle = target;
        }
    }

    public void update() {
        angle = Robot.elevator.getAngle();
        dAngle = Robot.elevator.getAngleSpeed();
        switch (state) {
            case STOP:
                //Stops the elevator
                Robot.elevator.move(0);
                break;

            case MOVING_TO:
                //Elevator moves to either switch or scale height
                output = movement.getPID(targetAngle, angle, dAngle);
                Robot.elevator.move(output);
                break;
        }
    }


}
