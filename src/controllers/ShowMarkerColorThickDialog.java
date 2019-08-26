package controllers;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.jfree.chart.plot.Marker;
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

public class ShowMarkerColorThickDialog {
	public static void showDialog(PackageData mainpack, XYPlot plot, Marker current_marker) {
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

		java.awt.Color initialColorMalp = mainpack.getPlot_preferences().getMarkerColorRGB();
		javafx.scene.paint.Color initialColorFXmAlp = new javafx.scene.paint.Color( (float) initialColorMalp.getRed() / 255, (float) initialColorMalp.getGreen() / 255, (float) initialColorMalp.getBlue() / 255, (float) initialColorMalp.getAlpha() / 255);
		
		float thickness = mainpack.getPlot_preferences().getLineThickness();
		float malpha = mainpack.getPlot_preferences().getMarkerAlpha();
		
		//create new dialog
		Stage dialogSaved = new Stage();
    	dialogSaved.initModality(Modality.APPLICATION_MODAL);
    	dialogSaved.initOwner(null);
    	dialogSaved.setResizable(true);
    	dialogSaved.setTitle("Change Plot Configs");
    	
    	//config grid
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(300);
    	grid.setPrefHeight(140);
    	
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
    	
    	
    	Label askMAlpha = new Label("Marker Alpha:");
    	TextFormatter<Float> inputMAlphaFormatter = new TextFormatter<>(converter, malpha, filter);
    	TextField inputMAlpha = new TextField();
    	inputMAlpha.setMaxWidth(90);
    	inputMAlpha.setTextFormatter(inputMAlphaFormatter);
    	inputMAlphaFormatter.setValue(malpha);
    	
    	Label askMColor = new Label("Marker Color:");
    	ColorPicker colorpickm = new ColorPicker(initialColorFXmAlp);
    	colorpickm.setMaxWidth(90);
    	
    	
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
	    
	    grid.add(askMAlpha, 0, 2, 1, 1);
	    GridPane.setHalignment(askMAlpha, HPos.CENTER);
	    GridPane.setHgrow(askMAlpha, Priority.ALWAYS);
	    GridPane.setVgrow(askMAlpha, Priority.ALWAYS);
	    
	    grid.add(inputMAlpha, 1, 2, 1, 1);
	    GridPane.setHalignment(inputMAlpha, HPos.LEFT);
	    GridPane.setHgrow(inputMAlpha, Priority.ALWAYS);
	    GridPane.setVgrow(inputMAlpha, Priority.ALWAYS);
	    
	    grid.add(askMColor , 0, 3, 1, 1);
	    GridPane.setHalignment(askMColor , HPos.CENTER);
	    GridPane.setHgrow(askMColor , Priority.ALWAYS);
	    GridPane.setVgrow(askMColor , Priority.ALWAYS);
	    
	    grid.add(colorpickm , 1, 3, 1, 1);
	    GridPane.setHalignment(colorpickm , HPos.LEFT);
	    GridPane.setHgrow(colorpickm , Priority.ALWAYS);
	    GridPane.setVgrow(colorpickm , Priority.ALWAYS);
	    
	    grid.add(buttonTypeOk, 0, 4, 2, 1);
	    GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
//	    GridPane.setValignment(buttonTypeOk, VPos.CENTER);
	    GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
	    GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
	    
	    buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	    	public void handle(ActionEvent event) {
	    		javafx.scene.paint.Color newcolorfx = colorpick.getValue();
	    		java.awt.Color newcolor = new java.awt.Color( (float) newcolorfx.getRed(), (float) newcolorfx.getGreen(), (float) newcolorfx.getBlue(), (float) newcolorfx.getOpacity());
	    		

	    		javafx.scene.paint.Color newcolorfxm = colorpickm.getValue();
	    		java.awt.Color newcolorm = new java.awt.Color( (float) newcolorfxm.getRed(), (float) newcolorfxm.getGreen(), (float) newcolorfxm.getBlue(), (float) newcolorfxm.getOpacity());
	    		
	    		mainpack.getPlot_preferences().setSeriesColorRGB(newcolor);
	    		mainpack.getPlot_preferences().setMarkerColorRGB(newcolorm);
	    		
	    		float new_thickness = inputWidthFormatter.getValue();
	    		float new_alpha = inputMAlphaFormatter.getValue();
	    		mainpack.getPlot_preferences().setLineThickness(new_thickness);
	    		mainpack.getPlot_preferences().setMarkerAlpha(new_alpha);
	    		dialogSaved.close();
	    		plot.getRenderer().setSeriesPaint(0, newcolor);
	        	plot.getRenderer().setSeriesStroke(0, new java.awt.BasicStroke(new_thickness));
	            current_marker.setPaint(newcolorm);
		        current_marker.setAlpha(new_alpha);
		        
	        }
	    });
	    dialogSaved.setScene(new Scene(grid));
	    dialogSaved.show();
	}
}
