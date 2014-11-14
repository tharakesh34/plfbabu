<%-- 
/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  Fusionchartrender.jsp                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-02-2012    														*
 *                                                                  						*
 * Modified Date    :  14-02-2012 														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
--%>

<%@page import="org.apache.commons.lang.StringUtils"%>
<%
	String chartSWF = request.getParameter("chartSWF");
	String strURL = request.getParameter("strURL");
	String strXML = request.getParameter("strXML");
	String chartId = request.getParameter("chartId");
	String chartWidthStr = request.getParameter("chartWidth");
	String chartHeightStr = request.getParameter("chartHeight");
	String debugModeStr = request.getParameter("debugMode");
	String registerWithJSStr = request.getParameter("registerWithJS");

	int chartWidth = 600;
	int chartHeight = 300;
	boolean debugMode = false;
	boolean registerWithJS = true;
	int debugModeInt = 0;
	int regWithJSInt = 0;

	if (null != chartWidthStr && !chartWidthStr.equals("")) {
		if (StringUtils.containsIgnoreCase(chartWidthStr, "per")) {
			chartWidthStr = chartWidthStr.toLowerCase();
			chartWidthStr = StringUtils.replace(chartWidthStr, "per",
					"%");
		}
	}
	if (null != chartHeightStr && !chartHeightStr.equals("")) {
		if (StringUtils.containsIgnoreCase(chartHeightStr, "per")) {
			chartHeightStr = chartHeightStr.toLowerCase();
			chartHeightStr = StringUtils.replace(chartHeightStr, "per",
					"%");
		}
	}
	if (null != debugModeStr && !debugModeStr.equals("")) {
		debugMode = new Boolean(debugModeStr).booleanValue();
		debugModeInt = boolToNum(new Boolean(debugMode));
	}
	if (null != registerWithJSStr && !registerWithJSStr.equals("")) {
		registerWithJS = new Boolean(registerWithJSStr).booleanValue();
		regWithJSInt = boolToNum(new Boolean(registerWithJS));
	}
%>
<div id='<%=chartId%>Div' align='center'>Chart</div>
<div id='<%=chartId%>ExportData'></div>
<script type='text/javascript'>

	FusionCharts.setCurrentRenderer('javascript');   /* Forcing to render by javascript*/
	
	var chart_<%=chartId%> = new FusionCharts("<%=chartSWF%>", "<%=chartId%>","<%=chartWidthStr%>"
    ,"<%=chartHeightStr%>", "<%=debugModeInt%>","<%=regWithJSInt%>");

    <%if (strXML.equals("")) {%>
        //<!-- Set the dataURL of the chart-->
        chart_<%=chartId%>.setDataURL("<%=strURL%>");

    <%} else {%>
        // Provide entire XML data using dataXML method
        chart_<%=chartId%>.setDataXML("<%=strXML%>");
    <%}%>

      chart_<%=chartId%>.configure("ChartNoDataText", "No Current Records To Display");//when there is no reocords message
      //<!-- Finally, render the chart.-->
      chart_<%=chartId%>.render("<%=chartId%>Div");
     
      chart_<%=chartId%>.configureLink (
    		  {
    		    swfUrl : "Column3D.swf",
    		    overlayButton:
    		    {    
    		      fontColor : '880000',
    		      bgColor:'FFEEEE',
    		      borderColor: '660000'
    		    }
    		  }, 0);
      
      chart_<%=chartId%>.configureLink (
    		  {
    		    swfUrl : "Column3D.swf",
    		    overlayButton:
    		    {    
    		      fontColor : '880000',
    		      bgColor:'FFEEEE',
    		      borderColor: '660000'
    		    }
    		  }, 0);
      
 
      chart_<%=chartId%>.configureLink (
    		  {
    		    swfUrl : "Column3D.swf",
    		    overlayButton:
    		    {    
    		      fontColor : '880000',
    		      bgColor:'FFEEEE',
    		      borderColor: '660000'
    		    }
    		  }, 0);
      
 
      chart_<%=chartId%>.configureLink (
    		  {
    		    swfUrl : "Column3D.swf",
    		    overlayButton:
    		    {    
    		      fontColor : '880000',
    		      bgColor:'FFEEEE',
    		      borderColor: '660000'
    		    }
    		  }, 0);
      
 
      
 
      //Below Code for Exporting chart as .jpeg,.png and .pdf files
      var myExportComponent = new FusionChartsExportObject("fcExporter1", "FCExporter.swf");
	  myExportComponent.render("<%=chartId%>ExportData");	
	  
</script>
<%!/**
	 * Converts a Boolean value to int value<br>
	 * @param bool Boolean value which needs to be converted to int value
	 * @return int value correspoding to the boolean : 1 for true and 0 for false
	 */
	public int boolToNum(Boolean bool) {
		int num = 0;
		if (bool.booleanValue()) {
			num = 1;
		}
		return num;
	}%>
