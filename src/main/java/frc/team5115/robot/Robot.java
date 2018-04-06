package frc.team5115.robot;

import frc.team5115.Constants;
import frc.team5115.statemachines.CarriageManager;
import frc.team5115.statemachines.CubeManipulatorManager;
import frc.team5115.statemachines.Drive;
import frc.team5115.statemachines.ElevatorManager;
import frc.team5115.systems.Carriage;
import frc.team5115.systems.DriveTrain;
import frc.team5115.systems.Elevator;
import frc.team5115.auto.Auto;
import frc.team5115.auto.DriveForwardSome;
import frc.team5115.systems.Intake;
import frc.team5115.statemachines.IntakeManager;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.networktables.NetworkTable;;

public class Robot extends TimedRobot {

    /**
     * The Robot class handles the basic functions of the robot
     * Subsystems and State Machines are created and run during the various stages of operation
     * Robot has 3 distinct phases, autonomous, teleop, and disabled
     * Autonomous - Robot runs by itself
     * Teleop - Robot is controlled by the driver
     * Disabled - Robot is off
     * (Note: If you have no clue as to what you're doing, there is a Google slides presentation on the shared FRC files in your drive named "How To code" )
     */

    /**
     * First, we define all of the subsystems, state machines, and other variables
     * To define an object or variable, follow this format:
     * public static class name;
     * e.g. public static DriveTrain DT;
     * class - The class of the object you are creating e.g. DriveTrain, int, double, etc.
     * name - Any name you want, but make it something meaningful like an acronym
     * Don't forget to end your lines with semicolons(;)
     */

    // Define subsystems
    public static DriveTrain drivetrain;
    public static PowerDistributionPanel PDP;

    public static DriveForwardSome autoDrive;
    public static Drive drive;
    public static DriverStation ds;
    public static SendableChooser positionChooser;
    public static SendableChooser strategyChooser;

    public static CubeManipulatorManager CMM;
    public static int position;
    public static int switchPosition;
    public static int scalePosition;
    public static int strategy;
    public static Auto auto;
    public static Intake intake;
    public static IntakeManager IM;
    public static Carriage carriage;
    public static CarriageManager CM;
    public static Elevator elevator;
    public static ElevatorManager EM;
    public static NetworkTable NT;

    public String gameData;

    double armTarget;

    int shouldI;

    public int dont(){
        return 1 / 0;
    }

    // Initialization phase of the robot class
    // This method runs once when the robot turns on and does not run again until the robot reboots
    public void robotInit() {
        /**
         * Constructing an object runs the initialization method of the class
         * To construct an object, follow this format:
         * name-of-object = new class();
         * e.g. DT = new DriveTrain();
         * To construct a variable, give it a default value
         * e.g. var = 0;
         * In this case, 0 is the default value that you set it to initially
         * You can also give the constructor variables like you would any other method
         * e.g. object = new Class(20);
         * Make sure that the value you give it is consistent with what the constructor method in the class asks for
         */

        // Initialize subsystems and statemachines
        drivetrain = new DriveTrain();
        PDP = new PowerDistributionPanel();
        drive = new Drive();
        autoDrive = new DriveForwardSome();
        intake = new Intake();
        IM = new IntakeManager();
        carriage = new Carriage();
        CM = new CarriageManager();
        elevator = new Elevator();
        EM = new ElevatorManager();
        CMM = new CubeManipulatorManager();
        ds = DriverStation.getInstance();

        // make the position chooser for smartdashboard
        // this stuff may be different for shuffleboard
        positionChooser = new SendableChooser();
        positionChooser.addDefault("Left", 1);
        positionChooser.addObject("Right", 2);
        positionChooser.addObject("Center", 3);
        SmartDashboard.putData("Position", positionChooser);

        // make the auto mode chooser
        strategyChooser = new SendableChooser();
        strategyChooser.addDefault("Strategy 1- center switch auto", 2); //these are the state numbers in Auto.java
        // deprecated autos
        //		strategyChooser.addObject("Strategy 2- put something in scale", 3);
        //		strategyChooser.addObject("Strategy 3- cross auto line and do nothing ", 4);
        //		strategyChooser.addObject("Strategy 4- start on a side, drop a cube if that side of the switch is ours", 5);
        //		strategyChooser.addObject("Strategy 5- same as 4 but we go for whichever side of the switch is ours", 7);
        strategyChooser.addObject("Strategy 6- side auto, switch preference", 8);
        strategyChooser.addObject("Strategy 7- side auto, scale preference", 9);
        strategyChooser.addObject("Strategy 8- side auto, switch preference, fast scale", 10);
        strategyChooser.addObject("Strategy 9- side auto, scale preference, fast scale", 11);
        SmartDashboard.putData("Strategy", strategyChooser);
        drive.setState(Drive.STOP);

        // retry sending the choosers
        Timer.delay(1);
        SmartDashboard.putData("Position", positionChooser);
        SmartDashboard.putData("Strategy", strategyChooser);
    }

