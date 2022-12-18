package com.pennant.pff.presentment.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.RePresentmentUploadDAO;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennant.pff.presentment.service.ExtractionService;
import com.pennant.pff.upload.dao.UploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

public class RePresentmentUploadServiceImpl implements UploadService<RePresentmentUploadDetail> {
	private static final Logger logger = LogManager.getLogger(RePresentmentUploadServiceImpl.class);

	private ExtractionService extractionService;

	private UploadDAO uploadDAO;
	private RePresentmentUploadDAO representmentUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private EntityDAO entityDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;

	@Override
	public FileUploadHeader getUploadHeader(String moduleCode) {
		WorkFlowDetails workFlow = uploadDAO.getWorkFlow(moduleCode);

		FileUploadHeader header = new FileUploadHeader();

		if (workFlow != null) {
			header.setWorkflowId(workFlow.getWorkFlowId());
		}

		header.setRecordStatus("Submitted");
		header.setAppDate(SysParamUtil.getAppDate());

		return header;
	}

	@Override
	public long saveHeader(FileUploadHeader header, TableType type) {
		return uploadDAO.saveHeader(header);
	}

	@Override
	public void validate(FileUploadHeader header, RePresentmentUploadDetail detail) {
		logger.info("Validating the Excel Data...");

		Date appDate = header.getAppDate();

		int appDateMonth = DateUtil.getMonth(appDate);

		detail.setHeaderId(header.getId());

		String reference = detail.getReference();

		if (StringUtils.isBlank(reference)) {
			setError(detail, PresentmentError.REPRMNT513);
			return;
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(reference, header.getEntityCode());

		if (fm == null) {
			setError(detail, PresentmentError.REPRMNT514);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, PresentmentError.REPRMNT515);
			return;
		}

		detail.setFm(fm);
		detail.setReferenceID(fm.getFinID());

		Date dueDate = detail.getDueDate();
		if (dueDate == null) {
			setError(detail, PresentmentError.REPRMNT516);
			return;
		}

		if (DateUtil.compare(dueDate, appDate) > 0) {
			setError(detail, PresentmentError.REPRMNT517);
			return;
		}

		String bounceCode = representmentUploadDAO.getBounceCode(reference, dueDate);

		if (bounceCode == null) {
			setError(detail, PresentmentError.REPRMNT518);
			return;
		}

		if (detail.getAcBounce().contains(bounceCode)) {
			setError(detail, PresentmentError.REPRMNT519);
			return;
		}

		if (representmentUploadDAO.isProcessed(reference, dueDate)) {
			setError(detail, PresentmentError.REPRMNT520);
			return;
		}

		if (profitDetailsDAO.getCurOddays(fm.getFinID()) == 0) {
			setError(detail, PresentmentError.REPRMNT521);
			return;
		}

		List<String> fileNames = representmentUploadDAO.isDuplicateExists(reference, dueDate, header.getId());

		if (!fileNames.isEmpty()) {
			StringBuilder message = new StringBuilder();

			message.append("Duplicate RePresentMent exists with combination FinReference -");
			message.append(reference);
			message.append(", Due Date -").append(dueDate);
			message.append("with filename").append(fileNames.get(0)).append("already exists");

			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode(PresentmentError.REPRMNT523.name());
			detail.setErrorDesc(message.toString());

			logger.info("Duplicate RePresentment found in RePresentUpload table..");
			return;
		}

		int curSchdMonth = DateUtil.getMonth(dueDate);
		if (curSchdMonth != appDateMonth) {
			setError(detail, PresentmentError.REPRMNT522);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	@Override
	public void update(FileUploadHeader uploadHeader) {
		this.uploadDAO.updateHeader(uploadHeader);
	}

	@Override
	public List<Entity> getEntities() {
		return this.entityDAO.getEntites();
	}

	@Override
	public List<FileUploadHeader> getUploadHeaderById(String entityCode, Long id, Date fromDate, Date toDate) {
		return uploadDAO.getHeaderData(entityCode, id, fromDate, toDate);
	}

	@Override
	public void approve(List<FileUploadHeader> headers) {
		new Thread(() -> {

			List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

			Date appDate = SysParamUtil.getAppDate();
			String acBounce = SysParamUtil.getValueAsString(SMTParameterConstants.BOUNCE_CODES_FOR_ACCOUNT_CLOSED);

			for (FileUploadHeader header : headers) {
				List<RePresentmentUploadDetail> details = representmentUploadDAO.loadRecordData(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (RePresentmentUploadDetail detail : details) {
					detail.setAcBounce(acBounce);
					validate(header, detail);

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				representmentUploadDAO.update(details);

				header.setSuccessRecords(sucessRecords);
				header.setFailureRecords(failRecords);

				StringBuilder remarks = new StringBuilder("Process Completed");

				if (failRecords > 0) {
					remarks.append(" with exceptions, ");
				}

				remarks.append(" Total Records : ").append(header.getTotalRecords());
				remarks.append(" Success Records : ").append(sucessRecords);
				remarks.append(" Failed Records : ").append(failRecords);
			}

			uploadDAO.updateHeader(headers);

			int extractPresentment = extractionService.extractRePresentment(headerIdList);

			if (extractPresentment > 0) {
				logger.info("RePresentment Process is Initiated");
			}
		}).start();
	}

	@Override
	public void reject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		String errorCode = PresentmentError.REPRMNT523.name();
		String errorDesc = PresentmentError.REPRMNT523.description();

		representmentUploadDAO.update(headerIdList, errorCode, errorDesc, EodConstants.PROGRESS_FAILED);

		uploadDAO.updateReject(headerIdList, errorDesc);
	}

	private void setError(RePresentmentUploadDetail detail, PresentmentError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Autowired
	public void setExtractionService(ExtractionService extractionService) {
		this.extractionService = extractionService;
	}

	@Autowired
	public void setRePresentmentUploadDAO(RePresentmentUploadDAO rePresentmentUploadDAO) {
		this.representmentUploadDAO = rePresentmentUploadDAO;
	}

	@Autowired
	public void setUploadDAO(UploadDAO uploadDAO) {
		this.uploadDAO = uploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Autowired
	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

}
