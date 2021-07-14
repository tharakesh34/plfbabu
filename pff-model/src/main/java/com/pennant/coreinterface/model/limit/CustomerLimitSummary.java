package com.pennant.coreinterface.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "Limits")
public class CustomerLimitSummary implements Serializable {

	private static final long serialVersionUID = 7108561336746598765L;

	private String limitReference;
	private String limitDesc;
	private String controllerUnder;
	private String rev_Nrev;
	private Date limitExpiryDate;
	private String limitCurrency;
	private String appovedAmountCcy;
	private BigDecimal appovedAmount = BigDecimal.ZERO;
	private String outstandingCcy;
	private BigDecimal outstanding = BigDecimal.ZERO;
	private String blockedCcy;
	private BigDecimal blocked = BigDecimal.ZERO;
	private String reservedCcy;
	private BigDecimal reserved = BigDecimal.ZERO;
	private String availableCcy;
	private BigDecimal available = BigDecimal.ZERO;
	private String customerid;
	private String limitGroup;
	private String limitItem;

	public CustomerLimitSummary() {

	}

	@JsonProperty("LimitReference")
	public String getLimitReference() {
		return limitReference;
	}

	public void setLimitReference(String limitReference) {
		this.limitReference = limitReference;
	}

	@JsonProperty("LimitDesc")
	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	@JsonProperty("ControllerUnder")
	public String getControllerUnder() {
		return controllerUnder;
	}

	public void setControllerUnder(String controllerUnder) {
		this.controllerUnder = controllerUnder;
	}

	@JsonProperty("Rev_Nrev")
	public String getRev_Nrev() {
		return rev_Nrev;
	}

	public void setRev_Nrev(String revNrev) {
		this.rev_Nrev = revNrev;
	}

	@JsonProperty("AppovedAmount")
	public BigDecimal getAppovedAmount() {
		return appovedAmount;
	}

	public void setAppovedAmount(BigDecimal appovedAmount) {
		this.appovedAmount = appovedAmount;
	}

	@JsonProperty("AppovedAmountCcy")
	public String getAppovedAmountCcy() {
		return appovedAmountCcy;
	}

	public void setAppovedAmountCcy(String appovedAmountCcy) {
		this.appovedAmountCcy = appovedAmountCcy;
	}

	@JsonProperty("Outstanding")
	public BigDecimal getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(BigDecimal outstanding) {
		this.outstanding = outstanding;
	}

	@JsonProperty("OutstandingCcy")
	public String getOutstandingCcy() {
		return outstandingCcy;
	}

	public void setOutstandingCcy(String outstandingCcy) {
		this.outstandingCcy = outstandingCcy;
	}

	@JsonProperty("Available")
	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	@JsonProperty("AvailableCcy")
	public String getAvailableCcy() {
		return availableCcy;
	}

	public void setAvailableCcy(String availableCcy) {
		this.availableCcy = availableCcy;
	}

	@JsonProperty("Reserved")
	public BigDecimal getReserved() {
		return reserved;
	}

	public void setReserved(BigDecimal reserved) {
		this.reserved = reserved;
	}

	@JsonProperty("ReservedCcy")
	public String getReservedCcy() {
		return reservedCcy;
	}

	public void setReservedCcy(String reservedCcy) {
		this.reservedCcy = reservedCcy;
	}

	@JsonProperty("LimitExpiryDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getLimitExpiryDate() {
		return limitExpiryDate;
	}

	public void setLimitExpiryDate(Date limitExpiryDate) {
		this.limitExpiryDate = limitExpiryDate;
	}

	@JsonProperty("Blocked")
	public BigDecimal getBlocked() {
		return blocked;
	}

	public void setBlocked(BigDecimal blocked) {
		this.blocked = blocked;
	}

	@JsonProperty("BlockedCcy")
	public String getBlockedCcy() {
		return blockedCcy;
	}

	public void setBlockedCcy(String blockedCcy) {
		this.blockedCcy = blockedCcy;
	}

	@JsonProperty("LimitCurrency")
	public String getLimitCurrency() {
		return limitCurrency;
	}

	public void setLimitCurrency(String limitCurrency) {
		this.limitCurrency = limitCurrency;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getLimitGroup() {
		return limitGroup;
	}

	public void setLimitGroup(String limitGroup) {
		this.limitGroup = limitGroup;
	}

	public String getLimitItem() {
		return limitItem;
	}

	public void setLimitItem(String limitItem) {
		this.limitItem = limitItem;
	}

	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		@Override
		public Date unmarshal(final String v) throws Exception {
			return dateFormat.parse(v);
		}

		@Override
		public String marshal(final Date v) throws Exception {
			return dateFormat.format(v);
		}
	}
}
