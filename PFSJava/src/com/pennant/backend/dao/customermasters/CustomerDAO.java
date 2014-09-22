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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.reports.AvailPastDue;

public interface CustomerDAO {

	 Customer getCustomer(boolean createNew);
	 Customer getNewCustomer(boolean createNew);
	 Customer getCustomerByID(long id,String type);
	 void update(Customer customer,String type);
	 void delete(Customer customer,String type);
	 long save(Customer customer,String type);
	 void initialize(Customer customer);
	 void refresh(Customer entity);
	 List<Customer> getCustomerByCif(long custId, String cifId);
	 Customer getCustomerByCIF(String cifId,String type);
	 Customer getCustomerForPostings(long custId);
	 String getNewProspectCustomerCIF();
	 List<FinanceProfitDetail> getCustFinAmtDetails(long custId,CustomerEligibilityCheck eligibilityCheck);
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
 	
} 