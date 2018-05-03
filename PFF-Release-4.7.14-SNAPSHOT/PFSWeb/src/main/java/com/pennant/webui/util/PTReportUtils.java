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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;

import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PTReportUtils implements Serializable {

	private static final long serialVersionUID = 8400638894656139790L;
	private static final Logger logger = Logger.getLogger(PTReportUtils.class);
	@SuppressWarnings("rawtypes")
	public static void getPTReport(String model, List searchObj, int maxResults) throws InterruptedException {
		logger.debug("Entering ");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("list", searchObj);
		map.put("reportName", model);
		map.put("maxcount", maxResults);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}	
	
	public static void getReport(String model, JdbcSearchObject<?> searchObj) throws InterruptedException {
		PagedListService listService=(PagedListService) SpringUtil.getBean("pagedListService");		
		getPTReport(model,listService.getBySearchObject(searchObj),searchObj.getMaxResults());		
	}

}