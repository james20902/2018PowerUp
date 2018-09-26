package frc.team5115.auto;

import frc.team5115.Constants;
import frc.team5115.PID;
import frc.team5115.robot.Robot;
import frc.team5115.statemachines.StateMachineBase;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LinePlusTurn extends StateMachineBase {

    public static final int DRIVING = 1;
    public static final int FINISHED = 2;

    boolean line;

    double targetDist;
    double targetAngle;

    double startDist;
    double startAngle;
    double distToTurn;
    double turnAngle;

    PID forwardController;
    PID turnController;

    public void start(double dist, double angle, double distToTurn, double maxForwardSpeed, double maxTurnSpeed) {
        line = true;

        targetDist = Robot.drivetrain.distanceTraveled() + dist;
        targetAngle = Robot.drivetrain.getYaw();

        turnAngle = angle;
        this.distToTurn = distToTurn;
        startDist = Robot.drivetrain.distanceTraveled();
        startAngle = Robot.drivetrain.getYaw();

        //Change back to our constants, this one doesn't work
        forwardController = new PID(Constants.AUTO_FORWARD_KP, Constants.AUTO_FORWARD_KI, Constants.AUTO_FORWARD_KD ,maxForwardSpeed);
        turnController = new PID(Constants.TURN_KP, Constants.TURN_KI ,Constants.AUTO_TURN_KD, maxTurnSpeed);
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

                if (Robot.drivetrain.distanceTraveled() - startDist >= distToTurn) {
                    targetAngle = startAngle + turnAngle;
                }

                Robot.drivetrain.drive(vForward, vTurn);

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
