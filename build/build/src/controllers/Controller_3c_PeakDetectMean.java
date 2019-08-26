package controllers;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

//import org.apache.commons.math.MathException;
//import org.apache.commons.math.distribution.NormalDistribution;
//import org.apache.commons.math.distribution.NormalDistributionImpl;
//import org.apache.commons.math3.distribution.NormalDistribution;
//import org.apache.commons.math3.distribution.NormalDistributionImpl;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.border.Border;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Group;
import model.ImageGroup;
import model.PackageData;
import model.TimeSpeed;
import model.VideoGroup;

public class Controller_3c_PeakDetectMean implements Initializable {
	private PackageData main_package;
	
	private static Group currentGroup;
	private JFreeChart currentChart;
	private double fps_value;
	private double pixel_value;
	private double average_value;
	private double upper_limit;
	private Marker current_marker;
//	private double lowerBoundDomain;
//    private double upperBoundDomain;
//	private double lowerBoundRange;
//    private double upperBoundRange;

	@FXML
	Button cmdNext;
	
	@FXML
	Button cmdBack;
	
	@FXML
	Button btnZoomMode;
	
	@FXML
	Button btnSelectMode;
	
	@FXML
	TextField txtAverage;
	
	private int fromField;
	private int toField;
//    @FXML
//    private TextField fromField;
//
//    @FXML
//    private TextField toField;
//	
    @FXML
	private SwingNode swgChart;
	
	@FXML
	private GridPane gridInsert;

	private List<TimeSpeed> timespeedlist;
	
	@FXML
    void handleMenuNewImage(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		FileReader.chooseSourceDirectory(primaryStage, url, main_package);
    }
    
    @FXML
    void handleMenuNewVideo(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
    	FileReader.chooseVideoFiles(primaryStage, url, main_package);
    }

    @FXML
    void handleClose(ActionEvent event){
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
    	primaryStage.close();
    }
    
    @FXML
    void handleCheckProgress(ActionEvent event) throws IOException {
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	
    	URL url = getClass().getResource("FXML_2a_ProgressBar.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
//        	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//        	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	((Controller_2a_ProgressBar)fxmlloader.getController()).setContext(main_package);
    	primaryStage.setTitle("ContractionWave - Processing Progress");
//    		primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
    	primaryStage.show();
    	
    	primaryStage.setX(prior_X);
    	primaryStage.setY(prior_Y);
    }
    
    @FXML
    void handleChangeFolder(ActionEvent event){
    	if(currentGroup.getType() == 0){//ImageGroup
	    	DirectoryChooser directoryChooser = new DirectoryChooser();
	    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
	    	
	    	File selectedDirectory = directoryChooser.showDialog(primaryStage);
	
	    	if(selectedDirectory != null){
	    	     boolean result = ((ImageGroup) currentGroup).changeImagePath(selectedDirectory.getAbsolutePath());
	    	     if(result){
	    	    	 Alert alert = new Alert(AlertType.INFORMATION);
	    	    	 alert.setTitle("Path Changed");
	    	    	 alert.setContentText("The path has been successfully changed.");
	    	    	 alert.showAndWait();
	    	     }else{
	    	    	 Alert alert = new Alert(AlertType.WARNING);
	    	    	 alert.setTitle("Path Failed");
	    	    	 String errorFile = currentGroup.getErrorMessage();
	    	    	 alert.setContentText("Could not find " + errorFile + " file.");
	    	    	 alert.showAndWait();
	    	     }
	    	}
    	}else{//VideoGroup
    		FileChooser fileChooser = new FileChooser();
    		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
	    	
    		File selectedFile = fileChooser.showOpenDialog(primaryStage);
    		
    		if(selectedFile != null){
    			boolean result = ((VideoGroup) currentGroup).changeVideoFile(selectedFile);
    			if(result){
	    	    	 Alert alert = new Alert(AlertType.INFORMATION);
	    	    	 alert.setTitle("File Changed");
	    	    	 alert.setContentText("The file has been successfully changed.");
	    	    	 alert.showAndWait();
	    	     }else{
	    	    	 Alert alert = new Alert(AlertType.WARNING);
	    	    	 alert.setTitle("File Failed");
	    	    	 String errorFile = currentGroup.getErrorMessage();
	    	    	 alert.setContentText("Could not find " + errorFile + " file.");
	    	    	 alert.showAndWait();
	    	     }
    		}
    	}
    }
    
    @FXML
    void handleReinitialize(ActionEvent event) throws IOException, ClassNotFoundException{
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	scene.removeEventFilter(KeyEvent.KEY_RELEASED, zoomKey);
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());

		((Controller_1_InitialScreen)fxmlloader.getController()).setContext(new PackageData(main_package.isLoad_preferences()));
		primaryStage.setTitle("ContractionWave");
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
    	stage.setTitle("ContractionWave");
		stage.initModality(Modality.APPLICATION_MODAL);
		//stage.initOwner(((Node)event.getSource()).getScene().getWindow());
    	stage.show();
    }
	
