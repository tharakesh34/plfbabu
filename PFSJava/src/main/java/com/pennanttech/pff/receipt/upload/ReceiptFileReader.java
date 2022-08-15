package com.pennanttech.pff.receipt.upload;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;

public class ReceiptFileReader {
	private static final int ROOT_ID = 0;
	private static final int FIN_REFERENCE = 1;
	private static final int PURPOSE = 2;
	private static final int EXCESS_ADJUST_TO = 3;
	private static final int ALLOCATION_TYPE = 4;
	private static final int RECEIPT_AMOUNT = 5;
	private static final int EFF_SCHD_METHOD = 6;
	private static final int REMARKS = 7;
	private static final int VALUE_DATE = 8;
	private static final int RECEIVED_DATE = 9;
	private static final int RECEIPT_MODE = 10;
	private static final int SUB_RECEIPT_MODE = 11;
	private static final int RECEIPT_CHANNEL = 12;
	private static final int FUNDING_AC = 13;
	private static final int PAYMENT_REF = 14;
	private static final int FAVOUR_NUMBER = 15;
	private static final int BANK_CODE = 16;
	private static final int CHEQUE_NO = 17;
	private static final int TRANSACTION_REF = 18;
	private static final int STATUS = 19;
	private static final int DEPOSIT_DATE = 20;
	private static final int REALIZATION_DATE = 21;
	private static final int INSTRUMENT_DATE = 22;
	private static final int PAN_NUMBER = 23;
	private static final int EXT_REFERENCE = 24;
	private static final int COLLECTION_AGENT_ID = 25;
	private static final int RECEIVED_FROM = 26;
	private static final int BOUNCE_DATE = 27;
	private static final int RECEIPT_ID = 28;
	private static final int REASON = 29;
	private static final int BCK_DTD_WTH_OLD_DUES = 30;

	private static final int ALLOC_TYPE = 1;
	private static final int REFERENCE_CODE = 2;
	private static final int PAID_AMOUNT = 3;
	private static final int WAIVED_AMOUNT = 4;

