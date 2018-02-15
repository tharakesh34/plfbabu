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
 * Creation Date    :  17-06-2013    														*
 *                                                                  						*
 * Modified Date    :  17-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2013       Pennant	                 0.1                                            * 
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
 * Model class for the <b>Currency table</b>.<br>
 *
 */
public class EquationCurrency extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -4491281004736574909L;

	public EquationCurrency() {
    	super();
    }
	
	private String entityCode;
	private String ccyCode;
	private String ccyNumber;
	private String ccyDesc;
	private String ccySwiftCode;
	private double ccyEditField;
	private double ccyMinorCcyUnits;
	private String ccyDrRateBasisCode;
	private String ccyCrRateBasisCode;
	private boolean ccyIsIntRounding;
	private double ccySpotRate;
	private boolean ccyIsReceprocal;
	private double ccyUserRateBuy;
	private double ccyUserRateSell;
	private boolean ccyIsMember;
	private boolean ccyIsGroup;
	private boolean ccyIsAlwForLoans;
	private boolean ccyIsAlwForDepo;
	private boolean ccyIsAlwForAc;
	private boolean ccyIsActive;
	private String ccyMinorCcyDesc;
	private String ccySymbol;
	
	public String getEntityCode() {
		return entityCode;
	}
	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
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
	public double getCcyEditField() {
		return ccyEditField;
	}
	public void setCcyEditField(double ccyEditField) {
		this.ccyEditField = ccyEditField;
	}
	public double getCcyMinorCcyUnits() {
		return ccyMinorCcyUnits;
	}
	public void setCcyMinorCcyUnits(double ccyMinorCcyUnits) {
		this.ccyMinorCcyUnits = ccyMinorCcyUnits;
	}
	public String getCcyDrRateBasisCode() {
		return ccyDrRateBasisCode;
	}
	public void setCcyDrRateBasisCode(String ccyDrRateBasisCode) {
		this.ccyDrRateBasisCode = ccyDrRateBasisCode;
	}
	public String getCcyCrRateBasisCode() {
		return ccyCrRateBasisCode;
	}
	public void setCcyCrRateBasisCode(String ccyCrRateBasisCode) {
		this.ccyCrRateBasisCode = ccyCrRateBasisCode;
	}
	public boolean isCcyIsIntRounding() {
		return ccyIsIntRounding;
	}
	public void setCcyIsIntRounding(boolean ccyIsIntRounding) {
		this.ccyIsIntRounding = ccyIsIntRounding;
	}
	public double getCcySpotRate() {
		return ccySpotRate;
	}
	public void setCcySpotRate(double ccySpotRate) {
		this.ccySpotRate = ccySpotRate;
	}
	public boolean isCcyIsReceprocal() {
		return ccyIsReceprocal;
	}
	public void setCcyIsReceprocal(boolean ccyIsReceprocal) {
		this.ccyIsReceprocal = ccyIsReceprocal;
	}
	public double getCcyUserRateBuy() {
		return ccyUserRateBuy;
	}
	public void setCcyUserRateBuy(double ccyUserRateBuy) {
		this.ccyUserRateBuy = ccyUserRateBuy;
	}
	public double getCcyUserRateSell() {
		return ccyUserRateSell;
	}
	public void setCcyUserRateSell(double ccyUserRateSell) {
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
	public String getCcyMinorCcyDesc() {
		return ccyMinorCcyDesc;
	}
	public void setCcyMinorCcyDesc(String ccyMinorCcyDesc) {
		this.ccyMinorCcyDesc = ccyMinorCcyDesc;
	}
	public String getCcySymbol() {
		return ccySymbol;
	}
	public void setCcySymbol(String ccySymbol) {
		this.ccySymbol = ccySymbol;
	}
}
