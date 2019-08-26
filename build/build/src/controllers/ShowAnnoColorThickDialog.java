package controllers;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import model.XYCircleAnnotation;

public class ShowAnnoColorThickDialog {
	
	public static void redrawAnnotations(PackageData mainpack, XYPlot plot2, List<Integer> valid_maximum_list, List<Integer> valid_minimum_list,List<Integer> first_points, List<Integer> fifth_points, int zoomMinValue) {
    	XYDataset dataset2 = plot2.getDataset();
        plot2.clearAnnotations();
		if (valid_maximum_list.size() + valid_minimum_list.size() < 1500 && mainpack.getPlot_preferences().isDrawAnnotations() == true) {
			System.out.println("Draw second!");
	        for(int x1 = 0; x1 < dataset2.getItemCount(0); x1++){
	        	double x = dataset2.getXValue(0, x1);
	        	double y = dataset2.getYValue(0, x1);
	        	if (valid_maximum_list.contains(x1 + zoomMinValue)) {
	        		plot2.addAnnotation(new XYCircleAnnotation(x, y, 5.0, mainpack.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (valid_minimum_list.contains(x1 + zoomMinValue)) {
	        		plot2.addAnnotation(new XYCircleAnnotation(x, y, 5.0, mainpack.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1 + zoomMinValue) && !valid_minimum_list.contains(x1 + zoomMinValue)) {
	        		plot2.addAnnotation(new XYCircleAnnotation(x, y, 5.0, mainpack.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1 + zoomMinValue) && !valid_minimum_list.contains(x1 + zoomMinValue)  && !first_points.contains(x1 + zoomMinValue)) {
	        		plot2.addAnnotation(new XYCircleAnnotation(x, y, 5.0, mainpack.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
	}
	
	public static void showDialog(PackageData mainpack, XYPlot plot, List<Integer> valid_maximum_list, List<Integer> valid_minimum_list,List<Integer> first_points, List<Integer> fifth_points, int zoomMinValue) {
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
		
		java.awt.Color initialColorStart = mainpack.getPlot_preferences().getFirstDotColorRGB();
		javafx.scene.paint.Color initialColorStartfx = new javafx.scene.paint.Color( (float) initialColorStart.getRed() / 255, (float) initialColorStart.getGreen() / 255, (float) initialColorStart.getBlue() / 255, (float) initialColorStart.getAlpha() / 255);
		
		java.awt.Color initialColorMax = mainpack.getPlot_preferences().getMaximumDotColorRGB();
		javafx.scene.paint.Color initialColorMaxfx = new javafx.scene.paint.Color( (float) initialColorMax.getRed() / 255, (float) initialColorMax.getGreen() / 255, (float) initialColorMax.getBlue() / 255, (float) initialColorMax.getAlpha() / 255);
		
		java.awt.Color initialColorMin = mainpack.getPlot_preferences().getMinimumDotColorRGB();
		javafx.scene.paint.Color initialColorMinfx = new javafx.scene.paint.Color( (float) initialColorMin.getRed() / 255, (float) initialColorMin.getGreen() / 255, (float) initialColorMin.getBlue() / 255, (float) initialColorMin.getAlpha() / 255);
		
		java.awt.Color initialColorEnd = mainpack.getPlot_preferences().getLastDotColorRGB();
		javafx.scene.paint.Color initialColorEndfx = new javafx.scene.paint.Color( (float) initialColorEnd.getRed() / 255, (float) initialColorEnd.getGreen() / 255, (float) initialColorEnd.getBlue() / 255, (float) initialColorEnd.getAlpha() / 255);
		
		
		
		
		
		float thickness = mainpack.getPlot_preferences().getLineThickness();
		boolean showan = mainpack.getPlot_preferences().isDrawAnnotations();
		
		//create new dialog
		Stage dialogSaved = new Stage();
    	dialogSaved.initModality(Modality.APPLICATION_MODAL);
    	dialogSaved.initOwner(null);
    	dialogSaved.setResizable(true);
    	dialogSaved.setTitle("Change Plot Configs");
    	
    	//config grid
    	GridPane grid = new GridPane();
    	grid.setPrefWidth(330);
    	grid.setPrefHeight(250);
    	
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
    	
    	
//    	Label askCheck = new Label("Show Peak Annotations?");
    	CheckBox newCheck = new CheckBox("Show Peak Annotations?");
//    	newCheck.setMaxWidth(150);
    	newCheck.setSelected(showan);
    	
    	Label askSColor = new Label("Peak Start Color:");
    	ColorPicker colorpicks = new ColorPicker(initialColorStartfx);
    	colorpicks.setMaxWidth(90);
    	
    	Label askMaxColor = new Label("Peak Max Color:");
    	ColorPicker colorpickmax = new ColorPicker(initialColorMaxfx);
    	colorpickmax.setMaxWidth(90);
    	
    	Label askMinColor = new Label("Peak Min Color:");
    	ColorPicker colorpickmin= new ColorPicker(initialColorMinfx);
    	colorpickmin.setMaxWidth(90);
    	
    	Label askEndColor = new Label("Peak End Color:");
    	ColorPicker colorpickend= new ColorPicker(initialColorEndfx);
    	colorpickend.setMaxWidth(90);
    	
    	
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
	    
	    grid.add(askSColor , 0, 2, 1, 1);
	    GridPane.setHalignment(askSColor , HPos.CENTER);
	    GridPane.setHgrow(askSColor , Priority.ALWAYS);
	    GridPane.setVgrow(askSColor , Priority.ALWAYS);
	    
	    grid.add(colorpicks , 1, 2, 1, 1);
	    GridPane.setHalignment(colorpicks , HPos.LEFT);
	    GridPane.setHgrow(colorpicks , Priority.ALWAYS);
	    GridPane.setVgrow(colorpicks , Priority.ALWAYS);
	    
	    grid.add(askMaxColor , 0, 3, 1, 1);
	    GridPane.setHalignment(askMaxColor , HPos.CENTER);
	    GridPane.setHgrow(askMaxColor , Priority.ALWAYS);
	    GridPane.setVgrow(askMaxColor , Priority.ALWAYS);
	    
	    grid.add(colorpickmax , 1, 3, 1, 1);
	    GridPane.setHalignment(colorpickmax , HPos.LEFT);
	    GridPane.setHgrow(colorpickmax , Priority.ALWAYS);
	    GridPane.setVgrow(colorpickmax , Priority.ALWAYS);
    	
	    grid.add(askMinColor , 0, 4, 1, 1);
	    GridPane.setHalignment(askMinColor , HPos.CENTER);
	    GridPane.setHgrow(askMinColor , Priority.ALWAYS);
	    GridPane.setVgrow(askMinColor , Priority.ALWAYS);
	    
	    grid.add(colorpickmin , 1, 4, 1, 1);
	    GridPane.setHalignment(colorpickmin , HPos.LEFT);
	    GridPane.setHgrow(colorpickmin , Priority.ALWAYS);
	    GridPane.setVgrow(colorpickmin , Priority.ALWAYS);
	    
	    grid.add(askEndColor , 0, 5, 1, 1);
	    GridPane.setHalignment(askEndColor , HPos.CENTER);
	    GridPane.setHgrow(askEndColor , Priority.ALWAYS);
	    GridPane.setVgrow(askEndColor , Priority.ALWAYS);
	    
	    grid.add(colorpickend , 1, 5, 1, 1);
	    GridPane.setHalignment(colorpickend , HPos.LEFT);
	    GridPane.setHgrow(colorpickend , Priority.ALWAYS);
	    GridPane.setVgrow(colorpickend , Priority.ALWAYS);
	    
	    grid.add(newCheck, 0, 6, 2, 1);
	    GridPane.setHalignment(newCheck, HPos.CENTER);
	    GridPane.setHgrow(newCheck, Priority.ALWAYS);
	    GridPane.setVgrow(newCheck, Priority.ALWAYS);
	    
	    grid.add(buttonTypeOk, 0, 7, 2, 1);
	    GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
//	    GridPane.setValignment(buttonTypeOk, VPos.CENTER);
	    GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
	    GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
	    
	    buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	    	public void handle(ActionEvent event) {
	    		javafx.scene.paint.Color newcolorfx = colorpick.getValue();
	    		java.awt.Color newcolor = new java.awt.Color( (float) newcolorfx.getRed(), (float) newcolorfx.getGreen(), (float) newcolorfx.getBlue(), (float) newcolorfx.getOpacity());
	    		
	    		
	    		javafx.scene.paint.Color newcolorfxst = colorpicks.getValue();
	    		java.awt.Color newcolorst = new java.awt.Color( (float) newcolorfxst.getRed(), (float) newcolorfxst.getGreen(), (float) newcolorfxst.getBlue(), (float) newcolorfxst.getOpacity());
	    		javafx.scene.paint.Color newcolorfxmax = colorpickmax.getValue();
	    		java.awt.Color newcolormax = new java.awt.Color( (float) newcolorfxmax.getRed(), (float) newcolorfxmax.getGreen(), (float) newcolorfxmax.getBlue(), (float) newcolorfxmax.getOpacity());
	    		javafx.scene.paint.Color newcolorfxmin = colorpickmin.getValue();
	    		java.awt.Color newcolormin = new java.awt.Color( (float) newcolorfxmin.getRed(), (float) newcolorfxmin.getGreen(), (float) newcolorfxmin.getBlue(), (float) newcolorfxmin.getOpacity());
	    		javafx.scene.paint.Color newcolorfxend = colorpickend.getValue();
	    		java.awt.Color newcolorend = new java.awt.Color( (float) newcolorfxend.getRed(), (float) newcolorfxend.getGreen(), (float) newcolorfxend.getBlue(), (float) newcolorfxend.getOpacity());
	    		
	    		
	    		mainpack.getPlot_preferences().setSeriesColorRGB(newcolor);
	    		
	    		mainpack.getPlot_preferences().setFirstDotColorRGB(newcolorst);
	    		mainpack.getPlot_preferences().setMaximumDotColorRGB(newcolormax);
	    		mainpack.getPlot_preferences().setMinimumDotColorRGB(newcolormin);
	    		mainpack.getPlot_preferences().setLastDotColorRGB(newcolorend);
	    		mainpack.getPlot_preferences().setDrawAnnotations(newCheck.isSelected());
	    		
	    		
	    		float new_thickness = inputWidthFormatter.getValue();
	    		mainpack.getPlot_preferences().setLineThickness(new_thickness);
	    		dialogSaved.close();
	    		plot.getRenderer().setSeriesPaint(0, newcolor);
	        	plot.getRenderer().setSeriesStroke(0, new java.awt.BasicStroke(new_thickness));
	        	ShowAnnoColorThickDialog.redrawAnnotations(mainpack, plot, valid_maximum_list, valid_minimum_list, first_points, fifth_points, zoomMinValue);
	        }
	    });
	    dialogSaved.setScene(new Scene(grid));
	    dialogSaved.show();
	}
}
