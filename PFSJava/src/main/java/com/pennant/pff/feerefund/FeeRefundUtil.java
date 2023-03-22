package com.pennant.pff.feerefund;

import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.pff.payment.model.PaymentHeader;

public class FeeRefundUtil {

	private FeeRefundUtil() {
		super();
	}

	public static PaymentInstruction getPI(FeeRefundInstruction fri) {
		PaymentInstruction pi = new PaymentInstruction();

		pi.setPaymentType(fri.getPaymentType());
		pi.setPaymentAmount(fri.getPaymentAmount());
		pi.setRemarks(fri.getRemarks());
		pi.setPartnerBankId(fri.getPartnerBankId());
		pi.setPartnerBankCode(fri.getPartnerBankCode());
		pi.setPartnerBankName(fri.getPartnerBankName());
		pi.setPartnerBankAcType(fri.getPartnerBankAc());
		pi.setPartnerBankAc(fri.getPartnerBankAc());
		pi.setIssuingBank(fri.getIssuingBank());
		pi.setIssuingBankName(fri.getIssuingBankName());
		pi.setFavourName(fri.getFavourName());
		pi.setFavourNumber(fri.getFavourNumber());
		pi.setPayableLoc(fri.getPayableLoc());
		pi.setPrintingLoc(fri.getPrintingLoc());
		pi.setPostDate(fri.getPostDate());
		pi.setValueDate(fri.getValueDate());
		pi.setBankBranchId(fri.getBankBranchId());
		pi.setBankBranchCode(fri.getBankBranchCode());
		pi.setBranchDesc(fri.getBranchDesc());
		pi.setBankName(fri.getBankName());
		pi.setBankBranchIFSC(fri.getBankBranchIFSC());
		pi.setpCCityName(fri.getpCCityName());
		pi.setAcctHolderName(fri.getAcctHolderName());
		pi.setAccountNo(fri.getAccountNo());
		pi.setPhoneNumber(fri.getPhoneNumber());
		pi.setPhoneCountryCode(fri.getPhoneCountryCode());
		pi.setClearingDate(fri.getClearingDate());
		pi.setStatus(fri.getStatus());
		pi.setActive(fri.getActive());
		pi.setPaymentCCy(fri.getPaymentCCy());
		pi.setLei(fri.getLei());

		return pi;
	}

	public static PaymentInstruction getPI(PaymentHeader ph, PaymentInstruction piTemp) {
		PaymentInstruction pi = ph.getPaymentInstruction();

		if (pi == null) {
			pi = new PaymentInstruction();
		}

		pi.setPaymentType(piTemp.getPaymentType());
		pi.setPaymentAmount(piTemp.getPaymentAmount());
		pi.setRemarks(piTemp.getRemarks());
		pi.setPartnerBankId(piTemp.getPartnerBankId());
		pi.setPartnerBankCode(piTemp.getPartnerBankCode());
		pi.setPartnerBankName(piTemp.getPartnerBankName());
		pi.setPartnerBankAcType(piTemp.getPartnerBankAc());
		pi.setPartnerBankAc(piTemp.getPartnerBankAc());
		pi.setIssuingBank(piTemp.getIssuingBank());
		pi.setIssuingBankName(piTemp.getIssuingBankName());
		pi.setFavourName(piTemp.getFavourName());
		pi.setFavourNumber(piTemp.getFavourNumber());
		pi.setPayableLoc(piTemp.getPayableLoc());
		pi.setPrintingLoc(piTemp.getPrintingLoc());
		pi.setPostDate(piTemp.getPostDate());
		pi.setValueDate(piTemp.getValueDate());
		pi.setBankBranchId(piTemp.getBankBranchId());
		pi.setBankBranchCode(piTemp.getBankBranchCode());
		pi.setBranchDesc(piTemp.getBranchDesc());
		pi.setBankName(piTemp.getBankName());
		pi.setBankBranchIFSC(piTemp.getBankBranchIFSC());
		pi.setpCCityName(piTemp.getpCCityName());
		pi.setAcctHolderName(piTemp.getAcctHolderName());
		pi.setAccountNo(piTemp.getAccountNo());
		pi.setPhoneNumber(piTemp.getPhoneNumber());
		pi.setPhoneCountryCode(piTemp.getPhoneCountryCode());
		pi.setClearingDate(piTemp.getClearingDate());
		pi.setStatus(piTemp.getStatus());
		pi.setActive(piTemp.getActive());
		pi.setPaymentCCy(piTemp.getPaymentCCy());
		pi.setLei(piTemp.getLei());

		return pi;
	}

	public static FeeRefundInstruction getFRI(FeeRefundHeader frh, PaymentInstruction pi) {
		FeeRefundInstruction fri = frh.getFeeRefundInstruction();

		if (fri == null) {
			fri = new FeeRefundInstruction();
		}

		fri.setPaymentType(pi.getPaymentType());
		fri.setPaymentAmount(pi.getPaymentAmount());
		fri.setRemarks(pi.getRemarks());
		fri.setPartnerBankId(pi.getPartnerBankId());
		fri.setPartnerBankCode(pi.getPartnerBankCode());
		fri.setPartnerBankName(pi.getPartnerBankName());
		fri.setPartnerBankAcType(pi.getPartnerBankAc());
		fri.setPartnerBankAc(pi.getPartnerBankAc());
		fri.setIssuingBank(pi.getIssuingBank());
		fri.setIssuingBankName(pi.getIssuingBankName());
		fri.setFavourName(pi.getFavourName());
		fri.setFavourNumber(pi.getFavourNumber());
		fri.setPayableLoc(pi.getPayableLoc());
		fri.setPrintingLoc(pi.getPrintingLoc());
		fri.setPostDate(pi.getPostDate());
		fri.setValueDate(pi.getValueDate());
		fri.setBankBranchId(pi.getBankBranchId());
		fri.setBankBranchCode(pi.getBankBranchCode());
		fri.setBranchDesc(pi.getBranchDesc());
		fri.setBankName(pi.getBankName());
		fri.setBankBranchIFSC(pi.getBankBranchIFSC());
		fri.setpCCityName(pi.getpCCityName());
		fri.setAcctHolderName(pi.getAcctHolderName());
		fri.setAccountNo(pi.getAccountNo());
		fri.setPhoneNumber(pi.getPhoneNumber());
		fri.setPhoneCountryCode(pi.getPhoneCountryCode());
		fri.setClearingDate(pi.getClearingDate());
		fri.setStatus(pi.getStatus());
		fri.setActive(pi.getActive());
		fri.setPaymentCCy(pi.getPaymentCCy());
		fri.setLei(pi.getLei());

		return fri;
	}

}
