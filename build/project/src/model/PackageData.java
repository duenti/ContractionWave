package model;

import static org.bytedeco.javacpp.opencv_core.extractChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bytedeco.javacpp.opencv_core.Mat;

public class PackageData  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private PlotPreferences plot_preferences;
	
	private Groups current_groups;
	private ExecutorService exec;
	private List<Integer> listPoints;
	private List<Mat> listFlows;
	private List<float[]> listMags;
	private int cores = 1;
	private boolean load_preferences;

	private double delta = 1.0;

	private double inter = 0.1;

	private double intra = 1.0;
	
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	public boolean isLoad_preferences() {
		return load_preferences;
	}

	public void setLoad_preferences(boolean load_preferences) {
		this.load_preferences = load_preferences;
	}

	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
    }
	
	public PackageData(Boolean load_pref) throws IOException, ClassNotFoundException {
		exec = Executors.newFixedThreadPool(cores);
		this.setLoad_preferences(load_pref);
		/*
		exec = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r);
			t.setDaemon(true); // allows app to exit if tasks are running
		return t ;
		});
		 */
		File tmpDir = new File(getInitialDirectory().toFile().getAbsolutePath() + "/preferences.pref");
		boolean exists = tmpDir.exists();
		if (exists == true && load_pref == true) {
	        FileInputStream fin = new FileInputStream(tmpDir);
			ObjectInputStream oin = new ObjectInputStream(fin);
			PlotPreferences readCase = (PlotPreferences) oin.readObject();
			this.setPlot_preferences(readCase);
			fin.close();
			oin.close();
		} else {
			this.setPlot_preferences(new PlotPreferences()); 
		}
		
		listPoints = new ArrayList<Integer>();
		listFlows = new ArrayList<Mat>();
		listMags = new ArrayList<float[]>();
		this.current_groups = new Groups();
		System.out.println(String.valueOf(this.current_groups.size()));
	}
	
	public ExecutorService getExec() {
		return exec;
	}
	
	public Groups getCurrent_groups() {
		return current_groups;
	}
	
	public void addNew_groups(Groups new_groups) {
		this.current_groups.add(new_groups);
	}

	public void setCurrent_groups(Groups current_groups) {
		this.current_groups = current_groups;
	}
	
	public void runGroups(boolean save_data) {
		for (int i = 0;i <this.current_groups.size(); i++) {
			Group g = current_groups.get(i);
			if (!g.getStatus().equals("Done")) {
				CalculationTask new_task = new CalculationTask(g, save_data);
				
				new_task.setOnFailed(e -> {
//					String group_name = e.getSource().getMessage();
					System.out.println("Mistake occured");
					Throwable thingThatWentWrong = new_task.getException();
					thingThatWentWrong.getCause().printStackTrace();
					System.out.println("Mistake occured");
					System.out.println("Teste Marcelo");
				});
				exec.execute(new_task);
			}
		}
	}
	
	public void runGetMatrices(Group group, PackageData parentPackage, int to1, int from1, int step1) {
		CalculationTaskSave new_task = new CalculationTaskSave(group, parentPackage, to1, from1, step1,0);
		exec.submit(new_task);
	}

	public List<Mat> getListFlows() {
		return listFlows;
	}
	
	public Mat getXflow(int i){
		Mat x_flow = new Mat();
		extractChannel(listFlows.get(i), x_flow, 0);
		return x_flow;
	}
	
	public Mat getYflow(int i){
		Mat y_flow = new Mat();
		extractChannel(listFlows.get(i), y_flow, 1);
		return y_flow;
	}

	public List<float[]> getListMags() {
		return listMags;
	}

	public void addListFlows(Mat flow) {
		this.listFlows.add(flow);
	}
	
	public void addListMags(float[] averages) {
		this.listMags.add(averages);
	}

	public List<Integer> getListPoints() {
		return listPoints;
	}

	public void setListPoints(List<Integer> listPoints) {
		this.listPoints = listPoints;
	}
	
	public void addListPoints(int a) {
		this.listPoints.add(a);
	}
	
	public void resetListsMag() {
		this.listPoints.clear();
		this.listMags.clear();
		this.listFlows.clear();
	}

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		exec.shutdown();
		this.cores = cores;
		if(current_groups.size() < cores)
			exec = Executors.newFixedThreadPool(current_groups.size());
		else
			exec = Executors.newFixedThreadPool(cores);
	}

	public PlotPreferences getPlot_preferences() {
		return plot_preferences;
	}

	public void setPlot_preferences(PlotPreferences plot_preferences) {
		this.plot_preferences = plot_preferences;
	}
	
	public void setDelta(double delta) {
		// TODO Auto-generated method stub
		this.delta = delta;
	}

	public void setInter(double inter) {
		// TODO Auto-generated method stub
		this.inter = inter;
	}

	public void setIntra(double intra) {
		// TODO Auto-generated method stub
		this.intra = intra;
	}
	

	public double getDelta() {
		// TODO Auto-generated method stub
		return this.delta;
	}

	public double getInter() {
		// TODO Auto-generated method stub
		return this.inter;
	}

	public double getIntra() {
		// TODO Auto-generated method stub
		return this.intra;
	}
}