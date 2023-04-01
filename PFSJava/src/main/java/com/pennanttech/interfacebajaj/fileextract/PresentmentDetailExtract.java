package com.pennanttech.interfacebajaj.fileextract;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.eventproperties.service.impl.EventPropertiesServiceImpl.EventType;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.presentment.PresentmentResponseProcess;
import com.pennant.pff.presentment.dao.ConsecutiveBounceDAO;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.interfacebajaj.fileextract.service.FileImport;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.script.ScriptEngine;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.external.PresentmentImportProcess;
import com.pennanttech.pff.notifications.service.NotificationService;

public class PresentmentDetailExtract extends FileImport implements Runnable {
	private static final Logger logger = LogManager.getLogger(PresentmentDetailExtract.class);

	/* Data Source */
	private DataSource dataSource;

	/* DAO's */
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private ConsecutiveBounceDAO consecutiveBounceDAO;
	private MandateDAO mandateDAO;
	private MandateStatusDAO mandateStatusDAO;

	/* Service's */
	private NotificationService notificationService;
	private ReceiptPaymentService receiptPaymentService;
	private ReceiptCancellationService receiptCancellationService;
	private PresentmentDetailService presentmentDetailService;
	private PresentmentImportProcess presentmentImportProcess;
	private PostingsPreparationUtil postingsPreparationUtil;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private ReceiptCalculator receiptCalculator;
	private EventPropertiesService eventPropertiesService;
	private FinMandateService finMandateService;

	/* Objects */
	private LoggedInUser userDetails;
	private DataEngineStatus deStatus;

	/* Variables */
	private String instrumentType = null;

	public static AtomicLong recordCount = new AtomicLong(0);
	public static AtomicLong successCount = new AtomicLong(0);
	public static AtomicLong failedCount = new AtomicLong(0);

	public static AtomicInteger totalThreads = new AtomicInteger(0);
	public static AtomicInteger processedThreads = new AtomicInteger(0);

	public PresentmentDetailExtract(DataSource datsSource) {
		super(datsSource);
		this.dataSource = datsSource;

		recordCount = new AtomicLong(0);
		successCount = new AtomicLong(0);
		failedCount = new AtomicLong(0);

		totalThreads = new AtomicInteger(0);
		processedThreads = new AtomicInteger(0);
	}

	@Override
	public void run() {
		RuleExecutionUtil.PRESENTMENT_RESP_SCRIPT_ENGINE_MAP.clear();

		try {
			instrumentTypeResponse();
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			for (Entry<String, ScriptEngine> entry : RuleExecutionUtil.PRESENTMENT_RESP_SCRIPT_ENGINE_MAP.entrySet()) {
				try {
					entry.getValue().setEod(false);
					entry.getValue().close();
				} catch (Exception e) {
					//
				}
			}
		}

		RuleExecutionUtil.PRESENTMENT_RESP_SCRIPT_ENGINE_MAP.clear();
	}

	private void instrumentTypeResponse() {
		logger.info(Literal.ENTERING);

		String configName = deStatus.getName();
		String fileName = getMedia().getName();

		long userId = getUserDetails().getUserId();
		Date appDate = SysParamUtil.getAppDate();

		deStatus.reset();
		deStatus.setFileName(fileName);
		deStatus.setRemarks("Initiated file reading...");

		logger.info("Data-engine config name: {}", configName);
		logger.info("Presentment response file name: {}", fileName);

		String database = App.DATABASE.name();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, database, true, appDate, deStatus);
		dataEngine.setMedia(getMedia());

		try {
			if (configName.equals("PRESENTMENT_RESPONSE_PDC")) {
				dataEngine.setProcessRecord((ProcessRecord) SpringBeanUtil.getBean("customPresentmentExtact"));
			}
		} catch (Exception e) {
			//
		}

