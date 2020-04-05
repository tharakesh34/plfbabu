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
 * FileName    		:  FinFeeDetailServiceImpl.java                                 		* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinTaxDetailsDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinFeeDetailServiceImpl extends GenericService<FinFeeDetail> implements FinFeeDetailService {
	private static final Logger logger = Logger.getLogger(FinFeeDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;

	private FinTaxDetailsDAO finTaxDetailsDAO;

	// Newly added
	private RuleExecutionUtil ruleExecutionUtil;
	private RuleDAO ruleDAO;
	private BranchDAO branchDAO;
	private ProvinceDAO provinceDAO;
	private TaxHeaderDetailsService taxHeaderDetailsService;
	private ProvinceService provinceService;
	private BranchService branchService;

	public FinFeeDetailServiceImpl() {
		super();
	}

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *        the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Override
	public List<FinReceiptDetail> getFinReceiptDetais(String finReference, long custId) {
		logger.debug(Literal.ENTERING);
		return getFinReceiptDetailDAO().getFinReceiptDetailByFinRef(finReference, custId);
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptsById(List<Long> feeIds, String type) {
		logger.debug(Literal.ENTERING);
		return getFinFeeReceiptDAO().getFinFeeReceiptByFinRef(feeIds, type);
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, isWIF, type);

		if (CollectionUtils.isNotEmpty(finFeeDetails)) {

			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				// Finance Fee Schedule Details
				finFeeDetail.setFinFeeScheduleDetailList(
						getFinFeeScheduleDetailDAO().getFeeScheduleByFeeID(finFeeDetail.getFeeID(), isWIF, type));
				//Fin Tax Details
				finFeeDetail.setFinTaxDetails(getFinTaxDetailsDAO().getFinTaxByFeeID(finFeeDetail.getFeeID(), type));
				//Fin Tax Header
				long taxHeaderId = finFeeDetail.getTaxHeaderId();
				if (taxHeaderId > 0) {
					TaxHeader taxheader = getTaxHeaderDetailsService().getTaxHeaderById(taxHeaderId, type);
					finFeeDetail.setTaxHeader(taxheader);
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return finFeeDetails;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type,
			String eventCodeRef) {
		logger.debug(Literal.ENTERING);
		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, isWIF, type,
				eventCodeRef);
		// Finance Fee Schedule Details
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				finFeeDetail.setFinFeeScheduleDetailList(
						getFinFeeScheduleDetailDAO().getFeeScheduleByFeeID(finFeeDetail.getFeeID(), isWIF, type));
				finFeeDetail.setFinTaxDetails(getFinTaxDetailsDAO().getFinTaxByFeeID(finFeeDetail.getFeeID(), type));
			}
		}
		logger.debug(Literal.LEAVING);
		return finFeeDetails;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailsByReferenceId(long referenceId, String eventCodeRef, String type) {
		logger.debug(Literal.ENTERING);

		List<FinFeeDetail> finFeeDetails = finFeeDetailDAO.getFinFeeDetailByReferenceId(referenceId, eventCodeRef,
				type);
		// Finance Fee Schedule Details and Fin Tax Details
		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				finFeeDetail.setFinFeeScheduleDetailList(
						finFeeScheduleDetailDAO.getFeeScheduleByFeeID(finFeeDetail.getFeeID(), false, type));
				finFeeDetail.setFinTaxDetails(finTaxDetailsDAO.getFinTaxByFeeID(finFeeDetail.getFeeID(), type));
			}
		}

		logger.debug(Literal.LEAVING);

		return finFeeDetails;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,
			boolean isWIF) {
		logger.debug(Literal.ENTERING);

		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && (finFeeDetail.isNewRecord()
					|| (!finFeeDetail.isNewRecord() && finFeeDetail.getTaxHeaderId() > 0))) {
				taxHeader.setRecordType(finFeeDetail.getRecordType());
				taxHeader.setNewRecord(finFeeDetail.isNew());
				taxHeader.setLastMntBy(finFeeDetail.getLastMntBy());
				taxHeader.setLastMntOn(finFeeDetail.getLastMntOn());
				taxHeader.setRecordStatus(finFeeDetail.getRecordStatus());
				TaxHeader txHeader = getTaxHeaderDetailsService().saveOrUpdate(taxHeader, tableType, auditTranType);
				finFeeDetail.setTaxHeaderId(txHeader.getHeaderId());
			}
		}
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, false, isWIF));

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
			FinTaxDetails finTaxDetails = fee.getFinTaxDetails();

			if (StringUtils.isEmpty(tableType)
					|| StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
				approveRec = true;
				fee.setRoleCode("");
				fee.setNextRoleCode("");
				fee.setTaskId("");
				fee.setNextTaskId("");
				fee.setWorkflowId(0);
			}

			if (fee.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
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
				} else if (fee.isNew()) {
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
					fee.setFeeSeq(getFinFeeDetailDAO().getFeeSeq(fee, isWIF, tableType) + 1);
				}

				if ((fee.isAlwPreIncomization()) && (fee.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
					FinFeeDetail finFeeDtl = getFinFeeDetailDAO().getFeeDetailByExtReference(fee.getTransactionId(),
							fee.getFeeTypeID(), "");
					if (finFeeDtl != null) {
						fee.setFeeID(finFeeDtl.getFeeID());
						fee.setFeeSeq(finFeeDtl.getFeeSeq());
						getFinFeeDetailDAO().update(fee, false, "");
					}
				} else {
					fee.setFeeID(getFinFeeDetailDAO().save(fee, isWIF, tableType));
				}

				finTaxDetails.setFeeID(fee.getFeeID());

				getFinTaxDetailsDAO().save(finTaxDetails, tableType);

				if (!fee.getFinFeeScheduleDetailList().isEmpty()) {
					for (FinFeeScheduleDetail finFeeSchDetail : fee.getFinFeeScheduleDetailList()) {
						finFeeSchDetail.setFeeID(fee.getFeeID());
					}
					getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(fee.getFinFeeScheduleDetailList(), isWIF,
							tableType);
				}
			}

			if (updateRecord) {
				getFinFeeDetailDAO().update(fee, isWIF, tableType);

				if (finTaxDetails.getFinTaxID() != 0 && finTaxDetails.getFinTaxID() != Long.MIN_VALUE) {
					getFinTaxDetailsDAO().update(finTaxDetails, tableType);
				}

				getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(fee.getFeeID(), isWIF, tableType);
				if (!fee.getFinFeeScheduleDetailList().isEmpty()) {
					for (FinFeeScheduleDetail finFeeSchDetail : fee.getFinFeeScheduleDetailList()) {
						finFeeSchDetail.setFeeID(fee.getFeeID());
					}
					getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(fee.getFinFeeScheduleDetailList(), isWIF,
							tableType);
				}
			}

			if (deleteRecord) {
				getFinTaxDetailsDAO().deleteByFeeID(fee.getFeeID(), tableType);
				getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(fee.getFeeID(), isWIF, tableType);
				getFinFeeDetailDAO().delete(fee, isWIF, tableType);
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
					} else if (finFeeReceipt.isNew()) {
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
					finFeeReceipt.setId(getFinFeeReceiptDAO().save(finFeeReceipt, tableType));
				}

				if (updateRecord) {
					getFinFeeReceiptDAO().update(finFeeReceipt, tableType);
				}

				if (deleteRecord) {
					getFinFeeReceiptDAO().delete(finFeeReceipt, tableType);
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
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && (finFeeDetail.isNewRecord()
					|| (!finFeeDetail.isNewRecord() && finFeeDetail.getTaxHeaderId() > 0))) {
				taxHeader.setRecordType(finFeeDetail.getRecordType());
				taxHeader.setNewRecord(finFeeDetail.isNew());
				taxHeader.setRecordStatus(finFeeDetail.getRecordStatus());
				taxHeader.setLastMntBy(finFeeDetail.getLastMntBy());
				taxHeader.setLastMntOn(finFeeDetail.getLastMntOn());
				TaxHeader txHeader = getTaxHeaderDetailsService().doApprove(taxHeader, tableType, auditTranType);
				finFeeDetail.setTaxHeaderId(txHeader.getHeaderId());
			}
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, true, isWIF));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/*
	 * @Override public List<AuditDetail> doApproveFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
	 * String auditTranType, String finReference) { logger.debug(Literal.ENTERING);
	 * 
	 * List<AuditDetail> auditDetails = new ArrayList<AuditDetail>(); Map<Long, FinFeeReceipt> map = new HashMap<Long,
	 * FinFeeReceipt>();
	 * 
	 * if (!StringUtils.equals(PennantConstants.TRAN_DEL, auditTranType)) {
	 * 
	 * FinFeeReceipt feeReceipt; for (FinFeeReceipt finFeeReceipt : finFeeReceipts) { if
	 * (!map.containsKey(finFeeReceipt.getReceiptID())) { feeReceipt = new FinFeeReceipt();
	 * feeReceipt.setReceiptID(finFeeReceipt.getReceiptID()); feeReceipt.setPaidAmount(finFeeReceipt.getPaidAmount());
	 * feeReceipt.setReceiptAmount(finFeeReceipt.getReceiptAmount());
	 * feeReceipt.setLastMntBy(finFeeReceipt.getLastMntBy());
	 * feeReceipt.setAvailableAmount(finFeeReceipt.getReceiptAmount().subtract( finFeeReceipt.getPaidAmount())); } else
	 * { feeReceipt = map.get(finFeeReceipt.getReceiptID());
	 * feeReceipt.setPaidAmount(feeReceipt.getPaidAmount().add(finFeeReceipt. getPaidAmount()));
	 * feeReceipt.setAvailableAmount(feeReceipt.getReceiptAmount().subtract( feeReceipt.getPaidAmount())); }
	 * 
	 * map.put(finFeeReceipt.getReceiptID(), feeReceipt); }
	 * 
	 * if (ImplementationConstants.UPFRONT_ADJUST_PAYABLEADVISE) { createPayableAdvise(finReference, map); } }
	 * 
	 * if (!ImplementationConstants.UPFRONT_ADJUST_PAYABLEADVISE) { createExcessAmount(finReference, map); }
	 * 
	 * auditDetails.addAll(processFinFeeReceipts(finFeeReceipts, tableType, auditTranType, true));
	 * 
	 * logger.debug(Literal.LEAVING); return auditDetails; }
	 */

	public Map<Long, List<FinFeeReceipt>> getUpfromtReceiptMap(List<FinFeeReceipt> finFeeReceipts) {
		Map<Long, List<FinFeeReceipt>> map = new HashMap<>();

		for (FinFeeReceipt finFeeRecipt : finFeeReceipts) {
			List<FinFeeReceipt> finFeeRecList = null;
			if (map.containsKey(finFeeRecipt.getReceiptID())) {
				finFeeRecList = map.get(finFeeRecipt.getReceiptID());
			} else {
				finFeeRecList = new ArrayList<>();
			}
			finFeeRecList.add(finFeeRecipt);
			map.put(finFeeRecipt.getReceiptID(), finFeeRecList);

		}

		return map;
	}

	@Override
	public List<AuditDetail> doApproveFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType, String finReference, long custId) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		Map<Long, List<FinFeeReceipt>> map = getUpfromtReceiptMap(finFeeReceipts);
		if (!StringUtils.equals(PennantConstants.TRAN_DEL, auditTranType)) {
			if (ImplementationConstants.UPFRONT_ADJUST_PAYABLEADVISE) {
				createPayableAdvises(finReference, map, custId);
			}
		}

		if (!ImplementationConstants.UPFRONT_ADJUST_PAYABLEADVISE) {
			createExcessAmounts(finReference, map, custId);
		}

		auditDetails.addAll(processFinFeeReceipts(finFeeReceipts, tableType, auditTranType, true));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public void createExcessAmounts(String finReference, Map<Long, List<FinFeeReceipt>> map, long custId) {
		logger.debug(Literal.ENTERING);

		FinExcessAmount finExcessAmount;
		BigDecimal excessAmount = getExcessAmount(finReference, map, custId);

		if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
			finExcessAmount = new FinExcessAmount();
			finExcessAmount.setFinReference(finReference);
			finExcessAmount.setAmountType(RepayConstants.EXAMOUNTTYPE_EXCESS);
			finExcessAmount.setAmount(excessAmount);
			finExcessAmount.setUtilisedAmt(BigDecimal.ZERO);
			finExcessAmount.setReservedAmt(BigDecimal.ZERO);
			finExcessAmount.setBalanceAmt(excessAmount);
			getFinExcessAmountDAO().saveExcess(finExcessAmount);
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public BigDecimal getExcessAmount(String finReference, Map<Long, List<FinFeeReceipt>> map, long custId) {

		List<FinReceiptDetail> finReceiptDetailsList = finReceiptDetailDAO.getFinReceiptDetailByFinRef(finReference,
				custId);
		BigDecimal excessAmount = BigDecimal.ZERO;
		for (FinReceiptDetail finReceiptDetail : finReceiptDetailsList) {
			if (map != null && map.containsKey(finReceiptDetail.getReceiptID())) {
				List<FinFeeReceipt> finFeeReceiptList = map.get(finReceiptDetail.getReceiptID());
				BigDecimal feePaidAmount = BigDecimal.ZERO;
				for (FinFeeReceipt feeReceipt : finFeeReceiptList) {
					feePaidAmount = feePaidAmount.add(feeReceipt.getPaidAmount());
				}
				excessAmount = excessAmount.add(finReceiptDetail.getAmount().subtract(feePaidAmount));
			} else {
				excessAmount = excessAmount.add(finReceiptDetail.getAmount());
			}
		}
		return excessAmount;
	}

	private void createPayableAdvises(String finReference, Map<Long, List<FinFeeReceipt>> map, long custId) {
		logger.debug(Literal.ENTERING);

		PFSParameter pfsParameter = SysParamUtil.getSystemParameterObject("MANUALADVISE_FEETYPEID");
		long feeTypeId = Long.parseLong(pfsParameter.getSysParmValue());
		ManualAdvise manualAdvise = null;
		List<FinReceiptDetail> finReceiptDetailsList = getFinReceiptDetailDAO()
				.getFinReceiptDetailByFinRef(finReference, custId);

		for (FinReceiptDetail finRecDetail : finReceiptDetailsList) {
			List<FinFeeReceipt> finFeeRecList = map.get(finRecDetail.getReceiptID());
			BigDecimal feePaid = BigDecimal.ZERO;
			BigDecimal available;
			long getLastMntBy = Long.MIN_VALUE;
			for (FinFeeReceipt feeReceipt : finFeeRecList) {
				feePaid = feePaid.add(feeReceipt.getPaidAmount());
				getLastMntBy = feeReceipt.getLastMntBy();
			}
			available = finRecDetail.getAmount().subtract(feePaid);
			if (available.compareTo(BigDecimal.ZERO) > 0) {

				manualAdvise = new ManualAdvise();
				manualAdvise.setAdviseType(2);
				manualAdvise.setFinReference(finReference);
				manualAdvise.setFeeTypeID(feeTypeId);
				manualAdvise.setSequence(0);
				manualAdvise.setAdviseAmount(available);
				manualAdvise.setPaidAmount(BigDecimal.ZERO);
				manualAdvise.setWaivedAmount(BigDecimal.ZERO);
				manualAdvise.setRemarks("FeeReceipt Remaining Amount");
				manualAdvise.setBounceID(0);
				manualAdvise.setReceiptID(finRecDetail.getReceiptID());
				manualAdvise.setValueDate(DateUtility.getAppDate());
				manualAdvise.setPostDate(DateUtility.getAppDate());
				manualAdvise.setReservedAmt(BigDecimal.ZERO);
				manualAdvise.setBalanceAmt(BigDecimal.ZERO);

				manualAdvise.setVersion(0);
				manualAdvise.setLastMntBy(getLastMntBy);
				manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				manualAdvise.setRoleCode("");
				manualAdvise.setNextRoleCode("");
				manualAdvise.setTaskId("");
				manualAdvise.setNextTaskId("");
				manualAdvise.setRecordType("");
				manualAdvise.setWorkflowId(0);
			}

			getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
		}
		// FIXME CH Get the latest Receipt details and update the payable.

		logger.debug(Literal.LEAVING);
	}

	private void createPayableAdvise(String finReference, Map<Long, FinFeeReceipt> map) {
		logger.debug(Literal.ENTERING);

		FinFeeReceipt feeReceipt;
		PFSParameter pfsParameter = SysParamUtil.getSystemParameterObject("MANUALADVISE_FEETYPEID");
		long feeTypeId = Long.valueOf(pfsParameter.getSysParmValue());
		ManualAdvise manualAdvise;

		// FIXME CH Get the latest Receipt details and update the payable.
		for (Long key : map.keySet()) {
			feeReceipt = map.get(key);
			if (feeReceipt.getAvailableAmount().compareTo(BigDecimal.ZERO) != 0) {
				manualAdvise = new ManualAdvise();
				manualAdvise.setAdviseType(2);
				manualAdvise.setFinReference(finReference);
				manualAdvise.setFeeTypeID(feeTypeId);
				manualAdvise.setSequence(0);
				manualAdvise.setAdviseAmount(feeReceipt.getAvailableAmount());
				manualAdvise.setPaidAmount(BigDecimal.ZERO);
				manualAdvise.setWaivedAmount(BigDecimal.ZERO);
				manualAdvise.setRemarks("FeeReceipt Remaining Amount");
				manualAdvise.setBounceID(0);
				manualAdvise.setReceiptID(feeReceipt.getReceiptID());
				manualAdvise.setValueDate(DateUtility.getAppDate());
				manualAdvise.setPostDate(DateUtility.getAppDate());
				manualAdvise.setReservedAmt(BigDecimal.ZERO);
				manualAdvise.setBalanceAmt(BigDecimal.ZERO);

				manualAdvise.setVersion(0);
				manualAdvise.setLastMntBy(feeReceipt.getLastMntBy());
				manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				manualAdvise.setRoleCode("");
				manualAdvise.setNextRoleCode("");
				manualAdvise.setTaskId("");
				manualAdvise.setNextTaskId("");
				manualAdvise.setRecordType("");
				manualAdvise.setWorkflowId(0);

				getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void createExcessAmount(String finReference, Map<Long, FinFeeReceipt> map, long custId) {
		logger.debug(Literal.ENTERING);

		FinFeeReceipt feeReceipt;
		FinExcessAmount finExcessAmount;

		List<FinReceiptDetail> finReceiptDetailsList = getFinReceiptDetailDAO()
				.getFinReceiptDetailByFinRef(finReference, custId);
		BigDecimal excessAmount = BigDecimal.ZERO;
		for (FinReceiptDetail finReceiptDetail : finReceiptDetailsList) {
			if (map != null && map.containsKey(finReceiptDetail.getReceiptID())) {
				feeReceipt = map.get(finReceiptDetail.getReceiptID());
				excessAmount = excessAmount.add(finReceiptDetail.getAmount().subtract(feeReceipt.getPaidAmount()));
			} else {
				excessAmount = excessAmount.add(finReceiptDetail.getAmount());
			}
		}

		if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
			finExcessAmount = new FinExcessAmount();
			finExcessAmount.setFinReference(finReference);
			finExcessAmount.setAmountType(RepayConstants.EXAMOUNTTYPE_EXCESS);
			finExcessAmount.setAmount(excessAmount);
			finExcessAmount.setUtilisedAmt(BigDecimal.ZERO);
			finExcessAmount.setReservedAmt(BigDecimal.ZERO);
			finExcessAmount.setBalanceAmt(excessAmount);
			getFinExcessAmountDAO().saveExcess(finExcessAmount);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,
			boolean isWIF) {
		logger.debug(Literal.ENTERING);

		String[] fields = null;
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			int auditSeq = 1;
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				finFeeScheduleDetailDAO.deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
				finTaxDetailsDAO.deleteByFeeID(finFeeDetail.getFeeID(), tableType);
				finFeeDetailDAO.delete(finFeeDetail, isWIF, tableType);
				if (finFeeDetail.getTaxHeaderId() > 0) {
					getTaxHeaderDetailsService().delete(finFeeDetail.getTaxHeaderId(), tableType);
				}
				fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						finFeeDetail.getBefImage(), finFeeDetail));
				auditSeq++;
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> deleteFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;

		if (CollectionUtils.isNotEmpty(finFeeReceipts)) {
			int auditSeq = 1;
			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				getFinFeeReceiptDAO().delete(finFeeReceipt, tableType);
				fields = PennantJavaUtil.getFieldDetails(finFeeReceipt, finFeeReceipt.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1],
						finFeeReceipt.getBefImage(), finFeeReceipt));
				auditSeq++;
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getFinFeeDetailAuditDetail(List<FinFeeDetail> finFeeDetails, String auditTranType,
			String method, long workFlowId) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();
		String[] fields = null;
		for (FinFeeDetail finFeeDetail : finFeeDetails) {

			if ("doApprove".equals(method) && !StringUtils.trimToEmpty(finFeeDetail.getRecordStatus())
					.equals(PennantConstants.RCD_STATUS_SAVED)) {
				// finFeeDetail.setWorkflowId(0);
				/*
				 * if (StringUtils.equals(finFeeDetail.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				 * finFeeDetail.setNewRecord(true); }
				 */
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
				if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())
						&& FinanceConstants.FINSER_EVENT_ORG.equals(finFeeDetail.getModuleDefiner())
						&& PennantConstants.RECORD_TYPE_UPD.equals(finFeeDetail.getRecordType())) {
					finFeeDetail.setNewRecord(false);
				}
			} else if ("doApprove".equals(method)
					&& (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent()))) {
				if (!isRcdType || PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(finFeeDetail.getRecordType())) {
					finFeeDetail.setNewRecord(false);
					finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(finFeeDetail.getRecordType())) {
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
			fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
			if (StringUtils.isNotEmpty(finFeeDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finFeeDetail.getBefImage(), finFeeDetail));
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> getFinFeeReceiptAuditDetail(List<FinFeeReceipt> finFeeReceipts, String auditTranType,
			String method, long workFlowId) {
		logger.debug(Literal.ENTERING);

		String[] fields = null;
		List<AuditDetail> auditDetails = new ArrayList<>();

		for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
			if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			fields = PennantJavaUtil.getFieldDetails(finFeeReceipt, finFeeReceipt.getExcludeFields());

			if (StringUtils.isNotEmpty(finFeeReceipt.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finFeeReceipt.getBefImage(), finFeeReceipt));
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

	private List<AuditDetail> doValidationFinFeeReceipts(FinanceDetail financeDetail, long workflowId, String method,
			String auditTranType, String usrLanguage, List<AuditDetail> auditDetails) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> finFeeAuditDetails = new ArrayList<>();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinFeeReceipt> finFeeReceipts = finScheduleData.getFinFeeReceipts();
		AuditDetail detail = new AuditDetail();
		// Removed empty condition as the below validation should happen when
		// there is no upfront receipt available for the paid amount.
		if (finFeeReceipts != null) {
			finFeeAuditDetails = getFinFeeReceiptAuditDetail(finFeeReceipts, auditTranType, method, workflowId);

			for (AuditDetail auditDetail : finFeeAuditDetails) {
				validateFinFeeReceipts(auditDetail, method, usrLanguage);
			}

		}
		// auditDetails.addAll(finFeeAuditDetails);
		BigDecimal totalFee = BigDecimal.ZERO;
		boolean error = false;

		if (!financeDetail.isActionSave() && financeDetail.isUpFrentFee()) {
			List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();
			for (FinFeeDetail finFeeDetail : feeDetails) {
				BigDecimal totalPaidAmount = BigDecimal.ZERO;

				for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {

					if (!StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, finFeeReceipt.getRecordType())) {
						if (finFeeDetail.getFeeTypeID() == 0) {
							if (StringUtils.equals(finFeeReceipt.getFeeTypeCode(), finFeeDetail.getVasReference())) {
								totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
							}
						} else {
							if (finFeeDetail.getFeeTypeID() == finFeeReceipt.getFeeTypeId()) {
								totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
							}
						}
					}
				}

				if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) != 0) {
					String feeTypeDesc = finFeeDetail.getFeeTypeDesc();
					if (StringUtils.isBlank(feeTypeDesc)) {
						feeTypeDesc = finFeeDetail.getVasReference();
					}
					if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
						String[] errParm = new String[1];
						String[] valueParm = new String[1];
						valueParm[0] = feeTypeDesc;
						errParm[0] = valueParm[0];
						detail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65019", errParm, valueParm), usrLanguage));
						auditDetails.add(detail);
						error = true;
						break;
					} else if (finFeeDetail.getPaidAmount().compareTo(totalPaidAmount) != 0) {
						String[] errParm = new String[2];
						String[] valueParm = new String[1];
						valueParm[0] = feeTypeDesc;
						errParm[0] = ":" + valueParm[0];
						errParm[1] = ":" + valueParm[0];
						detail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65018", errParm, valueParm), usrLanguage));
						auditDetails.add(detail);
						error = true;
						break;
					}
					totalFee = totalFee.add(finFeeDetail.getPaidAmount());

				}
			}
		}

		if (!financeDetail.isActionSave() && financeDetail.isUpFrentFee() && !error) {
			BigDecimal totalPaidAmt = getFinReceiptDetailDAO().getFinReceiptDetailsByFinRef(
					financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			if (totalPaidAmt.compareTo(totalFee) != 0) {
				detail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "FUF001", null, null), usrLanguage));
				auditDetails.add(detail);
			}
		}

		logger.debug(Literal.LEAVING);

		return finFeeAuditDetails;
	}

	private AuditDetail validateFinFeeDetail(AuditDetail auditDetail, String usrLanguage, String method,
			boolean isWIF) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinFeeDetail finFeeDetail = (FinFeeDetail) auditDetail.getModelData();
		FinFeeDetail tempFinFinDetail = null;
		if (finFeeDetail.isWorkflow()) {
			tempFinFinDetail = getFinFeeDetailDAO().getFinFeeDetailById(finFeeDetail, isWIF, "_Temp");
		}
		FinFeeDetail befFinFeeDetail = getFinFeeDetailDAO().getFinFeeDetailById(finFeeDetail, isWIF, "");
		FinFeeDetail oldFinFeeDetail = finFeeDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finFeeDetail.getFeeTypeDesc());
		errParm[0] = PennantJavaUtil.getLabel("FeeType") + ":" + valueParm[0];

		if (finFeeDetail.isNew()) { // for New record or new record into work
										// flow

			if (!finFeeDetail.isWorkflow()) {// With out Work flow only new
													// records
				if (befFinFeeDetail != null) { // Record Already Exists in the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
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
			if (!finFeeDetail.isWorkflow()) { // With out Work flow for update
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
		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(finFeeDetail.getTaxComponent())
				&& finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) != 0
				&& !PennantConstants.method_doReject.equals(method)
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(finFeeDetail.getRecordStatus())) {
			String[] valueParam = new String[1];
			valueParam[0] = finFeeDetail.getFeeTypeCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91137", valueParam)));
		}

		// Fee scheduled methods (Include to First Installment, entire tenor, N
		// installments, Paid by customer and Waived by bank) should not allowed
		// if Tax applicable
		String feeScheduleMethod = finFeeDetail.getFeeScheduleMethod();
		if (finFeeDetail.isTaxApplicable()
				&& (CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT.equals(feeScheduleMethod)
						|| CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS.equals(feeScheduleMethod)
						|| CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR.equals(feeScheduleMethod)
						|| CalculationConstants.REMFEE_WAIVED_BY_BANK.equals(feeScheduleMethod)
						|| CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod))) {
			String[] valueParam = new String[2];
			valueParam[0] = feeScheduleMethod;
			valueParam[1] = finFeeDetail.getFeeTypeCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91139", valueParam)));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFeeDetail.isWorkflow()) {
			auditDetail.setBefImage(befFinFeeDetail);
		}
		return auditDetail;
	}

	private AuditDetail validateFinFeeReceipts(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinFeeReceipt finFeeReceipt = (FinFeeReceipt) auditDetail.getModelData();
		FinFeeReceipt tempFinFeeReceipt = null;

		if (finFeeReceipt.isWorkflow()) {
			tempFinFeeReceipt = getFinFeeReceiptDAO().getFinFeeReceiptById(finFeeReceipt, "_Temp");
		}

		FinFeeReceipt befFinFeeReceipt = getFinFeeReceiptDAO().getFinFeeReceiptById(finFeeReceipt, "");
		FinFeeReceipt oldFinFeeReceipt = finFeeReceipt.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finFeeReceipt.getFeeTypeDesc());
		errParm[0] = PennantJavaUtil.getLabel("FeeType") + ":" + valueParm[0];

		if (finFeeReceipt.isNew()) { // for New record or new record into work
											// flow
			if (!finFeeReceipt.isWorkflow()) {// With out Work flow only new
													// records
				if (befFinFeeReceipt != null) { // Record Already Exists in the
													// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finFeeReceipt.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																									// records
																								// type
																								// is
																								// new
					if (befFinFeeReceipt != null || tempFinFeeReceipt != null) { // if
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
					if (befFinFeeReceipt == null || tempFinFeeReceipt != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!finFeeReceipt.isWorkflow()) { // With out Work flow for update
													// and delete
				if (befFinFeeReceipt == null) { // if records not exists in the
													// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinFeeReceipt != null
							&& !oldFinFeeReceipt.getLastMntOn().equals(befFinFeeReceipt.getLastMntOn())) {
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
				if (tempFinFeeReceipt == null) { // if records not exists in the
														// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
				if (tempFinFeeReceipt != null && oldFinFeeReceipt != null
						&& !oldFinFeeReceipt.getLastMntOn().equals(tempFinFeeReceipt.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFeeReceipt.isWorkflow()) {
			auditDetail.setBefImage(befFinFeeReceipt);
		}

		return auditDetail;
	}

	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {
		this.finFeeDetailDAO.updateTaxPercent(taxPercent);
	}

	@Override
	public void convertGSTFinTypeFees(FinFeeDetail finFeeDetail, FinTypeFees finTypeFee, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages) {
		String taxComponent = finTypeFee.getTaxComponent();
		BigDecimal taxableAmount = finTypeFee.getAmount();
		BigDecimal waivedAmount = finFeeDetail.getWaivedAmount();

		BigDecimal totalGST;
		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();

			finFeeDetail.setActualAmountOriginal(finTypeFee.getAmount());
			finFeeDetail.setActualAmountGST(totalGST);
			finFeeDetail.setActualAmount(totalGST.add(finTypeFee.getAmount()));

			if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				finFeeDetail.setPaidAmount(finTypeFee.getAmount().add(totalGST));
				finFeeDetail.setPaidAmountOriginal(finTypeFee.getAmount());
				finFeeDetail.setPaidAmountGST(totalGST);
			}

		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			finFeeDetail.setNetAmount(finTypeFee.getAmount());
			finFeeDetail.setNetAmountGST(totalGST);
			finFeeDetail.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			finFeeDetail.setActualAmountOriginal(taxableAmount.subtract(totalGST));

			finFeeDetail.setActualAmountGST(totalGST);
			finFeeDetail.setActualAmount(taxableAmount);

			if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				finFeeDetail.setPaidAmount(finTypeFee.getAmount());
			}
		}
	}

	@Override
	public void processGSTCalForRule(FinFeeDetail finFeeDetail, BigDecimal taxableAmount, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages, boolean apiRequest) {

		String taxComponent = finFeeDetail.getTaxComponent();
		BigDecimal waivedAmount = finFeeDetail.getWaivedAmount();

		BigDecimal totalGST;
		if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, taxComponent)) {
			if ((!apiRequest && !finFeeDetail.isFeeModified()) || !finFeeDetail.isAlwModifyFee()) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				finFeeDetail.setActualAmountOriginal(taxableAmount);
				finFeeDetail.setActualAmountGST(totalGST);
				finFeeDetail.setActualAmount(totalGST.add(taxableAmount));

				if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finFeeDetail.getFeeScheduleMethod())) {
					finFeeDetail.setPaidAmount(totalGST.add(taxableAmount));
					finFeeDetail.setPaidAmountOriginal(taxableAmount);
					finFeeDetail.setPaidAmountGST(totalGST);
				}
			}

			if (apiRequest) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				finFeeDetail.setActualAmountOriginal(taxableAmount);
				finFeeDetail.setActualAmountGST(totalGST);
				finFeeDetail.setActualAmount(totalGST.add(taxableAmount));

				if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finFeeDetail.getFeeScheduleMethod())) {
					finFeeDetail.setPaidAmount(totalGST.add(taxableAmount));
					finFeeDetail.setPaidAmountOriginal(taxableAmount);
					finFeeDetail.setPaidAmountGST(totalGST);
				}

			}

		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			finFeeDetail.setNetAmount(taxableAmount);
			finFeeDetail.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			finFeeDetail.setNetAmountGST(totalGST);
			finFeeDetail.setActualAmountOriginal(totalGST.add(finFeeDetail.getWaivedAmount()));

			finFeeDetail.setActualAmountGST(totalGST);
			finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(totalGST));

			if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finFeeDetail.getFeeScheduleMethod())) {
				finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(totalGST));
				finFeeDetail.setPaidAmountOriginal(totalGST.add(finFeeDetail.getWaivedAmount()));
				finFeeDetail.setPaidAmountGST(totalGST);
			}
		}

	}

	@Override
	public void processGSTCalForPercentage(FinFeeDetail finFeeDetail, BigDecimal taxableAmount,
			FinanceDetail financeDetail, Map<String, BigDecimal> taxPercentages, boolean apiRequest) {
		logger.debug(Literal.ENTERING);

		// 27-08-19: Fee Defaults setting when comes from API
		BigDecimal totalGST;
		if ((!apiRequest && finFeeDetail.isFeeModified()) || (apiRequest && finFeeDetail.isAlwModifyFee())) {
			if (apiRequest) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				finFeeDetail.setActualAmountOriginal(taxableAmount);
				finFeeDetail.setActualAmountGST(totalGST);
				finFeeDetail.setActualAmount(totalGST.add(taxableAmount));

				if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finFeeDetail.getFeeScheduleMethod())) {
					finFeeDetail.setPaidAmount(totalGST.add(taxableAmount));
					finFeeDetail.setPaidAmountOriginal(taxableAmount);
					finFeeDetail.setPaidAmountGST(totalGST);
				}
			}
			return;
		}

		String taxComponent = finFeeDetail.getTaxComponent();
		BigDecimal waivedAmount = finFeeDetail.getWaivedAmount();

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			if ((!apiRequest && !finFeeDetail.isFeeModified()) || !finFeeDetail.isAlwModifyFee()) {
				totalGST = GSTCalculator.getExclusiveGST(taxableAmount, taxPercentages).gettGST();
				finFeeDetail.setActualAmountOriginal(taxableAmount);
				finFeeDetail.setActualAmountGST(totalGST);
				finFeeDetail.setActualAmount(totalGST.add(taxableAmount));

				if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finFeeDetail.getFeeScheduleMethod())) {
					finFeeDetail.setPaidAmount(totalGST.add(taxableAmount));
					finFeeDetail.setPaidAmountOriginal(taxableAmount);
					finFeeDetail.setPaidAmountGST(totalGST);
				}
			}

		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxComponent)) {
			totalGST = GSTCalculator.getInclusiveGST(taxableAmount, waivedAmount, taxPercentages).gettGST();

			finFeeDetail.setNetAmount(taxableAmount);
			finFeeDetail.setNetAmountGST(totalGST);
			finFeeDetail.setNetAmountOriginal(taxableAmount.subtract(totalGST));
			finFeeDetail
					.setActualAmountOriginal(finFeeDetail.getNetAmountOriginal().add(finFeeDetail.getWaivedAmount()));

			finFeeDetail.setActualAmountGST(totalGST);
			finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(totalGST));

			if (CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(finFeeDetail.getFeeScheduleMethod())) {
				finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(totalGST));
				finFeeDetail.setPaidAmountOriginal(
						finFeeDetail.getPaidAmount().add(finFeeDetail.getWaivedAmount()).subtract(totalGST));
				finFeeDetail.setPaidAmountGST(totalGST);
			}
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
			Object exereslut = this.ruleExecutionUtil.executeRule(sqlRule, executionMap, finCcy,
					RuleReturnType.DECIMAL);
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
	public void calculateFees(FinFeeDetail fee, FinScheduleData scheduleData, Map<String, BigDecimal> taxPercentages) {
		FinanceMain financeMain = scheduleData.getFinanceMain();
		String finEvent = fee.getFinEvent();

		if (AccountEventConstants.ACCEVENT_ADDDBSP.equals(finEvent)) {
			AdvancePaymentUtil.calculateLOSAdvPayment(scheduleData, fee);
		} else if (AccountEventConstants.ACCEVENT_ADDDBSN.equals(finEvent)) {
			AdvanceRuleCode advanceRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());
			if (advanceRule != null
					&& (advanceRule == AdvanceRuleCode.ADVINT || advanceRule == AdvanceRuleCode.ADVEMI)) {
				List<String> list = new ArrayList<>();
				list.add(AccountEventConstants.ACCEVENT_ADDDBSP);
				list.add(AccountEventConstants.ACCEVENT_ADDDBSN);
				String finReference = financeMain.getFinReference();
				List<FinFeeDetail> fees = finFeeDetailDAO.getFeeDetails(finReference, advanceRule.name(), list);
				AdvancePaymentUtil.calculateLMSAdvPayment(scheduleData, fee, fees);
			}
		}

		calculateFees(fee, financeMain, taxPercentages);
	}

	@Override
	public void calculateFees(FinFeeDetail finFeeDetail, FinanceMain financeMain,
			Map<String, BigDecimal> taxPercentages) {
		logger.debug(Literal.ENTERING);

		BigDecimal waivedAmount = finFeeDetail.getWaivedAmount();
		BigDecimal netAmountOriginal = finFeeDetail.getActualAmountOriginal().subtract(waivedAmount);
		BigDecimal paidAmountOriginal = finFeeDetail.getPaidAmountOriginal();

		FinTaxDetails finTaxDetails = null;
		if (finFeeDetail.getFinTaxDetails() == null) {
			finTaxDetails = new FinTaxDetails();
		} else {
			finTaxDetails = finFeeDetail.getFinTaxDetails();
		}

		//CESS Calculations
		TaxHeader taxHeader = finFeeDetail.getTaxHeader();
		Taxes cess = null;
		if (taxHeader == null) {
			taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			finFeeDetail.setTaxHeader(taxHeader);
		}
		List<Taxes> taxDetails = taxHeader.getTaxDetails();
		if (CollectionUtils.isNotEmpty(taxDetails)) {
			for (Taxes taxes : taxDetails) {
				if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
					cess = taxes;
				}
			}
		}

		if (finFeeDetail.isTaxApplicable()) {

			BigDecimal cGST = taxPercentages.get(RuleConstants.CODE_CGST);
			BigDecimal sGST = taxPercentages.get(RuleConstants.CODE_SGST);
			BigDecimal iGST = taxPercentages.get(RuleConstants.CODE_IGST);
			BigDecimal uGST = taxPercentages.get(RuleConstants.CODE_UGST);
			BigDecimal totalGST = taxPercentages.get(RuleConstants.CODE_TOTAL_GST);

			finFeeDetail.setCgst(cGST);
			finFeeDetail.setIgst(iGST);
			finFeeDetail.setSgst(uGST);
			finFeeDetail.setUgst(sGST);
			finFeeDetail.setTaxPercent(totalGST);

			TaxAmountSplit taxSplit;
			if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {

				//CESS percentage
				if (cess == null) {
					cess = new Taxes();
					cess.setTaxType(RuleConstants.CODE_CESS);
					if (taxHeader.getTaxDetails() == null) {
						taxHeader.setTaxDetails(new ArrayList<>());
					}
					taxHeader.getTaxDetails().add(cess);
				}
				cess.setTaxPerc(taxPercentages.get(RuleConstants.CODE_CESS));

				//Actual Amounts
				BigDecimal actualOriginal = finFeeDetail.getActualAmountOriginal();
				taxSplit = GSTCalculator.getExclusiveGST(actualOriginal, taxPercentages);
				finTaxDetails.setActualCGST(taxSplit.getcGST());
				finTaxDetails.setActualIGST(taxSplit.getiGST());
				finTaxDetails.setActualSGST(taxSplit.getsGST());
				finTaxDetails.setActualUGST(taxSplit.getuGST());
				finTaxDetails.setActualTGST(taxSplit.gettGST());
				cess.setActualTax(taxSplit.getCess());

				finFeeDetail.setActualAmountGST(taxSplit.gettGST());
				finFeeDetail.setActualAmount(actualOriginal.add(taxSplit.gettGST()));

				// Paid Amounts
				taxSplit = GSTCalculator.getExclusiveGST(paidAmountOriginal, taxPercentages);
				finTaxDetails.setPaidCGST(taxSplit.getcGST());
				finTaxDetails.setPaidIGST(taxSplit.getiGST());
				finTaxDetails.setPaidSGST(taxSplit.getsGST());
				finTaxDetails.setPaidUGST(taxSplit.getuGST());
				finTaxDetails.setPaidTGST(taxSplit.gettGST());
				cess.setPaidTax(taxSplit.getCess());

				finFeeDetail.setPaidAmountGST(taxSplit.gettGST());
				finFeeDetail
						.setPaidAmount(paidAmountOriginal.add(taxSplit.gettGST()).subtract(finFeeDetail.getPaidTDS()));

				// Net Amounts
				taxSplit = GSTCalculator.getExclusiveGST(netAmountOriginal, taxPercentages);
				finTaxDetails.setNetCGST(taxSplit.getcGST());
				finTaxDetails.setNetIGST(taxSplit.getiGST());
				finTaxDetails.setNetSGST(taxSplit.getsGST());
				finTaxDetails.setNetUGST(taxSplit.getuGST());
				finTaxDetails.setNetTGST(taxSplit.gettGST());
				cess.setNetTax(taxSplit.getCess());

				finFeeDetail.setNetAmountGST(taxSplit.gettGST());
				finFeeDetail.setNetAmountOriginal(netAmountOriginal);
				finFeeDetail.setNetAmount(netAmountOriginal.add(taxSplit.gettGST()).subtract(finFeeDetail.getNetTDS()));

				// Remaining Fee
				BigDecimal remainingAmountOriginal = finFeeDetail.getActualAmountOriginal()
						.subtract(finFeeDetail.getPaidAmountOriginal()).subtract(waivedAmount);
				taxSplit = GSTCalculator.getExclusiveGST(remainingAmountOriginal, taxPercentages);
				finTaxDetails.setRemFeeSGST(taxSplit.getcGST());
				finTaxDetails.setRemFeeIGST(taxSplit.getiGST());
				finTaxDetails.setRemFeeCGST(taxSplit.getsGST());
				finTaxDetails.setRemFeeUGST(taxSplit.getuGST());
				finTaxDetails.setRemFeeTGST(taxSplit.gettGST());
				cess.setRemFeeTax(taxSplit.getCess());

				finTaxDetails.setRemFeeTGST(taxSplit.gettGST());
				finFeeDetail.setRemainingFeeGST(taxSplit.gettGST());
				finFeeDetail.setRemainingFeeOriginal(remainingAmountOriginal);
				finFeeDetail.setRemainingFee(
						remainingAmountOriginal.add(taxSplit.gettGST()).subtract(finFeeDetail.getRemTDS()));

				// Waived Amounts
				taxSplit = GSTCalculator.getExclusiveGST(waivedAmount, taxPercentages);
				finTaxDetails.setWaivedCGST(taxSplit.getcGST());
				finTaxDetails.setWaivedIGST(taxSplit.getiGST());
				finTaxDetails.setWaivedSGST(taxSplit.getsGST());
				finTaxDetails.setWaivedUGST(taxSplit.getuGST());
				finTaxDetails.setWaivedTGST(taxSplit.gettGST());
				finFeeDetail.setWaivedGST(taxSplit.gettGST());
				cess.setWaivedTax(taxSplit.getCess());
				finFeeDetail.setTaxHeader(taxHeader);

			} else if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE,
					finFeeDetail.getTaxComponent())) {
				if (cess == null) {
					cess = new Taxes();
					cess.setTaxType(RuleConstants.CODE_CESS);
					if (taxHeader.getTaxDetails() == null) {
						taxHeader.setTaxDetails(new ArrayList<>());
					}
					taxHeader.getTaxDetails().add(cess);
				}
				//Net Amount
				BigDecimal totalNetFee = finFeeDetail.getNetAmount().subtract(waivedAmount)
						.add(finFeeDetail.getNetTDS());

				taxSplit = GSTCalculator.getInclusiveGST(totalNetFee, taxPercentages);
				cess.setTaxPerc(taxPercentages.get(RuleConstants.CODE_CESS));

				finTaxDetails.setNetCGST(taxSplit.getcGST());
				finTaxDetails.setNetIGST(taxSplit.getiGST());
				finTaxDetails.setNetSGST(taxSplit.getsGST());
				finTaxDetails.setNetUGST(taxSplit.getuGST());
				finTaxDetails.setNetTGST(taxSplit.gettGST());
				cess.setNetTax(taxSplit.getCess());

				finFeeDetail.setNetAmountOriginal(totalNetFee.subtract(taxSplit.gettGST()));
				finFeeDetail.setNetAmountGST(taxSplit.gettGST());
				finFeeDetail.setNetAmount(totalNetFee.subtract(finFeeDetail.getNetTDS()));

				BigDecimal netFeeOriginal = taxSplit.getNetAmount();
				BigDecimal netTGST = taxSplit.gettGST();

				// Actual Amounts
				if (BigDecimal.ZERO.compareTo(waivedAmount) == 0) {
					finFeeDetail.setActualAmountOriginal(finFeeDetail.getNetAmountOriginal());
					finFeeDetail.setActualAmountGST(finFeeDetail.getNetAmountGST());
					finFeeDetail.setActualAmount(finFeeDetail.getNetAmount());

					finTaxDetails.setActualCGST(finTaxDetails.getNetCGST());
					finTaxDetails.setActualIGST(finTaxDetails.getNetIGST());
					finTaxDetails.setActualSGST(finTaxDetails.getNetSGST());
					finTaxDetails.setActualUGST(finTaxDetails.getNetUGST());
					finTaxDetails.setActualTGST(finTaxDetails.getNetTGST());
					cess.setActualTax(taxSplit.getCess());
				} else {
					taxSplit = GSTCalculator.getInclusiveGST(netFeeOriginal.subtract(waivedAmount), taxPercentages);
					finFeeDetail.setActualAmountOriginal(totalNetFee.subtract(netTGST));
					finFeeDetail.setActualAmountGST(taxSplit.gettGST());
					finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(taxSplit.gettGST()));

					finTaxDetails.setActualCGST(taxSplit.getcGST());
					finTaxDetails.setActualIGST(taxSplit.getiGST());
					finTaxDetails.setActualSGST(taxSplit.getsGST());
					finTaxDetails.setActualUGST(taxSplit.getuGST());
					finTaxDetails.setActualTGST(taxSplit.gettGST());
					cess.setActualTax(taxSplit.getCess());
				}

				// Paid Amounts
				BigDecimal totalPaidFee = finFeeDetail.getPaidAmount().add(finFeeDetail.getPaidTDS());
				taxSplit = GSTCalculator.getInclusiveGST(totalPaidFee, taxPercentages);

				finTaxDetails.setPaidCGST(taxSplit.getcGST());
				finTaxDetails.setPaidIGST(taxSplit.getiGST());
				finTaxDetails.setPaidSGST(taxSplit.getsGST());
				finTaxDetails.setPaidUGST(taxSplit.getuGST());
				cess.setPaidTax(taxSplit.getCess());

				finFeeDetail.setPaidAmountOriginal(taxSplit.getNetAmount().subtract(taxSplit.gettGST()));
				finFeeDetail.setPaidAmountGST(taxSplit.gettGST());
				finTaxDetails.setPaidTGST(taxSplit.gettGST());

				// Remaining Amount
				BigDecimal totalRemainingFee = totalNetFee.subtract(totalPaidFee);

				taxSplit = GSTCalculator.getInclusiveGST(totalRemainingFee, taxPercentages);

				finTaxDetails.setRemFeeCGST(taxSplit.getcGST());
				finTaxDetails.setRemFeeIGST(taxSplit.getiGST());
				finTaxDetails.setRemFeeSGST(taxSplit.getsGST());
				finTaxDetails.setRemFeeUGST(taxSplit.getuGST());
				cess.setRemFeeTax(taxSplit.getCess());

				finFeeDetail.setRemainingFeeOriginal(taxSplit.getNetAmount().subtract(taxSplit.gettGST()));
				finFeeDetail.setRemainingFee(totalRemainingFee.subtract(finFeeDetail.getRemTDS()));
				finFeeDetail.setRemainingFeeGST(taxSplit.gettGST());
				finTaxDetails.setRemFeeTGST(taxSplit.gettGST());

				// Waived Amounts
				taxSplit = GSTCalculator.getInclusiveGST(waivedAmount, taxPercentages);
				finTaxDetails.setWaivedCGST(taxSplit.getcGST());
				finTaxDetails.setWaivedIGST(taxSplit.getiGST());
				finTaxDetails.setWaivedSGST(taxSplit.getsGST());
				finTaxDetails.setWaivedUGST(taxSplit.getuGST());
				finTaxDetails.setWaivedTGST(taxSplit.gettGST());
				finFeeDetail.setWaivedGST(taxSplit.gettGST());
				cess.setWaivedTax(taxSplit.getCess());
			}
		} else {

			// Net Amount
			finFeeDetail.setNetAmountOriginal(netAmountOriginal);
			finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
			finFeeDetail.setNetAmount(netAmountOriginal.subtract(finFeeDetail.getNetTDS()));

			// Remaining Amount
			finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmountOriginal().subtract(waivedAmount)
					.subtract(finFeeDetail.getPaidAmount()));
			finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(waivedAmount)
					.subtract(finFeeDetail.getPaidAmount()).subtract(finFeeDetail.getRemTDS()));

			// Paid Amount
			finFeeDetail.setPaidAmountOriginal(finFeeDetail.getPaidAmount().add(finFeeDetail.getPaidTDS()));
			finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);

			// Actual Fee
			finTaxDetails.setActualCGST(BigDecimal.ZERO);
			finTaxDetails.setActualIGST(BigDecimal.ZERO);
			finTaxDetails.setActualSGST(BigDecimal.ZERO);
			finTaxDetails.setActualUGST(BigDecimal.ZERO);
			finTaxDetails.setActualTGST(BigDecimal.ZERO);

			// Paid Fee
			finTaxDetails.setPaidCGST(BigDecimal.ZERO);
			finTaxDetails.setPaidIGST(BigDecimal.ZERO);
			finTaxDetails.setPaidSGST(BigDecimal.ZERO);
			finTaxDetails.setPaidUGST(BigDecimal.ZERO);
			finTaxDetails.setPaidTGST(BigDecimal.ZERO);

			// Net Fee
			finTaxDetails.setNetCGST(BigDecimal.ZERO);
			finTaxDetails.setNetIGST(BigDecimal.ZERO);
			finTaxDetails.setNetSGST(BigDecimal.ZERO);
			finTaxDetails.setNetUGST(BigDecimal.ZERO);
			finTaxDetails.setNetTGST(BigDecimal.ZERO);

			// Remaining Fee
			finTaxDetails.setRemFeeCGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeIGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeSGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeUGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeTGST(BigDecimal.ZERO);

			// Waived Amounts
			finTaxDetails.setWaivedCGST(BigDecimal.ZERO);
			finTaxDetails.setWaivedIGST(BigDecimal.ZERO);
			finTaxDetails.setWaivedSGST(BigDecimal.ZERO);
			finTaxDetails.setWaivedUGST(BigDecimal.ZERO);
			finTaxDetails.setWaivedTGST(BigDecimal.ZERO);
			finFeeDetail.setWaivedGST(BigDecimal.ZERO);
		}

		finFeeDetail.setFinTaxDetails(finTaxDetails);

		logger.debug(Literal.LEAVING);
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
	public Map<String, Object> prepareGstMappingDetails(String fromBranchCode, String dftBranch,
			String highPriorityState, String highPriorityCountry, FinanceTaxDetail taxDetail, String branchCode) {

		HashMap<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;

		if (StringUtils.isNotBlank(dftBranch)) {
			if (fromBranchCode == null) {
				fromBranchCode = dftBranch;
			}

			Branch fromBranch = branchService.getBranch(fromBranchCode);
			String proVCntry = "" + fromBranch.getBranchProvince() + "@" + fromBranch.getBranchProvince();
			Province fromState = provinceService.getProvince(proVCntry);

			if (fromState != null) {
				gstExecutionMap.put("fromState", fromState.getCPProvince());
				gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
			}

			String toStateCode = "";
			String toCountryCode = "";

			if (taxDetail != null && StringUtils.isNotBlank(taxDetail.getApplicableFor())
					&& !PennantConstants.List_Select.equals(taxDetail.getApplicableFor())
					&& StringUtils.isNotBlank(taxDetail.getProvince())
					&& StringUtils.isNotBlank(taxDetail.getCountry())) {
				toStateCode = taxDetail.getProvince();
				toCountryCode = taxDetail.getCountry();
				gstExempted = taxDetail.isTaxExempted();
			} else {
				toStateCode = highPriorityState;
				toCountryCode = highPriorityCountry;
			}

			if (StringUtils.isBlank(toCountryCode) || StringUtils.isBlank(toStateCode)) { // if
																								// toCountry
																							// is
																							// not
																							// available
				gstExecutionMap.put("toState", "");
				gstExecutionMap.put("toUnionTerritory", 2);
				gstExecutionMap.put("toStateGstExempted", "");
			} else {

				String toCounSt = "" + toCountryCode + "@" + toStateCode;
				Province toState = provinceService.getProvince(toCounSt);

				if (toState == null) {
					gstExecutionMap.put("toState", "");
					gstExecutionMap.put("toUnionTerritory", 2);
					gstExecutionMap.put("toStateGstExempted", "");
				} else {
					gstExecutionMap.put("toState", toState.getCPProvince());
					gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
					gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
				}
			}

			gstExecutionMap.put("gstExempted", gstExempted);

		} else if (StringUtils.isNotBlank(branchCode)) {

			Branch fromBranch = branchService.getBranch(branchCode);
			String toCounSt = "" + fromBranch.getBranchCountry() + "@" + fromBranch.getBranchProvince();
			Province fromState = provinceService.getProvince(toCounSt);

			if (fromState != null) {
				gstExecutionMap.put("fromState", fromState.getCPProvince());
				gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
			}

			String toStateCode = "";
			String toCountryCode = "";

			if (taxDetail != null && StringUtils.isNotBlank(taxDetail.getApplicableFor())
					&& !PennantConstants.List_Select.equals(taxDetail.getApplicableFor())
					&& StringUtils.isNotBlank(taxDetail.getProvince())
					&& StringUtils.isNotBlank(taxDetail.getCountry())) {
				toStateCode = taxDetail.getProvince();
				toCountryCode = taxDetail.getCountry();
				gstExempted = taxDetail.isTaxExempted();
			}

			if (fromState != null && (StringUtils.isBlank(toCountryCode) || StringUtils.isBlank(toStateCode))) {
				gstExecutionMap.put("toState", fromState.getCPProvince());
				gstExecutionMap.put("toUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("toStateGstExempted", fromState.isTaxExempted());
			} else {
				String proVCntry = "" + toCountryCode + "@" + toStateCode;
				Province toState = provinceService.getProvince(proVCntry);

				if (toState == null && fromState != null) {
					gstExecutionMap.put("toState", fromState.getCPProvince());
					gstExecutionMap.put("toUnionTerritory", fromState.isUnionTerritory());
					gstExecutionMap.put("toStateGstExempted", fromState.isTaxExempted());
				} else if (toState != null) {
					gstExecutionMap.put("toState", toState.getCPProvince());
					gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
					gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
				}
			}

			gstExecutionMap.put("gstExempted", gstExempted);

		}

		return gstExecutionMap;
	}

	@Override
	public Map<String, Object> prepareGstMapping(String fromStateCode, String toStateCode) {
		Map<String, Object> gstExecutionMap = new HashMap<>();
		boolean gstExempted = false;

		Province fromState = getProvinceDAO().getProvinceById(fromStateCode, "");

		if (fromState != null) {
			gstExecutionMap.put("fromState", fromState.getCPProvince());
			gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
			gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
		}

		Province toState = getProvinceDAO().getProvinceById(toStateCode, "");

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
	public long getFinFeeTypeIdByFeeType(String feeTypeCode, String finReference) {
		return finFeeDetailDAO.getFinFeeTypeIdByFeeType(feeTypeCode, finReference, "_AView");
	}

	@Override
	public Branch getBranchById(String branchCode, String type) {
		return branchDAO.getBranchById(branchCode, type);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public FinTaxDetailsDAO getFinTaxDetailsDAO() {
		return finTaxDetailsDAO;
	}

	public void setFinTaxDetailsDAO(FinTaxDetailsDAO finTaxDetailsDAO) {
		this.finTaxDetailsDAO = finTaxDetailsDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public ProvinceDAO getProvinceDAO() {
		return provinceDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public TaxHeaderDetailsService getTaxHeaderDetailsService() {
		return taxHeaderDetailsService;
	}

	public void setTaxHeaderDetailsService(TaxHeaderDetailsService taxHeaderDetailsService) {
		this.taxHeaderDetailsService = taxHeaderDetailsService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}
}