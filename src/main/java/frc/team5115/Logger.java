package frc.team5115;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import frc.team5115.robot.Robot;
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
        String header = "";
        header +=("Match Number");
        header +=("Battery Voltage");
        header +=("Motor VoltageFL");
        header +=("Motor VoltageFR");
        header +=("Motor VoltageBL");
        header +=("Motor VoltageBR");
        header +=("Total Current");
        header +=("Driver Station Comms");
        header +=("Field Comms");
        header +=("Enabled?");
        header +=("Autonomous/Teleop");
        header +=("Average Speed");
        header +=("Yaw");
        header +=("Turn Velocity");
        header +=("Arm Hieght");
        header +=("Arm Status");
        header +=("Intake Status");

        //print header
        pw.println(header);
        pw.flush();
        //begin loop
        run();
    }


    public void run(){
        //list of things
        List<Object> tracker = new ArrayList<>();
        //define motors

        //when run is called, keep doing this
        try {
            while (true) {
                tracker.add(ds.getMatchNumber());
                tracker.add(RobotController.getBatteryVoltage());
                tracker.add("1:" + Robot.drivetrain.voltageFL() + " 2:" + Robot.drivetrain.voltageFR() + " 3:" + Robot.drivetrain.voltageBL() + " 4:" + Robot.drivetrain.voltageBR());
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
                tracker.add(Robot.drivetrain.averageSpeed());
                tracker.add(Robot.drivetrain.getYaw());
                tracker.add(Robot.drivetrain.getTurnVelocity());
                tracker.add(Robot.elevator.getAngle());
                tracker.add("Arm Status");
                tracker.add("Intake Status");

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
