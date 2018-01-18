package org.usfirst.frc.team2655.robot;

import org.usfirst.frc.team2655.robot.controllers.IController;
import org.usfirst.frc.team2655.robot.subsystem.DriveBaseSubsystem;
import org.usfirst.frc.team2655.robot.values.Values;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	
	// Our motor controllers. These will be initialized (created) in robotInit
	public static WPI_TalonSRX leftMotor = new WPI_TalonSRX(1);
	public static WPI_TalonSRX leftSlave1 = new WPI_TalonSRX(2);
    public static WPI_TalonSRX leftSlave2 = new WPI_TalonSRX(3);
    public static WPI_TalonSRX rightMotor = new WPI_TalonSRX(4);
	public static WPI_TalonSRX rightSlave1 = new WPI_TalonSRX(5);
    public static WPI_TalonSRX rightSlave2 = new WPI_TalonSRX(6);
    
	public static WPI_TalonSRX[] motors = new WPI_TalonSRX[] {leftMotor, leftSlave1, leftSlave2, rightMotor, rightSlave1, rightSlave2};
	
	// The Gyro
	
	// The RobotDrive class handles all the motors
	public static DifferentialDrive robotDrive = new DifferentialDrive(leftMotor, rightMotor);
	
	// Robot Subsystems
	public static DriveBaseSubsystem driveBase = new DriveBaseSubsystem();
	
	// Controller Selector
	public static SendableChooser<IController> controllerSelect = new SendableChooser<IController>();
	
	/**
	 * Setup the motor controllers and the drive object
	 */
	@Override
	public void robotInit() {
		// Setup controllers
		for(IController c : OI.controllers) {
			controllerSelect.addObject(c.getName(), c);
		}
		controllerSelect.addDefault(OI.controllers.get(0).getName(), OI.controllers.get(0));
		OI.selectController(OI.controllers.get(0));
	    	    		
		// Setup the rear motors to follow (copy) the front motors
		leftSlave1.follow(leftMotor);
		leftSlave2.follow(leftMotor);
		rightSlave1.follow(rightMotor);
		rightSlave2.follow(rightMotor);
				
		// Setup the motor controllers
		for(WPI_TalonSRX m : motors) {
			m.setInverted(true);
			m.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
			m.setSelectedSensorPosition(0, RobotProperties.TALON_PID_ID, RobotProperties.TALON_TIMEOUT);
		}
		
		
		// Add stuff to the dashboard
		SmartDashboard.putBoolean(Values.DRIVE_CUBIC, true);
		SmartDashboard.putBoolean(Values.ROTATE_CUBIC, false);
		SmartDashboard.putNumber(Values.GYRO, 0);
		SmartDashboard.putData(Values.CONTROLLER_SELECT, controllerSelect);
		
	}
	
	@Override
	public void robotPeriodic() {
		super.robotPeriodic();
		// Update controller choice
		IController selected = controllerSelect.getSelected();
		if(selected != OI.selectedController) {
			OI.selectController(selected);
		}
	}

	/**
	 * Called every 20ms during the driver controlled period
	 */
	@Override
	public void teleopPeriodic() {
		boolean driveCubic = SmartDashboard.getBoolean(Values.DRIVE_CUBIC, true);
		boolean rotateCubic = SmartDashboard.getBoolean(Values.ROTATE_CUBIC, true);
		
		double power =  driveCubic ? OI.driveAxis.getValue() : OI.driveAxis.getValueLinear();
		double rotation = -1 * (rotateCubic ? OI.rotateAxis.getValue() : OI.rotateAxis.getValueLinear());
				
		driveBase.drive(power, rotation);
		
		if (OI.js0.getRawButton(2)) {
			driveBase.driveDistance(.5, 24);
		}
	}
	
	
}
