package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControllerImage implements Initializable {

	@FXML
	ImageView windowView;
	
	public void setContext(double windowWidth, double windowHeight) {
		// TODO Auto-generated method stub
		windowView.setFitWidth(windowWidth);
		windowView.setFitHeight(windowHeight);
		
	}
	
	public void setImage(Image thisimage) {
		// TODO Auto-generated method stub
		windowView.setImage(thisimage);
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
