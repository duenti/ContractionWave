package controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Group;
import model.Groups;
import model.PackageData;

public class Controller_2d_FlowParametrization implements Initializable {
	private PackageData main_package;
	private Path rootDirP;
	private Groups groups = null;
	private List<Double> pyrScaleList = new ArrayList<Double>();
	private List<Double> polySigmaList = new ArrayList<Double>();
	private List<Integer> levelsList = new ArrayList<Integer>();
	private List<Integer> winSizeList = new ArrayList<Integer>();
	private List<Integer> iterationsList = new ArrayList<Integer>();
	private List<Integer> polyNList = new ArrayList<Integer>();

	private StringProperty groupLabelProperty = new SimpleStringProperty("");
//	private StringProperty groupPyrProperty = new SimpleStringProperty("");
//	private StringProperty groupLevelsProperty = new SimpleStringProperty("");
//	private StringProperty groupWinSizeProperty = new SimpleStringProperty("");
//	private StringProperty groupIterationsProperty = new SimpleStringProperty("");
//	private StringProperty groupPolyNProperty = new SimpleStringProperty("");
//	private StringProperty groupPolySigmaProperty = new SimpleStringProperty("");
//	
    @FXML
    private Button cmdParametrize;
    
    @FXML
    private Button btnSaveSet;

    @FXML
    private Label lblInfo, lblGroupId;
    
	@FXML
	Label lblGroup, lblRunPerc;

	@FXML
	TextField txtPyrScale, txtLevels, txtWinSize, txtIterations, txtPolySigma, txtNcores;

	@FXML
	private ComboBox cmbPolyN;
	
    @FXML
    private Slider sliderGroups;

    @FXML
    private Label groupLabel;
	
    @FXML
    private Button cmdBack;

    @FXML
    private TitledPane titlePane;
    
    @FXML
    void handleMenuNewImage(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdParametrize.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		FileReader.chooseSourceDirectory(primaryStage, url, main_package);
    }
    
    @FXML
    void handleMenuNewVideo(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdParametrize.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
    	FileReader.chooseVideoFiles(primaryStage, url, main_package);
    }

    @FXML
    void handleClose(ActionEvent event){
    	Stage primaryStage = (Stage) cmdParametrize.getScene().getWindow();
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
    	Stage primaryStage = (Stage) cmdParametrize.getScene().getWindow();
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

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
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
    void back(ActionEvent event) throws IOException {
    	Stage primaryStage;
    	primaryStage = (Stage) cmdParametrize.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	FXMLLoader fxmlloader = new FXMLLoader();
    	Parent root;
    	
    	if(groups.get(0).getType() == 0){//Image
    		URL url = getClass().getResource("FXML_2c_ImagesNew.fxml");
        	fxmlloader.setLocation(url);
        	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        	root = fxmlloader.load();
        	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());

//        	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//        	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        	
    		((Controller_2c_ImagesNew)fxmlloader.getController()).setContext(main_package, groups, rootDirP);
        	
    		primaryStage.setTitle("ContractionWave - Select Frames");
//    		primaryStage.setMaximized(true);
        	primaryStage.setScene(scene);
        	primaryStage.show();
    		
    		primaryStage.setX(prior_X);
    		primaryStage.setY(prior_Y);
    	}else{//Video
    		URL url = getClass().getResource("FXMLTela2AbrirImagensNew.fxml");
        	FileReader.chooseVideoFiles(primaryStage, url, main_package);
    	}
    }

