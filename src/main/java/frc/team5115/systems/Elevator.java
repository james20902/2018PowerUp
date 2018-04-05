package frc.team5115.systems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.team5115.Konstanten;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;


public class Elevator {

    double lastAngle = 00;
    public boolean movingArm = false;
    TalonSRX armMover;

    public Elevator(){
        armMover = new TalonSRX(Konstanten.MOVER_MOTOR_ID);
        armMover.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 5);
        armMover.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0x00);
        armMover.configForwardSoftLimitThreshold(Konstanten.POT_THRESHOLD, 5);
        armMover.configForwardSoftLimitEnable(false, 0);
    }

    public double getAngle(){
        return armMover.getSelectedSensorPosition(0);
    }

    public double getAngleSpeed(){
        return armMover.getSelectedSensorVelocity(0);
    }

    public void move(double speed){
        if (Math.abs(speed) > 0.1){
            movingArm = true;
        }
        else {
            movingArm = false;
        }

        armMover.set(ControlMode.PercentOutput, speed);
    }

    public boolean maxHeight(){
        return (getAngle() + Konstanten.ELEVATOR_THRESHOLD >= Konstanten.ELEVATOR_MAX);
    }
    public boolean minHeight(){
        return (getAngle() - Konstanten.ELEVATOR_THRESHOLD <= Konstanten.ELEVATOR_MIN);
    }
}
