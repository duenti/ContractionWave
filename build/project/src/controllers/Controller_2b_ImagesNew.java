package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Groups;
import model.ImageGroup;
import model.PackageData;
import model.VideoGroup;

public class Controller_2b_ImagesNew implements Initializable {
	private PackageData main_package;
	private String rootDir = "";
	private Path rootDirP;
	private List<File> selectedFiles = new ArrayList<File>();
	private boolean returnBool = false;
	private int method;
	
	@FXML
	private TreeView<String> filetreeview;
	
	@FXML
	private ListView<String> grouplistview;
	private static ObservableList<String> listviewitems;
	private static List<String> listviewpaths; 

    @FXML
    private Region region;

    @FXML
    private Button cmdBack;
    
    @FXML
    private Button cmdNext;

    @FXML
    private Button cmdShowAll;

    @FXML
    private Button cmdMergeGroups;

    @FXML
    private Button cmdHideAll;

    @FXML
    private Button cmdDeleteGroup;

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
    void back(ActionEvent event) throws IOException  {
		Stage primaryStage;
    	primaryStage = (Stage) cmdShowAll.getScene().getWindow();
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
		primaryStage.setTitle("ContractionWave - Welcome");
//		primaryStage.setMaximized(true);
    	primaryStage.setScene(scene);
    	primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);

    }
    
	@FXML
	private void next(ActionEvent event)  throws Exception {
    	Stage primaryStage;
    	primaryStage = (Stage) cmdShowAll.getScene().getWindow();
    	Scene oldScene = primaryStage.getScene();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
    	FXMLLoader fxmlloader = new FXMLLoader();
    	Parent root;
    	
    	Groups unqueued_groups = new Groups();
    	if(method == 0){//Image
    		for (int i = 0; i < listviewitems.size(); i++) {
    			ImageGroup ig = new ImageGroup(listviewitems.get(i),listviewpaths.get(i));
    			ig.setType(0);
    			unqueued_groups.add(ig);
    		}
    		URL url = getClass().getResource("FXML_2c_ImagesNew.fxml");
        	fxmlloader.setLocation(url);
        	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        	root = fxmlloader.load();
        	Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());

//        	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//        	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        	
    		((Controller_2c_ImagesNew)fxmlloader.getController()).setContext(main_package, unqueued_groups, rootDirP);
        	
    		primaryStage.setTitle("ContractionWave - View Groups");
        	primaryStage.setScene(scene);
        	primaryStage.show();
    		
    		primaryStage.setX(prior_X);
    		primaryStage.setY(prior_Y);
    	}else{//Video
    		for (int i = 0; i < listviewitems.size(); i++) {
    			VideoGroup vg = new VideoGroup(listviewitems.get(i),listviewpaths.get(i));
    			vg.setType(1);
    			unqueued_groups.add(vg);
    		}
    	    primaryStage = (Stage) cmdNext.getScene().getWindow();
    	    oldScene = primaryStage.getScene();
    	    URL url = getClass().getResource("FXML_2d_FlowParametrization.fxml");
    	    fxmlloader.setLocation(url);
    	    fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
    	    root = fxmlloader.load();
    	    Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());

//        	Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//        	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	    ((Controller_2d_FlowParametrization)fxmlloader.getController()).setContext(main_package, unqueued_groups, rootDirP);
    		primaryStage.setTitle("ContractionWave - Optical Flow Parametrization");
