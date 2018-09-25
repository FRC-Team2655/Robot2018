package org.usfirst.frc.team2655.robot;

import java.util.Arrays;
import java.util.List;

import org.usfirst.frc.team2655.robot.controllers.Button;
import org.usfirst.frc.team2655.robot.controllers.FancyAxis;
import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.controllers.PS4Controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {
	// The joystick(s)
	public static Joystick js0 = new Joystick(0);
	public static Joystick js1 = new Joystick(1);
	// Current controller
	public static IController selectedController = null;
	
	// All possible controllers
	public static List<IController> controllers = Arrays.asList(new IController[] {
			new PS4Controller(),
			
			//new PS2Controller(),
			//new NewLogitech(),
			//new LogitechController(),
			//new XboxController()
	});
	
	/**
	 * 	Select a controller and configure the class for the controller;
	 * @param controller The controller to use
	 */
	public static void selectController(Joystick js, IController controller) {
		if(controllers.contains(controller))
			selectedController = controller;
		else
			return;
		// Setup all axis and buttons here
		driveAxis = new FancyAxis(js, controller, controller.getDriveAxis(), controller.flipAxis(),
				controller.getDeadband(), 
				RobotProperties.MIN_MOVE_POWER, 
				RobotProperties.MID_MOVE_POWER);
		rotateAxis = new FancyAxis(js, controller, controller.getRotateAxis(), controller.flipAxis(),
				controller.getDeadband(), 
				RobotProperties.MIN_MOVE_POWER,
				RobotProperties.MID_MOVE_POWER);
		rightTankAxis = new FancyAxis(js, controller, controller.getRightTankAxis(), controller.flipAxis(),
				controller.getDeadband(),
				RobotProperties.MIN_MOVE_POWER,
				RobotProperties.MID_MOVE_POWER);
		lifterUpAxis = new FancyAxis(js, controller, controller.getUpAxis(), controller.flipAxis(), 
				0,
				0,
				0.5);
		lifterDownAxis = new FancyAxis(js, controller, controller.getDownAxis(), controller.flipAxis(),
				0,
				0,
				0.5);
		intakeReleaseButton = new Button(js, controller.getIntakeReleaseButton());
		intakeInButton = new Button(js, controller.getIntakeInButton());
		intakeOutButton = new Button(js, controller.getIntakeOutButton());
		resetButton = new Button(js, controller.getResetButton());
		autoDownButton = new Button(js, controller.getLifterDownButton());
		autoUpButton = new Button(js, controller.getLifterUpButton());
		victorySpinButton = new Button(js, controller.getVictorySpinButton());
		selectedJs = js;
	}
	
	// The axis and buttons
	public static FancyAxis driveAxis;
	public static FancyAxis rotateAxis;
	public static FancyAxis rightTankAxis;
	public static FancyAxis lifterUpAxis;
	public static FancyAxis lifterDownAxis;
	
	public static Button resetButton;
	public static Button intakeReleaseButton;
	public static Button intakeInButton;
	public static Button intakeOutButton;
	public static Button autoDownButton;
	public static Button autoUpButton;
	public static Button victorySpinButton;
	
	public static Joystick selectedJs;
}
