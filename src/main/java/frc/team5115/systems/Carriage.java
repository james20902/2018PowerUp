package frc.team5115.systems;

/*
   import com.ctre.phoenix.motorcontrol.ControlMode;
   import com.ctre.phoenix.motorcontrol.can.TalonSRX;
 */
import com.kauailabs.navx.frc.AHRS;
import frc.team5115.Constants;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Carriage {
    DoubleSolenoid grabberSolenoid;


    public Carriage(){
        grabberSolenoid = new DoubleSolenoid(Constants.PHEUMATIC_PCM_1_ID, Constants.CARRIAGE_FORWARD_CHANNEL, Constants.CARRIAGE_REVERSE_CHANNEL);
    }

    public void grab(){
        grabberSolenoid.set(DoubleSolenoid.Value.kForward);
    }
    public void eject(){
        grabberSolenoid.set(DoubleSolenoid.Value.kReverse);
    }
    public void stop(){
        grabberSolenoid.set(DoubleSolenoid.Value.kOff);
    }

}
