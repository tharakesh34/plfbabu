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
 * FileName    		:  Fusionchart.jsp                                    * 	  
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
<html>
<head>
<title>FusionCharts</title>
<script type="text/javascript" src="FusionCharts.js" ></script>
<script type="text/javascript" src="FusionChartsExportComponent.js" ></script>
</head>
<body bgcolor="#ffffff">

	<%
		String dashBoardName = (String) request.getParameter("chartID");
		String strXML = (String) request.getParameter("strlXml");
		String swfFile = (String) request.getParameter("swfFile");
		strXML = strXML.replace("\"", "'");
		String width = (String) request.getParameter("width");
		String height = (String) request.getParameter("height");
		System.out.println("swfFile" + swfFile);
	%>
		<jsp:include page="FusionChartsRenderer.jsp" flush="true">
			<jsp:param name="chartSWF" value="<%=swfFile %>" />
			<jsp:param name="strURL" value="" />
			<jsp:param name="strXML" value="<%=strXML %>" />
			<jsp:param name="chartId" value="<%=dashBoardName%>" />
			<jsp:param name="chartWidth" value="<%=width%>" />
			<jsp:param name="chartHeight" value="<%=height%>" />
			<jsp:param name="debugMode" value="false" />
			<jsp:param name="registerWithJS" value="true" />
		</jsp:include>
</body>
</html>