		// FIXME Need to handle in Niyogin Upgrade
		if (configName.equals("PRESENTMENT_RESPONSE")) {
			// dataEngine.setValidateRecord(new DefaultPresentmentRespValidation());
		}

		long headerId = presentmentDetailDAO.logHeader(fileName, null, "IMPORT", 0);

		logger.info("Import header-ID: {}", headerId);

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("HEADER_ID", headerId);

		dataEngine.setParameterMap(parameterMap);

		try {

			dataEngine.importData(configName);

			int totalRecords = 0;

			do {
				if (ExecutionStatus.S.name().equals(deStatus.getStatus())
						|| ExecutionStatus.F.name().equals(deStatus.getStatus())) {
					if (totalRecords > 0) {

						deStatus.setTotalRecords(totalRecords);
						// deStatus.setStatus(ExecutionStatus.I.name());

						long deExecutionId = (long) deStatus.getId();
						int successRecords = (int) deStatus.getSuccessRecords();
						int failedRecords = (int) deStatus.getFailedRecords();
						String remarks = deStatus.getRemarks();
						presentmentDetailDAO.updateHeader(headerId, deExecutionId, totalRecords, successRecords,
								failedRecords, ExecutionStatus.S.name(), remarks);

						processingPrsentments(headerId, fileName);

						deStatus.setTotalRecords(totalRecords);
						deStatus.setRemarks("File Reading completed. response job's will process the records.");
						deStatus.setProcessedRecords(totalRecords);
						deStatus.setFailedRecords(failedRecords);
						deStatus.setSuccessRecords(successRecords);
						break;
					}
				}

				if (deStatus.getTotalRecords() > 0) {
					totalRecords = (int) deStatus.getTotalRecords();
					logger.info("\nTotal Records: {}", totalRecords);
				} else if (StringUtils.isNotEmpty(deStatus.getRemarks())) {
					break;
				}

			} while (ExecutionStatus.S.name().equals(deStatus.getStatus())
					|| ExecutionStatus.F.name().equals(deStatus.getStatus()));

		} catch (Exception e) {
			deStatus.setRemarks(e.getMessage());
			deStatus.setStatus(ExecutionStatus.F.name());
			deStatus.setEndTime(DateUtil.getSysDate());
			logger.error(Literal.EXCEPTION, e);
		} finally {
			presentmentDetailDAO.deleteByHeaderId(headerId);
		}

