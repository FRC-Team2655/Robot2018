package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.PIDErrorBuffer;
import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.RobotProperties;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveBaseSubsystem extends Subsystem {
	
	// The rotate PID
	private final PIDErrorBuffer rotateErrorBuffer = new PIDErrorBuffer(20);
	private final PIDSource rotateSource = new PIDSource() {
    	@Override
    	public double pidGet() {
    		if(Robot.imu != null)
    			return Robot.imu.getAngleX();
    		return 0;
    	}

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}
    };
    private final PIDOutput rotateOutput = new PIDOutput() {
    	@Override
    	public void pidWrite(double output) {
    		double min = 0.1;
    		if(output != 0 && Math.abs(output) < min) {
    			output = Math.copySign(min, output);
    		}
    		SmartDashboard.putNumber("RotatePIDOut", Robot.imu.getAngleX());
    		rotateErrorBuffer.put(rotatePIDController.getError());
    		if(Math.abs(rotateErrorBuffer.average()) < 2 && rotatePIDController.isEnabled()) {
    			rotatePIDController.disable();
    			rotateErrorBuffer.clear();
    			drive(0, 0);
    		}else {
    			drive(0, output);
    		}
    	}
    };
    private final PIDOutput angleCorrectOutput = new PIDOutput() {
		@Override
		public void pidWrite(double output) {
			rotateCorrectOut = output;
			// Use the below code when tuning the angleCorrection PID
			//drive(0, rotateCorrectOut);
		}
    };
	public final PIDController rotatePIDController = new PIDController(0.005, 0.0000003, 0.002, 0, rotateSource, rotateOutput);
	public final PIDController angleCorrectionPIDController = new PIDController(0.01, 0, 0, 0, rotateSource, angleCorrectOutput);
	
    public void initDefaultCommand() {}
    
    public double rotateCorrectOut = 0;
    
    public void setBrake(boolean brakeMode) {
    	for(WPI_TalonSRX t : Robot.motors) {
    		t.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);
    	}
    }
    
    
    public DriveBaseSubsystem() {
    	super("DriveBase");
    	rotatePIDController.setContinuous(false);
    	rotatePIDController.setName("Rotate PID");
    	
    	// Set max allowed power for the PID
    	rotatePIDController.setOutputRange(-0.5, 0.5);
    	
    	angleCorrectionPIDController.setContinuous(false);
    	angleCorrectionPIDController.setName("Angle Correction PID");
    	
    	addChild(rotatePIDController);
    	addChild(angleCorrectionPIDController);
    }
    
    /**
     * Drive the robot
     * @param power Speed to drive
     * @param rotation Power to rotate with	
     */
    public void drive(double power, double rotation) {
    	double[] speeds = arcadeDrive(power, rotation, false);
    	driveTank(speeds[0], speeds[1]);
    }
    
    public void driveTank(double left, double right) {
    	if(left != 0)
    		Robot.leftMotor.set(ControlMode.Velocity, left * 3900);
    	else
    		Robot.leftMotor.set(0);
    	if(right != 0)
    		Robot.rightMotor.set(ControlMode.Velocity, right * 3900);
    	else
    		Robot.rightMotor.set(0);
    }
    
    public void rotatePID(double degree) {
    	rotatePIDController.setSetpoint(-degree);
    	rotatePIDController.enable();
    }
    
    public void setAngleCorrection(boolean enabled) {
    	if(enabled && Robot.imu != null) {
    		angleCorrectionPIDController.setSetpoint(Robot.imu.getAngleX());
    		angleCorrectionPIDController.enable();
    	}else {
    		rotateCorrectOut = 0;
    		angleCorrectionPIDController.disable();
    	}
    }
    
    /**
     * Average the values of all four encoders
     * @return The average number of ticks
     */
    public int getAvgTicks() {
    	int left = Robot.leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID); 
    	int right = Robot.rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID); 
    	int avg = (left + right) / 2;
    	return avg;
    	
    }
    
    public double[] arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
      // Square the inputs (while preserving the sign) to increase fine control
      // while permitting full power.
      if (squaredInputs) {
        xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
        zRotation = Math.copySign(zRotation * zRotation, zRotation);
      }

      double leftMotorOutput;
      double rightMotorOutput;

      double maxInput = Math.copySign(Math.max(Math.abs(xSpeed), Math.abs(zRotation)), xSpeed);

      zRotation *= 2;
      
      if (xSpeed >= 0.0) {
        // First quadrant, else second quadrant
        if (zRotation >= 0.0) {
          leftMotorOutput = maxInput;
          rightMotorOutput = xSpeed - zRotation;
        } else {
          leftMotorOutput = xSpeed + zRotation;
          rightMotorOutput = maxInput;
        }
      } else {
        // Third quadrant, else fourth quadrant
        if (zRotation >= 0.0) {
          leftMotorOutput = xSpeed + zRotation;
          rightMotorOutput = maxInput;
        } else {
          leftMotorOutput = maxInput;
          rightMotorOutput = xSpeed - zRotation;
        }
      }
      
      return new double[] {leftMotorOutput, rightMotorOutput};
    }
    
}


