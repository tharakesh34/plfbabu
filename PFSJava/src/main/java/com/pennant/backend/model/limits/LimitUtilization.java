package com.pennant.backend.model.limits;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class LimitUtilization implements Serializable {
	private static final long serialVersionUID = 7352614868843101891L;

	// request fields
	private String referenceNum;
	private String dealID;
	private String dealType;
	private String customerReference;
	private String limitRef;
	private String userID;
	private String overrideAuthorizerID;
	private BigDecimal dealAmount = BigDecimal.ZERO;
	private String dealCcy;
	private Date dealExpiry;
	private String draweeID;
	private String prevDealID;
	private BigDecimal mtm = BigDecimal.ZERO;
	private int tenor;
	private BigDecimal profitRate;
	private BigDecimal effProfitRate = BigDecimal.ZERO;
	private BigDecimal os_Amount = BigDecimal.ZERO;
	private BigDecimal pastDueAmount = BigDecimal.ZERO;
	private Date pastDueDate;
	private BigDecimal amountBuy = BigDecimal.ZERO;
	private String currencyBuy;
	private BigDecimal amountSell = BigDecimal.ZERO;
	private String currencySell;
	private Date valueDate;
	private String bookCcy;
	private BigDecimal bookPrice;
	private String marketCcy;
	private BigDecimal marketPrice = BigDecimal.ZERO;
	private String exceededDealRef;
	private String isBlocked;
	private String branchCode;
	private String requestType;
	private String timeStamp;

	// response fields
	private List<String> overrides;
	private String msgBreach;
	private String response;
	private String errMsg;
	private String returnCode;
	private String returnText;

	private boolean newRecord = true;

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public LimitUtilization() {
	    super();
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getOverrideAuthorizerID() {
		return overrideAuthorizerID;
	}

	public void setOverrideAuthorizerID(String overrideAuthorizerID) {
		this.overrideAuthorizerID = overrideAuthorizerID;
	}

	public BigDecimal getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}

	public String getDealCcy() {
		return dealCcy;
	}

	public void setDealCcy(String dealCcy) {
		this.dealCcy = dealCcy;
	}

	public Date getDealExpiry() {
		return dealExpiry;
	}

	public void setDealExpiry(Date dealExpiry) {
		this.dealExpiry = dealExpiry;
	}

	public String getDraweeID() {
		return draweeID;
	}

	public void setDraweeID(String draweeID) {
		this.draweeID = draweeID;
	}

	public String getPrevDealID() {
		return prevDealID;
	}

	public void setPrevDealID(String prevDealID) {
		this.prevDealID = prevDealID;
	}

	public BigDecimal getMtm() {
		return mtm;
	}

	public void setMtm(BigDecimal mtm) {
		this.mtm = mtm;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public BigDecimal getEffProfitRate() {
		return effProfitRate;
	}

	public void setEffProfitRate(BigDecimal effProfitRate) {
		this.effProfitRate = effProfitRate;
	}

	public BigDecimal getOs_Amount() {
		return os_Amount;
	}

	public void setOs_Amount(BigDecimal osAmount) {
		this.os_Amount = osAmount;
	}

	public BigDecimal getPastDueAmount() {
		return pastDueAmount;
	}

	public void setPastDueAmount(BigDecimal pastDueAmount) {
		this.pastDueAmount = pastDueAmount;
	}

	public Date getPastDueDate() {
		return pastDueDate;
	}

	public void setPastDueDate(Date pastDueDate) {
		this.pastDueDate = pastDueDate;
	}

	public BigDecimal getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(BigDecimal amountBuy) {
		this.amountBuy = amountBuy;
	}

	public String getCurrencyBuy() {
		return currencyBuy;
	}

	public void setCurrencyBuy(String currencyBuy) {
		this.currencyBuy = currencyBuy;
	}

	public BigDecimal getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(BigDecimal amountSell) {
		this.amountSell = amountSell;
	}

	public String getCurrencySell() {
		return currencySell;
	}

	public void setCurrencySell(String currencySell) {
		this.currencySell = currencySell;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getMarketCcy() {
		return marketCcy;
	}

	public void setMarketCcy(String marketCcy) {
		this.marketCcy = marketCcy;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getExceededDealRef() {
		return exceededDealRef;
	}

	public void setExceededDealRef(String exceededDealRef) {
		this.exceededDealRef = exceededDealRef;
	}

	public String getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(String isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getBookCcy() {
		return bookCcy;
	}

	public void setBookCcy(String bookCcy) {
		this.bookCcy = bookCcy;
	}

	public BigDecimal getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(BigDecimal bookPrice) {
		this.bookPrice = bookPrice;
	}

	public BigDecimal getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public List<String> getOverrides() {
		return overrides;
	}

	public void setOverrides(List<String> overrides) {
		this.overrides = overrides;
	}

	public String getMsgBreach() {
		return msgBreach;
	}

	public void setMsgBreach(String msgBreach) {
		this.msgBreach = msgBreach;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

}
