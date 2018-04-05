package frc.team5115.auto;

import frc.team5115.Konstanten;
import frc.team5115.PID;
import frc.team5115.robot.Robot;
import frc.team5115.statemachines.StateMachineBase;
import frc.team5115.auto.AutoDrive;
import frc.team5115.systems.DriveTrain;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto extends StateMachineBase{

    public static final int INIT = 1;
    public static final int SWITCH = 2;
    public static final int SCALE = 3;
    public static final int LINE = 4;
    public static final int DROPCUBE = 5;
    public static final int NOTHINGTODO = 6;
    public static final int SWITCHCROSS = 7;
    public static final int RISKYSIDESWITCH = 8;
    public static final int RISKYSIDESCALE = 9;
    public static final int FASTSIDESWITCH = 10;
    public static final int FASTSIDESCALE = 11;

    // all possible auto routines
    public static SwitchAutoCenter switchStrat;
    public static ScaleAuto scaleStrat;
    public static DropCubeAuto dropCubeStrat;
    public static LineAuto lineStrat;
    public static NothingToSeeHereAuto NothingToSeeHere;
    public static SwitchAutoSide SwitchAutoSide;
    public static RiskySideAuto_Switch riskySideSwitch;
    public static RiskySideAuto_Scale riskySideScale;
    public static FastSideAuto_Switch fastSideSwitch;
    public static FastSideAuto_Scale fastSideScale;

    int position;
    int switchPosition;
    int scalePosition;
    int strategy;

    public Auto(int p, int sp, int scp, int s) {
        position = p;
        switchPosition = sp;
        scalePosition = scp;
        strategy = s;

        switchStrat = new SwitchAutoCenter(switchPosition);
        scaleStrat = new ScaleAuto(scalePosition, position);
        dropCubeStrat = new DropCubeAuto(position, switchPosition);
        lineStrat = new LineAuto();
        NothingToSeeHere = new NothingToSeeHereAuto();
        SwitchAutoSide = new SwitchAutoSide(position, switchPosition);
        riskySideSwitch = new RiskySideAuto_Switch(position, switchPosition, scalePosition);
        riskySideScale = new RiskySideAuto_Scale(position, switchPosition, scalePosition);
        fastSideSwitch = new FastSideAuto_Switch(position, switchPosition, scalePosition);
        fastSideScale = new FastSideAuto_Scale(position, switchPosition, scalePosition);
    }

    //each time update is called in AutoDrive
    public void update () {
        //Run switch block and check for number
        switch (state) {
            case INIT:
                // this initializes all the auto statemachines together, every time
                // it works as long as update is only called in the right one and none of them have overidden their setState function
                // I (Brian) consider it bad practice
                // better practice would be to initialize them in setState for this class, so that setState is only called for the auto mode to be run
                switchStrat.setState(SwitchAutoCenter.INIT);
                scaleStrat.setState(ScaleAuto.INIT);
                dropCubeStrat.setState(DropCubeAuto.INIT);
                lineStrat.setState(LineAuto.INIT);
                NothingToSeeHere.setState(NothingToSeeHereAuto.INIT);
                SwitchAutoSide.setState(SwitchAutoSide.INIT);
                riskySideSwitch.setState(RiskySideAuto_Switch.INIT);
                riskySideScale.setState(RiskySideAuto_Scale.INIT);
                fastSideSwitch.setState(RiskySideAuto_Switch.INIT);
                fastSideScale.setState(RiskySideAuto_Scale.INIT);
                setState(strategy);
                break;

            case SWITCH:
                switchStrat.update();
                break;
            case SCALE:
                scaleStrat.update();
                break;
            case LINE:
                lineStrat.update();
                break;
            case DROPCUBE:
                dropCubeStrat.update();
                break;
            case NOTHINGTODO:
                NothingToSeeHere.update();
                break;
            case SWITCHCROSS:
                SwitchAutoSide.update();
                break;
            case RISKYSIDESWITCH:
                riskySideSwitch.update();
                break;
            case RISKYSIDESCALE:
                riskySideScale.update();
                break;
            case FASTSIDESWITCH:
                fastSideSwitch.update();
                break;
            case FASTSIDESCALE:
                fastSideScale.update();
                break;

        }
    }
}




