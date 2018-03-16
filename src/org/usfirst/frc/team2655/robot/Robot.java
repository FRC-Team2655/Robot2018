package org.usfirst.frc.team2655.robot;

import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.subsystem.DriveBaseSubsystem;
import org.usfirst.frc.team2655.robot.subsystem.IntakeSubsystem;
import org.usfirst.frc.team2655.robot.subsystem.NewLifter;
import org.usfirst.frc.team2655.robot.values.Values;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
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
	public static WPI_TalonSRX rightSlave1 = new WPI_TalonSRX(4);
	public static WPI_TalonSRX rightSlave2 = new WPI_TalonSRX(6);
	
	public static WPI_TalonSRX[] motors = new WPI_TalonSRX[] {leftMotor, leftSlave1, leftSlave2, rightMotor, rightSlave1, rightSlave2};
		
	//public static WPI_TalonSRX lifterMotor = new WPI_TalonSRX(6);
	//public static WPI_TalonSRX lifterSlave1 = new WPI_TalonSRX(7);
	
	public static DoubleSolenoid intakeSolenoid = new DoubleSolenoid(0, 1);
	public static DoubleSolenoid lifterSolenoid = new DoubleSolenoid(2, 3);
	
	//public static VictorSP climber = new VictorSP(2);
	
	public static WPI_TalonSRX intakeLeft = new WPI_TalonSRX(7), intakeRight = new WPI_TalonSRX(8);
	
	public static PowerDistributionPanel pdp = new PowerDistributionPanel(0);
	
	// Lifter
	//public static DigitalInput lifterTopSwitch = new DigitalInput(8);
	//public static DigitalInput lifterBottomSwitch = new DigitalInput(9);
	public static DigitalInput intakeSwitch = new DigitalInput(7);
	
	// The Gyro
	public static ADIS16448_IMU imu;
		
	// Robot Subsystems
	public static DriveBaseSubsystem driveBase = new DriveBaseSubsystem();
	public static IntakeSubsystem intake = new IntakeSubsystem();
	public static NewLifter newLifter = new NewLifter();
	//public static LifterSubsystem lifter = new LifterSubsystem();
	
	// Controller Selector
	public static SendableChooser<IController> controllerSelect = new SendableChooser<IController>();
				
	// Auto Selectors
	public static SendableChooser<Integer> autoScaleOption = new SendableChooser<Integer>();
	public static SendableChooser<Integer> autoCrossOption = new SendableChooser<Integer>();
	public static SendableChooser<Integer> autoPositionOption = new SendableChooser<Integer>();
	
	public static Autonomous a;
	
	public static Compressor compressor = new Compressor(0);
	
	/**
	 * Setup the motor controllers and the drive object
	 */
	@Override
	public void robotInit() {
		
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480);
		
		// Allow the driver to select a controller
		OI.selectController(OI.controllers.
				
				get(0));
		
		// Setup IMU and Motors
		imu = new ADIS16448_IMU();
				
		leftSlave1.follow(leftMotor);
		
		rightSlave1.follow(rightMotor);
		
		leftSlave2.follow(leftMotor);
		
		rightSlave2.follow(rightMotor);
		
		intakeRight.follow(intakeLeft);
				
		// Make (+) up
		//lifterMotor.setInverted(true);
		//lifterSlave1.setInverted(true);
		
		//lifterSlave1.follow(lifterMotor);
				
		// Do not allow LiveWindow to control the talons. It breaks follow mode
		LiveWindow.remove(leftMotor);
		LiveWindow.remove(rightMotor);
		LiveWindow.remove(leftSlave1);
		LiveWindow.remove(rightSlave1);
		LiveWindow.remove(leftSlave2);
		LiveWindow.remove(rightSlave2);
		LiveWindow.remove(intakeLeft);
		LiveWindow.remove(intakeRight);
		//LiveWindow.remove(lifterMotor);
		//LiveWindow.remove(lifterSlave1);
		
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
		
		//lifterMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		//lifterMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		
		/*leftMotor.config_kP(RobotProperties.TALON_PID_ID, 0.2, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kI(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kD(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kF(RobotProperties.TALON_PID_ID, 1.12, RobotProperties.TALON_TIMEOUT);*/
		
		if(imu != null)
			imu.reset(); // Make initial direction 0
		
		// Add stuff to the dashboard
		SmartDashboard.putBoolean(Values.DRIVE_CUBIC, true);
		SmartDashboard.putBoolean(Values.ROTATE_CUBIC, false);
		SmartDashboard.putData("Select Controller:", controllerSelect);
		SmartDashboard.putString(Values.CURRENT_AUTO, "");
		SmartDashboard.putBoolean(Values.VELOCITY_LOOP, false);
		SmartDashboard.putBoolean(Values.INTAKE_OVERRIDE, false);
		
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
		String gameData = "";
		// Retry after 100ms if there is no data at first
		long startTime = System.currentTimeMillis();
		while(gameData.length() == 0) {
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			long now = System.currentTimeMillis();
			if(now - startTime > 5000)
				break;
		}
		return gameData;
	}
	
	public String getAutoScript(String gameData) {
		// Get data from the dashboard
		int position = autoPositionOption.getSelected();
		boolean trySwitch = SmartDashboard.getBoolean(Values.AUTO_TRY_SWITCH, true);
		boolean driveCross = autoCrossOption.getSelected() == Values.AUTO_CROSS_CROSS;
		boolean scalePlace = autoScaleOption.getSelected() == Values.AUTO_SCALE_PLACE;
		String output = "";
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
		return output;
	}
	
	@Override
	public void autonomousInit() {
		int position = autoPositionOption.getSelected();
		driveBase.setBrake(true);
		// The resulting script's name
		String script = "";
		
		String gameData = getFieldData();
		
		// If there is data use it to determine which script to run in auto
		// GameData is in format [OURSWITCH][SCALE][OPPONENTSWITCH]
		// Example: "LRL" means that the left of both switches is ours and the right scale is ours
        if(gameData.length() > 0) {
			script = getAutoScript(gameData);
        }	
		
        // Setup auto and load the script.
		a = new Autonomous();
		boolean success = false;
		if(!script.trim().equals(""))
			success = a.loadScript(script);
		if(!success) {
			switch(position) {
			case 1:
			case 3:
				a.putScript(new String[] {"DRIVE"}, new Double[] {133.0});
				break;
			case 2:
				a.putScript(new String[] {"DRIVE", "ROTATE", "DRIVE", "ROTATE", "DRIVE", "DRIVE"}, 
						new Double[] {30.0, 90.0, 15.0, 0.0, 44.0, -4.0});
				break;
			}
		}
		
		double time = SmartDashboard.getNumber(Values.AUTO_DELAY, 0);
		if(time > 0) {
			a.addDelay(time);
		}
		
		Timer.delay(SmartDashboard.getNumber(Values.AUTO_DELAY, 0));
	}

	@Override
	public void autonomousPeriodic() {
		newLifter.setLifter(true);		
		a.feedAuto();
	}	
	
	@Override
	public void teleopInit() {
		// NO PIDs (just in case they were still alive from auto)
		driveBase.rotatePIDController.disable();
		driveBase.angleCorrectionPIDController.disable();
		driveBase.setBrake(false);
		compressor.setClosedLoopControl(false);
		compressor.setClosedLoopControl(true);
	}

	/**
	 * Set the IMU to 0
	 * Set encoders to 0
	 */
	public static void resetSensors() {
		if(imu != null)
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
			//OI.selectController(selected);
		}
		// Update dashboard values as needed
		if(imu != null)
			SmartDashboard.putNumber(Values.GYRO, imu.getAngleX());
		SmartDashboard.putNumber(Values.LEFT_ENC, leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
		SmartDashboard.putNumber(Values.RIGHT_ENC, rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
		//SmartDashboard.putNumber("LeftVelocity", leftMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
		//SmartDashboard.putNumber("RightVelocity", rightMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
		//SmartDashboard.putBoolean("LifterTop", lifter.isTopPressed());
		//SmartDashboard.putBoolean("LifterBottom", lifter.isBottomPressed());
		//SmartDashboard.putNumber("LifterEncoder", lifterMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
		SmartDashboard.putBoolean("IntakeSwitch", intake.isSwitchPressed());
	}
	
	/**
	 * Called every 20ms during the driver controlled period
	 */
	@Override
	public void teleopPeriodic() {
			
		// LIFTER!!
		
		if(OI.autoDownButton.isPressed() && !OI.autoUpButton.isPressed()) {
			newLifter.setLifter(false);
		}else if(OI.autoUpButton.isPressed() && !OI.autoDownButton.isPressed()) {
			newLifter.setLifter(true);
		}
		
		// INTAKE!!!
		
		double intakeSpeed = 0;
		if(OI.intakeInButton.isPressed() && !OI.intakeOutButton.isPressed()) {
			intakeSpeed = 0.75;
		}
		if(OI.intakeOutButton.isPressed() && !OI.intakeInButton.isPressed()) {
			intakeSpeed = -0.75;
		}
		
		intake.setLock(!OI.intakeReleaseButton.isPressed());
		intake.moveIntake(intakeSpeed);
		
		// DRIVE!!!
		
		boolean driveCubic = SmartDashboard.getBoolean(Values.DRIVE_CUBIC, true);
		boolean rotateCubic = SmartDashboard.getBoolean(Values.ROTATE_CUBIC, true);
		
		double power =  driveCubic ? OI.driveAxis.getValue() : OI.driveAxis.getValueLinear();
		double rotation =(rotateCubic ? OI.rotateAxis.getValue() : OI.rotateAxis.getValueLinear()) * -0.5;
		
		if(OI.resetButton.isPressed()) {
			resetSensors();
		}
				
		SmartDashboard.putNumber("Speed", rotation);
		driveBase.drive(power, rotation);
	}

	@Override
	public void testPeriodic() {
		if(OI.resetButton.isPressed()) {
			resetSensors();
		}
	}
	
	
	
	
}
