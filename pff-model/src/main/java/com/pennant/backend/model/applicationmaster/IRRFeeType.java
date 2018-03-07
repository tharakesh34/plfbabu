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
 * FileName    		:  IRRFeeType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2017    														*
 *                                                                  						*
 * Modified Date    :  21-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>IRRFeeType table</b>.<br>
 *
 */
public class IRRFeeType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long iRRID = Long.MIN_VALUE;
	private long feeTypeID;
	private String feeTypeCode;
	private String feeTypeDesc;
	private String iRRIDName;
	private BigDecimal feePercentage;
	private boolean newRecord=false;
	private String lovValue;
	private IRRFeeType befImage;
	private  LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public IRRFeeType() {
		super();
	}

	public IRRFeeType(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("iRRIDName");
		excludeFields.add("feeTypeIDName");
		excludeFields.add("feeTypeIDNameDesc");
		excludeFields.add("feeTypeDesc");
		excludeFields.add("feeTypeCode");
		return excludeFields;
	}

	public long getId() {
		return iRRID;
	}

	public void setId (long id) {
		this.iRRID = id;
	}
	public long getIRRID() {
		return iRRID;
	}
	public void setIRRID(long iRRID) {
		this.iRRID = iRRID;
	}
	public long getFeeTypeID() {
		return feeTypeID;
	}
	public void setFeeTypeID(long feeTypeID) {
		this.feeTypeID = feeTypeID;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public IRRFeeType getBefImage(){
		return this.befImage;
	}

	public void setBefImage(IRRFeeType beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getiRRID() {
		return iRRID;
	}

	public void setiRRID(long iRRID) {
		this.iRRID = iRRID;
	}

	public String getIRRIDName() {
		return iRRIDName;
	}

	public void setIRRIDName(String IRRIDName) {
		this.iRRIDName = IRRIDName;
	}

	public void setFeePercentage(BigDecimal feePercentage) {
		this.feePercentage = feePercentage;
	}

	public BigDecimal getFeePercentage() {
		return feePercentage;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public String getFeeTypeDesc() {
		return feeTypeDesc;
	}

	public void setFeeTypeDesc(String feeTypeDesc) {
		this.feeTypeDesc = feeTypeDesc;
	}

}
