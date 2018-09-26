package frc.team5115.systems;

import frc.team5115.Constants;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;


public class Intake {

	DigitalInput cubeDetectorL;
	DigitalInput cubeDetectorR;
	DoubleSolenoid cubeSolenoidLeft;
	DoubleSolenoid cubeSolenoidRight;
	DoubleSolenoid intakeLifterLeft;
	DoubleSolenoid intakeLifterRight;
	Spark intakeWheelsLeft;
	Spark intakeWheelsRight;

	public Intake(){
		cubeDetectorL = new DigitalInput(Constants.CUBE_DETECTOR_L);
		cubeDetectorR = new DigitalInput(Constants.CUBE_DETECTOR_R);
		cubeSolenoidLeft = new DoubleSolenoid(Constants.PHEUMATIC_PCM_0_ID, Constants.INTAKE_FORWARD_CHANNEL_LEFT, Constants.INTAKE_REVERSE_CHANNEL_LEFT);
		cubeSolenoidRight = new DoubleSolenoid(Constants.PHEUMATIC_PCM_0_ID, Constants.INTAKE_FORWARD_CHANNEL_RIGHT, Constants.INTAKE_REVERSE_CHANNEL_RIGHT);
		intakeLifterLeft = new DoubleSolenoid(Constants.PHEUMATIC_PCM_0_ID, Constants.LIFTER_FORWARD_CHANNEL_LEFT, Constants.LIFTER_REVERSE_CHANNEL_LEFT);
		intakeLifterRight = new DoubleSolenoid(Constants.PHEUMATIC_PCM_0_ID, Constants.LIFTER_FORWARD_CHANNEL_RIGHT, Constants.LIFTER_REVERSE_CHANNEL_RIGHT);
		intakeWheelsLeft = new Spark(Constants.INTAKE_SPARK_LEFT);
		intakeWheelsRight = new Spark(Constants.INTAKE_SPARK_RIGHT);
	}
	public String SolenoidValL(){
		if (cubeSolenoidLeft.get() == DoubleSolenoid.Value.kForward){
			return "Forward";
		} else if (cubeSolenoidLeft.get() == DoubleSolenoid.Value.kReverse){
			return "Reverse";
		} else {
			return "Off";
		}
	}
	public String SolenoidValR(){
		if (cubeSolenoidRight.get() == DoubleSolenoid.Value.kForward){
			return "Forward";
		} else if (cubeSolenoidRight.get() == DoubleSolenoid.Value.kReverse){
			return "Reverse";
		} else {
			return "Off";
		}
	}
	public void SolenoidL(DoubleSolenoid.Value value){
		cubeSolenoidLeft.set(value);
	}
	public void SolenoidR(DoubleSolenoid.Value value){
		cubeSolenoidRight.set(value);
	}
	public void grip(){
		System.out.println("grip");
		cubeSolenoidLeft.set(DoubleSolenoid.Value.kForward);
		cubeSolenoidRight.set(DoubleSolenoid.Value.kForward);
	}
	
	public void relax(){
		System.out.println("relax");
		cubeSolenoidLeft.set(DoubleSolenoid.Value.kOff);
		cubeSolenoidRight.set(DoubleSolenoid.Value.kOff);
	}
	public void release(){
		System.out.println("release");
		cubeSolenoidLeft.set(DoubleSolenoid.Value.kReverse);
		cubeSolenoidRight.set(DoubleSolenoid.Value.kReverse);
	}
	public void liftIntake(){
		intakeLifterLeft.set(DoubleSolenoid.Value.kForward);
		intakeLifterRight.set(DoubleSolenoid.Value.kForward);
	}
	public void lowerIntake(){
		intakeLifterLeft.set(DoubleSolenoid.Value.kReverse);
		intakeLifterRight.set(DoubleSolenoid.Value.kReverse);
	}
	
	public boolean isCube(){
		//System.out.println("l" + cubeDetectorL.get());
		//System.out.println("r" + cubeDetectorR.get());
		return !cubeDetectorL.get() && !cubeDetectorR.get();
	}
	public void intake(double dir){
		intakeWheelsLeft.set(-dir);
		intakeWheelsRight.set(dir * 0.9);
	}
	public void leftControl(double speed){
		intakeWheelsLeft.set(speed);
	}
	public void rightControl(double speed){
		intakeWheelsRight.set(speed);
	}
	public void bump(){
		intakeWheelsRight.set(-0.1);
	}
	
}