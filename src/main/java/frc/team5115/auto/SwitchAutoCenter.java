package frc.team5115.auto;

import frc.team5115.Constants;
import frc.team5115.PID;
import frc.team5115.robot.Robot;
import frc.team5115.auto.AutoDrive;
import frc.team5115.systems.DriveTrain;
import frc.team5115.statemachines.CarriageManager;
import frc.team5115.statemachines.CubeManipulatorManager;
import frc.team5115.statemachines.ElevatorManager;
import frc.team5115.statemachines.IntakeManager;
import frc.team5115.statemachines.StateMachineBase;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//drop a cube in the correct side of the switch
public class SwitchAutoCenter extends StateMachineBase {
	public static final int INIT = 0;
	public static final int DRIVING = 1;		//2 ft
	public static final int TURNING = 2;	//45 degrees
	public static final int DRIVING2 = 3;		//6.4 ft
	public static final int TURNING2 = 4;	//-45 degrees
	public static final int DRIVING3 = 5;		//4.8 ft
	public static final int PLACE = 6;
	public static final int FINISHED = 7;

	AutoDrive drive;

	
	AutoDrive turn; 
	double time;
	
	int switchPosition;
	
	int left = 1;
	int right = 2;
	
	public SwitchAutoCenter(int sp) {
		drive = new AutoDrive();
		switchPosition = sp;
	}
	
	public void setState(int s) {
    	switch (s) {
    	case DRIVING3:
    		time = Timer.getFPGATimestamp();
    		break;
    	case PLACE:
    		time = Timer.getFPGATimestamp();
    		break;
    	}
    	state = s;
    }
	
	protected void updateChildren() {
		drive.update();
		Robot.EM.update();
		Robot.IM.update();
		Robot.CM.update();
	}
	
	public void update() {
		SmartDashboard.putNumber("state", state);
		switch(state){
		case INIT:
			drive.startLine(1.5, 0.45);
			Robot.EM.setState(ElevatorManager.STOP);
			Robot.IM.setState(IntakeManager.STOW_CLOSED);
			Robot.CM.setState(CarriageManager.GRAB);
			setState(DRIVING);
			break;
		case DRIVING:
			updateChildren();
			if(drive.state == AutoDrive.FINISHED){
				if(switchPosition == left) {
					drive.startTurn(-45, 0.5);
				}
				else {
					drive.startTurn(45, 0.5);
				}
//				Robot.CMM.setState(CubeManipulatorManager.TRANSIT);
				setState(TURNING);
			}
			break;
			
		case TURNING:
			updateChildren();
			if (drive.state == AutoDrive.FINISHED) { 
				if(switchPosition == left) {
					drive.startLine(7, 0.5);
				}
				else {
					drive.startLine(5.5, 0.5);
				}
				Robot.EM.setTarget(Constants.SWITCH_HEIGHT);
				Robot.EM.setState(ElevatorManager.MOVING_TO);
				setState(DRIVING2);
			}
			break;
			
		case DRIVING2:
			updateChildren();
			Robot.CMM.collisionAvoidance();
			if (drive.state == AutoDrive.FINISHED) { 
				if(switchPosition == left) {
					drive.startTurn(45, 0.5);
				}
				else {
					drive.startTurn(-45, 0.5);
				}
				setState(TURNING2);
			}
			break;
					
		case TURNING2:
			updateChildren();
			if (drive.state == AutoDrive.FINISHED) { 
				if(switchPosition == left) {
					drive.startLine(4, 0.25);
				}
				else {
					drive.startLine(4, 0.25);
				}
			
				setState(DRIVING3);
			}
			break;
		case DRIVING3:
			updateChildren();
			if (drive.state == AutoDrive.FINISHED || Timer.getFPGATimestamp() - time > 2) { 
				Robot.CM.setState(CarriageManager.DUMP);
				Robot.drivetrain.drive(.2, 0);
				setState(PLACE);
			}
			break;
		case PLACE:
			updateChildren();
			Robot.drivetrain.drive(.2, 0);
			if(Timer.getFPGATimestamp() >= time + Constants.SPIT_DELAY)
				Robot.drivetrain.drive(0, 0);
				setState(FINISHED);
			break;
			
		case FINISHED:
			break;
		}
	}
}
