package com.pennant.pff.mandate.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.mandate.MandateUploadDAO;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateUpload;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.excess.MandateUploadError;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;

public class MandateUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(MandateUploadServiceImpl.class);

	private MandateUploadDAO mandateUploadDAO;
	private MandateService mandateService;
	private ValidateRecord mandateUploadValidateRecord;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				List<MandateUpload> details = mandateUploadDAO.loadRecordData(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				try {
					txStatus = transactionManager.getTransaction(txDef);

					for (MandateUpload detail : details) {
						doValidate(header, detail);

						if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
							failRecords++;
							continue;
						}
						Mandate mandate = detail.getMandate();
						mandate.setUserDetails(header.getUserDetails());
						mandate.setSourceId(RequestSource.UPLOAD.name());
						Mandate response = mandateService.createMandates(mandate);

						ErrorDetail error = response.getError();
						if (error != null) {
							failRecords++;
							detail.setProgress(EodConstants.PROGRESS_FAILED);
							detail.setErrorCode(error.getCode());
							detail.setErrorDesc(error.getError());
						} else {
							sucessRecords++;
							detail.setProgress(EodConstants.PROGRESS_SUCCESS);
							detail.setErrorCode("");
							detail.setErrorDesc("");
							detail.setReferenceID(response.getMandateID());
						}
					}

					mandateUploadDAO.update(details);
					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

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

			try {
				txStatus = transactionManager.getTransaction(txDef);

				updateHeader(headers, true);

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
			}

		}).start();

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			mandateUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
	public void doValidate(FileUploadHeader header, Object detail) {

		MandateUpload mu = null;

		if (detail instanceof MandateUpload) {
			mu = (MandateUpload) detail;
		}

		if (mu == null) {
			throw new AppException("Invalid Data transferred...");
		}

		Mandate mandate = mu.getMandate();

		String extMndt = mandate.getStrExternalMandate();
		String swapMndt = mandate.getStrSwapIsActive();
		String openMndt = mandate.getStrOpenMandate();

		InstrumentType instrumentType = InstrumentType.getType(mandate.getMandateType());

		switch (instrumentType) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:

			externalMandateRequired(mu, mandate, extMndt);

			swapMandateRequired(mu, mandate, swapMndt);

			openMandateRequired(mu, mandate, openMndt);

			break;

		case SI:
			break;

		case DAS:
			swapMandateRequired(mu, mandate, swapMndt);
			break;

		default:
			break;
		}

	}

	private void openMandateRequired(MandateUpload mu, Mandate mandate, String openMndt) {
		if ("T".equals(openMndt)) {
			mandate.setOpenMandate(true);
		} else if ("F".equals(openMndt)) {
			mandate.setOpenMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_013);
		}
	}

	private void swapMandateRequired(MandateUpload mu, Mandate mandate, String swapMndt) {
		if ("T".equals(swapMndt)) {
			mandate.setSwapIsActive(true);
		} else if ("F".equals(swapMndt)) {
			mandate.setSwapIsActive(false);
		} else {
			setError(mu, MandateUploadError.MANUP_014);
		}
	}

	private void externalMandateRequired(MandateUpload mu, Mandate mandate, String extMndt) {
		if ("T".equals(extMndt)) {
			mandate.setExternalMandate(true);
		} else if ("F".equals(extMndt)) {
			mandate.setExternalMandate(false);
		} else {
			setError(mu, MandateUploadError.MANUP_015);
		}
	}

	private void setError(MandateUpload detail, MandateUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setStatus("F");
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.MANDATES.name(), mandateUploadValidateRecord, this, "MandateUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return mandateUploadDAO.getSqlQuery();
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return mandateUploadValidateRecord;
	}

	@Autowired
	public void setMandateUploadDAO(MandateUploadDAO mandateUploadDAO) {
		this.mandateUploadDAO = mandateUploadDAO;
	}

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	@Autowired
	public void setMandateUploadValidateRecord(MandateUploadValidateRecord mandateUploadValidateRecord) {
		this.mandateUploadValidateRecord = mandateUploadValidateRecord;
	}

}
