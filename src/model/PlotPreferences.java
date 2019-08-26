package model;

import java.awt.Color;
import java.io.Serializable;

public class PlotPreferences implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float markerAlpha;
	private Color markerColorRGB;
	private Color seriesColorRGB;
	private Color firstDotColorRGB;
	private Color minimumDotColorRGB;
	private Color maximumDotColorRGB;
	private Color lastDotColorRGB;
	private Color backgroundColorRGB;
	private Color domainGridColorRGB;
	private Color rangeGridColorRGB;
	private boolean gridlineDefaultState;
	private boolean splineDefaultState;
	private float lineThickness;
	private boolean drawAnnotations;
	
	public PlotPreferences(){
		setDrawAnnotations(true);
		lineThickness = 1;
		markerAlpha = 0.8f;
		markerColorRGB = new Color(250,250,230);
		seriesColorRGB = Color.BLACK;
		firstDotColorRGB = Color.BLACK;
		minimumDotColorRGB = Color.BLUE;
		maximumDotColorRGB = Color.RED;
		lastDotColorRGB = Color.MAGENTA;
		backgroundColorRGB = new Color(0xFF, 0xFF, 0xFF);
		domainGridColorRGB = new Color(0x00, 0x00, 0x00);
		rangeGridColorRGB = new Color(0x00, 0x00, 0x00);
		gridlineDefaultState = true;
		setSplineDefaultState(false);
	}
	public float getLineThickness() {
		return lineThickness;
	}
	public void setLineThickness(float new_thickness) {
		lineThickness = new_thickness;
	}
	public float getMarkerAlpha() {
		return markerAlpha;
	}
	public void setMarkerAlpha(float markerAlpha) {
		this.markerAlpha = markerAlpha;
	}
	public Color getMarkerColorRGB() {
		return markerColorRGB;
	}
	public void setMarkerColorRGB(Color markerColorRGB) {
		this.markerColorRGB = markerColorRGB;
	}
	public Color getSeriesColorRGB() {
		return seriesColorRGB;
	}
	public void setSeriesColorRGB(Color seriesColorRGB) {
		this.seriesColorRGB = seriesColorRGB;
	}
	public Color getFirstDotColorRGB() {
		return firstDotColorRGB;
	}
	public void setFirstDotColorRGB(Color firstDotColorRGB) {
		this.firstDotColorRGB = firstDotColorRGB;
	}
	public Color getMinimumDotColorRGB() {
		return minimumDotColorRGB;
	}
	public void setMinimumDotColorRGB(Color minimumDotColorRGB) {
		this.minimumDotColorRGB = minimumDotColorRGB;
	}
	public Color getMaximumDotColorRGB() {
		return maximumDotColorRGB;
	}
	public void setMaximumDotColorRGB(Color maximumDotColorRGB) {
		this.maximumDotColorRGB = maximumDotColorRGB;
	}
	public Color getLastDotColorRGB() {
		return lastDotColorRGB;
	}
	public void setLastDotColorRGB(Color lastDotColorRGB) {
		this.lastDotColorRGB = lastDotColorRGB;
	}
	public Color getBackgroundColorRGB() {
		return backgroundColorRGB;
	}
	public void setBackgroundColorRGB(Color backgroundColorRGB) {
		this.backgroundColorRGB = backgroundColorRGB;
	}
	public Color getDomainGridColorRGB() {
		return domainGridColorRGB;
	}
	public void setDomainGridColorRGB(Color domainGridColorRGB) {
		this.domainGridColorRGB = domainGridColorRGB;
	}
	public Color getRangeGridColorRGB() {
		return rangeGridColorRGB;
	}
	public void setRangeGridColorRGB(Color rangeGridColorRGB) {
		this.rangeGridColorRGB = rangeGridColorRGB;
	}
	public boolean isGridlineDefaultState() {
		return gridlineDefaultState;
	}
	public void setGridlineDefaultState(boolean gridlineDefaultState) {
		this.gridlineDefaultState = gridlineDefaultState;
	}

	public boolean isSplineDefaultState() {
		return splineDefaultState;
	}
	public void setSplineDefaultState(boolean splineDefaultState) {
		this.splineDefaultState = splineDefaultState;
	}
	public boolean isDrawAnnotations() {
		return drawAnnotations;
	}
	public void setDrawAnnotations(boolean drawAnnotations) {
		this.drawAnnotations = drawAnnotations;
	}

}
