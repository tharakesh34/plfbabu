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
 * FileName    		:  Province.java                                                   * 	  
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
package com.pennanttech.bajaj.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>Province table</b>.<br>
 *
 */
public class Province extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3563961020581268151L;

	private String cPCountry;
	private String cPProvince;
	private String cPProvinceName;
	private boolean newRecord;
	private String lovValue;
	private Province befImage;
	private LoggedInUser userDetails;
	private String lovDescCPCountryName;
	private boolean systemDefault;
	private String bankRefNo;
	private boolean cPIsActive;
	private boolean taxExempted;
	private boolean unionTerritory;
	private String  taxStateCode;
	private boolean taxAvailable;
	private String businessArea;
	
	private List<TaxDetail> taxDetailList = new ArrayList<TaxDetail>();
 	
	
	public boolean isNew() {
		return isNewRecord();
	}

	public Province() {
		super();
	}

	public Province(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return cPProvince;
	}
	public void setId (String id) {
		this.cPProvince = id;
	}

	public String getCPCountry() {
		return cPCountry;
	}
	public void setCPCountry(String cPCountry) {
		this.cPCountry = cPCountry;
	}

	public String getLovDescCPCountryName() {
		return this.lovDescCPCountryName;
	}
	public void setLovDescCPCountryName(String lovDescCPCountryName) {
		this.lovDescCPCountryName = lovDescCPCountryName;
	}

	public String getCPProvince() {
		return cPProvince;
	}
	public void setCPProvince(String cPProvince) {
		this.cPProvince = cPProvince;
	}

	public String getCPProvinceName() {
		return cPProvinceName;
	}
	public void setCPProvinceName(String cPProvinceName) {
		this.cPProvinceName = cPProvinceName;
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

	public Province getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Province beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isSystemDefault() {
	    return systemDefault;
    }

	public void setSystemDefault(boolean systemDefault) {
	    this.systemDefault = systemDefault;
    }

	public String getBankRefNo() {
		return bankRefNo;
	}

	public void setBankRefNo(String bankRefNo) {
		this.bankRefNo = bankRefNo;
	}

	public boolean iscPIsActive() {
		return cPIsActive;
	}

	public void setcPIsActive(boolean cPIsActive) {
		this.cPIsActive = cPIsActive;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public boolean isTaxExempted() {
		return taxExempted;
	}

	public void setTaxExempted(boolean taxExempted) {
		this.taxExempted = taxExempted;
	}

	public boolean isUnionTerritory() {
		return unionTerritory;
	}

	public void setUnionTerritory(boolean unionTerritory) {
		this.unionTerritory = unionTerritory;
	}

	public String getTaxStateCode() {
		return taxStateCode;
	}

	public void setTaxStateCode(String taxStateCode) {
		this.taxStateCode = taxStateCode;
	}

	public boolean isTaxAvailable() {
		return taxAvailable;
	}

	public void setTaxAvailable(boolean taxAvailable) {
		this.taxAvailable = taxAvailable;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}

	public List<TaxDetail> getTaxDetailList() {
		return taxDetailList;
	}

	public void setTaxDetailList(List<TaxDetail> taxDetailList) {
		this.taxDetailList = taxDetailList;
	}

}
