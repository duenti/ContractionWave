package controllers;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

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
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.chart.ui.Layer;

import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
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
import model.XYCircleAnnotation;

public class Controller_3d_MagnitudeFirstCharts implements Initializable {
	private static PackageData main_package;
	private static double fps_val;
	private static double pixel_val;
	private static Group currentGroup;
	private JFreeChart currentChart;
	private static JFreeChart currentZoomChart;
	private static boolean zoomGridLinesState = true;
	private boolean mainGridLinesState = true;
	private double average_value;
	private double upper_limit;
	private List<IntervalMarker> intervalsList = new ArrayList<IntervalMarker>();
	private static List<TimeSpeed> timespeedlist;
//	private static List<TimeSpeed> timespeedlist_new;
	private XYDataset dataset_general;
	
	//TODO: set colors by using mainPackage Preference
	
	@FXML
	private SwingNode swgChart;

	@FXML
	private SwingNode swgNodeBig;

	@FXML
	private Button cmdBack;

	@FXML
	private Button cmdNext;

	@FXML
	private Region region;
	
	@FXML
	private Menu menuGroup;
	
	@FXML
	Spinner<Double> spinnerDelta;
	
	@FXML
	Spinner<Double> spinnerInter;
	
	@FXML
	Spinner<Double> spinnerIntra;
	
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
				writer.write(String.valueOf(i / fps_val));
				writer.write("\t");
				writer.write(String.valueOf((average * fps_val * pixel_val)-average_value));
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
			
