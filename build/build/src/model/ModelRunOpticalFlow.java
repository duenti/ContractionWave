package model;


import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_video.*;

import java.io.File;
import java.nio.FloatBuffer;

import org.bytedeco.javacpp.opencv_core.Mat;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;

public class ModelRunOpticalFlow {
	private String image1_path;
	private String image2_path;
	private Float magaverage;
	private Mat image1;
	private Mat image2;
	private Mat flow = new Mat();
	private long totaltime;
	private long totalfileSize;
	
    public ModelRunOpticalFlow(String image1f, String image2f) {
    	this.setImage1_path(image1f);
    	this.setImage2_path(image2f);
		Mat image1 = imread(image1f);
		Mat image2 = imread(image2f);
		cvtColor(image1, image1, COLOR_BGR2GRAY);
		cvtColor(image2, image2, COLOR_BGR2GRAY);
		long startTime = System.nanoTime();
    	this.setImage1(image1);
    	this.setImage2(image2);
    	this.calcOpticalFlow();    	
    	Mat this_flow = this.getFlow();
    	Mat x_flow = new Mat();
    	Mat y_flow = new Mat();
    	Mat mag = new Mat();
    	Mat ang = new Mat();
    	
    	extractChannel(this_flow, x_flow, 0);
    	extractChannel(this_flow, y_flow, 1);
    	cartToPolar(x_flow, y_flow, mag, ang);
    	Mat floatMags = new Mat();
    	mag.convertTo(floatMags, CV_32F);
    	FloatBuffer floatBufferMag = floatMags.createBuffer();
    	float[] floatArrayMag = new float[floatBufferMag.capacity()];
    	floatBufferMag.get(floatArrayMag);
    	float sum = 0;
    	for(float izx : floatArrayMag) {       
    	    sum += izx;
    	}
    	System.out.println(image1f + "_" + image2f + "__" + String.valueOf(java.lang.Math.abs(sum / floatArrayMag.length)));
    	this.setMagaverage(java.lang.Math.abs(sum / floatArrayMag.length));
    	
    	
//    	System.out.println("String.valueOf(this_flow.depth())");
//    	System.out.println(String.valueOf(this_flow.depth()));
//    	System.out.println("String.valueOf(this_flow.rows())");
//    	System.out.println(String.valueOf(this_flow.rows()));
//    	System.out.println("String.valueOf(this_flow.cols())");
//    	System.out.println(String.valueOf(this_flow.cols()));
//    	System.out.println("String.valueOf(this_flow.channels())");
//    	System.out.println(String.valueOf(this_flow.channels()));

//    	System.out.println("String.valueOf(channels.depth())");
//    	System.out.println(String.valueOf(channels.depth()));
//    	System.out.println("String.valueOf(channels.rows())");
//    	System.out.println(String.valueOf(channels.rows()));
//    	System.out.println("String.valueOf(channels.cols())");
//    	System.out.println(String.valueOf(channels.cols()));
//    	System.out.println("String.valueOf(channels.channels())");
//    	System.out.println(String.valueOf(channels.channels()));
    	
//    	Mat floatMat = new Mat();
//    	channels.convertTo(floatMat, CV_32F);
//    	FloatBuffer floatBuffer = floatMat.createBuffer();
//    	float[] floatArray = new float[floatBuffer.capacity()];
//    	floatBuffer.get(floatArray);
    	
    	//this_flow.data(this_flow.rows() * this_flow.cols() * this_flow.channels() + this_flow.cols() * this_flow.channels());
    	
    	//R = this_flow.data[row * src.cols * src.channels() + col * src.channels()];
    	
//    	System.out.println("Float Buffer");
//    	System.out.println(String.valueOf(floatArray.length));
    	//cartToPolar();
//    	cartToPolar();
//    	for (int zx = 0; zx < floatArray.length; zx++) {
//    		System.out.print(String.valueOf(floatArray[zx]));
//    	}
    	
//    	Mat channels = new Mat(2);
//    	split(this_flow, channels);
//    	UMat u = channels.getUMat(0);
//    	Mat v = channels.get(1);
    	
//        System.out.println(this_flow.dims());
//        System.out.println(this_flow.rows());
//        System.out.println(this_flow.cols());
    	//ArrayList<Mat> channels = new ArrayList<Mat>(2);
//    	Vector<Mat> channels(3);
//    	org.bytedeco.javacpp.opencv_core.split(this_flow, channels);
    	
//    	this_flow.arrayChannels();
//        for (int j = 0; j < this_flow.rows(); j++) {
//        	System.out.println("Row: " + String.valueOf(j) + " ");
//        	for (int jk = 0; jk < this_flow.cols(); jk++) {
//        		System.out.print(this_flow.row(j).col(jk) + ", ");
//        		//Point a = this_flow.;
//        	}
//        }
    	long estimatedTime = System.nanoTime() - startTime;
    	this.setTotaltime(estimatedTime);
    	this.setTotalfileSize(this.totalpairsize());
    }
	
	public long totalpairsize() {
    	File f = new File(this.getImage1_path().toString());
    	File f2 = new File(this.getImage2_path().toString());
    	long f_size = f.length();
    	long f_size2 = f2.length();
		return f_size + f_size2;
	}
	
	public void calcOpticalFlow() {
		calcOpticalFlowFarneback(this.getImage1(), this.getImage2(), this.getFlow(), 0.5, 1, 15, 1, 7, 1.5, 0);
	}


	public Mat getFlow() {
		return flow;
	}


	public void setFlow(Mat flow) {
		this.flow = flow;
	}


	public Mat getImage1() {
		return image1;
	}


	public void setImage1(Mat image1) {
		this.image1 = image1;
	}


	public Mat getImage2() {
		return image2;
	}


	public void setImage2(Mat image2) {
		this.image2 = image2;
	}

	public long getTotaltime() {
		return totaltime;
	}

	public void setTotaltime(long totaltime) {
		this.totaltime = totaltime;
	}

	public long getTotalfileSize() {
		return totalfileSize;
	}

	public void setTotalfileSize(long totalfileSize) {
		this.totalfileSize = totalfileSize;
	}

	public String getImage1_path() {
		return image1_path;
	}

	public void setImage1_path(String image1_path) {
		this.image1_path = image1_path;
	}

	public String getImage2_path() {
		return image2_path;
	}

	public void setImage2_path(String image2_path) {
		this.image2_path = image2_path;
	}

	public Float getMagaverage() {
		return magaverage;
	}

	public void setMagaverage(Float magaverage) {
		this.magaverage = magaverage;
	}
	
}
