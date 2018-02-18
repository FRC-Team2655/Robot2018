package org.usfirst.frc.team2655.robot.controllers;

public abstract class IController {
	
	// Should left/right and up/down be switched on the axis
	public abstract boolean flipAxis();
	
	// The name of the controller
	public abstract String getName();
	
	public abstract double adjustAxis(double original);
	
	// The deadband for the controller's axis
	public abstract double getDeadband();
	
	// Create a function to get the id for each axis's function
	public abstract int getDriveAxis();
	public abstract int getRotateAxis();
	public abstract int getRightTankAxis();
	
	// Create a function to get the id for each button's action
	public abstract int getResetButton();
	public abstract int getIntakeInButton();
	public abstract int getIntakeOutButton();
	public abstract int getIntakeReleaseButton();
	
}
