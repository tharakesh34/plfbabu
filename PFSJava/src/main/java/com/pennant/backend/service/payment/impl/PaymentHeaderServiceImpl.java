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
 * FileName    		:  PaymentHeaderServiceImpl.java                                                   * 	  
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.payment.PaymentHeaderDAO;
import com.pennant.backend.dao.payment.PaymentTaxDetailDAO;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.payment.PaymentTaxDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.InstrumentwiseLimitService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.payment.PaymentDetailService;
import com.pennant.backend.service.payment.PaymentHeaderService;
import com.pennant.backend.service.payment.PaymentInstructionService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PaymentHeader</b>.<br>
 */
public class PaymentHeaderServiceImpl extends GenericService<PaymentHeader> implements PaymentHeaderService {
	private static final Logger logger = Logger.getLogger(PaymentHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PaymentHeaderDAO paymentHeaderDAO;

	private PaymentDetailService paymentDetailService;
	private PaymentInstructionService paymentInstructionService;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	private PaymentTaxDetailDAO paymentTaxDetailDAO;
	private FinFeeDetailService finFeeDetailService;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerAddresDAO customerAddresDAO;
	//IMPS Splitting
	private FinAdvancePaymentsService finAdvancePaymentsService;
	private transient InstrumentwiseLimitService instrumentwiseLimitService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PaymentHeaderDAO getPaymentHeaderDAO() {
		return paymentHeaderDAO;
	}

	public void setPaymentHeaderDAO(PaymentHeaderDAO paymentHeaderDAO) {
		this.paymentHeaderDAO = paymentHeaderDAO;
	}

	public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
		this.paymentDetailService = paymentDetailService;
	}

