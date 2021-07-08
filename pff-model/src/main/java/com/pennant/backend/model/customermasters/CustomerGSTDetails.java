package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "gstNumber", "frequancy", "financialYear", "salAmount" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerGSTDetails extends AbstractWorkflowEntity implements Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "gstId")
	private long id = Long.MIN_VALUE;

	private long headerId;

	@XmlElement
	private String frequancy;
	@XmlElement
	private String financialYear;
	@XmlElement
	private BigDecimal salAmount = BigDecimal.ZERO;
	private boolean newRecord;
	private String lovValue;
	private CustomerGSTDetails befImage;
	private int keyValue = 0;

	private LoggedInUser userDetails;

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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFrequancy() {
		return frequancy;
	}

	public void setFrequancy(String frequancy) {
		this.frequancy = frequancy;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public BigDecimal getSalAmount() {
		return salAmount;
	}

	public void setSalAmount(BigDecimal salAmount) {
		this.salAmount = salAmount;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	@Override
	public boolean isNew() {
		return newRecord;
	}

	public CustomerGSTDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(CustomerGSTDetails befImage) {
		this.befImage = befImage;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("keyValue");
		return excludeFields;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

}