    @FXML
    void handleExportTSV(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.tsv");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
		writeTSV(file);
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
    }
        
    public void writeTSV(File file) throws Exception {
	    Writer writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(file));
	        String text2 = "Time (s)\tSpeed (\u00B5/s)\r\n";
	        writer.write(text2);
	        
	        for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
				double average = currentGroup.getMagnitudeListValue(i);
				writer.write(String.valueOf(i / fps_value));
				writer.write("\t");
				writer.write(String.valueOf(average * fps_value * pixel_value));
				writer.write("\r\n");
			}	        
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    finally {
	        writer.flush();
	        writer.close();
	    } 
	}
    
    @FXML
    void handleExportXLS(ActionEvent event) throws IOException{
    	Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("sample");
		Row row = spreadsheet.createRow(0);
		
		row.createCell(0).setCellValue("Time (s)");
		row.createCell(1).setCellValue("Average Speed (\u00B5/s)");
		
		for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
			double average = currentGroup.getMagnitudeListValue(i);
			row = spreadsheet.createRow(i + 1);
			
			row.createCell(0).setCellValue(i / fps_value);
			row.createCell(1).setCellValue(average * fps_value * pixel_value);			
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("time-speed.xls");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
		FileOutputStream fileOut = new FileOutputStream(file);
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
		
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
    }
    
    @FXML
    void handleExportJPEG(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.jpg");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        ChartPanel panel = (ChartPanel) swgChart.getContent();
        OutputStream out = new FileOutputStream(file);
        ChartUtils.writeChartAsJPEG(out,
                currentChart,
                panel.getWidth(),
                panel.getHeight());
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
    }
    
    @FXML
    void handleExportPNG(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.png");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        ChartPanel panel = (ChartPanel) swgChart.getContent();
        OutputStream out = new FileOutputStream(file);
        ChartUtils.writeChartAsPNG(out,
                currentChart,
                panel.getWidth(),
                panel.getHeight());
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
    }
    
    @FXML
    void handleExportTIFF(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.tiff");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        ChartPanel panel = (ChartPanel) swgChart.getContent();
        JFreeChart chart = panel.getChart();
        BufferedImage bImage = chart.createBufferedImage(panel.getWidth(), panel.getHeight());
        
        Mat imgLayer = Java2DFrameUtils.toMat(bImage);
        MatVector channels = new MatVector();
        Mat imgLayerRGB = new Mat(bImage.getHeight(), bImage.getWidth(), org.bytedeco.javacpp.opencv_core.CV_8UC3);
        org.bytedeco.javacpp.opencv_core.split(imgLayer, channels);
        Mat blueCh = channels.get(1);
        Mat greenCh = channels.get(2);
        Mat redCh = channels.get(3);
        MatVector channels2 = new MatVector(3);
        channels2.put(0, redCh);
        channels2.put(1, greenCh);
        channels2.put(2, blueCh);
        org.bytedeco.javacpp.opencv_core.merge(channels2, imgLayerRGB);
		imwrite(file.getCanonicalPath(), imgLayerRGB);
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
		ShowSavedDialog.showDialog();
    }
    
    @FXML
    void handleColors(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	ShowMarkerColorThickDialog.showDialog(main_package, plot, current_marker);
    }   
    
    private void commitColors() {
    	XYPlot plot = currentChart.getXYPlot();
//    	main_package.getPlot_preferences().setSeriesColorRGB( (java.awt.Color) plot.getRenderer().getSeriesPaint(0));
    	main_package.getPlot_preferences().setRangeGridColorRGB( (java.awt.Color) plot.getRangeGridlinePaint());
    	main_package.getPlot_preferences().setDomainGridColorRGB( (java.awt.Color) plot.getDomainGridlinePaint());
    	main_package.getPlot_preferences().setBackgroundColorRGB( (java.awt.Color) plot.getBackgroundPaint());
//    	main_package.getPlot_preferences().setMarkerAlpha();
//    	main_package.getPlot_preferences().setMarkerColorRGB();
    }
	
    
	@FXML
	void back(ActionEvent event) throws IOException, ClassNotFoundException {
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_3b2_DisplayResults.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	scene.removeEventFilter(KeyEvent.KEY_RELEASED, zoomKey);
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	commitColors();
		((Controller_3b2_DisplayResults)fxmlloader.getController()).setContext(main_package, currentGroup.getName(), fps_value, pixel_value);
		primaryStage.setTitle("ContractionWave - View First Results");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
	}

	private boolean validation(){
		String value1 = txtAverage.getText();
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setContentText("Please select area before advancing.");
		
		try{
			average_value = Double.parseDouble(value1);
		}catch(NumberFormatException e) {
//			JOptionPane.showMessageDialog(null, "Please select area before advancing.","Warning",JOptionPane.WARNING_MESSAGE);
			alert.showAndWait();
	        return false;
	    } catch(NullPointerException e) {
//	    	JOptionPane.showMessageDialog(null, "Please select area before advancing.","Warning",JOptionPane.WARNING_MESSAGE);
	    	alert.showAndWait();
	        return false;
	    }
		return true;
	}
	
	
	private boolean zoom_mode = false;
	
	public void triggerSelectMode() {
		zoom_mode = false;
		XYPlot plot_n = (XYPlot) currentChart.getPlot();
		ChartPanel now_linepanel2 = (ChartPanel)swgChart.getContent();
		MouseListener[] mouseListArray = now_linepanel2.getMouseListeners();
		for (int i = 0; i < mouseListArray.length; i++) {
			now_linepanel2.removeMouseListener(mouseListArray[i]);
		}
		MouseMotionListener[] mouseListArray2 = now_linepanel2.getMouseMotionListeners();
		for (int i = 0; i < mouseListArray2.length; i++) {
			now_linepanel2.removeMouseMotionListener(mouseListArray2[i]);
		}
		now_linepanel2.setRangeZoomable(false);
		now_linepanel2.setDomainZoomable(false);
		now_linepanel2.addMouseListener(mousemark);
		now_linepanel2.addMouseMotionListener(mousemark);
	}
	
	@FXML
	void handleSelectMode(ActionEvent event) {
		triggerSelectMode();
	}
	
	public void triggerZoomMode() {
		zoom_mode = true;
		XYPlot plot_n = (XYPlot) currentChart.getPlot();
		ChartPanel now_linepanel2 = (ChartPanel)swgChart.getContent();
		now_linepanel2.setMouseWheelEnabled(true);
		now_linepanel2.setRangeZoomable(true);
		now_linepanel2.setDomainZoomable(true);
		MouseListener[] mouseListArray = now_linepanel2.getMouseListeners();
		for (int i = 0; i < mouseListArray.length; i++) {
			now_linepanel2.removeMouseListener(mouseListArray[i]);
		}
		MouseMotionListener[] mouseListArray2 = now_linepanel2.getMouseMotionListeners();
		for (int i = 0; i < mouseListArray2.length; i++) {
			now_linepanel2.removeMouseMotionListener(mouseListArray2[i]);
		}
		now_linepanel2.setMouseWheelEnabled(true);
		now_linepanel2.setRangeZoomable(true);
		now_linepanel2.setDomainZoomable(true);
		now_linepanel2.addMouseListener(now_linepanel2);
		now_linepanel2.addMouseMotionListener(now_linepanel2);
	}
	
	@FXML
	void handleZoomMode(ActionEvent event) {
		triggerZoomMode();
	}
	
	
	@FXML
	void nextPageNavigate(ActionEvent event) throws ClassNotFoundException, IOException {
		if(!validation()) return;	
		calculateLowerUpperConfidenceBoundaryPercent(0.99, fromField, toField);
		Stage primaryStage = (Stage) cmdNext.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_3d_MagnitudeFirstCharts.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	scene.removeEventFilter(KeyEvent.KEY_RELEASED, zoomKey);
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Group g1 = currentGroup;
    	commitColors();
		((Controller_3d_MagnitudeFirstCharts)fxmlloader.getController()).setContext(main_package, g1, fps_value, pixel_value, average_value, upper_limit, timespeedlist);	
		primaryStage.setTitle("ContractionWave - First Analysis");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdNext.setGraphic(new ImageView(imgNext));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Display First Analysis");
		cmdNext.setTooltip(tooltip5);
		
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Back to group selection");
		cmdBack.setTooltip(tooltip6);
		
	}

	private EventHandler<KeyEvent> zoomKey; 
