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
 * FileName    		:  AuditLogDetails.java													*                           
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
package com.pennant.backend.model;

import java.sql.Timestamp;

import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class AuditLogDetils extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6820550678752392418L;

	private long auditId;
	private Timestamp auditDate;
	private String auditImage;

	public AuditLogDetils() {
		super();
	}

	public long getAuditId() {
		return auditId;
	}

	public void setAuditId(long auditId) {
		this.auditId = auditId;
	}

	public Timestamp getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Timestamp auditDate) {
		this.auditDate = auditDate;
	}

	public String getAuditImage() {
		return auditImage;
	}

	public void setAuditImage(String auditImage) {
		this.auditImage = auditImage;
	}

}
