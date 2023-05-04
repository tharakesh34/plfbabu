package com.pennant.pff.lien.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.liendetails.LienDetailsDAO;
import com.pennant.backend.dao.lienheader.LienHeaderDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.lien.dao.LienUploadDAO;
import com.pennant.pff.lien.service.LienService;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.model.lien.LienHeader;
import com.pennanttech.model.lien.LienUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class LienUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(LienUploadServiceImpl.class);

	private LienUploadDAO lienUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private LienDetailsDAO lienDetailsDAO;
	private LienHeaderDAO lienHeaderDAO;
	private LienService lienService;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LienUpload detail = null;

		if (object instanceof LienUpload) {
			detail = (LienUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();
		logger.info("Validating the Data for the reference {}", reference);

		if (StringUtils.isBlank(reference)) {
			setError(detail, LienUploadError.LUOU_101);
			return;
		}

		if (detail.getAction() == null) {
			setError(detail, LienUploadError.LUOU_104);
			return;
		}

		if (detail.getAccNumber() == null) {
			setError(detail, LienUploadError.LUOU_112);
			return;
		}

		if (detail.getSource() == null) {
			setError(detail, LienUploadError.LUOU_113);
			return;
		}

		LienDetails lu = lienDetailsDAO.getLienById(reference);

		if (lu != null && lu.getMarking() == null && detail.getAction().equals("Y")) {
			setError(detail, LienUploadError.LUOU_110);
			return;
		}

		if (lu != null && lu.getDemarking() != null && detail.getAction().equals("N")) {
			setError(detail, LienUploadError.LUOU_111);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;
			Date appDate = SysParamUtil.getAppDate();

			try {
				txStatus = transactionManager.getTransaction(txDef);
				for (FileUploadHeader header : headers) {
					Map<String, List<LienUpload>> lienUploadsMap = new HashMap<>();
					List<LienUpload> lienUploadList = lienUploadDAO.getDetails(header.getId());

					for (LienUpload lienUpload : lienUploadList) {
						String reference = lienUpload.getReference();
						if (lienUploadsMap.containsKey(reference)) {
							List<LienUpload> list = lienUploadsMap.get(reference);
							list.add(lienUpload);
						} else {
							List<LienUpload> list = new ArrayList<>();
							list.add(lienUpload);
							lienUploadsMap.put(reference, list);
						}
					}

					Set<String> References = lienUploadsMap.keySet();

					int sucessRecords = 0;
					int failRecords = 0;

					FinanceMain fm = null;

					for (String finReference : References) {
						List<LienUpload> lienUploads = lienUploadsMap.get(finReference);

						fm = financeMainDAO.getFinanceMain(finReference, header.getEntityCode());

						if (fm != null && !fm.isFinIsActive()) {
							for (LienUpload lienUpload : lienUploads) {
								lienUpload.setProgress(EodConstants.PROGRESS_FAILED);
								lienUpload.setErrorCode("9999");
								lienUpload.setErrorDesc("Fin Reference is not active.");
							}

							continue;
						} else {
							fm = new FinanceMain();
							fm.setFinReference(finReference);
							fm.setFinSourceID(RequestSource.UPLOAD.name());
						}

						fm.setAppDate(appDate);
						header.setAppDate(appDate);

						for (LienUpload lu : lienUploads) {
							lu.setFinanceMain(fm);
						}

						process(header, lienUploads);

						for (LienUpload luUpload : lienUploads) {
							if (luUpload.getProgress() == EodConstants.PROGRESS_FAILED) {
								failRecords++;
							} else {
								sucessRecords++;
							}
						}

						header.setSuccessRecords(sucessRecords);
						header.setFailureRecords(failRecords);
					}

					logger.info("Processed the File {}", header.getFileName());

					lienUploadDAO.updateStatus(lienUploadList);
					updateHeader(headers, true);
					transactionManager.commit(txStatus);

				}
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
			}

		}).start();

	}

	private void process(FileUploadHeader header, List<LienUpload> lienUploads) {
		for (LienUpload lienup : lienUploads) {
			lienup.setAppDate(header.getAppDate());

			doValidate(header, lienup);

			if (EodConstants.PROGRESS_FAILED == lienup.getProgress()) {
				continue;
			}

			lienup.setUserDetails(header.getUserDetails());

			if (lienup.getAction().equals("Y")) {
				lienup.setLienstatus(true);
				lienup.setMarking(Labels.getLabel("label_Lien_Type_Manual"));
				lienup.setMarkingDate(header.getAppDate());
				lienup.setMarkingReason(header.getRemarks());
				lienup.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
			} else {
				lienup.setLienstatus(false);
				lienup.setDemarkingReason(header.getRemarks());
				lienup.setDemarking(Labels.getLabel("label_Lien_Type_Manual"));
				lienup.setDemarkingDate(header.getAppDate());
				lienup.setReference(lienup.getReference());
				lienup.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Success"));
			}

			lienUploadDAO.update(lienup, lienup.getId());

			String accNumber = lienup.getAccNumber();
			LienHeader lienheader = lienHeaderDAO.getLienByAcc(accNumber);

			if (lienheader != null) {
				lienup.setLienID(lienheader.getLienID());
				lienup.setLienReference(lienheader.getLienReference());
			}

			FinanceMain fm = lienup.getFinanceMain();
			fm.setFinSourceID(RequestSource.UPLOAD.name());

			FinanceDetail fd = new FinanceDetail();
			LienHeader lienhead = new LienHeader();
			fd.getFinScheduleData().setFinanceMain(fm);
			Mandate mandate = new Mandate();
			mandate.setAccNumber(lienup.getAccNumber());
			fd.setMandate(mandate);
			lienhead.setLienID(lienup.getLienID());
			lienhead.setLienReference(lienup.getLienReference());
			lienhead.setId(lienup.getId());
			lienhead.setSource(lienup.getSource());
			fd.setLienHeader(lienhead);

			lienService.save(fd);
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		try {
			txStatus = transactionManager.getTransaction(txDef);

			lienUploadDAO.updateRejectStatus(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
		uploadProcess(UploadTypes.LIEN.name(), this, "Lien");

	}

	@Override
	public String getSqlQuery() {
		return lienUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		LienUpload details = (LienUpload) ObjectUtil.valueAsObject(record, LienUpload.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		doValidate(header, details);

		updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

	private void setError(LienUpload detail, LienUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Autowired
	public void setLienuploaddao(LienUploadDAO lienuploaddao) {
		this.lienUploadDAO = lienuploaddao;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setLienDetailsDAO(LienDetailsDAO lienDetailsDAO) {
		this.lienDetailsDAO = lienDetailsDAO;
	}

	@Autowired
	public void setLienHeaderDAO(LienHeaderDAO lienHeaderDAO) {
		this.lienHeaderDAO = lienHeaderDAO;
	}

	@Autowired
	public void setLienService(LienService lienService) {
		this.lienService = lienService;
	}

}