//	public void setContext(PackageData main_package2, String selecteditem, double number1, double number2, boolean use_double2) throws IOException, ClassNotFoundException {
	public void setContext(PackageData main_package2, Group g2, double number1, double number2, List<TimeSpeed> timespeedlist2) throws IOException, ClassNotFoundException {
		main_package = main_package2;
		fps_value = number1;
		pixel_value = number2;
		currentGroup = g2;
		timespeedlist = timespeedlist2;
		createPlot();
		zoomKey = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.Z) {
					triggerZoomMode();
				} else if (event.getCode() == KeyCode.S) {
					triggerSelectMode();
				}
			}
		};
		cmdNext.getScene().addEventFilter(KeyEvent.KEY_RELEASED, zoomKey);
	}
	
	
	private boolean previousPlot = false;
//	private ChartPanel general_linepanel2;
	
	private MouseMarker mousemark;
	
	private void createPlot() {
		currentChart = createChart(createDataset());
		ChartPanel linepanel2 = new ChartPanel(currentChart);
		JCheckBoxMenuItem gridLinesmenuItem2 = new JCheckBoxMenuItem();
		gridLinesmenuItem2.setSelected(true);
//		JCheckBoxMenuItem showPolynomial = new JCheckBoxMenuItem();
		
		gridLinesmenuItem2.setText("Gridlines on/off");
//		showPolynomial.setText("Polynomial on/off");
		
		GridLinesSwitch gridLinesZoomAction2 = new GridLinesSwitch(linepanel2); 
		gridLinesmenuItem2.addActionListener(gridLinesZoomAction2);
		
		linepanel2.getPopupMenu().add(gridLinesmenuItem2);
		
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		showSpline.setText("Render Splines on/off");
		SplineShow splineRendering = new SplineShow(linepanel2);
		showSpline.addActionListener(splineRendering);
		linepanel2.getPopupMenu().add(showSpline);
		
		Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        linepanel2.setBorder(border);
        linepanel2.setMouseWheelEnabled(true);
        
		
		linepanel2.setRangeZoomable(false);
		linepanel2.setDomainZoomable(false);
		mousemark = new MouseMarker(linepanel2);
		linepanel2.addMouseListener(mousemark);
		linepanel2.addMouseMotionListener(mousemark);
		//linepanel2.addke
		//linepanel2.removeMo

		/*
		linepanel2.addChartMouseListener(new ChartMouseListener(){

			@Override
			public void chartMouseClicked(ChartMouseEvent e) {
				System.out.println("CHART: " + String.valueOf(e.getTrigger().getY()));
				
			}
			
			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		*/
		
		
		swgChart.setContent(linepanel2);
		previousPlot = true;
	}
	
	private XYDataset createDataset() {
		XYSeries series1 = new XYSeries("Coordinates");
        
//		for (int j = 0; j < 20; j++) {
			for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
				double average = currentGroup.getMagnitudeListValue(i);
				series1.add(i / fps_value, average * fps_value * pixel_value);
//				series1.add(i+(currentGroup.getMagnitudeSize()*j), average);
			}
