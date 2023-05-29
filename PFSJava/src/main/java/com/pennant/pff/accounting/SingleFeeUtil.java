package com.pennant.pff.accounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennapps.core.util.ObjectUtil;

public class SingleFeeUtil {
	private SingleFeeUtil() {
		super();
	}

	public static List<TransactionEntry> getTransactionEntries(AEEvent aeEvent) {
		List<TransactionEntry> txnEntries = getTxnEntries(aeEvent);

		List<FeeType> feeTypes = getNotConfigurerFeeTypes(aeEvent.getFeesList(), txnEntries);

		List<TransactionEntry> list = getSingleFeeTxn(SingleFee.FEE, txnEntries);

		for (TransactionEntry singleFeeTxn : list) {

			setSingleFeeTxnEntries(txnEntries, singleFeeTxn, feeTypes);

			addTxnEntry(singleFeeTxn, txnEntries, feeTypes);

			List<TransactionEntry> feeWaiverOrRefundTxnList = singleFeeTxn.getSingleFeeWaiverOrRefundTxn();

			for (TransactionEntry feeWaiverOrRefundTxn : feeWaiverOrRefundTxnList) {
				addTxnEntry(feeWaiverOrRefundTxn, txnEntries, feeTypes);
			}

			setGstTxnEntries(txnEntries, singleFeeTxn, feeTypes);

		}

		txnEntries = filterByAdviseTye(feeTypes, txnEntries);

		txnEntries = getBulkingEntries(txnEntries);

		int transOrder = 10;
		for (TransactionEntry txn : txnEntries) {
			txn.setTransOrder(transOrder);
			transOrder = transOrder + 10;
		}

		return txnEntries;
	}

	private static List<TransactionEntry> getTxnEntries(AEEvent aeEvent) {
		List<TransactionEntry> txnEntries = new ArrayList<>();

		List<Long> acSetList = aeEvent.getAcSetIDList();

		for (Long accountSetId : acSetList) {
			if (aeEvent.isEOD()) {
				txnEntries.addAll(AccountingConfigCache.getCacheTransactionEntry(accountSetId));
			} else {
				txnEntries.addAll(AccountingConfigCache.getTransactionEntry(accountSetId));
			}
		}

		return txnEntries;
	}

	private static List<FeeType> getNotConfigurerFeeTypes(List<FeeType> feesList, List<TransactionEntry> txnEntries) {
		List<FeeType> feeTypes = new ArrayList<>();

		for (FeeType feeType : feesList) {
			boolean exists = false;
			for (TransactionEntry txnEntry : txnEntries) {
				if (StringUtils.equals(feeType.getFeeTypeCode(), txnEntry.getFeeCode())) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				feeTypes.add(feeType);
			}
		}

		return feeTypes;
	}

	private static List<TransactionEntry> getSingleFeeTxn(String feeType, List<TransactionEntry> txnEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		for (TransactionEntry transactionEntry : txnEntries) {
			if (transactionEntry.isSingelFeeEntry()) {
				continue;
			}

			String accountType = transactionEntry.getAccountType();
			if (accountType.equals(feeType) || isSingleFee(transactionEntry.getAmountRule())) {
				list.add(transactionEntry);
				transactionEntry.setSingelFeeEntry(true);
			}
		}

		return list;
	}

	private static List<TransactionEntry> filterByAdviseTye(List<FeeType> feeTypes, List<TransactionEntry> txnEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		for (TransactionEntry item : txnEntries) {
			if (!item.isSingelFeeEntry()) {
				list.add(item);
				continue;
			}

			for (FeeType feeType : feeTypes) {
				if (feeType.getFeeTypeCode().equals(item.getFeeCode())
						&& item.getReceivableOrPayable() == feeType.getAdviseType()) {
					list.add(item);
				}
			}

		}

		return list;
	}

