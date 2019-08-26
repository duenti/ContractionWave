package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowSavedDialog {
	public static void showDialog() {
		Stage dialogSaved = new Stage();
 		Button buttonTypeOk = new Button("Ok");
    	dialogSaved.initModality(Modality.NONE);
    	dialogSaved.initOwner(null);
    	dialogSaved.setResizable(true);
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(300);
    	grid.setPrefHeight(80);		
    	Label askQuestion = new Label("The requested files were successfully saved.");
//	    grid.setGridLinesVisible(true);	
	    grid.add(askQuestion, 0, 0, 3, 1);
	    GridPane.setHalignment(askQuestion, HPos.CENTER);
	    GridPane.setHgrow(askQuestion, Priority.ALWAYS);
	    GridPane.setVgrow(askQuestion, Priority.ALWAYS);
	    grid.add(buttonTypeOk, 1, 1, 1, 1);
	    GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
	    GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
	    GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
	    buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	    	public void handle(ActionEvent event) {
	    		dialogSaved.close();
	        }
	    });
	    dialogSaved.setScene(new Scene(grid));
	    dialogSaved.show();
	}
}
