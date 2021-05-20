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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportCreationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.enquiry.PostingsEnquiryDialogCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

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
		logger.debug("Entering");
		
		
		String reportSrc = ReportCreationUtil.getTemplate(PathUtil.REPORTS_ORGANIZATION, reportName);
		

		Connection connection = null;
		DataSource dataSourceObj = null;

		try {

			dataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
			connection = dataSourceObj.getConnection();

			Map<String, Object> reportArgumentsMap = new HashMap<String, Object>(5);
			reportArgumentsMap.put("userName", userName);
			reportArgumentsMap.put("reportHeading", reportName);
			reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
			reportArgumentsMap.put("appDate", SysParamUtil.getAppDate());
			reportArgumentsMap.put("appCcy", SysParamUtil.getAppCurrency());
			reportArgumentsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
			reportArgumentsMap.put("unitParam", "Pff");
			reportArgumentsMap.put("whereCondition", whereCond);
			reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
			reportArgumentsMap.put("bankName", Labels.getLabel("label_ClientName"));
			reportArgumentsMap.put("searchCriteria", searchCriteriaDesc.toString());

			Connection con = null;
			DataSource reportDataSourceObj = null;

			try {

				reportDataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
				con = reportDataSourceObj.getConnection();

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);
				JRXlsExporter excelExporter = new JRXlsExporter();
				excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printfileName);
				excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
				excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
				excelExporter.exportReport();
				Filedownload
						.save(new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			connection = null;
			dataSourceObj = null;
		}

		logger.debug("Leaving");
	}

	// #PSD:152141 UAT2: Users:Report: Indaas accounting report not available --END
	public static void generateReport(String userName, String reportName, String whereCond,
			StringBuilder searchCriteriaDesc, Window window, boolean createExcel, String fromDate) {
		logger.debug("Entering");

		Connection connection = null;
		DataSource dataSourceObj = null;

		try {

			dataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
			connection = dataSourceObj.getConnection();

			HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(5);
			reportArgumentsMap.put("userName", userName);
			reportArgumentsMap.put("reportHeading", reportName);
			reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
			reportArgumentsMap.put("appDate", SysParamUtil.getAppDate());
			reportArgumentsMap.put("appCcy", SysParamUtil.getValueAsString("APP_DFT_CURR"));
			reportArgumentsMap.put("appccyEditField", SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT));
			reportArgumentsMap.put("unitParam", "Pff");
			reportArgumentsMap.put("whereCondition", whereCond);
			reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
			reportArgumentsMap.put("bankName", Labels.getLabel("label_ClientName"));
			reportArgumentsMap.put("searchCriteria", searchCriteriaDesc.toString());
			reportArgumentsMap.put("fromDate", fromDate);

			String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ORGANIZATION) + "/" + reportName + ".jasper";

			Connection con = null;
			DataSource reportDataSourceObj = null;

			try {
				File file = new File(reportSrc);
				if (!file.exists()) {
					MessageUtil.showError(
							String.format("%s report not configured, please contact system administrator", reportName));
					return;
				}

				logger.debug("Buffer started");

				reportDataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
				con = reportDataSourceObj.getConnection();

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);
				JRXlsExporter excelExporter = new JRXlsExporter();
				excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printfileName);
				excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
				excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
				excelExporter.exportReport();
				Filedownload
						.save(new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} catch (SQLException e1) {
			logger.error(e1.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			connection = null;
			dataSourceObj = null;
		}

		logger.debug("Leaving");
	}
}
