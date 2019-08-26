package controllers;
	
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.PackageData;


public class Main extends Application {
	
	private PackageData main_package = null;
	
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
	}
	
//	private static Parent root;
	
	private static Stage primaryStage;
	
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setPrimaryStage(Stage pStage) {
        Main.primaryStage = pStage;
    }
	
	@Override
	public void start(Stage primaryStage1) throws Exception {
		Locale.setDefault(new Locale("en", "US")); //forces use of dots instead of commas
		setPrimaryStage(primaryStage1);
		primaryStage = primaryStage1;
		primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent event) {
				System.out.println("Closing all windows");
				Platform.exit();
			}
		}));
		System.out.println("test");
		File yourFile = new File("done.txt");
		yourFile.createNewFile(); // if file already exists will do nothing
		
		File tmpDir = new File(getInitialDirectory().toFile().getAbsolutePath() + "/preferences.pref");
		boolean exists = tmpDir.exists();
		
		if (exists == true) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Plot Configurations File Detected");
			alert.setHeaderText("Load Plot Configurations File?");
			alert.setContentText("Choose your option:");
			ButtonType buttonTypeOne = new ButtonType("Load & Start");
			ButtonType buttonTypeTwo = new ButtonType("Do not Load & Start");
			ButtonType buttonTypeThree= new ButtonType("Delete Config & Start");
			ButtonType buttonTypeFour= new ButtonType("Quit");
			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeFour);
			alert.getDialogPane().setPrefWidth(700);
//			alert.getDialogPane().setPrefWidth(value);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne){
				main_package = new PackageData(true);
				URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
		    	FXMLLoader fxmlloader = new FXMLLoader();
		    	fxmlloader.setLocation(url);
		    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
		        Parent root;
		    	root = fxmlloader.load();
		    	Scene scene = new Scene(root,920,690);
				((Controller_1_InitialScreen)fxmlloader.getController()).setContext(main_package);
				primaryStage.setTitle("ContractionWave - Welcome");
				//primaryStage.setMaximized(true);
				primaryStage.setScene(scene);
				primaryStage.show();	
			} else if (result.get() == buttonTypeTwo) {
				main_package = new PackageData(false);
				URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
		    	FXMLLoader fxmlloader = new FXMLLoader();
		    	fxmlloader.setLocation(url);
		    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
		        Parent root;
		    	root = fxmlloader.load();
		    	Scene scene = new Scene(root,920,690);
				((Controller_1_InitialScreen)fxmlloader.getController()).setContext(main_package);
				primaryStage.setTitle("ContractionWave - Welcome");
				//primaryStage.setMaximized(true);
				primaryStage.setScene(scene);
				primaryStage.show();	
			} else if (result.get() == buttonTypeThree) {
				tmpDir.delete();
				main_package = new PackageData(false);
				URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
		    	FXMLLoader fxmlloader = new FXMLLoader();
		    	fxmlloader.setLocation(url);
		    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
		        Parent root;
		    	root = fxmlloader.load();
		    	Scene scene = new Scene(root,920,690);
				((Controller_1_InitialScreen)fxmlloader.getController()).setContext(main_package);
				primaryStage.setTitle("ContractionWave - Welcome");
				//primaryStage.setMaximized(true);
				primaryStage.setScene(scene);
				primaryStage.show();	
			} else {
			    super.stop(); //To change body of generated methods, choose Tools | Templates.
			    if (main_package != null) {
			    	main_package.getExec().shutdown();
			    	main_package.getExec().shutdownNow();
			    }
			    System.exit(0);
			}
		} else {
			main_package = new PackageData(false);
			URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
	    	FXMLLoader fxmlloader = new FXMLLoader();
	    	fxmlloader.setLocation(url);
	    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
	        Parent root;
	    	root = fxmlloader.load();
	    	Scene scene = new Scene(root,920,690);
			((Controller_1_InitialScreen)fxmlloader.getController()).setContext(main_package);
			primaryStage.setTitle("ContractionWave - Welcome");
			//primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();	
		}
	}
	
	@Override
	public void stop() throws Exception {
		//ask to save last plot style changes in plot preferences file, if yes, save plot preferences
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Save Plot changes");
		alert.setHeaderText("Save Plot changes?");
		alert.setContentText("Choose your option:");

		ButtonType buttonTypeOne = new ButtonType("Save");
		ButtonType buttonTypeTwo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne){
			File tmpDir = new File(getInitialDirectory().toFile().getAbsolutePath() + "/preferences.pref");
			FileOutputStream fout = new FileOutputStream(tmpDir);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(main_package.getPlot_preferences());
			oos.close();
			fout.close();
		    super.stop(); //To change body of generated methods, choose Tools | Templates.
		    main_package.getExec().shutdown();
		    main_package.getExec().shutdownNow();
		    System.exit(0);
		} else {
		    super.stop(); //To change body of generated methods, choose Tools | Templates.
		    main_package.getExec().shutdown();
		    main_package.getExec().shutdownNow();
		    System.exit(0);
		}

	}
	
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