	private static List<TransactionEntry> getBulkingEntries(List<TransactionEntry> txnEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		Map<String, List<TransactionEntry>> bulkingEntries = new HashMap<>();

		for (TransactionEntry item : txnEntries) {
			if (item.isBulking()) {
				String key = item.getAccountType().concat(item.getTranscationCode());

				List<TransactionEntry> entries = bulkingEntries.get(key);

				if (entries == null) {
					entries = new ArrayList<>();
				}

				entries.add(item);

				bulkingEntries.put(key, entries);
			} else {
				list.add(item);
			}
		}

		list.addAll(getBulkingEntries(bulkingEntries));

		return list.stream().sorted((b1, b2) -> Integer.compare(b1.getTransOrder(), b2.getTransOrder())).toList();
	}

	private static void setSingleFeeTxnEntries(List<TransactionEntry> txnEntries, TransactionEntry singleFeeTxn,
			List<FeeType> feeTypes) {
		for (FeeType feeType : feeTypes) {

			if (!feeType.isManualAdvice()) {
				feeType.setAdviseType(AdviseType.RECEIVABLE.id());
			}

			if (singleFeeTxn.getReceivableOrPayable() == feeType.getAdviseType()) {
				String feeTypeCode = feeType.getFeeTypeCode();

				if ((AdvanceRuleCode.ADVINT.name().equals(feeTypeCode)
						|| AdvanceRuleCode.ADVEMI.name().equals(feeTypeCode)
						|| AdvanceRuleCode.CASHCLT.name().equals(feeTypeCode)
						|| AdvanceRuleCode.DSF.name().equals(feeTypeCode)
						|| FinanceConstants.SUBVEN_FEE.equals(feeTypeCode))) {
					continue;
				}

			}

			if (feeType.isTaxApplicable()) {
				setSingleFeeGSTTxn(singleFeeTxn, txnEntries);
			}

			if (feeType.isTdsReq()) {
				setSingleFeeTDSTxn(singleFeeTxn, txnEntries);
			}

			setSingleFeeWaiverOrRefundTxn(singleFeeTxn, txnEntries);

			String feeIncomeOrExpenseAcType = StringUtils.trimToNull(feeType.getIncomeOrExpenseAcType());
			if (feeIncomeOrExpenseAcType == null) {
				throw new AppException("SingelFee", "Account Type for the Fee Type [" + feeType.getFeeTypeCode()
						+ "] not configured. Please contact the system administrator.");
			}

		}
	}

	private static void setGstTxnEntries(List<TransactionEntry> txnEntries, TransactionEntry txnEntry,
			List<FeeType> feeTypes) {
		List<TransactionEntry> cgstTxnList = txnEntry.getSingleFeeCGSTTxn();
		List<TransactionEntry> sgstTxnList = txnEntry.getSingleFeeSGSTTxn();
		List<TransactionEntry> igstTxnList = txnEntry.getSingleFeeIGSTTxn();
		List<TransactionEntry> ugstTxnListList = txnEntry.getSingleFeeUGSTTxn();
		List<TransactionEntry> cessTxnList = txnEntry.getSingleFeeCESSTxn();
		List<TransactionEntry> tdsTxnList = txnEntry.getSingleFeeTDSTxn();

		for (TransactionEntry cgstTxn : cgstTxnList) {
			addTxnEntry(cgstTxn, txnEntries, feeTypes);
		}

		for (TransactionEntry sgstTxn : sgstTxnList) {
			addTxnEntry(sgstTxn, txnEntries, feeTypes);
		}

		for (TransactionEntry igstTxn : igstTxnList) {
			addTxnEntry(igstTxn, txnEntries, feeTypes);
		}

		for (TransactionEntry ugstTxn : ugstTxnListList) {
			addTxnEntry(ugstTxn, txnEntries, feeTypes);
		}

		for (TransactionEntry cessTxn : cessTxnList) {
			addTxnEntry(cessTxn, txnEntries, feeTypes);
		}

		for (TransactionEntry tdsTxn : tdsTxnList) {
			addTxnEntry(tdsTxn, txnEntries, feeTypes);
		}

	}

