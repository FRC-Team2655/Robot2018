package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.RobotProperties;
import org.usfirst.frc.team2655.robot.values.Values;

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
	
	private final PIDSource rotateSource = new PIDSource() {
    	@Override
    	public double pidGet() {
    		return Robot.imu.getAngleZ();
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
    		//SmartDashboard.putNumber(Values.ROTATE_PID, Robot.imu.getAngleZ());
    		/*if(Math.abs(rotatePIDController.getError()) < 1 && rotatePIDController.isEnabled()) {
    			rotatePIDController.disable();
    		}*/
    		drive(0, -output);
    	}
    };
	private final PIDController rotatePIDController = new PIDController(0, 0, 0, 0, rotateSource, rotateOutput);
	private double rotateSetpoint = 0;
	
    public void initDefaultCommand() {}
    
    public void setBrake(boolean brakeMode) {
    	for(WPI_TalonSRX t : Robot.motors) {
    		t.setNeutralMode(brakeMode ? NeutralMode.Brake : NeutralMode.Coast);
    	}
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
    	if(SmartDashboard.getBoolean(Values.VELOCITY_LOOP, false)) {
    		left *= 710.0 * 4096 / 600;
    		right *= 710.0 * 4096 / 600;
    		Robot.leftMotor.set(ControlMode.Velocity, left);
        	//Robot.rightMotor.set(ControlMode.Velocity, right);
    		
    		SmartDashboard.putNumber("Output", Robot.leftMotor.getSelectedSensorVelocity(RobotProperties.TALON_PID_ID));
    		SmartDashboard.putNumber("Target", left);
    	}else {
    		Robot.leftMotor.set(ControlMode.PercentOutput, left);
    		Robot.rightMotor.set(ControlMode.PercentOutput, right);
    	}
    }
    
    public void rotatePID(double degree) {
    	rotateSetpoint = Robot.imu.getAngleZ() + degree;
    	rotatePIDController.setSetpoint(rotateSetpoint);
    	rotatePIDController.enable();
    }
    
    /**
     * Average the values of all four encoders
     * @return The average number of ticks
     */
    public int getAvgTicks() {
    	int left = Robot.leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID) * -1; 
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
      
      return new double[] {leftMotorOutput, -rightMotorOutput};
    }
    
}


