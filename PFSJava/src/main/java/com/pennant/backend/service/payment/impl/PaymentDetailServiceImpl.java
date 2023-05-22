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
 * * FileName : PaymentDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentDetail</b>.<br>
 */
public class PaymentDetailServiceImpl extends GenericService<PaymentDetail> implements PaymentDetailService {
	private static final Logger logger = LogManager.getLogger(PaymentDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PaymentDetailDAO paymentDetailDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceMainDAO financeMainDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private CrossLoanKnockOffDAO crossLoanKnockOffDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		PaymentDetail paymentDetail = (PaymentDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (paymentDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (paymentDetail.isNewRecord()) {
			paymentDetail.setId(Long.parseLong(paymentDetailDAO.save(paymentDetail, tableType)));
			auditHeader.getAuditDetail().setModelData(paymentDetail);
			auditHeader.setAuditReference(String.valueOf(paymentDetail.getPaymentDetailId()));
		} else {
			paymentDetailDAO.update(paymentDetail, tableType);
		}

		auditHeaderDAO.addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentDetail paymentDetail = (PaymentDetail) auditHeader.getAuditDetail().getModelData();
		paymentDetailDAO.delete(paymentDetail, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public PaymentDetail getPaymentDetail(long paymentDetailId, String amountType) {
		return paymentDetailDAO.getPaymentDetail(paymentDetailId, "_View");
	}

	public PaymentDetail getApprovedPaymentDetail(long paymentDetailId, String amountType) {
		return paymentDetailDAO.getPaymentDetail(paymentDetailId, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentDetail paymentDetail = new PaymentDetail();
		BeanUtils.copyProperties((PaymentDetail) auditHeader.getAuditDetail().getModelData(), paymentDetail);

		paymentDetailDAO.delete(paymentDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentDetail.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(paymentDetailDAO.getPaymentDetail(paymentDetail.getPaymentDetailId(), ""));
		}

		if (paymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			paymentDetailDAO.delete(paymentDetail, TableType.MAIN_TAB);
		} else {
			paymentDetail.setRoleCode("");
			paymentDetail.setNextRoleCode("");
			paymentDetail.setTaskId("");
			paymentDetail.setNextTaskId("");
			paymentDetail.setWorkflowId(0);

			if (paymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				paymentDetail.setRecordType("");
				paymentDetailDAO.save(paymentDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				paymentDetail.setRecordType("");
				paymentDetailDAO.update(paymentDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(paymentDetail);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentDetail paymentDetail = (PaymentDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		paymentDetailDAO.delete(paymentDetail, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<AuditDetail> setPaymentDetailAuditData(List<PaymentDetail> paymentDetailList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new PaymentDetail(), new PaymentDetail().getExcludeFields());
		for (int i = 0; i < paymentDetailList.size(); i++) {
			PaymentDetail detail = paymentDetailList.get(i);
			if (StringUtils.isEmpty(detail.getRecordType())) {
				continue;
			}
			boolean isRcdType = false;
			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				detail.setNewRecord(true);
			}
			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> processPaymentDetails(List<AuditDetail> auditDetails, TableType type, String methodName,
			long linkedTranId, long finID) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<ManualAdviseMovements> adviseMovements = new ArrayList<>();

		for (int i = 0; i < auditDetails.size(); i++) {
			AuditDetail auditDetail = auditDetails.get(i);
			PaymentDetail pd = (PaymentDetail) auditDetail.getModelData();

			ErrorDetail errorDetail = isValidateReceipt(pd);

			if (errorDetail != null) {
				auditDetail.setErrorDetail(errorDetail);
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			TableType tableType = TableType.TEMP_TAB;
			if (TableType.MAIN_TAB.equals(type)) {
				tableType = TableType.MAIN_TAB;
				approveRec = true;
				pd.setRoleCode("");
				pd.setNextRoleCode("");
				pd.setTaskId("");
				pd.setNextTaskId("");
				pd.setWorkflowId(0);
			}
			if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (pd.isNewRecord()) {
				saveRecord = true;
				if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					pd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					pd.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					pd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (pd.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (pd.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = pd.getRecordType();
				recordStatus = pd.getRecordStatus();
				pd.setRecordType("");
				pd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {

				if (pd.getTaxHeader() != null) {
					Long taxheaderID = taxHeaderDetailsDAO.save(pd.getTaxHeader(), tableType.getSuffix());
					pd.setTaxHeaderId(taxheaderID);

					List<Taxes> taxList = pd.getTaxHeader().getTaxDetails();
					for (Taxes taxes : taxList) {
						taxes.setReferenceId(taxheaderID);
					}
					taxHeaderDetailsDAO.saveTaxes(taxList, tableType.getSuffix());
				}

				String detailId = paymentDetailDAO.save(pd, tableType);
				pd.setPaymentDetailId(Long.valueOf(detailId));

				// Payments processing
				if ("doApprove".equals(methodName)) {
					ManualAdviseMovements movement = doApprove(pd);
					if (movement != null) {
						adviseMovements.add(movement);
					}
				} else {
					saveOrUpdate(pd);
				}
			}
			if (updateRecord) {
				if (pd.getTaxHeader() != null && pd.getTaxHeader().getId() <= 0) {
					long taxheaderID = taxHeaderDetailsDAO.save(pd.getTaxHeader(), tableType.getSuffix());
					pd.setTaxHeaderId(taxheaderID);

					List<Taxes> taxList = pd.getTaxHeader().getTaxDetails();
					for (Taxes taxes : taxList) {
						taxes.setReferenceId(taxheaderID);
					}
					taxHeaderDetailsDAO.saveTaxes(taxList, tableType.getSuffix());
				}

				paymentDetailDAO.update(pd, tableType);
				saveOrUpdate(pd);
			}
			if (deleteRecord) {
				paymentDetailDAO.delete(pd, tableType);
				// Payments processing
				doReject(pd);
			}
			if (approveRec) {
				pd.setRecordType(rcdType);
				pd.setRecordStatus(recordStatus);
			}

			if ("doApprove".equals(methodName)) {
				if (!PennantConstants.RECORD_TYPE_NEW.equals(pd.getRecordType())) {
					pd.setBefImage(paymentDetailDAO.getPaymentDetail(pd.getPaymentId(), ""));
				}
			}
			auditDetail.setModelData(pd);
		}

		// GST Invoice preparation for Receivable Advises

		if (CollectionUtils.isNotEmpty(adviseMovements)) {

			FinanceDetail fd = new FinanceDetail();
			FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "", false);
			FinScheduleData schdData = fd.getFinScheduleData();
			schdData.setFinanceMain(fm);
			fd.setCustomerDetails(null);
			fd.setFinanceTaxDetail(null);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(fd);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setMovements(adviseMovements);

			this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<PaymentDetail> paymentDetailList, TableType tableType, String auditTranType,
			long paymentId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		PaymentDetail paymentDetail = null;
		if (paymentDetailList != null && !paymentDetailList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new PaymentDetail(),
					new PaymentDetail().getExcludeFields());
			for (int i = 0; i < paymentDetailList.size(); i++) {
				paymentDetail = paymentDetailList.get(i);
				if (StringUtils.isNotEmpty(paymentDetail.getRecordType())
						|| StringUtils.isEmpty(tableType.toString())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							paymentDetail.getBefImage(), paymentDetail));
				}

				if (paymentDetail.getTaxHeader() != null) {
					taxHeaderDetailsDAO.deleteById(paymentDetail.getTaxHeader().getHeaderId(), tableType.getSuffix());
					taxHeaderDetailsDAO.delete(paymentDetail.getTaxHeader().getHeaderId(), tableType.getSuffix());
				}
			}
			paymentDetailDAO.deleteList(paymentDetail, tableType);

			for (PaymentDetail detail : paymentDetailList) {
				doReject(detail);
			}
		}
		return auditDetails;
	}

	@Override
	public List<PaymentDetail> getPaymentDetailList(long paymentId, String type) {
		return paymentDetailDAO.getPaymentDetailList(paymentId, type);
	}

	private void saveOrUpdate(PaymentDetail pd) {
		logger.debug(Literal.ENTERING);

		// Excess Amount Reserve
		if (!AdviseType.isPayable(pd.getAmountType())) {
			// Excess Amount make utilization
			FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(pd.getPaymentDetailId(),
					pd.getReferenceId(), RepayConstants.RECEIPTTYPE_PAYABLE);

			if (exReserve == null) {
				FinExcessAmount fea = finExcessAmountDAO.getFinExcessByID(pd.getReferenceId());

				BigDecimal transferamt = crossLoanKnockOffDAO.getTransferAmount(fea.getExcessID());

				if (pd.getAmount().compareTo(fea.getBalanceAmt()) > 0 && transferamt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal reserveamt = fea.getReservedAmt().subtract(transferamt).add(fea.getAmount());
					fea.setReservedAmt(reserveamt);
					fea.setBalanceAmt(BigDecimal.ZERO);
					finExcessAmountDAO.updateExcess(fea);
					finExcessAmountDAO.saveExcessReserveLog(pd.getPaymentDetailId(), pd.getReferenceId(),
							pd.getAmount(), RepayConstants.RECEIPTTYPE_PAYABLE);
				} else {
					// Update Excess Amount in Reserve
					finExcessAmountDAO.updateExcessReserve(pd.getReferenceId(), pd.getAmount());

					// Save Excess Reserve Log Amount
					finExcessAmountDAO.saveExcessReserveLog(pd.getPaymentDetailId(), pd.getReferenceId(),
							pd.getAmount(), RepayConstants.RECEIPTTYPE_PAYABLE);
				}
			} else {
				if (pd.getAmount().compareTo(exReserve.getReservedAmt()) != 0) {
					BigDecimal diffInReserve = pd.getAmount().subtract(exReserve.getReservedAmt());

					// Update Reserve Amount in FinExcessAmount
					finExcessAmountDAO.updateExcessReserve(pd.getReferenceId(), diffInReserve);

					// Update Excess Reserve Log
					finExcessAmountDAO.updateExcessReserveLog(pd.getPaymentDetailId(), pd.getReferenceId(),
							diffInReserve, RepayConstants.RECEIPTTYPE_PAYABLE);
				}
			}
		} else {
			// Payable Amount make utilization
			ManualAdviseReserve payableReserve = manualAdviseDAO.getPayableReserve(pd.getPaymentDetailId(),
					pd.getReferenceId());

			BigDecimal amount = pd.getAmount();
			if (pd.getTaxHeader() != null
					&& StringUtils.equals(pd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
				// GST Calculations
				List<Taxes> taxDetails = pd.getTaxHeader().getTaxDetails();
				BigDecimal gstAmount = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(taxDetails)) {
					for (Taxes taxes : taxDetails) {
						gstAmount = gstAmount.add(taxes.getPaidTax());
					}
				}
				amount = amount.subtract(gstAmount);
			}
			if (payableReserve == null) {
				// Update Payable Amount in Reserve
				manualAdviseDAO.updatePayableReserveAmount(pd.getReferenceId(), amount);

				// Save Payable Reserve Log Amount
				manualAdviseDAO.savePayableReserveLog(pd.getPaymentDetailId(), pd.getReferenceId(), amount);
			} else {
				if (amount.compareTo(payableReserve.getReservedAmt()) != 0) {
					BigDecimal diffInReserve = amount.subtract(payableReserve.getReservedAmt());

					// Update Reserve Amount in Manual Advise
					manualAdviseDAO.updatePayableReserveAmount(pd.getReferenceId(), diffInReserve);

					// Update Payable Reserve Log
					manualAdviseDAO.updatePayableReserveLog(pd.getPaymentDetailId(), pd.getReferenceId(),
							diffInReserve);
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doReject(PaymentDetail paymentDetail) {
		logger.debug("Entering");

		// Excess Amount Reserve
		if (!AdviseType.isPayable(paymentDetail.getAmountType())) {
			// Excess Amount make utilization
			FinExcessAmountReserve exReserve = finExcessAmountDAO.getExcessReserve(paymentDetail.getPaymentDetailId(),
					paymentDetail.getReferenceId(), RepayConstants.RECEIPTTYPE_PAYABLE);
			if (exReserve != null) {

				FinExcessAmount fea = finExcessAmountDAO.getFinExcessByID(paymentDetail.getReferenceId());

				BigDecimal transferamt = crossLoanKnockOffDAO.getTransferAmount(fea.getExcessID());

				if (fea.getBalanceAmt().equals(BigDecimal.ZERO) && transferamt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal balanceAmt = fea.getReservedAmt().subtract(transferamt);
					fea.setReservedAmt(transferamt);
					fea.setBalanceAmt(balanceAmt);
					finExcessAmountDAO.updateExcess(fea);
					finExcessAmountDAO.deleteExcessReserve(paymentDetail.getPaymentDetailId(),
							paymentDetail.getReferenceId(), RepayConstants.RECEIPTTYPE_PAYABLE);
				} else {

					// Update Reserve Amount in FinExcessAmount
					finExcessAmountDAO.updateExcessReserve(paymentDetail.getReferenceId(),
							exReserve.getReservedAmt().negate());

					// Delete Reserved Log against Excess and Receipt ID
					finExcessAmountDAO.deleteExcessReserve(paymentDetail.getPaymentDetailId(),
							paymentDetail.getReferenceId(), RepayConstants.RECEIPTTYPE_PAYABLE);
				}
			} else { // Payable Amount Reserve
				// Payable Amount make utilization
				ManualAdviseReserve payableReserve = manualAdviseDAO
						.getPayableReserve(paymentDetail.getPaymentDetailId(), paymentDetail.getReferenceId());
				if (payableReserve != null) {
					// Update Reserve Amount in ManualAdvise
					manualAdviseDAO.updatePayableReserve(paymentDetail.getReferenceId(),
							payableReserve.getReservedAmt().negate());

					// Delete Reserved Log against Payable Advise ID and Receipt ID
					manualAdviseDAO.deletePayableReserve(paymentDetail.getPaymentDetailId(),
							paymentDetail.getReferenceId());
				}
			}
		}
		logger.debug("Leaving");
	}

	private ManualAdviseMovements processManualAdvice(PaymentDetail pd) {
		logger.debug(Literal.ENTERING);
		ManualAdviseMovements manualMovement = null;

		ManualAdvise advise = new ManualAdvise();
		advise.setAdviseID(pd.getReferenceId());

		BigDecimal amount = pd.getAmount();
		if (pd.getTaxHeader() != null
				&& StringUtils.equals(pd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
			// GST Calculations
			List<Taxes> taxDetails = pd.getTaxHeader().getTaxDetails();
			BigDecimal gstAmount = BigDecimal.ZERO;
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					gstAmount = gstAmount.add(taxes.getPaidTax());

					if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
						advise.setPaidCGST(taxes.getPaidTax());
					} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
						advise.setPaidSGST(taxes.getPaidTax());
					} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
						advise.setPaidIGST(taxes.getPaidTax());
					} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
						advise.setPaidUGST(taxes.getPaidTax());
					} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
						advise.setPaidCESS(taxes.getPaidTax());
					}
				}
			}
			amount = amount.subtract(gstAmount);
		}

		advise.setPaidAmount(amount);
		advise.setBalanceAmt(amount.negate());

		String finSource = pd.getFinSource();
		if (!UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD.equals(finSource)
				&& !UploadConstants.FINSOURCE_ID_AUTOPROCESS.equals(finSource)
				&& !UploadConstants.FINSOURCE_ID_UPLOAD.equals(finSource)
				&& !FinanceConstants.FEE_REFUND_APPROVAL.equals(finSource)) {
			advise.setReservedAmt(amount.negate());
			advise.setBalanceAmt(BigDecimal.ZERO);
		}

		manualAdviseDAO.updateAdvPayment(advise, TableType.MAIN_TAB);

		// Delete Reserved Log against Advise and Receipt Seq ID
		if (!UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD.equals(finSource)
				&& !UploadConstants.FINSOURCE_ID_AUTOPROCESS.equals(finSource)
				&& !UploadConstants.FINSOURCE_ID_UPLOAD.equals(finSource)
				&& !FinanceConstants.FEE_REFUND_APPROVAL.equals(finSource)) {
			manualAdviseDAO.deletePayableReserve(pd.getPaymentDetailId(), pd.getReferenceId());
		}

		// Payable Advise Movement Creation
		manualMovement = new ManualAdviseMovements();
		manualMovement.setAdviseID(pd.getReferenceId());
		manualMovement.setReceiptID(pd.getPaymentDetailId());
		manualMovement.setReceiptSeqID(0);
		manualMovement.setMovementDate(SysParamUtil.getAppDate());
		manualMovement.setMovementAmount(amount);
		manualMovement.setPaidAmount(amount);

		TaxHeader taxHeader = new TaxHeader();
		taxHeader.setNewRecord(true);
		taxHeader.setRecordType(PennantConstants.RCD_ADD);
		taxHeader.setVersion(taxHeader.getVersion() + 1);

		if (AdviseType.isPayable(pd.getAmountType())) {
			List<Taxes> taxDetails = pd.getTaxHeader().getTaxDetails();
			taxHeader.setTaxDetails(taxDetails);
		}

		if (taxHeader.getTaxDetails() == null) {
			taxHeader.setTaxDetails(new ArrayList<>());
		}

		manualMovement.setTaxHeader(taxHeader);
		manualAdviseDAO.saveMovement(manualMovement, TableType.MAIN_TAB.getSuffix());

		ManualAdvise manualAdvise = manualAdviseDAO.getManualAdviseById(pd.getReferenceId(), "_AView");

		if (manualAdvise == null) {
			manualMovement.setFeeTypeCode(pd.getFeeTypeCode());
			manualMovement.setFeeTypeDesc(pd.getFeeTypeDesc());
			manualMovement.setTaxApplicable(pd.isTaxApplicable());
			manualMovement.setTaxComponent(pd.getTaxComponent());
		} else {
			manualMovement.setFeeTypeCode(manualAdvise.getFeeTypeCode());
			manualMovement.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
			manualMovement.setTaxApplicable(manualAdvise.isTaxApplicable());
			manualMovement.setTaxComponent(manualAdvise.getTaxComponent());
		}

		logger.debug(Literal.LEAVING);

		return manualMovement;
	}

	private ManualAdviseMovements doApprove(PaymentDetail pd) {
		logger.debug(Literal.ENTERING);

		if (AdviseType.isPayable(pd.getAmountType())) {
			return processManualAdvice(pd);
		}

		String finSource = pd.getFinSource();
		if (!UploadConstants.FINSOURCE_ID_AUTOPROCESS.equals(finSource)
				&& !UploadConstants.FINSOURCE_ID_UPLOAD.equals(finSource)
				|| PennantConstants.FINSOURCE_ID_API.equals(finSource)) {
			finExcessAmountDAO.updateUtilise(pd.getReferenceId(), pd.getAmount());
		} else {
			finExcessAmountDAO.updateUtiliseOnly(pd.getReferenceId(), pd.getAmount());
		}

		// Delete Reserved Log against Excess and Receipt ID
		if (!StringUtils.equals(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD, finSource)
				&& !StringUtils.equals(PennantConstants.FINSOURCE_ID_API, finSource)) {
			finExcessAmountDAO.deleteExcessReserve(pd.getPaymentDetailId(), pd.getReferenceId(),
					RepayConstants.RECEIPTTYPE_PAYABLE);
		}

		// Excess Movement Creation
		FinExcessMovement movement = new FinExcessMovement();
		movement.setExcessID(pd.getReferenceId());
		movement.setReceiptID(pd.getPaymentDetailId());
		movement.setMovementType(RepayConstants.RECEIPTTYPE_PAYABLE);
		movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
		movement.setAmount(pd.getAmount());

		finExcessAmountDAO.saveExcessMovement(movement);

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void paymentReversal(PaymentInstruction paymentInstruction) {
		logger.debug(Literal.ENTERING);

		List<PaymentDetail> detailsList = paymentDetailDAO.getPaymentDetailList(paymentInstruction.getPaymentId(),
				TableType.MAIN_TAB.getSuffix());
		if (detailsList != null && !detailsList.isEmpty()) {
			for (PaymentDetail paymentDetail : detailsList) {
				if (!AdviseType.isPayable(paymentDetail.getAmountType())) {
					finExcessAmountDAO.updateExcessAmount(paymentDetail.getReferenceId(), "U",
							paymentDetail.getAmount().negate());
				} else {
					manualAdviseDAO.reverseUtilise(paymentDetail.getReferenceId(), paymentDetail.getAmount());
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public int updatePaymentStatus(PaymentInstruction paymentInstruction) {
		logger.debug(Literal.ENTERING);
		return paymentInstructionDAO.updatePaymentInstrucionStatus(paymentInstruction, TableType.MAIN_TAB);
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long paymentId, String type) {
		return paymentInstructionDAO.getPaymentInstruction(paymentId, type);
	}

	@Override
	public PaymentInstruction getPaymentInstructionDetails(long paymentId, String type) {
		return paymentInstructionDAO.getPaymentInstructionDetails(paymentId, type);
	}

	@Override
	public long getPymntsCustId(long paymentId) {
		return paymentInstructionDAO.getPymntsCustId(paymentId);
	}

	private ErrorDetail isValidateReceipt(PaymentDetail pd) {
		logger.debug(Literal.ENTERING);

		if (AdviseType.isPayable(pd.getAmountType())) {
			logger.debug(Literal.LEAVING);
			return null;
		}

		String receipModeStatus = finReceiptHeaderDAO.getReceiptModeStatuByExcessId(pd.getReferenceId());

		if (RepayConstants.PAYSTATUS_CANCEL.equals(receipModeStatus)
				|| RepayConstants.PAYSTATUS_BOUNCE.equals(receipModeStatus)) {
			return ErrorUtil.getErrorDetail(new ErrorDetail("REFUND_002", null));
		}

		return null;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setPaymentDetailDAO(PaymentDetailDAO paymentDetailDAO) {
		this.paymentDetailDAO = paymentDetailDAO;
	}

	@Autowired
	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	@Autowired
	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setCrossLoanKnockOffDAO(CrossLoanKnockOffDAO crossLoanKnockOffDAO) {
		this.crossLoanKnockOffDAO = crossLoanKnockOffDAO;
	}
}