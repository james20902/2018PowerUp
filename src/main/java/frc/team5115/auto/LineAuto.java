package frc.team5115.auto;

import frc.team5115.Constants;
import frc.team5115.PID;
import frc.team5115.robot.Robot;
import frc.team5115.auto.AutoDrive;
import frc.team5115.systems.DriveTrain;
import frc.team5115.statemachines.StateMachineBase;

//cross auto line
public class LineAuto extends StateMachineBase {
	public static final int INIT = 0;
	public static final int DRIVE = 1;	//12 ft
	public static final int FINISHED = 2;

	AutoDrive drive;
	
	public LineAuto() {
		drive = new AutoDrive();
		
	}
	public void update() {
		switch(state){
		case INIT:
			drive.startLine(8, .25);
			setState(DRIVE);
			break;
		case DRIVE:
			drive.update();
			if(drive.state == AutoDrive.FINISHED){
				setState(FINISHED);
			}
			break;
		case FINISHED:
			break;
		}
	}
}
