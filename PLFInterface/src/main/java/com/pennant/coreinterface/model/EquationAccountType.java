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
 * FileName    		:  EquationAccountType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2014    														*
 *                                                                  						*
 * Modified Date    :  24-06-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *  21-06-2014       Pennant	                 0.1                                            * 
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
package com.pennant.coreinterface.model;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>AccountType table</b>.<br>
 * 
 */
public class EquationAccountType extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -6862602123712610264L;
	
	//RMTAccountTypes Table Fields
	private String acType;
	private String acTypeDesc;
	private String acPurpose;
	private String acHeadCode;
	private boolean internalAc;
	private boolean custSysAc;
	private String acLmtCategory;
	private boolean acTypeIsActive;
	
	//AccountTypeNatures Table Fields
	private String acTypeNature1;
	private String acTypeNature2;
	private String acTypeNature3;
	private String acTypeNature4;
	private String acTypeNature5;
	private String acTypeNature6;
	private String acTypeNature7;
	private String acTypeNature8;
	private String acTypeNature9;
	private String acTypeNature10;
	
	public EquationAccountType() {
    	super();
    }

	public String getId() {
		return acType;
	}
	public void setId(String id) {
		this.acType = id;
	}

	public String getAcType() {
		return acType;
	}
	public void setAcType(String acType) {
		this.acType = acType;
	}

	public String getAcTypeDesc() {
		return acTypeDesc;
	}
	public void setAcTypeDesc(String acTypeDesc) {
		this.acTypeDesc = acTypeDesc;
	}

	public String getAcPurpose() {
		return acPurpose;
	}
	public void setAcPurpose(String acPurpose) {
		this.acPurpose = acPurpose;
	}

	public String getAcHeadCode() {
		return acHeadCode;
	}
	public void setAcHeadCode(String acHeadCode) {
		this.acHeadCode = acHeadCode;
	}

	public boolean isInternalAc() {
		return internalAc;
	}

	public void setInternalAc(boolean internalAc) {
		this.internalAc = internalAc;
	}

	public boolean isCustSysAc() {
		return custSysAc;
	}

	public void setCustSysAc(boolean custSysAc) {
		this.custSysAc = custSysAc;
	}
	
	public String getAcLmtCategory() {
    	return acLmtCategory;
    }

	public void setAcLmtCategory(String acLmtCategory) {
    	this.acLmtCategory = acLmtCategory;
    }

	public boolean isAcTypeIsActive() {
		return acTypeIsActive;
	}
	public void setAcTypeIsActive(boolean acTypeIsActive) {
		this.acTypeIsActive = acTypeIsActive;
	}

	public String getAcTypeNature1() {
		return acTypeNature1;
	}
	public void setAcTypeNature1(String acTypeNature1) {
		this.acTypeNature1 = acTypeNature1;
	}
	
	public String getAcTypeNature2() {
		return acTypeNature2;
	}
	public void setAcTypeNature2(String acTypeNature2) {
		this.acTypeNature2 = acTypeNature2;
	}
	
	public String getAcTypeNature3() {
		return acTypeNature3;
	}
	public void setAcTypeNature3(String acTypeNature3) {
		this.acTypeNature3 = acTypeNature3;
	}
	
	public String getAcTypeNature4() {
		return acTypeNature4;
	}
	public void setAcTypeNature4(String acTypeNature4) {
		this.acTypeNature4 = acTypeNature4;
	}
	
	public String getAcTypeNature5() {
		return acTypeNature5;
	}
	public void setAcTypeNature5(String acTypeNature5) {
		this.acTypeNature5 = acTypeNature5;
	}
	
	public String getAcTypeNature6() {
		return acTypeNature6;
	}
	public void setAcTypeNature6(String acTypeNature6) {
		this.acTypeNature6 = acTypeNature6;
	}
	
	public String getAcTypeNature7() {
		return acTypeNature7;
	}
	public void setAcTypeNature7(String acTypeNature7) {
		this.acTypeNature7 = acTypeNature7;
	}
	
	public String getAcTypeNature8() {
		return acTypeNature8;
	}
	public void setAcTypeNature8(String acTypeNature8) {
		this.acTypeNature8 = acTypeNature8;
	}
	
	public String getAcTypeNature9() {
		return acTypeNature9;
	}
	public void setAcTypeNature9(String acTypeNature9) {
		this.acTypeNature9 = acTypeNature9;
	}
	
	public String getAcTypeNature10() {
		return acTypeNature10;
	}
	public void setAcTypeNature10(String acTypeNature10) {
		this.acTypeNature10 = acTypeNature10;
	}
}
