package controllers;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
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
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jtransforms.fft.DoubleFFT_1D;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Group;
import model.Groups;
import model.ImageGroup;
import model.PackageData;
import model.TimeSpeed;
import model.VideoGroup;
import model.XYCircleAnnotation;

public class Controller_3b2_DisplayResults implements Initializable{
	private PackageData main_package;
	
	private static Group currentGroup;
	private JFreeChart currentChart;
	private double fps_value;
	private double pixel_value;
	
		
	private List<TimeSpeed> timespeedlist = new ArrayList<TimeSpeed>();
	
	@FXML
	Button cmdNext;
	
	@FXML
	Button cmdBack;
	
	@FXML
	private SwingNode swgChart;
	
	@FXML
	private TableView viewResultsTable;
	
	@FXML
	private TextField freqFourier;
		
	@FXML
	private TableColumn<TimeSpeed,Double> timeCol;
	@FXML
	private TableColumn<TimeSpeed,Double> speedCol;
	@FXML
	private TableColumn<TimeSpeed,Double> timeCol1;
	
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
//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
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
		ShowSavedDialog.showDialog();
		//JOptionPane.showMessageDialog(null, "File was saved successfully.");
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
		ShowSavedDialog.showDialog();
//        JOptionPane.showMessageDialog(null, "File was saved successfully.");
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
		ShowSavedDialog.showDialog();
//        JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
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
		ShowSavedDialog.showDialog();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportXLS(ActionEvent event) throws IOException{
    	Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("sample");
		Row row = spreadsheet.createRow(0);
		
		for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
			row.createCell(j).setCellValue(((TableColumn<TimeSpeed, Double>) viewResultsTable.getColumns().get(j)).getText());
		}
		for (int i = 0; i < viewResultsTable.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
				if(((TableColumnBase<TimeSpeed, Double>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
					row.createCell(j).setCellValue(((TableColumnBase<TimeSpeed, Double>) viewResultsTable.getColumns().get(j)).getCellData(i).toString()); 
				}
				else {
					row.createCell(j).setCellValue("");
				}   
			}
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
		ShowSavedDialog.showDialog();
//		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleColors(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	ShowColorThickDialog.showDialog(main_package, plot);
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
    	
//		URL url = getClass().getResource("FXML_3b_MicroscopeParametrization.fxml");
		URL url = getClass().getResource("FXML_3a_AnalysisSelection.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	commitColors();

		((Controller_3a_AnalysisSelection)fxmlloader.getController()).setContext(main_package);
		primaryStage.setTitle("ContractionWave - Select Group for Analysis");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
		
	}
	
	@FXML
	void nextPageNavigate(ActionEvent event) throws ClassNotFoundException, IOException {	
		Stage primaryStage = (Stage) cmdNext.getScene().getWindow();
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
  	
    	Group g1 = currentGroup;
    	commitColors();
		((Controller_3c_PeakDetectMean)fxmlloader.getController()).setContext(main_package, g1, fps_value, pixel_value, timespeedlist);	
		primaryStage.setTitle("ContractionWave - Select Gap Area between Peaks");
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
		tooltip5.setText("Select Gap area between Peaks");
		cmdNext.setTooltip(tooltip5);
		
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Microscope Parameters");
		cmdBack.setTooltip(tooltip6);
		
		//Fit columns
		viewResultsTable.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
		timeCol.setMaxWidth( 1f * Integer.MAX_VALUE * 33 ); // 33% width
		speedCol.setMaxWidth( 1f * Integer.MAX_VALUE * 34 ); // 33% width
		timeCol1.setMaxWidth(1f * Integer.MAX_VALUE * 33);
				
		timeCol.setCellValueFactory(new PropertyValueFactory<TimeSpeed,Double>("time"));
		timeCol1.setCellValueFactory(new PropertyValueFactory<TimeSpeed,Double>("time1"));
		speedCol.setCellValueFactory(new PropertyValueFactory<TimeSpeed,Double>("speed"));
		
		freqFourier.setEditable(true);
		
	}

	public void setContext(PackageData main_package2, String selecteditem, double number1, double number2) throws IOException, ClassNotFoundException {
		main_package = main_package2;
		fps_value = number1;
		pixel_value = number2;
		System.out.println("fps_value");
		System.out.println(fps_value);
		System.out.println("pixel_value");
		System.out.println(pixel_value);
		//group read
		System.out.println(selecteditem);
//		if (selecteditem.indexOf("_group.ser") > -1) {

		File tmpDir = new File(selecteditem + "_group.ser");
		if (tmpDir.exists() == true) {
			System.out.println("pull from file");
			FileInputStream file = new FileInputStream(selecteditem + "_group.ser"); 
			ObjectInputStream in = new ObjectInputStream(file); 
			// Method for reading serialized object
			currentGroup = (Group)in.readObject(); 
			in.close();
			file.close();
		} else {
			System.out.println("pull from mem");
			Groups g = main_package.getCurrent_groups();
			for (int i = 0; i < g.size(); i ++) {
				Group t = g.get(i);
				String name = t.getName();
				if (selecteditem.equals(name) == true) {
					currentGroup = t;
					break;
				}
			}
		}
		System.out.println("Group stats");
		System.out.println(currentGroup.getName());
		System.out.println(currentGroup.getMagnitudeSize());
		createPlot();
	}
	
	private boolean convStatus = false;
	
	@FXML
	public void switcherConv() {
		timespeedlist.clear();
		if (convStatus == false) {
			runConvolution();
		} else {
			stopConvolution();
		}
	}
	
	
	public void stopConvolution() {
		convStatus = false;
		currentGroup.restoreMagnitudeList();
		createPlot();
	}
	
	public void runConvolution() {
		convStatus = false;
		Button buttonTypeOk = new Button("Run");
		Button buttonTypeCancel = new Button("Cancel");
		
		Spinner<Integer> spinnerSize = new Spinner<Integer>();
		SpinnerValueFactory<Integer> intFac = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, currentGroup.getMagnitudeSize()-1, 5, 1);
		spinnerSize.setValueFactory(intFac);
		spinnerSize.setEditable(true);
		TextFormatter<Integer> formatter = new TextFormatter<Integer>(intFac.getConverter(), intFac.getValue());
		spinnerSize.getEditor().setTextFormatter(formatter);
		intFac.valueProperty().bindBidirectional(formatter.valueProperty());
		
		Spinner<Double> spinnerAvg = new Spinner<Double>();
		SpinnerValueFactory<Double> dobFac = facGen(0.00000001, 10000.0, 0.24390244, 0.001);
		spinnerAvg.setValueFactory(dobFac);
		spinnerAvg.setEditable(true);
		TextFormatter<Double> formatter2 = new TextFormatter<Double>(dobFac.getConverter(), dobFac.getValue());
		spinnerAvg.getEditor().setTextFormatter(formatter2);
		dobFac.valueProperty().bindBidirectional(formatter2.valueProperty());
		
		Stage dialogConvolution= new Stage();    		
    	dialogConvolution.initModality(Modality.APPLICATION_MODAL);
    	dialogConvolution.initOwner(null);
    	dialogConvolution.setResizable(true);
    	GridPane grid = new GridPane();

    	grid.setPrefWidth(300);
    	grid.setPrefHeight(120);
    	Label askQuestion = new Label("Data Smoothing:");
    	grid.add(askQuestion, 1, 0, 2, 1);
    	GridPane.setHalignment(askQuestion, HPos.CENTER);
    	GridPane.setHgrow(askQuestion, Priority.ALWAYS);
    	GridPane.setVgrow(askQuestion, Priority.ALWAYS);

//    	Label askVal = new Label("Value:");
//    	grid.add(askVal, 0, 1, 1, 1);
//    	GridPane.setHalignment(askVal, HPos.CENTER);
//    	GridPane.setHgrow(askVal, Priority.ALWAYS);
//    	GridPane.setVgrow(askVal, Priority.ALWAYS);
//    	grid.add(spinnerAvg, 1, 1, 1, 1);
//    	GridPane.setHalignment(spinnerAvg, HPos.CENTER);
//    	GridPane.setHgrow(spinnerAvg, Priority.ALWAYS);
//    	GridPane.setVgrow(spinnerAvg, Priority.ALWAYS);
    	
    	Label askSize = new Label("Size:");
//    	grid.add(askSize, 2, 1, 1, 1);
    	grid.add(askSize, 0, 1, 1, 1);
    	GridPane.setHalignment(askSize, HPos.CENTER);
    	GridPane.setHgrow(askSize, Priority.ALWAYS);
    	GridPane.setVgrow(askSize, Priority.ALWAYS);
//    	grid.add(spinnerSize, 3, 1, 1, 1);
    	grid.add(spinnerSize, 1, 1, 1, 1);
    	GridPane.setHalignment(spinnerSize, HPos.CENTER);
    	GridPane.setHgrow(spinnerSize, Priority.ALWAYS);
    	GridPane.setVgrow(spinnerSize, Priority.ALWAYS);
    	
    	grid.add(buttonTypeOk, 1, 2, 1, 1);
    	GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
    	grid.add(buttonTypeCancel, 2, 2, 1, 1);
    	GridPane.setHalignment(buttonTypeCancel, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeCancel, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeCancel, Priority.ALWAYS);
    	buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	int avgSize = spinnerSize.getValue();
            	double avgValue = spinnerAvg.getValue();
        		currentGroup.convoluteMagnitudeList(avgSize, 1.0);
        		createPlot();
                dialogConvolution.close();
                convStatus = true;
            }
        });
    	buttonTypeCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialogConvolution.close();
            }
        });
    	dialogConvolution.setScene(new Scene(grid));
    	dialogConvolution.show();
    	
	}
	
	
	public void writeTSV(File file) throws Exception {
	    Writer writer = null;
	    ObservableList selectedItems = viewResultsTable.getItems();
	    try {
	        writer = new BufferedWriter(new FileWriter(file));
	        String text2 = "Time(s)\tTime(ms)\tSpeed(\u00B5/s)\r\n";
	        writer.write(text2);
	        for (Object each : selectedItems) {
	        	TimeSpeed each_time_speed = (TimeSpeed) each;
	            String text = String.valueOf(each_time_speed.getTime()) + "\t" + String.valueOf(Math.round(each_time_speed.getTime1())) + "\t" + String.valueOf(each_time_speed.getSpeed()) + "\r\n";
	            writer.write(text);
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    finally {
	        writer.flush();
	        writer.close();
	    } 
	}	
	
	private void createPlot() {
		currentChart = createChart(createDataset());
		frequencyFind();
		ChartPanel linepanel2 = new ChartPanel(currentChart);
		//this crude chartpanel should be passed by setcontext
		JCheckBoxMenuItem gridLinesmenuItem2 = new JCheckBoxMenuItem();
		gridLinesmenuItem2.setSelected(true);
		JCheckBoxMenuItem showPolynomial = new JCheckBoxMenuItem();
		JCheckBoxMenuItem showFourierTransform = new JCheckBoxMenuItem();
		JCheckBoxMenuItem calcFourierSelection = new JCheckBoxMenuItem();
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		JMenuItem runConvolution = new JMenuItem();
		
		gridLinesmenuItem2.setText("Gridlines on/off");
		showPolynomial.setText("Filter Noise on/off");
		showFourierTransform.setText("Fourier Transform on/off");
		calcFourierSelection.setText("Fourier from Current View on/off");
		showSpline.setText("Render Splines on/off");
		runConvolution.setText("Noise Filtering on/off");
		
		GridLinesSwitch gridLinesZoomAction2 = new GridLinesSwitch(linepanel2); 
		gridLinesmenuItem2.addActionListener(gridLinesZoomAction2);
		
		PolynomialShow polynomialTransform = new PolynomialShow(linepanel2, fps_value, this); 
		showPolynomial.addActionListener(polynomialTransform);
		
		FourierTransformation fourierTransform = new FourierTransformation(linepanel2, fps_value);
		showFourierTransform.addActionListener(fourierTransform);
		
		CalcTransformation calcTransform = new CalcTransformation(linepanel2, fps_value);
		calcFourierSelection.addActionListener(calcTransform);
		
		SplineShow splineRendering = new SplineShow(linepanel2);
		showSpline.addActionListener(splineRendering);
		
		ConvolutionTransformation convTrans = new ConvolutionTransformation();
		runConvolution.addActionListener(convTrans);
		
		linepanel2.getPopupMenu().add(gridLinesmenuItem2);
//		linepanel2.getPopupMenu().add(showPolynomial);
		linepanel2.getPopupMenu().add(showSpline);
		linepanel2.getPopupMenu().add(showFourierTransform);
		linepanel2.getPopupMenu().add(calcFourierSelection);
//		linepanel2.getPopupMenu().add(runConvolution);
		
		linepanel2.setRangeZoomable(true);
		linepanel2.setDomainZoomable(true);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        linepanel2.setBorder(border);
        linepanel2.setMouseWheelEnabled(true);
        swgChart.setContent(linepanel2);
	}
	
	private XYDataset createDataset() {
		XYSeries series1 = new XYSeries("Optical Flow");
		for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
			double average = currentGroup.getMagnitudeListValue(i);
			TimeSpeed thisTime = new TimeSpeed(Double.valueOf(i), average, fps_value, pixel_value, i);
			timespeedlist.add(thisTime);
			viewResultsTable.getItems().add(thisTime);
			series1.add(i / fps_value, average * fps_value * pixel_value);
		}
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        return dataset;

    }
	
	private boolean fourier_change_on = false;
	private boolean fourier_plot = false;
	
	private JFreeChart createChart(XYDataset dataset) {
		original_dataset = dataset;
		fourier_dataset = dataset;
        JFreeChart chart = ChartFactory.createXYLineChart(
            currentGroup.getName(),
            "Time(s)",
            "Average Speed(\u00B5m/s)",
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
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.setBackgroundPaint(main_package.getPlot_preferences().getBackgroundColorRGB());
        plot.setDomainGridlinePaint(main_package.getPlot_preferences().getDomainGridColorRGB());
        plot.setRangeGridlinePaint(main_package.getPlot_preferences().getRangeGridColorRGB());
        plot.setDomainGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
        plot.setRangeGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
        plot.addChangeListener(new PlotChangeListener(){
        	@Override
        	public void plotChanged(PlotChangeEvent event) {
//        		System.out.println("I am called after a zoom event (and some other events too).");
        		if (fourier_change_on == true && fourier_plot == false) {
        			System.out.println("About to gen dataset fourier from plot");
    				XYSeriesCollection dataset_new = new XYSeriesCollection();		
    			    XYSeries series3 = new XYSeries("");        
    				for (int i = 0; i < dataset.getItemCount(0); i++) {
    					if (dataset.getXValue(0, i) >= plot.getDomainAxis().getLowerBound() && dataset.getXValue(0, i) <= plot.getDomainAxis().getUpperBound()) {
    				    	series3.add(dataset.getXValue(0, i), dataset.getYValue(0, i));
    					}
    				}
    				dataset_new.addSeries(series3);
    				fourier_dataset = (XYDataset) dataset_new;
    				frequencyFind();
        		}
        	}
        });
        
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
	
	private boolean is_polynomial_on = false;
	
	public void setPolynonState(boolean new_poly_state) {
		this.is_polynomial_on = new_poly_state;
	}
	
	public boolean getPolynonState() {
		return this.is_polynomial_on;
	}
	
	public class ConvolutionTransformation implements ActionListener {
		boolean is_convolution;
		
		public ConvolutionTransformation() {
			is_convolution = false;
		}
		
		public void setConvolution(boolean newstate) {
			this.is_convolution = newstate;
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			boolean state = this.is_convolution;
			this.is_convolution = !state;
			if (this.is_convolution == true) {
				runConvolution();
			} else {
				stopConvolution();
			}
		}
		
	}
	
	
	public double[] fftfreq(int n, double timestep) {
		double[] f = new double[n];
		int j = 0;
		//fix this function
		if (n % 2 == 0) {
			for (int i = 0; i <= ((n/2) - 1); i++) {
				f[j] = i / (n * timestep);
				j++;
			}
			for (int i = (-n/2); i < 0; i++) {
				f[j] = i / (n * timestep);
				j++;
			}
		} else {
			for (int i = 0; i <= ((n-1)/2); i++ ) {
				f[j] = i / (n * timestep);
				j++;
			}
			for (int i = (-(n-1)/2); i < 0; i ++) {
				f[j] = i / (n * timestep);
				j++;
			}
		}
		return f;
	}
	
	
	private double[] fourier_freqs; 
	private double[] fourier_magnitude; 
	private double fourier_delta = 1.0;
	private int fourier_index = 1;
	private List<Integer> maximum_list = new ArrayList<Integer>();
	private List<Integer> minimum_list = new ArrayList<Integer>();

	private XYDataset original_dataset;
	private XYDataset fourier_dataset;
    
	private void peakDetectionAlgorithm() {
		maximum_list.clear();
		minimum_list.clear();
		double delta = fourier_delta;
		boolean lookformax = true;
		double maximum = Double.MIN_VALUE;
		double minimum = Double.MAX_VALUE;
		Long maximum_pos = null;
		Long minimum_pos = null;
		for (int i = 0; i < fourier_magnitude.length; i++) {
			double average = fourier_magnitude[i];
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
	        		maximum_list.add((int)(long)maximum_pos);
	                minimum = average;
	                minimum_pos = (long) i;
	                lookformax = false;
	        	}
	        } else {
	        	if (average > minimum+delta) {
	        		minimum_list.add((int)(long)minimum_pos);
	        		maximum = average;
	        		maximum_pos = (long) i;
	        		lookformax = true;
	        	}
	        }
		}
	}
		
	private void frequencyFind() {
		XYDataset dataset = fourier_dataset;
		double [] input = new double[dataset.getItemCount(0)];
		for (int i = 0; i < dataset.getItemCount(0); i++) {
			input[i] = dataset.getYValue(0, i);
		}				
		DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
		double[] fft = new double[input.length* 2];
		System.arraycopy(input, 0, fft, 0, input.length);
		fftDo.realForwardFull(fft);
		double fps = 1.0/fps_value;
		double[] fft_frequency = fftfreq(input.length, fps);
//		double[] magnitude = new double[input.length];
		int j = 0;
		double found_freq = 0;
		double avg_magnitude = 0;
		List<Double> freqs = new ArrayList<Double>();
		List<Double> mags = new ArrayList<Double>();
		for (int i = 0; i < fft.length; i=i+2) {
	    	double re = (fft[i] * fft[i]) / Math.sqrt((double)fft.length);
	    	double im = (fft[i+1] * fft[i+1]) / Math.sqrt((double)fft.length);
	    	double value = Math.sqrt(Math.sqrt(re+im)) * 2.0;
	    	if (fft_frequency[j] >= 0) {
	    		freqs.add(fft_frequency[j]);
	    		mags.add(value);
				avg_magnitude += value;
	    	}
			j++;
		}
		avg_magnitude /= mags.size();
		
		double[] frequencies = new double[mags.size()];
		int v = 0;
		for (double d : freqs) {
			frequencies[v] = d;
			v++;
		}
		
		double[] magnitude = new double[mags.size()];
		v = 0;
		for (Double d1 : mags) {
			magnitude[v] = d1;
			v++;
		}
		
        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(2);
        
		freqFourier.setText(df.format(found_freq) + "");
//		fourier_freqs = fft_frequency;
		fourier_delta = avg_magnitude;
		fourier_freqs = frequencies;
		fourier_magnitude = magnitude;
		
		peakDetectionAlgorithm();
		if(fourier_index < maximum_list.size()){
			int index = maximum_list.get(fourier_index);
//			freqFourier.setText(df.format(fft_frequency[index]) + "");
			freqFourier.setText(df.format(fourier_freqs[index]) + "");
		}else{
			freqFourier.setText("None");
		}
	}
	
	private void runNewFourier() {
		peakDetectionAlgorithm();
		if(fourier_index < maximum_list.size()){
			int index = maximum_list.get(fourier_index );
	        DecimalFormat df = new DecimalFormat("#.#");
	        df.setMaximumFractionDigits(5);
			freqFourier.setText(df.format(fourier_freqs[index]) + "");
		}else{
			freqFourier.setText("None");
		}
	}

	@FXML
	void showAdvancedFourier(ActionEvent event) throws IOException {
		Dialog<Boolean> dialogFourier = new Dialog<>();
		dialogFourier.setHeaderText("Frequency Detection Options:");
		dialogFourier.setResizable(true);
		Label label1 = new Label("Delta Value: ");
		Label label2 = new Label("Peak Index (Positive): ");
		
		Spinner<Double> fourierDeltaSpin = new Spinner<Double>();
		Spinner<Integer> fourierIndexSpin= new Spinner<Integer>();
		
		SpinnerValueFactory<Double> dobFac2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, fourier_delta, 1.0);
		fourierDeltaSpin.setValueFactory(dobFac2);
		fourierDeltaSpin.setEditable(true);
		TextFormatter<Double> formatter3 = new TextFormatter<Double>(dobFac2.getConverter(), dobFac2.getValue());
		fourierDeltaSpin.getEditor().setTextFormatter(formatter3);
		dobFac2.valueProperty().bindBidirectional(formatter3.valueProperty());
		formatter3.valueProperty().addListener((s, ov, nv) -> {
			try {
				fourier_delta = nv;
				runNewFourier();
				if (fourier_plot == true) {
					XYPlot plote = (XYPlot) currentChart.getPlot();
					plote.clearAnnotations();
					int index = maximum_list.get(fourier_index);
					double x_do = fourier_freqs[index];
					double y_do = fourier_magnitude[index];
					plote.addAnnotation(new XYCircleAnnotation(x_do, y_do, 5.0, java.awt.Color.RED));
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});
		
		SpinnerValueFactory<Integer> intFac2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maximum_list.size(), fourier_index, 1);
		fourierIndexSpin.setValueFactory(intFac2);
		fourierIndexSpin.setEditable(true);
		TextFormatter<Integer> formatter4 = new TextFormatter<Integer>(intFac2.getConverter(), intFac2.getValue());
		fourierIndexSpin.getEditor().setTextFormatter(formatter4);
		intFac2.valueProperty().bindBidirectional(formatter4.valueProperty());
		formatter4.valueProperty().addListener((s, ov, nv) -> {
			try {
				fourier_index = nv;
				runNewFourier();
				if (fourier_plot == true) {
					XYPlot plote = (XYPlot) currentChart.getPlot();
					plote.clearAnnotations();
					int index = maximum_list.get(fourier_index);
					double x_do = fourier_freqs[index];
					double y_do = fourier_magnitude[index];
					plote.addAnnotation(new XYCircleAnnotation(x_do, y_do, 5.0, java.awt.Color.RED));
				}
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});
		
		GridPane grid = new GridPane();
    	grid.setPrefWidth(450);
    	grid.setPrefHeight(80);
		grid.add(label1, 1, 1);
		grid.add(fourierDeltaSpin, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(fourierIndexSpin, 2, 2);
		dialogFourier.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
		dialogFourier.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialogFourier.show();
	}
	
	public class CalcTransformation implements ActionListener  {
		private JFreeChart chart;
		private ChartPanel panel;
		private XYPlot plot;
		private XYDataset dataset;
		boolean is_calc_fourier_on;
		double fps_val;
		
		public CalcTransformation (ChartPanel panel1, double fps_value) {
	        this.panel = panel1;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	        this.is_calc_fourier_on = false;
	        this.fps_val = fps_value;
	        this.dataset = plot.getDataset();
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			this.is_calc_fourier_on = !this.is_calc_fourier_on;
			//GET DATASET FROM AREA
//			this.chart.getPlot().
			if (this.is_calc_fourier_on == true && fourier_plot == false) {
    			System.out.println("About to gen new dataset fourier");
				XYSeriesCollection dataset_new = new XYSeriesCollection();		
			    XYSeries series3 = new XYSeries("");        
				for (int i = 0; i < dataset.getItemCount(0); i++) {
					if (dataset.getXValue(0, i) >= plot.getDomainAxis().getLowerBound() && dataset.getXValue(0, i) <= plot.getDomainAxis().getUpperBound()) {
				    	series3.add(dataset.getXValue(0, i), dataset.getYValue(0, i));
					}
				}
				dataset_new.addSeries(series3);
				fourier_dataset = (XYDataset) dataset_new;
				fourier_change_on = true;
				frequencyFind();
			} else  {
    			System.out.println("About to reset dataset fourier");
				fourier_dataset = (XYDataset) original_dataset;
				fourier_change_on = false;
				frequencyFind();
			}
//	        plot.getRangeAxis().setAutoRange(true);
			System.out.println(plot.getDomainAxis().getLowerBound());
	        System.out.println(plot.getDomainAxis().getUpperBound());
			//SAVE ORIGINAL DATASET
			//CHANGE DATASET VARIABLE OF FOURIER TRANSFORMATION CALCULATION
		}
		
	}
	
	public class FourierTransformation implements ActionListener  {
		private JFreeChart chart;
		private ChartPanel panel;
		private XYPlot plot;
		private XYDataset dataset;
		boolean is_curves_on;
		double fps_val;
		
		public FourierTransformation (ChartPanel panel1, double fps_value) {
	        this.panel = panel1;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	        this.is_curves_on = false;
	        this.fps_val = fps_value;
	        this.dataset = plot.getDataset();
		}
		
		public double[] fftfreq(int n, double timestep) {
			double[] f = new double[n];
			int j = 0;
			//fix this function
			if (n % 2 == 0) {
				for (int i = 0; i <= ((n/2) - 1); i++) {
					f[j] = i / (n * timestep);
					j++;
				}
				for (int i = (-n/2); i < 0; i++) {
					f[j] = i / (n * timestep);
					j++;
				}
			} else {
				for (int i = 0; i <= ((n-1)/2); i++ ) {
					f[j] = i / (n * timestep);
					j++;
				}
				for (int i = (-(n-1)/2); i < 0; i ++) {
					f[j] = i / (n * timestep);
					j++;
				}
			}
			return f;
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			boolean state = this.is_curves_on;
			this.is_curves_on = !state;
			plot.clearAnnotations();
			if (this.is_curves_on) {
		        fourier_plot = true;
				double[] input = new double[fourier_dataset.getItemCount(0)];
				for (int i = 0; i < fourier_dataset.getItemCount(0); i++) {
					input[i] = fourier_dataset.getYValue(0, i);
				}			
			    DoubleFFT_1D fftDo = new DoubleFFT_1D(input.length);
			    double[] fft = new double[input.length* 2];
			    System.arraycopy(input, 0, fft, 0, input.length);
			    fftDo.realForwardFull(fft);
			    XYSeries series3 = new XYSeries("FFTfreq");
			    double fps = 1.0/fps_value;
			    double[] fft_frequency = fftfreq(input.length, fps);
			    double[] magnitude = new double[input.length];
			    int j = 0;
		        XYSeriesCollection dataset_new = new XYSeriesCollection();
			    for (int i = 0; i < fft.length; i=i+2) {
			    	double re = (fft[i] * fft[i]) / Math.sqrt((double)fft.length);
			    	double im = (fft[i+1] * fft[i+1]) / Math.sqrt((double)fft.length);
			    	magnitude[j] = Math.sqrt(Math.sqrt(re+im)) * 2.0;
			    	if (fft_frequency[j] >= 0) {
			    		series3.add(fft_frequency[j], magnitude[j]);
			    	}
			    	j++;
			    }
		        dataset_new.addSeries(series3);
		        plot.setDataset(dataset_new);
		        plot.getDomainAxis().setLabel("Frequency (Hz)");
		        plot.getRangeAxis().setLabel("Amplitude Density");	   
		        plot.getRangeAxis().setAutoRange(true);
		        plot.getDomainAxis().setAutoRange(true);
		        try {
		        	int index = maximum_list.get(fourier_index);
		        	double x_do = fourier_freqs[index];
		        	double y_do = fourier_magnitude[index];
		        	plot.addAnnotation(new XYCircleAnnotation(x_do, y_do, 5.0, java.awt.Color.RED));
		        } catch (java.lang.IndexOutOfBoundsException ev) {
		        	ev.printStackTrace();
		        }
			} else {
		        fourier_plot = false;
		        plot.setDataset(this.dataset);
		        plot.getDomainAxis().setLabel("Time(s)");
		        plot.getRangeAxis().setLabel("Average Speed(µm/s)");
		        plot.getRangeAxis().setAutoRange(true);
		        plot.getDomainAxis().setAutoRange(true);
			}
		}

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
	
	public class PolynomialShow implements ActionListener {
	    private JFreeChart chart;
	    private ChartPanel panel;
	    private XYPlot plot;
	    private XYDataset dataset;
	    private double fps_val;
	    private boolean is_polynomial_on; 

		public PolynomialShow(ChartPanel panel1, double fps_value, Controller_3b2_DisplayResults parent_controller) {
	        this.panel = panel1;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	        this.dataset = plot.getDataset();
	        this.fps_val = fps_value;
	        this.is_polynomial_on = parent_controller.getPolynonState();
			parent_controller.setPolynonState(!this.is_polynomial_on);
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			// TODO Auto-generated method stub
//			org.jfree.data.statistics.Regression.
			//https://stackoverflow.com/questions/878200/java-curve-fitting-library
			//https://stackoverflow.com/questions/49238446/polynomial-regression-with-apache-maths-3-6-1
		    double[] preg = org.jfree.data.statistics.Regression.getPolynomialRegression(this.dataset, 0, this.dataset.getItemCount(0)-2);
	        XYSeriesCollection dataset_new = new XYSeriesCollection();
		    XYSeries series1 = new XYSeries("Coordinates");
		    XYSeries series2 = new XYSeries("Polynomial");
		    for (int i = 0; i < preg.length; i++) {
//		    	dataset_new.add
		    	series1.add(i / this.fps_val, this.dataset.getYValue(0, i));
		    	series2.add(i / this.fps_val, preg[i]);
		    }
	        dataset_new.addSeries(series1);
	        dataset_new.addSeries(series2);
	        plot.setDataset(dataset_new);
	        
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

}
