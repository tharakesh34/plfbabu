package com.pennant.backend.model.limit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinanceMain;

@XmlType(propOrder = { "headerId", "custCIF", "custGrpCode", "referenceCode", "referenceNumber", "limitCurrency", "limitAmount",
		"returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class LimitTransactionDetail implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 3749037992177767072L;
	private long transactionId = Long.MIN_VALUE;
	@XmlElement(name="limitId")
	private long headerId;
	@XmlElement
	private String referenceCode;
	@XmlElement
	private String referenceNumber;
	private String limitGroup;
	private String limitLine;
	private String transactionType;
	private Timestamp transactionDate;
	private boolean overrideFlag;
	private BigDecimal transactionAmount;
	private String transactionCurrency;
	@XmlElement(name = "ccy")
	private String limitCurrency;
	@XmlElement(name = "amount")
	private BigDecimal limitAmount;
	@XmlElement(name = "cif")
	private String custCIF;
	@XmlElement(name = "customerGroup")
	private String custGrpCode;
	@XmlElement
	private WSReturnStatus returnStatus;
	@XmlTransient
	private long createdBy;
	@XmlTransient
	private String createdUser;
	@XmlTransient
	private Timestamp createdOn;
	private XMLGregorianCalendar createdDate;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	private Timestamp lastMntOn;
	private XMLGregorianCalendar lastMaintainedOn;
	private FinanceMain financeMain;
	private int schSeq;

	// API validation purpose only
	@SuppressWarnings("unused")
	private LimitTransactionDetail validateLimitTransactionDetail = this;

	public LimitTransactionDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("validateLimitTransactionDetail");
		excludeFields.add("custCIF");
		excludeFields.add("custGrpCode");
		return excludeFields;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public long getId() {
		return transactionId;
	}

	@Override
	public void setId(long id) {
		this.transactionId = id;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getLimitGroup() {
		return limitGroup;
	}

	public void setLimitGroup(String limitGroup) {
		this.limitGroup = limitGroup;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public boolean isOverrideFlag() {
		return overrideFlag;
	}

	public void setOverrideFlag(boolean overrideFlag) {
		this.overrideFlag = overrideFlag;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionCurrency() {
		return transactionCurrency;
	}

	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}

	public String getLimitCurrency() {
		return limitCurrency;
	}

	public void setLimitCurrency(String limitCurrency) {
		this.limitCurrency = limitCurrency;
	}

	public BigDecimal getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public XMLGregorianCalendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(XMLGregorianCalendar createdDate) {
		this.createdDate = createdDate;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public XMLGregorianCalendar getLastMaintainedOn() {
		return lastMaintainedOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar lastMaintainedOn) {
		this.lastMaintainedOn = lastMaintainedOn;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public String getLimitLine() {
		return limitLine;
	}

	public void setLimitLine(String limitLine) {
		this.limitLine = limitLine;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustGrpCode() {
		return custGrpCode;
	}

	public void setCustGrpCode(String custGrpCode) {
		this.custGrpCode = custGrpCode;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public int getSchSeq() {
		return schSeq;
	}

	public void setSchSeq(int schSeq) {
		this.schSeq = schSeq;
	}

}
