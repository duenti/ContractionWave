package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.chart.plot.IntervalMarker;

public class IntervalPeak {
	private List<Peak> thesePeaks = new ArrayList<Peak>();

	private IntervalMarker thisMarker;
	private int startInterval;
	private int endInterval;
	private double fps_val;
	private double average_value;
	private double pixel_val;
	
	public IntervalPeak(Group currentGroup, double average_value1, double fps_val1, double pixel_val1, IntervalMarker e, List<Integer> maximum_list, List<Integer> minimum_list, List<Integer> first_points, List<Integer> fifth_points) {
		setThisMarker(e);
		//first find start value
		fps_val = fps_val1;
		pixel_val = pixel_val1;
		average_value = average_value1;
		int start = -1;
		double start_val = e.getStartValue();
		for (int i = 0; i < currentGroup.getMagnitudeSize(); i ++){
			double time = i / fps_val;
			if (time >= start_val) {
				start = i;
				break;
			}
		}
//		start = (int)Math.floor(start_val);
		setStartInterval(start);
		//then end value
		int end = -1;
		double end_val = e.getEndValue();
		for (int i = currentGroup.getMagnitudeSize();  i >= 0; i --){
			double time = i / fps_val;
			if (time < end_val) {
				end = i;
				break;
			}
		}
//		end = (int)Math.ceil(end_val);
		setEndInterval(end);
		System.out.println("Model start");
		System.out.println(start);
		System.out.println(start_val);
		System.out.println("Model end");
		System.out.println(end);
		System.out.println(end_val);
		boolean count_start = false; 
		boolean count_end = false;
		boolean first_done = false;
		int f_point = -1;
		int end_point = -1;
		int mid_point = -1;
		int sec_point = -1;
		int fourth_point = -1;
		int last_sec_point_index = 0;
		int last_third_point_index = 0;
		int last_fourth_point_index = 0;
		int last_fifth_point_index = 0;
		for (int f = 0; f < first_points.size(); f++) {
			int first_point = first_points.get(f);
			if (first_point >= start && first_point <= end) {
				System.out.println("Found first point!");
				System.out.println(first_point);
				//look for second, third, fourth and fifth points
				int possible_second = -1;
				for (int m = last_sec_point_index; m < maximum_list.size(); m++) {
					int putative_max = maximum_list.get(m);
					if (putative_max > first_point) {
						possible_second = putative_max;
						last_sec_point_index  = m;
						break;
					}
				}
				if (possible_second == -1) {
					continue;
				}
				int possible_third = -1;
				for (int mi = last_third_point_index; mi < minimum_list.size(); mi++) {
					int putative_min = minimum_list.get(mi);
					if (putative_min > possible_second) {
						possible_third = putative_min;
						last_third_point_index  = mi;
						break;
					}
				}
				if (possible_third == -1) {
					continue;
				}
				int possible_fourth = -1;
				for (int mx = last_fourth_point_index; mx < maximum_list.size(); mx++) {
					int putative_max2 = maximum_list.get(mx);
					if (putative_max2 > possible_third) {
						possible_fourth = putative_max2;
						last_fourth_point_index  = mx;
						break;
					}
				}
				if (possible_fourth == -1) {
					continue;
				}
				int possible_fifth = -1;
				for (int l = last_fifth_point_index; l < fifth_points.size(); l++) {
					int putative_last = fifth_points.get(l);
					if (putative_last > possible_fourth) {
						possible_fifth = putative_last;
						last_fifth_point_index  = l;
						break;
					}
				}
				if (possible_fifth == -1) {
					continue;
				}
				//create Peak now
				double tcr = ((double) (possible_fifth - first_point)) / fps_val;
				double tc = ((double) (possible_third - first_point))  / fps_val;
				double tr = ((double) (possible_fifth - possible_third))  / fps_val;
				double tc_vmc = ((double) (possible_second - first_point))  / fps_val;
				double tc_vmc_min = ((double) (possible_third - possible_second))  / fps_val;
				double tr_vmr = ((double) (possible_fourth - possible_third)) / fps_val;
				double tr_vmr_b = ((double) (possible_fifth - possible_fourth))  / fps_val;
				double t_vmc_vmr = ((double) (possible_fourth - possible_second))  / fps_val;	
				//calculate speed parameters
				double vmc = (currentGroup.getMagnitudeListValue(possible_second) * fps_val * pixel_val - average_value );
				double vmin = (currentGroup.getMagnitudeListValue(possible_third) * fps_val * pixel_val - average_value );
				double vmr = (currentGroup.getMagnitudeListValue(possible_fourth) * fps_val * pixel_val - average_value);
				double d_vmc_vmr = vmc - vmr;
				
				//calculate area parameters by trapezoidal rule]
				double area_t = getIntegralFromList(currentGroup.getMagnitudeList().subList(first_point, possible_fifth+1).stream().map(n -> (n * fps_val * pixel_val) - average_value).collect(Collectors.toList()), 1.0);
				double area_c = getIntegralFromList(currentGroup.getMagnitudeList().subList(first_point, possible_third+1).stream().map(n -> (n * fps_val * pixel_val) - average_value).collect(Collectors.toList()), 1.0);
				double area_r = getIntegralFromList(currentGroup.getMagnitudeList().subList(possible_third, possible_fifth+1).stream().map(n -> (n * fps_val * pixel_val) - average_value).collect(Collectors.toList()), 1.0);
				//allow border redefinition in plot
				//TODO generate new parker by changing start and end of marker to first_point - 1 to last_point + 1, check if next graphs are gen correct
				Peak v = new Peak(first_point, possible_fifth, possible_third, possible_second, possible_fourth, e.getStartValue() , e.getEndValue() , tcr, tc, tr, tc_vmc, tc_vmc_min, tr_vmr, tr_vmr_b, t_vmc_vmr, vmc, vmin, vmr, d_vmc_vmr, area_t, area_c, area_r, thisMarker);
				if(!this.getThesePeaks().contains(v)) {
					System.out.println("Added new peak");
					this.getThesePeaks().add(v);
				}
				System.out.println("Found fifth point!");
				System.out.println(possible_fifth);
			}
		}
		
//		for (int g = start; g <= end; g++) {
//			if (first_points.contains(g)) {
//				//start count until fifth point
//				count_start = true;
//				count_end = false;
//				f_point = g;
//			} else if (minimum_list.contains(g)) {
//				mid_point = g;
//			} else if (maximum_list.contains(g)) {
//				if (first_done == false) {
//					sec_point = g;
//					first_done = true;
//				} else {
//					fourth_point = g;
//				}
//			} else if (fifth_points.contains(g)) {
//				//end count until fifth point
//				count_start = false;
//				count_end = true;
//				end_point = g;
//			}
//			
//			if (count_start == true) {
//			} else if ((count_end == true) && (f_point > -1) && (sec_point > -1) && (mid_point > -1) && (fourth_point > -1) && (end_point > -1) ){
//				//calculate time parameters
//				double tcr = ((double) (end_point - f_point)) / fps_val;
//				double tc = ((double) (mid_point - f_point))  / fps_val;
//				double tr = ((double) (end_point - mid_point))  / fps_val;
//				double tc_vmc = ((double) (sec_point - f_point))  / fps_val;
//				double tc_vmc_min = ((double) (mid_point - sec_point))  / fps_val;
//				double tr_vmr = ((double) (fourth_point - mid_point)) / fps_val;
//				double tr_vmr_b = ((double) (end_point - fourth_point))  / fps_val;
//				double t_vmc_vmr = ((double) (fourth_point - sec_point))  / fps_val;	
//				//calculate speed parameters
//				double vmc = currentGroup.getMagnitudeListValue(sec_point);
//				double vmin = currentGroup.getMagnitudeListValue(mid_point);
//				double vmr = currentGroup.getMagnitudeListValue(fourth_point);
//				double d_vmc_vmr = vmc - vmr;
//				
//				//calculate area parameters by trapezoidal rule]
//				double area_t = getIntegralFromList(currentGroup.getMagnitudeList().subList(f_point, end_point+1), 1.0);
//				double area_c = getIntegralFromList(currentGroup.getMagnitudeList().subList(f_point, mid_point+1), 1.0);
//				double area_r = getIntegralFromList(currentGroup.getMagnitudeList().subList(mid_point, end_point+1), 1.0);
//				//allow border redefinition in plot
//				Peak v = new Peak(f_point, end_point, mid_point, sec_point, fourth_point, e.getStartValue() , e.getEndValue() , tcr, tc, tr, tc_vmc, tc_vmc_min, tr_vmr, tr_vmr_b, t_vmc_vmr, vmc, vmin, vmr, d_vmc_vmr, area_t, area_c, area_r, thisMarker);
//				thesePeaks.add(v);
//				first_done = false;
//				f_point = -1;
//				end_point = -1;
//				mid_point = -1;
//				sec_point = -1;
//				fourth_point = -1;
//				count_start = false;
//				count_end = false;
//			}
//		}
	}
	