	public static ReceiptUploadDetail read(Row row) {
		ReceiptUploadDetail rud = new ReceiptUploadDetail();

		for (Cell cell : row) {
			switch (cell.getColumnIndex()) {
			case ROOT_ID:
				rud.setRootId(cell.toString());
				break;
			case FIN_REFERENCE:
				rud.setReference(cell.toString());
				break;
			case PURPOSE:
				rud.setReceiptPurpose(cell.toString());
				break;
			case EXCESS_ADJUST_TO:
				rud.setExcessAdjustTo(cell.toString());
				break;
			case ALLOCATION_TYPE:
				rud.setAllocationType(cell.toString());
				break;
			case RECEIPT_AMOUNT:
				rud.setStrReceiptAmount(cell.toString());
				break;
			case EFF_SCHD_METHOD:
				String effSchM = cell.toString();
				if (StringUtils.isNotBlank(effSchM)) {
					rud.setEffectSchdMethod(effSchM);
				}
				break;
			case REMARKS:
				String remarks = cell.toString();
				if (StringUtils.isNotBlank(remarks)) {
					rud.setRemarks(remarks);
				}
				break;
			case VALUE_DATE:
				rud.setStrValueDate(cell.toString());
				break;
			case RECEIVED_DATE:
				rud.setStrReceivedDate(cell.toString());
				break;
			case RECEIPT_MODE:
				String receiptMode = cell.toString();
				if (StringUtils.isNotBlank(receiptMode)) {
					rud.setReceiptMode(receiptMode);
				}
				break;
			case SUB_RECEIPT_MODE:
				String subReceiptMode = cell.toString();
				if (StringUtils.isNotBlank(subReceiptMode)) {
					rud.setSubReceiptMode(subReceiptMode);
				}
				break;
			case RECEIPT_CHANNEL:
				String channel = cell.toString();
				if (StringUtils.isNotBlank(channel)) {
					rud.setReceiptChannel(channel);
				}
				break;
			case FUNDING_AC:
				String fundingAc = cell.toString();
				if (StringUtils.isNotBlank(fundingAc)) {
					rud.setFundingAc(fundingAc);
				}
				break;
			case PAYMENT_REF:
				String paymentRef = cell.toString();
				if (StringUtils.isNotBlank(paymentRef)) {
					rud.setPaymentRef(paymentRef);
				}

				break;
			case FAVOUR_NUMBER:
				String favourNumber = cell.toString();
				if (StringUtils.isNotBlank(favourNumber)) {
					rud.setFavourNumber(favourNumber);
					rud.setTransactionRef(favourNumber);
				}
				break;
			case BANK_CODE:
				String bankCode = cell.toString();
				if (StringUtils.isNotBlank(bankCode)) {
					rud.setBankCode(bankCode);
				}
				break;
			case CHEQUE_NO:
				String chequeNo = cell.toString();
				if (StringUtils.isNotBlank(chequeNo)) {
					rud.setChequeNo(chequeNo);
				}
				break;
			case TRANSACTION_REF:
				String transactionRef = cell.toString();
				if (StringUtils.isNotBlank(transactionRef)) {
					rud.setTransactionRef(transactionRef);
				}
				break;
			case STATUS:
				String status = cell.toString();
				if (StringUtils.isNotBlank(status)) {
					rud.setStatus(status);
				}
				break;
			case DEPOSIT_DATE:
				rud.setStrDepositDate(cell.toString());
				break;
			case REALIZATION_DATE:
				rud.setStrRealizationDate(cell.toString());
				break;
			case INSTRUMENT_DATE:
				rud.setStrInstrumentDate(cell.toString());
				break;
			case PAN_NUMBER:
				String panNumber = cell.toString();
				if (StringUtils.isNotBlank(panNumber)) {
					rud.setPanNumber(panNumber);
				}
				break;
			case EXT_REFERENCE:
				String extReference = cell.toString();
				if (StringUtils.isNotBlank(extReference)) {
					rud.setExtReference(extReference);
				}
				break;
			case COLLECTION_AGENT_ID:
				String agentID = cell.toString();
				if (StringUtils.isBlank(agentID)) {
					agentID = "0";
				}
				rud.setStrCollectionAgentId(agentID);
				break;
			case RECEIVED_FROM:
				String receivedFrom = cell.toString();
				if (StringUtils.isNotBlank(receivedFrom)) {
					rud.setReceivedFrom(receivedFrom);
				}
				break;
			case BOUNCE_DATE:
				rud.setStrBounceDate(cell.toString());
				break;
			case RECEIPT_ID:
				String receiptId = cell.toString();
				if (StringUtils.isNotBlank(receiptId)) {
					rud.setReceiptId(Long.parseLong(receiptId));
				} else {
					rud.setReceiptId(0L);
					rud.setNewReceipt(true);
				}
				break;
			case REASON:
				String bounceReason = cell.toString();
				if (StringUtils.isNotBlank(bounceReason)) {
					rud.setBounceReason(bounceReason);
				}
				break;
			case BCK_DTD_WTH_OLD_DUES:
				rud.setStrBckdtdWthOldDues(cell.toString());
				break;
			}
		}

		return rud;
	}

	public static UploadAlloctionDetail readAllocations(Row row) {
		UploadAlloctionDetail uad = new UploadAlloctionDetail();
		for (Cell cell : row) {
			switch (cell.getColumnIndex()) {
			case ROOT_ID:
				uad.setRootId(cell.toString());
				break;
			case ALLOC_TYPE:
				uad.setAllocationType(cell.toString());
				break;
			case REFERENCE_CODE:
				uad.setReferenceCode(cell.toString());
				break;
			case PAID_AMOUNT:
				uad.setStrPaidAmount(cell.toString());
				break;
			case WAIVED_AMOUNT:
				uad.setStrWaivedAmount(cell.toString());
				break;
			default:
				break;
			}
		}

		return uad;
	}

}