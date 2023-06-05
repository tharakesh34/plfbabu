package com.pennant.backend.model.loanenquiryresponse;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicant.ApplicantDetails;
import com.pennant.backend.model.chargedetails.ChargeDetails;
import com.pennant.backend.model.customerdata.CustomerData;
import com.pennant.backend.model.loandetail.LoanDetail;
import com.pennant.backend.model.paymentmode.PaymentMode;

@XmlRootElement(name = "loan")
@XmlAccessorType(XmlAccessType.NONE)
public class LoanEnquiryResponse {

	@XmlElement
	private LoanDetail loanDetail;
	@XmlElement
	private List<LoanDetail> loanDetails;
	@XmlElement
	private CustomerData customerData;
	@XmlElement
	private List<CustomerData> CustomerDataList;
	@XmlElement
	List<ApplicantDetails> applicantDetails;
	@XmlElement
	List<PaymentMode> paymentModes;
	@XmlElement
	List<ChargeDetails> chargeDetails;
	@XmlElement
	private WSReturnStatus returnStatus;

	public LoanEnquiryResponse() {
		super();
	}

	public LoanDetail getLoanDetail() {
		return loanDetail;
	}

	public void setLoanDetail(LoanDetail loanDetail) {
		this.loanDetail = loanDetail;
	}

	public List<LoanDetail> getLoanDetails() {
		return loanDetails;
	}

	public void setLoanDetails(List<LoanDetail> loanDetails) {
		this.loanDetails = loanDetails;
	}

	public CustomerData getCustomerData() {
		return customerData;
	}

	public void setCustomerData(CustomerData customerData) {
		this.customerData = customerData;
	}

	public List<CustomerData> getCustomerDataList() {
		return CustomerDataList;
	}

	public void setCustomerDataList(List<CustomerData> customerDataList) {
		CustomerDataList = customerDataList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<ApplicantDetails> getApplicantDetails() {
		return applicantDetails;
	}

	public void setApplicantDetails(List<ApplicantDetails> applicantDetails) {
		this.applicantDetails = applicantDetails;
	}

	public List<PaymentMode> getPaymentModes() {
		return paymentModes;
	}

	public void setPaymentModes(List<PaymentMode> paymentModes) {
		this.paymentModes = paymentModes;
	}

	public List<ChargeDetails> getChargeDetails() {
		return chargeDetails;
	}

	public void setChargeDetails(List<ChargeDetails> chargeDetails) {
		this.chargeDetails = chargeDetails;
	}
}
