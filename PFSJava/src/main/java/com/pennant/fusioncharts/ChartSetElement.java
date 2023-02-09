package com.pennant.fusioncharts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "catogery", "series", "label", "value" })
@XmlAccessorType(XmlAccessType.NONE)
public class ChartSetElement extends ChartUtil implements Serializable {

	private static final long serialVersionUID = 7091233294525883661L;

	@XmlElement
	private String catogery;
	@XmlElement
	private String series = "";
	@XmlElement
	private String label;
	@XmlElement
	private BigDecimal value;
	private String reference;// It is used for drill down charts to refer parent label
	private String displayValue;
	private String color;
	private String link;
	private String toolText;
	private boolean showLabel = false;
	private boolean showValue = false;
	private boolean dashed;
	private int alpha;
	private List<String> catogeryList;
	private List<String> seriesList;
	// command design pattern
	private List<ChartSetElement> innerChrtSetElementsList;
	private String finSourceId;

	public ChartSetElement() {
		super();

	}

	public ChartSetElement(String label, BigDecimal value) {
		super();
		this.label = label;
		this.value = value;
	}

	public ChartSetElement(String catogery, String series, BigDecimal value) {
		super();
		this.catogery = catogery;
		this.series = series;
		this.value = value;
	}

	/**
	 * This method returns String of XML tag
	 * 
	 * @return
	 */
	public String getSetElement() {
		StringBuilder setElement = new StringBuilder("<set ");
		setElement = getIntElement("label", this.label, setElement);
		setElement = getIntElement("value", this.value != null ? this.value.toString() : "0", setElement);
		setElement = getIntElement("displayValue", this.displayValue, setElement);
		setElement = getIntElement("color", this.color, setElement);
		setElement = getIntElement("link", this.link, setElement);
		setElement = getIntElement("toolText", this.toolText, setElement);
		setElement = getIntElement("showLabel", this.showLabel, setElement);
		setElement = getIntElement("showValue", this.showValue, setElement);
		setElement = getIntElement("dashed", this.dashed, setElement);
		setElement = getIntElement("alpha", this.alpha, setElement);
		setElement.append(" /> ");

		return setElement.toString();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getToolText() {
		return toolText;
	}

	public void setToolText(String toolText) {
		this.toolText = toolText;
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public boolean isShowValue() {
		return showValue;
	}

	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

	public boolean isDashed() {
		return dashed;
	}

	public void setDashed(boolean dashed) {
		this.dashed = dashed;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public List<String> getCatogeryList() {
		return catogeryList;
	}

	public void setCatogeryList(List<String> catogeryList) {
		this.catogeryList = catogeryList;
	}

	public List<String> getSeriesList() {
		return seriesList;
	}

	public void setSeriesList(List<String> seriesList) {
		this.seriesList = seriesList;
	}

	public String getCatogery() {
		return catogery;
	}

	public void setCatogery(String catogery) {
		this.catogery = catogery;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public ChartSetElement(BigDecimal value) {
		super();
		this.value = value;
	}

	public void setInnerChrtSetElementsList(List<ChartSetElement> innerChrtSetElementsList) {
		this.innerChrtSetElementsList = innerChrtSetElementsList;
	}

	public List<ChartSetElement> getInnerChrtSetElementsList() {
		return innerChrtSetElementsList;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReference() {
		return reference == null ? "" : reference;
	}

	public String getFinSourceId() {
		return finSourceId;
	}

	public void setFinSourceId(String finSourceId) {
		this.finSourceId = finSourceId;
	}

}
