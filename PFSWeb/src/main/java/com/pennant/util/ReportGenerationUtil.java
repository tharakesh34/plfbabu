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
 *
 * FileName    		:  ReportGenerationUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.util;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportCreationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.enquiry.PostingsEnquiryDialogCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportGenerationUtil implements Serializable {
	private static final long serialVersionUID = 7293149519883033383L;
	private static Logger logger = LogManager.getLogger(PostingsEnquiryDialogCtrl.class);

	public ReportGenerationUtil() {
		super();
	}

	public static void generateReport(String reportName, Object object, List listData, int reportType, String userName,
			Window window) {
		logger.info(Literal.ENTERING + reportName);

		try {

			if (window != null) {
				ReportCreationUtil.showPDF(reportName, object, listData, userName, window);
			} else {
				ReportCreationUtil.showPDF(reportName, object, listData, userName);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError("Template does not exist.");
			ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
		}

	}

	public static void generateReport(String reportName, Object object, List listData, String userName, Window window) {
		try {

			ReportCreationUtil.downloadExcel(reportName, object, listData, userName);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError("Template does not exist.");
			ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
		}
		logger.debug(Literal.LEAVING);
	}

	public static void print(List listData, String reportName, String userName, Window dialogWindow)
			throws JRException, FileNotFoundException {
		logger.debug("Entering");

		try {
			JRBeanCollectionDataSource subListDS = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			String reportSrc = PathUtil.getPath(PathUtil.REPORTS_CHECKS) + "/ChecksMain.jasper";

			for (int i = 0; i < listData.size(); i++) {

				Object obj = listData.get(i);
				if (obj instanceof List) {
					subListDS = new JRBeanCollectionDataSource((List) obj);
				} else {
					List subList = new ArrayList();
					subList.add(obj);
					subListDS = new JRBeanCollectionDataSource(subList);
				}
				parameters.put("subDataSource" + (i + 1), subListDS);
			}

			// Set the parameters
			parameters.put("userName", userName);
			parameters.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			parameters.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

			byte[] buf = JasperRunManager.runReportToPdf(reportSrc, parameters, subListDS);

			final Map<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("reportBuffer", buf);
			if (dialogWindow != null) {
				auditMap.put("dialogWindow", dialogWindow);
			}

			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
		} catch (JRException e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public static void generateReport(String userName, String reportName, String whereCond,
			StringBuilder searchCriteriaDesc) {
		logger.info(Literal.ENTERING);

		ReportCreationUtil.downloadExcel(PathUtil.REPORTS_ORGANIZATION, reportName, userName, whereCond,
				searchCriteriaDesc, ReportCreationUtil.getConnection());

		logger.info(Literal.LEAVING);
	}

}