//    		primaryStage.setMaximized(true);
    	    primaryStage.setScene(scene);
    	    primaryStage.show();
    		
    		primaryStage.setX(prior_X);
    		primaryStage.setY(prior_Y);
    	}
		
	}
    
	private void expandTreeView(TreeItem<?> child2){
	    if(child2 != null && !child2.isLeaf()){
	        child2.setExpanded(true);
	        for(TreeItem<?> child:child2.getChildren()){
	            expandTreeView(child);
	        }
	    }
	}
	
	private void collapseTreeView(TreeItem<?> item){
	    if(item != null && !item.isLeaf()){
	        item.setExpanded(false);
	        for(TreeItem<?> child:item.getChildren()){
	            collapseTreeView(child);
	        }
	    }
	}
	
	@SuppressWarnings("unchecked")
	private static String getFullPath(TreeItem<?> child2) {
		StringBuilder pathBuilder = new StringBuilder();
		for (TreeItem<String> item = (TreeItem<String>) child2; item != null ; item = item.getParent()) {
		    pathBuilder.insert(0, item.getValue());
		    if (item.getParent() != null) {
		    	pathBuilder.insert(0, File.separator);
		    }
		}
		String path = pathBuilder.toString();
		return path;
	}
	
	private void findUnCheckItems(TreeItem<?> child2, String needle) {
	    if (((CheckBoxTreeItem<?>) child2).isSelected() && child2.isLeaf() == true) {
	    	String this_path = getFullPath(child2);
	    	if (this_path.equals(needle)) {
	    		((CheckBoxTreeItem<?>) child2).setSelected(false);
	    		this.setReturnBool(true);
	    	}
	    }
	    for (TreeItem<?> child : child2.getChildren()) {
	    	findUnCheckItems((TreeItem<?>) child, needle);
	    }
	}
	
	@FXML
	private void handleExpandAll(ActionEvent event) {
		TreeItem<String> rootItem = filetreeview.getRoot(); 
    	expandTreeView(rootItem);
	}
	
	@FXML
	private void handleCollapseAll(ActionEvent event) {
		TreeItem<String> rootItem = filetreeview.getRoot(); 
    	collapseTreeView(rootItem);
	}

	@FXML
	private void handleMergeSelection(ActionEvent event) {
		System.out.println(filetreeview.getSelectionModel().getSelectedItems().toString());
		//open dialog to name group
		TextInputDialog dialog = new TextInputDialog("Group");
		dialog.setTitle("Merge Groups");
		dialog.setHeaderText("Merge two groups");
		String outtxt = "Groups to be merged:\n\n";
		String full_items_paths = "";
		Boolean first = false;
		for (TreeItem<String> i_item : filetreeview.getSelectionModel().getSelectedItems()) {
			String item_path = getFullPath(i_item);
			//String i_string_to = i_item.getValue();
			String i_string_to = item_path;
			outtxt += i_string_to + "\n";
			if (first == true) {
				full_items_paths += "__" + item_path ;
			} else {
				full_items_paths += item_path;
			}
			first = true;
		}
		//outtxt += "\nPor favor digite um nome para o novo grupo:";
		dialog.setContentText(outtxt);

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    System.out.println("Nome do novo grupo: " + result.get());
		    listviewitems.add(result.get());
		    listviewpaths.add(full_items_paths);
		}
	}
	
	@FXML
	private void handleRemoveSelection(ActionEvent event) {
		findUnCheckItems(filetreeview.getRoot(), listviewpaths.get(grouplistview.getSelectionModel().getSelectedIndex()));
		System.out.println(String.valueOf(this.getReturnBool()));
		
		if (this.getReturnBool() == false) {
			listviewpaths.remove(grouplistview.getSelectionModel().getSelectedIndex());
			listviewitems.remove(grouplistview.getSelectionModel().getSelectedItem().toString());
		}
		this.setReturnBool(false);
	}
	
	public static void createTree(File file, CheckBoxTreeItem<String> parent) {
	    if (file.isDirectory()) {
	        //CheckBoxTreeItem<String> treeItem = new CheckBoxTreeItem<>(parent.getValue() + '/' + file.getName());
	    	CheckBoxTreeItem<String> treeItem = new CheckBoxTreeItem<String>(file.getName());
	        //filepaths.put(file.getName(), parent.getValue() + '/' + file.getName());
	    	//add recursive select button
	        treeItem.selectedProperty().addListener((obs, oldVal, newVal) -> {
	        	System.out.println(treeItem.getValue() + " selection state: " + newVal);
	        	if (treeItem.isLeaf()) {
	        		String full_path = getFullPath(treeItem);
	        		if (newVal == true) {
	        			//if newVal equals true, push group to ListView
	        			listviewitems.add(treeItem.getValue());
	        			listviewpaths.add(full_path);
	        		} else {
	        			//if newVal equals false, remove group from ListView
	        			listviewitems.remove(treeItem.getValue());
	        			listviewpaths.remove(full_path);
	        		}
	        		System.out.println(full_path);
	        	}
	        });
	        
	        parent.getChildren().add(treeItem);
	        for (File f : file.listFiles()) {
	            createTree(f, treeItem);
	        }
	    }
	}
	
	private File inputDirFile;
	public void displayTreeView(String inputDirectoryLocation) {
	    // Creates the root item.
	    CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<String>(inputDirectoryLocation);

	    // Creates the cell factory.
	    filetreeview.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
	    
	    // Get a list of files.
	    File fileInputDirectoryLocation = new File(inputDirectoryLocation);
	    inputDirFile = fileInputDirectoryLocation;
	    File fileList[] = fileInputDirectoryLocation.listFiles();
	    
	    //Add event
	    rootItem.selectedProperty().addListener((obs, oldVal, newVal) -> {
        	if (rootItem.getChildren().size() == 0) {
        		String full_path = getFullPath(rootItem);
        		if (newVal == true) {
        			//if newVal equals true, push group to ListView
        			listviewitems.add(inputDirFile.getName());
        			listviewpaths.add(full_path);
        		} else {
        			//if newVal equals false, remove group from ListView
        			listviewitems.remove(inputDirFile.getName());
        			listviewpaths.remove(full_path);
        		}
        	}
        });
	    
	    // create tree
	    int subdir_num = 0;
	    for (File file : fileList) {
	    	if (file.isDirectory() == true) {
	    		subdir_num += 1;
	    	}
	        createTree(file, rootItem);
	    }
	    filetreeview.setRoot(rootItem);
	    
    	System.out.println(subdir_num);
	    if (subdir_num == 0) {
	    	rootItem.setSelected(true);
	    }
	}
	
	public void setContext(PackageData main_package_init, Path rootDir2) {
		main_package = main_package_init;
		cmdShowAll.setVisible(true);
		cmdMergeGroups.setVisible(true);
		cmdHideAll.setVisible(true);
		method = 0;
		rootDirP = rootDir2;
		Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	filetreeview.getScene().setCursor(Cursor.WAIT);
    	    }
    	});
    	
		rootDir = rootDir2.toString();
		System.out.println("rootDir 2:" + rootDir);
		//root dir defined, treeview now needs to be started
		displayTreeView(rootDir);
		
    	Platform.runLater(new Runnable() {
    	    @Override
    	    public void run() {
    	    	filetreeview.getScene().setCursor(Cursor.DEFAULT);
    	    }
    	}); 	
    }
	
	public void setContext(PackageData main_package_init, List<File> files, Path rootDirZ){
		main_package = main_package_init;
		cmdShowAll.setVisible(false);
		cmdMergeGroups.setVisible(false);
		cmdHideAll.setVisible(false);
		method = 1;
		rootDirP = rootDirZ;
		
		selectedFiles = files;
		
		CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<String> ("Video Files");
		rootItem.setSelected(true);
		rootItem.setExpanded(true);
		// Creates the File items.
		for(File f : selectedFiles){
			CheckBoxTreeItem<String> item = new CheckBoxTreeItem<String>(f.getAbsolutePath());
			
			item.selectedProperty().addListener((obs, oldVal, newVal) -> {
	        	System.out.println(item.getValue() + " selection state: " + newVal);
	        	if (item.isLeaf()) {
	        		if (newVal == true) {
	        			//if newVal equals true, push group to ListView
	        			listviewitems.add(f.getName());
	        			listviewpaths.add(f.getAbsolutePath());
	        		} else {
	        			//if newVal equals false, remove group from ListView
	        			listviewitems.remove(f.getName());
	        			listviewpaths.remove(f.getAbsolutePath());
	        		}
	        		System.out.println(f.getName());
	        	}
	        });
			
			item.setSelected(true);
			rootItem.getChildren().add(item);
		}

	    // Creates the cell factory.
	    filetreeview.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

	    filetreeview.setRoot(rootItem);
	}
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		filetreeview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		grouplistview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listviewitems = FXCollections.observableArrayList();
		listviewpaths = new ArrayList<String>();
		grouplistview.setItems(listviewitems);
		
		Image imgExpand = new Image(getClass().getResourceAsStream("/expanding-two-opposite-arrows-diagonal-symbol-of-interface.png"));
		cmdShowAll.setGraphic(new ImageView(imgExpand));
		Tooltip tooltip = new Tooltip();
		tooltip.setText("Expand all subdirectories");
		cmdShowAll.setTooltip(tooltip);
		
		Image imgHide = new Image(getClass().getResourceAsStream("/collapse-two-arrows-diagonal-symbol.png"));
		cmdHideAll.setGraphic(new ImageView(imgHide));
		Tooltip tooltip2 = new Tooltip();
		tooltip2.setText("Hide all subdirectories");
		cmdHideAll.setTooltip(tooltip2);
	
		Image imgGroup = new Image(getClass().getResourceAsStream("/add-circular-outlined-button.png"));
		cmdMergeGroups.setGraphic(new ImageView(imgGroup));
		Tooltip tooltip3 = new Tooltip();
		tooltip3.setText("Merge selected groups");
		cmdMergeGroups.setTooltip(tooltip3);
		
		Image imgRemove = new Image(getClass().getResourceAsStream("/close-circular-button-symbol.png"));
		cmdDeleteGroup.setGraphic(new ImageView(imgRemove));
		Tooltip tooltip4 = new Tooltip();
		tooltip4.setText("Delete group");
		cmdDeleteGroup.setTooltip(tooltip4);

		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Back to the initial scene");
		cmdBack.setTooltip(tooltip5);
		
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdNext.setGraphic(new ImageView(imgNext));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("View Group Info");
		cmdNext.setTooltip(tooltip6);
		
	}
	
	public boolean getReturnBool() {
		return returnBool;
	}

	
	public void setReturnBool(boolean returnBool) {
		this.returnBool = returnBool;
	}

}
