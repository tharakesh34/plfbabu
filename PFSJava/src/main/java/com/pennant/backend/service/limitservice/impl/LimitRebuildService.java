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
 *
 * FileName : LimitRebuildService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 23-06-2017 *
 * 
 * Modified Date : 23-06-2017 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * * * * * * * * *
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;

public class LimitRebuildService implements LimitRebuild {
	private static Logger logger = LogManager.getLogger(LimitRebuildService.class);

	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private LimitDetailDAO limitDetailDAO;
	@Autowired
	private LimitHeaderDAO limitHeaderDAO;
	@Autowired
	private FinanceDisbursementDAO financeDisbursementDAO;
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

		Set<String> set = fieldMap.get("fm_");
		set.add("ClosingStatus");
		set.add("RecordType");

		List<FinanceMain> financeMains = new ArrayList<>();
		financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custID, set, false));
		financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custID, set, true));

		List<LimitReferenceMapping> mappings = new ArrayList<>();

		Map<String, FinanceType> finTypeMap = new HashMap<>();
		FinanceType financeType = null;
		LimitReferenceMapping mapping = null;
		for (FinanceMain finMain : financeMains) {
			String finType = finMain.getFinType();

			financeType = finTypeMap.computeIfAbsent(finType, ft -> getFinanceTye(finType, fieldMap.get("ft_")));
			mapping = identifyLine(finMain, financeType, customer, headerId, limitDetailsList);

			if (!(FinanceConstants.CLOSE_STATUS_CANCELLED.equals(finMain.getClosingStatus())
					|| PennantConstants.RCD_STATUS_CANCELLED.equals(finMain.getRecordStatus()))) {

				BigDecimal priBal = osPriBal.get(finMain.getFinReference());

				if (priBal == null) {
					priBal = BigDecimal.ZERO;
				}

				finMain.setOsPriBal(priBal);
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

		fm.add("finID");
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

		List<FinanceMain> financeMains = new ArrayList<>();
		List<LimitHeader> headers = new ArrayList<>();
		long custId;
		for (Customer customer : customers) {
			custId = customer.getCustID();
			financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custId, fieldMap.get("fm_"), false));
			financeMains.addAll(limitHeaderDAO.getLimitFieldsByCustId(custId, fieldMap.get("fm_"), true));

			Map<String, BigDecimal> osPriBal = limitDetailDAO.getOsPriBal(custId);
			for (FinanceMain fm : financeMains) {
				BigDecimal osPriBalance = osPriBal.get(fm.getFinReference());
				fm.setOsPriBal(osPriBalance == null ? BigDecimal.ZERO : osPriBalance);
			}

			LimitHeader limitHeaderByCustomerId = limitHeaderDAO.getLimitHeaderByCustomerId(custId, "");

			if (limitHeaderByCustomerId != null) {
				headers.add(limitHeaderByCustomerId);
			}
		}

		List<LimitReferenceMapping> mappings = new ArrayList<>();

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
				// process rebuild
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
		List<FinanceMain> financeMains = new ArrayList<>();
		List<LimitHeader> headers = new ArrayList<>();

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

		List<LimitReferenceMapping> mappings = new ArrayList<>();

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

	private void processRebuild(FinanceMain fm, LimitHeader lh, long inProgressHeaderID, List<LimitDetails> ldList,
			LimitReferenceMapping mapping) {

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		String finCategory = fm.getFinCategory();
		String finCcy = fm.getFinCcy();
		String limitCcy = lh.getLimitCcy();
		List<LimitDetails> list = getCustomerLimitDetails(mapping);
		String limitLine = mapping.getLimitLine();
		boolean addTempblock = false;

		// calculate reserve and utilized
		BigDecimal tranReseervAmt = fm.getFinAssetValue();

		if (fm.isLimitValid()) {
			// check the there is block in not then don not proceed
			LimitTransactionDetail transaction = getTransaction(finReference, inProgressHeaderID, 0);
			if (transaction == null) {
				// then no need block the amount
				return;
			}
		}

		BigDecimal tranUtilisedAmt = BigDecimal.ZERO;
		List<FinanceDisbursement> disbursementDetailList = financeDisbursementDAO.getFinanceDisbursementDetails(finID,
				"", false);

		for (FinanceDisbursement disbursement : disbursementDetailList) {
			if (StringUtils.equals(disbursement.getDisbStatus(), FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}
			tranUtilisedAmt = tranUtilisedAmt.add(disbursement.getDisbAmount()).add(disbursement.getFeeChargeAmt());
		}

		tranUtilisedAmt = tranUtilisedAmt.subtract(fm.getDownPayment());

		if (fm.getFinCurrAssetValue().compareTo(tranReseervAmt) == 0) {
			addTempblock = true;
		}

		// convert to limit currency
		BigDecimal limitReserveAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranReseervAmt);
		BigDecimal limitUtilisedAmt = CalculationUtil.getConvertedAmount(finCcy, limitCcy, tranUtilisedAmt);
		BigDecimal osPriBal = CalculationUtil.getConvertedAmount(finCcy, limitCcy, fm.getOsPriBal());

		// update reserve and utilization
		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(ldList, details);

			if (limitToUpdate == null) {
				continue;
			}

			// add to reserve
			limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(limitReserveAmt));

			// add to osPriBal
			limitToUpdate.setOsPriBal(limitToUpdate.getOsPriBal().add(osPriBal));

			// if records are not approved then we are considering reserve only
			if (fm.isLimitValid()) {
				continue;
			}

			// not required in case of multiple disbursement with max disbursement check
			if (addTempblock) {
				// for Under process loans in addDisbursment
				LimitTransactionDetail transaction = getTransaction(finReference, inProgressHeaderID, -1);
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

			limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().add(limitUtilisedAmt));

		}

		// if records are not approved then we are considering reserve only
		if (fm.isLimitValid()) {
			return;
		}

		// update revolving nature by payments made
		BigDecimal repay = financeScheduleDetailDAO.getPriPaidAmount(finID);
		BigDecimal repayLimit = CalculationUtil.getConvertedAmount(finCcy, limitCcy, repay);

		for (LimitDetails details : list) {
			LimitDetails limitToUpdate = getLimitdetails(ldList, details);

			if (limitToUpdate == null) {
				continue;
			}

			if (isRevolving(limitLine, list)) {
				limitToUpdate.setUtilisedLimit(limitToUpdate.getUtilisedLimit().subtract(repayLimit));
			} else {
				limitToUpdate.setNonRvlUtilised(limitToUpdate.getNonRvlUtilised().subtract(repayLimit));
			}

			if (FinanceConstants.PRODUCT_ODFACILITY.equals(finCategory)) {
				limitToUpdate.setReservedLimit(limitToUpdate.getReservedLimit().add(repayLimit));
			}

		}

	}

	private boolean isRevolving(String limitLine, List<LimitDetails> limitDetails) {
		for (LimitDetails limitDetail : limitDetails) {
			if (StringUtils.equals(limitDetail.getLimitLine(), limitLine)) {
				return limitDetail.isRevolving();
			}
		}

		return false;
	}

	private LimitTransactionDetail getTransaction(String finReference, long headerid, int type) {
		return limitTransactionDetailsDAO.getTransaction(LimitConstants.FINANCE, finReference, LimitConstants.BLOCK,
				headerid, type);
	}

	private boolean processStructuralChanges(List<LimitDetails> limitDetailsList, LimitHeader limitHeader) {
		boolean changed = false;
		List<LimitDetails> list = new ArrayList<>();

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

		return changed;
	}

	private boolean isExist(List<LimitDetails> limitDetailsList, LimitStructureDetail limitStructureDetail) {
		for (LimitDetails limitDetails : limitDetailsList) {
			if (limitDetails.getLimitStructureDetailsID() == limitStructureDetail.getLimitStructureDetailsID()) {
				return true;
			}
		}
		return false;
	}

	private LimitDetails getLimitDetails(LimitStructureDetail limitStructureDetail, LimitHeader limitHeader) {
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

	private void resetLimitDetails(List<LimitDetails> limitDetailsList) {
		for (LimitDetails limitDetail : limitDetailsList) {
			limitDetail.setReservedLimit(BigDecimal.ZERO);
			limitDetail.setUtilisedLimit(BigDecimal.ZERO);
			limitDetail.setOsPriBal(BigDecimal.ZERO);
		}
	}

	private LimitDetails getLimitdetails(List<LimitDetails> list, LimitDetails limitofind) {
		for (LimitDetails limitDetail : list) {
			if (limitofind.getDetailId() == limitDetail.getDetailId()) {
				return limitDetail;
			}

		}
		return null;
	}

	private LimitReferenceMapping identifyLine(FinanceMain finMain, FinanceType financeType, Customer customer,
			long headerId, List<LimitDetails> limitDetailsList) {
		logger.debug(" Entering ");

		String finRef = finMain.getFinReference();
		LimitReferenceMapping mapping = new LimitReferenceMapping();
		mapping.setReferenceCode(LimitConstants.FINANCE);
		mapping.setReferenceNumber(finRef);
		mapping.setHeaderId(headerId);
		mapping.setNewRecord(true);

		Map<String, Object> dataMap = getDataMap(finMain, financeType, customer);

		if (CollectionUtils.isEmpty(limitDetailsList)) {
			return mapping;
		}

		boolean uncalssifed = true;
		for (LimitDetails details : limitDetailsList) {
			if (StringUtils.isBlank(details.getSqlRule())) {
				continue;
			}

			boolean ruleResult = (boolean) RuleExecutionUtil.executeRule(details.getSqlRule(), dataMap, "",
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

		// Return the Limit items
		logger.debug(" Leaving ");
		return mapping;
	}

	private Map<String, Object> getDataMap(FinanceMain financeMain, FinanceType financeType, Customer customer) {

		Map<String, Object> dataMap = new HashMap<>();
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

	private List<LimitDetails> getCustomerLimitDetails(LimitReferenceMapping mapping) {
		logger.debug(Literal.ENTERING);

		long headerId = mapping.getHeaderId();
		String limitLine = mapping.getLimitLine();
		List<String> groupCodes = new ArrayList<>();
		groupCodes.add(LimitConstants.LIMIT_ITEM_TOTAL);

		if (!LimitConstants.LIMIT_ITEM_UNCLSFD.equals(limitLine)) {
			String groupCode = limitGroupLinesDAO.getGroupByLineAndHeader(limitLine, headerId);
			String parentGroup = groupCode;

			while (!StringUtils.isEmpty(parentGroup)) {
				groupCodes.add(parentGroup);
				parentGroup = limitGroupLinesDAO.getGroupByGroupAndHeader(parentGroup, headerId);
			}
		}

		List<LimitDetails> list = limitDetailDAO.getLimitByLineAndgroup(headerId, limitLine, groupCodes);

		logger.debug(Literal.LEAVING);
		return list;
	}
}
