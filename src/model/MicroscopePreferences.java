package model;

import java.io.Serializable;

public class MicroscopePreferences implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private double pixel_value;
		private double fps_value;
		
		public MicroscopePreferences(double p_value, double f_value){
			this.setPixel_value(p_value);
			this.setFps_value(f_value);
		}

		public double getPixel_value() {
			return pixel_value;
		}

		public void setPixel_value(double pixel_value) {
			this.pixel_value = pixel_value;
		}

		public double getFps_value() {
			return fps_value;
		}

		public void setFps_value(double fps_value) {
			this.fps_value = fps_value;
		}
}