		logger.info(Literal.LEAVING);
	}

	public void truncateTable(String tableName) {
		presentmentDetailDAO.truncate(tableName);
	}

	public void processingPrsentments(long importHeaderId, String batchReference) {
		presentmentDetailDAO.logRespDetails(importHeaderId, importHeaderId);
		presentmentDetailDAO.updateProgess(importHeaderId, 1);
	}

	public void processingPrsentments1(long importHeaderId, String batchReference) {
		logger.info(Literal.ENTERING);

		if (deStatus == null) {
			deStatus = new DataEngineStatus();
		}

		deStatus.setRemarks("Start processing...");

		logger.info("Creating the header for processing..");

		long headerId = presentmentDetailDAO.logHeader(batchReference, null, "PROCESS", 0);

		logger.info("Process header-ID: {}", headerId);

		int totalRecords = presentmentDetailDAO.logRespDetails(importHeaderId, headerId);

		logger.info("Total Records: {}", totalRecords);

		deStatus.setTotalRecords(totalRecords);
		deStatus.setStatus(ExecutionStatus.I.name());

		updateThreads(headerId, totalRecords);

		EventProperties eventProperties = eventPropertiesService
				.getEventProperties(EventType.PRESENTMENT_RESPONSE_UPLOAD);

		List<Integer> threads = presentmentDetailDAO.getThreads(headerId);

		int threadCount = threads.size();

		logger.info("Total Threads: {}", threadCount);

		totalThreads.set(threadCount);

		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("PLF_PRESENTMENT_RESP_THREAD_");

		for (Integer threadId : threads) {
			PresentmentResponseProcess prp = new PresentmentResponseProcess(dataSource, headerId, threadId,
					eventProperties);
			prp.setReceiptPaymentService(receiptPaymentService);
			prp.setPresentmentDetailService(presentmentDetailService);
			prp.setPostingsPreparationUtil(postingsPreparationUtil);
			prp.setPresentmentDetailDAO(presentmentDetailDAO);
			prp.setFinReceiptHeaderDAO(finReceiptHeaderDAO);
			prp.setFinanceMainDAO(financeMainDAO);
			prp.setCustomerDAO(customerDAO);
			prp.setFinanceScheduleDetailDAO(financeScheduleDetailDAO);
			prp.setFinanceProfitDetailDAO(financeProfitDetailDAO);
			prp.setConsecutiveBounceDAO(consecutiveBounceDAO);
			prp.setMandateDAO(mandateDAO);
			prp.setMandateStatusDAO(mandateStatusDAO);
			prp.setRepaymentPostingsUtil(repaymentPostingsUtil);
			prp.setFinODDetailsDAO(finODDetailsDAO);
			prp.setReceiptCalculator(receiptCalculator);
			prp.setFinReceiptDetailDAO(finReceiptDetailDAO);
			prp.setFinanceRepaymentsDAO(financeRepaymentsDAO);
			prp.setFinExcessAmountDAO(finExcessAmountDAO);
			prp.setReceiptCancellationService(receiptCancellationService);
			prp.setNotificationService(notificationService);
			prp.setPresentmentImportProcess(presentmentImportProcess);
			prp.setFinMandateService(finMandateService);
			prp.setDeStatus(deStatus);

			taskExecutor.execute(prp);
		}

		do {
			deStatus.setRemarks("Processing...");

			deStatus.setProcessedRecords(recordCount.get());
			deStatus.setSuccessRecords(successCount.get());
			deStatus.setFailedRecords(failedCount.get());
			deStatus.setStatus(ExecutionStatus.I.name());

			if (processedThreads.get() == totalThreads.get()) {
				long deExecutionId = (long) deStatus.getId();

				logger.info("Logging Un-processed records...");
				int unProcessedRcdCnt = logUnProcessedRcds(headerId);
				logger.info("Un-processed records..." + unProcessedRcdCnt);

				failedCount.set(failedCount.get() + unProcessedRcdCnt);
				recordCount.set(recordCount.get() + unProcessedRcdCnt);

				deStatus.setProcessedRecords(recordCount.get());
				deStatus.setSuccessRecords(successCount.get());
				deStatus.setFailedRecords(failedCount.get());

				deStatus.setRemarks("Processing complted... Updating record counts..");

				String status = ExecutionStatus.F.name();
				if (failedCount.intValue() <= 0) {
					status = ExecutionStatus.S.name();
				}

				String remarks = getRemarks();

				presentmentDetailDAO.updateHeader(headerId, deExecutionId, recordCount.intValue(),
						successCount.intValue(), failedCount.intValue(), status, remarks);

				setExceptionLog(deStatus);

				deStatus.setRemarks(remarks);
				deStatus.setStatus(status);
				deStatus.setEndTime(DateUtil.getSysDate());

				presentmentDetailDAO.logRespDetailsLog(headerId);

				logger.info("Updating the stats in PresentmentHeader table...");
				updatePresentmentHeader(headerId);

				presentmentDetailDAO.truncate("PRESENTMENT_RESP_DTLS");

				break;
			}

		} while (processedThreads.get() != totalThreads.get());

		logger.info(Literal.LEAVING);
	}

	private String getRemarks() {
		StringBuilder remarks = new StringBuilder();

		if (recordCount.intValue() > 0) {
			if (failedCount.intValue() > 0) {
				remarks.append(" Completed with exceptions, total Records: ");
				remarks.append(recordCount.intValue());
				remarks.append(", Success: ");
				remarks.append(successCount.intValue() + ".");
				remarks.append(", Failure: ");
				remarks.append(failedCount.intValue() + ".");
			} else {
				remarks.append(" Completed successfully, total Records: ");
				remarks.append(recordCount.intValue());
				remarks.append(", Success: ");
				remarks.append(successCount.intValue() + ".");
			}
		}

		return remarks.toString();
	}

	private void updatePresentmentHeader(long headerId) {
		List<Long> presentmentHeaders = presentmentDetailDAO.getPresentmentHeaderIdsByHeaderId(headerId);

		for (Long id : presentmentHeaders) {
			List<String> statusList = presentmentDetailDAO.getStatusListByHeader(id);
			int successCount = 0;
			int failedCount = 0;
			int totalCount = statusList.size();

			for (String sts : statusList) {
				if (RepayConstants.PEXC_SUCCESS.equals(sts) || RepayConstants.PEXC_BOUNCE.equals(sts)) {
					successCount++;
				} else if (RepayConstants.PEXC_FAILURE.equals(sts)) {
					failedCount++;
				}
			}

			presentmentDetailDAO.updateHeaderCounts(id, successCount, failedCount);

			if (totalCount == (successCount + failedCount)) {
				presentmentDetailDAO.updateHeaderStatus(id, RepayConstants.PEXC_RECEIVED);
			}
		}
	}

	private int logUnProcessedRcds(long headerId) {
		List<String> presentmentRefList = presentmentDetailDAO.getUnProcessedPrentmntRef(headerId);

		if (CollectionUtils.isNotEmpty(presentmentRefList)) {
			for (String presntmntRef : presentmentRefList) {
				String errorDesc = "Presentment not exists for this reference.";
				presentmentDetailDAO.updateDataEngineLog(deStatus.getId(), presntmntRef, "F", errorDesc);
			}
		}
		return presentmentRefList.size();
	}

	private void updateThreads(long headerId, int totalRecords) {
		logger.info(Literal.ENTERING);
		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_RESP_PROCESS_THREAD_COUNT);

		long noOfRows = Math.round((new Double(totalRecords) / new Double(threadCount)));

		boolean recordsLessThanThread = false;

		if (totalRecords < threadCount) {
			recordsLessThanThread = true;
			noOfRows = 1;
		}

		long from = presentmentDetailDAO.getMinIDByHeaderID(headerId);
		long to = presentmentDetailDAO.getMaxIDByHeaderID(headerId);
		for (int i = 1; i <= threadCount; i++) {

			int customerCount = 0;
			if (i == threadCount) {
				/* Last thread will have the remaining records */
				noOfRows = totalRecords;
			}

			to = to + noOfRows;
			customerCount = presentmentDetailDAO.updateThreadID(headerId, from, to, i);
			from = to;

			if (recordsLessThanThread && i == customerCount) {
				break;
			}
		}

		logger.info(Literal.LEAVING);
	}

	private void setExceptionLog(DataEngineStatus status) {
		List<DataEngineLog> engineLogs = presentmentDetailDAO.getDEExceptions(status.getId());
		if (CollectionUtils.isNotEmpty(engineLogs)) {
			status.setDataEngineLogList(engineLogs);
		}
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public DataEngineStatus getStatus() {
		return deStatus;
	}

	public void setStatus(DataEngineStatus status) {
		this.deStatus = status;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPresentmentImportProcess(PresentmentImportProcess presentmentImportProcess) {
		this.presentmentImportProcess = presentmentImportProcess;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setConsecutiveBounceDAO(ConsecutiveBounceDAO consecutiveBounceDAO) {
		this.consecutiveBounceDAO = consecutiveBounceDAO;
	}

	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public EventPropertiesService getEventPropertiesService() {
		return eventPropertiesService;
	}

	public void setEventPropertiesService(EventPropertiesService eventPropertiesService) {
		this.eventPropertiesService = eventPropertiesService;
	}

	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

}
