package com.pennant.fusioncharts;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.dashboard.DashboardConfiguration;

public class ChartUtil {
	private static final Logger logger = LogManager.getLogger(ChartUtil.class);

	/**
	 * This is basic XML content string formation method It Forms XML tag for each element of chart e.g. if data in
	 * setElement Object is like label='Raju' and value='30' the resultant tag is like <label ='Raju' value ='30'/>
	 * 
	 * @param setElements
	 * @return
	 */
	public static String getSetElement(List<ChartSetElement> setElements) {
		StringBuilder setElement = new StringBuilder();
		if (setElements != null) {
			for (int i = 0; i < setElements.size(); i++) {
				setElement.append("\n");
				setElement.append(setElements.get(i).getSetElement());

			}
			setElement.append("\n");
		}

		return setElement.toString();
	}

	/**
	 * This is basic XML content string formation method It Forms XML tag for each dial of chart
	 * 
	 * @param setElements
	 * @return
	 */
	public static String getSetDial(List<ChartSetElement> setElements) {
		StringBuilder setDial = new StringBuilder("<dials>");
		if (setElements != null) {
			for (int i = 0; i < setElements.size(); i++) {
				setDial.append("<dial ");
				setDial.append("value=\"");
				setDial.append(setElements.get(i).getValue());
				setDial.append("\" />");
			}

			setDial.append("</dials>");
		}

		return setDial.toString();
	}

	/**
	 * This method forms the data XML for series type charts It takes list of elements of data and it Iterate through
	 * all the list and forms the String XML in format of which produces a Series type fusion chart
	 * 
	 * @param setElements
	 * @return
	 */
	public static String getSeriesTypeElements(List<ChartSetElement> setElements, String renderAs) {
		logger.debug("Entering");
		StringBuilder setElement = new StringBuilder();
		if (setElements.size() > 0) {
			Map<String, BigDecimal> catogeries = new HashMap<String, BigDecimal>();
			String prvSer = "";
			setElement.append("\n");
			// Form the <categories><category label='2001'/><category label='2002'/>.....</categories> data
			setElement.append("<categories>");
			setElement.append("\n");
			for (int i = 0; i < setElements.size(); i++) {
				if (!catogeries.containsKey(setElements.get(i).getCatogery())) {
					catogeries.put(setElements.get(i).getCatogery(), setElements.get(i).getValue());
					setElement.append("<category label='" + setElements.get(i).getCatogery() + "'/>");
				}
			}
			setElement.append("</categories>");
			setElement.append("\n");
			/*
			 * Sort the list by series then each set of series form <dataset seriesName="series1 "><set value="1"><set
			 * value="2"> </dataSet> <dataset seriesName="series2 "><set value="1"><set value="2"> </dataSet>
			 */
			Comparator<Object> comp = new BeanComparator<Object>("series");
			Collections.sort(setElements, comp);
			int setElementCount = 0;
			for (int i = 0; i < setElements.size(); i++) {
				// If i=0 start <dataset >tag
				if (i == 0) {
					prvSer = setElements.get(0).getSeries();
					setElement.append("\n");
					setElement.append("<dataset ");
					setElement.append("seriesName='");
					setElement.append(setElements.get(i).getSeries());
					setElement.append("' ");
					setElement.append(StringUtils.trimToEmpty(renderAs));
					setElement.append(" >");
					setElement.append("\n");

				} else {
					prvSer = setElements.get(i - 1).getSeries();
				}
				// If previous series does not match with old close <dataset >tag and start new <dataset > tag
				if (i != 0 && !prvSer.equals(setElements.get(i).getSeries())) {
					setElement.append("</dataset> ");
					setElement.append("\n");
					setElement.append("<dataset ");
					setElement.append("seriesName='");
					setElement.append(setElements.get(i).getSeries());
					setElement.append("' ");
					setElement.append(StringUtils.trimToEmpty(renderAs));
					setElement.append(" >");
					setElement.append("\n");
					setElement.append(setElements.get(i).getSetElement());
					setElementCount = setElementCount + 1;

				} else {
					setElement.append(setElements.get(i).getSetElement());
					setElementCount = setElementCount + 1;
				}
				setElement.append("\n");
			}
			setElement.append("</dataset> ");
		}

		logger.debug("Leaving");
		return setElement.toString();
	}

