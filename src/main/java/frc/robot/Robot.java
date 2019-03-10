package frc.robot;

import frc.robot.controllers.IController;
import frc.robot.subsystem.DriveBaseSubsystem;
import frc.robot.subsystem.IntakeSubsystem;
import frc.robot.subsystem.NewLifter;
import frc.robot.values.Values;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	
	public boolean controllerSwitchPrevValue = false;
	
	public static final boolean newIntake = false;
	
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
		
		CameraServer.getInstance().addAxisCamera("Front Camera", "axis-camera");
		
		// Allow the driver to select a controller
		OI.selectController(OI.js0, OI.controllers.get(0));
				
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
		
		
		rightMotor.setInverted(true);
		rightSlave1.setInverted(true);
		rightSlave2.setInverted(true);
		
		leftMotor.setSensorPhase(true);
		rightMotor.setSensorPhase(true);
		
		
		// Setup encoders. (-) direction is forward (b/c up on JS is negative)
		leftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		leftMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		
		leftMotor.configNominalOutputForward(0, RobotProperties.TALON_TIMEOUT);
		leftMotor.configNominalOutputReverse(0, RobotProperties.TALON_TIMEOUT);
		leftMotor.configPeakOutputForward(1, RobotProperties.TALON_TIMEOUT);
		leftMotor.configPeakOutputReverse(-1, RobotProperties.TALON_TIMEOUT);
		
		rightMotor.configNominalOutputForward(0, RobotProperties.TALON_TIMEOUT);
		rightMotor.configNominalOutputReverse(0, RobotProperties.TALON_TIMEOUT);
		rightMotor.configPeakOutputForward(1, RobotProperties.TALON_TIMEOUT);
		rightMotor.configPeakOutputReverse(-1, RobotProperties.TALON_TIMEOUT);
		
		TalonPIDDisplay leftDisplay = new TalonPIDDisplay(leftMotor, ControlMode.Velocity, 0.1, 0, 0, 0.335);
		SmartDashboard.putData("Left Talon", leftDisplay);
		
		TalonPIDDisplay rightDisplay = new TalonPIDDisplay(rightMotor, ControlMode.Velocity, 0.1, 0, 0, 0.33);
		SmartDashboard.putData("Right Talon", rightDisplay);
		
		if(imu != null)
			imu.reset(); // Make initial direction 0
		
		// Add stuff to the dashboard
		SmartDashboard.putBoolean(Values.DRIVE_CUBIC, true);
		SmartDashboard.putBoolean(Values.ROTATE_CUBIC, false);
		SmartDashboard.putData("Select Controller:", controllerSelect);
		SmartDashboard.putString(Values.CURRENT_AUTO, "");
		SmartDashboard.putBoolean(Values.INTAKE_OVERRIDE, false);
		SmartDashboard.putBoolean(Values.DEAD_ENCODER, false);
		
		// Auto Options
		autoPositionOption.addObject("1 - Left", 1);
		autoPositionOption.addDefault("2 - Center", 2);
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
		SmartDashboard.putBoolean(Values.VELOCITY_DRIVE, false);
	}
	
	@Override 
	public void disabledInit(){
		// Make sure auto is dead and not trying to control anything
		if(a != null)
			a.killAuto();
		if(AutoCommands.intakeControl != null)
			AutoCommands.intakeControl.stop();
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

		if(gameData.equalsIgnoreCase("Test")) {
			return "TEST";
		}
		
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
					output = "1D";
				}
				if(scalePlace == false) {
			 		output = "1D";
			 	}
			}else {
				
				if(driveCross == true) {
					output = "1D";
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
					output = "3D";
				}
				if(scalePlace == false) {
			 		output = "3D";
			 	}
			}else {
				
				if(driveCross == true) {
					output = "3D";
				}
				if(driveCross == false) {
					output = "3D";
				}
			}
			break;
		}
		return output;
	}

	public String getPathfinderScript(String gameData){
		if(gameData.equalsIgnoreCase("Test")) {
			return "TEST";
		}

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
					output = "1A-Path";
				}else if(scaleLeft == true) {
					if(scalePlace == true) {
						output = "1D";
					}
					if(scalePlace == false) {
						output = "1D";
					}
				}else {

					if(driveCross == true) {
						output = "1D";
					}
					if(driveCross == false) {
						output = "1D";
					}
				}
				break;
			case 2:
				if(switchLeft == true) {
					output = "2L-Path";
				}
				if(switchLeft == false) {
					output = "2R-Path";
				}
				break;
			case 3:
				if(trySwitch == true && switchLeft == false) {
					output = "3A-Path";
				}else if(scaleLeft == false) {
					if(scalePlace == true) {
						output = "3D";
					}
					if(scalePlace == false) {
						output = "3D";
					}
				}else {

					if(driveCross == true) {
						output = "3D";
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
		
		imu.reset();
		
		compressor.setClosedLoopControl(false);
		compressor.setClosedLoopControl(true);
		newLifter.setLifter(true);
		
		int position = autoPositionOption.getSelected();
		driveBase.setBrake(true);
		// The resulting script's name
		String script = "";
		
		String gameData = getFieldData();
		
		// If there is data use it to determine which script to run in auto
		// GameData is in format [OURSWITCH][SCALE][OPPONENTSWITCH]
		// Example: "LRL" means that the left of both switches is ours and the right scale is ours
        if(gameData.length() > 0) {
        	if(SmartDashboard.getBoolean(Values.DEAD_ENCODER, false))
        		script = getAutoScript(gameData);
        	else
        		script = getPathfinderScript(gameData);
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
				a.putScript(new String[] {"DRIVE"}, new Double[] {142.0});
				break;
			case 2:
				a.putScript(new String[] {"ROTATE", "DRIVE"}, 
						new Double[] {25.0, 102.0});
				break;
			}
		}
		
		double time = SmartDashboard.getNumber(Values.AUTO_DELAY, 0);
		if(time > 0) {
			a.addDelay(time);
		}
	}

	@Override
	public void autonomousPeriodic() {		
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
		
		if(OI.resetButton.isPressed()) {
			resetSensors();
		}
		
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
		SmartDashboard.putNumber("LeftVelocity", leftMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
		SmartDashboard.putNumber("RightVelocity", rightMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
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
		
		// CONTROLLER SWITCH!!!
		
		/*boolean switchController = OI.js1.getRawButton(9);
		if (switchController && !controllerSwitchPrevValue) {
			if(OI.selectedJs == OI.js0)
				OI.selectController(OI.js1, OI.controllers.get(0));
			else
				OI.selectController(OI.js0, OI.controllers.get(0));
		}
		controllerSwitchPrevValue = switchController;*/
			
		// LIFTER!!
		
		if(OI.autoDownButton.isPressed() && !OI.autoUpButton.isPressed()) {
			newLifter.setLifter(false);
		}else if(OI.autoUpButton.isPressed() && !OI.autoDownButton.isPressed()) {
			newLifter.setLifter(true);
		}
		
		// INTAKE!!!
		
		double intakeSpeed = 0;
		if(OI.intakeInButton.isPressed() && !OI.intakeOutButton.isPressed()) {
			intakeSpeed = 0.8;
		}
		if(OI.intakeOutButton.isPressed() && !OI.intakeInButton.isPressed()) {
			intakeSpeed = -0.8;
		}
		
		intake.setLock(!OI.intakeReleaseButton.isPressed());
		intake.moveIntake(intakeSpeed);
		
		// DRIVE!!!
		
		boolean driveCubic = SmartDashboard.getBoolean(Values.DRIVE_CUBIC, true);
		boolean rotateCubic = SmartDashboard.getBoolean(Values.ROTATE_CUBIC, true);
		
		double power =  (driveCubic ? OI.driveAxis.getValue() : OI.driveAxis.getValueLinear()) * -1;
		double rotation = (rotateCubic ? OI.rotateAxis.getValue() : OI.rotateAxis.getValueLinear());

		if(SmartDashboard.getBoolean(Values.VELOCITY_DRIVE, false)){
			rotation *= 0.55;
		}else{
			rotation *= 0.45;
		}

		// VICTORY SPIN
		int direction = OI.selectedJs.getPOV(0);
		if(OI.victorySpinButton.isPressed() && (direction == 90 || direction == 270)){
			power = 0;
			rotation = (direction == 90) ? 1 : -1;
		}

		// Use velocity closed loop if both encoders work and velocity closed loop is enabled on SmartDashboard
		driveBase.drive(power, rotation, true);

	}

	@Override
	public void testPeriodic() {
		if(OI.resetButton.isPressed()) {
			resetSensors();
		}
	}
	
	
	
	
}
