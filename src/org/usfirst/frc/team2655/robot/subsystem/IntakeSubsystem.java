package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.values.Values;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem {
		
	public boolean isUnlocked = false;
	
	public boolean isSwitchPressed() {
		return !Robot.intakeSwitch.get();
	}
	
	public void moveIntake(double speed) {
		boolean cond = !isSwitchPressed() || SmartDashboard.getBoolean(Values.INTAKE_OVERRIDE, false);
		if((cond && speed > 0) || speed < 0) {
			Robot.intakeMotors.set(speed);
		}else {
			Robot.intakeMotors.set(0);
		}
	}
	
	public void setLock(boolean lock) {
		Robot.intakeSolenoid.set(lock ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}
	
}
