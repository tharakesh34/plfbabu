package com.pennant.pff.noc.upload.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NOCConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.upload.dao.BlockAutoGenLetterUploadDAO;
import com.pennant.pff.noc.upload.error.BlockAutoGenLetterUploadError;
import com.pennant.pff.noc.upload.model.BlockAutoGenLetterUpload;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class BlockAutoGenLetterUploadServiceImpl extends AUploadServiceImpl<BlockAutoGenLetterUpload> {
	private static final Logger logger = LogManager.getLogger(BlockAutoGenLetterUploadServiceImpl.class);

	private BlockAutoGenLetterUploadDAO blockAutoGenLetterUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;
	private AutoLetterGenerationDAO autoLetterGenerationDAO;

	public BlockAutoGenLetterUploadServiceImpl() {
		super();
	}

	protected BlockAutoGenLetterUpload getDetail(Object object) {
		if (object instanceof BlockAutoGenLetterUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		BlockAutoGenLetterUpload detail = getDetail(object);

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		if (StringUtils.isBlank(reference)) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_01);
			return;
		}

		Long finID = financeMainDAO.getFinID(reference);

		if (finID == null) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_01);
			return;
		}

		detail.setReferenceID(finID);

		String action = detail.getAction();

		if (StringUtils.isBlank(action)) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_05);
			return;
		}

		validateAction(detail, action);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<BlockAutoGenLetterUpload> details = blockAutoGenLetterUploadDAO.getDetails(header.getId());
				header.getUploadDetails().addAll(details);

				header.setAppDate(appDate);

				for (BlockAutoGenLetterUpload detail : details) {
					detail.setCreatedOn(header.getCreatedOn());
					detail.setCreatedBy(header.getCreatedBy());
					detail.setApprovedOn(header.getApprovedOn());
					detail.setApprovedBy(header.getApprovedBy());

					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						setFailureStatus(detail);
					} else {
						setSuccesStatus(detail);
						process(detail);
					}
				}

				try {
					blockAutoGenLetterUploadDAO.update(details);

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
				h1.getUploadDetails().addAll(blockAutoGenLetterUploadDAO.getDetails(h1.getId()));
			});

			blockAutoGenLetterUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

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
		return blockAutoGenLetterUploadDAO.getSqlQuery();
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.BLOCK_AUTO_GEN_LTR.name(), this, "BlockAutoGenLetterUploadHeader");
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		BlockAutoGenLetterUpload genLetter = (BlockAutoGenLetterUpload) ObjectUtil.valueAsObject(paramSource,
				BlockAutoGenLetterUpload.class);

		genLetter.setReference(ObjectUtil.valueAsString(paramSource.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		genLetter.setHeaderId(header.getId());
		genLetter.setAppDate(header.getAppDate());

		doValidate(header, genLetter);

		updateProcess(header, genLetter, paramSource);

		header.getUploadDetails().add(genLetter);

		logger.debug(Literal.LEAVING);
	}

	private void process(BlockAutoGenLetterUpload detail) {
		TransactionStatus txnStatus = getTransactionStatus();

		try {
			if (blockAutoGenLetterUploadDAO.isValidateAction(detail.getReferenceID())) {
				blockAutoGenLetterUploadDAO.delete(detail.getReferenceID());
				saveLetterGenarationDetails(detail.getReferenceID());
			}

			if (NOCConstants.BLOCK.equals(detail.getAction())) {
				blockAutoGenLetterUploadDAO.save(detail);
			}

			blockAutoGenLetterUploadDAO.savebyLog(detail);

			transactionManager.commit(txnStatus);
		} catch (Exception e) {
			if (txnStatus != null) {
				transactionManager.rollback(txnStatus);
			}

			setFailureStatus(detail, e.getMessage());
		}
	}

	private void saveLetterGenarationDetails(Long referenceID) {
		FinanceMain fm = blockAutoGenLetterUploadDAO.getFinanceMain(referenceID, TableType.MAIN_TAB);

		if (fm.getClosingStatus() == null) {
			return;
		}

		Date appDate = SysParamUtil.getAppDate();

		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingDAO.getLetterMapping(fm.getFinType());

		String closureType = getClosureType(fm.getClosingStatus());

		for (LoanTypeLetterMapping ltlp : letterMapping) {

			LetterType letterType = LetterType.getType(ltlp.getLetterType());

			if ((letterType == LetterType.CANCELLATION
					&& FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus()))
					|| (letterType == LetterType.NOC || letterType == LetterType.CLOSURE)
							&& (ClosureType.isClosure(closureType) || ClosureType.isForeClosure(closureType))) {
				if (!blockAutoGenLetterUploadDAO.isLetterInitiated(referenceID, ltlp.getLetterType())) {
					GenerateLetter gl = new GenerateLetter();
					gl.setFinID(referenceID);
					gl.setRequestType("D");
					gl.setLetterType(ltlp.getLetterType());
					gl.setCreatedDate(appDate);
					gl.setCreatedBy(ltlp.getCreatedBy());
					gl.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					gl.setAgreementTemplate(ltlp.getAgreementCodeId());
					gl.setEmailTemplate(ltlp.getEmailTemplateId());
					gl.setModeofTransfer(ltlp.getLetterMode());

					autoLetterGenerationDAO.save(gl);
				}
			}
		}
	}

	private String getClosureType(String closingStatus) {
		String closureType = "";
		switch (closingStatus) {
		case FinanceConstants.CLOSE_STATUS_MATURED, FinanceConstants.CLOSE_STATUS_EARLYSETTLE: {
			closureType = ClosureType.CLOSURE.code();
			break;
		}
		case FinanceConstants.CLOSE_STATUS_CANCELLED: {
			closureType = ClosureType.CANCEL.code();
			break;
		}

		default:
		}
		return closureType;
	}

	private void validateAction(BlockAutoGenLetterUpload detail, String action) {
		if (!(NOCConstants.BLOCK.equals(action) || NOCConstants.REMOVE_BLOCK.equals(action))) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_02);
			return;
		}

		if (NOCConstants.BLOCK.equals(action)
				&& blockAutoGenLetterUploadDAO.isValidateAction(detail.getReferenceID())) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_03);
			return;
		}

		if (NOCConstants.REMOVE_BLOCK.equals(action)
				&& !blockAutoGenLetterUploadDAO.isValidateAction(detail.getReferenceID())) {
			setError(detail, BlockAutoGenLetterUploadError.BALG_04);
		}
	}

	private void setError(BlockAutoGenLetterUpload detail, BlockAutoGenLetterUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	@Autowired
	public void setBlockAutoLetterGenerateUploadDAO(BlockAutoGenLetterUploadDAO blockAutoLetterGenerateUploadDAO) {
		this.blockAutoGenLetterUploadDAO = blockAutoLetterGenerateUploadDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setLoanTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
	}

	@Autowired
	public void setAutoLetterGenerationDAO(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
	}
}