package com.pennant.fusioncharts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChartsConfig extends ChartUtil {
	private static final Logger logger = LogManager.getLogger(ChartsConfig.class);
	private String caption;
	private String subCaption;
	private String yAxisName;
	private String xAxisName;
	private String remarks;

	private boolean showPlotBorder;
	private String plotBorderColor;
	private int plotBorderAlpha;
	private int plotFillAlpha;
	private String overlapColumns;

	private List<ChartSetElement> setElements = new ArrayList<ChartSetElement>();
	private List<String> catogeryList = new ArrayList<String>();

	public ChartsConfig(String caption, String subCaption, String yAxisName, String xAxisName) {
		super();
		this.caption = caption;
		this.subCaption = subCaption;
		this.yAxisName = yAxisName;
		this.xAxisName = xAxisName;
	}

	public void SetDataPlot(boolean showPlotBorder, String plotBorderColor, int plotBorderAlpha, int plotFillAlpha,
			String overlapColumns) {

		this.showPlotBorder = showPlotBorder;
		this.plotBorderColor = plotBorderColor;
		this.plotBorderAlpha = plotBorderAlpha;
		this.plotFillAlpha = plotFillAlpha;
		this.overlapColumns = overlapColumns;

	}

	/**
	 * This method will return DataXML for single series charts
	 * 
	 * @return
	 */
	public String getChartXML() {
		logger.debug("Entering");
		StringBuilder chartXML = new StringBuilder("<chart ");

		chartXML = setChartBasicElements(chartXML);
		if (getRemarks() != null && StringUtils.isNotEmpty(getRemarks())) {
			chartXML.append(" " + getRemarks() + " ");
		}

		chartXML.append(">");
		chartXML.append(getSetElement(setElements));

		chartXML.append("</chart>");

		logger.debug("Leaving");
		return chartXML.toString();
	}

	/**
	 * This method will return DataXML for multiple series charts
	 * 
	 * @return
	 */

	public String getSeriesChartXML(String renderAs) {
		logger.debug("Entering");
		StringBuilder chartXML = new StringBuilder("<chart ");
		chartXML = setChartBasicElements(chartXML);
		if (getRemarks() != null && StringUtils.isNotEmpty(getRemarks())) {
			chartXML.append(" " + getRemarks() + " ");
		}

		chartXML.append(">");
		chartXML.append(getSeriesTypeElements(setElements, renderAs));

		chartXML.append("</chart>");

		logger.debug("Leaving");
		return chartXML.toString();
	}

	/**
	 * This method returns dataXML for drill down charts
	 * 
	 * @return
	 */
	public String getDrillDownChartXML() {
		logger.debug("Entering");
		StringBuilder chartXML = new StringBuilder("<chart ");
		chartXML = setChartBasicElements(chartXML);
		String[] remarks = null;
		if (StringUtils.contains(getRemarks(), "||")) {
			remarks = getRemarks().split(Pattern.quote("||"));
		}
		if (remarks != null && remarks.length > 0) {
			chartXML.append(" " + remarks[0] + " ");
		}
		chartXML.append(">");
		chartXML.append(getDrillDownTypeElements(setElements, remarks));
		chartXML.append("</chart>");

		logger.debug("Leaving");
		return chartXML.toString();

	}

	/**
	 * This method will return DataXML for Gauge
	 * 
	 * @return
	 */
	public String getAGaugeXML() {
		logger.debug("Entering");
		StringBuilder chartXML = new StringBuilder("<chart ");

		chartXML = setChartBasicElements(chartXML);
		if (getRemarks() != null && StringUtils.isNotEmpty(getRemarks())) {
			chartXML.append(" " + getRemarks() + " ");
		}

		chartXML.append(">");
		chartXML.append(getGaugeColourRange());
		chartXML.append(getSetDial(setElements));
		chartXML.append(getGaugeOthers());
		chartXML.append("</chart>");

		logger.debug("Leaving");
		return chartXML.toString();
	}

	public String getGaugeColourRange() {
		StringBuilder colourRange = new StringBuilder("<colorRange>");
		colourRange.append("<color minValue=\"0\" maxValue=\"9.99\" code=\"8BBA00\"/>");
		colourRange.append("<color minValue=\"10.00\" maxValue=\"19.99\" code=\"F6BD0F\"/>");
		colourRange.append("<color minValue=\"20.00\" maxValue=\"100.00\" code=\"FF654F\"/>");
		colourRange.append("</colorRange>");

		return colourRange.toString();
	}

	public String getGaugeOthers() {
		StringBuilder others = new StringBuilder("<annotations>");
		others.append("<annotationGroup id=\"Grp1\" showBelow=\"1\" showShadow=\"1\">");
		others.append(
				"<annotation type=\"rectangle\" x=\"$chartStartX+5\" y=\"$chartStartY+5\" toX=\"$chartEndX-5\" toY=\"$chartEndY-5\" radius=\"10\" fillColor=\"FFFFFF,FFFFFF\" showBorder=\"0\" />");
		others.append("</annotationGroup>");
		others.append("</annotations>");
		others.append("<styles>");
		others.append("<definition>");
		others.append("<style name=\"RectShadow\" type=\"shadow\" strength=\"3\"/>");
		others.append("</definition>");
		// others.append("<style name=\"trendvaluefont\" type=\"font\" bold=\"1\" borderColor=\"FFFFDD\">");
		others.append("<application>");
		others.append("apply toObject=\"Grp1\" styles=\"RectShadow\">");
		others.append("</application>");
		others.append("</styles>");

		return others.toString();
	}

	/**
	 * This method get the XML string for below elements
	 * 
	 * @param chartXML
	 * @return
	 */
	private StringBuilder setChartBasicElements(StringBuilder chartXML) {
		chartXML = getIntElement("caption", this.caption, chartXML);
		chartXML = getIntElement("subCaption", this.subCaption, chartXML);
		chartXML = getIntElement("yAxisName", this.yAxisName, chartXML);
		chartXML = getIntElement("xAxisName", this.xAxisName, chartXML);
		chartXML = getIntElement("showPlotBorder", this.showPlotBorder, chartXML);
		chartXML = getIntElement("plotBorderColor", this.plotBorderColor, chartXML);
		chartXML = getIntElement("plotBorderAlpha", this.plotBorderAlpha, chartXML);
		chartXML = getIntElement("plotFillAlpha", this.plotFillAlpha, chartXML);
		chartXML = getIntElement("overlapColumns", this.overlapColumns, chartXML);
		// chartXML.append(" "+ Labels.getLabel("label_Chart_CommanAttributes"));

		return chartXML;
	}

	// Getters and setters
	public List<ChartSetElement> getSetElements() {
		return setElements;
	}

	public void setSetElements(List<ChartSetElement> setElements) {
		this.setElements = setElements;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setCatogeryList(List<String> catogeryList) {
		this.catogeryList = catogeryList;
	}

	public List<String> getCatogeryList() {
		return catogeryList;
	}
}
