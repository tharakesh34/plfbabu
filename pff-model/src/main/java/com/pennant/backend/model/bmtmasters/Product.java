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
 * FileName    		:  Product.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.bmtmasters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Product table</b>.<br>
 * 
 */
public class Product extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 3936360447748889441L;

	private String productCode;
	private String productDesc;
	private String productCategory;
	private boolean newRecord;
	private String lovValue;
	private Product befImage;
	private LoggedInUser userDetails;
	//Allow Manual Deviation 
	private boolean allowDeviation;
	
	private List<ProductAsset> productAssetList;
	private List<ProductDeviation> productDeviationDetails;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("productDeviationDetails");
		return excludeFields;
	}
	public boolean isNew() {
		return isNewRecord();
	}

	public Product() {
		super();
	}

	public Product(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public Product getBefImage() {
		return this.befImage;
	}
	public void setBefImage(Product beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setProductAssetList(List<ProductAsset> productAssetList) {
		this.productAssetList = productAssetList;
	}
	public List<ProductAsset> getProductAssetList() {
		return productAssetList;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public List<ProductDeviation> getProductDeviationDetails() {
		return productDeviationDetails;
	}

	public void setProductDeviationDetails(List<ProductDeviation> productDeviationDetails) {
		this.productDeviationDetails = productDeviationDetails;
	}

	public boolean isAllowDeviation() {
		return allowDeviation;
	}

	public void setAllowDeviation(boolean allowDeviation) {
		this.allowDeviation = allowDeviation;
	}
}
