package frc.team5115;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import frc.team5115.systems.DriveTrain;
import frc.team5115.systems.Elevator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger extends Thread {
    //define our objects
    public static DriverStation ds;
    public static PowerDistributionPanel PDP;
    public static  DriveTrain DT;
    public static Elevator elevator;
    //define our talons to grab voltages individually
    TalonSRX frontleft;
    TalonSRX frontright;
    TalonSRX backleft;
    TalonSRX backright;
    //define time format
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yy HH-mm");
    //define a print writer
    PrintWriter pw;

    public void Logger(){
        //begin writing to file
        try {
            pw = new PrintWriter("log-" + dtf.format(LocalDateTime.now()) + ".csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //header
        List<String> trackerName = new ArrayList<>();
        trackerName.add("Match Number");
        trackerName.add("Battery Voltage");
        trackerName.add("Motor Voltage");
        trackerName.add("Total Current");
        trackerName.add("Driver Station Comms");
        trackerName.add("Field Comms");
        trackerName.add("Enabled?");
        trackerName.add("Autonomous/Teleop");
        trackerName.add("Average Speed");
        trackerName.add("Yaw");
        trackerName.add("Turn Velocity");
        trackerName.add("Arm Hieght");
        trackerName.add("Arm Status");
        trackerName.add("Intake Status");

        //print header
        pw.println(trackerName);
        pw.flush();
        //begin loop
        run();
    }


    public void run(){
        //shorten our objects
        ds = DriverStation.getInstance();
        PDP = new PowerDistributionPanel();
        DT = new DriveTrain();
        elevator = new Elevator();
        //assign our talons ID's
        frontleft = new TalonSRX(Constants.FRONT_LEFT_MOTOR_ID);
        frontright = new TalonSRX(Constants.FRONT_RIGHT_MOTOR_ID);
        backleft = new TalonSRX(Constants.BACK_LEFT_MOTOR_ID);
        backright = new TalonSRX(Constants.BACK_RIGHT_MOTOR_ID);
        //list of things
        List<Object> tracker = new ArrayList<>();
        tracker.add(ds.getMatchNumber());
        tracker.add(RobotController.getBatteryVoltage());
        tracker.add("1:" + frontleft.getMotorOutputVoltage() + " 2:" + frontright.getMotorOutputVoltage() + " 3:" + backleft.getMotorOutputVoltage() + " 4:" + backright.getMotorOutputVoltage());
        tracker.add(PDP.getTotalCurrent());
        tracker.add(ds.isDSAttached());
        tracker.add(ds.isFMSAttached());
        tracker.add(ds.isEnabled());
        //just some logic to print teleop or autonomous
        if(ds.isAutonomous()){
            tracker.add("Autonomous");
        } else if(ds.isAutonomous() && ds.isEnabled()){
            tracker.add("TeleOp");
        }
        tracker.add(DT.averageSpeed());
        tracker.add(DT.getYaw());
        tracker.add(DT.getTurnVelocity());
        tracker.add(elevator.getAngle());
        tracker.add("Arm Status");
        tracker.add("Intake Status");

        //when run is called, keep doing this
        try {
            while (true) {
                pw.println(tracker);
                pw.flush();
                Thread.sleep(100);
            }
            //until any kind of Exception happens
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }


}
