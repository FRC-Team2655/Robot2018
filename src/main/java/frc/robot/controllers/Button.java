package frc.robot.controllers;

import edu.wpi.first.wpilibj.Joystick;

public class Button {
	
	private Joystick joystick;
	private int button;
	
	public Button(Joystick joystick, int button) {
		this.joystick = joystick;
		this.button = button;
	}
	
	public boolean isPressed() {
		return joystick.getRawButton(button);
	}
	
}
