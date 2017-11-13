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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zkmax.zul.Fusionchart;
import org.zkoss.zkmax.zul.Portalchildren;
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
 * This is the controller class for the
 * /WEB-INF/pages/DashboardDetails/DashboardDetail/dashboardDetailDialog.zul
 * file.
 */

public class DashboardCreate extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = -4201689911130684236L;
	private static final Logger logger = Logger.getLogger(DashboardCreate.class);
	protected Fusionchart fusionchart;
	public ChartDetail chartDetail;
	private int height = getContentAreaHeight() * 85/100 /2;
	ChartUtil chartUtil = new ChartUtil();
	private DashboardConfigurationService dashboardConfigurationService;
	
	private Map<String, DashboardConfiguration> chartMap = new HashMap<String, DashboardConfiguration>();
	boolean isMaximized = false;

	/**
	 * Creates a new panel and inserts the Dash boards in to it.
	 * 
	 * @param info
	 * @return
	 */
	public synchronized void createPanel(DashboardConfiguration info, Portalchildren portalchildren) {
		logger.debug("Entering ");

		Panel panel = new Panel();
		panel.setId(info.getDashboardCode());		
		chartMap.put(panel.getId(), info);
		
		isMaximized = false;
		panel.addEventListener(Events.ON_MAXIMIZE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Panel panel = (Panel)event.getTarget();
				
				MaximizeEvent  maximizeEvent = (MaximizeEvent)event;
				isMaximized = maximizeEvent.isMaximized();
				
				Portalchildren pc = (Portalchildren) event.getTarget().getParent();
				
				createComponenet(chartMap.get(panel.getId()), pc, panel);
			}
		});
				
		createComponenet(info, portalchildren, panel);
	}

	private void createComponenet(DashboardConfiguration info, Portalchildren portalchildren, Panel panel) {
		panel.setTitle(info.getDashboardDesc());
		panel.setBorder("normal");
		panel.setStyle("padding:1px;");
		panel.setCollapsible(true);
		panel.setClosable(true);
		panel.setMaximizable(true);
		panel.setHeight(height+ "px");
		
		Panelchildren panelchildren = null;
		if(panel.getFirstChild() != null && panel.getFirstChild() instanceof Panelchildren) {
			panelchildren = (Panelchildren) panel.getFirstChild();
		} else {
			panelchildren = new Panelchildren();
			panel.appendChild(panelchildren);
			portalchildren.appendChild(panel);
		}
			

		chartDetail = getChartDetail(info);
	
		String strXML = chartDetail.getStrXML();
		strXML = StringEscapeUtils.escapeJavaScript(strXML);

		chartDetail.setStrXML(strXML);
		chartDetail.setChartId(info.getDashboardCode());
		
		chartDetail.setChartWidth("100%");		
		if(isMaximized) {
			chartDetail.setChartHeight(height + 160 + "px");
		} else {
			chartDetail.setChartHeight(height - 80 + "px");
		} 
		
		chartDetail.setiFrameHeight("100%");
		chartDetail.setiFrameWidth("100%");

		setChartDetail(chartDetail);
		
		if(panelchildren != null && panelchildren.getChildren() != null) {
			panelchildren.getChildren().clear();
		}

		Executions.createComponents("/Charts/FusionChart.zul", panelchildren, Collections.singletonMap("chartDetail", chartDetail));
	}

	/**
	 * This method prepares chart related data to ChartDetail object and returns
	 * it.
	 * 
	 * @param aDBConfig
	 * @return
	 */
	public ChartDetail getChartDetail(DashboardConfiguration aDBConfig) {
		logger.debug("Entering ");
		String chartStrXML = "";
		ChartsConfig chartsConfig = new ChartsConfig(aDBConfig.getCaption(), aDBConfig.getSubCaption(), "", "");
		aDBConfig.setLovDescChartsConfig(chartsConfig);
		
		if (StringUtils.isBlank(aDBConfig.getDataXML())) {
			chartStrXML = getLabelAndValues(aDBConfig);
		} else {
			chartStrXML = aDBConfig.getDataXML();
		}
		
		ChartDetail chartDetail = new ChartDetail();
		chartDetail.setStrXML(chartStrXML);
		ChartUtil chartUtil = new ChartUtil();
		chartDetail.setSwfFile(chartUtil.getSWFFileName(aDBConfig));
		
		logger.debug("Leaving ");
		return chartDetail;

	}

	/**
	 * This method returns labels and values of chart
	 * 
	 * @param dbConfig
	 * @return
	 */
	public String getLabelAndValues(DashboardConfiguration dbConfig) {
		logger.debug("Entering ");

		String whereCondition = "";
		dbConfig.setLovDescDataObject(dbConfig);
		List<ChartSetElement> listSetElements = getDashboardConfigurationService().getLabelAndValues(dbConfig,
				whereCondition, getUserWorkspace().getLoggedInUser(), getUserWorkspace().getSecurityRoles());
		if (listSetElements != null && listSetElements.size() > 0) {
			dbConfig.getLovDescChartsConfig().setSetElements(listSetElements);
			dbConfig.getLovDescChartsConfig().setRemarks(dbConfig.getRemarks());
		}

		if (chartUtil.isAGauge(dbConfig)) {
			return dbConfig.getLovDescChartsConfig().getAGaugeXML();
		} else if (dbConfig.isDrillDownChart()) {
			return dbConfig.getLovDescChartsConfig().getDrillDownChartXML();
		} else if (chartUtil.isMultiSeries(dbConfig)) {
			return dbConfig.getLovDescChartsConfig().getSeriesChartXML(dbConfig.getRenderAs());
		} else {
			return dbConfig.getLovDescChartsConfig().getChartXML();
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
