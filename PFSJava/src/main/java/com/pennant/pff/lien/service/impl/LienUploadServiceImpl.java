package com.pennant.pff.lien.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class LienUploadServiceImpl extends AUploadServiceImpl<LienUpload> {
	private static final Logger logger = LogManager.getLogger(LienUploadServiceImpl.class);

	private LienUploadDAO lienUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private LienDetailsDAO lienDetailsDAO;
	private LienHeaderDAO lienHeaderDAO;

	public LienUploadServiceImpl() {
		super();
	}

	@Override
	protected LienUpload getDetail(Object object) {
		if (object instanceof LienUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		LienUpload detail = getDetail(object);

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

		List<LienDetails> lu = lienDetailsDAO.getLienDtlsByRefAndAcc(detail.getReference(), detail.getAccNumber(),
				true);
		boolean isExists = false;

		if (detail.getAction().equals("Y")) {
			isExists = true;
		}

		for (LienDetails lienDetails : lu) {
			if (lienDetails != null && lienDetails.getMarking() != null) {
				if (detail.getAction().equals("Y") && lienDetails.isLienStatus()) {
					setError(detail, LienUploadError.LUOU_110, String.valueOf(lienDetails.getLienID()));
					return;
				}

				if (detail.getAction().equals("N") && !lienDetails.isLienStatus()) {
					setError(detail, LienUploadError.LUOU_110, String.valueOf(lienDetails.getLienID()));
					return;
				}

				if (detail.getAction().equals("N") && detail.getReference().equals(lienDetails.getReference())) {
					isExists = true;
				}
			}
		}

		if (!isExists) {
			setError(detail, LienUploadError.LUOU_114);
			return;
		}

		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			Date appDate = SysParamUtil.getAppDate();

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

				Set<String> references = lienUploadsMap.keySet();

				int sucessRecords = 0;
				int failRecords = 0;

				FinanceMain fm = null;

				for (String finReference : references) {
					List<LienUpload> lienUploads = lienUploadsMap.get(finReference);

					fm = financeMainDAO.getFinanceMain(finReference, header.getEntityCode());

					if (fm != null && !fm.isFinIsActive()) {
						for (LienUpload lienUpload : lienUploads) {
							setFailureStatus(lienUpload, "Fin Reference is not active.");
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
						header.getUploadDetails().add(luUpload);
					}

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);
				}

				logger.info("Processed the File {}", header.getFileName());

				lienUploadDAO.updateStatus(lienUploadList);
			}

			try {
				updateHeader(headers, true);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
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

			String accNumber = lienup.getAccNumber();
			LienHeader lienheader = lienHeaderDAO.getLienByAccAndStatus(accNumber, true);
			boolean isNew = false;

			TransactionStatus txStatus = getTransactionStatus();
			try {

				if (lienheader != null) {
					lienup.setLienID(lienheader.getLienID());
					lienup.setLienReference(lienheader.getLienReference());
				} else {
					lienheader = new LienHeader();
					isNew = true;
				}

				FinanceMain fm = lienup.getFinanceMain();
				fm.setFinSourceID(RequestSource.UPLOAD.name());

				FinanceDetail fd = new FinanceDetail();
				fd.getFinScheduleData().setFinanceMain(fm);
				Mandate mandate = new Mandate();
				mandate.setAccNumber(lienup.getAccNumber());
				fd.setMandate(mandate);
				fd.setLienHeader(lienheader);

				if (lienup.getAction().equals("Y")) {
					lienup.setLienstatus(true);
					lienup.setMarking(Labels.getLabel("label_Lien_Type_Manual"));
					lienup.setMarkingDate(header.getAppDate());
					lienup.setMarkingReason(header.getRemarks());
					lienup.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));

					lienheader.setMarking(Labels.getLabel("label_Lien_Type_Manual"));
					lienheader.setMarkingDate(header.getAppDate());
					lienheader.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
					lienheader.setLienStatus(true);
					lienheader.setAccountNumber(lienup.getAccNumber());
					lienheader.setReference(lienup.getReference());
				} else {
					lienup.setLienstatus(false);
					lienup.setDemarkingReason(header.getRemarks());
					lienup.setDemarking(Labels.getLabel("label_Lien_Type_Manual"));
					lienup.setDemarkingDate(header.getAppDate());
					lienup.setReference(lienup.getReference());
					lienup.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));

					List<LienDetails> lienDetail = lienDetailsDAO.getLienListByLienId(lienheader.getLienID());
					boolean isAllInActive = true;
					for (LienDetails lu : lienDetail) {
						if (lu.getReference().equals(fm.getFinReference())) {
							LienDetails liendeatils = getLienDetails(header, lienup, lu);
							lienDetailsDAO.update(liendeatils);
						}
						if (lu.isLienStatus()) {
							isAllInActive = false;
						}
					}

					if (isAllInActive) {
						lienheader.setDemarking(Labels.getLabel("label_Lien_Type_Manual"));
						lienheader.setDemarkingDate(header.getAppDate());
						lienheader.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
						lienheader.setLienStatus(false);
					}
				}

				lienUploadDAO.update(lienup, lienup.getId());

				lienheader.setLienID(lienup.getLienID());
				lienheader.setLienReference(lienup.getLienReference());

				lienheader.setSource(lienup.getSource());
				lienheader.setAccountNumber(lienup.getAccNumber());
				lienheader.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
				LienDetails lu = lienDetailsDAO.getLienByHeaderId(lienheader.getId());
				lu = getLienDetails(header, lienup, lu);

				lienheader.setId(lienup.getId());
				lu.setHeaderID(lienup.getId());
				if (isNew) {
					lienHeaderDAO.save(lienheader);
				} else {
					lienHeaderDAO.update(lienheader);
				}

				if (lienup.getAction().equals("Y")) {
					if (lienheader.isLienStatus()) {
						lienDetailsDAO.save(lu);
					}
				}

				Map<String, String> map = new HashMap<>();
				map.put("Lien ID", String.valueOf(lu.getLienID()));
				lienup.setExtendedFields(map);

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			}
		}
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(lienUploadDAO.getDetails(h1.getId()));
			});

			lienUploadDAO.updateRejectStatus(headerIdList, REJECT_CODE, REJECT_DESC);

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
		uploadProcess(UploadTypes.LIEN.name(), this, "Lien");
	}

	@Override
	public String getSqlQuery() {
		return lienUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		LienUpload detail = (LienUpload) ObjectUtil.valueAsObject(paramSource, LienUpload.class);

		detail.setReference(ObjectUtil.valueAsString(paramSource.getValue("reference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		detail.setHeaderId(header.getId());
		detail.setAppDate(header.getAppDate());

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		header.getUploadDetails().add(detail);

		logger.debug(Literal.LEAVING);
	}

	private void setError(LienUpload detail, LienUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	private void setError(LienUpload detail, LienUploadError error, String arg) {
		setFailureStatus(detail, error.name(), String.format(error.description(), arg));
	}

	private LienDetails getLienDetails(FileUploadHeader header, LienUpload lienup, LienDetails lu) {

		if (lu == null) {
			lu = new LienDetails();
		}

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		if (lienup.getAction().equals("Y")) {
			lu.setMarking(Labels.getLabel("label_Lien_Type_Manual"));
			lu.setMarkingDate(SysParamUtil.getAppDate());
			lu.setMarkingReason(lienup.getRemarks());
			lu.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
			lu.setLienStatus(true);
		} else {
			lu.setDemarking(Labels.getLabel("label_Lien_Type_Manual"));
			lu.setDemarkingDate(SysParamUtil.getAppDate());
			lu.setDemarkingReason(lienup.getRemarks());
			lu.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
			lu.setLienStatus(false);
		}

		lu.setHeaderID(lienup.getId());
		lu.setLienID(lienup.getLienID());
		lu.setSource(lienup.getSource());
		lu.setReference(lienup.getReference());
		lu.setAccountNumber(lienup.getAccNumber());
		lu.setLienReference(lienup.getLienReference());
		lu.setVersion(1);
		lu.setCreatedBy(header.getCreatedBy());
		lu.setCreatedOn(currentTime);
		lu.setApprovedOn(currentTime);
		lu.setApprovedBy(header.getApprovedBy());
		lu.setLastMntBy(header.getLastMntBy());
		lu.setLastMntOn(currentTime);

		return lu;
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
}