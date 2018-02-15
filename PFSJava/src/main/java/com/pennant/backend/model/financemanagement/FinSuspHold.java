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
 * FileName    		:  FinSuspHold.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.financemanagement;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>FinSuspHold table</b>.<br>
 * 
 */
public class FinSuspHold extends AbstractWorkflowEntity implements Entity  {

	private static final long serialVersionUID = -6305409759684865400L;

	private long suspHoldID=Long.MIN_VALUE;
	private String product;
	private String productDesc;
	private String finType;
	private String finTypeDesc;
	private String finReference;
	private long custID ;
	private String custCIF ;
	private String CustShrtName ;
	private boolean active;
	private boolean newRecord = false;
	private String lovValue;
	private FinSuspHold befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinSuspHold() {
		super();
	}

	public FinSuspHold(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		excludeFields.add("productDesc");
		excludeFields.add("finTypeDesc");
		excludeFields.add("custCIF");
		excludeFields.add("CustShrtName");
		return excludeFields;
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return suspHoldID;
	}	
	public void setId (long id) {
		this.suspHoldID = id;
	}

	public long getSuspHoldID() {
		return suspHoldID;
	}
	public void setSuspHoldID(long suspHoldID) {
		this.suspHoldID = suspHoldID;
	}

	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}

	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return finTypeDesc;
	}
	public void setFinTypeDesc(String finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return CustShrtName;
	}
	public void setCustShrtName(String custShrtName) {
		CustShrtName = custShrtName;
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

	public FinSuspHold getBefImage() {
		return this.befImage;
	}
	public void setBefImage(FinSuspHold beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
