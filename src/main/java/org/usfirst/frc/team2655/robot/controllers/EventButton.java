package org.usfirst.frc.team2655.robot.controllers;

import edu.wpi.first.wpilibj.Joystick;

public class EventButton {
	Joystick js;
	int buttonNum;
	public EventButton(Joystick js, int buttonNum) {
		this.js = js;
		this.buttonNum = buttonNum;
	}
	
}
