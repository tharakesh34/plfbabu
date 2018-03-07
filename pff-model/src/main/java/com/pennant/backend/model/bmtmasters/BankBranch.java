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
 * FileName    		:  BankBranch.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.bmtmasters;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BankBranch table</b>.<br>
 *
 */
public class BankBranch extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	
	private long bankBranchID = Long.MIN_VALUE;
	private String bankCode;
	private String bankName;
	private String branchCode;
	private String branchDesc;
	private String city;
	private String PCCityName;
	private String mICR;
	private String iFSC;
	private String addOfBranch;
	private boolean cheque;
	private boolean dd;
	private boolean ecs;
	private boolean nach;
	private boolean dda;
	private boolean active;
	private boolean newRecord;
	private String lovValue;
	private BankBranch befImage;
	private LoggedInUser userDetails;
	private int accNoLength;
		
	public boolean isNew() {
		return isNewRecord();
	}

	public BankBranch() {
		super();
	}

	public BankBranch(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("bankName");
			excludeFields.add("PCCityName");
			excludeFields.add("accNoLength");
	return excludeFields;
	}
	
	public long getId() {
		return bankBranchID;
	}
	
	public void setId (long id) {
		this.bankBranchID = id;
	}
	
	public long getBankBranchID() {
		return bankBranchID;
	}
	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}
	
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	public String getBranchDesc() {
		return branchDesc;
	}
	public void setBranchDesc(String branchDesc) {
		this.branchDesc = branchDesc;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getMICR() {
		return mICR;
	}
	public void setMICR(String mICR) {
		this.mICR = mICR;
	}
	
	public String getIFSC() {
		return iFSC;
	}
	public void setIFSC(String iFSC) {
		this.iFSC = iFSC;
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

	public BankBranch getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(BankBranch beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPCCityName() {
		return PCCityName;
	}

	public void setPCCityName(String pCCityName) {
		PCCityName = pCCityName;
	}

	public boolean isEcs() {
		return ecs;
	}

	public void setEcs(boolean ecs) {
		this.ecs = ecs;
	}

	public boolean isDda() {
		return dda;
	}

	public void setDda(boolean dda) {
		this.dda = dda;
	}

	public boolean isNach() {
		return nach;
	}

	public void setNach(boolean nach) {
		this.nach = nach;
	}

	public boolean isCheque() {
		return cheque;
	}

	public void setCheque(boolean cheque) {
		this.cheque = cheque;
	}

	public boolean isDd() {
		return dd;
	}

	public void setDd(boolean dd) {
		this.dd = dd;
	}

	public String getAddOfBranch() {
		return addOfBranch;
	}

	public void setAddOfBranch(String addOfBranch) {
		this.addOfBranch = addOfBranch;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getAccNoLength() {
		return accNoLength;
	}

	public void setAccNoLength(int accNoLength) {
		this.accNoLength = accNoLength;
	}

}
