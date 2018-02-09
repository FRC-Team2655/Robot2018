package org.usfirst.frc.team2655.robot;

import java.util.Arrays;
import java.util.List;

import org.usfirst.frc.team2655.robot.controllers.FancyAxis;
import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.controllers.PS4Controller;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
	// The joystick(s)
	public static Joystick js0 = new Joystick(0);
	
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
	 * @param js The joystick to associate the controller with
	 */
	public static void selectController(IController controller) {
		if(controllers.contains(controller))
			selectedController = controller;
		else
			return;
		// Setup all axis and buttons here
		driveAxis = new FancyAxis(js0, controller, controller.getDriveAxis(), controller.flipAxis(),
				controller.getDeadband(), 
				RobotProperties.MIN_MOVE_POWER, 
				RobotProperties.MID_MOVE_POWER);
		rotateAxis = new FancyAxis(js0, controller, controller.getRotateAxis(), controller.flipAxis(),
				controller.getDeadband(), 
				RobotProperties.MIN_MOVE_POWER,
				RobotProperties.MID_MOVE_POWER);
		rightTankAxis = new FancyAxis(js0, controller, controller.getRightTankAxis(), controller.flipAxis(),
				controller.getDeadband(),
				RobotProperties.MIN_MOVE_POWER,
				RobotProperties.MID_MOVE_POWER);
		//IntakeInButton = new Button(js0, controller.getIntakeInButton());
		//IntakeOutButton = new Button(js0, controller.getIntakeOutButton());
		//IntakeLockButton = new Button(js0, controller.getIntakeLockButton());
	}
	
	// The axis and buttons
	public static FancyAxis driveAxis;
	public static FancyAxis rotateAxis;
	public static FancyAxis rightTankAxis;
	
	//public static Button resetButton;
	//public static Button IntakeInButton;
	//public static Button IntakeOutButton;
	//public static Button IntakeLockButton;
}
