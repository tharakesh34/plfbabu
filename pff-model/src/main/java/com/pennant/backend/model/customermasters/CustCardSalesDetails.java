package com.pennant.backend.model.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "id", "cardSalesId", "month", "salesAmount", "noOfSettlements", "totalNoOfCredits",
		"totalNoOfDebits", "totalCreditValue", "totalDebitValue", "inwardBounce", "outwardBounce" })
@XmlAccessorType(XmlAccessType.NONE)
public class CustCardSalesDetails extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "id")
	private long id = Long.MIN_VALUE;
	@XmlElement(name = "cardSalesId")
	private long cardSalesId;
	@XmlElement(name = "month")
	private Date month;
	@XmlElement(name = "salesAmount")
	private BigDecimal salesAmount;
	@XmlElement(name = "noOfSettlements")
	private int noOfSettlements;
	@XmlElement(name = "totalNoOfCredits")
	private int totalNoOfCredits;
	@XmlElement(name = "totalNoOfDebits")
	private int totalNoOfDebits;
	@XmlElement(name = "totalCreditValue")
	private BigDecimal totalCreditValue;
	@XmlElement(name = "totalDebitValue")
	private BigDecimal totalDebitValue;
	@XmlElement(name = "inwardBounce")
	private BigDecimal inwardBounce;
	@XmlElement(name = "outwardBounce")
	private BigDecimal outwardBounce;
	private CustCardSalesDetails befImage;
	private LoggedInUser userDetails;
	private boolean newRecord = false;
	private String lovValue;
	private int keyValue = 0;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("keyValue");
		return excludeFields;
	}

	public boolean isNew() {
		return false;
	}

	public long getId() {
		return id;
	}

	public Date getMonth() {
		return month;
	}

	public BigDecimal getSalesAmount() {
		return salesAmount;
	}

	public int getTotalNoOfCredits() {
		return totalNoOfCredits;
	}

	public int getTotalNoOfDebits() {
		return totalNoOfDebits;
	}

	public BigDecimal getTotalCreditValue() {
		return totalCreditValue;
	}

	public BigDecimal getTotalDebitValue() {
		return totalDebitValue;
	}

	public CustCardSalesDetails getBefImage() {
		return befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public void setSalesAmount(BigDecimal salesAmount) {
		this.salesAmount = salesAmount;
	}

	public void setTotalNoOfCredits(int totalNoOfCredits) {
		this.totalNoOfCredits = totalNoOfCredits;
	}

	public void setTotalNoOfDebits(int totalNoOfDebits) {
		this.totalNoOfDebits = totalNoOfDebits;
	}

	public void setTotalCreditValue(BigDecimal totalCreditValue) {
		this.totalCreditValue = totalCreditValue;
	}

	public void setTotalDebitValue(BigDecimal totalDebitValue) {
		this.totalDebitValue = totalDebitValue;
	}

	public void setBefImage(CustCardSalesDetails befImage) {
		this.befImage = befImage;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}

	public int getNoOfSettlements() {
		return noOfSettlements;
	}

	public void setNoOfSettlements(int noOfSettlements) {
		this.noOfSettlements = noOfSettlements;
	}

	public BigDecimal getInwardBounce() {
		return inwardBounce;
	}

	public BigDecimal getOutwardBounce() {
		return outwardBounce;
	}

	public void setInwardBounce(BigDecimal inwardBounce) {
		this.inwardBounce = inwardBounce;
	}

	public void setOutwardBounce(BigDecimal outwardBounce) {
		this.outwardBounce = outwardBounce;
	}

	public long getCardSalesId() {
		return cardSalesId;
	}

	public void setCardSalesId(long cardSalesId) {
		this.cardSalesId = cardSalesId;
	}

}
