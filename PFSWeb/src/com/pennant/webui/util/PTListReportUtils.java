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
 * FileName    		:  PTReportUtils.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
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

package com.pennant.webui.util;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.model.reports.ReportListHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.reports.ReportListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.systemmasters.academic.AcademicListCtrl;

public class PTListReportUtils implements Serializable {

	private static final long serialVersionUID = 8400638894656139790L;
	private final static Logger logger = Logger.getLogger(AcademicListCtrl.class);
	
	JdbcSearchObject<ReportListDetail> searchObject;
	private static ReportListService reportListService;

	public PTListReportUtils (){
		super();
	}

	public PTListReportUtils (String module, JdbcSearchObject<?> searchObj,int size) throws InterruptedException{
		super();
		
		logger.debug("Entering");
		ReportListHeader header = new ReportListHeader();
		searchObject = new JdbcSearchObject<ReportListDetail>(ReportListDetail.class);
		setReportListService() ;

		this.searchObject.setSorts(searchObj.getSorts());
		this.searchObject.setFilters(searchObj.getFilters());
		this.searchObject.addTabelName(searchObj.getTabelName());
		ReportList reportList = getReportListDetails(module);
		searchObject.setMaxResults(size + 1);

		if(reportList !=null){
			
			String[] fields = reportList.getValues();
			String[] types = reportList.getType();

			for (int i = 0; i < fields.length; i++) {
				this.searchObject.addField(fields[i]+" AS field"+ types[i].substring(0, 1).toUpperCase()+ types[i].substring(1).trim()+StringUtils.leftPad(String.valueOf(i+1), 2,'0'));
			}	

			header.setFiledLabel(reportList.getLabels());
			@SuppressWarnings("static-access")
			Map<String, Object> parameters = header.getReportListHeader(header);

			parameters = reportList.getMainHeaderDetails(parameters);
			
			//Set Report Images to parameter Fields
			if(PennantConstants.server_OperatingSystem.equals("LINUX")){
				parameters.put("organizationLogo",SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_ORG_LOGO_PATH").toString());
				parameters.put("productLogo",SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_PRODUCT_LOGO_PATH").toString());
			}else{
				parameters.put("organizationLogo",SystemParameterDetails.getSystemParameterValue("REPORTS_ORG_LOGO_PATH").toString());
				parameters.put("productLogo",SystemParameterDetails.getSystemParameterValue("REPORTS_PRODUCT_LOGO_PATH").toString());
			}

			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			List<ReportListDetail> reportData = pagedListService.getBySearchObject(searchObject);
			header.setListDetails(reportData);

			JRBeanCollectionDataSource ListDetailsDS = new JRBeanCollectionDataSource(reportData);

			String reportSrc;
			if(module.equals(Labels.getLabel("label_CheckList.value"))){

				reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_LIST_PATH").toString()+"/"+"CheckListReport.jasper";
				if(PennantConstants.server_OperatingSystem.equals("LINUX")){
					reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LIST_PATH").toString()+ "/" +"CheckListReport.jasper";
				}
				reportList.getLabels();
			} else if(module.equals(Labels.getLabel("label_SecurityRole.value"))){
				reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_LIST_PATH").toString()+"/"+"SecRoles.jasper";
				if(PennantConstants.server_OperatingSystem.equals("LINUX")){
					reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LIST_PATH").toString()+ "/" +"CheckListReport.jasper";
				}
				reportList.getLabels();
			} else {

				reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_LIST_PATH").toString()+"/"+reportList.getReportFileName()+ ".jasper";
				if(PennantConstants.server_OperatingSystem.equals("LINUX")){
					reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_LIST_PATH").toString()+ "/" +reportList.getReportFileName()+ ".jasper";
				}
				reportList.getLabels();
			}
			try {
				File file = new File(reportSrc) ;
				if(file.exists()){
					byte[] buf = null;
					buf=JasperRunManager.runReportToPdf(reportSrc,parameters,ListDetailsDS);
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("reportBuffer", buf);

					// call the ZUL-file with the parameters packed in a map
					Executions.createComponents("/WEB-INF/pages/Reports/reports.zul",null,map);
				}else{
					PTMessageUtils.showErrorMessage("Not Yet Implemented !!!");
				}
			} catch (JRException e) {
				PTMessageUtils.showErrorMessage(e.toString());
				//e.printStackTrace();
			}
		}else{
			// Display Error for Configuration
			PTMessageUtils.showErrorMessage("Error in Configuring the " +module+ " report");
		}
		logger.debug("Leaving");
	}

	private ReportList getReportListDetails(String module){
		ReportList reportList = getReportListService().getApprovedReportListById(module);
		return reportList;
	}


	public static ReportListService getReportListService() {
		return reportListService;
	}

	public void setReportListService() {
		if(reportListService == null){
			PTListReportUtils.reportListService = (ReportListService) SpringUtil.getBean("reportListService");
		}
	}

}