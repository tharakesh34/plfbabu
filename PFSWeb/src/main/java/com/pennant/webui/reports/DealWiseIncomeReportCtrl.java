/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ReportGenerationPromptDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-09-2012
 * * * Modified Date : 23-09-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-09-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/reports/DealWiseIncomeReport.zul file.
 */
public class DealWiseIncomeReportCtrl extends GFCBaseCtrl<ReportConfiguration> {
	private static final long serialVersionUID = 4678287540046204660L;
	private static final Logger logger = LogManager.getLogger(DealWiseIncomeReportCtrl.class);

	protected Window window_DealWiseIncomeReportCtrl;

	private Date dateValueDate = null;
	private DailyDownloadInterfaceService dailyDownloadInterfaceService;
	protected Tabbox tabbox;

	public DealWiseIncomeReportCtrl() {
		super();
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 */
	public void onCreate$window_DealWiseIncomeReportCtrl(Event event) {
		logger.debug("Entering" + event.toString());

		try {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();

			dateValueDate = SysParamUtil.getAppDate();

			Date prvMnthStartDate = DateUtil
					.getMonthStart(DateUtil.addDays(DateUtil.getMonthStart(dateValueDate), -1));

			// Saving new Income Account Transaction Details From Core System
			getDailyDownloadInterfaceService().processIncomeAccTransactions(prvMnthStartDate);

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("MonthEndReportEvent", event);
			Window child_Window = (Window) Executions
					.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, map);
			child_Window.onClose();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}

	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}

	public void setDailyDownloadInterfaceService(DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}

}
