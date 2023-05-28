package com.pennant.pff.bulkfeewaiverupload.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FeeWaiverUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.bulkfeewaiverupload.dao.BulkFeeWaiverUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.model.bulkfeewaiverupload.BulkFeeWaiverUpload;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class BulkFeeWaiverUploadServiceImpl extends AUploadServiceImpl<BulkFeeWaiverUpload> {
	private static final Logger logger = LogManager.getLogger(BulkFeeWaiverUploadServiceImpl.class);

	private BulkFeeWaiverUploadDAO bulkFeeWaiverUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FeeWaiverUploadHeaderService feeWaiverUploadHeaderService;
	private FeeWaiverHeaderService feeWaiverHeaderService;

	public BulkFeeWaiverUploadServiceImpl() {
		super();
	}

	@Override
	protected BulkFeeWaiverUpload getDetail(Object object) {
		if (object instanceof BulkFeeWaiverUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BulkFeeWaiverUpload detail = getDetail(object);

		detail.setUserDetails(header.getUserDetails());

		logger.info("Validating the Data for the reference {}", detail.getReference());

		FinanceMain fm = financeMainDAO.getFinanceMain(detail.getReference(), header.getEntityCode());

		if (fm == null) {
			setError(detail, BulkFeeWaiverUploadError.FWU_001);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, BulkFeeWaiverUploadError.FWU_002);
			return;
		}

		detail.setReferenceID(fm.getFinID());
		detail.setFinanceMain(fm);

		FeeType fee = feeWaiverUploadHeaderService.getApprovedFeeTypeByFeeCode(detail.getFeeTypeCode());
		if (fee == null) {
			setError(detail, BulkFeeWaiverUploadError.FWU_003);
			return;
		}

		detail.setFeeTypeID(fee.getFeeTypeID());

		BigDecimal amount = detail.getWaivedAmount();
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			setError(detail, BulkFeeWaiverUploadError.FWU_004);
			return;
		}

		detail.setWaiverHeader(prepare(detail, header));
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());
				List<BulkFeeWaiverUpload> details = bulkFeeWaiverUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);

				for (BulkFeeWaiverUpload detail : details) {
					detail.setUserDetails(header.getUserDetails());
					detail.setAppDate(appDate);
					prepareUserDetails(header, detail);

					doValidate(header, detail);

					if (EodConstants.PROGRESS_SUCCESS == detail.getProgress()) {
						processWaiver(detail, header);
					}
				}

				try {
					bulkFeeWaiverUploadDAO.update(details);

					updateHeader(headers, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

		}).start();

		logger.debug(Literal.LEAVING);
	}

	private void processWaiver(BulkFeeWaiverUpload detail, FileUploadHeader header) {
		TransactionStatus txStatus = getTransactionStatus();
		AuditHeader ah = getAuditHeader(detail.getWaiverHeader(), PennantConstants.TRAN_WF);

		try {
			ah = feeWaiverHeaderService.doApprove(ah);
			transactionManager.commit(txStatus);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(detail, e.getMessage());
			return;
		}

		if (ah == null) {
			setFailureStatus(detail, "Audit Header is null.");
			return;
		}

		if (ah.getErrorMessage() != null) {
			setFailureStatus(detail, ah.getErrorMessage().get(0));
		}

		setSuccesStatus(detail);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(bulkFeeWaiverUploadDAO.getDetails(h1.getId()));
			});

			bulkFeeWaiverUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
		return bulkFeeWaiverUploadDAO.getSqlQuery();
	}

	private FeeWaiverHeader prepare(BulkFeeWaiverUpload detail, FileUploadHeader header) {
		FeeWaiverHeader fwh = feeWaiverHeaderService.getFeeWaiverByFinRef(prepareFWH(detail, header));

		if (!fwh.isAlwtoProceed()) {
			setError(detail, BulkFeeWaiverUploadError.FWU_005);
			return fwh;
		}

		boolean feeExists = false;
		BigDecimal amount = detail.getWaivedAmount();
		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			if (fwd.getFeeTypeCode().equals(detail.getFeeTypeCode())) {
				feeExists = true;

				BigDecimal remainingFee = fwd.getReceivableAmount().subtract(fwd.getReceivedAmount());
				if (amount.compareTo(remainingFee) > 0) {
					fwd.setCurrWaiverAmount(remainingFee);
					fwd.setWaivedAmount(remainingFee);
				} else {
					fwd.setCurrWaiverAmount(amount);
					fwd.setWaivedAmount(amount);
				}
				amount = amount.subtract(fwd.getWaivedAmount());
			}
		}

		if (amount.compareTo(BigDecimal.ZERO) != 0) {
			setError(detail, BulkFeeWaiverUploadError.FWU_006);
			return fwh;
		}

		if (!feeExists) {
			setError(detail, BulkFeeWaiverUploadError.FWU_007);
			return fwh;
		}

		String rcdMaintainSts = feeWaiverUploadHeaderService.getFinanceMainByRcdMaintenance(detail.getReferenceID());
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			setFailureStatus(detail, "FWU_999", Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return fwh;
		}

		prepareFWD(fwh, detail);

		setSuccesStatus(detail);

		return fwh;
	}

	private FeeWaiverHeader prepareFWH(BulkFeeWaiverUpload detail, FileUploadHeader header) {
		FeeWaiverHeader fwh = new FeeWaiverHeader();
		LoggedInUser userDetails = detail.getUserDetails();
		if (userDetails == null) {
			userDetails = new LoggedInUser();
			userDetails.setLoginUsrID(header.getApprovedBy());
			userDetails.setUserName(header.getApprovedByName());
		}

		fwh.setNewRecord(true);
		fwh.setFinID(detail.getReferenceID());
		fwh.setFinReference(detail.getReference());
		fwh.setEvent(FinServiceEvent.FEEWAIVERS);
		fwh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fwh.setFinSourceID(UploadConstants.FINSOURCE_ID_UPLOAD);
		fwh.setPostingDate(detail.getAppDate());
		fwh.setValueDate(detail.getAppDate());
		fwh.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fwh.setVersion(1);
		fwh.setUserDetails(userDetails);
		fwh.setLastMntBy(userDetails.getUserId());
		fwh.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		return fwh;
	}

	private void prepareFWD(FeeWaiverHeader fwh, BulkFeeWaiverUpload detail) {
		FinanceMain fm = detail.getFinanceMain();

		Map<String, BigDecimal> taxPercentages = fm.getTaxPercentages();
		if (MapUtils.isEmpty(taxPercentages)) {
			taxPercentages = GSTCalculator.getTaxPercentages(fm.getFinID());
		}

		if (!FinServiceEvent.FEEWAIVERS.equals(fm.getRcdMaintainSts())) {
			fwh.setNewRecord(true);
		}

		fwh.setFinReference(fm.getFinReference());

		loadFWDList(fwh, detail);

		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			String feetypecode = StringUtils.trimToEmpty(detail.getFeeTypeCode());
			BigDecimal amount = fwd.getWaivedAmount();
			if (feetypecode.equals(StringUtils.trimToEmpty(fwd.getFeeTypeCode()))) {
				fwd.setLastMntOn(fwh.getLastMntOn());
				if (amount.compareTo(BigDecimal.ZERO) == 0) {
					fwd.setCurrWaiverGST(BigDecimal.ZERO);
					fwd.setCurrActualWaiver(BigDecimal.ZERO);
				}
				prepareGST(fwd, amount, taxPercentages);
				fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
			}
		}
	}

	private void loadFWDList(FeeWaiverHeader fwh, BulkFeeWaiverUpload detail) {
		List<FeeWaiverDetail> fwdList = new ArrayList<>();

		if (fwh.isNewRecord()) {
			for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
				if (isValidRecord(detail, fwd)) {
					fwd.setFeeTypeCode(detail.getFeeTypeCode());
					fwdList.add(fwd);
				}
			}
		} else {
			for (FeeWaiverDetail feeWaiver : fwh.getFeeWaiverDetails()) {
				if (feeWaiver.getReceivableAmount().compareTo(BigDecimal.ZERO) > 0) {
					fwdList.add(feeWaiver);
				}
			}
		}

		fwh.setFeeWaiverDetails(fwdList);
	}

	private boolean isValidRecord(BulkFeeWaiverUpload detail, FeeWaiverDetail fwd) {
		return fwd.getBalanceAmount() != null && fwd.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0
				&& fwd.getFeeTypeCode().equals(detail.getFeeTypeCode().trim());
	}

	private void prepareGST(FeeWaiverDetail fwd, BigDecimal waiverAmount, Map<String, BigDecimal> gstPercentages) {
		if (fwd.isTaxApplicable()) {
			TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(waiverAmount, gstPercentages);
			GSTCalculator.calculateActualGST(fwd, taxSplit, gstPercentages);

			fwd.setCurrActualWaiver(waiverAmount.subtract(taxSplit.gettGST()));
			fwd.setCurrWaiverGST(taxSplit.gettGST());
		} else {
			fwd.setWaivedAmount(waiverAmount);
			fwd.setCurrWaiverAmount(waiverAmount);
			fwd.setCurrActualWaiver(waiverAmount);
			fwd.setCurrWaiverGST(BigDecimal.ZERO);
		}
	}

	private AuditHeader getAuditHeader(FeeWaiverHeader fwh, String tranType) {
		return new AuditHeader(fwh.getFinReference(), null, null, null, new AuditDetail(tranType, 1, null, fwh),
				fwh.getUserDetails(), null);
	}

	private void setError(BulkFeeWaiverUpload detail, BulkFeeWaiverUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.FEE_WAIVER.name(), this, "BulkFeeWaiverUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		BulkFeeWaiverUpload bfee = new BulkFeeWaiverUpload();

		bfee.setHeaderId(headerID);
		bfee.setAppDate(SysParamUtil.getAppDate());
		bfee.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));
		bfee.setFeeTypeCode(ObjectUtil.valueAsString(paramSource.getValue("feeTypeCode")));
		bfee.setWaivedAmount(ObjectUtil.valueAsBigDecimal(paramSource.getValue("waivedAmount")));

		doValidate(header, bfee);

		updateProcess(header, bfee, paramSource);

		List<BulkFeeWaiverUpload> details = new ArrayList<>();
		details.add(bfee);

		bulkFeeWaiverUploadDAO.update(details);

		header.getUploadDetails().add(bfee);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setBulkFeeWaiverUploadDAO(BulkFeeWaiverUploadDAO feeWaiverUploadDAO) {
		this.bulkFeeWaiverUploadDAO = feeWaiverUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFeeWaiverUploadHeaderService(FeeWaiverUploadHeaderService feeWaiverUploadHeaderService) {
		this.feeWaiverUploadHeaderService = feeWaiverUploadHeaderService;
	}

	@Autowired
	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

}
