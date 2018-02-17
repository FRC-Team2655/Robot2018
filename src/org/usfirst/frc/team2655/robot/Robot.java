package org.usfirst.frc.team2655.robot;

import java.util.ArrayList;
import java.util.Arrays;

import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.subsystem.DriveBaseSubsystem;
import org.usfirst.frc.team2655.robot.values.Values;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	
	// Our motor controllers. These will be initialized (created) in robotInit
	public static WPI_TalonSRX leftMotor = new WPI_TalonSRX(1);
	public static WPI_TalonSRX leftSlave1 = new WPI_TalonSRX(2);
    public static WPI_TalonSRX leftSlave2 = new WPI_TalonSRX(3);
    public static WPI_TalonSRX rightMotor = new WPI_TalonSRX(5);
	public static WPI_TalonSRX rightSlave1 = new WPI_TalonSRX(6);
    public static WPI_TalonSRX rightSlave2 = new WPI_TalonSRX(4);
    
	public static WPI_TalonSRX[] motors = new WPI_TalonSRX[] {leftMotor, leftSlave1, leftSlave2, rightMotor, rightSlave1, rightSlave2};
	
	// The Gyro
	public static ADIS16448_IMU imu;
		
	// Robot Subsystems
	public static DriveBaseSubsystem driveBase = new DriveBaseSubsystem();
	
	// Controller Selector
	public static SendableChooser<IController> controllerSelect = new SendableChooser<IController>();
				
	// Auto Selectors
	public static SendableChooser<Integer> autoScaleOption = new SendableChooser<Integer>();
	public static SendableChooser<Integer> autoCrossOption = new SendableChooser<Integer>();
	public static SendableChooser<Integer> autoPositionOption = new SendableChooser<Integer>();
	
	public static Autonomous a;
	
	/**
	 * Setup the motor controllers and the drive object
	 */
	@Override
	public void robotInit() {
		
		// Allow the driver to selectr a controller
		controllerSelect.addDefault(OI.controllers.get(0).getName(), OI.controllers.get(0));
		for(int i = 1; i < OI.controllers.size(); i++) {
			IController c = OI.controllers.get(i);
			controllerSelect.addObject(c.getName(), c);
		}
		OI.selectController(OI.controllers.get(0));
		
		// Setup IMU and Motors
		imu = new ADIS16448_IMU();
				
		leftSlave1.follow(leftMotor);
		leftSlave2.follow(leftMotor);
		
		rightSlave1.follow(rightMotor);
		rightSlave2.follow(rightMotor);
		
		// Do not allow LiveWindow to control the talons. It breaks follow mode
		LiveWindow.remove(leftMotor);
		LiveWindow.remove(rightMotor);
		LiveWindow.remove(leftSlave1);
		LiveWindow.remove(leftSlave2);
		LiveWindow.remove(rightSlave1);
		LiveWindow.remove(rightSlave2);
		
		// Setup the motor controllers
		for(WPI_TalonSRX m : motors) {
			m.setInverted(true);
		}
		
		// Setup encoders. (-) direction is forward (b/c up on JS is negative)
		leftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		leftMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		leftMotor.setSensorPhase(true);
		
		/*leftMotor.config_kP(RobotProperties.TALON_PID_ID, 0.2, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kI(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kD(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kF(RobotProperties.TALON_PID_ID, 1.12, RobotProperties.TALON_TIMEOUT);*/
		
		imu.reset(); // Make initial direction 0
		
		// Add stuff to the dashboard
		SmartDashboard.putBoolean(Values.DRIVE_CUBIC, true);
		SmartDashboard.putBoolean(Values.ROTATE_CUBIC, false);
		SmartDashboard.putData("Select Controller:", controllerSelect);
		SmartDashboard.putString(Values.CURRENT_AUTO, "");
		SmartDashboard.putBoolean(Values.VELOCITY_LOOP, false);
		
		// Auto Options
		autoPositionOption.addDefault("1 - Left", 1);
		autoPositionOption.addObject("2 - Center", 2);
		autoPositionOption.addObject("3 - Right", 3);
		
		autoScaleOption.addDefault("Place on scale", Values.AUTO_SCALE_PLACE);
		autoScaleOption.addObject("Only drive to scale", Values.AUTO_SCALE_MOVE);
		
		autoCrossOption.addDefault("Only cross baseline", Values.AUTO_CROSS_DRIVE);
		autoCrossOption.addObject("Cross to far side of scale", Values.AUTO_CROSS_CROSS);
		
		SmartDashboard.putData(Values.AUTO_POSITION_CHOOSER, autoPositionOption);
		SmartDashboard.putNumber(Values.AUTO_DELAY, 0);
		SmartDashboard.putBoolean(Values.AUTO_TRY_SWITCH, true);
		SmartDashboard.putData(Values.AUTO_SCALE_CHOOSER, autoScaleOption);
		SmartDashboard.putData(Values.AUTO_CROSS_CHOOSER, autoCrossOption);	
		
		//SmartDashboard.putData("LeftClosedLoop", new TalonPIDDisplay(leftMotor, 0, 0, 0, 1.12));
		//SmartDashboard.putData("RightClosedLoop", new TalonPIDDisplay(rightMotor, 0, 0, 0, 1.12));
	}
	
	@Override 
	public void disabledInit(){
		// Make sure auto is dead and not trying to control anything
		if(a != null)
			a.killAuto();
	}
	
	/**
	 * Get the data from the field
	 * @return The data
	 */
	public String getFieldData() {
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		// Retry after 100ms if there is no data at first
		if(gameData == null ||gameData.length() == 0) {
			Timer.delay(0.1);
			gameData = DriverStation.getInstance().getGameSpecificMessage();
		}
		return gameData;
	}
	
	@Override
	public void autonomousInit() {
		
		// Get data from the dashboard
		int position = autoPositionOption.getSelected();
		boolean trySwitch = SmartDashboard.getBoolean(Values.AUTO_TRY_SWITCH, true);
		boolean driveCross = autoCrossOption.getSelected() == Values.AUTO_CROSS_CROSS;
		boolean scalePlace = autoScaleOption.getSelected() == Values.AUTO_SCALE_PLACE;
		// The resulting script's name
		String defaultScript = "";
		// Set a default for each position.
		switch(position) {
    	case 1:
    		defaultScript = "1D"; break;
    	case 2:
    		defaultScript = "2L"; break; // If in the middle need to avoid power cube zone
    	case 3:
    		defaultScript = "3D"; break;
    	}
		String output = defaultScript;
		
		String gameData = getFieldData();
		
		// If there is data use it to determine which script to run in auto
		// GameData is in format [OURSWITCH][SCALE][OPPONENTSWITCH]
		// Example: "LRL" means that the left of both switches is ours and the right scale is ours
        if(gameData.length() > 0) {
		
			// Which side of the switch/scale is ours
			boolean switchLeft = gameData.charAt(0) == 'L'; // Check if our side of our switch is left
			boolean scaleLeft = gameData.charAt(1) == 'L'; // Check is our side of the scale is left
		
			
			switch(position) {
			case 1:
				if(trySwitch == true && switchLeft == true) {
					output = "1A";
				}else if(scaleLeft == true) {
					if(scalePlace == true) {
						output = "1B";
					}
					if(scalePlace == false) {
				 		output = "1B-Drive";
				 	}
				}else {
					
					if(driveCross == true) {
						output = "1C";
					}
					if(driveCross == false) {
						output = "1D";
					}
				}
				break;
			case 2:
				if(switchLeft == true) {
					output = "2L";			
				}
				if(switchLeft == false) {
					output = "2R";
				}
				break;
			case 3:
				if(trySwitch == true && switchLeft == false) {
					output = "3A";
				}else if(scaleLeft == false) {
					if(scalePlace == true) {
						output = "3B";
					}
					if(scalePlace == false) {
				 		output = "3B-Drive";
				 	}
				}else {
					
					if(driveCross == true) {
						output = "3C";
					}
					if(driveCross == false) {
						output = "3D";
					}
				}
				break;
			}
        }	
		
        // Setup auto and load the script.
		a = new Autonomous();
		boolean success = a.loadScript(output);
		if(!success) {
			// If the script failed to load try using the default script for the position
			success = a.loadScript(defaultScript);
			if(!success) {
				// If the default fails to load just drive (2/3 chance cross baseline 1/3 change run into power cube zone)
				a.putScript(new ArrayList<String>(Arrays.asList(new String[] {"DRIVE"})), 
						new ArrayList<Double>(Arrays.asList(new Double[] {196.0})));
			}
		}
	}

	@Override
	public void autonomousPeriodic() {
		a.feedAuto();
	}

	/**
	 * Set the IMU to 0
	 * Set encoders to 0
	 */
	public static void resetSensors() {
		imu.reset();
		resetEncoders();
	}
	
	/**
	 * Set encoders to 0
	 */
	public static void resetEncoders() {
		leftMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
	}
	
	@Override
	public void robotPeriodic() {
		// Update controller choice
		IController selected = controllerSelect.getSelected();
		if(selected != OI.selectedController) {
			OI.selectController(selected);
		}
		// Update dashboard values as needed
		SmartDashboard.putNumber(Values.GYRO, imu.getAngleZ());
		SmartDashboard.putNumber(Values.LEFT_ENC, leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
		SmartDashboard.putNumber(Values.RIGHT_ENC, rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
		//SmartDashboard.putNumber("LeftVelocity", leftMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
		//SmartDashboard.putNumber("RightVelocity", rightMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
	}
	
	/**
	 * Called every 20ms during the driver controlled period
	 */
	@Override
	public void teleopPeriodic() {
		// NO PIDs (just in case they were still alive from auto)
		Robot.driveBase.rotatePIDController.disable();
		Robot.driveBase.angleCorrectionPIDController.disable();
		boolean driveCubic = SmartDashboard.getBoolean(Values.DRIVE_CUBIC, true);
		boolean rotateCubic = SmartDashboard.getBoolean(Values.ROTATE_CUBIC, true);
		
		double power =  driveCubic ? OI.driveAxis.getValue() : OI.driveAxis.getValueLinear();
		double rotation = -0.3 * (rotateCubic ? OI.rotateAxis.getValue() : OI.rotateAxis.getValueLinear());
		
		if(OI.js0.getRawButtonPressed(2)) {
			resetSensors();
		}
		
		driveBase.drive(power, rotation);
	}

	@Override
	public void testPeriodic() {
		if(OI.js0.getRawButtonPressed(2)) {
			resetSensors();
		}
	}
	
	
	
	
}
