package com.pennanttech.external.collectionreceipt.service;

import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;

public interface CollectionReceiptDataSplit {

	default ExtCollectionReceiptData splitAndSetData(String lineData) {
		ExtCollectionReceiptData collectionData = new ExtCollectionReceiptData();
		String[] dataArray = lineData.toString().split("\\|");
		collectionData.setAgreementNumber(TextFileUtil.getLongItem(dataArray, 1));
		collectionData.setCollection(TextFileUtil.getItem(dataArray, 2));
		collectionData.setReceiptChannel(TextFileUtil.getItem(dataArray, 3));
		collectionData.setAgencyId(TextFileUtil.getLongItem(dataArray, 4));
		collectionData.setChequeNumber(TextFileUtil.getLongItem(dataArray, 5));
		collectionData.setDealingBankId(TextFileUtil.getLongItem(dataArray, 6));
		collectionData.setDrawnOn(TextFileUtil.getItem(dataArray, 7));
		collectionData.setTowards(TextFileUtil.getItem(dataArray, 8));
		collectionData.setGrandTotal(TextFileUtil.getBigDecimalItem(dataArray, 9));
		collectionData.setReceiptDate(TextFileUtil.getItem(dataArray, 10));
		collectionData.setChequeDate(TextFileUtil.getItem(dataArray, 11));
		collectionData.setReceiptType(TextFileUtil.getItem(dataArray, 12));
		collectionData.setReceiptNumber(TextFileUtil.getLongItem(dataArray, 13));
		collectionData.setChequeStatus(TextFileUtil.getItem(dataArray, 14));
		collectionData.setAutoAlloc(TextFileUtil.getItem(dataArray, 15));
		collectionData.setEmiAmount(TextFileUtil.getBigDecimalItem(dataArray, 16));
		collectionData.setLppAmount(TextFileUtil.getBigDecimalItem(dataArray, 17));
		collectionData.setBccAmount(TextFileUtil.getBigDecimalItem(dataArray, 18));
		collectionData.setExcessAmount(TextFileUtil.getBigDecimalItem(dataArray, 19));
		collectionData.setOthercharge1(TextFileUtil.getItem(dataArray, 20));
		collectionData.setOtherAmt1(TextFileUtil.getBigDecimalItem(dataArray, 21));
		collectionData.setOtherCharge2(TextFileUtil.getItem(dataArray, 22));
		collectionData.setOtherAmt2(TextFileUtil.getBigDecimalItem(dataArray, 23));
		collectionData.setOtherCharge3(TextFileUtil.getItem(dataArray, 24));
		collectionData.setOtherAmt3(TextFileUtil.getBigDecimalItem(dataArray, 25));
		collectionData.setOtherCharge4(TextFileUtil.getItem(dataArray, 26));
		collectionData.setOtherAmt4(TextFileUtil.getBigDecimalItem(dataArray, 27));
		collectionData.setRemarks(TextFileUtil.getItem(dataArray, 28));
		collectionData.setBatched(TextFileUtil.getLongItem(dataArray, 29));
		collectionData.setRedepositionflg(TextFileUtil.getItem(dataArray, 30));
		collectionData.setRowNum(TextFileUtil.getLongItem(dataArray, 31));
		collectionData.setChecksum(TextFileUtil.getItem(dataArray, 32));

		return collectionData;
	}

}