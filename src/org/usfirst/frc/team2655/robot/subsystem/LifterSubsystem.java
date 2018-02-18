package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.RobotProperties;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LifterSubsystem extends Subsystem {
	
	// Number of encoder ticks before limiting speed
	private double lowerThreshold = 400;
	private double upperThreshold = 6500;
	
    public void initDefaultCommand() {}
    
    public boolean isTopPressed() {
    	return !Robot.lifterTopSwitch.get();
    }
    public boolean isBottomPressed() {
    	return !Robot.lifterBottomSwitch.get();
    }
    
    // MAKE SURE (+) is UP
    public void lift(double speed) {
    	if(false) {
    	speed = adjustSpeed(speed);
    	if((speed > 0 && !isTopPressed()) || (speed < 0 && !isBottomPressed())) {
    		Robot.lifterMotor.set(speed);
    	}else if(!isBottomPressed()) {
    		Robot.lifterMotor.set(0.15); // This speed holds the current height
    	}else {
    		Robot.lifterMotor.set(0);
    	}
    	}
    } 
    
    public double adjustSpeed(double speed) {
    	double encoderPos = Robot.lifterMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID);
    	if(encoderPos > upperThreshold && speed > 0) {
    		return Math.copySign(Math.min(0.45, Math.abs(speed)), speed);
    	}
    	if(encoderPos < lowerThreshold && speed < 0) {
    		return Math.copySign(Math.min(0.01, Math.abs(speed)), speed);
    	}
    	return speed;
    }
    
    public void liftDistance(double speed, double distance) {
    	
    }
    
}

