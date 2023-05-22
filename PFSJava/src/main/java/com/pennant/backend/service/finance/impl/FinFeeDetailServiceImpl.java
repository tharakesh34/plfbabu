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
 * * FileName : FinFeeDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinFeeDetailServiceImpl extends GenericService<FinFeeDetail> implements FinFeeDetailService {
	private static final Logger logger = LogManager.getLogger(FinFeeDetailServiceImpl.class);

	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private BranchDAO branchDAO;
	private ProvinceDAO provinceDAO;
	private TaxHeaderDetailsService taxHeaderDetailsService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;

	public FinFeeDetailServiceImpl() {
		super();
	}

	@Override
	public List<FinReceiptHeader> getUpfrontReceipts(long finID, String reference) {
		logger.debug(Literal.ENTERING);

		List<FinReceiptHeader> headers = new ArrayList<>();

		List<FinReceiptHeader> list = finReceiptDetailDAO.getReceiptsForDuplicateCheck(finID, reference);

		list.forEach(rch -> {
			if ("FeePayment".equals(rch.getReceiptPurpose()) && !"C".equals(rch.getReceiptModeStatus())) {
				headers.add(rch);
			}
		});

		logger.debug(Literal.LEAVING);
		return headers;
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptsById(List<Long> feeIds, String type) {
		return finFeeReceiptDAO.getFinFeeReceiptByFinRef(feeIds, type);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String offerID, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> feeList = finFeeDetailDAO.getFinFeeDetailByFinRef(offerID, isWIF, type);

		setFeeSchedulesAndTaxGeader(feeList, isWIF, type);

		logger.debug(Literal.LEAVING);
		return feeList;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(long finID, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> feeList = finFeeDetailDAO.getFinFeeDetailByFinRef(finID, isWIF, type);

		setFeeSchedulesAndTaxGeader(feeList, isWIF, type);

		logger.debug(Literal.LEAVING);
		return feeList;
	}

	private void setFeeSchedulesAndTaxGeader(List<FinFeeDetail> feeList, boolean isWIF, String type) {
		for (FinFeeDetail fee : feeList) {
			String feeScheduleMethod = fee.getFeeScheduleMethod();
			if (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
					|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
					|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)) {

				long feeID = fee.getFeeID();
				fee.setFinFeeScheduleDetailList(finFeeScheduleDetailDAO.getFeeScheduleByFeeID(feeID, isWIF, type));
			}

			Long taxHeaderId = fee.getTaxHeaderId();
			if (fee.isTaxApplicable() && (taxHeaderId != null && taxHeaderId > 0)) {
				fee.setTaxHeader(taxHeaderDetailsService.getTaxHeaderById(taxHeaderId, type));
			}
		}
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(long finID, boolean isWIF, String type, String eventCodeRef) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> feeList = finFeeDetailDAO.getFinFeeDetailByFinRef(finID, isWIF, type, eventCodeRef);

		for (FinFeeDetail fee : feeList) {
			fee.setFinFeeScheduleDetailList(finFeeScheduleDetailDAO.getFeeScheduleByFeeID(fee.getFeeID(), isWIF, type));

			Long taxHeaderId = fee.getTaxHeaderId();
			if (taxHeaderId != null && taxHeaderId > 0) {
				TaxHeader taxheader = taxHeaderDetailsService.getTaxHeaderById(taxHeaderId, type);
				fee.setTaxHeader(taxheader);
			}
		}

		logger.debug(Literal.LEAVING);
		return feeList;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailsByReferenceId(long referenceId, String eventCodeRef, String type) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> feeList = finFeeDetailDAO.getFinFeeDetailByReferenceId(referenceId, eventCodeRef, type);

		for (FinFeeDetail fee : feeList) {
			fee.setFinFeeScheduleDetailList(finFeeScheduleDetailDAO.getFeeScheduleByFeeID(fee.getFeeID(), false, type));

			Long taxHeaderId = fee.getTaxHeaderId();
			if (taxHeaderId != null && taxHeaderId > 0) {
				TaxHeader taxheader = taxHeaderDetailsService.getTaxHeaderById(taxHeaderId, type);
				fee.setTaxHeader(taxheader);
			}
		}

		logger.debug(Literal.LEAVING);

		return feeList;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinFeeDetail> feeList, String tableType, String auditTranType,
			boolean isWIF) {
		logger.debug(Literal.ENTERING);

		for (FinFeeDetail fee : feeList) {
			if (fee.getTaxHeaderId() == null) {
				fee.setTaxHeaderId(0L);
			}

			TaxHeader taxHeader = fee.getTaxHeader();
			if (taxHeader != null && (fee.isNewRecord()
					|| (!fee.isNewRecord() && (fee.getTaxHeaderId() != null && fee.getTaxHeaderId() > 0)))) {
				taxHeader.setRecordType(fee.getRecordType());
				taxHeader.setNewRecord(fee.isNewRecord());
				taxHeader.setLastMntBy(fee.getLastMntBy());
				taxHeader.setLastMntOn(fee.getLastMntOn());
				taxHeader.setRecordStatus(fee.getRecordStatus());

				Long taxHeaderId = fee.getTaxHeaderId();
				if (taxHeaderId != null && taxHeaderId > 0) {
					taxHeader.setHeaderId(taxHeaderId);
				}

				if (fee.isTaxApplicable() && !isWIF) {
					TaxHeader txHeader = taxHeaderDetailsService.saveOrUpdate(taxHeader, tableType, auditTranType);
					fee.setTaxHeaderId(txHeader.getHeaderId());
				}
			}
		}
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processFinFeeDetails(feeList, tableType, auditTranType, false, isWIF));

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public List<AuditDetail> saveOrUpdateFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		auditDetails.addAll(processFinFeeReceipts(finFeeReceipts, tableType, auditTranType, false));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processFinFeeDetails(List<FinFeeDetail> fees, String tableType, String auditTranType,
			boolean isApproveRcd, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(fees)) {
			return auditDetails;
		}

		int i = 0;
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (FinFeeDetail fee : fees) {
			if (!isApproveRcd && (fee.isRcdVisible() && !fee.isDataModified())) {
				continue;
			}

			if (StringUtils.equals(fee.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = isApproveRcd;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(tableType)
					|| StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
				approveRec = true;
				fee.setRoleCode("");
				fee.setNextRoleCode("");
				fee.setTaskId("");
				fee.setNextTaskId("");
				fee.setWorkflowId(0);
			}

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(fee.getRecordType())) {
				deleteRecord = true;
			} else if (fee.isNewRecord()) {
				saveRecord = true;
				if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					fee.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					fee.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					fee.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (fee.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = fee.getRecordType();
				recordStatus = fee.getRecordStatus();
				fee.setRecordType("");
				fee.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				if (fee.isNewRecord() && !approveRec) {
					fee.setFeeSeq(finFeeDetailDAO.getFeeSeq(fee, isWIF, tableType) + 1);
				}

				if ((fee.isAlwPreIncomization()) && (fee.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
					List<FinFeeDetail> finFeeDtls = finFeeDetailDAO.getFeeDetailByExtReference(fee.getTransactionId(),
							fee.getFeeTypeID(), "");

					for (FinFeeDetail feeDtl : finFeeDtls) {
						fee.setFeeID(feeDtl.getFeeID());
						fee.setFeeSeq(feeDtl.getFeeSeq());
						fee.setPaidAmount(feeDtl.getPaidAmount());
						finFeeDetailDAO.update(fee, false, "");
					}
				} else {
					fee.setFeeID(finFeeDetailDAO.save(fee, isWIF, tableType));
				}

				if (CollectionUtils.isNotEmpty(fee.getFinFeeScheduleDetailList())) {
					for (FinFeeScheduleDetail finFeeSchDetail : fee.getFinFeeScheduleDetailList()) {
						finFeeSchDetail.setFeeID(fee.getFeeID());
					}
					finFeeScheduleDetailDAO.saveFeeScheduleBatch(fee.getFinFeeScheduleDetailList(), isWIF, tableType);
				}
			}

			if (updateRecord) {
				finFeeDetailDAO.update(fee, isWIF, tableType);

				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(fee.getFeeID(), isWIF, tableType);
				if (CollectionUtils.isNotEmpty(fee.getFinFeeScheduleDetailList())) {
					for (FinFeeScheduleDetail finFeeSchDetail : fee.getFinFeeScheduleDetailList()) {
						finFeeSchDetail.setFeeID(fee.getFeeID());
					}
					finFeeScheduleDetailDAO.saveFeeScheduleBatch(fee.getFinFeeScheduleDetailList(), isWIF, tableType);
				}
			}

			if (deleteRecord) {
				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(fee.getFeeID(), isWIF, tableType);
				finFeeDetailDAO.delete(fee, isWIF, tableType);
			}

			if (approveRec) {
				fee.setRecordType(rcdType);
				fee.setRecordStatus(recordStatus);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(fee, fee.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], fee.getBefImage(), fee));
			i++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> processFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType, boolean isApproveRcd) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(finFeeReceipts)) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";

				if (StringUtils.isEmpty(tableType)
						|| StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finFeeReceipt.setRoleCode("");
					finFeeReceipt.setNextRoleCode("");
					finFeeReceipt.setTaskId("");
					finFeeReceipt.setNextTaskId("");
				}

				if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finFeeReceipt.isNewRecord()) {
					saveRecord = true;
					if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finFeeReceipt.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = finFeeReceipt.getRecordType();
					recordStatus = finFeeReceipt.getRecordStatus();
					finFeeReceipt.setRecordType("");
					finFeeReceipt.setWorkflowId(0);
					finFeeReceipt.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				if (saveRecord) {
					finFeeReceipt.setId(finFeeReceiptDAO.save(finFeeReceipt, tableType));
				}

				if (updateRecord) {
					finFeeReceiptDAO.update(finFeeReceipt, tableType);
				}

				if (deleteRecord) {
					finFeeReceiptDAO.delete(finFeeReceipt, tableType);
				}

				if (approveRec) {
					finFeeReceipt.setRecordType(rcdType);
					finFeeReceipt.setRecordStatus(recordStatus);
				}

				String[] fields = PennantJavaUtil.getFieldDetails(finFeeReceipt, finFeeReceipt.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						finFeeReceipt.getBefImage(), finFeeReceipt));
				i++;
			}
		}
		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,
			boolean isWIF) {
		logger.debug(Literal.ENTERING);

		for (FinFeeDetail finFeeDetail : finFeeDetails) {

			if (CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(finFeeDetail.getFeeScheduleMethod())) {
				finFeeDetail.setPaidAmount(finFeeDetail.getPaidAmount().add(finFeeDetail.getRemainingFee()));
				finFeeDetail.setPaidAmountOriginal(
						finFeeDetail.getPaidAmountOriginal().add(finFeeDetail.getRemainingFeeOriginal()));
				finFeeDetail.setPaidAmountGST(finFeeDetail.getPaidAmountGST().add(finFeeDetail.getRemainingFeeGST()));
				finFeeDetail.setPaidTDS(finFeeDetail.getPaidTDS().add(finFeeDetail.getRemTDS()));

				finFeeDetail.setRemainingFee(BigDecimal.ZERO);
				finFeeDetail.setRemainingFeeOriginal(BigDecimal.ZERO);
				finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
				finFeeDetail.setRemTDS(BigDecimal.ZERO);
			}

			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && (finFeeDetail.isNewRecord() || (!finFeeDetail.isNewRecord()
					&& (finFeeDetail.getTaxHeaderId() != null && finFeeDetail.getTaxHeaderId() > 0)))) {
				taxHeader.setRecordType(finFeeDetail.getRecordType());
				taxHeader.setNewRecord(finFeeDetail.isNewRecord());
				taxHeader.setRecordStatus(finFeeDetail.getRecordStatus());
				taxHeader.setLastMntBy(finFeeDetail.getLastMntBy());
				taxHeader.setLastMntOn(finFeeDetail.getLastMntOn());
				TaxHeader txHeader = taxHeaderDetailsService.doApprove(taxHeader, tableType, auditTranType);
				finFeeDetail.setTaxHeaderId(txHeader.getHeaderId());
			}
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, true, isWIF));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public Map<Long, List<FinFeeReceipt>> getUpfromtReceiptMap(List<FinFeeReceipt> feeReceipts) {
		Map<Long, List<FinFeeReceipt>> map = new HashMap<>();

		for (FinFeeReceipt feeReceipt : feeReceipts) {
			List<FinFeeReceipt> finFeeRecList = null;
			if (map.containsKey(feeReceipt.getReceiptID())) {
				finFeeRecList = map.get(feeReceipt.getReceiptID());
			} else {
				finFeeRecList = new ArrayList<>();
			}
			finFeeRecList.add(feeReceipt);
			map.put(feeReceipt.getReceiptID(), finFeeRecList);
		}

		return map;
	}

	@Override
	public BigDecimal getExcessAmount(long finID, Map<Long, List<FinFeeReceipt>> map, long custId) {
		List<FinReceiptHeader> headers = getUpfrontReceipts(finID, String.valueOf(custId));

		BigDecimal excessAmount = BigDecimal.ZERO;
		for (FinReceiptHeader rch : headers) {
			long receiptID = rch.getReceiptID();
			if (map.containsKey(receiptID)) {
				List<FinFeeReceipt> finFeeReceiptList = map.get(receiptID);
				BigDecimal feePaidAmount = BigDecimal.ZERO;
				for (FinFeeReceipt feeReceipt : finFeeReceiptList) {
					BigDecimal paidAmount = feeReceipt.getPaidAmount();
					feePaidAmount = feePaidAmount.add(paidAmount);
				}
				excessAmount = excessAmount.add(rch.getReceiptAmount().subtract(feePaidAmount));
			} else {
				excessAmount = excessAmount.add(rch.getReceiptAmount());
			}
		}
		return excessAmount;
	}

	@Override
	public List<AuditDetail> delete(List<FinFeeDetail> feeList, String tableType, String auditTranType, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(feeList)) {
			logger.debug(Literal.LEAVING);
			return auditDetails;
		}

		int auditSeq = 1;
		for (FinFeeDetail fee : feeList) {
			finFeeScheduleDetailDAO.deleteFeeScheduleBatch(fee.getFeeID(), isWIF, tableType);
			finFeeDetailDAO.delete(fee, isWIF, tableType);

			if (fee.getTaxHeaderId() != null && fee.getTaxHeaderId() > 0) {
				taxHeaderDetailsService.delete(fee.getTaxHeaderId(), tableType);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(fee, fee.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1], fee.getBefImage(), fee));
			auditSeq++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> deleteFinFeeReceipts(List<FinFeeReceipt> feeReceipts, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isEmpty(feeReceipts)) {
			return auditDetails;
		}

		int auditSeq = 1;
		for (FinFeeReceipt feeReceipt : feeReceipts) {
			finFeeReceiptDAO.delete(feeReceipt, tableType);

			String[] fields = PennantJavaUtil.getFieldDetails(feeReceipt, feeReceipt.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1], feeReceipt.getBefImage(),
					feeReceipt));
			auditSeq++;
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getFinFeeDetailAuditDetail(List<FinFeeDetail> finFeeDetails, String auditTranType,
			String method, long workFlowId) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		for (FinFeeDetail finFeeDetail : finFeeDetails) {

			if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finFeeDetail.getRecordStatus())
					.equals(PennantConstants.RCD_STATUS_SAVED)) {
				finFeeDetail.setWorkflowId(0);

				if (PennantConstants.RECORD_TYPE_NEW.equals(finFeeDetail.getRecordType())) {
					finFeeDetail.setNewRecord(true);
				}
			} else {
				finFeeDetail.setWorkflowId(workFlowId);
			}

			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(finFeeDetail.getRecordType())) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finFeeDetail.getRecordType())) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finFeeDetail.getRecordType())) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFeeDetail.setNewRecord(true);
				if (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent())
						&& FinServiceEvent.ORG.equals(finFeeDetail.getModuleDefiner())
						&& PennantConstants.RECORD_TYPE_UPD.equals(finFeeDetail.getRecordType())) {
					if (finFeeDetailDAO.isFinFeeDetailExists(finFeeDetail, TableType.TEMP_TAB.getSuffix())) {
						finFeeDetail.setNewRecord(false);
					} else {
						finFeeDetail.setNewRecord(true);
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					}

				}
			} else if ("doApprove".equals(method) && (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent()))) {
				if (!isRcdType || PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(finFeeDetail.getRecordType())) {
					finFeeDetail.setNewRecord(false);
					if (!finFeeDetailDAO.isFinFeeDetailExists(finFeeDetail, TableType.MAIN_TAB.getSuffix())) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					}
				}
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(finFeeDetail.getRecordType())) {
					finFeeDetail.setNewRecord(true);
				}
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(finFeeDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(finFeeDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finFeeDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			String[] fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
			if (StringUtils.isNotEmpty(finFeeDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finFeeDetail.getBefImage(), finFeeDetail));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> validateFinFeeReceipts(FinanceDetail financeDetail, long workflowId, String method,
			String auditTranType, String usrLanguage, List<AuditDetail> auditDetails) {
		return doValidationFinFeeReceipts(financeDetail, workflowId, method, auditTranType, usrLanguage, auditDetails);
	}

	@Override
	public List<AuditDetail> validate(List<FinFeeDetail> finFeeDetails, long workflowId, String method,
			String auditTranType, String usrLanguage, boolean isWIF) {
		return doValidation(finFeeDetails, workflowId, method, auditTranType, usrLanguage, isWIF);
	}

	private List<AuditDetail> doValidation(List<FinFeeDetail> finFeeDetails, long workflowId, String method,
			String auditTranType, String usrLanguage, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			List<AuditDetail> finFeeAuditDetails = getFinFeeDetailAuditDetail(finFeeDetails, auditTranType, method,
					workflowId);
			for (AuditDetail auditDetail : finFeeAuditDetails) {
				validateFinFeeDetail(auditDetail, method, usrLanguage, isWIF);
			}
			auditDetails.addAll(finFeeAuditDetails);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> doValidationFinFeeReceipts(FinanceDetail fd, long workflowId, String method,
			String auditTranType, String usrLanguage, List<AuditDetail> auditDetails) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> finFeeAuditDetails = new ArrayList<>();
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinFeeReceipt> feeReceipts = schdData.getFinFeeReceipts();

		AuditDetail auditDetail = new AuditDetail();

		// auditDetails.addAll(finFeeAuditDetails);
		BigDecimal totalFee = BigDecimal.ZERO;
		boolean error = false;

		if (!fd.isActionSave() && fd.isUpFrentFee()) {
			List<FinFeeDetail> feeList = schdData.getFinFeeDetailList();
			// In Case Of IMD is inProgress
			List<Long> feeIds = new ArrayList<Long>(1);

			for (FinFeeDetail fee : feeList) {
				BigDecimal totalPaidAmount = BigDecimal.ZERO;
				feeIds.add(fee.getFeeID());

				for (FinFeeReceipt finFeeReceipt : feeReceipts) {

					if (!StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, finFeeReceipt.getRecordType())) {
						if (fee.getFeeTypeID() == 0) {
							if (StringUtils.equals(finFeeReceipt.getFeeTypeCode(), fee.getVasReference())) {
								totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
							}
						} else {
							if (fee.getFeeTypeID() == finFeeReceipt.getFeeTypeId()) {
								totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
							}
						}
					}
				}

				if (fee.getPaidAmount().compareTo(BigDecimal.ZERO) != 0) {
					String feeTypeDesc = fee.getFeeTypeDesc();
					if (StringUtils.isBlank(feeTypeDesc)) {
						feeTypeDesc = fee.getVasReference();
					}

					if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
						String[] errParm = new String[1];
						String[] valueParm = new String[1];
						valueParm[0] = feeTypeDesc;
						errParm[0] = valueParm[0];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65019", errParm, valueParm), usrLanguage));
						auditDetails.add(auditDetail);
						error = true;
						break;
					} else if (fee.getPaidAmount().compareTo(totalPaidAmount) != 0) {
						String[] errParm = new String[2];
						String[] valueParm = new String[1];
						valueParm[0] = feeTypeDesc;
						errParm[0] = ":" + valueParm[0];
						errParm[1] = ":" + valueParm[0];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65018", errParm, valueParm), usrLanguage));
						auditDetails.add(auditDetail);
						error = true;
						break;
					}
					totalFee = totalFee.add(fee.getPaidAmount());

				}
			}

			// In Case Of IMD is inProgress
			if (!feeIds.isEmpty()) {
				ErrorDetail errorDetail = validateUpfrontFees(feeIds);
				if (errorDetail != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(errorDetail, usrLanguage));
					auditDetails.add(auditDetail);
					error = true;
				}
			}
		}

		if ("doReject".equalsIgnoreCase(method) || "doApprove".equalsIgnoreCase(method)) {
			List<FinFeeDetail> feeDetails = schdData.getFinFeeDetailList();
			BigDecimal totalPaidFee = BigDecimal.ZERO;
			List<Long> feeIds = new ArrayList<Long>(1);

			for (FinFeeDetail finFeeDetail : feeDetails) {
				// If we Select VAS Fee Payment Mode as Cash or Cheque getting error "Fees not paid"
				if (!AccountingEvent.VAS_FEE.equalsIgnoreCase(finFeeDetail.getFinEvent())) {
					feeIds.add(finFeeDetail.getFeeID());
					if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) != 0) {
						totalPaidFee = totalPaidFee.add(finFeeDetail.getPaidAmount());
					}
				}
			}

			ErrorDetail errorDetail = validateUpfrontFees(feeIds);

			if (errorDetail != null) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(errorDetail, usrLanguage));
				auditDetails.add(auditDetail);
			}

			BigDecimal totalPaidAmt = finReceiptDetailDAO
					.getFinReceiptDetailsByFinRef(fd.getFinScheduleData().getFinanceMain().getFinReference());

			// BUF FIX 136896
			if (totalPaidAmt.compareTo(totalPaidFee) < 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "FUF001", null, null), usrLanguage));
				auditDetails.add(auditDetail);
			}
		}

		if (!fd.isActionSave() && (fd.isUpFrentFee() || "doApprove".equalsIgnoreCase(method)) && !error) {
			BigDecimal totalPaidAmt = finReceiptDetailDAO
					.getFinReceiptDetailsByFinRef(fd.getFinScheduleData().getFinanceMain().getFinReference());
			// BUF FIX 136896
			if (totalPaidAmt.compareTo(totalFee) < 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "FUF001", null, null), usrLanguage));
				auditDetails.add(auditDetail);
			}
		}

		logger.debug(Literal.LEAVING);

		return finFeeAuditDetails;
	}

	public ErrorDetail validateUpfrontFees(List<Long> feeIds) {
		if (!feeIds.isEmpty()) {
			List<FinFeeReceipt> feeReceiptsList = getFinFeeReceiptsById(feeIds, "_Temp");
			if (CollectionUtils.isNotEmpty(feeReceiptsList)) {
				// error
				return new ErrorDetail(PennantConstants.KEY_FIELD, "IMD001", null, null);
			}
		}
		return null;
	}

	private AuditDetail validateFinFeeDetail(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isWIF) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinFeeDetail fee = (FinFeeDetail) auditDetail.getModelData();
		FinFeeDetail tempFinFinDetail = null;
		if (fee.isWorkflow()) {
			tempFinFinDetail = finFeeDetailDAO.getFinFeeDetailById(fee, isWIF, "_Temp");
		}
		FinFeeDetail befFinFeeDetail = finFeeDetailDAO.getFinFeeDetailById(fee, isWIF, "");
		FinFeeDetail oldFinFeeDetail = fee.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(fee.getFeeTypeDesc());
		errParm[0] = PennantJavaUtil.getLabel("FeeType") + ":" + valueParm[0];

		if (fee.isNewRecord()) { // for New record or new record into work
			// flow

			if (!fee.isWorkflow()) {// With out Work flow only new
									// records
				if (befFinFeeDetail != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (fee.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																					// records
																					// type
																					// is
																					// new
					if (befFinFeeDetail != null || tempFinFinDetail != null) { // if
																				// records
																				// already
																				// exists
																				// in
																				// the
																				// main
																				// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinFeeDetail == null || tempFinFinDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!fee.isWorkflow()) { // With out Work flow for update
										// and delete

				if (befFinFeeDetail == null) { // if records not exists in the
												// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinFeeDetail != null
							&& !oldFinFeeDetail.getLastMntOn().equals(befFinFeeDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempFinFinDetail == null) { // if records not exists in the
												// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinFinDetail != null && oldFinFeeDetail != null
						&& !oldFinFeeDetail.getLastMntOn().equals(tempFinFinDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		// Waiver should not allow for Inclusive Fees
		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(fee.getTaxComponent())
				&& fee.getWaivedAmount().compareTo(BigDecimal.ZERO) != 0
				&& !PennantConstants.method_doReject.equals(method)
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(fee.getRecordStatus())) {
			String[] valueParam = new String[1];
			valueParam[0] = fee.getFeeTypeCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91137", valueParam)));
		}

		// Fee scheduled methods (Include to First Installment, entire tenor, N
		// installments, Paid by customer and Waived by bank) should not allowed
		// if Tax applicable
		String feeScheduleMethod = fee.getFeeScheduleMethod();
		if (fee.isTaxApplicable() && (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
				|| CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(feeScheduleMethod))) {
			String[] valueParam = new String[2];
			valueParam[0] = feeScheduleMethod;
			valueParam[1] = fee.getFeeTypeCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91139", valueParam)));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !fee.isWorkflow()) {
			auditDetail.setBefImage(befFinFeeDetail);
		}
		return auditDetail;
	}

	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {
		this.finFeeDetailDAO.updateTaxPercent(taxPercent);
	}

	@Override
	public void convertGSTFinTypeFees(FinFeeDetail fee, FinTypeFees finTypeFee, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages) {
		String taxComponent = finTypeFee.getTaxComponent();
		BigDecimal taxableAmount = finTypeFee.getAmount();
		BigDecimal waivedAmount = fee.getWaivedAmount();

		BigDecimal totalGST;

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();

			fee.setActualAmountOriginal(finTypeFee.getAmount());
			fee.setActualAmountGST(totalGST);
			fee.setActualAmount(totalGST.add(finTypeFee.getAmount()));
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			fee.setNetAmount(finTypeFee.getAmount());
			fee.setNetAmountGST(totalGST);
			fee.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			fee.setActualAmountOriginal(taxableAmount.subtract(totalGST));

			fee.setActualAmountGST(totalGST);
			fee.setActualAmount(taxableAmount);

		}
	}

	@Override
	public void convertGSTFinFeeConfig(FinFeeDetail fee, FinFeeConfig finFeeConfig, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);

		String taxComponent = finFeeConfig.getTaxComponent();
		BigDecimal taxableAmount = finFeeConfig.getAmount();
		BigDecimal waivedAmount = fee.getWaivedAmount();

		BigDecimal totalGST;
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();

			fee.setActualAmountOriginal(finFeeConfig.getAmount());
			fee.setActualAmountGST(totalGST);
			fee.setActualAmount(totalGST.add(finFeeConfig.getAmount()));

			if (StringUtils.equals(finFeeConfig.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				fee.setPaidAmount(finFeeConfig.getAmount().add(totalGST));
				fee.setPaidAmountOriginal(finFeeConfig.getAmount());
				fee.setPaidAmountGST(totalGST);
			}

		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			fee.setNetAmount(finFeeConfig.getAmount());
			fee.setNetAmountGST(totalGST);
			fee.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			fee.setActualAmountOriginal(taxableAmount.subtract(totalGST));

			fee.setActualAmountGST(totalGST);
			fee.setActualAmount(taxableAmount);

			if (StringUtils.equals(finFeeConfig.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				fee.setPaidAmount(finFeeConfig.getAmount());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void processGSTCalForRule(FinFeeDetail fee, BigDecimal taxableAmount, FinanceDetail fd,
			Map<String, BigDecimal> taxPercentages, boolean apiRequest) {

		String taxComponent = fee.getTaxComponent();
		BigDecimal waivedAmount = fee.getWaivedAmount();

		BigDecimal totalGST;
		if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, taxComponent)) {
			if ((!apiRequest && !fee.isFeeModified()) || !fee.isAlwModifyFee()) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				fee.setActualAmountOriginal(taxableAmount);
				fee.setActualAmountGST(totalGST);
				fee.setActualAmount(totalGST.add(taxableAmount));
			}

			if (apiRequest) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				fee.setActualAmountOriginal(taxableAmount);
				fee.setActualAmountGST(totalGST);
				fee.setActualAmount(totalGST.add(taxableAmount));
			}

		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			fee.setNetAmount(taxableAmount);
			fee.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			fee.setNetAmountGST(totalGST);
			fee.setActualAmountOriginal(totalGST.add(fee.getWaivedAmount()));

			fee.setActualAmountGST(totalGST);
			fee.setActualAmount(fee.getActualAmountOriginal().add(totalGST));
		}

	}

	@Override
	public void processGSTCalForPercentage(FinFeeDetail fee, BigDecimal taxableAmount, FinanceDetail fd,
			Map<String, BigDecimal> taxPercentages, boolean apiRequest) {
		logger.debug(Literal.ENTERING);

		// 27-08-19: Fee Defaults setting when comes from API
		BigDecimal totalGST;
		if ((!apiRequest && fee.isFeeModified()) || (apiRequest && fee.isAlwModifyFee())) {
			if (apiRequest) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				fee.setActualAmountOriginal(taxableAmount);
				fee.setActualAmountGST(totalGST);
				fee.setActualAmount(totalGST.add(taxableAmount));
			}
			return;
		}

		String taxComponent = fee.getTaxComponent();
		BigDecimal waivedAmount = fee.getWaivedAmount();

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			if ((!apiRequest && !fee.isFeeModified()) || !fee.isAlwModifyFee()) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				fee.setActualAmountOriginal(taxableAmount);
				fee.setActualAmountGST(totalGST);
				fee.setActualAmount(totalGST.add(taxableAmount));
			}

		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			if (fee.getNetTDS().compareTo(BigDecimal.ZERO) == 0) {
				fee.setNetAmount(taxableAmount);
			}
			fee.setNetAmountGST(totalGST);
			fee.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			fee.setActualAmountOriginal(fee.getNetAmountOriginal().add(fee.getWaivedAmount()));

			fee.setActualAmountGST(totalGST);
			fee.setActualAmount(fee.getActualAmountOriginal().add(totalGST));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	@Override
	public BigDecimal getFeeResult(String sqlRule, Map<String, Object> executionMap, String finCcy) {
		logger.debug(Literal.ENTERING);

		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = RuleExecutionUtil.executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		logger.debug(Literal.LEAVING);

		return result;
	}

	@Override
	public void calculateFees(FinFeeDetail fee, FinScheduleData schdData, Map<String, BigDecimal> taxPercentages) {
		FinanceMain fm = schdData.getFinanceMain();
		String finEvent = fee.getFinEvent();

		if (AccountingEvent.ADDDBSP.equals(finEvent)) {
			AdvancePaymentUtil.calculateLOSAdvPayment(schdData, fee);
		} else if (AccountingEvent.ADDDBSN.equals(finEvent)) {
			AdvanceRuleCode advanceRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());
			if (advanceRule != null
					&& (advanceRule == AdvanceRuleCode.ADVINT || advanceRule == AdvanceRuleCode.ADVEMI)) {
				List<String> list = new ArrayList<>();
				list.add(AccountingEvent.ADDDBSP);
				list.add(AccountingEvent.ADDDBSN);
				List<FinFeeDetail> fees = finFeeDetailDAO.getFeeDetails(fm.getFinID(), advanceRule.name(), list);
				AdvancePaymentUtil.calculateLMSAdvPayment(schdData, fee, fees);
			}
		}

		calculateFees(fee, fm, taxPercentages);
	}

	@Override
	public void calculateFees(FinFeeDetail fee, FinanceMain fm, Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);

		BigDecimal waivedAmount = fee.getWaivedAmount();
		BigDecimal netAmountOriginal = fee.getActualAmountOriginal().subtract(waivedAmount);
		BigDecimal paidAmountOriginal = fee.getPaidAmountOriginal();

		// GST Calculations
		TaxHeader taxHeader = fee.getTaxHeader();
		Taxes cgstTax = null;
		Taxes sgstTax = null;
		Taxes igstTax = null;
		Taxes ugstTax = null;
		Taxes cessTax = null;

		if (taxHeader == null) {
			taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			fee.setTaxHeader(taxHeader);
		}

		List<Taxes> taxDetails = taxHeader.getTaxDetails();

		if (CollectionUtils.isNotEmpty(taxDetails)) {
			for (Taxes taxes : taxDetails) {
				if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
					cgstTax = taxes;
				} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
					sgstTax = taxes;
				} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
					igstTax = taxes;
				} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
					ugstTax = taxes;
				} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
					cessTax = taxes;
				}
			}
		}

		BigDecimal cGSTPerc = taxPercentages.get(RuleConstants.CODE_CGST);
		BigDecimal sGSTPerc = taxPercentages.get(RuleConstants.CODE_SGST);
		BigDecimal iGSTPerc = taxPercentages.get(RuleConstants.CODE_IGST);
		BigDecimal uGSTPerc = taxPercentages.get(RuleConstants.CODE_UGST);
		BigDecimal cessPerc = taxPercentages.get(RuleConstants.CODE_CESS);
		BigDecimal totalGST = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);

		fee.setTaxPercent(totalGST);

		if (taxHeader.getTaxDetails() == null) {
			taxHeader.setTaxDetails(new ArrayList<>());
		}

		// CGST
		if (cgstTax == null) {
			cgstTax = getTaxDetail(RuleConstants.CODE_CGST, cGSTPerc, taxHeader);
			taxHeader.getTaxDetails().add(cgstTax);
		} else {
			cgstTax.setTaxPerc(cGSTPerc);
		}

		// SGST
		if (sgstTax == null) {
			sgstTax = getTaxDetail(RuleConstants.CODE_SGST, sGSTPerc, taxHeader);
			taxHeader.getTaxDetails().add(sgstTax);
		} else {
			sgstTax.setTaxPerc(sGSTPerc);
		}

		// IGST
		if (igstTax == null) {
			igstTax = getTaxDetail(RuleConstants.CODE_IGST, iGSTPerc, taxHeader);
			taxHeader.getTaxDetails().add(igstTax);
		} else {
			igstTax.setTaxPerc(iGSTPerc);
		}

		// UGST
		if (ugstTax == null) {
			ugstTax = getTaxDetail(RuleConstants.CODE_UGST, uGSTPerc, taxHeader);
			taxHeader.getTaxDetails().add(ugstTax);
		} else {
			ugstTax.setTaxPerc(uGSTPerc);
		}

		// CESS percentage
		if (cessTax == null) {
			cessTax = getTaxDetail(RuleConstants.CODE_CESS, cessPerc, taxHeader);
			taxHeader.getTaxDetails().add(cessTax);
		} else {
			cessTax.setTaxPerc(cessPerc);
		}

		if (fee.isTaxApplicable()) {
			TaxAmountSplit taxSplit;
			if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, fee.getTaxComponent())) {

				// Actual Amounts
				BigDecimal actualOriginal = fee.getActualAmountOriginal();
				taxSplit = GSTCalculator.getExclusiveGST(actualOriginal, taxPercentages);
				cgstTax.setActualTax(taxSplit.getcGST());
				sgstTax.setActualTax(taxSplit.getsGST());
				igstTax.setActualTax(taxSplit.getiGST());
				ugstTax.setActualTax(taxSplit.getuGST());
				cessTax.setActualTax(taxSplit.getCess());

				fee.setActualAmountGST(taxSplit.gettGST());
				fee.setActualAmount(actualOriginal.add(taxSplit.gettGST()));

				// Paid Amounts
				if (fee.isPaidCalcReq()) {
					BigDecimal paidAmt = fee.getPaidAmount().add(fee.getPaidTDS());
					taxSplit = GSTCalculator.getInclusiveGST(paidAmt, taxPercentages);
					cgstTax.setPaidTax(taxSplit.getcGST());
					sgstTax.setPaidTax(taxSplit.getsGST());
					igstTax.setPaidTax(taxSplit.getiGST());
					ugstTax.setPaidTax(taxSplit.getuGST());
					cessTax.setPaidTax(taxSplit.getCess());

					fee.setPaidAmountGST(taxSplit.gettGST());
					fee.setPaidAmount(paidAmountOriginal.add(taxSplit.gettGST()).subtract(fee.getPaidTDS()));
				}

				// Net Amounts
				taxSplit = GSTCalculator.getExclusiveGST(netAmountOriginal, taxPercentages);
				cgstTax.setNetTax(taxSplit.getcGST());
				sgstTax.setNetTax(taxSplit.getsGST());
				igstTax.setNetTax(taxSplit.getiGST());
				ugstTax.setNetTax(taxSplit.getuGST());
				cessTax.setNetTax(taxSplit.getCess());

				fee.setNetAmountGST(taxSplit.gettGST());
				fee.setNetAmountOriginal(netAmountOriginal);
				fee.setNetAmount(netAmountOriginal.add(taxSplit.gettGST()).subtract(fee.getNetTDS()));

				// Remaining Fee
				BigDecimal remainingAmountOriginal = fee.getActualAmountOriginal().subtract(fee.getPaidAmountOriginal())
						.subtract(waivedAmount);
				// taxSplit = GSTCalculator.getExclusiveGST(remainingAmountOriginal, taxPercentages);
				cgstTax.setRemFeeTax(cgstTax.getNetTax().subtract(cgstTax.getPaidTax()));
				sgstTax.setRemFeeTax(sgstTax.getNetTax().subtract(sgstTax.getPaidTax()));
				igstTax.setRemFeeTax(igstTax.getNetTax().subtract(igstTax.getPaidTax()));
				ugstTax.setRemFeeTax(ugstTax.getNetTax().subtract(ugstTax.getPaidTax()));
				cessTax.setRemFeeTax(cessTax.getNetTax().subtract(cessTax.getPaidTax()));

				BigDecimal totRemGST = cgstTax.getRemFeeTax().add(sgstTax.getRemFeeTax()).add(igstTax.getRemFeeTax())
						.add(ugstTax.getRemFeeTax()).add(cessTax.getRemFeeTax());
				fee.setRemainingFeeGST(totRemGST);
				fee.setRemainingFeeOriginal(remainingAmountOriginal);
				fee.setRemainingFee(remainingAmountOriginal.add(totRemGST).subtract(fee.getRemTDS()));

				// Waived Amounts
				taxSplit = GSTCalculator.getExclusiveGST(waivedAmount, taxPercentages);
				cgstTax.setWaivedTax(taxSplit.getcGST());
				sgstTax.setWaivedTax(taxSplit.getsGST());
				igstTax.setWaivedTax(taxSplit.getiGST());
				ugstTax.setWaivedTax(taxSplit.getuGST());
				cessTax.setWaivedTax(taxSplit.getCess());
				fee.setWaivedGST(taxSplit.gettGST());
				fee.setTaxHeader(taxHeader);

			} else if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, fee.getTaxComponent())) {

				// Net Amount
				BigDecimal totalNetFee = fee.getActualAmount().add(fee.getNetTDS());

				taxSplit = GSTCalculator.getInclusiveGST(totalNetFee, taxPercentages);

				cgstTax.setNetTax(taxSplit.getcGST());
				sgstTax.setNetTax(taxSplit.getsGST());
				igstTax.setNetTax(taxSplit.getiGST());
				ugstTax.setNetTax(taxSplit.getuGST());
				cessTax.setNetTax(taxSplit.getCess());

				fee.setNetAmountOriginal(totalNetFee.subtract(taxSplit.gettGST()));
				fee.setNetAmountGST(taxSplit.gettGST());
				fee.setNetAmount(totalNetFee.subtract(fee.getNetTDS()));

				// Actual Amounts
				fee.setActualAmountOriginal(fee.getNetAmountOriginal());
				fee.setActualAmountGST(fee.getNetAmountGST());
				fee.setActualAmount(fee.getNetAmount());

				cgstTax.setActualTax(taxSplit.getcGST());
				sgstTax.setActualTax(taxSplit.getsGST());
				igstTax.setActualTax(taxSplit.getiGST());
				ugstTax.setActualTax(taxSplit.getuGST());
				cessTax.setActualTax(taxSplit.getCess());

				// Paid Amounts
				BigDecimal totalPaidFee = fee.getPaidAmount().add(fee.getPaidTDS());
				if (fee.isPaidCalcReq()) {

					taxSplit = GSTCalculator.getInclusiveGST(totalPaidFee, taxPercentages);
					cgstTax.setPaidTax(taxSplit.getcGST());
					sgstTax.setPaidTax(taxSplit.getsGST());
					igstTax.setPaidTax(taxSplit.getiGST());
					ugstTax.setPaidTax(taxSplit.getuGST());
					cessTax.setPaidTax(taxSplit.getCess());

					fee.setPaidAmountOriginal(totalPaidFee.subtract(taxSplit.gettGST()));
					fee.setPaidAmountGST(taxSplit.gettGST());
				}

				// Remaining Fee
				BigDecimal remainingAmountOriginal = fee.getActualAmountOriginal().subtract(fee.getPaidAmountOriginal())
						.subtract(waivedAmount);
				// taxSplit = GSTCalculator.getInclusiveGST(remainingAmountOriginal, taxPercentages);
				cgstTax.setRemFeeTax(cgstTax.getNetTax().subtract(cgstTax.getPaidTax()));
				sgstTax.setRemFeeTax(sgstTax.getNetTax().subtract(sgstTax.getPaidTax()));
				igstTax.setRemFeeTax(igstTax.getNetTax().subtract(igstTax.getPaidTax()));
				ugstTax.setRemFeeTax(ugstTax.getNetTax().subtract(ugstTax.getPaidTax()));
				cessTax.setRemFeeTax(cessTax.getNetTax().subtract(cessTax.getPaidTax()));

				BigDecimal totRemGST = cgstTax.getRemFeeTax().add(sgstTax.getRemFeeTax()).add(igstTax.getRemFeeTax())
						.add(ugstTax.getRemFeeTax()).add(cessTax.getRemFeeTax());
				fee.setRemainingFeeGST(totRemGST);
				fee.setRemainingFeeOriginal(remainingAmountOriginal);
				fee.setRemTDS(fee.getNetTDS().subtract(fee.getPaidTDS()));
				fee.setRemainingFee(remainingAmountOriginal.add(totRemGST).subtract(fee.getRemTDS()));

				// Waived Amounts
				taxSplit = GSTCalculator.getInclusiveGST(waivedAmount, taxPercentages);
				cgstTax.setWaivedTax(taxSplit.getcGST());
				sgstTax.setWaivedTax(taxSplit.getsGST());
				igstTax.setWaivedTax(taxSplit.getiGST());
				ugstTax.setWaivedTax(taxSplit.getuGST());
				cessTax.setWaivedTax(taxSplit.getCess());
				fee.setWaivedGST(taxSplit.gettGST());
			}
		} else {

			// Net Amount
			fee.setNetAmountOriginal(netAmountOriginal);
			fee.setNetAmountGST(BigDecimal.ZERO);
			fee.setNetAmount(netAmountOriginal.subtract(fee.getNetTDS()));

			if (BigDecimal.ZERO.compareTo(waivedAmount) == 0) {
				fee.setActualAmount(fee.getNetAmount());
			} else {
				fee.setActualAmount(fee.getActualAmountOriginal());
			}

			// Remaining Amount
			fee.setRemainingFeeOriginal(
					fee.getActualAmountOriginal().subtract(waivedAmount).subtract(fee.getPaidAmount()));
			fee.setRemainingFeeGST(BigDecimal.ZERO);
			fee.setRemainingFee(fee.getActualAmount().subtract(waivedAmount).subtract(fee.getPaidAmount())
					.subtract(fee.getRemTDS()));

			// Paid Amount
			fee.setPaidAmountOriginal(fee.getPaidAmount().add(fee.getPaidTDS()));
			fee.setPaidAmountGST(BigDecimal.ZERO);

			// Actual Fee
			cgstTax.setActualTax(BigDecimal.ZERO);
			sgstTax.setActualTax(BigDecimal.ZERO);
			igstTax.setActualTax(BigDecimal.ZERO);
			ugstTax.setActualTax(BigDecimal.ZERO);
			cessTax.setActualTax(BigDecimal.ZERO);

			// Paid Fee
			cgstTax.setPaidTax(BigDecimal.ZERO);
			sgstTax.setPaidTax(BigDecimal.ZERO);
			igstTax.setPaidTax(BigDecimal.ZERO);
			ugstTax.setPaidTax(BigDecimal.ZERO);
			cessTax.setPaidTax(BigDecimal.ZERO);

			// Net Fee
			cgstTax.setNetTax(BigDecimal.ZERO);
			sgstTax.setNetTax(BigDecimal.ZERO);
			igstTax.setNetTax(BigDecimal.ZERO);
			ugstTax.setNetTax(BigDecimal.ZERO);
			cessTax.setNetTax(BigDecimal.ZERO);

			// Remaining Fee
			cgstTax.setRemFeeTax(BigDecimal.ZERO);
			sgstTax.setRemFeeTax(BigDecimal.ZERO);
			igstTax.setRemFeeTax(BigDecimal.ZERO);
			ugstTax.setRemFeeTax(BigDecimal.ZERO);
			cessTax.setRemFeeTax(BigDecimal.ZERO);

			// Waived Amounts
			cgstTax.setWaivedTax(BigDecimal.ZERO);
			sgstTax.setWaivedTax(BigDecimal.ZERO);
			igstTax.setWaivedTax(BigDecimal.ZERO);
			ugstTax.setWaivedTax(BigDecimal.ZERO);
			cessTax.setWaivedTax(BigDecimal.ZERO);
			fee.setWaivedGST(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, TaxHeader taxHeader) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	@Override
	public BigDecimal calculatePercentage(BigDecimal amount, BigDecimal gstPercentage, String taxRoundMode,
			int taxRoundingTarget) {
		logger.debug(Literal.ENTERING);

		BigDecimal result = amount.multiply(gstPercentage.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN));
		result = CalculationUtil.roundAmount(result, taxRoundMode, taxRoundingTarget);

		logger.debug(Literal.LEAVING);
		return result;
	}

	@Override
	public Map<String, Object> prepareGstMappingByBranch(String fromBranch, String toBranch) {

		Branch frBrn = branchDAO.getBranchById(fromBranch, "");
		Province fromState = provinceDAO.getProvinceById(frBrn.getBranchCountry(), frBrn.getBranchProvince(), "");

		Branch toBrn = branchDAO.getBranchById(toBranch, "");
		Province toState = provinceDAO.getProvinceById(toBrn.getBranchCountry(), toBrn.getBranchProvince(), "");

		return prepareGstMapping(fromState.getCPProvince(), toState.getCPProvince());
	}

	@Override
	public Map<String, Object> prepareGstMapping(String fromStateCode, String toStateCode) {
		Map<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;

		Province fromState = provinceDAO.getProvinceById(fromStateCode, "");

		if (fromState != null) {
			gstExecutionMap.put("fromState", fromState.getCPProvince());
			gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
			gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
		}

		Province toState = provinceDAO.getProvinceById(toStateCode, "");

		if (toState != null) {
			gstExecutionMap.put("toState", toState.getCPProvince());
			gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
			gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
		}

		gstExecutionMap.put("gstExempted", gstExempted);

		return gstExecutionMap;
	}

	@Override
	public boolean getFeeTypeId(long feeTypeId, String finType, int moduleId, boolean originationFee) {
		return finFeeDetailDAO.isFinTypeFeeExists(feeTypeId, finType, moduleId, originationFee);
	}

	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode, long finID) {
		return finFeeDetailDAO.getFinFeeTypeIdByFeeType(feeTypeCode, finID, "_AView");
	}

	@Override
	public Branch getBranchById(String branchCode, String type) {
		return branchDAO.getBranchById(branchCode, type);
	}

	@Override
	public void updateFeesFromUpfront(FinFeeDetail finFeeDetail, String type) {
		finFeeDetailDAO.updateFeesFromUpfront(finFeeDetail, type);
	}

	@Override
	public BigDecimal calDropLineLPOS(FinScheduleData schdData, Date appDate) {

		BigDecimal limitAmt = BigDecimal.ZERO;

		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		if (schedules == null || schedules.isEmpty()) {
			schedules = financeScheduleDetailDAO.getFinSchdDetailsForBatch(fm.getFinID());
		}

		if (appDate.compareTo(fm.getGrcPeriodEndDate()) <= 0) {
			limitAmt = fm.getFinAssetValue();
		} else {
			for (FinanceScheduleDetail schedule : schedules) {

				if (appDate.compareTo(schedule.getSchDate()) < 0) {
					break;
				}
				limitAmt = schedule.getODLimit();
			}
		}
		return limitAmt;
	}

	@Override
	public List<FinFeeDetail> convertToFinanceFees(FinanceDetail fd, String userBranch) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> fees = new ArrayList<>();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String finBranch = fm.getFinBranch();
		String finCCY = fm.getFinCcy();

		long custId = 0;

		CustomerDetails cd = fd.getCustomerDetails();
		if (cd != null) {
			custId = cd.getCustomer().getCustID();
		}

		FinanceTaxDetail ftd = fd.getFinanceTaxDetail();
		Map<String, BigDecimal> taxPer = GSTCalculator.getTaxPercentages(custId, finCCY, userBranch, finBranch, ftd);

		String subventionFeeCode = PennantConstants.FEETYPE_SUBVENTION;

		List<FinTypeFees> finTypeFeesList = fd.getFinTypeFeesList();

		String roundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();

		for (FinTypeFees fee : finTypeFeesList) {
			FinFeeDetail ffd = new FinFeeDetail();
			ffd.setNewRecord(true);
			ffd.setOriginationFee(fee.isOriginationFee());
			ffd.setFinEvent(fee.getFinEvent());
			ffd.setFinEventDesc(fee.getFinEventDesc());
			ffd.setFeeTypeID(fee.getFeeTypeID());
			ffd.setFeeOrder(fee.getFeeOrder());
			ffd.setFeeTypeCode(fee.getFeeTypeCode());
			ffd.setFeeTypeDesc(fee.getFeeTypeDesc());
			ffd.setFeeScheduleMethod(fee.getFeeScheduleMethod());
			ffd.setCalculationType(fee.getCalculationType());
			ffd.setRuleCode(fee.getRuleCode());
			ffd.setAlwPreIncomization(fee.isAlwPreIncomization());

			BigDecimal feeAmount = fee.getAmount();
			BigDecimal finAmount = CalculationUtil.roundAmount(feeAmount, roundingMode, roundingTarget);
			fee.setAmount(finAmount);

			ffd.setFixedAmount(feeAmount);
			ffd.setPercentage(fee.getPercentage());
			ffd.setCalculateOn(fee.getCalculateOn());
			ffd.setAlwDeviation(fee.isAlwDeviation());
			ffd.setMaxWaiverPerc(fee.getMaxWaiverPerc());
			ffd.setAlwModifyFee(fee.isAlwModifyFee());
			ffd.setAlwModifyFeeSchdMthd(fee.isAlwModifyFeeSchdMthd());
			ffd.setCalculatedAmount(feeAmount);
			ffd.setTaxComponent(fee.getTaxComponent());
			ffd.setTaxApplicable(fee.isTaxApplicable());

			if (fee.isTaxApplicable()) {
				if (subventionFeeCode.equals(fee.getFeeTypeCode())) {
					Long mdid = fm.getManufacturerDealerId();
					taxPer = GSTCalculator.getDealerTaxPercentages(mdid, finCCY, userBranch, finBranch, ftd);
				}

				convertGSTFinTypeFees(ffd, fee, fd, taxPer);

				fees.add(ffd);

				continue;
			}

			ffd.setActualAmountOriginal(feeAmount);
			ffd.setActualAmountGST(BigDecimal.ZERO);
			ffd.setActualAmount(feeAmount);

			BigDecimal waivedAmount = ffd.getWaivedAmount();
			BigDecimal netAmountOriginal = ffd.getActualAmountOriginal().subtract(waivedAmount);

			ffd.setNetAmountOriginal(netAmountOriginal);
			ffd.setNetAmountGST(BigDecimal.ZERO);
			ffd.setNetAmount(netAmountOriginal);

			if (CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(fee.getFeeScheduleMethod())) {
				ffd.setWaivedAmount(feeAmount);
				waivedAmount = feeAmount;
			}

			BigDecimal paidAmountOriginal = ffd.getPaidAmountOriginal();

			ffd.setRemainingFeeOriginal(ffd.getActualAmount().subtract(waivedAmount).subtract(paidAmountOriginal));
			ffd.setRemainingFeeGST(BigDecimal.ZERO);
			ffd.setRemainingFee(ffd.getActualAmount().subtract(waivedAmount).subtract(ffd.getPaidAmount()));

			fees.add(ffd);
		}

		logger.debug(Literal.LEAVING);
		return fees;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailsByTran(String reference, boolean isWIF, String type) {
		return finFeeDetailDAO.getFinFeeDetailsByTran(reference, isWIF, type);
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public void setTaxHeaderDetailsService(TaxHeaderDetailsService taxHeaderDetailsService) {
		this.taxHeaderDetailsService = taxHeaderDetailsService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailByFinRef(long finID, boolean isWIF, String type) {
		return finFeeDetailDAO.getFinFeeDetailByFinRef(finID, isWIF, type);
	}
}