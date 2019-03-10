package frc.robot.controllers;

import edu.wpi.first.wpilibj.Joystick;

public class EventButton {
	Joystick js;
	int buttonNum;
	public EventButton(Joystick js, int buttonNum) {
		this.js = js;
		this.buttonNum = buttonNum;
	}
	
}
