/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/*--------------------Edited by the InTech Megabots - 2012--------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Dashboard;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.Imaging.EagleEye;

//@author Erik

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    
    Joystick drive_control, control;
    Drive drive;
    double x,y,t, rpm;
    String readString, xTurn;
    int xTurnVal;
    Dashboard dash;
    DriverStationLCD station;
    AxisCamera camera;
    CANJaguar launcher, belt2;
    SerialPort serial;
    Relay belt1;
    
    CANSpeedController csc;
    
    wedge wedge;
    
    boolean wheelsOn;
    
    Victor turret;
    Encoder wheelEncoder;
    Timer timer;
    
    //EagleEye tracking;
    Relay lights;
    
    boolean autoWheels = true;
    
    //Servo servo1, servo2;
    
    public void robotInit() {
        try {
            drive_control = new Joystick(1);
            control = new Joystick(2);
            drive = new Drive();
            station = DriverStationLCD.getInstance();
            camera = AxisCamera.getInstance();
            camera.writeResolution(AxisCamera.ResolutionT.k320x240);
            camera.writeBrightness(0);
            camera.writeMaxFPS(10);
            launcher = new CANJaguar(7);
            belt1 = new Relay(1);
            //belt1.setDirection(Relay.Direction.kBoth);
            belt2 = new CANJaguar(8);
            turret = new Victor(4);
            
            /*launcher.setPID(0.5, 0.001, 0.0);
            launcher.configEncoderCodesPerRev(360);
            launcher.changeControlMode(CANJaguar.ControlMode.kSpeed);
            launcher.enableControl();*/
            launcher.configEncoderCodesPerRev(360);
            launcher.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            launcher.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            /*wheelEncoder = new Encoder(1, 2, true, CounterBase.EncodingType.k4X);
            wheelEncoder.setDistancePerPulse(1);
            wheelEncoder.setReverseDirection(true);
            wheelEncoder.start();*/
            csc = new CANSpeedController(.02, 0, 0, launcher);
            csc.setInputRange(0, 3000);
            csc.enable();
            
            timer = new Timer();
            //tracking = new EagleEye();
            lights = new Relay(3);
            /*try {
                serial = new SerialPort(115200);
            } catch (Exception ex) {
                System.out.println("Cannot open serial connection " + ex);
            }*/
            
        } catch (CANTimeoutException ex) {
            System.out.println(ex);
        }
        
        wedge = new wedge(2, 1, 2);
        //servo1 = new Servo(1);
        //servo2 = new Servo(2);
        //wedge = new Relay(2);
    }
    
    
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        wedge.moveDown();
        lights.set(Relay.Value.kForward);
        //tracking.run();
        try {
            driveMainBelt(true,true);
            csc.setSetpoint(2300); //15' = ~2400rpm
            if(launcher.getSpeed()<-2250){
                    try{
                        belt2.setX(-1);
                    }catch(Exception e){}
            }
            else{
                belt2.setX(0);
            }
            station.println(DriverStationLCD.Line.kMain6, 1, "RPM: " + String.valueOf(csc.tmpResult));
        } catch (CANTimeoutException ex) {}
        
        /*try{
            if(tracking.foundTarget()){
                station.println(DriverStationLCD.Line.kUser2, 1, "Distance: " + String.valueOf(tracking.getTarget("HIGH").distance));
                station.println(DriverStationLCD.Line.kUser3, 1, "Suggested RPM: " + String.valueOf(tracking.getTarget("HIGH").distance*62.8));
                if(x>0)
                    station.println(DriverStationLCD.Line.kUser4, 1, "Rotation: <--");
                else if(x<0)
                    station.println(DriverStationLCD.Line.kUser4, 1, "Roation: -->");
                else
                    station.println(DriverStationLCD.Line.kUser4, 1, "I don't need to rotate");
            }
            else{
                station.println(DriverStationLCD.Line.kUser2, 1, "Help, I can't find target by myself!");
                station.println(DriverStationLCD.Line.kUser3, 1, "");
                station.println(DriverStationLCD.Line.kUser4, 1, "");
            }
        }catch(Exception e){}*/
        station.updateLCD();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        lights.set(Relay.Value.kForward);
        //tracking.run();
        /*try {
            readString = serial.readString();
            readString = readString.substring(readString.indexOf("data{"), readString.indexOf("}"));
            xTurn = readString.substring(readString.indexOf("x("), readString.indexOf(")"));
            xTurnVal = Integer.parseInt(xTurn);
        } catch (VisaException ex) {
            System.out.println("Error Reading string - " + ex);
        }*/
        y = drive_control.getRawAxis(1);
        x = drive_control.getRawAxis(2);
        t = drive_control.getRawAxis(3);
        
        if(y > 1)
            y = 1;
        else if(y < -1)
            y = -1;
        
        if(x > 1)
            x = 1;
        else if(x < -1)
            x = -1;
        
        drive.setDriveJ(x, y, t);
                
        /*if(drive_control.getRawButton(12))
            //wheelsOn = true;
        else if(drive_control.getRawButton(11));
            wheelsOn = false;*/
        
        /*try{
            launcher.setX((drive_control.getRawAxis(4)-1)/2);
        }catch(Exception e){}*/
        
        //System.out.println((drive_control.getRawAxis(4)-1)*-1350);
        if(!autoWheels){
            csc.setSetpoint((drive_control.getRawAxis(4)-1)*-1500);
            try {
                //launcher.setX((drive_control.getRawAxis(4)+1)*2000);
                System.out.println(-launcher.getSpeed() + " -- " + csc.tmpResult + " -- " + (drive_control.getRawAxis(4)-1)*-1500);
            } catch (Exception e) {System.out.println(e);}
            if(drive_control.getRawButton(11))
                autoWheels=true;
        }
        else{
            csc.setSetpoint(2300);
            if(drive_control.getRawButton(11))
                autoWheels=false;
        }
        
        /*if(wheelsOn)
            shoot(1);
        else
            shoot(0);*/
        
        if(drive_control.getRawButton(5)){
            wedge.moveDown();
            //servo1.set(1);
            //servo2.set(0);
            //wedge.set(Relay.Value.kForward);
        }
        else if(drive_control.getRawButton(6)){
            wedge.moveUp();
            //servo1.set(0);
            //servo2.set(1);
            //wedge.set(Relay.Value.kReverse);
        }
        else{
            //servo1.set(.5);
            //servo2.set(.5);
            //wedge.set(Relay.Value.kOff);
        }
        
        if(drive_control.getRawButton(3))
            turret.set(.5);
        else if(drive_control.getRawButton(4))
            turret.set(-.5);
        else
            turret.set(0);
        
        if(drive_control.getRawButton(2))
            driveMainBelt(true, true);
        else if(drive_control.getRawButton(7))
            driveMainBelt(false, true);
        else
            driveMainBelt(false, false);
        
        if(drive_control.getTrigger()){
            try{
                belt2.setX(-1);
            }catch(Exception e){}
        }
        else if(drive_control.getRawButton(8)){
            try{
                belt2.setX(1);
            }catch(Exception e){}
        }
        else{
            try{
                belt2.setX(0);
            }catch(Exception e){}
        }
        
        if(control.getRawButton(3)){
            turret.set(.25);
        }
        else if(control.getRawButton(4)){
            turret.set(-.25);
        }
        else{
            turret.set(0);
        }
        
        try {
            station.println(DriverStationLCD.Line.kMain6, 1, "RPM: " + String.valueOf(-launcher.getSpeed()));
        } catch (CANTimeoutException ex) {
        }
        /*try{
            if(tracking.foundTarget()){
                station.println(DriverStationLCD.Line.kUser2, 1, "Distance: " + String.valueOf(tracking.getTarget("HIGH").distance));
                station.println(DriverStationLCD.Line.kUser3, 1, "Suggested RPM: " + String.valueOf(tracking.getTarget("HIGH").distance*62.8));
                if(x>0)
                    station.println(DriverStationLCD.Line.kUser4, 1, "Rotation: <--");
                else if(x<0)
                    station.println(DriverStationLCD.Line.kUser4, 1, "Roation: -->");
                else
                    station.println(DriverStationLCD.Line.kUser4, 1, "I don't need to rotate");
            }
            else{
                station.println(DriverStationLCD.Line.kUser2, 1, "Help, I can't find target by myself!");
                station.println(DriverStationLCD.Line.kUser3, 1, "");
                station.println(DriverStationLCD.Line.kUser4, 1, "");
            }
        }catch(Exception e){}*/
        station.updateLCD();
    }
    
    public void shoot(double speed){
        try {
            launcher.setX(speed);
        } catch (CANTimeoutException ex) {
            System.out.println("Cannot launch " + ex);
        }
    }
    
    public void driveMainBelt(boolean forward, boolean move){
        if(move){
            if(forward){
                belt1.set(Relay.Value.kForward);
            }
            else
                belt1.set(Relay.Value.kReverse);
        }
        else
            belt1.set(Relay.Value.kOff);

    }
}
