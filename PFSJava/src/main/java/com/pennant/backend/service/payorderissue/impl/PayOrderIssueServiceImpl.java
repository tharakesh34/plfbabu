/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PayOrderIssueHeaders. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * RepayOrderIssueHeaderion or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PayOrderIssueHeaderServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
 * 16-05-2018       Madhubabu                  0.2          added the validation for        * 
 *                                                        disbursement  by checking Otc/PDD * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.payorderissue.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.pennydrop.PennyDropDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.payment.PaymentsProcessService;
import com.pennant.backend.service.payorderissue.PayOrderIssueService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.external.BankAccountValidationService;

/**
 * Service implementation for methods that depends on <b>PayOrderIssueHeader</b>.<br>
 * 
 */
public class PayOrderIssueServiceImpl extends GenericService<PayOrderIssueHeader> implements PayOrderIssueService {
	private static final Logger logger = Logger.getLogger(PayOrderIssueServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private BeneficiaryDAO beneficiaryDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private FinanceMainDAO financeMainDAO;
	private DisbursementPostings disbursementPostings;
	private FinAdvancePaymentsService finAdvancePaymentsService; // ##PSD:
																	// 128172-Auto
																	// move the
																	// data to
																	// staging
																	// table
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	private FinCovenantTypeDAO finCovenantTypeDAO;
	@Autowired
	private VASRecordingDAO vasRecordingDAO;
	private PartnerBankService partnerBankService;
	@Autowired
	private PostingsDAO postingsDAO;
	private PaymentsProcessService paymentsProcessService;
	@Autowired
	private PennyDropDAO pennyDropDAO;
	@Autowired(required = false)
	private transient BankAccountValidationService bankAccountValidationService;

	public PayOrderIssueServiceImpl() {
		super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public PayOrderIssueHeaderDAO getPayOrderIssueHeaderDAO() {
		return payOrderIssueHeaderDAO;
	}

	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	public FinCovenantTypeDAO getFinCovenantTypeDAO() {
		return finCovenantTypeDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * BMTPayOrderIssueHeader/BMTPayOrderIssueHeader_Temp by using PayOrderIssueHeaderDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using PayOrderIssueHeaderDAO's update method
	 * 3) Audit the record in to AuditHeader and AdtBMTPayOrderIssueHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		PayOrderIssueHeader payOrderIssueHeader = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();

		if (payOrderIssueHeader.isWorkflow()) {
			tableType = "_Temp";
		}

		if (payOrderIssueHeader.isNew()) {
			getPayOrderIssueHeaderDAO().save(payOrderIssueHeader, tableType);
		} else {
			getPayOrderIssueHeaderDAO().update(payOrderIssueHeader, tableType);
		}

		List<AuditDetail> details = processFinAdvancepayments(payOrderIssueHeader, tableType, null);
		auditDetails.addAll(details);

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, payOrderIssueHeader.getBefImage(),
				payOrderIssueHeader));
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * BMTPayOrderIssueHeader by using PayOrderIssueHeaderDAO's delete method with type as Blank 3) Audit the record in
	 * to AuditHeader and AdtBMTPayOrderIssueHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		PayOrderIssueHeader payOrderIssueHeader = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();
		getPayOrderIssueHeaderDAO().delete(payOrderIssueHeader, "");
		List<FinAdvancePayments> list = payOrderIssueHeader.getFinAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(payOrderIssueHeader.getFinReference(), "_Temp");
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getPayOrderIssueHeaderById fetch the details by using PayOrderIssueHeaderDAO's getPayOrderIssueHeaderById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return PayOrderIssueHeader
	 */

