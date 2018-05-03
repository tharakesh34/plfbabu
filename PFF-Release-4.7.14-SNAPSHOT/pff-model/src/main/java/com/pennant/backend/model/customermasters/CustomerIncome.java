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
 * FileName    		:  CustomerIncome.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustomerIncome table</b>.<br>
 *
 */

@XmlType(propOrder = {"incomeExpense", "category", "custIncomeType", "custIncome"})
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerIncome extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -1276183069308329161L;

	private long custID =Long.MIN_VALUE;
	@XmlElement
	private String custIncomeType;
	@XmlElement
	private String incomeExpense;
	@XmlElement
	private String category;
	private String lovDescCategoryName;
	private BigDecimal margin;
	private String lovDescCustIncomeTypeName;
	@XmlElement
	private BigDecimal custIncome;
	private boolean jointCust = false;
	private boolean newRecord;
	private String lovValue;
	private CustomerIncome befImage;
	private LoggedInUser userDetails;
	private String lovDescCustCIF;
	private String lovDescCustShrtName;
	private String sourceId;
	private BigDecimal totalRepayAmt;
	private Date maturityDate;
	private Date finStartDate;
	private String finCcy;
	private String toCcy;


	public boolean isNew() {
		return isNewRecord();
	}

	public CustomerIncome() {
		super();
	}

	public CustomerIncome(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("sourceId");
		excludeFields.add("totalRepayAmt");
		excludeFields.add("maturityDate");
		excludeFields.add("finStartDate");
		excludeFields.add("finCcy");
		excludeFields.add("toCcy");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}

	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getLovDescCustCIF() {
		return lovDescCustCIF;
	}
	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}

	public String getCustIncomeType() {
		return custIncomeType;
	}
	public void setCustIncomeType(String custIncomeType) {
		this.custIncomeType = custIncomeType;
	}
	public String getLovDescCustIncomeTypeName() {
		return this.lovDescCustIncomeTypeName;
	}
	public void setLovDescCustIncomeTypeName(String lovDescCustIncomeTypeName) {
		this.lovDescCustIncomeTypeName = lovDescCustIncomeTypeName;
	}

	public BigDecimal getCustIncome() {
		return custIncome;
	}
	public void setCustIncome(BigDecimal custIncome) {
		this.custIncome = custIncome;
	}


	public boolean isJointCust() {
    	return jointCust;
    }

	public void setJointCust(boolean jointCust) {
    	this.jointCust = jointCust;
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

	public CustomerIncome getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(CustomerIncome beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}
	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}

	public void setLoginDetails(LoggedInUser userDetails){
		setLastMntBy(userDetails.getUserId());
		this.userDetails=userDetails;
	}

	public void setMargin(BigDecimal margin) {
	    this.margin = margin;
    }

	public BigDecimal getMargin() {
	    return margin;
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

	public void setLovDescCategoryName(String lovDescCategoryName) {
	    this.lovDescCategoryName = lovDescCategoryName;
    }

	public String getLovDescCategoryName() {
	    return lovDescCategoryName;
    }

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public BigDecimal getTotalRepayAmt() {
		return totalRepayAmt;
	}

	public void setTotalRepayAmt(BigDecimal totalRepayAmt) {
		this.totalRepayAmt = totalRepayAmt;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Date getFinStartDate() {
		return finStartDate;
	}

	public void setFinStartDate(Date finStartDate) {
		this.finStartDate = finStartDate;
	}

	public String getFinCcy() {
		return finCcy;
	}

	public void setFinCcy(String finCcy) {
		this.finCcy = finCcy;
	}

	public String getToCcy() {
		return toCcy;
	}

	public void setToCcy(String toCcy) {
		this.toCcy = toCcy;
	}

}
