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

@XmlRootElement(name = "InvestmentContract")
public class InvestmentContract implements Serializable {

	private static final long serialVersionUID = 8518146405336823330L;

	public InvestmentContract() {

	}

	private String invstContractNo;
	private String invstHolderName;
	private String accountType;
	private String branchCode;
	private String currencyCode;
	private BigDecimal investmentAmount = BigDecimal.ZERO;
	private Date openDate;
	private Date maturityDate;
	private BigDecimal depositTenor = BigDecimal.ZERO;
	private String categoryID;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	@XmlElement(name = "InvestmentContractNo")
	public String getInvstContractNo() {
		return invstContractNo;
	}

	public void setInvstContractNo(String invstContractNo) {
		this.invstContractNo = invstContractNo;
	}

	@XmlElement(name = "InvestmentHolderName")
	public String getInvstHolderName() {
		return invstHolderName;
	}

	public void setInvstHolderName(String invstHolderName) {
		this.invstHolderName = invstHolderName;
	}

	@XmlElement(name = "AccountType")
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
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
	public BigDecimal getInvestmentAmount() {
		return investmentAmount;
	}

	public void setInvestmentAmount(BigDecimal investmentAmount) {
		this.investmentAmount = investmentAmount;
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

	@XmlElement(name = "DepositTenor")
	public BigDecimal getDepositTenor() {
		return depositTenor;
	}

	public void setDepositTenor(BigDecimal depositTenor) {
		this.depositTenor = depositTenor;
	}

	@XmlElement(name = "CategoryID")
	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
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
