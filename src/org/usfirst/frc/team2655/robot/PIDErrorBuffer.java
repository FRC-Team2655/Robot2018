package org.usfirst.frc.team2655.robot;

import java.util.ArrayList;

public class PIDErrorBuffer {
	private ArrayList<Double> data;
	private int size;
	
	public PIDErrorBuffer(int size) {
		this.size = size;
		data = new ArrayList<>();
	}
	
	public Double average() {
		if(data.size() == 0)
			return Double.MAX_VALUE;
		Double val = 0.0;
		for(Double d : data){
			val += d;
		}
		return val / data.size();
	}
	
	public void clear() {
		data.clear();
	}
	
	public void put(Double error) {
		if(data.size() < size) {
			data.add(error);
		}else {
			data.remove(0);
			data.add(error);
		}
	}
}
