package com.pennant.pff.bulkfeewaiverupload.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
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

public class BulkFeeWaiverUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(BulkFeeWaiverUploadServiceImpl.class);

	private BulkFeeWaiverUploadDAO bulkFeeWaiverUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private FeeWaiverUploadHeaderService feeWaiverUploadHeaderService;
	private FeeWaiverHeaderService feeWaiverHeaderService;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BulkFeeWaiverUpload detail = null;

		if (object instanceof BulkFeeWaiverUpload) {
			detail = (BulkFeeWaiverUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

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

		prepare(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;
			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());
				List<BulkFeeWaiverUpload> details = bulkFeeWaiverUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (BulkFeeWaiverUpload detail : details) {
					detail.setUserDetails(header.getUserDetails());
					detail.setAppDate(appDate);

					doValidate(header, detail);

					if (EodConstants.PROGRESS_SUCCESS == detail.getProgress()) {
						AuditHeader ah = getAuditHeader(prepare(detail), PennantConstants.TRAN_WF);

						txStatus = transactionManager.getTransaction(txDef);

						AuditHeader few = feeWaiverHeaderService.doApprove(ah);

						if (few.getErrorMessage() != null) {
							detail.setProgress(EodConstants.PROGRESS_FAILED);
							detail.setErrorDesc(few.getErrorMessage().get(0).getMessage().toString());
							detail.setErrorCode(few.getErrorMessage().get(0).getCode().toString());
						} else {
							detail.setProgress(EodConstants.PROGRESS_SUCCESS);
							detail.setErrorDesc("");
							detail.setErrorCode("");
						}

						transactionManager.commit(txStatus);
					}

					if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
						failRecords++;
					} else {
						sucessRecords++;
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
					}
				}

				try {
					txStatus = transactionManager.getTransaction(txDef);

					bulkFeeWaiverUploadDAO.update(details);

					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					updateHeader(headers, true);

					logger.info("BulkFeeWaiver Process is Initiated");

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}
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

			bulkFeeWaiverUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
	public String getSqlQuery() {
		return bulkFeeWaiverUploadDAO.getSqlQuery();
	}

	private FeeWaiverHeader prepare(BulkFeeWaiverUpload detail) {
		FeeWaiverHeader fwh = feeWaiverHeaderService.getFeeWaiverByFinRef(prepareFWH(detail));

		if (!fwh.isAlwtoProceed()) {
			setError(detail, BulkFeeWaiverUploadError.FWU_005);
			return fwh;
		}

		boolean feeExists = false;
		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			if (fwd.getFeeTypeCode().equals(detail.getFeeTypeCode())) {
				feeExists = true;
				BigDecimal remainingFee = fwd.getReceivableAmount().subtract(fwd.getReceivedAmount());

				if (detail.getWaivedAmount().compareTo(remainingFee) > 0) {
					setError(detail, BulkFeeWaiverUploadError.FWU_006);
					return fwh;
				}
			}
		}

		if (!feeExists) {
			setError(detail, BulkFeeWaiverUploadError.FWU_007);
			return fwh;
		}

		String rcdMaintainSts = detail.getFinanceMain().getRcdMaintainSts();
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode("FWU_999");
			detail.setErrorDesc(Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts));
			return fwh;
		}

		prepareFWD(fwh, detail);

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");

		return fwh;
	}

	private FeeWaiverHeader prepareFWH(BulkFeeWaiverUpload detail) {
		FeeWaiverHeader fwh = new FeeWaiverHeader();
		LoggedInUser userDetails = detail.getUserDetails();
		if (userDetails == null) {
			userDetails = new LoggedInUser();
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

		BigDecimal amount = detail.getWaivedAmount();

		if (!FinServiceEvent.FEEWAIVERS.equals(fm.getRcdMaintainSts())) {
			fwh.setNewRecord(true);
		}

		fwh.setFinReference(fm.getFinReference());

		loadFWDList(fwh, detail);

		for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
			String feetypecode = StringUtils.trimToEmpty(detail.getFeeTypeCode());
			if (feetypecode.equals(StringUtils.trimToEmpty(fwd.getFeeTypeCode()))) {
				fwd.setLastMntOn(fwh.getLastMntOn());
				fwd.setCurrWaiverAmount(amount);
				if (amount.compareTo(BigDecimal.ZERO) == 0) {
					fwd.setCurrWaiverGST(BigDecimal.ZERO);
					fwd.setCurrActualWaiver(BigDecimal.ZERO);
				}
				prepareGST(fwd, amount, taxPercentages);
				fwd.setBalanceAmount(fwd.getReceivableAmount().subtract(fwd.getCurrWaiverAmount()));
				break;
			}
		}
	}

	private void loadFWDList(FeeWaiverHeader fwh, BulkFeeWaiverUpload detail) {
		List<FeeWaiverDetail> fwdList = new ArrayList<>();

		if (fwh.isNewRecord()) {
			for (FeeWaiverDetail fwd : fwh.getFeeWaiverDetails()) {
				if (fwd.getBalanceAmount() != null && fwd.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0) {
					if (fwd.getFeeTypeCode().equals(detail.getFeeTypeCode().trim())) {
						fwd.setWaivedAmount(detail.getWaivedAmount());
						fwd.setFeeTypeCode(detail.getFeeTypeCode());
						fwdList.add(fwd);
					}
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
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.FEE_WAIVER.name(), this, "BulkFeeWaiverUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		BulkFeeWaiverUpload bfee = new BulkFeeWaiverUpload();

		bfee.setHeaderId(headerID);
		bfee.setAppDate(SysParamUtil.getAppDate());
		bfee.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));
		bfee.setFeeTypeCode(ObjectUtil.valueAsString(record.getValue("feeTypeCode")));
		bfee.setWaivedAmount(ObjectUtil.valueAsBigDecimal(record.getValue("waivedAmount")));

		doValidate(header, bfee);

		updateProcess(header, bfee, record);

		List<BulkFeeWaiverUpload> details = new ArrayList<>();
		details.add(bfee);

		bulkFeeWaiverUploadDAO.update(details);

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
	public void setfeeWaiverUploadHeaderService(FeeWaiverUploadHeaderService feeWaiverUploadHeaderService) {
		this.feeWaiverUploadHeaderService = feeWaiverUploadHeaderService;
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
