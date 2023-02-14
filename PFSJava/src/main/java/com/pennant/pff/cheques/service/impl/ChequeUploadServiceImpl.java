package com.pennant.pff.cheques.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.pdc.ChequeHeaderDAO;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.pdc.upload.ChequeUpload;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.cheques.dao.ChequeUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;

public class ChequeUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(ChequeUploadServiceImpl.class);

	private ChequeUploadDAO chequeUploadDAO;
	private ChequeHeaderService chequeHeaderService;
	private BankBranchService bankBranchService;
	private FinanceMainDAO financeMainDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private ChequeHeaderDAO chequeHeaderDAO;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Map<String, List<ChequeUpload>> map = new HashMap<>();

			for (FileUploadHeader header : headers) {
				List<ChequeUpload> details = chequeUploadDAO.getDetails(header.getId());

				header.setTotalRecords(details.size());
				for (ChequeUpload detail : details) {
					String finReference = detail.getReference();
					if (map.containsKey(finReference)) {
						List<ChequeUpload> list = map.get(finReference);
						list.add(detail);
					} else {
						List<ChequeUpload> list = new ArrayList<>();

						list.add(detail);
						map.put(finReference, list);
					}
				}

				Set<String> finReferences = map.keySet();

				int sucessRecords = 0;
				int failRecords = 0;

				for (String finReference : finReferences) {
					List<ChequeUpload> chequeUploads = map.get(finReference);

					Long finID = financeMainDAO.getActiveFinID(finReference);

					ChequeHeader chequeHeader = chequeHeaderService.getChequeHeaderByRef(finID);

					if (chequeHeader == null) {
						ErrorDetail error = ErrorUtil.getError("90502", "Cheque Header ");
						setError(chequeUploads, error);
						continue;
					}

					chequeHeader.setUserDetails(header.getUserDetails());

					if (!CollectionUtils.isEmpty(chequeHeader.getChequeDetailList())) {
						chequeHeader.setBankBranchID(chequeHeader.getChequeDetailList().get(0).getBankBranchID());
						chequeHeader.setAccHolderName(chequeHeader.getChequeDetailList().get(0).getAccHolderName());
						chequeHeader.setAccountNo(chequeHeader.getChequeDetailList().get(0).getAccountNo());
						chequeHeader.setChequeSerialNo(chequeHeader.getChequeDetailList().get(0).getChequeSerialNo());
					}

					List<ChequeDetail> cheques = new ArrayList<>();

					List<ChequeDetail> addcheques = new ArrayList<>();
					List<ChequeDetail> delcheques = new ArrayList<>();

					for (ChequeUpload upload : chequeUploads) {
						upload.setReferenceID(finID);
						String action = upload.getAction();
						doValidate(header, upload);

						if (upload.getProgress() != EodConstants.PROGRESS_FAILED) {
							cheques.add(upload.getChequeDetail());
						}

						upload.getChequeDetail().setHeaderID(chequeHeader.getId());
						if (action.equals("A")) {
							addcheques.add(upload.getChequeDetail());
						} else {
							delcheques.add(upload.getChequeDetail());
						}
					}

					try {
						txStatus = transactionManager.getTransaction(txDef);

						if (!addcheques.isEmpty()) {
							chequeHeader.setChequeDetailList(addcheques);
							process(chequeHeader, chequeUploads);
						}

						if (!delcheques.isEmpty()) {
							chequeHeader.setChequeDetailList(delcheques);
							for (ChequeDetail detail : delcheques) {
								chequeDetailDAO.deleteCheques(detail);
							}

							chequeHeader.setNoOfCheques(chequeUploads.size() - delcheques.size());
							chequeHeaderDAO.updatesize(chequeHeader);
						}

						for (ChequeUpload chequeUpload : chequeUploads) {
							if (chequeUpload.getProgress() == EodConstants.PROGRESS_FAILED) {
								failRecords++;
							} else {
								sucessRecords++;
							}
						}

						chequeUploadDAO.update(chequeUploads);

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
			}
		}).start();

	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		try {
			txStatus = transactionManager.getTransaction(txDef);
			chequeUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

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
	public void doValidate(FileUploadHeader header, Object object) {
		ChequeUpload upload = null;

		if (object instanceof ChequeUpload) {
			upload = (ChequeUpload) object;
		}

		ChequeDetail cd = upload.getChequeDetail();

		String action = upload.getAction();

		if (!("A".equals(action) || "D".equals(action))) {
			upload.setProgress(EodConstants.PROGRESS_FAILED);
			upload.setErrorCode("999");
			upload.setErrorDesc("Action is invalid.");
		}

		String ifsc = cd.getIfsc();
		String micr = cd.getMicr();

		if (ifsc == null) {
			ErrorDetail error = ErrorUtil.getError("90502", "Cheque Header ");
			setError(upload, error);
			return;
		}

		if (micr == null) {
			ErrorDetail error = ErrorUtil.getError("90502", "Ifsc ");
			setError(upload, error);
			return;
		}

		BankBranch bankBranch = bankBranchService.getBankBranchByIFSCMICR(ifsc, micr);

		if (bankBranch == null) {
			ErrorDetail error = ErrorUtil.getError("90703", ifsc, micr);
			setError(upload, error);
			return;
		}

		cd.setBankBranchID(bankBranch.getBankBranchID());
	}

	@Override
	public String getSqlQuery() {
		return chequeUploadDAO.getSqlQuery();
	}

	private void process(ChequeHeader header, List<ChequeUpload> uploads) {
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData data = new FinScheduleData();

		fd.setFinScheduleData(data);
		header.setSourceId(RequestSource.UPLOAD.name());
		fd.setChequeHeader(header);

		fd.setFinReference(header.getFinReference());

		header.setNoOfCheques(header.getChequeDetailList().size());
		header.setChequeDetailList(header.getChequeDetailList());

		ErrorDetail error = chequeHeaderService.validateBasicDetails(fd, "");

		if (error != null) {
			setError(uploads, error);
			return;
		}

		error = chequeHeaderService.chequeValidationForUpdate(fd, PennantConstants.method_save, "");

		if (error != null) {
			setError(uploads, error);
			return;
		}

		error = chequeHeaderService.processChequeDetail(fd, "", header.getUserDetails());

		if (error != null) {
			setError(uploads, error);
			return;
		}

		for (ChequeUpload detail : uploads) {
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
			detail.setErrorCode("");
			detail.setErrorDesc("");
		}

	}

	private void setError(ChequeUpload detail, ErrorDetail error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.getCode());
		detail.setErrorDesc(error.getError());
	}

	private void setError(List<ChequeUpload> uploads, ErrorDetail error) {
		for (ChequeUpload detail : uploads) {
			setError(detail, error);
		}
	}

	@Autowired
	public void setChequeUploadDAO(ChequeUploadDAO chequeUploadDAO) {
		this.chequeUploadDAO = chequeUploadDAO;
	}

	@Autowired
	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setChequeHeaderDAO(ChequeHeaderDAO chequeHeaderDAO) {
		this.chequeHeaderDAO = chequeHeaderDAO;
	}

}