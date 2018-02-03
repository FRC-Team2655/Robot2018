package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LifterSubsystem extends Subsystem {

	private Encoder encoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	
    public void initDefaultCommand() {}
    
    public void LiftRun(double speed) {
    	Robot.lifterMotor.set(speed);
    } 
    
    public void liftDistance(double speed, double distance) {
    	Math.copySign(speed, distance);
    	int setpoint = encoder.get() + (int)((distance / 9.42) * 1440);
    	Robot.lifterMotor.set(speed);
    	while(Math.abs(encoder.get()) < Math.abs(setpoint)) ;
    	Robot.lifterMotor.set(0);
    }
    
}