	/**
	 * This method forms the data XML for Drill Down type charts It takes list of elements of data and it Iterate
	 * through all the list and forms the String XML in format of which produces a Drill Down fusion chart
	 * 
	 * @param setElements
	 * @param remarks
	 * @return
	 */
	public static String getDrillDownTypeElements(List<ChartSetElement> setElements, String[] remarks) {
		StringBuilder xmlData = new StringBuilder(getSetElement(setElements));
		logger.debug("Entering");
		// Here we are Drilling the information from inside each list element
		// Second Level chart
		for (int i = 0; i < setElements.size(); i++) {
			String id = StringUtils.remove(setElements.get(i).getLink(), "newchart-xml-");
			xmlData.append("  <linkeddata id='" + id + "' >");
			String aRemarks = "";
			if (remarks.length > 1) {
				aRemarks = remarks[1];
			} else {
				aRemarks = "";
			}
			xmlData.append("<chart " + aRemarks + " >");
			if (setElements.get(i).getInnerChrtSetElementsList() != null) {
				xmlData.append(getSetElement(setElements.get(i).getInnerChrtSetElementsList()));
				// Third Level Chart
				for (int j = 0; j < setElements.get(i).getInnerChrtSetElementsList().size(); j++) {
					id = StringUtils.remove(setElements.get(i).getInnerChrtSetElementsList().get(j).getLink(),
							"newchart-xml-");
					xmlData.append("  <linkeddata id='" + id + "' >");
					if (remarks.length > 2) {
						aRemarks = remarks[2];
					} else {
						aRemarks = "";
					}
					xmlData.append("<chart " + aRemarks + " >");
					if (setElements.get(i).getInnerChrtSetElementsList().get(j).getInnerChrtSetElementsList() != null) {
						xmlData.append(getSetElement(
								setElements.get(i).getInnerChrtSetElementsList().get(j).getInnerChrtSetElementsList()));
						// Fourth Level Chart
						for (int k = 0; k < setElements.get(i).getInnerChrtSetElementsList().get(j)
								.getInnerChrtSetElementsList().size(); k++) {
							id = StringUtils.remove(setElements.get(i).getInnerChrtSetElementsList().get(j)
									.getInnerChrtSetElementsList().get(k).getLink(), "newchart-xml-");
							xmlData.append("  <linkeddata id='" + id + "' >");
							if (remarks.length > 3) {
								aRemarks = remarks[3];
							} else {
								aRemarks = "";
							}
							xmlData.append("<chart " + aRemarks + " >");
							xmlData.append(getSetElement(setElements.get(i).getInnerChrtSetElementsList().get(j)
									.getInnerChrtSetElementsList().get(k).getInnerChrtSetElementsList()));
							xmlData.append("</chart></linkeddata>");
						}
					}
					xmlData.append("</chart></linkeddata>");
				}
			}
			xmlData.append("</chart></linkeddata>");
		}
		setElements = null;

		logger.debug("Leaving");
		return xmlData.toString();
	}

	/**
	 * This method return chart type against DashBoard type and dimension eg:if type is pie and dimension is 2d return
	 * "Pie2D" else type is pie and dimension is 3d return "Pie3D"
	 * 
	 * @param aDashboardConfiguration
	 * @return
	 */

