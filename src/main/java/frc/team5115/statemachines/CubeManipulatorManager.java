package frc.team5115.statemachines;

import frc.team5115.Konstanten;
import frc.team5115.robot.InputManager;
import frc.team5115.robot.Robot;

import edu.wpi.first.wpilibj.Timer;

public class CubeManipulatorManager extends StateMachineBase {

    public static final int INTAKE = 1;
    public static final int LOWER_INTAKE = 2;
    public static final int PASS_TO_INTAKE = 3;
    public static final int PASS_TO_ARM = 4;
    public static final int TRANSIT = 5;
    public static final int DRIVIN_AROUND_WIT_DA_INTAKE_DOWN = 6;
    public static final int EMPTY = 7;

    public double armGoal = Robot.elevator.getAngle();
    private double time;

    protected void updateChildren() {
        Robot.IM.update();
        Robot.EM.update();
        Robot.CM.update();
    }

    public void collisionAvoidance() {
        if (Robot.elevator.minHeight() && !Robot.elevator.movingArm) {
            Robot.IM.setState(IntakeManager.GRIP_UP);
        } else if (Robot.elevator.getAngle() <= Konstanten.INTAKE_HEIGHT) {
            Robot.IM.setState(IntakeManager.STOW_OPEN);
        } else {
            Robot.IM.setState(IntakeManager.STOW_CLOSED);
        }
    }

