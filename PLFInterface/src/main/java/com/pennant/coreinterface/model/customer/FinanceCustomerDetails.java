package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

@XmlRootElement(name = "FetchFinanceCustomerDetailReply")
public class FinanceCustomerDetails implements Serializable {

	private static final long serialVersionUID = 8413751533672702122L;

	private String referenceNum;
	private String finReference;
	private String customerType;
	private String customerId;
	private String customerIdNum;
	private String customerName;
	private String mobileNo;
	private String emailId;
	private Date installmentDate;
	private int noOfInstallments;
	private BigDecimal emiAmount = BigDecimal.ZERO;
	private String returnCode;
	private String returnText;
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

	@XmlElement(name = "ISNumber")
	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	@XmlElement(name = "CustomerType")
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	@XmlElement(name = "CustomerID")
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@XmlElement(name = "CustomerIDNum")
	public String getCustomerIdNum() {
		return customerIdNum;
	}

	public void setCustomerIdNum(String customerIdNum) {
		this.customerIdNum = customerIdNum;
	}

	@XmlElement(name = "CustomerName")
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@XmlElement(name = "MobileNo")
	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	@XmlElement(name = "EmailID")
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@XmlElement(name = "InstallmentDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getInstallmentDate() {
		return installmentDate;
	}

	public void setInstallmentDate(Date installmentDate) {
		this.installmentDate = installmentDate;
	}

	@XmlElement(name = "NoOfInstallments")
	public int getNoOfInstallments() {
		return noOfInstallments;
	}

	public void setNoOfInstallments(int noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}

	@XmlElement(name = "EMIAmount")
	public BigDecimal getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(BigDecimal emiAmount) {
		this.emiAmount = emiAmount;
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

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(InterfaceMasterConfigUtil.SHORT_DATE);

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
