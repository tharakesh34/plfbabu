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
 * FileName    		:  FinAssetEvaluation.java                                                   * 	  
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinAssetEvaluation table</b>.<br>
 *
 */
public class FinAssetEvaluation extends AbstractWorkflowEntity {
	
	private static final long serialVersionUID = -6234931333270161797L;

	private String finReference;
	private String typeofValuation;
	private boolean custAwareVisit;
	private String custRepreName;
	private boolean leased;
	private BigDecimal totalRevenue = BigDecimal.ZERO;
	private String tenantContactNum;
	private boolean tenantAwareVisit;
	private String remarks;
	private String panelFirm;
	private String reuReference;
	private String propertyDesc;
	private Date vendorInstructedDate;
	private Date reportDeliveredDate;
	private Date inspectionDate;
	private Date finalReportDate;
	private BigDecimal marketValueAED = BigDecimal.ZERO;
	private Date valuationDate;
	private String status;
	private long vendorValuer;
	private String vendorValuerDesc;
	private BigDecimal valuerFee = BigDecimal.ZERO;
	private BigDecimal customerFee = BigDecimal.ZERO;
	private String valuationComments;
	private BigDecimal expRentalIncome = BigDecimal.ZERO;
	private boolean propIsRented;
	private String propertyStatus;
	private BigDecimal percWorkCompletion = BigDecimal.ZERO;
	private boolean illegalDivAlteration;
	private boolean nocReqDevMunicipality;
	private String reuDecision;
	private BigDecimal unitVillaSize;
	
	private boolean newRecord=false;
	private String lovValue;
	private FinAssetEvaluation befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinAssetEvaluation() {
		super();
	}

	public FinAssetEvaluation(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("vendorValuerDesc");
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
	
	public String getTypeofValuation() {
		return typeofValuation;
	}
	public void setTypeofValuation(String typeofValuation) {
		this.typeofValuation = typeofValuation;
	}

	public boolean isCustAwareVisit() {
		return custAwareVisit;
	}
	public void setCustAwareVisit(boolean custAwareVisit) {
		this.custAwareVisit = custAwareVisit;
	}

	public String getCustRepreName() {
		return custRepreName;
	}
	public void setCustRepreName(String custRepreName) {
		this.custRepreName = custRepreName;
	}

	public boolean isLeased() {
		return leased;
	}
	public void setLeased(boolean leased) {
		this.leased = leased;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public String getTenantContactNum() {
		return tenantContactNum;
	}
	public void setTenantContactNum(String tenantContactNum) {
		this.tenantContactNum = tenantContactNum;
	}

	public boolean isTenantAwareVisit() {
		return tenantAwareVisit;
	}
	public void setTenantAwareVisit(boolean tenantAwareVisit) {
		this.tenantAwareVisit = tenantAwareVisit;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPanelFirm() {
		return panelFirm;
	}
	public void setPanelFirm(String panelFirm) {
		this.panelFirm = panelFirm;
	}

	public String getReuReference() {
		return reuReference;
	}
	public void setReuReference(String reuReference) {
		this.reuReference = reuReference;
	}

	public String getPropertyDesc() {
		return propertyDesc;
	}
	public void setPropertyDesc(String propertyDesc) {
		this.propertyDesc = propertyDesc;
	}

	public Date getVendorInstructedDate() {
		return vendorInstructedDate;
	}
	public void setVendorInstructedDate(Date vendorInstructedDate) {
		this.vendorInstructedDate = vendorInstructedDate;
	}

	public Date getReportDeliveredDate() {
		return reportDeliveredDate;
	}
	public void setReportDeliveredDate(Date reportDeliveredDate) {
		this.reportDeliveredDate = reportDeliveredDate;
	}

	public Date getInspectionDate() {
		return inspectionDate;
	}
	public void setInspectionDate(Date inspectionDate) {
		this.inspectionDate = inspectionDate;
	}

	public Date getFinalReportDate() {
		return finalReportDate;
	}
	public void setFinalReportDate(Date finalReportDate) {
		this.finalReportDate = finalReportDate;
	}

	public BigDecimal getMarketValueAED() {
		return marketValueAED;
	}
	public void setMarketValueAED(BigDecimal marketValueAED) {
		this.marketValueAED = marketValueAED;
	}

	public Date getValuationDate() {
		return valuationDate;
	}
	public void setValuationDate(Date valuationDate) {
		this.valuationDate = valuationDate;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public long getVendorValuer() {
		return vendorValuer;
	}
	public void setVendorValuer(long vendorValuer) {
		this.vendorValuer = vendorValuer;
	}

	public String getVendorValuerDesc() {
		return vendorValuerDesc;
	}
	public void setVendorValuerDesc(String vendorValuerDesc) {
		this.vendorValuerDesc = vendorValuerDesc;
	}

	public BigDecimal getValuerFee() {
		return valuerFee;
	}
	public void setValuerFee(BigDecimal valuerFee) {
		this.valuerFee = valuerFee;
	}

	public BigDecimal getCustomerFee() {
		return customerFee;
	}
	public void setCustomerFee(BigDecimal customerFee) {
		this.customerFee = customerFee;
	}

	public String getValuationComments() {
		return valuationComments;
	}
	public void setValuationComments(String valuationComments) {
		this.valuationComments = valuationComments;
	}

	public BigDecimal getExpRentalIncome() {
		return expRentalIncome;
	}
	public void setExpRentalIncome(BigDecimal expRentalIncome) {
		this.expRentalIncome = expRentalIncome;
	}

	public boolean isPropIsRented() {
		return propIsRented;
	}
	public void setPropIsRented(boolean propIsRented) {
		this.propIsRented = propIsRented;
	}

	public String getPropertyStatus() {
		return propertyStatus;
	}
	public void setPropertyStatus(String propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	public BigDecimal getPercWorkCompletion() {
		return percWorkCompletion;
	}
	public void setPercWorkCompletion(BigDecimal percWorkCompletion) {
		this.percWorkCompletion = percWorkCompletion;
	}

	public boolean isIllegalDivAlteration() {
		return illegalDivAlteration;
	}
	public void setIllegalDivAlteration(boolean illegalDivAlteration) {
		this.illegalDivAlteration = illegalDivAlteration;
	}

	public boolean isNocReqDevMunicipality() {
		return nocReqDevMunicipality;
	}
	public void setNocReqDevMunicipality(boolean nocReqDevMunicipality) {
		this.nocReqDevMunicipality = nocReqDevMunicipality;
	}

	public String getReuDecision() {
		return reuDecision;
	}
	public void setReuDecision(String reuDecision) {
		this.reuDecision = reuDecision;
	}

	public BigDecimal getUnitVillaSize() {
		return unitVillaSize;
	}
	public void setUnitVillaSize(BigDecimal unitVillaSize) {
		this.unitVillaSize = unitVillaSize;
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

	public FinAssetEvaluation getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinAssetEvaluation beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
