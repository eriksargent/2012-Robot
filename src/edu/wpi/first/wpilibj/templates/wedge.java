/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Relay;


/**
 *
 * @author esthepiking
 */
public class wedge{
    private Thread thread;
    public boolean up = false;
    public boolean down = false;
    public boolean isUp;
    
    public Relay relay;
    public Servo servo1, servo2;
    
    private class wedgeThread extends Thread{
        
        wedge wedge;
        
        wedgeThread(wedge w){
            wedge = w;
        }
        
        public void run(){
            
            while(true){
                if(down){
                    isUp = false;
                    relay.set(Relay.Value.kForward);
                    try{
                        Thread.sleep(300);
                    }catch(InterruptedException e){}
                    relay.set(Relay.Value.kOff);
                    servo1.set(0);
                    servo2.set(1);
                    try{
                        Thread.sleep(700);
                    }catch(InterruptedException e){}
                    servo1.set(.5);
                    servo2.set(.5);
                    down = false;
                }
                else if(up){
                    isUp = true;
                    servo1.set(1);
                    servo2.set(0);
                    try{
                        Thread.sleep(700);
                    }catch(InterruptedException e){}
                    servo1.set(.5);
                    servo2.set(.5);
                    relay.set(Relay.Value.kReverse);
                    try{
                        Thread.sleep(350);
                    }catch(InterruptedException e){}
                    relay.set(Relay.Value.kOff);
                    up = false;
                }
                else{
                    try{
                        Thread.sleep(10);
                    }catch(InterruptedException e){}
                }
                    
            }
        }
    }
    public wedge(int r, int s1, int s2){
        relay = new Relay(r);
        servo1 = new Servo(s1);
        servo2 = new Servo(s2);
        thread = new wedgeThread(this);
        thread.start();
    }
    
    public void moveUp(){
        if (isUp==false)
            up = true;
    }
    
    public void moveDown(){
        if(isUp)
            down = true;
    }
}
