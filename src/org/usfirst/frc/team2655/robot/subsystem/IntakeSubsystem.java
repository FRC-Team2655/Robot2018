package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class IntakeSubsystem {
		
	public boolean isUnlocked = false;
	
	public boolean isSwitchPressed() {
		return !Robot.intakeSwitch.get();
	}
	
	public void moveIntake(double speed) {
		if((!isSwitchPressed() && speed > 0) || speed < 0) {
			Robot.intakeMotors.set(speed);
		}else {
			Robot.intakeMotors.set(0);
		}
	}
	
	public void setLock(boolean lock) {
		Robot.intakeSolenoid.set(lock ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}
	
}
