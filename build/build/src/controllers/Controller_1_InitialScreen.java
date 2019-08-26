package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.jfree.chart.plot.IntervalMarker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ContinueData;
import model.Group;
import model.PackageData;
import model.TimeSpeed;

public class Controller_1_InitialScreen implements Initializable {
//	private static Parent root;
	private static PackageData main_package;
	
    @FXML
    private Button cmdNewImageProject;

    @FXML
    private Button cmdNewVideoProject;

    @FXML
    private Button cmdCheckProgress;

    @FXML
    private Button cmdStartAnalysis;
    
    @FXML
    private Button cmdContinueAnalysis;
    
    @FXML
    private ImageView thisimgview;
    
    @FXML
    void handleMenuNewImage(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdNewImageProject.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		FileReader.chooseSourceDirectory(primaryStage, url, main_package);
    }
    
    @FXML
    void handleMenuNewVideo(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdNewImageProject.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
    	FileReader.chooseVideoFiles(primaryStage, url, main_package);
    }


    @FXML
    void handleClose(ActionEvent event){
    	Stage primaryStage = (Stage) cmdNewImageProject.getScene().getWindow();
    	primaryStage.close();
    }
    
    @FXML
    void handleReinitialize(ActionEvent event) throws IOException, ClassNotFoundException{
    	Stage primaryStage = (Stage) cmdCheckProgress.getScene().getWindow();
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
    void handleCheckProgress(ActionEvent event) throws IOException {
		Stage primaryStage = (Stage) cmdCheckProgress.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	
		URL url = getClass().getResource("FXML_2a_ProgressBar.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
		((Controller_2a_ProgressBar)fxmlloader.getController()).setContext(main_package);
		primaryStage.setTitle("ContractionWave - Processing Progress");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }

    @FXML
    void handleNewImageProject(ActionEvent event) throws IOException{
		Stage primaryStage = (Stage) cmdNewImageProject.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		FileReader.chooseSourceDirectory(primaryStage, url, main_package);
    }

    @FXML
    void handleNewVideoProject(ActionEvent event) throws IOException {//Video
		Stage primaryStage = (Stage) cmdNewImageProject.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
    	FileReader.chooseVideoFiles(primaryStage, url, main_package);
    }

    @FXML
    void handleStartAnalysis(ActionEvent event) throws IOException, ClassNotFoundException {
		Stage primaryStage = (Stage) cmdCheckProgress.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	
		URL url = getClass().getResource("FXML_3a_AnalysisSelection.fxml");
//		URL url = Main.class.getResource("FXML_3a_AnalysisSelection.fxml");
		
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
//    	Scene scene = new Scene(root);
//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	
		((Controller_3a_AnalysisSelection)fxmlloader.getController()).setContext(main_package);
		primaryStage.setTitle("ContractionWave - Select Group for Analysis");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
//		Main.getPrimaryStage().setTitle("Image Optical Flow - Select Group for Analysis");
//		Main.getPrimaryStage().setMaximized(true);
//		Main.getPrimaryStage().setScene(scene);
//		Main.getPrimaryStage().show();
    } 
        
    @SuppressWarnings("resource")
	@FXML
    void handleContinueAnalysis(ActionEvent event) throws IOException, ClassNotFoundException {
    	//validation with previous packaging
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Load Progress?");
		alert.setHeaderText("Do you want to load a Previously saved Progress File?");
		alert.setContentText("Choose your option:");

		ButtonType buttonTypeOne = new ButtonType("Load Progress");
		ButtonType buttonTypeCancel = new ButtonType("Cancel operation", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne){
	    	FileChooser fileChooser = new FileChooser();
	        Stage primaryStage;
	    	primaryStage = (Stage) cmdContinueAnalysis.getScene().getWindow();
	    	Scene oldScene = primaryStage.getScene();
	    	double prior_X = primaryStage.getX();
	    	double prior_Y = primaryStage.getY();
	    	
	        //Show save file dialog
	        File file = fileChooser.showOpenDialog(primaryStage);
	        FileInputStream fin = new FileInputStream(file);
			ObjectInputStream oin = new ObjectInputStream(fin);
			ContinueData readCase = (ContinueData) oin.readObject();
//	    	PackageData main_package_data = readCase.getMain_package_data();
//			main_package.setCores(readCase.getCores());
			main_package.setCurrent_groups(readCase.getGs());
	    	Group currentGroup = readCase.getCurrentGroup();
	    	double fps_val = readCase.getFps_val();
	    	double pixel_val = readCase.getPixel_val();
	    	double average_value = readCase.getAverage_value();
	    	double upper_limit = readCase.getUpper_limit();
	    	List<IntervalMarker> intervalsList = readCase.getIntervalsList();
	    	List<Integer> maximum_list = readCase.getMaximum_list();
	    	List<Integer> minimum_list = readCase.getMinimum_list();
	    	List<Integer> first_points = readCase.getFirst_points();
	    	List<Integer> fifth_points = readCase.getFifth_points();
	    	List<TimeSpeed> timespeedlist = readCase.getTimeSpeedList();
	    	main_package.setDelta(readCase.getDelta());
	    	main_package.setInter(readCase.getInter());
	    	main_package.setIntra(readCase.getIntra());
			URL url = getClass().getResource("FXML_3d2_PeakParametersPlot.fxml");
	    	FXMLLoader fxmlloader = new FXMLLoader();
	    	fxmlloader.setLocation(url);
	    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
	        Parent root;
	    	root = fxmlloader.load();
//	    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//	    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
	    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight()); 	
			((Controller_3d2_PeakParametersPlot)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, average_value, upper_limit, intervalsList, maximum_list, minimum_list, first_points, fifth_points, timespeedlist, true);
			primaryStage.setTitle("ContractionWave - Select Group for Analysis");
//			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setX(prior_X);
			primaryStage.setY(prior_Y);
		}    	
    }

    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//import plot preferences and set them in package data if possible
		Image imgFolder = new Image(getClass().getResourceAsStream("/folder-icon.png"));
		cmdNewImageProject.setGraphic(new ImageView(imgFolder));
		Tooltip tooltip = new Tooltip();
		tooltip.setText("New Project - Images");
		cmdNewImageProject.setTooltip(tooltip);
		
		Image vidFolder = new Image(getClass().getResourceAsStream("/folder-photo-icon.png"));
		cmdNewVideoProject.setGraphic(new ImageView(vidFolder));
		Tooltip tooltip2 = new Tooltip();
		tooltip2.setText("New Project - Videos");
		cmdNewVideoProject.setTooltip(tooltip2);
		
		Image alsFolder = new Image(getClass().getResourceAsStream("/folder-green-vbox-icon.png"));
		cmdStartAnalysis.setGraphic(new ImageView(alsFolder));
		Tooltip tooltip3 = new Tooltip();
		tooltip3.setText("Analyse Processed Groups");
		cmdStartAnalysis.setTooltip(tooltip3);
	
		Image progFolder = new Image(getClass().getResourceAsStream("/folder-blue-activities-icon.png"));
		cmdCheckProgress.setGraphic(new ImageView(progFolder));
		Tooltip tooltip4 = new Tooltip();
		tooltip4.setText("View Processing Progress");
		cmdCheckProgress.setTooltip(tooltip4);
		
		Image analysisFolder = new Image(getClass().getResourceAsStream("/folder-green-yandex-disk-icon.png"));
		cmdContinueAnalysis.setGraphic(new ImageView(analysisFolder));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Continue From Previously selected Peaks");
		cmdContinueAnalysis.setTooltip(tooltip5);
		
		Image newImg = new Image(getClass().getResourceAsStream("/LogoCW3.png"));
		thisimgview.setImage(newImg);
	}

	public void setContext(PackageData main_package_init) {
		main_package = main_package_init;
	}

}