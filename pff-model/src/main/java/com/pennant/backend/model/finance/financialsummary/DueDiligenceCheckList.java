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
 *																							*
 * FileName    		:  CustomerDocument.java                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance.financialsummary;

import java.sql.Timestamp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custDocCategory", "custDocTitle", "custDocIssuedCountry", "custDocSysName", "custDocIssuedOn",
		"custDocExpDate", "docPurpose", "custDocName", "custDocType", "custDocImage", "docUri" })
@XmlAccessorType(XmlAccessType.NONE)
public class DueDiligenceCheckList extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6420966711989511378L;

	private long id = Long.MIN_VALUE;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String particulars;
	private String status;

	private boolean newRecord;
	private DueDiligenceCheckList befImage;

	public boolean isNew() {
		return isNewRecord();
	}

	public DueDiligenceCheckList() {
		super();
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DueDiligenceCheckList getBefImage() {
		return befImage;
	}

	public void setBefImage(DueDiligenceCheckList befImage) {
		this.befImage = befImage;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
