package com.pennant.fusioncharts;

public class ColumnAndBarCharts {

	private String caption;
	private String subCaption;

	private String yAxisName;
	private String xAxisName;
	
	private String numberPrefix;
	private String showBorder="0";
	private String imageSave="0";
	private String imageSaveURL;
	
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public String getSubCaption() {
		return subCaption;
	}
	public void setSubCaption(String subCaption) {
		this.subCaption = subCaption;
	}
	
	public String getyAxisName() {
		return yAxisName;
	}
	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}
	
	public String getxAxisName() {
		return xAxisName;
	}
	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}
	
	public String getNumberPrefix() {
		return numberPrefix;
	}
	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}
	
	public String getShowBorder() {
		return showBorder;
	}
	public void setShowBorder(String showBorder) {
		this.showBorder = showBorder;
	}
	
	public String getImageSave() {
		return imageSave;
	}
	public void setImageSave(String imageSave) {
		this.imageSave = imageSave;
	}
	
	public String getImageSaveURL() {
		return imageSaveURL;
	}
	public void setImageSaveURL(String imageSaveURL) {
		this.imageSaveURL = imageSaveURL;
	}
	
}
