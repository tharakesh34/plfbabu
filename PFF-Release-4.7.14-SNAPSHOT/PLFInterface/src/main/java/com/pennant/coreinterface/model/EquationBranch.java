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
public class EquationBranch extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -2795250414419318271L;
	
	private String branchCode = null;
	private String branchDesc;
	private String branchAddrLine1;
	private String branchAddrLine2;
	private String branchPOBox;
	private String branchCity;
	private String branchProvince;
	private String branchCountry;
	private String branchFax;
	private String branchTel;
	private String branchSwiftBankCde;
	private String branchSwiftCountry;
	private String branchSwiftLocCode;
	private String branchSwiftBrnCde;
	private String branchSortCode;
	private boolean branchIsActive;
	
	public EquationBranch() {
		super();
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
	
	public String getBranchAddrLine1() {
		return branchAddrLine1;
	}
	public void setBranchAddrLine1(String branchAddrLine1) {
		this.branchAddrLine1 = branchAddrLine1;
	}
	
	public String getBranchAddrLine2() {
		return branchAddrLine2;
	}
	public void setBranchAddrLine2(String branchAddrLine2) {
		this.branchAddrLine2 = branchAddrLine2;
	}
	
	public String getBranchPOBox() {
		return branchPOBox;
	}
	public void setBranchPOBox(String branchPOBox) {
		this.branchPOBox = branchPOBox;
	}
	
	public String getBranchCity() {
		return branchCity;
	}
	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}
	
	public String getBranchProvince() {
		return branchProvince;
	}
	public void setBranchProvince(String branchProvince) {
		this.branchProvince = branchProvince;
	}
	
	public String getBranchCountry() {
		return branchCountry;
	}
	public void setBranchCountry(String branchCountry) {
		this.branchCountry = branchCountry;
	}
	
	public String getBranchFax() {
		return branchFax;
	}
	public void setBranchFax(String branchFax) {
		this.branchFax = branchFax;
	}
	
	public String getBranchTel() {
		return branchTel;
	}
	public void setBranchTel(String branchTel) {
		this.branchTel = branchTel;
	}
	
	public String getBranchSwiftBankCde() {
		return branchSwiftBankCde;
	}
	public void setBranchSwiftBankCde(String branchSwiftBankCde) {
		this.branchSwiftBankCde = branchSwiftBankCde;
	}
	
	public String getBranchSwiftCountry() {
		return branchSwiftCountry;
	}
	public void setBranchSwiftCountry(String branchSwiftCountry) {
		this.branchSwiftCountry = branchSwiftCountry;
	}
	
	public String getBranchSwiftLocCode() {
		return branchSwiftLocCode;
	}
	public void setBranchSwiftLocCode(String branchSwiftLocCode) {
		this.branchSwiftLocCode = branchSwiftLocCode;
	}
	
	public String getBranchSwiftBrnCde() {
		return branchSwiftBrnCde;
	}
	public void setBranchSwiftBrnCde(String branchSwiftBrnCde) {
		this.branchSwiftBrnCde = branchSwiftBrnCde;
	}
	
	public String getBranchSortCode() {
		return branchSortCode;
	}
	public void setBranchSortCode(String branchSortCode) {
		this.branchSortCode = branchSortCode;
	}
	
	public boolean isBranchIsActive() {
		return branchIsActive;
	}
	public void setBranchIsActive(boolean branchIsActive) {
		this.branchIsActive = branchIsActive;
	}
}
