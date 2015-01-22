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
 * FileName    		:  ReportGenerationPromptDialogCtrl.java                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012   														*
 *                                                                  						*
 * Modified Date    :  23-09-2012      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012         Pennant	                 0.1                                        * 
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

package com.pennant.webui.reports;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/reports/DealWiseIncomeReport.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DealWiseIncomeReportCtrl extends  GFCBaseListCtrl<ReportConfiguration> implements Serializable {

	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = Logger.getLogger(DealWiseIncomeReportCtrl.class);

	protected Window         window_DealWiseIncomeReportCtrl;
	
	private Date dateValueDate = null;
	private DailyDownloadInterfaceService dailyDownloadInterfaceService; 
	protected Tabbox         tabbox;

	/** 
	 * On creating Window 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DealWiseIncomeReportCtrl(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try{

			tabbox = (Tabbox)event.getTarget().getParent().getParent().getParent();

			dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());

			Date prvMnthStartDate = DateUtility.getMonthStartDate(DateUtility.addDays(DateUtility.getMonthStartDate(dateValueDate), -1));

			//Saving new Income Account Transaction Details From Core System
			getDailyDownloadInterfaceService().processIncomeAccTransactions(prvMnthStartDate);

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("MonthEndReportEvent", event);
			Window   child_Window = (Window) Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, map);
			child_Window.onClose();
		}catch (Exception e) {
			logger.error("Error while creating Window"+e.toString());
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_ReportConfiguredError.error"));
			doClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	
	/**
	 * This method closes the Window
	 */
	public  void doClose(){
		logger.debug("Entering");
		try {
			this.window_DealWiseIncomeReportCtrl.onClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
	}


	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}

	public void setDailyDownloadInterfaceService(
			DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}
	
}
