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
 * FileName    		:  ExpenseType.java                                                   	* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.amtmasters;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ExpenseType table</b>.<br>
 *
 */
public class ExpenseType extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;
	private long expenceTypeId = Long.MIN_VALUE;
	private String expenceTypeName;
	private String expenseFor;
	private boolean newRecord;
	private String lovValue;
	private ExpenseType befImage;
	private LoggedInUser userDetails;

	public ExpenseType() {
		super();
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public ExpenseType(long id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods
	
	public long getId() {
		return expenceTypeId;
	}
	
	public void setId (long id) {
		this.expenceTypeId = id;
	}
	
	public long getExpenceTypeId() {
		return expenceTypeId;
	}
	public void setExpenceTypeId(long expenceTypeId) {
		this.expenceTypeId = expenceTypeId;
	}
	
	public String getExpenceTypeName() {
		return expenceTypeName;
	}
	public void setExpenceTypeName(String expenceTypeName) {
		this.expenceTypeName = expenceTypeName;
	}
	
	public String getExpenseFor() {
    	return expenseFor;
    }

	public void setExpenseFor(String expenseFor) {
    	this.expenseFor = expenseFor;
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

	public ExpenseType getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ExpenseType beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
