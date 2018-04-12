package org.usfirst.frc.team2655.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class TalonPIDDisplay extends SendableBase{
	private WPI_TalonSRX talon;
	ControlMode mode;
	int slot;
	double p, i, d, f;
	
	private double setpoint = 0;
	private boolean enabled = false;
	
	public TalonPIDDisplay(WPI_TalonSRX talon, ControlMode mode, double p, double i, double d, double f) {
		this(talon, mode, p, i, d, f, 0);
	}
	
	public TalonPIDDisplay(WPI_TalonSRX talon, ControlMode mode, double p, double i, double d, double f, int slot) {
		super();
		this.talon = talon;
		this.mode = mode;
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.slot = slot;
	}
	
	
	public double getP() {
		return p;
	}
	public double getI() {
		return i;
	}
	public double getD() {
		return d;
	}
	public double getF() {
		return f;
	}
	
	public void setP(double p) {
		this.p = p;
		talon.config_kP(slot, p, 0);
	}
	public void setI(double i) {
		this.i = i;
		talon.config_kI(slot, i, 0);
	}
	public void setD(double d) {
		this.d = d;
		talon.config_kD(slot, d, 0);
	}
	public void setF(double f) {
		this.f = f;
		talon.config_kF(slot, f, 0);
	}
	
	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
		if(enabled) {
			talon.set(mode, setpoint);
		}
	}
	public double getSetpoint() {
		return this.setpoint;
	}
	
	public boolean isEnabled() {return this.enabled;}
	public void setEnabled(boolean enable) {
		this.enabled = enable;
		if(enable) {
			talon.set(mode, setpoint);
		}else {
			talon.set(ControlMode.PercentOutput, 0);
		}
	};
	
	public boolean reset() {
		return true;
	}
	
	@Override
	  public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("PIDController");
	    builder.setSafeState(this::reset);
	    builder.addDoubleProperty("p", this::getP, this::setP);
	    builder.addDoubleProperty("i", this::getI, this::setI);
	    builder.addDoubleProperty("d", this::getD, this::setD);
	    builder.addDoubleProperty("f", this::getF, this::setF);
	    builder.addDoubleProperty("setpoint", this::getSetpoint, this::setSetpoint);
	    builder.addBooleanProperty("enabled", this::isEnabled, this::setEnabled);
	  }
	
}