	@Override
	public PayOrderIssueHeader getPayOrderIssueHeaderById(String id) {
		logger.debug("Entering");
		PayOrderIssueHeader issueHeader = getPayOrderIssueHeaderDAO().getPayOrderIssueByHeaderRef(id, "_View");
		FinanceMain finMian = getFinanceMainDAO().getDisbursmentFinMainById(issueHeader.getFinReference(),
				TableType.MAIN_TAB);
		issueHeader.setLoanApproved(true);
		if (finMian == null) {
			finMian = getFinanceMainDAO().getDisbursmentFinMainById(id, TableType.TEMP_TAB);
			issueHeader.setLoanApproved(false);
		}
		issueHeader.setFinanceMain(finMian);

		issueHeader.setFinAdvancePaymentsList(
				finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(issueHeader.getFinReference(), "_View"));

		List<FinanceDisbursement> teemList = financeDisbursementDAO
				.getFinanceDisbursementDetails(issueHeader.getFinReference(), TableType.TEMP_TAB.getSuffix(), false);
		List<FinanceDisbursement> mainList = financeDisbursementDAO
				.getFinanceDisbursementDetails(issueHeader.getFinReference(), TableType.MAIN_TAB.getSuffix(), false);

		List<FinanceDisbursement> deductDisbFeeList = financeDisbursementDAO
				.getDeductDisbFeeDetails(finMian.getFinReference());

		if (CollectionUtils.isNotEmpty(deductDisbFeeList)) {
			for (FinanceDisbursement disbursement : deductDisbFeeList) {
				for (FinanceDisbursement finDisb : mainList) {
					if (finDisb.getDisbSeq() == disbursement.getDisbSeq()) {
						finDisb.setDeductFeeDisb(disbursement.getDeductFeeDisb());
						break;
					}
				}
			}
		}

		// Covenants List
		issueHeader.setCovenantTypeList(
				getFinCovenantTypeDAO().getFinCovenantTypeByFinRef(issueHeader.getFinReference(), "_View", false));
		// Document details
		issueHeader.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(issueHeader.getFinReference(),
				FinanceConstants.MODULE_NAME, "", ""));

		if (issueHeader.isLoanApproved()) {
			issueHeader.setFinanceDisbursements(mainList);
		} else {
			issueHeader.setFinanceDisbursements(teemList);
		}
		String module = FinanceConstants.MODULE_NAME;
		String document = SysParamUtil.getValueAsString("DISB_DOC");

		if (StringUtils.isNotEmpty(document)) {
			DocumentDetails docDetails = null;
			if (issueHeader.isLoanApproved()) {
				docDetails = documentDetailsDAO.getDocumentDetails(finMian.getFinReference(), document, module,
						TableType.MAIN_TAB.getSuffix());

				issueHeader.setFinanceDisbursements(mainList);

			} else {
				docDetails = documentDetailsDAO.getDocumentDetails(finMian.getFinReference(), document, module,
						TableType.TEMP_TAB.getSuffix());
			}
			issueHeader.setDocumentDetails(docDetails);
		}

		issueHeader.setApprovedFinanceDisbursements(mainList);
		if (SysParamUtil.isAllowed(SMTParameterConstants.INSURANCE_INST_ON_DISB)) {
			issueHeader
					.setvASRecordings(vasRecordingDAO.getVASRecordingsStatusByReference(finMian.getFinReference(), ""));
		}

