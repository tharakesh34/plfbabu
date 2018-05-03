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
 * FileName    		:  IncomeType.java                                                   * 	  
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

package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>IncomeType table</b>.<br>
 * 
 */
public class IncomeType extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -260562868574383176L;
	
	private String incomeExpense;
	private String category;
	private String lovDescCategoryName;
	private String incomeTypeCode;
	private String incomeTypeDesc;
	private BigDecimal margin;
	private boolean incomeTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private IncomeType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public IncomeType() {
		super();
	}

	public IncomeType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return incomeTypeCode;
	}
	public void setId(String id) {
		this.incomeTypeCode = id;
	}

	public String getIncomeTypeCode() {
		return incomeTypeCode;
	}
	public void setIncomeTypeCode(String incomeTypeCode) {
		this.incomeTypeCode = incomeTypeCode;
	}

	public String getIncomeTypeDesc() {
		return incomeTypeDesc;
	}
	public void setIncomeTypeDesc(String incomeTypeDesc) {
		this.incomeTypeDesc = incomeTypeDesc;
	}

	public boolean isIncomeTypeIsActive() {
		return incomeTypeIsActive;
	}
	public void setIncomeTypeIsActive(boolean incomeTypeIsActive) {
		this.incomeTypeIsActive = incomeTypeIsActive;
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

	public IncomeType getBefImage() {
		return this.befImage;
	}
	public void setBefImage(IncomeType beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setIncomeExpense(String incomeExpense) {
	    this.incomeExpense = incomeExpense;
    }

	public String getIncomeExpense() {
	    return incomeExpense;
    }

	public void setCategory(String category) {
	    this.category = category;
    }

	public String getCategory() {
	    return category;
    }

	public void setMargin(BigDecimal margin) {
	    this.margin = margin;
    }

	public BigDecimal getMargin() {
	    return margin;
    }

	public void setLovDescCategoryName(String lovDescCategoryName) {
	    this.lovDescCategoryName = lovDescCategoryName;
    }

	public String getLovDescCategoryName() {
	    return lovDescCategoryName;
    }
	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
