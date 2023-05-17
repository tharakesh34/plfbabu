package com.pennant.pff.presentment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

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

public class FateCorrectionUploadServiceImpl extends AUploadServiceImpl<PresentmentRespUpload> {
	private static final Logger logger = LogManager.getLogger(FateCorrectionUploadServiceImpl.class);

	private PresentmentRespUploadDAO presentmentRespUploadDAO;
	private FinanceMainDAO financeMainDAO;

	public FateCorrectionUploadServiceImpl() {
		super();
	}

	@Override
	protected PresentmentRespUpload getDetail(Object object) {
		if (object instanceof PresentmentRespUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		PresentmentRespUpload detail = getDetail(object);

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
		setSuccesStatus(detail);
	}

	private void setError(PresentmentRespUpload detail, PresentmentError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<PresentmentRespUpload> details = presentmentRespUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				header.getUploadDetails().addAll(details);

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

				presentmentRespUploadDAO.update(details);

				header.setSuccessRecords(sucessRecords);
				header.setFailureRecords(failRecords);

				List<FileUploadHeader> headerList = new ArrayList<>();
				headerList.add(header);

				updateHeader(headerList, true);

				logger.info("Fate Correction Process is Initiated");

				TransactionStatus txStatus = getTransactionStatus();

				try {
					process(header);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);

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
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(presentmentRespUploadDAO.getDetails(h1.getId()));
			});

			presentmentRespUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

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
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		PresentmentRespUpload presentment = (PresentmentRespUpload) ObjectUtil.valueAsObject(paramSource,
				PresentmentRespUpload.class);

		presentment.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		presentment.setHeaderId(header.getId());
		presentment.setAppDate(header.getAppDate());

		doValidate(header, presentment);

		updateProcess(header, presentment, paramSource);

		header.getUploadDetails().add(presentment);

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