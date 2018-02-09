package org.usfirst.frc.team2655.robot.controllers;

public class PS4Controller extends IController {

	@Override
	public boolean flipAxis() {
		return false;
	}

	@Override
	public String getName() {
		return "PS4 Controller";
	}

	@Override
	public double getDeadband() {
		return 0.1;
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
	public int getIntakeInButton() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntakeOutButton() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntakeLockButton() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRightTankAxis() {
		return 5;
	}

	@Override
	public double adjustAxis(double original) {
		return original;
	}

}
