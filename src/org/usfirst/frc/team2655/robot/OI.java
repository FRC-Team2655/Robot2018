package org.usfirst.frc.team2655.robot;

import java.util.Arrays;
import java.util.List;

import org.usfirst.frc.team2655.robot.controllers.FancyAxis;
import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.controllers.LogitechController;
import org.usfirst.frc.team2655.robot.controllers.PS2Controller;
import org.usfirst.frc.team2655.robot.controllers.XboxController;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
	// The joystick(s)
	public static Joystick js0 = new Joystick(0);
	
	// Current controller
	public static IController selectedController = null;
	
	// All possible controllers
	public static List<IController> controllers = Arrays.asList(new IController[] {
			new PS2Controller(),
			new LogitechController(),
			new XboxController()
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
		driveAxis = new FancyAxis(js0, controller.getDriveAxis(), controller.flipAxis(),
				controller.getDeadband(), 
				RobotProperties.MIN_MOVE_POWER, 
				RobotProperties.MID_MOVE_POWER);
		rotateAxis = new FancyAxis(js0, controller.getRotateAxis(), controller.flipAxis(),
				controller.getDeadband(), 
				RobotProperties.MIN_MOVE_POWER,
				RobotProperties.MID_MOVE_POWER);
	}
	
	// The axis and buttons
	public static FancyAxis driveAxis;
	public static FancyAxis rotateAxis;
	//public static Button resetButton; 
}
