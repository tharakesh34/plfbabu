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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;

import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.model.reports.ReportListHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.reports.ReportListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PTListReportUtils implements Serializable {

	private static final long serialVersionUID = 8400638894656139790L;
	private static final Logger logger = Logger.getLogger(PTListReportUtils.class);
	
	private static ReportListService reportListService;

	public PTListReportUtils (){
		super();
	}

	public PTListReportUtils (String code, JdbcSearchObject<?> searchObj,int size) throws InterruptedException{
		super();
		
		logger.debug("Entering");
		ReportListHeader header = new ReportListHeader();
		JdbcSearchObject<ReportListDetail> searchObject = new JdbcSearchObject<ReportListDetail>(ReportListDetail.class);
		setReportListService() ;

		searchObject.setSorts(searchObj.getSorts());
		searchObject.setFilters(searchObj.getFilters());
		searchObject.addTabelName(searchObj.getTabelName());
		if(StringUtils.trimToNull(searchObj.getWhereClause()) != null){
			searchObject.addWhereClause(searchObj.getWhereClause());
		}
		
		//Report List Details Fetching
		ReportList reportList = getReportListService().getApprovedReportListById(code);
		searchObject.setMaxResults(size + 1);

		if(reportList !=null){
			
			String[] fields = reportList.getValues();
			String[] types = reportList.getType();
			
			if(reportList.isFormatReq()){
				searchObject.addField("ReportFormat");
			}

			for (int i = 0; i < fields.length; i++) {
				searchObject.addField(fields[i]+" AS field"+ types[i].substring(0, 1).toUpperCase()+ types[i].substring(1).trim()+StringUtils.leftPad(String.valueOf(i+1), 2,'0'));
			}	
			
			fields = null;
			types = null;

			header.setFiledLabel(reportList.getLabels());
			@SuppressWarnings("static-access")
			Map<String, Object> parameters = header.getReportListHeader(header);
			parameters = reportList.getMainHeaderDetails(parameters);
			
			//Set Report Images to parameter Fields
			parameters.put("organizationLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
			parameters.put("productLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			if ("DDARep".equals(code)) {//FIXME
				pagedListService = (PagedListService) SpringUtil.getBean("extPagedListService");
			}
			JRBeanCollectionDataSource listDetailsDS = new JRBeanCollectionDataSource( pagedListService.getBySearchObject(searchObject));

			String reportSrc = PathUtil.getPath(PathUtil.REPORTS_LIST);
			if (code.equals(Labels.getLabel("label_CheckList.value"))) {
				reportSrc = reportSrc + "/" + "CheckListReport.jasper";
			} else {
				reportSrc = reportSrc + "/" + reportList.getReportFileName() + ".jasper";
			}
			
			File file = null;
			try {
				file = new File(reportSrc) ;
				if(file.exists()){
					byte[] buf = null;
					buf=JasperRunManager.runReportToPdf(reportSrc,parameters,listDetailsDS);
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("reportBuffer", buf);
					map.put("reportName", reportList.getReportHeading());

					// call the ZUL-file with the parameters packed in a map
					Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul",null,map);
				}else{
					MessageUtil.showError(Labels.getLabel("message.error.reportNotImpl"));
				}
			} catch (JRException e) {
				MessageUtil.showError(e);
			}finally{
				
				fields = null;
				types= null;
				file = null;
				reportSrc= null;
				reportList = null;
				parameters= null;
				pagedListService = null;
				searchObject = null;
				header = null;
				listDetailsDS = null;
			}
		}else{
			// Display Error for Configuration
			MessageUtil.showError(Labels.getLabel("message.error.reportNotFound"));
		}
		logger.debug("Leaving");
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