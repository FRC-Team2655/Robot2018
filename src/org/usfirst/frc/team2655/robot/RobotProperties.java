package org.usfirst.frc.team2655.robot;

public class RobotProperties {
	public static final double WHEEL_DIAMETER = 5; // Inches
	public static final double MIN_MOVE_POWER = 0.0; // Power needed to start moving the robot
	public static final double MID_MOVE_POWER = 0.5; // The medium power for the cubic funtion of an axis
	
	public static final int LEFT_IN_CHN = 10;
	public static final int RIGHT_IN_CHN = 11;
	
	// Talon SRX config

	/* Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops.  
	 * For now we just want the primary one.
	 */
	public static final int TALON_PID_ID = 0;

	/*
	 * set to zero to skip waiting for confirmation, set to nonzero to wait
	 * and report to DS if action fails.
	 */
	public static final int TALON_TIMEOUT = 0; // Do not timeout or wait for errors
}