//		}
		//peak detection algorithm receives a group
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        return dataset;

    }
	
	private JFreeChart createChart(XYDataset dataset) {			
        JFreeChart chart = ChartFactory.createXYLineChart(
        	currentGroup.getName(),
            "Time (s)",
            "Average Speed (\u00B5m/s)",
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
//        lowerBoundDomain = chart.getXYPlot().getDomainAxis().getLowerBound();
//        upperBoundDomain = chart.getXYPlot().getDomainAxis().getUpperBound();
//    	lowerBoundRange = chart.getXYPlot().getRangeAxis().getLowerBound();
//        upperBoundRange = chart.getXYPlot().getRangeAxis().getUpperBound();

        plot.setDomainPannable(true);
        plot.setRangePannable(true);


        plot.setBackgroundPaint(main_package.getPlot_preferences().getBackgroundColorRGB());
        plot.setDomainGridlinePaint(main_package.getPlot_preferences().getDomainGridColorRGB());
        plot.setRangeGridlinePaint(main_package.getPlot_preferences().getRangeGridColorRGB());
        plot.setDomainGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
        plot.setRangeGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
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
	
//	private void defineAverages(int lower, int upper) {
//		double average_val = 0.0;
//    	for (int i = lower; i < upper; i++) {
//        	double value = currentGroup.getMagnitudeListValue(i);
//        	average_val += value;
//        }
//    	average_value = average_val / (upper - lower);
//    	txtAverage.setText(String.valueOf(average_value));
//	}
	
	
	public class SplineShow implements ActionListener {
	    private JFreeChart chart;
	    private ChartPanel panel;
	    private XYPlot plot;
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
	

	private void calculateLowerUpperConfidenceBoundaryPercent(double percent, int lower, int upper) {
	    // calculate the mean value (= average)
		List<Double> givenNumbers = currentGroup.getMagnitudeList();
	    double sum = 0.0;
	    for (int i = lower; i < upper; i++) {
	    	double num = givenNumbers.get(i);
	        sum += num;
	    }
	    double mean = sum / givenNumbers.size();
	    
//	    average_value = mean;
//    	txtAverage.setText(String.valueOf(average_value));
    	
	    // calculate standard deviation
	    double squaredDifferenceSum = 0.0;
	    for (double num : givenNumbers) {
	        squaredDifferenceSum += (num - mean) * (num - mean);
	    }
	    double variance = squaredDifferenceSum / givenNumbers.size();
	    double standardDeviation = Math.sqrt(variance);
	    //https://stackoverflow.com/questions/6353678/calculate-normal-distrubution-using-java
	    //https://stackoverflow.com/questions/30340551/java-calculate-confidence-interval
	    // value for 95% confidence interval, source: https://en.wikipedia.org/wiki/Confidence_interval#Basic_Steps
	    NormalDistribution d = new NormalDistribution(0, standardDeviation);
	    double confidenceLevel = d.inverseCumulativeProbability(percent);
	    //double confidenceLevel = 1.96;
	    double temp = confidenceLevel * standardDeviation / Math.sqrt(givenNumbers.size());
	    upper_limit = mean + temp;
	}
	
	
	private Double last_start = Double.NaN;
	private Double last_end = Double.NaN;
	private final class MouseMarker extends MouseAdapter{
	    private Marker marker;
	    private Double markerStart = Double.NaN;
	    private Double markerEnd = Double.NaN;
	    private final XYPlot plot;
	    private final JFreeChart chart;
	    private  final ChartPanel panel;
	    private Double x_start = Double.NaN;
	    private Double x_end = Double.NaN;
	    private Double y_start = Double.NaN;
	    private Double y_end = Double.NaN;
	    private XYShapeAnnotation shapeAnnotation;

	    public MouseMarker(ChartPanel panel) {
	        this.panel = panel;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	    }
	    
	    public void createMarker(Double mStart, Double mEnd) {
	    	markerStart = mStart;
	    	markerEnd = mEnd;
	    	updateMarker();
	    }
	    
	    private void updateMarker(){
	        if (marker != null){
	            plot.removeDomainMarker(marker,Layer.BACKGROUND);
	        }
	        if (markerStart.equals(markerEnd)) {
	    	    markerStart = Double.NaN;
	    	    markerEnd = Double.NaN;
	        }
	        if (!markerStart.isNaN()) {
	        	if(!markerEnd.isNaN()) {
//	        	int v1 = markerStart.intValue();
//                int v2 = markerEnd.intValue();
	        	last_start = markerStart;
	        	last_end = markerEnd;
                double v1 = markerStart.doubleValue();
                double v2 = markerEnd.doubleValue();
	        	if ( v2 > v1){
	                marker = new IntervalMarker(markerStart, markerEnd);
	        		List<Double> givenNumbers = currentGroup.getMagnitudeList();
	        	    double sum = 0.0;
	        	    int size_numbers = 0;
	        	    boolean start = false;
	        	    int ind = -1;
	        	    double max = 0.0;
	        	    List<Double> values = new ArrayList<Double>();
	        	    for (TimeSpeed a : timespeedlist) {
	        	    	double value = a.getTime();
	        	    	if (value >= v1 && value <= v2) {
	        	    		ind = a.getIndex();
	        	    		double num = givenNumbers.get(ind);
	        	    		sum += num * pixel_value * fps_value;
	        	    		values.add(num * pixel_value * fps_value);
	        	    		if ( (num * pixel_value * fps_value) > max) {
	        	    			max = (num * pixel_value * fps_value);
	        	    		}
	        	    		size_numbers += 1;
	        	    		if (start == false) {
	        	                fromField = ind;
	        	    		}
	        	    		start = true;
	        	    	}
	        	    }
	        	    if (ind != -1) {
	        	    	toField = ind;
	        	    }
	        	    
	        	    StandardDeviation sdObj = new StandardDeviation();
	        	    double[] dValues = new double[values.size()];
	        	    for(int i = 0; i < values.size(); i++){
	        	    	Double dObj = values.get(i);
	        	    	double dVal = dObj.doubleValue();
	        	    	System.out.println(dVal);
	        	    	dValues[i] = dVal;
	        	    }
	        	    double sd = sdObj.evaluate(dValues);
	        	    
	        	    double mean = sum / size_numbers;
	        	    
//	        	    txtAverage.setText(String.valueOf(mean));
	        	    txtAverage.setText(String.valueOf(mean+sd));
	                //marker.setPaint(new java.awt.Color(0xDD, 0xFF, 0xDD, 0x80));
	        	    current_marker = marker;
	                marker.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
	                marker.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
	                plot.addDomainMarker(marker,Layer.BACKGROUND);
	            } else {
	                marker = new IntervalMarker(markerEnd, markerStart);
	        		List<Double> givenNumbers = currentGroup.getMagnitudeList();
	        	    double sum = 0.0;
	        	    int size_numbers = 0;
	        	    boolean start = false;
	        	    int ind = -1;
	        	    for (TimeSpeed a : timespeedlist) {
	        	    	double value = a.getSpeed();
	        	    	if (value >= v2 && value <= v1) {
	        	    		ind = a.getIndex();
	        	    		double num = givenNumbers.get(ind);
	        	    		sum += num * pixel_value * fps_value;
	        	    		size_numbers += 1;
	        	    		if (start == false) {
	        	                fromField = ind;
	        	    		}
	        	    		start = true;
	        	    	}
	        	    }
	        	    if (ind != -1) {
	        	    	toField = ind;
	        	    }
	        	    double mean = sum / size_numbers;
	        	    txtAverage.setText(String.valueOf(mean));
	                //marker.setPaint(new java.awt.Color(0xDD, 0xFF, 0xDD, 0x80));
	        	    current_marker = marker;
	                marker.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
	                marker.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
	                plot.addDomainMarker(marker,Layer.BACKGROUND);
	            }
	        	
	        	}
	        }
	    }
	    private Double getPosition(MouseEvent e){
	    	Point2D p = e.getPoint();
	        Rectangle2D plotArea = panel.getScreenDataArea();
	        XYPlot plot = (XYPlot) chart.getPlot();
	        System.out.println(plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge()));
	        return plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	        markerEnd = getPosition(e);
	        try {
	        	plot.removeAnnotation(shapeAnnotation);
	        	updateMarker();
			} catch (IllegalArgumentException z) {
				// TODO: handle exception
				z.printStackTrace();
			}
	    }
	    
	    @Override
	    public void mousePressed(MouseEvent e) {	    	
	    	markerStart = getPosition(e);
	        //start drawing event
	        Point2D p = e.getPoint();
	        Rectangle2D plotArea = panel.getScreenDataArea();
	        Rectangle2D dataArea = panel.getChartRenderingInfo().getPlotInfo().getDataArea();
	        x_start = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
//	        y_start = plot.getRangeAxis().java2DToValue(p.getY(), dataArea, plot.getRangeAxisEdge());
	        y_start = plot.getRangeAxis().java2DToValue(dataArea.getMinY(), dataArea, plot.getRangeAxisEdge());
	        //System.out.println("(" + String.valueOf(x_start) + "," + String.valueOf(y_start) + ")");
	    }
	    
	    @Override
	    public void mouseDragged(MouseEvent e) {
	    	Point2D p = e.getPoint();
	        Rectangle2D plotArea = panel.getScreenDataArea();
	        Rectangle2D dataArea = panel.getChartRenderingInfo().getPlotInfo().getDataArea();
	        x_end = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
//	        y_end = plot.getRangeAxis().java2DToValue(p.getY(), dataArea, plot.getRangeAxisEdge());
	        y_end = plot.getRangeAxis().java2DToValue(dataArea.getMaxY(), dataArea, plot.getRangeAxisEdge());
	        drawRect();
	    }
	    	    
	    public void drawRect() {
	    	if (shapeAnnotation != null) {
		    	plot.removeAnnotation(shapeAnnotation);
	    	}
	    	Double do_start_x = x_end;
	    	Double do_end_x = x_start;
	    	if (x_end > x_start) {
	    		do_start_x = x_start;
	    		do_end_x = x_end;
	    	}
	    	Double do_start_y = y_end;
	    	Double do_end_y = y_start;
	    	if (y_end > y_start) {
	    		do_start_y = y_start;
	    		do_end_y = y_end;
	    	}
	    	Rectangle2D.Double rect = new Rectangle2D.Double(do_start_x, do_start_y, do_end_x - do_start_x, do_end_y - do_start_y);
	    	//System.out.println(x_start + ","  + y_start + "," + x_end + "," + y_end);
	    	shapeAnnotation = new XYShapeAnnotation(rect);
	    	plot.addAnnotation(shapeAnnotation);
	    }
	}
	
	
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
//    class IncrementHandler implements EventHandler<javafx.scene.input.MouseEvent> {
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
//        public void handle(javafx.scene.input.MouseEvent event) {
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