	public void setPaymentInstructionService(PaymentInstructionService paymentInstructionService) {
		this.paymentInstructionService = paymentInstructionService;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table PaymentHeader/PaymentHeader_Temp
	 * by using PaymentHeaderDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using PaymentHeaderDAO's update method 3) Audit the record in to AuditHeader and
	 * AdtPaymentHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		PaymentHeader paymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (paymentHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (paymentHeader.isNew()) {
			paymentHeader.setId(Long.parseLong(getPaymentHeaderDAO().save(paymentHeader, tableType)));
			setPaymentHeaderId(paymentHeader);
			auditHeader.getAuditDetail().setModelData(paymentHeader);
			auditHeader.setAuditReference(String.valueOf(paymentHeader.getPaymentId()));
		} else {
			getPaymentHeaderDAO().update(paymentHeader, tableType);

			// Payment Tax Details for Payables
			getPaymentTaxDetailDAO().deleteByPaymentID(paymentHeader.getPaymentId(), tableType);
		}

		// PaymentHeader
		if (paymentHeader.getPaymentDetailList() != null && paymentHeader.getPaymentDetailList().size() > 0) {
			List<AuditDetail> payAuditDetails = paymentHeader.getAuditDetailMap().get("PaymentDetails");
			payAuditDetails = this.paymentDetailService.processPaymentDetails(payAuditDetails, tableType, "", 0,
					paymentHeader.getFinReference());
			auditDetails.addAll(payAuditDetails);
		}

		// PaymentInstructions
		if (paymentHeader.getPaymentInstruction() != null) {
			List<AuditDetail> paymentInstrAuditDetails = paymentHeader.getAuditDetailMap().get("PaymentInstructions");
			paymentInstrAuditDetails = this.paymentInstructionService
					.processPaymentInstrDetails(paymentInstrAuditDetails, tableType, "");
			auditDetails.addAll(paymentInstrAuditDetails);
		}
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	private void setPaymentHeaderId(PaymentHeader paymentHeader) {
		if (paymentHeader.getPaymentDetailList() != null && paymentHeader.getPaymentDetailList().size() > 0) {
			for (PaymentDetail detail : paymentHeader.getPaymentDetailList()) {
				detail.setPaymentId(paymentHeader.getPaymentId());
			}
		}
		if (paymentHeader.getPaymentInstruction() != null) {
			paymentHeader.getPaymentInstruction().setPaymentId(paymentHeader.getPaymentId());
		}
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * PaymentHeader by using PaymentHeaderDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtPaymentHeader by using auditHeaderDAO.addAudit(auditHeader)
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

		PaymentHeader paymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		getPaymentHeaderDAO().delete(paymentHeader, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(paymentHeader, TableType.MAIN_TAB, auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getPaymentHeader fetch the details by using PaymentHeaderDAO's getPaymentHeaderById method.
	 * 
	 * @param paymentId
	 *            paymentId of the PaymentHeader.
	 * @return PaymentHeader
	 */
	@Override
	public PaymentHeader getPaymentHeader(long paymentId) {
		PaymentHeader paymentHeader = getPaymentHeaderDAO().getPaymentHeader(paymentId, "_View");
		List<PaymentDetail> list = this.paymentDetailService.getPaymentDetailList(paymentHeader.getPaymentId(),
				"_View");

		if (list != null) {
			paymentHeader.setPaymentDetailList(list);
			for (PaymentDetail paymentDetail : list) {
				paymentDetail.setPaymentTaxDetail(
						getPaymentTaxDetailDAO().getTaxDetailByID(paymentDetail.getPaymentDetailId(), "_View"));
			}
		}

		PaymentInstruction paymentInstruction = this.paymentInstructionService
				.getPaymentInstructionDetails(paymentHeader.getPaymentId(), "_View");
		if (paymentInstruction != null) {
			paymentHeader.setPaymentInstruction(paymentInstruction);
		}
		return paymentHeader;
	}

	/**
	 * getApprovedPaymentHeaderById fetch the details by using PaymentHeaderDAO's getPaymentHeaderById method . with
	 * parameter id and type as blank. it fetches the approved records from the PaymentHeader.
	 * 
	 * @param paymentId
	 *            paymentId of the PaymentHeader. (String)
	 * @return PaymentHeader
	 */
	public PaymentHeader getApprovedPaymentHeader(long paymentId) {
		return getPaymentHeaderDAO().getPaymentHeader(paymentId, "_AView");
	}

	@Override
	public FinanceMain getFinanceDetails(String finReference) {
		return getPaymentHeaderDAO().getFinanceDetails(finReference);
	}

	@Override
	public List<FinExcessAmount> getfinExcessAmount(String finReference) {
		return getPaymentHeaderDAO().getfinExcessAmount(finReference);
	}

	@Override
	public List<ManualAdvise> getManualAdvise(String finReference) {
		return getPaymentHeaderDAO().getManualAdvise(finReference);
	}
	
	@Override
	public List<ManualAdvise> getManualAdviseForEnquiry(String finReference) {
		return getPaymentHeaderDAO().getManualAdviseForEnquiry(finReference);
	}


	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getPaymentHeaderDAO().delete with
	 * parameters paymentHeader,"" b) NEW Add new record in to main table by using getPaymentHeaderDAO().save with
	 * parameters paymentHeader,"" c) EDIT Update record in the main table by using getPaymentHeaderDAO().update with
	 * parameters paymentHeader,"" 3) Delete the record from the workFlow table by using getPaymentHeaderDAO().delete
	 * with parameters paymentHeader,"_Temp" 4) Audit the record in to AuditHeader and AdtPaymentHeader by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtPaymentHeader by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		PaymentHeader paymentHeader = new PaymentHeader();
		BeanUtils.copyProperties((PaymentHeader) auditHeader.getAuditDetail().getModelData(), paymentHeader);

		// Processing Accounting Details
		if (StringUtils.equals(paymentHeader.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			auditHeader = executeAccountingProcess(auditHeader, DateUtility.getAppDate());
		}

		BeanUtils.copyProperties((PaymentHeader) auditHeader.getAuditDetail().getModelData(), paymentHeader);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(paymentHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(paymentHeaderDAO.getPaymentHeader(paymentHeader.getPaymentId(), ""));
		}

		if (paymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(paymentHeader, TableType.MAIN_TAB, tranType));
			getPaymentHeaderDAO().delete(paymentHeader, TableType.MAIN_TAB);
		} else {
			paymentHeader.setRoleCode("");
			paymentHeader.setNextRoleCode("");
			paymentHeader.setTaskId("");
			paymentHeader.setNextTaskId("");
			paymentHeader.setWorkflowId(0);

			if (paymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				paymentHeader.setRecordType("");
				getPaymentHeaderDAO().save(paymentHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				paymentHeader.setRecordType("");
				getPaymentHeaderDAO().update(paymentHeader, TableType.MAIN_TAB);
			}

			// PaymentDetails
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> paymentDetails = paymentHeader.getAuditDetailMap().get("PaymentDetails");
				if (paymentDetails != null && !paymentDetails.isEmpty()) {
					paymentDetails = this.paymentDetailService.processPaymentDetails(paymentDetails, TableType.MAIN_TAB,
							"doApprove", paymentHeader.getLinkedTranId(), paymentHeader.getFinReference());
					auditDetails.addAll(paymentDetails);
				}
			}
			// PaymentInstruction
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				List<AuditDetail> paymentinstructions = paymentHeader.getAuditDetailMap().get("PaymentInstructions");

				if (paymentinstructions != null && !paymentinstructions.isEmpty()) {
					paymentinstructions = this.paymentInstructionService.processPaymentInstrDetails(paymentinstructions,
							TableType.MAIN_TAB, "doApprove");
					auditDetails.addAll(paymentinstructions);
				}
			}
		}

