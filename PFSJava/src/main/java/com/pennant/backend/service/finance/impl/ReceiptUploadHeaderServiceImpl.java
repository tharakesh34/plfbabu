
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
 * FileName    		:  UploadHeaderServiceImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class ReceiptUploadHeaderServiceImpl extends GenericService<ReceiptUploadHeader>
		implements ReceiptUploadHeaderService {
	private static final Logger logger = LogManager.getLogger(ReceiptUploadHeaderServiceImpl.class);

	private ReceiptUploadHeaderDAO receiptUploadHeaderDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private UploadAllocationDetailDAO uploadAllocationDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	public ReceiptUploadHeaderServiceImpl() {
		super();
	}

	public ReceiptUploadHeaderDAO getReceiptUploadHeaderDAO() {
		return receiptUploadHeaderDAO;
	}

	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
	}

	public boolean isFileNameExist(String fileName) {
		return this.receiptUploadHeaderDAO.isFileNameExist(fileName);
	}

	@Override
	public ReceiptUploadHeader getUploadHeaderById(long id, boolean getsuccessRecords) {
		logger.debug(Literal.ENTERING);

		ReceiptUploadHeader receiptUploadHeader = getReceiptUploadHeaderDAO().getReceiptHeaderById(id, "_view");

		List<ReceiptUploadDetail> receiptUploadDetailList = new ArrayList<>();

		if (getsuccessRecords) {
			receiptUploadDetailList = getReceiptUploadDetailDAO()
					.getUploadReceiptDetails(receiptUploadHeader.getUploadHeaderId(), getsuccessRecords);
		} else {
			receiptUploadDetailList = getReceiptUploadDetailDAO()
					.getUploadReceiptDetails(receiptUploadHeader.getUploadHeaderId(), getsuccessRecords);
		}

		receiptUploadHeader.setReceiptUploadList(receiptUploadDetailList);

		logger.debug(Literal.LEAVING);
		return receiptUploadHeader;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
		BeanUtils.copyProperties((ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData(),
				receiptUploadHeader);

		getReceiptUploadHeaderDAO().delete(receiptUploadHeader, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(receiptUploadHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					getReceiptUploadHeaderDAO().getReceiptHeaderById(receiptUploadHeader.getUploadHeaderId(), ""));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(receiptUploadHeader.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			getReceiptUploadHeaderDAO().delete(receiptUploadHeader, TableType.MAIN_TAB);
		} else {
			receiptUploadHeader.setRoleCode("");
			receiptUploadHeader.setNextRoleCode("");
			receiptUploadHeader.setTaskId("");
			receiptUploadHeader.setNextTaskId("");
			receiptUploadHeader.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(receiptUploadHeader.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				receiptUploadHeader.setRecordType("");
				getReceiptUploadHeaderDAO().save(receiptUploadHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				receiptUploadHeader.setRecordType("");
				getReceiptUploadHeaderDAO().update(receiptUploadHeader, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(receiptUploadHeader);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		ReceiptUploadHeader receiptHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getReceiptUploadHeaderDAO().delete(receiptHeader, TableType.TEMP_TAB);

		List<Long> listReceiptDetailsid = getReceiptUploadDetailDAO()
				.getListofReceiptUploadDetails(receiptHeader.getUploadHeaderId());

		for (Long id : listReceiptDetailsid) {
			getUploadAllocationDetailDAO().delete(id);
		}
		getReceiptUploadDetailDAO().delete(receiptHeader.getUploadHeaderId());
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		ReceiptUploadHeader receiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (receiptUploadHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (receiptUploadHeader.isNewRecord()) {
			receiptUploadHeader.setUploadHeaderId(getReceiptUploadHeaderDAO().save(receiptUploadHeader, tableType));
			auditHeader.getAuditDetail().setModelData(receiptUploadHeader);
			auditHeader.setAuditReference(String.valueOf(receiptUploadHeader.getUploadHeaderId()));

			if (receiptUploadHeader.getReceiptUploadList() != null
					&& !receiptUploadHeader.getReceiptUploadList().isEmpty()) {
				for (ReceiptUploadDetail receiptUploadDetail : receiptUploadHeader.getReceiptUploadList()) {
					receiptUploadDetail.setUploadheaderId(receiptUploadHeader.getUploadHeaderId());
					long detailId = this.receiptUploadDetailDAO.save(receiptUploadDetail);

					if (receiptUploadDetail.getListAllocationDetails() != null
							&& !receiptUploadDetail.getListAllocationDetails().isEmpty()) {
						this.uploadAllocationDetailDAO.save(receiptUploadDetail.getListAllocationDetails(), detailId,
								receiptUploadDetail.getRootId());
					}
				}
			}

		} else {
			getReceiptUploadHeaderDAO().update(receiptUploadHeader, tableType);
		}
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		ReceiptUploadHeader receiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		getReceiptUploadHeaderDAO().delete(receiptUploadHeader, TableType.MAIN_TAB);

		// child
		getReceiptUploadDetailDAO().delete(receiptUploadHeader.getUploadHeaderId());
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug("Entering");

		// Get the model object.
		ReceiptUploadHeader receiptUploadHeader = (ReceiptUploadHeader) auditDetail.getModelData();
		String code = receiptUploadHeader.getFileName();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AddrTypeCode") + ": " + code;

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug("Leaving");
		return auditDetail;
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ReceiptUploadDetailDAO getReceiptUploadDetailDAO() {
		return receiptUploadDetailDAO;
	}

	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Override
	public void updateStatus(ReceiptUploadDetail receiptUploadDetail) {
		getReceiptUploadDetailDAO().updateStatus(receiptUploadDetail);
	}

	@Override
	public void uploadHeaderStatusCnt(long receiptUploadId, int sucessCount, int failedCount) {
		getReceiptUploadHeaderDAO().uploadHeaderStatusCnt(receiptUploadId, sucessCount, failedCount);
	}

	public UploadAllocationDetailDAO getUploadAllocationDetailDAO() {
		return uploadAllocationDetailDAO;
	}

	public void setUploadAllocationDetailDAO(UploadAllocationDetailDAO uploadAllocationDetailDAO) {
		this.uploadAllocationDetailDAO = uploadAllocationDetailDAO;
	}

	@Override
	public boolean isFileDownloaded(long id, int receiptDownloaded) {
		return this.receiptUploadHeaderDAO.isFileDownlaoded(id, receiptDownloaded);
	}

	@Override
	public void updateUploadProgress(long id, int receiptDownloaded) {
		this.receiptUploadHeaderDAO.updateUploadProgress(id, receiptDownloaded);
	}

	@Override
	public void updateRejectStatusById(String id, String errorMsg) {
		this.receiptUploadDetailDAO.updateRejectStatusById(id, errorMsg);

	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(String reference, String referenceCode, String type) {
		return getManualAdviseDAO().getManualAdviseByRef(reference, referenceCode, type);
	}

	@Override
	public long saveReceiptResponseFileHeader(String procName) {
		return this.receiptResponseDetailDAO.saveReceiptResponseFileHeader(procName);
	}

	@Override
	public List<ReceiptUploadDetail> getReceiptResponseDetails() {
		return this.receiptResponseDetailDAO.getReceiptResponseDetails();
	}

	public ReceiptResponseDetailDAO getReceiptResponseDetailDAO() {
		return receiptResponseDetailDAO;
	}

	public void setReceiptResponseDetailDAO(ReceiptResponseDetailDAO receiptResponseDetailDAO) {
		this.receiptResponseDetailDAO = receiptResponseDetailDAO;
	}

	@Override
	public List<UploadAlloctionDetail> getReceiptResponseAllocationDetails(String rootId) {
		return this.receiptResponseDetailDAO.getReceiptResponseAllocationDetails(rootId);
	}

	@Override
	public void updateReceiptResponseFileHeader(long batchId, int recordCount, int sCount, int fCount, String remarks) {
		this.receiptResponseDetailDAO.updateReceiptResponseFileHeader(batchId, recordCount, sCount, fCount, remarks);
	}

	@Override
	public void updateReceiptResponseDetails(ReceiptUploadDetail receiptresponseDetail, long jobid) {
		this.receiptResponseDetailDAO.updateReceiptResponseDetails(receiptresponseDetail, jobid);

	}

	@Override
	public void updatePickBatchId(long jobid) {
		this.receiptResponseDetailDAO.updatePickBatchId(jobid);

	}

	@Override
	public String getLoanReferenc(String finReference, String fileName) {
		return getReceiptUploadDetailDAO().getLoanReferenc(finReference, fileName);
	}

	@Override
	public int getFinanceCountById(String reference, String string, boolean b) {
		return getFinanceMainDAO().getFinanceCountById(reference, string, b);
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public boolean isFinReferenceExitsWithEntity(String reference, String type, String validatedValue) {
		return getFinanceMainDAO().isFinReferenceExitsWithEntity(reference, type, validatedValue);
	}

	@Override
	public boolean isReceiptDetailsExits(String reference, String paytypeCheque, String chequeNo, String favourNumber,
			String type) {
		return getFinReceiptHeaderDAO().isReceiptDetailsExits(reference, paytypeCheque, chequeNo, favourNumber, type);
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Override
	public boolean isFinReferenceExists(String reference, String type, boolean isWIF) {
		return getFinanceMainDAO().isFinReferenceExists(reference, type, isWIF);
	}

	@Override
	public int[] getHeaderStatusCnt(long receiptUploadId) {

		List<Long> receiptIdValues = getReceiptUploadHeaderDAO().getHeaderStatusCnt(receiptUploadId);
		int sucessCount = 0;
		int failCount = 0;
		for (Long receiptId : receiptIdValues) {
			if (receiptId == null) {
				failCount++;
			} else {
				sucessCount++;
			}
		}
		int[] val = new int[2];
		val[0] = sucessCount;
		val[1] = failCount;
		return val;
	}

	@Override
	public boolean isChequeExist(String reference, String paytypeCheque, String bankCode, String favourNumber,
			String type) {
		return getFinReceiptHeaderDAO().isChequeExists(reference, paytypeCheque, bankCode, favourNumber, type);
	}

	@Override
	public boolean isOnlineExist(String reference, String subReceiptMode, String tranRef, String type) {
		return getFinReceiptHeaderDAO().isOnlineExists(reference, subReceiptMode, tranRef, type);
	}

}