	private static void addTxnEntry(TransactionEntry txnEntry, List<TransactionEntry> txnEntries,
			List<FeeType> feeTypes) {
		int index = txnEntries.indexOf(txnEntry);
		txnEntries.addAll(index, createEntries(txnEntry, feeTypes));
		txnEntries.remove(txnEntry);
	}

	private static List<TransactionEntry> getBulkingEntries(Map<String, List<TransactionEntry>> bulkingEntries) {
		List<TransactionEntry> list = new ArrayList<>();

		for (Entry<String, List<TransactionEntry>> items : bulkingEntries.entrySet()) {
			TransactionEntry newTxnEntry = null;

			StringBuilder tsb = new StringBuilder();
			StringBuilder arsb = new StringBuilder();

			for (TransactionEntry item : items.getValue()) {
				if (newTxnEntry == null) {
					newTxnEntry = ObjectUtil.clone(item);
				}

				String amountRule = item.getAmountRule();

				amountRule = removeSpaces(amountRule);

				amountRule = amountRule.replace(";", "");
				amountRule = amountRule.replace(" ;", "");
				amountRule = amountRule.replace("; ", "");
				amountRule = amountRule.replace(" ; ", "");

				tsb = tsb.append("(" + amountRule.substring(7, amountRule.length() - 1)).append(")+");
			}

			arsb.append(PennantConstants.RESULT.concat(tsb.replace(tsb.length() - 1, tsb.length(), ";").toString()));

			if (newTxnEntry != null) {
				newTxnEntry.setAmountRule(arsb.toString());

				list.add(newTxnEntry);
			}

		}

		return list;
	}

	private static void setSingleFeeGSTTxn(TransactionEntry singleFeeTxn, List<TransactionEntry> txnEntries) {
		singleFeeTxn.setSingleFeeCGSTTxn(getSingleFeeTxn(SingleFee.CGST, txnEntries));
		singleFeeTxn.setSingleFeeSGSTTxn(getSingleFeeTxn(SingleFee.SGST, txnEntries));
		singleFeeTxn.setSingleFeeUGSTTxn(getSingleFeeTxn(SingleFee.UGST, txnEntries));
		singleFeeTxn.setSingleFeeIGSTTxn(getSingleFeeTxn(SingleFee.IGST, txnEntries));
		singleFeeTxn.setSingleFeeCESSTxn(getSingleFeeTxn(SingleFee.CESS, txnEntries));
	}

	private static void setSingleFeeTDSTxn(TransactionEntry singleFeeTxn, List<TransactionEntry> txnEntries) {
		singleFeeTxn.setSingleFeeTDSTxn(getSingleFeeTxn(SingleFee.TDS, txnEntries));
	}

	private static void setSingleFeeWaiverOrRefundTxn(TransactionEntry singleFeeTxn,
			List<TransactionEntry> txnEntries) {
		singleFeeTxn.setSingleFeeWaiverOrRefundTxn(getSingleFeeTxn(SingleFee.WR, txnEntries));
	}

	private static List<TransactionEntry> createEntries(TransactionEntry singleFeeTxn, List<FeeType> feeTypes) {
		List<TransactionEntry> list = new ArrayList<>();

		for (FeeType feeType : feeTypes) {
			if (singleFeeTxn.getReceivableOrPayable() == feeType.getAdviseType()) {
				String feeTypeCode = feeType.getFeeTypeCode();

				if ((AdvanceRuleCode.ADVINT.name().equals(feeTypeCode)
						|| AdvanceRuleCode.ADVEMI.name().equals(feeTypeCode)
						|| AdvanceRuleCode.CASHCLT.name().equals(feeTypeCode)
						|| AdvanceRuleCode.DSF.name().equals(feeTypeCode)
						|| FinanceConstants.SUBVEN_FEE.equals(feeTypeCode))) {
					continue;
				}

			}

			list.add(createNewTE(singleFeeTxn, feeType));
		}

		return list;
	}

