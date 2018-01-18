package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.RobotProperties;

import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;


public class DriveBaseSubsystem extends PIDSubsystem {
		
	/**
	 * Initialize LiveWindow PIDs
	 */
	public DriveBaseSubsystem() {
		super("drive", 1, 0, 0, 0);
		this.setAbsoluteTolerance(140);
		getPIDController().setContinuous(false);
		LiveWindow.add(this.getPIDController());
	}
	
    public void initDefaultCommand() {}
    
    /**
     * Drive the robot
     * @param power Speed to drive
     * @param rotation Power to rotate with	
     */
    public void drive(double power, double rotation) {
    	Robot.robotDrive.arcadeDrive(power, rotation, false);
    }
    
    /**
     * Drive until a distance at a certain speed in a straight line
     * @param speed The speed to drive at (-1 to 1)
     * @param distance The distance to drive until (inches)
     */
    public void driveDistance(double speed, double distance) {
    	if (distance > 0) {
    		speed = Math.abs(speed);
    	} else {
    		speed = -1 * Math.abs(speed);
    	}
    	double target = getAvgTicks() + (distance / 18.7 * 1440); // Distance in ticks
    	double ticks = getAvgTicks();
    	    	
    	while(Math.abs(ticks) < Math.abs(target)) {
    		drive(speed, 0);
    		ticks = getAvgTicks();
    	}
    	drive(0, 0);
    }
    
    /**
     * Average the values of all four encoders
     * @return The average number of ticks
     */
    private int getAvgTicks() {
    	int left = Robot.leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID) * -1; 
    	int right = Robot.rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID); 
    	int avg = (left + right) / 2;
    	return avg;
    	
    }
    
    protected double returnPIDInput() {
    	double val = getAvgTicks();
    	return val;
    }

    protected void usePIDOutput(double output) {
    	drive(output, 0);
    }
    
    
}


