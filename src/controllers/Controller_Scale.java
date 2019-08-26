package controllers;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

import org.apache.poi.common.usermodel.fonts.FontFamily;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Controller_Scale implements Initializable{
	
	private double scale_start;
	private double scale_end;
	private int width = 120;
	private int plot_width = 30;
	private int height = 450;
	private double spacing = 10;
	private int font_size = 15;
	private int tick_number = 0;
	public static final IndexColorModel JET = ColorMap.getJet();
	private ColorMap this_jet;
	
	@FXML
	private TextField heightInput, widthInput, spacingInput;
	
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
        Mat jetLayerRGB = new Mat(height, width, org.bytedeco.javacpp.opencv_core.CV_8UC3);
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
		ShowSavedDialog.showDialog();
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
		        int prev_width = 0 + plot_width;
		        plot_width = Integer.valueOf(newValue);		        	
				if (plot_width < 30) {
					plot_width = 30;
					widthInput.setText(String.valueOf(plot_width));
				}
				width = ((width - prev_width) + plot_width);
		    }
		});
		
		spacingInput.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	spacingInput.setText(newValue.replaceAll("[^\\d]", ""));
		        }
	        	if (spacing < scale_end && spacing > 0) {
	        		spacing = Double.valueOf(newValue);
	        	}
				int spacing_int = (int) spacing;
				double spacing_delta =  (double) spacing_int;
				if (spacing - spacing_delta != 0) {
					//round spacing max to two numbers
				}
				calcTickNumber();
				//get maximum spacing number, calculate its maximum font size to fit 50 pixel font box
				calcTickMaxFontSize();
		    }
		});
		//get maximum spacing number, calculate its maximum font size to fit 50 pixel font box
	}
	
	private List<Double> tickList = new ArrayList<Double>();
			
	public void calcTickNumber() {
		tickList.clear();
		tick_number = 0;
		for (double i = scale_start; i <= scale_end; i=i+spacing) {
			tickList.add(i);
			tick_number++;
		}
		if (!tickList.contains(scale_end)) {
			tickList.add(scale_end);
			tick_number++;
		}
	}
	
	
	private int font_height = 0;
	private int font_width = 0;
	
	public void calcTickMaxFontSize() {
		int max_tick_length = 0;
		String max_tick = "";
		for (double tick : tickList) {
			int tick_string_len = String.valueOf(tick).length();
			if (max_tick_length  < tick_string_len) {
				max_tick_length = tick_string_len;
				max_tick = String.valueOf(tick);
			}
		}
		Text theText = new Text(max_tick);
		theText.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, font_size));
		double text_width = theText.getBoundsInLocal().getWidth();
		double text_total_height = theText.getBoundsInLocal().getHeight() * tick_number;
		boolean fit_text = ((text_width < 50) && (text_total_height < height));
		while (fit_text != true && font_size >= 6) {
			font_size = font_size - 1;
			theText.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, font_size));
			text_width = theText.getBoundsInLocal().getWidth();
			text_total_height = theText.getBoundsInLocal().getHeight() * tick_number;
			fit_text = ((text_width < 50) && (text_total_height < height));
		}
		font_height = (int) theText.getBoundsInLocal().getHeight();
		font_width = (int) theText.getBoundsInLocal().getWidth();
	}
	
	public void setContext(double mask_value1, double scale_start1, double scale_end1) {
		System.out.println("About to gen test scale");
		scale_start = scale_start1;
		scale_end = scale_end1;
		this_jet = new ColorMap(scale_start, scale_end, JET);
		calcTickNumber();
		calcTickMaxFontSize();
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
	}
	
	private BufferedImage generateCanvasScale() {
		Canvas canvas1 = new Canvas(width, height); //create canvas
		GraphicsContext gc = canvas1.getGraphicsContext2D();
    	gc.setFill(Color.WHITE);
    	gc.fillRect(0, 0, canvas1.getWidth(), canvas1.getHeight()); //fill canvas with white rectangle
		gc.save();
		PixelWriter pixel_writing = canvas1.getGraphicsContext2D().getPixelWriter(); //start pixelwriter
		for (int x = 0; x <= plot_width; x++) {
			for (int y = 0; y <= height; y++) {
				java.awt.Color a = this_jet.getColor(convertToMag(y)); //get color for each dot
        		Color this_color = new Color( (Double.valueOf(a.getRed()) / 255.0), (Double.valueOf(a.getGreen()) / 255.0 ) , (Double.valueOf(a.getBlue()) / 255.0), 1.0);
				pixel_writing.setColor( x, ((int)canvas1.getHeight()-y), this_color); //print color to canvas
			}
		}

		 //tick width to write
		
		if (tickCheck.isSelected() == true) { //if ticks enabled, prepare to paint ticks
			System.out.println("About to print each tick");
			gc.setFill(Color.BLACK);
			gc.setTextAlign(TextAlignment.CENTER);
	        gc.setTextBaseline(VPos.CENTER);
	        javafx.scene.text.Font number_font = javafx.scene.text.Font.font("Arial",FontWeight.BOLD, font_size);
	        gc.setFont(number_font);
	        //graphic context settings above
	        int x = plot_width+2;
	        System.out.println("Tick number: " + tick_number);
			for (int e_tick_n = 1; e_tick_n < tick_number-1; e_tick_n++) {
				int y = ( (int) convertScaleToHeight(tickList.get(e_tick_n)) );
				String current_print_a = String.valueOf((e_tick_n) * spacing);
				
				double current_print_a_num = round(e_tick_n,2); //generate scale end
				int current_print_a_num_int = (int) current_print_a_num;
				double delta_to_a =  (double) current_print_a_num_int;
				if (current_print_a_num - delta_to_a == 0) {
					current_print_a = current_print_a.split(Pattern.quote("."))[0];
				}
				
				Text theText = new Text(current_print_a);
				theText.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, font_size));
				int font_current_width = (int) theText.getBoundsInLocal().getWidth();
				gc.fillText(current_print_a, x+(font_current_width/2), ((int)canvas1.getHeight()-y) ); //write each tick
			}
		}
		
		int x = plot_width+1;
		double current_print_f_num = round(scale_end,2); //generate scale end
		int current_print_f_num_int = (int) current_print_f_num;
		double delta_to_f =  (double) current_print_f_num_int;
		String current_print_f = String.valueOf(current_print_f_num);
		if (current_print_f_num - delta_to_f == 0) {
			current_print_f = current_print_f.split(Pattern.quote("."))[0];
		}
		
		double current_print_s_num = round(scale_start,2); //generate scale start
		int current_print_s_num_int = (int) current_print_s_num;
		double delta_to_s =  (double) current_print_s_num_int;
		String current_print_s = String.valueOf(round(scale_start,2));
		if (current_print_s_num - delta_to_s == 0) {
			current_print_s = current_print_s.split(Pattern.quote("."))[0];
		}
		int y = ((int) convertScaleToHeight(scale_end));
		
		Text theText = new Text(current_print_f);
		theText.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, font_size));
		int font_current_width = (int) theText.getBoundsInLocal().getWidth();
				
		gc.fillText(current_print_f, x+(font_current_width/2), (font_height/2)  ); //draw scale end

		
		Text theText2 = new Text(current_print_f);
		theText2.setFont(javafx.scene.text.Font.font("Arial", FontWeight.BOLD, font_size));
		font_current_width = (int) theText2.getBoundsInLocal().getWidth();

		gc.fillText(current_print_s, x+(font_current_width/2), ( (int)canvas1.getHeight() - (font_height/2) )); //draw scale start
		
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        //restore canvas default settings
        
        javafx.scene.text.Font subtitle_font = javafx.scene.text.Font.font("Arial",FontWeight.BOLD, 15);
        gc.setFont(subtitle_font);
	    Transform transform = Transform.rotate(-90.0, (int)canvas1.getWidth()-35, (int)(canvas1.getHeight()/2));
	    gc.setTransform(new Affine(transform));
	    gc.fillText("Speed (\u00B5m/s)", (int)canvas1.getWidth()-35, (int)(canvas1.getHeight()/2));
	    gc.restore();
	    
	    int x_arrow = (int)canvas1.getWidth()-20;
	    int y_arrow_init = (int)(canvas1.getHeight()/2) - 30;
	    int y_arrow_end = (int)(canvas1.getHeight()/2) + 30;
		drawArrow(gc, x_arrow, y_arrow_end, x_arrow, y_arrow_init);
		
    	WritableImage writableImage = new WritableImage((int) canvas1.getWidth(), (int)canvas1.getHeight());
    	SnapshotParameters sp = new SnapshotParameters();
    	sp.setFill(Color.TRANSPARENT);
        canvas1.snapshot(sp, writableImage);
        BufferedImage final_scale = SwingFXUtils.fromFXImage(writableImage, null);
        return final_scale;
	}

}