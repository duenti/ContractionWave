package controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Group;
import model.Groups;
import model.ImageGroup;
import model.PackageData;

public class Controller_2c_ImagesNew implements Initializable {
	private PackageData main_package;
	private Path rootDirP;
	private Groups groups;

	
	@FXML
	Button cmdBack, cmdNext;
	
	@FXML
    private TableView<ImageGroup> mainTable;

    @FXML
    private TableColumn<ImageGroup, String> namecol;

    @FXML
    private TableColumn<ImageGroup, Integer> imgnumcol;

    @FXML
    private TableColumn<ImageGroup, String> sizecol;

    @FXML
    private TableColumn<ImageGroup, String> sortcol;
	
//	@FXML
//	MenuItem menuAlphanumeric, menuAlphanumericReverse, menuNumeric, menuNumericReverse, menuDate, menuDateReverse;
	
	@FXML
	private ListView<String> listviewchooseimg;

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
	
	public void setContext(PackageData main_package_init, Groups gs, Path rootDirZ){
		main_package = main_package_init;
		rootDirP = rootDirZ;
		groups = gs;
		for (int i = 0; i < gs.size(); i++) {
			Group this_group = gs.get(i);
			mainTable.getItems().add( (ImageGroup) this_group);
		}
	}
	
	@FXML
	void back(ActionEvent event) throws IOException  {
		Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
	    FXMLLoader fxmlloader = new FXMLLoader();
	    fxmlloader.setLocation(url);
	    fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
	    
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	primaryStage.getScene().setCursor(Cursor.WAIT);
    	    }
    	});
    	System.out.println("rootDirP.toString()");
    	System.out.println(rootDirP.toString());
    	((Controller_2b_ImagesNew)fxmlloader.getController()).setContext(main_package, rootDirP);
    	
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	primaryStage.getScene().setCursor(Cursor.DEFAULT);
    	    }
    	}); 	

		primaryStage.setTitle("ContractionWave - Select the images path:");
//		primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
    	primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
	
	@FXML
	private void navigateNextPage(ActionEvent event) throws IOException {
		//goes to progress page and if queue not started, starts the queue
    	Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	URL url = getClass().getResource("FXML_2d_FlowParametrization.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	
    	((Controller_2d_FlowParametrization)fxmlloader.getController()).setContext(main_package, groups, rootDirP);
		primaryStage.setTitle("ContractionWave - Flow Parametrization");
//		primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
    	primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
	}
		
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-curve-outline.png"));
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Return to Initial Screen");
		cmdBack.setTooltip(tooltip5);
		
//		Image imgNext = new Image(getClass().getResourceAsStream("/login-square-arrow-button-outline.png"));
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdNext.setGraphic(new ImageView(imgNext));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Parametrize Optical Flow");
		cmdNext.setTooltip(tooltip6);
				
		namecol.setCellValueFactory(new PropertyValueFactory<ImageGroup, String>("name"));
		imgnumcol.setCellValueFactory(new PropertyValueFactory<ImageGroup, Integer>("size"));
		sizecol.setCellValueFactory(new PropertyValueFactory<ImageGroup, String>("sizecol"));
		ObservableList<String> options = FXCollections.observableArrayList("Numbers", "Modified Date", "Alphanumeric", "Reverse Numbers", "Reverse Modified Date", "Reverse Alphanumeric");
		
		sortcol.setCellValueFactory(i -> {
	        final String value = i.getValue().getSorttype();
	        // binding to constant value
	        return Bindings.createObjectBinding(() -> value);
	    });

	    sortcol.setCellFactory(col -> {
	        TableCell<ImageGroup, String> c = new TableCell<>();
	        final ComboBox<String> comboBox = new ComboBox<>(options);
//	        c.itemProperty().addListener((observable, oldValue, newValue) -> {
//	            if (oldValue != null) {
//	                comboBox.valueProperty().unbindBidirectional(oldValue);
//	            }
//	            if (newValue != null) {
//	                comboBox.valueProperty().bindBidirectional(newValue);
//	            }
//	        });
	        comboBox.getSelectionModel().selectedItemProperty().addListener( (observable2, oldValue, newValue) ->{
	            if (oldValue != newValue) {
	            	System.out.println("changed!");
	            	TableCell<ImageGroup, StringProperty> this_c = (TableCell<ImageGroup, StringProperty>) comboBox.getParent();
	            	ImageGroup this_group = (ImageGroup) this_c.getTableRow().getItem();
	            	if (newValue.equals("Numbers")) {
	            		this_group.sortByNumbers();
	            	} else if (newValue.equals("Modified Date")) {
	            		this_group.sortByLastModifiedDate();
	            	} else if (newValue.equals("Alphanumeric")) {
	        			this_group.sortByAlphanumericName();
	            	} else if (newValue.equals("Reverse Numbers")) {
	            		this_group.sortByNumbers();
	            		this_group.reverseImages();
	            	} else if (newValue.equals("Reverse Modified Date")) {
	            		this_group.sortByLastModifiedDate();
	            		this_group.reverseImages();
	            	} else if (newValue.equals("Reverse Alphanumeric")) {
	            		this_group.sortByAlphanumericName();
	            		this_group.reverseImages();
	            	}
	            }
	        });
	        comboBox.getSelectionModel().select(0);
	        c.graphicProperty().bind(Bindings.when(c.emptyProperty()).then((Node) null).otherwise(comboBox));
	        return c;
	    });
	}

}