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
 * FileName    		:  CollateralStructure.java                                                   * 	  
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
package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CollateralStructure table</b>.<br>
 * 
 */
@XmlType(propOrder = { "collateralType", "collateralDesc", "ltvType", "ltvPercentage", "marketableSecurities",
		"collateralLocReq", "collateralValuatorReq", "remarks", "active", "allowLtvWaiver", "maxLtvWaiver",
		"extendedFieldHeader", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class CollateralStructure extends AbstractWorkflowEntity {
	private static final long	serialVersionUID	= 1L;

	@XmlElement
	private String				collateralType;
	private String				collateralTypeName;
	
	@XmlElement
	private String				collateralDesc;
	@XmlElement
	private String				ltvType;
	private String				ltvTypeName;
	
	@XmlElement(name="ltvPerc")
	private BigDecimal			ltvPercentage;
	
	@XmlElement
	private boolean				marketableSecurities;
	@XmlElement
	private boolean				active;
	private boolean				preValidationReq;
	private boolean				postValidationReq;
	
	@XmlElement
	private boolean				collateralLocReq;
	@XmlElement
	private boolean				collateralValuatorReq;
	
	@XmlElement
	private String				remarks;
	
	@XmlElement(name="alwLtvWaiver")
	private boolean				allowLtvWaiver;
	
	@XmlElement(name="maxLtvWaiverPerc")
	private BigDecimal			maxLtvWaiver;
	private boolean				newRecord			= false;
	private String				lovValue;
	private CollateralStructure	befImage;
	@XmlTransient
	private LoggedInUser		userDetails;

	@XmlElement(name="extendedDetail")
	private ExtendedFieldHeader	extendedFieldHeader;
	
	@XmlElement
	private WSReturnStatus returnStatus;
	
	private String fields;
	private String actualBlock;
	private String sQLRule;
	private String				preValidation;
	private String				postValidation;

	public boolean isNew() {
		return isNewRecord();
	}

	public CollateralStructure() {
		super();
	}

	public CollateralStructure(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("collateralTypeName");
		excludeFields.add("ltvTypeName");
		excludeFields.add("extendedFieldHeader");
		excludeFields.add("extendedFieldList");
		excludeFields.add("returnStatus");
		return excludeFields;
	}

	public String getId() {
		return collateralType;
	}

	public void setId(String id) {
		this.collateralType = id;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public String getCollateralTypeName() {
		return this.collateralTypeName;
	}

	public void setCollateralTypeName(String collateralTypeName) {
		this.collateralTypeName = collateralTypeName;
	}

	public String getCollateralDesc() {
		return collateralDesc;
	}

	public void setCollateralDesc(String collateralDesc) {
		this.collateralDesc = collateralDesc;
	}

	public String getLtvType() {
		return ltvType;
	}

	public void setLtvType(String ltvType) {
		this.ltvType = ltvType;
	}

	public String getLtvTypeName() {
		return this.ltvTypeName;
	}

	public void setLtvTypeName(String ltvTypeName) {
		this.ltvTypeName = ltvTypeName;
	}

	public BigDecimal getLtvPercentage() {
		return ltvPercentage;
	}

	public void setLtvPercentage(BigDecimal ltvPercentage) {
		this.ltvPercentage = ltvPercentage;
	}

	public boolean isMarketableSecurities() {
		return marketableSecurities;
	}

	public void setMarketableSecurities(boolean marketableSecurities) {
		this.marketableSecurities = marketableSecurities;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public boolean isCollateralLocReq() {
		return collateralLocReq;
	}

	public void setCollateralLocReq(boolean collateralLocReq) {
		this.collateralLocReq = collateralLocReq;
	}

	public boolean isCollateralValuatorReq() {
		return collateralValuatorReq;
	}

	public void setCollateralValuatorReq(boolean collateralValuatorReq) {
		this.collateralValuatorReq = collateralValuatorReq;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isAllowLtvWaiver() {
		return allowLtvWaiver;
	}

	public void setAllowLtvWaiver(boolean allowLtvWaiver) {
		this.allowLtvWaiver = allowLtvWaiver;
	}

	public BigDecimal getMaxLtvWaiver() {
		return maxLtvWaiver;
	}

	public void setMaxLtvWaiver(BigDecimal maxLtvWaiver) {
		this.maxLtvWaiver = maxLtvWaiver;
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

	public CollateralStructure getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CollateralStructure beforeImage) {
		this.befImage = beforeImage;
	}

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

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getActualBlock() {
		return actualBlock;
	}
	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public String getSQLRule() {
		return sQLRule;
	}
	public void setSQLRule(String sQLRule) {
		this.sQLRule = sQLRule;
	}
	
	public HashMap<String, Object> getDeclaredFieldValues() {
		HashMap<String, Object> structureMap = new HashMap<String, Object>();
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				structureMap.put(this.getClass().getDeclaredFields()[i].getName(), this.getClass()
						.getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}
		return structureMap;
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
}
