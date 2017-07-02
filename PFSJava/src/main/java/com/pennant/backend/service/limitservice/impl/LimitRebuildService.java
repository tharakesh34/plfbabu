/**
Copyright 2011 - Pennant Technologies
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
 *
 * FileName    		:  LimitRebuildService.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  23-06-2017															*
 *                                                                  
 * Modified Date    :  23-06-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
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

package com.pennant.backend.service.limitservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.RuleReturnType;

public class LimitRebuildService implements LimitRebuild {

	private static Logger				logger	= Logger.getLogger(LimitManagement.class);

	@Autowired
	private CustomerDAO					customerDAO;
	@Autowired
	private LimitDetailDAO				limitDetailDAO;
	@Autowired
	private LimitHeaderDAO				limitHeaderDAO;
	@Autowired
	private FinanceMainDAO				financeMainDAO;
	@Autowired
	private FinanceDisbursementDAO		financeDisbursementDAO;
	@Autowired
	private RuleExecutionUtil			ruleExecutionUtil;
	@Autowired
	private LimitGroupLinesDAO			limitGroupLinesDAO;
	@Autowired
	private FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	@Autowired
	private LimitStructureDetailDAO		limitStructureDetailDAO;
	@Autowired
	private LimitReferenceMappingDAO	limitReferenceMappingDAO;

	@Override
	public void processCustomerRebuild(long custID) throws DatatypeConfigurationException {

		LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custID, "");
		if (limitHeader != null) {
			//get the limit details
			long headerId = limitHeader.getHeaderId();
			List<LimitDetails> limitDetailsList = limitDetailDAO.getLimitDetails(headerId);
			// Verify the structure for changes
			processStructuralChanges(limitDetailsList, limitHeader);
			//reset limit details
			resetLimitDetails(limitDetailsList);

			List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
			// get all the finance in the group
			financeMains.addAll(financeMainDAO.getBYCustIdForLimitRebuild(custID, false));
			financeMains.addAll(financeMainDAO.getBYCustIdForLimitRebuild(custID, true));

			List<LimitReferenceMapping> mappings = new ArrayList<LimitReferenceMapping>();
			for (FinanceMain finMain : financeMains) {
				//identify the limit line 
				LimitReferenceMapping mapping = identifyLine(finMain, null, null, headerId, limitDetailsList);
				//process rebuild
				processRebuild(finMain, limitHeader, limitDetailsList, mapping);
				// add the mapping
				mappings.add(mapping);
			}

			//save mapping by deleting and delete or move the transaction log
			if (mappings.isEmpty()) {
				limitReferenceMappingDAO.deleteByHeaderID(headerId);
				for (LimitReferenceMapping limitReferenceMapping : mappings) {
					limitReferenceMappingDAO.save(limitReferenceMapping);
				}
			}
		}
	}

	@Override
	public void processCustomerGroupRebuild(long custGroupID) throws DatatypeConfigurationException {

		LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(custGroupID, "");
		if (limitHeader != null) {
			//get the limit details
			long headerId = limitHeader.getHeaderId();
			List<LimitDetails> limitDetailsList = limitDetailDAO.getLimitDetails(headerId);
			//Verify the structure for changes
			processStructuralChanges(limitDetailsList, limitHeader);
			//reset limit details
			resetLimitDetails(limitDetailsList);
			// get the all the customers in the group 
			List<Customer> customers = customerDAO.getCustomerByGroupID(custGroupID);
			List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
			// get all the finance in the group
			for (Customer customer : customers) {
				financeMains.addAll(financeMainDAO.getBYCustIdForLimitRebuild(customer.getCustID(), false));
				financeMains.addAll(financeMainDAO.getBYCustIdForLimitRebuild(customer.getCustID(), true));
			}

			List<LimitReferenceMapping> mappings = new ArrayList<LimitReferenceMapping>();
			for (FinanceMain finMain : financeMains) {
				//identify the limit line 
				Customer customer = getCustomer(customers, finMain.getCustID());
				LimitReferenceMapping mapping = identifyLine(finMain, null, customer, headerId, limitDetailsList);
				//process rebuild
				processRebuild(finMain, limitHeader, limitDetailsList, mapping);
				// add the mapping
				mappings.add(mapping);
			}

			//save mapping by deleting and delete or move the transaction log
			if (mappings.isEmpty()) {
				limitReferenceMappingDAO.deleteByHeaderID(headerId);
				for (LimitReferenceMapping limitReferenceMapping : mappings) {
					limitReferenceMappingDAO.save(limitReferenceMapping);
				}
			}

		}

	}

	private Customer getCustomer(List<Customer> customers, long custID) {
		for (Customer customer : customers) {
			if (customer.getCustID() == custID) {
				return customer;
			}
		}
		return new Customer();
	}

	/**
	 * 
	 * @param finMain
	 * @param limitHeader
	 * @param limitDetailsList
	 * @param mapping
	 */
	private void processRebuild(FinanceMain finMain, LimitHeader limitHeader, List<LimitDetails> limitDetailsList,
			LimitReferenceMapping mapping) {
		logger.debug(" Entering ");

		String finRef = finMain.getFinReference();
		String finCategory = finMain.getFinCategory();
		String finCcy = finMain.getFinCcy();
		String limitCcy = limitHeader.getLimitCcy();
		List<LimitDetails> list = getCustomerLimitDetails(mapping);

		// calculate reserve and utilized
		BigDecimal tranReseervAmt = finMain.getFinAssetValue();
		BigDecimal tranUtilisedAmt = BigDecimal.ZERO;
		List<FinanceDisbursement> disbursementDetailList = financeDisbursementDAO.getFinanceDisbursementDetails(finRef,
				"", false);

		for (FinanceDisbursement disbursement : disbursementDetailList) {
			if (!StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				tranUtilisedAmt = tranUtilisedAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
				if (disbursement.getDisbDate().getTime() == finMain.getFinStartDate().getTime()) {
					tranUtilisedAmt = tranUtilisedAmt.subtract(finMain.getDownPayment());
				}
			}
		}

		// convert to limit currency
		BigDecimal limitReserveAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranReseervAmt);
		BigDecimal limitUtilisedAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranUtilisedAmt);

		// update reserve and utilization
		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(limitDetailsList, details);

			// add to reserve
			limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(limitReserveAmt));

			// if records are not approved then we are considering reserve only
			if (finMain.isLimitValid()) {
				continue;
			}
			
			//TODO for Under process loans in addDisbursment

			// remove from reserve
			if (limitReserveAmt.compareTo(limitUtilisedAmt) >= 0) {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().subtract(limitUtilisedAmt));
			} else {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().subtract(limitReserveAmt));
			}
			// add to utilized
			limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().add(limitUtilisedAmt));
		}

		// if records are not approved then we are considering reserve only
		if (finMain.isLimitValid()) {
			return;
		}

		// update revolving nature by payments made
		BigDecimal repay = financeScheduleDetailDAO.getPriPaidAmount(finRef);
		BigDecimal repayLimit = CalculationUtil.getConvertedAmount(finCcy, limitCcy, repay);

		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(limitDetailsList, details);
			limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().subtract(repayLimit));
			if (FinanceConstants.PRODUCT_ODFACILITY.equals(finCategory)) {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(repayLimit));
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param limitHeader
	 * @throws DatatypeConfigurationException
	 */
	private void processStructuralChanges(List<LimitDetails> limitDetailsList, LimitHeader limitHeader)
			throws DatatypeConfigurationException {
		logger.debug(" Entering ");

		List<LimitDetails> list = new ArrayList<LimitDetails>();
		List<LimitStructureDetail> structureList = limitStructureDetailDAO
				.getLimitStructureDetailById(limitHeader.getLimitStructureCode(), "");

		if (structureList.size() != limitDetailsList.size()) {

			for (LimitStructureDetail limitStructureDetail : structureList) {

				if (!isExist(limitDetailsList, limitStructureDetail)) {
					LimitDetails details = getLimitDetails(limitStructureDetail, limitHeader);
					list.add(details);
				}
			}
		}

		// Add New Structural Changes
		limitDetailDAO.saveList(list, "");
		limitDetailsList.addAll(list);

		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param limitStructureDetail
	 * @return
	 */
	private boolean isExist(List<LimitDetails> limitDetailsList, LimitStructureDetail limitStructureDetail) {
		for (LimitDetails limitDetails : limitDetailsList) {
			if (limitDetails.getLimitStructureDetailsID() == limitStructureDetail.getLimitStructureDetailsID()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param limitStructureDetail
	 * @param limitHeader
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private LimitDetails getLimitDetails(LimitStructureDetail limitStructureDetail, LimitHeader limitHeader)
			throws DatatypeConfigurationException {

		LimitDetails limitDetail = new LimitDetails();

		limitDetail.setDetailId(Long.MIN_VALUE);
		limitDetail.setLimitHeaderId(limitHeader.getHeaderId());
		limitDetail.setLimitStructureDetailsID(limitStructureDetail.getLimitStructureDetailsID());
		limitDetail.setExpiryDate(limitHeader.getLimitExpiryDate());

		limitDetail.setLimitSanctioned(BigDecimal.ZERO);
		limitDetail.setReservedLimit(BigDecimal.ZERO);
		limitDetail.setUtilisedLimit(BigDecimal.ZERO);

		limitDetail.setLimitCheck(false);
		limitDetail.setLimitChkMethod(LimitConstants.LIMIT_CHECK_RESERVED);

		limitDetail.setVersion(1);
		limitDetail.setCreatedBy(limitStructureDetail.getCreatedBy());
		limitDetail.setCreatedOn(limitStructureDetail.getCreatedOn());
		limitDetail.setLastMntBy(limitStructureDetail.getLastMntBy());
		limitDetail.setLastMntOn(limitStructureDetail.getLastMntOn());
		limitDetail.setRecordStatus("Approved");
		limitDetail.setRoleCode("");
		limitDetail.setNextRoleCode("");
		limitDetail.setTaskId("");
		limitDetail.setNextTaskId("");
		limitDetail.setRecordType("");
		limitDetail.setWorkflowId(0);
		limitDetail.setRevolving(true);

		return limitDetail;
	}

	/**
	 * 
	 * @param limitDetailsList
	 */
	private void resetLimitDetails(List<LimitDetails> limitDetailsList) {
		for (LimitDetails limitDetail : limitDetailsList) {
			limitDetail.setReservedLimit(BigDecimal.ZERO);
			limitDetail.setUtilisedLimit(BigDecimal.ZERO);
		}
	}

	/**
	 * 
	 * @param list
	 * @param limitofind
	 * @return
	 */
	private LimitDetails getLimitdetails(List<LimitDetails> list, LimitDetails limitofind) {
		for (LimitDetails limitDetails : list) {
			if (limitofind.getDetailId() == limitDetails.getDetailId()) {
				return limitDetails;
			}

		}
		return null;
	}

	/**
	 * @param finMain
	 * @param financeType
	 * @param headerId
	 * @param limitDetailsList2
	 * @return
	 */
	private LimitReferenceMapping identifyLine(FinanceMain finMain, FinanceType financeType, Customer customer,
			long headerId, List<LimitDetails> limitDetailsList) {
		logger.debug(" Entering ");

		String finRef = finMain.getFinReference();
		LimitReferenceMapping mapping = null;
		mapping = limitReferenceMappingDAO.getLimitReferencemapping(finRef, headerId);
		if (mapping == null) {
			HashMap<String, Object> dataMap = getDataMap(finMain, financeType, customer);
			if (limitDetailsList != null && limitDetailsList.size() > 0) {
				boolean uncalssifed = true;
				for (LimitDetails details : limitDetailsList) {

					if (StringUtils.isBlank(details.getSqlRule())) {
						continue;
					}

					boolean ruleResult = (boolean) ruleExecutionUtil.executeRule(details.getSqlRule(), dataMap, "",
							RuleReturnType.BOOLEAN);
					if (ruleResult) {
						mapping = getLimitRefMapping(LimitConstants.FINANCE, finRef, details.getLimitLine(), headerId);
						uncalssifed = false;
						break;
					}
				}

				if (uncalssifed) {
					mapping = getLimitRefMapping(LimitConstants.FINANCE, finRef, LimitConstants.LIMIT_ITEM_UNCLSFD,
							headerId);
				}
			}
		}
		// Return the Limit items
		logger.debug(" Leaving ");
		return mapping;
	}

	/**
	 * @param financeMain
	 * @param customer
	 * @param financeType
	 * @return
	 */
	private HashMap<String, Object> getDataMap(FinanceMain financeMain, FinanceType financeType, Customer customer) {

		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (financeMain != null) {
			dataMap.putAll(financeMain.getDeclaredFieldValues());
		}
		if (financeType != null) {
			dataMap.putAll(financeType.getDeclaredFieldValues());
		}
		if (customer != null) {
			dataMap.putAll(customer.getDeclaredFieldValues());
		}

		return dataMap;
	}

	/**
	 * @param refCode
	 * @param finreference
	 * @param limitLine
	 * @return
	 */
	private LimitReferenceMapping getLimitRefMapping(String refCode, String finreference, String limitLine,
			long headerId) {

		LimitReferenceMapping limitReferenceMapping = new LimitReferenceMapping();
		limitReferenceMapping.setNewRecord(true);
		limitReferenceMapping.setReferenceNumber(finreference);
		limitReferenceMapping.setReferenceCode(refCode);
		limitReferenceMapping.setLimitLine(limitLine);
		limitReferenceMapping.setHeaderId(headerId);

		return limitReferenceMapping;
	}

	/**
	 * @param limitLine
	 * @param isCustomer
	 * @param header
	 * @return
	 */
	private List<LimitDetails> getCustomerLimitDetails(LimitReferenceMapping mapping) {
		logger.debug(" Entering ");

		long headerId = mapping.getHeaderId();
		String limitLine = mapping.getLimitLine();
		List<LimitDetails> list = new ArrayList<LimitDetails>();
		List<String> groupCodes = new ArrayList<>();
		groupCodes.add(LimitConstants.LIMIT_ITEM_TOTAL);

		if (!limitLine.equals(LimitConstants.LIMIT_ITEM_UNCLSFD)) {
			String groupCode = limitGroupLinesDAO.getGroupByLineAndHeader(limitLine, headerId);
			String parentGroup = groupCode;

			while (!StringUtils.isEmpty(parentGroup)) {
				groupCodes.add(parentGroup);
				parentGroup = limitGroupLinesDAO.getGroupByGroupAndHeader(parentGroup, headerId);
			}
		}

		list = limitDetailDAO.getLimitByLineAndgroup(headerId, limitLine, groupCodes);

		logger.debug(" Leaving ");
		return list;
	}
}
