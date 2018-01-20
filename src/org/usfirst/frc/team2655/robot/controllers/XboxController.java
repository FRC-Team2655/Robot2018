package org.usfirst.frc.team2655.robot.controllers;

public class XboxController extends IController {

	@Override
	public String getName() {
		return "Xbox Controler";
	}

	@Override
	public double getDeadband() {
		return .1;
	}

	@Override
	public int getDriveAxis() {
		return 1;
	}

	@Override
	public int getRotateAxis() {
		return 4;
	}

	@Override
	public boolean flipAxis() {
		return false;
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

}
