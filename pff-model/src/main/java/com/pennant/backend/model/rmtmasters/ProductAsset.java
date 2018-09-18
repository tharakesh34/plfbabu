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
 * FileName    		:  ProductAsset.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-11-2011    														*
 *                                                                  						*
 * Modified Date    :  18-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-11-2011       Pennant~	                 0.1                                            * 
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

package com.pennant.backend.model.rmtmasters;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ProductAsset table</b>.<br>
 *
 */
public class ProductAsset extends AbstractWorkflowEntity implements Entity{

	private static final long serialVersionUID = -2377149322561552420L;
	private long assetID = Long.MIN_VALUE ;
	private String productCode;
	private String assetCode;
	private String assetDesc;
	private boolean assetIsActive;
	private boolean newRecord=false;
	private String lovValue;
	private ProductAsset befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public ProductAsset() {
		super();
	}
	public ProductAsset(long id) {
		super();
		this.setAssetID(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return assetID;
	}
	public void setId (long id) {
		this.assetID = id;
	}

	public long getAssetID() {
		return assetID;
	}
	public void setAssetID(long assetID) {
		this.assetID = assetID;
	}

	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public String getAssetDesc() {
		return assetDesc;
	}
	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}

	public boolean isAssetIsActive() {
		return assetIsActive;
	}
	public void setAssetIsActive(boolean assetIsActive) {
		this.assetIsActive = assetIsActive;
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

	public ProductAsset getBefImage() {
		return befImage;
	}
	public void setBefImage(ProductAsset befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;

	}
}
