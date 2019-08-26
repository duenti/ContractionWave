package controllers;

import static org.bytedeco.javacpp.opencv_core.CV_8U;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

//import controllers.Controller_3b2_DisplayResults.IncrementHandler;
import edu.mines.jtk.awt.ColorMap;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.Group;
import model.ImageGroup;
import model.PackageData;
import model.TimeSpeed;
import model.VideoGroup;
import model.XYCircleAnnotation;

public class Controller_3e_ViewJetQuiverMergeSingle2 implements Initializable {
	private int width = 0;
	private int height = 0;
	private String currentRenderType = "Jet";
	private boolean jet_state = false;
	private boolean quiver_state = false;
	private boolean merge_state = false;
	private IntervalMarker marker;
	private XYDataset general_dataset;
	private boolean contour_state = false;
	private double mask_value = 0.08;
	private double scale_start = 0.05;
	private double scale_end = 57.0;
	private int xwindow = 8;
	private int ywindow = 15;  
	private int global_min;
	public static final IndexColorModel JET = ColorMap.getJet();
	private ColorMap this_jet = new ColorMap(scale_start, scale_end, JET);
	private int current_index;
	private final int ARR_SIZE = 4; //size of triangle
	
    private PackageData main_package;
	private static Group currentGroup;
	private double fps_value;
	private double pixel_value;
	private double average_value;
	private double upper_limit;
	
	private List<TimeSpeed> timespeedlist;
	private List<IntervalMarker> intervalsList;
	private List<Integer> maximum_list;
	private List<Integer> minimum_list;
	private List<Integer> first_points;
	private List<Integer> fifth_points;
	private Timeline sliderTimer;
		
	@FXML
	void handleMenuNewImage(ActionEvent event) throws IOException{
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		if (sliderState == true) {
			sliderState = false;
			sliderTimer.stop();
		}
		if (imageDstate == true) {
			dialogImage.close();
		}
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		main_package.getListFlows().clear();
		FileReader.chooseSourceDirectory(primaryStage, url, main_package);
	}

	@FXML
	void handleMenuNewVideo(ActionEvent event) throws IOException{
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		if (sliderState == true) {
			sliderState = false;
			sliderTimer.stop();
		}
		if (imageDstate == true) {
			dialogImage.close();
		}
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		main_package.getListFlows().clear();
		FileReader.chooseVideoFiles(primaryStage, url, main_package);
	}

	@FXML
	void handleClose(ActionEvent event){
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		if (sliderState == true) {
			sliderState = false;
			sliderTimer.stop();
		}
		if (imageDstate == true) {
			dialogImage.close();
		}
		primaryStage.close();
	}

	@FXML
	void handleReinitialize(ActionEvent event) throws IOException, ClassNotFoundException{
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
		if (sliderState == true) {
			sliderState = false;
			sliderTimer.stop();
		}
		if (imageDstate == true) {
			dialogImage.close();
		}
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
		FXMLLoader fxmlloader = new FXMLLoader();
		fxmlloader.setLocation(url);
		fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
		Parent root;
		root = fxmlloader.load();
		Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
		main_package.getListFlows().clear();
		((Controller_1_InitialScreen)fxmlloader.getController()).setContext(new PackageData(main_package.isLoad_preferences()));
		primaryStage.setTitle("Image Optical Flow");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
	}

	@FXML
	void handleAbout(ActionEvent event) throws IOException{
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("FXML_About.fxml"));
		stage.setScene(new Scene(root));
		stage.setTitle("Image Optical Flow");
		stage.initModality(Modality.APPLICATION_MODAL);
		//stage.initOwner(((Node)event.getSource()).getScene().getWindow());
		stage.show();
	}

	
	private void waitCursor() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	Stage primaryStage;
		    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
		    	primaryStage.getScene().setCursor(Cursor.WAIT);
		    }
		});
	}
	
	private void returnCursor() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	Stage primaryStage;
		    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
		    	primaryStage.getScene().setCursor(Cursor.DEFAULT);
		    }
		});
	}
	
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
    }
	
	private boolean save_as_video = false;
	//private List<BufferedImage> videoImage = new ArrayList<BufferedImage>();
	
	private BufferedImage videoImage = null;
	
	void saveCurrentImageVideo(Image this_img) {
    	BufferedImage bImage = SwingFXUtils.fromFXImage(this_img, null);
    	videoImage = bImage;
	}
	
    void saveCurrentImageView(Image this_img) throws IOException {
    	System.out.println("Trying to save image 2");
        //Show save file dialog
        File file = new File(current_filename);
        System.out.println("New File: " + file.getAbsolutePath());
    	BufferedImage bImage = SwingFXUtils.fromFXImage(this_img, null);
    	if (this_img == null) {
    		System.out.println("Null Image");
    	}
    	if (bImage == null) {
    		System.out.println("Null Buffer");
    	}
    		System.out.println("Saving....");
    		Mat imgMat = Java2DFrameUtils.toMat(bImage);
    		
    		MatVector channels_a = new MatVector();
	        Mat finalMat = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(imgMat, channels_a);
	        Mat blueCh_a = channels_a.get(1);
	        Mat greenCh_a = channels_a.get(2);
	        Mat redCh_a = channels_a.get(3);
	        MatVector channels2_a = new MatVector(3);
	        channels2_a.put(0, redCh_a);
	        channels2_a.put(1, greenCh_a);
	        channels2_a.put(2, blueCh_a);
	        org.bytedeco.javacpp.opencv_core.merge(channels2_a, finalMat);
	        
    		imwrite(current_filename, finalMat);
    }
    
	@FXML
	void handleExportScale(ActionEvent event) throws IOException {
		Stage dialogScale = new Stage();    
		URL url = getClass().getResource("FXML_Scale.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
    	Parent root;
    	root = fxmlloader.load();
    	((Controller_Scale)fxmlloader.getController()).setContext(mask_value, scale_start, scale_end);
    	dialogScale.setScene(new Scene(root));
		dialogScale.setTitle("Save Magnitude Scale to Figure:");		
    	dialogScale.initModality(Modality.APPLICATION_MODAL);
    	dialogScale.initOwner(null);
    	dialogScale.setResizable(true);
    	dialogScale.show();
	}
    
    private String current_filename = "";
    
	@FXML
	void handleExportJetJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
//    	Stage dialog = new Stage();
//    	dialog.initOwner(primaryStage);
//    	dialog.initModality(Modality.APPLICATION_MODAL); 
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
	    	current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_Mag" +".jpg";
	    	renderImageView(i, "Jet", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportJetTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
//    	Stage dialog = new Stage();
//    	dialog.initOwner(primaryStage);
//    	dialog.initModality(Modality.APPLICATION_MODAL); 
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
	    	current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_Mag" +".tiff";
	    	renderImageView(i, "Jet", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}

	@FXML
	void handleExportJetPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
	    	current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_Mag" +".png";
	    	renderImageView(i, "Jet", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	public static BufferedImage convertBufToType (BufferedImage sourceImage, int targetType) {
        // if the source image is already the target type, return the source image
		BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
	}
	
	void genericVideoExportation(String type_do) throws IOException, InterruptedException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() + "_" + type_do.replace("Jet", "Mag").replace("Quiver", "Vect") + ".mp4";
        waitCursor();
        save_as_video = true;
        Rational framerate_do = Rational.make(1, (int)frameRate);
        Muxer muxer = Muxer.make(current_filename, null, "mp4");
        MuxerFormat format = muxer.getFormat();
        Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
//        System.out.println(codec.toString());
//        System.out.println(codec.getName());
        Encoder encoder = Encoder.make(codec);
        encoder.setWidth(width);
        encoder.setHeight(height);
//        System.out.println(width);
//        System.out.println(height);
//        System.out.println(encoder.getWidth());
//        System.out.println(encoder.getHeight());
        io.humble.video.PixelFormat.Type pixelformat = io.humble.video.PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate_do);
        encoder.setProperty("b", 0);
        //io.humble.video.Property
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }
        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);
        MediaPictureConverter converter = null;
        MediaPicture picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
        picture.setTimeBase(framerate_do);
        MediaPacket packet = MediaPacket.make();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			renderImageView(i, type_do, true);
			//add videoImage to created video
			 BufferedImage screen = convertBufToType(videoImage, BufferedImage.TYPE_3BYTE_BGR);
			 if (converter == null) {
				 converter = MediaPictureConverterFactory.createConverter(screen, picture);
			 }
			 converter.toPicture(picture, screen, i);
			 do {
				 encoder.encode(packet, picture);
				 if (packet.isComplete()) {
					 muxer.write(packet, false);
				 }
			 } while (packet.isComplete());
