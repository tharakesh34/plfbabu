/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerDetails.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified Date
 * : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.customermasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custCIF", "custCoreBank", "custCtgCode", "custDftBranch", "custBaseCcy",
		"primaryRelationOfficer", "customer", "employmentDetailsList", "addressList", "customerPhoneNumList",
		"customerEMailList", "customerIncomeList", "customerDocumentsList", "customerBankInfoList",
		"customerChequeInfoList", "customerExtLiabilityList", "dedupReq", "extendedDetails", "balckListCustomers",
		"blackListReq", "customerDirectorList", "customerDedupList", "customerGstLists", "custCardSales",
		"customerFinanceDetailList", "returnStatus", "gstDetailsList" })
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerDetails implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private long custID;
	private boolean newRecord = false;

	@XmlElement(name = "cif")
	private String custCIF;

	@XmlElement(name = "coreBankID")
	private String custCoreBank;

	@XmlElement(name = "categoryCode")
	private String custCtgCode;

	@XmlElement(name = "defaultBranch")
	private String custDftBranch;

	@XmlElement(name = "baseCurrency")
	private String custBaseCcy;

	@XmlElement
	private Long primaryRelationOfficer = (long) 0;

	private String sourceId;

	@XmlElement(name = "personalInfo")
	private Customer customer;

	private CustEmployeeDetail custEmployeeDetail;
	private CustomerDocument customerDocument;

	// @XmlElement(name = "extendedDetail")
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;

	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails = null;

	private List<CustomerRating> ratingsList;

	@XmlElementWrapper(name = "employments")
	@XmlElement(name = "employment")
	private List<CustomerEmploymentDetail> employmentDetailsList;

	@XmlElementWrapper(name = "documents")
	@XmlElement(name = "document")
	private List<CustomerDocument> customerDocumentsList;

	@XmlElementWrapper(name = "addresses")
	@XmlElement(name = "address")
	private List<CustomerAddres> addressList = new ArrayList<>();

	@XmlElementWrapper(name = "customerGstLists")
	@XmlElement(name = "customerGstList")
	private List<CustomerGST> customerGstList = new ArrayList<>();
	@XmlElementWrapper(name = "phones")
	@XmlElement(name = "phone")
	private List<CustomerPhoneNumber> customerPhoneNumList;

	@XmlElementWrapper(name = "gstDetails")
	@XmlElement(name = "gstDetail")
	private List<GSTDetail> gstDetailsList;

	@XmlElementWrapper(name = "emails")
	@XmlElement(name = "email")
	private List<CustomerEMail> customerEMailList;

	@XmlElementWrapper(name = "customersIncome")
	@XmlElement(name = "customerIncome")
	private List<CustomerIncome> customerIncomeList;

	@XmlElementWrapper(name = "customerDirector")
	@XmlElement(name = "customerDirector")
	private List<DirectorDetail> customerDirectorList;

	@XmlElementWrapper(name = "customersBankInfo")
	@XmlElement(name = "customerBankInfo")
	private List<CustomerBankInfo> customerBankInfoList;

	@XmlElementWrapper(name = "accountsBehaviour")
	@XmlElement(name = "accountBehaviour")
	private List<CustomerChequeInfo> customerChequeInfoList;

	@XmlElementWrapper(name = "customersExtLiability")
	@XmlElement(name = "customerExtLiability")
	private List<CustomerExtLiability> customerExtLiabilityList;
	private List<FinanceEnquiry> custFinanceExposureList;
	@XmlElementWrapper(name = "dedup")
	@XmlElement(name = "dedup")
	private List<CustomerDedup> customerDedupList;
	@XmlElementWrapper(name = "balckListCustomers")
	@XmlElement(name = "balckListCustomers")
	private List<BlackListCustomers> balckListCustomers;
	private CoreCustomer coreCustomer;
	private Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
	private CustomerDedup custDedup;
	private String coreReferenceNum;

	private List<FinanceEnquiry> customerFinances = new ArrayList<>();
	private FinanceEnquiry customerFinance;
	private List<FinanceMain> financeMainList;
	private List<VASRecording> vasRecordingList = new ArrayList<>();
	private List<CollateralSetup> collateraldetailList;
	@XmlElementWrapper(name = "custCardSales")
	@XmlElement(name = "custCardSales")
	private List<CustCardSales> custCardSales;

	@XmlElement
	public WSReturnStatus returnStatus = null;
	@XmlElement
	private Boolean dedupReq = false;
	@XmlElement
	private Boolean blackListReq = false;

	private boolean cibilExecuted = false;
	private boolean cibilALreadyRun = false;

	// used for Interfaces
	private Long usrID;
	private String usrLogin;
	private String matches;
	@XmlElement
	private List<CustomerFinanceDetail> customerFinanceDetailList;

	private boolean reInitiateCibil = true;
	private String actualError;
	private String phone1;
	private String phone2;
	private String mobile1;
	private String mobile2;

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public String getCustCoreBank() {
		return custCoreBank;
	}

	public void setCustCoreBank(String custCoreBank) {
		this.custCoreBank = custCoreBank;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}

	public String getCustDftBranch() {
		return custDftBranch;
	}

	public void setCustDftBranch(String custDftBranch) {
		this.custDftBranch = custDftBranch;
	}

	public String getCustBaseCcy() {
		return custBaseCcy;
	}

	public void setCustBaseCcy(String custBaseCcy) {
		this.custBaseCcy = custBaseCcy;
	}

	public Long getPrimaryRelationOfficer() {
		return primaryRelationOfficer;
	}

	public void setPrimaryRelationOfficer(Long primaryRelationOfficer) {
		this.primaryRelationOfficer = primaryRelationOfficer;
	}

	public List<CustomerGST> getCustomerGstList() {
		return customerGstList;
	}

	public void setCustomerGstList(List<CustomerGST> customerGstList) {
		this.customerGstList = customerGstList;
	}

	private CustomerDetails befImage;
	private LoggedInUser userDetails;

	public CustomerDetails() {
		super();
		this.customer = new Customer();
		this.custDedup = new CustomerDedup();
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustEmployeeDetail getCustEmployeeDetail() {
		return custEmployeeDetail;
	}

	public void setCustEmployeeDetail(CustEmployeeDetail custEmployeeDetail) {
		this.custEmployeeDetail = custEmployeeDetail;
	}

	public List<CustomerRating> getRatingsList() {
		return ratingsList;
	}

	public void setRatingsList(List<CustomerRating> ratingsList) {
		this.ratingsList = ratingsList;
	}

	public List<CustomerAddres> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<CustomerAddres> addressList) {
		this.addressList = addressList;
	}

	public List<CustomerIncome> getCustomerIncomeList() {
		return customerIncomeList;
	}

	public void setCustomerIncomeList(List<CustomerIncome> customerIncomeList) {
		this.customerIncomeList = customerIncomeList;
	}

	public CustomerDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(CustomerDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setCustomerPhoneNumList(List<CustomerPhoneNumber> customerPhoneNumList) {
		this.customerPhoneNumList = customerPhoneNumList;
	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumList() {
		return customerPhoneNumList;
	}

	public void setCustomerEMailList(List<CustomerEMail> customerEMailList) {
		this.customerEMailList = customerEMailList;
	}

	public List<CustomerEMail> getCustomerEMailList() {
		return customerEMailList;
	}

	public void setCustomerDocumentsList(List<CustomerDocument> customerDocumentsList) {
		this.customerDocumentsList = customerDocumentsList;
	}

	public List<CustomerDocument> getCustomerDocumentsList() {
		return customerDocumentsList;
	}

	public Map<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(Map<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public CustomerDedup getCustDedup() {
		CustomerPhoneNumber custPhoneNumber = null;
		CustomerAddres custAddress = null;
		CustomerEMail custEmail = null;
		custDedup = new CustomerDedup();

		if (this.customer != null && this.customer.getCustCIF() != null) {
			custDedup.setCustId(this.customer.getCustID());
			custDedup.setCustCIF(this.customer.getCustCIF() != null ? this.customer.getCustCIF() : "");
			custDedup.setCustCoreBank(this.customer.getCustCoreBank() != null ? this.customer.getCustCoreBank() : "");
			custDedup.setCustDOB(this.customer.getCustDOB());
			custDedup.setCustShrtName(this.customer.getCustShrtName());
			custDedup.setCustNationality(
					this.customer.getCustNationality() != null ? this.customer.getCustNationality() : "");
			custDedup.setCustCtgCode(StringUtils.trimToEmpty(this.customer.getCustCtgCode()));
			custDedup.setCustDftBranch(StringUtils.trimToEmpty(this.customer.getCustDftBranch()));
			custDedup.setCustSector(StringUtils.trimToEmpty(this.customer.getCustSector()));
			custDedup.setCustSubSector(StringUtils.trimToEmpty(this.customer.getCustSubSector()));
		}

		// Customer Phone Number
		List<CustomerPhoneNumber> custPhoneNoList = this.customerPhoneNumList;
		if (custPhoneNoList != null && !custPhoneNoList.isEmpty()) {
			for (int i = 0; i < custPhoneNoList.size(); i++) {
				custPhoneNumber = custPhoneNoList.get(i);
				custDedup.setPhoneNumber(custPhoneNumber.getPhoneNumber());
			}
		}

		// Customer Address Number
		List<CustomerAddres> custAddressList = this.addressList;
		if (custAddressList != null && !custAddressList.isEmpty()) {
			for (int i = 0; i < custAddressList.size(); i++) {
				custAddress = custAddressList.get(i);
				custDedup.setPhoneNumber(custAddress.getCustAddrPhone());
			}
		}

		// Customer Email Details
		List<CustomerEMail> customerEmails = this.customerEMailList;
		if (customerEmails != null && !customerEmails.isEmpty()) {
			for (int i = 0; i < customerEmails.size(); i++) {
				custEmail = customerEmails.get(i);
				custDedup.setCustEMail(custEmail.getCustEMail());
			}
		}
		return custDedup;
	}

	public void setCustDedup(CustomerDedup custDedup) {
		this.custDedup = custDedup;
	}

	public void setEmploymentDetailsList(List<CustomerEmploymentDetail> employmentDetailsList) {
		this.employmentDetailsList = employmentDetailsList;
	}

	public List<CustomerEmploymentDetail> getEmploymentDetailsList() {
		return employmentDetailsList;
	}

	public List<DirectorDetail> getCustomerDirectorList() {
		return customerDirectorList;
	}

	public void setCustomerDirectorList(List<DirectorDetail> customerDirectorList) {
		this.customerDirectorList = customerDirectorList;
	}

	public List<CustomerBankInfo> getCustomerBankInfoList() {
		return customerBankInfoList;
	}

	public void setCustomerBankInfoList(List<CustomerBankInfo> customerBankInfoList) {
		this.customerBankInfoList = customerBankInfoList;
	}

	public List<CustomerChequeInfo> getCustomerChequeInfoList() {
		return customerChequeInfoList;
	}

	public void setCustomerChequeInfoList(List<CustomerChequeInfo> customerChequeInfoList) {
		this.customerChequeInfoList = customerChequeInfoList;
	}

	public List<CustomerExtLiability> getCustomerExtLiabilityList() {
		return customerExtLiabilityList;
	}

	public void setCustomerExtLiabilityList(List<CustomerExtLiability> customerExtLiabilityList) {
		this.customerExtLiabilityList = customerExtLiabilityList;
	}

	public List<FinanceEnquiry> getCustFinanceExposureList() {
		return custFinanceExposureList;
	}

	public void setCustFinanceExposureList(List<FinanceEnquiry> custFinanceExposureList) {
		this.custFinanceExposureList = custFinanceExposureList;
	}

	public List<CustomerDedup> getCustomerDedupList() {
		return customerDedupList;
	}

	public void setCustomerDedupList(List<CustomerDedup> customerDedupList) {
		this.customerDedupList = customerDedupList;
	}

	public String getCoreReferenceNum() {
		return coreReferenceNum;
	}

	public void setCoreReferenceNum(String coreReferenceNum) {
		this.coreReferenceNum = coreReferenceNum;
	}

	public CoreCustomer getCoreCustomer() {
		return coreCustomer;
	}

	public void setCoreCustomer(CoreCustomer coreCustomer) {
		this.coreCustomer = coreCustomer;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public List<FinanceEnquiry> getCustomerFinances() {
		return customerFinances;
	}

	public void setCustomerFinances(List<FinanceEnquiry> customerFinances) {
		this.customerFinances = customerFinances;
	}

	/**
	 * @return the customerFinance
	 */
	public FinanceEnquiry getCustomerFinance() {
		return customerFinance;
	}

	/**
	 * @param customerFinance the customerFinance to set
	 */
	public void setCustomerFinance(FinanceEnquiry customerFinance) {
		this.customerFinance = customerFinance;
	}

	public Boolean isDedupReq() {
		return dedupReq;
	}

	public void setDedupReq(Boolean dedupReq) {
		this.dedupReq = dedupReq;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	/**
	 * @return the extendedFieldRender
	 */
	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	/**
	 * @param extendedFieldRender the extendedFieldRender to set
	 */
	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public List<FinanceMain> getFinanceMainList() {
		return financeMainList;
	}

	public void setFinanceMainList(List<FinanceMain> financeMainList) {
		this.financeMainList = financeMainList;
	}

	public List<VASRecording> getVasRecordingList() {
		return vasRecordingList;
	}

	public void setVasRecordingList(List<VASRecording> vasRecordingList) {
		this.vasRecordingList = vasRecordingList;
	}

	public List<CollateralSetup> getCollateraldetailList() {
		return collateraldetailList;
	}

	public void setCollateraldetailList(List<CollateralSetup> collateraldetailList) {
		this.collateraldetailList = collateraldetailList;
	}

	public CustomerDocument getCustomerDocument() {
		return customerDocument;
	}

	public void setCustomerDocument(CustomerDocument customerDocument) {
		this.customerDocument = customerDocument;
	}

	public boolean isCibilALreadyRun() {
		return cibilALreadyRun;
	}

	public void setCibilALreadyRun(boolean cibilALreadyRun) {
		this.cibilALreadyRun = cibilALreadyRun;
	}

	public boolean isCibilExecuted() {
		return cibilExecuted;
	}

	public void setCibilExecuted(boolean cibilExecuted) {
		this.cibilExecuted = cibilExecuted;
	}

	public List<BlackListCustomers> getBalckListCustomers() {
		return balckListCustomers;
	}

	public void setBalckListCustomers(List<BlackListCustomers> balckListCustomers) {
		this.balckListCustomers = balckListCustomers;
	}

	public Boolean isBlackListReq() {
		return blackListReq;
	}

	public void setBlackListReq(Boolean blackListReq) {
		this.blackListReq = blackListReq;
	}

	public List<CustCardSales> getCustCardSales() {
		return custCardSales;
	}

	public void setCustCardSales(List<CustCardSales> custCardSales) {
		this.custCardSales = custCardSales;
	}

	public Long getUsrID() {
		return usrID;
	}

	public void setUsrID(Long usrID) {
		this.usrID = usrID;
	}

	public String getUsrLogin() {
		return usrLogin;
	}

	public void setUsrLogin(String usrLogin) {
		this.usrLogin = usrLogin;
	}

	public String getMatches() {
		return matches;
	}

	public void setMatches(String matches) {
		this.matches = matches;
	}

	public List<CustomerFinanceDetail> getCustomerFinanceDetailList() {
		return customerFinanceDetailList;
	}

	public void setCustomerFinanceDetailList(List<CustomerFinanceDetail> customerFinanceDetailList) {
		this.customerFinanceDetailList = customerFinanceDetailList;
	}

	public boolean isReInitiateCibil() {
		return reInitiateCibil;
	}

	public void setReInitiateCibil(boolean reInitiateCibil) {
		this.reInitiateCibil = reInitiateCibil;
	}

	public String getActualError() {
		return actualError;
	}

	public void setActualError(String actualError) {
		this.actualError = actualError;
	}

	public List<GSTDetail> getGstDetailsList() {
		return gstDetailsList;
	}

	public void setGstDetailsList(List<GSTDetail> gstDetailsList) {
		this.gstDetailsList = gstDetailsList;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
}
