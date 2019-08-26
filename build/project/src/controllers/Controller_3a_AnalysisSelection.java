package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

//import controllers.Controller_3e_ViewJetQuiverMergeSingle.IncrementHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Group;
import model.Groups;
import model.MicroscopePreferences;
import model.PackageData;

public class Controller_3a_AnalysisSelection implements Initializable {
//	private static Parent root;
	private PackageData main_package;
    private ObservableList<String> doneFiles = FXCollections.observableArrayList();
	
    @FXML
    private ListView<String> groupsListView;

    @FXML
    private Button cmdNext;

    @FXML
    private Button cmdBack;

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
    void back(ActionEvent event) throws IOException {
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
		primaryStage.setTitle("ContractionWave - Welcome");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }

    @FXML
    void navigateNextPage(ActionEvent event) throws IOException, ClassNotFoundException {
    	validateSelection();
    }

	private double fps_value;
	private double pixel_value;
    
	
	@FXML
	void handleDialogMicroscope(ActionEvent event) throws ClassNotFoundException, IOException {
		showDialogMicroscope(false);
	}
	
	public void showDialogMicroscope(boolean next) throws IOException, ClassNotFoundException {
		Spinner<Double> txtFPS = new Spinner<Double>();    	
		Spinner<Double> txtPixels = new Spinner<Double>();
		SpinnerValueFactory<Double> fpsFac;
		SpinnerValueFactory<Double> pixelFac;
		
		
		File inputfile = new File(getInitialDirectory().toFile().getAbsolutePath() + File.separator +"microscope.pref");
		if (inputfile.exists()) {
		    FileInputStream fin = new FileInputStream(inputfile);
			ObjectInputStream oin = new ObjectInputStream(fin);
			MicroscopePreferences readCase = (MicroscopePreferences) oin.readObject();
			
			fpsFac = facGen(1.0, 10000.0, readCase.getFps_value(), 1.0);
			pixelFac = facGen(0.001, 10000.0, readCase.getPixel_value(), 0.001);
			
			fin.close();
			oin.close();
			
		}else{
			fpsFac = facGen(1.0, 10000.0, 200.0, 1.0);
			pixelFac = facGen(0.001, 10000.0, 0.02375, 0.001);
		}
		
		
		//SpinnerValueFactory<Double> fpsFac = facGen(1.0, 10000.0, 200.0, 1.0);
		txtFPS.setValueFactory(fpsFac);
		txtFPS.setEditable(true);
		TextFormatter<Double> formatter = new TextFormatter<Double>(fpsFac.getConverter(), fpsFac.getValue());
		txtFPS.getEditor().setTextFormatter(formatter);
		fpsFac.valueProperty().bindBidirectional(formatter.valueProperty());
		formatter.valueProperty().addListener((s, ov, nv) -> {
			fps_value = nv;
		});
		
		//SpinnerValueFactory<Double> pixelFac = facGen(0.001, 10000.0, 0.02375, 0.001);
		txtPixels.setValueFactory(pixelFac);
		txtPixels.setEditable(true);
		TextFormatter<Double> formatter2 = new TextFormatter<Double>(pixelFac.getConverter(), pixelFac.getValue());
		txtPixels.getEditor().setTextFormatter(formatter2);
		pixelFac.valueProperty().bindBidirectional(formatter2.valueProperty());
		formatter2.valueProperty().addListener((s, ov, nv) -> {
			pixel_value = nv;
		});
		
		Label label1 = new Label();
		label1.setText("Video Recorder (FPS):");
		Label label2 = new Label();
		label2.setText("Pixel Value (µ):");
		
    	Dialog<Boolean> dialogMicroscope = new Dialog<>();
    	dialogMicroscope.initModality(Modality.APPLICATION_MODAL);
    	dialogMicroscope.initOwner(null); //bug do refactor
    	dialogMicroscope.setHeaderText("Set Experiment Settings:");
    	dialogMicroscope.setTitle("Experiment Settings");
    	dialogMicroscope.setResizable(true);
    	GridPane grid = new GridPane();
    	grid.add(label1, 1, 1);
    	grid.add(txtFPS, 2, 1);
    	grid.add(label2, 1, 2);
    	grid.add(txtPixels, 2, 2);
    	dialogMicroscope.getDialogPane().setContent(grid);
    	ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.OK_DONE);
    	dialogMicroscope.getDialogPane().getButtonTypes().add(buttonTypeOk);
    	dialogMicroscope.show();
    	