    public void update() {
        switch (state) {
            case STOP:
                // EVERYTHING INACTIVE
                updateChildren();
                Robot.IM.setState(IntakeManager.STOP);
                Robot.CM.setState(CarriageManager.STOP);
                Robot.EM.setState(ElevatorManager.STOP);

                if (InputManager.intake()) {
                    setState(INTAKE);
                }
                break;

            case INTAKE:
                // ELEVATOR DOWN, INTAKE ACTIVE,CARRIAGE ARMED
                // IF SENSOR INPUT IS RECOGNIZED, GO TO TRANSIT
                Robot.CM.setState(CarriageManager.DUMP);
                Robot.EM.setTarget(Konstanten.RETURN_HEIGHT);
                armGoal = Konstanten.RETURN_HEIGHT;
                updateChildren();

                if (InputManager.bump()) { // bump button
                    Robot.IM.setState(IntakeManager.CORRECT);
                } else if (Robot.intake.isCube()){ // if the cube is detected, stop intaking
                    Robot.IM.setState(IntakeManager.GRIP_DOWN);
                } else if (InputManager.eject()) { // if eject is pressed eject the cube
                    Robot.IM.setState(IntakeManager.SPIT);
                } else if (Timer.getFPGATimestamp() <= time + 1) { // if the intake has been down less that 1 second, have the arms open
                    Robot.IM.setState(IntakeManager.OPEN_DOWN);
                } else { // otherwise intake normally with the arms closed
                    Robot.IM.setState(IntakeManager.INTAKE);
                }

                // when the intake button is let up, pass the cube to the arm
                if (!InputManager.intake()) {
                    setState(PASS_TO_ARM);
                }

                break;

            case PASS_TO_INTAKE:
                // WAITING FOR ARM TO COME DOWN TO PASS CUBE BACK TO INTAKE
                Robot.EM.setTarget(Konstanten.RETURN_HEIGHT);
                armGoal = Konstanten.RETURN_HEIGHT;
                updateChildren();

                if (Robot.elevator.minHeight()) {
                    Robot.IM.setState(IntakeManager.GRIP_UP);
                    Robot.CM.setState(CarriageManager.DUMP);

                    // record time for the delay between ungripping the holder and lowering the intake
                    time = Timer.getFPGATimestamp();

                    setState(DRIVIN_AROUND_WIT_DA_INTAKE_DOWN);
                } else { // arm is still moving
                    collisionAvoidance();
                }
                break;

            case DRIVIN_AROUND_WIT_DA_INTAKE_DOWN:
                // INTAKE DOWN, ARM DOWN
                updateChildren();

                Robot.EM.setTarget(Konstanten.RETURN_HEIGHT);

                // don't lower the intake until the delay has passed
                if (Timer.getFPGATimestamp() >= time + Konstanten.PASSBACK_TIME) {
                    Robot.IM.setState(IntakeManager.GRIP_DOWN);
                } else {
                    Robot.IM.setState(IntakeManager.GRIP_UP);
                }

                //user inputs
                if (InputManager.eject()) {
                    Robot.IM.setState(IntakeManager.SPIT);

                    // record time for the delay between setting the intake to spit and raising it back up
                    time = Timer.getFPGATimestamp();

                    setState(EMPTY);
                }
                // if any arm height is detected, pass the cube back and go to that height
                // (setting armGoal should go into effect once we reach the TRANSIT state again)
                if (InputManager.moveUp()) {
                    armGoal = Robot.elevator.getAngle() + Konstanten.ELEVATOR_STEP;
                    setState(PASS_TO_ARM);
                }
                if (InputManager.moveDown()) {
                    setState(PASS_TO_ARM);
                }
                if (InputManager.scaleHeight()) {
                    armGoal = Konstanten.SCALE_HEIGHT;
                    setState(PASS_TO_ARM);
                }
                if (InputManager.switchHeight()) {
                    armGoal = Konstanten.SWITCH_HEIGHT;
                    setState(PASS_TO_ARM);
                }
                if (InputManager.returnHeight()) {
                    armGoal = Konstanten.RETURN_HEIGHT;
                    setState(PASS_TO_ARM);
                }
                if (InputManager.intake()) {
                    setState(INTAKE);
                }

                break;
            case PASS_TO_ARM:
                // ARM DOWN, INTAKE UP, CARRIAGE OPEN
                // IF USER INPUT IS RECOGNIZED, GO TO EMPTY
                updateChildren();

                Robot.EM.setTarget(Konstanten.RETURN_HEIGHT);
                Robot.CM.setState(CarriageManager.DUMP);

                // if arm is down, do the handoff
                if (Robot.elevator.minHeight()) {
                    Robot.IM.setState(IntakeManager.GRIP_UP);

                    // record time for the delay between raising the intake and gripping the holder
                    time = Timer.getFPGATimestamp();

                    setState(TRANSIT);
                } else { // arm is not down yet
                    Robot.IM.setState(IntakeManager.GRIP_DOWN);
                }

                break;

            case TRANSIT:
                // HOLDING A CUBE IN THE ARM AT VARIOUS HEIGHTS
                updateChildren();

                // when all the way down, have the intake grip instead of just stow
                if (Robot.elevator.minHeight()) {
                    Robot.IM.setState(IntakeManager.GRIP_UP);
                }
                else {
                    collisionAvoidance();
                }

                // delay between raising the intake (coming from INTAKE or DRIVIN_AROUND_WIT_DA_INTAKE_DOWN) and gripping the holder
                if (Timer.getFPGATimestamp() >= time + Konstanten.PASSOFF_TIME) {
                    // grip holder, move arm wherever
                    Robot.CM.setState(CarriageManager.GRAB);
                    Robot.EM.setTarget(armGoal);
                } else { // delay has not passed
                    // open holder and keep arm down
                    Robot.EM.setTarget(Konstanten.RETURN_HEIGHT);
                    Robot.CM.setState(CarriageManager.DUMP);
                }

                // user inputs
                if (InputManager.intake()) {
                    setState(INTAKE);
                }

                if ((InputManager.moveUp()) && !Robot.elevator.maxHeight()){
                    armGoal = Robot.elevator.getAngle() + Konstanten.ELEVATOR_STEP;
                }

                if (InputManager.moveDown() && !Robot.elevator.minHeight()){
                    armGoal = Robot.elevator.getAngle() - Konstanten.ELEVATOR_STEP;
                }

                if (InputManager.eject()) {
                    setState(EMPTY);
                }

                if (InputManager.switchHeight()) {
                    armGoal = Konstanten.SWITCH_HEIGHT;
                }

                if (InputManager.scaleHeight()) {
                    armGoal = Konstanten.SCALE_HEIGHT;
                }

                if (InputManager.returnHeight()) {
                    armGoal = Konstanten.RETURN_HEIGHT;
                }

                if (InputManager.spit()) {
                    setState(PASS_TO_INTAKE);
                }
                break;

            case EMPTY:
                // CUBE IS EJECTED, ARM GOES TO DRIVER INPUT
                Robot.CM.setState(CarriageManager.DUMP);
                collisionAvoidance();
                updateChildren();

                // if we're coming from DRIVIN_AROUND_WIT_DA_INTAKE_DOWN, wait for the ejection to finish
                // otherwise, time should already be something from much longer ago
                if (Timer.getFPGATimestamp() >= time + Konstanten.SPIT_DELAY) {
                    Robot.IM.setState(IntakeManager.STOW_CLOSED);
                }

                Robot.EM.setTarget(armGoal);

                // user input
                if ((InputManager.moveUp()) && !Robot.elevator.maxHeight()){
                    armGoal = Robot.elevator.getAngle() + Konstanten.ELEVATOR_STEP;
                }

                if (InputManager.moveDown() && !Robot.elevator.minHeight()){
                    armGoal = Robot.elevator.getAngle() - Konstanten.ELEVATOR_STEP;
                }

                if (InputManager.switchHeight()) {
                    armGoal = Konstanten.SWITCH_HEIGHT;
                }

                if (InputManager.scaleHeight()) {
                    armGoal = Konstanten.SCALE_HEIGHT;
                }

                if (InputManager.returnHeight()) {
                    armGoal = Konstanten.RETURN_HEIGHT;
                }

                if (InputManager.intake()){
                    time = Timer.getFPGATimestamp();
                    Robot.EM.setTarget(Konstanten.RETURN_HEIGHT);
                    setState(INTAKE);
                }

                if (InputManager.grabIntake()) {
                    setState(TRANSIT);
                }

                break;
        }
    }
}
