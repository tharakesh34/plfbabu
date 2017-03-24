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
 * FileName    		:  AuditLogServiceImpl.java												*                           
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

package com.pennant.backend.service.impl;

import java.util.List;

import com.pennant.backend.dao.audit.AuditLogDAO;
import com.pennant.backend.model.AuditLogDetils;
import com.pennant.backend.service.AuditLogService;


public class AuditLogServiceImpl implements AuditLogService{
	
	private AuditLogDAO auditLogDAO;
	
	public AuditLogServiceImpl() {
		super();
	}
	
	public List<AuditLogDetils> getLogDetails(String modName,String whereCond){
		return getAuditLogDAO().getLogDetails(modName, whereCond);	
	}

	public AuditLogDAO getAuditLogDAO() {
		return auditLogDAO;
	}

	public void setAuditLogDAO(AuditLogDAO auditLogDAO) {
		this.auditLogDAO = auditLogDAO;
	}
	@Override
	public List<AuditLogDetils> getLogDetails(String moduleName,String[] keyFields, String recordRole,long currentUser, Object beanObject) {
		return getAuditLogDAO().getLogDetails(moduleName,keyFields ,recordRole, currentUser, beanObject);
	}
	
	
	
}
