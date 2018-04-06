package frc.team5115.auto;

import frc.team5115.Constants;
import frc.team5115.PID;
import frc.team5115.robot.Robot;
import frc.team5115.statemachines.StateMachineBase;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDrive extends StateMachineBase {

    public static final int DRIVING = 1;
    public static final int FINISHED = 2;

    // used to determine if we're running a line or a turn
    // only comes into play in situations where you need to single out a specific controller
    boolean line;

    double targetDist;
    double targetAngle;

    PID forwardController;
    PID turnController;

    public void startLine(double dist, double maxSpeed) {
        line = true;
        //Takes some time to reset gyros + encoders
        Timer.delay(0.1);
        targetDist = Robot.drivetrain.distanceTraveled() + dist;
        targetAngle = Robot.drivetrain.getYaw();

        forwardController = new PID(Constants.AUTO_FORWARD_KP, Constants.AUTO_FORWARD_KI, Constants.AUTO_FORWARD_KD ,maxSpeed);
        turnController = new PID(Constants.AUTO_TURN_KP, Constants.AUTO_TURN_KI ,Constants.AUTO_TURN_KD);

        setState(DRIVING);
    }

    public void startTurn(double angle, double maxSpeed) {
        line = false;
        targetDist = Robot.drivetrain.distanceTraveled();
        targetAngle = Robot.drivetrain.getYaw() + (angle);

        forwardController = new PID(Constants.AUTO_FORWARD_KP, Constants.AUTO_FORWARD_KI, Constants.AUTO_FORWARD_KD);
        turnController = new PID(Constants.TURN_KP, Constants.TURN_KI ,Constants.AUTO_TURN_KD, maxSpeed);

        setState(DRIVING);
    }

    public void update() {
        SmartDashboard.putNumber("autodrive state: ", state);
        System.out.println("autodrive target: " + targetDist);
        switch (state) {
            case DRIVING:
                Robot.drivetrain.inuse = true;

                // run every Constants.getAsDouble()DELAY seconds while driving
                double vForward = forwardController.getPID(targetDist, Robot.drivetrain.distanceTraveled(), Robot.drivetrain.averageSpeed());

                double clearYaw = clearSteer(Robot.drivetrain.getYaw(), targetAngle);
                double vTurn = turnController.getPID(targetAngle, clearYaw, Robot.drivetrain.getTurnVelocity());

                if (!line && Math.abs(turnController.getError()) > 4 * Constants.TURN_TOLERANCE) {
                    vTurn += 0.15 * Math.signum(vTurn);
                }

                // tell the drivetrain to do the stuff
                Robot.drivetrain.drive(vForward, vTurn);

                // if both controllers are finished, finish
                if (forwardController.isFinished(Constants.FORWARD_TOLERANCE, Constants.FORWARD_DTOLERANCE) && turnController.isFinished(Constants.TURN_TOLERANCE, Constants.TURN_DTOLERANCE)) {
                    Robot.drivetrain.drive(0, 0);
                    setState(FINISHED);
                }

                break;
            case FINISHED:
                Robot.drivetrain.inuse = false;
                Robot.drivetrain.drive(0, 0);

                forwardController = null;
                turnController = null;
                break;
        }
    }

    private double clearSteer(double yaw, double target) {
        if (Math.abs(target - yaw) > 180) {
            if (target < 180) {
                yaw -= 360;
            } else {
                yaw += 360;
            }
        }

        return yaw;
    }

}
