package org.usfirst.frc.team2655.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;

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
	
	public void runScript() {
		
		for(int i = 0; i < args.size(); i++) {
			
			switch(commands.get(i).toUpperCase()) {
			case "DRIVE":
				drive(args.get(i), null);
				break;
			
			case "ROTATE":
				rotate(args.get(i), null);
				break;
				
			case "DELAY":
				delay(args.get(i), null);
				break;
				
			case "RAISE_LIFTER":
				raiseLifter(args.get(i), null);
				break;
				
			case "OUTPUT":
				output(args.get(i), null);
				break;
				
			}
			
		}
		
	}
	
	//This will eventually drive the robit
	private void drive(Double arg1, Double arg2) {
		double target = Robot.driveBase.getAvgTicks() + (arg1 / 18.8496) * 4096;
    	Robot.driveBase.setBrake(true);
    	while(Math.abs(Robot.driveBase.getAvgTicks()) < target) {
    		
    	}
	}
	//This will eventually rotate the robit
	private void rotate(Double arg1, Double arg2) {
		Robot.driveBase.rotatePID(arg1);
	}
	
	private void delay(Double arg1, Double arg2) {
		Timer.delay(arg1);
	}
	
	private void raiseLifter(Double arg1, Double arg2) {
		//Robot.lifter.liftDistance(1, arg1);
	}
	
	private void output(Double arg1, Double arg2) {
		//Robot.intake.moveIntake((int)(double)arg1);
	}

}