	public String getChartType(DashboardConfiguration aDashboardConfiguration) {
		String type = aDashboardConfiguration.getDashboardType();
		String dimension = aDashboardConfiguration.getDimension();
		if (type.equals(Labels.getLabel("label_Select_Bar"))) {
			if (!aDashboardConfiguration.isMultiSeries()) {
				if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
					return "Bar2D";
				} else {
					return "Column3D";
				}
			} else {
				if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
					return "MSBar2D";
				} else {
					return "MSBar3D";
				}
			}
		}

		if (type.equals(Labels.getLabel("label_Select_Column"))) {
			if (!aDashboardConfiguration.isMultiSeries()) {
				if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
					return "Column2D";
				} else {
					return "Column3D";
				}
			} else {
				if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
					return "MSColumn2D";
				} else {
					return "MSColumn3D";
				}
			}
		}

		if (type.equals(Labels.getLabel("label_Select_Line"))) {

			if (!aDashboardConfiguration.isMultiSeries()) {
				return "Line";
			} else {
				if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
					return "MSLine";
				} else {
					return "MSCombi3D";
				}
			}
		}

		if (type.equals(Labels.getLabel("label_Select_Area"))) {
			if (!aDashboardConfiguration.isMultiSeries()) {
				return "Area2D";
			} else {
				if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
					return "MSArea";
				} else {
					return "MSCombi3D";
				}
			}
		}

		if (type.equals(Labels.getLabel("label_Select_Pie"))) {
			if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
				return "Pie2D";
			} else {
				return "Pie3D";
			}

		}

		if (type.equals(Labels.getLabel("label_Select_Staked"))) {
			if (dimension.equals(Labels.getLabel("label_Select_2D"))) {
				return "StackedBar2D";
			} else {
				return "StackedBar3D";
			}
		}

		if (type.equals(Labels.getLabel("label_Select_Funnel"))) {
			return "Funnel";
		}

		if (type.equals(Labels.getLabel("label_Select_Pyramid"))) {
			return "Pyramid";
		}

		if (type.equals(Labels.getLabel("label_Select_Cylinder"))) {
			return "Cylinder";
		}

		if (type.equals(Labels.getLabel("label_Select_AngularGauge"))) {
			return "AngularGauge";
		}

		if (type.equals(Labels.getLabel("label_Select_HLinearGauge"))) {
			return "HLinearGauge";
		}

		return "Column3D";
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @param buffer
	 * @return
	 */
	public static StringBuilder getIntElement(String field, String value, StringBuilder buffer) {
		if (StringUtils.isNotBlank(value)) {
			buffer.append(field + "='");
			buffer.append(value);
			buffer.append("' ");
		}

		return buffer;
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @param buffer
	 * @return
	 */
	public static StringBuilder getIntElement(String field, double value, StringBuilder buffer) {
		buffer.append(field + "='");
		buffer.append(value);
		buffer.append("' ");

		return buffer;
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @param buffer
	 * @return
	 */
	public static StringBuilder getIntElement(String field, boolean value, StringBuilder buffer) {
		if (value) {
			buffer.append(field + "='1' ");
		}

		return buffer;
	}

	/**
	 * 
	 * @param field
	 * @param value
	 * @param buffer
	 * @return
	 */
	public static StringBuilder getIntElement(String field, int value, StringBuilder buffer) {
		if (value != 0) {
			buffer.append(field + "='");
			buffer.append(value);
			buffer.append("' ");
		}

		return buffer;
	}

	public boolean isMultiSeries(DashboardConfiguration aDashboardConfiguration) {

		if (aDashboardConfiguration.isMultiSeries()) {
			if (Labels.getLabel("label_Select_Funnel").equals(aDashboardConfiguration.getDashboardType())
					|| Labels.getLabel("label_Select_Pyramid").equals(aDashboardConfiguration.getDashboardType())) {
				return false;
			}
			return aDashboardConfiguration.isMultiSeries();
		}
		return false;
	}

	public boolean isAGauge(DashboardConfiguration aDashboardConfiguration) {

		if (Labels.getLabel("label_Select_AngularGauge").equals(aDashboardConfiguration.getDashboardType())) {
			return true;
		}
		return false;
	}

}