//			 Thread.sleep((long) (1000 * framerate_do.getDouble()));
		}
		do {
			encoder.encode(packet, null);
			if (packet.isComplete()) {
				muxer.write(packet, false);
			}
		} while (packet.isComplete());
		muxer.close();
		save_as_video = false;
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportJetAVI(ActionEvent event) throws IOException, InterruptedException {
		genericVideoExportation("Jet");
	}

	@FXML
	void handleExportMergeBothJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
	    	current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagVectMerge"  +".jpg";
	    	renderImageView(i, "JetQuiverMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportMergeBothTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
	    	current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagVectMerge"  +".tiff";
	    	renderImageView(i, "JetQuiverMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}

	@FXML
	void handleExportMergeBothPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagVectMerge" +".png";
	    	renderImageView(i, "JetQuiverMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportMergeBothAVI(ActionEvent event) throws IOException, InterruptedException {
		genericVideoExportation("JetQuiverMerge");
	}

	@FXML
	void handleExportMergeJetJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagMerge" +".jpg";
	    	renderImageView(i, "JetMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportMergeJetTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagMerge" +".tiff";
	    	renderImageView(i, "JetMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}

	@FXML
	void handleExportMergeJetPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagMerge" +".png";
	    	renderImageView(i, "JetMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportMergeJetAVI(ActionEvent event) throws IOException, InterruptedException {
		genericVideoExportation("JetMerge");
	}

	@FXML
	void handleExportMergeQuiverJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_VectMerge"  +".jpg";
	    	renderImageView(i, "QuiverMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportMergeQuiverTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_VectMerge"  +".tiff";
	    	renderImageView(i, "QuiverMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}

	@FXML
	void handleExportMergeQuiverPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i+ "_VectMerge" +".png";
	    	renderImageView(i, "QuiverMerge", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportMergeQuiverAVI(ActionEvent event) throws IOException, InterruptedException {
		genericVideoExportation("QuiverMerge");
	}

	@FXML
	void handleExportQuiverJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_Vect" +".jpg";
	    	renderImageView(i, "Quiver", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportQuiverTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_Vect" +".tiff";
	    	renderImageView(i, "Quiver", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportQuiverAVI(ActionEvent event) throws IOException, InterruptedException {
		genericVideoExportation("Quiver");
	}

	@FXML
	void handleExportQuiverPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_Vect" +".png";
	    	renderImageView(i, "Quiver", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportJetQuiverJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagVect"+".jpg";
	    	renderImageView(i, "JetQuiver", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportJetQuiverTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagVect"+".tiff";
	    	renderImageView(i, "JetQuiver", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportJetQuiverPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		for (int i = 0; i < general_dataset.getItemCount(0)-1; i++) {
			current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +i + "_MagVect" +".png";
	    	renderImageView(i, "JetQuiver", true);
		}
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportJetQuiverAVI(ActionEvent event) throws IOException, InterruptedException {
		genericVideoExportation("JetQuiver");
	}
	
	@FXML
	void handleExportCurrentJPG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		current_filename =chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +current_index + "_Current" +".jpg";
	    renderImageView(current_index, currentRenderType , true);
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportCurrentTIFF(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		current_filename =chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +current_index + "_Current" +".tiff";
	    renderImageView(current_index, currentRenderType , true);
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	@FXML
	void handleExportCurrentPNG(ActionEvent event) throws IOException {
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) sliderGroups.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
        waitCursor();
		current_filename = chosenDir.getAbsolutePath().toString() + "/"+ currentGroup.getName() +  "_" +current_index + "_Current" +".png";
	    renderImageView(current_index, currentRenderType , true);
		returnCursor();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
	}
	
	private boolean sliderState = false;
	@FXML
	void handlePlayStopButton(ActionEvent event){
		System.out.println(cmdSliderPlay.getText());
		if(cmdSliderPlay.getText().equals("Play")){
			System.out.println("Play");
			sliderState = true;
			sliderTimer.play();
			
			cmdSliderPlay.setText("Stop");
		}else{
			System.out.println("Stop");
			sliderState = false;
			sliderTimer.stop();
			
			cmdSliderPlay.setText("Play");
		}
	}
	
    @FXML
    private Button cmdBack;

    @FXML
    private ImageView imgview1;

    @FXML
    private Slider sliderGroups;

    @FXML
    private CheckBox quiverCheck;

    @FXML
    private CheckBox jetCheck;

    @FXML
    private CheckBox mergeCheck;

    @FXML
    private Button buttonJetAdvanced;

    @FXML
    private Spinner<Double> spinnerMask;

    @FXML
    private Spinner<Double> spinnerScaleStart;

    @FXML
    private Spinner<Double> spinnerScaleEnd;

    @FXML
    private Spinner<Integer> spinnerFPS;
    
    @FXML
    private ToggleButton togglebuttonContour;

    @FXML
    private Button buttonMergeAdvanced;

    @FXML
    private SwingNode chartNode;
	
	@FXML
	private Button cmdSliderPlay;
	
	@FXML
	private HBox emptyBoxSize;
    
    private JFreeChart currentChart;
	private double lowerBoundDomain;
	private double upperBoundDomain;
	private double alpha_under_two = 0.7;
//	private double alpha_above_two = 0.3;
//	private double alpha_under_three = 1.0;
//	private double alpha_above_three = 0.3;
	private boolean ask_saved;
	
	
    @FXML
    void handleColors(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	ShowAnnoMarkerColorThickDialog.showDialog(main_package, marker, plot, maximum_list, minimum_list, first_points, fifth_points, global_min);
    }
	
	private void commitColors() {
    	XYPlot plot = currentChart.getXYPlot();
    	main_package.getPlot_preferences().setSeriesColorRGB( (java.awt.Color) plot.getRenderer().getSeriesPaint(0));
    	main_package.getPlot_preferences().setRangeGridColorRGB( (java.awt.Color) plot.getRangeGridlinePaint());
    	main_package.getPlot_preferences().setDomainGridColorRGB( (java.awt.Color) plot.getDomainGridlinePaint());
    	main_package.getPlot_preferences().setBackgroundColorRGB( (java.awt.Color) plot.getBackgroundPaint());
//    	main_package.getPlot_preferences().setMarkerAlpha();
//    	main_package.getPlot_preferences().setMarkerColorRGB();
    }
	
    
    @FXML
    void back(ActionEvent event) throws ClassNotFoundException, IOException {
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
		if (sliderState == true) {
			sliderState = false;
			sliderTimer.stop();
		}
		if (imageDstate == true) {
			dialogImage.close();
		}
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_3d2_PeakParametersPlot.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	commitColors();
    	main_package.getListFlows().clear();
    	((Controller_3d2_PeakParametersPlot)fxmlloader.getController()).setContext(main_package, currentGroup, fps_value, pixel_value, average_value, upper_limit, intervalsList, maximum_list, minimum_list, first_points, fifth_points, timespeedlist, ask_saved);
    	primaryStage.setTitle("Image Optical Flow - Peak Parameters Plot");
//    	primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
    
    
        
    @FXML
    void showAdvancedJet(ActionEvent event) throws IOException {
    	Dialog<Boolean> dialogJet = new Dialog<>();
    	dialogJet.setHeaderText("Vectors X and Y Window Size Options (pixels):");
    	dialogJet.setResizable(true);
    	Label label1 = new Label("X Window: ");
    	Label label2 = new Label("Y Window: ");
    	Spinner<Integer> xwindowSpin = new Spinner<Integer>();
    	Spinner<Integer> ywindowSpin= new Spinner<Integer>();
    	
    	SpinnerValueFactory<Integer> dobX = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, xwindow, 1);
    	xwindowSpin.setValueFactory(dobX);
    	xwindowSpin.setEditable(true);
		TextFormatter<Integer> formatterX = new TextFormatter<Integer>(dobX.getConverter(), dobX.getValue());
		xwindowSpin.getEditor().setTextFormatter(formatterX);
		dobX.valueProperty().bindBidirectional(formatterX.valueProperty());
		formatterX.valueProperty().addListener((s, ov, nv) -> {
			xwindow = nv;
			try {
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
    	SpinnerValueFactory<Integer> dobY = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, ywindow, 1);
    	ywindowSpin.setValueFactory(dobY);
    	ywindowSpin.setEditable(true);
		TextFormatter<Integer> formatterY = new TextFormatter<Integer>(dobY.getConverter(), dobY.getValue());
		ywindowSpin.getEditor().setTextFormatter(formatterY);
		dobY.valueProperty().bindBidirectional(formatterY.valueProperty());
		formatterY.valueProperty().addListener((s, ov, nv) -> {
			ywindow = nv;
			try {
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(xwindowSpin, 2, 1);
    	grid.add(label2, 1, 2);
    	grid.add(ywindowSpin, 2, 2);
    	dialogJet.getDialogPane().setContent(grid);
    	ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
    	dialogJet.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	dialogJet.show();
    }
    

    @FXML
    void showAdvancedMerge(ActionEvent event) throws IOException {
    	Dialog<Boolean> dialogJet = new Dialog<>();
    	dialogJet.setHeaderText("Cell Segmentation Options:");
    	dialogJet.setResizable(true);
    	Label label1 = new Label("Blur Size: ");
    	Label label2 = new Label("Kernel Dilation: ");
    	Label label3 = new Label("Kernel Erosion: ");
    	Label label4 = new Label("Kernel Smoothing: ");
    	Label label5 = new Label("Border Width: ");
    	Label label6 = new Label("Sigma Value: ");
    	Label label7 = new Label("Magnitude Layer Alpha: ");
    	Spinner<Integer> blurSpin = new Spinner<Integer>();
    	Spinner<Integer> dilationSpin= new Spinner<Integer>();
    	Spinner<Integer> erosionSpin = new Spinner<Integer>();
    	Spinner<Integer> smoothingSpin= new Spinner<Integer>();
    	Spinner<Integer> borderSpin = new Spinner<Integer>();
    	Spinner<Double> sigmaSpin = new Spinner<Double>();
    	Spinner<Double> alphabackSpin = new Spinner<Double>();
    	
    	SpinnerValueFactory<Integer> intB = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, blur_size, 2);
    	blurSpin.setValueFactory(intB);
    	blurSpin.setEditable(true);
		TextFormatter<Integer> formatterB = new TextFormatter<Integer>(intB.getConverter(), intB.getValue());
		blurSpin.getEditor().setTextFormatter(formatterB);
		intB.valueProperty().bindBidirectional(formatterB.valueProperty());
		formatterB.valueProperty().addListener((s, ov, nv) -> {
			blur_size = nv;
			try {
				if(nv % 2 == 0){
					blur_size++;
					blurSpin.getValueFactory().setValue(blur_size);
				}
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				blur_size = 5;
				blurSpin.getValueFactory().setValue(blur_size);
				e.printStackTrace();
			}
		});
				
		SpinnerValueFactory<Integer> intD = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, kernel_dilation, 2);
    	dilationSpin.setValueFactory(intD);
    	dilationSpin.setEditable(true);
		TextFormatter<Integer> formatterD = new TextFormatter<Integer>(intD.getConverter(), intD.getValue());
		dilationSpin.getEditor().setTextFormatter(formatterD);
		intD.valueProperty().bindBidirectional(formatterD.valueProperty());
		formatterD.valueProperty().addListener((s, ov, nv) -> {
			kernel_dilation = nv;
			try {
				if(nv % 2 == 0){
					kernel_dilation++;
					dilationSpin.getValueFactory().setValue(kernel_dilation);
				}
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				kernel_dilation = 3;
				dilationSpin.getValueFactory().setValue(kernel_dilation);
				e.printStackTrace();
			}
		});
		
		SpinnerValueFactory<Integer> intE = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, kernel_erosion, 2);
    	erosionSpin.setValueFactory(intE);
    	erosionSpin.setEditable(true);
		TextFormatter<Integer> formatterE = new TextFormatter<Integer>(intE.getConverter(), intE.getValue());
		erosionSpin.getEditor().setTextFormatter(formatterE);
		intE.valueProperty().bindBidirectional(formatterE.valueProperty());
		formatterE.valueProperty().addListener((s, ov, nv) -> {
			kernel_erosion = nv;
			try {
				if(nv % 2 == 0){
					kernel_erosion++;
					erosionSpin.getValueFactory().setValue(kernel_erosion);
				}
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				kernel_erosion = 11;
				erosionSpin.getValueFactory().setValue(kernel_erosion);
				e.printStackTrace();
			}
		});
		
		SpinnerValueFactory<Integer> intS = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, kernel_smoothing_contours, 2);
    	smoothingSpin.setValueFactory(intS);
    	smoothingSpin.setEditable(true);
		TextFormatter<Integer> formatterS = new TextFormatter<Integer>(intS.getConverter(), intS.getValue());
		smoothingSpin.getEditor().setTextFormatter(formatterS);
		intS.valueProperty().bindBidirectional(formatterS.valueProperty());
		formatterS.valueProperty().addListener((s, ov, nv) -> {
			kernel_smoothing_contours = nv;
			if(nv % 2 == 0){
				kernel_smoothing_contours++;
				smoothingSpin.getValueFactory().setValue(kernel_smoothing_contours);
			}
			try {
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				kernel_smoothing_contours = 5;
				smoothingSpin.getValueFactory().setValue(kernel_smoothing_contours);
				e.printStackTrace();
			}
		});
		
		SpinnerValueFactory<Integer> intBor = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, border_value, 1);
		borderSpin.setValueFactory(intBor);
		borderSpin.setEditable(true);
		TextFormatter<Integer> formatterBor = new TextFormatter<Integer>(intBor.getConverter(), intBor.getValue());
		borderSpin.getEditor().setTextFormatter(formatterBor);
		intBor.valueProperty().bindBidirectional(formatterBor.valueProperty());
		formatterBor.valueProperty().addListener((s, ov, nv) -> {
			border_value = nv;
			try {
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				border_value = 60;
				borderSpin.getValueFactory().setValue(border_value);
				e.printStackTrace();
			}
		});	
		
		SpinnerValueFactory<Double> intSig = facGen(0, Double.MAX_VALUE, sigma, 0.01);
		sigmaSpin.setValueFactory(intSig);
		sigmaSpin.setEditable(true);
		TextFormatter<Double> formatterSig = new TextFormatter<Double>(intSig.getConverter(), intSig.getValue());
		sigmaSpin.getEditor().setTextFormatter(formatterSig);
		intSig.valueProperty().bindBidirectional(formatterSig.valueProperty());
		formatterSig.valueProperty().addListener((s, ov, nv) -> {
			sigma = nv;
			try {
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				sigma = 0.15;
				sigmaSpin.getValueFactory().setValue(sigma);
				e.printStackTrace();
			}
		});
		
		SpinnerValueFactory<Double> intAlp = facGen(0, 1.0, alpha_under_two, 0.01);
		alphabackSpin.setValueFactory(intAlp);
		alphabackSpin.setEditable(true);
		TextFormatter<Double> formatterAlp = new TextFormatter<Double>(intAlp.getConverter(), intAlp.getValue());
		alphabackSpin.getEditor().setTextFormatter(formatterAlp);
		intAlp.valueProperty().bindBidirectional(formatterAlp.valueProperty());
		formatterAlp.valueProperty().addListener((s, ov, nv) -> {
			alpha_under_two = nv;
			try {
				renderImageView(current_index, currentRenderType, false);
			} catch (Exception e) {
				alpha_under_two = 1.0;
				alphabackSpin.getValueFactory().setValue(alpha_under_two);
				e.printStackTrace();
			}
		});
		
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(blurSpin, 2, 1);
    	grid.add(label2, 1, 2);
    	grid.add(dilationSpin, 2, 2);
    	grid.add(label3, 1, 3);
    	grid.add(erosionSpin, 2, 3);
    	grid.add(label4, 1, 4);
    	grid.add(smoothingSpin, 2, 4);
    	grid.add(label5, 1, 5);
    	grid.add(borderSpin, 2, 5);
    	grid.add(label6, 1, 6);
    	grid.add(sigmaSpin, 2, 6);
    	grid.add(label7, 1, 7);
    	grid.add(alphabackSpin, 2, 7);
    	dialogJet.getDialogPane().setContent(grid);
    	ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
    	dialogJet.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	dialogJet.show();
    }

    @FXML
    void toggleContour(ActionEvent event) throws IOException {
    	contour_state = !contour_state;
    	renderImageView(current_index, currentRenderType , false);
    	if (contour_state == true) {
    		togglebuttonContour.setText("Contour Filter is ON");
    	} else {
    		togglebuttonContour.setText("Contour Filter is OFF");
    	}
    }

    private boolean checkSec;
    private double frameRate = 1;
    
    public void setContext(PackageData main_package_data, Group g1, double fps_value1, double pixel_value1, double average_value1, double upper_limit1, int start, int stop, int step, List<IntervalMarker> intervalsList2, List<Integer> maximum_list2, List<Integer> minimum_list2, List<Integer> first_points2, List<Integer> fifth_points2, List<TimeSpeed> timespeedlist2, boolean saved, boolean checkSec1) throws IOException{
    	main_package = main_package_data;
		currentGroup = g1;
		fps_value = fps_value1;
		pixel_value = pixel_value1;
		average_value = average_value1;
		upper_limit = upper_limit1;
		timespeedlist = timespeedlist2;
		intervalsList = intervalsList2;
		maximum_list = maximum_list2;
		minimum_list = minimum_list2;
		first_points = first_points2;
		fifth_points = fifth_points2;
		checkSec = checkSec1;
		width = currentGroup.getWidth();
		height = currentGroup.getHeight();
		ask_saved = saved;
    	current_index = 0;
    	//go around flow list and get maximum value
    	int done_a = 0;
    	double maximum_value = 0.0;
    	for (int i = 0; i < main_package.getListFlows().size(); i++) {
    		done_a = 0;
    		for(int x = 0; x < height; x++){
    			for(int y = 0; y < width; y++){
                	double final_float = 0.0;
                	final_float = main_package.getListMags().get(i)[done_a];
                	final_float = final_float * pixel_value * fps_value;
                	if (final_float > maximum_value) {
                		maximum_value = final_float;
                	}
                	done_a += 1;
    			}
    			
    		}
		}
    	System.out.println(maximum_value);
    	System.out.println(average_value);
    	mask_value = average_value;
    	scale_start = average_value;
    	scale_end = maximum_value;
    	System.out.println(scale_end);
    	
		
		SpinnerValueFactory<Double> intMask = facGen(0.0, 10000.0, mask_value, 0.1);
		spinnerMask.setValueFactory(intMask);
		spinnerMask.setEditable(true);
		TextFormatter<Double> formatterMask = new TextFormatter<Double>(intMask.getConverter(), intMask.getValue());
		spinnerMask.getEditor().setTextFormatter(formatterMask);
		intMask.valueProperty().bindBidirectional(formatterMask.valueProperty());
		formatterMask.valueProperty().addListener((s, ov, nv) -> {
			try {
				mask_value = nv;
				renderImageView(current_index, currentRenderType , false);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});
		SpinnerValueFactory<Double> intScaleStart = facGen(0.0, 10000.0, scale_start, 0.1);
		spinnerScaleStart.setValueFactory(intScaleStart);
		spinnerScaleStart.setEditable(true);
		TextFormatter<Double> formatterScaleStart = new TextFormatter<Double>(intScaleStart.getConverter(), intScaleStart.getValue());
		spinnerScaleStart.getEditor().setTextFormatter(formatterScaleStart);
		intScaleStart.valueProperty().bindBidirectional(formatterScaleStart.valueProperty());
		formatterScaleStart.valueProperty().addListener((s, ov, nv) -> {
			try {
				scale_start = nv;
				renderImageView(current_index, currentRenderType , false);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});
		SpinnerValueFactory<Double> intScaleEnd = facGen(0.0, 10000.0, scale_end, 0.1);
		spinnerScaleEnd.setValueFactory(intScaleEnd);
		spinnerScaleEnd.setEditable(true);
		TextFormatter<Double> formatterScaleEnd = new TextFormatter<Double>(intScaleEnd.getConverter(), intScaleEnd.getValue());
		spinnerScaleEnd.getEditor().setTextFormatter(formatterScaleEnd);
		intScaleEnd.valueProperty().bindBidirectional(formatterScaleEnd.valueProperty());
		formatterScaleEnd.valueProperty().addListener((s, ov, nv) -> {
			try {
				scale_end = nv;
				renderImageView(current_index, currentRenderType , false);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});
		
		
		createSliderAnimation(1);
		
		SpinnerValueFactory<Integer> intFPS = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1, 1);
		spinnerFPS.setValueFactory(intFPS);
		spinnerFPS.setEditable(true);
		TextFormatter<Integer> formatterFPS = new TextFormatter<Integer>(intFPS.getConverter(), intFPS.getValue());
		spinnerFPS.getEditor().setTextFormatter(formatterFPS);
		intFPS.valueProperty().bindBidirectional(formatterFPS.valueProperty());
		formatterFPS.valueProperty().addListener((s, ov, nv) -> {
			int newFPS = nv;
			frameRate = newFPS;
			double frameSeconds = 1.0/(double)newFPS;
			sliderState = false;
			sliderTimer.stop();
			createSliderAnimation(frameSeconds);
			if(cmdSliderPlay.getText().equals("Stop")){
				sliderState = true;
				sliderTimer.play();
			}	
		});
		
    	writeLinePlot(start, stop);
    	
    	//set default minimum as the maximum buffer zone value
		sliderGroups.setMin(1);
//		sliderGroups.setMax(main_package.getListFlows().size() + 1);
		sliderGroups.setMax(main_package.getListFlows().size());
		sliderGroups.setValue(1);
		sliderGroups.setBlockIncrement(1.0);
		sliderGroups.setMajorTickUnit(1.0);
		sliderGroups.setMinorTickCount(0);
		sliderGroups.setShowTickLabels(false);
		sliderGroups.setShowTickMarks(true);
		sliderGroups.setSnapToTicks(true);
		jetCheck.setSelected(true);
		constructRenderType();
//		renderImageScale();
    }
    
    
    public void constructRenderType() {
    	if (jet_state == true && quiver_state == true && merge_state == true) {
    		currentRenderType = "JetQuiverMerge";
    	} else if (jet_state == false && quiver_state == false && merge_state == false) {
        	currentRenderType = "None";
        } else if (jet_state == true && quiver_state == false && merge_state == false) {
    		currentRenderType = "Jet";
    	} else if (jet_state == false && quiver_state == true && merge_state == false) {
        	currentRenderType = "Quiver";
        } else if (jet_state == false && quiver_state == false && merge_state == true) {
    		currentRenderType = "Merge";
    	} else if (jet_state == false && quiver_state == true && merge_state == true) {
        	currentRenderType = "QuiverMerge";
        } else if (jet_state == true && quiver_state == false && merge_state == true) {
    		currentRenderType = "JetMerge";
    	} else if (jet_state == true && quiver_state == true && merge_state == false) {
        	currentRenderType = "JetQuiver";
        }
    }
    
	private void createSliderAnimation(double seconds){
		sliderTimer = new Timeline(new KeyFrame(Duration.seconds(seconds), event -> {
		    double i = sliderGroups.getValue();
		    if(i >= sliderGroups.getMax()) {
		    	sliderGroups.setValue(1);
		    }
		    else {
		    	sliderGroups.setValue(i+1);
		    }
		}));
		sliderTimer.setCycleCount(Timeline.INDEFINITE);
	}
    
	private ControllerImage image_controller = null;
	private Stage dialogImage;
	private boolean imageDstate = false;
	
	public boolean isImageDState() {
		return imageDstate;
	}
	
	public void closeDialogImageStage() {
		this.dialogImage.close();
	}
	
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Back to Peak Parameters table");
		cmdBack.setTooltip(tooltip5);
//		toSaveView.setVisible(false);
		sliderGroups.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
				System.out.println("Changed!!!!!");
				int groupIndex = new_val.intValue()-1;
				current_index = groupIndex;
				changeMarker();
				try {
					renderImageView(groupIndex, currentRenderType , false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		jetCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				jet_state = !jet_state;
				constructRenderType();
				System.out.println(currentRenderType);
				try {
					renderImageView(current_index, currentRenderType , false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		});
		
		quiverCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				quiver_state = !quiver_state;
				constructRenderType();
				System.out.println(currentRenderType);
				try {
					renderImageView(current_index, currentRenderType , false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		});
		
		mergeCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				merge_state = !merge_state;
				constructRenderType();
				System.out.println(currentRenderType);
				try {
					renderImageView(current_index, currentRenderType , false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		});
		
		ChangeListener<Number> gridSizeListenerWidth = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//            	System.out.println("width resize:");
            	double newValue2 = newValue.doubleValue();
//            	System.out.println(newValue2);
            	if (imgview1.getFitWidth() < width) {
//            		System.out.println("Changed width");
//            		System.out.println(imgview1.getFitHeight());
            		imgview1.setFitWidth(newValue2 * 9);
//            		System.out.println(imgview1.getFitWidth());
//            		renderImageScale();
            	}
        		renderImageScale();
            }
        };
        
		ChangeListener<Number> gridSizeListenerHeight = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//            	System.out.println("height resize:");
            	double newValue2 = newValue.doubleValue();
//            	System.out.println(newValue2);
            	if (imgview1.getFitHeight() < height) {
//            		System.out.println("Changed height");
//            		System.out.println(imgview1.getFitHeight());
            		imgview1.setFitHeight(newValue2 * 8);
//            		System.out.println(imgview1.getFitHeight());
//            		renderImageScale();
            	}
        		renderImageScale();
            }
        };
		
		emptyBoxSize.widthProperty().addListener(gridSizeListenerWidth);
		emptyBoxSize.heightProperty().addListener(gridSizeListenerHeight);
		
		//Imageview, on double click:
		imgview1.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		            if(mouseEvent.getClickCount() == 2){
		                System.out.println("Double clicked");
		                dialogImage = new Stage();    
		        		URL url = getClass().getResource("FXML_Image.fxml");
		            	FXMLLoader fxmlloader2 = new FXMLLoader();
//		            	image_controller = new ControllerImage();
		            	fxmlloader2.setLocation(url);
		            	fxmlloader2.setBuilderFactory(new JavaFXBuilderFactory());
//		            	fxmlloader.setController(image_controller);

		            	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//		            	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
		            	double windowWidth = screenSize.getWidth() * 0.8;
		            	double windowHeight = screenSize.getHeight() * 0.8;
		            	if (width < windowWidth) {
		            		windowWidth = width;
		            	}
		            	if (height < windowHeight) {
		            		windowHeight = height;
		            	}
		            	Parent root;
		            	try {
							root = fxmlloader2.load();
			            	Scene imageScene = new Scene(root, windowWidth, windowHeight);
			            	((ControllerImage)fxmlloader2.getController()).setContext(windowWidth, windowHeight);
			            	((ControllerImage)fxmlloader2.getController()).setImage(imgview1.getImage());
			            	image_controller = (ControllerImage)fxmlloader2.getController();
			            	dialogImage.setScene(imageScene);
//			        		dialogImage.setTitle("Save Jet Scale to Figure:");		
			            	dialogImage.initModality(Modality.NONE);
			            	dialogImage.initOwner(null);
			            	dialogImage.setResizable(false);
			            	imageDstate = true;
			            	dialogImage.show();
			            	dialogImage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			                    public void handle(WindowEvent we) {
			                        System.out.println("Stage is closing");
			                        imageDstate = false;
			                        image_controller = null;
			                    }
			                });
			            	
						} catch (IOException e) {
							e.printStackTrace();
						}
		            }
		        }
		    }
		});
		//set private variable containing controller class of new dialog equal to the controller of the dialog
		//stage is open with the width height of the image OR of 90% of the screen if image bigger than 90% of screen in any dimension
		//controller class has setContext which removes all children of dialog stage and adds a single image view with the size of the stage 
		//set stage close event according to http://www.java2s.com/Code/Java/JavaFX/Stagecloseevent.htm and https://stackoverflow.com/questions/26619566/javafx-stage-close-handler
		//on open private variable for IF dialog open is true; on close, private variable for IF dialog open should be false
		//if private variable for dialog open is true, when the imageView is changed, the Image from the dialog Stage is also changed by the controller class setContext
	}
    
	private void changeMarker() {
        XYPlot plot = (XYPlot) currentChart.getPlot();
        plot.removeDomainMarker(marker,Layer.BACKGROUND);
		int index = current_index;
		int t_start;
		if (index - 1 < 0) {
			t_start = index;
		} else {
			t_start = index - 1;
		}
		
		int t_end;
		if (index + 1 >= general_dataset.getItemCount(0)) {
			t_end = index;
		} else {
			t_end = index + 1;
		}
		double unit = general_dataset.getXValue(0, t_end) - general_dataset.getXValue(0, t_start);
        marker = new IntervalMarker(general_dataset.getXValue(0, index) - unit/2, general_dataset.getXValue(0, index) + unit/2);
        marker.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
        marker.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
        plot.addDomainMarker(marker,Layer.BACKGROUND);
	}
	
    
  //PLOT GENERATING FUNCTIONS
  	private void writeLinePlot(int min, int max) {
  		global_min = min;
  		currentChart = createChart(createDataset(min, max), min);
  		ChartPanel linepanel = new ChartPanel(currentChart);

		JCheckBoxMenuItem gridLinesmenuItem = new JCheckBoxMenuItem();
		gridLinesmenuItem.setSelected(true);
  		gridLinesmenuItem.setText("Gridlines on/off");
  		GridLinesSwitch gridLinesZoomAction = new GridLinesSwitch(linepanel); 
  		gridLinesmenuItem.addActionListener(gridLinesZoomAction);
  		linepanel.getPopupMenu().add(gridLinesmenuItem);
  		
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		showSpline.setText("Render Splines on/off");
		SplineShow splineRendering = new SplineShow(linepanel);
		showSpline.addActionListener(splineRendering);
		linepanel.getPopupMenu().add(showSpline);
  		
  		linepanel.setRangeZoomable(false);
  		linepanel.setDomainZoomable(false);
          Border border = BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(4, 4, 4, 4),
                  BorderFactory.createEtchedBorder()
          );
          linepanel.setBorder(border);
          linepanel.setMouseWheelEnabled(true);
          
          chartNode.setContent(linepanel);
  	}
  	
  	private XYDataset createDataset(int min, int max) {
  		XYSeries series1 = new XYSeries("Optical Flow");
  		for (int i = min; i < max; i++) {
  			double average = currentGroup.getMagnitudeListValue(i);
//  			series1.add(i / fps_value, average * fps_value * pixel_value);
			double new_time = i / fps_value;
			if (checkSec == false) {
				new_time *= 1000;
			}
  			series1.add(new_time, (average * fps_value * pixel_value) - average_value);
  		}
  		//peak detection algorithm receives a group
          XYSeriesCollection dataset = new XYSeriesCollection();
          dataset.addSeries(series1);
          return dataset;
      }
  	
  	private int minimum_value_this = 0;
  	private JFreeChart createChart(XYDataset dataset, int min) {
  		  minimum_value_this = min;
  		  String time_str =  "Time (s)";
  		  if (checkSec == false) {
  		  	time_str = "Time (\u00B5s)";
  		  }
          JFreeChart chart = ChartFactory.createXYLineChart(
              "",
              time_str,
              "Speed (\u00B5m/s)",
              dataset,
              PlotOrientation.VERTICAL,
              true,
              true,
              false
          );
          XYPlot plot = (XYPlot) chart.getPlot();
          Font title_font = new Font("Dialog", Font.PLAIN, 17); 
          Font domain_range_axis_font = new Font("Dialog", Font.PLAIN, 14); 
          chart.getTitle().setFont(title_font);
          chart.removeLegend();
          plot.getDomainAxis().setLabelFont(domain_range_axis_font);
          plot.getRangeAxis().setLabelFont(domain_range_axis_font);
          plot.getRangeAxis().setUpperMargin(0.1);
          lowerBoundDomain = chart.getXYPlot().getDomainAxis().getLowerBound();
          upperBoundDomain = chart.getXYPlot().getDomainAxis().getUpperBound();

          plot.setDomainPannable(true);
          plot.setRangePannable(true);
          
          double unit = dataset.getXValue(0, 1) - dataset.getXValue(0, 0);
//          marker = new IntervalMarker(general_dataset.getXValue(0, index) - unit/2, general_dataset.getXValue(0, index) + unit/2);
//          marker = new IntervalMarker(dataset.getXValue(0, 0) - dataset.getXValue(0, 0) / 2  , dataset.getXValue(0, 0) + dataset.getXValue(0, 1) / 2 );
          marker = new IntervalMarker(dataset.getXValue(0, 0) - unit/2, dataset.getXValue(0, 0) + unit/2);
          general_dataset = dataset;
          
          marker.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
          marker.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
          plot.addDomainMarker(marker, Layer.BACKGROUND);
          
          //plot.setSeriesPaint(0, new Color(0x00, 0x00, 0xFF));
          plot.setBackgroundPaint(main_package.getPlot_preferences().getBackgroundColorRGB());
          plot.setDomainGridlinePaint(main_package.getPlot_preferences().getDomainGridColorRGB());
          plot.setRangeGridlinePaint(main_package.getPlot_preferences().getRangeGridColorRGB());
          plot.setDomainGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
          plot.setRangeGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());

          if (upperBoundDomain - lowerBoundDomain <= 200 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
  	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
  	        	double x = dataset.getXValue(0, x1);
  	        	double y = dataset.getYValue(0, x1);
  	        	if (maximum_list.contains(x1+min)) {
  	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
  	        	}
  	        	if (minimum_list.contains(x1+min)) {
  	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
  	        	}
  	        	if (first_points.contains(x1+min) && !minimum_list.contains(x1+min)) {
  	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
  	        	}
  	        	if (fifth_points.contains(x1+min) && !minimum_list.contains(x1+min)  && !first_points.contains(x1+min)) {
  	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
  	        	}
  	        }
  		}
          plot.addChangeListener(new PlotChangeListener(){
          	@Override
          	public void plotChanged(PlotChangeEvent event) {
//          		System.out.println("I am called after a zoom event (and some other events too).");
          		if ( (plot.getDomainAxis().getLowerBound() != lowerBoundDomain || plot.getDomainAxis().getUpperBound() != upperBoundDomain) && main_package.getPlot_preferences().isDrawAnnotations() == true ) {
          			lowerBoundDomain = plot.getDomainAxis().getLowerBound();
          			upperBoundDomain = plot.getDomainAxis().getUpperBound();
          			if (upperBoundDomain - lowerBoundDomain <= 200) {
          		        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
          		        	double x = dataset.getXValue(0, x1);
          		        	double y = dataset.getYValue(0, x1);
          		        	if (maximum_list.contains(x1+min)) {
          		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
          		        	}
          		        	if (minimum_list.contains(x1+min)) {
          		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
          		        	}
          		        	if (first_points.contains(x1+min) && !minimum_list.contains(x1+min)) {
          		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
          		        	}
          		        	if (fifth_points.contains(x1+min) && !minimum_list.contains(x1+min)  && !first_points.contains(x1+min)) {
          		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
          		        	}
          		        }
          			} else {
          				plot.clearAnnotations();
          			}
          		}
          }});
          if (main_package.getPlot_preferences().isSplineDefaultState() == true) {
  			XYSplineRenderer renderer = new XYSplineRenderer();
  			renderer.setDefaultShapesVisible(false);
  	        renderer.setDefaultShapesFilled(false);
  	        renderer.setSeriesPaint(0, main_package.getPlot_preferences().getSeriesColorRGB());
        	renderer.setSeriesStroke(0, new java.awt.BasicStroke(main_package.getPlot_preferences().getLineThickness()));
          } else {
          	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
      		renderer.setDefaultShapesVisible(false);
              renderer.setDefaultShapesFilled(false);
              renderer.setSeriesPaint(0, main_package.getPlot_preferences().getSeriesColorRGB());
          	renderer.setSeriesStroke(0, new java.awt.BasicStroke(main_package.getPlot_preferences().getLineThickness()));

          }
          return chart;
      }
  	
  	
  	// ACCESSORY PLOT CLASSES	
  	public class GridLinesSwitch implements ActionListener {
  	    private JFreeChart chart;
  	    private ChartPanel panel;
  	    private XYPlot plot;
  	    
  		public GridLinesSwitch(ChartPanel panel) {
  	        this.setPanel(panel);
  	        this.chart = panel.getChart();
  	        this.plot = (XYPlot) chart.getPlot();
  		}
  		
  		@Override
  		public void actionPerformed(java.awt.event.ActionEvent e) {
  			if (plot.isDomainGridlinesVisible() == true) {
  				plot.setDomainGridlinesVisible(false);
  				plot.setRangeGridlinesVisible(false);
  		        main_package.getPlot_preferences().setGridlineDefaultState(false);
  			} else {
  				plot.setDomainGridlinesVisible(true);
  				plot.setRangeGridlinesVisible(true);
  		        main_package.getPlot_preferences().setGridlineDefaultState(true);
  			}
  		}

  		public ChartPanel getPanel() {
  			return panel;
  		}

  		public void setPanel(ChartPanel panel) {
  			this.panel = panel;
  		}
  		
  	}
  	
  	
  	public static BufferedImage convertToType(BufferedImage image, int type) {
  	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
  	    Graphics2D graphics = newImage.createGraphics();
  	    graphics.drawImage(image, 0, 0, null);
  	    graphics.dispose();
  	    return newImage;
  	}
  	
  	public static BufferedImage colorToAlpha(BufferedImage raw, java.awt.Color remove) {
  	    int WIDTH = raw.getWidth();
  	    int HEIGHT = raw.getHeight();
  	    BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
  	    int pixels[]=new int[WIDTH*HEIGHT];
  	    raw.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    for(int i=0; i<pixels.length;i++)
  	    {
  	        if (pixels[i] == remove.getRGB()) 
  	        {
  	        pixels[i] = 0x00ffffff;
  	        }
  	    }
  	    image.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    return image;
  	}
  	
  	public static BufferedImage alphaToColor(BufferedImage raw, java.awt.Color remove) {
  	    int WIDTH = raw.getWidth();
  	    int HEIGHT = raw.getHeight();
  	    BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
  	    int pixels[]=new int[WIDTH*HEIGHT];
  	    raw.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    for(int i=0; i<pixels.length;i++) {
  	        if (pixels[i] == remove.getRGB()) {
  	        	pixels[i] = 0xff000000;
  	        }
  	    }
  	    image.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    return image;
  	}
  	
  	public static BufferedImage modAlpha(BufferedImage modMe, double modAmount) {
  	    int WIDTH = modMe.getWidth();
  	    int HEIGHT = modMe.getHeight();
  	    BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
  	    int pixels[]=new int[WIDTH*HEIGHT];
  	    modMe.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    for(int i=0; i<pixels.length;i++) {
  	    	int rgb_prev = pixels[i];
  	    	int alpha = (pixels[i]>> 24) & 0xff;
            alpha *= modAmount; //similar distortion to tape saturation (has scrunching effect, eliminates clipping)
            alpha &= 0xff;      //keeps alpha in 0-255 range
            pixels[i] &= 0x00ffffff; //remove old alpha info
            pixels[i] |= (alpha << 24);  //add new alpha info
  	        if (rgb_prev == java.awt.Color.BLACK.getRGB()) {
  	        	pixels[i] = 0x00ffffff;
  	        }
  	    }
  	    image.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    return image;
	}
    
  	
  	public Mat loadImgSrc(int index) throws IOException {
  		Mat return_mat = null;
        if (currentGroup.getType() == 0) {
            ImageGroup g3 = (ImageGroup) currentGroup;
            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
            return_mat = imread(path_img.getCanonicalPath());
        } else {
            VideoGroup g3 = (VideoGroup) currentGroup;
        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
    		frameGrabber.start();
    		int N = frameGrabber.getLengthInFrames();
    		for(int j = 0; j<N; j++){
    			Frame frame = frameGrabber.grab();
    			Mat img = converterToMat.convert(frame);
    			if(j ==index){
    				return_mat = img.clone();
    				break;
    			}
    		}
    		frameGrabber.close();
        }
        return return_mat;
  	}
  	
  	public BufferedImage bufferInvertChannels(BufferedImage startimage) {
  	    int WIDTH = startimage.getWidth();
  	    int HEIGHT = startimage.getHeight();
  	    BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
  	    int pixels[]=new int[WIDTH*HEIGHT];
  	    startimage.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    for(int i=0; i<pixels.length;i++) {
  	    	int rgb_prev = pixels[i];
  	    	java.awt.Color current_color = new java.awt.Color(rgb_prev);
  	    	java.awt.Color new_color =  new java.awt.Color(current_color.getBlue(), current_color.getGreen(), current_color.getRed());
  	    	pixels[i] = new_color.getRGB();
  	    }
  	    image.setRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
  	    return image;
  	}
  	
	public void renderImageView(int index, String rendertype, boolean image_not_save) throws IOException{
		Image finalImage = null;
		if (rendertype.equals("Jet")) {
			Boolean to_merge = false;
	        if (contour_state == false) {
	        	finalImage = writeJetImage(index, to_merge);
	        } else {
	        	WritableImage myWritableImage = writeJetImage(index, to_merge);
		        Mat img_src = null;
		        if (currentGroup.getType() == 0) {
		            ImageGroup g3 = (ImageGroup) currentGroup;
		            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
		            img_src = imread(path_img.getCanonicalPath());
		        } else {
		            VideoGroup g3 = (VideoGroup) currentGroup;
		        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
		    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		    		frameGrabber.start();
		    		int N = frameGrabber.getLengthInFrames();
		    		for(int j = 0; j<N; j++){
		    			Frame frame = frameGrabber.grab();
		    			Mat img = converterToMat.convert(frame);
		    			if(j ==index){
		    				img_src = img.clone();
		    				break;
		    			}
		    		}
		    		frameGrabber.close();
		        }
		        BufferedImage overlay = SwingFXUtils.fromFXImage(myWritableImage, null);
		        Mat jetLayer = Java2DFrameUtils.toMat(overlay);
		        MatVector channels = new MatVector();
		        Mat jetLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
		        org.bytedeco.javacpp.opencv_core.split(jetLayer, channels);
		        Mat blueCh = channels.get(1);
		        Mat greenCh = channels.get(2);
		        Mat redCh = channels.get(3);
		        MatVector channels2 = new MatVector(3);
		        channels2.put(0, redCh);
		        channels2.put(1, greenCh);
		        channels2.put(2, blueCh);
		        org.bytedeco.javacpp.opencv_core.merge(channels2, jetLayerRGB);
	        	img_src.convertTo(img_src, CV_8U);
	        	jetLayerRGB.convertTo(jetLayerRGB, CV_8U);
	        	Mat mask2 = generateContour(img_src);	        	
	        	Mat combinedImage2 = new Mat();
	        	jetLayerRGB.copyTo(combinedImage2, mask2);
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage2);
				finalImage = SwingFXUtils.toFXImage(combined, null);
	        }
		}
		else if (rendertype.equals("JetMerge")) {
			Boolean to_merge = true;
			WritableImage myWritableImage = writeJetImage(index, to_merge);
	        Mat img_src = null;
	        if (currentGroup.getType() == 0) {
	            ImageGroup g3 = (ImageGroup) currentGroup;
	            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
	            img_src = imread(path_img.getCanonicalPath());
	        } else {
	            VideoGroup g3 = (VideoGroup) currentGroup;
	        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
	    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
	    		frameGrabber.start();
	    		int N = frameGrabber.getLengthInFrames();
	    		for(int j = 0; j<N; j++){
	    			Frame frame = frameGrabber.grab();
	    			Mat img = converterToMat.convert(frame);
	    			if(j ==index){
	    				img_src = img.clone();
	    				break;
	    			}
	    		}
	    		frameGrabber.close();
	        }
	        BufferedImage overlay = SwingFXUtils.fromFXImage(myWritableImage, null);
	        Mat jetLayer = Java2DFrameUtils.toMat(overlay);
	        MatVector channels = new MatVector();
	        Mat jetLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(jetLayer, channels);
	        Mat blueCh = channels.get(1);
	        Mat greenCh = channels.get(2);
	        Mat redCh = channels.get(3);
	        MatVector channels2 = new MatVector(3);
	        channels2.put(2, redCh);
	        channels2.put(1, greenCh);
	        channels2.put(0, blueCh);
	        org.bytedeco.javacpp.opencv_core.merge(channels2, jetLayerRGB);
	        if (contour_state == true) {
	        	img_src.convertTo(img_src, CV_8U);
//	        	jetLayerRGB.convertTo(jetLayerRGB, CV_8U);
	        }
	        
			BufferedImage source_buffer = Java2DFrameUtils.toBufferedImage(img_src);
			BufferedImage jetAlpha = Java2DFrameUtils.toBufferedImage(jetLayerRGB);
			BufferedImage jetAlphaMod = modAlpha(jetAlpha, alpha_under_two);
//			BufferedImage jetAlphaModTransparent = colorToAlpha(jetAlphaMod, java.awt.Color.BLACK);
			BufferedImage combinedAlphaedSrc = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			// paint both images, preserving the alpha channels
			Graphics gz = combinedAlphaedSrc.getGraphics();
			gz.drawImage(source_buffer, 0, 0, null);
			gz.drawImage(jetAlphaMod, 0, 0, null);
			
			//convert alphaed to Mat object
	        Mat combinedImageA = Java2DFrameUtils.toMat(combinedAlphaedSrc);
	        MatVector channelsz = new MatVector();
	        Mat combinedImage = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(combinedImageA, channelsz);
	        Mat blueChz = channelsz.get(1);
	        Mat greenChz = channelsz.get(2);
	        Mat redChz = channelsz.get(3);
	        MatVector channelsz2 = new MatVector(3);
	        channelsz2.put(0, redChz);
	        channelsz2.put(1, greenChz);
	        channelsz2.put(2, blueChz);
	        org.bytedeco.javacpp.opencv_core.merge(channelsz, combinedImage);
	        
	        if (contour_state == true) {
	        	Mat mask2 = generateContour(img_src);	        	
	        	Mat combinedImage2 = new Mat();
	        	combinedImage.copyTo(combinedImage2, mask2);
	        	
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage2);
				BufferedImage combined1 = colorToAlpha(combined, java.awt.Color.BLACK);
				
				BufferedImage img_src_buffered = Java2DFrameUtils.toBufferedImage(img_src);

				// create the new image, canvas size is the max. of both image sizes
				BufferedImage combined2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				// paint both images, preserving the alpha channels
				Graphics g = combined2.getGraphics();
				g.drawImage(img_src_buffered, 0, 0, null);
				g.drawImage(combined1, 0, 0, null);
				finalImage = SwingFXUtils.toFXImage(combined2, null);
	        } else {
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage);
		        finalImage = SwingFXUtils.toFXImage(combined, null);
	        }
		}
		else if (rendertype.equals("Quiver")) {
        	Boolean to_merge = false;
        	BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        if (contour_state == false) {
	        	finalImage = writeCanvas(index, to_merge, bi);
	        } else {
	        	//open image to contour
				ToMat converter = new OpenCVFrameConverter.ToMat();
		        Mat img_src = null;
		        if (currentGroup.getType() == 0) {
		            ImageGroup g3 = (ImageGroup) currentGroup;
		            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
		            img_src = imread(path_img.getCanonicalPath());
		        } else {
		            VideoGroup g3 = (VideoGroup) currentGroup;
		        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
		    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		    		frameGrabber.start();
		    		int N = frameGrabber.getLengthInFrames();
		    		for(int j = 0; j<N; j++){
		    			Frame frame = frameGrabber.grab();
		    			Mat img = converterToMat.convert(frame);
		    			if(j ==index){
		    				img_src = img.clone();
		    				break;
		    			}
		    		}
		    		frameGrabber.close();
		        }
		        
		        //generate contour mask
	        	img_src.convertTo(img_src, CV_8U);
	        	Mat mask2 = generateContour(img_src);
	        	BufferedImage contourMask = Java2DFrameUtils.toBufferedImage(mask2);
	        	
	    		WritableImage writableImage = writeCanvas(index, to_merge, bi);
		        BufferedImage overlay = SwingFXUtils.fromFXImage(writableImage, null);
		        Mat quiverLayer = Java2DFrameUtils.toMat(overlay);
		        MatVector channels = new MatVector();
		        Mat quiverLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
		        org.bytedeco.javacpp.opencv_core.split(quiverLayer, channels);
		        Mat blueCh = channels.get(1);
		        Mat greenCh = channels.get(2);
		        Mat redCh = channels.get(3);
		        MatVector channels2 = new MatVector(3);
		        channels2.put(0, redCh);
		        channels2.put(1, greenCh);
		        channels2.put(2, blueCh);
		        org.bytedeco.javacpp.opencv_core.merge(channels2, quiverLayerRGB);
	        	quiverLayerRGB.convertTo(quiverLayerRGB, CV_8U);	
	        	Mat combinedImage2 = new Mat();
	        	quiverLayerRGB.copyTo(combinedImage2, mask2);
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage2);
				finalImage = SwingFXUtils.toFXImage(combined, null);
	        }
		} else if (rendertype.equals("QuiverMerge")) {
			Boolean to_merge = true;
			ToMat converter = new OpenCVFrameConverter.ToMat();
	        Mat img_src = null;
	        if (currentGroup.getType() == 0) {
	            ImageGroup g3 = (ImageGroup) currentGroup;
	            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
	            img_src = imread(path_img.getCanonicalPath());
	        } else {
	            VideoGroup g3 = (VideoGroup) currentGroup;
	        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
	    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
	    		frameGrabber.start();
	    		int N = frameGrabber.getLengthInFrames();
	    		for(int j = 0; j<N; j++){
	    			Frame frame = frameGrabber.grab();
	    			Mat img = converterToMat.convert(frame);
	    			if(j ==index){
	    				img_src = img.clone();
	    				break;
	    			}
	    		}
	    		frameGrabber.close();
	        }
	        BufferedImage srcBuf = Java2DFrameUtils.toBufferedImage(img_src);
	        WritableImage writableImage = writeCanvas(index, to_merge, srcBuf);
	        BufferedImage overlay = SwingFXUtils.fromFXImage(writableImage, null);
	        Mat quiverLayer = Java2DFrameUtils.toMat(overlay);
	        MatVector channels = new MatVector();
	        Mat quiverLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(quiverLayer, channels);
	        Mat blueCh = channels.get(1);
	        Mat greenCh = channels.get(2);
	        Mat redCh = channels.get(3);
	        MatVector channels2 = new MatVector(3);
	        channels2.put(0, redCh);
	        channels2.put(1, greenCh);
	        channels2.put(2, blueCh);
	        org.bytedeco.javacpp.opencv_core.merge(channels2, quiverLayerRGB);
	        Mat combinedImage = new Mat();
//	        if (contour_state == true) {
//	        	img_src.convertTo(img_src, CV_8U);
//	        	quiverLayerRGB.convertTo(quiverLayerRGB, CV_8U);
//	        }
	        //OLD QUIVER
	        quiverLayerRGB.copyTo(combinedImage);
	        if (contour_state == true) {
	        	img_src.convertTo(img_src, CV_8U);
	        	quiverLayerRGB.convertTo(quiverLayerRGB, CV_8U);
	        	Mat mask2 = generateContour(img_src);	        	
	        	Mat combinedImage2 = new Mat();
	        	combinedImage.copyTo(combinedImage2, mask2);
	        	
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage2);
				BufferedImage combined1 = colorToAlpha(combined, java.awt.Color.BLACK);
				
				BufferedImage img_src_buffered = Java2DFrameUtils.toBufferedImage(img_src);

				// create the new image, canvas size is the max. of both image sizes
				BufferedImage combined2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				// paint both images, preserving the alpha channels
				Graphics g = combined2.getGraphics();
				g.drawImage(img_src_buffered, 0, 0, null);
				g.drawImage(combined1, 0, 0, null);
				finalImage = SwingFXUtils.toFXImage(combined2, null);
	        } else {
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage);
		        finalImage = SwingFXUtils.toFXImage(combined, null);
	        }
		} else if (rendertype.equals("JetQuiver")) {
			Boolean to_merge = true;
			WritableImage myWritableImage = writeJetImage(index, to_merge);
			//OLD QUIVER
			//WritableImage myWritableImage2 = writeCanvas(index, to_merge);
			
			//OLD STILL
//			WritableImage myWritableImage2 = writeCanvas(index, to_merge, new Mat());
	        Mat img_src = null;
	        if (currentGroup.getType() == 0) {
	            ImageGroup g3 = (ImageGroup) currentGroup;
	            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
	            img_src = imread(path_img.getCanonicalPath());
	        } else {
	            VideoGroup g3 = (VideoGroup) currentGroup;
	        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
	    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
	    		frameGrabber.start();
	    		int N = frameGrabber.getLengthInFrames();
	    		for(int j = 0; j<N; j++){
	    			Frame frame = frameGrabber.grab();
	    			Mat img = converterToMat.convert(frame);
	    			if(j ==index){
	    				img_src = img.clone();
	    				break;
	    			}
	    		}
	    		frameGrabber.close();
	        }
	        
			
			
	        BufferedImage overlay = SwingFXUtils.fromFXImage(myWritableImage, null);

	        Mat jetLayer = Java2DFrameUtils.toMat(overlay);
	        MatVector channels = new MatVector();
	        Mat jetLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(jetLayer, channels);
	        Mat blueCh = channels.get(1);
	        Mat greenCh = channels.get(2);
	        Mat redCh = channels.get(3);
	        MatVector channels2 = new MatVector(3);
	        channels2.put(0, redCh);
	        channels2.put(1, greenCh);
	        channels2.put(2, blueCh);
	        org.bytedeco.javacpp.opencv_core.merge(channels2, jetLayerRGB);
	        if (contour_state == true) {
	        	jetLayerRGB.convertTo(jetLayerRGB, CV_8U);
//	        	quiverLayerRGB.convertTo(quiverLayerRGB, CV_8U);
	        }
	        //new method: add alpha to jet
			BufferedImage jetBuffer = Java2DFrameUtils.toBufferedImage(jetLayerRGB);
			BufferedImage jetBufferAlpha = modAlpha(jetBuffer, alpha_under_two);
			//black to not alpha
			BufferedImage jetBufferAlphaBlack = alphaToColor(jetBufferAlpha, java.awt.Color.BLACK);
	        //draw quiver on resulting jet
			WritableImage myWritableImage2 = writeCanvas(index, to_merge, jetBufferAlphaBlack);
	        BufferedImage quiverImage2 = SwingFXUtils.fromFXImage(myWritableImage2, null);
			//convert to matrix
	        Mat overlayJetQuiver = Java2DFrameUtils.toMat(quiverImage2);
	        MatVector channels3z = new MatVector();
	        Mat combinedImage = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(overlayJetQuiver, channels3z);
	        Mat blueCh2z = channels3z.get(1);
	        Mat greenCh2z = channels3z.get(2);
	        Mat redCh2z = channels3z.get(3);
	        MatVector channels4z = new MatVector(3);
	        channels4z.put(0, redCh2z);
	        channels4z.put(1, greenCh2z);
	        channels4z.put(2, blueCh2z);
	        org.bytedeco.javacpp.opencv_core.merge(channels4z, combinedImage);
			
	        if (contour_state == true) {
	        	Mat mask2 = generateContour(img_src);
	        	Mat combinedImage2 = new Mat();
	        	combinedImage.copyTo(combinedImage2, mask2);
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage2);
		        finalImage = SwingFXUtils.toFXImage(combined, null);
	        } else {
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage);
		        finalImage = SwingFXUtils.toFXImage(combined, null);
	        }
		} else if (rendertype.equals("JetQuiverMerge")) {
			Boolean to_merge = true;
			WritableImage myWritableImage = writeJetImage(index, to_merge);
			
			//OLD QUIVER
//			WritableImage myWritableImage2 = writeCanvas(index, to_merge);
			
	        Mat img_src = null;
	        if (currentGroup.getType() == 0) {
	            ImageGroup g3 = (ImageGroup) currentGroup;
	            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
	            img_src = imread(path_img.getCanonicalPath());
	        } else {
	            VideoGroup g3 = (VideoGroup) currentGroup;
	        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
	    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
	    		frameGrabber.start();
	    		int N = frameGrabber.getLengthInFrames();
	    		for(int j = 0; j<N; j++){
	    			Frame frame = frameGrabber.grab();
	    			Mat img = converterToMat.convert(frame);
	    			if(j ==index){
	    				img_src = img.clone();
	    				break;
	    			}
	    		}
	    		frameGrabber.close();
	        }
	        

	        //OLD JET
	        BufferedImage overlay = SwingFXUtils.fromFXImage(myWritableImage, null);
	        Mat jetLayer = Java2DFrameUtils.toMat(overlay);
	        MatVector channels = new MatVector();
	        Mat jetLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(jetLayer, channels);
	        Mat blueCh = channels.get(1);
	        Mat greenCh = channels.get(2);
	        Mat redCh = channels.get(3);
	        MatVector channels2 = new MatVector(3);
	        channels2.put(2, redCh);
	        channels2.put(1, greenCh);
	        channels2.put(0, blueCh);
	        org.bytedeco.javacpp.opencv_core.merge(channels2, jetLayerRGB);
	        
	        if (contour_state == true) {
	        	img_src.convertTo(img_src, CV_8U);
	        	
	        	//OLD QUIVER
//	        	quiverLayerRGB.convertTo(quiverLayerRGB, CV_8U);
	        	//OLD JET
//	        	jetLayerRGB.convertTo(jetLayer, CV_8U);
	        }
			BufferedImage source_buffer = Java2DFrameUtils.toBufferedImage(img_src);
			BufferedImage writableImageAlphaed2 = Java2DFrameUtils.toBufferedImage(jetLayerRGB);
			BufferedImage writableImageAlphaedO = modAlpha(writableImageAlphaed2, alpha_under_two);
			BufferedImage writableImageAlphaedTransparent = colorToAlpha(writableImageAlphaedO, java.awt.Color.BLACK);
			BufferedImage combinedAlphaedSrc = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			// paint both images, preserving the alpha channels
			Graphics gz = combinedAlphaedSrc.getGraphics();
			gz.drawImage(source_buffer, 0, 0, null);
			gz.drawImage(writableImageAlphaedTransparent, 0, 0, null);
			
			//convert alphaed to Mat object
	        Mat combinedImageA = Java2DFrameUtils.toMat(combinedAlphaedSrc);
	        MatVector channelsz = new MatVector();
	        Mat combinedImage = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(combinedImageA, channelsz);
	        Mat blueChz = channelsz.get(1);
	        Mat greenChz = channelsz.get(2);
	        Mat redChz = channelsz.get(3);
	        MatVector channelsz2 = new MatVector(3);
	        channelsz2.put(0, redChz);
	        channelsz2.put(1, greenChz);
	        channelsz2.put(2, blueChz);
	        org.bytedeco.javacpp.opencv_core.merge(channelsz, combinedImage);

	        BufferedImage srcBuf = Java2DFrameUtils.toBufferedImage(combinedImage);
			WritableImage myWritableImage2 = writeCanvas(index, to_merge, srcBuf);
	        BufferedImage overlay2 = SwingFXUtils.fromFXImage(myWritableImage2, null);
	        Mat quiverLayer = Java2DFrameUtils.toMat(overlay2);
	        MatVector channels3 = new MatVector();
	        Mat quiverLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
	        org.bytedeco.javacpp.opencv_core.split(quiverLayer, channels3);
	        Mat blueCh2 = channels3.get(1);
	        Mat greenCh2 = channels3.get(2);
	        Mat redCh2 = channels3.get(3);
	        MatVector channels4 = new MatVector(3);
	        channels4.put(0, redCh2);
	        channels4.put(1, greenCh2);
	        channels4.put(2, blueCh2);
	        org.bytedeco.javacpp.opencv_core.merge(channels4, quiverLayerRGB);
	        quiverLayerRGB.copyTo(combinedImage);
	        if (contour_state == true) {
	        	Mat mask2 = generateContour(img_src);	        	
	        	Mat combinedImage2 = new Mat();
	        	combinedImage.copyTo(combinedImage2, mask2);
	        	
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage2);
				BufferedImage combined1 = colorToAlpha(combined, java.awt.Color.BLACK);
				
				BufferedImage img_src_buffered = Java2DFrameUtils.toBufferedImage(img_src);

				// create the new image, canvas size is the max. of both image sizes
				BufferedImage combined2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				// paint both images, preserving the alpha channels
				Graphics g = combined2.getGraphics();
				g.drawImage(img_src_buffered, 0, 0, null);
				g.drawImage(combined1, 0, 0, null);
				finalImage = SwingFXUtils.toFXImage(combined2, null);
	        } else {
				BufferedImage combined = Java2DFrameUtils.toBufferedImage(combinedImage);
		        finalImage = SwingFXUtils.toFXImage(combined, null);
	        }
		} else if (rendertype.equals("Merge")){
			//render image alone
			ToMat converter = new OpenCVFrameConverter.ToMat();
	        Mat img_src = null;
	        if (currentGroup.getType() == 0) {
	            ImageGroup g3 = (ImageGroup) currentGroup;
	            File path_img  = g3.getImages().get(main_package.getListPoints().get(index));
	            img_src = imread(path_img.getCanonicalPath());
	        } else {
	            VideoGroup g3 = (VideoGroup) currentGroup;
	        	FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(g3.getVideo().getAbsolutePath());
	    		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
	    		frameGrabber.start();
	    		int N = frameGrabber.getLengthInFrames();
	    		for(int j = 0; j<N; j++){
	    			Frame frame = frameGrabber.grab();
	    			Mat img = converterToMat.convert(frame);
	    			if(j ==index){
	    				img_src = img.clone();
	    				break;
	    			}
	    		}
	    		frameGrabber.close();
	        }
	        if (contour_state == true) {
//	        	img_src = generateContour(img_src);
//	        	img_src.convertTo(img_src, CV_8U);
	        	Mat mask2 = generateContour(img_src);	        	
	        	Mat combinedImage2 = new Mat();
	        	img_src.copyTo(img_src, mask2);
	        }
	        Frame original_image_frame = converter.convert(img_src);
	        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
	        BufferedImage original_image = paintConverter.getBufferedImage(original_image_frame,1);
	        finalImage = SwingFXUtils.toFXImage(original_image, null);
	        
		} else {
			//render black image
		}
		
		if (image_not_save == false) {
			imgview1.setImage(finalImage);
			renderImageScale();
			System.out.println("Rendering scale");
			if (imageDstate == true) {
				System.out.println("About to change dialog image");
				image_controller.setImage(imgview1.getImage());
			}
		} else {
//			imgview1.setImage(finalImage);
			if (save_as_video == false) {
				System.out.println("Trying to save image!");
				saveCurrentImageView(finalImage);
			} else {
				saveCurrentImageVideo(finalImage);
			}
		}
	}	
	
	//FUNCTIONS TO RENDER THE SCALE IMAGE BELOW
	private double convertScaleToHeight(double y) {
		double a = (scale_end-scale_start) / imgview1.getFitHeight();
		double b = scale_start;
		return ((y-b)/a);
	}
	
	private double convertToMag(double x) {
		double a = (scale_end-scale_start) / imgview1.getFitHeight();
		double b = scale_start;
		return ((x*a)+b);
	}
	
	
	public static String rightPadZeros(String str, int num) {
		return String.format("%1$-" + num + "s", str).replace(' ', '0');
	}
	
	
	private final int SCALE_ARR_SIZE = 4;
	
	void drawScaleArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
	    gc.setFill(Color.BLACK);
	    gc.setLineWidth(2.0);
	    
	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx * dx + dy * dy);

	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));
	    gc.strokeLine(0, 0, len, 0);
	    gc.strokeLine(len, 0, len - SCALE_ARR_SIZE, -SCALE_ARR_SIZE);
	    gc.strokeLine(len, 0, len - SCALE_ARR_SIZE, SCALE_ARR_SIZE);
