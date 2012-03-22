/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*--------------------Edited by the InTech Megabots - 2012--------------------*/

//@author Erik


package edu.wpi.first.wpilibj.templates;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.can.*;
import edu.wpi.first.wpilibj.CANSpeedControllerDrive;

import com.sun.squawk.util.MathUtils;

public class Drive {
    private CANJaguar f_right;
    private CANJaguar f_left;
    private CANJaguar r_right;
    private CANJaguar r_left;
    
    private CANSpeedControllerDrive frPID;
    private CANSpeedControllerDrive flPID;
    private CANSpeedControllerDrive rrPID;
    private CANSpeedControllerDrive rlPID;
    
    int maxSpeed = 430;
    
    public Drive(){
        try{
            f_right = new CANJaguar(2);
            f_right.enableControl();
            f_left = new CANJaguar(3);
            f_left.enableControl();
            r_right = new CANJaguar(4);
            r_right.enableControl();
            r_left = new CANJaguar(5);
            r_left.enableControl();
            
            /*f_right.configEncoderCodesPerRev(250);
            f_right.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            f_right.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            
            f_left.configEncoderCodesPerRev(250);
            f_left.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            f_left.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            
            r_left.configEncoderCodesPerRev(250);
            r_left.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            r_left.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            
            r_right.configEncoderCodesPerRev(250);
            r_right.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
            r_right.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
            
            flPID = new CANSpeedControllerDrive(.01, 0, 0, f_left);
            flPID.setInputRange(-maxSpeed, maxSpeed);
            flPID.enable();
            
            frPID = new CANSpeedControllerDrive(.01, 0, 0, f_right);
            frPID.setInputRange(-maxSpeed, maxSpeed);
            frPID.enable();
            
            rlPID = new CANSpeedControllerDrive(.01, 0, 0, r_left);
            rlPID.setInputRange(-maxSpeed, maxSpeed);
            rlPID.enable();
            
            rrPID = new CANSpeedControllerDrive(.01, 0, 0, r_right);
            rrPID.setInputRange(-maxSpeed, maxSpeed);
            rrPID.enable();*/
        }
        catch (CANTimeoutException e){
        System.out.println("Error Initializing Jaguars - " + e);
        }
    }

    public void setDriveJ(double x, double y, double turn) {
        double dir;
        double pwr;
        pwr = Math.sqrt(x*x+y*y);
        dir = MathUtils.atan2(y, -x)/Math.PI * 180;
        setDriveA(dir,pwr,turn);
    }
    public void setDriveA(double dir,double pwr, double t) {
        double fr,fl,rr,rl;
        pwr = limit(pwr);
        t = limit(t)/1.5;
        fr = Math.cos((dir + 45)/180*Math.PI);
        fl = Math.cos((dir - 45)/180*Math.PI);
        rr = fl*pwr - t;
        rl = fr*pwr + t;
        fl = fl*pwr + t;
        fr = fr*pwr - t;

        setMotors(fl,fr,rl,rr);
    }

    public double limit(double d) {
        d = d > 1? 1:d;
        d = d < -1? 1:d;
        return d;
    }

    public void setMotors(double fl, double fr, double rl, double rr) {
        double g;
        fl = limit(fl);
        fr = limit(fr);
        rl = limit(rl);
        rr = limit(rr);
        
        try{
            if (f_right.getPowerCycled()){
                f_right = new CANJaguar(2);
                f_right.enableControl();
            }
            if (f_left.getPowerCycled()){
                f_left = new CANJaguar(3);
                f_left.enableControl();
            }
            if (r_right.getPowerCycled()){
                r_right = new CANJaguar(4);
                r_right.enableControl();
            }
            if (r_left.getPowerCycled()){
                r_left = new CANJaguar(5);
                r_left.enableControl();
            }
        
            f_right.setX(-fr);
            f_left.setX(fl);
            r_right.setX(-rr);
            r_left.setX(rl);
            
            /*flPID.setSetpoint(-(fr*maxSpeed));
            frPID.setSetpoint(fl*maxSpeed);
            rlPID.setSetpoint(rr*maxSpeed);
            rrPID.setSetpoint(-(rr*maxSpeed));*/
        }
        catch (Exception e){
            System.out.println("Drive Error - " + e);
        }
    }
}
