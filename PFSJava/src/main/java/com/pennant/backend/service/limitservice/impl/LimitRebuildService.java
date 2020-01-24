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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;

public class LimitRebuildService implements LimitRebuild {
	private static Logger logger = Logger.getLogger(LimitRebuildService.class);

	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private LimitDetailDAO limitDetailDAO;
	@Autowired
	private LimitHeaderDAO limitHeaderDAO;
	@Autowired
	private FinanceDisbursementDAO financeDisbursementDAO;
	@Autowired
	private RuleExecutionUtil ruleExecutionUtil;
	@Autowired
	private LimitGroupLinesDAO limitGroupLinesDAO;
	@Autowired
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	@Autowired
	private LimitStructureDetailDAO limitStructureDetailDAO;
	@Autowired
	private LimitReferenceMappingDAO limitReferenceMappingDAO;
	@Autowired
	private LimitTransactionDetailsDAO limitTransactionDetailsDAO;

	@Autowired
	private RuleDAO ruleDAO;

	@Override
	public void processCustomerRebuild(long custID, boolean rebuildOnStrChg) {
		LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderByCustomerId(custID, "");

		if (limitHeader == null || !limitHeader.isActive()) {
			return;
		}

		Map<String, Set<String>> fieldMap = getLimitFieldMap();
		Customer customer = limitHeaderDAO.getLimitFieldsByCustId(custID, fieldMap.get("ct_"));

		long headerId = limitHeader.getHeaderId();
		List<LimitDetails> limitDetailsList = limitDetailDAO.getLimitDetails(headerId);

		Map<String, BigDecimal> osPriBal = limitDetailDAO.getOsPriBal(custID);

		if (rebuildOnStrChg) {
			processStructuralChanges(limitDetailsList, limitHeader);
		}

		resetLimitDetails(limitDetailsList);

		List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
		financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custID, fieldMap.get("fm_"), false));
		financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custID, fieldMap.get("fm_"), true));

		List<LimitReferenceMapping> mappings = new ArrayList<LimitReferenceMapping>();

		Map<String, FinanceType> finTypeMap = new HashMap<>();
		FinanceType financeType = null;
		LimitReferenceMapping mapping = null;
		for (FinanceMain finMain : financeMains) {
			String finType = finMain.getFinType();
			
			BigDecimal priBal = osPriBal.get(finMain.getFinReference());

			if (priBal == null) {
				priBal = BigDecimal.ZERO;
			}

			finMain.setOsPriBal(priBal);

			financeType = finTypeMap.computeIfAbsent(finType, ft -> getFinanceTye(finType, fieldMap.get("ft_")));
			mapping = identifyLine(finMain, financeType, customer, headerId, limitDetailsList);

			if (!StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
				processRebuild(finMain, limitHeader, limitHeader.getHeaderId(), limitDetailsList, mapping);
				mappings.add(mapping);
			}

		}

		if (!mappings.isEmpty()) {
			limitReferenceMappingDAO.deleteByHeaderID(headerId);
			limitReferenceMappingDAO.saveBatch(mappings);
		}

		limitDetailDAO.updateReserveUtiliseList(limitDetailsList, "");

	}

	private FinanceType getFinanceTye(String finType, Set<String> fields) {
		return limitHeaderDAO.getLimitFieldsByFinTpe(finType, fields);
	}

	private Map<String, Set<String>> getLimitFieldMap() {
		Map<String, Set<String>> limitFieldMap = new HashMap<>();

		List<String> limitFields = limitHeaderDAO.getLimitRuleFields();

		Set<String> fm = new HashSet<>();
		Set<String> ct = new HashSet<>();
		Set<String> ft = new HashSet<>();

		for (String field : limitFields) {
			if (StringUtils.startsWithIgnoreCase(field, "fm_")) {
				fm.add(StringUtils.substring(field, 3));
			} else if (StringUtils.startsWithIgnoreCase(field, "ct_")) {
				ct.add(StringUtils.substring(field, 3));
			} else if (StringUtils.startsWithIgnoreCase(field, "ft_")) {
				ft.add(StringUtils.substring(field, 3));
			}
		}

		fm.add("finReference");

		limitFieldMap.put("fm_", fm);
		limitFieldMap.put("ct_", ct);
		limitFieldMap.put("ft_", ft);

		return limitFieldMap;
	}

	@Override
	public void processCustomerGroupRebuild(long rebuildGroupID, boolean removedFromGroup, boolean addedNewlyToGroup) {
		LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(rebuildGroupID, "");

		if (limitHeader == null || !limitHeader.isActive()) {
			return;
		}

		long headerId = limitHeader.getHeaderId();
		List<LimitDetails> limitDetailsList = limitDetailDAO.getLimitDetails(headerId);

		processStructuralChanges(limitDetailsList, limitHeader);

		resetLimitDetails(limitDetailsList);

		Map<String, Set<String>> fieldMap = getLimitFieldMap();
		List<Customer> customers = customerDAO.getCustomerByGroupID(rebuildGroupID);

		List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
		List<LimitHeader> headers = new ArrayList<LimitHeader>();
		long custId;
		for (Customer customer : customers) {
			custId = customer.getCustID();
			financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custId, fieldMap.get("fm_"), false));
			financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custId, fieldMap.get("fm_"), true));

			Map<String, BigDecimal> osPriBal = limitDetailDAO.getOsPriBal(custId);
			for (FinanceMain fm : financeMains) {
				fm.setOsPriBal(osPriBal.get(fm.getFinReference()));
			}

			LimitHeader limitHeaderByCustomerId = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "");

			if (limitHeaderByCustomerId != null) {
				headers.add(limitHeaderByCustomerId);
			}
		}

		List<LimitReferenceMapping> mappings = new ArrayList<LimitReferenceMapping>();

		Map<String, FinanceType> finTypeMap = new HashMap<>();
		FinanceType financeType = null;

		if (!financeMains.isEmpty()) {
			for (FinanceMain finMain : financeMains) {
				if (addedNewlyToGroup) {
					limitTransactionDetailsDAO.updateHeaderIDWithFin(finMain.getFinReference(), 0, headerId);
				}

				String finType = finMain.getFinType();
				financeType = finTypeMap.computeIfAbsent(finType, ft -> getFinanceTye(finType, fieldMap.get("ft_")));

				Customer customer = getCustomer(customers, finMain.getCustID());
				LimitReferenceMapping mapping = identifyLine(finMain, financeType, customer, headerId,
						limitDetailsList);
				long transactionID = 0;
				LimitHeader custHeader = getCustLimitHeader(headers, customer.getCustID());
				if (custHeader != null) {
					transactionID = custHeader.getHeaderId();
				} else {
					transactionID = headerId;
				}
				//process rebuild
				if (!StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
					processRebuild(finMain, limitHeader, transactionID, limitDetailsList, mapping);
				}

				if (removedFromGroup) {
					limitTransactionDetailsDAO.updateHeaderIDWithFin(finMain.getFinReference(), headerId, 0);
				}
				// add the mapping
				mappings.add(mapping);
			}
		} else {
			if (removedFromGroup) {
				limitTransactionDetailsDAO.updateHeaderID(headerId, 0);
			}
		}

		limitReferenceMappingDAO.deleteByHeaderID(headerId);
		if (!mappings.isEmpty()) {
			limitReferenceMappingDAO.saveBatch(mappings);
		}
		limitDetailDAO.updateReserveUtiliseList(limitDetailsList, "");

	}

	private LimitHeader getCustLimitHeader(List<LimitHeader> headers, long custid) {
		if (headers != null && !headers.isEmpty()) {
			for (LimitHeader limitHeader : headers) {
				if (limitHeader.getCustomerId() == custid) {
					return limitHeader;
				}
			}
		}
		return null;
	}

	@Override
	public void processCustomerGroupSwap(long rebuildGroupID, long prvGroupID) {
		LimitHeader limitHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(rebuildGroupID, "");

		if (limitHeader == null || !limitHeader.isActive()) {
			return;
		}

		LimitHeader prvlimitHeader = limitHeaderDAO.getLimitHeaderByCustomerGroupCode(prvGroupID, "");

		long prvLimitHeaderID = 0;

		if (prvlimitHeader != null) {
			prvLimitHeaderID = prvlimitHeader.getHeaderId();
		}

		long headerId = limitHeader.getHeaderId();

		List<LimitDetails> limitDetailsList = limitDetailDAO.getLimitDetails(headerId);

		processStructuralChanges(limitDetailsList, limitHeader);

		resetLimitDetails(limitDetailsList);

		List<Customer> customers = customerDAO.getCustomerByGroupID(rebuildGroupID);
		List<FinanceMain> financeMains = new ArrayList<FinanceMain>();
		List<LimitHeader> headers = new ArrayList<LimitHeader>();

		Map<String, Set<String>> fieldMap = getLimitFieldMap();
		long custId;
		for (Customer customer : customers) {
			custId = customer.getCustID();

			financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custId, fieldMap.get("fm_"), false));
			financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custId, fieldMap.get("fm_"), true));

			Map<String, BigDecimal> osPriBal = limitDetailDAO.getOsPriBal(custId);
			for (FinanceMain fm : financeMains) {
				fm.setOsPriBal(osPriBal.get(fm.getFinReference()));
			}

			LimitHeader limitHeaderByCustomerId = limitHeaderDAO.getLimitHeaderByCustomerId(customer.getCustID(), "");

			if (limitHeaderByCustomerId != null) {
				headers.add(limitHeaderByCustomerId);
			}
		}

		List<LimitReferenceMapping> mappings = new ArrayList<LimitReferenceMapping>();

		Map<String, FinanceType> finTypeMap = new HashMap<>();
		FinanceType financeType = null;

		for (FinanceMain finMain : financeMains) {
			Customer customer = getCustomer(customers, finMain.getCustID());

			String finType = finMain.getFinType();
			financeType = finTypeMap.computeIfAbsent(finType, ft -> getFinanceTye(finType, fieldMap.get("ft_")));

			LimitReferenceMapping mapping = identifyLine(finMain, financeType, customer, headerId, limitDetailsList);

			long transactionID = 0;
			LimitHeader custHeader = getCustLimitHeader(headers, customer.getCustID());
			if (custHeader != null) {
				transactionID = custHeader.getHeaderId();
			} else {
				transactionID = prvLimitHeaderID;
			}

			if (!StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {
				processRebuild(finMain, limitHeader, transactionID, limitDetailsList, mapping);
			}

			limitTransactionDetailsDAO.updateHeaderIDWithFin(finMain.getFinReference(), prvLimitHeaderID, headerId);
			mappings.add(mapping);
		}

		if (!mappings.isEmpty()) {
			limitReferenceMappingDAO.deleteByHeaderID(headerId);
			limitReferenceMappingDAO.saveBatch(mappings);
		}

		limitDetailDAO.updateReserveUtiliseList(limitDetailsList, "");

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
	private void processRebuild(FinanceMain finMain, LimitHeader limitHeader, long inProgressHeaderID,
			List<LimitDetails> limitDetailsList, LimitReferenceMapping mapping) {
		logger.debug(" Entering ");

		String finRef = finMain.getFinReference();
		String finCategory = finMain.getFinCategory();
		String finCcy = finMain.getFinCcy();
		String limitCcy = limitHeader.getLimitCcy();
		List<LimitDetails> list = getCustomerLimitDetails(mapping);
		boolean addTempblock = false;

		// calculate reserve and utilized
		BigDecimal tranReseervAmt = finMain.getFinAssetValue();

		if (finMain.isLimitValid()) {
			//check the there is block in not then don not proceed
			LimitTransactionDetail transaction = getTransaction(finRef, inProgressHeaderID, 0);
			if (transaction == null) {
				// then no need block the amount
				return;
			}
		}

		BigDecimal tranUtilisedAmt = BigDecimal.ZERO;
		List<FinanceDisbursement> disbursementDetailList = financeDisbursementDAO.getFinanceDisbursementDetails(finRef,
				"", false);

		for (FinanceDisbursement disbursement : disbursementDetailList) {
			if (StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}
			tranUtilisedAmt = tranUtilisedAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
		}
		tranUtilisedAmt = tranUtilisedAmt.subtract(finMain.getDownPayment());

		if (finMain.getFinCurrAssetValue().compareTo(tranReseervAmt) == 0) {
			addTempblock = true;
		}

		// convert to limit currency
		BigDecimal limitReserveAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranReseervAmt);
		BigDecimal limitUtilisedAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranUtilisedAmt);
		BigDecimal osPriBal = CalculationUtil.getConvertedAmount(finCcy, limitCcy, finMain.getOsPriBal());

		// update reserve and utilization
		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(limitDetailsList, details);

			// add to reserve
			limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(limitReserveAmt));

			//add to osPriBal
			limitToUpdate.setOsPriBal(limitToUpdate.getOsPriBal().add(osPriBal));

			// if records are not approved then we are considering reserve only
			if (finMain.isLimitValid()) {
				continue;
			}

			//not required in case of multiple disbursement with max disbursement check
			if (addTempblock) {
				// for Under process loans in addDisbursment
				LimitTransactionDetail transaction = getTransaction(finRef, inProgressHeaderID, -1);
				if (transaction != null) {
					// add to reserve
					limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(transaction.getLimitAmount()));
				}
			}

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
			if (!details.isRevolving() && StringUtils.isEmpty(details.getGroupCode())) {
				continue;
			}

			LimitDetails limitToUpdate = getLimitdetails(limitDetailsList, details);
			limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().subtract(repayLimit));
			if (FinanceConstants.PRODUCT_ODFACILITY.equals(finCategory)) {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(repayLimit));
			}
		}

		logger.debug(" Leaving ");
	}

	private LimitTransactionDetail getTransaction(String finRef, long headerid, int type) {
		return limitTransactionDetailsDAO.getTransaction(LimitConstants.FINANCE, finRef, LimitConstants.BLOCK, headerid,
				type);
	}

	/**
	 * 
	 * @param limitDetailsList
	 * @param limitHeader
	 * @throws DatatypeConfigurationException
	 */
	private boolean processStructuralChanges(List<LimitDetails> limitDetailsList, LimitHeader limitHeader) {
		logger.debug(" Entering ");
		boolean changed = false;
		List<LimitDetails> list = new ArrayList<LimitDetails>();
		List<LimitStructureDetail> structureList = limitStructureDetailDAO
				.getLimitStructureDetailById(limitHeader.getLimitStructureCode(), "");

		for (LimitStructureDetail limitStructureDetail : structureList) {
			if (!isExist(limitDetailsList, limitStructureDetail)) {
				changed = true;
				LimitDetails details = getLimitDetails(limitStructureDetail, limitHeader);
				list.add(details);
			}
		}
		// Add New Structural Changes
		if (!list.isEmpty()) {
			limitDetailDAO.saveList(list, "");
			limitDetailsList.addAll(list);
		}

		logger.debug(" Leaving ");
		return changed;
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
	private LimitDetails getLimitDetails(LimitStructureDetail limitStructureDetail, LimitHeader limitHeader) {

		LimitDetails limitDetail = new LimitDetails();

		String sqlRule = this.ruleDAO.getAmountRule(limitStructureDetail.getLimitLine(), RuleConstants.MODULE_LMTLINE,
				RuleConstants.EVENT_CUSTOMER);

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
		try {
			limitDetail.setCreatedOn(limitStructureDetail.getCreatedOn());
		} catch (DatatypeConfigurationException e) {
			logger.error("Exception", e);
		}
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
			limitDetail.setOsPriBal(BigDecimal.ZERO);
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
		LimitReferenceMapping mapping = new LimitReferenceMapping();
		mapping.setReferenceCode(LimitConstants.FINANCE);
		mapping.setReferenceNumber(finRef);
		mapping.setHeaderId(headerId);
		mapping.setNewRecord(true);

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
					mapping.setLimitLine(details.getLimitLine());
					uncalssifed = false;
					break;
				}
			}

			if (uncalssifed) {
				mapping.setLimitLine(LimitConstants.LIMIT_ITEM_UNCLSFD);
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
