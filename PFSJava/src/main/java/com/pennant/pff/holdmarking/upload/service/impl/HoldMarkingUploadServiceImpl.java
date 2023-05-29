package com.pennant.pff.holdmarking.upload.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.holdmarking.model.HoldMarkingDetail;
import com.pennant.pff.holdmarking.model.HoldMarkingHeader;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingDetailDAO;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingHeaderDAO;
import com.pennant.pff.holdmarking.upload.dao.HoldMarkingUploadDAO;
import com.pennant.pff.holdmarking.upload.error.HoldMarkingUploadError;
import com.pennant.pff.holdmarking.upload.model.HoldMarkingUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class HoldMarkingUploadServiceImpl extends AUploadServiceImpl<HoldMarkingUpload> {
	private static final Logger logger = LogManager.getLogger(HoldMarkingUploadServiceImpl.class);

	private HoldMarkingUploadDAO holdMarkingUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private HoldMarkingDetailDAO holdMarkingDetailDAO;
	private HoldMarkingHeaderDAO holdMarkingHeaderDAO;

	public HoldMarkingUploadServiceImpl() {
		super();
	}

	protected HoldMarkingUpload getDetail(Object object) {
		if (object instanceof HoldMarkingUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		HoldMarkingUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		if (StringUtils.isBlank(reference)) {
			setError(detail, HoldMarkingUploadError.HM_01);
			return;
		}

		Long finID = financeMainDAO.getFinID(reference);

		if (finID == null) {
			setError(detail, HoldMarkingUploadError.HM_01);
			return;
		}

		detail.setReferenceID(finID);

		String type = detail.getType();

		if (StringUtils.isBlank(type)) {
			setError(detail, HoldMarkingUploadError.HM_05);
			return;
		}

		BigDecimal amount = detail.getAmount();

		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			setError(detail, HoldMarkingUploadError.HM_06);
			return;
		}

		validateType(detail, type);

		if (detail.getProgress() != EodConstants.PROGRESS_FAILED) {
			setSuccesStatus(detail);
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<HoldMarkingUpload> details = holdMarkingUploadDAO.getDetails(header.getId());

				header.getUploadDetails().addAll(details);
				header.setAppDate(appDate);

				for (HoldMarkingUpload detail : details) {
					detail.setCreatedOn(header.getCreatedOn());
					detail.setCreatedBy(header.getCreatedBy());
					detail.setApprovedOn(header.getApprovedOn());
					detail.setApprovedBy(header.getApprovedBy());

					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						setFailureStatus(detail);
					} else {
						setSuccesStatus(detail);

						try {
							processHoldMarking(detail);
							setSuccesStatus(detail);
						} catch (Exception e) {
							setFailureStatus(detail, e.getMessage());
						}
					}
				}

				try {
					holdMarkingUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

		}).start();
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(holdMarkingUploadDAO.getDetails(h1.getId()));
			});

			holdMarkingUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
	public String getSqlQuery() {
		return holdMarkingUploadDAO.getSqlQuery();
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.HOLD_MARKING.name(), this, "HoldMarkingUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		HoldMarkingUpload holdMarking = (HoldMarkingUpload) ObjectUtil.valueAsObject(paramSource,
				HoldMarkingUpload.class);

		holdMarking.setReference(ObjectUtil.valueAsString(paramSource.getValue("reference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		holdMarking.setHeaderId(header.getId());
		holdMarking.setAppDate(header.getAppDate());

		doValidate(header, holdMarking);

		header.getUploadDetails().add(holdMarking);

		updateProcess(header, holdMarking, paramSource);

		logger.debug(Literal.LEAVING);
	}

	private void processHoldMarking(HoldMarkingUpload detail) {
		int count = 0;

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		Date appDate = SysParamUtil.getAppDate();
		long userId = 1000;

		if (PennantConstants.HOLD_MARKING.equals(detail.getType())) {
			HoldMarkingHeader hmh = new HoldMarkingHeader();
			hmh.setFinReference(detail.getReference());
			hmh.setFinID(detail.getReferenceID());
			hmh.setAccountNumber(detail.getAccountNumber());
			hmh.setHoldAmount(detail.getAmount());
			hmh.setBalance(detail.getAmount());

			long headerId = holdMarkingHeaderDAO.saveHeader(hmh);

			HoldMarkingDetail hmd = new HoldMarkingDetail();
			hmd.setHoldID(hmh.getHoldID());
			hmd.setHeaderID(headerId);
			hmd.setFinReference(detail.getReference());
			hmd.setFinID(detail.getReferenceID());
			hmd.setHoldType(detail.getType());
			hmd.setMarking(PennantConstants.MANUAL_ASSIGNMENT);
			hmd.setMovementDate(appDate);
			hmd.setStatus(InsuranceConstants.PENDING);
			hmd.setAmount(detail.getAmount());
			hmd.setLogID(++count);
			hmd.setHoldReleaseReason(detail.getRemarks());
			hmd.setCreatedBy(userId);
			hmd.setCreatedOn(currentTime);
			hmd.setLastMntBy(userId);
			hmd.setLastMntOn(currentTime);
			hmd.setApprovedOn(currentTime);
			hmd.setApprovedBy(userId);

			holdMarkingDetailDAO.saveDetail(hmd);

			Map<String, String> map = new HashMap<>();
			if (hmd != null) {
				map.put("Hold ID", String.valueOf(hmd.getHoldID()));
				detail.setExtendedFields(map);
			}

		} else {
			List<HoldMarkingHeader> list = holdMarkingHeaderDAO.getHoldByFinId(detail.getReferenceID(),
					detail.getAccountNumber());

			if (list.isEmpty()) {
				logger.debug(Literal.LEAVING);
				return;
			}

			if (CollectionUtils.isNotEmpty(list)) {
				list = list.stream().sorted((l1, l2) -> Long.compare(l1.getHoldID(), l2.getHoldID()))
						.collect(Collectors.toList());
			}

			for (HoldMarkingHeader headerList : list) {

				BigDecimal detailAmount = detail.getAmount();

				if (detailAmount.compareTo(BigDecimal.ZERO) > 0) {

					if (detailAmount.compareTo(headerList.getBalance()) > 0) {
						headerList.setBalance(BigDecimal.ZERO);
						headerList.setReleaseAmount(headerList.getHoldAmount());

						detailAmount = detailAmount.subtract(headerList.getHoldAmount());
					} else {
						headerList.setBalance(headerList.getBalance().subtract(detailAmount));
						headerList.setReleaseAmount(headerList.getReleaseAmount().add(detailAmount));

						detailAmount = BigDecimal.ZERO;
					}

					holdMarkingHeaderDAO.updateHeader(headerList);
				}

			}

			HoldMarkingHeader hmh = new HoldMarkingHeader();

			if (CollectionUtils.isNotEmpty(list)) {
				hmh = list.stream().sorted((l1, l2) -> Long.compare(l1.getHoldID(), l2.getHoldID()))
						.collect(Collectors.toList()).get(0);
			}

			count = holdMarkingDetailDAO.getCountId(hmh.getHoldID());

			HoldMarkingDetail hmd = new HoldMarkingDetail();
			hmd.setHoldID(hmh.getHoldID());
			hmd.setHeaderID(hmh.getId());
			hmd.setFinReference(detail.getReference());
			hmd.setFinID(detail.getReferenceID());
			hmd.setHoldType(detail.getType());
			hmd.setMarking(PennantConstants.MANUAL_ASSIGNMENT);
			hmd.setMovementDate(appDate);
			hmd.setStatus(InsuranceConstants.PENDING);
			hmd.setAmount(detail.getAmount());
			hmd.setLogID(++count);
			hmd.setHoldReleaseReason(detail.getRemarks());
			hmd.setCreatedBy(userId);
			hmd.setCreatedOn(currentTime);
			hmd.setLastMntBy(userId);
			hmd.setLastMntOn(currentTime);
			hmd.setApprovedOn(currentTime);
			hmd.setApprovedBy(userId);

			holdMarkingDetailDAO.saveDetail(hmd);

		}

	}

	private void validateType(HoldMarkingUpload detail, String type) {
		if (!(PennantConstants.HOLD_MARKING.equals(type) || PennantConstants.REMOVE_HOLD_MARKING.equals(type))) {
			setError(detail, HoldMarkingUploadError.HM_02);
			return;
		}

		if (PennantConstants.REMOVE_HOLD_MARKING.equals(type)
				&& !(holdMarkingUploadDAO.isValidateType(detail.getReferenceID(), detail.getAccountNumber()))) {
			setError(detail, HoldMarkingUploadError.HM_03);
			return;
		}
	}

	private void setError(HoldMarkingUpload detail, HoldMarkingUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Autowired
	public void setBlockAutoLetterGenerateUploadDAO(HoldMarkingUploadDAO holdMarkingUploadDAO) {
		this.holdMarkingUploadDAO = holdMarkingUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setHoldMarkingDetailDAO(HoldMarkingDetailDAO holdMarkingDetailDAO) {
		this.holdMarkingDetailDAO = holdMarkingDetailDAO;
	}

	@Autowired
	public void setHoldMarkingHeaderDAO(HoldMarkingHeaderDAO holdMarkingHeaderDAO) {
		this.holdMarkingHeaderDAO = holdMarkingHeaderDAO;
	}
}