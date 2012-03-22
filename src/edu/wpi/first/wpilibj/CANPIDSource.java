/*
Wrapper class for using a encoder or potentiometer through a CANJaguar in a PIDColtroller
 */
package edu.wpi.first.wpilibj;

/**
 *
 * @author Zac
 */
public class CANPIDSource implements PIDSource {
    CANJaguar m_jaguar;
    private PIDSourceParameter m_pidSource;
    double lastVal = 0;
    
    public static class PIDSourceParameter {
        public final int value;
        static final int kPosition_val = 0;
        static final int kRate_val = 1;
        public static final PIDSourceParameter kPosition = new PIDSourceParameter(kPosition_val);
        public static final PIDSourceParameter kRate = new PIDSourceParameter(kRate_val);

        private PIDSourceParameter(int value) {
            this.value = value;
        }
    }
    
    public CANPIDSource(CANJaguar jag, PIDSourceParameter PIDType){
        m_pidSource = PIDType;
        m_jaguar = jag;
    }
    
    public double pidGet() {
        switch (m_pidSource.value) {
        case PIDSourceParameter.kPosition_val:
            try{
                lastVal = m_jaguar.getPosition();
            }
            catch(Exception e){}
            return lastVal;
        case PIDSourceParameter.kRate_val:
            try{
                lastVal = m_jaguar.getSpeed();
            }
            catch(Exception e){}
            return lastVal;
        default:
            return 0.0;
        }
    }
}
