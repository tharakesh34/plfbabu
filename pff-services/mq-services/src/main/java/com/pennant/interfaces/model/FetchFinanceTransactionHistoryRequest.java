package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "FetchFinanceTransactionHistoryRequest")
public class FetchFinanceTransactionHistoryRequest {

	private String referenceNum;
	private String financeRef;
	private Date transactionFromDate;
	private Date transactionToDate;
	private BigDecimal transactionFromAmount;
	private BigDecimal transactionToAmount;
	private Timestamp timestamp;

	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "FinanceRef")
	public String getFinanceRef() {
		return financeRef;
	}

	public void setFinanceRef(String financeRef) {
		this.financeRef = financeRef;
	}

	@XmlElement(name = "TransactionFromDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getTransactionFromDate() {
		return transactionFromDate;
	}

	public void setTransactionFromDate(Date transactionFromDate) {
		this.transactionFromDate = transactionFromDate;
	}

	@XmlElement(name = "TransactionToDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getTransactionToDate() {
		return transactionToDate;
	}

	public void setTransactionToDate(Date transactionToDate) {
		this.transactionToDate = transactionToDate;
	}

	@XmlElement(name = "TransactionFromAmount")
	public BigDecimal getTransactionFromAmount() {
		return transactionFromAmount;
	}

	public void setTransactionFromAmount(BigDecimal transactionFromAmount) {
		this.transactionFromAmount = transactionFromAmount;
	}

	@XmlElement(name = "TransactionToAmount")
	public BigDecimal getTransactionToAmount() {
		return transactionToAmount;
	}
	
	public void setTransactionToAmount(BigDecimal transactionToAmount) {
		this.transactionToAmount = transactionToAmount;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@XmlElement(name = "Timestamp")
	public Timestamp getTimestamp() {
		return this.timestamp;
	}
	
	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
