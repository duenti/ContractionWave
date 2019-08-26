package model;

import java.io.Serializable;
import java.util.List;

import org.jfree.chart.plot.IntervalMarker;


public class ContinueData implements Serializable {
	private static final long serialVersionUID = 1L;
	private int cores;
	private Groups gs;
	private Group currentGroup;
	private double fps_val;
	private double pixel_val;
	private double average_value;
	private double upper_limit;
	private List<IntervalMarker> intervalsList;
	private List<Integer> maximum_list;
	private List<Integer> minimum_list;
	private List<Integer> first_points;
	private List<Integer> fifth_points;
	private List<TimeSpeed> timespeedlist;
	private double delta;
	private double intra;
	private double inter;
	
	public ContinueData(Groups gs1, int cores1,  Group currentGroup1,  double fps_val1, double pixel_val1, double average_value1 , double upper_limit1, List<IntervalMarker> intervalsList1, List<Integer> maximum_list1,  List<Integer> minimum_list1, List<Integer> first_points1, List<Integer> fifth_points1, List<TimeSpeed> timespeedlist1, double delta1, double intra1, double inter1) {
		gs = gs1;
		cores = cores1;
		currentGroup = currentGroup1;
		fps_val = fps_val1;
		pixel_val = pixel_val1;
		upper_limit = upper_limit1;
		average_value = average_value1;
		intervalsList = intervalsList1;
		maximum_list = maximum_list1;
		minimum_list = minimum_list1;
		first_points = first_points1;
		fifth_points = fifth_points1;
		timespeedlist = timespeedlist1;
		delta = delta1;
		intra = intra1;
		inter = inter1;
	}

	public Group getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(Group currentGroup) {
		this.currentGroup = currentGroup;
	}

	public double getFps_val() {
		return fps_val;
	}

	public void setFps_val(double fps_val) {
		this.fps_val = fps_val;
	}

	public double getPixel_val() {
		return pixel_val;
	}

	public void setPixel_val(double pixel_val) {
		this.pixel_val = pixel_val;
	}

	public double getAverage_value() {
		return average_value;
	}

	public void setAverage_value(double average_value) {
		this.average_value = average_value;
	}

	public double getUpper_limit() {
		return upper_limit;
	}

	public void setUpper_limit(double upper_limit) {
		this.upper_limit = upper_limit;
	}

	public List<IntervalMarker> getIntervalsList() {
		return intervalsList;
	}

	public void setIntervalsList(List<IntervalMarker> intervalsList) {
		this.intervalsList = intervalsList;
	}

	public List<Integer> getMaximum_list() {
		return maximum_list;
	}

	public void setMaximum_list(List<Integer> maximum_list) {
		this.maximum_list = maximum_list;
	}

	public List<Integer> getMinimum_list() {
		return minimum_list;
	}

	public void setMinimum_list(List<Integer> minimum_list) {
		this.minimum_list = minimum_list;
	}

	public List<Integer> getFirst_points() {
		return first_points;
	}

	public void setFirst_points(List<Integer> first_points) {
		this.first_points = first_points;
	}

	public List<Integer> getFifth_points() {
		return fifth_points;
	}

	public void setFifth_points(List<Integer> fifth_points) {
		this.fifth_points = fifth_points;
	}

	public List<TimeSpeed> getTimeSpeedList() {
		return timespeedlist;
	}

	public Groups getGs() {
		return gs;
	}

	public void setGs(Groups gs) {
		this.gs = gs;
	}

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public double getIntra() {
		return intra;
	}

	public void setIntra(double intra) {
		this.intra = intra;
	}

	public double getInter() {
		return inter;
	}

	public void setInter(double inter) {
		this.inter = inter;
	}	
}
