package frc.team5115;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import frc.team5115.systems.DriveTrain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger extends Thread {
    //define time format
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    //write new file with present time
    FileWriter log = new FileWriter("C:\\Users\\james\\Desktop\\PowerUp\\log-" + dtf.format(LocalDateTime.now()) + ".csv");
    //define a print writer
    PrintWriter pw;

    public void Logger(){
        //begin writing to file
        pw = new PrintWriter(log);
        //header
        pw.println("Match Number, Battery Voltage, Motor Voltage, Total Current, Driver Station Comms, Field Comms, Enabled, Autonomous/Teleop, Average Speed, Yaw, Turn Velocity, Arm Hieght, Arm Status, Intake Status");
        pw.flush();
        //begin loop
        start();
    }


    public void start(){
        //while the bot's enabled
        //keep doing this
        try {
            while (DriverStation.getInstance().isEnabled()) {
                pw.println(DriverStation.getInstance().getMatchNumber() + "," + RobotController.getBatteryVoltage() + "," + DriverStation.getInstance().getMatchNumber() + ",");
                pw.flush();
                Thread.sleep(100);
            }
            //until an IOException happens
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }


}
