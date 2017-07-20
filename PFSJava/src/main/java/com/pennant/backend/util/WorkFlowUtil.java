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
 * FileName    		:  WorkFlowUtil.java													*                           
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

package com.pennant.backend.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;

public class WorkFlowUtil {

	private static WorkFlowDetailsService workFlowDetailsService;

	private static Map<String, WorkFlowDetails> workFlowMap = null;

	public static void init(){
		workFlowMap=null;
	}
	
	public static WorkFlowDetails getWorkflow(long id) {
		return workFlowDetailsService.getWorkFlowDetailsByID(id);
	}

	public static WorkFlowDetails getDetailsByType(String workFlowType){

		if (workFlowMap==null){
			loadWorkFlowData();
		}

		return workFlowDetailsService.getWorkFlowDetailsByFlowType(workFlowType);
	}


	public static WorkFlowDetails getWorkFlowDetails(String moduleName) {
		WorkFlowDetails workFlowDetails = null;
		
		if("".equals(moduleName)) {
			return null; // TODO  Workflow setting shoud be removed in all the DAO's
		}

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(moduleName);
		if (moduleMapping != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(moduleMapping.getWorkflowType());

		}
		return workFlowDetails;
	}

	public static long getWorkFlowID(String workFlowType){
		long workflowId=0;
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(workFlowType);
		if (moduleMapping!=null){
			WorkFlowDetails workFlowDetails =  WorkFlowUtil.getDetailsByType(moduleMapping.getWorkflowType());
			if (workFlowDetails!=null){
				workflowId = workFlowDetails.getWorkFlowId();	
			}
		}
		return workflowId;
	}

	public void setWorkFlowDetailsService(WorkFlowDetailsService workFlowDetailsService) {
		WorkFlowUtil.workFlowDetailsService = workFlowDetailsService;
	}

	public static WorkFlowDetailsService getWorkFlowDetailsService() {
		return workFlowDetailsService;
	}
	
	public static void loadWorkFlowData(WorkFlowDetails workFlowDetails) {
		if (workFlowMap != null) {
			
			if (workFlowMap.containsKey(workFlowDetails.getWorkFlowType())) {
				workFlowMap.remove(workFlowDetails.getWorkFlowType());
			}
			
			workFlowMap.put(workFlowDetails.getWorkFlowType(), workFlowDetails);
		}
	}

	private static  void loadWorkFlowData(){
		workFlowMap = new HashMap<String, WorkFlowDetails>() {
			private static final long serialVersionUID = -3549857310897774789L;
			{
				WorkFlowDetailsService workFlowDetailsService = getWorkFlowDetailsService();
				if (workFlowDetailsService!=null) {
	                List<WorkFlowDetails> list = getWorkFlowDetailsService().getActiveWorkFlowDetails();
	                if (list != null) {
		                for (int i = 0; i < list.size(); i++) {
			                WorkFlowDetails workFlowDetails = list.get(i);
			                put(workFlowDetails.getWorkFlowType(), workFlowDetails);
		                }
	                }
                }

			}
		};

	}

}
