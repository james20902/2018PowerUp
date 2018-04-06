package frc.team5115.robot;

import frc.team5115.Constants;

import edu.wpi.first.wpilibj.Joystick;

public class InputManager {

    /**
     * InputManager is the place where inputs from the joystick go to die
     * Here, they are converted into methods for the rest of the code
     * In order to reference a joystick input, you must reference the InputManager class
     * If you don't really understand Getters and Setters you should go to Stack overflow or try (through trial and error) making them in a new project to see how they work
     */

    static Joystick joy = new Joystick(0);

    //The following methods deal with the basic driving functionalities
    public static double getForward() {
        return -treatAxis(joy.getRawAxis(Constants.AXIS_Y));
    }

    public static double getTurn() {
        return joy.getRawAxis(Constants.AXIS_X);
    }

    //These methods are controlled by the nub on the top of the joystick
    public static double getHat() {
        return joy.getPOV();
    }

    public static double getThrottle() {
        // Joystick give 1 to -1 but we need 0 to 1
        return (1 - joy.getRawAxis(Constants.AXIS_THROTTLE)) / 2;
    }

    // Handles expo and deadband
    public static double treatAxis(double val) {
        if (Math.abs(val) < Constants.JOYSTICK_DEADBAND) {
            val = 0;
        }
        else {
            double sign = (Math.signum(val));
            val = Math.pow(Math.abs(val), Constants.JOYSTICK_EXPO);

            if(sign != Math.signum(val)){
                val *= sign;
            }
        }

        return val;
    }

    public static boolean kill(){
        return joy.getRawButton(Constants.KILL);
    }

    public static boolean switchHeight(){
        return getHat() == 270 || getHat() == 90;
    }
    public static boolean scaleHeight(){
        return getHat() == 0;
    }

    public static boolean returnHeight(){
        return getHat() == 180;
    }

    public static boolean intake(){
        return joy.getRawButton(Constants.INTAKE);
    }

    public static boolean eject(){
        return joy.getRawButton(Constants.EJECT);
    }

    public static boolean moveUp(){
        return joy.getRawButton(Constants.UP);
    }

    public static boolean moveDown(){
        return joy.getRawButton(Constants.DOWN);
    }

    public static boolean spit(){
        return joy.getRawButton(Constants.SPIT);
    }

    public static boolean grabIntake(){
        return joy.getRawButton(Constants.GRAB_INTAKE);
    }

    public static boolean bump(){
        return joy.getRawButton(Constants.CORRECTCUBE);
    }

    public static boolean getButton(int b) {
        return joy.getRawButton(b);
    }
}
