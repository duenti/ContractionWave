package controllers;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacv.Java2DFrameUtils;

import edu.mines.jtk.awt.ColorMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Controller_Scaleold implements Initializable{
	
	private double mask_value;
	private double scale_start;
	private double scale_end;
	private int width = 30;
	private int height = 450;
	private double spacing = 10;
	private int font_size = 15;
	public static final IndexColorModel JET = ColorMap.getJet();
	private ColorMap this_jet;
	
//	@FXML
//	private Canvas testcanvas;
	@FXML
	private TextField heightInput, widthInput, spacingInput, fontInput;
	
	@FXML
	private CheckBox tickCheck;
	
	@FXML
	private Button btnScale;
	
	private static Path rootDir; // The chosen root or source directory
	private static final String DEFAULT_DIRECTORY =
            System.getProperty("user.dir"); //  or "user.home"
	
	private static Path getInitialDirectory() {
        return (rootDir == null) ? Paths.get(DEFAULT_DIRECTORY) : rootDir;
    }
	
	@FXML
	void handleGenScale(ActionEvent event) {
		//Ask for directory
		DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Saving path selection:");
        chooser.setInitialDirectory(getInitialDirectory().toFile());
    	Stage primaryStage;
    	primaryStage = (Stage) tickCheck.getScene().getWindow();
        File chosenDir = chooser.showDialog(primaryStage);
    	String current_filename = chosenDir.getAbsolutePath().toString() + "/" + "scale" +".tiff";
        BufferedImage bImage1 = generateCanvasScale();
    	Mat jetLayer = Java2DFrameUtils.toMat(bImage1);
        MatVector channels = new MatVector();
        Mat jetLayerRGB = new Mat(height+((height * 0.1 )+50), width+(width/2+80), org.bytedeco.javacpp.opencv_core.CV_8UC3);
        org.bytedeco.javacpp.opencv_core.split(jetLayer, channels);
        Mat blueCh = channels.get(1);
        Mat greenCh = channels.get(2);
        Mat redCh = channels.get(3);
        MatVector channels2 = new MatVector(3);
        channels2.put(0, redCh);
        channels2.put(1, greenCh);
        channels2.put(2, blueCh);
        org.bytedeco.javacpp.opencv_core.merge(channels2, jetLayerRGB);
		imwrite(current_filename, jetLayerRGB);		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		heightInput.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	heightInput.setText(newValue.replaceAll("[^\\d]", ""));
		        }
				height = Integer.valueOf(newValue);			        	
				if (height < 80) {
					height = 80;
					heightInput.setText(String.valueOf(height));
				}	        	
		    }
		});
		
		widthInput.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	widthInput.setText(newValue.replaceAll("[^\\d]", ""));
		        }
				width = Integer.valueOf(newValue);		        	
				if (width < 30) {
					width = 30;
					widthInput.setText(String.valueOf(width));
				}
		    }
		});
		
		spacingInput.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	spacingInput.setText(newValue.replaceAll("[^\\d]", ""));
		        }
	        	if (spacing < height && spacing < width) {
	        		spacing = Double.valueOf(newValue);
	        	}
		    }
		});
		
		fontInput.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	fontInput.setText(newValue.replaceAll("[^\\d]", ""));
		        }
	        	if (font_size < height && font_size < width) {
	        		font_size = Integer.valueOf(newValue);
	        	}
		    }
		});
		
	}
	
	public void setContext(double mask_value1, double scale_start1, double scale_end1) {
		System.out.println("About to gen test scale 1");
		mask_value = mask_value1;
		scale_start = scale_start1;
		scale_end = scale_end1;
		this_jet = new ColorMap(scale_start, scale_end, JET);
	}
	
	private double convertScaleToHeight(double y) {
		double a = (scale_end-scale_start) / height;
		double b = scale_start;
		return ((y-b)/a);
	}
	
	private double convertToMag(double x) {
		double a = (scale_end-scale_start) / height;
		double b = scale_start;
		return ((x*a)+b);
	}
	
	public static String rightPadZeros(String str, int num) {
		return String.format("%1$-" + num + "s", str).replace(' ', '0');
	}
	
	public static double round(double d, int decimalPlace) {
	    BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	private final int ARR_SIZE = 4;
	
	void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
	    gc.setFill(Color.BLACK);
	    gc.setLineWidth(2.0);

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx * dx + dy * dy);

	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));
	    gc.strokeLine(0, 0, len, 0);
	    gc.strokeLine(len, 0, len - ARR_SIZE, -ARR_SIZE);
	    gc.strokeLine(len, 0, len - ARR_SIZE, ARR_SIZE);
