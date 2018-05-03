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
 * FileName    		:  CommitmentRate.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2016    														*
 *                                                                  						*
 * Modified Date    :  22-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.commitment;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CommitmentRate table</b>.<br>
 *
 */
public class CommitmentRate extends AbstractWorkflowEntity {
private static final long serialVersionUID = 1L;

	private String cmtReference;
	private String cmtRvwFrq;
	private String cmtBaseRate;
	private String cmtBaseRateName;
	private String cmtSpecialRate;
	private String cmtSpecialRateName;
	
	private BigDecimal cmtMargin = BigDecimal.ZERO;
	private BigDecimal cmtActualRate = BigDecimal.ZERO;
	private BigDecimal cmtCalculatedRate = BigDecimal.ZERO;
	
 	private boolean newRecord = false;
	private String lovValue;
	private CommitmentRate befImage;
	private  LoggedInUser userDetails;
	
	private String cmtCcy;
	private BigDecimal cmtPftRateMin;
	private BigDecimal cmtPftRateMax;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public CommitmentRate() {
		super();
	}

	public CommitmentRate(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();

			excludeFields.add("cmtBaseRateName");
			excludeFields.add("cmtSpecialRateName");
			excludeFields.add("cmtCcy");
			excludeFields.add("cmtPftRateMin");
			excludeFields.add("cmtPftRateMax");
			
	return excludeFields;
	}

	public String getCmtReference() {
		return cmtReference;
	}
	public void setCmtReference(String cmtReference) {
		this.cmtReference = cmtReference;
	}
	
	public String getId() {
		return cmtRvwFrq;
	}
	
	public void setId (String id) {
		this.cmtRvwFrq = id;
	}
	public String getCmtRvwFrq() {
		return cmtRvwFrq;
	}
	public void setCmtRvwFrq(String cmtRvwFrq) {
		this.cmtRvwFrq = cmtRvwFrq;
	}
 
	public String getCmtBaseRate() {
		return cmtBaseRate;
	}
	public void setCmtBaseRate(String cmtBaseRate) {
		this.cmtBaseRate = cmtBaseRate;
	}
	public String getCmtBaseRateName() {
		return this.cmtBaseRateName;
	}

	public void setCmtBaseRateName (String cmtBaseRateName) {
		this.cmtBaseRateName = cmtBaseRateName;
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

	public CommitmentRate getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(CommitmentRate beforeImage){
		this.befImage=beforeImage;
	}

	public  LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails( LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public BigDecimal getCmtMargin() {
		return cmtMargin;
	}

	public void setCmtMargin(BigDecimal cmtMargin) {
		this.cmtMargin = cmtMargin;
	}

	public String getCmtSpecialRate() {
		return cmtSpecialRate;
	}

	public void setCmtSpecialRate(String cmtSpecialRate) {
		this.cmtSpecialRate = cmtSpecialRate;
	}

	public String getCmtSpecialRateName() {
		return cmtSpecialRateName;
	}

	public void setCmtSpecialRateName(String cmtSpecialRateName) {
		this.cmtSpecialRateName = cmtSpecialRateName;
	}

	public BigDecimal getCmtActualRate() {
		return cmtActualRate;
	}

	public void setCmtActualRate(BigDecimal cmtActualRate) {
		this.cmtActualRate = cmtActualRate;
	}

	public BigDecimal getCmtCalculatedRate() {
		return cmtCalculatedRate;
	}

	public void setCmtCalculatedRate(BigDecimal cmtCalculatedRate) {
		this.cmtCalculatedRate = cmtCalculatedRate;
	}

	public String getCmtCcy() {
		return cmtCcy;
	}

	public void setCmtCcy(String cmtCcy) {
		this.cmtCcy = cmtCcy;
	}

	public BigDecimal getCmtPftRateMin() {
		return cmtPftRateMin;
	}

	public void setCmtPftRateMin(BigDecimal cmtPftRateMin) {
		this.cmtPftRateMin = cmtPftRateMin;
	}

	public BigDecimal getCmtPftRateMax() {
		return cmtPftRateMax;
	}

	public void setCmtPftRateMax(BigDecimal cmtPftRateMax) {
		this.cmtPftRateMax = cmtPftRateMax;
	}

}