    @FXML
    void handleMethodStartRun(ActionEvent event) throws IOException {
		if(!validation()) return;		
		
		Stage primaryStage;
    	primaryStage = (Stage) cmdParametrize.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	URL url = getClass().getResource("FXML_2a_ProgressBar.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
		for (int initial = 0; initial < groups.size(); initial++) {
			Group each_group = groups.get(initial);
			each_group.setParameters(pyrScaleList.get(initial), levelsList.get(initial), winSizeList.get(initial), iterationsList.get(initial), polyNList.get(initial), polySigmaList.get(initial));
//			main_package.getCurrent_groups().add(each_group);
		}
		main_package.addNew_groups(groups);
		int cores = Integer.valueOf(txtNcores.getText());
		main_package.setCores(cores);
//		main_package.setCurrent_groups(this.groups);
    	
    	
    	
		((Controller_2a_ProgressBar)fxmlloader.getController()).setContext(groups, main_package);
//		((Controller_2a_ProgressBar)fxmlloader.getController()).setContext(main_package);
		primaryStage.setTitle("ContractionWave - Processing Progress");
//		primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
//		primaryStage.setMaximized(true);
    	primaryStage.show();
    	//((Controller_2a_ProgressBar)fxmlloader.getController()).ask_save_groups();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
	
	private boolean validation(){
		String value = txtPyrScale.getText();
		try{
			Double.parseDouble(value);
		}catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Pyramids Scale must be a real number.");
			alert.showAndWait();
//			JOptionPane.showMessageDialog(null, "Pyramids Scale must be a real number.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("You must inform the pyramids scale.");
			alert.showAndWait();
//	    	JOptionPane.showMessageDialog(null, "You must inform the pyramids scale.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
		
		value = txtPolySigma.getText();
		try{
			Double.parseDouble(value);
		}catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Sigma must be a real number.");
			alert.showAndWait();
//			JOptionPane.showMessageDialog(null, "Sigma must be a real number.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("You must inform the sigma value.");
			alert.showAndWait();
//	    	JOptionPane.showMessageDialog(null, "You must inform the sigma value.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
		
		value = txtLevels.getText();
		try{
			Integer.parseInt(value);
		}catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Levels must be a integer number.");
			alert.showAndWait();
//			JOptionPane.showMessageDialog(null, "Levels must be a integer number.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("You must inform the levels.");
			alert.showAndWait();
//	    	JOptionPane.showMessageDialog(null, "You must inform the levels.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
		
		value = txtWinSize.getText();
		try{
			Integer.parseInt(value);
		}catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Window Size must be a integer numbe");
			alert.showAndWait();
//			JOptionPane.showMessageDialog(null, "Window Size must be a integer number.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("You must inform the window size.");
			alert.showAndWait();
//	    	JOptionPane.showMessageDialog(null, "You must inform the window size.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
		
		value = txtIterations.getText();
		try{
			Integer.parseInt(value);
		}catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Iterations must be a integer number.");
			alert.showAndWait();
//			JOptionPane.showMessageDialog(null, "Iterations must be a integer number.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("You must inform the number of iterations.");
			alert.showAndWait();
//	    	JOptionPane.showMessageDialog(null, "You must inform the number of iterations.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
		
		value = cmbPolyN.getSelectionModel().getSelectedItem().toString();
		try{
			Integer.parseInt(value);
		}catch(NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("Pixel size must be a integer number.");
			alert.showAndWait();
//			JOptionPane.showMessageDialog(null, "Pixel size must be a integer number.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    } catch(NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setContentText("You must inform the pixel size.");
			alert.showAndWait();
//	    	JOptionPane.showMessageDialog(null, "You must inform the pixel size.","Warning",JOptionPane.WARNING_MESSAGE);
	        return false;
	    }
		
		return true;
	}
	
	@FXML
	void handleForAllSave(ActionEvent event) {
		for (int initial = 0; initial < groups.size(); initial++) {
			
			pyrScaleList.set(initial, Double.valueOf(txtPyrScale.getText()));
        	polySigmaList.set(initial, Double.valueOf(txtPolySigma.getText()));
			levelsList.set(initial, Integer.valueOf(txtLevels.getText()));
			winSizeList.set(initial, Integer.valueOf(txtWinSize.getText()));
			iterationsList.set(initial, Integer.valueOf(txtIterations.getText()));
			polySigmaList.set(initial, Double.valueOf(txtPolySigma.getText()));
			polyNList.set(initial, Integer.valueOf(cmbPolyN.getSelectionModel().getSelectedItem().toString()));
		}
	}
    
	
	private int groupIndex = 0;
	
	public void setContext(PackageData main_package_init, Groups g, Path rootDir) {
        // initialize fileList dependent data here rather then in initialize()
		main_package = main_package_init;
		groups = g;
		rootDirP = rootDir;
		groupLabelProperty.set(groups.get(groupIndex).getName());
		for (int initial = 0; initial < groups.size(); initial++) {
			//Group each_group = groups.get(initial);
			pyrScaleList.add(0.5);
			levelsList.add(1);
			winSizeList.add(15);
			iterationsList.add(1);
			polySigmaList.add(1.5);
			polyNList.add(7);
		}

		groupLabelProperty.set(groups.get(groupIndex).getName());
//		groupPyrProperty.set(String.valueOf(pyrScaleList.get(groupIndex)));
//		groupPolySigmaProperty.set(String.valueOf(polySigmaList.get(groupIndex)));
//		groupLevelsProperty.set(String.valueOf(levelsList.get(groupIndex)));
//		groupWinSizeProperty.set(String.valueOf(winSizeList.get(groupIndex)));
//		groupIterationsProperty.set(String.valueOf(iterationsList.get(groupIndex)));
//		groupPolyNProperty.set(String.valueOf(polyNList.get(groupIndex)));
		

//		
		txtPyrScale.setText(String.valueOf(pyrScaleList.get(groupIndex)));
		pyrScaleList.set(groupIndex, Double.valueOf(txtPyrScale.getText()));
		txtPolySigma.setText(String.valueOf(polySigmaList.get(groupIndex)));
    	polySigmaList.set(groupIndex, Double.valueOf(txtPolySigma.getText()));
		txtLevels.setText((String.valueOf(levelsList.get(groupIndex))));
		levelsList.set(groupIndex, Integer.valueOf(txtLevels.getText()));
		txtWinSize.setText(String.valueOf(winSizeList.get(groupIndex)));
		winSizeList.set(groupIndex, Integer.valueOf(txtWinSize.getText()));
		txtIterations.setText(String.valueOf(iterationsList.get(groupIndex)));
		iterationsList.set(groupIndex, Integer.valueOf(txtIterations.getText()));
//		txtPolySigma.setText(String.valueOf(polyNList.get(groupIndex)));
//		polySigmaList.set(groupIndex, Double.valueOf(txtPolySigma.getText()));
//		
		
		//this.lblGroup.setText("Group: " + groups.get(0).getName());
	
		sliderGroups.setMin(1);
		sliderGroups.setMax(groups.size());
		sliderGroups.setValue(1);
		sliderGroups.setBlockIncrement(1.0);
		sliderGroups.setMajorTickUnit(1.0);
		sliderGroups.setMinorTickCount(0);
		sliderGroups.setShowTickLabels(true);
		sliderGroups.setShowTickMarks(true);
		sliderGroups.setSnapToTicks(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		int cores = Runtime.getRuntime().availableProcessors();
		if(cores > 1)
			txtNcores.setText(String.valueOf(cores-1));
		else
			txtNcores.setText(String.valueOf(1));
			
		groupLabelProperty.set("");
		
		groupLabel.textProperty().bind(groupLabelProperty);
		
		titlePane.setExpanded(false);
		cmbPolyN.setEditable(false);
		cmbPolyN.getItems().addAll("5","7");
		cmbPolyN.getSelectionModel().select(1);
		
		txtPyrScale.setEditable(true);
		txtLevels.setEditable(true);
		txtWinSize.setEditable(true);
		txtIterations.setEditable(true);
		txtPolySigma.setEditable(true);
		
		txtPyrScale.setTooltip(new Tooltip("Specify the image scale (<1) to build pyramids for each image; pyr_scale=0.5 means a classical pyramid, where each next layer is twice smaller than the previous one."));
		txtLevels.setTooltip(new Tooltip("No extra layers are created and only the original images are used."));
		txtWinSize.setTooltip(new Tooltip("Averaging window size; larger values increase the algorithm robustness to image noise and give more chances for fast motion detection, but yield more blurred motion field."));
		txtIterations.setTooltip(new Tooltip("Number of iterations the algorithm does at each pyramid level."));
		cmbPolyN.setTooltip(new Tooltip("Size of the pixel neighborhood used to find polynomial expansion in each pixel; larger values mean that the image will be approximated with smoother surfaces."));
		txtPolySigma.setTooltip(new Tooltip("Standard deviation of the Gaussian that is used to smooth derivatives used as a basis for the polynomial expansion."));
		
//		txtPyrScale.textProperty().bind(groupPyrProperty);
//		txtLevels.textProperty().bind(groupLevelsProperty);
//		txtWinSize.textProperty().bind(groupWinSizeProperty);
//		txtIterations.textProperty().bind(groupIterationsProperty);
//		//txtPolyN.textProperty().bind(groupPolyNProperty);
//		txtPolySigma.textProperty().bind(groupPolySigmaProperty);
		
		txtPyrScale.textProperty().addListener((observable, oldValue, newValue) -> {
			pyrScaleList.set(groupIndex, Double.valueOf(newValue));
		});
		
		txtLevels.textProperty().addListener((observable, oldValue, newValue) -> {
			levelsList.set(groupIndex, Integer.valueOf(newValue));
		});
		
		txtWinSize.textProperty().addListener((observable, oldValue, newValue) -> {
			winSizeList.set(groupIndex, Integer.valueOf(newValue));
		});
		
		txtIterations.textProperty().addListener((observable, oldValue, newValue) -> {
			iterationsList.set(groupIndex, Integer.valueOf(newValue));
		});
		
		cmbPolyN.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String t, String t1) {
	        	polyNList.set(groupIndex, Integer.valueOf(t1));
	          if(t1.equals("5")){
	        	  txtPolySigma.setText(String.valueOf("1.1"));
	        	  polySigmaList.set(groupIndex, 1.1);
//	        	  groupPolySigmaProperty.set("1.1");
	          }else{
	        	  txtPolySigma.setText(String.valueOf("1.5"));
	        	  polySigmaList.set(groupIndex, 1.5);
//	        	  groupPolySigmaProperty.set("1.5");
	          }
	        }    
	    });		
		txtPolySigma.textProperty().addListener((observable, oldValue, newValue) -> {
			polySigmaList.set(groupIndex, Double.valueOf(newValue));
		});
		
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdParametrize.setGraphic(new ImageView(imgNext));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Run Optical Flow");
		cmdParametrize.setTooltip(tooltip5);
		
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Back to the previous scene");
		cmdBack.setTooltip(tooltip6);
		//setRunningVisibility(false);
		
		sliderGroups.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
				System.out.print(new_val.intValue());
				System.out.print(" ");
				System.out.println(groups.size());
				if(new_val.intValue() < groups.size() && new_val.intValue() > 0){
					pyrScaleList.set(groupIndex, Double.valueOf(txtPyrScale.getText()));
		        	polySigmaList.set(groupIndex, Double.valueOf(txtPolySigma.getText()));
					levelsList.set(groupIndex, Integer.valueOf(txtLevels.getText()));
					winSizeList.set(groupIndex, Integer.valueOf(txtWinSize.getText()));
					iterationsList.set(groupIndex, Integer.valueOf(txtIterations.getText()));
					polyNList.set(groupIndex, Integer.valueOf(cmbPolyN.getSelectionModel().getSelectedItem().toString()));
					//first save changes
					
					//then get group index
					groupIndex = new_val.intValue()-1;
					
					//then renew screen values
					lblGroupId.setText(String.valueOf(groupIndex+1));
					groupLabelProperty.set(groups.get(groupIndex).getName());
					txtPyrScale.setText(String.valueOf(pyrScaleList.get(groupIndex)));
					txtPolySigma.setText(String.valueOf(polySigmaList.get(groupIndex)));
					txtLevels.setText((String.valueOf(levelsList.get(groupIndex))));
					txtWinSize.setText(String.valueOf(winSizeList.get(groupIndex)));
					txtIterations.setText(String.valueOf(iterationsList.get(groupIndex)));
					
					String current_val = polyNList.get(groupIndex).toString();
					
					//f
					ObservableList<String> datacmb = cmbPolyN.getItems();
					for (String z : datacmb) {
						System.out.println(z);
						if (z.equals(current_val) == true) {
							cmbPolyN.setValue(current_val);
							break;
						}
					}
				}
			}
		});
	}
	
	


}