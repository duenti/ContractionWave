package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class IntervalPeaks implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<IntervalPeak> listIntervalPeaks;
	private List<Peak> listPeaks;
	private boolean seconds = true;
	
	public IntervalPeaks() {
		listIntervalPeaks = new ArrayList<IntervalPeak>();
		setListPeaks(new ArrayList<Peak>());
	}
	
	public void convertTime() {
		if (seconds == true) {
			for (Peak a : this.getListPeaks()) {
				a.convertPeakTime(seconds);
			}
			seconds = false;
		} else {
			for (Peak a : this.getListPeaks()) {
				a.convertPeakTime(seconds);
			}
			seconds = true;
		}
	}
	
	public void addIntervalPeak(IntervalPeak e) {
		listIntervalPeaks.add(e);
		for (Peak f : e.getThesePeaks()) {
			if(!listPeaks.contains(f)) {
				System.out.println("Added new peak 2");
				listPeaks.add(f);
			}
		}
	}
	
	public List<IntervalPeak> getListIntervalPeaks() {
		return listIntervalPeaks;
	}
	
	public ObservableList<Peak> getObservableList(){
		return FXCollections.observableArrayList(listPeaks);
	}

	public void setListIntervalPeaks(List<IntervalPeak> listIntervalPeaks) {
		this.listIntervalPeaks = listIntervalPeaks;
	}

	public List<Peak> getListPeaks() {
		return listPeaks;
	}

	public void setListPeaks(List<Peak> listPeaks) {
		this.listPeaks = listPeaks;
	}
	
}
