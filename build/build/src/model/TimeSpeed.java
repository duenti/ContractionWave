package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TimeSpeed implements Serializable {
	/**
	 * 
	 */
	
	public static double round(double d, int decimalPlace) {
	    BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	private static final long serialVersionUID = 1L;
	private double time;
	private double time1;
	private double speed;
	private int index;
	public TimeSpeed(double time, double speed, double time_conv, double speed_conv, int index) {
		setIndex(index);
		setTime((time / time_conv));
		setTime1((time / time_conv) * 1000.0);
		setSpeed( round((speed * time_conv * speed_conv),3) );
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int a) {
		this.index = a;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getTime1() {
		return time1;
	}
	public void setTime1(double time1) {
		this.time1 = time1;
	}
}