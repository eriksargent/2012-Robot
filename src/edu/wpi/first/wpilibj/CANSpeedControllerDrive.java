/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.parsing.IUtility;
import edu.wpi.first.wpilibj.util.BoundaryException;
import java.util.TimerTask;
/**
 *
 * @author Zac
 */
public class CANSpeedControllerDrive {
    
    public static final double kDefaultPeriod = .05;

    private double m_P;			// factor for "proportional" control
    private double m_I;			// factor for "integral" control
    private double m_D;			// factor for "derivative" control
    private double m_maximumOutput = 1.0;	// |maximum output|
    private double m_minimumOutput = -1.0;	// |minimum output|
    private double m_maximumInput = 0.0;		// maximum input - limit setpoint to this
    private double m_minimumInput = 0.0;		// minimum input - limit setpoint to this
    private boolean m_enabled = false; 			//is the pid controller enabled
    private double m_prevError = 0.0;	// the prior sensor input (used to compute velocity)
    private double m_totalError = 0.0; //the sum of the errors for use in the integral calc
    private double m_tolerance = 0.05;	//the percetage error that is considered on target
    private double m_setpoint = 0.0;
    private double m_error = 0.0;
    private double m_result = 0.0;
    private double m_period = kDefaultPeriod;
    private CANJaguar m_jaguar;
    java.util.Timer m_controlLoop;
    private double m_minI;
    private double m_maxI;
    public double tmpResult;
    
    private class CANTask extends TimerTask {
        
        private CANSpeedControllerDrive m_controller;
        
        public CANTask(CANSpeedControllerDrive controller) {
            if (controller == null) {
                throw new NullPointerException("Given PIDController was null");
            }
            m_controller = controller;
        }

        public void run() {
            m_controller.calculate();
        }    
    }
    
    public CANSpeedControllerDrive (double Kp, double Ki, double Kd, CANJaguar jaguar, double period) {
        if(jaguar == null){
            throw new NullPointerException("Null CANJaguar given");
        }
        m_controlLoop = new java.util.Timer();

        m_P = Kp;
        m_I = Ki;
        m_D = Kd;
        m_jaguar = jaguar;
        m_period = period;

        m_controlLoop.schedule(new CANTask(this), 0L, (long) (m_period * 1000));
    }
    
    public CANSpeedControllerDrive (double Kp, double Ki, double Kd, CANJaguar jaguar) {
        this(Kp, Ki, Kd, jaguar, kDefaultPeriod);
    }
    
    public void free() {
        m_controlLoop.cancel();
        m_controlLoop = null;
    }
    
    private void calculate() {
        boolean enabled;
        CANJaguar jaguar;
        Encoder encoder;
        double input;
        
        synchronized (this) {
            if(m_jaguar == null){
                return;
            }
            enabled = m_enabled;
            jaguar = m_jaguar;
        }
        if (enabled) {
            try{
                input = -m_jaguar.getSpeed();
            }catch(Exception e){
                return;
            }
            double result;

            synchronized (this) {
                m_error = input - m_setpoint;
                m_totalError += m_error;
                if(m_totalError > m_maxI){
                    m_totalError = m_maxI;
                }
                if(m_totalError < m_minI){
                    m_totalError = m_minI;
                }

                result = (m_P * m_error + m_I * m_totalError + m_D * (m_error - m_prevError));
                m_prevError = m_error;
                m_result += result;
                if (result > m_maximumOutput) {
                    result = m_maximumOutput;
                } else if (result < m_minimumOutput) {
                    result = m_minimumOutput;
                }
            }
            try{
                jaguar.setX(result);
            }catch(Exception e){}
            tmpResult = result;
            //System.out.print(result + " --- ");
        }
    }
    
    public synchronized void setPID(double p, double i, double d) {
        m_P = p;
        m_I = i;
        m_D = d;
    }
    
    public synchronized void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            m_minimumInput = maximumInput;
            m_maximumInput = minimumInput;
        }
        m_minimumInput = minimumInput;
        m_maximumInput = maximumInput;
        setSetpoint(m_setpoint);
    }
    
    public synchronized void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            m_minimumOutput = maximumOutput;
            m_maximumOutput = minimumOutput;
        }
        m_minimumOutput = minimumOutput;
        m_maximumOutput = maximumOutput;
    }
    
    public synchronized void setSetpoint(double setpoint) {
        if (m_maximumInput > m_minimumInput) {
            if (setpoint > m_maximumInput) {
                m_setpoint = m_maximumInput;
            } else if (setpoint < m_minimumInput) {
                m_setpoint = m_minimumInput;
            } else {
                m_setpoint = setpoint;
            }
        } else {
            m_setpoint = setpoint;
        }
    }
    
    public synchronized void setTolerance(double percent) {
        m_tolerance = percent;
    }
    
    public synchronized boolean onTarget() {
        return (Math.abs(m_error) < m_tolerance / 100 *
                (m_maximumInput - m_minimumInput));
    }
    
    public synchronized void enable() {
        m_enabled = true;
    }
    
    public synchronized boolean disable() {
        try{
            m_jaguar.setX(0);
        }catch(Exception e){return false;}
        m_enabled = false;
        return true;
    }
    public synchronized boolean isEnable() {
        return m_enabled;
    }
    public synchronized void reset() {
        disable();
        m_prevError = 0;
        m_totalError = 0;
        m_result = 0;
    }
}