    // Runs once when the autonomous phase of the game starts
    public void autonomousInit() {
        drivetrain.resetGyro();
        drivetrain.resetEncoders();
        Timer.delay(0.1);

        position = (int) positionChooser.getSelected();

        // data format in LLL, LRL, RLR, or RRR starting with the closest switch
        // assign a 1 if the left plate is ours, 2 if the right plate is ours
        gameData = ds.getGameSpecificMessage();
        switchPosition = ('L' == gameData.charAt(0)) ? 1 : 2;
        scalePosition = ('L' == gameData.charAt(1)) ? 1 : 2;

        strategy = (int) strategyChooser.getSelected();

        auto = new Auto(position, switchPosition, scalePosition, strategy);

        drivetrain.inuse = true;

        /**
         * To initialize a state machine, follow this format:
         * object.setState(initialState)
         * e.g. dt.setState(DriveTrain.START);
         * Per this example, the initialState value should come from the class of the object
         * You could just give it a normal integer like 0 or 1, but this is nicer organizationally
         */
        carriage.grab();
        auto.setState(Auto.INIT);

        // we think this prevents the drivetrain from being backwards. something to do with the encoders
        drivetrain.drive(0.01, 0);
    }

    //Runs periodically while the game is in the autonomous phase
    public void autonomousPeriodic() {
        Timer.delay(.005);

        auto.update();
    }

    // Runs once when the game enters the driver operated stage
    public void teleopInit() {
        // make sure the robot doesn't move or do anything crazy coming out of auto
        drivetrain.inuse = false;
        drivetrain.drive(0,0);
        drivetrain.resetEncoders();
        CMM.armGoal = elevator.getAngle();

        CMM.setState(CubeManipulatorManager.TRANSIT);
        drive.setState(Drive.DRIVING);
        EM.setState(ElevatorManager.MOVING_TO);
    }

    // Runs periodically when the game is in the driver operated stage
    public void teleopPeriodic() {
        Timer.delay(.005);
        drive.update();
        CMM.update();



        // This code was used for testing subsystems independently. It should have been put in testperiodic
        //
        //	 	EM.setTarget(armTarget);
        //	 	EM.update();
        //		IM.update();
        //		CM.update();
        //
        //		if (InputManager.getButton(12)) {	// lower and start intake
        //			intake.lowerIntake();
        //			Timer.delay(0.5);
        //			intake.relax();
        //		}
        //		if (InputManager.getButton(6)) {	// grip intake
        //			intake.grip();
        //		}
        //		if (InputManager.getButton(4)) {	// release intake
        //			intake.release();
        //		}
        //		if (InputManager.getButton(11)) {	// relax intake
        //			intake.relax();
        //		}
        //		if (InputManager.getButton(1)) {	// release carriage
        //			carriage.eject();
        //		}
        //		if (InputManager.getButton(9)) {	// grip carriage
        //			carriage.grab();
        //		}
        //
        //		if (InputManager.getButton(5)) {
        //			//armTarget += Constants.ELEVATOR_STEP;
        //			elevator.move(0.5);
        //			System.out.println("arm up");
        //		}
        //		else if (InputManager.getButton(3)) {
        //			//armTarget -= Constants.ELEVATOR_STEP;
        //			elevator.move(-0.5);
        //			System.out.println("arm down");
        //		} else {
        //			elevator.move(0);
        //		}
        //
        //		if (InputManager.getButton(9)) {
        //			armTarget = Constants.RETURN_HEIGHT;
        //		}
        //		if (InputManager.getButton(8)) {
        //			armTarget = Constants.SWITCH_HEIGHT;
        //		}
        //		if (InputManager.getButton(7)) {
        //			armTarget = Constants.SCALE_HEIGHT;
        //		}

        //		if (InputManager.getButton(Constants.KILL)) {
        //			dont();
        //		}
    }



    // Runs when the robot is disabled
    public void disabledInit() {
        autoDrive.setState(DriveForwardSome.FINISHED);
    }


    // Runs periodically while the robot is disabled
    public void disabledPeriodic() {
        autoDrive.setState(DriveForwardSome.FINISHED);
        drivetrain.drive(0,0);
        CMM.setState(CubeManipulatorManager.STOP);
        EM.setState(ElevatorManager.STOP);
    }

}
