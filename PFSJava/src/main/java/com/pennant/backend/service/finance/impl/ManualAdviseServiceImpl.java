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
 * FileName    		:  ManualAdviseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.AdviseDueTaxDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ManualAdvise</b>.<br>
 */
public class ManualAdviseServiceImpl extends GenericService<ManualAdvise> implements ManualAdviseService {
	private static final Logger logger = LogManager.getLogger(ManualAdviseServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FeeTypeService feeTypeService;
	private FinanceDetailService financeDetailService;
	private FinFeeDetailService finFeeDetailService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private GSTInvoiceTxnService gstInvoiceTxnService;
	private FinanceMainDAO financeMainDAO;

	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentManagerDAO documentManagerDAO;

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
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the manualAdviseDAO
	 */
	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	/**
	 * @param manualAdviseDAO
	 *            the manualAdviseDAO to set
	 */
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table ManualAdvise/ManualAdvise_Temp
	 * by using ManualAdviseDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using ManualAdviseDAO's update method 3) Audit the record in to AuditHeader and AdtManualAdvise
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (manualAdvise.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (manualAdvise.isNew()) {
			manualAdvise.setId(Long.parseLong(getManualAdviseDAO().save(manualAdvise, tableType)));
			auditHeader.getAuditDetail().setModelData(manualAdvise);
			auditHeader.setAuditReference(String.valueOf(manualAdvise.getAdviseID()));
		} else {
			getManualAdviseDAO().update(manualAdvise, tableType);
		}

		//Document Details
		List<DocumentDetails> documentsList = manualAdvise.getDocumentDetails();
		if (CollectionUtils.isNotEmpty(documentsList)) {
			List<AuditDetail> details = manualAdvise.getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, manualAdvise, tableType.getSuffix());
			auditDetails.addAll(details);
			auditHeader.setAuditDetails(auditDetails);
		}

		String rcdMaintainSts = FinServiceEvent.MANUALADVISE;
		financeMainDAO.updateMaintainceStatus(manualAdvise.getFinReference(), rcdMaintainSts);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ManualAdvise by using ManualAdviseDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtManualAdvise by using auditHeaderDAO.addAudit(auditHeader)
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

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		getManualAdviseDAO().delete(manualAdvise, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getManualAdvise fetch the details by using ManualAdviseDAO's getManualAdviseById method.
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise.
	 * @return ManualAdvise
	 */
	@Override
	public ManualAdvise getManualAdviseById(long adviseID) {
		ManualAdvise manualAdvise = getManualAdviseDAO().getManualAdviseById(adviseID, "_View");
		if (manualAdvise != null) {
			// Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					String.valueOf(manualAdvise.getAdviseID()), PennantConstants.PAYABLE_ADVISE_DOC_MODULE_NAME,
					FinServiceEvent.RECEIPT, "_View");
			if (CollectionUtils.isNotEmpty(manualAdvise.getDocumentDetails())) {
				manualAdvise.getDocumentDetails().addAll(documentList);
			} else {
				manualAdvise.setDocumentDetails(documentList);
			}

		}
		return manualAdvise;
	}

	/**
	 * getApprovedManualAdviseById fetch the details by using ManualAdviseDAO's getManualAdviseById method . with
	 * parameter id and type as blank. it fetches the approved records from the ManualAdvise.
	 * 
	 * @param adviseID
	 *            adviseID of the ManualAdvise. (String)
	 * @return ManualAdvise
	 */
	public ManualAdvise getApprovedManualAdvise(long adviseID) {
		ManualAdvise manualAdvise = getManualAdviseDAO().getManualAdviseById(adviseID, "_AView");
		if (manualAdvise != null) {
			// Document Details
			List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(
					String.valueOf(manualAdvise.getAdviseID()), PennantConstants.PAYABLE_ADVISE_DOC_MODULE_NAME,
					FinServiceEvent.RECEIPT, "_AView");
			if (CollectionUtils.isNotEmpty(manualAdvise.getDocumentDetails())) {
				manualAdvise.getDocumentDetails().addAll(documentList);
			} else {
				manualAdvise.setDocumentDetails(documentList);
			}
		}
		return manualAdvise;
	}

	@Override
	public String getTaxComponent(Long adviseID, String type) {
		return getManualAdviseDAO().getTaxComponent(adviseID, type);
	}

	/**
	 * Getting advice fee type.
	 * 
	 * @param manualAdvise
	 * @return
	 */
	@Override
	public ManualAdvise getAdviceFeeType(ManualAdvise manualAdvise) {
		FeeType javaFeeType = this.feeTypeService.getApprovedFeeTypeById(manualAdvise.getFeeTypeID());
		if (javaFeeType != null) {
			com.pennant.backend.model.finance.FeeType modelFeeType = new com.pennant.backend.model.finance.FeeType();
			BeanUtils.copyProperties(javaFeeType, modelFeeType);
			manualAdvise.setFeeType(modelFeeType);
		}
		return manualAdvise;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getManualAdviseDAO().delete with
	 * parameters manualAdvise,"" b) NEW Add new record in to main table by using getManualAdviseDAO().save with
	 * parameters manualAdvise,"" c) EDIT Update record in the main table by using getManualAdviseDAO().update with
	 * parameters manualAdvise,"" 3) Delete the record from the workFlow table by using getManualAdviseDAO().delete with
	 * parameters manualAdvise,"_Temp" 4) Audit the record in to AuditHeader and AdtManualAdvise by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtManualAdvise by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = new ManualAdvise();
		BeanUtils.copyProperties((ManualAdvise) auditHeader.getAuditDetail().getModelData(), manualAdvise);

		// Processing Accounting Details
		if (StringUtils.equals(manualAdvise.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			com.pennant.backend.model.finance.FeeType feeType = manualAdvise.getFeeType();
			if (feeType != null && feeType.isDueAccReq()) {
				manualAdvise = executeDueAccountingProcess(manualAdvise, auditHeader.getAuditBranchCode());
			}
		}

		if (StringUtils.equals(manualAdvise.getFinSource(), UploadConstants.FINSOURCE_ID_PFF)) {
			getManualAdviseDAO().delete(manualAdvise, TableType.TEMP_TAB);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(manualAdvise.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(manualAdviseDAO.getManualAdviseById(manualAdvise.getAdviseID(), ""));
		}

		if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getManualAdviseDAO().delete(manualAdvise, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion(manualAdvise, TableType.MAIN_TAB.getSuffix(), tranType));
		} else {
			manualAdvise.setRoleCode("");
			manualAdvise.setNextRoleCode("");
			manualAdvise.setTaskId("");
			manualAdvise.setNextTaskId("");
			manualAdvise.setWorkflowId(0);

			if (manualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				manualAdvise.setRecordType("");
				getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				manualAdvise.setRecordType("");
				getManualAdviseDAO().update(manualAdvise, TableType.MAIN_TAB);
			}

			//Document Details
			List<DocumentDetails> documentsList = manualAdvise.getDocumentDetails();
			if (CollectionUtils.isNotEmpty(documentsList)) {
				List<AuditDetail> details = manualAdvise.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, manualAdvise, TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(details);
			}
		}

		if (!manualAdvise.isNewRecord()) {
			//deleting data from _temp tables while Approve
			auditHeader.setAuditDetails(
					listDeletion(manualAdvise, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		}

		financeMainDAO.updateMaintainceStatus(manualAdvise.getFinReference(), "");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetails(auditDetails);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(manualAdvise);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getManualAdviseDAO().delete with parameters manualAdvise,"_Temp" 3) Audit the record in
	 * to AuditHeader and AdtManualAdvise by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditDetails.addAll(listDeletion(manualAdvise, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		manualAdviseDAO.delete(manualAdvise, TableType.TEMP_TAB);
		financeMainDAO.updateMaintainceStatus(manualAdvise.getFinReference(), "");
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private ManualAdvise executeDueAccountingProcess(ManualAdvise advise, String postBranch) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetails(advise.getFinReference());

		AEEvent aeEvent = prepareAccSetData(advise, postBranch, financeMain);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Advise Due accounting postings failed. Please contact Adminstrator.");
		}

		long linkedTranId = aeEvent.getLinkedTranId();
		if (linkedTranId <= 0) {
			logger.debug(Literal.LEAVING);
			return advise;
		}

		// Resetting Advise Data
		advise.setLinkedTranId(linkedTranId);

		boolean isGSTInvOnDue = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
		if (!isGSTInvOnDue) {
			return advise;
		}

		advise.setDueCreation(true);

		// GST Invoice Preparation for Receivable Advise/ Bounce
		ManualAdviseMovements advMovement = aeEvent.getMovement();
		AdviseDueTaxDetail detail = new AdviseDueTaxDetail();

		List<Taxes> taxDetails = advMovement.getTaxHeader().getTaxDetails();
		BigDecimal gstAmount = BigDecimal.ZERO;
		for (Taxes taxes : taxDetails) {
			gstAmount = gstAmount.add(taxes.getPaidTax());
			String taxType = taxes.getTaxType();

			switch (taxType) {
			case RuleConstants.CODE_CGST:
				detail.setCGST(taxes.getPaidTax());
				break;
			case RuleConstants.CODE_SGST:
				detail.setSGST(taxes.getPaidTax());
				break;
			case RuleConstants.CODE_IGST:
				detail.setIGST(taxes.getPaidTax());
				break;
			case RuleConstants.CODE_UGST:
				detail.setUGST(taxes.getPaidTax());
				break;
			case RuleConstants.CODE_CESS:
				detail.setCESS(taxes.getPaidTax());
				break;
			default:
				break;
			}

		}
		detail.setAmount(advMovement.getPaidAmount());
		detail.setTotalGST(gstAmount);

		Long invoiceID = null;
		if (gstAmount.compareTo(BigDecimal.ZERO) > 0 && isGSTInvOnDue) {
			List<ManualAdviseMovements> advMovements = new ArrayList<>();
			advMovements.add(advMovement);
			FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);

			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setLinkedTranId(linkedTranId);
			invoiceDetail.setFinanceDetail(financeDetail);
			invoiceDetail.setWaiver(false);
			invoiceDetail.setMovements(advMovements);
			invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

			invoiceID = this.gstInvoiceTxnService.advTaxInvoicePreparation(invoiceDetail);

		}

		// saving Due tax advice details
		saveDueTaxDetail(advise, detail, invoiceID);

		logger.debug(Literal.LEAVING);
		return advise;
	}

	/**
	 * Saving Due tax advice details
	 * 
	 * @param advise
	 */
	private void saveDueTaxDetail(ManualAdvise advise, AdviseDueTaxDetail detail, Long invoiceID) {

		detail.setAdviseID(advise.getAdviseID());
		detail.setTaxType(advise.getTaxComponent());
		detail.setAmount(advise.getAdviseAmount());
		detail.setInvoiceID(invoiceID);

		detail.setTotalGST(detail.getCGST().add(detail.getSGST()).add(detail.getIGST()).add(detail.getUGST())
				.add(detail.getCESS()));

		// Saving Tax Details
		getManualAdviseDAO().saveDueTaxDetail(detail);
	}

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterfaceException
	 * 
	 */
	@Override
	public List<ReturnDataSet> getAccountingSetEntries(ManualAdvise manualAdvise) throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetails(manualAdvise.getFinReference());
		AEEvent aeEvent = prepareAccSetData(manualAdvise, "", financeMain);
		aeEvent = postingsPreparationUtil.getAccounting(aeEvent);

		logger.debug(Literal.LEAVING);
		return aeEvent.getReturnDataSet();
	}

	/**
	 * 
	 * @param advise
	 * @return
	 */
	private AEEvent prepareAccSetData(ManualAdvise advise, String postBranch, FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		boolean taxApplicable = advise.isTaxApplicable();
		String taxType = advise.getTaxComponent();
		BigDecimal adviseAmount = advise.getAdviseAmount();

		AEEvent aeEvent = new AEEvent();

		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_ADVDUE);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Finance main
		amountCodes.setFinType(financeMain.getFinType());

		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setValueDate(advise.getValueDate());
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(financeMain.getEntityCode());

		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		Map<String, Object> eventMapping = aeEvent.getDataMap();

		TaxHeader taxHeader = null;
		if (taxApplicable) {

			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(financeMain.getFinReference());

			taxHeader = new TaxHeader();
			taxHeader.setNewRecord(true);
			taxHeader.setRecordType(PennantConstants.RCD_ADD);
			taxHeader.setVersion(taxHeader.getVersion() + 1);
			if (taxHeader.getTaxDetails() == null) {
				taxHeader.setTaxDetails(new ArrayList<>());
			}

			//CGST
			Taxes cgstTax = getTaxDetail(RuleConstants.CODE_CGST, taxPercentages.get(RuleConstants.CODE_CGST),
					taxHeader);
			taxHeader.getTaxDetails().add(cgstTax);

			//SGST
			Taxes sgstTax = getTaxDetail(RuleConstants.CODE_SGST, taxPercentages.get(RuleConstants.CODE_SGST),
					taxHeader);
			taxHeader.getTaxDetails().add(sgstTax);

			//IGST
			Taxes igstTax = getTaxDetail(RuleConstants.CODE_IGST, taxPercentages.get(RuleConstants.CODE_IGST),
					taxHeader);
			taxHeader.getTaxDetails().add(igstTax);

			//UGST
			Taxes ugstTax = getTaxDetail(RuleConstants.CODE_UGST, taxPercentages.get(RuleConstants.CODE_UGST),
					taxHeader);
			taxHeader.getTaxDetails().add(ugstTax);

			//CESS percentage
			Taxes cessTax = getTaxDetail(RuleConstants.CODE_CESS, taxPercentages.get(RuleConstants.CODE_CESS),
					taxHeader);
			taxHeader.getTaxDetails().add(cessTax);

			TaxAmountSplit taxSplit = null;
			if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
				taxSplit = GSTCalculator.getExclusiveGST(adviseAmount, taxPercentages);
			} else if (StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
				taxSplit = GSTCalculator.getInclusiveGST(adviseAmount, taxPercentages);
			}

			cgstTax.setPaidTax(taxSplit.getcGST());
			sgstTax.setPaidTax(taxSplit.getsGST());
			igstTax.setPaidTax(taxSplit.getiGST());
			ugstTax.setPaidTax(taxSplit.getuGST());
			cessTax.setPaidTax(taxSplit.getCess());

			// Total GST
			BigDecimal totalGstAmount = cgstTax.getPaidTax().add(sgstTax.getPaidTax()).add(igstTax.getPaidTax())
					.add(ugstTax.getPaidTax()).add(cessTax.getPaidTax());

			eventMapping.put("ae_feeCGST", cgstTax.getPaidTax());
			eventMapping.put("ae_feeSGST", sgstTax.getPaidTax());
			eventMapping.put("ae_feeIGST", igstTax.getPaidTax());
			eventMapping.put("ae_feeUGST", ugstTax.getPaidTax());
			eventMapping.put("ae_feeCESS", cessTax.getPaidTax());
			eventMapping.put("ae_feeGST_TOT", totalGstAmount);

			ManualAdviseMovements advMovement = new ManualAdviseMovements();
			advMovement.setFeeTypeCode(advise.getFeeTypeCode());
			advMovement.setFeeTypeDesc(advise.getFeeTypeDesc());
			advMovement.setMovementAmount(advise.getAdviseAmount());
			advMovement.setTaxApplicable(taxApplicable);
			advMovement.setTaxComponent(taxType);

			if (BigDecimal.ZERO.compareTo(totalGstAmount) == 0) {
				advMovement.setPaidAmount(adviseAmount);
			} else {
				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
					advMovement.setPaidAmount(adviseAmount.subtract(totalGstAmount));
				} else {
					advMovement.setPaidAmount(adviseAmount);
				}
			}

			advMovement.setTaxHeader(taxHeader);
			aeEvent.setMovement(advMovement);
		}

		// GST parameters
		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(financeMain.getFinReference());
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					eventMapping.put(key, gstExecutionMap.get(key));
				}
			}
		}

		eventMapping.put("ae_feeAmount", adviseAmount);
		aeEvent.setDataMap(eventMapping);
		aeEvent.getAcSetIDList().add(advise.getFeeType().getDueAccSet());

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	private Taxes getTaxDetail(String taxType, BigDecimal taxPerc, TaxHeader taxHeader) {
		Taxes taxes = new Taxes();
		taxes.setTaxType(taxType);
		taxes.setTaxPerc(taxPerc);
		return taxes;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getManualAdviseDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<ManualAdviseMovements> getAdivseMovements(long id) {
		return getManualAdviseDAO().getAdviseMovements(id);
	}

	@Override
	public FinanceMain getFinanceDetails(String finReference) {
		return manualAdviseDAO.getFinanceDetails(finReference);
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public GSTInvoiceTxnService getGstInvoiceTxnService() {
		return gstInvoiceTxnService;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}

	@Override
	public long getNewAdviseID() {
		return manualAdviseDAO.getNewAdviseID();
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		ManualAdvise manualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (manualAdvise.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		//Document Details
		if (CollectionUtils.isNotEmpty(manualAdvise.getDocumentDetails())) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(manualAdvise, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		manualAdvise.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(manualAdvise);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param manualAdvise
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setDocumentDetailsAuditData(ManualAdvise manualAdvise, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		DocumentDetails document = new DocumentDetails();
		String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
		for (int i = 0; i < manualAdvise.getDocumentDetails().size(); i++) {
			DocumentDetails documentDetails = manualAdvise.getDocumentDetails().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(documentDetails.getRecordType()))) {
				continue;
			}

			documentDetails.setWorkflowId(manualAdvise.getWorkflowId());
			boolean isRcdType = false;

			if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (manualAdvise.isWorkflow()) {
					isRcdType = true;
				}
			} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				documentDetails.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| documentDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			documentDetails.setRecordStatus(manualAdvise.getRecordStatus());
			documentDetails.setUserDetails(manualAdvise.getUserDetails());
			documentDetails.setLastMntOn(manualAdvise.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetails.getBefImage(),
					documentDetails));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Document Details
	 * 
	 * @param auditDetails
	 * @param manualAdvise
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingDocumentDetailsList(List<AuditDetail> auditDetails, ManualAdvise manualAdvise,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		for (int i = 0; i < auditDetails.size(); i++) {

			DocumentDetails documentDetails = (DocumentDetails) auditDetails.get(i).getModelData();
			documentDetails.setReferenceId(String.valueOf(manualAdvise.getId()));
			if (StringUtils.isBlank(documentDetails.getRecordType())) {
				continue;
			}
			if (!(DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode()))) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				boolean isTempRecord = false;
				if (StringUtils.isEmpty(type) || type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					documentDetails.setRoleCode("");
					documentDetails.setNextRoleCode("");
					documentDetails.setTaskId("");
					documentDetails.setNextTaskId("");
				}
				documentDetails.setLastMntBy(manualAdvise.getLastMntBy());
				documentDetails.setWorkflowId(0);

				if (DocumentCategories.CUSTOMER.getKey().equals(documentDetails.getCategoryCode())) {
					approveRec = true;
				}

				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(documentDetails.getRecordType())) {
					deleteRecord = true;
					isTempRecord = true;
				} else if (documentDetails.isNewRecord()) {
					saveRecord = true;
					if (PennantConstants.RCD_ADD.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(documentDetails.getRecordType())) {
						documentDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(documentDetails.getRecordType())) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(documentDetails.getRecordType())) {
					updateRecord = true;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(documentDetails.getRecordType())) {
					if (approveRec) {
						deleteRecord = true;
					} else if (documentDetails.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}

				if (approveRec) {
					rcdType = documentDetails.getRecordType();
					recordStatus = documentDetails.getRecordStatus();
					documentDetails.setRecordType("");
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					if (StringUtils.isEmpty(documentDetails.getReferenceId())) {
						documentDetails.setReferenceId(String.valueOf(manualAdvise.getId()));
					}
					documentDetails.setFinEvent(FinServiceEvent.RECEIPT);
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
					}
					if (documentDetails.getDocId() < 0) {
						documentDetails.setDocId(Long.MIN_VALUE);
					}
					documentDetailsDAO.save(documentDetails, type);
				}

				if (updateRecord) {
					// When a document is updated, insert another file into the DocumentManager table's.
					// Get the new DocumentManager.id & set to documentDetails.getDocRefId()
					if (documentDetails.getDocImage() != null && documentDetails.getDocRefId() <= 0) {
						DocumentManager documentManager = new DocumentManager();
						documentManager.setDocImage(documentDetails.getDocImage());
						documentDetails.setDocRefId(documentManagerDAO.save(documentManager));
					}
					documentDetailsDAO.update(documentDetails, type);
				}

				if (deleteRecord && ((StringUtils.isEmpty(type) && !isTempRecord) || (StringUtils.isNotEmpty(type)))) {
					if (!type.equals(PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
						documentDetailsDAO.delete(documentDetails, type);
					}
				}

				if (approveRec) {
					documentDetails.setFinEvent("");
					documentDetails.setRecordType(rcdType);
					documentDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(documentDetails);
			}
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	// Method for Deleting all records related to receipt in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(ManualAdvise manualAdvise, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Document Details. 
		List<AuditDetail> documentDetails = manualAdvise.getAuditDetailMap().get("DocumentDetails");
		if (documentDetails != null && documentDetails.size() > 0) {
			DocumentDetails document = new DocumentDetails();
			DocumentDetails documentDetail = null;
			List<DocumentDetails> docList = new ArrayList<DocumentDetails>();
			String[] fields = PennantJavaUtil.getFieldDetails(document, document.getExcludeFields());
			for (int i = 0; i < documentDetails.size(); i++) {
				documentDetail = (DocumentDetails) documentDetails.get(i).getModelData();
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				docList.add(documentDetail);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], documentDetail.getBefImage(),
						documentDetail));
			}
			documentDetailsDAO.deleteList(docList, tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}