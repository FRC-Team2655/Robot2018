package org.usfirst.frc.team2655.robot.controllers;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import edu.wpi.first.wpilibj.Joystick;

public class FancyAxis {
	
	private Joystick joystick;
	private IController controller;
	private int axis;
	
	private boolean flipAxis;
	
	private double deadband = 0;
	private double minPower = 0;
	private double midPower = 0.5;
		
	private double[] coefficents;
	
	public FancyAxis(Joystick joystick, IController controller, int axis, boolean flipAxis) {
		this.joystick = joystick;
		this.controller = controller;
		this.axis = axis;
		this.flipAxis = flipAxis;
		updateRegression();
	}
	
	

	public FancyAxis(Joystick joystick, IController controller, int axis, boolean flipAxis, double deadband, double minPower, double midPower) {
		this.joystick = joystick;
		this.controller = controller;
		this.axis = axis;
		this.flipAxis = flipAxis;
		this.deadband = deadband;
		this.minPower = minPower;
		this.midPower = midPower;
		updateRegression();
	}



	private void updateRegression() {
		// This creates a cubic regression using APache's Math library
		double midDeadband = (1 - deadband) / 2 + deadband; // Middle of deadband and 1
		
		// These are coordinates used for the linear regression
		double[] x = new double[] { deadband, midDeadband, midDeadband + .01, 1};
		double[] y = new double[] {minPower, midPower, midPower, 1};

		// Setup the points
		WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < x.length; i++){
            obs.add(x[i], y[i]);
        }
        // Generate the curve and get the coefficents
        // y = ax^3 + bx^2 + cx + d
        // array is [d, c, b, a]
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);
        coefficents = fitter.fit(obs.toList());
	}
	
	public double getDeadband() {
		return deadband;
	}


	public void setDeadband(double deadband) {
		this.deadband = deadband;
		updateRegression();
	}


	public double getMinPower() {
		return minPower;
	}


	public void setMinPower(double minPower) {
		this.minPower = minPower;
		updateRegression();
	}


	public double getMidPower() {
		return midPower;
	}


	public void setMidPower(double midPower) {
		this.midPower = midPower;
		updateRegression();
	}
	
	
	
	public Joystick getJoystick() {
		return joystick;
	}



	public void setJoystick(Joystick joystick) {
		this.joystick = joystick;
	}



	public int getAxis() {
		return axis;
	}



	public void setAxis(int axis) {
		this.axis = axis;
	}



	/**
	 * Get the with a using the cubic model
	 * @return The value if it is outside the deadband otherwise zero.
	 */
	public double getValue() {
		double value = controller.adjustAxis(joystick.getRawAxis(axis));
		if(flipAxis)
			value *= -1;
		if(Math.abs(value) < deadband) return 0; // Make sure we are within the deadband	        
		double x = Math.abs(value);
		// Calculate the value using y = ax^3 + bx^2 + cx + d
		double pwr = coefficents[3] * Math.pow(x, 3) + coefficents[2] * Math.pow(x, 2) + coefficents[1] * x + coefficents[0];
		if(value < 0)
			pwr *= -1; // Match the sign
		return pwr;
	}
	
	/**
	 * Get the value using the dead zone but without the cubic model
	 * @return The value if it is outside the deadband otherwise zero.
	 */
	public double getValueLinear() {
		double value = controller.adjustAxis(joystick.getRawAxis(axis));
		if(flipAxis)
			value *= -1;
	    if(Math.abs(value) < deadband) return 0;
	    else return value;
	}
	
}