//	    gc.strokeLine(len, 0, len + ARR_SIZE, ARR_SIZE);
//	    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);
	}
	
	@FXML
	ImageView scaleImgView;
	
	public static double round(double d, int decimalPlace) {
	    BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	private BufferedImage generateCanvasScale() {
		//emptyBoxSize.widthProperty()
		
//		int scale_width = (int)(emptyBoxSize.getWidth());
//		if (scale_width < 70) {
//			scale_width = 70;
//		}
//		if (scale_width )
		int scale_width  = 70;
//		System.out.println(scale_width);
//		int scale_height = (int)(emptyBoxSize.getHeight() * 8);
		int scale_height = (int) imgview1.getFitHeight();
		if (scale_height < imgview1.getFitHeight()) {
			scale_height += 1;
		}
		System.out.println(scale_height);
		scaleImgView.setFitHeight(imgview1.getFitHeight());
		//scaleImgView.setFitHeight(height);
		scaleImgView.setFitWidth(scale_width);
		//Calculate: width, height, spacing, font-size dynamically by using the grid
		//set imageview fitheight and width
		Canvas canvas1 = new Canvas((int) scale_width, (int) scale_height);
		GraphicsContext gc = canvas1.getGraphicsContext2D();
    	gc.setFill(Color.TRANSPARENT);
    	gc.fillRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
		gc.save();
		PixelWriter pixel_writing = canvas1.getGraphicsContext2D().getPixelWriter();
		for (int x = 0; x < scale_width - 50; x++) {
			//for (int y = 0; y < scale_height; y++) {
			for (int y = scale_height; y > 0; y--) {
				int y_new = y;
        		java.awt.Color a = this_jet.getColor(convertToMag(y_new));
        		Color this_color = new Color( (Double.valueOf(a.getRed()) / 255.0), (Double.valueOf(a.getGreen()) / 255.0 ) , (Double.valueOf(a.getBlue()) / 255.0), 1.0);
				//pixel_writing.setColor( x, ((int)canvas1.getHeight()-y), this_color);
				pixel_writing.setColor( x, (int)canvas1.getHeight()-y, this_color);
			}
			//}
		}
		//write tick labels
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
        javafx.scene.text.Font number_font = javafx.scene.text.Font.font("Arial",FontWeight.BOLD, 12);
		gc.setFont(number_font);

		int x = (int) scale_width - 30; //tick width to write
		int y = ((int) convertScaleToHeight(scale_end));
		//			gc.fillText(String.valueOf(scale_end), x, y);
		int currentpad_f = 5;
		String current_print_f = String.valueOf(round(scale_end,2));
//		if (!current_print_f.contains(".")) {
//			current_print_f += ".";
//		} else {
//			int dot_after = current_print_f.split("\\.")[0].length();
//			currentpad_f = currentpad_f - dot_after;
//		}
//		current_print_f = rightPadZeros(current_print_f, currentpad_f);

		int currentpad_s = 5;
		String current_print_s = String.valueOf(round(scale_start,2));
//		if (!current_print_s.contains(".")) {
//			current_print_s += ".";
//		} else {
//			int dot_after = current_print_s.split("\\.")[0].length();
//			currentpad_s = currentpad_s - dot_after;
//		}
//		current_print_s = rightPadZeros(current_print_s, currentpad_s);
		
		gc.fillText(current_print_f, x, ((int)canvas1.getHeight()-y+10));
		y = ((int) convertScaleToHeight(scale_start));
		gc.fillText(current_print_s, x, ((int)canvas1.getHeight()-y-10));
		
		
		
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        javafx.scene.text.Font subtitle_font = javafx.scene.text.Font.font("Arial",FontWeight.BOLD, 15);
        gc.setFont(subtitle_font);
        
	    Transform transform = Transform.rotate(-90.0, (int)canvas1.getWidth()-35, (int)(canvas1.getHeight()/2));
	    gc.setTransform(new Affine(transform));
//	    gc.fillText("Speed (\u00B5m/s)", (int)canvas1.getWidth()-20, (int)(canvas1.getHeight()/2));
	    gc.fillText("Speed (\u00B5m/s)", (int)canvas1.getWidth()-35, (int)(canvas1.getHeight()/2));
	    gc.restore();
	    
//	    int x_arrow = (int)canvas1.getWidth()-35;
	    int x_arrow = (int)canvas1.getWidth()-20;
	    int y_arrow_init = (int)(canvas1.getHeight()/2) - 46;
	    int y_arrow_end = (int)(canvas1.getHeight()/2) + 46;
	    
		drawScaleArrow(gc, x_arrow, y_arrow_end, x_arrow, y_arrow_init);
		
    	WritableImage writableImage = new WritableImage((int) canvas1.getWidth(), (int)canvas1.getHeight());
    	SnapshotParameters sp = new SnapshotParameters();
    	sp.setFill(Color.TRANSPARENT);
        canvas1.snapshot(sp, writableImage);
        BufferedImage final_scale = SwingFXUtils.fromFXImage(writableImage, null);
        return final_scale;
	}
	
	void renderImageScale() {
		//render imgviewscale Image
		BufferedImage final_scale = generateCanvasScale();
		scaleImgView.setImage(SwingFXUtils.toFXImage(final_scale, null));
		System.out.println("Dif:");
		System.out.println(scaleImgView.getFitHeight() - imgview1.getFitHeight());
		//Controller_Scale.
	}
	
	//END FUNCTIONS TO RENDER THE SCALE IMAGE
	
	public WritableImage writeJetImage(int index, boolean to_merge) {
		WritableImage myWritableImage = new WritableImage(width, height);
        PixelWriter myPixelWriter = myWritableImage.getPixelWriter();
        int done = 0;
        this_jet = new ColorMap(scale_start, scale_end, JET);
        
        for(int x = 0; x < height; x++){
            for(int y = 0; y < width; y++){            	
            	double final_float;
            	final_float = main_package.getListMags().get(index)[done];
            	final_float = final_float * pixel_value * fps_value;
            	if (final_float < mask_value) {
            		Color this_color;
            		if (to_merge == true) {
            			this_color = new Color(1.0,1.0,1.0,0.0);
            		} else {
            			this_color = new Color(0.0,0.0,0.0, 1.0);
            		}
					myPixelWriter.setColor(y, x, this_color);
            		
            	} else  {
            		java.awt.Color a = this_jet.getColor(final_float);
            		Color this_color = new Color( (Double.valueOf(a.getRed()) / 255.0), (Double.valueOf(a.getGreen()) / 255.0 ) , (Double.valueOf(a.getBlue()) / 255.0), 1.0);
            		myPixelWriter.setColor(y, x, this_color);
            	}
            	done = done + 1;
            }
        }
        return myWritableImage;
	}
	
	
	public WritableImage writeCanvas(int index, boolean to_merge, BufferedImage backgroundImageBuf) {
		Canvas canvas1 = new Canvas((int) width, (int) height);    	
		Mat x_sqrd = new Mat();
    	Mat y_sqrd = new Mat();
    	Mat x_flow = main_package.getXflow(index);
    	Mat y_flow = main_package.getYflow(index);
    	
    	org.bytedeco.javacpp.opencv_core.pow(x_flow, 2.0, x_sqrd);
    	org.bytedeco.javacpp.opencv_core.pow(y_flow, 2.0, y_sqrd);
    	Mat xy_sum = new Mat();
    	org.bytedeco.javacpp.opencv_core.add(x_sqrd, y_sqrd , xy_sum);
    	Mat xy_sum_srqt = new Mat();
    	org.bytedeco.javacpp.opencv_core.sqrt(xy_sum, xy_sum_srqt);
    	Mat x_div = new Mat();
    	Mat y_div = new Mat();
    	org.bytedeco.javacpp.opencv_core.divide(x_flow,  xy_sum_srqt, x_div);
    	org.bytedeco.javacpp.opencv_core.divide(y_flow,  xy_sum_srqt, y_div);
    	org.bytedeco.javacpp.opencv_core.multiplyPut(x_div, 0.8);
    	org.bytedeco.javacpp.opencv_core.multiplyPut(y_div, 0.8);
    	
    	FloatBuffer u_diff = x_div.createBuffer();
    	float[] floatArrayOp = new float[u_diff.capacity()];
    	u_diff.get(floatArrayOp);
    	
    	FloatBuffer v_diff = y_div.createBuffer();
    	float[] floatArrayOp2 = new float[v_diff.capacity()];
    	v_diff.get(floatArrayOp2);
    	float[][] u_total = new float[height][width];
    	float[][] v_total = new float[height][width];
    	double[][] mag_total = new double[height][width];
    	int zy = -1;
    	int zx = 0;
    	for(int z = 0; z < floatArrayOp2.length; z++) {
    		if (z % width == 0) {
    			zy += 1;
    			zx = 0;
    		}
    		u_total[zy][zx] = floatArrayOp2[z];
    		v_total[zy][zx] = floatArrayOp[z];
        	double final_float = main_package.getListMags().get(index)[z] * pixel_value * fps_value;
    		//mag_total[zy][zx] = main_package.getListMags().get(index)[z];
        	mag_total[zy][zx] = final_float;
    		zx += 1;
    	}
  	
    	
    	GraphicsContext gc = canvas1.getGraphicsContext2D();
    	if (to_merge == false) {
    		gc.setFill(Color.BLACK);
    		gc.fillRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
    	} else {
			Image backgroundImage = SwingFXUtils.toFXImage(backgroundImageBuf , null);
    		gc.drawImage(backgroundImage, 0, 0);
    	}
    	
    	int maxx = 0;
    	for(int x = 0; x < width; x=x+ywindow){
    		maxx += 1;
    	}
    	int maxy = 0;
    	for(int y = 0; y < height; y=y+xwindow){
    		maxy += 1;
    	}
    	
    	float[][] u_skip = new float[maxy][maxx];
    	float[][] v_skip = new float[maxy][maxx];
    	double[][] mag_skip = new double[maxy][maxx];
    	int zy2 = 0;
    	for (int zy1 = 0; zy1 < height; zy1=zy1+xwindow) {
    		int zx2 = 0;
    		for (int zx1 = 0; zx1 < width; zx1=zx1+ywindow) {
    			u_skip[zy2][zx2] = u_total[zy1][zx1];
    			v_skip[zy2][zx2] = v_total[zy1][zx1];
    			mag_skip[zy2][zx2] = mag_total[zy1][zx1];
    			zx2 += 1;
    		}
    		zy2 += 1;
    	}
    	float vec1[][] = new float[maxy][maxx];
    	for(int y = 0; y < maxy; y++) {
    		for (int x=0; x < maxx; x++) {
    			vec1[y][x] = ywindow * x;
    		}
    	}
    	float vec2[][] = new float[maxy][maxx];
    	for(int y = 0; y < maxy; y++) {
    		for (int x=0; x < maxx; x++) {
    			vec2[y][x] = xwindow * y;
    		}
    	}
        this_jet = new ColorMap(scale_start, scale_end, JET);
    	for(int y = 0; y < maxy; y++) {
    		for (int x = 0; x < maxx; x++) {
    			float x0 = vec1[y][x];
    			float x1 = vec1[y][x] + v_skip[y][x];
    			float y0 = vec2[y][x];
    			float y1 = vec2[y][x] - u_skip[y][x];
    			Color this_color;
    			if (mag_skip[y][x]< (float)mask_value) {
    				this_color = new Color(0.0,0.0,0.0, 0.0);
    			} else {
            		java.awt.Color a = this_jet.getColor(mag_skip[y][x]);
            		this_color = new Color( (Double.valueOf(a.getRed()) / 255.0), (Double.valueOf(a.getGreen()) / 255.0 ) , (Double.valueOf(a.getBlue()) / 255.0), 1.0);
            		drawArrow(gc, x0, y0, x1, y1, this_color, index);
    			}
    		}
    	}
    	WritableImage writableImage = new WritableImage((int) canvas1.getWidth(), (int)canvas1.getHeight());
    	SnapshotParameters sp = new SnapshotParameters();
    	sp.setFill(Color.TRANSPARENT);
        canvas1.snapshot(sp, writableImage);
        return writableImage;
	}
	
		
    void drawArrow(GraphicsContext gc, float x1, float y1, float x2, float y2, Color color, int index) {
        gc.setFill(color);
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        Transform transform = Transform.translate(x1, y1);
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));
        gc.setStroke(color);
        gc.setLineWidth(1.8);
        gc.strokeLine(0, 0, len * -10, 0);
        gc.fillPolygon(new double[]{len + 6.5, len- ARR_SIZE, len - ARR_SIZE, len + 6.5}, new double[]{0, -ARR_SIZE , ARR_SIZE, 0}, 4);
    }
    
    private double sigma = 0.15;
    
	public Mat autoCanny(Mat img_src) {
		UByteRawIndexer sI = img_src.createIndexer();
		double [] pixel_values =  new double[img_src.rows() * img_src.cols()];
        int nbChannels = img_src.channels();
		int z = 0;
		for (int y = 0; y < img_src.rows(); y++) {
	        for (int x = 0; x < img_src.cols(); x++) {
	        	pixel_values[z] = sI.get(y, x);
	        	z++;
	        }
		}
		Median median = new Median();
		double medianValue = median.evaluate(pixel_values);
		System.out.println("medianValue");
		System.out.println(medianValue);
		int lower = (int) Math.round(Math.max(0.0, ((0.1-sigma) * medianValue) ) );
		int upper = (int) Math.round(Math.min(255.0, ((0.1+sigma) * medianValue) ) );
		Mat edged = new Mat();
		org.bytedeco.javacpp.opencv_imgproc.Canny(img_src, edged, lower, upper);
		return edged;
//		return edged;
	}
    
	private int kernel_dilation = 3;
	private int blur_size = 5;
	private int kernel_erosion = 11;
	private int kernel_smoothing_contours = 5;
	private int border_value = 60;
		
    public Mat generateContour(Mat img_src) {
//    	https://github.com/bytedeco/javacv/blob/master/samples/ImageSegmentation.java
    	Mat blur = new Mat();
    	org.bytedeco.javacpp.opencv_core.Size rect = new org.bytedeco.javacpp.opencv_core.Size(blur_size,blur_size);
    	org.bytedeco.javacpp.opencv_imgproc.GaussianBlur(img_src, blur, rect , 0);
    	Mat edged = autoCanny(blur);
    	Mat kernel = Mat.ones(kernel_dilation, kernel_dilation, CV_8U).asMat();
    	Mat dilated = new Mat();
    	org.bytedeco.javacpp.opencv_imgproc.dilate(edged, dilated, kernel);
    	Mat mask = Mat.zeros(img_src.rows()+2, img_src.cols()+2, CV_8U).asMat();
    	Mat floodfilled = dilated.clone();
//    	Rect filling = new Rect();
    	org.bytedeco.javacpp.opencv_imgproc.floodFill(floodfilled, mask, new Point(0, 0), new Scalar(255, 255, 255, 255));
    	Mat floodfill_inverse = new Mat();
    	org.bytedeco.javacpp.opencv_core.bitwise_not(floodfilled, floodfill_inverse);
    	Mat FillingHoles = org.bytedeco.javacpp.opencv_core.or(dilated, floodfill_inverse).asMat();
    	Mat kernel2 = Mat.ones(kernel_erosion, kernel_erosion, CV_8U).asMat();
    	Mat eroded = new Mat();
    	org.bytedeco.javacpp.opencv_imgproc.erode(FillingHoles, eroded, kernel2);
    	Mat kernel3 = Mat.ones(kernel_smoothing_contours , kernel_smoothing_contours , CV_8U).asMat();
    	Mat smoothedContour = new Mat();
    	org.bytedeco.javacpp.opencv_imgproc.morphologyEx(eroded, smoothedContour, org.bytedeco.javacpp.opencv_imgproc.MORPH_OPEN, kernel3);
//    	imwrite("/home/marcelo/Desktop/testsmoothedContour.tif", smoothedContour);
    	Mat smoothedContourThresholded = new Mat();
    	double ret = org.bytedeco.javacpp.opencv_imgproc.threshold(smoothedContour, smoothedContourThresholded, 250.0, 255.0, 0);
    	MatVector contours = new MatVector();
    	Mat hierarchy = new Mat();
    	org.bytedeco.javacpp.opencv_imgproc.findContours(smoothedContourThresholded, contours, hierarchy, org.bytedeco.javacpp.opencv_imgproc.RETR_LIST, org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE);
    	System.out.println("contoured!");
    	Mat mask2 = Mat.zeros(img_src.rows(), img_src.cols(), CV_8U).asMat();
    	int areaIndex = -1;
    	double largestArea = 0.0;
    	MatVector contour = new MatVector();
        for (int i = 0; i < contours.size(); i++) {
            //  Find the area of contour
            double area = org.bytedeco.javacpp.opencv_imgproc.contourArea(contours.get(i), false);
            if (area > largestArea) {
            	largestArea = area;
            	areaIndex = i;
            }
        }
    	contour.put(contours.get(areaIndex));
		//https://stackoverflow.com/questions/33800557/python-opencv-draw-contour-only-on-the-outside-border
        org.bytedeco.javacpp.opencv_imgproc.drawContours(mask2, contour, 0, new Scalar(255,255,255,255), border_value, 8, new Mat(), 0, new Point(0,0));
        org.bytedeco.javacpp.opencv_imgproc.drawContours(mask2, contours, areaIndex, new Scalar(255,255,255,255));
        org.bytedeco.javacpp.opencv_imgproc.fillPoly(mask2, contour, new Scalar(255,255,255,255));
        Mat ObjectInterest = mask2.clone();
		Mat combinedImage = new Mat();
//		org.bytedeco.javacpp.opencv_core.addWeighted(img_src, 0.7, mask2, 0.3, 0, combinedImage);
//		imwrite("/home/marcelo/Desktop/mask2.tif", mask2); //TODO REMOVE
//		imwrite("/home/marcelo/Desktop/test.tif", combinedImage); //TODO REMOVE
		Mat cropped = new Mat();
		img_src.copyTo(cropped, mask2);
//		imwrite("/home/marcelo/Desktop/cropped.tif", cropped); //TODO REMOVE
//		return cropped;
		return mask2;
    }
    
    
    
    
	public class SplineShow implements ActionListener {
	    private JFreeChart chart;
	    private ChartPanel panel;
	    private XYPlot plot;
	    private XYDataset dataset;
	    private XYLineAndShapeRenderer original_render;
		private boolean is_curves_on;
		
		public SplineShow (ChartPanel panel1) {
	        this.panel = panel1;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	        this.original_render = (XYLineAndShapeRenderer) plot.getRenderer();
	        this.is_curves_on = false;
//			parent_controller.setPolynonState(!this.is_curves_on);
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			boolean state = this.is_curves_on;
			this.is_curves_on = !state;
			if (this.is_curves_on) {
	  			XYSplineRenderer renderer = new XYSplineRenderer();
	  			renderer.setDefaultShapesVisible(false);
	  	        renderer.setDefaultShapesFilled(false);
		        for (int i = 0; i < this.plot.getDataset().getSeriesCount(); i++) {
			        renderer.setSeriesPaint(i, original_render.getSeriesPaint(i));		        	
		        }
		        this.chart.getXYPlot().setRenderer(renderer);
		        main_package.getPlot_preferences().setSplineDefaultState(true);
			} else {
	  			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	  			renderer.setDefaultShapesVisible(false);
	  	        renderer.setDefaultShapesFilled(false);
		        for (int i = 0; i < this.plot.getDataset().getSeriesCount(); i++) {
			        renderer.setSeriesPaint(i, original_render.getSeriesPaint(i));		        	
		        }
		        this.chart.getXYPlot().setRenderer(renderer);
			}
		}
	}
    
    public SpinnerValueFactory<Double> facGen(double start, double end, double init, double step) {
    	SpinnerValueFactory.DoubleSpinnerValueFactory dblFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(start, end, init, step);
    	dblFactory.setConverter(new StringConverter<Double>() {
    		private final DecimalFormat df = new DecimalFormat("#.#########");
    		@Override public String toString(Double value) {
    			if (value == null) {
    				return "";
    				}
    			return df.format(value);
    		}
    		@Override public Double fromString(String value) {
    			try {
    				// If the specified value is null or zero-length, return null
    				if (value == null) {
    					return null;
    				}
    				value = value.trim();
    				if (value.length() < 1) {
    					return null;
    				}
    				// Perform the requested parsing
    				return df.parse(value).doubleValue();
    			} catch (ParseException ex) {
    				throw new RuntimeException(ex);
    			}
    		}
    	});
    	return dblFactory;
    }
    
      
