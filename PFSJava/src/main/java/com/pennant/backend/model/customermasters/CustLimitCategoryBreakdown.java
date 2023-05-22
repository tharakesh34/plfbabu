package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CustLimitCategoryBreakdown implements Serializable {

	private static final long serialVersionUID = 1348575696181175930L;

	private String accountNum;
	private String currency;
	private BigDecimal equivalent;
	private Date endDate;
	private String type;
	private String comments;

	public CustLimitCategoryBreakdown() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(String accountNum) {
		this.accountNum = accountNum;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getEquivalent() {
		return equivalent;
	}

	public void setEquivalent(BigDecimal equivalent) {
		this.equivalent = equivalent;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
