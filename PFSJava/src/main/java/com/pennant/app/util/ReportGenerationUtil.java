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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.util.PennantConstants;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

public class ReportGenerationUtil implements Serializable {
    private static final long serialVersionUID = 7293149519883033383L;
	private final static Logger logger = Logger.getLogger(ReportGenerationUtil.class);
	
	public ReportGenerationUtil() {
	    super();
    }
	
	@SuppressWarnings("rawtypes")
	public static boolean generateReport(String reportName, Object object,List listData, boolean isRegenerate, 
			int reportType,String userName, Window window) throws InterruptedException{
		return generateReport(reportName, object, listData, isRegenerate, reportType, userName, window, false);
	}

	@SuppressWarnings("rawtypes")
	public static boolean generateReport(String reportName, Object object,List listData, boolean isRegenerate, 
			int reportType,String userName, Window window,boolean createExcel) throws InterruptedException {

		logger.debug("Entering");

		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_FINANCE)+ "/" +reportName+ ".jasper";

        if (isRegenerate) {
        	try {
        		createReport(reportName, object,listData, reportSrc,userName,window,createExcel);
        	} catch (JRException e) {
        		logger.error("Exception: ", e);
        		Messagebox.show("Template does not exist.", Labels.getLabel("message.Information"),Messagebox.OK, Messagebox.INFORMATION);
        		ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41006", null, null), "EN");
        	}
        }

		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method For generating Report based upon passing Data Structure
	 * @param reportName
	 * @param object
	 * @param listData
	 * @param reportSrc
	 * @param userName
	 * @param dialogWindow
	 * @throws JRException
	 * @throws InterruptedException
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void createReport(String reportName, Object object,List listData, String reportSrc,String userName,Window dialogWindow,boolean createExcel)
			throws JRException, InterruptedException {
		logger.debug("Entering");

		try {
			JRBeanCollectionDataSource subListDS;
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			// Generate the main report data source
			List mainList = new ArrayList();
			mainList.add(object);

			JRBeanCollectionDataSource mainDS = new JRBeanCollectionDataSource(mainList);
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
			parameters.put("organizationLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			parameters.put("productLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

			if("FINENQ_BulkDifferemmentDetails".equals(reportName)){
				String recalType=((BulkProcessHeader)object).getReCalType();
				if(recalType.equals(CalculationConstants.RPYCHG_ADJMDT) || recalType.equals(CalculationConstants.RPYCHG_ADDTERM)){
					parameters.put("recalTypeSubParm","T");
				}
			}
			
			if(createExcel){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				String printfileName = JasperFillManager.fillReportToFile(reportSrc, parameters, mainDS);
				JRXlsExporter excelExporter = new JRXlsExporter();

				excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,printfileName); 
				excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);  
				excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);  
				excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE); 
				excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE); 
				excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS,Boolean.FALSE);  
				excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER,Boolean.FALSE);       
				excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN,Boolean.TRUE);
				excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
				excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
				excelExporter.exportReport();
				Filedownload.save(new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
			}else{

				byte[] buf = null;
				logger.debug("Entering JasperRunManager");
				buf =JasperRunManager.runReportToPdf(reportSrc, parameters,	mainDS);

				logger.debug("Leaving JasperRunManager");
				final HashMap<String, Object> auditMap = new HashMap<String, Object>();
				auditMap.put("reportBuffer", buf);
				String genReportName = Labels.getLabel(reportName);
				auditMap.put("reportName", StringUtils.isBlank(genReportName) ? reportName : genReportName);
				if (dialogWindow != null){
					auditMap.put("dialogWindow", dialogWindow);
				}

				logger.debug("Adding to zul");

				// call the ZUL-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
				logger.debug("Completed adding to zul");
			}
		} catch (JRException e) {
			logger.error("Exception: ", e);
			Messagebox.show("Template does not exist.", Labels.getLabel("message.Information"),Messagebox.OK, Messagebox.INFORMATION);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Printing Checks
	 * @param listData
	 * @param reportName
	 * @param userName
	 * @param dialogWindow
	 * @throws JRException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
    public static void print(List listData,String reportName,String userName, Window dialogWindow) throws JRException, FileNotFoundException {
		logger.debug("Entering");
		
		JasperPrint jasperPrint=null;
		byte[] buf = null;

		try {
			JRBeanCollectionDataSource subListDS = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			String reportSrc = PathUtil.getPath(PathUtil.REPORTS_CHECKS)+"/ChecksMain.jasper";
			
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
			parameters.put("organizationLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			parameters.put("productLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

			logger.debug("Entering JasperRunManager");
			logger.debug("Report Path : "+reportSrc);
			buf =JasperRunManager.runReportToPdf(reportSrc, parameters,	subListDS);
			logger.debug("Leaving JasperRunManager");
			
			final HashMap<String, Object> auditMap = new HashMap<String, Object>();
			auditMap.put("reportBuffer", buf);
			if (dialogWindow != null){
				auditMap.put("dialogWindow", dialogWindow);
			}
			
			logger.debug("Adding to zul");
			
			// call the ZUL-file with the parameters packed in a map
			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
			logger.debug("Completed adding to zul");

			logger.debug("Leaving JasperRunManager");

		} catch (JRException e) {
			logger.error("Exception: ", e);
			Messagebox.show(e.toString());
		}finally{
			buf = null;
		}
		logger.debug("Leaving");
	}
	

}
