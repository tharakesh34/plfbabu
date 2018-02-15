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
 * FileName    		:  FeeType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-01-2017    														*
 *                                                                  						*
 * Modified Date    :  03-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.feetype;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FeeType table</b>.<br>
 *
 */
public class FeeType extends AbstractWorkflowEntity  implements Entity {
private static final long serialVersionUID = 1L;

	private long feeTypeID = Long.MIN_VALUE;
	private String feeTypeCode;
	private String feeTypeDesc;
	private boolean manualAdvice;
	private int 	adviseType;
	private Long 	accountSetId;
	private String 	accountSetCode;
	private String 	accountSetCodeName;
	private boolean active;
	private boolean newRecord;
	private String lovValue;
	private FeeType befImage;
	private  LoggedInUser userDetails;
	private String hostFeeTypeCode;
	//GST fields
	private boolean taxApplicable;
	private String	taxComponent;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FeeType() {
		super();
	}

	public FeeType(long id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("accountSetCode");
		excludeFields.add("accountSetCodeName");
	return excludeFields;
	}

	public long getId() {
		return feeTypeID;
	}
	
	public void setId (long id) {
		this.feeTypeID = id;
	}
	public long getFeeTypeID() {
		return feeTypeID;
	}
	public void setFeeTypeID(long feeTypeID) {
		this.feeTypeID = feeTypeID;
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

	public FeeType getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(FeeType beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
	public Long getAccountSetId() {
		return accountSetId;
	}

	public void setAccountSetId(Long accountSetId) {
		this.accountSetId = accountSetId;
	}

	public String getAccountSetCode() {
		return accountSetCode;
	}

	public void setAccountSetCode(String accountSetCode) {
		this.accountSetCode = accountSetCode;
	}

	public String getAccountSetCodeName() {
		return accountSetCodeName;
	}

	public void setAccountSetCodeName(String accountSetCodeName) {
		this.accountSetCodeName = accountSetCodeName;
	}

	public boolean isManualAdvice() {
		return manualAdvice;
	}

	public void setManualAdvice(boolean manualAdvice) {
		this.manualAdvice = manualAdvice;
	}

	public int getAdviseType() {
		return adviseType;
	}

	public void setAdviseType(int adviseType) {
		this.adviseType = adviseType;
	}
	public String getHostFeeTypeCode() {
		return hostFeeTypeCode;
	}

	public void setHostFeeTypeCode(String hostFeeTypeCode) {
		this.hostFeeTypeCode = hostFeeTypeCode;
	}
	public boolean isTaxApplicable() {
		return taxApplicable;
	}

	public void setTaxApplicable(boolean taxApplicable) {
		this.taxApplicable = taxApplicable;
	}

	public String getTaxComponent() {
		return taxComponent;
	}

	public void setTaxComponent(String taxComponent) {
		this.taxComponent = taxComponent;
	}
}
