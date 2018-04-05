package frc.team5115.statemachines;

import frc.team5115.Konstanten;

import frc.team5115.PID;
import frc.team5115.robot.InputManager;
import frc.team5115.robot.Robot;

public class CarriageManager extends StateMachineBase {

    public static final int STOP = 0;
    public static final int GRAB = 1;
    public static final int DUMP = 2;

    public void update() {
        switch (state) {
            case STOP:
                Robot.carriage.stop();
                break;
            case GRAB:
                Robot.carriage.grab();
                break;
            case DUMP:
                Robot.carriage.eject();
                break;
        }
    }
}