	private static TransactionEntry createNewTE(final TransactionEntry transactionEntry, final FeeType feeType) {
		String feeTypeCode = feeType.getFeeTypeCode();

		TransactionEntry txnEntry = ObjectUtil.clone(transactionEntry);

		String transDesc = txnEntry.getTransDesc();

		String accountType = StringUtils.trimToEmpty(txnEntry.getAccountType());

		if (accountType.startsWith("FEE_IE")) {
			switch (accountType) {
			case SingleFee.CGST:
				txnEntry.setAccountType(feeType.getCgstAcType());
				transDesc = feeType.getCgstAcTypeDesc();
				break;
			case SingleFee.SGST:
				txnEntry.setAccountType(feeType.getSgstAcType());
				transDesc = feeType.getSgstAcTypeDesc();
				break;
			case SingleFee.IGST:
				txnEntry.setAccountType(feeType.getIgstAcType());
				transDesc = feeType.getIgstAcTypeDesc();
				break;
			case SingleFee.UGST:
				txnEntry.setAccountType(feeType.getUgstAcType());
				transDesc = feeType.getUgstAcTypeDesc();
				break;
			case SingleFee.CESS:
				txnEntry.setAccountType(feeType.getCessAcType());
				transDesc = feeType.getCessAcTypeDesc();
				break;
			case SingleFee.TDS:
				txnEntry.setAccountType(feeType.getTdsAcType());
				transDesc = feeType.getTdsAcTypeDesc();
				break;
			case SingleFee.WR:
				txnEntry.setAccountType(feeType.getWaiverOrRefundAcType());
				transDesc = feeType.getWaiverOrRefundAcTypeDesc();
				break;
			default:
				txnEntry.setAccountType(feeType.getIncomeOrExpenseAcType());
				transDesc = feeType.getIncomeOrExpenseAcTypeDesc();
				break;
			}
		} else {
			txnEntry.setAccountType(accountType);

		}

		txnEntry.setTransDesc(transDesc);

		accountType = StringUtils.trimToNull(txnEntry.getAccountType());

		if (accountType == null) {
			throw new AppException("Account Type should configured for the Fee Type: " + feeTypeCode);
		}

		String amountRule = txnEntry.getAmountRule();

		if (isSingleFee(amountRule)) {
			amountRule = amountRule.replace("FEE", feeTypeCode.toUpperCase());
		}

		txnEntry.setAmountRule(amountRule);
		txnEntry.setFeeCode(feeTypeCode);
		return txnEntry;
	}

	private static boolean isSingleFee(String amountRule) {
		String[] amountCodes = getAmountCodes(amountRule);

		for (String feeCode : amountCodes) {
			if (feeCode.startsWith("FEE_")) {
				return true;
			}
		}

		return false;
	}

	private static String removeSpaces(String amountRule) {
		amountRule = amountRule.replace("Result=", "Result=");
		amountRule = amountRule.replace("Result =", "Result=");
		amountRule = amountRule.replace("Result= ", "Result=");
		amountRule = amountRule.replace("Result = ", "Result=");
		return amountRule.replace("Result = ", "Result=");
	}

	private static String[] getAmountCodes(String amountRule) {
		amountRule = amountRule.replace("Result=", "");
		amountRule = amountRule.replace("Result =", "");
		amountRule = amountRule.replace("Result= ", "");
		amountRule = amountRule.replace("Result = ", "");
		amountRule = amountRule.replace("Result = ", "");

		amountRule = amountRule.trim();

		amountRule = amountRule.replace("(", "");
		amountRule = amountRule.replace(")", "");

		amountRule = amountRule.trim();

		amountRule = amountRule.replace("+", "#");
		amountRule = amountRule.replace(" +", "#");
		amountRule = amountRule.replace("+ ", "#");
		amountRule = amountRule.replace(" + ", "#");

		amountRule = amountRule.trim();

		amountRule = amountRule.replace("-", "#");
		amountRule = amountRule.replace(" -", "#");
		amountRule = amountRule.replace("- ", "#");
		amountRule = amountRule.replace(" - ", "#");

		amountRule = amountRule.trim();

		return amountRule.split("#");
	}
}
