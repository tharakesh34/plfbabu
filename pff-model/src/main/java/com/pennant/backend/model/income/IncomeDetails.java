package com.pennant.backend.model.income;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class IncomeDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -1276183069308329161L;

	private long id;
	private long incomeId;
	private String incomeType;
	private String incomeTypeName;
	private String expense;
	private String category;
	private String categoryName;
	private BigDecimal margin;
	private BigDecimal income;

	private boolean newRecord;
	private String lovValue;
	private CustomerIncome befImage;
	private LoggedInUser userDetails;
	private String sourceId;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("sourceId");
		excludeFields.add("incomeTypeName");
		excludeFields.add("categoryName");

		return excludeFields;
	}

	public BigDecimal getCalculatedAmount() {
		if (margin == null || income == null) {
			return BigDecimal.ZERO;
		} else {
			return income.multiply(margin.divide(new BigDecimal(100), RoundingMode.HALF_UP)).divide(new BigDecimal(100),
					RoundingMode.HALF_UP);
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getIncomeId() {
		return incomeId;
	}

	public void setIncomeId(long incomeId) {
		this.incomeId = incomeId;
	}

	public String getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	public String getIncomeTypeName() {
		return incomeTypeName;
	}

	public void setIncomeTypeName(String incomeTypeName) {
		this.incomeTypeName = incomeTypeName;
	}

	public String getExpense() {
		return expense;
	}

	public void setExpense(String expense) {
		this.expense = expense;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
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
}
