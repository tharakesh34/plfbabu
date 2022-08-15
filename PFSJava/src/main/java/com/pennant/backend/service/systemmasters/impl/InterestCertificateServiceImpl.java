/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : InterestCertficateServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.systemmasters.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.VasMovementDetailDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.systemmasters.InterestCertificateDAO;
import com.pennant.backend.model.agreement.CovenantAggrement;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.AgreementDetail.CoApplicant;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.service.extended.fields.ExtendedFieldService;

/**
 * Service implementation for methods that depends on <b>InterestCertficate</b>.<br>
 * 
 */
public class InterestCertificateServiceImpl extends GenericService<InterestCertificate>
		implements InterestCertificateService {
	private static Logger logger = LogManager.getLogger(InterestCertificateServiceImpl.class);

	private ExtendedFieldService extendedFieldService;
	private InterestCertificateDAO interestCertificateDAO;
	private FinanceMainDAO financeMainDAO;
	private VASRecordingDAO vASRecordingDAO;
	private VasMovementDetailDAO vasMovementDetailDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	int format = 2;

	public InterestCertificateServiceImpl() {
		super();
	}

	@Override
	public InterestCertificate getInterestCertificateDetails(String finReference, Date startDate, Date endDate,
			boolean isProvCert) {
		logger.debug(Literal.ENTERING);
		BigDecimal totalvasAmt = BigDecimal.ZERO;
		BigDecimal totalLoanAmt = BigDecimal.ZERO;
		BigDecimal totalDisbAmt = BigDecimal.ZERO;
		BigDecimal loanRatio = BigDecimal.ZERO;
		BigDecimal vasRatio = BigDecimal.ZERO;

		InterestCertificate intCert = interestCertificateDAO.getInterestCertificateDetails(finReference);

		if (intCert == null) {
			return null;
		}

		/*
		 * InterestCertificate certificate = null; if (isProvCert) { certificate =
		 * interestCertificateDAO.getSumOfPrinicipalAndProfitAmount(finReference, startDate, endDate); } else {
		 * certificate = interestCertificateDAO.getSumOfPrinicipalAndProfitAmountPaid(finReference, startDate, endDate);
		 * }
		 */
		// Get Co-Applicants

		List<Customer> coApplicantList = interestCertificateDAO.getCoApplicantNames(finReference);
		StringBuilder coapplicant = new StringBuilder();
		for (Customer coApplicant : coApplicantList) {
			coapplicant.append(",").append(StringUtils.trimToEmpty(coApplicant.getCustShrtName()));
			AgreementDetail agreementDetail = new AgreementDetail();
			CoApplicant coApp = agreementDetail.new CoApplicant();
			coApp.setCustName(StringUtils.trimToEmpty(coApplicant.getCustShrtName()));
			coApp.setCustSalutation(StringUtils.trimToEmpty(coApplicant.getCustSalutationCode()));
			intCert.getCoApplicantList().add(coApp);
		}

		if (CollectionUtils.isEmpty(coApplicantList)) {
			AgreementDetail agreementDetail = new AgreementDetail();
			CoApplicant coApp = agreementDetail.new CoApplicant();
			intCert.getCoApplicantList().add(coApp);
		}

		if (StringUtils.contains(coapplicant, ",")) {
			String coAppNames = coapplicant.toString();
			coapplicant.replace(coAppNames.lastIndexOf(","), coAppNames.lastIndexOf(",") + 1, " and ");
		}

		intCert.setCoApplicant(coapplicant.toString());
		// set Loan ExtendedFields
		if (extendedFieldService != null) {
			extendedFieldService.setExtendedFields(intCert);
		}

		format = CurrencyUtil.getFormat(intCert.getFinCcy());
		InterestCertificate summary = interestCertificateDAO.getSumOfPrinicipalAndProfitAmount(finReference, startDate,
				endDate);
		Map<String, Object> amounts = interestCertificateDAO.getSumOfPriPftEmiAmount(finReference, startDate, endDate);

		if (summary != null && summary.getFinSchdPftPaid() != null && summary.getFinSchdPriPaid() != null) {
			String finSchdPftPaid = PennantApplicationUtil.amountFormate(summary.getFinSchdPftPaid(), format);
			finSchdPftPaid = finSchdPftPaid.replace(",", "");
			String finSchdPriPaid = PennantApplicationUtil.amountFormate(summary.getFinSchdPriPaid(), format);
			finSchdPriPaid = finSchdPriPaid.replace(",", "");
			intCert.setFinSchdPftPaid(new BigDecimal(finSchdPftPaid));
			intCert.setFinSchdPriPaid(new BigDecimal(finSchdPriPaid));
			intCert.setSchdPftPaid(PennantApplicationUtil.amountFormate(summary.getFinSchdPftPaid(), format));
			intCert.setSchdPriPaid(PennantApplicationUtil.amountFormate(summary.getFinSchdPriPaid(), format));
			intCert.setTotalPaid(PennantApplicationUtil
					.amountFormate(summary.getFinSchdPriPaid().add(summary.getFinSchdPftPaid()), format));
			try {
				intCert.setSchdPftPaidInWords(
						summary.getFinSchdPftPaid() == BigDecimal.ZERO ? ""
								: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
										PennantApplicationUtil.formateAmount(summary.getFinSchdPftPaid(), format),
										"")));
				intCert.setSchdPriPaidInWords(
						summary.getFinSchdPriPaid() == BigDecimal.ZERO ? ""
								: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
										PennantApplicationUtil.formateAmount(summary.getFinSchdPriPaid(), format),
										"")));
				intCert.setTotalPaidInWords(
						summary.getFinSchdPftPaid().add(summary.getFinSchdPriPaid()) == BigDecimal.ZERO ? ""
								: WordUtils.capitalize(NumberToEnglishWords.getAmountInText(
										PennantApplicationUtil.formateAmount(
												summary.getFinSchdPriPaid().add(summary.getFinSchdPftPaid()), format),
										"")));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		} else {
			intCert.setFinSchdPftPaid(new BigDecimal(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format)));
			intCert.setFinSchdPriPaid(new BigDecimal(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format)));
			intCert.setSchdPftPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format));
			intCert.setSchdPriPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format));
			intCert.setTotalPaid(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, format));
		}

		intCert.setFinAmount(PennantApplicationUtil.amountFormate(new BigDecimal(intCert.getFinAmount()), format));
		String assetVal = intCert.getFinAssetvalue();
		intCert.setFinAssetvalue(PennantApplicationUtil.amountFormate(new BigDecimal(assetVal), format));

		if (amounts != null && !amounts.isEmpty()) {

			BigDecimal pftSchd = (BigDecimal) amounts.get("profitschd");
			BigDecimal priSchd = (BigDecimal) amounts.get("principalschd");
			BigDecimal emiAmount = (BigDecimal) amounts.get("repayamount");
			intCert.setPftSchd(PennantApplicationUtil.amountFormate(pftSchd, format));
			intCert.setPriSchd(PennantApplicationUtil.amountFormate(priSchd, format));
			intCert.setEmiAmt(PennantApplicationUtil.amountFormate(emiAmount, format));

			try {
				intCert.setPftSchdInWords(pftSchd == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords
								.getAmountInText(PennantApplicationUtil.formateAmount(pftSchd, format), "")));
				intCert.setPriSchdInWords(priSchd == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords
								.getAmountInText(PennantApplicationUtil.formateAmount(priSchd, format), "")));
				intCert.setEmiAmtInWords(emiAmount == BigDecimal.ZERO ? ""
						: WordUtils.capitalize(NumberToEnglishWords
								.getAmountInText(PennantApplicationUtil.formateAmount(emiAmount, format), "")));
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}

		}

		// Grace repay Details
		Map<String, Object> graceRepayMap = interestCertificateDAO.getTotalGrcRepayProfit(finReference, startDate,
				endDate);

		if (graceRepayMap != null && !graceRepayMap.isEmpty()) {
			BigDecimal grcPft = (BigDecimal) graceRepayMap.get("grcpft");
			BigDecimal grcPftPaid = (BigDecimal) graceRepayMap.get("grcpftpaid");
			intCert.setGrcPft(PennantApplicationUtil.amountFormate(grcPft, format));
			intCert.setGrcPftPaid(PennantApplicationUtil.amountFormate(grcPftPaid, format));
		}
		intCert.setTotOustandingamt(
				PennantApplicationUtil.amountFormate(new BigDecimal(intCert.getTotOustandingamt()), format));
		intCert.setTotalPftBal(PennantApplicationUtil.amountFormate(new BigDecimal(intCert.getTotalPftBal()), format));
		intCert.setTotalPriBal(PennantApplicationUtil.amountFormate(new BigDecimal(intCert.getTotalPriBal()), format));
		intCert.setFinCurrassetValue(
				PennantApplicationUtil.amountFormate(new BigDecimal(intCert.getFinCurrassetValue()), format));

		// The bellow is the setting for the interest amount and principle amounts based on financial year related to
		// Interest certificate agreement.
		InterestCertificate interestCert = interestCertificateDAO.getSchedPrinicipalAndProfit(finReference, startDate,
				endDate);
		if (interestCert != null && interestCert.getRepayPftAmt() != null && interestCert.getRepayPriAmt() != null) {

			intCert.setRepayPftAmtStr(PennantApplicationUtil.amountFormate(interestCert.getRepayPftAmt(), format));
			intCert.setRepayPriAmtStr(PennantApplicationUtil.amountFormate(interestCert.getRepayPriAmt(), format));

			intCert.setTotRepayAmount(PennantApplicationUtil
					.amountFormate(interestCert.getRepayPftAmt().add(interestCert.getRepayPriAmt()), format));

			intCert.setSchdPftPaidStr(PennantApplicationUtil.amountFormate(interestCert.getSchdProfitPaid(), format));
			intCert.setSchdPriPaidStr(
					PennantApplicationUtil.amountFormate(interestCert.getSchdPrinciplePaid(), format));

			intCert.setTotSchdAmount(PennantApplicationUtil
					.amountFormate(interestCert.getSchdProfitPaid().add(interestCert.getSchdPrinciplePaid()), format));
		}

		// collateral address setup
		String collateralRef = interestCertificateDAO.getCollateralRef(intCert.getFinReference());
		if (collateralRef != null) {
			String collateralType = interestCertificateDAO.getCollateralType(collateralRef);
			if (collateralType != null) {
				int fields = SysParamUtil.getValueAsInt("COL_ADDR_FIELD_COUNT");
				for (int j = 1; j <= fields; j++) {
					String ColumnField = interestCertificateDAO.getCollateralTypeField("PROVISIONALCERTIFICATE",
							"COLLATERAL_" + collateralType + "_ED", "Addresstype" + j);
					if (ColumnField != null) {
						String ColumnValue = interestCertificateDAO.getCollateralTypeValue(
								"COLLATERAL_" + collateralType + "_ED", ColumnField, collateralRef);
						if (ColumnValue != null) {
							try {
								intCert.getClass().getMethod("setAddressType" + j, new Class[] { String.class })
										.invoke(intCert, ColumnValue);
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
							}
						}
					}
				}
			}
		}
		calculatePPAmounts(intCert, finReference, startDate, endDate, isProvCert);

		// Ratio based division for Loan repay amount and Insurance amount.
		if (ImplementationConstants.ALLOW_LOAN_VAS_RATIO_CALC) {
			List<BigDecimal> amountsByRef = getAmountsByRef(finReference, intCert.getFinCurrAssetvalue(), startDate,
					endDate, totalvasAmt, totalLoanAmt, totalDisbAmt, loanRatio, vasRatio);
			vasRatio = amountsByRef.get(1);
			loanRatio = amountsByRef.get(2);
			BigDecimal priPaid = intCert.getPriPaidSch();
			BigDecimal pftPaid = intCert.getPftPaidSch();
			BigDecimal totalPaid = priPaid.add(pftPaid);

			BigDecimal principalPaid = priPaid.multiply(loanRatio);
			principalPaid = principalPaid.setScale(format, RoundingMode.HALF_UP);
			BigDecimal principalPaidVAS = priPaid.multiply(vasRatio);
			principalPaidVAS = principalPaidVAS.setScale(format, RoundingMode.HALF_UP);
			BigDecimal profitPaid = pftPaid.multiply(loanRatio);
			profitPaid = profitPaid.setScale(format, RoundingMode.HALF_UP);
			BigDecimal profitPaidVAS = pftPaid.multiply(vasRatio);
			profitPaidVAS = profitPaidVAS.setScale(format, RoundingMode.HALF_UP);

			intCert.setPriPaid(PennantApplicationUtil.amountFormate(principalPaid, format));
			intCert.setVasPriPaid(PennantApplicationUtil.amountFormate(principalPaidVAS, format));
			intCert.setPftPaid(PennantApplicationUtil.amountFormate(profitPaid, format));
			intCert.setVasPftPaid(PennantApplicationUtil.amountFormate(profitPaidVAS, format));

			intCert.setTotalPaid(PennantApplicationUtil.amountFormate(totalPaid, format));
			intCert.setSchdPftPaid(PennantApplicationUtil.amountFormate(pftPaid, format));
			intCert.setSchdPriPaid(PennantApplicationUtil.amountFormate(priPaid, format));

		}
		logger.debug(Literal.LEAVING);
		return intCert;

	}

	private BigDecimal getPPAmtFromAllowcations(long receiptID) {
		// Does we need to consider the part Pay amounts which are made with in FY?
		// Receipt Allocations
		BigDecimal ppAmount = BigDecimal.ZERO;
		List<ReceiptAllocationDetail> allocationDetails = receiptAllocationDetailDAO
				.getAllocationsByReceiptID(receiptID, "");
		for (ReceiptAllocationDetail allocationDetail : allocationDetails) {
			// Part Payment
			if (RepayConstants.ALLOCATION_PP.equals(allocationDetail.getAllocationType())) {
				ppAmount = ppAmount.add(allocationDetail.getPaidAmount());
			}
		}

		return ppAmount;
	}

	private void calculatePPAmounts(InterestCertificate intCert, String finReference, Date fromDate, Date toDate,
			boolean isProvCert) {
		// Sum of PftScd,PriScd,PartPaid Amount,SchdPftPaid,SchdPriPaid
		FinanceScheduleDetail scheduleDetail = interestCertificateDAO.getScheduleDetailsByFinReference(finReference,
				fromDate, toDate);

		if (scheduleDetail == null) {
			return;
		}

		// Deductions: Need to get the Part Payments(Receipt Header,Details)
		BigDecimal rcPPAmount = BigDecimal.ZERO;
		BigDecimal rcExgAtmount = BigDecimal.ZERO;
		BigDecimal rcPmayAmount = BigDecimal.ZERO;
		BigDecimal rpSchdPriPaid = BigDecimal.ZERO;
		BigDecimal rpSchdPftPaid = BigDecimal.ZERO;

		String exGratiaTxt = SysParamUtil.getValueAsString(SMTParameterConstants.TRANSACTIONREF_TXT_IN_RECEIPT);

		if (finReceiptHeaderDAO != null && finReceiptDetailDAO != null) {
			List<FinReceiptHeader> frhs = finReceiptHeaderDAO.getReceiptHeaderByID(finReference,
					FinServiceEvent.EARLYRPY, fromDate, toDate, "");
			// Receipt Header
			if (CollectionUtils.isNotEmpty(frhs)) {
				for (FinReceiptHeader frh : frhs) {
					// Receipt Details
					long receiptID = frh.getReceiptID();
					List<FinReceiptDetail> frds = finReceiptDetailDAO.getReceiptHeaderByID(receiptID, "");

					if (CollectionUtils.isNotEmpty(frds)) {
						for (FinReceiptDetail frd : frds) {
							// EX Gratia
							String transactionRef = frd.getTransactionRef();
							if (StringUtils.isNotEmpty(transactionRef)) {
								// Ex Gratia
								if (transactionRef.contains(exGratiaTxt)) {
									rcExgAtmount = rcExgAtmount.add(getPPAmtFromAllowcations(receiptID));
								}
								// PMAY
							} else if (RepayConstants.RECEIVED_GOVT.equals(frh.getReceivedFrom())) {
								rcPmayAmount = rcPmayAmount.add(getPPAmtFromAllowcations(receiptID));
								// PP
							} else {
								rcPPAmount = rcPPAmount.add(getPPAmtFromAllowcations(receiptID));
							}
						}
					}

				}
			}
		}
		// Get the total partpay amount which has been PAID next FY
		InterestCertificate certificate = interestCertificateDAO.getRepayDetails(finReference, fromDate, toDate);
		if (certificate != null) {
			if (certificate.getFinSchdPriPaid() != null) {
				// Future Principle PAID as per FY
				rpSchdPriPaid = rpSchdPriPaid.add(certificate.getFinSchdPriPaid());
			}
			if (certificate.getFinSchdPftPaid() != null) {
				// Future Profit PAID as per FY
				rpSchdPftPaid = rpSchdPftPaid.add(certificate.getFinSchdPftPaid());
			}
		}

		BigDecimal totPftSchd = scheduleDetail.getProfitSchd();
		BigDecimal totPriSchd = scheduleDetail.getPrincipalSchd();

		BigDecimal totPftPaid = scheduleDetail.getSchdPftPaid();
		BigDecimal totPriPaid = scheduleDetail.getSchdPriPaid();
		// Principal Deductions
		BigDecimal totPriDeduction = rcPmayAmount.add(rcExgAtmount).add(rpSchdPriPaid);
		// Profit Deductions
		BigDecimal totPftDeduction = rpSchdPftPaid;

		BigDecimal reportPriAmount = BigDecimal.ZERO;
		BigDecimal reportPftAmount = BigDecimal.ZERO;
		if (isProvCert) {
			reportPriAmount = totPriSchd.subtract(totPriDeduction);
			reportPftAmount = totPftSchd.subtract(totPftDeduction);
		} else {
			reportPriAmount = totPriPaid.subtract(totPriDeduction);
			reportPftAmount = totPftPaid.subtract(totPftDeduction);
		}
		intCert.setPriPaidSch(reportPriAmount);
		intCert.setPftPaidSch(reportPftAmount);

		intCert.setSchdPftPaid(PennantApplicationUtil.amountFormate(intCert.getPftPaidSch(), format));
		intCert.setSchdPriPaid(PennantApplicationUtil.amountFormate(intCert.getPriPaidSch(), format));
		intCert.setTotalPaid(
				PennantApplicationUtil.amountFormate(intCert.getPftPaidSch().add(intCert.getPriPaidSch()), format));

	}

	public List<BigDecimal> getAmountsByRef(String finReference, BigDecimal finCurrAssetValue, Date startDate,
			Date endDate, BigDecimal totalvasAmt, BigDecimal totalLoanAmt, BigDecimal totalDisbAmt,
			BigDecimal loanRatio, BigDecimal vasRatio) {
		logger.debug(Literal.ENTERING);
		// total disbursement amount
		totalDisbAmt = PennantApplicationUtil.formateAmount(finCurrAssetValue, 2);
		totalvasAmt = processVasRecordingDetails(finReference, startDate, endDate, totalvasAmt);
		List<BigDecimal> calculateRatio = calculateRatio(totalvasAmt, totalLoanAmt, totalDisbAmt, loanRatio, vasRatio);
		logger.debug(Literal.LEAVING);
		return calculateRatio;
	}

	private List<BigDecimal> calculateRatio(BigDecimal totalvasAmt, BigDecimal totalLoanAmt, BigDecimal totalDisbAmt,
			BigDecimal loanRatio, BigDecimal vasRatio) {
		List<BigDecimal> list = new ArrayList<>();
		// Considering the total loan amount as totalDisbAmt + totalvasAmt for calculation
		totalLoanAmt = totalDisbAmt.add(totalvasAmt);
		// calculating the total loan ratio from total loan amount including VAS amount
		if (totalvasAmt.compareTo(BigDecimal.ZERO) > 0 && totalLoanAmt.compareTo(BigDecimal.ZERO) > 0) {
			// vas ratio
			vasRatio = totalvasAmt.divide(totalLoanAmt, 2, RoundingMode.HALF_UP);
		}
		// calculating the total loan ratio from total loan amount including VAS
		if (totalDisbAmt.compareTo(BigDecimal.ZERO) > 0 && totalLoanAmt.compareTo(BigDecimal.ZERO) > 0) {
			// loan ratio
			loanRatio = totalDisbAmt.divide(totalLoanAmt, 2, RoundingMode.HALF_UP);
		}
		list.add(totalLoanAmt);
		list.add(vasRatio);
		list.add(loanRatio);
		return list;

	}

	private BigDecimal processVasRecordingDetails(String finReference, Date startDate, Date endDate,
			BigDecimal totalvasAmt) {
		logger.debug(Literal.ENTERING);
		List<VASRecording> recordings = vASRecordingDAO.getVASRecordingsByLinkRef(finReference, "");
		// calculate totalVasAmt
		if (CollectionUtils.isNotEmpty(recordings)) {
			for (VASRecording vasRecording : recordings) {
				totalvasAmt = totalvasAmt.add(PennantApplicationUtil.formateAmount(vasRecording.getFee(), 2));
			}
		}
		// Get VAS Movements
		BigDecimal movementAmount = vasMovementDetailDAO.getVasMovementDetailByRef(finReference, startDate, endDate,
				"");
		if (movementAmount.compareTo(BigDecimal.ZERO) > 0 && totalvasAmt.compareTo(BigDecimal.ZERO) > 0) {
			movementAmount = PennantApplicationUtil.formateAmount(movementAmount, 2);
			// Reduce the movement amount from total VAS outstanding amount.
			totalvasAmt = totalvasAmt.subtract(movementAmount);
		}
		logger.debug(Literal.LEAVING);
		return totalvasAmt;
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, TableType tabelType) {
		return financeMainDAO.getFinanceMain(finReference, tabelType);
	}

	@Override
	public List<CovenantAggrement> getCovenantReportStatus(String finreference) {
		return interestCertificateDAO.getCovenantReportStatus(finreference);
	}

	public void setInterestCertificateDAO(InterestCertificateDAO interestCertificateDAO) {
		this.interestCertificateDAO = interestCertificateDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired(required = false)
	public void setExtendedFieldService(ExtendedFieldService extendedFieldService) {
		this.extendedFieldService = extendedFieldService;
	}

	@Autowired(required = false)
	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	@Autowired(required = false)
	public void setVasMovementDetailDAO(VasMovementDetailDAO vasMovementDetailDAO) {
		this.vasMovementDetailDAO = vasMovementDetailDAO;
	}

	@Autowired(required = false)
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired(required = false)
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired(required = false)
	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

}