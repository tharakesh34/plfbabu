package com.pennant.coreinterface.model.deposits;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

@XmlRootElement(name = "GetInvestmentAccountDetailsReply")
public class FetchDepositDetail implements Serializable {

	private static final long serialVersionUID = 4396116722765788718L;
	
	public FetchDepositDetail() {
		
	}
	
	private String referenceNum;
	private String invstContractNo;
	private String returnCode;
	private String returnText;
	private String custCIF;
	private String branchCode;
	private String currencyCode;
	private BigDecimal invstAmount = BigDecimal.ZERO;
	private Date openDate;
	private Date maturityDate;
	private String accountType;
	private String accountName;
	private int depositTenor;
	private Date finalMaturityDate;
	private BigDecimal autoRollOverTenor = BigDecimal.ZERO;
	private BigDecimal totalReceivedProfit = BigDecimal.ZERO;
	private BigDecimal profitRate = BigDecimal.ZERO;
	private String principleLiquidAccount;
	private String pftLiquidationAccount;
	private BigDecimal lienBalance = BigDecimal.ZERO;
	private String lienDesc;
	private String status;
	private long timeStamp;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "InvestmentContractNumber")
	public String getInvstContractNo() {
		return invstContractNo;
	}

	public void setInvstContractNo(String invstContractNo) {
		this.invstContractNo = invstContractNo;
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

	@XmlElement(name = "CustomerNumber")
	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	@XmlElement(name = "BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@XmlElement(name = "CurrencyCode")
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@XmlElement(name = "InvestmentAmount")
	public BigDecimal getInvstAmount() {
		return invstAmount;
	}

	public void setInvstAmount(BigDecimal invstAmount) {
		this.invstAmount = invstAmount;
	}

	@XmlElement(name = "OpenDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	@XmlElement(name = "MaturityDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	@XmlElement(name = "AccountType")
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@XmlElement(name = "AccountName")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@XmlElement(name = "DepositTenor")
	public int getDepositTenor() {
		return depositTenor;
	}

	public void setDepositTenor(int depositTenor) {
		this.depositTenor = depositTenor;
	}

	@XmlElement(name = "FinalMaturityDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getFinalMaturityDate() {
		return finalMaturityDate;
	}

	public void setFinalMaturityDate(Date finalMaturityDate) {
		this.finalMaturityDate = finalMaturityDate;
	}

	@XmlElement(name = "AutoRollOverTenor")
	public BigDecimal getAutoRollOverTenor() {
		return autoRollOverTenor;
	}

	public void setAutoRollOverTenor(BigDecimal autoRollOverTenor) {
		this.autoRollOverTenor = autoRollOverTenor;
	}

	@XmlElement(name = "TotalReceivedProfit")
	public BigDecimal getTotalReceivedProfit() {
		return totalReceivedProfit;
	}

	public void setTotalReceivedProfit(BigDecimal totalReceivedProfit) {
		this.totalReceivedProfit = totalReceivedProfit;
	}

	@XmlElement(name = "ProfitRate")
	public BigDecimal getProfitRate() {
		return profitRate;
	}

	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}

	@XmlElement(name = "PrincipleLiquidationAccount")
	public String getPrincipleLiquidAccount() {
		return principleLiquidAccount;
	}

	public void setPrincipleLiquidAccount(String principleLiquidAccount) {
		this.principleLiquidAccount = principleLiquidAccount;
	}

	@XmlElement(name = "ProfitLiquidationAccount")
	public String getPftLiquidationAccount() {
		return pftLiquidationAccount;
	}

	public void setPftLiquidationAccount(String pftLiquidationAccount) {
		this.pftLiquidationAccount = pftLiquidationAccount;
	}

	@XmlElement(name = "LienBalance")
	public BigDecimal getLienBalance() {
		return lienBalance;
	}

	public void setLienBalance(BigDecimal lienBalance) {
		this.lienBalance = lienBalance;
	}

	@XmlElement(name = "LienDescription")
	public String getLienDesc() {
		return lienDesc;
	}

	public void setLienDesc(String lienDesc) {
		this.lienDesc = lienDesc;
	}

	@XmlElement(name = "Status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(InterfaceMasterConfigUtil.MQDATE);

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
