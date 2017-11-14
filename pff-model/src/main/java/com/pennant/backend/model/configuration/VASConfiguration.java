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
 * FileName    		:  VASConfiguration.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.model.configuration;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>VASConfiguration table</b>.<br>
 * 
 */
@XmlType(propOrder = { "productCode", "productDesc", "productType", "vasFee", "allowFeeToModify", "manufacturerId",
		"recAgainst", "feeAccrued", "recurringType", "freeLockPeriod", "remarks", "active", "extendedFieldDetailList","returnStatus" })
@XmlRootElement(name = "vasConfiguration")
@XmlAccessorType(XmlAccessType.NONE)
public class VASConfiguration extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	@XmlElement(name="product")
	private String productCode;
	@XmlElement
	private String productDesc;
	@XmlElement
	private String productType;
	private String productTypeDesc;
	private String productCategory;
	private String productCategoryDesc;
	@XmlElement
	private BigDecimal vasFee;
	@XmlElement
	private boolean allowFeeToModify;
	@XmlElement
	private long manufacturerId;
	private String manufacturerName;
	@XmlElement
	private String recAgainst;
	@XmlElement
	private boolean feeAccrued;
	private long feeAccounting;
	private String feeAccountingName;
	private String feeAccountingDesc;
	private long accrualAccounting;
	private String accrualAccountingName;
	private String accrualAccountingDesc;
	@XmlElement
	private boolean recurringType;
	@XmlElement
	private int freeLockPeriod;
	private boolean preValidationReq;
	private boolean postValidationReq;
	private String	preValidation;
	private String  postValidation;

	@XmlElement
	private boolean active;
	@XmlElement
	private String remarks;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private VASConfiguration befImage;
	@XmlTransient
	private LoggedInUser userDetails;
	private ExtendedFieldHeader	extendedFieldHeader;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlElementWrapper(name="extendedFields")
	@XmlElement(name="extendedField")
	private List<ExtendedFieldDetail> extendedFieldDetailList;

	public boolean isNew() {
		return isNewRecord();
	}

	public VASConfiguration() {
		super();
	}

	public VASConfiguration(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("feeAccountingName");
		excludeFields.add("accrualAccountingName");
		excludeFields.add("feeAccountingDesc");
		excludeFields.add("accrualAccountingDesc");
		excludeFields.add("manufacturerName");
		excludeFields.add("productTypeDesc");
		excludeFields.add("productCategory");
		excludeFields.add("productCategoryDesc");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("returnStatus");
		excludeFields.add("extendedFieldDetailList");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return productCode;
	}
	public void setId(String id) {
		this.productCode = id;
	}

	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductTypeDesc() {
		return productTypeDesc;
	}

	public void setProductTypeDesc(String productTypeDesc) {
		this.productTypeDesc = productTypeDesc;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public BigDecimal getVasFee() {
		return vasFee;
	}

	public void setVasFee(BigDecimal vasFee) {
		this.vasFee = vasFee;
	}

	public boolean isAllowFeeToModify() {
		return allowFeeToModify;
	}

	public void setAllowFeeToModify(boolean allowFeeToModify) {
		this.allowFeeToModify = allowFeeToModify;
	}

	public long getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getRecAgainst() {
		return recAgainst;
	}
	public void setRecAgainst(String recAgainst) {
		this.recAgainst = recAgainst;
	}

	public boolean isFeeAccrued() {
		return feeAccrued;
	}
	public void setFeeAccrued(boolean feeAccrued) {
		this.feeAccrued = feeAccrued;
	}

	public long getFeeAccounting() {
		return feeAccounting;
	}
	public void setFeeAccounting(long feeAccounting) {
		this.feeAccounting = feeAccounting;
	}

	public String getFeeAccountingName() {
		return this.feeAccountingName;
	}
	public void setFeeAccountingName(String feeAccountingName) {
		this.feeAccountingName = feeAccountingName;
	}

	public long getAccrualAccounting() {
		return accrualAccounting;
	}
	public void setAccrualAccounting(long accrualAccounting) {
		this.accrualAccounting = accrualAccounting;
	}

	public String getAccrualAccountingName() {
		return this.accrualAccountingName;
	}
	public void setAccrualAccountingName(String accrualAccountingName) {
		this.accrualAccountingName = accrualAccountingName;
	}

	public boolean isRecurringType() {
		return recurringType;
	}
	public void setRecurringType(boolean recurringType) {
		this.recurringType = recurringType;
	}

	public int getFreeLockPeriod() {
		return freeLockPeriod;
	}
	public void setFreeLockPeriod(int freeLockPeriod) {
		this.freeLockPeriod = freeLockPeriod;
	}

	public boolean isPreValidationReq() {
		return preValidationReq;
	}
	public void setPreValidationReq(boolean preValidationReq) {
		this.preValidationReq = preValidationReq;
	}

	public boolean isPostValidationReq() {
		return postValidationReq;
	}
	public void setPostValidationReq(boolean postValidationReq) {
		this.postValidationReq = postValidationReq;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}
	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public VASConfiguration getBefImage() {
		return this.befImage;
	}
	public void setBefImage(VASConfiguration beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFeeAccountingDesc() {
		return feeAccountingDesc;
	}
	public void setFeeAccountingDesc(String feeAccountingDesc) {
		this.feeAccountingDesc = feeAccountingDesc;
	}

	public String getAccrualAccountingDesc() {
		return accrualAccountingDesc;
	}
	public void setAccrualAccountingDesc(String accrualAccountingDesc) {
		this.accrualAccountingDesc = accrualAccountingDesc;
	}

	public String getPreValidation() {
		return preValidation;
	}

	public void setPreValidation(String preValidation) {
		this.preValidation = preValidation;
	}

	public String getPostValidation() {
		return postValidation;
	}

	public void setPostValidation(String postValidation) {
		this.postValidation = postValidation;
	}

	public String getProductCategoryDesc() {
		return productCategoryDesc;
	}

	public void setProductCategoryDesc(String productCategoryDesc) {
		this.productCategoryDesc = productCategoryDesc;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<ExtendedFieldDetail> getExtendedFieldDetailList() {
		return extendedFieldDetailList;
	}

	public void setExtendedFieldDetailList(List<ExtendedFieldDetail> extendedFieldDetailList) {
		this.extendedFieldDetailList = extendedFieldDetailList;
	}
}
