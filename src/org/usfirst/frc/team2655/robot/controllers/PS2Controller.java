package org.usfirst.frc.team2655.robot.controllers;

public class PS2Controller extends IController {

	
	
	@Override
	public double getDeadband() {
		return .2;
	}

	@Override
	public String getName() {
		return "PS2 Controller";
	}
	
	@Override
	public int getDriveAxis() {
		return 1;
	}

	@Override
	public int getRotateAxis() {
		return 3;
	}

	@Override
	public boolean flipAxis() {
		return false;
	}

}
