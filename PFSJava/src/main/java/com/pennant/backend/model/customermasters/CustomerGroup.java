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
 * FileName    		:  CustomerGroup.java                                                   * 	  
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

package com.pennant.backend.model.customermasters;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>CustomerGroup table</b>.<br>
 *
 */
public class CustomerGroup extends AbstractWorkflowEntity implements Entity {
	
	private static final long serialVersionUID = 6577918356160899476L;
	
	private long custGrpID = Long.MIN_VALUE;
	private String custGrpCode;
	private String custGrpDesc;
	private String custGrpRO1;
	private String lovDescCustGrpRO1Name;
	private long custGrpLimit;
	private boolean custGrpIsActive;
	private boolean newRecord;
	private String lovValue;
	private CustomerGroup befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerGroup() {
		super();
	}

	public CustomerGroup(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public long getId() {
		return custGrpID;
	}
	public void setId (long id) {
		this.custGrpID = id;
	}
	
	public long getCustGrpID() {
		return custGrpID;
	}
	public void setCustGrpID(long custGrpID) {
		this.custGrpID = custGrpID;
	}
	
	public String getCustGrpCode() {
		return custGrpCode;
	}
	public void setCustGrpCode(String custGrpCode) {
		this.custGrpCode = custGrpCode;
	}
	
	public String getCustGrpDesc() {
		return custGrpDesc;
	}
	public void setCustGrpDesc(String custGrpDesc) {
		this.custGrpDesc = custGrpDesc;
	}
	
	public String getCustGrpRO1() {
		return custGrpRO1;
	}
	public void setCustGrpRO1(String custGrpRO1) {
		this.custGrpRO1 = custGrpRO1;
	}

	public String getLovDescCustGrpRO1Name() {
		return lovDescCustGrpRO1Name;
	}
	public void setLovDescCustGrpRO1Name(String lovDescCustGrpRO1Name) {
		this.lovDescCustGrpRO1Name = lovDescCustGrpRO1Name;
	}

	public long getCustGrpLimit() {
		return custGrpLimit;
	}
	public void setCustGrpLimit(long custGrpLimit) {
		this.custGrpLimit = custGrpLimit;
	}

	public boolean isCustGrpIsActive() {
		return custGrpIsActive;
	}
	public void setCustGrpIsActive(boolean custGrpIsActive) {
		this.custGrpIsActive = custGrpIsActive;
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

	public CustomerGroup getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerGroup beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