//	    gc.strokeLine(len, 0, len + ARR_SIZE, ARR_SIZE);
//	    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);
	}
	
	private BufferedImage generateCanvasScale() {
		Canvas canvas1 = new Canvas((int) width+(width/2+80), (int) height+((height * 0.1 )+50));
		GraphicsContext gc = canvas1.getGraphicsContext2D();
    	gc.setFill(Color.WHITE);
    	gc.fillRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
		gc.save();
		PixelWriter pixel_writing = canvas1.getGraphicsContext2D().getPixelWriter();
		

//        int tick_number = (int) (scale_end / spacing);
		for (int x = 0; x < width; x++) {
			for (int y = 20; y < height+20; y++) {
				int y_new = y-20;
        		java.awt.Color a = this_jet.getColor(convertToMag(y_new));
//				java.awt.Color a = this_jet.getColor(convertToMag(y));
        		Color this_color = new Color( (Double.valueOf(a.getRed()) / 255.0), (Double.valueOf(a.getGreen()) / 255.0 ) , (Double.valueOf(a.getBlue()) / 255.0), 1.0);
//				pixel_writing.setColor(x, y, this_color);
				pixel_writing.setColor( x, ((int)canvas1.getHeight()-30-y), this_color);
				
			}
		}
		//write tick labels
		if (tickCheck.isSelected() == true) {
			gc.setFill(Color.BLACK);
			gc.setTextAlign(TextAlignment.CENTER);
	        gc.setTextBaseline(VPos.CENTER);
	        javafx.scene.text.Font number_font = javafx.scene.text.Font.font("Arial",FontWeight.BOLD, font_size);
	        
	        gc.setFont(number_font);

	        int tick_number = (int) (scale_end / spacing);
	        System.out.println(tick_number);
	        if (tick_number * spacing < scale_end) {
	        	tick_number++;
	        }
	        
			for (int t_i = 1; t_i < tick_number; t_i++) {
				int x = width+20; //tick width to write
				int y = ((int) convertScaleToHeight((t_i) * spacing)) + 20;
				//normal
//				gc.fillText(String.valueOf((t_i) * spacing), x, y);
				//inverse
				int currentpad = 5;
				
				String current_print_a = String.valueOf((t_i) * spacing);
				if (!current_print_a.contains(".")) {
					current_print_a += ".";
				} else {
					int dot_after = current_print_a.split("\\.")[0].length();
					currentpad = currentpad - dot_after;
				}
				current_print_a = rightPadZeros(current_print_a, currentpad);
				gc.fillText(current_print_a, x, ((int)canvas1.getHeight()-30-y) );
			}
	        
	        //inverse
//			for (int t_i = tick_number; t_i >= 1; t_i--) {
//				int x = width+20; //tick width to write
//				int y = ((int) convertScaleToHeight((t_i) * spacing)) + 20;
//				gc.fillText(String.valueOf((t_i) * spacing), x, y);
//			}
			int x = width+20; //tick width to write
			int y = ((int) convertScaleToHeight(scale_end)) + 20;
//			gc.fillText(String.valueOf(scale_end), x, y);
			int currentpad_f = 5;
			double current_print_f_num = round(scale_end,2);
			int current_print_f_num_int = (int) current_print_f_num;
			double delta_to_f =  (double) current_print_f_num_int;
			String current_print_f = String.valueOf(current_print_f_num);
			if (current_print_f_num - delta_to_f == 0) {
				current_print_f = current_print_f.split(Pattern.quote("."))[0];
			}
			
			int currentpad_s = 5;
			double current_print_s_num = round(scale_start,2);
			int current_print_s_num_int = (int) current_print_s_num;
			double delta_to_s =  (double) current_print_s_num_int;
			String current_print_s = String.valueOf(round(scale_start,2));
			if (current_print_s_num - delta_to_s == 0) {
				current_print_s = current_print_s.split(Pattern.quote("."))[0];
			}
			
			gc.fillText(current_print_f, x, ((int)canvas1.getHeight()-30-y));
			y = ((int) convertScaleToHeight(scale_start)) + 20;
			gc.fillText(current_print_s, x, ((int)canvas1.getHeight()-30-y));
//			
		}
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        javafx.scene.text.Font subtitle_font = javafx.scene.text.Font.font("Arial",FontWeight.BOLD, 15);
        gc.setFont(subtitle_font);
	    Transform transform = Transform.rotate(-90.0, (int)canvas1.getWidth()-35, (int)(canvas1.getHeight()/2));
	    gc.setTransform(new Affine(transform));
	    gc.fillText("Speed (\u00B5m/s)", (int)canvas1.getWidth()-35, (int)(canvas1.getHeight()/2));
	    gc.restore();
	    
//	    int x_arrow = (int)canvas1.getWidth()-45;
	    int x_arrow = (int)canvas1.getWidth()-20;
//    	int y_arrow_init = (int)(canvas1.getHeight()/2) - (height/10);
//    	int y_arrow_end = (int)(canvas1.getHeight()/2) + (height/10);
//	    if (y_arrow_end - y_arrow_init  < 30) {
//	    	y_arrow_init = (int)(canvas1.getHeight()/2) - 15;
//	    	y_arrow_end = (int)(canvas1.getHeight()/2) + 15;
//	    }
	    int y_arrow_init = (int)(canvas1.getHeight()/2) - 46;
	    int y_arrow_end = (int)(canvas1.getHeight()/2) + 46;
	    
	    
	    
//		drawArrow(gc, x_arrow, y_arrow_init, x_arrow, y_arrow_end);
		drawArrow(gc, x_arrow, y_arrow_end, x_arrow, y_arrow_init);
		
    	WritableImage writableImage = new WritableImage((int) canvas1.getWidth(), (int)canvas1.getHeight());
    	SnapshotParameters sp = new SnapshotParameters();
    	sp.setFill(Color.TRANSPARENT);
        canvas1.snapshot(sp, writableImage);
        BufferedImage final_scale = SwingFXUtils.fromFXImage(writableImage, null);
        return final_scale;
	}

}