		logger.debug("Leaving");
		return issueHeader;
	}

	/**
	 * getApprovedPayOrderIssueHeaderById fetch the details by using PayOrderIssueHeaderDAO's getPayOrderIssueHeaderById
	 * method . with parameter id and type as blank. it fetches the approved records from the BMTPayOrderIssueHeader.
	 * 
	 * @param id
	 *            (String)
	 * @return PayOrderIssueHeader
	 */

	public PayOrderIssueHeader getApprovedPayOrderIssueHeaderById(String id, String code) {
		PayOrderIssueHeader payOrderIssueHeader = getPayOrderIssueHeaderDAO().getPayOrderIssueByHeaderRef(id, "_AView");
		payOrderIssueHeader.setFinAdvancePaymentsList(
				finAdvancePaymentsDAO.getFinAdvancePaymentsByFinRef(payOrderIssueHeader.getFinReference(), "_AView"));
		return payOrderIssueHeader;
	}

	/**
	 * ` * doApprove method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * based on the Record type do following actions a) DELETE Delete the record from the main table by using
	 * getPaymentOrderIssueHeaderDAO().delete with parameters payOrderIssueHeader,"" b) NEW Add new record in to main
	 * table by using getPaymentOrderIssueHeaderDAO().save with parameters payOrderIssueHeader,"" c) EDIT Update record
	 * in the main table by using getPaymentOrderIssueHeaderDAO().update with parameters payOrderIssueHeader,"" 3)
	 * Delete the record from the workFlow table by using getPaymentOrderIssueHeaderDAO().delete with parameters
	 * payOrderIssueHeader,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTPayOrderIssueHeader by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtBMTPayOrderIssueHeader by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		PayOrderIssueHeader payOrderIssueHeader = new PayOrderIssueHeader();
		BeanUtils.copyProperties((PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData(),
				payOrderIssueHeader);
		calcluatePOHeaderDetails(payOrderIssueHeader);

		// Splitting IMPS Requests
		if (!PennantConstants.RECORD_TYPE_DEL.equals(payOrderIssueHeader.getRecordType())) {
			payOrderIssueHeader.setFinAdvancePaymentsList(
					finAdvancePaymentsService.splitRequest(payOrderIssueHeader.getFinAdvancePaymentsList()));
		}

		boolean posted = true;
		Map<Integer, Long> data = null;
		if (!SysParamUtil.isAllowed(SMTParameterConstants.HOLD_DISB_INST_POST)) {
			try {
				data = disbursementPostings.prepareDisbPostingApproval(payOrderIssueHeader.getFinAdvancePaymentsList(),
						payOrderIssueHeader.getFinanceMain(), auditHeader.getAuditBranchCode());
				for (Long linkedID : data.values()) {
					if (linkedID == Long.MIN_VALUE) {
						posted = false;
					}
				}
			} catch (Exception e) {
				posted = false;
			}
			if (!posted) {
				auditHeader.setErrorDetails(new ErrorDetail("0000", "Postigs Failed", null));
				return auditHeader;
			}
		}
		if (!posted) {
			auditHeader.setErrorDetails(new ErrorDetail("0000", "Postigs Failed", null));
			return auditHeader;
		}

		if (payOrderIssueHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getPayOrderIssueHeaderDAO().delete(payOrderIssueHeader, "");
		} else {
			payOrderIssueHeader.setRoleCode("");
			payOrderIssueHeader.setNextRoleCode("");
			payOrderIssueHeader.setTaskId("");
			payOrderIssueHeader.setNextTaskId("");
			payOrderIssueHeader.setWorkflowId(0);

			if (payOrderIssueHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				payOrderIssueHeader.setRecordType("");
				getPayOrderIssueHeaderDAO().save(payOrderIssueHeader, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				payOrderIssueHeader.setRecordType("");
				getPayOrderIssueHeaderDAO().update(payOrderIssueHeader, "");
			}

		}

		// Retrieving List of Audit Details For PayOrderIssueHeader Asset
		// related modules
		List<AuditDetail> details = processFinAdvancepayments(payOrderIssueHeader, "", data);
		auditDetails.addAll(details);

		finAdvancePaymentsDAO.deleteByFinRef(payOrderIssueHeader.getFinReference(), "_Temp");

		getPayOrderIssueHeaderDAO().delete(payOrderIssueHeader, "_Temp");

		processPayment(payOrderIssueHeader);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(payOrderIssueHeader);
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	private void processPayment(PayOrderIssueHeader payOrderIssueHeader) {

		List<FinAdvancePayments> advancePayments = payOrderIssueHeader.getFinAdvancePaymentsList();
		List<FinAdvancePayments> resultPayments = new ArrayList<>();
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.setFinScheduleData(new FinScheduleData());
		financeDetail.getFinScheduleData().setFinanceMain(new FinanceMain());
		if (CollectionUtils.isNotEmpty(advancePayments)) {
			for (FinAdvancePayments finAdvancePayments : advancePayments) {
				if (finAdvancePayments.isPaymentProcReq()) {
					resultPayments.add(finAdvancePayments);
					financeDetail.setFinReference(finAdvancePayments.getFinReference());
					financeDetail.getFinScheduleData().getFinanceMain()
							.setFinReference(finAdvancePayments.getFinReference());
					finAdvancePayments.setUserDetails(payOrderIssueHeader.getUserDetails());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(resultPayments)) {
			financeDetail.setAdvancePaymentsList(resultPayments);
			this.paymentsProcessService.process(financeDetail, DisbursementConstants.CHANNEL_DISBURSEMENT);
		}
	}

	private void calcluatePOHeaderDetails(PayOrderIssueHeader payOrderIssueHeader) {
		logger.debug("Entering");

		if (payOrderIssueHeader.getFinAdvancePaymentsList() != null
				&& !payOrderIssueHeader.getFinAdvancePaymentsList().isEmpty()) {
			BigDecimal totIssuedPOAmt = payOrderIssueHeader.getIssuedPOAmount();
			int totIssuedPOCount = payOrderIssueHeader.getIssuedPOCount();
			for (FinAdvancePayments finAdvancePayments : payOrderIssueHeader.getFinAdvancePaymentsList()) {
				if (StringUtils.equals(PennantConstants.PO_STATUS_ISSUE, finAdvancePayments.getPOStatus())) {
					totIssuedPOAmt = totIssuedPOAmt.add(finAdvancePayments.getAmtToBeReleased());
					totIssuedPOCount++;
				}
			}
			payOrderIssueHeader.setIssuedPOAmount(totIssuedPOAmt);
			payOrderIssueHeader.setpODueAmount(payOrderIssueHeader.getTotalPOAmount().subtract(totIssuedPOAmt));
			payOrderIssueHeader.setIssuedPOCount(totIssuedPOCount);
			payOrderIssueHeader.setpODueCount(payOrderIssueHeader.getTotalPOCount() - totIssuedPOCount);
		}

		logger.debug("Leaving");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getPaymentOrderIssueHeaderDAO().delete with parameters payOrderIssueHeader,"_Temp" 3)
	 * Audit the record in to AuditHeader and AdtBMTPayOrderIssueHeader by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		PayOrderIssueHeader payOrderIssueHeader = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getPayOrderIssueHeaderDAO().delete(payOrderIssueHeader, "_Temp");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,

				payOrderIssueHeader.getBefImage(), payOrderIssueHeader));

		List<FinAdvancePayments> list = payOrderIssueHeader.getFinAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			finAdvancePaymentsDAO.deleteByFinRef(payOrderIssueHeader.getFinReference(), "_Temp");
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

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
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getPaymentOrderIssueHeaderDAO().getErrorDPayOrderIssueHeaderith Error ID and language as parameters. if any
	 * error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		PayOrderIssueHeader payOrderIssueHeader = (PayOrderIssueHeader) auditDetail.getModelData();

		PayOrderIssueHeader tempPayOrderIssueHeader = null;
		if (payOrderIssueHeader.isWorkflow()) {
			tempPayOrderIssueHeader = getPayOrderIssueHeaderDAO()
					.getPayOrderIssueByHeaderRef(payOrderIssueHeader.getFinReference(), "_Temp");
		}
		PayOrderIssueHeader befPayOrderIssueHeader = getPayOrderIssueHeaderDAO()
				.getPayOrderIssueByHeaderRef(payOrderIssueHeader.getFinReference(), "");
		PayOrderIssueHeader oldPayOrderIssueHeader = payOrderIssueHeader.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = payOrderIssueHeader.getFinReference();

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (payOrderIssueHeader.isNew()) { // for New record or new record into
												// work flow

			if (!payOrderIssueHeader.isWorkflow()) {// With out Work flow only
														// new records
				if (befPayOrderIssueHeader != null) { // Record Already Exists
															// in the table then
														// error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));

				}
			} else { // with work flow
				if (payOrderIssueHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																										// records
																									// type
																									// is
																									// new
					if (befPayOrderIssueHeader != null || tempPayOrderIssueHeader != null) { // if
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
					if (befPayOrderIssueHeader == null || tempPayOrderIssueHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!payOrderIssueHeader.isWorkflow()) { // With out Work flow for
															// update and delete

				if (befPayOrderIssueHeader == null) { // if records not exists
															// in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldPayOrderIssueHeader != null
							&& !oldPayOrderIssueHeader.getLastMntOn().equals(befPayOrderIssueHeader.getLastMntOn())) {
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

				if (tempPayOrderIssueHeader == null) { // if records not exists
															// in the Work flow
														// table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempPayOrderIssueHeader != null && oldPayOrderIssueHeader != null
						&& !oldPayOrderIssueHeader.getLastMntOn().equals(tempPayOrderIssueHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		if (payOrderIssueHeader.getRecordStatus().equals(PennantConstants.RCD_STATUS_SUBMITTED)) {

			validateOtcPayment(auditDetail, payOrderIssueHeader);
		}

		boolean noValidation = isnoValidationUserAction(payOrderIssueHeader.getRecordStatus());
		if (!noValidation) {
			List<FinAdvancePayments> advpayments = payOrderIssueHeader.getFinAdvancePaymentsList();
			String tableType = null;
			if (payOrderIssueHeader.isLoanApproved()) {
				tableType = TableType.MAIN_TAB.getSuffix();
			} else {
				tableType = TableType.MAIN_TAB.getSuffix();
			}
			List<FinanceDisbursement> finDisbursementDetails = financeDisbursementDAO
					.getFinanceDisbursementDetails(payOrderIssueHeader.getFinReference(), tableType, false);

			for (FinAdvancePayments finAdvancePay : advpayments) {
				if (!isDeleteRecord(finAdvancePay)) {
					for (FinanceDisbursement finDisbursmentDetail : finDisbursementDetails) {
						if (finAdvancePay.getDisbSeq() == finDisbursmentDetail.getDisbSeq()
								&& finAdvancePay.getLlDate() != null && DateUtility
										.compare(finDisbursmentDetail.getDisbDate(), finAdvancePay.getLlDate()) != 0) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "65032", errParm, valueParm),
									usrLanguage));
						}
					}

					String partnerBankBankcode = partnerBankService.getBankCodeById(finAdvancePay.getPartnerBankID());

					if (!StringUtils.equals(finAdvancePay.getBranchBankCode(), partnerBankBankcode)) {
						if (StringUtils.equals(finAdvancePay.getPaymentType(),
								DisbursementConstants.PAYMENT_TYPE_IFT)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "65033", errParm, valueParm),
									usrLanguage));
						}
					} else if (!StringUtils.equals(finAdvancePay.getPaymentType(),
							DisbursementConstants.PAYMENT_TYPE_IFT)) {
						String[] errParam = new String[1];
						errParam[0] = finAdvancePay.getPaymentType();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65038", errParam, valueParm),
								usrLanguage));
					}
				}

				if (bankAccountValidationService != null) {
					int count = pennyDropDAO.getPennyDropCount(finAdvancePay.getBeneficiaryAccNo(),
							finAdvancePay.getiFSC());
					if (count == 0) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41020", errParm, valueParm), usrLanguage));
					}
				}
			}

		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !payOrderIssueHeader.isWorkflow()) {
			auditDetail.setBefImage(befPayOrderIssueHeader);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	private void validateOtcPayment(AuditDetail auditDetail, PayOrderIssueHeader header) {
		// ####_0.2
		// Not allowed to approve loan with Disbursement type if it is
		// not in the configured OTC Types

		String alwrepayMethods = (String) SysParamUtil.getValue("COVENANT_REPAY_OTC_TYPE");
		if (alwrepayMethods != null) {
			String[] valueParm = new String[2];
			boolean isFound = false;
			boolean isOTCPayment = false;
			boolean isDocExist = false;

			String[] repaymethod = alwrepayMethods.split(",");
			if (header.getFinAdvancePaymentsList() != null && header.getFinAdvancePaymentsList().size() > 0) {
				for (FinAdvancePayments finAdvancePayments : header.getFinAdvancePaymentsList()) {
					isFound = false;
					for (String rpymethod : repaymethod) {
						if (StringUtils.equals(finAdvancePayments.getPaymentType(), rpymethod)) {
							isFound = true;
							break;
						}
					}
					if (!isFound) {
						valueParm[0] = finAdvancePayments.getPaymentType();
						isOTCPayment = true;
						break;
					}
				}
				if (isOTCPayment) {
					if (header.getCovenantTypeList() != null && header.getCovenantTypeList().size() > 0) {
						for (FinCovenantType covenantType : header.getCovenantTypeList()) {
							isDocExist = false;
							if (!covenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
								// validate the document against the current
								// list.
								for (DocumentDetails documentDetails : header.getDocumentDetailsList()) {
									if (documentDetails.getDocCategory().equals(covenantType.getCovenantType())
											&& !documentDetails.getRecordType()
													.equals(PennantConstants.RECORD_TYPE_CAN)) {
										isDocExist = true;
										break;
									}
								}
								if (!isDocExist && covenantType.isAlwOtc()) {
									valueParm[1] = Labels.getLabel("label_FinCovenantTypeDialog_AlwOTC.value");
									auditDetail.setErrorDetail(
											ErrorUtil.getErrorDetail(new ErrorDetail("41101", valueParm)));
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param payOrderIssueHeader
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processFinAdvancepayments(PayOrderIssueHeader payOrderIssueHeader, String type,
			Map<Integer, Long> data) {
		logger.debug(" Entering ");

		List<FinAdvancePayments> list = payOrderIssueHeader.getFinAdvancePaymentsList();

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (list == null || list.isEmpty()) {
			return auditDetails;
		}

		int count = 0;

		for (FinAdvancePayments finAdvpay : list) {

			String auditTransType = PennantConstants.TRAN_WF;

			String rcdType = finAdvpay.getRecordType();
			boolean save = true;
			FinAdvancePayments tempfinAdvPay = finAdvancePaymentsDAO.getFinAdvancePaymentsById(finAdvpay, type);
			if (tempfinAdvPay != null) {
				save = false;
			}
			if (StringUtils.isBlank(rcdType)) {
				continue;
			}

			if (rcdType.equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finAdvpay.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}

			finAdvpay.setRecordStatus(payOrderIssueHeader.getRecordStatus());
			finAdvpay.setLastMntOn(payOrderIssueHeader.getLastMntOn());

			if (StringUtils.isEmpty(type)) {
				finAdvpay.setRoleCode("");
				finAdvpay.setNextRoleCode("");
				finAdvpay.setTaskId("");
				finAdvpay.setNextTaskId("");
				finAdvpay.setRecordType("");
				// other
				if (rcdType.equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finAdvpay.setStatus(DisbursementConstants.STATUS_CANCEL);
				} else {
					finAdvpay.setStatus(DisbursementConstants.STATUS_APPROVED);
				}
				finAdvpay.setpOIssued(true);
			}
			if (finAdvpay.isHoldDisbursement()) {
				finAdvpay.setStatus(DisbursementConstants.STATUS_HOLD);
			} else {
				finAdvpay.setStatus(DisbursementConstants.STATUS_NEW);
			}
			if (save) {
				count = count + 1;

				if (StringUtils.isEmpty(type)) {
					auditTransType = PennantConstants.TRAN_ADD;
					if (data != null && data.containsKey(finAdvpay.getPaymentSeq())) {
						finAdvpay.setLinkedTranId(data.get(finAdvpay.getPaymentSeq()));
					}
				}
				finAdvancePaymentsDAO.save(finAdvpay, type);
				if (StringUtils.isEmpty(type)) {
					finAdvpay.setPaymentProcReq(true);
				}
			} else {
				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					finAdvancePaymentsDAO.delete(finAdvpay, type);
					count = count + 1;
					if (StringUtils.isEmpty(type)) {
						auditTransType = PennantConstants.TRAN_DEL;
					}
				} else {
					if (tempfinAdvPay != null) {
						if (tempfinAdvPay.isHoldDisbursement()) {
							finAdvpay.setPaymentProcReq(true);
						}
					}
					if (data != null && data.containsKey(finAdvpay.getPaymentSeq())) {
						finAdvpay.setLinkedTranId(data.get(finAdvpay.getPaymentSeq()));
					}
					finAdvancePaymentsDAO.update(finAdvpay, type);

					count = count + 1;
					if (StringUtils.isEmpty(type)) {
						finAdvpay.setBefImage(tempfinAdvPay);
						auditTransType = PennantConstants.TRAN_UPD;
					}
				}
			}
			auditDetails.add(getAuditDetails(finAdvpay, count, auditTransType));
		}

		logger.debug(" Leaving ");
		return auditDetails;
	}

	public AuditDetail getAuditDetails(FinAdvancePayments finAdvancePayments, int auditSeq, String transType) {
		String[] fields = PennantJavaUtil.getFieldDetails(new FinAdvancePayments(),
				new FinAdvancePayments().getExcludeFields());
		return new AuditDetail(transType, auditSeq, fields[0], fields[1], finAdvancePayments.getBefImage(),
				finAdvancePayments);
	}

	public void addToCustomerBeneficiary(FinAdvancePayments finAdvPay, long cusID) {
		int count = beneficiaryDAO.getBeneficiaryByBankBranchId(finAdvPay.getBeneficiaryAccNo(),
				finAdvPay.getBankBranchID(), "_View");
		if (count == 0) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(cusID);
			beneficiary.setBankBranchID(finAdvPay.getBankBranchID());
			beneficiary.setAccNumber(finAdvPay.getBeneficiaryAccNo());
			beneficiary.setAccHolderName(finAdvPay.getBeneficiaryName());
			beneficiary.setPhoneCountryCode(finAdvPay.getPhoneCountryCode());
			beneficiary.setPhoneAreaCode(finAdvPay.getPhoneAreaCode());
			beneficiary.setPhoneNumber(finAdvPay.getPhoneNumber());
			beneficiaryDAO.save(beneficiary, "");
		}
	}

	private boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(DisbursementConstants.STATUS_CANCEL, aFinAdvancePayments.getStatus())
				|| StringUtils.equals(DisbursementConstants.STATUS_REJECTED, aFinAdvancePayments.getStatus())) {
			return true;
		}
		return false;
	}

	private boolean isnoValidationUserAction(String userAction) {
		boolean noValidation = false;
		if (userAction != null) {
			if (userAction.equalsIgnoreCase("Cancel") || userAction.contains("Reject")
					|| userAction.contains("Resubmit") || userAction.contains("Decline")) {
				noValidation = true;
			}
		}
		return noValidation;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	@Override
	public List<ReturnDataSet> getInsurancePostings(String finReference) {
		return postingsDAO.getPostingsByFinRef(finReference);

	}

	@Override
	public List<ReturnDataSet> getDisbursementPostings(String finReference, String event) {
		return postingsDAO.getPostingsByFinRefAndEvent(finReference, event);
	}

	public PostingsDAO getPostingsDAO() {
		return postingsDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setPaymentsProcessService(PaymentsProcessService paymentsProcessService) {
		this.paymentsProcessService = paymentsProcessService;
	}

}