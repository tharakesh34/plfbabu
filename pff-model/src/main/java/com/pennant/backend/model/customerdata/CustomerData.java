package com.pennant.backend.model.customerdata;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.loandetail.LoanDetail;

@XmlType(propOrder = { "custCIF", "custShrtName", "custDOB", "gender", "custCRCPR", "fullName", "custMotherMaiden",
		"custFNameLclLng", "fullAddress", "custAddresses", "custPhoneNumbers", "custEmails", "custEmployments",
		"loanDetail", "loanDetails", "returnStatus" })

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerData {

	@XmlElement
	private String custCIF;
	@XmlElement
	private String custShrtName;
	@XmlElement
	private Date custDOB;
	@XmlElement
	private String gender;
	@XmlElement
	private String custCRCPR;
	@XmlElement
	private String fullName;
	@XmlElement(name = "fatherName")
	private String custMotherMaiden;
	@XmlElement(name = "motherName")
	private String custFNameLclLng;
	@XmlElement
	private String fullAddress;
	@XmlElement
	private List<CustomerAddres> custAddresses;
	@XmlElement
	private List<CustomerPhoneNumber> custPhoneNumbers;
	@XmlElement
	private List<CustomerEMail> custEmails;
	@XmlElement
	private List<CustomerEmploymentDetail> custEmployments;
	@XmlElement(name = "loan")
	private LoanDetail loanDetail;
	@XmlElementWrapper(name = "loans")
	@XmlElement
	private List<LoanDetail> loanDetails;
	@XmlElement
	private WSReturnStatus returnStatus;

	public CustomerData() {
		super();
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustShrtName() {
		return custShrtName;
	}

	public void setCustShrtName(String custShrtName) {
		this.custShrtName = custShrtName;
	}

	public Date getCustDOB() {
		return custDOB;
	}

	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}

	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public List<CustomerAddres> getCustAddresses() {
		return custAddresses;
	}

	public void setCustAddresses(List<CustomerAddres> custAddresses) {
		this.custAddresses = custAddresses;
	}

	public List<CustomerPhoneNumber> getCustPhoneNumbers() {
		return custPhoneNumbers;
	}

	public void setCustPhoneNumbers(List<CustomerPhoneNumber> custPhoneNumbers) {
		this.custPhoneNumbers = custPhoneNumbers;
	}

	public List<CustomerEMail> getCustEmails() {
		return custEmails;
	}

	public void setCustEmails(List<CustomerEMail> custEmails) {
		this.custEmails = custEmails;
	}

	public List<CustomerEmploymentDetail> getCustEmployments() {
		return custEmployments;
	}

	public void setCustEmployments(List<CustomerEmploymentDetail> custEmployments) {
		this.custEmployments = custEmployments;
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

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}

	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}

	public String getCustFNameLclLng() {
		return custFNameLclLng;
	}

	public void setCustFNameLclLng(String custFNameLclLng) {
		this.custFNameLclLng = custFNameLclLng;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
