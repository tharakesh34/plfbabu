package com.pennanttech.external.collectionreceipt.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennanttech.external.app.constants.ErrorCodesConstants;
import com.pennanttech.external.app.util.TextFileUtil;
import com.pennanttech.external.collectionreceipt.dao.ExtCollectionReceiptDao;
import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.ExtCollectionReceiptData;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class CollectionReceiptService implements ErrorCodesConstants {

	private ExtCollectionReceiptDao extCollectionReceiptDao;

	public ExtCollectionReceiptData prepareData(CollReceiptDetail collReceiptDetail) {
		ExtCollectionReceiptData collectionData = new ExtCollectionReceiptData();
		String[] dataArray = collReceiptDetail.getRecordData().split("\\|");
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

	public String calculateCheckSum(String[] dataArray, long rowNum) {

		long agreementNumber = TextFileUtil.getLongItem(dataArray, 1);
		BigDecimal grandTotal = TextFileUtil.getBigDecimalItem(dataArray, 9);
		String chequeDate = TextFileUtil.getItem(dataArray, 11);
		String receiptDate = TextFileUtil.getItem(dataArray, 10);
		String receiptType = TextFileUtil.getItem(dataArray, 12);

		int agreementCHK = generateChecksum(String.valueOf(agreementNumber));
		int grTotalCHK = generateChecksum(String.valueOf(grandTotal));
		int chqDateCHK = generateChecksum(String.valueOf(chequeDate));
		int receiptDateCHK = generateChecksum(String.valueOf(receiptDate));
		int chqTypeCHK = generateChecksum(String.valueOf(receiptType));

		int totalChk = agreementCHK + grTotalCHK + chqDateCHK + receiptDateCHK + chqTypeCHK;

		return rowNum + "" + totalChk;
	}

	private int generateChecksum(String data) {
		int rcdCS = 0;
		for (int i = 0; i < data.length(); i++) {
			char digit = data.charAt(i);
			int asciiCode = digit;
			rcdCS = rcdCS + asciiCode;
		}
		return rcdCS;
	}

	public CreateReceiptUpload getCreateReceiptUploadBean(ExtCollectionReceiptData collectionData) {
		boolean isCheque = false;
		CreateReceiptUpload cru = new CreateReceiptUpload();
		cru.setReference(String.valueOf(collectionData.getAgreementNumber()));
		cru.setReferenceID(collectionData.getReceiptNumber());
		cru.setAllocationType("M");
		cru.setAppDate(SysParamUtil.getAppDate());
		cru.setValueDate(getFormattedDate(collectionData.getReceiptDate()));
		cru.setReceiptModeStatus("R");
		cru.setRealizationDate(getFormattedDate(collectionData.getReceiptDate()));
		cru.setReceiptAmount(getAbsoluteAmount(collectionData.getGrandTotal()));
		cru.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		cru.setReceiptPurpose("SP");
		cru.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		cru.setReceiptChannel(PennantConstants.List_Select);
		// collectionData
		if ("Q".equals(StringUtils.stripToEmpty(collectionData.getReceiptType()))) {
			isCheque = true;
		}

		if (isCheque) {
			cru.setReceiptMode(ReceiptMode.CHEQUE);
			cru.setDepositDate(getFormattedDate(collectionData.getChequeDate()));
			cru.setChequeNumber(String.valueOf(collectionData.getChequeNumber()));
		} else {
			cru.setReceiptMode(ReceiptMode.CASH);
			cru.setDepositDate(getFormattedDate(collectionData.getReceiptDate()));
		}
		cru.setBankCode(String.valueOf(collectionData.getDealingBankId()));
		cru.setEffectSchdMethod("");

		List<CreateReceiptUpload> alloc = new ArrayList<CreateReceiptUpload>();

		if (collectionData.getBccAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc1 = new CreateReceiptUpload();
			alloc1.setCode(Allocation.BOUNCE);
			alloc1.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getBccAmount()))));
			alloc.add(alloc1);
		}
		if (collectionData.getLppAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc2 = new CreateReceiptUpload();
			alloc2.setCode(Allocation.ODC);
			alloc2.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getLppAmount()))));
			alloc.add(alloc2);
		}
		if (collectionData.getEmiAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc3 = new CreateReceiptUpload();
			alloc3.setCode(Allocation.EMI);
			alloc3.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getEmiAmount()))));
			alloc.add(alloc3);
		}
		if (collectionData.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc4 = new CreateReceiptUpload();
			alloc4.setCode("ADVEMI");
			alloc4.setAmount(
					getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getExcessAmount() + ".00"))));
			alloc.add(alloc4);
		}

		if (collectionData.getOtherAmt1().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc5 = new CreateReceiptUpload();
			alloc5.setCode(String.valueOf(collectionData.getOthercharge1()));
			alloc5.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt1()))));
			alloc.add(alloc5);
		}

		if (collectionData.getOtherAmt2().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc6 = new CreateReceiptUpload();
			alloc6.setCode(String.valueOf(collectionData.getOtherCharge2()));
			alloc6.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt2()))));
			alloc.add(alloc6);
		}

		if (collectionData.getOtherAmt3().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc7 = new CreateReceiptUpload();
			alloc7.setCode(String.valueOf(collectionData.getOtherCharge3()));
			alloc7.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt3()))));
			alloc.add(alloc7);
		}

		if (collectionData.getOtherAmt4().compareTo(BigDecimal.ZERO) > 0) {
			CreateReceiptUpload alloc8 = new CreateReceiptUpload();
			alloc8.setCode(String.valueOf(collectionData.getOtherCharge4()));
			alloc8.setAmount(getAbsoluteAmount(getRoundAmount(String.valueOf(collectionData.getOtherAmt4()))));
			alloc.add(alloc8);
		}

		cru.setAllocations(alloc);
		return cru;
	}

	private BigDecimal getAbsoluteAmount(BigDecimal amt) {
		String amtt = String.valueOf(amt);
		if (amtt.contains(".")) {
			amtt = amtt.replace(".", "");
		}
		return new BigDecimal(amtt);
	}

	private BigDecimal getRoundAmount(String strAmount) {
		BigDecimal convertedAmt = new BigDecimal(strAmount);
		return convertedAmt.setScale(2);
	}

	private Date getFormattedDate(String strDate) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
			return formatter.parse(strDate);
		} catch (Exception e) {
			return null;
		}

	}

	public void dataValidations(CollReceiptDetail extRcd, ExtCollectionReceiptData collectionData) {
		if (collectionData.getAgreementNumber() == 0) {
			extRcd.setErrorCode(CR2000);
			return;
		}
		if (StringUtils.trimToEmpty(collectionData.getReceiptChannel()).isEmpty()) {
			extRcd.setErrorCode(CR2001);
			return;
		}

		if (collectionData.getAgencyId() == 0) {
			extRcd.setErrorCode(CR2002);
			return;
		}

		if (StringUtils.trimToEmpty(collectionData.getReceiptType()).isEmpty()) {
			extRcd.setErrorCode(CR2003);
			return;
		}

		if (collectionData.getDealingBankId() == 0) {
			extRcd.setErrorCode(CR2006);
			return;
		}

		if (StringUtils.trimToEmpty(collectionData.getReceiptDate()).isEmpty()) {
			extRcd.setErrorCode(CR2007);
			return;
		}

		if (DateUtil.compare(getFormattedDate(collectionData.getReceiptDate()), SysParamUtil.getAppDate()) > 0) {
			extRcd.setErrorCode(CR2008);
			return;
		}

		if (collectionData.getExcessAmount().compareTo(collectionData.getGrandTotal()) > 0) {
			extRcd.setErrorCode(CR2010);
			return;
		}

		List<String> validTypes = new ArrayList<>();
		validTypes.add("C");
		validTypes.add("Q");

		if (!StringUtils.trimToEmpty(collectionData.getReceiptType()).isEmpty()) {
			if (!validTypes.contains(collectionData.getReceiptType())) {
				extRcd.setErrorCode(CR2004);
				return;
			}
		}

		if (!StringUtils.trimToEmpty(collectionData.getReceiptType()).isEmpty()) {
			if ("Q".equalsIgnoreCase(StringUtils.trimToEmpty(collectionData.getReceiptType()))) {
				if (collectionData.getChequeNumber() == 0) {
					extRcd.setErrorCode(CR2005);
					return;
				}

				if (StringUtils.trimToEmpty(collectionData.getChequeDate()).isEmpty()) {
					extRcd.setErrorCode(CR2009);
					return;
				}

				if (DateUtil.compare(getFormattedDate(collectionData.getChequeDate()),
						getFormattedDate(collectionData.getReceiptDate())) > 0) {
					extRcd.setErrorCode(CR2008);
					return;
				}
			}
		}

		if ((!StringUtils.trimToEmpty(collectionData.getReceiptType()).isEmpty()) && (collectionData.getAgencyId() != 0)
				&& (collectionData.getAgreementNumber() != 0)) {
			boolean isAgreementFound = extCollectionReceiptDao
					.validateAgreementNumber(String.valueOf(collectionData.getAgreementNumber()));
			if (!isAgreementFound) {
				extRcd.setErrorCode(CR2011);
				return;
			}

			boolean isAgencyIdFound = extCollectionReceiptDao.validateAgencyId(collectionData.getAgencyId());
			if (!isAgencyIdFound) {
				extRcd.setErrorCode(CR2012);
				return;
			}

			List<ValueLabel> receiptTypes = PennantStaticListUtil.getReceiptChannels();
			List<String> receiptValues = new ArrayList<>();

			for (ValueLabel val : receiptTypes) {
				receiptValues.add(val.getValue());
			}

			if (!receiptValues.contains(collectionData.getReceiptChannel())) {
				extRcd.setErrorCode(CR2013);
			}
		}

	}

	public void setExtCollectionReceiptDao(ExtCollectionReceiptDao extCollectionReceiptDao) {
		this.extCollectionReceiptDao = extCollectionReceiptDao;
	}

}