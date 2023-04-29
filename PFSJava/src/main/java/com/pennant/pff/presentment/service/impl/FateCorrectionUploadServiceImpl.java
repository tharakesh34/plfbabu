package com.pennant.pff.presentment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.presentment.dao.PresentmentRespUploadDAO;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.presentment.PresentmentRespUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadTypes;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennapps.core.util.ObjectUtil;

public class FateCorrectionUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(FateCorrectionUploadServiceImpl.class);

	private PresentmentRespUploadDAO presentmentRespUploadDAO;
	private FinanceMainDAO financeMainDAO;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		PresentmentRespUpload detail = null;

		if (object instanceof PresentmentRespUpload) {
			detail = (PresentmentRespUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

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

		if (presentmentRespUploadDAO.isDuplicateKeyPresent(reference, detail.getClearingStatus(),
				detail.getClearingDate())) {
			setError(detail, PresentmentError.FC_601);
			return;
		}

		PresentmentDetail pd = presentmentRespUploadDAO.getPresentmentDetail(reference, detail.getClearingDate());

		if (pd == null) {
			setError(detail, PresentmentError.FC_602);
			return;
		}

		String status = pd.getStatus();

		if (!(RepayConstants.PEXC_BOUNCE.equals(status)) && !(RepayConstants.PEXC_SUCCESS.equals(status))) {
			if (PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType())) {
				setError(detail, PresentmentError.FC_606);
			} else {
				setError(detail, PresentmentError.FC_605);
			}
			return;
		}

		if (status.equals(detail.getClearingStatus())) {
			if (RepayConstants.PEXC_BOUNCE.equals(status)) {
				setError(detail, PresentmentError.FC_608);
			}

			if (RepayConstants.PEXC_SUCCESS.equals(status)) {
				setError(detail, PresentmentError.FC_607);
			}
			return;
		}

		if (RepayConstants.PEXC_BOUNCE.equals(detail.getClearingStatus())
				&& (StringUtils.isEmpty(detail.getBounceCode()) || StringUtils.isEmpty(detail.getBounceRemarks()))) {
			setError(detail, PresentmentError.FC_604);
			return;
		}

		detail.setPresentmentReference(pd.getPresentmentRef());
		detail.setBounceCode(detail.getBounceCode());
		detail.setBounceRemarks(detail.getBounceRemarks());
		detail.setAccountNumber(pd.getAccountNo());

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	private void setError(PresentmentRespUpload detail, PresentmentError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<PresentmentRespUpload> details = presentmentRespUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (PresentmentRespUpload fc : details) {
					doValidate(header, fc);

					if (fc.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					presentmentRespUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);

					updateHeader(headerList, true);

					logger.info("Fate Correction Process is Initiated");

					process(header);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				logger.info("Processed the File {}", header.getFileName());
			}

		}).start();

	}

	private void process(FileUploadHeader header) {
		long id = presentmentRespUploadDAO.saveRespHeader(header);
		presentmentRespUploadDAO.saveRespDetails(header.getId(), id);
		presentmentRespUploadDAO.updateProcessingFlag(id);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		try {
			txStatus = transactionManager.getTransaction(txDef);

			presentmentRespUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));
			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.FATE_CORRECTION.name(), this, "FateCorrection");
	}

	@Override
	public String getSqlQuery() {
		return presentmentRespUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentRespUpload presentment = (PresentmentRespUpload) ObjectUtil.valueAsObject(record,
				PresentmentRespUpload.class);

		presentment.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLAOD_HEADER");

		presentment.setHeaderId(header.getId());
		presentment.setAppDate(header.getAppDate());

		doValidate(header, presentment);

		updateProcess(header, presentment, record);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setPresentmentRespUploadDAO(PresentmentRespUploadDAO presentmentRespUploadDAO) {
		this.presentmentRespUploadDAO = presentmentRespUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}