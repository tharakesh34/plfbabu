package com.pennant.fusioncharts;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Html;

import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;

public class ChartUtil {
	private final static Logger logger = Logger.getLogger(ChartUtil.class);

	/**
	 * This is basic XML content string formation method 
	 * It Forms XML tag for each element of chart 
	 *  e.g. if data in setElement Object is like label='Raju' and value='30'
	 *     the resultant tag is like <label ='Raju' value ='30'/>
	 * @param setElements
	 * @return
	 */
	public static String getSetElement(List<ChartSetElement> setElements){
		StringBuffer setElement= new StringBuffer(); 
		if(setElements!=null){
			for (int i = 0; i < setElements.size(); i++) {
				setElement.append("\n");
				setElement.append(setElements.get(i).getSetElement());

			} 
			setElement.append("\n");
		}
		return setElement.toString();
	}
	
	/**
	 * This is basic XML content string formation method 
	 * It Forms XML tag for each dial of chart 
	 * @param setElements
	 * @return
	 */
	public static String getSetDial(List<ChartSetElement> setElements){
		StringBuffer setDial= new StringBuffer("<dials>"); 
		if(setElements!=null){
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
	 * This method forms the data XML for series type charts 
	 *  It takes list of elements of data and it Iterate through all the list and forms the String XML in format of 
	 *  which produces a Series type fusion chart
	 * @param setElements
	 * @return
	 */
	public static String getSeriesTypeElements(List<ChartSetElement> setElements,String renderAs){
		logger.debug("Entering");
		StringBuffer setElement= new StringBuffer(); 
		if(setElements.size()>0){
			Map<String,BigDecimal>       catogeries=new HashMap<String, BigDecimal>();
			String prvSer="";
			setElement.append("\n");
			//Form the <categories><category label='2001'/><category label='2002'/>.....</categories> data 
			setElement.append("<categories>");
			setElement.append("\n"); 
			for (int i = 0; i < setElements.size(); i++) {
				if(!catogeries.containsKey(setElements.get(i).getCatogery())){
					catogeries.put(setElements.get(i).getCatogery(),setElements.get(i).getValue());
					setElement.append("<category label='"+setElements.get(i).getCatogery()+"'/>");
				}
			} 
			setElement.append("</categories>");
			setElement.append("\n");
			@SuppressWarnings("unchecked")
			/*Sort the list by series then each set of series form 
			<dataset seriesName="series1 "><set value="1"><set value="2"> </dataSet>
			<dataset seriesName="series2 "><set value="1"><set value="2"> </dataSet>
			 */
			Comparator<Object> comp = new BeanComparator("series");
			Collections.sort(setElements, comp);
			int setElementCount=0;
			for (int i = 0; i <   setElements.size(); i++) {
				//If i=0 start  <dataset >tag 
				if(i==0){
					prvSer=setElements.get(0).getSeries();
					setElement.append("\n");
					setElement.append("<dataset ");
					setElement.append("seriesName='");
					setElement.append(setElements.get(i).getSeries());
					setElement.append("' ");
					setElement.append(StringUtils.trimToEmpty(renderAs));
					setElement.append(" >");
					setElement.append("\n");
					
				} else {
					prvSer=setElements.get(i-1).getSeries();
				}
				//If previous  series does not match with old close <dataset >tag and start new <dataset > tag 
				if(i!=0 && !prvSer.equals(setElements.get(i).getSeries()) ) {
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
					setElementCount=setElementCount+1;

				}else{
					setElement.append(setElements.get(i).getSetElement());
					setElementCount=setElementCount+1;
				}
				setElement.append("\n");
			}
			setElement.append("</dataset> ");
		}
		logger.debug("Leaving");
		return setElement.toString();
	}
	/**
	 * This method forms the data XML for Drill Down  type charts 
	 *  It takes list of elements of data and it Iterate through all the list and forms the String XML in format of 
	 *  which produces a Drill Down fusion chart
	 * @param setElements
	 * @param remarks
	 * @return
	 */
	public static String getDrillDownTypeElements(List<ChartSetElement> setElements,String[] remarks){ 
		StringBuffer xmlData=new StringBuffer(getSetElement(setElements));
		logger.debug("Entering");
		//Here we are Drilling the information from inside each list element
		//Second Level chart
		for(int i=0;i<setElements.size();i++){
			String id=StringUtils.remove(setElements.get(i).getLink(), "newchart-xml-");
			xmlData.append("  <linkeddata id='"+id+"' >");
			String aRemarks="";
			if(remarks.length>1){
				aRemarks=remarks[1];
			}else{
				aRemarks="";
			}
			xmlData.append("<chart "+aRemarks+" >");
			if(setElements.get(i).getInnerChrtSetElementsList()!=null){
				xmlData.append(getSetElement(setElements.get(i).getInnerChrtSetElementsList()));
				//Third Level Chart
				for(int j=0;j<setElements.get(i).getInnerChrtSetElementsList().size();j++){
					id=StringUtils.remove(setElements.get(i).getInnerChrtSetElementsList().get(j).getLink(), "newchart-xml-");
					xmlData.append("  <linkeddata id='"+id+"' >");
					if(remarks.length>2){
						aRemarks=remarks[2];
					}else{
						aRemarks="";
					}
					xmlData.append("<chart "+aRemarks+" >");
					if(setElements.get(i).getInnerChrtSetElementsList().get(j).getInnerChrtSetElementsList()!=null){
						xmlData.append(getSetElement(setElements.get(i).getInnerChrtSetElementsList().get(j).getInnerChrtSetElementsList()));
						//Fourth Level Chart
						for(int k=0;k<setElements.get(i).getInnerChrtSetElementsList().get(j).getInnerChrtSetElementsList().size();k++){
							id=StringUtils.remove(setElements.get(i).getInnerChrtSetElementsList().get(j)
									.getInnerChrtSetElementsList().get(k).getLink(), "newchart-xml-");
							xmlData.append("  <linkeddata id='"+id+"' >");
							if(remarks.length>3){
								aRemarks=remarks[3];
							}else{
								aRemarks="";
							}
							xmlData.append("<chart "+aRemarks+" >");
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
		setElements=null;
		logger.debug("Leaving");
		return xmlData.toString();
	}

	/**
	 * This method return SWF file(flash file name ) against DashBoard type and dimension
	 * eg:if type is pie and dimension is 2d return "Pie2D.swf"
	 *          else type is pie and dimension is 3d return "Pie3D.swf"
	 * @param aDashboardConfiguration
	 * @return
	 */

	public String getSWFFileName (DashboardConfiguration aDashboardConfiguration){
		String type=aDashboardConfiguration.getDashboardType();
		String dimension=aDashboardConfiguration.getDimension();
		if(type.equals(Labels.getLabel("label_Select_Bar"))){
			if(!aDashboardConfiguration.isMultiSeries()){
				if(dimension.equals(Labels.getLabel("label_Select_2D"))){
					return "Bar2D.swf";
				}else{
					return "Column3D.swf";
				}
			}else{
				if(dimension.equals(Labels.getLabel("label_Select_2D"))){
					return "MSBar2D.swf";
				}else{
					return "MSBar3D.swf";
				}
			}
		}
		
		if(type.equals(Labels.getLabel("label_Select_Column"))){
			if(!aDashboardConfiguration.isMultiSeries()){
				if(dimension.equals(Labels.getLabel("label_Select_2D"))){
					return "Column2D.swf";
				}else{
					return "Column3D.swf";
				}
			}else{
				if(dimension.equals(Labels.getLabel("label_Select_2D"))){
					return "MSColumn2D.swf";
				}else{
					return "MSColumn3D.swf";
				}
			}
		}
		
		if(type.equals(Labels.getLabel("label_Select_Line"))){
			
			if(!aDashboardConfiguration.isMultiSeries()){
					return "Line.swf";
			}else{
				if(dimension.equals(Labels.getLabel("label_Select_2D"))){
					return "MSLine.swf";
				}else{
					return "MSCombi3D.swf";
				}
			}
		}

		if(type.equals(Labels.getLabel("label_Select_Area"))){
			if(!aDashboardConfiguration.isMultiSeries()){
					return "Area2D.swf";
			}else{
				if(dimension.equals(Labels.getLabel("label_Select_2D"))){
					return "MSArea.swf";
				}else{
					return "MSCombi3D.swf";
				}
			}
		}
		
		if(type.equals(Labels.getLabel("label_Select_Pie"))){
			if(dimension.equals(Labels.getLabel("label_Select_2D"))){
				return"Pie2D.swf";
			}else{
				return "Pie3D.swf";
			}

		}

		if(type.equals(Labels.getLabel("label_Select_Staked"))){
			if(dimension.equals(Labels.getLabel("label_Select_2D"))){
				return"StackedBar2D.swf";
			}else{
				return"StackedBar3D.swf";
			}
		}

		if(type.equals(Labels.getLabel("label_Select_Funnel"))){
				return"Funnel.swf";
		}

		if(type.equals(Labels.getLabel("label_Select_Pyramid"))){
			return"Pyramid.swf";
		}
		
		
		if(type.equals(Labels.getLabel("label_Select_Cylinder"))){
			return"Cylinder.swf";
		}
		
		if(type.equals(Labels.getLabel("label_Select_AngularGauge"))){
			return"AngularGauge.swf";
		}

		if(type.equals(Labels.getLabel("label_Select_HLinearGauge"))){
			return"HLinearGauge.swf";
		}

		return "Column3D.swf";
	}
	/**
	 * This method do the following
	 *  1) This is posting a request to FusionChart.jsp with the parameters 
	 * Chart id,Data XML as String,SWF File ,chart height and width
	 * 2)The resultant response is a chart .we are keeping that in IFrame 
	 * @param chartDetail
	 * @return chartFormHtml(org.zk.zul.Html)
	 */
	public Html getHtmlContent(ChartDetail chartDetail){
		logger.debug("Entering");
		String strXML=chartDetail.getStrXML().replaceAll("\\n", "");
		strXML=strXML.replaceAll("\'", "&quot;");

		Html chartFormHtml = new Html();
		String formID="'Form"+chartDetail.getChartId()+"'";
		String iFrame="iframe"+chartDetail.getChartId()+"'";
		StringBuffer htmlCode= new StringBuffer(); 
		htmlCode.append("<form id="+formID+" target="+iFrame+"  method='post' action='./Charts/FusionChart.jsp'>" );

		htmlCode.append("<input type='hidden' name='chartID' value='"+chartDetail.getChartId()+"'/>");
		htmlCode.append("<input type='hidden' name='strlXml' value='"+strXML+"'/>");
		htmlCode.append("<input type='hidden' name='swfFile' value='"+chartDetail.getSwfFile()+"'/>");
		htmlCode.append("<input type='hidden' name='width'   value='"+chartDetail.getChartWidth()+"'/>");
		htmlCode.append("<input type='hidden' name='height'  value='"+chartDetail.getChartHeight()+"'/>");
		htmlCode.append("</form>");
		htmlCode.append("<iframe id ="+iFrame+"  name="+iFrame+" border='0' frameborder='0' height='"
				+chartDetail.getiFrameHeight()+"' width='"+chartDetail.getiFrameWidth()+"'></iframe>");
		htmlCode.append("<script type='text/javascript'>");
		htmlCode.append(" document.getElementById("+formID+").submit();");
		htmlCode.append("</script>");
		chartFormHtml.setContent(htmlCode.toString());
		logger.debug("Leaving");
		return chartFormHtml;
	}


	/**
	 * 
	 * @param definitionMap
	 * @return
	 */
	private static String getStyleDefinition(Map<String, ChartStyleDefinition> definitionMap){
		logger.debug("Entering");
		StringBuffer definition= new StringBuffer(); 

		Collection<ChartStyleDefinition> definitions = definitionMap.values();

		if(definitions.size()>0){
			definition.append("<definition>");
			definition.append("\n");
		}else{
			return definition.toString();
		}

		Iterator<ChartStyleDefinition> iterator = definitions.iterator();

		while (iterator.hasNext()){ 
			ChartStyleDefinition styleDefinition= iterator.next();
			if("font".equalsIgnoreCase(styleDefinition.getType())){
				definition.append(styleDefinition.getFontStyle());
			}else if("ANIMATION".equalsIgnoreCase(styleDefinition.getType())){
				definition.append(styleDefinition.getAnimationStyle());
			}else if("Shadow".equalsIgnoreCase(styleDefinition.getType())){
				definition.append(styleDefinition.getShadowStyle());
			}else if("Glow".equalsIgnoreCase(styleDefinition.getType())){
				definition.append(styleDefinition.getGlowStyle());
			}else if("Bevel".equalsIgnoreCase(styleDefinition.getType())){
				definition.append(styleDefinition.getBevelStyle());
			}else if("Blur".equalsIgnoreCase(styleDefinition.getType())){
				definition.append(styleDefinition.getBlurStyle());
			}

			definition.append("\n");
		} 


		definition.append("</definition>");
		definition.append("\n");
		logger.debug("Leaving");
		return definition.toString();

	}
	/**
	 * This method returns application tag of chart
	 * @param applicationMap
	 * @return
	 */
	private static String getApplication(Map<String, ChartStyleApplication> applicationMap){
		logger.debug("Entering");
		StringBuffer application= new StringBuffer(); 

		Collection<ChartStyleApplication> definitions = applicationMap.values();

		if(definitions.size()>0){
			application.append("<application>");
			application.append("\n");
		}else{
			return application.toString();
		}

		Iterator<ChartStyleApplication> iterator = definitions.iterator();

		while (iterator.hasNext()){ 
			ChartStyleApplication styleApplication= iterator.next();
			application.append(styleApplication.getApplicationString());
			application.append("\n");
		}

		application.append("</definition>");
		application.append("\n");
		logger.debug("Leaving");
		return application.toString();
	}
	/**
	 * This method returns Style tags of Chart
	 * @param definitionMap
	 * @param applicationMap
	 * @return
	 */
	public static String getStyles(Map<String, ChartStyleDefinition> definitionMap,Map<String, ChartStyleApplication> applicationMap){

		StringBuffer styles= new StringBuffer(); 

		String definition = getStyleDefinition(definitionMap);
		String application = getApplication(applicationMap);

		if(StringUtils.isNotBlank(definition)){

			styles.append(" <styles>");
			styles.append(definition);
			styles.append("\n");

		}else{
			return "";
		}

		if(StringUtils.isNotBlank(application)){
			styles.append(application);
			styles.append("\n");
		}

		styles.append(" </styles>");

		return styles.toString();
	}
	/**
	 * 
	 * @param field
	 * @param value
	 * @param buffer
	 * @return
	 */
	public static StringBuffer getIntElement(String field,String value,StringBuffer buffer){

		if(StringUtils.isNotBlank(value)){
			buffer.append( field +"='");
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
	public static StringBuffer getIntElement(String field,double value,StringBuffer buffer){

		buffer.append(field +"='");
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
	public static StringBuffer getIntElement(String field,boolean value,StringBuffer buffer){

		if(value){
			buffer.append(field+"='1' ");
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
	public static StringBuffer getIntElement(String field,int value,StringBuffer buffer){

		if(value!=0){
			buffer.append(field+"='");
			buffer.append(value);
			buffer.append("' ");
		}
		return buffer;
	}
	
	public boolean isMultiSeries(DashboardConfiguration aDashboardConfiguration){
		
		if(aDashboardConfiguration.isMultiSeries()){
			if(Labels.getLabel("label_Select_Funnel").equals(aDashboardConfiguration.getDashboardType()) || Labels.getLabel("label_Select_Pyramid").equals(aDashboardConfiguration.getDashboardType())){
				return false;
			}
			return aDashboardConfiguration.isMultiSeries();
		}
		return false;
	}
	
	public boolean isAGauge(DashboardConfiguration aDashboardConfiguration){
		
		if(Labels.getLabel("label_Select_AngularGauge").equals(aDashboardConfiguration.getDashboardType())){
			return true;
		}
		return false;
	}
	
}