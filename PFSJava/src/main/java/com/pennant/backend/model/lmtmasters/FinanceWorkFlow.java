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
 * FileName    		:  FinanceWorkFlow.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.lmtmasters;

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FinanceWorkFlow table</b>.<br>
 *
 */
public class FinanceWorkFlow extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 6176092273862848126L;
	private String finType;
	private String typeCode;
	private String finEvent;
	private String lovDescFinTypeName;
	private String collateralDesc;
	private String vasProductDesc;
	private String commitmentTypeDesc;
	private String lovDescFacilityTypeName;
	private String lovDescFinDivisionName;
	private String lovDescProductCodeName;
	private String lovDescPromoFinTypeDesc;
	private String lovDescProductName;
	private String screenCode = "DDE";
	private String workFlowType;
	private String moduleName;
	private String lovDescWorkFlowTypeName;
	private String lovDescWorkFlowRolesName;
	private String lovDescFirstTaskOwner;
	private boolean newRecord;
	private String lovValue;
	private FinanceWorkFlow befImage;
	private LoggedInUser userDetails;

	private String lovDescPromotionCode;
	private String lovDescPromotionName;
	private String FinAssetType;
	
	private String productCategory;
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("FinAssetType");
		excludeFields.add("productCategory");
		excludeFields.add("collateralDesc");
		excludeFields.add("vasProductDesc");
		excludeFields.add("commitmentTypeDesc");
		excludeFields.add("typeCode");
		return excludeFields;
	}
	
	
	public boolean isNew() {
		return isNewRecord();
	}

	public FinanceWorkFlow() {
		super();
	}
	public FinanceWorkFlow(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return finType;
	}
	public void setId (String id) {
		this.finType = id;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public String getLovDescFinTypeName() {
		return this.lovDescFinTypeName;
	}
	public void setLovDescFinTypeName (String lovDescFinTypeName) {
		this.lovDescFinTypeName = lovDescFinTypeName;
	}
	
	public String getCollateralDesc() {
		return collateralDesc;
	}
	public void setCollateralDesc(String collateralDesc) {
		this.collateralDesc = collateralDesc;
	}

	public String getVasProductDesc() {
		return vasProductDesc;
	}
	public void setVasProductDesc(String vasProductDesc) {
		this.vasProductDesc = vasProductDesc;
	}

	public String getCommitmentTypeDesc() {
		return commitmentTypeDesc;
	}
	public void setCommitmentTypeDesc(String commitmentTypeDesc) {
		this.commitmentTypeDesc = commitmentTypeDesc;
	}

	public void setLovDescProductCodeName(String lovDescProductCodeName) {
	    this.lovDescProductCodeName = lovDescProductCodeName;
    }
	public String getLovDescProductCodeName() {
	    return lovDescProductCodeName;
    }

	public String getScreenCode() {
		return screenCode;
	}
	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}
	
	public String getWorkFlowType() {
		return workFlowType;
	}
	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public String getModuleName() {
    	return moduleName;
    }

	public void setModuleName(String moduleName) {
    	this.moduleName = moduleName;
    }

	public String getLovDescWorkFlowTypeName() {
		return this.lovDescWorkFlowTypeName;
	}
	public void setLovDescWorkFlowTypeName (String lovDescWorkFlowTypeName) {
		this.lovDescWorkFlowTypeName = lovDescWorkFlowTypeName;
	}
	
	public String getLovDescWorkFlowRolesName() {
		return lovDescWorkFlowRolesName;
	}
	public void setLovDescWorkFlowRolesName(String lovDescWorkFlowRolesName) {
		this.lovDescWorkFlowRolesName = lovDescWorkFlowRolesName;
	}

	public String getLovDescFirstTaskOwner() {
    	return lovDescFirstTaskOwner;
    }
	public void setLovDescFirstTaskOwner(String lovDescFirstTaskOwner) {
    	this.lovDescFirstTaskOwner = lovDescFirstTaskOwner;
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

	public FinanceWorkFlow getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceWorkFlow beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public String getLovDescFacilityTypeName() {
    	return lovDescFacilityTypeName;
    }
	public void setLovDescFacilityTypeName(String lovDescFacilityTypeName) {
    	this.lovDescFacilityTypeName = lovDescFacilityTypeName;
    }
	
	public void setLovDescFinDivisionName(String lovDescFinDivisionName) {
	    this.lovDescFinDivisionName = lovDescFinDivisionName;
    }
	public String getLovDescFinDivisionName() {
	    return lovDescFinDivisionName;
    }

	public String getLovDescProductName() {
	    return lovDescProductName;
    }
	public void setLovDescProductName(String lovDescProductName) {
	    this.lovDescProductName = lovDescProductName;
    }

	public String getLovDescPromoFinTypeDesc() {
	    return lovDescPromoFinTypeDesc;
    }
	public void setLovDescPromoFinTypeDesc(String lovDescPromoFinTypeDesc) {
	    this.lovDescPromoFinTypeDesc = lovDescPromoFinTypeDesc;
    }

	public String getLovDescPromotionName() {
	    return lovDescPromotionName;
    }
	public void setLovDescPromotionName(String lovDescPromotionName) {
	    this.lovDescPromotionName = lovDescPromotionName;
    }

	public String getLovDescPromotionCode() {
	    return lovDescPromotionCode;
    }
	public void setLovDescPromotionCode(String lovDescPromotionCode) {
	    this.lovDescPromotionCode = lovDescPromotionCode;
    }

	public String getFinAssetType() {
	    return FinAssetType;
    }
	public void setFinAssetType(String finAssetType) {
	    FinAssetType = finAssetType;
    }

	public String getFinEvent() {
	    return finEvent;
    }
	public void setFinEvent(String finEvent) {
	    this.finEvent = finEvent;
    }

	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}


	public String getTypeCode() {
		return typeCode;
	}


	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

}
