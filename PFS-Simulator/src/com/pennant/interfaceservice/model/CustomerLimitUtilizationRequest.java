package com.pennant.interfaceservice.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "CustomerLimitUtilization")
public class CustomerLimitUtilizationRequest {

	private String referenceNum;
	private String dealID;
	private String customerReference;
	private String status;
	private String limitRef;
	private String userID;
	private String OverrideApprover;
	private BigDecimal DealAmount = BigDecimal.ZERO;
	private String DealCcy;
	private Date DealExpiry;
	private String draweeID;
	private String brokerID;
	private String PrevDealID;
	private BigDecimal MTM = BigDecimal.ZERO;
	private int Tenor;
	private BigDecimal profitRate;
	private BigDecimal EffProfitRate = BigDecimal.ZERO;
	private BigDecimal OutstandingAmount = BigDecimal.ZERO;
	private BigDecimal PastDueAmount = BigDecimal.ZERO;
	private Date pastDueDate;
	private BigDecimal amountBuy = BigDecimal.ZERO;
	private String currencyBuy;
	private BigDecimal amountSell = BigDecimal.ZERO;
	private String currencySell;
	private Date valueDate;
	private String bookCcy;
	private BigDecimal bookPrice;
	private String MarketCcy;
	private BigDecimal marketPrice = BigDecimal.ZERO;
	private String ExceededDealRef;
	private String isBlocked;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name="ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name="DealID")
	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	@XmlElement(name="CustomerReference")
	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	@XmlElement(name="LimitRef")
	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	@XmlElement(name="UserID")
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@XmlElement(name="OverrideApprover")
	public String getOverrideApprover() {
		return OverrideApprover;
	}

	public void setOverrideApprover(String overrideApprover) {
		OverrideApprover = overrideApprover;
	}

	@XmlElement(name="DealAmount")
	public BigDecimal getDealAmount() {
		return DealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		DealAmount = dealAmount;
	}

	@XmlElement(name="DealCcy")
	public String getDealCcy() {
		return DealCcy;
	}

	public void setDealCcy(String dealCcy) {
		DealCcy = dealCcy;
	}

	@XmlElement(name="DealExpiry")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getDealExpiry() {
		return DealExpiry;
	}

	public void setDealExpiry(Date dealExpiry) {
		DealExpiry = dealExpiry;
	}

	@XmlElement(name="DraweeID")
	public String getDraweeID() {
		return draweeID;
	}

	public void setDraweeID(String draweeID) {
		this.draweeID = draweeID;
	}

	@XmlElement(name="BrokerID")
	public String getBrokerID() {
		return brokerID;
	}

	public void setBrokerID(String brokerID) {
		this.brokerID = brokerID;
	}

	@XmlElement(name="PrevDealID")
	public String getPrevDealID() {
		return PrevDealID;
	}

	public void setPrevDealID(String prevDealID) {
		PrevDealID = prevDealID;
	}

	@XmlElement(name="MTM")
	public BigDecimal getMTM() {
		return MTM;
	}

	public void setMTM(BigDecimal mTM) {
		MTM = mTM;
	}

	@XmlElement(name="Tenor")
	public int getTenor() {
		return Tenor;
	}

	public void setTenor(int tenor) {
		Tenor = tenor;
	}

	@XmlElement(name="EffProfitRate")
	public BigDecimal getEffProfitRate() {
		return EffProfitRate;
	}

	public void setEffProfitRate(BigDecimal effProfitRate) {
		EffProfitRate = effProfitRate;
	}

	@XmlElement(name="OutstandingAmount")
	public BigDecimal getOutstandingAmount() {
		return OutstandingAmount;
	}

	public void setOutstandingAmount(BigDecimal outstandingAmount) {
		OutstandingAmount = outstandingAmount;
	}

	@XmlElement(name="PastDueAmount")
	public BigDecimal getPastDueAmount() {
		return PastDueAmount;
	}

	public void setPastDueAmount(BigDecimal pastDueAmount) {
		PastDueAmount = pastDueAmount;
	}

	@XmlElement(name="PastDueDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getPastDueDate() {
		return pastDueDate;
	}

	public void setPastDueDate(Date pastDueDate) {
		this.pastDueDate = pastDueDate;
	}

	@XmlElement(name="AmountBuy")
	public BigDecimal getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(BigDecimal amountBuy) {
		this.amountBuy = amountBuy;
	}

	@XmlElement(name="CurrencyBuy")
	public String getCurrencyBuy() {
		return currencyBuy;
	}

	public void setCurrencyBuy(String currencyBuy) {
		this.currencyBuy = currencyBuy;
	}

	@XmlElement(name="AmountSell")
	public BigDecimal getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(BigDecimal amountSell) {
		this.amountSell = amountSell;
	}

	@XmlElement(name="CurrencySell")
	public String getCurrencySell() {
		return currencySell;
	}

	public void setCurrencySell(String currencySell) {
		this.currencySell = currencySell;
	}

	@XmlElement(name="ValueDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	@XmlElement(name="MarketCcy")
	public String getMarketCcy() {
		return MarketCcy;
	}

	public void setMarketCcy(String marketCcy) {
		MarketCcy = marketCcy;
	}

	@XmlElement(name="MarketPrice")
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	@XmlElement(name="ExceededDealRef")
	public String getExceededDealRef() {
		return ExceededDealRef;
	}

	public void setExceededDealRef(String exceededDealRef) {
		ExceededDealRef = exceededDealRef;
	}

	@XmlElement(name="isBlocked")
	public String getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(String isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlElement(name="BookCcy")
	public String getBookCcy() {
		return bookCcy;
	}

	public void setBookCcy(String bookCcy) {
		this.bookCcy = bookCcy;
	}
	@XmlElement(name="BookPrice")
	public BigDecimal getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(BigDecimal bookPrice) {
		this.bookPrice = bookPrice;
	}
	@XmlElement(name="ProfRate")
	public BigDecimal getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}
			
	 private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
	        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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

