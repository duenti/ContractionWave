package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

public class XYCircleAnnotation extends AbstractXYAnnotation implements XYAnnotation {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
    private double y;
    private double radius;
    private Color color;
    
    public XYCircleAnnotation(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }
    

    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);

        double xx = domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        double yy = rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);

        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = yy;
            yy = xx;
        }
        g2.setPaint(color);
        g2.draw(new Ellipse2D.Double(xx - radius, yy - radius, 
                radius * 2.0, radius * 2.0));
    }
}