package org.usfirst.frc.team2655.robot.controllers;

public class LogitechController extends IController {

	@Override
	public String getName() {
		return "Logitech Controller";
	}

	@Override
	public double getDeadband() {
		return .15;
	}

	@Override
	public int getDriveAxis() {
		return 1;
	}

	@Override
	public int getRotateAxis() {
		return 2;
	}

	@Override
	public boolean flipAxis() {
		return false;
	}

}
