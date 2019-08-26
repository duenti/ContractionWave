package controllers;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.jfree.chart.plot.XYPlot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.PackageData;

public class ShowColorThickDialog {
	public static void showDialog(PackageData mainpack, XYPlot plot) {
		//create general helper objects
		Pattern validEditingState = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

		UnaryOperator<TextFormatter.Change> filter = c -> {
		    String text = c.getControlNewText();
		    if (validEditingState.matcher(text).matches()) {
		        return c ;
		    } else {
		        return null ;
		    }
		};

		StringConverter<Float> converter = new StringConverter<Float>() {
		    @Override
		    public Float fromString(String s) {
		        if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
		            return 0.0f ;
		        } else {
		            return Float.valueOf(s);
		        }
		    }

		    @Override
		    public String toString(Float d) {
		        return d.toString();
		    }
		};
		
		//get and convert data
		java.awt.Color initialColor = mainpack.getPlot_preferences().getSeriesColorRGB();
		javafx.scene.paint.Color initialColorFX = new javafx.scene.paint.Color( (float) initialColor.getRed() / 255, (float) initialColor.getGreen() / 255, (float) initialColor.getBlue() / 255, (float) initialColor.getAlpha() / 255);
		float thickness = mainpack.getPlot_preferences().getLineThickness();
		
		//create new dialog
		Stage dialogSaved = new Stage();
    	dialogSaved.initModality(Modality.APPLICATION_MODAL);
    	dialogSaved.initOwner(null);
    	dialogSaved.setResizable(true);
    	dialogSaved.setTitle("Change Plot Configs");
    	
    	//config grid
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(300);
    	grid.setPrefHeight(110);
    	
    	//create input objects
//    	Label askQuestion = new Label("Change Plot Configs:");
    	Label askWidth = new Label("Line Width (default 1):");
    	TextFormatter<Float> inputWidthFormatter = new TextFormatter<>(converter, thickness, filter);
    	TextField inputWidth = new TextField();
    	inputWidth.setMaxWidth(90);
    	inputWidth.setTextFormatter(inputWidthFormatter);
    	inputWidthFormatter.setValue(thickness);
    	Label askColor = new Label("Line Color:");
    	ColorPicker colorpick = new ColorPicker(initialColorFX);
    	colorpick.setMaxWidth(90);
    	Button buttonTypeOk = new Button("Ok");
    	
//	    grid.setGridLinesVisible(true);	
//	    grid.add(askQuestion, 0, 0, 2, 1);
//	    GridPane.setHalignment(askQuestion, HPos.CENTER);
//	    GridPane.setValignment(askQuestion, VPos.CENTER);
//	    GridPane.setHgrow(askQuestion, Priority.ALWAYS);
//	    GridPane.setVgrow(askQuestion, Priority.ALWAYS);

	    grid.add(askWidth, 0, 0, 1, 1);
	    GridPane.setHalignment(askWidth, HPos.CENTER);
	    GridPane.setHgrow(askWidth, Priority.ALWAYS);
	    GridPane.setVgrow(askWidth, Priority.ALWAYS);
	    
	    grid.add(inputWidth, 1, 0, 1, 1);
	    GridPane.setHalignment(inputWidth, HPos.LEFT);
	    GridPane.setHgrow(inputWidth, Priority.ALWAYS);
	    GridPane.setVgrow(inputWidth, Priority.ALWAYS);
	    
	    grid.add(askColor , 0, 1, 1, 1);
	    GridPane.setHalignment(askColor , HPos.CENTER);
	    GridPane.setHgrow(askColor , Priority.ALWAYS);
	    GridPane.setVgrow(askColor , Priority.ALWAYS);
	    
	    grid.add(colorpick , 1, 1, 1, 1);
	    GridPane.setHalignment(colorpick , HPos.LEFT);
	    GridPane.setHgrow(colorpick , Priority.ALWAYS);
	    GridPane.setVgrow(colorpick , Priority.ALWAYS);
	    
	    grid.add(buttonTypeOk, 0, 2, 2, 1);
	    GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
//	    GridPane.setValignment(buttonTypeOk, VPos.CENTER);
	    GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
	    GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
	    
	    buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	    	public void handle(ActionEvent event) {
	    		javafx.scene.paint.Color newcolorfx = colorpick.getValue();
	    		java.awt.Color newcolor = new java.awt.Color( (float) newcolorfx.getRed(), (float) newcolorfx.getGreen(), (float) newcolorfx.getBlue(), (float) newcolorfx.getOpacity());
	    		mainpack.getPlot_preferences().setSeriesColorRGB(newcolor);
	    		float new_thickness = inputWidthFormatter.getValue();
	    		mainpack.getPlot_preferences().setLineThickness(new_thickness);
	    		dialogSaved.close();
	    		plot.getRenderer().setSeriesPaint(0, newcolor);
	        	plot.getRenderer().setSeriesStroke(0, new java.awt.BasicStroke(new_thickness));
	        }
	    });
	    dialogSaved.setScene(new Scene(grid));
	    dialogSaved.show();
	}
}
