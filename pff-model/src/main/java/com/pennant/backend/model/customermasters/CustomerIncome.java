/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerIncome.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date
 * : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

@XmlType(propOrder = { "incomeExpense", "category", "incomeType", "income", "margin" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerIncome extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1276183069308329161L;

	private long id;
	private long linkId;
	private long custId = Long.MIN_VALUE;
	private String custCif;
	private String custShrtName;
	@XmlElement(name = "custIncomeType")
	private String incomeType;
	private String incomeTypeDesc;
	@XmlElement
	private String incomeExpense;
	@XmlElement
	private String category;
	private String categoryDesc;
	@XmlElement(name = "custIncome")
	private BigDecimal income;
	@XmlElement
	private BigDecimal margin;
	private String lovValue;
	private CustomerIncome befImage;
	private LoggedInUser userDetails;

	private String sourceId;
	private BigDecimal totalRepayAmt;
	private Date maturityDate;
	private Date finStartDate;
	private String finCcy;
	private String toCcy;
	private boolean marginDeviation = false;
	private String inputSource;
	@XmlElement
	private BigDecimal grossIncome;

	private boolean jointCust = false;
	private int custType;

	public CustomerIncome() {
		super();
	}

	public CustomerIncome(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("custCif");
		excludeFields.add("custShrtName");
		excludeFields.add("incomeTypeDesc");
		excludeFields.add("categoryDesc");

		excludeFields.add("sourceId");
		excludeFields.add("totalRepayAmt");
		excludeFields.add("maturityDate");
		excludeFields.add("finStartDate");
		excludeFields.add("finCcy");
		excludeFields.add("toCcy");
		excludeFields.add("marginDeviation");
		excludeFields.add("custId");
		excludeFields.add("custCif");
		excludeFields.add("custShrtName");
		excludeFields.add("jointCust");
		excludeFields.add("incomeTypeDesc");
		excludeFields.add("categoryDesc");
		excludeFields.add("incomeTypeDesc");
		excludeFields.add("inputSource");
		excludeFields.add("custType");
		excludeFields.add("grossIncome");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLinkId() {
		return linkId;
	}

	public void setLinkId(long linkId) {
		this.linkId = linkId;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCustCif() {
		return custCif;
	}

	public void setCustCif(String custCif) {
		this.custCif = custCif;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public String getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	public String getIncomeTypeDesc() {
		return incomeTypeDesc;
	}

	public void setIncomeTypeDesc(String incomeTypeDesc) {
		this.incomeTypeDesc = incomeTypeDesc;
	}

	public String getIncomeExpense() {
		return incomeExpense;
	}

	public void setIncomeExpense(String incomeExpense) {
		this.incomeExpense = incomeExpense;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public CustomerIncome getBefImage() {
		return befImage;
	}

	public void setBefImage(CustomerIncome befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
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

	public boolean isMarginDeviation() {
		return marginDeviation;
	}

	public void setMarginDeviation(boolean marginDeviation) {
		this.marginDeviation = marginDeviation;
	}

	public boolean isJointCust() {
		return jointCust;
	}

	public void setJointCust(boolean jointCust) {
		this.jointCust = jointCust;
	}

	public int getCustType() {
		return custType;
	}

	public void setCustType(int custType) {
		this.custType = custType;
	}

	public String getInputSource() {
		return inputSource;
	}

	public void setInputSource(String inputSource) {
		this.inputSource = inputSource;
	}

	public void setLoginDetails(LoggedInUser userDetails) {
		setLastMntBy(userDetails.getUserId());
		this.userDetails = userDetails;
	}

	public BigDecimal getCalculatedAmount() {
		if (margin == null || income == null) {
			return BigDecimal.ZERO;
		} else {
			return income.multiply(margin.divide(new BigDecimal(100), RoundingMode.HALF_UP)).divide(new BigDecimal(100),
					RoundingMode.HALF_UP);
		}
	}

	public BigDecimal getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(BigDecimal grossIncome) {
		this.grossIncome = grossIncome;
	}

}
