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
 * FileName    		:  BundledProductsDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.finance;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BundledProductsDetail table</b>.<br>
 *
 */
public class AgreementFieldDetails extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -6234931333270161797L;

	private String finReference;
	private String custCity;
	private String sellerName;
	private String custNationality;
	private String plotOrUnitNo;
	private String otherbankName;
	private String propertyType;
	private String sectorOrCommunity;
	private String finAmount;
	private String proprtyDesc;
	private String propertyLocation;
	private String custPoBox;
	
	private String jointApplicant;
	private String sellerNationality;
	private String sellerPobox;
	private String propertyUse;
	private String plotareainsqft;
	private String builtupAreainSqft;
	private String ahbBranch;
	private String fininstitution;
	private String facilityName;
	private String sellerCntbAmt;
	private String custCntAmt;
	private String otherBankAmt;
	private String propertyOwner;
	private String collateralAuthority;
	private String collateral1;
	private String sellerInternal;
	private String area;
	private boolean newRecord=false;
	private AgreementFieldDetails befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public AgreementFieldDetails() {
		super();
	}
	public AgreementFieldDetails(String id) {
		super();
		this.setId(id);
	}
	
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		
		return excludeFields;
	}
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finReference;
	}
	public void setId (String finReference) {
		this.finReference = finReference;
	}
	
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	
	public String getCustCity() {
		return custCity;
	}

	public String getCustPoBox() {
		return custPoBox;
	}

	public void setCustPoBox(String custPoBox) {
		this.custPoBox = custPoBox;
	}

	public String getJointApplicant() {
		return jointApplicant;
	}

	public void setJointApplicant(String jointApplicant) {
		this.jointApplicant = jointApplicant;
	}

	public String getSellerNationality() {
		return sellerNationality;
	}

	public void setSellerNationality(String sellerNationality) {
		this.sellerNationality = sellerNationality;
	}

	public String getSellerPobox() {
		return sellerPobox;
	}

	public void setSellerPobox(String sellerPobox) {
		this.sellerPobox = sellerPobox;
	}

	public String getPropertyUse() {
		return propertyUse;
	}

	public void setPropertyUse(String propertyUse) {
		this.propertyUse = propertyUse;
	}

	public String getPlotareainsqft() {
		return plotareainsqft;
	}

	public void setPlotareainsqft(String plotareainsqft) {
		this.plotareainsqft = plotareainsqft;
	}

	public String getBuiltupAreainSqft() {
		return builtupAreainSqft;
	}

	public void setBuiltupAreainSqft(String builtupAreainSqft) {
		this.builtupAreainSqft = builtupAreainSqft;
	}

	public String getAhbBranch() {
		return ahbBranch;
	}

	public void setAhbBranch(String ahbBranch) {
		this.ahbBranch = ahbBranch;
	}

	public String getFininstitution() {
		return fininstitution;
	}

	public void setFininstitution(String fininstitution) {
		this.fininstitution = fininstitution;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getSellerCntbAmt() {
		return sellerCntbAmt;
	}

	public void setSellerCntbAmt(String sellerCntbAmt) {
		this.sellerCntbAmt = sellerCntbAmt;
	}

	public String getOtherBankAmt() {
		return otherBankAmt;
	}

	public void setOtherBankAmt(String otherBankAmt) {
		this.otherBankAmt = otherBankAmt;
	}

	public String getPropertyOwner() {
		return propertyOwner;
	}

	public void setPropertyOwner(String propertyOwner) {
		this.propertyOwner = propertyOwner;
	}

	public String getCollateralAuthority() {
		return collateralAuthority;
	}

	public void setCollateralAuthority(String collateralAuthority) {
		this.collateralAuthority = collateralAuthority;
	}

	public String getCollateral1() {
		return collateral1;
	}

	public void setCollateral1(String collateral1) {
		this.collateral1 = collateral1;
	}

	public String getSellerInternal() {
		return sellerInternal;
	}

	public void setSellerInternal(String sellerInternal) {
		this.sellerInternal = sellerInternal;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getCustNationality() {
		return custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getPlotOrUnitNo() {
		return plotOrUnitNo;
	}

	public void setPlotOrUnitNo(String plotOrUnitNo) {
		this.plotOrUnitNo = plotOrUnitNo;
	}

	public String getOtherbankName() {
		return otherbankName;
	}

	public void setOtherbankName(String otherbankName) {
		this.otherbankName = otherbankName;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	

	public String getCustCntAmt() {
		return custCntAmt;
	}

	public void setCustCntAmt(String custCntAmt) {
		this.custCntAmt = custCntAmt;
	}

	public String getSectorOrCommunity() {
		return sectorOrCommunity;
	}

	public void setSectorOrCommunity(String sectorOrCommunity) {
		this.sectorOrCommunity = sectorOrCommunity;
	}

	public String getFinAmount() {
		return finAmount;
	}

	public void setFinAmount(String finAmount) {
		this.finAmount = finAmount;
	}

	public String getProprtyDesc() {
		return proprtyDesc;
	}

	public void setProprtyDesc(String proprtyDesc) {
		this.proprtyDesc = proprtyDesc;
	}

	public String getPropertyLocation() {
		return propertyLocation;
	}

	public void setPropertyLocation(String propertyLocation) {
		this.propertyLocation = propertyLocation;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	
	public AgreementFieldDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(AgreementFieldDetails befImage) {
		this.befImage = befImage;
	}
	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	
	
}
