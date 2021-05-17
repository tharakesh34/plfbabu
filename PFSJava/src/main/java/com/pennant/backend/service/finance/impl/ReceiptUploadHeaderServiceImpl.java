
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennant.backend.model.receiptupload.ReceiptUploadTracker;
import com.pennant.backend.model.receiptupload.ThreadAllocation;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.upload.ReceiptUploadThreadProcess;

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
	private DataSource dataSource;
	private ReceiptService receiptService;
	private DataFormatter objDefaultFormat = new DataFormatter();

	public ReceiptUploadHeaderServiceImpl() {
		super();
	}

	@Override
	public ReceiptUploadHeader getUploadHeaderById(long id, boolean getsuccessRecords) {
		logger.debug(Literal.ENTERING);

		ReceiptUploadHeader receiptUploadHeader = receiptUploadHeaderDAO.getReceiptHeaderById(id, "_view");

		if (receiptUploadHeader == null) {
			return null;
		}

		receiptUploadHeader.setAttemptNo(receiptUploadHeaderDAO.setHeaderAttempNo(id) + 1);

		List<ReceiptUploadDetail> receiptUploadDetailList = new ArrayList<>();

		if (getsuccessRecords) {
			receiptUploadDetailList = receiptUploadDetailDAO
					.getUploadReceiptDetails(receiptUploadHeader.getUploadHeaderId(), getsuccessRecords);
		} else {
			receiptUploadDetailList = receiptUploadDetailDAO
					.getUploadReceiptDetails(receiptUploadHeader.getUploadHeaderId(), getsuccessRecords);
		}

		receiptUploadHeader.setReceiptUploadList(receiptUploadDetailList);

		logger.debug(Literal.LEAVING);
		return receiptUploadHeader;
	}

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
		BeanUtils.copyProperties((ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData(),
				receiptUploadHeader);

		receiptUploadHeaderDAO.delete(receiptUploadHeader, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(receiptUploadHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					receiptUploadHeaderDAO.getReceiptHeaderById(receiptUploadHeader.getUploadHeaderId(), ""));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(receiptUploadHeader.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			receiptUploadHeaderDAO.delete(receiptUploadHeader, TableType.MAIN_TAB);
		} else {
			receiptUploadHeader.setRoleCode("");
			receiptUploadHeader.setNextRoleCode("");
			receiptUploadHeader.setTaskId("");
			receiptUploadHeader.setNextTaskId("");
			receiptUploadHeader.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(receiptUploadHeader.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				receiptUploadHeader.setRecordType("");
				receiptUploadHeaderDAO.save(receiptUploadHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				receiptUploadHeader.setRecordType("");
				receiptUploadHeaderDAO.update(receiptUploadHeader, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(receiptUploadHeader);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		ReceiptUploadHeader receiptHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		receiptUploadHeaderDAO.delete(receiptHeader, TableType.TEMP_TAB);

		List<Long> listReceiptDetailsid = receiptUploadDetailDAO
				.getListofReceiptUploadDetails(receiptHeader.getUploadHeaderId());

		for (Long id : listReceiptDetailsid) {
			uploadAllocationDetailDAO.delete(id);
		}
		receiptUploadDetailDAO.delete(receiptHeader.getUploadHeaderId());
		auditHeaderDAO.addAudit(auditHeader);
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

		ReceiptUploadHeader receiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		TableType tableType = TableType.MAIN_TAB;

		if (receiptUploadHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (receiptUploadHeader.isNewRecord()) {
			receiptUploadHeader.setUploadHeaderId(receiptUploadHeaderDAO.save(receiptUploadHeader, tableType));
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
			receiptUploadHeaderDAO.update(receiptUploadHeader, tableType);
		}
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		ReceiptUploadHeader receiptUploadHeader = (ReceiptUploadHeader) auditHeader.getAuditDetail().getModelData();
		receiptUploadHeaderDAO.delete(receiptUploadHeader, TableType.TEMP_TAB);

		List<Long> listReceiptDetailsid = receiptUploadDetailDAO
				.getListofReceiptUploadDetails(receiptUploadHeader.getUploadHeaderId());

		for (Long id : listReceiptDetailsid) {
			uploadAllocationDetailDAO.delete(id);
		}

		receiptUploadDetailDAO.delete(receiptUploadHeader.getUploadHeaderId());
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		ReceiptUploadHeader receiptUploadHeader = (ReceiptUploadHeader) auditDetail.getModelData();
		String code = receiptUploadHeader.getFileName();
		String[] parameters = new String[1];
		parameters[0] = PennantJavaUtil.getLabel("label_AddrTypeCode") + ": " + code;

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Override
	public void updateStatus(ReceiptUploadDetail receiptUploadDetail) {
		receiptUploadDetailDAO.updateStatus(receiptUploadDetail);
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

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Override
	public List<ManualAdvise> getManualAdviseByRef(String reference, String referenceCode, String type) {
		return manualAdviseDAO.getManualAdviseByRef(reference, referenceCode, type);
	}

	@Override
	public long saveReceiptResponseFileHeader(String procName) {
		return this.receiptResponseDetailDAO.saveReceiptResponseFileHeader(procName);
	}

	@Override
	public List<ReceiptUploadDetail> getReceiptResponseDetails() {
		return this.receiptResponseDetailDAO.getReceiptResponseDetails();
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
		return receiptUploadDetailDAO.getLoanReferenc(finReference, fileName);
	}

	@Override
	public int getFinanceCountById(String reference, String string, boolean b) {
		return this.financeMainDAO.getFinanceCountById(reference, string, b);
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public boolean isFinRefExitsWithEntity(String reference, String type, String entity) {
		return this.financeMainDAO.isFinReferenceExitsWithEntity(reference, type, entity);
	}

	@Override
	public boolean isReceiptDetailsExits(String reference, String paytypeCheque, String chequeNo, String favourNumber,
			String type) {
		return this.finReceiptHeaderDAO.isReceiptDetailsExits(reference, paytypeCheque, chequeNo, favourNumber, type);
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Override
	public boolean isFinReferenceExists(String reference, String type, boolean isWIF) {
		return this.financeMainDAO.isFinReferenceExists(reference, type, isWIF);
	}

	@Override
	public int[] getHeaderStatusCnt(long receiptUploadId) {

		List<Long> receiptIds = receiptUploadHeaderDAO.getHeaderStatusCnt(receiptUploadId);
		int sucessCount = 0;
		int failCount = 0;
		for (Long receiptId : receiptIds) {
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
		return this.finReceiptHeaderDAO.isChequeExists(reference, paytypeCheque, bankCode, favourNumber, type);
	}

	@Override
	public boolean isOnlineExist(String reference, String subReceiptMode, String tranRef, String type) {
		return this.finReceiptHeaderDAO.isOnlineExists(reference, subReceiptMode, tranRef, type);
	}

	@Override
	public void executeThreads(List<Long> headerIdList, LoggedInUser loggedInUser, int startThread, int endThread,
			Map<Long, ReceiptUploadLog> attemptMap) {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("Receipt_Upload_Approver");
		CountDownLatch latch = new CountDownLatch((int) (endThread - startThread));

		logger.info("Thread Execution started for pool {}", endThread - startThread);

		IntStream.range(startThread, endThread).forEach(e -> {
			ReceiptUploadThreadProcess threadProcess = new ReceiptUploadThreadProcess();
			threadProcess.setDataSource(dataSource);
			threadProcess.setReceiptUploadDetailDAO(receiptUploadDetailDAO);
			threadProcess.setUploadAllocationDetailDAO(uploadAllocationDetailDAO);
			threadProcess.setReceiptService(receiptService);
			threadProcess.setLoggedInUser(loggedInUser);
			threadProcess.setHeaderIdList(headerIdList);
			threadProcess.setThreadId(e + 1);
			threadProcess.setLatch(latch);
			threadProcess.setAttemptMap(attemptMap);
			taskExecutor.execute(threadProcess);
		});

		try {
			latch.await();

			logger.info("Thread Execution for the pool thread{} to thread{} ended", startThread + 1, endThread + 1);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int updateThread(List<Long> headerIdList) {
		List<ThreadAllocation> threadAllocations = receiptUploadDetailDAO.getFinRefWithCount(headerIdList);

		if (threadAllocations.isEmpty()) {
			logger.warn("No ReceipUploadtDetails Found with HeaderId's{}", headerIdList);
			return 0;
		}

		int threadNo = 1;
		int batchSize = 0;

		int maxBatchSize = SysParamUtil.getValueAsInt(SMTParameterConstants.RECEIPT_UPLOAD_THREAD_BATCH_SIZE);
		for (ThreadAllocation thrdAllctn : threadAllocations) {
			int finCount = thrdAllctn.getCount();

			if (finCount + batchSize > maxBatchSize) {
				batchSize = 0;
				threadNo++;
			}

			thrdAllctn.setThreadId(threadNo);
			batchSize = batchSize + finCount;
		}

		logger.info("{} Threads Created with batchSize{} for the Header Ids{}", threadNo, maxBatchSize, headerIdList);

		logger.info("Updating ThreadAllocations for the Header Ids{}...", headerIdList);
		int count = receiptUploadDetailDAO.updateThreadAllocationByFinRef(threadAllocations, headerIdList);
		logger.info("{} Records Updated with ThreadId for the Header Ids{}", count, headerIdList);
		return threadNo;
	}

	@Override
	public Map<Long, ReceiptUploadLog> updateProgress(List<ReceiptUploadHeader> uploadHeaderList) {
		long count = 0;
		List<Long> headerIdList = uploadHeaderList.stream().map(ReceiptUploadHeader::getId)
				.collect(Collectors.toList());
		try {
			uploadHeaderList.forEach(e -> {
				long Id = e.getId();
				logger.info("Updating status as inprogress for headerId{}", Id);
				receiptUploadHeaderDAO.updateUploadProgress(Id, ReceiptUploadConstants.RECEIPT_INPROCESS);
			});

			logger.info("Updating status for ReceiptUploadDetails as Inprogress for the headerIds{}", headerIdList);
			count = receiptUploadDetailDAO.updateStatus(headerIdList);
			logger.info("{} ReceiptUploadDetails updated as Inprogress for the headerIds{}", count, headerIdList);

			logger.info("Updating attempt for the headerIds{}", headerIdList);
			return receiptUploadHeaderDAO.createAttempLog(uploadHeaderList);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void initiateImport(ReceiptUploadHeader ruh, Workbook workbook, Map<Long, Integer> statusMap,
			ExcelFileImport fileImport) {
		List<ReceiptUploadDetail> rudList = new ArrayList<>();
		List<UploadAlloctionDetail> uadList = new ArrayList<>();

		ReceiptUploadTracker rut = new ReceiptUploadTracker();
		rut.setHeaderId(ruh.getId());
		rut.setImportStatusMap(statusMap);
		rut.setTotalProcesses(5);
		try {
			validateFileData(workbook, ruh, rudList, rut);

			prepareAllocations(workbook, uadList, rut);

			linkReceiptAllocations(rudList, uadList, rut);

			ruh.setBefImage(ruh);

			validateReceipt(ruh, rudList, rut);

			ruh.setReceiptUploadList(rudList);

			try {
				fileImport.backUpFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			saveRecord(ruh, rut);
		} catch (Exception e) {
			updateImportFail(ruh);
			logger.error(Literal.EXCEPTION, e);
		}
		statusMap.remove(ruh.getId());
	}

	private void updateImportFail(ReceiptUploadHeader ruh) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = transactionManager.getTransaction(txDef);
		updateUploadProgress(ruh.getId(), ReceiptUploadConstants.RECEIPT_IMPORTFAILED);
		transactionManager.commit(transactionStatus);
	}

	private void saveRecord(ReceiptUploadHeader ruh, ReceiptUploadTracker rut) {
		int sucess = 0;
		int failed = 0;
		List<ReceiptUploadDetail> receiptUploadList = ruh.getReceiptUploadList();
		rut.setBatchSize(receiptUploadList.size());

		logger.info("Saving ReceiptUploadDetails and UploadAllocationDetailsd...");
		for (ReceiptUploadDetail rud : receiptUploadList) {

			if (ReceiptDetailStatus.SUCCESS.getValue() == rud.getProcessingStatus()) {
				sucess++;
			}
			if (ReceiptDetailStatus.FAILED.getValue() == rud.getProcessingStatus()) {
				failed++;
			}

			rud.setUploadheaderId(ruh.getUploadHeaderId());
			long detailId = this.receiptUploadDetailDAO.save(rud);
			if (rud.getListAllocationDetails() != null && !rud.getListAllocationDetails().isEmpty()) {
				this.uploadAllocationDetailDAO.save(rud.getListAllocationDetails(), detailId, rud.getRootId());
			}

			rut.incrementProgress();
		}

		ruh.setTotalRecords(sucess + failed);
		ruh.setFailedCount(failed);
		ruh.setSuccessCount(sucess);
		ruh.setUploadProgress(ReceiptUploadConstants.RECEIPT_IMPORTED);

		logger.info("Updating ReceiptUploadHeader as Imported...");

		int count = receiptUploadHeaderDAO.update(ruh, TableType.TEMP_TAB);
		if (count == 1) {
			logger.info("ReceiptUploadHeader {} successfully updated with successcount{}, failurecount{} ", ruh.getId(),
					sucess, failed);
		}
	}

	private void validateReceipt(ReceiptUploadHeader ruh, List<ReceiptUploadDetail> rudList, ReceiptUploadTracker rut) {
		logger.info("Receipt validation process started..");

		rut.setBatchSize(rudList.size());

		rudList.parallelStream().forEach(rud -> {
			if (!rud.getErrorDetails().isEmpty()) {
				rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());

				String code = StringUtils.trimToEmpty(rud.getErrorDetails().get(0).getCode());
				String description = StringUtils.trimToEmpty(rud.getErrorDetails().get(0).getError());

				rud.setReason(String.format("%s %s %s", code, "-", description));
				rut.incrementProgress();
				return;
			}

			FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, ruh.getEntityCode());
			fsi.setReqType("Inquiry");
			fsi.setReceiptUpload(true);
			FinanceDetail financeDetail = receiptService.receiptTransaction(fsi, fsi.getReceiptPurpose());

			WSReturnStatus returnStatus = financeDetail.getReturnStatus();
			if (returnStatus != null) {
				rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());

				String code = StringUtils.trimToEmpty(returnStatus.getReturnCode());
				String description = StringUtils.trimToEmpty(returnStatus.getReturnText());

				rud.setReason(String.format("%s %s %s", code, "-", description));
			} else {
				rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
				rud.setReason("");
			}
			rut.incrementProgress();
		});
		logger.info("Receipt validation process completed..");
	}

	private boolean validateFileData(Workbook workbook, ReceiptUploadHeader ruh, List<ReceiptUploadDetail> rudList,
			ReceiptUploadTracker rut) {
		logger.info("Validating File Data and Preparing ReceiptUploadDetails...");

		String fileName = ruh.getFileName();
		long headerId = ruh.getId();

		final Set<String> setTxnKeys = new HashSet<String>();
		final Set<String> setTxnKeysCheque = new HashSet<String>();
		final Set<String> receiptValidList = new HashSet<String>();
		Sheet rchSheet = workbook.getSheetAt(0);
		int rowCount = rchSheet.getLastRowNum();
		String txnKey = "";
		String errorMsg = "";
		rut.setBatchSize(rowCount);
		Date appDate = SysParamUtil.getAppDate();
		boolean dedupCheck = SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTUPLOAD_DEDUPCHECK);

		for (int i = 1; i <= rowCount; i++) {
			Row rchRow = rchSheet.getRow(i);

			if (rchRow == null) {
				continue;
			}

			ReceiptUploadDetail rud = prepareReceiptUploadDetail(rchRow, headerId, appDate);

			if (rud.getFavourNumber() != null && rud.getFavourNumber().length() > 6) {
				errorMsg = "Favour Number more than 6 digits";
				setErrorToRUD(rud, "90405", errorMsg);
			}

			if (!isFinRefExitsWithEntity(rud.getReference(), "", ruh.getEntityCode())) {
				setErrorToRUD(rud, "RU0004", rud.getReference());
			}

			if (dedupCheck) {

				if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_ONLINE)) {
					txnKey = rud.getReference() + "/" + rud.getTransactionRef() + "/" + rud.getSubReceiptMode();
					if (!setTxnKeys.add(txnKey)) {
						errorMsg = "with combination REFERENCE/TRANSACTIONREF/SubReceiptMode:" + txnKey;
						setErrorToRUD(rud, "90273", errorMsg);
					}
				} else if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_DD)) {
					txnKey = rud.getReference() + "/" + rud.getReceiptMode() + "/" + rud.getBankCode() + "/"
							+ rud.getFavourNumber();
					if (!setTxnKeysCheque.add(txnKey)) {
						errorMsg = "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey;
						setErrorToRUD(rud, "90273", errorMsg);
					}
				}
				boolean isRecDtlExist = isReceiptDetailExist(rud);
				if (!isRecDtlExist) {
					boolean isTranExist = false;
					if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
							|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(),
									DisbursementConstants.PAYMENT_TYPE_DD)) {
						String mode = rud.getReceiptMode();
						isTranExist = isChequeExist(rud.getReference(), mode, rud.getBankCode(), rud.getFavourNumber(),
								"_View");
						if (isTranExist) {
							errorMsg = "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey;
							setErrorToRUD(rud, "90273", errorMsg);
						}
					} else if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(),
							DisbursementConstants.PAYMENT_TYPE_ONLINE)) {
						isTranExist = isOnlineExist(rud.getReference(), rud.getSubReceiptMode(),
								rud.getTransactionRef(), "_View");
						if (isTranExist) {
							errorMsg = "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey;
							setErrorToRUD(rud, "90273", errorMsg);
						}
					}
				}
			}

			String dedup = rud.getReference() + rud.getTransactionRef() + rud.getValueDate() + rud.getReceiptAmount();
			logger.info("Checking duplicate Receipt in ReceiptUploadDetails_Temp table..");

			List<String> filenameList = receiptUploadDetailDAO.isDuplicateExists(rud);

			boolean dedupinFile = !filenameList.isEmpty();
			if (!receiptValidList.add(dedup) || dedupinFile) {
				StringBuilder message = new StringBuilder();

				message.append("Duplicate Receipt exists with combination FinReference -").append(rud.getReference());
				message.append(", Value Date -").append(rud.getValueDate()).append("Receipt Amount -")
						.append(rud.getReceiptAmount());

				if (StringUtils.isNotBlank(rud.getTransactionRef())) {
					message.append(" and Transaction Reference").append(rud.getTransactionRef());
				}

				message.append("already exists");

				if (dedupinFile) {
					message.append("with filename").append(filenameList.get(0));
				}

				setErrorToRUD(rud, "21005", message.toString());

				logger.info("Duplicate Receipt found in ReceiptUploadDetails_Temp table..");
			} else {
				logger.info("There is No Duplicate Receipt found in ReceiptUploadDetails_Temp table..");
			}

			if (StringUtils.equals(rud.getReceiptPurpose(), FinanceConstants.EARLYSETTLEMENT)
					|| StringUtils.equals(rud.getReceiptPurpose(), FinanceConstants.PARTIALSETTLEMENT)) {
				if (StringUtils.isNotBlank(fileName)) {
					String finReferenceValue = getLoanReferenc(rud.getReference(), fileName);
					if (StringUtils.isNotBlank(finReferenceValue)) {
						errorMsg = "Receipt In process for " + rud.getReference();
						setErrorToRUD(rud, "90273", errorMsg);
					}
				}
			}

			ErrorDetail errorDetail = receiptService.getWaiverValidation(rud.getReference(), rud.getReceiptPurpose(),
					rud.getValueDate());
			if (errorDetail != null) {
				rud.getErrorDetails().add(ErrorUtil.getErrorDetail(errorDetail));
			}

			rudList.add(rud);

			rut.incrementProgress();
		}

		if (rudList == null || rudList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
			return true;
		}

		logger.info("File Data Validation and ReceiptUploadDetails Preparation was completed...");
		return false;
	}

	private boolean isReceiptDetailExist(ReceiptUploadDetail rud) {
		boolean isreceiptdataExits = false;
		if ((StringUtils.equalsIgnoreCase(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))
				&& StringUtils.equalsIgnoreCase(rud.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			String mode = rud.getReceiptMode();

			if (StringUtils.equalsIgnoreCase(rud.getReceiptPurpose(), "SP")) {
				isreceiptdataExits = isReceiptDetailsExits(rud.getReference(), mode, rud.getChequeNo(),
						rud.getFavourNumber(), "");
			} else {
				isreceiptdataExits = isReceiptDetailsExits(rud.getReference(), mode, rud.getChequeNo(),
						rud.getFavourNumber(), "_Temp");
			}
		}
		return isreceiptdataExits;

	}

	private ReceiptUploadDetail prepareReceiptUploadDetail(Row rchRow, long headerId, Date appDate) {

		ReceiptUploadDetail rud = new ReceiptUploadDetail();
		String strValue = "";
		long longValue = 0;
		Date dateValue = appDate;

		// Root ID
		strValue = getCellStringValue(rchRow, 0).trim();
		rud.setRootId(strValue);
		rud.setUploadheaderId(headerId);

		strValue = getCellStringValue(rchRow, 1).trim().toUpperCase();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReference(strValue);
		} else {
			setErrorToRUD(rud, "RU0040", "Blanks/Nulls in [REFERENCE] ");
		}

		// Receipt Purpose
		strValue = getCellStringValue(rchRow, 2).trim().toUpperCase();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceiptPurpose(strValue);
		} else {
			setErrorToRUD(rud, "RU0040", "Blanks/Nulls in [RECEIPTPURPOSE] ");
		}

		if (!StringUtils.equals(strValue, "SP") && !StringUtils.equals(strValue, "EP")
				&& !StringUtils.equals(strValue, "ES")) {
			setErrorToRUD(rud, "RU0040", "Values other than SP/EP/ES in [RECEIPTPURPOSE] ");
		}

		// Excess Adjusted to
		strValue = getCellStringValue(rchRow, 3).trim();
		if (StringUtils.isBlank(strValue)) {
			strValue = "E";
		}

		if (!StringUtils.equals(strValue, "E") && !StringUtils.equals(strValue, "A") && StringUtils.isNotBlank(strValue)
				&& !StringUtils.equals(strValue, "#")) {
			setErrorToRUD(rud, "RU0040", "Values other than E/A/ /# in [EXCESSADJUSTTO] ");
		} else {
			rud.setExcessAdjustTo(strValue);
		}

		// Allocation type
		strValue = getCellStringValue(rchRow, 4);
		if (StringUtils.isBlank(strValue)) {
			strValue = "A";
		}

		if (!StringUtils.equals(strValue, "A") && !StringUtils.equals(strValue, "M")) {
			setErrorToRUD(rud, "RU0040", "Values other than A/M in [ALLOCATIONTYPE] ");
		} else {
			rud.setAllocationType(strValue);
		}

		if (StringUtils.equals(strValue, "M") && !StringUtils.equals(rud.getReceiptPurpose(), "SP")) {
			setErrorToRUD(rud, "RU0040", "Values other than A in [ALLOCATIONTYPE] ");
		}

		// Receipt Amount
		strValue = getCellStringValue(rchRow, 5);
		if (StringUtils.isBlank(strValue)) {
			strValue = "0";
		}

		try {
			// receipt upload issue fixed allow the decimal values in receipupload file receiptamount(27-12-2019)
			BigDecimal precisionAmount = new BigDecimal(strValue);
			precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				setErrorToRUD(rud, "RU0040", "Minor Currency (Decimals) in [RECEIPTAMOUNT] ");
				rud.setReceiptAmount(actualAmount);
			} else {
				// precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
				rud.setReceiptAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) <= 0) {
				setErrorToRUD(rud, "RU0040", "[RECEIPTAMOUNT] with value <=0 ");
			}
		} catch (Exception e) {
			rud.setReceiptAmount(BigDecimal.ZERO);
			setErrorToRUD(rud, "RU0040", "[RECEIPTAMOUNT] ");
		}

		// Effective Schedule Method
		strValue = getCellStringValue(rchRow, 6);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setEffectSchdMethod(strValue);
		}

		// Remarks
		strValue = getCellStringValue(rchRow, 7).trim();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setRemarks(strValue);
		}

		if (strValue.length() > 100) {
			setErrorToRUD(rud, "RU0040", "[REMARKS] with length more than 100 characters");

		}

		// Value Date
		strValue = getCellStringValue(rchRow, 8);
		try {
			if (StringUtils.isNotBlank(strValue)) {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setValueDate(dateValue);
			} else {

				setErrorToRUD(rud, "RU0040", "Blanks in [VALUEDATE] ");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [VALUEDATE] ");
		}

		// Received Date
		strValue = getCellStringValue(rchRow, 9);

		try {
			if (StringUtils.isNotBlank(strValue)) {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setReceivedDate(dateValue);
			} else {
				setErrorToRUD(rud, "RU0040", "Blanks in [RECEIVEDDATE] ");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [RECEIVEDDATE] ");
		}

		// 30-08-19:Date comparision should be with date and not on string
		// Value Date and Received Date
		String strValueDate = getCellStringValue(rchRow, 8);
		String strReceivedDate = getCellStringValue(rchRow, 9);

		try {
			if (StringUtils.isNotBlank(strValueDate) && StringUtils.isNotBlank(strReceivedDate)
					&& !(DateUtility.getDate(strReceivedDate, DateFormat.LONG_DATE.getPattern())
							.compareTo(DateUtility.getDate(strValueDate, DateFormat.LONG_DATE.getPattern())) >= 0)) {
				setErrorToRUD(rud, "RU0008", "");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0008", "");
		}
		// Receipt Mode
		strValue = getCellStringValue(rchRow, 10);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceiptMode(strValue);
		}

		if (strValue.length() > 10) {
			setErrorToRUD(rud, "RU0040", "[RECEIPTMODE] with length more than 10 characters");
		}

		// Sub Receipt Mode
		strValue = getCellStringValue(rchRow, 11);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setSubReceiptMode(strValue);
		}

		if (strValue.length() > 11) {
			setErrorToRUD(rud, "RU0040", "[SUBRECEIPTMODE] with length more than 10 characters");
		}

		// Receipt Channel
		strValue = getCellStringValue(rchRow, 12);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceiptChannel(strValue);
		}

		if (strValue.length() > 10) {
			setErrorToRUD(rud, "RU0040", "[RECEIPTCHANNEL] with length more than 10 characters");
		}

		try {
			if (StringUtils.isBlank(strValue)
					&& (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CASH)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
				setErrorToRUD(rud, "RU0040", "Blanks in [RECEIPTCHANNEL]");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Blanks in [RECEIPTCHANNEL]");
		}

		// Funding Account
		strValue = getCellStringValue(rchRow, 13);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setFundingAc(strValue);
		}

		if (strValue.length() > 8) {
			setErrorToRUD(rud, "RU0040", "[FUNDINGAC] with length more than 8 characters");
		}

		// Payment reference
		strValue = getCellStringValue(rchRow, 14);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setPaymentRef(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[PAYMENTREF] with length more than 50 characters");
		}

		// Favour Number
		strValue = getCellStringValue(rchRow, 15).trim();

		// Check no validation in case payment type cheque
		if (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				&& StringUtils.isBlank(strValue)) {
			setErrorToRUD(rud, "RU0040", "[FAVOURNUMBER] is Mandatary");
		}

		if (StringUtils.isNotBlank(strValue)) {
			rud.setFavourNumber(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[FAVOURNUMBER] with length more than 50");
		}

		// Bank Code
		strValue = getCellStringValue(rchRow, 16).trim();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setBankCode(strValue);
		}

		// Cheque Account Number
		strValue = getCellStringValue(rchRow, 17).trim();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setChequeNo(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[CHEQUEACNO] with length more than 50");
		}

		// Transaction Reference
		strValue = getCellStringValue(rchRow, 18);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setTransactionRef(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[TRANSACTIONREF] with length more than 50");
		}

		// Status
		strValue = getCellStringValue(rchRow, 19);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setStatus(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[STATUS] with length more than 1");
		}

		// Deposit Date
		strValue = getCellStringValue(rchRow, 20);
		try {
			if (StringUtils.isBlank(strValue)
					&& (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
				setErrorToRUD(rud, "RU0040", "Blanks in [DEPOSITDATE] ");
			} else {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setDepositDate(dateValue);
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [DEPOSITDATE] ");
		}

		// Realization Date
		strValue = getCellStringValue(rchRow, 21);
		try {
			if (StringUtils.isBlank(strValue)
					&& (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
				setErrorToRUD(rud, "RU0040", "Blanks in [REALIZATIONDATE] ");
			} else {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setRealizationDate(dateValue);
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [REALIZATIONDATE] ");
		}

		// Instrument Date
		strValue = getCellStringValue(rchRow, 22);
		try {
			if (StringUtils.isNotBlank(strValue)) {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				// rch.set
			} else {
				if (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
						|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD)) {
					setErrorToRUD(rud, "RU0040", "Blanks in [INSTRUMENTDATE] ");
				}
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [INSTRUMENTDATE] ");
		}

		// PAN Number
		strValue = getCellStringValue(rchRow, 23);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setPanNumber(strValue);
		}

		// External Reference
		strValue = getCellStringValue(rchRow, 24);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setExtReference(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[EXTERNALREF] with length more than 1 ");
		}

		// Collection Agent
		strValue = getCellStringValue(rchRow, 25);

		if (StringUtils.isBlank(strValue)) {
			strValue = "0";
		}

		if (StringUtils.isNumeric(strValue)) {
			longValue = Long.parseLong(strValue);
			rud.setCollectionAgentId(longValue);
		} else {
			setErrorToRUD(rud, "RU0040", "Non numeric value in [COLLECTIONAGENT] ");
		}

		// Received From
		strValue = getCellStringValue(rchRow, 26);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceivedFrom(strValue);
		}

		if (rud.getErrorDetails() == null || rud.getErrorDetails().isEmpty()) {
			rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
			rud.setReason("");
		} else {
			rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
			rud.setReason(rud.getErrorDetails().get(0).getError());
		}

		logger.debug(Literal.LEAVING);
		return rud;
	}

	private void prepareAllocations(Workbook workbook, List<UploadAlloctionDetail> uadList, ReceiptUploadTracker rut) {
		logger.info("Preparing Allocations started..");

		String strValue = "";

		Sheet rchSheet = workbook.getSheetAt(1);
		int rowCount = rchSheet.getLastRowNum();
		rut.setBatchSize(rowCount);

		for (int i = 1; i <= rowCount; i++) {
			Row rchRow = rchSheet.getRow(i);

			if (rchRow == null) {
				continue;
			}

			UploadAlloctionDetail uad = new UploadAlloctionDetail();
			strValue = getCellStringValue(rchRow, 0);
			if (StringUtils.isBlank(strValue)) {
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: <ROOT>_id with blank value");
			}

			uad.setRootId(strValue);

			strValue = getCellStringValue(rchRow, 1);
			if (StringUtils.isBlank(strValue)) {
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: [ALLOCATIONTYPE] with blank value ");
			}
			uad.setAllocationType(strValue);

			strValue = getCellStringValue(rchRow, 2);
			if (StringUtils.isNotBlank(strValue)) {
				if (strValue.length() > 8) {
					setErrorToUAD(uad, "RU0040",
							"Allocation Sheet: [REFERENCECODE] with lenght more than 8 characters ");
				}
			}
			uad.setReferenceCode(strValue);

			strValue = getCellStringValue(rchRow, 3);
			if (StringUtils.isBlank(strValue)) {
				strValue = "0";
			}

			try {
				BigDecimal precisionAmount = new BigDecimal(strValue);
				precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
				BigDecimal actualAmount = precisionAmount;

				precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
				if (precisionAmount.compareTo(actualAmount) != 0) {
					actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
					actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: Minor Currency (Decimals) in [PAIDAMOUNT] ");
					uad.setPaidAmount(actualAmount);
				} else {
					uad.setPaidAmount(precisionAmount);
				}

				if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: [PAIDAMOUNT] with value <0 ");
				}
			} catch (Exception e) {
				uad.setPaidAmount(BigDecimal.ZERO);
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: [PAIDAMOUNT] ");
			}

			strValue = getCellStringValue(rchRow, 4);
			if (StringUtils.isBlank(strValue)) {
				strValue = "0";
			}

			try {
				BigDecimal precisionAmount = new BigDecimal(strValue);
				BigDecimal actualAmount = precisionAmount;

				precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
				if (precisionAmount.compareTo(actualAmount) != 0) {
					actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
					actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: Minor Currency (Decimals) in [WAIVEDAMOUNT] ");
					uad.setWaivedAmount(actualAmount);
				} else {
					precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
					uad.setWaivedAmount(precisionAmount);
				}

				if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: [WAIVEDAMOUNT] with value <0 ");
				}
			} catch (Exception e) {
				uad.setWaivedAmount(BigDecimal.ZERO);
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: [WAIVEDAMOUNT] ");
			}

			uadList.add(uad);
			rut.incrementProgress();
		}

		logger.info("Preparing Allocations completed..");
		return;

	}

	private void linkReceiptAllocations(List<ReceiptUploadDetail> rudList, List<UploadAlloctionDetail> uadList,
			ReceiptUploadTracker rut) {
		logger.info("Linking ReceiptAllocations to the Upload Details started..");

		rut.setBatchSize(rudList.size());
		for (ReceiptUploadDetail rud : rudList) {

			List<UploadAlloctionDetail> radList = setFromUadList(rud.getRootId(), uadList);
			rud.setListAllocationDetails(radList);

			boolean isManualAloc = StringUtils.equals(rud.getAllocationType(), "M");

			if (isManualAloc && radList.isEmpty()) {
				setErrorToRUD(rud, "RU0040", "Allocation Type is M but allocations not found");
			} else if (!isManualAloc && !radList.isEmpty()) {
				setErrorToRUD(rud, "RU0040", "Allocation Type is A but allocations found");
			}

			// Bring any errors from allocation details to header details
			if (radList.isEmpty()) {
				rut.incrementProgress();
				continue;
			}

			// Bring errors from allocation details to header details
			setErrorsToRUD(rud);

			// Validate sum of allocations against the receipt amount
			BigDecimal manualAllocated = BigDecimal.ZERO;
			for (int j = 0; j < radList.size(); j++) {
				manualAllocated = manualAllocated.add(radList.get(j).getPaidAmount());
			}

			if (manualAllocated.compareTo(rud.getReceiptAmount()) != 0) {
				String strAlloate = PennantApplicationUtil.amountFormate(manualAllocated, 2);
				setErrorToRUD(rud, "RU0040", "Manual allocation " + strAlloate + " Not matching with Receipt Amount ");
			}

			if (rud.getErrorDetails() == null || rud.getErrorDetails().isEmpty()) {
				rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
				rud.setReason("");
			} else {
				rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
				rud.setReason(rud.getErrorDetails().get(0).getError());
			}

			rut.incrementProgress();
		}
		logger.info("Linking ReceiptAllocations to the Upload Details completed..");
	}

	private List<UploadAlloctionDetail> setFromUadList(String rootID, List<UploadAlloctionDetail> uadList) {
		List<UploadAlloctionDetail> radList = new ArrayList<>();
		for (int i = 0; i < uadList.size(); i++) {
			UploadAlloctionDetail uad = uadList.get(i);
			if (StringUtils.equals(rootID, uad.getRootId())) {
				radList.add(uad);
				uadList.remove(i);
				i = i - 1;
			}
		}
		return radList;
	}

	private void setErrorsToRUD(ReceiptUploadDetail rud) {
		List<UploadAlloctionDetail> list = rud.getListAllocationDetails();

		for (UploadAlloctionDetail uploadAlloctionDetail : list) {
			List<ErrorDetail> uadErrors = uploadAlloctionDetail.getErrorDetails();
			if (CollectionUtils.isEmpty(uadErrors)) {
				continue;
			}

			for (ErrorDetail errorDetail : uadErrors) {
				rud.getErrorDetails().add(errorDetail);
			}
		}
	}

	private String getCellStringValue(Row rchRow, int cellIdx) {
		Cell cell = rchRow.getCell(cellIdx);
		return StringUtils.trimToEmpty(objDefaultFormat.formatCellValue(cell, null));
	}

	public void setErrorToRUD(ReceiptUploadDetail rud, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		rud.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	public void setErrorToUAD(UploadAlloctionDetail uad, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		uad.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	public boolean isFileNameExist(String fileName) {
		return this.receiptUploadHeaderDAO.isFileNameExist(fileName);
	}

	@Override
	public void updateStatus(List<Long> headerIdList, Map<Long, ReceiptUploadLog> attemptMap) {
		for (Long Id : headerIdList) {
			int[] statuscount = getHeaderStatusCnt(Id);
			receiptUploadHeaderDAO.uploadHeaderStatusCnt(Id, statuscount[0], statuscount[1]);
			logger.info(
					"Receipt creation process completed for the Header -{} with Total success count{}, Total failure count{}",
					Id, statuscount[0], statuscount[1]);

			ReceiptUploadLog ua = attemptMap.get(Id);

			logger.info("Processed records{} , Success records{}, Failed records{} In Attempt- {} for the HeaderId {}",
					ua.getProcessedRecords().get(), ua.getSuccessRecords().get(), ua.getFailRecords().get(),
					ua.getAttemptNo(), Id);

			receiptUploadHeaderDAO.updateAttemptLog(ua);
			attemptMap.remove(Id);
		}
	}

	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setReceiptService(@Lazy ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}