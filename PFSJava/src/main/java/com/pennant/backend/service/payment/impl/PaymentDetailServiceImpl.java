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
 * FileName    		:  PaymentDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.payment.PaymentDetailDAO;
import com.pennant.backend.dao.payment.PaymentInstructionDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentDetail</b>.<br>
 */
public class PaymentDetailServiceImpl extends GenericService<PaymentDetail> implements PaymentDetailService {
	private static final Logger logger = Logger.getLogger(PaymentDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PaymentDetailDAO paymentDetailDAO;
	private PaymentInstructionDAO paymentInstructionDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	//GST Invoice Report changes
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceDetailService financeDetailService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PaymentDetailDAO getPaymentDetailDAO() {
		return paymentDetailDAO;
	}

	public void setPaymentDetailDAO(PaymentDetailDAO paymentDetailDAO) {
		this.paymentDetailDAO = paymentDetailDAO;
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

	public PaymentInstructionDAO getPaymentInstructionDAO() {
		return paymentInstructionDAO;
	}

	public void setPaymentInstructionDAO(PaymentInstructionDAO paymentInstructionDAO) {
		this.paymentInstructionDAO = paymentInstructionDAO;
	}

	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * PaymentDeatils/PaymentDeatils_Temp by using PaymentDeatilsDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using PaymentDeatilsDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtPaymentDeatils by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
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

		if (paymentDetail.isNew()) {
			paymentDetail.setId(Long.parseLong(getPaymentDetailDAO().save(paymentDetail, tableType)));
			auditHeader.getAuditDetail().setModelData(paymentDetail);
			auditHeader.setAuditReference(String.valueOf(paymentDetail.getPaymentDetailId()));
		} else {
			getPaymentDetailDAO().update(paymentDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PaymentDeatils by using PaymentDeatilsDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtPaymentDeatils by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentDetail paymentDetail = (PaymentDetail) auditHeader.getAuditDetail().getModelData();
		getPaymentDetailDAO().delete(paymentDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPaymentDeatils fetch the details by using PaymentDeatilsDAO's getPaymentDeatilsById method.
	 * 
	 * @param paymentDetailId
	 *            paymentDetailId of the PaymentDetail.
	 * @param amountType
	 *            amountType of the PaymentDetail.
	 * @return PaymentDeatils
	 */
	@Override
	public PaymentDetail getPaymentDetail(long paymentDetailId, String amountType) {
		return getPaymentDetailDAO().getPaymentDetail(paymentDetailId, "_View");
	}

	/**
	 * getApprovedPaymentDeatilsById fetch the details by using PaymentDeatilsDAO's getPaymentDeatilsById method . with
	 * parameter id and type as blank. it fetches the approved records from the PaymentDeatils.
	 * 
	 * @param paymentDetailId
	 *            paymentDetailId of the PaymentDetail.
	 * @param amountType
	 *            amountType of the PaymentDetail. (String)
	 * @return PaymentDeatils
	 */
	public PaymentDetail getApprovedPaymentDetail(long paymentDetailId, String amountType) {
		return getPaymentDetailDAO().getPaymentDetail(paymentDetailId, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPaymentDetailDAO().delete with
	 * parameters paymentDetail,"" b) NEW Add new record in to main table by using getPaymentDetailDAO().save with
	 * parameters paymentDetail,"" c) EDIT Update record in the main table by using getPaymentDetailDAO().update with
	 * parameters paymentDetail,"" 3) Delete the record from the workFlow table by using getPaymentDetailDAO().delete
	 * with parameters paymentDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtPaymentDeatils by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtPaymentDeatils by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
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

		getPaymentDetailDAO().delete(paymentDetail, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentDetail.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(paymentDetailDAO.getPaymentDetail(paymentDetail.getPaymentDetailId(), ""));
		}

		if (paymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPaymentDetailDAO().delete(paymentDetail, TableType.MAIN_TAB);
		} else {
			paymentDetail.setRoleCode("");
			paymentDetail.setNextRoleCode("");
			paymentDetail.setTaskId("");
			paymentDetail.setNextTaskId("");
			paymentDetail.setWorkflowId(0);

			if (paymentDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				paymentDetail.setRecordType("");
				getPaymentDetailDAO().save(paymentDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				paymentDetail.setRecordType("");
				getPaymentDetailDAO().update(paymentDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(paymentDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPaymentDetailDAO().delete with parameters paymentDetail,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtPaymentDeatils by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
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
		getPaymentDetailDAO().delete(paymentDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPaymentDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

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
			long linkedTranId, String finReference) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<ManualAdviseMovements> adviseMovements = new ArrayList<ManualAdviseMovements>();

		for (int i = 0; i < auditDetails.size(); i++) {
			PaymentDetail paymentDetail = (PaymentDetail) auditDetails.get(i).getModelData();
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
				paymentDetail.setRoleCode("");
				paymentDetail.setNextRoleCode("");
				paymentDetail.setTaskId("");
				paymentDetail.setNextTaskId("");
				paymentDetail.setWorkflowId(0);
			}
			if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (paymentDetail.isNewRecord()) {
				saveRecord = true;
				if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					paymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					paymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					paymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (paymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (paymentDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = paymentDetail.getRecordType();
				recordStatus = paymentDetail.getRecordStatus();
				paymentDetail.setRecordType("");
				paymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {

				if (paymentDetail.getTaxHeader() != null) {
					Long taxheaderID = taxHeaderDetailsDAO.save(paymentDetail.getTaxHeader(), tableType.getSuffix());
					paymentDetail.setTaxHeaderId(taxheaderID);

					List<Taxes> taxList = paymentDetail.getTaxHeader().getTaxDetails();
					for (Taxes taxes : taxList) {
						taxes.setReferenceId(taxheaderID);
					}
					taxHeaderDetailsDAO.saveTaxes(taxList, tableType.getSuffix());
				}
				if (paymentDetail.getPaymentDetailId() <= 0 || tableType.equals(TableType.MAIN_TAB)) {
					String detailId = getPaymentDetailDAO().save(paymentDetail, tableType);
					paymentDetail.setPaymentDetailId(Long.valueOf(detailId));
				}

				// Payments processing
				if ("doApprove".equals(methodName)) {
					ManualAdviseMovements movement = doApprove(paymentDetail, finReference);
					if (movement != null) {
						adviseMovements.add(movement);
					}
				} else {
					saveOrUpdate(paymentDetail);
				}
			}
			if (updateRecord) {
				if (paymentDetail.getTaxHeader() != null && paymentDetail.getTaxHeader().getId() <= 0) {
					long taxheaderID = taxHeaderDetailsDAO.save(paymentDetail.getTaxHeader(), tableType.getSuffix());
					paymentDetail.setTaxHeaderId(taxheaderID);

					List<Taxes> taxList = paymentDetail.getTaxHeader().getTaxDetails();
					for (Taxes taxes : taxList) {
						taxes.setReferenceId(taxheaderID);
					}
					taxHeaderDetailsDAO.saveTaxes(taxList, tableType.getSuffix());
				}

				getPaymentDetailDAO().update(paymentDetail, tableType);
				saveOrUpdate(paymentDetail);
			}
			if (deleteRecord) {
				getPaymentDetailDAO().delete(paymentDetail, tableType);
				// Payments processing
				doReject(paymentDetail);
			}
			if (approveRec) {
				paymentDetail.setRecordType(rcdType);
				paymentDetail.setRecordStatus(recordStatus);
			}

			if ("doApprove".equals(methodName)) {
				if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentDetail.getRecordType())) {
					paymentDetail.setBefImage(paymentDetailDAO.getPaymentDetail(paymentDetail.getPaymentId(), ""));
				}
			}
			auditDetails.get(i).setModelData(paymentDetail);
		}

		//GST Invoice preparation for Receivable Advises

		if (CollectionUtils.isNotEmpty(adviseMovements)) {
			FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(finReference, "", false);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setMovements(adviseMovements);

			this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);
		}
		logger.debug("Leaving");
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
			getPaymentDetailDAO().deleteList(paymentDetail, tableType);

			for (PaymentDetail detail : paymentDetailList) {
				doReject(detail);
			}
		}
		return auditDetails;
	}

	@Override
	public List<PaymentDetail> getPaymentDetailList(long paymentId, String type) {
		return getPaymentDetailDAO().getPaymentDetailList(paymentId, type);
	}

	private void saveOrUpdate(PaymentDetail paymentDetail) {
		logger.debug("Entering");

		// Excess Amount Reserve
		if (!String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
			// Excess Amount make utilization
			FinExcessAmountReserve exReserve = getFinExcessAmountDAO()
					.getExcessReserve(paymentDetail.getPaymentDetailId(), paymentDetail.getReferenceId());
			if (exReserve == null) {

				// Update Excess Amount in Reserve
				getFinExcessAmountDAO().updateExcessReserve(paymentDetail.getReferenceId(), paymentDetail.getAmount());

				// Save Excess Reserve Log Amount
				getFinExcessAmountDAO().saveExcessReserveLog(paymentDetail.getPaymentDetailId(),
						paymentDetail.getReferenceId(), paymentDetail.getAmount(), RepayConstants.RECEIPTTYPE_PAYABLE);
			} else {
				if (paymentDetail.getAmount().compareTo(exReserve.getReservedAmt()) != 0) {
					BigDecimal diffInReserve = paymentDetail.getAmount().subtract(exReserve.getReservedAmt());

					// Update Reserve Amount in FinExcessAmount
					getFinExcessAmountDAO().updateExcessReserve(paymentDetail.getReferenceId(), diffInReserve);

					// Update Excess Reserve Log
					getFinExcessAmountDAO().updateExcessReserveLog(paymentDetail.getPaymentDetailId(),
							paymentDetail.getReferenceId(), diffInReserve, RepayConstants.RECEIPTTYPE_PAYABLE);
				}
			}
		} else {
			// Payable Amount make utilization
			ManualAdviseReserve payableReserve = getManualAdviseDAO()
					.getPayableReserve(paymentDetail.getPaymentDetailId(), paymentDetail.getReferenceId());

			BigDecimal amount = paymentDetail.getAmount();
			if (paymentDetail.getTaxHeader() != null && StringUtils.equals(paymentDetail.getTaxComponent(),
					FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
				//GST Calculations
				List<Taxes> taxDetails = paymentDetail.getTaxHeader().getTaxDetails();
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
				getManualAdviseDAO().updatePayableReserve(paymentDetail.getReferenceId(), amount);

				// Save Payable Reserve Log Amount
				getManualAdviseDAO().savePayableReserveLog(paymentDetail.getPaymentDetailId(),
						paymentDetail.getReferenceId(), amount);
			} else {
				if (amount.compareTo(payableReserve.getReservedAmt()) != 0) {
					BigDecimal diffInReserve = amount.subtract(payableReserve.getReservedAmt());

					// Update Reserve Amount in Manual Advise
					getManualAdviseDAO().updatePayableReserve(paymentDetail.getReferenceId(), diffInReserve);

					// Update Payable Reserve Log
					getManualAdviseDAO().updatePayableReserveLog(paymentDetail.getPaymentDetailId(),
							paymentDetail.getReferenceId(), diffInReserve);
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doReject(PaymentDetail paymentDetail) {
		logger.debug("Entering");

		// Excess Amount Reserve
		if (!String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
			// Excess Amount make utilization
			FinExcessAmountReserve exReserve = getFinExcessAmountDAO()
					.getExcessReserve(paymentDetail.getPaymentDetailId(), paymentDetail.getReferenceId());
			if (exReserve != null) {
				// Update Reserve Amount in FinExcessAmount
				getFinExcessAmountDAO().updateExcessReserve(paymentDetail.getReferenceId(),
						exReserve.getReservedAmt().negate());

				// Delete Reserved Log against Excess and Receipt ID
				getFinExcessAmountDAO().deleteExcessReserve(paymentDetail.getPaymentDetailId(),
						paymentDetail.getReferenceId(), RepayConstants.RECEIPTTYPE_PAYABLE);
			}
		} else { // Payable Amount Reserve
			// Payable Amount make utilization
			ManualAdviseReserve payableReserve = getManualAdviseDAO()
					.getPayableReserve(paymentDetail.getPaymentDetailId(), paymentDetail.getReferenceId());
			if (payableReserve != null) {
				// Update Reserve Amount in ManualAdvise
				getManualAdviseDAO().updatePayableReserve(paymentDetail.getReferenceId(),
						payableReserve.getReservedAmt().negate());

				// Delete Reserved Log against Payable Advise ID and Receipt ID
				getManualAdviseDAO().deletePayableReserve(paymentDetail.getPaymentDetailId(),
						paymentDetail.getReferenceId());
			}
		}
		logger.debug("Leaving");
	}

	private ManualAdviseMovements doApprove(PaymentDetail paymentDetail, String finReference) {
		logger.debug("Entering");

		ManualAdviseMovements manualMovement = null;

		// Excess Amounts
		if (!String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
			// Excess Amount make utilization
			getFinExcessAmountDAO().updateUtilise(paymentDetail.getReferenceId(), paymentDetail.getAmount());

			// Delete Reserved Log against Excess and Receipt ID
			getFinExcessAmountDAO().deleteExcessReserve(paymentDetail.getPaymentDetailId(),
					paymentDetail.getReferenceId(), RepayConstants.RECEIPTTYPE_PAYABLE);

			// Excess Movement Creation
			FinExcessMovement movement = new FinExcessMovement();
			movement.setExcessID(paymentDetail.getReferenceId());
			movement.setReceiptID(paymentDetail.getPaymentDetailId());
			movement.setMovementType(RepayConstants.RECEIPTTYPE_PAYABLE);
			movement.setTranType(AccountConstants.TRANTYPE_CREDIT);
			movement.setAmount(paymentDetail.getAmount());
			getFinExcessAmountDAO().saveExcessMovement(movement);

		} else {

			ManualAdvise advise = new ManualAdvise();
			advise.setAdviseID(paymentDetail.getReferenceId());

			BigDecimal amount = paymentDetail.getAmount();
			if (paymentDetail.getTaxHeader() != null && StringUtils.equals(paymentDetail.getTaxComponent(),
					FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
				//GST Calculations
				List<Taxes> taxDetails = paymentDetail.getTaxHeader().getTaxDetails();
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
			if (!StringUtils.equals(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD, paymentDetail.getFinSource())) {
				advise.setReservedAmt(amount.negate());
			}

			advise.setBalanceAmt(amount.negate());
			getManualAdviseDAO().updateAdvPayment(advise, TableType.MAIN_TAB);

			// Delete Reserved Log against Advise and Receipt Seq ID
			if (!StringUtils.equals(UploadConstants.FINSOURCE_ID_CD_PAY_UPLOAD, paymentDetail.getFinSource())) {
				getManualAdviseDAO().deletePayableReserve(paymentDetail.getPaymentDetailId(),
						paymentDetail.getReferenceId());
			}

			// Payable Advise Movement Creation
			manualMovement = new ManualAdviseMovements();
			manualMovement.setAdviseID(paymentDetail.getReferenceId());
			manualMovement.setReceiptID(paymentDetail.getPaymentDetailId());
			manualMovement.setReceiptSeqID(0);
			manualMovement.setMovementDate(SysParamUtil.getAppDate());
			manualMovement.setMovementAmount(amount);
			manualMovement.setPaidAmount(amount);

			TaxHeader taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			if (String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
				List<Taxes> taxDetails = paymentDetail.getTaxHeader().getTaxDetails();
				taxHeader.setTaxDetails(taxDetails);
			}
			if (taxHeader.getTaxDetails() == null) {
				taxHeader.setTaxDetails(new ArrayList<>());
			}
			manualMovement.setTaxHeader(taxHeader);
			getManualAdviseDAO().saveMovement(manualMovement, TableType.MAIN_TAB.getSuffix());

			ManualAdvise manualAdvise = getManualAdviseDAO().getManualAdviseById(paymentDetail.getReferenceId(),
					"_AView");

			manualMovement.setFeeTypeCode(manualAdvise.getFeeTypeCode());
			manualMovement.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
			manualMovement.setTaxApplicable(manualAdvise.isTaxApplicable());
			manualMovement.setTaxComponent(manualAdvise.getTaxComponent());

			logger.debug("Leaving");
		}
		return manualMovement;
	}

	@Override
	public void paymentReversal(PaymentInstruction paymentInstruction) {
		logger.debug(Literal.ENTERING);

		List<PaymentDetail> detailsList = getPaymentDetailDAO().getPaymentDetailList(paymentInstruction.getPaymentId(),
				TableType.MAIN_TAB.getSuffix());
		if (detailsList != null && !detailsList.isEmpty()) {
			for (PaymentDetail paymentDetail : detailsList) {
				if (!String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
					getFinExcessAmountDAO().updateExcessAmount(paymentDetail.getReferenceId(), "U",
							paymentDetail.getAmount().negate());
				} else {
					getManualAdviseDAO().reverseUtilise(paymentDetail.getReferenceId(), paymentDetail.getAmount());
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public int updatePaymentStatus(PaymentInstruction paymentInstruction) {
		logger.debug(Literal.ENTERING);
		return getPaymentInstructionDAO().updatePaymentInstrucionStatus(paymentInstruction, TableType.MAIN_TAB);
	}

	@Override
	public PaymentInstruction getPaymentInstruction(long paymentId, String type) {
		return getPaymentInstructionDAO().getPaymentInstruction(paymentId, type);
	}

	@Override
	public PaymentInstruction getPaymentInstructionDetails(long paymentId, String type) {
		return getPaymentInstructionDAO().getPaymentInstructionDetails(paymentId, type);
	}

	public TaxHeaderDetailsDAO getTaxHeaderDetailsDAO() {
		return taxHeaderDetailsDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

}