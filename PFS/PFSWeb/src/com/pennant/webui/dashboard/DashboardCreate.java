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
 * FileName    		:  DashboardDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
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

package com.pennant.webui.dashboard;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.fusionchart.Fusionchart;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zkmax.zul.Portalchildren;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;

import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/DashboardDetails/DashboardDetail/dashboardDetailDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class DashboardCreate extends  GFCBaseListCtrl<Object> implements Serializable {
	private static final long serialVersionUID = -4201689911130684236L;
	private final static Logger logger = Logger.getLogger(DashboardCreate.class);
	protected  Fusionchart    fusionchart;
	private    String         borderLayoutHeight = getBorderLayoutHeight();
	private    int            myHeight=Integer.parseInt(StringUtils.replaceChars(borderLayoutHeight, "px","")) * 85/100;
	public     ChartDetail    chartDetail;
	ChartUtil  chartUtil=new ChartUtil();
	private DashboardConfigurationService dashboardConfigurationService;

	/**
	 * Creates a new panel and inserts the Dash boards in to it. 
	 * 
	 * @param info
	 * @return
	 */
	public synchronized Panel  createPanel(DashboardConfiguration info,Portalchildren portalchildren) {

		logger.debug("Entering ");

		Panel dashboardPanel = initPanel(new Panel());// Get a panel template
		dashboardPanel.setId(info.getDashboardCode());
		dashboardPanel.setTitle(info.getDashboardDesc());
		Panelchildren panelchildren = dashboardPanel.getPanelchildren();
		ChartDetail chartDetail=getChartDetail(info);
		chartDetail.setChartId(info.getDashboardCode());
		chartDetail.setChartHeight("88%");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("100%");
		chartDetail.setiFrameWidth("100%");
		setChartDetail(chartDetail);
		//Get HTML Content which renders fusion chart by calling chartUtil.getHtmlContent(chartDetail)
		panelchildren.appendChild(chartUtil.getHtmlContent(chartDetail));
		dashboardPanel.appendChild(panelchildren);
		return dashboardPanel;
	}
	/**
	 * Initialize the panel
	 * @param panel
	 * @return
	 */
	private Panel initPanel(Panel panel) {
		panel.setBorder("normal");
		panel.setCollapsible(true);
		panel.setClosable(true);  
		panel.setMaximizable(true);
		panel.setHeight(myHeight/2+"px");
		panel.addEventListener(Events.ON_MAXIMIZE, new onPanelMaximized());
		panel.addEventListener(Events.ON_CLOSE, new onPanelClosed());
		panel.appendChild(new Panelchildren());
		return panel;
	}
	/**
	 * 
	 * Event listener for panel maximize event
	 *
	 */
	class onPanelMaximized implements EventListener{

		@Override
		public void onEvent(Event event) throws Exception {
			MaximizeEvent maxEvent = (MaximizeEvent) event; 
			Panel panel = (Panel) maxEvent.getTarget();  
			onPanelMaximizedHelper(panel);
		}
	}
	/**
	 * This method ReDraws  the chart when panel is maximized
	 * @param panel
	 */
	public void onPanelMaximizedHelper(Panel panel){
		logger.debug("Entering");

		Panelchildren pc=(Panelchildren) panel.getChildren().get(0);
		Html   html=(Html) pc.getChildren().get(0);
		Portallayout portLayout=(Portallayout)panel.getParent().getParent();
		if(panel.isMaximized()){
			pc.getChildren().clear();
			portLayout.setHeight(myHeight+"px");
			String HtmlContent =html.getContent();
			//check for values must not display
			if(!HtmlContent.contains("forceNoValues=&quot;1&quot;")){
				//make show values when panel maximized
				if(HtmlContent.contains("showValues=&quot;0&quot;")){
					HtmlContent=StringUtils.replace(HtmlContent, "showValues=&quot;0&quot;", "showValues=&quot;1&quot;");
				}
			}
			html.setContent(HtmlContent);
			pc.appendChild(html);
			panel.setWidth("100%");
			panel.setHeight(myHeight+"px");

		}else{
			pc.getChildren().clear();
			portLayout.setHeight("");
			String HtmlContent =html.getContent();
			if(HtmlContent.contains("showValues=&quot;1&quot;")){
				//make not to show values when panel minimized
				HtmlContent=StringUtils.replace(HtmlContent, "showValues=&quot;1&quot;", "showValues=&quot;0&quot;");
			}
			html.setContent(HtmlContent);
			pc.appendChild(html);
			panel.setHeight(myHeight/2+"px");
			panel.setOpen(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * 
	 * Event listener for panel maximize event
	 *
	 */
	class onPanelClosed implements EventListener{

		@Override
		public void onEvent(Event event) throws Exception {
			Event closeEvent = (Event) event; 
			Panel panel = (Panel) closeEvent.getTarget();
			Portallayout portLayout=(Portallayout)panel.getParent().getParent();
			portLayout.setHeight("");
		}
	}
	/**
	 * This method prepares  chart related data to ChartDetail object and returns it.
	 * @param aDashboardConfiguration
	 * @return
	 */
	public ChartDetail getChartDetail(DashboardConfiguration aDashboardConfiguration){
		logger.debug("Entering ");
		String chartStrXML="";
		ChartsConfig chartsConfig=new ChartsConfig(aDashboardConfiguration.getCaption(),aDashboardConfiguration.getSubCaption()
				,"","");
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		//check data retrieves from database or static XML data
		if(StringUtils.trimToEmpty(aDashboardConfiguration.getDataXML()).equals("")){
			//Get data from database and create XML String using result set by calling getLabelAndValues()
			chartStrXML=getLabelAndValues(aDashboardConfiguration);
		}else{
			//complete  chart XML  
			chartStrXML=aDashboardConfiguration.getDataXML();
		}
		ChartDetail chartDetail=new ChartDetail();
		chartDetail.setStrXML(chartStrXML);
		ChartUtil chartUtil=new ChartUtil();
		//Get Flash file against select type and dimension 2D or 3D/Pie or Bar like 
		chartDetail.setSwfFile(chartUtil.getSWFFileName(aDashboardConfiguration));
		logger.debug("Leaving ");
		return chartDetail;

	}
	/**
	 * This method returns labels and values of chart
	 * @param dashboardConfiguration
	 * @return
	 */
	public String getLabelAndValues(DashboardConfiguration dashboardConfiguration){
		logger.debug("Entering ");

		String whereCondition ="";
		dashboardConfiguration.setLovDescDataObject(dashboardConfiguration);
		//Retrieve data as list of ChartSetElement
		List<ChartSetElement> listSetElements=getDashboardConfigurationService()
		.getLabelAndValues(dashboardConfiguration,whereCondition);
		if(listSetElements!=null && listSetElements.size()>0){
			dashboardConfiguration.getLovDescChartsConfig().setSetElements(listSetElements);
			dashboardConfiguration.getLovDescChartsConfig().setRemarks(dashboardConfiguration.getRemarks());
		}
		
		if(chartUtil.isAGauge(dashboardConfiguration)){
			return dashboardConfiguration.getLovDescChartsConfig().getAGaugeXML();
		}else if(dashboardConfiguration.isDrillDownChart()){
			return dashboardConfiguration.getLovDescChartsConfig().getDrillDownChartXML();
		}else if(chartUtil.isMultiSeries(dashboardConfiguration)){
			return dashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(dashboardConfiguration.getRenderAs());
		}else{
			return dashboardConfiguration.getLovDescChartsConfig().getChartXML();
		}
	}

	public void setDashboardConfigurationService(
			DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}
	public DashboardConfigurationService getDashboardConfigurationService() {
		return dashboardConfigurationService;
	}
	public void setChartDetail(ChartDetail chartDetail) {
		this.chartDetail = chartDetail;
	}
	public ChartDetail getChartDetail() {
		return chartDetail;
	}
} 
