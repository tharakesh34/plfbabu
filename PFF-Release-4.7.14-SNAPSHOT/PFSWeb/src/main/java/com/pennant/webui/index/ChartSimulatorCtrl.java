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
 * FileName    		:  ChartCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  31-04-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-04-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.index;

import java.util.Collections;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.webui.dashboard.dashboardconfiguration.DashboardConfigurationDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/Nationality/chart.zul file.
 */
public class ChartSimulatorCtrl extends GFCBaseCtrl<ChartDetail> {
	private static final long serialVersionUID = 5012323906026616623L;
	private static final Logger logger = Logger.getLogger(ChartSimulatorCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_chart_simulator;     // autoWired
	protected ChartDetail chartDetail; // autoWired
	protected Div    divFrame;
	protected Button btnClose;
	protected DashboardConfigurationDialogCtrl dashboardConfigurationDialogCtrl;

	// Component Events

	public ChartSimulatorCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Nationality object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_chart_simulator(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_chart_simulator);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("chartDetail")) {
			this.chartDetail = (ChartDetail) arguments.get("chartDetail");

		}
		if (arguments.containsKey("dashboardConfigurationDialogCtrl")) {
			this.dashboardConfigurationDialogCtrl = (DashboardConfigurationDialogCtrl)arguments.get("dashboardConfigurationDialogCtrl");

		}
		if(this.chartDetail!=null){
			doShowChart(this.chartDetail);
			this.dashboardConfigurationDialogCtrl.window_DashboardConfigurationDialog.setVisible(false);
			this.dashboardConfigurationDialogCtrl.window_DashboardConfigurationDialog.appendChild(this.window_chart_simulator);
		}
		setDialog(DialogType.EMBEDDED);
		logger.debug("Leaving ");
	}

	public void doShowChart(ChartDetail chartDetail){
		logger.debug("Entering ");

		if(this.chartDetail!=null){
				divFrame.setHeight("100%");
				// new code to display chart by skipping jsps
				String strXML = chartDetail.getStrXML(); 
				strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
				strXML = StringEscapeUtils.escapeJavaScript(strXML);
				chartDetail.setStrXML(strXML);

				Executions.createComponents("/Charts/Chart.zul", divFrame,
						Collections.singletonMap("chartDetail", chartDetail));
			}
			logger.debug("Leaving ");
	}
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			this.window_chart_simulator.onClose();
			this.dashboardConfigurationDialogCtrl.window_DashboardConfigurationDialog.setVisible(true);
			//This code is fix for fire fox .for code mirror became disable after any event .
			Codemirror remarks=(Codemirror) this.dashboardConfigurationDialogCtrl.window_DashboardConfigurationDialog.getFellow("remarks");
			remarks.setReadonly(false);
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	

} 
