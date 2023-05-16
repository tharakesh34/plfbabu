package com.pennant.pff.noc.upload.service.impl;

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
import com.pennant.backend.util.NOCConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.noc.upload.dao.BlockAutoGenLetterUploadDAO;
import com.pennant.pff.noc.upload.error.BlockAutoGenLetterUploadError;
import com.pennant.pff.noc.upload.model.BlockAutoGenLetterUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class BlockAutoGenLetterUploadServiceImpl extends AUploadServiceImpl<BlockAutoGenLetterUpload> {
	private static final Logger logger = LogManager.getLogger(BlockAutoGenLetterUploadServiceImpl.class);

	private BlockAutoGenLetterUploadDAO blockAutoGenLetterUploadDAO;
	private FinanceMainDAO financeMainDAO;

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

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());

				int sucessRecords = 0;
				int failRecords = 0;

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

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}

					header.getUploadDetails().add(detail);
				}

				try {
					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

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
}