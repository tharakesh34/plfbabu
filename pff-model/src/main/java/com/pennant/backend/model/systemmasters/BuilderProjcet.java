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
 * FileName    		:  BuilderProjcet.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-05-2017    														*
 *                                                                  						*
 * Modified Date    :  22-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BuilderProjcet table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "name", "builderId", "apfNo", "branchBankCode", "branchBankName", "branchCode", "iFSC" })

@XmlAccessorType(XmlAccessType.FIELD)
public class BuilderProjcet extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String name;
	private long builderId;
	private String builderIdName;
	private String segmentation;
	private String apfNo;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private BuilderProjcet befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	//Project Details
	private String registrationNumber;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String landmark;
	private String areaOrLocality;
	private String city;
	private String state;
	private String pinCode;
	private String projectType;
	private String typesOfApf;
	private int totalUnits;
	private int numberOfTowers;
	private int noOfIndependentHouses;
	private Date projectStartDate;
	private Date projectEndDate;
	private String remarks;
	private String commencementCertificateNo;
	private String commencecrtfctissuingauthority;
	private int totalPlotArea;
	private int constructedArea;
	private String technicalDone;
	private String legalDone;
	private String rcuDone;
	private BigDecimal constrctincompletionpercentage = BigDecimal.ZERO;
	private BigDecimal disbursalRecommendedPercentage = BigDecimal.ZERO;

	@XmlElement
	private String beneficiaryName;
	private String accountNo;
	private long bankBranchID;

	@XmlElement
	private String branchBankCode;
	@XmlElement
	private String branchBankName;

	@XmlElement
	private String branchCode;
	private String branchDesc;

	@XmlElement
	private String iFSC;

	private List<ProjectUnits> projectUnits;
	private List<DocumentDetails> documentDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public BuilderProjcet() {
		super();
	}

	public BuilderProjcet(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("builderIdName");
		excludeFields.add("documentDetails");
		excludeFields.add("projectUnits");
		excludeFields.add("documentDetails");
		excludeFields.add("auditDetailMap");
		excludeFields.add("segmentation");
		excludeFields.add("branchCode");
		excludeFields.add("branchDesc");
		excludeFields.add("iFSC");
		excludeFields.add("bankCode");
		excludeFields.add("branchBankCode");
		excludeFields.add("branchBankName");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getBuilderId() {
		return builderId;
	}

	public void setBuilderId(long builderId) {
		this.builderId = builderId;
	}

	public String getbuilderIdName() {
		return this.builderIdName;
	}

	public void setbuilderIdName(String builderIdName) {
		this.builderIdName = builderIdName;
	}

	public String getApfNo() {
		return apfNo;
	}

	public void setApfNo(String apfNo) {
		this.apfNo = apfNo;
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

	public BuilderProjcet getBefImage() {
		return this.befImage;
	}

	public void setBefImage(BuilderProjcet beforeImage) {
		this.befImage = beforeImage;
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

	public String getBuilderIdName() {
		return builderIdName;
	}

	public void setBuilderIdName(String builderIdName) {
		this.builderIdName = builderIdName;
	}

	public String getSegmentation() {
		return segmentation;
	}

	public void setSegmentation(String segmentation) {
		this.segmentation = segmentation;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getAreaOrLocality() {
		return areaOrLocality;
	}

	public void setAreaOrLocality(String areaOrLocality) {
		this.areaOrLocality = areaOrLocality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getTypesOfApf() {
		return typesOfApf;
	}

	public void setTypesOfApf(String typesOfApf) {
		this.typesOfApf = typesOfApf;
	}

	public int getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(int totalUnits) {
		this.totalUnits = totalUnits;
	}

	public int getNumberOfTowers() {
		return numberOfTowers;
	}

	public void setNumberOfTowers(int numberOfTowers) {
		this.numberOfTowers = numberOfTowers;
	}

	public int getNoOfIndependentHouses() {
		return noOfIndependentHouses;
	}

	public void setNoOfIndependentHouses(int noOfIndependentHouses) {
		this.noOfIndependentHouses = noOfIndependentHouses;
	}

	public Date getProjectStartDate() {
		return projectStartDate;
	}

	public void setProjectStartDate(Date projectStartDate) {
		this.projectStartDate = projectStartDate;
	}

	public Date getProjectEndDate() {
		return projectEndDate;
	}

	public void setProjectEndDate(Date projectEndDate) {
		this.projectEndDate = projectEndDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCommencementCertificateNo() {
		return commencementCertificateNo;
	}

	public void setCommencementCertificateNo(String commencementCertificateNo) {
		this.commencementCertificateNo = commencementCertificateNo;
	}

	public String getCommencecrtfctissuingauthority() {
		return commencecrtfctissuingauthority;
	}

	public void setCommencecrtfctissuingauthority(String commencecrtfctissuingauthority) {
		this.commencecrtfctissuingauthority = commencecrtfctissuingauthority;
	}

	public int getTotalPlotArea() {
		return totalPlotArea;
	}

	public void setTotalPlotArea(int totalPlotArea) {
		this.totalPlotArea = totalPlotArea;
	}

	public int getConstructedArea() {
		return constructedArea;
	}

	public void setConstructedArea(int constructedArea) {
		this.constructedArea = constructedArea;
	}

	public String getTechnicalDone() {
		return technicalDone;
	}

	public void setTechnicalDone(String technicalDone) {
		this.technicalDone = technicalDone;
	}

	public String getLegalDone() {
		return legalDone;
	}

	public void setLegalDone(String legalDone) {
		this.legalDone = legalDone;
	}

	public String getRcuDone() {
		return rcuDone;
	}

	public void setRcuDone(String rcuDone) {
		this.rcuDone = rcuDone;
	}

	public BigDecimal getConstrctincompletionpercentage() {
		return constrctincompletionpercentage;
	}

	public void setConstrctincompletionpercentage(BigDecimal constrctincompletionpercentage) {
		this.constrctincompletionpercentage = constrctincompletionpercentage;
	}

	public BigDecimal getDisbursalRecommendedPercentage() {
		return disbursalRecommendedPercentage;
	}

	public void setDisbursalRecommendedPercentage(BigDecimal disbursalRecommendedPercentage) {
		this.disbursalRecommendedPercentage = disbursalRecommendedPercentage;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public long getBankBranchID() {
		return bankBranchID;
	}

	public void setBankBranchID(long bankBranchID) {
		this.bankBranchID = bankBranchID;
	}

	public String getBranchBankCode() {
		return branchBankCode;
	}

	public void setBranchBankCode(String branchBankCode) {
		this.branchBankCode = branchBankCode;
	}

	public String getBranchBankName() {
		return branchBankName;
	}

	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
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

	public String getiFSC() {
		return iFSC;
	}

	public void setiFSC(String iFSC) {
		this.iFSC = iFSC;
	}

	public List<ProjectUnits> getProjectUnits() {
		return projectUnits;
	}

	public void setProjectUnits(List<ProjectUnits> projectUnits) {
		this.projectUnits = projectUnits;
	}

	public List<DocumentDetails> getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(List<DocumentDetails> documentDetails) {
		this.documentDetails = documentDetails;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
}