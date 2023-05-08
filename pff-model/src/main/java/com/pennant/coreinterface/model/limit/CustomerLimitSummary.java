package com.pennant.coreinterface.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
	    super();
	}

	@XmlElement(name = "LimitReference")
	public String getLimitReference() {
		return limitReference;
	}

	public void setLimitReference(String limitReference) {
		this.limitReference = limitReference;
	}

	@XmlElement(name = "LimitDesc")
	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	@XmlElement(name = "ControllerUnder")
	public String getControllerUnder() {
		return controllerUnder;
	}

	public void setControllerUnder(String controllerUnder) {
		this.controllerUnder = controllerUnder;
	}

	@XmlElement(name = "Rev_Nrev")
	public String getRev_Nrev() {
		return rev_Nrev;
	}

	public void setRev_Nrev(String revNrev) {
		this.rev_Nrev = revNrev;
	}

	@XmlElement(name = "AppovedAmount")
	public BigDecimal getAppovedAmount() {
		return appovedAmount;
	}

	public void setAppovedAmount(BigDecimal appovedAmount) {
		this.appovedAmount = appovedAmount;
	}

	@XmlElement(name = "AppovedAmountCcy")
	public String getAppovedAmountCcy() {
		return appovedAmountCcy;
	}

	public void setAppovedAmountCcy(String appovedAmountCcy) {
		this.appovedAmountCcy = appovedAmountCcy;
	}

	@XmlElement(name = "Outstanding")
	public BigDecimal getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(BigDecimal outstanding) {
		this.outstanding = outstanding;
	}

	@XmlElement(name = "OutstandingCcy")
	public String getOutstandingCcy() {
		return outstandingCcy;
	}

	public void setOutstandingCcy(String outstandingCcy) {
		this.outstandingCcy = outstandingCcy;
	}

	@XmlElement(name = "Available")
	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	@XmlElement(name = "AvailableCcy")
	public String getAvailableCcy() {
		return availableCcy;
	}

	public void setAvailableCcy(String availableCcy) {
		this.availableCcy = availableCcy;
	}

	@XmlElement(name = "Reserved")
	public BigDecimal getReserved() {
		return reserved;
	}

	public void setReserved(BigDecimal reserved) {
		this.reserved = reserved;
	}

	@XmlElement(name = "ReservedCcy")
	public String getReservedCcy() {
		return reservedCcy;
	}

	public void setReservedCcy(String reservedCcy) {
		this.reservedCcy = reservedCcy;
	}

	@XmlElement(name = "LimitExpiryDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getLimitExpiryDate() {
		return limitExpiryDate;
	}

	public void setLimitExpiryDate(Date limitExpiryDate) {
		this.limitExpiryDate = limitExpiryDate;
	}

	@XmlElement(name = "Blocked")
	public BigDecimal getBlocked() {
		return blocked;
	}

	public void setBlocked(BigDecimal blocked) {
		this.blocked = blocked;
	}

	@XmlElement(name = "BlockedCcy")
	public String getBlockedCcy() {
		return blockedCcy;
	}

	public void setBlockedCcy(String blockedCcy) {
		this.blockedCcy = blockedCcy;
	}

	@XmlElement(name = "LimitCurrency")
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
		public Date unmarshal(final String v) throws ParseException {
			return dateFormat.parse(v);
		}

		@Override
		public String marshal(final Date v) {
			return dateFormat.format(v);
		}
	}
}
