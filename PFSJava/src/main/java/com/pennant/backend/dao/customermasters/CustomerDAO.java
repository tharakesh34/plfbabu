/**
` * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  CustomerDAO.java                                                   * 	  
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
package com.pennant.backend.dao.customermasters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.reports.AvailPastDue;

public interface CustomerDAO {
	Customer getCustomer(boolean createNew);

	Customer getNewCustomer(boolean createNew);

	Customer getCustomerByID(long id, String type);

	void update(Customer customer, String type);

	void delete(Customer customer, String type);

	long save(Customer customer, String type);

	boolean isDuplicateCif(long custId, String cif);

	Customer getCustomerByCIF(String cifId, String type);
	
	Customer checkCustomerByCIF(String cifId, String type);

	WIFCustomer getWIFCustomerByCIF(long cifId, String type);

	Customer getCustomerForPostings(long custId);

	String getNewProspectCustomerCIF();

	List<FinanceProfitDetail> getCustFinAmtDetails(long custId, CustomerEligibilityCheck eligibilityCheck);

	String getCustEmpDesg(long custID);

	BigDecimal getCustRepayOtherTotal(long custID);

	BigDecimal getCustRepayBankTotal(long custID);

	String getCustCurEmpAlocType(long custID);

	String getCustWorstSts(long custID);

	boolean isJointCustExist(long custID);

	long saveWIFCustomer(WIFCustomer customer);

	void updateWIFCustomer(WIFCustomer customer);

	WIFCustomer getWIFCustomerByID(long custId, String custCRCPR, String type);

	void updateProspectCustomer(Customer customer);

	Date getCustBlackListedDate(String custCRCPR, String type);

	ProspectCustomer getProspectCustomer(String finReference, String type);

	String getCustCRCPRById(long custId, String type);

	String getCustomerByCRCPR(String custCRCPR, String type);

	void updateFromFacility(Customer customer, String type);

	AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy);

	String getCustWorstStsDesc(long custID);

	String getCustWorstStsbyCurFinSts(long custID, String finReference, String curFinSts);

	Customer getCustomerByID(final long id);

	List<FinanceEnquiry> getCustomerFinanceDetailById(long custId);

	boolean financeExistForCustomer(final long id, String type);

	long getCustCRCPRByCustId(String custCRCPR, String type);

	WIFCustomer getWIFByCustCRCPR(String custCRCPR, String type);

	boolean isDuplicateCrcpr(long custId, String custCRCPR);

	void updateProspectCustCIF(String oldCustCIF, String newCustCIF);

	String getCustCoreBankIdByCIF(String custCIF);

	String getNewCoreCustomerCIF();

	void updateCorebankCustCIF(String newCoreCustCIF);

	void updateCustSuspenseDetails(Customer aCustomer, String tableType);

	void saveCustSuspMovements(Customer aCustomer);

	String getCustSuspRemarks(long custID);

	Customer getSuspendCustomer(Long custID);

	FinanceExposure getCoAppRepayBankTotal(String custCIF);

	BigDecimal getCustRepayProcBank(long custID, String curFinReference);

	ArrayList<Customer> getCustomerByLimitRule(String queryCode, String sqlQuery);

	int getLookupCount(String tableName, String columnName, String value);

	int getCustomerCountByCIF(String custCIF, String type);

	boolean getCustomerByCoreBankId(String custCoreBank);

	void updateCustStatus(String custStatus, Date statusChgdate, long custId);


	Customer getCustomerStatus(long custId);
	
	Customer getCustomerEOD(long id);

	Date getCustAppDate(long custId);

	void updateCustAppDate(long custId, Date custAppDate, String newCustStatus);

	List<Customer> getCustomerByGroupID(long custGroupID);

	int updateCustCRCPR(String custDocTitle,long custID);
	
	boolean customerExistingCustGrp(long custGrpID, String type) ;
}