//    private static final PseudoClass PRESSED = PseudoClass.getPseudoClass("pressed");
//  
//    class IncrementHandler implements EventHandler<MouseEvent> {
//        private Spinner spinner;
//        private boolean increment;
//        private long startTimestamp;
//        private int currentFrame = 0;
//        private int previousFrame = 0;  
//
//        private long initialDelay = 1000l * 1000L * 750L; // 0.75 sec
//        private Node button;
//
//        private final AnimationTimer timer = new AnimationTimer() {
//
//            @Override
//            public void handle(long now) {
//            	if (currentFrame == previousFrame || currentFrame % 10 == 0) {
//	                if (now - startTimestamp >= initialDelay) {
//	                    // trigger updates every frame once the initial delay is over
//	                    if (increment) {
//	                        spinner.increment();
//	                    } else {
//	                        spinner.decrement();
//	                    }
//	                }
//            	}
//            	++currentFrame;
//            }
//        };
//
//        @Override
//        public void handle(MouseEvent event) {
//            if (event.getButton() == MouseButton.PRIMARY) {
//                Spinner source = (Spinner) event.getSource();
//                Node node = event.getPickResult().getIntersectedNode();
//
//                Boolean increment = null;
//                // find which kind of button was pressed and if one was pressed
//                while (increment == null && node != source) {
//                    if (node.getStyleClass().contains("increment-arrow-button")) {
//                        increment = Boolean.TRUE;
//                    } else if (node.getStyleClass().contains("decrement-arrow-button")) {
//                        increment = Boolean.FALSE;
//                    } else {
//                        node = node.getParent();
//                    }
//                }
//                if (increment != null) {
//                    event.consume();
//                    source.requestFocus();
//                    spinner = source;
//                    this.increment = increment;
//
//                    // timestamp to calculate the delay
//                    startTimestamp = System.nanoTime();
//
//                    button = node;
//
//                    // update for css styling
//                    node.pseudoClassStateChanged(PRESSED, true);
//
//                    // first value update
//                    timer.handle(startTimestamp + initialDelay);
//
//                    // trigger timer for more updates later
//                    timer.start();
//                }
//            }
//        }
//
//        public void stop() {
//            timer.stop();
//            button.pseudoClassStateChanged(PRESSED, false);
//            button = null;
//            spinner = null;
//            previousFrame = currentFrame;
//        }
//    }
    
}
