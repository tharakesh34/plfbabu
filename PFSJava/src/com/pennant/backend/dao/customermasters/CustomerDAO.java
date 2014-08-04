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

	public Customer getCustomer(boolean createNew);
	public Customer getNewCustomer(boolean createNew);
	public Customer getCustomerByID(long id,String type);
	public void update(Customer customer,String type);
	public void delete(Customer customer,String type);
	public long save(Customer customer,String type);
	public void initialize(Customer customer);
	public void refresh(Customer entity);
	public List<Customer> getCustomerByCif(long custId, String cifId);
	public Customer getCustomerByCIF(String cifId,String type);
	public Customer getCustomerForPostings(long custId);
	public String getNewProspectCustomerCIF();
	public List<FinanceProfitDetail> getCustFinAmtDetails(long custId,CustomerEligibilityCheck eligibilityCheck);
	public String getCustEmpDesg(long custID);
	public BigDecimal getCustRepayOtherTotal(long custID);
	public BigDecimal getCustRepayBankTotal(long custID);
	public String getCustCurEmpAlocType(long custID);
	public String getCustWorstSts(long custID);
	public boolean isJointCustExist(long custID);
	public long saveWIFCustomer(WIFCustomer customer);
	public void updateWIFCustomer(WIFCustomer customer);
	public WIFCustomer getWIFCustomerByID(long custId, String custCRCPR, String type);
	public void updateProspectCustomer(Customer customer);
	public Date getCustBlackListedDate(String custCRCPR, String type);
	public ProspectCustomer getProspectCustomer(String finReference, String type);
	public String getCustCRCPRById(long custId, String type);
	public String getCustomerByCRCPR(String custCRCPR, String type);
	void updateFromFacility(Customer customer, String type);
	public AvailPastDue getCustPastDueDetailByCustId(AvailPastDue pastDue, String limitCcy);
	public String getCustWorstStsDesc(long custID);
	public String getCustWorstStsbyCurFinSts(long custID, String finReference, String curFinSts);
 	
} 