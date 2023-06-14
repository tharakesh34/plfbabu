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
 * * FileName : UploadHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2017 * *
 * Modified Date : 17-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.dao.finance.FinExpenseMovementsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.UploadFinExpensesDAO;
import com.pennant.backend.dao.finance.UploadFinTypeExpenseDAO;
import com.pennant.backend.dao.finance.UploadHeaderDAO;
import com.pennant.backend.dao.finance.UploadTaxPercentDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.assignmentupload.AssignmentUpload;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.backend.model.expenses.UploadFinExpenses;
import com.pennant.backend.model.expenses.UploadFinTypeExpense;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennant.backend.model.receiptupload.UploadReceipt;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.amtmasters.ExpenseTypeService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.MiscPostingUploadService;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.service.finance.UploadManualAdviseService;
import com.pennant.backend.service.rmtmasters.FinTypeExpenseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.extension.AccountingExtension;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class UploadHeaderServiceImpl extends GenericService<UploadHeader> implements UploadHeaderService {
	private static final Logger logger = LogManager.getLogger(UploadHeaderServiceImpl.class);

	private UploadHeaderDAO uploadHeaderDAO;
	private UploadFinExpensesDAO uploadFinExpensesDAO;
	private FinExpenseDetailsDAO finExpenseDetailsDAO;
	private FinExpenseMovementsDAO finExpenseMovementsDAO;
	private ExpenseTypeService expenseTypeService;
	private FinanceMainDAO financeMainDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinTypeExpenseService finTypeExpenseService;
	private FeeTypeService feeTypeService;
	private UploadTaxPercentDAO uploadTaxPercentDAO;
	private FinFeeDetailService finFeeDetailService;
	private UploadFinTypeExpenseDAO uploadFinTypeExpenseDAO;
	private MiscPostingUploadService miscPostingUploadService;
	private AuditHeaderDAO auditHeaderDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private UploadManualAdviseService uploadManualAdviseService;
	private PostingsPreparationUtil postingsPreparationUtil;

	public FinServiceInstrutionDAO getFinServiceInstructionDAO() {
		return finServiceInstructionDAO;
	}

	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	public UploadHeaderServiceImpl() {
		super();
	}

	@Override
	public UploadHeader getUploadHeader(long uploadId) {
		return this.uploadHeaderDAO.getUploadHeader(uploadId);
	}

	@Override
	public UploadHeader getUploadHeaderById(long uploadId, String type) {
		return this.uploadHeaderDAO.getUploadHeaderById(uploadId, type);
	}

	@Override
	public boolean isFileNameExist(String fileName) {
		return this.uploadHeaderDAO.isFileNameExist(fileName);
	}

	@Override
	public long save(UploadHeader uploadHeader) {
		return this.uploadHeaderDAO.save(uploadHeader);
	}

	@Override
	public void saveUploadFinExpenses(List<UploadFinExpenses> uploadFinExpensesList) {
		logger.debug(Literal.ENTERING);

		this.uploadFinExpensesDAO.saveUploadFinExpenses(uploadFinExpensesList);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinanceMain> getFinancesByExpenseType(String finType, Date finApprovalStartDate,
			Date finApprovalEndDate) {
		return this.financeMainDAO.getFinancesByExpenseType(finType, finApprovalStartDate, finApprovalEndDate);
	}

	@Override
	public long saveFinExpenseDetails(FinExpenseDetails finExpenseDetails) {
		return this.finExpenseDetailsDAO.saveFinExpenseDetails(finExpenseDetails);
	}

	@Override
	public long saveFinExpenseMovements(FinExpenseMovements expense) {
		FinanceMain fm = expense.getFinanceMain();

		Long accountingID = AccountingEngine.getAccountSetID(fm, AccountingEvent.EXPENSE,
				FinanceConstants.MODULEID_FINTYPE);
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.EXPENSE);

		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setFinID(fm.getFinID());
		aeEvent.setFinReference(fm.getFinReference());
		aeEvent.setEntityCode(fm.getEntityCode());
		aeEvent.setFinType(fm.getFinType());

		if (accountingID != null && accountingID > 0) {
			aeEvent.getAcSetIDList().add(accountingID);
		}

		aeEvent.setValueDate(SysParamUtil.getAppDate());

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
			aeEvent.setAeAmountCodes(amountCodes);
		}

		amountCodes.setFinType(fm.getFinType());

		if (AccountingExtension.IND_AS_ACCOUNTING_REQ) {

			Map<String, Object> dataMap = aeEvent.getDataMap();
			dataMap.putAll(amountCodes.getDeclaredFieldValues());

			for (ExpenseType feeType : expenseTypeService.getExpenseTypes()) {
				dataMap.put(feeType.getExpenseTypeCode() + "_AMZ_N", BigDecimal.ZERO);
			}

			dataMap.put(expense.getExpenseTypeCode() + "_AMZ_N", expense.getTransactionAmount());

			postingsPreparationUtil.postAccounting(aeEvent);

			if (CollectionUtils.isEmpty(aeEvent.getReturnDataSet())) {
				throw new AppException("Accounting configuration is invalid for the event :" + AccountingEvent.EXPENSE);
			}

			long linkedTranId = aeEvent.getLinkedTranId();
			expense.setLinkedTranId(linkedTranId);

			if ("O".equals(expense.getTransactionType())) {
				List<FinExpenseMovements> list = finExpenseMovementsDAO.getFinExpenseMovements(fm.getFinID(),
						expense.getExpenseTypeID());

				for (FinExpenseMovements item : list) {
					long revLinkedTranId = postingsPreparationUtil.reversalByLinkedTranID(item.getLinkedTranId());

					finExpenseMovementsDAO.updateRevLinkedTranID(item.getFinExpenseMovemntId(), revLinkedTranId);
				}
			}
		}

		long expensId = finExpenseMovementsDAO.saveFinExpenseMovements(expense);

		return expensId;
	}

	@Override
	public long getFinExpenseIdByExpType(String expTypeCode) {
		return this.expenseTypeService.getFinExpenseIdByExpType(expTypeCode, "");
	}

	@Override
	public FinExpenseDetails getFinExpenseDetailsByReference(String finReference, long expenseTypeId) {
		return this.finExpenseDetailsDAO.getFinExpenseDetailsByReference(finReference, expenseTypeId);
	}

	@Override
	public FinanceMain getFinanceMain(String finReference) {
		return this.financeMainDAO.getFinanceMain(finReference, TableType.MAIN_TAB);
	}

	@Override
	public int getFinTypeCount(String finType) {
		return this.financeTypeDAO.getFinTypeCount(finType, "");
	}

	@Override
	public Long getActiveFinID(String finReference) {
		return this.financeMainDAO.getActiveFinID(finReference, TableType.MAIN_TAB);
	}

	@Override
	public void update(FinExpenseDetails finExpenseDetails) {
		logger.debug(Literal.ENTERING);
		this.finExpenseDetailsDAO.update(finExpenseDetails);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateRecordCounts(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);
		this.uploadHeaderDAO.updateRecordCounts(uploadHeader);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinExpenseDetails> getFinExpenseDetailById(String finReference) {
		return this.finExpenseDetailsDAO.getFinExpenseDetailsById(finReference);
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovementById(long finID, long finExpenseId) {
		return this.finExpenseMovementsDAO.getFinExpenseMovementById(finID, finExpenseId);
	}

	@Override
	public FinTypeExpense getFinExpensesByFinType(String finType, long expenseTypeId) {
		return finTypeExpenseService.getFinExpensesByFinType(finType, expenseTypeId);
	}

	@Override
	public AuditHeader doApproveFinTypeExpense(AuditHeader auditHeader) {

		return finTypeExpenseService.doApprove(auditHeader);
	}

	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode) {

		return feeTypeService.getFinFeeTypeIdByFeeType(feeTypeCode);
	}

	@Override
	public void saveFeeUploadDetails(List<UploadTaxPercent> uploadDetailsList) {

		this.uploadTaxPercentDAO.saveUploadDetails(uploadDetailsList);
	}

	@Override
	public void saveExpenseUploadDetails(List<UploadFinTypeExpense> uploadDetailsList) {
		this.uploadFinTypeExpenseDAO.saveUploadDetails(uploadDetailsList);

	}

	@Override
	public List<UploadTaxPercent> getSuccesFailedCountForFactor(long uploadId) {
		return this.uploadTaxPercentDAO.getSuccesFailedCount(uploadId);
	}

	@Override
	public List<UploadFinTypeExpense> getSuccesFailedCountExpense(long uploadId) {

		return this.uploadFinTypeExpenseDAO.getSuccesFailedCount(uploadId);
	}

	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {

		finFeeDetailService.updateTaxPercent(taxPercent);
	}

	@Override
	public void updateRecord(UploadHeader uploadHeader) {
		this.uploadHeaderDAO.updateRecord(uploadHeader);

	}

	public void setUploadHeaderDAO(UploadHeaderDAO uploadHeaderDAO) {
		this.uploadHeaderDAO = uploadHeaderDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setUploadFinExpensesDAO(UploadFinExpensesDAO uploadFinExpensesDAO) {
		this.uploadFinExpensesDAO = uploadFinExpensesDAO;
	}

	public void setFinExpenseDetailsDAO(FinExpenseDetailsDAO finExpenseDetailsDAO) {
		this.finExpenseDetailsDAO = finExpenseDetailsDAO;
	}

	public void setExpenseTypeService(ExpenseTypeService expenseTypeService) {
		this.expenseTypeService = expenseTypeService;
	}

	public void setFinExpenseMovementsDAO(FinExpenseMovementsDAO finExpenseMovementsDAO) {
		this.finExpenseMovementsDAO = finExpenseMovementsDAO;
	}

	public FinTypeExpenseService getFinTypeExpenseService() {
		return finTypeExpenseService;
	}

	public void setFinTypeExpenseService(FinTypeExpenseService finTypeExpenseService) {
		this.finTypeExpenseService = finTypeExpenseService;
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public UploadTaxPercentDAO getUploadTaxPercentDAO() {
		return uploadTaxPercentDAO;
	}

	public void setUploadTaxPercentDAO(UploadTaxPercentDAO uploadTaxPercentDAO) {
		this.uploadTaxPercentDAO = uploadTaxPercentDAO;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public UploadFinTypeExpenseDAO getUploadFinTypeExpenseDAO() {
		return uploadFinTypeExpenseDAO;
	}

	public void setUploadFinTypeExpenseDAO(UploadFinTypeExpenseDAO uploadFinTypeExpenseDAO) {
		this.uploadFinTypeExpenseDAO = uploadFinTypeExpenseDAO;
	}

	@Override
	public void updateFileDownload(long uploadId, boolean fileDownload, String type) {
		this.uploadHeaderDAO.updateFileDownload(uploadId, fileDownload, type);
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		this.uploadHeaderDAO.delete(uploadHeader, TableType.MAIN_TAB);

		// Child
		this.miscPostingUploadService.deleteByUploadId(uploadHeader.getUploadId());
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		UploadHeader uploadHeader = new UploadHeader();
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		BeanUtils.copyProperties((UploadHeader) auditHeader.getAuditDetail().getModelData(), uploadHeader);

		if (uploadHeader.getTotalRecords() != uploadHeader.getFailedCount()) {
			this.uploadHeaderDAO.delete(uploadHeader, TableType.TEMP_TAB);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(uploadHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(this.uploadHeaderDAO.getUploadHeaderById(uploadHeader.getUploadId(), ""));
		}

		if (uploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			// List
			@SuppressWarnings("unused")
			List<AuditDetail> deleteAudit = deleteChilds(uploadHeader, "", tranType);
			// auditDetails.addAll(deleteAudit);//Skipping audit
			this.uploadHeaderDAO.delete(uploadHeader, TableType.MAIN_TAB);
		} else {
			uploadHeader.setRoleCode("");
			uploadHeader.setNextRoleCode("");
			uploadHeader.setTaskId("");
			uploadHeader.setNextTaskId("");
			uploadHeader.setWorkflowId(0);
			uploadHeader.setApprovedDate(SysParamUtil.getAppDate());
			uploadHeader.setApproverId(auditHeader.getAuditUsrId());

			if (PennantConstants.RECORD_TYPE_NEW.equals(uploadHeader.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				uploadHeader.setRecordType("");
				this.uploadHeaderDAO.save(uploadHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				uploadHeader.setRecordType("");
				this.uploadHeaderDAO.update(uploadHeader, TableType.MAIN_TAB);
			}
			// Manual Advise Upload
			if (CollectionUtils.isNotEmpty(uploadHeader.getUploadManualAdvises())) {
				List<AuditDetail> adviseUpload = uploadHeader.getAuditDetailMap().get("AdviseUploads");
				adviseUpload = this.uploadManualAdviseService.processAdviseUploadsDetails(adviseUpload,
						uploadHeader.getUploadId(), TableType.MAIN_TAB.getSuffix());
				this.uploadHeaderDAO.delete(uploadHeader, TableType.TEMP_TAB);
				auditDetails.addAll(adviseUpload);
			}

			// updating
			if (CollectionUtils.isNotEmpty(uploadHeader.getMiscPostingUploads())) {
				miscPostingUploadService.updateList(uploadHeader.getMiscPostingUploads());

				// adding posting detail in JVPostings table
				miscPostingUploadService.insertInJVPosting(uploadHeader);
			}

		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new UploadHeader(), uploadHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				uploadHeader.getBefImage(), uploadHeader));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(uploadHeader);

		// List
		getAuditHeaderDAO().addAudit(auditHeader);

		// List
		auditHeader = prepareChildsAudit(auditHeader, "doApprove");
		getAuditHeaderDAO().addAudit(auditHeader);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public void validateAssignmentScreenLevel(AssignmentUpload assignmentUpload, String entityCode) {
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		this.uploadHeaderDAO.delete(uploadHeader, TableType.TEMP_TAB);
		this.miscPostingUploadService.deleteByUploadId(uploadHeader.getUploadId());
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<>();
		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType1 = TableType.MAIN_TAB;
		if (uploadHeader.isWorkflow()) {
			tableType1 = TableType.TEMP_TAB;
		}

		if (uploadHeader.isNewRecord()) {
			uploadHeader.setUploadId(this.uploadHeaderDAO.save(uploadHeader, tableType1));
			// MiscPostingUploads
			if (CollectionUtils.isNotEmpty(uploadHeader.getMiscPostingUploads())) {
				this.miscPostingUploadService.save(uploadHeader.getMiscPostingUploads(), uploadHeader.getUploadId());
			}
			auditHeader.getAuditDetail().setModelData(uploadHeader);
			auditHeader.setAuditReference(String.valueOf(uploadHeader.getUploadId()));
		} else {
			this.uploadHeaderDAO.update(uploadHeader, tableType1);
			if (CollectionUtils.isNotEmpty(uploadHeader.getMiscPostingUploads())) {
				this.miscPostingUploadService.updateList(uploadHeader.getMiscPostingUploads());
			}
		}
		// Manual Advise Upload
		if (CollectionUtils.isNotEmpty(uploadHeader.getUploadManualAdvises())) {
			List<AuditDetail> adviseUpload = uploadHeader.getAuditDetailMap().get("AdviseUploads");
			adviseUpload = this.uploadManualAdviseService.processAdviseUploadsDetails(adviseUpload,
					uploadHeader.getUploadId(), TableType.TEMP_TAB.getSuffix());
			auditDetails.addAll(adviseUpload);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private void validateEntityCode(AuditHeader auditHeader) {
		logger.info("Validating Upload Details...");

		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();

		if (!uploadHeader.isValidationReq()) {
			return;
		}

		Set<String> finReferences = new HashSet<>();
		String entityCode = uploadHeader.getEntityCode();

		List<AssignmentUpload> assignmentUploads = uploadHeader.getAssignmentUploads();
		List<RefundUpload> refundUploads = uploadHeader.getRefundUploads();
		List<MiscPostingUpload> miscPostingUploads = uploadHeader.getMiscPostingUploads();
		List<UploadManualAdvise> uploadManualAdvises = uploadHeader.getUploadManualAdvises();

		assignmentUploads.forEach(l1 -> finReferences.add(l1.getFinReference()));
		refundUploads.forEach(l1 -> finReferences.add(l1.getFinReference()));
		miscPostingUploads.forEach(l1 -> finReferences.add(l1.getReference()));
		uploadManualAdvises.forEach(l1 -> finReferences.add(l1.getFinReference()));

		for (String finReference : finReferences) {
			Long finID = financeMainDAO.getFinID(finReference, entityCode, TableType.MAIN_TAB);
			if (finID == null) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("MU0001", "", valueParm)));
				break;
			}
		}

	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		List<AuditDetail> auditDetails = new ArrayList<>();

		// List
		auditHeader = prepareChildsAudit(auditHeader, method);
		auditHeader.setErrorList(validateChilds(auditHeader, auditHeader.getUsrLanguage(), method));
		validateEntityCode(auditHeader);

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getUploadHeaderDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		UploadHeader uploadHeader = (UploadHeader) auditDetail.getModelData();

		// Check the unique keys.
		if (uploadHeader.isNewRecord() && this.uploadHeaderDAO.isDuplicateKey(uploadHeader.getUploadId(),
				uploadHeader.getFileName(), uploadHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_MiscPostingUploadDialog_Filename.value") + ": "
					+ uploadHeader.getFileName();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", parameters)));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
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
		// Map<String, List<AuditDetail>> auditDetailMap = new
		// HashMap<String, List<AuditDetail>>();
		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		// String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (uploadHeader.isWorkflow()) {
				// auditTranType = PennantConstants.TRAN_WF;
			}
		}

		auditHeader.getAuditDetail().setModelData(uploadHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public List<AuditDetail> deleteChilds(UploadHeader uploadHeader, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(uploadHeader.getMiscPostingUploads())) {
			// this.miscPostingUploadService.deleteByUploadId(uploadHeader.getUploadId());
		}

		// Manual Advise Uploads
		if (CollectionUtils.isNotEmpty(uploadHeader.getUploadManualAdvises())) {
			auditDetails.addAll(this.uploadManualAdviseService.delete(uploadHeader.getUploadManualAdvises(), tableType,
					auditTranType, uploadHeader.getUploadId()));
		}

		logger.debug(Literal.LEAVING);

		return auditDetails;
	}

	// =================================== List maintain
	private AuditHeader prepareChildsAudit(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		UploadHeader uploadHeader = (UploadHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (uploadHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Manual Advise Upload
		if (CollectionUtils.isNotEmpty(uploadHeader.getUploadManualAdvises())) {
			for (UploadManualAdvise adviseUpload : uploadHeader.getUploadManualAdvises()) {
				adviseUpload.setWorkflowId(uploadHeader.getWorkflowId());
				adviseUpload.setRecordStatus(uploadHeader.getRecordStatus());
				adviseUpload.setUserDetails(uploadHeader.getUserDetails());
				adviseUpload.setLastMntOn(uploadHeader.getLastMntOn());
				adviseUpload.setLastMntBy(uploadHeader.getLastMntBy());
				adviseUpload.setRoleCode(uploadHeader.getRoleCode());
				adviseUpload.setNextRoleCode(uploadHeader.getNextRoleCode());
				adviseUpload.setTaskId(uploadHeader.getTaskId());
				adviseUpload.setNextTaskId(uploadHeader.getNextTaskId());
			}

			auditDetailMap.put("AdviseUploads", this.uploadManualAdviseService
					.setAdviseUploadsAuditData(uploadHeader.getUploadManualAdvises(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("AdviseUploads"));
		}

		uploadHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(uploadHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");

		return auditHeader;
	}

	private List<ErrorDetail> validateChilds(AuditHeader auditHeader, String usrLanguage, String method) {
		logger.debug("Entering");

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();

		// Manual Advise Uploads
		List<ErrorDetail> adviseErrorDetails = this.uploadManualAdviseService.validateAdviseUploads(auditHeader,
				usrLanguage, method);
		if (CollectionUtils.isNotEmpty(adviseErrorDetails)) {
			errorDetails.addAll(adviseErrorDetails);
		}

		// If we want any other child you have to add here

		return errorDetails;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public MiscPostingUploadService getMiscPostingUploadService() {
		return miscPostingUploadService;
	}

	public void setMiscPostingUploadService(MiscPostingUploadService miscPostingUploadService) {
		this.miscPostingUploadService = miscPostingUploadService;
	}

	@Override
	public List<MiscPostingUpload> getMiscPostingUploadListByUploadId(long uploadId) {
		return miscPostingUploadService.getMiscPostingUploadsByUploadId(uploadId);
	}

	@Override
	public UploadHeader getApprovedUploadHeaderById(long academicID) {
		return null;
	}

	@Override
	public UploadHeader getUploadHeader() {
		return uploadHeaderDAO.getUploadHeader();
	}

	@Override
	public FeeType getApprovedFeeTypeByFeeCode(String finTypeCode) {
		return this.feeTypeDAO.getApprovedFeeTypeByFeeCode(finTypeCode);
	}

	@Override
	public List<String> getFinEventByFinRef(String finReference, String type) {
		return finServiceInstructionDAO.getFinEventByFinRef(finReference, type);
	}

	public FeeTypeDAO getFeeTypeDAO() {
		return feeTypeDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public UploadManualAdviseService getUploadManualAdviseService() {
		return uploadManualAdviseService;
	}

	public void setUploadManualAdviseService(UploadManualAdviseService uploadManualAdviseService) {
		this.uploadManualAdviseService = uploadManualAdviseService;
	}

	@Override
	public List<UploadManualAdvise> getManualAdviseListByUploadId(long uploadId) {
		return uploadManualAdviseService.getManualAdviseListByUploadId(uploadId);
	}

	@Override
	public boolean isFileDownload(long uploadID, String tableType) {
		return uploadHeaderDAO.isFileDownload(uploadID, tableType);
	}

	@Override
	public List<UploadReceipt> getSuccesFailedReceiptCount(long uploadId) {
		return uploadHeaderDAO.getSuccesFailedReceiptCount(uploadId);
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}