package com.pennant.pff.receipt.validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.MasterDefUtil;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.document.DocVerificationUtil;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennant.pff.receipt.dao.CreateReceiptUploadDAO;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class CreateReceiptUploadDataValidator implements ValidateRecord {
	private static final Logger logger = LogManager.getLogger(CreateReceiptUploadDataValidator.class);

	private CreateReceiptUploadDAO createReceiptUploadDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinanceMainDAO financeMainDAO;
	private ReceiptService receiptService;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;

	public CreateReceiptUploadDataValidator() {
		super();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		// TODO Auto-generated method stub

	}

	public void validate(CreateReceiptUpload rud, FileUploadHeader header) {
		prepareKeys(rud);

		String reference = rud.getReference();
		rud.setReferenceID(financeMainDAO.getFinIDByFinReference(reference, "", false));

		isFileExists(rud);

		if (StringUtils.isBlank(reference)) {
			setError(rud, "Blanks/Nulls in [REFERENCE] ");
			return;
		}

		String purpose = rud.getReceiptPurpose();
		if (StringUtils.isBlank(purpose)) {
			setError(rud, "Values other than SP/EP/ES in [RECEIPTPURPOSE] ");
			return;
		}

		if (FinanceConstants.EARLYSETTLEMENT.equals(purpose) || FinanceConstants.PARTIALSETTLEMENT.equals(purpose)) {
			if (StringUtils.isNotBlank(createReceiptUploadDAO.getLoanReference(reference, header.getFileName()))) {
				setError(rud, "90273", "Receipt In process for " + reference);
			}
		}

		if (FinanceConstants.EARLYSETTLEMENT.equals(purpose)) {
			if (finAdvancePaymentsDAO.getStatusCountByFinRefrence(rud.getReferenceID()) > 0) {
				setError(rud, "90508", rud.getReference());
			}
		}

		if (!"SP".equals(purpose) && !"EP".equals(purpose) && !"ES".equals(purpose)) {
			setError(rud, "Values other than SP/EP/ES in [RECEIPTPURPOSE] ");
			return;
		}

		String execssAdjust = rud.getExcessAdjustTo();
		if (StringUtils.isBlank(execssAdjust)) {
			execssAdjust = "E";
		}

		if (!"E".equals(execssAdjust) && !"T".equals(execssAdjust) && !"A".equals(execssAdjust)
				&& !"#".equals(execssAdjust)) {
			setError(rud, "Values other than E/A/ /# in [EXCESSADJUSTTO] ");
			return;
		} else {
			rud.setExcessAdjustTo(execssAdjust);
		}

		String allocType = rud.getAllocationType();
		if (StringUtils.isBlank(allocType)) {
			allocType = "A";
		}

		if (!"A".equals(allocType) && !"M".equals(allocType)) {
			setError(rud, "Values other than A/M in [ALLOCATIONTYPE] ");
			return;
		} else {
			rud.setAllocationType(allocType);
		}

		if ("M".equals(allocType) && !"SP".equals(rud.getReceiptPurpose())) {
			setError(rud, "Values other than A in [ALLOCATIONTYPE] ");
			return;
		}

		try {
			BigDecimal precisionAmount = rud.getReceiptAmount();
			precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);

			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				rud.setReceiptAmount(actualAmount);
				setError(rud, "Minor Currency (Decimals) in [RECEIPTAMOUNT] ");
				return;
			} else {
				rud.setReceiptAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) <= 0) {
				setError(rud, "[RECEIPTAMOUNT] with value <= 0");
				return;
			}
		} catch (Exception e) {
			rud.setReceiptAmount(BigDecimal.ZERO);
			setError(rud, "[RECEIPTAMOUNT] is not valid.");
			return;
		}

		if (rud.getRemarks().length() > 100) {
			setError(rud, "[REMARKS] with length more than 100 characters");
			return;
		}

		/*
		 * if (DateUtil.compare(rud.getReceivedDate(), rud.getValueDate()) < 0) { setError(rud,
		 * "[RECEIVEDDATE] should be greater than or equal to [VALUEDATE]."); return; }
		 */
		String receiptMode = rud.getReceiptMode();
		if (receiptMode.length() > 10) {
			setError(rud, "[RECEIPTMODE] with length more than 10 characters");
			return;
		}

		String status = rud.getReceiptModeStatus();
		if (ReceiptMode.CASH.equals(receiptMode) && RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {
			setError(rud, "[RECEIPTMODE] with Bounce Not allowed");
			return;
		}

		if (rud.getSubReceiptMode().length() > 11) {
			setError(rud, "[SUBRECEIPTMODE] with length more than 10 characters");
			return;
		}

		String channel = rud.getReceiptChannel();
		if (channel.length() > 10) {
			throw new AppException("RU0040", "[RECEIPTCHANNEL] with length more than 10 characters");
		}

		boolean bounceOrCheque = ReceiptMode.CHEQUE.equals(receiptMode) || ReceiptMode.DD.equals(receiptMode);

		if (StringUtils.isBlank(channel) && (ReceiptMode.CASH.equals(receiptMode) || bounceOrCheque)) {
			setError(rud, "Blanks in [RECEIPTCHANNEL]");
			return;
		}

		/*
		 * if (rud.getFundingAc().length() > 8) { setError(rud, "[FUNDINGAC] with length more than 8 characters");
		 * return; }
		 */
		if (rud.getPaymentRef().length() > 50) {
			setError(rud, "[PAYMENTREF] with length more than 50 characters");
			return;
		}

		String favourNumber = rud.getTransactionRef();
		if (ReceiptMode.CHEQUE.equals(receiptMode) && StringUtils.isBlank(favourNumber)) {
			setError(rud, "[FAVOURNUMBER] is Mandatory");
			return;
		}

		if (favourNumber.length() > 6) {
			setError(rud, "[FAVOURNUMBER] with length more than 6");
			return;
		}

		if (rud.getChequeNumber().length() > 50) {
			setError(rud, "[CHEQUEACNO] with length more than 50");
			return;
		}

		if (rud.getTransactionRef().length() > 50) {
			setError(rud, "[TRANSACTIONREF] with length more than 50");
			return;
		}

		String modeStatus = rud.getStatus();
		if (modeStatus.length() > 50) {
			setError(rud, "[STATUS] with length more than 1");
			return;
		}

		if (rud.getExternalRef().length() > 50) {
			setError(rud, "[EXTERNALREF] with length more than 1 ");
			return;
		}

		/*
		 * String strCollectionAgentId = rud.getStrCollectionAgentId(); if (StringUtils.isNumeric(strCollectionAgentId))
		 * { rud.setCollectionAgentId(Long.parseLong(strCollectionAgentId)); } else { setError(rud,
		 * "Non numeric value in [COLLECTIONAGENT] "); return; }
		 */

		/*
		 * String strBckdtdWthOldDues = rud.getStrBckdtdWthOldDues(); if (StringUtils.isBlank(strBckdtdWthOldDues)) {
		 * strBckdtdWthOldDues = "0"; }
		 */

		String entityCode = financeMainDAO.getEntityCodeByRef(reference);
		if (!header.getEntityCode().equals(entityCode)) {
			setError(rud, rud.getReference(), "Loan Reference is not matching with entity");
			return;
		}

		if (rud.isDedupCheck()) {
			checkDedup(rud);
		}

		String panNumber = rud.getPanNumber();
		if (StringUtils.isNotEmpty(panNumber)) {
			if (MasterDefUtil.isValidationReq(MasterDefUtil.DocType.PAN)) {
				DocVerificationHeader docHeader = new DocVerificationHeader();
				docHeader.setDocNumber(panNumber);
				docHeader.setDocReference(reference);

				ErrorDetail error = DocVerificationUtil.doValidatePAN(docHeader, true);

				if (error != null) {
					setError(rud, error.getMessage());
					return;
				}
			}
		}

		ErrorDetail error = receiptService.getWaiverValidation(rud.getReferenceID(), purpose, rud.getValueDate());

		if (error != null) {
			rud.setProgress(EodConstants.PROGRESS_FAILED);
			rud.setErrorCode(error.getCode());
			rud.setErrorDesc(error.getMessage());
			return;
		}

		rud.setProgress(EodConstants.PROGRESS_SUCCESS);
		rud.setErrorCode("");
		rud.setErrorDesc("");
	}

	private void prepareKeys(CreateReceiptUpload rud) {
		final Set<String> txnKeys = new HashSet<>();
		final Set<String> txnChequeKeys = new HashSet<>();
		final Set<String> receiptValidList = new HashSet<>();

		rud.setTxnKeys(txnKeys);
		rud.setTxnChequeKeys(txnChequeKeys);
		rud.setReceiptValidList(receiptValidList);
	}

	private boolean isReceiptDetailExist(CreateReceiptUpload rud) {
		String receiptMode = rud.getReceiptMode();

		if (isChequeOrDDMode(receiptMode)
				&& RepayConstants.PAYSTATUS_REALIZED.equalsIgnoreCase(rud.getReceiptModeStatus())) {
			if ("SP".equalsIgnoreCase(rud.getReceiptPurpose())) {
				return finReceiptHeaderDAO.isReceiptExists(rud, "");
			} else {
				return finReceiptHeaderDAO.isReceiptExists(rud, "_Temp");
			}
		}

		return false;
	}

	private void checkDedup(CreateReceiptUpload rud) {
		String txnKey = "";

		String receiptMode = rud.getReceiptMode();

		if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equalsIgnoreCase(receiptMode)) {
			txnKey = getOnlineTxnKey(rud);
			if (!rud.getTxnKeys().add(txnKey)) {
				setError(rud, "90273", "With combination REFERENCE/TRANSACTIONREF/SubReceiptMode: " + txnKey);
				return;
			}
		}

		boolean chequeOrDD = isChequeOrDDMode(receiptMode);

		if (chequeOrDD) {
			txnKey = getOfflineTxnKey(rud);
			if (!rud.getTxnChequeKeys().add(txnKey)) {
				setError(rud, "90273", "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey);
				return;
			}
		}

		if (isReceiptDetailExist(rud)) {
			return;
		}

		if (!"B".equals(rud.getStatus()) && chequeOrDD && this.finReceiptHeaderDAO.isChequeExists(rud)) {
			setError(rud, "90273", "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey);
			return;
		}

		if (DisbursementConstants.PAYMENT_TYPE_ONLINE.equalsIgnoreCase(receiptMode)
				&& this.finReceiptHeaderDAO.isOnlineExists(rud)) {
			setError(rud, "90273", "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey);
		}
	}

	private void isFileExists(CreateReceiptUpload rud) {
		List<String> filenameList = createReceiptUploadDAO.isDuplicateExists(rud);

		boolean dedupinFile = !filenameList.isEmpty();

		String dedup = rud.getReference() + rud.getTransactionRef() + rud.getValueDate() + rud.getReceiptAmount();

		logger.info("Checking duplicate Receipt in ReceiptUploadDetails_Temp table..");

		if (StringUtils.isEmpty(rud.getPaymentRef())) {
			return;
		}

		if (!rud.getReceiptValidList().add(dedup) || dedupinFile) {
			StringBuilder message = new StringBuilder();

			message.append("Duplicate Receipt exists with combination of ");
			message.append(" FinReference - ").append(rud.getReference());
			message.append(", Value Date - ").append(DateUtil.format(rud.getValueDate(), DateFormat.FULL_DATE));
			message.append(", Receipt Amount - ")
					.append(PennantApplicationUtil.amountFormate(rud.getReceiptAmount(), 2));

			if (StringUtils.isNotBlank(rud.getTransactionRef())) {
				message.append(" and Transaction Reference").append(rud.getTransactionRef());
			}

			message.append(" already exists");

			if (dedupinFile) {
				message.append(" with filename").append(filenameList.get(0));
			}

			setError(rud, "21005", message.toString());
			logger.info("Duplicate Receipt found in ReceiptUploadDetails_Temp table..");
		}
	}

	private String getOnlineTxnKey(CreateReceiptUpload rud) {
		StringBuilder key = new StringBuilder();
		key.append(rud.getReference()).append("/");
		key.append(rud.getTransactionRef()).append("/");
		key.append(rud.getSubReceiptMode()).append("/");
		key.append(rud.getPaymentRef());

		return key.toString();
	}

	private String getOfflineTxnKey(CreateReceiptUpload rud) {
		StringBuilder key = new StringBuilder();
		key.append(rud.getReference()).append("/");
		key.append(rud.getReceiptMode()).append("/");
		key.append(rud.getBankCode()).append("/");
		key.append(rud.getTransactionRef());

		return key.toString();
	}

	public void setError(CreateReceiptUpload rud, String message) {
		setError(rud, "RU0040", message);
	}

	public void setError(CreateReceiptUpload rud, String code, String message) {
		String[] valueParm = new String[1];
		valueParm[0] = message;
		rud.setProgress(EodConstants.PROGRESS_FAILED);
		ErrorDetail errorDetail = new ErrorDetail(code, message, valueParm);

		rud.setErrorCode(errorDetail.getCode());
		rud.setErrorDesc(errorDetail.getError());
	}

	private boolean isChequeOrDDMode(String receiptMode) {
		return ReceiptMode.CHEQUE.equalsIgnoreCase(receiptMode) || ReceiptMode.DD.equalsIgnoreCase(receiptMode);
	}

	public void validateAllocations(CreateReceiptUpload uad) {
		String allocationType = uad.getAllocationType();

		if (StringUtils.isBlank(allocationType)) {
			setError(uad, "Allocation Sheet: [ALLOCATIONTYPE] with blank value ");
		}

		String referenceCode = uad.getCode();
		if (StringUtils.isNotBlank(referenceCode)) {
			if (referenceCode.length() > 8) {
				setError(uad, "Allocation Sheet: [REFERENCECODE] with lenght more than 8 characters ");
			}
		}

		try {
			BigDecimal precisionAmount = uad.getPaidAmount();
			precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				setError(uad, "Allocation Sheet: Minor Currency (Decimals) in [PAIDAMOUNT] ");
				uad.setPaidAmount(actualAmount);
			} else {
				uad.setPaidAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
				setError(uad, "Allocation Sheet: [PAIDAMOUNT] with value <0 ");
			}
		} catch (Exception e) {
			uad.setPaidAmount(BigDecimal.ZERO);
			setError(uad, "Allocation Sheet: [PAIDAMOUNT] ");
		}
	}

	@Autowired
	public void setCreateReceiptUploadDAO(CreateReceiptUploadDAO createReceiptUploadDAO) {
		this.createReceiptUploadDAO = createReceiptUploadDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

}
