package org.usfirst.frc.team2655.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.usfirst.frc.team2655.robot.values.Values;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous { 
	
	public Autonomous() {
		System.out.println("Auto");
	}
	String scriptPath = "C:\\Users\\Platypi\\Desktop\\AutoScripts";
	private ArrayList<String> commands = new ArrayList<>();
 	private ArrayList<Double> args = new ArrayList<>();		
	
	//Gets the autonomous scripts for the drive and rotate functions
	public boolean loadScript(String ScriptName) {
		
		try {
			BufferedReader reader = new BufferedReader( new FileReader( new File(scriptPath + "\\" + ScriptName) ) );
			String currentLine = "";
			
			while((currentLine = reader.readLine()) != null) {
				String[] columns = currentLine.split(",");
				String CMD = columns[0];
				double arg = Double.parseDouble(columns[1]);
				commands.add(CMD);
				args.add(arg);
			}
			reader.close();
			return true;
			
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void putScript(ArrayList<String> commands, ArrayList<Double> args) {
		this.commands = commands;
		this.args = args;
	}
	
	String command = "";
	int commandIndex = -1;
	Double arg1 = null, arg2 = null;
	boolean commandDone = true;
	
	public void feedAuto() {
		
		if(commandDone) {
			commandIndex++;
			if(commandIndex < commands.size()) {
				command = commands.get(commandIndex);
				arg1 = args.get(commandIndex);
			}else {
				command = "DONE";
				arg1 = null;
			}
			SmartDashboard.putString(Values.CURRENT_AUTO, command);
		}
		
		switch(command.toUpperCase()) {
		case "DRIVE":
			commandDone = drive(); break;
		case "ROTATE":
			commandDone = rotate(); break;
		case "DELAY":
			commandDone = delay(); break;
		case "DONE":
			break;
		default:
			commandDone = true; // Unknown command = skip
			break;
		}
	}
	
	
	double driveTarget = -1;
	//This will eventually drive the robit
	private boolean drive() {
		if(driveTarget == -1) {
			driveTarget = Robot.driveBase.getAvgTicks() + (arg1 / 18.8496) * 1440;
	    	Robot.driveBase.setBrake(true);
		}
    	if(Math.abs(Robot.driveBase.getAvgTicks()) < Math.abs(driveTarget)) {
    		Robot.driveBase.drive(Math.copySign(0.5, arg1), 0);
    		return false;
    	}else {
    		driveTarget = -1;
	    	Robot.driveBase.drive(0, 0);
	    	Timer.delay(0.1); // Let brake mode do its thing
	    	Robot.driveBase.setBrake(false);
	    	return true;
    	}
	}
	//This will eventually rotate the robit
	private boolean rotate() {
		Robot.driveBase.rotatePID(arg1);
		return !Robot.driveBase.rotatePIDController.isEnabled();
	}
	
	private long delayStart = -1;
	private boolean delay() {
		if(delayStart == -1) {
			delayStart = System.currentTimeMillis();
		}
		if(System.currentTimeMillis() - delayStart >= arg1) {
			delayStart = -1;
			return true;
		}else {
			return false;
		}
	}

}
