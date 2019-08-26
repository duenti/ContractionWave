package model;

import java.math.BigDecimal;
import java.util.Objects;

import org.jfree.chart.plot.IntervalMarker;

import javafx.beans.property.SimpleBooleanProperty;

public class Peak {
	int f_point;
	int end_point;
	int mid_point;
	int sec_point;
	int fourth_point;
	private String pos;
	private double tcr;
	private double tc;
	private double tr;
	private double tc_vmc;
	private double tc_vmc_min;
	private double tr_vmr;
	private double tr_vmr_b;
	private double t_vmc_vmr;
	private double vmc;
	private double vmin;
	private double vmr;
	private double d_vmc_vmr;
	private double area_t;
	private double area_c;
	private double area_r;
	private double start_value;
	private double end_value;
	private IntervalMarker thisMarker;
//	private boolean selectedStatus;
	private SimpleBooleanProperty selectedStatus = new SimpleBooleanProperty();
	
	public SimpleBooleanProperty selectedProperty() {
		return selectedStatus;
	}
	
	public void setSelected(boolean selected) {
		this.selectedStatus.set(selected);
	}
	
	public static double round(double d, int decimalPlace) {
	    BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	public Peak(int f_point1, int end_point1, int mid_point1, int sec_point1, int fourth_point1, double start_value1, double end_value1, double tcr1, double tc1, double tr1, double tc_vmc1, double tc_vmc_min1, double tr_vmr1, double tr_vmr_b1, double t_vmc_vmr1, double vmc1, double vmin1, double vmr1, double d_vmc_vmr1, double area_t1, double area_c1, double area_r1, IntervalMarker e) {
		this.setSelected(false);
		f_point = f_point1;
		end_point = end_point1;
		mid_point = mid_point1;
		sec_point = sec_point1;
		fourth_point = fourth_point1;
		setThisMarker(e);
		setStart_value(start_value1);
		setEnd_value(end_value1);
		setPos(String.valueOf((int) start_value) + "-" + String.valueOf((int) end_value));
		tcr = round(tcr1, 3);
		tc = round(tc1, 3);
		tr = round(tr1, 3);
		tc_vmc = round(tc_vmc1, 3);
		tc_vmc_min = round(tc_vmc_min1, 3);
		tr_vmr = round(tr_vmr1, 3);
		tr_vmr_b = round(tr_vmr_b1, 3);
		t_vmc_vmr = round(t_vmc_vmr1, 3);
		vmc = round(vmc1,3);
		vmin = round(vmin1,3);
		vmr = round(vmr1,3);
		d_vmc_vmr = round(d_vmc_vmr1,3);
		area_t = round(area_t1,3);
		area_c = round(area_c1,3);
		area_r = round(area_r1,3);
	}
	
	private boolean micro = false;
	public void convertPeakTime(boolean to_micro) {
		if (to_micro == true) {
			if (micro == false) {
				tcr *= 1000;
				tc  *= 1000;
				tr *= 1000;
				tc_vmc *= 1000 ;
				tc_vmc_min *= 1000;
				tr_vmr *= 1000;
				tr_vmr_b *= 1000;
				t_vmc_vmr *= 1000;
				micro = true;
			}
		} else  {
			if (micro == true) {
				tcr /= 1000;
				tc  /= 1000;
				tr /= 1000;
				tc_vmc /= 1000 ;
				tc_vmc_min /= 1000;
				tr_vmr /= 1000;
				tr_vmr_b /= 1000;
				t_vmc_vmr /= 1000;
				micro = false;
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
	    // self check
	    if (this == o)
	        return true;
	    // null check
	    if (o == null)
	        return false;
	    // type check and cast
	    if (getClass() != o.getClass())
	        return false;
	    Peak person = (Peak) o;
	    // field comparison	    
	    return Objects.equals(this.getF_point(), person.getF_point())
	    		&& Objects.equals(this.getSec_point(), person.getSec_point())
	    		&& Objects.equals(this.getMid_point(), person.getMid_point())
	    		&& Objects.equals(this.getFourth_point(), person.getFourth_point())
	            && Objects.equals(this.getEnd_point(), person.getEnd_point());
	}
	
	public int getF_point() {
		return f_point;
	}

	public void setF_point(int f_point) {
		this.f_point = f_point;
	}

	public int getEnd_point() {
		return end_point;
	}

	public void setEnd_point(int end_point) {
		this.end_point = end_point;
	}

	public int getMid_point() {
		return mid_point;
	}

	public void setMid_point(int mid_point) {
		this.mid_point = mid_point;
	}

	public int getSec_point() {
		return sec_point;
	}

	public void setSec_point(int sec_point) {
		this.sec_point = sec_point;
	}

	public int getFourth_point() {
		return fourth_point;
	}

	public void setFourth_point(int fourth_point) {
		this.fourth_point = fourth_point;
	}

	public double getTcr() {
		return tcr;
	}

	public void setTcr(double tcr) {
		this.tcr = tcr;
	}

	public double getTc() {
		return tc;
	}

	public void setTc(double tc) {
		this.tc = tc;
	}

	public double getTr() {
		return tr;
	}

	public void setTr(double tr) {
		this.tr = tr;
	}

	public double getTc_vmc() {
		return tc_vmc;
	}

	public void setTc_vmc(double tc_vmc) {
		this.tc_vmc = tc_vmc;
	}

	public double getTc_vmc_min() {
		return tc_vmc_min;
	}

	public void setTc_vmc_min(double tc_vmc_min) {
		this.tc_vmc_min = tc_vmc_min;
	}

	public double getTr_vmr() {
		return tr_vmr;
	}

	public void setTr_vmr(double tr_vmr) {
		this.tr_vmr = tr_vmr;
	}

	public double getTr_vmr_b() {
		return tr_vmr_b;
	}

	public void setTr_vmr_b(double tr_vmr_b) {
		this.tr_vmr_b = tr_vmr_b;
	}

	public double getT_vmc_vmr() {
		return t_vmc_vmr;
	}

	public void setT_vmc_vmr(double t_vmc_vmr) {
		this.t_vmc_vmr = t_vmc_vmr;
	}

	public double getVmc() {
		return vmc;
	}

	public void setVmc(double vmc) {
		this.vmc = vmc;
	}

	public double getVmin() {
		return vmin;
	}

	public void setVmin(double vmin) {
		this.vmin = vmin;
	}

	public double getVmr() {
		return vmr;
	}

	public void setVmr(double vmr) {
		this.vmr = vmr;
	}

	public double getD_vmc_vmr() {
		return d_vmc_vmr;
	}

	public void setD_vmc_vmr(double d_vmc_vmr) {
		this.d_vmc_vmr = d_vmc_vmr;
	}

	public double getArea_t() {
		return area_t;
	}

	public void setArea_t(double area_t) {
		this.area_t = area_t;
	}

	public double getArea_c() {
		return area_c;
	}

	public void setArea_c(double area_c) {
		this.area_c = area_c;
	}

	public double getArea_r() {
		return area_r;
	}

	public void setArea_r(double area_r) {
		this.area_r = area_r;
	}

	public double getStart_value() {
		return start_value;
	}

	public void setStart_value(double start_value) {
		this.start_value = start_value;
	}

	public double getEnd_value() {
		return end_value;
	}

	public void setEnd_value(double end_value) {
		this.end_value = end_value;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public IntervalMarker getThisMarker() {
		return thisMarker;
	}

	public void setThisMarker(IntervalMarker thisMarker) {
		this.thisMarker = thisMarker;
	}
}
