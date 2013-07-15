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
package com.pennant.app.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;

public class ReportGenerationUtil implements Serializable {
	
    private static final long serialVersionUID = 7293149519883033383L;
	private final static Logger logger = Logger.getLogger(ReportGenerationUtil.class);
	
	private transient static FinanceDetailService financeDetailService;
	private static String path;

	@SuppressWarnings("rawtypes")
	public static boolean generateReport(String reportName, Object object,List listData, boolean isRegenerate, 
			int reportType,String userName, Window window) throws InterruptedException {

		logger.debug("Entering");

		String reportSrc = "";
		File filePath;
		path = SystemParameterDetails.getSystemParameterValue("REPORTS_LOAN_PDF_PATH").toString()+ "/"+ reportName + ".pdf";
		if(PennantConstants.server_OperatingSystem.equals("LINUX")){
			path = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LOAN_PDF_PATH").toString()+ "/"+ reportName + ".pdf";
		}

		// Check if folder/file exist or not,if folder/file doesn't exist create a new folder
		// and file and add data to it.If folder exist add file to the folder.
		filePath = new File(path);
		if (!filePath.exists()) {
			File directory = new File(SystemParameterDetails.getSystemParameterValue("REPORTS_LOAN_PDF_PATH").toString());
			if(PennantConstants.server_OperatingSystem.equals("LINUX")){
				directory = new File(SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LOAN_PDF_PATH").toString());

			}
			directory.mkdir();
			File file = new File(reportName + ".pdf");
			path = directory + "/" + file;
		}

		// Fetch report Name based on the aggrementCode
		// If not Found throw error;
		try {
			reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_LOAN_JASPER_PATH").toString()+ "/" +reportName+ ".jasper";
			if(PennantConstants.server_OperatingSystem.equals("LINUX")){			
				
			reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LOAN_JASPER_PATH").toString()+ "/" +reportName+ ".jasper";
			}

			// Check for report existing in the defined path for the reference;
			// if not found save the report in the path after generation
			File findFile = new File(path);
			if (!findFile.isFile()) {
				createReport(object,listData, reportSrc,userName,window);
				isRegenerate = false;
			}

			// if found save the record in the path and isRegenerate is true;
			// delete the existing record () and generate new report and save it. if
			// any error while delete then send error message.
			if (findFile.isFile()) {
				if (isRegenerate) {
					//findFile.delete();
					try {
						createReport(object,listData, reportSrc,userName,window);
					} catch (Exception e) {
						Messagebox.show(e.toString());
						ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
					}
				}
			}
		} catch (JRException e) {
			e.printStackTrace();
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * 
	 * @param finMain
	 * @throws JRException
	 * @throws InterruptedException 
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void createReport(Object object,List listData, String reportSrc,String userName,Window dialogWindow)
			throws JRException, InterruptedException {
		logger.debug("Entering ");

		try {
			JRBeanCollectionDataSource subListDS;
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			// Generate the main report data source
			List mainList = new ArrayList();
			mainList.add(object);

			JRBeanCollectionDataSource mainDS = new JRBeanCollectionDataSource(mainList);
			
			//UnComment after verification of all Reports -- TODO
			for (int i = 0; i < listData.size(); i++) {
				
				Object obj = listData.get(i);	            
				if(obj instanceof List){
					subListDS = new JRBeanCollectionDataSource((List) obj);
				}else {
					List subList = new ArrayList();
					subList.add(obj);
					subListDS = new JRBeanCollectionDataSource(subList);
				}
				
				parameters.put("subDataSource"+(i+1), subListDS);
            }

			// Set the parameters
			parameters.put("userName", userName);
			parameters.put("organizationLogo",SystemParameterDetails.getSystemParameterValue("REPORTS_ORG_LOGO_PATH").toString());
			parameters.put("productLogo",SystemParameterDetails.getSystemParameterValue("REPORTS_PRODUCT_LOGO_PATH").toString());

			byte[] buf = null;
			logger.debug("Entering JasperRunManager");
			buf =JasperRunManager.runReportToPdf(reportSrc, parameters,	mainDS);
			
			logger.debug("Leaving JasperRunManager");
			final HashMap<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("reportBuffer", buf);
			if (dialogWindow != null){
				auditMap.put("dialogWindow", dialogWindow);
			}
			
			logger.debug("Adding to zul");
			
			// call the ZUL-file with the parameters packed in a map
			Executions.createComponents("/WEB-INF/pages/Reports/reports.zul", null, auditMap);
			logger.debug("Completed adding to zul");

		} catch (JRException jex) {
			logger.error(jex);
			Messagebox.show(jex.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for generating Static data of reports
	 * @param reportName
	 * @param object
	 * @param listData
	 * @param isRegenerate
	 * @param reportType
	 * @param userName
	 * @param window
	 * @return
	 * @throws InterruptedException
	 */
	public static boolean generateAgreementReport(String reportName,boolean isRegenerate, 
			int reportType,String userName, Window window) throws InterruptedException {

		logger.debug("Entering");

		String reportSrc = "";
		// Fetch report Name based on the aggrementCode
		// If not Found throw error;
		try {
			reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_AGRMNT_PATH").toString()+ "/" +reportName+ ".jasper";
			if(PennantConstants.server_OperatingSystem.equals("LINUX")){
				reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LOAN_JASPER_PATH").toString()+ "/" +reportName+ ".jasper";
			}
			// Check for report existing in the defined path for the reference;
			// if not found save the report in the path after generation
			File findFile = new File(reportSrc);
			if (findFile.exists()) {
				byte[] buf = null;
				buf =JasperRunManager.runReportToPdf(reportSrc, new HashMap<String, Object>(),	new JREmptyDataSource());
				isRegenerate = false;
				final HashMap<String, Object> agrMap = new HashMap<String, Object>();
				agrMap.put("reportBuffer", buf);
				if (window != null){
					agrMap.put("dialogWindow", window);
				}

				// call the ZUL-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/Reports/reports.zul", null, agrMap);
			}else{
				Messagebox.show("Not Yet Implemented !!!", "ERROR", Messagebox.OK,Messagebox.ERROR );
			}
		} catch (JRException e) {
			e.printStackTrace();
		}

		/*if (reportType == PennantConstants.REPORT_OPEN) {
			// Open the report
			try {

				Desktop desktop = null;
				if (Desktop.isDesktopSupported()) {
					desktop = Desktop.getDesktop();
				}
				desktop.open(new File(path));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			// Printer the report

		}*/
		logger.debug("Leaving");
		return false;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public static FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(
			FinanceDetailService financeDetailService) {
		ReportGenerationUtil.financeDetailService = financeDetailService;
	}

}
