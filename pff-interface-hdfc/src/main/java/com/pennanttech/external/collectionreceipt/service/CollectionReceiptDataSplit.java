package com.pennanttech.external.collectionreceipt.service;

import java.math.BigDecimal;
import java.util.Date;

import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;

public interface CollectionReceiptDataSplit {

	default ExtCollectionReceiptData splitAndSetData(String lineData) {
		try {
			ExtCollectionReceiptData collectionData = new ExtCollectionReceiptData();
			String[] dataArray = lineData.toString().split("\\|");
			collectionData.setAgreementNumber(setLongData(dataArray, 1, collectionData));
			collectionData.setCollection(setStringData(dataArray, 2, collectionData));
			collectionData.setReceiptChannel(setStringData(dataArray, 3, collectionData));
			collectionData.setAgencyId(setLongData(dataArray, 4, collectionData));
			collectionData.setChequeNumber(setLongData(dataArray, 5, collectionData));
			collectionData.setDealingBankId(setLongData(dataArray, 6, collectionData));
			collectionData.setDrawnOn(setStringData(dataArray, 7, collectionData));
			collectionData.setTowards(setStringData(dataArray, 8, collectionData));
			collectionData.setGrandTotal(setBigDecimalData(dataArray, 9, collectionData));
			collectionData.setReceiptDate(setDateData(dataArray, 10, collectionData));
			collectionData.setChequeDate(setDateData(dataArray, 11, collectionData));
			collectionData.setReceiptType(setStringData(dataArray, 12, collectionData));
			collectionData.setReceiptNumber(setLongData(dataArray, 13, collectionData));
			collectionData.setChequeStatus(setStringData(dataArray, 14, collectionData));
			collectionData.setAutoAlloc(setStringData(dataArray, 15, collectionData));
			collectionData.setEmiAmount(setBigDecimalData(dataArray, 16, collectionData));
			collectionData.setLppAmount(setBigDecimalData(dataArray, 17, collectionData));
			collectionData.setBccAmount(setBigDecimalData(dataArray, 18, collectionData));
			collectionData.setExcessAmount(setBigDecimalData(dataArray, 19, collectionData));
			collectionData.setOthercharge1(setLongData(dataArray, 20, collectionData));
			collectionData.setOtherAmt1(setBigDecimalData(dataArray, 21, collectionData));
			collectionData.setOtherCharge2(setLongData(dataArray, 22, collectionData));
			collectionData.setOtherAmt2(setBigDecimalData(dataArray, 23, collectionData));
			collectionData.setOtherCharge3(setLongData(dataArray, 24, collectionData));
			collectionData.setOtherAmt3(setBigDecimalData(dataArray, 25, collectionData));
			collectionData.setOtherCharge4(setLongData(dataArray, 26, collectionData));
			collectionData.setOtherAmt4(setBigDecimalData(dataArray, 27, collectionData));
			collectionData.setRemarks(setStringData(dataArray, 28, collectionData));
			collectionData.setBatched(setLongData(dataArray, 29, collectionData));
			collectionData.setRedepositionflg(setStringData(dataArray, 30, collectionData));
			collectionData.setRowNum(setLongData(dataArray, 31, collectionData));
			collectionData.setChecksum(setStringData(dataArray, 32, collectionData));
			return collectionData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String setStringData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		if (dataArray.length >= position) {
			return dataArray[position - 1];
		}
		return "";
	}

	private long setLongData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		if (dataArray.length >= position) {
			if (!"".equals(dataArray[position - 1])) {
				return Long.parseLong(dataArray[position - 1]);
			}
		}
		return 0L;
	}

	@SuppressWarnings("deprecation")
	private Date setDateData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		try {
			if (dataArray.length >= position) {
				return new Date(dataArray[position - 1]);
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private BigDecimal setBigDecimalData(String[] dataArray, int position, ExtCollectionReceiptData collectionData) {
		if (dataArray.length >= position) {
			if (!"".equals(dataArray[position - 1])) {
				return new BigDecimal(dataArray[position - 1]);
			}
		}
		return new BigDecimal(0);
	}

}