			row.createCell(0).setCellValue(i / fps_val);
			row.createCell(1).setCellValue((average * fps_val * pixel_val)-average_value);			
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
                currentZoomChart,
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
                currentZoomChart,
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
    	XYPlot plot2 = currentZoomChart.getXYPlot();
    	ShowMultMarkerColorThickDialog.showDialog(main_package, plot, plot2, intervalsList, valid_maximum_list, valid_minimum_list, first_points, fifth_points, last_addition);
    }
    
    @FXML
    void handleSetAverage(ActionEvent event) {
    	Dialog<Boolean> dialogAverageSet= new Dialog<>();
		dialogAverageSet.setHeaderText("Set Average Value:");
		dialogAverageSet.setResizable(true);
		Label label1 = new Label("Average Value (µ/s): ");
		Spinner<Double> averageSetSpin = new Spinner<Double>();
		SpinnerValueFactory<Double> dobFacN = facGen(0, Double.MAX_VALUE, average_value, 0.01);
		averageSetSpin.setValueFactory(dobFacN);
		averageSetSpin.setEditable(true);
		TextFormatter<Double> formatter1n = new TextFormatter<Double>(dobFacN.getConverter(), dobFacN.getValue());
		averageSetSpin.getEditor().setTextFormatter(formatter1n);
		dobFacN.valueProperty().bindBidirectional(formatter1n.valueProperty());
		formatter1n.valueProperty().addListener((s, ov, nv) -> {
			try {
				average_value = nv.doubleValue();
				maximum_list.clear();
				minimum_list.clear();
				valid_maximum_list.clear();
				valid_minimum_list.clear();
				first_points.clear();
				fifth_points.clear();
				rewriteLinePlotPop();
				//redraw markers to avoid caos
				XYPlot fplot = (XYPlot) currentChart.getPlot();
	//			intervalsList.clear();
				for (IntervalMarker az : intervalsList) {
					az.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
					az.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
					fplot.addDomainMarker(az,Layer.BACKGROUND);
				}
				writeFlowLinePlotZoom(last_min_zoom, last_max_zoom, last_addition);
				//reset previous zoom
				main_package.setDelta(Double.valueOf(spinnerDelta.getValue() / fps_val / pixel_val));
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});

		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(averageSetSpin, 2, 1);
		//grid.add(label2, 1, 2);
		//grid.add(fourierIndexSpin, 2, 2);
		dialogAverageSet.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		dialogAverageSet.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialogAverageSet.show();
		//average_value = average_value2;
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
    void back(ActionEvent event) throws IOException, ClassNotFoundException {
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();

    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_3c_PeakDetectMean.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	commitColors();
		((Controller_3c_PeakDetectMean)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, timespeedlist);
//		((Controller_3c_PeakDetectMean)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, timespeedlist_new);
		primaryStage.setTitle("ContractionWave - Define Gap Zone");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
    
    
    private boolean saved = false;
    
    @FXML
	void nextPageNavigate(ActionEvent event) throws IOException, ClassNotFoundException {

		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setContentText("Please select a Cycle in the Upper Plot by clicking and dragging.");
		if(intervalsList.size() == 0) {
//			JOptionPane.showMessageDialog(null, "Please select a Cycle in the Upper Plot by clicking and dragging.");
			alert.showAndWait();
			return;
		}
    	Stage primaryStage = (Stage) cmdNext.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
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
//    	JFreeChart currentChart2 = currentChart;
//    	((XYPlot)currentChart2.getPlot()).getDomainAxis().setAutoRange(true);
//    	((XYPlot)currentChart2.getPlot()).getRangeAxis().setAutoRange(true);
    	commitColors();
    	((Controller_3d2_PeakParametersPlot)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, average_value, upper_limit, intervalsList, valid_maximum_list, valid_minimum_list, first_points, fifth_points, timespeedlist, saved);
//    	((Controller_3d2_PeakParametersPlot)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, average_value, upper_limit, intervalsList, valid_maximum_list, valid_minimum_list, first_points, fifth_points, timespeedlist_new, saved);
    	primaryStage.setTitle("ContractionWave - Peak Parameters Plot");
//    	primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
	}
	    
	public void setContext(PackageData main_package_data, Group g1, double fps_val1, double pixel_val1, double average_value2, double upper_limit2, List<TimeSpeed>timespeedlist2) throws IOException, ClassNotFoundException {
		//read file, plot first graph, adapt second
		main_package = main_package_data;
		average_value = average_value2;
		upper_limit = upper_limit2;
		fps_val = fps_val1;
		pixel_val = pixel_val1;
		currentGroup = g1;
		timespeedlist = timespeedlist2;
		double delta1 = 1.0 * fps_val * pixel_val;
//		if (main_package.getDelta() > 0) {
//			delta1 = main_package.getDelta() * fps_val * pixel_val;
//		} else {
//			double median = 0.0;
//			List<Double> to_median = new ArrayList<Double>();
//			for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
//				double average = currentGroup.getMagnitudeListValue(i);
//				double avg2 = Double.valueOf((average * fps_val * pixel_val) - (average_value));
//				to_median.add(avg2);
//			}
//			if (to_median.size() % 2 == 0) {
//				median = (to_median.get((to_median.size() / 2)-1) + to_median.get(to_median.size() / 2)) / 2.0;
//			} else {
//				median = to_median.get( (int) (( (double) to_median.size() / 2.0) - 0.5));
//			}
////			delta1 = median;
//			delta1 = 1.0 * fps_val * pixel_val;
//			main_package.setDelta(median / fps_val / pixel_val);
//		}
		System.out.println("Delta: " + (main_package.getDelta() * fps_val * pixel_val));
		double inter1 = main_package.getInter();
		double intra1 = main_package.getIntra();
		resetAndRun();
		spinnerDelta.getValueFactory().setValue(delta1);
		spinnerInter.getValueFactory().setValue(inter1);
		spinnerIntra.getValueFactory().setValue(intra1);
	}
	
	public void resetAndRun() {
		intervalsList.clear();
		maximum_list.clear();
		minimum_list.clear();
		valid_maximum_list.clear();
		valid_minimum_list.clear();
		first_points.clear();
		fifth_points.clear();
		System.out.println("Resetted view");
		writeLinePlotPop();
		last_min_zoom = 0;
		last_max_zoom = currentGroup.getMagnitudeSize();
		last_addition = 0;
		writeFlowLinePlotZoom(0, currentGroup.getMagnitudeSize(), 0);
	}
	
	
		
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Back to microscope parametrization");
		cmdBack.setTooltip(tooltip5);
		
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdNext.setGraphic(new ImageView(imgNext));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Display and Save Jet, Quiver and Merge plots");
		cmdNext.setTooltip(tooltip6);
		
		SpinnerValueFactory deltaFac = facGen(0.0, 10000.0, 1.0, 1.0);
		spinnerDelta.setValueFactory(deltaFac);
		spinnerDelta.setEditable(true);
		TextFormatter formatter = new TextFormatter(deltaFac.getConverter(), deltaFac.getValue());
		spinnerDelta.getEditor().setTextFormatter(formatter);
		// bidi-bind the values
		deltaFac.valueProperty().bindBidirectional(formatter.valueProperty());
		formatter.valueProperty().addListener((s, ov, nv) -> {
		    // do stuff that needs to be done on commit
			maximum_list.clear();
			minimum_list.clear();
			valid_maximum_list.clear();
			valid_minimum_list.clear();
			first_points.clear();
			fifth_points.clear();
			rewriteLinePlotPop();
			//redraw markers to avoid caos
			XYPlot fplot = (XYPlot) currentChart.getPlot();
			for (IntervalMarker az : intervalsList) {
				az.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
				az.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
				fplot.addDomainMarker(az,Layer.BACKGROUND);
			}
			writeFlowLinePlotZoom(last_min_zoom, last_max_zoom, last_addition);
			main_package.setDelta(Double.valueOf(spinnerDelta.getValue() / fps_val / pixel_val));
		});

		SpinnerValueFactory intraFac = facGen(0.0, 10000.0, 1.0, 0.01);
		spinnerIntra.setValueFactory(intraFac);
		spinnerIntra.setEditable(true);
		
		TextFormatter formatter2 = new TextFormatter(intraFac.getConverter(), intraFac.getValue());
		intraFac.valueProperty().bindBidirectional(formatter2.valueProperty());
		spinnerIntra.getEditor().setTextFormatter(formatter2);
		formatter2.valueProperty().addListener((s, ov, nv) -> {
			maximum_list.clear();
			minimum_list.clear();
			valid_maximum_list.clear();
			valid_minimum_list.clear();
			first_points.clear();
			fifth_points.clear();
			rewriteLinePlotPop();
			//redraw markers to avoid caos
			XYPlot fplot = (XYPlot) currentChart.getPlot();
//			intervalsList.clear();
			for (IntervalMarker az : intervalsList) {
				az.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
				az.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
				fplot.addDomainMarker(az,Layer.BACKGROUND);
			}
			writeFlowLinePlotZoom(last_min_zoom, last_max_zoom, last_addition);
			main_package.setIntra(Double.valueOf(spinnerIntra.getValue()));
		});
		
		SpinnerValueFactory interFac = facGen(0.0, 10000.0, 0.1, 0.01);
		spinnerInter.setValueFactory(interFac);
		spinnerInter.setEditable(true);
		TextFormatter formatter3 = new TextFormatter(interFac.getConverter(), interFac.getValue());
		interFac.valueProperty().bindBidirectional(formatter3.valueProperty());
		spinnerInter.getEditor().setTextFormatter(formatter3);
		formatter3.valueProperty().addListener((s, ov, nv) -> {
			maximum_list.clear();
			minimum_list.clear();
			valid_maximum_list.clear();
			valid_minimum_list.clear();
			first_points.clear();
			fifth_points.clear();
			rewriteLinePlotPop();
			//redraw markers to avoid caos
			XYPlot fplot = (XYPlot) currentChart.getPlot();
//			intervalsList.clear();
			for (IntervalMarker az : intervalsList) {
				az.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
				az.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
				fplot.addDomainMarker(az,Layer.BACKGROUND);
			}
			writeFlowLinePlotZoom(last_min_zoom, last_max_zoom, last_addition);
			main_package.setInter(Double.valueOf(spinnerInter.getValue()));
		});
	}
	
	@FXML
	private void exportChartImage() throws IOException{
		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
    	
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
        	//chart width and height in the end
        	ChartUtils.saveChartAsPNG(file, currentChart, 800, 600);
        }
	}
	
	@FXML
	private void exportZoomChartImage() throws IOException{
		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
    	
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
        	//chart width and height in the end
        	ChartUtils.saveChartAsPNG(file, currentZoomChart, 800, 600);
        }
	}
	
	@FXML
	private void exportChartData() throws IOException{
		FileChooser fileChooser = new FileChooser();
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
    	
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        
        PrintWriter writer = new PrintWriter(file);
        XYDataset dataset = currentChart.getXYPlot().getDataset();
        
        for(int x = 0; x < dataset.getItemCount(0); x++){
        	double y = dataset.getYValue(0, x);
        	writer.print(x);
        	writer.print(",");
        	writer.println(y);
        }
        writer.close();
	}
	
	@FXML
	private void exportZoomChartData() throws IOException{
		FileChooser fileChooser = new FileChooser();
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
    	
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        
        PrintWriter writer = new PrintWriter(file);
        XYDataset dataset = currentZoomChart.getXYPlot().getDataset();
        
        for(int x = 0; x < dataset.getItemCount(0); x++){
        	double y = dataset.getYValue(0, x);
        	writer.print(x);
        	writer.print(",");
        	writer.println(y);
        }
        writer.close();
	}
	

	
	private XYDataset createDataset() {
		XYSeries series1 = new XYSeries("Coordinates");
        
		for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
			double average = currentGroup.getMagnitudeListValue(i);
//			series1.add(i / fps_val, (average * fps_val * pixel_val)  );
			series1.add(i / fps_val, Double.valueOf((average * fps_val * pixel_val) - (average_value))  );
		}
			
		//peak detection algorithm receives a group
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset_general = dataset;
        return dataset;

    }
	
	private LinkedHashMap<Integer, Double> maximum_hash = new LinkedHashMap<Integer, Double>();
	private LinkedHashMap<Integer, Double> minimum_hash = new LinkedHashMap<Integer, Double>();
	private List<Integer> maximum_list = new ArrayList<Integer>();
	private List<Integer> minimum_list = new ArrayList<Integer>();
	
	private List<Integer> valid_maximum_list = new ArrayList<Integer>();
	private List<Integer> valid_minimum_list = new ArrayList<Integer>();

    private List<Integer> first_points = new ArrayList<Integer>();		
    private List<Integer> fifth_points = new ArrayList<Integer>();
       
    private void newPeakDetectionAlgorithm2() {
    	double delta = spinnerDelta.getValue();
    	double intraPeaks = spinnerIntra.getValue();
    	

    	List<Integer> possible_max = new ArrayList<Integer>();
    	List<Integer> possible_f = new ArrayList<Integer>();
    	possible_f.add(0);
//    	possible_f.add(currentGroup.getMagnitudeSize()-1);
    	List<Integer> possible_l = new ArrayList<Integer>();
//    	possible_l.add(currentGroup.getMagnitudeSize()-1);
    	
    	double time_step = dataset_general.getYValue(0, 1);
    	if (intraPeaks < time_step) {
    		return;
    	}
    	    	
    	for (int point = 0; point < currentGroup.getMagnitudeSize(); point++) {
    		double speed = dataset_general.getYValue(0, point);
			boolean prev_tendency = true;
    		boolean prev_tendency2 = true;
			if (point > 0) {
	    		prev_tendency2 = false;
				double previous_speed = dataset_general.getYValue(0, point-1);
				double delta_back = speed - previous_speed;
				if (speed <= 0 && delta_back < 0) {
					prev_tendency2 = true;
				}
			}
			
    		boolean next_tendency = true;
    		if (point < currentGroup.getMagnitudeSize()-1) {
    			prev_tendency = false;
    			next_tendency = false;
    			//calculate next tendency
    			double next_speed = dataset_general.getYValue(0, point+1);
    			double delta_front = speed - next_speed;
				if (speed <= 0 && next_speed > 0) {
					prev_tendency = true;
				}
    			if (speed <= 0 && delta_front < 0) {
    				next_tendency = true;
    			}
    		}
    		if (speed > delta) {
    			possible_max.add(point);
    		} else if (speed <= 0) {
    			if (prev_tendency == true) {
    				if (possible_f.contains(point) ==  false) {
    					possible_f.add(point);
    				}
    			}
    			if (prev_tendency2 == true && next_tendency == true) {
    				if (possible_l.contains(point) ==  false) {
    					possible_l.add(point);
    				}
    			}
    		}
    	}

		if (possible_f.contains(currentGroup.getMagnitudeSize()-1) ==  false) {
			possible_f.add(currentGroup.getMagnitudeSize()-1);
		}
		if (possible_l.contains(currentGroup.getMagnitudeSize()-1) ==  false) {
			possible_l.add(currentGroup.getMagnitudeSize()-1);
		}
		
    	//validate all possible first, last pairs by maximum interpeak distance AND by a minimum of two points bigger than delta
    	List<Integer> first_possible_list = new ArrayList<Integer>();
    	List<Integer> last_possible_list = new ArrayList<Integer>();
    	LinkedHashMap<String, List<Integer>> first_to_max = new LinkedHashMap<String, List<Integer>>();
    	for (int s : possible_f) {
    		double time1 = dataset_general.getXValue(0, s);
    		for (int e : possible_l) {
    			if (e > s) {
	    			double time2 = dataset_general.getXValue(0, e);
	    			double time_delta = time2 - time1;
//	    			System.out.println(s + " " + e + ": " + time_delta + " " + intraPeaks);
	    			if (time_delta <= intraPeaks && e - s > 4) { //interval from start to end smaller than intraPeaks but bigger than 4
	    				IntStream a = IntStream.range(s, e); 
	    				List<Integer> between = a.boxed().collect(Collectors.toList()); //get all values between start and end
	    				List<Integer> duplicates = new ArrayList<Integer>(between); 
	    				duplicates.retainAll(possible_max); //get all above delta between start and end
	    				if (duplicates.size() >= 2) { //if two or more above delta, add first and last as possible points to explore
	    			    	//compare pairs and in case of same first or last remove ones with smaller index
	    					if (first_possible_list.contains(s)) {
	    						int s_index = first_possible_list.indexOf(s);
	    						int other_e = last_possible_list.get(s_index);
	    						if (e < other_e) {
	    							first_possible_list.add(s_index, s);
	    							first_possible_list.remove(s_index+1);
	    							last_possible_list.add(s_index, e);
	    							last_possible_list.remove(s_index+1);
	    							first_to_max.remove(String.valueOf(s) + "_" + String.valueOf(other_e));
			    					first_to_max.put(String.valueOf(s) + "_" + String.valueOf(e), duplicates);
	    						}
	    					} else if (last_possible_list.contains(e)) {
	    						int e_index = last_possible_list.indexOf(e);
	    						int other_s = first_possible_list.get(e_index);
	    						if (s > other_s) {
	    							first_possible_list.add(e_index, s);
	    							first_possible_list.remove(e_index+1);
	    							last_possible_list.add(e_index, e);
	    							last_possible_list.remove(e_index+1);
	    							first_to_max.remove(String.valueOf(other_s) + "_" + String.valueOf(e));
			    					first_to_max.put(String.valueOf(s) + "_" + String.valueOf(e), duplicates);
	    						}
	    					} else {
	    						first_possible_list.add(s);
	    						last_possible_list.add(e);
		    					first_to_max.put(String.valueOf(s) + "_" + String.valueOf(e), duplicates);
	    					}
	    				}
	    			}
    			}
    		}
		}
//    	System.out.println("Printing lists2:");
//    	System.out.println(first_possible_list.toString());
//    	System.out.println(last_possible_list.toString());
    	
    	for (int i = 0; i < first_possible_list.size(); i++) {
    		int first = first_possible_list.get(i);
    		int last = last_possible_list.get(i);
    		boolean jump = false;
    		for (int k = 0; k < first_points.size(); k++) {
    			int f_dot = first_points.get(k);
    			int l_dot = fifth_points.get(k);
				if (last < f_dot || last < l_dot) {
					jump = true;
					break;
				}
			}
    		if (jump == true) {
    			continue;
    		}
//        	System.out.println("Allowed!");
    		int min = -1;
    		double min_value = Double.MAX_VALUE;
    		int max1 = -1;
    		double max_value1 = Double.MIN_VALUE;
    		int max2 = -1;
    		double max_value2 = Double.MIN_VALUE;
    		List<Integer> current_maximums = first_to_max.get(String.valueOf(first) + "_" + String.valueOf(last));
//    		System.out.println("Maximums: " + current_maximums.toString());
			//get smallest minimum between first and last and two possible maximums
    		for (int first_plus = first+1; first_plus < last-1; first_plus++) {
    			double current_speed = dataset_general.getYValue(0, first_plus);
				double prev_speed = dataset_general.getYValue(0, first_plus-1);
				double delta_prev = current_speed - prev_speed;
				double next_speed = dataset_general.getYValue(0, first_plus+1);
				double delta_next = current_speed - next_speed;
				if (delta_prev < 0 && delta_next < 0) { //check if is a well
//	    			System.out.println("Found well! well: " + first_plus);
					boolean smaller_find = false;
					boolean bigger_find = false;
					for (int a : current_maximums) { //check if well is between any two maximums
						if (a < first_plus) {
//							System.out.println("Found max smaller than! well: " + first_plus);
							smaller_find = true;
						} else if (a > first_plus) {
//							System.out.println("Found max bigger than! well: " + first_plus);
							bigger_find = true;
						} 
					}
					if (smaller_find == true && bigger_find == true && current_speed < min_value) { //get smallest well
//						System.out.println("Found small well! well: " + first_plus);
						min_value = current_speed;
						min = first_plus;
					}
				}
			}
    		if (min > 0) {
//    			System.out.println("Found Min! Min: " + min);
    			//from smallest well to first get biggest point
    			for (int j = first; j < min; j++) {
    				double speed_this = dataset_general.getYValue(0, j);
    				if (speed_this > max_value1) {
    					max_value1 = speed_this;
    					max1 = j;
    				}
    			}
    			//from smallest well to last get biggest point
    			for (int j = min; j < last; j++) {
    				double speed_this = dataset_general.getYValue(0, j);
    				if (speed_this > max_value2) {
    					max_value2 = speed_this;
    					max2 = j;
    				}
    			}
    			if (max1 > -1 && max2 > -1) {
//    				System.out.println("Found Max 1 and 2! Max 1: " + max1 + " Max2: " + max2);
        			first_points.add(first);
            		valid_maximum_list.add(max1);
            		valid_maximum_list.add(max2);
            		valid_minimum_list.add(min);
        			fifth_points.add(last);
    			}
    		}	
		}
    }
    
    
	private void peakDetectionAlgorithm(double delta) {
		//estimacao de janela http://paulbourke.net/fractals/fracdim/		
		boolean lookformax = true;
		double maximum = Double.MIN_VALUE;
		double minimum = Double.MAX_VALUE;
		Long maximum_pos = null;
		Long minimum_pos = null;

		for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
			double average = currentGroup.getMagnitudeListValue(i);
			if (average > maximum) {
	            maximum = average;
	            maximum_pos = (long) i;
			}
	        if (average < minimum && average>0) {
	            minimum = average;
	            minimum_pos = (long) i;
	        }
	        if (lookformax == true) {
	        	if (average < maximum-delta) {
	        		maximum_hash.put((int)(long)maximum_pos, maximum);
	        		maximum_list.add((int)(long)maximum_pos);
	                minimum = average;
	                minimum_pos = (long) i;
	                lookformax = false;
	        	}
	        } else {
	        	if (average > minimum+delta) {
	        		minimum_hash.put((int)(long)minimum_pos, minimum);
	        		minimum_list.add((int)(long)minimum_pos);
	        		maximum = average;
	        		maximum_pos = (long) i;
	        		lookformax = true;
	        	}
	        }
		}
		//search criteria: look for maximum and minimum points that obey the minimum distance rules
		//first rule: defined by spinner minimum contraction/relaxation period (intrapeak period)
        double minIntra = spinnerIntra.getValue();
        System.out.println("minIntra");
        System.out.println(minIntra);
		//second rule: defined by spinner minimum cycle period (interpeaks period)
        double minInter = spinnerInter.getValue();
