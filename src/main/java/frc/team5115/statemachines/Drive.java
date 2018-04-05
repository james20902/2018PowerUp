package frc.team5115.statemachines;

import frc.team5115.Konstanten;

import frc.team5115.PID;
import frc.team5115.robot.InputManager;
import frc.team5115.robot.Robot;

public class Drive extends StateMachineBase {

    public static final int INIT = 1;
    public static final int DRIVING = 2;
    public static final int TIPPING = 3;

    public void update() {
        switch (state) {
            case STOP:
                Robot.drivetrain.drive(0, 0);
                break;

            case DRIVING:
                // inuse check is for in case there is another statemachine driving autonomously
                // we didn't need it this year, and in general the drivers don't like it when you take over the drivetrain
                // however, a hybrid mode (say, forward/backward given by drivers, turning given by vision) could be really useful
                // a hybrid mode is probably best implemented as another state in this class
                if (!Robot.drivetrain.inuse) {
                    double forwardSpeed = InputManager.getForward() * InputManager.getThrottle() * Konstanten.TOP_SPEED;
                    double turnSpeed = InputManager.getTurn() * InputManager.getThrottle() * Konstanten.TOP_TURN_SPEED;

                    Robot.drivetrain.drive(forwardSpeed, turnSpeed);

                    // if tipping to far back and accelerating forward too fast, take over to avoid falling
                    if ((Robot.drivetrain.getPitch() < Konstanten.TIP_THRESHOLD) && (Robot.drivetrain.forwarAccel() > Konstanten.ACCEL_THRESHOLD)){
                        Robot.drivetrain.inuse = true;
                        setState(TIPPING);
                    }
                }
                break;

            case TIPPING:
                double power = -(Robot.drivetrain.getPitch() / Konstanten.TIP_THRESHOLD);
                Robot.drivetrain.drive(-power, 0);

                if (Robot.drivetrain.getPitch() > Konstanten.SAFE_ANGLE){
                    Robot.drivetrain.inuse = false;
                    setState(DRIVING);
                }
                break;
        }
    }

}
