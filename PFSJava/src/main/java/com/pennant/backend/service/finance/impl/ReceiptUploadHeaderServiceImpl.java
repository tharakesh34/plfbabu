
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

import java.io.IOException;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.ReceiptResponseDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadLog;
import com.pennant.backend.model.receiptupload.ReceiptUploadTracker;
import com.pennant.backend.model.receiptupload.ThreadAllocation;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.upload.ReceiptDataValidator;
import com.pennanttech.pff.receipt.upload.ReceiptFileReader;
import com.pennanttech.pff.receipt.upload.ReceiptUploadThreadProcess;

public class ReceiptUploadHeaderServiceImpl extends GenericService<ReceiptUploadHeader>
		implements ReceiptUploadHeaderService {
	private static final Logger logger = LogManager.getLogger(ReceiptUploadHeaderServiceImpl.class);

	private ReceiptUploadHeaderDAO receiptUploadHeaderDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private UploadAllocationDetailDAO uploadAllocationDetailDAO;
	private ReceiptResponseDetailDAO receiptResponseDetailDAO;
	private DataSource dataSource;
	private ReceiptService receiptService;
	private ReceiptDataValidator receiptDataValidator;

	public ReceiptUploadHeaderServiceImpl() {
		super();
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

		validateFileData(workbook, ruh, rudList, rut);

		prepareAllocations(workbook, uadList, rut);

		linkReceiptAllocations(rudList, uadList, rut);

		ruh.setBefImage(ruh);

		validateReceipt(ruh, rudList, rut);

		ruh.setReceiptUploadList(rudList);

		try {
			fileImport.backUpFile();
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		saveRecord(ruh, rut);
		statusMap.remove(ruh.getId());
	}

	private boolean validateFileData(Workbook workbook, ReceiptUploadHeader ruh, List<ReceiptUploadDetail> rudList,
			ReceiptUploadTracker rut) {
		logger.info("Validating File Data and Preparing ReceiptUploadDetails...");

		final Set<String> txnKeys = new HashSet<>();
		final Set<String> txnChequeKeys = new HashSet<>();
		final Set<String> receiptValidList = new HashSet<>();

		String fileName = ruh.getFileName();
		long headerId = ruh.getId();

		Sheet rchSheet = workbook.getSheetAt(0);
		int rowCount = rchSheet.getLastRowNum();
		rut.setBatchSize(rowCount);

		boolean dedupCheck = SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTUPLOAD_DEDUPCHECK);
		Date appDate = SysParamUtil.getAppDate();

		for (int i = 1; i <= rowCount; i++) {
			Row rchRow = rchSheet.getRow(i);

			if (rchRow == null) {
				continue;
			}

			ReceiptUploadDetail rud = ReceiptFileReader.read(rchRow);

			rud.setUploadheaderId(headerId);
			rud.setAppDate(appDate);
			rud.setEntityCode(ruh.getEntityCode());
			rud.setDedupCheck(dedupCheck);
			rud.setFileName(fileName);
			rud.setTxnKeys(txnKeys);
			rud.setTxnChequeKeys(txnChequeKeys);
			rud.setReceiptValidList(receiptValidList);

			receiptDataValidator.validate(rud);

			rudList.add(rud);

			rut.incrementProgress();
		}

		if (CollectionUtils.isEmpty(rudList)) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
			return true;
		}

		logger.info("File Data Validation and ReceiptUploadDetails Preparation was completed...");
		return false;
	}

	private void prepareAllocations(Workbook workbook, List<UploadAlloctionDetail> uadList, ReceiptUploadTracker rut) {
		logger.info("Preparing Allocations started..");

		Sheet rchSheet = workbook.getSheetAt(1);
		int rowCount = rchSheet.getLastRowNum();
		rut.setBatchSize(rowCount);

		for (int i = 1; i <= rowCount; i++) {
			Row rchRow = rchSheet.getRow(i);

			if (rchRow == null) {
				continue;
			}

			UploadAlloctionDetail uad = ReceiptFileReader.readAllocations(rchRow);
			receiptDataValidator.validateAllocations(uad);

			uadList.add(uad);
			rut.incrementProgress();
		}

		logger.info("Preparing Allocations completed..");
	}

	private void linkReceiptAllocations(List<ReceiptUploadDetail> rudList, List<UploadAlloctionDetail> uadList,
			ReceiptUploadTracker rut) {
		logger.info("Linking ReceiptAllocations to the Upload Details started..");

		rut.setBatchSize(rudList.size());
		for (ReceiptUploadDetail rud : rudList) {
			receiptDataValidator.validateReceipt(rud, uadList);
			rut.incrementProgress();
		}

		logger.info("Linking ReceiptAllocations to the Upload Details completed..");
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
			fsi.setRequestSource(RequestSource.UPLOAD);
			FinanceDetail financeDetail = receiptService.receiptTransaction(fsi);

			FinScheduleData schd = financeDetail.getFinScheduleData();
			if (!schd.getErrorDetails().isEmpty()) {
				ErrorDetail error = schd.getErrorDetails().get(0);
				rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());

				String code = StringUtils.trimToEmpty(error.getCode());
				String description = StringUtils.trimToEmpty(error.getError());

				rud.setReason(String.format("%s %s %s", code, "-", description));
			} else {
				rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
				rud.setReason("");
			}
			rut.incrementProgress();
		});
		logger.info("Receipt validation process completed..");
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

	@Override
	public long saveReceiptResponseFileHeader(String procName) {
		return this.receiptResponseDetailDAO.saveReceiptResponseFileHeader(procName);
	}

	@Override
	public List<ReceiptUploadDetail> getReceiptResponseDetails() {
		return this.receiptResponseDetailDAO.getReceiptResponseDetails();
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

			logger.info("Thread Execution for the pool thread{} to thread{} ended", startThread + 1, endThread);

		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
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

	public void updateImportFail(ReceiptUploadHeader ruh) {
		updateUploadProgress(ruh.getId(), ReceiptUploadConstants.RECEIPT_IMPORTFAILED);
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

	@Autowired
	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
	}

	@Autowired
	public void setUploadAllocationDetailDAO(UploadAllocationDetailDAO uploadAllocationDetailDAO) {
		this.uploadAllocationDetailDAO = uploadAllocationDetailDAO;
	}

	@Autowired
	public void setReceiptResponseDetailDAO(ReceiptResponseDetailDAO receiptResponseDetailDAO) {
		this.receiptResponseDetailDAO = receiptResponseDetailDAO;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setReceiptDataValidator(ReceiptDataValidator receiptDataValidator) {
		this.receiptDataValidator = receiptDataValidator;
	}

}