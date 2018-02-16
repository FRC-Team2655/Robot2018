package org.usfirst.frc.team2655.robot;

import edu.wpi.first.wpilibj.Timer;

public final class AutoCommands {
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
		public void initCommand(Double arg1, Double arg2) {
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
	public static class DriveCommand extends AutoCommand{
		
		public DriveCommand() {
			super(0);
		}

		private double targetDistance;
		@Override
		public void initCommand(Double arg1, Double arg2) {
			if(arg1 == null) {
				complete();
				return;
			}
			Robot.resetEncoders();
			Timer.delay(0.1); // Wait for encoders to reset
			Robot.driveBase.setBrake(true);
			targetDistance = -arg1 / 18.8496 * 1440;
			Robot.driveBase.setAngleCorrection(true);
			super.initCommand(arg1, arg2);
		}
		
		@Override
		public void complete() {
			Robot.driveBase.setAngleCorrection(false);
			Robot.driveBase.drive(0, 0);
			Timer.delay(0.1);
			Robot.driveBase.setBrake(false);
			super.complete();
		}

		@Override
		public void feedCommand() {
			if(Math.abs(Robot.driveBase.getAvgTicks()) < Math.abs(targetDistance)) {
				Robot.driveBase.drive(Math.copySign(0.5, targetDistance), Robot.driveBase.rotateCorrectOut);
			}else {
				complete();
			}
			super.feedCommand();
		}
		
	}
	public static class RotateCommand extends AutoCommand{		
		public RotateCommand() {
			super(0);
		}
		@Override
		public void initCommand(Double arg1, Double arg2) {
			if(arg1 == null) {
				complete();
				return;
			}
			Robot.driveBase.setBrake(true);
			Robot.driveBase.rotatePID(arg1);
			super.initCommand(arg1, arg2);
		}
		@Override
		public void complete() {
			Robot.driveBase.setBrake(false);
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
	public static class DelayCommand extends AutoCommand{

		public DelayCommand() {
			super(0);
		}

		@Override
		public void initCommand(Double arg1, Double arg2) {
			if(arg1 == null) {
				complete();
				return;
			}
			setTimeout((long)(arg1 * 1000.0));
			super.initCommand(arg1, arg2);
		}
		
		
		
	}
}
