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
		return 6;
	}

	@Override
	public int getIntakeOutButton() {
		return 5;
	}

	@Override
	public int getIntakeReleaseButton() {
		return 8;
	}

	@Override
	public int getRightTankAxis() {
		return 5;
	}

	@Override
	public double adjustAxis(double original) {
		return original;
	}

	@Override
	public int getResetButton() {
		return 10;
	}

	@Override
	public int getUpAxis() {
		return 4;
	}

	@Override
	public int getDownAxis() {
		return 3;
	}

	@Override
	public int getLifterDownButton() {
		return 3;
	}

	@Override
	public int getLifterUpButton() {
		return 2;
	}

	@Override
	public int getVictorySpinButton() {
		return 14;
	}

}
