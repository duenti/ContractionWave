package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.PackageData;

public class FileReader {
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
    }
	
	private static void determineRootDirectory(File chosenDir) {    
        rootDir = (chosenDir != null) ?
                        chosenDir.toPath() : getInitialDirectory();
    }
	
	public static void chooseSourceDirectory(Stage primaryStage, URL url, PackageData main_package_init) throws IOException {    

    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a root path:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
        File chosenDir = chooser.showDialog(null);
        //primaryStage.setMaximized(true);
        determineRootDirectory(chosenDir);
        if(chosenDir == null){
        	return;
        }

		//URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		FXMLLoader fxmlloader = new FXMLLoader();
	    fxmlloader.setLocation(url);
	    fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
	    
        Parent root;
    	root = fxmlloader.load();
    	Scene oldScene = primaryStage.getScene();
    	Scene scene = new Scene(root , oldScene.getWidth(), oldScene.getHeight());

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	primaryStage.getScene().setCursor(Cursor.WAIT);
    	    }
    	});
		((Controller_2b_ImagesNew)fxmlloader.getController()).setContext(main_package_init, rootDir);
    	
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	primaryStage.getScene().setCursor(Cursor.DEFAULT);
    	    }
    	}); 	

		primaryStage.setTitle("ContractionWave - Select the images path:"); 	
//    	primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
    	primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
	
	public static void chooseVideoFiles(Stage primaryStage, URL url, PackageData main_package_init) throws IOException {

    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	
		FileChooser fc = new FileChooser();
    	fc.setTitle("Selecione the video files:");
        fc.setInitialDirectory(getInitialDirectory().toFile());
        fc.getExtensionFilters().addAll(
        		new FileChooser.ExtensionFilter("MPEG-4 Files", "*.mp4"),
        		new FileChooser.ExtensionFilter("AVI Files", "*.avi"),
        		new FileChooser.ExtensionFilter("Matroska Files", "*.mkv"),
        		new FileChooser.ExtensionFilter("Windows Media Files", "*.wmv"));
//    	List<File> selectedFiles = fc.showOpenMultipleDialog(primaryStage);
    	List<File> selectedFiles = fc.showOpenMultipleDialog(null);
//    	primaryStage.setMaximized(true);
    	if(selectedFiles == null) return;
    	
    	FXMLLoader fxmlloader = new FXMLLoader();
	    fxmlloader.setLocation(url);
	    fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
	    
        Parent root;
    	root = fxmlloader.load();
    	Scene oldScene = primaryStage.getScene();
    	Scene scene = new Scene(root , oldScene.getWidth(), oldScene.getHeight());

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	primaryStage.getScene().setCursor(Cursor.WAIT);
    	    }
    	});
    	
    	((Controller_2b_ImagesNew)fxmlloader.getController()).setContext(main_package_init, selectedFiles, rootDir);
    	
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	primaryStage.getScene().setCursor(Cursor.DEFAULT);
    	    }
    	});
		primaryStage.setTitle("ContractionWave - Select the images path:"); 	
//    	primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
    	primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
	}
}
