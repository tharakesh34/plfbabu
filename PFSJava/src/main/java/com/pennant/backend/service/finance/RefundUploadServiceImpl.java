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

package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.refundupload.RefundUploadDAO;
import com.pennant.backend.dao.rmtmasters.FinTypePartnerBankDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>RefundUpload</b>.<br>
 * 
 */
public class RefundUploadServiceImpl extends GenericService<RefundUpload> implements RefundUploadService {
	private static final Logger logger = LogManager.getLogger(RefundUploadServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private RefundUploadDAO refundUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FinTypePartnerBankDAO finTypePartnerBankDAO;
	private BankBranchDAO bankBranchDAO;
	private BankDetailDAO bankDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PaymentHeaderService paymentHeaderService;

	private Map<String, BigDecimal> payableAmountsMap = new HashMap<String, BigDecimal>();
	private Map<Long, BigDecimal> adviseAmountsMap = new HashMap<Long, BigDecimal>();

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the refundUploadDAO
	 */
	public RefundUploadDAO getRefundUploadDAO() {
		return refundUploadDAO;
	}

	/**
	 * @param refundUploadDAO the refundUploadDAO to set
	 */
	public void setRefundUploadDAO(RefundUploadDAO refundUploadDAO) {
		this.refundUploadDAO = refundUploadDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinTypePartnerBankDAO getFinTypePartnerBankDAO() {
		return finTypePartnerBankDAO;
	}

	public void setFinTypePartnerBankDAO(FinTypePartnerBankDAO finTypePartnerBankDAO) {
		this.finTypePartnerBankDAO = finTypePartnerBankDAO;
	}

	public BankBranchDAO getBankBranchDAO() {
		return bankBranchDAO;
	}

	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	public BankDetailDAO getBankDetailDAO() {
		return bankDetailDAO;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public PaymentHeaderService getPaymentHeaderService() {
		return paymentHeaderService;
	}

	public void setPaymentHeaderService(PaymentHeaderService paymentHeaderService) {
		this.paymentHeaderService = paymentHeaderService;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table RefundUploads/RefundUploads_Temp
	 * by using RefundUploadsDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using RefundUploadsDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtRefundUploads by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		RefundUpload refundUpload = (RefundUpload) auditHeader.getAuditDetail().getModelData();

		if (refundUpload.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (refundUpload.isNewRecord()) {
			getRefundUploadDAO().save(refundUpload, tableType);
			auditHeader.getAuditDetail().setModelData(refundUpload);
			auditHeader.setAuditReference(String.valueOf(refundUpload.getFinReference()));
		} else {
			getRefundUploadDAO().update(refundUpload, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * getRefundUploadsByUploadId fetch the details by using RefundUploadsDAO's getRefundUploadsByUploadId method.
	 * 
	 * @param finType (String)
	 * @param type    (String) ""/_Temp/_View
	 * @return RefundUploads
	 */
	@Override
	public List<RefundUpload> getRefundUploadsByUploadId(long uploadId) {
		return getRefundUploadDAO().getRefundUploadsByUploadId(uploadId, "_TView");
	}

	/**
	 * getRefundUploadsById fetch the details by using RefundUploadsDAO's getRefundUploadByRef method.
	 * 
	 * @param uploadId (long)
	 * @return RefundUploads
	 */
	@Override
	public List<RefundUpload> getApprovedRefundUploadsByUploadId(long uploadId) {
		return getRefundUploadDAO().getRefundUploadsByUploadId(uploadId, "_View");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getRefundUploadDAO().delete with
	 * parameters promotionFee,"" b) NEW Add new record in to main table by using getRefundUploadDAO().save with
	 * parameters promotionFee,"" c) EDIT Update record in the main table by using getRefundUploadDAO().update with
	 * parameters promotionFee,"" 3) Delete the record from the workFlow table by using getRefundUploadDAO().delete with
	 * parameters promotionFee,"_Temp" 4) Audit the record in to AuditHeader and AdtRefundUploads by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtRefundUploads by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		RefundUpload refundUpload = new RefundUpload();
		BeanUtils.copyProperties((RefundUpload) auditHeader.getAuditDetail().getModelData(), refundUpload);

		if (PennantConstants.RECORD_TYPE_DEL.equals(refundUpload.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			// getRefundUploadDAO().delete(refundUpload, ""); // because delete will not be applicable here
		} else {
			refundUpload.setRoleCode("");
			refundUpload.setNextRoleCode("");
			refundUpload.setTaskId("");
			refundUpload.setNextTaskId("");
			refundUpload.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(refundUpload.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				refundUpload.setRecordType("");
				getRefundUploadDAO().save(refundUpload, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				refundUpload.setRecordType("");
				getRefundUploadDAO().update(refundUpload, "");
			}
		}
		// prepare Payment Instrunctionos
		if (!UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(refundUpload.getStatus())) {
			createPaymentInstctions(refundUpload);
		}

		getRefundUploadDAO().deleteByUploadId(refundUpload.getUploadId(), "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(refundUpload);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	private void createPaymentInstctions(RefundUpload refundUpload) {
		logger.debug("Entering");

		PaymentHeader paymentHeader = preparePayments(refundUpload);
		AuditHeader paymentsAuditHeader = getAuditHeader(paymentHeader, PennantConstants.TRAN_WF);

		paymentHeaderService.doApprove(paymentsAuditHeader);

		logger.debug("Leaving");
	}

	public PaymentHeader preparePayments(RefundUpload refundUpload) {
		logger.debug("Entering");

		Timestamp sysDate = new Timestamp(System.currentTimeMillis());
		long bankBranchId = 0;
		// Payment Header
		PaymentHeader paymentHeader = new PaymentHeader();
		paymentHeader.setFinReference(refundUpload.getFinReference());
		paymentHeader.setPaymentType(DisbursementConstants.CHANNEL_PAYMENT);
		paymentHeader.setPaymentAmount(refundUpload.getPayableAmount());
		paymentHeader.setCreatedOn(sysDate);
		paymentHeader.setApprovedOn(sysDate);
		paymentHeader.setStatus(RepayConstants.PAYMENT_APPROVE);
		paymentHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		paymentHeader.setNewRecord(true);
		paymentHeader.setVersion(1);
		paymentHeader.setUserDetails(refundUpload.getUserDetails());
		paymentHeader.setLastMntBy(refundUpload.getLastMntBy());
		paymentHeader.setLastMntOn(refundUpload.getLastMntOn());
		paymentHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		if (UploadConstants.FINSOURCE_ID_UPLOAD.equals(refundUpload.getFinSource())) {
			paymentHeader.setFinSource(UploadConstants.FINSOURCE_ID_UPLOAD);
		} else if (UploadConstants.FINSOURCE_ID_API.equals(refundUpload.getFinSource())) {
			paymentHeader.setFinSource(UploadConstants.FINSOURCE_ID_API);
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(refundUpload.getFinReference(), TableType.MAIN_TAB);

		if (fm == null) {
			throw new InterfaceException("9999", "Loan Reference should not exist.");
		}

		long finID = fm.getFinID();
		String finType = fm.getFinType();

		FinTypePartnerBank finTypePartnerBank = finTypePartnerBankDAO.getFinTypePartnerBankByPartnerBankCode(
				refundUpload.getPartnerBank(), finType, refundUpload.getPaymentType());
		if (finTypePartnerBank == null) {
			throw new InterfaceException("9999", "Partner banks should not linked to Loan Type.");
		}

		if (DisbursementConstants.PAYMENT_TYPE_RTGS.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_CASH.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_IMPS.equals(refundUpload.getPaymentType())) {
			BankBranch bankBranch = this.bankBranchDAO.getBankBrachByIFSCandMICR(refundUpload.getIFSC(),
					refundUpload.getMICR(), "");

			if (bankBranch == null) {
				throw new InterfaceException("9999", "Invalid IFSC/MICR.");
			} else {
				bankBranchId = bankBranch.getBankBranchID();
			}
		}

		if (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(refundUpload.getPaymentType())) {
			BankDetail bankDetail = this.bankDetailDAO.getBankDetailById(refundUpload.getIssuingBank(), "");
			if (bankDetail == null) {
				throw new InterfaceException("9999", "Issuing Bank and IFSC not available.");
			}
		}

		// Payment Details
		List<PaymentDetail> paymentDetailList = new ArrayList<PaymentDetail>();

		if (UploadConstants.REFUNDUPLOAD_MANUAL_ADVISE_PAYABLE.equals(refundUpload.getType())) {
			BigDecimal totalAmount = BigDecimal.ZERO;
			List<ManualAdvise> advises = getManualAdviseDAO().getManualAdviseByRefAndFeeCode(finID,
					AdviseType.PAYABLE.id(), refundUpload.getFeeType());

			if (CollectionUtils.isNotEmpty(advises)) {

				List<PaymentDetail> tempDetailList = new ArrayList<PaymentDetail>();
				for (ManualAdvise advise : advises) {
					totalAmount = totalAmount.add(advise.getBalanceAmt());
				}

				if (BigDecimal.ZERO.compareTo(totalAmount) >= 0) {
					throw new InterfaceException("9999", "Manual advise details is not available");
				} else {

					for (ManualAdvise advise : advises) {
						if (!adviseAmountsMap.containsKey(advise.getAdviseID())) {
							adviseAmountsMap.put(advise.getAdviseID(), advise.getBalanceAmt());
						}
						PaymentDetail paymentDetail = new PaymentDetail();
						paymentDetail.setAmount(BigDecimal.ZERO);
						paymentDetail.setReferenceId(advise.getAdviseID());
						paymentDetail.setAvailableAmount(advise.getBalanceAmt());
						paymentDetail.setAmountType(String.valueOf(advise.getAdviseType()));
						paymentDetail.setFeeTypeCode(advise.getFeeTypeCode());
						paymentDetail.setFeeTypeDesc(advise.getFeeTypeDesc());
						paymentDetail.setRecordType(PennantConstants.RCD_ADD);
						paymentDetail.setNewRecord(true);
						paymentDetail.setVersion(1);
						paymentDetail.setUserDetails(refundUpload.getUserDetails());
						paymentDetail.setLastMntBy(refundUpload.getLastMntBy());
						paymentDetail.setLastMntOn(refundUpload.getLastMntOn());
						paymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						paymentDetail.setApiRequest(true);
						tempDetailList.add(paymentDetail);
					}

					BigDecimal refbalance = refundUpload.getPayableAmount();

					for (ManualAdvise advise : advises) {

						if (refbalance.compareTo(BigDecimal.ZERO) == 0) {
							break;
						}

						long key = advise.getAdviseID();
						BigDecimal mapBal = BigDecimal.ZERO;
						mapBal = mapBal.add(adviseAmountsMap.get(key));

						if (BigDecimal.ZERO.compareTo(mapBal) == 0) {
							continue;
						}

						if (refbalance.compareTo(mapBal) > 0) {

							for (PaymentDetail detail : tempDetailList) {
								if (detail.getAmount().compareTo(BigDecimal.ZERO) == 0) {
									detail.setAmount(mapBal);
									adviseAmountsMap.put(key, adviseAmountsMap.get(key).subtract(mapBal));
									refbalance = refbalance.subtract(mapBal);
								}
								break;
							}

						} else if (refbalance.compareTo(mapBal) < 0) {
							for (PaymentDetail detail : tempDetailList) {
								if (detail.getAmount().compareTo(BigDecimal.ZERO) == 0) {
									detail.setAmount(refbalance);
									adviseAmountsMap.put(key, adviseAmountsMap.get(key).subtract(refbalance));
									refbalance = refbalance.subtract(refbalance);
								}
							}
						} else if (refbalance.compareTo(mapBal) == 0) {
							for (PaymentDetail detail : tempDetailList) {

								if (detail.getAmount().compareTo(BigDecimal.ZERO) == 0
										&& detail.getReferenceId() == key) {
									detail.setAmount(mapBal);
									adviseAmountsMap.put(key, adviseAmountsMap.get(key).subtract(mapBal));
									refbalance = refbalance.subtract(mapBal);
								}
							}
						}
					}

					for (PaymentDetail paymentDetail : tempDetailList) {
						if (paymentDetail.getAmount().compareTo(BigDecimal.ZERO) == 0) {
							continue;
						}
						paymentDetailList.add(paymentDetail);
					}
				}
			}
		} else {
			PaymentDetail paymentDetail = new PaymentDetail();
			paymentDetail.setAmountType(refundUpload.getType());
			List<FinExcessAmount> list = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
					paymentDetail.getAmountType());

			for (FinExcessAmount fea : list) {
				paymentDetail.setAmount(refundUpload.getPayableAmount());
				paymentDetail.setAvailableAmount(fea.getBalanceAmt());
				paymentDetail.setReferenceId(fea.getExcessID());
				paymentDetail.setRecordType(PennantConstants.RCD_ADD);
				paymentDetail.setNewRecord(true);
				paymentDetail.setVersion(1);
				paymentDetail.setUserDetails(refundUpload.getUserDetails());
				paymentDetail.setLastMntBy(refundUpload.getLastMntBy());
				paymentDetail.setLastMntOn(refundUpload.getLastMntOn());
				paymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				paymentDetail.setApiRequest(true);
				paymentDetailList.add(paymentDetail);
			}
		}

		// Payment Instructions
		PaymentInstruction paymentInstruction = new PaymentInstruction();
		paymentInstruction.setPostDate(refundUpload.getPaymentDate());
		paymentInstruction.setPaymentType(refundUpload.getPaymentType());
		paymentInstruction.setPaymentAmount(refundUpload.getPayableAmount());
		paymentInstruction.setRemarks(refundUpload.getRemarks());
		paymentInstruction.setBankBranchCode(refundUpload.getIssuingBank());
		paymentInstruction.setBankBranchId(bankBranchId);
		paymentInstruction.setFavourName(refundUpload.getFavourName());
		paymentInstruction.setIssuingBank(refundUpload.getIssuingBank());
		paymentInstruction.setPayableLoc(refundUpload.getPayableLocation());
		paymentInstruction.setPrintingLoc(refundUpload.getPrintingLocation());
		paymentInstruction.setAcctHolderName(refundUpload.getAccountHolderName());
		paymentInstruction.setAccountNo(refundUpload.getAccountNumber());
		paymentInstruction.setPhoneNumber(refundUpload.getPhoneNumber());
		paymentInstruction.setValueDate(refundUpload.getValueDate());
		paymentInstruction.setPaymentCCy(fm.getFinCcy());
		paymentInstruction.setPartnerBankCode(refundUpload.getPartnerBank());
		paymentInstruction.setPartnerBankId(finTypePartnerBank.getPartnerBankID());
		paymentInstruction.setStatus(DisbursementConstants.STATUS_NEW);

		paymentInstruction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentInstruction.setRecordType(PennantConstants.RCD_ADD);
		paymentInstruction.setNewRecord(true);
		paymentInstruction.setVersion(1);
		paymentInstruction.setUserDetails(refundUpload.getUserDetails());
		paymentInstruction.setLastMntBy(refundUpload.getLastMntBy());
		paymentInstruction.setLastMntOn(refundUpload.getLastMntOn());

		// Extra validation fields
		paymentInstruction.setPartnerBankAcType(finTypePartnerBank.getAccountType());
		paymentInstruction.setApiRequest(true);

		// In table availabele but mapping not available
		// paymentInstruction.setFavourNumber(refundUpload.getFavourName());
		// paymentInstruction.setPhoneCountryCode(phoneCountryCode);
		// paymentInstruction.setClearingdate(clearingdate);
		// paymentInstruction.setTransactionRef(transactionRef);
		// paymentInstruction.setRejectReason("");
		// paymentInstruction.setRealizationDate(null);

		paymentHeader.setPaymentDetailList(paymentDetailList);
		paymentHeader.setPaymentInstruction(paymentInstruction);

		logger.debug("Leaving");
		return paymentHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(PaymentHeader paymentHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, paymentHeader.getBefImage(), paymentHeader);
		return new AuditHeader(String.valueOf(paymentHeader.getPaymentId()),
				String.valueOf(paymentHeader.getPaymentId()), null, null, auditDetail, paymentHeader.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getRefundUploadDAO().delete with parameters promotionFee,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtRefundUploads by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		// RefundUpload refundUpload = (RefundUpload) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		// getRefundUploadDAO().delete(refundUpload, "_TEMP"); // because delete will not be applicable here

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	@Override
	public List<ErrorDetail> validateRefundUploads(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		// RefundUploads
		if (uploadHeader.getAuditDetailMap().get("RefundUploads") != null) {

			int successCount = 0;
			int failCount = 0;
			auditDetails = uploadHeader.getAuditDetailMap().get("RefundUploads");
			payableAmountsMap = new HashMap<String, BigDecimal>();

			for (AuditDetail auditDetail : auditDetails) {
				AuditDetail refundAudit = validation(auditDetail, usrLanguage, method);
				RefundUpload refundUpload = (RefundUpload) auditDetail.getModelData();
				if (UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS.equals(refundUpload.getStatus())) {
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

		logger.debug("Leaving");

		return errorDetails;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getRefundUploadDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		RefundUpload refundUpload = (RefundUpload) auditDetail.getModelData();

		// Check the unique keys.
		if (refundUpload.isNewRecord() && !UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(refundUpload.getStatus())) {
			validateLengths(refundUpload);
			if (!UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(refundUpload.getStatus())) {
				validateData(refundUpload);
			}
		}

		auditDetail.setModelData(refundUpload);

		return auditDetail;
	}

	@Override
	public List<AuditDetail> setRefundUploadsAuditData(List<RefundUpload> refundUploadList, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new RefundUpload(), new RefundUpload().getExcludeFields());

		for (int i = 0; i < refundUploadList.size(); i++) {

			RefundUpload refundUpload = refundUploadList.get(i);

			if (StringUtils.isEmpty(refundUpload.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;
			if (PennantConstants.RCD_ADD.equalsIgnoreCase(refundUpload.getRecordType())) {
				refundUpload.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(refundUpload.getRecordType())) {
				refundUpload.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(refundUpload.getRecordType())) {
				refundUpload.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			if ("saveOrUpdate".equals(method) && isRcdType) {
				refundUpload.setNewRecord(true);
			}
			if (!PennantConstants.TRAN_WF.equals(auditTranType)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(refundUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(refundUpload.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(refundUpload.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], refundUpload.getBefImage(),
					refundUpload));
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public List<AuditDetail> processRefundUploadsDetails(List<AuditDetail> auditDetails, long uploadId, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		adviseAmountsMap = new HashMap<Long, BigDecimal>();

		for (int i = 0; i < auditDetails.size(); i++) {
			RefundUpload refundUpload = (RefundUpload) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				refundUpload.setRoleCode("");
				refundUpload.setNextRoleCode("");
				refundUpload.setTaskId("");
				refundUpload.setNextTaskId("");
				refundUpload.setWorkflowId(0);
			}

			String recordType = refundUpload.getRecordType();
			refundUpload.setUploadId(uploadId);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(recordType)) {
				deleteRecord = true;
			} else if (refundUpload.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(recordType)) {
					refundUpload.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recordType)) {
					refundUpload.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recordType)) {
					refundUpload.setRecordType(PennantConstants.RECORD_TYPE_UPD);
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
				} else if (refundUpload.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = recordType;
				recordStatus = refundUpload.getRecordStatus();
				refundUpload.setRecordType("");
				refundUpload.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getRefundUploadDAO().save(refundUpload, type);
				if (approveRec) {
					// prepare Payment Instructions
					if (!UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(refundUpload.getStatus())) {
						createPaymentInstctions(refundUpload);
					}
				}
			}
			if (updateRecord) {
				getRefundUploadDAO().update(refundUpload, type);
			}
			if (deleteRecord) {
				// getRefundUploadDAO().delete(refundUpload, type); // because delete will not be applicable here
			}
			if (approveRec) {
				refundUpload.setRecordType(rcdType);
				refundUpload.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(refundUpload);
		}

		logger.debug("Leaving");

		return auditDetails;
	}

	@Override
	public List<AuditDetail> delete(List<RefundUpload> refundUploadList, String tableType, String auditTranType,
			long uploadId) {
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (refundUploadList != null && !refundUploadList.isEmpty()) {
			String[] fields = PennantJavaUtil.getFieldDetails(new RefundUpload(),
					new RefundUpload().getExcludeFields());
			for (int i = 0; i < refundUploadList.size(); i++) {
				RefundUpload refundUpload = refundUploadList.get(i);
				if (StringUtils.isNotEmpty(refundUpload.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							refundUpload.getBefImage(), refundUpload));
				}
			}
			getRefundUploadDAO().deleteByUploadId(uploadId, tableType);
		}

		return auditDetails;

	}

	private void validateLengths(RefundUpload refundUpload) {
		int errorCount = 0;
		String reason = "";

		// Fin Reference
		if (StringUtils.isNotBlank(refundUpload.getFinReference()) && refundUpload.getFinReference().length() > 20) {
			errorCount++;
			reason = "Reference length should be lessthan or equal to 20";
			refundUpload.setFinReference(null);
		}
		// Payable Amount
		if (refundUpload.getPayableAmount() != null && refundUpload.getPayableAmount().toString().length() > 20) {
			errorCount++;
			reason = "Payable Amount length should be lessthan or equal to 18";
			refundUpload.setPayableAmount(BigDecimal.ZERO);
		}
		// Type
		if (StringUtils.isNotBlank(refundUpload.getType()) && refundUpload.getType().length() > 1) {
			errorCount++;
			reason = "Type length should be 1";
			refundUpload.setPayableAmount(BigDecimal.ZERO);
		}
		// Payment Type
		if (StringUtils.isNotBlank(refundUpload.getPaymentType()) && refundUpload.getPaymentType().length() > 8) {
			errorCount++;
			reason = "Payment Type length should be lessthan or equal to 8";
			refundUpload.setPaymentType(null);
		}
		// Fee Type
		if (StringUtils.isNotBlank(refundUpload.getFeeType()) && refundUpload.getFeeType().length() > 8) {
			errorCount++;
			reason = "Fee Type length should be lessthan or equal to 8";
			refundUpload.setFeeType(null);
		}
		// Partner Bank
		if (StringUtils.isNotBlank(refundUpload.getPartnerBank()) && refundUpload.getPartnerBank().length() > 8) {
			errorCount++;
			reason = "Partner Bank length should be lessthan or equal to 8";
			refundUpload.setPartnerBank(null);
		}
		// Remarks
		if (StringUtils.isNotBlank(refundUpload.getRemarks()) && refundUpload.getRemarks().length() > 100) {
			errorCount++;
			reason = "Remarks length should be lessthan or equal to 100";
			refundUpload.setRemarks(null);
		}
		// IFSC
		if (StringUtils.isNotBlank(refundUpload.getIFSC()) && refundUpload.getIFSC().length() > 20) {
			errorCount++;
			reason = "IFSC length should be lessthan or equal to 20";
			refundUpload.setIFSC(null);
		}
		// Account Number
		if (StringUtils.isNotBlank(refundUpload.getAccountNumber()) && refundUpload.getAccountNumber().length() > 100) {
			errorCount++;
			reason = "Account Number length should be lessthan or equal to 100";
			refundUpload.setAccountNumber(null);
		}
		// Account Holder Name
		if (StringUtils.isNotBlank(refundUpload.getAccountHolderName())
				&& refundUpload.getAccountHolderName().length() > 200) {
			errorCount++;
			reason = "Account Holder Name length should be lessthan or equal to 200";
			refundUpload.setAccountHolderName(null);
		}
		// Phone Number
		if (StringUtils.isNotBlank(refundUpload.getPhoneNumber()) && refundUpload.getPhoneNumber().length() > 12) {
			errorCount++;
			reason = "Phone Number length should be lessthan or equal to 12";
			refundUpload.setPhoneNumber(null);
		}
		// Issuing Bank
		if (StringUtils.isNotBlank(refundUpload.getIssuingBank()) && refundUpload.getIssuingBank().length() > 8) {
			errorCount++;
			reason = "Issuing Bank length should be lessthan or equal to 8";
			refundUpload.setIssuingBank(null);
		}
		// Favouring Name
		if (StringUtils.isNotBlank(refundUpload.getFavourName()) && refundUpload.getFavourName().length() > 200) {
			errorCount++;
			reason = "Favouring Name length should be lessthan or equal to 200";
			refundUpload.setFavourName(null);
		}
		// Payable Location
		if (StringUtils.isNotBlank(refundUpload.getPayableLocation())
				&& refundUpload.getPayableLocation().length() > 50) {
			errorCount++;
			reason = "Payable Location length should be lessthan or equal to 50";
			refundUpload.setPayableLocation(null);
		}
		// Printing Location
		if (StringUtils.isNotBlank(refundUpload.getPrintingLocation())
				&& refundUpload.getPrintingLocation().length() > 50) {
			errorCount++;
			reason = "Printing Location length should be lessthan or equal to 50";
			refundUpload.setPrintingLocation(null);
		}

		if (errorCount > 0) {
			if (errorCount > 1) {
				reason = "Invalid record.";
			}
			refundUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
			refundUpload.setRejectReason(reason);
		}

	}

	private void validateData(RefundUpload refundUpload) {
		int errorCount = 0;
		String reason = "";
		BigDecimal availableAmount = BigDecimal.ZERO;

		// FinReference
		String finReference = refundUpload.getFinReference();
		Long finID = null;
		String finType = null;
		if (StringUtils.isBlank(finReference)) {
			errorCount++;
			reason = "Reference is mandatory";
		} else {
			finID = financeMainDAO.getFinID(finReference, TableType.MAIN_TAB);
			finType = financeMainDAO.getFinanceType(finID, TableType.MAIN_TAB);
			if (finID == null) {
				errorCount++;
				reason = "Invalid Reference";
			} else {
				finType = financeMainDAO.getFinanceType(finID, TableType.MAIN_TAB);
				boolean recordMaintainance = this.paymentHeaderService.isInProgress(finID);
				if (!recordMaintainance) {
					recordMaintainance = this.refundUploadDAO.getRefundUploadsByFinReference(finReference,
							refundUpload.getUploadId(), "_Temp");
				}
				if (recordMaintainance) {
					errorCount++;
					reason = "Reference is in maintainance";
				}
			}
		}
		// Payment Type
		if (errorCount == 0 && !DisbursementConstants.PAYMENT_TYPE_RTGS.equals(refundUpload.getPaymentType())
				&& !DisbursementConstants.PAYMENT_TYPE_DD.equals(refundUpload.getPaymentType())
				&& !DisbursementConstants.PAYMENT_TYPE_NEFT.equals(refundUpload.getPaymentType())
				&& !DisbursementConstants.PAYMENT_TYPE_CASH.equals(refundUpload.getPaymentType())
				&& !DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())
				&& !DisbursementConstants.PAYMENT_TYPE_IMPS.equals(refundUpload.getPaymentType())) {
			errorCount++;
			reason = "Payment Type allowed values are :RTGS,DD,NEFT,CASH,CHEQUE,IMPS";
		}
		// Payable Date
		if (errorCount == 0 && (refundUpload.getPaymentDate() == null
				|| (DateUtility.compare(refundUpload.getPaymentDate(), SysParamUtil.getAppDate())) < 0)) {
			errorCount++;
			reason = "Payable Date is always current date or future date";
		}
		// Value Date
		if (errorCount == 0
				&& (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())
						|| DisbursementConstants.PAYMENT_TYPE_DD.equals(refundUpload.getPaymentType()))
				&& refundUpload.getValueDate() == null) {
			errorCount++;
			reason = "Value Date is mandatory"; // TODO add value date condition
		}
		// Type
		if (errorCount == 0 && !UploadConstants.REFUNDUPLOAD_EXCESS_AMOUNT.equals(refundUpload.getType())
				&& !UploadConstants.REFUNDUPLOAD_ADVANCE_AMOUNT.equals(refundUpload.getType())
				&& !UploadConstants.REFUNDUPLOAD_MANUAL_ADVISE_PAYABLE.equals(refundUpload.getType())) {
			errorCount++;
			reason = "Type should be E/A/M";
		}
		// Fee Type
		if (errorCount == 0) {
			if (UploadConstants.REFUNDUPLOAD_MANUAL_ADVISE_PAYABLE.equals(refundUpload.getType())) {
				if (StringUtils.isBlank(refundUpload.getFeeType())) {
					errorCount++;
					reason = "Fee Type is mandatory for Type is M.";
				} else {
					List<ManualAdvise> advises = getManualAdviseDAO().getManualAdviseByRefAndFeeCode(finID,
							AdviseType.PAYABLE.id(), refundUpload.getFeeType());
					if (CollectionUtils.isNotEmpty(advises)) {
						for (ManualAdvise advise : advises) {
							availableAmount = availableAmount.add(advise.getBalanceAmt());
						}
					}
				}
			} else if (StringUtils.isNotBlank(refundUpload.getFeeType())) {
				errorCount++;
				reason = "Fee Type sholud be empty for Type is M";
			}
		}

		// Payable Amount
		if (errorCount == 0) {
			if (BigDecimal.ZERO.compareTo(refundUpload.getPayableAmount()) >= 0) {
				errorCount++;
				reason = "Payable Amount should be greater than 0 and less than or equal to available amount";
			} else {
				if (UploadConstants.REFUNDUPLOAD_MANUAL_ADVISE_PAYABLE.equals(refundUpload.getType())) {
					if (availableAmount.compareTo(refundUpload.getPayableAmount()) < 0) {
						errorCount++;
						reason = "Payable Amount should be greater than 0 and less than or equal to available amount";
					}
				} else {
					List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
							refundUpload.getType());

					BigDecimal balanceAmt = BigDecimal.ZERO;
					for (FinExcessAmount fea : excessList) {
						balanceAmt = balanceAmt.add(fea.getBalanceAmt());
					}
					if (balanceAmt.compareTo(refundUpload.getPayableAmount()) < 0) {
						errorCount++;
						reason = "Payable Amount should be greater than 0 and less than or equal to available amount";
					}
				}
			}
		}

		// Partner Bank
		if (errorCount == 0) {
			if (StringUtils.isBlank(refundUpload.getPartnerBank())) {
				errorCount++;
				reason = "Partner Bank name should be available in the applicable partner banks selected in the loan type";
			} else {
				// Condition should be add
				FinTypePartnerBank pb = finTypePartnerBankDAO.getFinTypePartnerBankByPartnerBankCode(
						refundUpload.getPartnerBank(), finType, refundUpload.getPaymentType());
				if (pb == null) {
					errorCount++;
					reason = "Partner Bank name should be available in the applicable partner banks selected in the loan type";
				}
			}
		}

		////////////////////////// Conditional Mandatory columns/////////////////////////
		// IFSC and MICR
		BankBranch bankBranch = null;
		if (errorCount == 0 && (DisbursementConstants.PAYMENT_TYPE_RTGS.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_CASH.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_IMPS.equals(refundUpload.getPaymentType()))) {
			if (StringUtils.isBlank(refundUpload.getIFSC())) {
				errorCount++;
				reason = "IFSC is mandatory";
			} else {
				if (StringUtils.isBlank(refundUpload.getMICR())) {
					errorCount++;
					reason = "MICR is mandatory";
				} else {
					bankBranch = this.bankBranchDAO.getBankBrachByIFSCandMICR(refundUpload.getIFSC(),
							refundUpload.getMICR(), "");
					if (bankBranch == null) {
						errorCount++;
						reason = "Invalid IFSC/MICR.";
					}
				}
			}
		}

		// Issuing Bank
		if (errorCount == 0 && (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(refundUpload.getPaymentType()))) {
			if (StringUtils.isBlank(refundUpload.getIssuingBank())) {
				errorCount++;
				reason = "Issuing Bank is mandatory";
			} else {
				BankDetail bankDetail = this.bankDetailDAO.getBankDetailById(refundUpload.getIssuingBank(), "");
				if (bankDetail == null) {
					errorCount++;
					reason = "Invalid Issuing Bank";
				}
			}
		}

		// Account Number
		if (errorCount == 0 && (DisbursementConstants.PAYMENT_TYPE_RTGS.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_CASH.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_IMPS.equals(refundUpload.getPaymentType()))) {
			if (StringUtils.isBlank(refundUpload.getAccountNumber())) {
				errorCount++;
				reason = "Account Number is mandatory";
			}
		}

		// Account Holder Name
		if (errorCount == 0 && (DisbursementConstants.PAYMENT_TYPE_RTGS.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_NEFT.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_CASH.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_IMPS.equals(refundUpload.getPaymentType()))) {
			if (StringUtils.isBlank(refundUpload.getAccountHolderName())) {
				errorCount++;
				reason = "Account Holder Name is mandatory";
			}
		}
		// Phone Number
		if (errorCount == 0 && DisbursementConstants.PAYMENT_TYPE_IMPS.equals(refundUpload.getPaymentType())) {
			if (StringUtils.isBlank(refundUpload.getPhoneNumber())) {
				errorCount++;
				reason = "Phone Number is mandatory";
			}
		}

		// Favoring Name
		if (errorCount == 0 && (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(refundUpload.getPaymentType()))) {
			if (StringUtils.isBlank(refundUpload.getFavourName())) {
				errorCount++;
				reason = "Favoring Name is mandatory";
			}
		}
		// Payable Location
		if (errorCount == 0 && (DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())
				|| DisbursementConstants.PAYMENT_TYPE_DD.equals(refundUpload.getPaymentType()))) {
			if (StringUtils.isBlank(refundUpload.getPayableLocation())) {
				errorCount++;
				reason = "Payable Location is mandatory";
			}
		}
		// Printing Location
		if (errorCount == 0 && DisbursementConstants.PAYMENT_TYPE_CHEQUE.equals(refundUpload.getPaymentType())) {
			if (StringUtils.isBlank(refundUpload.getPrintingLocation())) {
				errorCount++;
				reason = "Printing Location is mandatory";
			}
		}

		// Payable Amount Balance checking
		if (errorCount == 0) {
			String key = "";
			BigDecimal payAmount = BigDecimal.ZERO;

			if (UploadConstants.REFUNDUPLOAD_MANUAL_ADVISE_PAYABLE.equals(refundUpload.getType())) {

				key = finReference + "-" + refundUpload.getType() + "-" + refundUpload.getFeeType();

				if (payableAmountsMap.containsKey(key)) {
					payAmount = payableAmountsMap.get(key).add(refundUpload.getPayableAmount());
					if (payAmount.compareTo(availableAmount) > 0) {
						errorCount++;
						reason = "Payable Amount should be greater than 0 and less than or equal to available amount";
					} else {
						payableAmountsMap.put(key, payAmount);
					}
				} else {
					payableAmountsMap.put(key, refundUpload.getPayableAmount());
				}
			} else {
				List<FinExcessAmount> excessList = finExcessAmountDAO.getExcessAmountsByRefAndType(finID,
						refundUpload.getType());

				BigDecimal balanceAmt = BigDecimal.ZERO;
				for (FinExcessAmount fea : excessList) {
					balanceAmt = balanceAmt.add(fea.getBalanceAmt());
				}

				key = finReference + "-" + refundUpload.getType();

				if (payableAmountsMap.containsKey(key)) {
					payAmount = payableAmountsMap.get(key).add(refundUpload.getPayableAmount());
					if (balanceAmt.compareTo(payAmount) < 0) {
						errorCount++;
						reason = "Payable Amount should be greater than 0 and less than or equal to available amount";
					} else {
						payableAmountsMap.put(key, payAmount);
					}
				} else {
					payableAmountsMap.put(key, refundUpload.getPayableAmount());
				}
			}
		}

		if (errorCount > 0) {
			if (errorCount > 1) {
				reason = "Invalid record.";
			}
			refundUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
			refundUpload.setRejectReason(reason);
		}
	}

	public Map<String, BigDecimal> getPayableAmountsMap() {
		return payableAmountsMap;
	}

	public void setPayableAmountsMap(Map<String, BigDecimal> payableAmountsMap) {
		this.payableAmountsMap = payableAmountsMap;
	}
}