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
 * FileName    		:  Currency.java                                                   * 	  
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
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Currency table</b>.<br>
 *
 */
public class Currency extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -7893835195187974710L;
	
	private String ccyCode;
	private String ccyNumber;
	private String ccyDesc;
	private String ccySwiftCode;
	private int ccyEditField;
	private BigDecimal ccyMinorCcyUnits;
	private String ccyDrRateBasisCode;
	private String lovDescCcyDrRateBasisCodeName;
	private String ccyCrRateBasisCode;
	private String lovDescCcyCrRateBasisCodeName;
	private String ccySymbol;
	private String ccyMinorCcyDesc;
	private boolean ccyIsIntRounding;
	private BigDecimal ccySpotRate;
	private boolean ccyIsReceprocal;
	private BigDecimal ccyUserRateBuy;
	private BigDecimal ccyUserRateSell;
	private boolean ccyIsMember;
	private boolean ccyIsGroup;
	private boolean ccyIsAlwForLoans;
	private boolean ccyIsAlwForDepo;
	private boolean ccyIsAlwForAc;
	private boolean ccyIsActive;
	private boolean newRecord;
	private String lovValue;
	private Currency befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Currency() {
		super();
	}

	public Currency(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return ccyCode;
	}	
	public void setId (String id) {
		this.ccyCode = id;
	}
	
	public String getCcyCode() {
		return ccyCode;
	}
	public void setCcyCode(String ccyCode) {
		this.ccyCode = ccyCode;
	}
	
	public String getCcyNumber() {
		return ccyNumber;
	}
	public void setCcyNumber(String ccyNumber) {
		this.ccyNumber = ccyNumber;
	}
	
	public String getCcyDesc() {
		return ccyDesc;
	}
	public void setCcyDesc(String ccyDesc) {
		this.ccyDesc = ccyDesc;
	}
		
	public String getCcySwiftCode() {
		return ccySwiftCode;
	}
	public void setCcySwiftCode(String ccySwiftCode) {
		this.ccySwiftCode = ccySwiftCode;
	}
		
	public int getCcyEditField() {
		return ccyEditField;
	}
	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}
		
	public BigDecimal getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}
	public void setCcyMinorCcyUnits(BigDecimal ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}
		
	public String getCcyDrRateBasisCode() {
		return ccyDrRateBasisCode;
	}
	public void setCcyDrRateBasisCode(String ccyDrRateBasisCode) {
		this.ccyDrRateBasisCode = ccyDrRateBasisCode;
	}
	
	public String getLovDescCcyDrRateBasisCodeName() {
		return this.lovDescCcyDrRateBasisCodeName;
	}
	public void setLovDescCcyDrRateBasisCodeName(String lovDescCcyDrRateBasisCodeName) {
		this.lovDescCcyDrRateBasisCodeName = lovDescCcyDrRateBasisCodeName;
	}
	
	public String getCcyCrRateBasisCode() {
		return ccyCrRateBasisCode;
	}
	public void setCcyCrRateBasisCode(String ccyCrRateBasisCode) {
		this.ccyCrRateBasisCode = ccyCrRateBasisCode;
	}

	public String getLovDescCcyCrRateBasisCodeName() {
		return this.lovDescCcyCrRateBasisCodeName;
	}
	public void setLovDescCcyCrRateBasisCodeName(String lovDescCcyCrRateBasisCodeName) {
		this.lovDescCcyCrRateBasisCodeName = lovDescCcyCrRateBasisCodeName;
	}
	
	public String getCcySymbol() {
		return ccySymbol;
	}
	public void setCcySymbol(String ccySymbol) {
		this.ccySymbol = ccySymbol;
	}

	public String getCcyMinorCcyDesc() {
		return ccyMinorCcyDesc;
	}
	public void setCcyMinorCcyDesc(String ccyMinorCcyDesc) {
		this.ccyMinorCcyDesc = ccyMinorCcyDesc;
	}

	public boolean isCcyIsIntRounding() {
		return ccyIsIntRounding;
	}
	public void setCcyIsIntRounding(boolean ccyIsIntRounding) {
		this.ccyIsIntRounding = ccyIsIntRounding;
	}
		
	public BigDecimal getCcySpotRate() {
		return ccySpotRate;
	}
	public void setCcySpotRate(BigDecimal ccySpotRate) {
		this.ccySpotRate = ccySpotRate;
	}
		
	public boolean isCcyIsReceprocal() {
		return ccyIsReceprocal;
	}
	public void setCcyIsReceprocal(boolean ccyIsReceprocal) {
		this.ccyIsReceprocal = ccyIsReceprocal;
	}
		
	public BigDecimal getCcyUserRateBuy() {
		return ccyUserRateBuy;
	}
	public void setCcyUserRateBuy(BigDecimal ccyUserRateBuy) {
		this.ccyUserRateBuy = ccyUserRateBuy;
	}
		
	public BigDecimal getCcyUserRateSell() {
		return ccyUserRateSell;
	}
	public void setCcyUserRateSell(BigDecimal ccyUserRateSell) {
		this.ccyUserRateSell = ccyUserRateSell;
	}
		
	public boolean isCcyIsMember() {
		return ccyIsMember;
	}
	public void setCcyIsMember(boolean ccyIsMember) {
		this.ccyIsMember = ccyIsMember;
	}
		
	public boolean isCcyIsGroup() {
		return ccyIsGroup;
	}
	public void setCcyIsGroup(boolean ccyIsGroup) {
		this.ccyIsGroup = ccyIsGroup;
	}
		
	public boolean isCcyIsAlwForLoans() {
		return ccyIsAlwForLoans;
	}
	public void setCcyIsAlwForLoans(boolean ccyIsAlwForLoans) {
		this.ccyIsAlwForLoans = ccyIsAlwForLoans;
	}
		
	public boolean isCcyIsAlwForDepo() {
		return ccyIsAlwForDepo;
	}
	public void setCcyIsAlwForDepo(boolean ccyIsAlwForDepo) {
		this.ccyIsAlwForDepo = ccyIsAlwForDepo;
	}
	
	public boolean isCcyIsAlwForAc() {
		return ccyIsAlwForAc;
	}
	public void setCcyIsAlwForAc(boolean ccyIsAlwForAc) {
		this.ccyIsAlwForAc = ccyIsAlwForAc;
	}
		
	public boolean isCcyIsActive() {
		return ccyIsActive;
	}
	public void setCcyIsActive(boolean ccyIsActive) {
		this.ccyIsActive = ccyIsActive;
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

	public Currency getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(Currency beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
