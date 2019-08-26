package model;

import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_core.cartToPolar;
import static org.bytedeco.javacpp.opencv_core.extractChannel;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_video.calcOpticalFlowFarneback;

import java.io.File;
import java.nio.FloatBuffer;

import javax.swing.JOptionPane;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class CalculationTaskSave extends Task<Void>{
    private Group thisgroup;
    private double pyrScale;
    private double polySigma;
    private int levels;
    private int winSize;
    private int iterations;
    private int polyN;
    private int from;
    private int to;
    private int step;
    private PackageData parent;
    private int storeType; //0 FROM RAM, 1 FROM HD
    
    public CalculationTaskSave(Group group, PackageData parentPackage, int to1, int from1, int step1, int type) {
        this.thisgroup = group;
        this.pyrScale = group.getPyrScale();
        this.pyrScale = group.getPyrScale();
        this.polySigma = group.getPolySigma();
        this.levels = group.getLevels();
        this.winSize = group.getWinSize();
        this.iterations = group.getIterations();
        this.polyN = group.getPolyN();
        this.parent = parentPackage;
        this.to =  to1;
        this.from = from1;
        this.step = step1;
        this.storeType = type;
    }
    
	public void calculateFlowImage(int from, int to, int step){
		ImageGroup g1 = (ImageGroup) this.thisgroup;
//		for(int i = from; i < to-1; i=i+step){
		for(int i = from; i < to; i=i+step){
			File file1 = g1.getImages().get(i);
			File file2 = g1.getImages().get(i+1);
			Mat img1 = imread(file1.getAbsolutePath());
			cvtColor(img1, img1, COLOR_BGR2GRAY);
			Mat img2 = imread(file2.getAbsolutePath());
			cvtColor(img2, img2, COLOR_BGR2GRAY);
			
			Mat flow = new Mat();
			Mat x_flow = new Mat();
		   	Mat y_flow = new Mat();
		   	Mat mag = new Mat();
		   	Mat ang = new Mat();
			calcOpticalFlowFarneback(img1, img2, flow, this.pyrScale, this.levels, this.winSize, this.iterations, this.polyN, this.polySigma, 0);
//			flowList.add(flow);
			extractChannel(flow, x_flow, 0);
		   	extractChannel(flow, y_flow, 1);
		   	cartToPolar(x_flow, y_flow, mag, ang);
		   	Mat floatMags = new Mat();
		   	mag.convertTo(floatMags, CV_32F);
		   	FloatBuffer floatBufferMag = floatMags.createBuffer();
		   	float[] floatArrayMag = new float[floatBufferMag.capacity()];
		   	floatBufferMag.get(floatArrayMag);
//	    	System.out.println("Printing important INFO!");
//	    	System.out.println(floatMags.channels());
//	    	System.out.println(floatMags.capacity());
//	    	System.out.println(floatMags.rows());
//	    	System.out.println(floatMags.cols());
		   	
		   	int index = i;
		   	Platform.runLater(() -> {
		   		parent.addListPoints(index);
		   		//parent.addList_x_flow(x_flow);
		   		//parent.addList_y_flow(y_flow);
		   		parent.addListFlows(flow);
		   		parent.addListMags(floatArrayMag);
			});
		}
		return;
	}
	
	public void calculateFlowVideo(int from, int to, int step) throws Exception{
		VideoGroup g1 = (VideoGroup) this.thisgroup;
		File video = g1.getVideo();
		@SuppressWarnings("resource")
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(video.getAbsolutePath());
		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		frameGrabber.start();
		Frame frame1 = frameGrabber.grab();
		int N = frameGrabber.getLengthInFrames();
		for(int i = 0; i<N; i+=step){
			if(i > to) return;
			Mat img1 = converterToMat.convert(frame1);
			g1.setWidth(img1.cols());
			g1.setHeight(img1.rows());
//			g1.addFrame(img1);
			cvtColor(img1, img1, COLOR_BGR2GRAY);
			Frame frame2 = frameGrabber.grab();
			Mat img2 = converterToMat.convert(frame2);
			cvtColor(img2, img2, COLOR_BGR2GRAY);
			frame1 = frame2;
			
			if(i >= from && i < to){
				Mat flow = new Mat();
				Mat x_flow = new Mat();
		    	Mat y_flow = new Mat();
		    	Mat mag = new Mat();
		    	Mat ang = new Mat();
				calcOpticalFlowFarneback(img1, img2, flow, pyrScale, levels, winSize, iterations, polyN, polySigma, 0);
				extractChannel(flow, x_flow, 0);
		    	extractChannel(flow, y_flow, 1);
		    	cartToPolar(x_flow, y_flow, mag, ang);
		    	Mat floatMags = new Mat();
		    	mag.convertTo(floatMags, CV_32F);
//		    	System.out.println("Printing important INFO!");
//		    	System.out.println(floatMags.channels());
//		    	System.out.println(floatMags.capacity());
//		    	System.out.println(floatMags.rows());
//		    	System.out.println(floatMags.cols());		
		    	FloatBuffer floatBufferMag = floatMags.createBuffer();
		    	float[] floatArrayMag = new float[floatBufferMag.capacity()];
		    	floatBufferMag.get(floatArrayMag);
//		    	float sum = 0;
//		    	for(float izx : floatArrayMag) {       
//		    	    sum += izx;
//		    	}
		    	int index = i;
//		    	double magAverage = java.lang.Math.abs(sum / floatArrayMag.length);
//		    	System.out.println(magAverage);
		    	//magList.add(magAverage);
			   	Platform.runLater(() -> {
			   		System.out.println("Adding flow: " + String.valueOf(index));
			   		parent.addListPoints(index);
			   		//parent.addList_x_flow(x_flow);
			   		//parent.addList_y_flow(y_flow);
			   		parent.addListFlows(flow);
			   		parent.addListMags(floatArrayMag);
				});
			}			
		}
		frameGrabber.flush();
		frameGrabber.close();
		return;
	}
	
	@Override
	protected Void call() throws Exception {
		try {
			String name = this.thisgroup.getName();
			updateMessage(name);
			int type = this.thisgroup.getType();
			System.out.print("TIPO: ");
			System.out.println(type);
			if (type == 0) {
				calculateFlowImage(this.from, this.to, this.step);
			} else {
				calculateFlowVideo(this.from, this.to, this.step);
			}
			Group localgroup = this.thisgroup;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					localgroup.setSavestatus("Done");
	//				parent.setCurrentGroup(name);
				}
	        });
		} catch(Exception ex) {
			System.out.println("Marcelo erro teste 4");
			ex.getCause().printStackTrace();
			System.out.println("Marcelo erro teste 5");
			JOptionPane.showMessageDialog(null, "Could not run flow. Please try again", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public int getStoreType() {
		return storeType;
	}

	public void setStoreType(int type) {
		this.storeType = type;
	}
}