		auditHeader.setAuditDetails(deleteChilds(paymentHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType()));
		String[] fields = PennantJavaUtil.getFieldDetails(new PaymentHeader(), paymentHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				paymentHeader.getBefImage(), paymentHeader));
		getAuditHeaderDAO().addAudit(auditHeader);

		getPaymentHeaderDAO().delete(paymentHeader, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(paymentHeader);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * split the each transaction based on the Imps Maximum amount
	 * 
	 * @param paymentInstructions
	 * @param instrumentwiseLimit
	 * @return
	 */
	private List<AuditDetail> splitRequest(List<AuditDetail> paymentInstructions) {
		logger.debug("Entering");

		List<AuditDetail> finalPaymentsList = new ArrayList<AuditDetail>();
		InstrumentwiseLimit instrumentwiseLimit = getInstrumentwiseLimitService()
				.getInstrumentWiseModeLimit(DisbursementConstants.PAYMENT_TYPE_IMPS);

		if (instrumentwiseLimit != null) {

			for (int i = 0; i < paymentInstructions.size(); i++) {

				AuditDetail auditDetail = paymentInstructions.get(i);
				PaymentInstruction paymentInstruction = (PaymentInstruction) auditDetail.getModelData();

				if (!PennantConstants.RECORD_TYPE_DEL.equals(paymentInstruction.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equals(paymentInstruction.getRecordType())
						&& DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentInstruction.getPaymentType())
						&& !DisbursementConstants.STATUS_AWAITCON.equals(paymentInstruction.getStatus())
						&& !DisbursementConstants.STATUS_PAID.equals(paymentInstruction.getStatus())
						&& !DisbursementConstants.STATUS_REALIZED.equals(paymentInstruction.getStatus())
						&& !DisbursementConstants.STATUS_REJECTED.equals(paymentInstruction.getStatus())
						&& !DisbursementConstants.STATUS_CANCEL.equals(paymentInstruction.getStatus())) {

					if (paymentInstruction.getPaymentAmount()
							.compareTo(instrumentwiseLimit.getMaxAmtPerInstruction()) > 0) {

						BigDecimal noOfRecords = paymentInstruction.getPaymentAmount()
								.divide(instrumentwiseLimit.getMaxAmtPerInstruction(), 0, RoundingMode.UP);
						int records = noOfRecords.intValueExact();
						BigDecimal totAmount = BigDecimal.ZERO;

						for (int j = 1; j <= records; j++) {
							PaymentInstruction paymentInstr = new PaymentInstruction();
							AuditDetail auditDetailTemp = new AuditDetail();

							BeanUtils.copyProperties(paymentInstruction, paymentInstr);
							BeanUtils.copyProperties(auditDetail, auditDetailTemp);

							if (records == j) {
								paymentInstr
										.setPaymentAmount(paymentInstruction.getPaymentAmount().subtract(totAmount));
							} else {
								paymentInstr.setPaymentAmount(instrumentwiseLimit.getMaxAmtPerInstruction());
							}
							paymentInstr.setPaymentInstructionId(Long.MIN_VALUE);
							paymentInstr.setNewRecord(true);
							paymentInstr.setPostDate(DateUtility.getAppDate());
							totAmount = totAmount.add(instrumentwiseLimit.getMaxAmtPerInstruction());

							auditDetailTemp.setModelData(paymentInstr);

							finalPaymentsList.add(auditDetailTemp);
						}
					} else {
						paymentInstruction.setPostDate(DateUtility.getAppDate());
						finalPaymentsList.add(auditDetail);
					}
				} else {
					finalPaymentsList.add(auditDetail);
				}
			}
		} else {
			finalPaymentsList.addAll(paymentInstructions);
		}

		logger.debug("Leaving");

		return finalPaymentsList;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPaymentHeaderDAO().delete with parameters paymentHeader,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtPaymentHeader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		PaymentHeader paymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				processChildsAudit(deleteChilds(paymentHeader, TableType.TEMP_TAB, auditHeader.getAuditTranType())));
		getPaymentHeaderDAO().delete(paymentHeader, TableType.TEMP_TAB);
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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}
		auditHeader = nextProcess(auditHeader);

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPaymentHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	// =================================== List maintaince
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		PaymentHeader paymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (paymentHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// PaymentDetails
		if (paymentHeader.getPaymentDetailList() != null && paymentHeader.getPaymentDetailList().size() > 0) {
			for (PaymentDetail detail : paymentHeader.getPaymentDetailList()) {
				detail.setPaymentId(paymentHeader.getPaymentId());
				detail.setWorkflowId(paymentHeader.getWorkflowId());
			}
			auditDetailMap.put("PaymentDetails", this.paymentDetailService
					.setPaymentDetailAuditData(paymentHeader.getPaymentDetailList(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PaymentDetails"));
		}

		// Insurance Details
		if (paymentHeader.getPaymentInstruction() != null) {
			PaymentInstruction detail = paymentHeader.getPaymentInstruction();
			detail.setPaymentId(paymentHeader.getPaymentId());
			detail.setWorkflowId(paymentHeader.getWorkflowId());
			detail.setRecordStatus(paymentHeader.getRecordStatus());
			detail.setRecordType(paymentHeader.getRecordType());
			detail.setNewRecord(paymentHeader.isNewRecord());
			detail.setUserDetails(paymentHeader.getUserDetails());
			detail.setLastMntOn(paymentHeader.getLastMntOn());
			detail.setRoleCode(paymentHeader.getRoleCode());
			detail.setNextRoleCode(paymentHeader.getNextRoleCode());
			detail.setTaskId(paymentHeader.getTaskId());
			detail.setNextTaskId(paymentHeader.getNextTaskId());

			auditDetailMap.put("PaymentInstructions",
					this.paymentInstructionService.setPaymentInstructionDetailsAuditData(
							paymentHeader.getPaymentInstruction(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("PaymentInstructions"));
		}

		paymentHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(paymentHeader);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving");

		return auditHeader;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		PaymentHeader paymentHeader = (PaymentHeader) auditHeader.getAuditDetail().getModelData();
		List<AuditDetail> auditDetails = null;

		// PaymentDetails
		if (paymentHeader.getAuditDetailMap().get("PaymentDetails") != null) {
			auditDetails = paymentHeader.getAuditDetailMap().get("PaymentDetails");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.paymentDetailService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}

		//PaymentInstruction
		if (paymentHeader.getAuditDetailMap().get("PaymentInstruction") != null) {
			auditDetails = paymentHeader.getAuditDetailMap().get("PaymentInstructions");
			for (AuditDetail auditDetail : auditDetails) {
				List<ErrorDetail> details = this.paymentInstructionService.validation(auditDetail, usrLanguage, method)
						.getErrorDetails();
				if (details != null) {
					errorDetails.addAll(details);
				}
			}
		}
		logger.debug("Leaving");
		return errorDetails;
	}

	public List<AuditDetail> deleteChilds(PaymentHeader paymentHeader, TableType tableType, String auditTranType) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		// Payment Tax Details for Payables
		getPaymentTaxDetailDAO().deleteByPaymentID(paymentHeader.getPaymentId(), tableType);

		// PaymentDetails
		if (paymentHeader.getPaymentDetailList() != null && !paymentHeader.getPaymentDetailList().isEmpty()) {
			auditDetails.addAll(this.paymentDetailService.delete(paymentHeader.getPaymentDetailList(), tableType,
					auditTranType, paymentHeader.getPaymentId()));
		}
		// PaymentInstructions
		if (paymentHeader.getPaymentInstruction() != null) {
			auditDetails.addAll(this.paymentInstructionService.delete(paymentHeader.getPaymentInstruction(), tableType,
					auditTranType, paymentHeader.getPaymentId()));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> processChildsAudit(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (list == null || list.isEmpty()) {
			return auditDetails;
		}
		for (AuditDetail detail : list) {
			String transType = "";
			String rcdType = "";
			Object object = detail.getModelData();

			if (object instanceof PaymentDetail) {
				rcdType = ((PaymentDetail) object).getRecordType();
			} else if (object instanceof PaymentInstruction) {
				rcdType = ((PaymentInstruction) object).getRecordType();
			}

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}
			auditDetails.add(new AuditDetail(transType, detail.getAuditSeq(), detail.getBefImage(), object));
		}
		logger.debug("Leaving");

		return auditDetails;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param auditHeader
	 * @param appDate
	 * @return
	 * @throws AccountNotFoundException
	 */
	public AuditHeader executeAccountingProcess(AuditHeader auditHeader, Date appDate) throws InterfaceException {
		logger.debug("Entering");

		AEEvent aeEvent = new AEEvent();
		PaymentHeader paymentHeader = new PaymentHeader();
		BeanUtils.copyProperties((PaymentHeader) auditHeader.getAuditDetail().getModelData(), paymentHeader);

		if (paymentHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_PAYMTINS);
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			if (amountCodes == null) {
				amountCodes = new AEAmountCodes();
			}
			FinanceMain financeMain = getPaymentHeaderDAO().getFinanceDetails(paymentHeader.getFinReference());
			amountCodes.setFinType(financeMain.getFinType());
			aeEvent.setBranch(financeMain.getFinBranch());
			aeEvent.setCustID(financeMain.getCustID());

			PaymentInstruction paymentInstruction = paymentHeader.getPaymentInstruction();
			if (paymentInstruction != null) {
				amountCodes.setPartnerBankAc(paymentInstruction.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(paymentInstruction.getPartnerBankAcType());
				aeEvent.setValueDate(paymentInstruction.getPostDate());
			}

			// GST parameters
			String highPriorityState = null;
			String highPriorityCountry = null;

			// Fetch High priority Address
			CustomerAddres addres = getCustomerAddresDAO().getHighPriorityCustAddr(financeMain.getCustID(), "");
			if (addres != null) {
				highPriorityState = addres.getCustAddrProvince();
				highPriorityCountry = addres.getCustAddrCountry();
			}

			// Set Tax Details if Already exists
			FinanceTaxDetail taxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeMain.getFinReference(),
					"");
			Map<String, Object> gstExecutionMap = getFinFeeDetailService().prepareGstMappingDetails(
					financeMain.getFinBranch(), financeMain.getFinBranch(), highPriorityState, highPriorityCountry,
					taxDetail, financeMain.getFinBranch());

			aeEvent.setCcy(financeMain.getFinCcy());
			aeEvent.setFinReference(financeMain.getFinReference());
			aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

			BigDecimal excessAmount = BigDecimal.ZERO;
			BigDecimal emiInAdavance = BigDecimal.ZERO;
			Map<String, Object> eventMapping = aeEvent.getDataMap();
			for (PaymentDetail paymentDetail : paymentHeader.getPaymentDetailList()) {
				if (String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE).equals(paymentDetail.getAmountType())) {
					BigDecimal payableAmount = BigDecimal.ZERO;
					if (eventMapping.containsKey(paymentDetail.getFeeTypeCode() + "_P")) {
						payableAmount = (BigDecimal) eventMapping.get(paymentDetail.getFeeTypeCode() + "_P");
					}
					eventMapping.put(paymentDetail.getFeeTypeCode() + "_P",
							payableAmount.add(paymentDetail.getAmount()));

					if (paymentDetail.getPaymentTaxDetail() != null) {

						PaymentTaxDetail tax = paymentDetail.getPaymentTaxDetail();

						//CGST
						BigDecimal gstAmount = BigDecimal.ZERO;
						if (eventMapping.containsKey(paymentDetail.getFeeTypeCode() + "_CGST_P")) {
							gstAmount = (BigDecimal) eventMapping.get(paymentDetail.getFeeTypeCode() + "_CGST_P");
						}
						eventMapping.put(paymentDetail.getFeeTypeCode() + "_CGST_P", gstAmount.add(tax.getPaidCGST()));

						//SGST
						gstAmount = BigDecimal.ZERO;
						if (eventMapping.containsKey(paymentDetail.getFeeTypeCode() + "_SGST_P")) {
							gstAmount = (BigDecimal) eventMapping.get(paymentDetail.getFeeTypeCode() + "_SGST_P");
						}
						eventMapping.put(paymentDetail.getFeeTypeCode() + "_SGST_P", gstAmount.add(tax.getPaidSGST()));

						//UGST
						gstAmount = BigDecimal.ZERO;
						if (eventMapping.containsKey(paymentDetail.getFeeTypeCode() + "_UGST_P")) {
							gstAmount = (BigDecimal) eventMapping.get(paymentDetail.getFeeTypeCode() + "_UGST_P");
						}
						eventMapping.put(paymentDetail.getFeeTypeCode() + "_UGST_P", gstAmount.add(tax.getPaidUGST()));

						//IGST
						gstAmount = BigDecimal.ZERO;
						if (eventMapping.containsKey(paymentDetail.getFeeTypeCode() + "_IGST_P")) {
							gstAmount = (BigDecimal) eventMapping.get(paymentDetail.getFeeTypeCode() + "_IGST_P");
						}
						eventMapping.put(paymentDetail.getFeeTypeCode() + "_IGST_P", gstAmount.add(tax.getPaidIGST()));

					}
				} else if ("E".equals(paymentDetail.getAmountType())) {
					excessAmount = excessAmount.add(paymentDetail.getAmount());
				} else if ("A".equals(paymentDetail.getAmountType())) {
					emiInAdavance = emiInAdavance.add(paymentDetail.getAmount());
				}
			}
			eventMapping.put("pi_excessAmount", excessAmount);
			eventMapping.put("pi_emiInAdvance", emiInAdavance);
			eventMapping.put("pi_paymentAmount", paymentHeader.getPaymentInstruction().getPaymentAmount());
			aeEvent.setDataMap(eventMapping);

			if (gstExecutionMap != null) {
				for (String mapkey : gstExecutionMap.keySet()) {
					if (StringUtils.isNotBlank(mapkey)) {
						aeEvent.getDataMap().put(mapkey, gstExecutionMap.get(mapkey));
					}
				}
			}

			long accountsetId = AccountingConfigCache.getAccountSetID(financeMain.getFinType(),
					AccountEventConstants.ACCEVENT_PAYMTINS, FinanceConstants.MODULEID_FINTYPE);

			aeEvent.getAcSetIDList().add(accountsetId);

			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		}
		paymentHeader.setLinkedTranId(aeEvent.getLinkedTranId());
		auditHeader.getAuditDetail().setModelData(paymentHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	@Override
	public PaymentInstruction getPaymentInstruction(long paymentId) {
		return this.paymentInstructionService.getPaymentInstruction(paymentId);
	}
	

	public PaymentTaxDetailDAO getPaymentTaxDetailDAO() {
		return paymentTaxDetailDAO;
	}

	public void setPaymentTaxDetailDAO(PaymentTaxDetailDAO paymentTaxDetailDAO) {
		this.paymentTaxDetailDAO = paymentTaxDetailDAO;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public InstrumentwiseLimitService getInstrumentwiseLimitService() {
		return instrumentwiseLimitService;
	}

	public void setInstrumentwiseLimitService(InstrumentwiseLimitService instrumentwiseLimitService) {
		this.instrumentwiseLimitService = instrumentwiseLimitService;
	}

	@Override
	public boolean getPaymentHeadersByFinReference(String finReference, String type) {
		// TODO Auto-generated method stub
		return false;
	}
}