    public double getIntegralFromList(List<Double>ar, double xDist) {
    	double base = 0;
    	double prev = 0;
    	double triHeight = 0;
    	double rectHeight = 0;
    	double tri = 0;
    	double rect = 0;
    	double integral = 0;
    	for (int i = 0; i < ar.size(); i++) {
    		triHeight=Math.abs(ar.get(i)-prev); // get Height Triangle
    		tri = xDist*triHeight/2;    // get Area Triangle
    		if(ar.get(i)<=prev){
    			rectHeight = Math.abs(base-ar.get(i)); // get Height Rectangle
    		}else {
    			rectHeight = Math.abs(base-(ar.get(i)-triHeight)); // get Height Rectangle
    		}
    		rect = xDist*rectHeight;    // get Area Rectangle
    		integral += (rect + tri); // add Whole Area to Integral
    		prev=ar.get(i);
    	}
    	return integral;
    }

	public IntervalMarker getThisMarker() {
		return thisMarker;
	}
	
	public void setThisMarker(IntervalMarker thisMarker) {
		this.thisMarker = thisMarker;
	}

	public int getStartInterval() {
		return startInterval;
	}

	public void setStartInterval(int startInterval) {
		this.startInterval = startInterval;
	}

	public int getEndInterval() {
		return endInterval;
	}

	public void setEndInterval(int endInterval) {
		this.endInterval = endInterval;
	}
	
	public List<Peak> getThesePeaks() {
		return thesePeaks;
	}

	public void setThesePeaks(List<Peak> thesePeaks) {
		this.thesePeaks = thesePeaks;
	}

}
