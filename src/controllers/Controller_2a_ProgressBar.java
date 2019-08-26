package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.Group;
import model.Groups;
import model.PackageData;

public class Controller_2a_ProgressBar implements Initializable {
	private PackageData main_package;
	
	@FXML
	private TableColumn<Group,String> colGroupName;
	@FXML
	private TableColumn<Group, String> colStatus;
	@FXML
	private TableColumn<Group,Double> colProgress;
	@FXML
	private TableColumn<Group, Double> colTimeRemaining;

	@FXML
	private TableView<Group> tableRunStatus;
	
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
    void handleReinitialize(ActionEvent event) throws IOException, ClassNotFoundException{
    	reinit(); 
    }
    
    void reinit() throws IOException, ClassNotFoundException {
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();

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
    	Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
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
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	Scene oldScene = primaryStage.getScene();
		URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();

//    	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
    	killTimeline();
		((Controller_1_InitialScreen)fxmlloader.getController()).setContext(main_package);
		primaryStage.setTitle("ContractionWave - Welcome");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);

    }
    
    public void setContext(PackageData main_package_init) { //first form of setContext. involves listening to the queue
    	//here groups are added to their queue
    	main_package = main_package_init;
    	Groups gs = main_package.getCurrent_groups();
    	
    	tableRunStatus.setItems(gs.getObservableList());
    	
//    	ask_save_groups();
    	
    	timeline = new Timeline(new KeyFrame(Duration.seconds(0.001),
    			new EventHandler<ActionEvent>() {
    				@Override
    				public void handle(ActionEvent event) {
    					tableRunStatus.refresh();
    					boolean is_it_done = true;
    					for (int i = 0; i < main_package.getCurrent_groups().size(); i++) {
    						Group g = main_package.getCurrent_groups().get(i);
    						String currentStatus = g.getStatus();
    						if (!currentStatus.equals("Done")) {
    							is_it_done = false;
    						}
    					}
    		    		if (is_it_done == true) {
    		    			killTimeline();
    		    		}
    				}
    			}
    	));
    	timeline.setCycleCount(Timeline.INDEFINITE);
    	timeline.play();
    }
    
    public void setContext(Groups gs2, PackageData main_package_init) { //first form of setContext. involves listening to the queue
    	//here groups are added to their queue
    	main_package = main_package_init;
    	Groups gs = main_package.getCurrent_groups();
    	
    	tableRunStatus.setItems(gs.getObservableList());
    	
    	ask_save_groups();
    	
    	timeline = new Timeline(new KeyFrame(Duration.seconds(0.001),
    			new EventHandler<ActionEvent>() {
    				@Override
    				public void handle(ActionEvent event) {
    					tableRunStatus.refresh();
    					boolean is_it_done = true;
    					for (int i = 0; i < main_package.getCurrent_groups().size(); i++) {
    						Group g = main_package.getCurrent_groups().get(i);
    						String currentStatus = g.getStatus();
    						if (!currentStatus.equals("Done")) {
    							is_it_done = false;
    						}
    					}
    		    		if (is_it_done == true) {
    		    			killTimeline();
    		    		}
    				}
    			}
    	));
    	timeline.setCycleCount(Timeline.INDEFINITE);
    	timeline.play();
    }
    
    
    private Timeline timeline;
    
    private void killTimeline() {
    	timeline.stop();
    }
    
    public void ask_save_groups() {
		Button buttonTypeOk = new Button("Save");
		Button buttonTypeCancel = new Button("Skip");
		Button buttonTypeReturn = new Button("Cancel");
		Stage dialogMicroscope= new Stage();    		
    	dialogMicroscope.initModality(Modality.APPLICATION_MODAL);
    	dialogMicroscope.initOwner(null);
    	dialogMicroscope.setResizable(false);
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(450);
    	grid.setPrefHeight(80);
    	Label askQuestion = new Label("Save Progress in Hard Drive?");
    	GridPane.setHalignment(askQuestion, HPos.CENTER);
    	GridPane.setHgrow(askQuestion, Priority.ALWAYS);
    	GridPane.setVgrow(askQuestion, Priority.ALWAYS);
    	grid.add(askQuestion, 0, 0, 3, 1);
    	
    	GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
    	grid.add(buttonTypeOk, 0, 1, 1, 1);

    	GridPane.setHalignment(buttonTypeCancel, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeCancel, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeCancel, Priority.ALWAYS);
    	grid.add(buttonTypeCancel, 1, 1, 1, 1);
    	
    	GridPane.setHalignment(buttonTypeReturn, HPos.CENTER);
    	GridPane.setHgrow(buttonTypeReturn, Priority.ALWAYS);
    	GridPane.setVgrow(buttonTypeReturn, Priority.ALWAYS);
    	grid.add(buttonTypeReturn, 2, 1, 1, 1);
    	
    	buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Controller_2a_ProgressBar.setSave_groups_status(true);
                dialogMicroscope.close();
    	    	main_package.runGroups(true);
            }
        });
    	buttonTypeCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Controller_2a_ProgressBar.setSave_groups_status(false);
                dialogMicroscope.close();
    	    	main_package.runGroups(false);
            }
        });
    	buttonTypeReturn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Controller_2a_ProgressBar.setSave_groups_status(false);
                dialogMicroscope.close();
                try {
					reinit();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    	dialogMicroscope.setOnCloseRequest(new EventHandler<WindowEvent>() {
    		@Override
    	      public void handle(WindowEvent we) {
//    	    	main_package.runGroups(false);
                try {
					reinit();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	      }
    	}); 
    	dialogMicroscope.setScene(new Scene(grid));
    	dialogMicroscope.show();
    }
    
    
//	public static boolean isSave_groups_status() {
//		return save_groups_status;
//	}
//
//	public static void setSave_groups_status(boolean save_groups_status2) {
//		save_groups_status = save_groups_status2;
//	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {                
		Image imgBack = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Back to the Initial Menu");
		cmdBack.setTooltip(tooltip5);
		
		//Fit columns
		tableRunStatus.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
		colGroupName.setMaxWidth( 1f * Integer.MAX_VALUE * 40 ); // 40% width
		colStatus.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
		colProgress.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
		colTimeRemaining.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
		
		//Fit data to table
		colGroupName.setCellValueFactory(new PropertyValueFactory<Group,String>("name"));
		colStatus.setCellValueFactory(new PropertyValueFactory<Group,String>("status"));
		colProgress.setCellValueFactory(new PropertyValueFactory<Group,Double>("progress"));
		colTimeRemaining.setCellValueFactory(new PropertyValueFactory<Group,Double>("remainingTime"));
		colProgress.setCellFactory(ProgressBarTableCell.forTableColumn());
		
	}
}