package com.pennant.backend.model.dashboard;

public class ChartDetail implements java.io.Serializable {

	private static final long serialVersionUID = -292875688509626740L;
	private String dataURL = "";
	private String strXML;
	private String chartWidth;
	private String chartHeight;
	private String chartType;
	private String chartId;
	private String iFrameWidth;
	private String iFrameHeight;

	public ChartDetail() {
	    super();
	}

	public String getiFrameWidth() {
		return iFrameWidth;
	}

	public void setiFrameWidth(String iFrameWidth) {
		this.iFrameWidth = iFrameWidth;
	}

	public String getiFrameHeight() {
		return iFrameHeight;
	}

	public void setiFrameHeight(String iFrameHeight) {
		this.iFrameHeight = iFrameHeight;
	}

	public String getDataURL() {
		return dataURL;
	}

	public void setDataURL(String dataURL) {
		this.dataURL = dataURL;
	}

	public String getStrXML() {
		return strXML;
	}

	public void setStrXML(String strXML) {
		this.strXML = strXML;
	}

	public String getChartWidth() {
		return chartWidth;
	}

	public void setChartWidth(String chartWidth) {
		this.chartWidth = chartWidth;
	}

	public String getChartHeight() {
		return chartHeight;
	}

	public void setChartHeight(String chartHeight) {
		this.chartHeight = chartHeight;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getChartId() {
		return chartId;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
}
