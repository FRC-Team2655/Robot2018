package frc.robot.subsystem;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

public class NewLifter extends Subsystem{
	
	@Override
	protected void initDefaultCommand() {}
	
	public void setLifter(boolean isUp) {
		Robot.lifterSolenoid.set(isUp ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}
	
}
