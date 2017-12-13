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
 * FileName    		:  AssetType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2016       PENNANT	                 0.1                                            * 
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

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>AssetType table</b>.<br>
 *
 */
public class AssetType extends AbstractWorkflowEntity {
private static final long serialVersionUID = 1L;

	private String assetType;
	private String assetDesc;
	private String remarks;
	private String preValidation;
	private String postValidation;
	private boolean active;
	private boolean newRecord=false;
	private String lovValue;
	private AssetType befImage;
	private LoggedInUser userDetails;
	private ExtendedFieldHeader	extendedFieldHeader	= new ExtendedFieldHeader();
	
	public boolean isNew() {
		return isNewRecord();
	}

	public AssetType() {
		super();
	}

	public AssetType(String id) {
		super();
		this.setId(id);
	}
	
	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("extendedFieldHeader");
	return excludeFields;
	}

	public String getId() {
		return assetType;
	}
	
	public void setId (String id) {
		this.assetType = id;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	
	public String getAssetDesc() {
		return assetDesc;
	}
	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public AssetType getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(AssetType beforeImage){
		this.befImage=beforeImage;
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
