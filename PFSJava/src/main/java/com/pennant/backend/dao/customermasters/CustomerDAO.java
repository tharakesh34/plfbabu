/**
 * ` * Copyright 2011 - Pennant Technologies
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
 * * FileName : CustomerDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified Date :
 * 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.app.core.CustEODEvent;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.ws.model.customer.SRMCustRequest;

public interface CustomerDAO {
	Customer getCustomer(boolean createNew, Customer customer);

	Customer getNewCustomer(boolean createNew, Customer customer);

	Customer getCustomerByID(long id, String type);

	void update(Customer customer, String type);

	void delete(Customer customer, String type);

	long save(Customer customer, String type);

	boolean isDuplicateCif(long custId, String cif);

	Customer getCustomerByCIF(String cifId, String type);

	Customer checkCustomerByCIF(String cifId, String type);

	Customer getCustomer(String cif);

	Customer getCustomer(long custID);

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

	ProspectCustomer getProspectCustomer(long finID, String type);

	String getCustCRCPRById(long custId, String type);

	String getCustomerByCRCPR(String custCRCPR, String type);

	void updateFromFacility(Customer customer, String type);

	String getCustWorstStsDesc(long custID);

	String getCustWorstStsbyCurFinSts(long custID, String finReference, String curFinSts);

	Customer getCustomerByID(final long id);

	List<FinanceEnquiry> getCustomerFinanceDetailById(Customer customer);

	boolean financeExistForCustomer(long id);

	long getCustCRCPRByCustId(String custCRCPR);

	WIFCustomer getWIFByCustCRCPR(String custCRCPR, String type);

	boolean isDuplicateCrcpr(long custId, String custCRCPR);

	String getCustCoreBankIdByCIF(String custCIF);

	void updateCustSuspenseDetails(Customer aCustomer, String tableType);

	void saveCustSuspMovements(Customer aCustomer);

	String getCustSuspRemarks(long custID);

	Customer getSuspendCustomer(Long custID);

	FinanceExposure getCoAppRepayBankTotal(String custCIF);

	BigDecimal getCustRepayProcBank(long custID, String curFinReference);

	int getLookupCount(String tableName, String columnName, Object value);

	int getCustomerCountByCIF(String custCIF, String type);

	boolean getCustomerByCoreBankId(String custCoreBank);

	void updateCustStatus(String custStatus, Date statusChgdate, long custId);

	String getCustomerStatus(long custId);

	Customer getCustomerEOD(long custId);

	Customer getCustomerEOD(String coreBankId);

	Date getCustAppDate(long custId);

	void updateCustAppDate(CustEODEvent custEODEvent);

	List<Customer> getCustomerByGroupID(long custGroupID);

	int updateCustCRCPR(String custDocTitle, long custID);

	boolean customerExistingCustGrp(long custGrpID, String type);

	int getCustCountByDealerId(long dealerId);

	boolean isCasteExist(long casteId, String type);

	boolean isReligionExist(long religionId, String type);

	int getCustomerCountByCustID(long custID, String type);

	List<Customer> getCustomerDetailsByCRCPR(String custCRCPR, String custCtgCode, String type);

	Customer getCustomerByCoreBankId(String externalCif, String type);

	String getCustomerByCRCPR(String custCRCPR, String custCtgCode, String type);

	boolean isDuplicateCif(long custId, String cif, String custCtgCode);

	boolean isDuplicateCrcpr(long custId, String custCRCPR, String custCtgCode);

	boolean isDuplicateCoreBankId(long custId, String custCoreBank);

	Customer getCustomerDetailForFinancials(String custCIF, String tableType);

	int getCrifScoreValue(String tablename, String reference);

	boolean isCrifDeroge(String tablename, String reference);

	List<Long> getCustomerDetailsBySRM(SRMCustRequest srmCustRequest);

	Long getCustomerIdByCIF(String custCIF);

	String getCustomerIdCIF(Long custId);

	Customer getCustomerForPresentment(long id);

	boolean isCustTypeExists(String custType, String type);

	String getExternalCibilResponse(String cif, String tableName);

	List<String> isDuplicateCRCPR(long custId, String custCRCPR, String custCtgCode);

	List<FinanceEnquiry> getCustomerFinances(long custId, long finID, String segmentType);

	boolean isPanFoundByCustIds(List<Long> coAppCustIds, String panNumber);

	String getCustDefaulBranchByCIF(String custCIF);

	long getCustIDByCIF(String custCIF);

	Customer getCustomerForAutoRefund(long custID);

	Customer getCustomerCoreBankID(String cif);

	String getCustShrtNameByFinID(long finID);

	CustomerCoreBank getCoreBankByFinID(long finID);

	CustomerCoreBank getCoreBankByCustID(long custID);

	Date getCustomerDOBByCustID(long custID);

	List<Long> getByCustShrtName(String CustShrtName, TableType tableType);

	List<Long> getByCustCRCPR(String custCRCPR, TableType tableType);

	List<Long> getByAccNumber(String accNumber, TableType tableType);

	List<Long> getByAccNumber(String accNumber);

	List<Long> getByPhoneNumber(String phoneNumber, TableType tableType);

	List<Long> getByCustShrtNameAndPhoneNumber(String CustShrtName, String phoneNumber, TableType tableType);

	List<Long> getByCustShrtNameAndDOB(String custShrtName, Date custDOB, TableType tableType);

	List<Long> getByCustShrtNameAndEMIAmount(String custShrtName, BigDecimal repayAmount);
}
