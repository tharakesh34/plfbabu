/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerDetails.java                                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.model.customermasters;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;

/**
 * Model class for the <b>Customer table</b>.<br>
 * 
 */
@XmlType(propOrder = { "custCIF", "custCoreBank", "custCtgCode", "custDftBranch", "custBaseCcy",
		"primaryRelationOfficer", "customer", "employmentDetailsList", "addressList", "customerPhoneNumList",
		"customerEMailList", "customerIncomeList", "customerDocumentsList", "customerBankInfoList",
		"customerChequeInfoList", "customerExtLiabilityList","dedupReq","returnStatus","customerDedupList" })
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
	private String primaryRelationOfficer;

	private String sourceId;

	@XmlElement(name = "personalInfo")
	private Customer customer;

	private CustEmployeeDetail custEmployeeDetail;

	private List<CustomerRating> ratingsList;

	@XmlElementWrapper(name = "employments")
	@XmlElement(name = "employment")
	private List<CustomerEmploymentDetail> employmentDetailsList;

	@XmlElementWrapper(name = "documents")
	@XmlElement(name = "document")
	private List<CustomerDocument> customerDocumentsList;// ======Customer ID's

	@XmlElementWrapper(name = "addresses")
	@XmlElement(name = "address")
	private List<CustomerAddres> addressList;

	@XmlElementWrapper(name = "phones")
	@XmlElement(name = "phone")
	private List<CustomerPhoneNumber> customerPhoneNumList;

	@XmlElementWrapper(name = "emails")
	@XmlElement(name = "email")
	private List<CustomerEMail> customerEMailList;

	@XmlElementWrapper(name = "customersIncome")
	@XmlElement(name = "customerIncome")
	private List<CustomerIncome> customerIncomeList;

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
	private CoreCustomer coreCustomer;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private CustomerDedup custDedup;
	private String coreReferenceNum; // used while performing ReleaseCIF service
	
	private List<FinanceEnquiry> customerFinances;
	private FinanceEnquiry customerFinance;

	@XmlElement
	private WSReturnStatus returnStatus = null;
	@XmlElement
	private boolean dedupReq;

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

	public String getPrimaryRelationOfficer() {
		return primaryRelationOfficer;
	}

	public void setPrimaryRelationOfficer(String primaryRelationOfficer) {
		this.primaryRelationOfficer = primaryRelationOfficer;
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

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public CustomerDedup getCustDedup() {

		CustomerPhoneNumber custPhoneNumber = null;
		CustomerAddres custAddress = null;
		CustomerEMail custEmail = null;
		custDedup = new CustomerDedup();

		if (this.customer.getCustCIF() != null) {
			custDedup.setCustId(this.customer.getCustID());
			custDedup.setCustCIF(this.customer.getCustCIF() != null ? this.customer.getCustCIF() : "");
			custDedup.setCustCoreBank(this.customer.getCustCoreBank() != null ? this.customer.getCustCoreBank() : "");
			custDedup.setCustDOB(this.customer.getCustDOB());
			custDedup.setCustShrtName(this.customer.getCustShrtName());
			custDedup.setCustNationality(this.customer.getCustNationality() != null ? this.customer
					.getCustNationality() : "");
			custDedup.setCustCtgCode(StringUtils.trimToEmpty(this.customer.getCustCtgCode()));
			custDedup.setCustDftBranch(StringUtils.trimToEmpty(this.customer.getCustDftBranch()));
			custDedup.setCustSector(StringUtils.trimToEmpty(this.customer.getCustSector()));
			custDedup.setCustSubSector(StringUtils.trimToEmpty(this.customer.getCustSubSector()));
		}

		// Customer Phone Number
		List<CustomerPhoneNumber> custPhoneNoList = this.customerPhoneNumList;
		if (custPhoneNoList != null && custPhoneNoList.size() > 0) {
			for (int i = 0; i < custPhoneNoList.size(); i++) {
				custPhoneNumber = (CustomerPhoneNumber) custPhoneNoList.get(i);
				custDedup.setPhoneNumber(custPhoneNumber.getPhoneNumber());
			}
		}

		// Customer Address Number
		List<CustomerAddres> custAddressList = this.addressList;
		if (custAddressList != null && custAddressList.size() > 0) {
			for (int i = 0; i < custAddressList.size(); i++) {
				custAddress = (CustomerAddres) custAddressList.get(i);
				custDedup.setPhoneNumber(custAddress.getCustAddrPhone());
			}
		}

		// Customer Email Details
		List<CustomerEMail> customerEMailList = this.customerEMailList;
		if (customerEMailList != null && customerEMailList.size() > 0) {
			for (int i = 0; i < customerEMailList.size(); i++) {
				custEmail = (CustomerEMail) customerEMailList.get(i);
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

	public boolean isDedupReq() {
		return dedupReq;
	}

	public void setDedupReq(boolean dedupReq) {
		this.dedupReq = dedupReq;
	}

}