//        boolean minFlag = true;
//        boolean maxFlag = false;
        boolean max_cycle = false;
        double last_peak_dist = -1.0;
        List<Integer> pre_valid_list = new ArrayList<Integer>();
        for (int z = 0; z < maximum_list.size() - 1; z++) {
        	//algoritmo: loop de dois em dois, so validos os maximos em distancia minima de X pontos, so contados uma vez a partir do primeiro maximo encontrado
        	int first_maximum_index = maximum_list.get(z);
        	int next_maximum_index = maximum_list.get(z+1);
        	double first_value = dataset_general.getXValue(0, first_maximum_index);
        	double next_value = dataset_general.getXValue(0, next_maximum_index);
        	double intra_distance = next_value - first_value;
        	if (intra_distance <= minIntra && max_cycle == false) {
        		pre_valid_list.add(first_maximum_index);
        		pre_valid_list.add(next_maximum_index);
        		max_cycle = true;
        		last_peak_dist = next_value;
        		z++; //skip added points
        	}
        	if (max_cycle == true  && intra_distance <= minIntra) {
        		double inter_distance = first_value - last_peak_dist;
        		if (inter_distance >= minInter) {
            		pre_valid_list.add(first_maximum_index);
            		pre_valid_list.add(next_maximum_index);
            		last_peak_dist = next_value;
        		}
        		z++; //two points skip
        	} //one point skip
        }
        
        
        LinkedHashMap<Integer, Integer> first_max_to_minimum_hash = new LinkedHashMap<Integer, Integer>();
        for (int z = 0; z < minimum_list.size(); z++) {
        	int minimum_index = minimum_list.get(z);
        	double minimum_value = dataset_general.getXValue(0, minimum_index);
        	double minimum_flow = dataset_general.getYValue(0, minimum_index);
        	for (int v = 0; v < pre_valid_list.size() - 1; v=v+2) {
            	//algoritmo: loop de dois em dois, so validos os minimos entre cada um dos maximos definidos como validos
            	int first_maximum_index = pre_valid_list.get(v);
            	int next_maximum_index = pre_valid_list.get(v+1);
            	double first_value = dataset_general.getXValue(0, first_maximum_index);
            	double next_value = dataset_general.getXValue(0, next_maximum_index);
            	if (minimum_value > first_value && minimum_value < next_value && minimum_flow > 0) {
            		valid_maximum_list.add(first_maximum_index);
            		valid_maximum_list.add(next_maximum_index);
            		valid_minimum_list.add(minimum_index);
            		first_max_to_minimum_hash.put(first_maximum_index, minimum_index);
            		break;
            	}
        	}
        }
        
        
        double average_corrected = average_value;
        System.out.println("average_corrected");
        System.out.println(average_corrected);
        for (int v = 0; v < valid_maximum_list.size() - 1; v=v+2) {
        	int first_maximum_index = valid_maximum_list.get(v);            
        	int min_index = first_max_to_minimum_hash.get(first_maximum_index);
        	double minimum_flow = dataset_general.getYValue(0, min_index);
    		//search criteria look for first and fifth points based on simple set of rules:
    		//first point - movable window for first point below or equal zero whose following neighbour is positive
        	boolean first_found = false;
        	for (int b = first_maximum_index-1; b >= 1; b--) {
        		double next_value = dataset_general.getYValue(0, b-1);
        		double previous_value = dataset_general.getYValue(0, b+1);
        		double query_value = dataset_general.getYValue(0, b);
        		double delta_front = query_value - next_value;
        		double delta_back = query_value - previous_value;
        		if (query_value <= 0 && previous_value > 0) {
//        		if (delta_front < 0 && delta_back < 0 && query_value <= 0){
        			//first point found, break
        			first_points.add(b);
        			first_found = true;
        			break;
        		}
        	}
        	if (first_found == false) {
    			first_points.add(0);
        	}
    		//fifth point - movable window for first point below or equal zero whose following neighbour has positive derivative
        	boolean fifth_found = false; 
        	int next_maximum_index = valid_maximum_list.get(v+1);
        	for (int b = next_maximum_index-1; b < currentGroup.getMagnitudeSize() - 2; b++) {
//        		double next_next_value = dataset_general.getYValue(0, b+2);
        		double next_value = dataset_general.getYValue(0, b+1);
        		double query_value = dataset_general.getYValue(0, b);
        		double previous_value = dataset_general.getYValue(0, b-1);
//        		double next_delta = next_next_value - next_value;
//        		if (query_value <= 0 && next_delta > 0) {
        		double delta_front = query_value - next_value;
        		double delta_back = query_value - previous_value;
        		if (delta_back < 0 && delta_front < 0 && query_value <= 0){
        			//fifth point found, break
        			fifth_points.add(b);
        			fifth_found = true;
        			break;
        		}
        	}
        	if (fifth_found == false) {
    			fifth_points.add(currentGroup.getMagnitudeSize()-1);
        	}
        }
	}

	
	private double lowerBoundDomain;
    private double upperBoundDomain;
    
	private JFreeChart createChart(XYDataset dataset) {	
		// TODO - Configure Dot generation to match what was done by Sergio
		System.out.println("Creating new chart!");
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
        //plot.getRangeAxis().setUpperBound(plot.getRangeAxis().getUpperBound() * 1.3);
        lowerBoundDomain = chart.getXYPlot().getDomainAxis().getLowerBound();
        upperBoundDomain = chart.getXYPlot().getDomainAxis().getUpperBound();

        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        //plot.setSeriesPaint(0, new Color(0x00, 0x00, 0xFF));
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
        //https://stackoverflow.com/questions/11350380/place-a-circle-on-top-of-an-xylinechart-in-jfreechart
//        double delta = spinnerDelta.getValue() / fps_val / pixel_val;
		
		//maximum and minimum detection
//		peakDetectionAlgorithm(delta);
		newPeakDetectionAlgorithm2();
		if (valid_maximum_list.size() + valid_minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (valid_maximum_list.contains(x1)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (valid_minimum_list.contains(x1)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1) && !valid_minimum_list.contains(x1)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1) && !valid_minimum_list.contains(x1)  && !first_points.contains(x1)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
        plot.addChangeListener(new PlotChangeListener(){
        	@Override
        	public void plotChanged(PlotChangeEvent event) {
//        		System.out.println("I am called after a zoom event (and some other events too).");
        		if ( (plot.getDomainAxis().getLowerBound() != lowerBoundDomain || plot.getDomainAxis().getUpperBound() != upperBoundDomain) && main_package.getPlot_preferences().isDrawAnnotations() == true) {
        			lowerBoundDomain = plot.getDomainAxis().getLowerBound();
        			upperBoundDomain = plot.getDomainAxis().getUpperBound();
    				plot.clearAnnotations();
//        			if (upperBoundDomain - lowerBoundDomain <= 200) {
        			if (valid_maximum_list.size() + valid_minimum_list.size() < 1500) {
        		        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
        		        	double x = dataset.getXValue(0, x1);
        		        	double y = dataset.getYValue(0, x1);
        		        	if (valid_maximum_list.contains(x1)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
        		        	}
        		        	if (valid_minimum_list.contains(x1)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
        		        	}
        		        	if (first_points.contains(x1) && !valid_minimum_list.contains(x1)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
        		        	}
        		        	if (fifth_points.contains(x1) && !valid_minimum_list.contains(x1)  && !first_points.contains(x1)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
        		        	}
        		        }
        			} else {
        				plot.clearAnnotations();
        			}
//            		System.out.println("Zoomed domain!");
        		}
        }});
        currentChart = chart;
        
        return chart;
    }
	
	
	private void rewriteLinePlotPop() {
		ChartPanel linepanel2 = (ChartPanel) swgChart.getContent();
		JFreeChart new_chart = createChart(createDataset());
//		linepanel2.setChart(null);
//		linepanel2.remove(0);
		linepanel2.setChart(new_chart);
		
		/*
		JCheckBoxMenuItem gridLinesmenuItem2 = new JCheckBoxMenuItem();
		gridLinesmenuItem2.setSelected(true);
		gridLinesmenuItem2.setText("Gridlines on/off");
		GridLinesSwitch gridLinesZoomAction2 = new GridLinesSwitch(linepanel2); 
		gridLinesmenuItem2.addActionListener(gridLinesZoomAction2);
		linepanel2.getPopupMenu().add(gridLinesmenuItem2);		
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		showSpline.setText("Render Splines on/off");
		SplineShow splineRendering = new SplineShow(linepanel2);
		showSpline.addActionListener(splineRendering);
		linepanel2.getPopupMenu().add(showSpline);
		*/
		
		linepanel2.setRangeZoomable(false);
		linepanel2.setDomainZoomable(false);
		this_mouse_marker.setNewStuff(linepanel2);
		linepanel2.addMouseListener(this_mouse_marker);
		linepanel2.addMouseMotionListener(this_mouse_marker);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        linepanel2.setBorder(border);
        linepanel2.setMouseWheelEnabled(true);
	}
	
	
	private MouseMarker this_mouse_marker;
	private void writeLinePlotPop() {
		ChartPanel linepanel2 = new ChartPanel(createChart(createDataset()));
		JCheckBoxMenuItem gridLinesmenuItem2 = new JCheckBoxMenuItem();
		gridLinesmenuItem2.setSelected(true);
		gridLinesmenuItem2.setText("Gridlines on/off");
		GridLinesSwitch gridLinesZoomAction2 = new GridLinesSwitch(linepanel2); 
		gridLinesmenuItem2.addActionListener(gridLinesZoomAction2);
		linepanel2.getPopupMenu().add(gridLinesmenuItem2);		
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		showSpline.setText("Render Splines on/off");
		SplineShow splineRendering = new SplineShow(linepanel2);
		showSpline.addActionListener(splineRendering);
		linepanel2.getPopupMenu().add(showSpline);

		linepanel2.setRangeZoomable(false);
		linepanel2.setDomainZoomable(false);
//		MouseMarker this_mouse_marker = new MouseMarker(linepanel2,intervalsList);
		this_mouse_marker = new MouseMarker(linepanel2,intervalsList);
		linepanel2.addMouseListener(this_mouse_marker);
		linepanel2.addMouseMotionListener(this_mouse_marker);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        linepanel2.setBorder(border);
        linepanel2.setMouseWheelEnabled(true);
        swgChart.setContent(null);
        swgChart.setContent(
				//new ChartPanel(createChart(createDataset()))
				linepanel2
				);
	}
	
	private double last_min_zoom;
	private double last_max_zoom;
	private int last_addition;
	
	private final class MouseMarker extends MouseAdapter{
	    private List<IntervalMarker> markers;
	    private Double markerStart = Double.NaN;
	    private Double markerEnd = Double.NaN;
	    private XYPlot plot;
	    private JFreeChart chart;
	    private ChartPanel panel;
	    private Double x_start = Double.NaN;
	    private Double x_end = Double.NaN;
	    private Double y_start = Double.NaN;
	    private Double y_end = Double.NaN;
	    private XYShapeAnnotation shapeAnnotation;
	    private int currentMarker = -1;
	    public MouseMarker(ChartPanel panel, List<IntervalMarker> intervalList) {
	        this.panel = panel;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	        this.markers = new ArrayList<IntervalMarker>();
	    }
	    
	    public void setNewStuff(ChartPanel panel) {
	        this.panel = panel;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	    }

	    private void updateMarker(){
	        if (markerStart.equals(markerEnd)) {
	    	    markerStart = Double.NaN;
	    	    markerEnd = Double.NaN;
	        }
	        if (!markerStart.isNaN()) {
	        	if(!markerEnd.isNaN()) {
	                double v1 = markerStart.doubleValue();
	                double v2 = markerEnd.doubleValue();
//	                System.out.print(v1);
//	                System.out.print(" - ");
//	                System.out.println(v2);
	                
		        	if ( v2 > v1 && v2-v1 > (plot.getDataset().getXValue(0, 1) - plot.getDataset().getXValue(0, 0))*5  ){
		                IntervalMarker marker = new IntervalMarker(markerStart, markerEnd);
		                if(!markers.contains(marker)) {

			                swgNodeBig.setContent(null);
			                int addition = 0;
			                for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
			                	double time = i / fps_val;
			                	if (time > v1) {
			                		addition = i;
			                		break;
			                	}
			                }
			                currentMarker += 1;
			                last_min_zoom = v1;
			                last_max_zoom = v2;
			                last_addition = addition;
			                writeFlowLinePlotZoom(v1, v2, addition);
			                //marker.setPaint(new java.awt.Color(0xDD, 0xFF, 0xDD, 0x80));
			                marker.setPaint(main_package.getPlot_preferences().getMarkerColorRGB());
			                marker.setAlpha(main_package.getPlot_preferences().getMarkerAlpha());
			                plot.addDomainMarker(marker,Layer.BACKGROUND);
			                markers.add(marker);
			                System.out.println("added new marker: " + markers.size());
		    				intervalsList = markers.stream().collect(Collectors.toList());
	//		                intervalsList = markers;
		    				
		                }
		            }
	        	}
	        }
	    }
	    private Double getPosition(MouseEvent e){
	        Point2D p = e.getPoint();
	        Rectangle2D plotArea = panel.getScreenDataArea();
	        XYPlot plot = (XYPlot) chart.getPlot();
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
//	        y_start = p.getY();
//	        y_start = plot.getRangeAxis().java2DToValue(p.getY(), dataArea, plot.getRangeAxisEdge());
	        y_start = plot.getRangeAxis().java2DToValue(dataArea.getMinY(), dataArea, plot.getRangeAxisEdge());
	    }
	    
	    @Override
	    public void mouseDragged(MouseEvent e) {
	    	Point2D p = e.getPoint();
	        Rectangle2D plotArea = panel.getScreenDataArea();
	        Rectangle2D dataArea = panel.getChartRenderingInfo().getPlotInfo().getDataArea();
	        x_end = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
//	        y_end = p.getY();
//	        y_end = plot.getRangeAxis().java2DToValue(p.getY(), dataArea, plot.getRangeAxisEdge());
	        y_end = plot.getRangeAxis().java2DToValue(dataArea.getMaxY(), dataArea, plot.getRangeAxisEdge());
	        drawRect();
	    }
	    	    
	    public void drawRect() {
	    	//TODO Fix X and Y start and end values
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
	    	shapeAnnotation = new XYShapeAnnotation(rect);
	    	plot.addAnnotation(shapeAnnotation);
	    }
	    
	    //Double click to remove marker
	    @Override
	    public void mouseClicked(MouseEvent e){
	    	if (e.getClickCount() > 1) {
	    		double position = getPosition(e);
	    		for(int i = 0; i < markers.size(); i++){
	    			IntervalMarker im = markers.get(i);
	    			double start = im.getStartValue();
	    			double end = im.getEndValue();
	    			if(position >= start && position <= end){
	    				if (i == currentMarker) {
		    				if (i - 1 >= 0) {
				                double new_start =  markers.get(i-1).getStartValue();
				    			double new_end = markers.get(i-1).getEndValue();
				                int addition = 0;
				                for (int j = 0; j < currentGroup.getMagnitudeSize(); j++) {
				                	double time = j / fps_val;
				                	if (time > new_start) {
				                		addition = j;
				                		break;
				                	}
				                }
				                last_min_zoom = new_start;
				                last_max_zoom = new_end;
				                last_addition = addition;
				                writeFlowLinePlotZoom(new_start, new_end, addition);
				                currentMarker -= 1;
		    				} else  {
		    					if (i + 1 < markers.size()) {
					                double new_start =  markers.get(i+1).getStartValue();
					    			double new_end = markers.get(i+1).getEndValue();
					                int addition = 0;
					                for (int j = 0; j < currentGroup.getMagnitudeSize(); j++) {
					                	double time = j / fps_val;
					                	if (time > new_start) {
					                		addition = j;
					                		break;
					                	}
					                }
					                last_min_zoom = new_start;
					                last_max_zoom = new_end;
					                last_addition = addition;
					                writeFlowLinePlotZoom(new_start, new_end, addition);
					                currentMarker += 1;
		    					} else {
		    						last_min_zoom = 0;
		    						last_max_zoom = currentGroup.getMagnitudeSize();
		    						last_addition = 0;
		    						writeFlowLinePlotZoom(0, currentGroup.getMagnitudeSize(), 0);
		    						currentMarker = -1;
		    					}
		    				}
	    				} else if (i < currentMarker) {
	    					currentMarker -= 1;
	    				}
	    				markers.remove(i);
//	    				intervalsList = markers;
	    				intervalsList = markers.stream().collect(Collectors.toList());
	    				plot.removeDomainMarker(im,Layer.BACKGROUND);
	    				return;
	    			}
	    		}
	    	}
	    }
	}
	
    private static XYDataset createDatasetZoom(double minVal, double maxVal, double average_value) {

        XYSeries series1 = new XYSeries("Coordinates");
	    for (TimeSpeed a : timespeedlist) {
	    	double time = a.getTime();
	    	if (time >= minVal && time <= maxVal) {
	    		double speed= a.getSpeed();
	    		series1.add(time, speed- (average_value));
//	    		series1.add(time, speed);
	    	}
	    }
	    
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        return dataset;

    }
    
        
    private static double lowerBoundDomainZoom;
    private static double upperBoundDomainZoom;
        
    private static JFreeChart createChartZoom(XYDataset dataset, List<Integer> this_maximum_list, List<Integer> this_minimum_list, List<Integer> first_points, List<Integer> fifth_points, double minVal, int minIndVal) {
//    	System.out.println("minIndVal");
//    	System.out.println(minIndVal);
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
        
        lowerBoundDomainZoom = chart.getXYPlot().getDomainAxis().getLowerBound();
        upperBoundDomainZoom = chart.getXYPlot().getDomainAxis().getUpperBound();
        //domainAxis = plot.getDomainAxis();
        //domainAxis = plot.getRangeAxis();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        //plot.setSeriesPaint(0, new Color(0x00, 0x00, 0xFF));
        plot.setBackgroundPaint(main_package.getPlot_preferences().getBackgroundColorRGB());
        plot.setDomainGridlinePaint(main_package.getPlot_preferences().getDomainGridColorRGB());
        plot.setRangeGridlinePaint(main_package.getPlot_preferences().getRangeGridColorRGB());
        plot.setDomainGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
        plot.setRangeGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
//		System.out.println("this_maximum_list");
//		System.out.println(this_maximum_list);
//		System.out.println("this_minimum_list");
//		System.out.println(this_minimum_list);
//        if (upperBoundDomainZoom - lowerBoundDomainZoom <= 200) {
		if (this_maximum_list.size() + this_minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (this_maximum_list.contains(x1 + minIndVal)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (this_minimum_list.contains(x1 + minIndVal)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1 + minIndVal) && !this_minimum_list.contains(x1 + minIndVal)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1 + minIndVal) && !this_minimum_list.contains(x1 + minIndVal)  && !first_points.contains(x1 + minIndVal)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
	       plot.addChangeListener(new PlotChangeListener(){
	        	@Override
	        	public void plotChanged(PlotChangeEvent event) {
//	        		System.out.println("I am called after a zoom event (and some other events too).");
	        		if ( (plot.getDomainAxis().getLowerBound() != lowerBoundDomainZoom || plot.getDomainAxis().getUpperBound() != upperBoundDomainZoom) && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        			lowerBoundDomainZoom = plot.getDomainAxis().getLowerBound();
	        			upperBoundDomainZoom = plot.getDomainAxis().getUpperBound();
//	        			if (upperBoundDomainZoom - lowerBoundDomainZoom <= 200) {
        				plot.clearAnnotations();
	        			if (this_maximum_list.size() + this_minimum_list.size() < 1500) {
	        		        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        		        	double x = dataset.getXValue(0, x1);
	        		        	double y = dataset.getYValue(0, x1);
	        		        	if (this_maximum_list.contains(x1 + minIndVal)) {
	        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        		        	}
	        		        	if (this_minimum_list.contains(x1 + minIndVal)) {
	        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        		        	}
	        		        	if (first_points.contains(x1 + minIndVal) && !this_minimum_list.contains(x1 + minIndVal)) {
	        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        		        	}
	        		        	if (fifth_points.contains(x1 + minIndVal) && !this_minimum_list.contains(x1 + minIndVal)  && !first_points.contains(x1 + minIndVal)) {
	        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        		        	}
	        		        }
	        			} else {
	        				plot.clearAnnotations();
	        			}
//	            		System.out.println("Zoomed domain!");
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
	       currentZoomChart = chart;
	       return chart;
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
	
	private double zoomMinValue = 0.0;
	
	private void writeFlowLinePlotZoom(double minVal, double maxVal, int minIndVal) {
		zoomMinValue = minVal;
		ChartPanel linechartpanel = new ChartPanel(createChartZoom(createDatasetZoom(minVal, maxVal, average_value), valid_maximum_list, valid_minimum_list, first_points, fifth_points, minVal, minIndVal));

		JCheckBoxMenuItem gridLinesmenuItem = new JCheckBoxMenuItem();
		gridLinesmenuItem.setSelected(true);
		gridLinesmenuItem.setText("Gridlines on/off");
		GridLinesSwitch gridLinesZoomAction = new GridLinesSwitch(linechartpanel); 
		gridLinesmenuItem.addActionListener(gridLinesZoomAction);
		linechartpanel.getPopupMenu().add(gridLinesmenuItem);
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		showSpline.setText("Render Splines on/off");
		SplineShow splineRendering = new SplineShow(linechartpanel);
		showSpline.addActionListener(splineRendering);
		linechartpanel.getPopupMenu().add(showSpline);
		
		linechartpanel.setRangeZoomable(true);
		linechartpanel.setDomainZoomable(true);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        linechartpanel.setBorder(border);
        swgNodeBig.setContent(null);
		swgNodeBig.setContent(linechartpanel);
	}
	
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
//        	
////        	Node node = evt.getPickResult().getIntersectedNode();
////			if (node.getStyleClass().contains("increment-arrow-button") ||
////					node.getStyleClass().contains("decrement-arrow-button")) {
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