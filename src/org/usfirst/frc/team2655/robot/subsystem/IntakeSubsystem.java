package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.RobotProperties;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem {
		
	public boolean isUnlocked = false;
	
	private double currentMax = 10.0; //Amps
	private int timeout = 20; // Number of 30ms iterations
	private int aboveLimit = 0;
	
	public void moveIntake(double speed) {
		if(Robot.pdp.getCurrent(RobotProperties.LEFT_IN_CHN) > currentMax && speed > 0) {
			aboveLimit++;
		}else if(aboveLimit > 0 && aboveLimit < timeout) {
			aboveLimit--;
		}
		if(speed < 0) {
			aboveLimit = 0;
		}
		SmartDashboard.putNumber("CurrentCounter", aboveLimit);
		if(aboveLimit < timeout) {
			Robot.intakeMotors.set(speed);
		}else {
			Robot.intakeMotors.set(0);
		}
	}
	
	public void setLock(boolean lock) {
		Robot.intakeSolenoid.set(lock ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}
	
}
