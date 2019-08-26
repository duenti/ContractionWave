package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bytedeco.javacpp.opencv_core.Mat;

public abstract class Group implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<String> paths;
	protected List<Mat> matrices;
	protected List<Mat> flowList;
	protected List<Double> magList;
	protected List<Double> magListCopy;
	protected String name;
	private int type; //0 for Image; 1 for Video
	protected double pyrScale;
	protected int levels;
	protected int winSize;
	protected int iterations;
	protected int polyN;
	protected double polySigma;
	protected String status;
	protected String savestatus;
	protected double progress;
	protected String remainingTime;
	protected String errorMessage;
	
	protected Group(String pname, String ppath, int ptype){
		paths = new ArrayList<String>();
		matrices = new ArrayList<Mat>();
		flowList = new ArrayList<Mat>();
		magList = new ArrayList<Double>();
		magListCopy = new ArrayList<Double>();
		name = pname;
		setType(ptype);
		paths.add(ppath);
		status = "Queued";
		savestatus = "Queued";
		progress = 0;
		remainingTime = "Infinite";
		errorMessage = "";
	}
	protected Group(String pname,List<String> ppath, int ptype){
		matrices = new ArrayList<Mat>();
		flowList = new ArrayList<Mat>();
		magList = new ArrayList<Double>();
		magListCopy = new ArrayList<Double>();
		name = pname;
		paths = ppath;
		setType(ptype);
		status = "Queued";
		savestatus = "Queued";
		progress = 0;
		remainingTime = "Infinite";
		errorMessage = "";
	}
	
//	public abstract void calculateMagnitudes(ProgressBar test, Label lblRunPerc, double pyrScale, int levels, int winSize, int iterations, int polyN, double polySigma) throws Exception;
	public abstract void setParameters(double pyrScale, int levels, int winSize, int iterations, int polyN, double polySigma);	
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract int size();
	
	public List<String> getPaths(){
		return paths;
	}
	
	public void setSavestatus(String status) {
		this.savestatus = status;
	}
	
	public String getSavestatus() {
		return savestatus;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Double> getMagnitudeList(){
		return magList;
	}
	
	public void setMagnitudeList(List<Double> magList1){
		magList.clear();
		magList= magList1.stream().collect(Collectors.toList());
	}
	
	public double getMagnitudeListValue(int i){
		return magList.get(i);
	}
	
	public void clearFlowList(){
		flowList.clear();
		magList.clear();
	}
	
	public int getNumberOfFrames(){
		return matrices.size();
	}
	
	public int getMagnitudeSize(){
		return magList.size();
	}
	
	public void clearMagnitudeList(){
		magList.clear();
	}
	
	public void addToMagnitudeList(double magAverage){
		magList.add(magAverage);
	}
	
	public void convoluteMagnitudeList(int n_ave, double ave_value) {
//		Collections.copy(magListCopy , magList);
		magListCopy = magList.stream().collect(Collectors.toList());
		double max_first_val = Collections.max(magListCopy);
		List<Double> new_value_list = new ArrayList<Double>();
//		int n_ave = 5;
//		double ave_value = 0.24390244; //smaller vector so convolution == cross-correlation
		for (int i = -n_ave; i < magList.size() + n_ave; i++) { //boundaries
			double new_val = 0.0;
			for (int j = i; j < i + n_ave; j++) { //sliding window
				if (j >= 0 && j < magList.size() ) { 
					new_val += (ave_value * magList.get(j)) / n_ave;
				} else if (j < 0) { //negative indexes mean 0
					new_val += (ave_value * magList.get(0)) / n_ave;
				} else {
					new_val += (ave_value * magList.get(magList.size()-1)) / n_ave;
				}
			}
			new_value_list.add(new_val);
		}
        List<Double> new_value_list2 = new ArrayList<Double>();
		for (int z = n_ave-2; z < magList.size() + n_ave-2; z++) { //same mode
			new_value_list2.add(new_value_list.get(z));
		}
//		double max_second_val = Collections.max(new_value_list2);
//		double diff_max = max_second_val - max_first_val;
		magList= new_value_list2; //.stream().map(i -> i - diff_max).collect(Collectors.toList());
	}
	
	public void restoreMagnitudeList() {
		magList= magListCopy.stream().collect(Collectors.toList());
		magListCopy.clear();
	}
		
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(!Group.class.isAssignableFrom(obj.getClass())){
			return false;
		}
		
		final Group other = (Group) obj;
		if ((this.paths == null) ? (other.paths != null) : !this.paths.equals(other.paths)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = 1;
		for(String path : paths){
			hash = 31*hash + (path==null ? 0 : path.hashCode());
		}
		return hash;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getPyrScale() {
		return this.pyrScale;
	}
	public double getPolySigma() {
		return this.polySigma;
	}
	public int getLevels() {
		return this.levels;
	}
	public int getWinSize() {
		return this.winSize;
	}
	public int getIterations() {
		return this.iterations;
	}
	public int getPolyN() {
		return this.polyN;
	}
	public void setProgress(double v){
		this.progress = v;
	}
	public double getProgress(){
		return this.progress;
	}
	public String getRemainingTime(){
		return this.remainingTime;
	}
	public void setRemainingTime(String v){
		this.remainingTime = v;
	}
	public String getErrorMessage(){
		return errorMessage;
	}
	public void setPath(String directory){
		paths.clear();
		paths.add(directory);
	}
}