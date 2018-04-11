package org.usfirst.frc.team2655.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.usfirst.frc.team2655.robot.AutoCommands.AutoCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.DelayCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.DriveCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.IntakeCloseCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.IntakeOffCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.IntakeOnCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.IntakeOpenCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.LowerLifterCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.OutputCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.PathCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.RaiseLifterCommand;
import org.usfirst.frc.team2655.robot.AutoCommands.RotateCommand;
import org.usfirst.frc.team2655.robot.values.Values;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class handles autonomous mode based on a set of commands with arguments.
 * @author Marcus Behel
 *
 */
public class Autonomous { 
	public static final String scriptPath = "/auto-scripts";
	public static final String pathsPath = scriptPath + "/paths/";
	private ArrayList<String> commands = new ArrayList<>();
 	private ArrayList<Object> args = new ArrayList<>();		
	
	/**
	 * Load a set of commands and arguments from a CSV file on the roboRIO
	 * @param ScriptName The script to load
	 * @return Was the script successfully loaded
	 */
	public boolean loadScript(String ScriptName) {
		if(!ScriptName.endsWith(".csv"))
			ScriptName += ".csv";
		try {
			BufferedReader reader = new BufferedReader( new FileReader( new File(scriptPath + "/" + ScriptName) ) );
			String currentLine = "";
			
			while((currentLine = reader.readLine()) != null) {
				String[] columns = currentLine.split(",");
				String CMD = columns[0];
				Object arg = columns[1];
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
	
	public void addDelay(Double time) {
		commands.add(0, "DELAY");
		args.add(0, time);
	}
	
	/**
	 * Manually set commands and arguments for auto
	 * @param commands The commands
	 * @param args The arguments
	 */
	public void putScript(String[] commands, Object[] args) {
		this.commands = new ArrayList<String>(Arrays.asList(commands));
		this.args = new ArrayList<Object>(Arrays.asList(args));
	}
	
	/**
	 * Get a AutoCommand Object ({@link AutoCommands.java}) for the given command
	 * @param commandName The command
	 * @return An object or null
	 */
	private AutoCommand getCommand(String commandName) {
		switch(commandName.toUpperCase()) {
		case "DRIVE":
			return new DriveCommand();
		case "ROTATE": 
			return new RotateCommand();
		case "DELAY":
			return new DelayCommand();
		case "OUTPUT":
			return new OutputCommand();
		case "RAISE_LIFTER":
			return new RaiseLifterCommand();
		case "LOWER_LIFTER":
			return new LowerLifterCommand();
		case "INTAKE_ON":
			return new IntakeOnCommand();
		case "INTAKE_OFF":
			return new IntakeOffCommand();
		case "INTAKE_OPEN":
			return new IntakeOpenCommand();
		case "INTAKE_CLOSE":
			return new IntakeCloseCommand();
		case "PATH":
			return new PathCommand();
		default:
			return null;
		}
	}
	
	String commandName = "";
	int commandIndex = -1;
	Object arg1 = null, arg2 = null;
	AutoCommand command;
	
	/**
	 * Check if the current command is done and if it is move on to the next one.
	 * This should be called in autonomousPeriodic (or every 30ms in auto mode)
	 */
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

	/**
	 * Have the current command complete and stop trying to control systems.
	 * This should be run in disabledInit (or when auto ends)
	 */
	public void killAuto() {
		if(command != null)
			command.complete();
	}
	
}
