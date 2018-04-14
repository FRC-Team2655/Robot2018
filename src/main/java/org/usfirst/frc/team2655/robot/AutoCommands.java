package org.usfirst.frc.team2655.robot;

import java.io.File;

import edu.wpi.first.wpilibj.Timer;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;

/**
 * This is a container class for all of the commands used in auto.
 * @author Marcus Behel
 *
 */
public final class AutoCommands {
	/**
	 * The base class for all auto commands.
	 * This class has init (start), complete (end), and feed (during) functions for auto.
	 * This class also keeps track of a command's status (started, not started, done)
	 * @author Marcus Behel
	 *
	 */
	public static abstract class AutoCommand{
		private boolean isStarted = false;
		private boolean isDone = false;
		private long timeout = 0;
		private long startTime = 0;
		
		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}
		
		public long getTimeout() {
			return timeout;
		}
		
		public AutoCommand(long timeout) {
			this.timeout = timeout;
		}
		
		public boolean isStarted() {
			return isStarted;
		}
		public boolean isDone() {
			return isDone;
		}
		public void initCommand(Object arg1, Object arg2) {
			startTime = System.currentTimeMillis();
			isStarted = true;
		}
		public void complete() {
			isDone = true;
		}
		private void checkTimeout() {
			long now = System.currentTimeMillis();
			if(isStarted && !isDone && now >= (startTime + timeout) && timeout > 0) {
				complete();
			}
		}
		public void feedCommand() {
			checkTimeout();
		}
	}
	
	// Drive with angle correction until a distance
	public static class DriveCommand extends AutoCommand{		
		public DriveCommand() {
			super(0);
		}
		private double lastPos = 0;
		private int stopCounter = 0;
		private double targetDistance;
		private double distanceLeft;
		@Override
		public void initCommand(Object arg1, Object arg2) {
			if(arg1 == null) {
				complete();
				return;
			}
			Robot.resetEncoders();
			Timer.delay(0.1); // Wait for encoders to reset
			targetDistance = ((Double)arg1) / 18.8496 * 4096;
			distanceLeft = targetDistance;
			Robot.driveBase.setAngleCorrection(true);
			super.initCommand(arg1, arg2);
		}
		
		@Override
		public void complete() {
			Robot.driveBase.setAngleCorrection(false);
			Robot.driveBase.drive(0, 0);
			super.complete();
		}

		@Override
		public void feedCommand() {
			double ticks = Robot.driveBase.getAvgTicks();
			distanceLeft = targetDistance - ticks;
			
			// If we have moved less than 10 ticks between iterations assume we are stopped
			if(lastPos != 0 && Math.abs(ticks - lastPos) < 10) {
				stopCounter++;
			}else {
				stopCounter = 0;
			}
			lastPos = ticks;
			
			// [distance from start or end] / threshold + minSpeed
			double rampup = Math.abs(targetDistance - distanceLeft) / 8192.0 + 0.3;
			double rampdown = Math.abs(distanceLeft) / 24576.0 + 0.1;
			
			double speed = Math.copySign(Math.min(1.0, Math.min(rampup, rampdown)) , distanceLeft);
			
			// Past target or average within 1-4 rotation for 10 iterations
			if(Math.abs(ticks) < Math.abs(targetDistance) && stopCounter < 10) {
				Robot.driveBase.drive(Math.copySign(speed, targetDistance), Robot.driveBase.rotateCorrectOut);
			}else {
				complete();
			}
			super.feedCommand();
		}
		
	}
	
	// Rotate to an absolute position
	public static class RotateCommand extends AutoCommand{		
		public RotateCommand() {
			super(5000);
		}
		@Override
		public void initCommand(Object arg1, Object arg2) {
			if(arg1 == null) {
				complete();
				return;
			}
			Robot.driveBase.rotatePID((Double)arg1);
			super.initCommand(arg1, arg2);
		}
		@Override
		public void complete() {
			super.complete();
		}
		@Override
		public void feedCommand() {
			if(!Robot.driveBase.rotatePIDController.isEnabled()) {
				complete();
			}
			super.feedCommand();
		}
		
	}
	
	// Wait a certain amount of time (without blocking the robot code)
	public static class DelayCommand extends AutoCommand{

		public DelayCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			if(arg1 == null) {
				complete();
				return;
			}
			setTimeout((long)(((Double)arg1) * 1000.0));
			super.initCommand(arg1, arg2);
		}
		
		
		
	}
	public static class OutputCommand extends DelayCommand{
		@Override
		public void feedCommand() {
			Robot.intake.moveIntake(-0.8);
			super.feedCommand();
		}
		@Override
		public void complete() {
			Robot.intake.moveIntake(0);
			super.complete();
		}
	}
	public static class RaiseLifterCommand extends AutoCommand{

		public RaiseLifterCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			super.complete();
		}

		@Override
		public void feedCommand() {
			Robot.newLifter.setLifter(true);
			complete();
			super.feedCommand();
		}
		
	}
	public static class LowerLifterCommand extends AutoCommand{

		public LowerLifterCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			super.complete();
		}

		@Override
		public void feedCommand() {
			Robot.newLifter.setLifter(false);
			complete();
			super.feedCommand();
		}
	}
	
	static Thread intakeControl = null;
	public static class IntakeOnCommand extends AutoCommand{

		public IntakeOnCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			super.complete();
		}

		@Override
		public void feedCommand() {
			intakeControl = new Thread() {
				@Override
				public void run() {
					while(true) {
						Robot.intake.moveIntake(1.0);
						try {Thread.sleep(30);}catch(Exception e) {}
					}
				}
			};
			intakeControl.start();
			complete();
			super.feedCommand();
		}
	}
	public static class IntakeOffCommand extends AutoCommand{

		public IntakeOffCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			Robot.intake.moveIntake(0);
			super.complete();
		}

		@Override
		public void feedCommand() {
			if(intakeControl != null)
				intakeControl.stop();
			complete();
			super.feedCommand();
		}
	}
	public static class IntakeOpenCommand extends AutoCommand{

		public IntakeOpenCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			Robot.intake.setLock(false);
			System.out.println("Open");
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			super.complete();
		}

		@Override
		public void feedCommand() {
			complete();
			super.feedCommand();
		}
	}
	public static class IntakeCloseCommand extends AutoCommand{

		public IntakeCloseCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			Robot.intake.setLock(true);
			System.out.println("Close");
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			super.complete();
		}

		@Override
		public void feedCommand() {
			complete();
			super.feedCommand();
		}
	}
	
	public static class PathCommand extends AutoCommand{
		private double lastPos = 0;
		private int stopCounter = 0;
		private EncoderFollower left, right;
		public PathCommand() {
			super(0);
		}

		@Override
		public void initCommand(Object arg1, Object arg2) {
			String name = (String)arg1;
			Trajectory leftTrajectory = null, rightTrajectory = null;
			File leftFile = new File(Autonomous.pathsPath + name + "_left_detailed.csv");
			File rightFile = new File(Autonomous.pathsPath + name + "_right_detailed.csv");

			if(leftFile.exists())
				leftTrajectory = Pathfinder.readFromCSV(leftFile);
			else
				System.err.println("Left trajectory file does not exist.");
			if(rightFile.exists())
				rightTrajectory = Pathfinder.readFromCSV(rightFile);
			else
				System.err.println("Right trajectory file does not exist.");

			if(leftTrajectory != null) {
				left = new EncoderFollower(leftTrajectory);
				left.configureEncoder(Robot.leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID), 4096, 0.152);
				left.configurePIDVA(1.0, 0.0, 0.0, 1 / RobotProperties.MAX_VEL, 0);
			}
			if(rightTrajectory != null) {
				right = new EncoderFollower(rightTrajectory);
				right.configureEncoder(Robot.rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID), 4096, 0.152);
				right.configurePIDVA(1.0, 0.0, 0.0, 1 /RobotProperties.MAX_VEL, 0);
			}

			if(left == null || right == null)
				complete();
			super.initCommand(arg1, arg2);
		}

		@Override
		public void complete() {
			Robot.driveBase.drive(0, 0);
			super.complete();
		}

		@Override
		public void feedCommand() {
			if(left == null || right == null)
				return;
			double ticks = Robot.driveBase.getAvgTicks();			
			// If we have moved less than 10 ticks between iterations assume we are stopped
			if(lastPos != 0 && Math.abs(ticks - lastPos) < 10) {
				stopCounter++;
			}else {
				stopCounter = 0;
			}
			lastPos = ticks;
			
			double l = left.calculate(Robot.leftMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
			double r = right.calculate(Robot.rightMotor.getSelectedSensorPosition(RobotProperties.TALON_PID_ID));
			
			double gyro_heading = Robot.imu.getAngleX();    // Assuming the gyro is giving a value in degrees
			double desired_heading = Pathfinder.r2d(left.getHeading());  // Should also be in degrees

			double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
			double turn = 0.8 * (-1.0/80.0) * angleDifference;

			l += turn;
			r -= turn;

			if(stopCounter >= 10 || (l == 0 && r == 0 && Math.abs(angleDifference) <= 1)) {
				complete();
			}else {
				Robot.driveBase.driveTankVelocity(l , r);
			}
			
			super.feedCommand();
		}
	}
}
