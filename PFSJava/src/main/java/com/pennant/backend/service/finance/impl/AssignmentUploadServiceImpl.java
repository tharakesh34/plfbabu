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

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.AssignmentDAO;
import com.pennant.backend.dao.applicationmaster.AssignmentDealDAO;
import com.pennant.backend.dao.assignmentupload.AssignmentUploadDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.assignmentupload.AssignmentUpload;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.AssignmentUploadService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * Service implementation for methods that depends on <b>AssignmentUpload</b>.<br>
 * 
 */
public class AssignmentUploadServiceImpl extends GenericService<AssignmentUpload> implements AssignmentUploadService {
	private static final Logger logger = LogManager.getLogger(AssignmentUploadServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private AssignmentUploadDAO assignmentUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private AssignmentDAO assignmentDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private AssignmentDealDAO assignmentDealDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		String tableType = "";
		AssignmentUpload assignmentUpload = (AssignmentUpload) auditHeader.getAuditDetail().getModelData();

		if (assignmentUpload.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (assignmentUpload.isNewRecord()) {
			assignmentUploadDAO.save(assignmentUpload, tableType);
			auditHeader.getAuditDetail().setModelData(assignmentUpload);
			auditHeader.setAuditReference(String.valueOf(assignmentUpload.getId()));
		} else {
			assignmentUploadDAO.update(assignmentUpload, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public List<AssignmentUpload> getAssignmentUploadsByUploadId(long uploadId) {
		return assignmentUploadDAO.getAssignmentUploadsByUploadId(uploadId, "_TView");
	}

	@Override
	public List<AssignmentUpload> getApprovedAssignmentUploadsByUploadId(long uploadId) {
		return assignmentUploadDAO.getAssignmentUploadsByUploadId(uploadId, "_View");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		AssignmentUpload assignmentUpload = new AssignmentUpload();
		BeanUtils.copyProperties((AssignmentUpload) auditHeader.getAuditDetail().getModelData(), assignmentUpload);

		if (PennantConstants.RECORD_TYPE_DEL.equals(assignmentUpload.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			// assignmentUploadDAO.delete(assignmentUpload, ""); // because delete will not be applicable here
		} else {
			assignmentUpload.setRoleCode("");
			assignmentUpload.setNextRoleCode("");
			assignmentUpload.setTaskId("");
			assignmentUpload.setNextTaskId("");
			assignmentUpload.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(assignmentUpload.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				assignmentUpload.setRecordType("");
				assignmentUploadDAO.save(assignmentUpload, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				assignmentUpload.setRecordType("");
				assignmentUploadDAO.update(assignmentUpload, "");
			}
		}

		assignmentUploadDAO.deleteByUploadId(assignmentUpload.getUploadId(), "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(assignmentUpload);

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		// AssignmentUpload assignmentUpload = (AssignmentUpload) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// assignmentUploadDAO.delete(assignmentUpload, "_TEMP"); // because delete will not be applicable here

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, null);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public List<ErrorDetail> validateAssignmentUploads(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		// AssignmentUploads
		if (uploadHeader.getAuditDetailMap().get("AssignmentUploads") != null) {

			int successCount = 0;
			int failCount = 0;
			auditDetails = uploadHeader.getAuditDetailMap().get("AssignmentUploads");

			for (AuditDetail auditDetail : auditDetails) {
				AuditDetail refundAudit = validation(auditDetail, usrLanguage, method, uploadHeader.getEntityCode());
				AssignmentUpload assignmentUpload = (AssignmentUpload) auditDetail.getModelData();
				if (UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS.equals(assignmentUpload.getStatus())) {
					successCount++;
				} else {
					failCount++;
				}
				List<ErrorDetail> details = refundAudit.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}

			// Success and failed count updation
			uploadHeader.setSuccessCount(successCount);
			uploadHeader.setFailedCount(failCount);
			uploadHeader.setTotalRecords(successCount + failCount);
			auditHeader.getAuditDetail().setModelData(uploadHeader);
		}

		logger.debug(Literal.LEAVING);

		return errorDetails;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, String entityCode) {
		logger.debug(Literal.ENTERING);

		AssignmentUpload assignmentUpload = (AssignmentUpload) auditDetail.getModelData();

		// Check the unique keys.
		if (assignmentUpload.isNewRecord()
				&& !UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(assignmentUpload.getStatus())) {
			validateLengths(assignmentUpload);
			if (!UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(assignmentUpload.getStatus())) {
				validateData(assignmentUpload, entityCode);
			}
		}

		auditDetail.setModelData(assignmentUpload);

		return auditDetail;
	}

	@Override
	public void validateAssignmentScreenLevel(AssignmentUpload assignmentUpload, String entityCode) {
		validateLengths(assignmentUpload);
		if (!UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(assignmentUpload.getStatus())) {
			validateData(assignmentUpload, entityCode);
		}
	}

	@Override
	public List<AuditDetail> setAssignmentUploadsAuditData(List<AssignmentUpload> assignmentUploadList,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new AssignmentUpload(),
				new AssignmentUpload().getExcludeFields());

		for (int i = 0; i < assignmentUploadList.size(); i++) {

			AssignmentUpload assignmentUpload = assignmentUploadList.get(i);

			if (StringUtils.isEmpty(assignmentUpload.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(assignmentUpload.getRecordType())) {
				assignmentUpload.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(assignmentUpload.getRecordType())) {
				assignmentUpload.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(assignmentUpload.getRecordType())) {
				assignmentUpload.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				assignmentUpload.setNewRecord(true);
			}
			if (!PennantConstants.TRAN_WF.equals(auditTranType)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(assignmentUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(assignmentUpload.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(assignmentUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], assignmentUpload.getBefImage(),
					assignmentUpload));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	@Override
	public List<AuditDetail> processAssignmentUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type,
			String postingBranch) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			AssignmentUpload assignmentUpload = (AssignmentUpload) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				assignmentUpload.setRoleCode("");
				assignmentUpload.setNextRoleCode("");
				assignmentUpload.setTaskId("");
				assignmentUpload.setNextTaskId("");
				assignmentUpload.setWorkflowId(0);
			}

			String recordType = assignmentUpload.getRecordType();
			assignmentUpload.setUploadId(uploadId);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(recordType)) {
				deleteRecord = true;
			} else if (assignmentUpload.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(recordType)) {
					assignmentUpload.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recordType)) {
					assignmentUpload.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recordType)) {
					assignmentUpload.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(recordType)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(recordType)) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(recordType)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (assignmentUpload.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = assignmentUpload.getRecordStatus();
				assignmentUpload.setRecordType("");
				assignmentUpload.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				assignmentUploadDAO.save(assignmentUpload, type);

				// Success case updating the Assignment Id
				if (approveRec && UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS.equals(assignmentUpload.getStatus())) {
					generateAccounting(assignmentUpload, postingBranch);
					// Update Assignment Id in Finance Main
					financeMainDAO.updateAssignmentId(assignmentUpload.getFinID(), assignmentUpload.getAssignmentId());
				}
			}
			if (updateRecord) {
				assignmentUploadDAO.update(assignmentUpload, type);
			}
			if (deleteRecord) {
				// assignmentUploadDAO.delete(assignmentUpload, type); // because delete will not be applicable
				// here
			}
			if (approveRec) {
				assignmentUpload.setRecordType(rcdType);
				assignmentUpload.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(assignmentUpload);
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	private void generateAccounting(AssignmentUpload assignUpload, String postingBranch) {
		long finID = assignUpload.getFinID();
		String finReference = assignUpload.getFinReference();

		Assignment assignment = this.assignmentDAO.getAssignment(assignUpload.getAssignmentId(), "");
		FinanceMain financeMain = this.financeMainDAO.getFinanceForAssignments(finID);

		// if assignment is not available or actual days is less than or equal to 0
		if (assignment == null || financeMain == null) {
			return;
		}
		BigDecimal sharePercent = assignment.getSharingPercentage();

		// Get the Finance Schedule details
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);

		if (CollectionUtils.isNotEmpty(schedules)) {
			FinanceScheduleDetail curSchd = null;
			FinanceScheduleDetail nextSchd = null;
			int scheduleCount = -1;
			BigDecimal schAmount = BigDecimal.ZERO; // for dues calculation
			BigDecimal schPaidAmount = BigDecimal.ZERO; // for dues calculation

			// get the next schedule payment
			for (FinanceScheduleDetail schedule : schedules) {

				if (DateUtil.compare(schedule.getSchDate(), assignUpload.getAssignmentDate()) >= 0) {
					// Partial Settlement schedule
					if (schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) {
						if (schedule.getProfitSchd().compareTo(schedule.getProfitCalc()) == 0) { // if schedule payment
																									// and partial
																									// settlement both
																									// should be in same
																									// day
							nextSchd = schedule;
							break;
						} else {
							continue;
						}
					} else { // normal schedule
						nextSchd = schedule;
						break;
					}
				}
				schAmount = schAmount.add(schedule.getPrincipalSchd()).add(schedule.getProfitSchd());
				schPaidAmount = schPaidAmount.add(schedule.getSchdPftPaid()).add(schedule.getSchdPriPaid());

				scheduleCount++;
			}

			// get the current schedule payment
			if (scheduleCount == -1) {
				curSchd = nextSchd;
			} else {
				curSchd = schedules.get(scheduleCount);
			}

			// difference between assignment date and current schedule date
			int effectiveDiffDays = DateUtil.getDaysBetween(assignUpload.getAssignmentDate(), curSchd.getSchDate());

			// Effective days Interest amount calculation
			BigDecimal effectiveProfit = BigDecimal.ZERO;

			if (nextSchd != null) {
				effectiveProfit = nextSchd.getProfitCalc().multiply(new BigDecimal(effectiveDiffDays))
						.divide(new BigDecimal(nextSchd.getNoOfDays()), 9, RoundingMode.HALF_DOWN);
			}

			// Total Interest calculation from Current schedule date.
			BigDecimal afterAssignProfit = BigDecimal.ZERO;
			for (FinanceScheduleDetail schedule : schedules) {
				if (DateUtil.compare(schedule.getSchDate(), nextSchd.getSchDate()) >= 0) {
					afterAssignProfit = afterAssignProfit.add(schedule.getProfitCalc());
				}
			}

			int effectSchdCount = -1;
			FinanceScheduleDetail effectSchd = null;
			FinanceScheduleDetail effectNextSchd = null;
			BigDecimal assignAmount = BigDecimal.ZERO;

			// get the Effective next date schedule
			for (FinanceScheduleDetail schedule : schedules) {
				if (DateUtil.compare(schedule.getSchDate(), assignUpload.getEffectiveDate()) > 0) {
					if (schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) { // Partial Settlement case
						if (schedule.getProfitSchd().compareTo(schedule.getProfitCalc()) == 0) {
							// if schedule payment and partial settlement both should be in same day
							effectNextSchd = schedule;
							break;
						} else {
							continue;
						}
					} else {
						effectNextSchd = schedule;
						break;
					}
				} else if (DateUtil.compare(schedule.getSchDate(), assignUpload.getEffectiveDate()) == 0) {
					if (schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) { // Partial Settlement case
						if (schedule.getProfitSchd().compareTo(schedule.getProfitCalc()) == 0) {
							// if schedule payment and partial settlement both should be in same day
							assignAmount = schedule.getClosingBalance();
							effectNextSchd = schedule;
							break;
						} else {
							continue;
						}
					} else {
						effectNextSchd = schedule;
						assignAmount = schedule.getClosingBalance();
						break;
					}
				}

				effectSchdCount++;
			}

			// Assigning Principal Amount
			if (assignAmount.compareTo(BigDecimal.ZERO) == 0) {
				effectSchd = schedules.get(effectSchdCount);
				assignAmount = effectSchd.getClosingBalance();
			}
			assignAmount = assignAmount.multiply(sharePercent).divide(new BigDecimal(100), 9, RoundingMode.HALF_DOWN);
			assignAmount = CalculationUtil.roundAmount(assignAmount, RoundingMode.HALF_DOWN.name(), 0); // if rounding
																										// required

			// if customer paid any Schedule amount between the assignment date and effective date
			BigDecimal effectivePaidPriAmt = BigDecimal.ZERO;

			if (effectNextSchd != null) {
				if (DateUtil.compare(effectNextSchd.getSchDate(), nextSchd.getSchDate()) != 0) {
					effectivePaidPriAmt = effectNextSchd.getSchdPriPaid().subtract(effectNextSchd.getPartialPaidAmt()); // Because
																														// of
																														// Partial
																														// Settlement
																														// Case
					effectivePaidPriAmt = effectivePaidPriAmt.multiply(sharePercent).divide(new BigDecimal(100), 9,
							RoundingMode.HALF_DOWN);
					effectivePaidPriAmt = CalculationUtil.roundAmount(effectivePaidPriAmt,
							RoundingMode.HALF_DOWN.name(), 0); // if rounding required
				}
			}

			BigDecimal assginExcessAmt = BigDecimal.ZERO;
			List<FinExcessAmount> list = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
					RepayConstants.EXAMOUNTTYPE_EXCESS);
			for (FinExcessAmount fea : list) {
				assginExcessAmt = assginExcessAmt.add(fea.getBalanceAmt());
			}

			assginExcessAmt = assginExcessAmt.multiply(sharePercent).divide(new BigDecimal(100), 9,
					RoundingMode.HALF_DOWN);
			assginExcessAmt = CalculationUtil.roundAmount(assginExcessAmt, RoundingMode.HALF_DOWN.name(), 0);

			BigDecimal assignEMIAdvAmt = BigDecimal.ZERO;
			list = finExcessAmountDAO.getExcessAmountsByRefAndType(finID, RepayConstants.EXAMOUNTTYPE_EMIINADV);

			for (FinExcessAmount fea : list) {
				assignEMIAdvAmt = assignEMIAdvAmt.add(fea.getBalanceAmt());
			}
			assignEMIAdvAmt = assignEMIAdvAmt.multiply(sharePercent).divide(new BigDecimal(100), 9,
					RoundingMode.HALF_DOWN);
			assignEMIAdvAmt = CalculationUtil.roundAmount(assignEMIAdvAmt, RoundingMode.HALF_DOWN.name(), 0);

			// BPI amount calculation
			BigDecimal bpi1Amount = bpi1Calculation(schedules, assignUpload, sharePercent);
			BigDecimal bpi2Amount = bpi2Calculation(schedules, assignUpload, nextSchd, sharePercent);

			// AssignPftAmount
			BigDecimal assignPftAmount = afterAssignProfit.subtract(effectiveProfit);
			assignPftAmount = assignPftAmount.multiply(sharePercent).divide(new BigDecimal(100), 9,
					RoundingMode.HALF_DOWN);
			assignPftAmount = CalculationUtil.roundAmount(assignPftAmount, RoundingMode.HALF_DOWN.name(), 0); // if
																												// rounding
																												// required

			// AssignODAmount
			BigDecimal assignODAmount = schAmount.subtract(schPaidAmount);
			assignODAmount = assignODAmount.multiply(sharePercent).divide(new BigDecimal(100), 9,
					RoundingMode.HALF_DOWN);
			assignODAmount = CalculationUtil.roundAmount(assignODAmount, RoundingMode.HALF_DOWN.name(), 0); // if
																											// rounding
																											// required

			// if customer paid any Part Payment amount between the assignment date and effective date
			BigDecimal assignPartPayment = BigDecimal.ZERO;
			for (FinanceScheduleDetail schedule : schedules) {
				if (schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0
						&& DateUtil.compare(schedule.getSchDate(), assignUpload.getEffectiveDate()) >= 0
						&& DateUtil.compare(schedule.getSchDate(), assignUpload.getAssignmentDate()) <= 0) {
					assignPartPayment = assignPartPayment.add(schedule.getPartialPaidAmt());
				}
			}
			if (assignPartPayment.compareTo(BigDecimal.ZERO) > 0) {
				assignPartPayment = assignPartPayment.multiply(sharePercent).divide(new BigDecimal(100), 9,
						RoundingMode.HALF_DOWN);
				assignPartPayment = CalculationUtil.roundAmount(assignPartPayment, RoundingMode.HALF_DOWN.name(), 0); // if
																														// rounding
																														// required
			}

			// Amount codes mapping
			AEAmountCodes amountCodes = new AEAmountCodes();
			amountCodes.setAssignPriAmount(assignAmount); // Assignment Principal Amount
			amountCodes.setAssignPftAmount(assignPftAmount); // Assignment Profit Amount
			amountCodes.setAssignODAmount(assignODAmount); // Assignment OD Amount
			amountCodes.setAssignPaidPriAmt(effectivePaidPriAmt); // Assignment Principal paid amount (if any schedule
																	// happens between Effective date and Assignment
																	// Dates)
			amountCodes.setAssignExcessAmt(assginExcessAmt); // Assignment Excess Amount
			amountCodes.setAssignEMIAdvAmt(assignEMIAdvAmt); // Assignment EMI in Advance Amount
			amountCodes.setAssignPartPayment(assignPartPayment); // Assignment Part Payment amount (if any part payments
																	// happens between Effective date and Assignment
																	// Date)
			amountCodes.setAssignmentPerc(sharePercent); // Assignment Percentage
			amountCodes.setFinType(financeMain.getFinType()); // Loan Type

			// Postings preparation
			AEEvent aeEvent = new AEEvent();

			aeEvent.setFinID(finID);
			aeEvent.setFinReference(finReference);
			aeEvent.setAccountingEvent(AccountingEvent.ASSIGNMENT);
			aeEvent.setPostDate(SysParamUtil.getAppDate());
			aeEvent.setValueDate(SysParamUtil.getDerivedAppDate());
			aeEvent.setFinType(financeMain.getFinType());
			aeEvent.setCustID(financeMain.getCustID());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setPostingUserBranch(postingBranch);
			aeEvent.setEntityCode(assignment.getEntityCode());
			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.getAcSetIDList().clear();
			long accountSetId = 0;
			if (StringUtils.isNotBlank(financeMain.getPromotionCode()) && financeMain.getPromotionSeqId() == 0) {
				accountSetId = AccountingConfigCache.getAccountSetID(financeMain.getPromotionCode(),
						AccountingEvent.ASSIGNMENT, FinanceConstants.MODULEID_PROMOTION);
			} else {
				accountSetId = AccountingConfigCache.getAccountSetID(financeMain.getFinType(),
						AccountingEvent.ASSIGNMENT, FinanceConstants.MODULEID_FINTYPE);
			}
			aeEvent.getAcSetIDList().add(accountSetId);
			aeEvent.setNewRecord(true);

			Set<String> excludeFees = null;
			List<AssignmentDealExcludedFee> excludeFeesList = this.assignmentDealDAO
					.getApprovedAssignmentDealExcludedFeeList(assignment.getDealId());
			if (CollectionUtils.isNotEmpty(excludeFeesList)) {
				excludeFees = new HashSet<String>();
				for (AssignmentDealExcludedFee excludeFee : excludeFeesList) {
					excludeFees.add(excludeFee.getFeeTypeCode());
				}
			}
			amountCodes.setBusinessvertical(financeMain.getBusinessVerticalCode());
			amountCodes.setAlwflexi(financeMain.isAlwFlexi());
			amountCodes.setFinbranch(financeMain.getFinBranch());
			amountCodes.setEntitycode(financeMain.getEntityCode());

			aeEvent.setAeAmountCodes(amountCodes);
			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();
			if (excludeFees != null) {
				dataMap.put(AccountConstants.POSTINGS_EXCLUDE_FEES, excludeFees);
			}
			dataMap = amountCodes.getDeclaredFieldValues(dataMap);
			Map<String, Object> map = financeMain.getGlSubHeadCodes();

			if (MapUtils.isEmpty(map)) {
				map.putAll(financeMainDAO.getGLSubHeadCodes(finID));
			}

			dataMap.put("emptype", map.get("EMPTYPE"));
			dataMap.put("branchcity", map.get("BRANCHCITY"));
			dataMap.put("fincollateralreq", map.get("FINCOLLATERALREQ"));
			dataMap.put("btloan", financeMain.getLoanCategory());
			aeEvent.setDataMap(dataMap);

			// Prepared Postings execution
			postingsPreparationUtil.postAccounting(aeEvent);

			// update BPI Amounts for Reporting purpose in Finance Profit Details
			FinanceProfitDetail finProfitDetails = new FinanceProfitDetail();
			finProfitDetails.setFinID(finID);
			finProfitDetails.setFinReference(finReference);
			finProfitDetails.setAssignBPI1(bpi1Amount);
			finProfitDetails.setAssignBPI2(bpi2Amount);
			profitDetailsDAO.updateAssignmentBPIAmounts(finProfitDetails);
		}
	}

	private BigDecimal bpi1Calculation(List<FinanceScheduleDetail> finScheduleDetails,
			AssignmentUpload assignmentUpload, BigDecimal sharePercent) {

		// BPI calculation
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		int prvSchdCount = -1;

		// get the current schedule
		for (FinanceScheduleDetail schedule : finScheduleDetails) {
			if (DateUtil.compare(schedule.getSchDate(), assignmentUpload.getEffectiveDate()) >= 0) {
				if (schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) { // Partial Settlement case
					if (schedule.getProfitSchd().compareTo(schedule.getProfitCalc()) == 0) {
						// if schedule payment and partial settlement both should be in same day
						curSchd = schedule;
						break;
					} else {
						continue;
					}
				} else {
					curSchd = schedule;
					break;
				}
			}
			prvSchdCount++;
		}

		// get the previous schedule
		prvSchd = finScheduleDetails.get(prvSchdCount);

		// get the actual difference days from Effective date and previous schedule date
		int effectiveDaysDiff = DateUtil.getDaysBetween(assignmentUpload.getEffectiveDate(), prvSchd.getSchDate());

		BigDecimal bpi1Amount = BigDecimal.ZERO;

		if (curSchd != null) {
			bpi1Amount = curSchd.getProfitCalc().divide(new BigDecimal(curSchd.getNoOfDays()), 9,
					RoundingMode.HALF_DOWN); // Gives profit for 1 day
		}

		bpi1Amount = bpi1Amount.multiply(new BigDecimal(effectiveDaysDiff));
		bpi1Amount = bpi1Amount.multiply(sharePercent).divide(new BigDecimal(100), 9, RoundingMode.HALF_DOWN);// Sharing
																												// percentage
		bpi1Amount = CalculationUtil.roundAmount(bpi1Amount, RoundingMode.HALF_DOWN.name(), 0); // if rounding required

		return bpi1Amount;
	}

	private BigDecimal bpi2Calculation(List<FinanceScheduleDetail> finScheduleDetails,
			AssignmentUpload assignmentUpload, FinanceScheduleDetail nextSchd, BigDecimal sharePercent) {

		FinanceScheduleDetail curSchd = null;
		// get the current schedule
		for (FinanceScheduleDetail schedule : finScheduleDetails) {
			if (DateUtil.compare(schedule.getSchDate(), assignmentUpload.getEffectiveDate()) >= 0) {
				if (schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) { // Partial Settlement case
					if (schedule.getProfitSchd().compareTo(schedule.getProfitCalc()) == 0) {
						// if schedule payment and partial settlement both should be in same day
						curSchd = schedule;
						break;
					} else {
						continue;
					}
				} else {
					curSchd = schedule;
					break;
				}
			}
		}

		// get the actual difference days from effective date and current schedule date
		int effectDaysDiff = 0;
		BigDecimal intrestAmount = BigDecimal.ZERO;
		BigDecimal intrestAmount2 = BigDecimal.ZERO;
		int assignDiffDays = 0;

		if (curSchd != null && nextSchd != null) {
			effectDaysDiff = DateUtil.getDaysBetween(curSchd.getSchDate(), assignmentUpload.getEffectiveDate());
			intrestAmount = curSchd.getProfitCalc().divide(new BigDecimal(curSchd.getNoOfDays()), 9,
					RoundingMode.HALF_DOWN);// Gives 1 day profit
			intrestAmount = intrestAmount.multiply(new BigDecimal(effectDaysDiff));
			// Difference between assignment date and current schedule date
			assignDiffDays = DateUtil.getDaysBetween(assignmentUpload.getAssignmentDate(), curSchd.getSchDate());

			intrestAmount2 = nextSchd.getProfitCalc().divide(new BigDecimal(nextSchd.getNoOfDays()), 9,
					RoundingMode.HALF_DOWN);
			intrestAmount2 = intrestAmount2.multiply(new BigDecimal(assignDiffDays));
		}

		BigDecimal bpi2Amount = BigDecimal.ZERO;
		bpi2Amount = bpi2Amount.add(intrestAmount).add(intrestAmount2);
		bpi2Amount = bpi2Amount.multiply(sharePercent).divide(new BigDecimal(100), 9, RoundingMode.HALF_DOWN);
		bpi2Amount = CalculationUtil.roundAmount(bpi2Amount, RoundingMode.HALF_DOWN.name(), 0); // if rounding required

		return bpi2Amount;
	}

	@Override
	public List<AuditDetail> delete(List<AssignmentUpload> assignmentUploadList, String tableType, String auditTranType,
			long uploadId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (assignmentUploadList != null && !assignmentUploadList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new AssignmentUpload(),
					new AssignmentUpload().getExcludeFields());
			for (int i = 0; i < assignmentUploadList.size(); i++) {
				AssignmentUpload assignmentUpload = assignmentUploadList.get(i);
				if (StringUtils.isNotEmpty(assignmentUpload.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							assignmentUpload.getBefImage(), assignmentUpload));
				}
			}
			assignmentUploadDAO.deleteByUploadId(uploadId, tableType);
		}

		return auditDetails;

	}

	private void validateLengths(AssignmentUpload assignmentUpload) {
		int errorCount = 0;
		String reason = "";

		// Fin Reference
		if (StringUtils.isNotBlank(assignmentUpload.getFinReference())
				&& assignmentUpload.getFinReference().length() > 20) {
			errorCount++;
			reason = "Reference length should be lessthan or equal to 20";
			assignmentUpload.setFinReference(null);
		}

		if (errorCount > 0) {
			if (errorCount > 1) {
				reason = "Invalid record.";
			}
			assignmentUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
			assignmentUpload.setRejectReason(reason);
		}

	}

	private void validateData(AssignmentUpload assignmentUpload, String entityCode) {
		int errorCount = 0;
		String reason = "";
		FinanceMain fm = null;

		// FinReference
		long finID = assignmentUpload.getFinID();
		String finReference = assignmentUpload.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			errorCount++;
			reason = "Loan Reference is mandatory.";
		} else {
			fm = this.financeMainDAO.getFinanceForAssignments(finID);
			if (fm == null) {
				errorCount++;
				reason = "Invalid Loan Reference.";
			} else if (!fm.isFinIsActive()) {
				errorCount++;
				reason = "Loan Reference should be active.";
			} else if (StringUtils.isNotBlank(entityCode) && !StringUtils.equals(entityCode, fm.getEntityCode())) {
				errorCount++;
				reason = "Loan reference and assignment partner should be from same entity code.";
			} else if (fm.getAssignmentId() > 0) {
				errorCount++;
				reason = "Loan Reference is already assigned.";
			} else if (fm.getFinCurrAssetValue().compareTo(fm.getFinAssetValue()) != 0) {
				errorCount++;
				reason = "Partially disbursed loans will not be considered for Assignment.";
			} else if (fm.isAlwFlexi()) {
				errorCount++;
				reason = "Flexi loans will not be considered for Assignment.";
			} else if (assignmentUpload.isNewRecord()) {
				boolean isDuplicate = assignmentUploadDAO.getAssignmentUploadsByFinReference(finReference, 0, "_View");
				if (isDuplicate) {
					errorCount++;
					reason = "Duplicate Loan Reference.";
				}
			}
		}

		// Assignment Code
		Assignment assignment = this.assignmentDAO.getAssignment(assignmentUpload.getAssignmentId(), "_AView");
		if (assignment == null) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Invalid Assignment Code.";
			} else {
				reason = reason + " Invalid Assignment Code.";
			}
		} else if (!assignment.isActive()) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Assignment Code should be active.";
			} else {
				reason = reason + " Assignment Code should be active.";
			}
		} else if (fm != null && !StringUtils.equals(fm.getFinType(), assignment.getLoanType())) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Loan type assigned to the Assignment Code should match with the loan type of the loan.";
			} else {
				reason = reason
						+ " Loan type assigned to the Assignment Code should match with the loan type of the loan.";
			}
		} else if (fm != null && !StringUtils.equals(fm.getEntityCode(), assignment.getEntityCode())) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Entity of the Assignment Code and the assigning loan should be same.";
			} else {
				reason = reason + " Entity of the Assignment Code and the assigning loan should be same.";
			}
		}

		// Effective Date
		if (DateUtil.compare(assignmentUpload.getEffectiveDate(), SysParamUtil.getAppDate()) > 0) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Effective date should be less than or equal to application date.";
			} else {
				reason = reason + " Effective date should be less than or equal to application date.";
			}
		} else if (fm != null && DateUtil.compare(assignmentUpload.getEffectiveDate(), fm.getFinStartDate()) == 0) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Effective date should be greater than Loan Start Date.";
			} else {
				reason = reason + " Effective date should be greater than Loan Start Date.";
			}
		} else if (fm != null && DateUtil.compare(assignmentUpload.getEffectiveDate(), fm.getMaturityDate()) > 0) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Effective date should be less than Loan Maturity Date.";
			} else {
				reason = reason + " Effective date should be less than Loan Maturity Date.";
			}
		}

		// Assignment Date
		if (DateUtil.compare(assignmentUpload.getAssignmentDate(), SysParamUtil.getAppDate()) > 0) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Assignment date should be less than or equal to application date";
			} else {
				reason = reason + " Assignment date should be less than or equal to application date";
			}
		} else if (fm != null && DateUtil.compare(assignmentUpload.getAssignmentDate(), fm.getFinStartDate()) == 0) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Assignment date should be greater than Loan Start Date.";
			} else {
				reason = reason + "Assignment date should be greater than Loan Start Date.";
			}
		} else if (fm != null && DateUtil.compare(assignmentUpload.getAssignmentDate(), fm.getMaturityDate()) > 0) {
			errorCount++;
			if (StringUtils.isBlank(reason)) {
				reason = "Assignment date should be less than Loan Maturity Date.";
			} else {
				reason = reason + " Assignment date should be less than Loan Maturity Date.";
			}
		}

		if (errorCount == 0) {
			if (DateUtil.compare(assignmentUpload.getEffectiveDate(), assignmentUpload.getAssignmentDate()) > 0) {
				errorCount++;
				if (StringUtils.isBlank(reason)) {
					reason = "Effective date should be less than or equal to assignment date.";
				} else {
					reason = reason + " Effective date should be less than or equal to assignment date.";
				}
			} else {
				int maxAssignDays = SysParamUtil.getValueAsInt("ASSIGNMENT_MAX_ALLOWED_DAYS");
				int effectiveDiffDays = DateUtil.getDaysBetween(assignmentUpload.getEffectiveDate(),
						assignmentUpload.getAssignmentDate());
				if (effectiveDiffDays > maxAssignDays) {
					errorCount++;
					if (StringUtils.isBlank(reason)) {
						reason = "Difference between Assignment date and Effective date shoul be less than or equal to "
								+ maxAssignDays + " days.";
					} else {
						reason = reason
								+ " Difference between Assignment date and Effective date shoul be less than or equal to "
								+ maxAssignDays + " days.";
					}
				}
			}
		}

		if (errorCount == 0) {
			int assignmentMonth = DateUtil.getMonth(assignmentUpload.getAssignmentDate());
			int effectiveMonth = DateUtil.getMonth(assignmentUpload.getEffectiveDate());
			int appMonth = DateUtil.getMonth(SysParamUtil.getAppDate());
			int assignmentYear = DateUtil.getYear(assignmentUpload.getAssignmentDate());
			int effectiveYear = DateUtil.getYear(assignmentUpload.getEffectiveDate());
			int appYear = DateUtil.getYear(SysParamUtil.getAppDate());

			if (assignmentMonth != effectiveMonth) {
				errorCount++;
				if (StringUtils.isBlank(reason)) {
					reason = "Assignment date of month and Effective date of month shoul be equal.";
				} else {
					reason = reason + " Assignment date of month and Effective date of month shoul be equal.";
				}
			} else {

				if (assignmentYear != effectiveYear) {
					errorCount++;
					if (StringUtils.isBlank(reason)) {
						reason = "Assignment date of year and Effective date of year shoul be equal.";
					} else {
						reason = reason + " Assignment date of year and Effective date of year shoul be equal.";
					}
				}
			}

			if (errorCount == 0) {
				if (appMonth != effectiveMonth) {
					errorCount++;
					reason = "Assignment date of month and Effective date of month shoul be equal to application date of month.";
				} else {
					if (appYear != effectiveYear) {
						errorCount++;
						reason = "Assignment date of year and Effective date of year shoul be equal to application date of year.";
					}
				}
			}
		}

		if (errorCount > 0) {
			assignmentUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
			assignmentUpload.setRejectReason(reason);
		}
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setAssignmentUploadDAO(AssignmentUploadDAO assignmentUploadDAO) {
		this.assignmentUploadDAO = assignmentUploadDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setAssignmentDealDAO(AssignmentDealDAO assignmentDealDAO) {
		this.assignmentDealDAO = assignmentDealDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}