    	dialogMicroscope.setOnCloseRequest(event -> {
			fps_value = txtFPS.getValue();
			pixel_value = txtPixels.getValue();
        	if(dialogMicroscope.getResult() == null) return;
    		File tmpDir = new File(getInitialDirectory().toFile().getAbsolutePath() + File.separator + "microscope.pref");
    		boolean exists = tmpDir.exists();
    		if (exists == true) {
    			tmpDir.delete();
    		}
    		MicroscopePreferences a = new MicroscopePreferences(pixel_value, fps_value);
    		File tmpDir2 = new File(getInitialDirectory().toFile().getAbsolutePath() + File.separator + "microscope.pref");
    		FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(tmpDir2);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(fout);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		try {
				oos.writeObject(a);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		try {
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		try {
				fout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if (next == true) {
    			nextWindow();
    		}
    	});
	}
	
	
	void askDeleteFile(String selecteditem) {
		Button buttonTypeOk = new Button("Delete");
		Button buttonTypeCancel = new Button("Cancel");
		Stage dialogDeleteF= new Stage();    		
		dialogDeleteF.initModality(Modality.APPLICATION_MODAL);
		dialogDeleteF.initOwner(null);
		dialogDeleteF.setResizable(false);
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(450);
    	grid.setPrefHeight(80);
    	Label askQuestion = new Label("Delete file: " + selecteditem + " ?");
    	GridPane.setHalignment(askQuestion, HPos.CENTER);
    	GridPane.setHgrow(askQuestion, Priority.ALWAYS);
    	GridPane.setVgrow(askQuestion, Priority.ALWAYS);
    	grid.add(askQuestion, 0, 0, 2, 1);
    	
    	GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
    	grid.add(buttonTypeOk, 0, 1, 1, 1);

    	GridPane.setHalignment(buttonTypeCancel, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeCancel, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeCancel, Priority.ALWAYS);
    	grid.add(buttonTypeCancel, 1, 1, 1, 1);
    	buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
        		Path currentRelativePath = Paths.get("");
        		String s = currentRelativePath.toAbsolutePath().toString() + File.separator + selecteditem + "_group.ser";
        		System.out.println(s);
        		File t = new File(s);
        		t.delete();
        		groupsListView.getItems().remove(selecteditem + " (Saved File)");
        		groupsListView.refresh();
            	dialogDeleteF.close();
            }
        });
    	buttonTypeCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	dialogDeleteF.close();
            }
        });
    	dialogDeleteF.setScene(new Scene(grid));
    	dialogDeleteF.show();
	}
	
	void askDeleteMemory(String selecteditem) {
		Button buttonTypeOk = new Button("Delete");
		Button buttonTypeCancel = new Button("Cancel");
		Stage dialogDeleteF= new Stage();    		
		dialogDeleteF.initModality(Modality.APPLICATION_MODAL);
		dialogDeleteF.initOwner(null);
		dialogDeleteF.setResizable(false);
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(450);
    	grid.setPrefHeight(80);
    	Label askQuestion = new Label("Delete Group: " + selecteditem + " ?");
    	GridPane.setHalignment(askQuestion, HPos.CENTER);
    	GridPane.setHgrow(askQuestion, Priority.ALWAYS);
    	GridPane.setVgrow(askQuestion, Priority.ALWAYS);
    	grid.add(askQuestion, 0, 0, 2, 1);
    	
    	GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
    	grid.add(buttonTypeOk, 0, 1, 1, 1);

    	GridPane.setHalignment(buttonTypeCancel, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeCancel, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeCancel, Priority.ALWAYS);
    	buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
        		Groups g = main_package.getCurrent_groups();
        		g.remove(selecteditem);
        		groupsListView.getItems().remove(selecteditem + " (Temporary)");
        		groupsListView.refresh();
//        		doneFiles.remove(selecteditem);
            	dialogDeleteF.close();
            }
        });
    	buttonTypeCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	dialogDeleteF.close();
            }
        }); 
    	dialogDeleteF.setScene(new Scene(grid));
    	dialogDeleteF.show();
	}
	
	@FXML
	void handleDeleteGroup(ActionEvent event) {
		String selecteditem = groupsListView.getSelectionModel().getSelectedItem();
		if (selecteditem.indexOf(" (Saved File)") > -1) {
			//ask delete file, if yes delete file
			String selecteditem2 = selecteditem.split(Pattern.quote(" (Saved File)"))[0];
			askDeleteFile(selecteditem2);
		} else {
			//delete from memory
			String selecteditem2 = selecteditem.split(Pattern.quote(" (Temporary)"))[0];
			askDeleteMemory(selecteditem2);
		}
	}
	
	public void nextWindow() {
		String selecteditem = groupsListView.getSelectionModel().getSelectedItem();
		if (selecteditem.indexOf(" (Saved File)") > -1) {
			selecteditem = selecteditem.split(Pattern.quote(" (Saved File)"))[0];
		}
		if (selecteditem.indexOf(" (Temporary)") > -1) {
			selecteditem = selecteditem.split(Pattern.quote(" (Temporary)"))[0];
		}
		Stage primaryStage = (Stage) cmdNext.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	
		URL url = getClass().getResource("FXML_3b2_DisplayResults.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = null;
    	try {
			root = fxmlloader.load();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
	
		try {
			((Controller_3b2_DisplayResults)fxmlloader.getController()).setContext(main_package, selecteditem, fps_value, pixel_value);
			primaryStage.setTitle("ContractionWave - Input Microscope Data");
//			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setX(prior_X);
			primaryStage.setY(prior_Y);
			System.out.println("Moving on!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
    }
	
	private void validateSelection() throws IOException, ClassNotFoundException {
		if (groupsListView.getSelectionModel().getSelectedItem() != null) {
			//check existance of file containing previous configurations

			File tmpDir = new File(getInitialDirectory().toFile().getAbsolutePath() + "/microscope.pref");
			boolean exists = tmpDir.exists();
			if (exists == false) {
				showDialogMicroscope(true);
			} else {
	    		nextWindow();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Please select a valid group.");
			alert.showAndWait();
		}

	}

	public void setContext(PackageData main_package_data) throws ClassNotFoundException, IOException {
		main_package = main_package_data;
		Groups g = main_package.getCurrent_groups();
		for (int i = 0; i < g.size(); i ++) {
			Group t = g.get(i);
			String name = t.getName();
			doneFiles.add(name + " (Temporary)");
			//TODO add check for Done/Running status
//			if (doneFiles.contains(name + "_group.ser") == false) {
//				doneFiles.add(name);
//			}
		}
		FXCollections.sort(doneFiles);
		groupsListView.setItems(doneFiles);
		
		File tmpDir = new File(getInitialDirectory().toFile().getAbsolutePath() + File.separator +"microscope.pref");
		boolean exists = tmpDir.exists();
		if (exists == true) {
	        FileInputStream fin = new FileInputStream(tmpDir);
			ObjectInputStream oin = new ObjectInputStream(fin);
			MicroscopePreferences readCase = (MicroscopePreferences) oin.readObject();
			pixel_value = readCase.getPixel_value();
			fps_value = readCase.getFps_value();
			fin.close();
			oin.close();
			
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Back to Initial Screen");
		cmdBack.setTooltip(tooltip5);
		
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdNext.setGraphic(new ImageView(imgNext));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Microscope Parametrization");
		cmdNext.setTooltip(tooltip6);
		
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		File[] groupfiles = finder(s);
		for (int i = 0; i < groupfiles.length; i++) {
			File currentfile = groupfiles[i];
			doneFiles.add(currentfile.getName().toString().split(Pattern.quote("_group.ser")) [0] + " (Saved File)");
		}
	
	}
	
	public File[] finder( String dirName){
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() { 
                 public boolean accept(File dir, String filename)
                      { return filename.endsWith("_group.ser"); }
        } );

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
