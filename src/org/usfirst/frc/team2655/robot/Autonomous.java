package org.usfirst.frc.team2655.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.usfirst.frc.team2655.robot.AutoCommands.AutoCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.DelayCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.DriveCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.RotateCommand;
import org.usfirst.frc.team2655.robot.values.Values;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous { 
	String scriptPath = "/auto-scripts";
	private ArrayList<String> commands = new ArrayList<>();
 	private ArrayList<Double> args = new ArrayList<>();		
	
	//Gets the autonomous scripts for the drive and rotate functions
	public boolean loadScript(String ScriptName) {
		if(!ScriptName.endsWith(".csv"))
			ScriptName += ".csv";
		try {
			BufferedReader reader = new BufferedReader( new FileReader( new File(scriptPath + "/" + ScriptName) ) );
			String currentLine = "";
			
			while((currentLine = reader.readLine()) != null) {
				String[] columns = currentLine.split(",");
				String CMD = columns[0];
				Double arg = null;
				try{arg = Double.parseDouble(columns[1]); } catch(Exception e) {}
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
	
	public AutoCommand getCommand(String commandName) {
		switch(commandName.toUpperCase()) {
		case "DRIVE":
			return new DriveCommand();
		case "ROTATE": 
			return new RotateCommand();
		case "DELAY":
			return new DelayCommand();
		default:
			return null;
		}
	}
	
	String commandName = "";
	int commandIndex = -1;
	Double arg1 = null, arg2 = null;
	AutoCommand command;
	public void feedAuto() {
		if(command == null || command.isDone()) {
			if(commandIndex == -1) {
				// Reset all sensors when auto starts.
				Robot.resetSensors();
				Timer.delay(0.1);
			}
			commandIndex++;
			if(commandIndex < commands.size()) {
				commandName = commands.get(commandIndex);
				arg1 = args.get(commandIndex);
			}else {
				commandName = "DONE";
				arg1 = null;
			}
			command = getCommand(commandName);
			if(command != null)
				command.initCommand(arg1, arg2);
			SmartDashboard.putString(Values.CURRENT_AUTO, commandName);
		}
		
		if(command != null && !command.isDone())
			command.feedCommand();
		
	}

	public void killAuto() {
		if(command != null)
			command.complete();
	}
	
}
