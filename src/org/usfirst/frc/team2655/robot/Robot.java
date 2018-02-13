package org.usfirst.frc.team2655.robot;

import java.util.ArrayList;
import java.util.Arrays;

import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.subsystem.DriveBaseSubsystem;
import org.usfirst.frc.team2655.robot.values.Values;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
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
				
	Autonomous a;
	
	/**
	 * Setup the motor controllers and the drive object
	 */
	@Override
	public void robotInit() {
		
		controllerSelect.addDefault(OI.controllers.get(0).getName(), OI.controllers.get(0));
		for(int i = 1; i < OI.controllers.size(); i++) {
			IController c = OI.controllers.get(i);
			controllerSelect.addObject(c.getName(), c);
		}
		OI.selectController(OI.controllers.get(0));
		
		imu = new ADIS16448_IMU();
				
		leftSlave1.follow(leftMotor);
		leftSlave2.follow(leftMotor);
		
		rightSlave1.follow(rightMotor);
		rightSlave2.follow(rightMotor);
		
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
		
		leftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		leftMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		rightMotor.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		leftMotor.setSensorPhase(true);
		
		leftMotor.config_kP(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kI(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kD(RobotProperties.TALON_PID_ID, 0, RobotProperties.TALON_TIMEOUT);
		leftMotor.config_kF(RobotProperties.TALON_PID_ID, 1, RobotProperties.TALON_TIMEOUT);
		
		imu.reset(); // Make initial direction 0
		
		// Add stuff to the dashboard
		SmartDashboard.putBoolean(Values.DRIVE_CUBIC, true);
		SmartDashboard.putBoolean(Values.ROTATE_CUBIC, false);
		SmartDashboard.putData("Select Controller:", controllerSelect);
		SmartDashboard.putString(Values.CURRENT_AUTO, "");
		//SmartDashboard.putBoolean(Values.VELOCITY_LOOP, false);
		
		//SmartDashboard.putData("LeftClosedLoop", new TalonPIDDisplay(leftMotor, 0, 0, 0, 0.2111));
	}
	
	
	
	@Override
	public void autonomousInit() {
		a = new Autonomous();
		a.putScript(new ArrayList<String>(Arrays.asList(new String[] {"DRIVE"})), 
				new ArrayList<Double>(Arrays.asList(new Double[] {72.0})));
	}



	@Override
	public void autonomousPeriodic() {
		a.feedAuto();
	}



	public void resetSensors() {
		imu.reset();
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
		SmartDashboard.putNumber(Values.GYRO, imu.getAngleZ());
		SmartDashboard.putNumber(Values.LEFT_ENC, leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
		SmartDashboard.putNumber(Values.RIGHT_ENC, rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
	}

	/**
	 * Called every 20ms during the driver controlled period
	 */
	@Override
	public void teleopPeriodic() {
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
