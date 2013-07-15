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

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;

/**
 * Model class for the <b>Customer table</b>.<br>
 *
 */
public class CustomerDetails implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private long custID ;
	private boolean newRecord=false;

	private Customer customer;
	private CustomerEmploymentDetail customerEmploymentDetail;
	private List<CustomerRating> ratingsList;
	private List<CustomerAddres> addressList;
	private List<CustomerIncome> customerIncomeList;
	private List<CustomerPRelation> customerPRelationList;
	private List<CustomerPhoneNumber> customerPhoneNumList;
	private List<CustomerEMail> customerEMailList;
	private List<CustomerDocument> customerDocumentsList;
	private CorporateCustomerDetail corporateCustomerDetail;
	private List<DirectorDetail> directorsList;
	private List<CustomerBalanceSheet> balanceSheetList;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
	private CustomerDedup	custDedup;

	private CustomerDetails befImage;
	private LoginUserDetails userDetails;
	public CustomerDetails(){
		super();
		this.customerEmploymentDetail = new CustomerEmploymentDetail();
		this.customer 				  = new Customer();
		this.custDedup				  = new CustomerDedup();	
		this.corporateCustomerDetail = new CorporateCustomerDetail();
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

	public CustomerEmploymentDetail getCustomerEmploymentDetail() {
		return customerEmploymentDetail;
	}
	public void setCustomerEmploymentDetail(
			CustomerEmploymentDetail customerEmploymentDetail) {
		this.customerEmploymentDetail = customerEmploymentDetail;
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

	public List<CustomerPRelation> getCustomerPRelationList() {
		return customerPRelationList;
	}
	public void setCustomerPRelationList(
			List<CustomerPRelation> customerPRelationList) {
		this.customerPRelationList = customerPRelationList;
	}

	public CustomerDetails getBefImage() {
		return befImage;
	}
	public void setBefImage(CustomerDetails befImage) {
		this.befImage = befImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
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

	public CorporateCustomerDetail getCorporateCustomerDetail() {
		return corporateCustomerDetail;
	}
	public void setCorporateCustomerDetail(
			CorporateCustomerDetail corporateCustomerDetail) {
		this.corporateCustomerDetail = corporateCustomerDetail;
	}

	public void setDirectorsList(List<DirectorDetail> directorsList) {
		this.directorsList = directorsList;
	}
	public List<DirectorDetail> getDirectorsList() {
		return directorsList;
	}

	public List<CustomerBalanceSheet> getBalanceSheetList() {
		return balanceSheetList;
	}
	public void setBalanceSheetList(List<CustomerBalanceSheet> balanceSheetList) {
		this.balanceSheetList = balanceSheetList;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}
	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public CustomerDedup getCustDedup() {

		CustomerPhoneNumber custPhoneNumber = null;
		CustomerAddres		custAddress		= null;
		CustomerEMail		custEmail		= null;
		custDedup = new CustomerDedup();

		if(this.customer.getCustCIF()!= null){
			custDedup.setCustId(this.customer.getCustID());
			custDedup.setCustCIF(this.customer.getCustCIF()!=null?this.customer.getCustCIF():"");
			custDedup.setCustCoreBank(this.customer.getCustCoreBank()!=null?this.customer.getCustCoreBank():"");
			custDedup.setCustFName(this.customer.getCustFName()!=null?this.customer.getCustFName():"");
			custDedup.setCustMName(this.customer.getCustMName()!=null?this.customer.getCustMName():"");
			custDedup.setCustLName(this.customer.getCustLName()!=null?this.customer.getCustLName():"");
			custDedup.setCustMotherMaiden(this.customer.getCustMotherMaiden() != null?this.customer.getCustMotherMaiden():"");
			custDedup.setLikeCustFName(this.customer.getCustFName()!=null?"%"+this.customer.getCustFName()+"%":"");
			custDedup.setLikeCustMName(this.customer.getCustMName()!=null?"%"+this.customer.getCustMName()+"%":"");
			custDedup.setLikeCustLName(this.customer.getCustLName()!=null?"%"+this.customer.getCustLName()+"%":"");
			custDedup.setLikecustMotherMaiden(this.customer.getCustMotherMaiden() != null?"%"+this.customer.getCustMotherMaiden()+"%":"");
			custDedup.setCustDOB(this.customer.getCustDOB());		
			custDedup.setCustCOB(this.customer.getCustCOB());		
			custDedup.setCustNationality(this.customer.getCustNationality()!=null ?this.customer.getCustNationality():"");
			custDedup.setCustPassportExpiry(this.customer.getCustPassportExpiry());
			custDedup.setCustPassportNo(this.customer.getCustPassportNo()!=null?this.customer.getCustPassportNo():"");
			custDedup.setCustPOB(this.customer.getCustPOB()!=null?this.customer.getCustPOB():"");
			custDedup.setCustResdCountry(this.customer.getCustResdCountry()!=null?this.customer.getCustResdCountry():"");
			custDedup.setCustSalutationCode(this.customer.getCustSalutationCode()!=null?this.customer.getCustSalutationCode():"");
			custDedup.setCustVisaNum(this.customer.getCustVisaNum()!=null?this.customer.getCustVisaNum():"");
			custDedup.setCustTradeLicenceNum(this.customer.getCustTradeLicenceNum()!=null?this.customer.getCustTradeLicenceNum():"");
		}		

		//Customer Phone Number
		List<CustomerPhoneNumber> custPhoneNoList = this.customerPhoneNumList;
		if(custPhoneNoList != null && custPhoneNoList.size()>0){    			
			for(int i =0;i <custPhoneNoList.size();i++){    					
				custPhoneNumber = (CustomerPhoneNumber)custPhoneNoList.get(i);
				custDedup.setPhoneNumber(custPhoneNumber.getPhoneNumber());
			}
		}

		//Customer Address Number
		List<CustomerAddres> custAddressList = this.addressList;				
		if(custAddressList != null && custAddressList.size()>0){    			
			for(int i =0;i <custAddressList.size();i++){    					
				custAddress = (CustomerAddres)custAddressList.get(i);
				custDedup.setPhoneNumber(custAddress.getCustAddrPhone());
			}
		}

		//Customer Email Details
		List<CustomerEMail> customerEMailList = this.customerEMailList;				
		if(customerEMailList != null && customerEMailList.size()>0){    			
			for(int i =0;i <customerEMailList.size();i++){    					
				custEmail = (CustomerEMail)customerEMailList.get(i);
				custDedup.setCustEMail(custEmail.getCustEMail());
			}
		}
		custDedup.setDedupFields(Labels.getLabel("label_DedupFields_label"));
		return custDedup;
	}
	public void setCustDedup(CustomerDedup custDedup) {
		this.custDedup = custDedup;
	}

}
