package frc.team5115.statemachines;

//import com.cruzsbrian.robolog.Constants;
import frc.team5115.Constants;
import frc.team5115.PID;
import frc.team5115.robot.InputManager;
import frc.team5115.robot.Robot;

import edu.wpi.first.wpilibj.Timer;

public class IntakeManager extends StateMachineBase {
    public static final int STOP = 0;
    public static final int INTAKE = 1;
    public static final int CORRECT = 2;
    public static final int SPIT = 3;
    public static final int GRIP_DOWN = 4;
    public static final int GRIP_UP = 5;
    public static final int STOW_OPEN = 6;
    public static final int STOW_CLOSED = 7;
    public static final int OPEN_DOWN = 8;

    PID turnController;

    public void update() {
        switch (state) {
            case STOP:
                Robot.intake.relax();
                Robot.intake.intake(0);
                Robot.intake.liftIntake();
                break;
            case INTAKE:
                Robot.intake.relax();
                Robot.intake.intake(Constants.INTAKE_SPEED);
                Robot.intake.lowerIntake();
                break;
            case CORRECT:
                Robot.intake.relax();
                Robot.intake.bump();
                Robot.intake.lowerIntake();
                break;
            case SPIT:
                Robot.intake.relax();
                Robot.intake.intake(Constants.SPIT_SPEED);
                break;
            case GRIP_DOWN:
                Robot.intake.grip();
                Robot.intake.intake(0);
                Robot.intake.lowerIntake();
                break;
            case GRIP_UP:
                Robot.intake.grip();
                Robot.intake.intake(0.2);
                Robot.intake.liftIntake();
                break;
            case STOW_OPEN:
                Robot.intake.release();
                Robot.intake.intake(0);
                Robot.intake.liftIntake();
                break;
            case STOW_CLOSED:
                Robot.intake.grip();
                Robot.intake.intake(0);
                Robot.intake.liftIntake();
                break;
            case OPEN_DOWN:
                Robot.intake.lowerIntake();
                Robot.intake.release();
                Robot.intake.intake(Constants.INTAKE_SPEED);
        }
    }

}
