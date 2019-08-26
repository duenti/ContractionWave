package controllers;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class MouseWheelChartPanel  extends ChartPanel implements MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private JComponent thiscomp;
	
		public MouseWheelChartPanel(JComponent chart, JFreeChart chart2) {
	    	super(chart2);
//	    	JComponent chart = chart2.com;
			thiscomp = chart;
		}
	    
	    @Override
		public void mouseWheelMoved(MouseWheelEvent e) {
	        if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL) return;
	        if (e.getWheelRotation()< 0) increaseZoom(this.thiscomp, true);
	        else        	               decreaseZoom(this.thiscomp, true);
	    }
	    
	    public synchronized void increaseZoom(JComponent chart, boolean saveAction){
	        ChartPanel ch = (ChartPanel)chart;
	        zoomChartAxis(ch, true);
	    }  
	    
	    public synchronized void decreaseZoom(JComponent chart, boolean saveAction){
	        ChartPanel ch = (ChartPanel)chart;
	        zoomChartAxis(ch, false);
	    }  
	    
	    private void zoomChartAxis(ChartPanel chartP, boolean increase){    	    	
	        int width = chartP.getMaximumDrawWidth() - chartP.getMinimumDrawWidth();
	        int height = chartP.getMaximumDrawHeight() - chartP.getMinimumDrawWidth();        
	        if(increase){
	        	chartP.zoomInBoth(width/2, height/2);
	        }else{
	        	chartP.zoomOutBoth(width/2, height/2);
	        }

	    }

}
