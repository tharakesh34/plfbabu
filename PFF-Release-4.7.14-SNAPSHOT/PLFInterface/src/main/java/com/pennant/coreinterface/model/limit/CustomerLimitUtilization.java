package com.pennant.coreinterface.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

@XmlRootElement(name = "CustomerLimitUtilization")
public class CustomerLimitUtilization implements Serializable {

	private static final long serialVersionUID = -2913568360032482170L;

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
	private String brokerID;
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
	private long timeStamp;

	// response fields
	private List<String> overrides;
	private String msgBreach;
	private String response;
	private String errMsg;
	private String returnCode;
	private String returnText;

	public CustomerLimitUtilization() {
		super();
	}

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "DealID")
	public String getDealID() {
		return dealID;
	}

	public void setDealID(String dealID) {
		this.dealID = dealID;
	}

	@XmlElement(name = "DealType")
	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	@XmlElement(name = "CustomerReference")
	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	@XmlElement(name = "LimitRef")
	public String getLimitRef() {
		return limitRef;
	}

	public void setLimitRef(String limitRef) {
		this.limitRef = limitRef;
	}

	@XmlElement(name = "UserID")
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@XmlElement(name = "OverrideAuthorizerID")
	public String getOverrideAuthorizerID() {
		return overrideAuthorizerID;
	}

	public void setOverrideAuthorizerID(String overrideAuthorizerID) {
		this.overrideAuthorizerID = overrideAuthorizerID;
	}

	@XmlElement(name = "DealAmount")
	public BigDecimal getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}

	@XmlElement(name = "DealCcy")
	public String getDealCcy() {
		return dealCcy;
	}

	public void setDealCcy(String dealCcy) {
		this.dealCcy = dealCcy;
	}

	@XmlElement(name = "DealExpiry")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getDealExpiry() {
		return dealExpiry;
	}

	public void setDealExpiry(Date dealExpiry) {
		this.dealExpiry = dealExpiry;
	}

	@XmlElement(name = "DraweeID")
	public String getDraweeID() {
		return draweeID;
	}

	public void setDraweeID(String draweeID) {
		this.draweeID = draweeID;
	}

	@XmlElement(name = "BrokerID")
	public String getBrokerID() {
		return brokerID;
	}

	public void setBrokerID(String brokerID) {
		this.brokerID = brokerID;
	}

	@XmlElement(name = "PrevDealID")
	public String getPrevDealID() {
		return prevDealID;
	}

	public void setPrevDealID(String prevDealID) {
		this.prevDealID = prevDealID;
	}

	@XmlElement(name = "MTM")
	public BigDecimal getMtm() {
		return mtm;
	}

	public void setMtm(BigDecimal mtm) {
		this.mtm = mtm;
	}

	@XmlElement(name = "Tenor")
	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	@XmlElement(name = "EffProfitRate")
	public BigDecimal getEffProfitRate() {
		return effProfitRate;
	}

	public void setEffProfitRate(BigDecimal effProfitRate) {
		this.effProfitRate = effProfitRate;
	}

	@XmlElement(name = "OS_Amount")
	public BigDecimal getOs_Amount() {
		return os_Amount;
	}

	public void setOs_Amount(BigDecimal osAmount) {
		this.os_Amount = osAmount;
	}

	@XmlElement(name = "PastDueAmount")
	public BigDecimal getPastDueAmount() {
		return pastDueAmount;
	}

	public void setPastDueAmount(BigDecimal pastDueAmount) {
		this.pastDueAmount = pastDueAmount;
	}

	@XmlElement(name = "PastDueDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getPastDueDate() {
		return pastDueDate;
	}

	public void setPastDueDate(Date pastDueDate) {
		this.pastDueDate = pastDueDate;
	}

	@XmlElement(name = "AmBuy")
	public BigDecimal getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(BigDecimal amountBuy) {
		this.amountBuy = amountBuy;
	}

	@XmlElement(name = "CcyBuy")
	public String getCurrencyBuy() {
		return currencyBuy;
	}

	public void setCurrencyBuy(String currencyBuy) {
		this.currencyBuy = currencyBuy;
	}

	@XmlElement(name = "AmSell")
	public BigDecimal getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(BigDecimal amountSell) {
		this.amountSell = amountSell;
	}

	@XmlElement(name = "CCySell")
	public String getCurrencySell() {
		return currencySell;
	}

	public void setCurrencySell(String currencySell) {
		this.currencySell = currencySell;
	}

	@XmlElement(name = "ValueDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	@XmlElement(name = "MarketCcy")
	public String getMarketCcy() {
		return marketCcy;
	}

	public void setMarketCcy(String marketCcy) {
		this.marketCcy = marketCcy;
	}

	@XmlElement(name = "MarketPrice")
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	@XmlElement(name = "ExceededDealRef")
	public String getExceededDealRef() {
		return exceededDealRef;
	}

	public void setExceededDealRef(String exceededDealRef) {
		this.exceededDealRef = exceededDealRef;
	}

	@XmlElement(name = "IsBlocked")
	public String getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(String isBlocked) {
		this.isBlocked = isBlocked;
	}

	@XmlElement(name = "BookCcy")
	public String getBookCcy() {
		return bookCcy;
	}

	public void setBookCcy(String bookCcy) {
		this.bookCcy = bookCcy;
	}

	@XmlElement(name = "BookPrice")
	public BigDecimal getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(BigDecimal bookPrice) {
		this.bookPrice = bookPrice;
	}

	@XmlElement(name = "ProfitRate")
	public BigDecimal getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@XmlElement(name = "BranchCode")
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

	@XmlElement(name = "MsgBreach")
	public String getMsgBreach() {
		return msgBreach;
	}

	public void setMsgBreach(String msgBreach) {
		this.msgBreach = msgBreach;
	}
	
	@XmlElement(name = "Response")
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@XmlElement(name = "ErrMsg")
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnText")
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
	
	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(
				InterfaceMasterConfigUtil.SHORT_DATE);

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
