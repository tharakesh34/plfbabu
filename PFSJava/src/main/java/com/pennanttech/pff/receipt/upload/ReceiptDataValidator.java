package com.pennanttech.pff.receipt.upload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.backend.dao.applicationmaster.BounceReasonDAO;
import com.pennant.backend.dao.applicationmaster.RejectDetailDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.ReceiptUploadConstants.ReceiptDetailStatus;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.document.DocVerificationUtil;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class ReceiptDataValidator {
	private static final Logger logger = LogManager.getLogger(ReceiptDataValidator.class);

	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private BounceReasonDAO bounceReasonDAO;
	private RejectDetailDAO rejectDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private ReceiptService receiptService;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;

	public void validate(ReceiptUploadDetail rud) {

		String reference = rud.getReference();
		rud.setFinID(financeMainDAO.getFinIDByFinReference(reference, "", false));

		isFileExists(rud);

		rud.setNewReceipt(true);
		Long receiptID = rud.getReceiptId();
		String status = rud.getStatus();

		if ((!rud.getBounceReason().isEmpty() || !rud.getCancelReason().isEmpty()) && receiptID != null) {
			if (StringUtils.isNotBlank(status)) {
				if (RepayConstants.PAYSTATUS_BOUNCE.equals(status) || RepayConstants.PAYSTATUS_CANCEL.equals(status)) {
					rud.setNewReceipt(false);
				} else {
					setError(rud, "Status other than B, C");
				}
			} else {
				setError(rud, "Blanks/Nulls in [STATUS] ");
			}
		}

		if (StringUtils.isBlank(reference)) {
			String msg = "Blanks/Nulls in [REFERENCE] ";
			if (!rud.isNewReceipt()) {
				msg = "Blanks/Nulls in [REFERENCE] for Receipt Bounce/Cancel";
			}

			setError(rud, msg);
			return;
		}

		String purpose = rud.getReceiptPurpose();
		if (StringUtils.isBlank(purpose) && rud.isNewReceipt()) {
			setError(rud, "Values other than SP/EP/ES in [RECEIPTPURPOSE] ");
			return;
		}

		if (FinanceConstants.EARLYSETTLEMENT.equals(purpose) || FinanceConstants.PARTIALSETTLEMENT.equals(purpose)) {
			if (StringUtils.isNotBlank(receiptUploadDetailDAO.getLoanReferenc(reference, rud.getFileName()))) {
				setError(rud, "90273", "Receipt In process for " + reference);
			}
		}

		if (FinanceConstants.EARLYSETTLEMENT.equals(purpose)) {
			if (finAdvancePaymentsDAO.getStatusCountByFinRefrence(rud.getFinID()) > 0) {
				setErrorToRUD(rud, "90508", rud.getReference());
			}
		}

		if (!"SP".equals(purpose) && !"EP".equals(purpose) && !"ES".equals(purpose) && rud.isNewReceipt()) {
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
			String receiptAmt = rud.getStrReceiptAmount();
			if (StringUtils.isBlank(receiptAmt)) {
				receiptAmt = "0";
			}
			BigDecimal precisionAmount = new BigDecimal(receiptAmt);
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

			if (precisionAmount.compareTo(BigDecimal.ZERO) <= 0 && rud.isNewReceipt()) {
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

		String strValueDate = rud.getStrValueDate();
		try {
			if (StringUtils.isNotBlank(strValueDate)) {
				rud.setValueDate(DateUtil.parse(strValueDate, DateFormat.LONG_DATE.getPattern()));
			} else if (rud.isNewReceipt()) {
				setError(rud, "Blanks in [VALUEDATE] ");
				return;
			}
		} catch (Exception e) {
			setError(rud, "Value in [VALUEDATE] ");
			return;
		}

		String strReceivedDate = rud.getStrReceivedDate();
		try {
			if (StringUtils.isNotBlank(strReceivedDate)) {
				rud.setReceivedDate(DateUtil.parse(strReceivedDate, DateFormat.LONG_DATE.getPattern()));
			} else if (rud.isNewReceipt()) {
				setError(rud, "Blanks in [RECEIVEDDATE] ");
				return;
			}
		} catch (Exception e) {
			setError(rud, "Value in [RECEIVEDDATE] ");
			return;
		}

		if (DateUtil.compare(rud.getReceivedDate(), rud.getValueDate()) < 0) {
			setError(rud, "[RECEIVEDDATE] should be greater than or equal to [VALUEDATE].");
			return;
		}

		String receiptMode = rud.getReceiptMode();
		if (receiptMode.length() > 10) {
			setError(rud, "[RECEIPTMODE] with length more than 10 characters");
			return;
		}

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

		if (rud.getFundingAc().length() > 8) {
			setError(rud, "[FUNDINGAC] with length more than 8 characters");
			return;
		}

		if (rud.getPaymentRef().length() > 50) {
			setError(rud, "[PAYMENTREF] with length more than 50 characters");
			return;
		}

		String favourNumber = rud.getFavourNumber();
		if (ReceiptMode.CHEQUE.equals(receiptMode) && StringUtils.isBlank(favourNumber)) {
			setError(rud, "[FAVOURNUMBER] is Mandatory");
			return;
		}

		if (favourNumber.length() > 6) {
			setError(rud, "[FAVOURNUMBER] with length more than 6");
			return;
		}

		if (rud.getChequeNo().length() > 50) {
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

		String depositDate = rud.getStrDepositDate();
		try {
			if (StringUtils.isBlank(depositDate) && bounceOrCheque) {
				setError(rud, "Blanks in [DEPOSITDATE] ");
				return;
			} else {
				rud.setDepositDate(DateUtil.parse(depositDate, DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			setError(rud, "Value in [DEPOSITDATE] ");
			return;
		}

		String realizationDate = rud.getStrRealizationDate();
		try {
			if (StringUtils.isBlank(realizationDate) && bounceOrCheque) {
				setError(rud, "Blanks in [REALIZATIONDATE] ");
				return;
			} else {
				rud.setRealizationDate(DateUtil.parse(realizationDate, DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			setError(rud, "Value in [REALIZATIONDATE] ");
			return;
		}

		String instrumentDate = rud.getStrInstrumentDate();
		try {
			if (StringUtils.isNotBlank(instrumentDate)) {
				rud.setInstrumentDate(DateUtil.parse(instrumentDate, DateFormat.LONG_DATE.getPattern()));
			} else {
				if (bounceOrCheque) {
					setError(rud, "Blanks in [INSTRUMENTDATE] ");
					return;
				}
			}
		} catch (Exception e) {
			setError(rud, "Value in [INSTRUMENTDATE] ");
			return;
		}

		if (rud.getExtReference().length() > 50) {
			setError(rud, "[EXTERNALREF] with length more than 1 ");
			return;
		}

		String strCollectionAgentId = rud.getStrCollectionAgentId();
		if (StringUtils.isNumeric(strCollectionAgentId)) {
			rud.setCollectionAgentId(Long.parseLong(strCollectionAgentId));
		} else {
			setError(rud, "Non numeric value in [COLLECTIONAGENT] ");
			return;
		}

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(modeStatus) || RepayConstants.PAYSTATUS_CANCEL.equals(modeStatus)) {
			validateBounceAndCancel(rud);
			if (StringUtils.isNotEmpty(rud.getReason())) {
				return;
			}
		}

		String strBckdtdWthOldDues = rud.getStrBckdtdWthOldDues();
		if (StringUtils.isBlank(strBckdtdWthOldDues)) {
			strBckdtdWthOldDues = "0";
		}

		if (strBckdtdWthOldDues.equals("0")) {
			rud.setBckdtdWthOldDues(false);
		} else if (strBckdtdWthOldDues.equals("1")) {
			rud.setBckdtdWthOldDues(true);
		} else {
			setError(rud, "Expected value for back dated with old dues is 0 or 1");
			return;
		}

		String entityCode = financeMainDAO.getEntityCodeByRef(reference);
		if (!rud.getEntityCode().equals(entityCode)) {
			setError(rud, rud.getReference(), "Loan Reference is not matching with entity");
			return;
		}

		if (rud.isDedupCheck()) {
			checkDedup(rud);
		}

		String panNumber = rud.getPanNumber();
		if (StringUtils.isNotEmpty(panNumber)) {
			if (MasterDefUtil.isValidationReq(MasterDefUtil.DocType.PAN)) {
				DocVerificationHeader header = new DocVerificationHeader();
				header.setDocNumber(panNumber);
				header.setDocReference(reference);

				ErrorDetail error = DocVerificationUtil.doValidatePAN(header, true);

				if (error != null) {
					setError(rud, error.getMessage());
					return;
				}
			}
		}

		receiptService = (ReceiptService) SpringBeanUtil.getBean("receiptService");

		receiptService.getWaiverValidation(rud.getFinID(), purpose, rud.getValueDate());

		rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
		rud.setReason("");
		rud.setNewReceipt(true);
	}

	public void validateAllocations(UploadAlloctionDetail uad) {
		String rootID = uad.getRootId();
		if (StringUtils.isBlank(rootID)) {
			setError(uad, "Allocation Sheet: <ROOT>_id with blank value");
		}

		String allocationType = uad.getAllocationType();

		if (StringUtils.isBlank(allocationType)) {
			setError(uad, "Allocation Sheet: [ALLOCATIONTYPE] with blank value ");
		}

		String referenceCode = uad.getReferenceCode();
		if (StringUtils.isNotBlank(referenceCode)) {
			if (referenceCode.length() > 8) {
				setError(uad, "Allocation Sheet: [REFERENCECODE] with lenght more than 8 characters ");
			}
		}

		String paidAmt = uad.getStrPaidAmount();
		if (StringUtils.isBlank(paidAmt)) {
			paidAmt = "0";
		}

		try {
			BigDecimal precisionAmount = new BigDecimal(paidAmt);
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

		String waivedAmt = uad.getStrWaivedAmount();

		if (StringUtils.isBlank(waivedAmt)) {
			waivedAmt = "0";
		}

		try {
			BigDecimal precisionAmount = new BigDecimal(waivedAmt);
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				setError(uad, "Allocation Sheet: Minor Currency (Decimals) in [WAIVEDAMOUNT] ");
				uad.setWaivedAmount(actualAmount);
			} else {
				precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
				uad.setWaivedAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
				setError(uad, "Allocation Sheet: [WAIVEDAMOUNT] with value <0 ");
			}
		} catch (Exception e) {
			uad.setWaivedAmount(BigDecimal.ZERO);
			setError(uad, "Allocation Sheet: [WAIVEDAMOUNT] ");
		}

	}

	private List<UploadAlloctionDetail> setFromUadList(String rootID, List<UploadAlloctionDetail> uadList) {
		List<UploadAlloctionDetail> radList = new ArrayList<>();

		for (int i = 0; i < uadList.size(); i++) {
			UploadAlloctionDetail uad = uadList.get(i);
			if (rootID.equals(uad.getRootId())) {
				radList.add(uad);
				uadList.remove(i);
				i = i - 1;
			}
		}

		return radList;
	}

	private void setErrors(ReceiptUploadDetail rud) {
		List<UploadAlloctionDetail> list = rud.getListAllocationDetails();

		for (UploadAlloctionDetail uad : list) {
			List<ErrorDetail> uadErrors = uad.getErrorDetails();
			if (CollectionUtils.isEmpty(uadErrors)) {
				continue;
			}

			uadErrors.forEach(e -> rud.getErrorDetails().add(e));
		}
	}

	public void validateReceipt(ReceiptUploadDetail rud, List<UploadAlloctionDetail> uadList) {
		List<UploadAlloctionDetail> radList = setFromUadList(StringUtils.trimToEmpty(rud.getRootId()), uadList);
		rud.setListAllocationDetails(radList);

		boolean isManualAloc = "M".equals(rud.getAllocationType());

		if (rud.isNewReceipt()) {
			if (isManualAloc && radList.isEmpty()) {
				setError(rud, "Allocation Type is M but allocations not found");
				return;
			}

			if (!isManualAloc && !radList.isEmpty()) {
				setError(rud, "Allocation Type is A but allocations found");
				return;
			}
		}

		if (radList.isEmpty()) {
			return;
		}

		setErrors(rud);
		if (CollectionUtils.isNotEmpty(rud.getErrorDetails())) {
			rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
			rud.setReason(rud.getErrorDetails().get(0).getError());
			rud.setReceiptId(null);
			return;
		}

		BigDecimal manualAllocated = BigDecimal.ZERO;

		for (UploadAlloctionDetail rad : radList) {
			manualAllocated = manualAllocated.add(rad.getPaidAmount());
		}

		if (rud.isNewReceipt()) {
			if (manualAllocated.compareTo(rud.getReceiptAmount()) != 0) {
				String strAlloate = PennantApplicationUtil.amountFormate(manualAllocated, 2);
				setError(rud, "Manual allocation " + strAlloate + " Not matching with Receipt Amount ");
				return;
			}
		}

		rud.setProcessingStatus(ReceiptDetailStatus.SUCCESS.getValue());
		rud.setReason("");
	}

	private void isFileExists(ReceiptUploadDetail rud) {
		List<String> filenameList = receiptUploadDetailDAO.isDuplicateExists(rud);

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
			message.append(", Value Date - ").append(rud.getStrValueDate());
			message.append(", Receipt Amount - ").append(rud.getStrReceiptAmount());

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

	private void validateBounceAndCancel(ReceiptUploadDetail rud) {
		String receiptMode = rud.getReceiptMode();
		String status = rud.getStatus();
		String receiptPurpose = rud.getReceiptPurpose();

		if (RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {
			if (!isChequeOrDDMode(receiptMode)) {
				setError(rud, "Invalid Receipt Mode in [RECEIPTMODE]");
				return;
			}
		}

		if (rud.getReceiptId() != 0) {
			FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(rud.getReceiptId(), "");
			if (!"SP".equals(rud.getReceiptPurpose())) {
				if (rch != null) {
					String message = "Receipt is already realized, No applicable to Bounce or Cancel";
					setError(rud, "92021", message);
					return;
				} else {
					rch = finReceiptHeaderDAO.getReceiptHeaderByID(rud.getReceiptId(), "_Temp");
				}
			}
			if (rch != null && rch.getReference().equals(rud.getReference())) {
				String modeSts = rch.getReceiptModeStatus();

				if (RepayConstants.PAYSTATUS_BOUNCE.equals(modeSts)
						|| RepayConstants.PAYSTATUS_CANCEL.equals(modeSts)) {
					setError(rud, "RU0052", "Receipt already Bounced/Cancelled");
				}

				rud.setAllocationType(rch.getAllocationType());
				if (rch.getRealizationDate() != null) {
					rud.setRealizationDate(rch.getRealizationDate());
				}
				rud.setDepositDate(rch.getDepositDate());
				rud.setReceivedDate(rch.getReceivedDate());
				rud.setValueDate(rch.getValueDate());
				rud.setReceiptMode(rch.getReceiptMode());
				rud.setReceiptChannel(rch.getReceiptChannel());
				rud.setEffectSchdMethod(rch.getEffectSchdMethod());
				rud.setExcessAdjustTo(rch.getExcessAdjustTo());
				rud.setExtReference(rch.getExtReference());
				rud.setReceiptAmount(rch.getReceiptAmount());
				rud.setSubReceiptMode(rch.getSubReceiptMode());
				rud.setFundingAc(String.valueOf(rch.getPartnerBankId()));
				rud.setTransactionRef(rch.getTransactionRef());

				if (FinServiceEvent.SCHDRPY.equals(rch.getReceiptPurpose())) {
					rud.setReceiptPurpose("SP");
				}

				if (rch.getBankCode() != null) {
					rud.setBankCode(rch.getBankCode());
				}

				rud.setRemarks(rch.getRemarks());
				rud.setReceiptId(rud.getReceiptId());
				rud.setNewReceipt(false);
			} else {
				String message = "Approved Receipt is not there with the Provided Loan Reference & ReceiptID combination";
				setError(rud, "92021", message);
			}

			if (isNotValidPurposeAndStatus(rch, status, receiptPurpose)) {
				setError(rud, "92021",
						"Partial Payment and Early Settlement receipts are not allowed to Bounce/Cancel");
				return;
			}

			if (RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {
				if (!ReceiptMode.CHEQUE.equals(receiptMode) && !ReceiptMode.DD.equals(receiptMode)) {
					setError(rud, "92021", "Status Bounce is only allowed for CHEQUE/DD cases");
				}
			}
		}

		if (!"SP".equals(rud.getReceiptPurpose())) {
			setError(rud, "92021", "Status B/C is not allowed for Receipt Purpose EP/ES");
			return;
		}

		String strBounceDate = rud.getStrBounceDate();
		if (StringUtils.isNotBlank(strBounceDate)) {
			Date bncCnclDt = DateUtil.parse(strBounceDate, DateFormat.LONG_DATE.getPattern());
			Date appDate = rud.getAppDate();
			if ((bncCnclDt.compareTo(appDate) <= 0) && (bncCnclDt.compareTo(rud.getReceivedDate()) >= 0)) {
				rud.setBounceDate(bncCnclDt);
			} else {
				setError(rud,
						"BounceDate should be less than or equal to current application date & greater than or equal to ReceiptDate in [BOUNCE/CANCELDATE] ");
				return;
			}
		} else {
			setError(rud, "Blank in [BOUNCE/CANCELDATE]");
			return;
		}

		String reason = rud.getBounceReason();

		if (StringUtils.isNotBlank(reason)) {
			Long bounceID = bounceReasonDAO.getBounceIDByCode(reason);
			boolean existsRejectCode = rejectDetailDAO.isExistsRejectCode(reason);
			if (bounceID != null && RepayConstants.PAYSTATUS_BOUNCE.equals(status)) {
				rud.setBounceReason(reason);
				rud.setBounceId(bounceID);
			} else if (existsRejectCode && RepayConstants.PAYSTATUS_CANCEL.equals(status)) {
				rud.setCancelReason(reason);
			} else {
				setError(rud, "Invalid code in [BOUNCE/CANCELREASON]");
				return;
			}
		} else {
			setError(rud, "Blank in [BOUNCE/CANCELREASON]");
		}
	}

	private boolean isNotValidPurposeAndStatus(FinReceiptHeader rch, String status, String receiptPurpose) {
		return (rch != null && !"SP".equals(receiptPurpose)
				&& (RepayConstants.PAYSTATUS_BOUNCE.equals(status) || RepayConstants.PAYSTATUS_CANCEL.equals(status))
				&& "R".equals(rch.getReceiptModeStatus()));
	}

	private void checkDedup(ReceiptUploadDetail rud) {
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
			rud.setReceiptdetailExits(true);
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

	private String getOnlineTxnKey(ReceiptUploadDetail rud) {
		StringBuilder key = new StringBuilder();
		key.append(rud.getReference()).append("/");
		key.append(rud.getTransactionRef()).append("/");
		key.append(rud.getSubReceiptMode()).append("/");
		key.append(rud.getPaymentRef());

		return key.toString();
	}

	private String getOfflineTxnKey(ReceiptUploadDetail rud) {
		StringBuilder key = new StringBuilder();
		key.append(rud.getReference()).append("/");
		key.append(rud.getReceiptMode()).append("/");
		key.append(rud.getBankCode()).append("/");
		key.append(rud.getFavourNumber());

		return key.toString();
	}

	private boolean isReceiptDetailExist(ReceiptUploadDetail rud) {
		String receiptMode = rud.getReceiptMode();

		if (isChequeOrDDMode(receiptMode) && RepayConstants.PAYSTATUS_REALIZED.equalsIgnoreCase(rud.getStatus())) {
			if ("SP".equalsIgnoreCase(rud.getReceiptPurpose())) {
				return finReceiptHeaderDAO.isReceiptExists(rud, "");
			} else {
				return finReceiptHeaderDAO.isReceiptExists(rud, "_Temp");
			}
		}

		return false;
	}

	private boolean isChequeOrDDMode(String receiptMode) {
		return ReceiptMode.CHEQUE.equalsIgnoreCase(receiptMode) || ReceiptMode.DD.equalsIgnoreCase(receiptMode);
	}

	public void setErrorToRUD(ReceiptUploadDetail rud, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		rud.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	public void setError(ReceiptUploadDetail rud, String message) {
		setError(rud, "RU0040", message);
	}

	public void setError(ReceiptUploadDetail rud, String code, String message) {
		String[] valueParm = new String[1];
		valueParm[0] = message;
		rud.setProcessingStatus(ReceiptDetailStatus.FAILED.getValue());
		ErrorDetail errorDetail = new ErrorDetail(code, message, valueParm);

		rud.getErrorDetails().add(errorDetail);
		rud.setReason(errorDetail.getError());
	}

	public void setError(UploadAlloctionDetail uad, String message) {
		setError(uad, "RU0040", message);
	}

	public void setError(UploadAlloctionDetail uad, String code, String message) {
		String[] valueParm = new String[1];
		valueParm[0] = message;

		uad.getErrorDetails().add(new ErrorDetail(code, message, valueParm));
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setBounceReasonDAO(BounceReasonDAO bounceReasonDAO) {
		this.bounceReasonDAO = bounceReasonDAO;
	}

	@Autowired
	public void setRejectDetailDAO(RejectDetailDAO rejectDetailDAO) {
		this.rejectDetailDAO = rejectDetailDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setReceiptUploadDetailDAO(ReceiptUploadDetailDAO receiptUploadDetailDAO) {
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
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
