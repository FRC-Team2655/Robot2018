package org.usfirst.frc.team2655.robot.subsystem;

import org.usfirst.frc.team2655.robot.Robot;
import org.usfirst.frc.team2655.robot.RobotProperties;
import org.usfirst.frc.team2655.robot.values.Values;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveBaseSubsystem  {
		
	// The rotate PID
	
	private PIDSource rotateSource = new PIDSource() {
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
    private PIDOutput rotateOutput = new PIDOutput() {
    	@Override
    	public void pidWrite(double output) {
    		SmartDashboard.putNumber(Values.ROTATE_PID, Robot.imu.getAngleZ());
    		if(Math.abs(rotatePIDController.getError()) < 1) {
    			rotatePIDController.disable();
    		}
    		drive(0, -output);
    	}
    };
	PIDController rotatePIDController = new PIDController(0, 0, 0, 0, rotateSource, rotateOutput);
	private double rotateSetpoint = 0;
	
	/**
	 * Initialize LiveWindow PIDs
	 */
	public DriveBaseSubsystem() {
		rotatePIDController.setName("Rotate PID");
		rotatePIDController.setContinuous(false);
		LiveWindow.add(rotatePIDController);
	}
	
    public void initDefaultCommand() {}
    
    /**
     * Drive the robot
     * @param power Speed to drive
     * @param rotation Power to rotate with	
     */
    public void drive(double power, double rotation) {
    	Robot.robotDrive.arcadeDrive(power, rotation, false);
    }
    
    public void rotatePID(double degree) {
    	rotateSetpoint = Robot.imu.getAngleZ() + degree;
    	rotatePIDController.setSetpoint(rotateSetpoint);
    	rotatePIDController.enable();
    }
    
    /**
     * Drive until a distance at a certain speed in a straight line
     * @param speed The speed to drive at (-1 to 1)
     * @param distance The distance to drive until (inches)
     */
    /*public void driveDistance(double speed, double distance) {
    	if (distance > 0) {
    		speed = Math.abs(speed);
    	} else {
    		speed = -1 * Math.abs(speed);
    	}
    	double target = getAvgTicks() + (distance / 18.7 * 1440); // Distance in ticks
    	double ticks = getAvgTicks();
    	    	
    	while(Math.abs(ticks) < Math.abs(target)) {
    		drive(speed, 0);
    		ticks = getAvgTicks();
    	}
    	drive(0, 0);
    }*/
    
    /**
     * Average the values of all four encoders
     * @return The average number of ticks
     */
    private int getAvgTicks() {
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


