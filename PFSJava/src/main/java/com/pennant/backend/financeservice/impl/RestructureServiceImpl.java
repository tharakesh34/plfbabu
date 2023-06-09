package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.LatePayMarkingService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.RestructureDAO;
import com.pennant.backend.dao.financemanagement.FinanceStepDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.RestructureType;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennapps.core.util.ObjectUtil;

public class RestructureServiceImpl extends GenericService<FinServiceInstruction> implements RestructureService {
	private static final Logger logger = LogManager.getLogger(RestructureServiceImpl.class);

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private RestructureDAO restructureDAO;
	private FinanceMainDAO financeMainDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private LatePayMarkingService latePayMarkingService;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FeeTypeDAO feeTypeDAO;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private FinanceTypeDAO financeTypeDAO;
	private BaseRateDAO baseRateDAO;
	private FinanceStepDetailDAO financeStepDetailDAO;
	private MandateDAO mandateDAO;

	@Override
	public FinScheduleData doRestructure(FinScheduleData schdData, FinServiceInstruction fsi) {
		return restrcutureSchedule(schdData, fsi);
	}

	private FinScheduleData restrcutureSchedule(FinScheduleData schdData, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		RestructureDetail rd = schdData.getRestructureDetail();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		// Setting of Loan information Before image data on Restructuring Details
		FinanceProfitDetail fpd = getFinProfitDetailsById(finID);

		fsi.setFinID(finID);
		fsi.setFinReference(finReference);
		fsi.setFinEvent(FinServiceEvent.RESTRUCTURE);

		rd.setFinID(finID);
		rd.setFinReference(finReference);
		rd.setOldTenure(fpd.getNOInst());
		rd.setOldBalTenure(fpd.getFutureInst());
		rd.setOldMaturity(fm.getMaturityDate());
		rd.setOldInterest(fpd.getTotalPftSchd());
		rd.setOldCpzInterest(fpd.getTotalPftCpz());

		// As per BHFL team request we have consider the final EMI amount from last second schedule.
		FinanceScheduleDetail lastSchd = schedules.get(schedules.size() - 2);
		rd.setOldFinalEmi(lastSchd.getRepayAmount());
		rd.setRepayProfitRate(lastSchd.getCalculatedRate());
		rd.setOldBucket(financeMainDAO.getBucketByFinStatus(fm.getFinID()));
		rd.setOldDpd(fpd.getCurODDays());
		rd.setOldEmiOs((fpd.getTotalpriSchd().add(fpd.getTotalPftSchd()))
				.subtract(fpd.getTdSchdPri().add(fpd.getTdSchdPft())));
		rd.setOldMaxUnplannedEmi(fm.getMaxUnplannedEmi());
		rd.setOldAvailedUnplanEmi(fm.getAvailedUnPlanEmi());
		rd.setActLoanAmount(fm.getFinAssetValue().add(fm.getFeeChargeAmt()));
		rd.setFinCurrAssetValue(fm.getFinCurrAssetValue().add(fm.getFeeChargeAmt()));
		rd.setLastBilledDate(fpd.getPrvRpySchDate());
		rd.setAppDate(SysParamUtil.getAppDate());
		rd.setOldPOsAmount(fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
		rd.setOldEmiOverdue(fpd.getTdSchdPri().subtract(fpd.getTotalPriPaid()));

		BigDecimal otherCharge = getReceivableAmt(finID, false);
		rd.setOtherCharge(otherCharge);

		BigDecimal bounceCharge = getReceivableAmt(finID, true);
		rd.setBounceCharge(bounceCharge);

		BigDecimal penaltyAmount = getTotalPenaltyBal(finID, null);
		rd.setOldPenaltyAmount(penaltyAmount);

		rd.setRestructureCharge(BigDecimal.ZERO);
		rd.setNewExtOdDays(fpd.getExtODDays());
		rd.setOldEmiOverdue(fpd.getODProfit().add(fpd.getODPrincipal()));

		// Removing Future Repay Instructions if any exists which are greater than or equal to Maturity Date
		List<RepayInstruction> riList = schdData.getRepayInstructions();
		if (riList.size() > 1) {
			RepayInstruction ri = riList.get(riList.size() - 1);
			if (ri.getRepayDate().compareTo(fm.getMaturityDate()) >= 0) {
				riList.remove(riList.size() - 1);
			}
		}
		schdData.setRepayInstructions(riList);

		// Setting Restructure details for Further calculation Usage
		schdData.setRestructureDetail(rd);

		BigDecimal oldTotalPft = schdData.getFinanceMain().getTotalGrossPft();
		FinScheduleData scheduleData = null;
		scheduleData = ObjectUtil.clone(schdData);

		FinanceScheduleDetail curSchd = null;
		if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
			boolean isSchdRcdFOund = false;
			int prvIndex = 0;
			for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
				curSchd = scheduleData.getFinanceScheduleDetails().get(i);
				if (curSchd.getSchDate().compareTo(rd.getRestructureDate()) == 0) {
					isSchdRcdFOund = true;
					break;
				} else if (curSchd.getSchDate().compareTo(rd.getRestructureDate()) < 0) {
					prvIndex = i;
				} else {
					break;
				}
			}

			if (!isSchdRcdFOund) {
				scheduleData = addSchdRcd(scheduleData, rd.getRestructureDate(), prvIndex);
				scheduleData.setFinanceScheduleDetails(sortSchdDetails(scheduleData.getFinanceScheduleDetails()));
			}
		}

		// Rate Modification for All Modified Schedules
		if (ImplementationConstants.RESTRUCTURE_RATE_CHG_ALW) {
			for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
				curSchd = scheduleData.getFinanceScheduleDetails().get(i);
				if (curSchd.getSchDate().compareTo(rd.getRestructureDate()) >= 0) {
					if (StringUtils.isEmpty(fsi.getBaseRate())) {
						curSchd.setCalculatedRate(fsi.getActualRate() == null ? BigDecimal.ZERO : fsi.getActualRate());
					} else {
						BigDecimal recalculateRate = RateUtil.rates(fsi.getBaseRate(), fm.getFinCcy(), null,
								fsi.getMargin(), curSchd.getSchDate(), fm.getRpyMinRate(), fm.getRpyMaxRate())
								.getNetRefRateLoan();

						curSchd.setCalculatedRate(recalculateRate);
					}
					curSchd.setBaseRate(StringUtils.trimToNull(fsi.getBaseRate()));
					curSchd.setSplRate(StringUtils.trimToNull(fsi.getSplRate()));
					curSchd.setMrgRate(
							StringUtils.trimToNull(fsi.getBaseRate()) == null ? BigDecimal.ZERO : fsi.getMargin());
					curSchd.setActRate(fsi.getActualRate());
				}
			}
		}

		// Calculate the Restructure Capitalization Amount
		if (ImplementationConstants.RESTRUCTURE_ALW_CHARGES) {
			setRestructureCharges(rd, scheduleData);
		}

		// Schedule Restructuring
		scheduleData = ScheduleCalculator.procRestructure(scheduleData);

		// Plan EMI Holidays Resetting after Rescheduling
		if (scheduleData.getFinanceMain().isPlanEMIHAlw()) {
			scheduleData.getFinanceMain().setEventFromDate(rd.getRestructureDate());
			scheduleData.getFinanceMain().setEventToDate(scheduleData.getFinanceMain().getMaturityDate());
			scheduleData.getFinanceMain().setRecalFromDate(rd.getRestructureDate());
			scheduleData.getFinanceMain().setRecalToDate(scheduleData.getFinanceMain().getMaturityDate());
			scheduleData.getFinanceMain().setRecalSchdMethod(scheduleData.getFinanceMain().getScheduleMethod());
			scheduleData.getFinanceMain().setEqualRepay(true);
			scheduleData.getFinanceMain().setCalculateRepay(true);

			if (StringUtils.equals(scheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData = ScheduleCalculator.getFrqEMIHoliday(scheduleData);
			} else {
				scheduleData = ScheduleCalculator.getAdhocEMIHoliday(scheduleData);
			}
		}

		BigDecimal newTotalPft = scheduleData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		scheduleData.setPftChg(pftDiff);
		scheduleData.getFinanceMain().setScheduleRegenerated(true);
		scheduleData.getFinanceMain().resetRecalculationFields();
		scheduleData.setFinServiceInstruction(fsi);

		logger.debug(Literal.LEAVING);
		return scheduleData;
	}

	private void setRestructureCharges(RestructureDetail rd, FinScheduleData scheduleData) {
		boolean isBPICapitalized = false;

		BigDecimal amountToPOS = BigDecimal.ZERO;
		BigDecimal cpzRestructAmt = BigDecimal.ZERO;

		if (rd != null && rd.getChargeList() != null) {
			for (RestructureCharge rsChrg : rd.getChargeList()) {
				if (!rsChrg.isCapitalized()) {
					continue;
				}

				if ("BPI".equals(rsChrg.getAlocType())) {
					isBPICapitalized = true;
					continue;
				}

				BigDecimal totAmt = rsChrg.getActualAmount().subtract(rsChrg.getTdsAmount());
				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(rsChrg.getTaxType())) {
					totAmt = totAmt.add(rsChrg.getCgst()).add(rsChrg.getSgst()).add(rsChrg.getUgst())
							.add(rsChrg.getIgst()).add(rsChrg.getCess());
				}

				amountToPOS = amountToPOS.add(totAmt);

				if (!Allocation.FEE.equals(rsChrg.getAlocType())) {
					cpzRestructAmt = cpzRestructAmt.add(totAmt);
				}
			}
		}

		// Adding default to Fee & Charges for Display process
		if (cpzRestructAmt.compareTo(BigDecimal.ZERO) > 0) {
			String restructFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_RESTRUCT_CPZ);
			if (StringUtils.isNotBlank(restructFeeCode)) {
				for (FinFeeDetail ffd : scheduleData.getFinFeeDetailList()) {

					ffd.setValueDate(rd.getRestructureDate());

					if (!StringUtils.equals(restructFeeCode, ffd.getFeeTypeCode())) {
						continue;
					}

					ffd.setActualAmount(cpzRestructAmt);
					ffd.setActualAmountOriginal(cpzRestructAmt);
					ffd.setNetAmount(cpzRestructAmt);
					ffd.setNetAmountOriginal(cpzRestructAmt);
					ffd.setRemainingFee(cpzRestructAmt);
					ffd.setRemainingFeeOriginal(cpzRestructAmt);
					ffd.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_SALE_PRICE);
				}
			}
		}

		if (amountToPOS.compareTo(BigDecimal.ZERO) > 0) {
			// Find highest sequence for the current disbursement
			List<FinanceDisbursement> finDisbDetails = scheduleData.getDisbursementDetails();
			int disbSeq = 0;
			for (FinanceDisbursement curDisb : finDisbDetails) {
				if (curDisb.getDisbSeq() > disbSeq) {
					disbSeq = curDisb.getDisbSeq();
				}
			}

			// ADD new disbursement Record.
			FinanceDisbursement dd = new FinanceDisbursement();
			dd.setDisbAmount(BigDecimal.ZERO);
			dd.setDisbDate(rd.getRestructureDate());
			dd.setFeeChargeAmt(amountToPOS);
			dd.setDisbSeq(disbSeq + 1);
			scheduleData.getDisbursementDetails().add(dd);

			// Adding Amount to Schedule Capitalization
			for (FinanceScheduleDetail curSchd : scheduleData.getFinanceScheduleDetails()) {
				if (curSchd.getSchDate().compareTo(rd.getRestructureDate()) == 0) {
					curSchd.setFeeChargeAmt(amountToPOS);
					curSchd.setClosingBalance(curSchd.getClosingBalance().add(amountToPOS));
					if (isBPICapitalized) {
						curSchd.setCpzOnSchDate(true);
						curSchd.setBpiOrHoliday(FinanceConstants.FLAG_RESTRUCTURE);
					}
					break;
				}
			}
		}
	}

	private FinScheduleData addSchdRcd(FinScheduleData schdData, Date newSchdDate, int prvIndex) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();

		FinanceScheduleDetail prvSchd = schdData.getFinanceScheduleDetails().get(prvIndex);

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinID(fm.getFinID());
		sd.setFinReference(fm.getFinReference());
		sd.setBpiOrHoliday("");
		sd.setSchDate(newSchdDate);
		sd.setDefSchdDate(newSchdDate);
		sd.setBaseRate(prvSchd.getBaseRate());
		sd.setSplRate(prvSchd.getSplRate());
		sd.setMrgRate(prvSchd.getMrgRate());
		sd.setActRate(prvSchd.getActRate());
		sd.setCalculatedRate(prvSchd.getCalculatedRate());
		sd.setSchdMethod(prvSchd.getSchdMethod());
		sd.setPftDaysBasis(prvSchd.getPftDaysBasis());
		sd.setClosingBalance(prvSchd.getClosingBalance());

		schdData.getFinanceScheduleDetails().add(sd);
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));
		fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {
		if (CollectionUtils.isNotEmpty(financeScheduleDetail)) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;

	}

	@Override
	public List<RestructureCharge> getRestructureChargeList(FinScheduleData schdData, Date restructureDate) {
		List<RestructureCharge> charges = new ArrayList<>();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();

		// Schedule Overdue Principal & Interest Amount
		// -----------------------------------------
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal pftDue = BigDecimal.ZERO;
		boolean bpiCalcReq = true;
		FinanceScheduleDetail prvSchd = null;

		for (int i = 0; i < fsdList.size(); i++) {
			FinanceScheduleDetail curSchd = fsdList.get(i);
			if (i != 0) {
				prvSchd = fsdList.get(i - 1);
			}
			if (curSchd.getSchDate().compareTo(restructureDate) <= 0) {
				priDue = priDue.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				pftDue = pftDue.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));

				if (curSchd.getSchDate().compareTo(restructureDate) == 0) {
					bpiCalcReq = false;
				}
				continue;
			} else {
				break;
			}
		}

		int chargeSeq = 0;
		if (priDue.compareTo(BigDecimal.ZERO) > 0) {
			charges.add(getChargeRcd(priDue, BigDecimal.ZERO, null, ++chargeSeq, Allocation.PRI,
					Labels.getLabel("label_RecceiptDialog_AllocationType_PRI"), null, true));
		}
		if (pftDue.compareTo(BigDecimal.ZERO) > 0) {
			charges.add(getChargeRcd(pftDue, BigDecimal.ZERO, null, ++chargeSeq, Allocation.PFT,
					Labels.getLabel("label_RecceiptDialog_AllocationType_PFT"), null, true));
		}

		// BPI Amount calculation (interest calculation b/w Last Scheduled Date and Restructure Date
		if (bpiCalcReq) {
			BigDecimal bpiCalcAmt = CalculationUtil.calInterest(prvSchd.getSchDate(), restructureDate,
					prvSchd.getClosingBalance(), prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

			String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);
			BigDecimal calIntFraction = prvSchd.getProfitFraction();

			if (!CalculationConstants.PFTFRACTION_ADJ_NEXT_INST.equals(roundAdjMth)) {
				calIntFraction = BigDecimal.ZERO;
			}

			bpiCalcAmt = bpiCalcAmt.add(calIntFraction);

			BigDecimal calIntRounded = BigDecimal.ZERO;
			if (bpiCalcAmt.compareTo(BigDecimal.ZERO) > 0) {
				calIntRounded = CalculationUtil.roundAmount(bpiCalcAmt, fm.getCalRoundingMode(),
						fm.getRoundingTarget());
			}
			bpiCalcAmt = calIntRounded;

			if (bpiCalcAmt.compareTo(BigDecimal.ZERO) > 0) {
				charges.add(getChargeRcd(bpiCalcAmt, BigDecimal.ZERO, null, ++chargeSeq, "BPI",
						Labels.getLabel("label_RecceiptDialog_AllocationType_BPI"), null, true));
			}
		}

		// Restructure Charges / Fees calculated by Application based on FinType Fees Configuration
		if (CollectionUtils.isNotEmpty(schdData.getFinFeeDetailList())) {
			String restructFeeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_RESTRUCT_CPZ);
			for (FinFeeDetail ffd : schdData.getFinFeeDetailList()) {
				if (!CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(ffd.getFeeScheduleMethod())) {
					continue;
				}

				if (StringUtils.equals(restructFeeCode, ffd.getFeeTypeCode())) {
					continue;
				}

				RestructureCharge frc = new RestructureCharge();
				frc.setChargeSeq(++chargeSeq);
				frc.setAlocType(Allocation.FEE);
				frc.setAlocTypeDesc(ffd.getFeeTypeDesc());
				frc.setActualAmount(ffd.getRemainingFeeOriginal());
				frc.setFeeCode(ffd.getFeeTypeCode());
				frc.setCapitalized(true);
				frc.setTaxType(ffd.getTaxComponent());

				if (ffd.getTaxHeader() != null) {
					List<Taxes> taxDetails = ffd.getTaxHeader().getTaxDetails();

					for (Taxes taxes : taxDetails) {
						String taxType = taxes.getTaxType();

						switch (taxType) {
						case RuleConstants.CODE_CGST:
							frc.setCgst(taxes.getNetTax());
							break;
						case RuleConstants.CODE_SGST:
							frc.setSgst(taxes.getNetTax());
							break;
						case RuleConstants.CODE_IGST:
							frc.setIgst(taxes.getNetTax());
							break;
						case RuleConstants.CODE_UGST:
							frc.setUgst(taxes.getNetTax());
							break;
						case RuleConstants.CODE_CESS:
							frc.setCess(taxes.getNetTax());
							break;
						default:
							break;
						}
					}
				}

				if (ffd.isTdsReq()) {
					frc.setTdsAmount(frc.getTdsAmount());
				}

				frc.setTotalAmount(ffd.getRemainingFee());
				charges.add(frc);
			}
		}

		// LPP & LPI Details
		// ------------------------
		FinODPenaltyRate odPenalRate = schdData.getFinODPenaltyRate();
		if (odPenalRate == null) {
			odPenalRate = finODPenaltyRateDAO.getEffectivePenaltyRate(finID, "_AView");
		}

		FeeType lppFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.ODC);

		String taxType = null;
		if (lppFeeType != null && lppFeeType.isTaxApplicable()) {
			taxType = lppFeeType.getTaxComponent();
		}

		if (odPenalRate != null && !odPenalRate.isoDTDSReq()) {
			lppFeeType.setTdsReq(false);
		}

		BigDecimal tdsAmount = BigDecimal.ZERO;

		// Fetching Actual Late Payments based on Value date passing
		BigDecimal lpiBal = BigDecimal.ZERO;
		BigDecimal lppBal = BigDecimal.ZERO;

		List<FinODDetails> overdueList = finODDetailsDAO.getFinODDetailsByFinRef(finID);

		// Recalculation of OD Details based on user selected Restructure Date
		Date appDate = SysParamUtil.getAppDate();
		if (DateUtil.compare(restructureDate, appDate) != 0) {
			List<FinanceRepayments> repayments = financeRepaymentsDAO.getFinRepayList(finID);
			latePayMarkingService.calPDOnBackDatePayment(fm, overdueList, restructureDate,
					schdData.getFinanceScheduleDetails(), repayments, true, true);
		}

		TaxAmountSplit lppTax = new TaxAmountSplit();
		lppTax.setTaxType(taxType);

		if (CollectionUtils.isNotEmpty(overdueList)) {
			for (FinODDetails fod : overdueList) {
				if (fod.getFinODSchdDate().compareTo(restructureDate) > 0) {
					break;
				}

				lpiBal = lpiBal.add(fod.getLPIBal());
				if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
					lppBal = lppBal.add(fod.getTotPenaltyBal());
					BigDecimal taxableAmount = fod.getTotPenaltyBal();

					if (StringUtils.isNotEmpty(taxType)) {
						String finCcy = fm.getFinCcy();
						TaxAmountSplit taxSplit = GSTCalculator.calculateGST(finID, finCcy, taxType, taxableAmount);
						if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
							taxableAmount = taxableAmount.subtract(taxSplit.gettGST());
						}

						lppTax.setcGST(lppTax.getcGST().add(taxSplit.getcGST()));
						lppTax.setsGST(lppTax.getsGST().add(taxSplit.getsGST()));
						lppTax.setuGST(lppTax.getuGST().add(taxSplit.getuGST()));
						lppTax.setiGST(lppTax.getiGST().add(taxSplit.getiGST()));
						lppTax.setCess(lppTax.getCess().add(taxSplit.getCess()));
						lppTax.settGST(lppTax.gettGST().add(taxSplit.gettGST()));
					}

					if (lppFeeType.isTdsReq() && fm.isTDSApplicable()) {
						tdsAmount = tdsAmount.add(getTDSAmount(fm, taxableAmount));
					}
				}
			}

			if (lpiBal.compareTo(BigDecimal.ZERO) > 0) {
				charges.add(getChargeRcd(lpiBal, BigDecimal.ZERO, null, ++chargeSeq, Allocation.LPFT,
						Labels.getLabel("label_RecceiptDialog_AllocationType_LPFT"), null, false));
			}
			if (lppBal.compareTo(BigDecimal.ZERO) > 0) {
				charges.add(getChargeRcd(lppBal, tdsAmount, lppTax, ++chargeSeq, Allocation.ODC,
						Labels.getLabel("label_RecceiptDialog_AllocationType_ODC"), null, false));
			}
		}

		// Receivable and Bounce Charges
		// ----------------------------
		List<ManualAdvise> adviseList = manualAdviseDAO.getReceivableAdvises(finID, "_AView");

		if (CollectionUtils.isNotEmpty(adviseList)) {
			// Bounce Tax Details
			FeeType bounceFeeType = null;
			BigDecimal adviseDue = BigDecimal.ZERO;
			TaxAmountSplit bounceTax = new TaxAmountSplit();
			BigDecimal bounceDue = BigDecimal.ZERO;
			BigDecimal bounceTds = BigDecimal.ZERO;
			Map<String, RestructureCharge> advMap = new HashMap<String, RestructureCharge>();

			for (ManualAdvise advise : adviseList) {
				boolean isTdsApplicable = false;
				adviseDue = advise.getAdviseAmount().subtract(advise.getPaidAmount())
						.subtract(advise.getWaivedAmount());
				if (adviseDue.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				// Not allowing Advises/Bounces if created after Restructured Date
				if (DateUtil.compare(advise.getValueDate(), restructureDate) > 0) {
					continue;
				}

				String type = null;
				String desc = null;
				taxType = null;
				tdsAmount = BigDecimal.ZERO;

				// Adding Advise Details to Map
				if (advise.getBounceID() > 0) {
					if (bounceFeeType == null) {
						bounceFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.BOUNCE);
					}
					if (bounceFeeType != null && bounceFeeType.isTaxApplicable()) {
						taxType = bounceFeeType.getTaxComponent();
						bounceTax.setTaxType(taxType);
					}
					isTdsApplicable = bounceFeeType.isTdsReq();
					type = Allocation.BOUNCE;
					desc = "Bounce Charges";
				} else {
					type = Allocation.MANADV;
					desc = advise.getFeeTypeDesc();
					isTdsApplicable = advise.isTdsReq();
					// Calculation Receivable Advises
					if (advise.isTaxApplicable()) {
						taxType = advise.getTaxComponent();
					}
				}

				BigDecimal taxableAmount = adviseDue;
				TaxAmountSplit taxSplit = new TaxAmountSplit();

				taxSplit = GSTCalculator.calculateGST(finID, fm.getFinCcy(), taxType, taxableAmount);

				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
					taxableAmount = taxableAmount.subtract(taxSplit.gettGST());
				}

				taxSplit.setTaxType(taxType);

				if (advise.getBounceID() > 0) {
					bounceTax.setcGST(bounceTax.getcGST().add(taxSplit.getcGST()));
					bounceTax.setsGST(bounceTax.getsGST().add(taxSplit.getsGST()));
					bounceTax.setuGST(bounceTax.getuGST().add(taxSplit.getuGST()));
					bounceTax.setiGST(bounceTax.getiGST().add(taxSplit.getiGST()));
					bounceTax.setCess(bounceTax.getCess().add(taxSplit.getCess()));
					bounceTax.settGST(bounceTax.gettGST().add(taxSplit.gettGST()));
				}

				if (isTdsApplicable && fm.isTDSApplicable()) {
					tdsAmount = getTDSAmount(fm, adviseDue);
					if (advise.getBounceID() > 0) {
						bounceTds = bounceTds.add(tdsAmount);
					}
				}

				if (advise.getBounceID() > 0) {
					bounceDue = bounceDue.add(adviseDue);
				} else {
					if (adviseDue.compareTo(BigDecimal.ZERO) > 0) {

						RestructureCharge advRc;
						if (advMap.containsKey(advise.getFeeTypeCode())) {
							advRc = advMap.get(advise.getFeeTypeCode());
							advRc.setActualAmount(advRc.getActualAmount().add(adviseDue));

							advRc.setCgst(advRc.getCgst().add(taxSplit.getcGST()));
							advRc.setSgst(advRc.getSgst().add(taxSplit.getsGST()));
							advRc.setUgst(advRc.getUgst().add(taxSplit.getuGST()));
							advRc.setIgst(advRc.getIgst().add(taxSplit.getiGST()));
							advRc.setCess(advRc.getCess().add(taxSplit.getCess()));

							if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxSplit.getTaxType())) {
								advRc.setTotalAmount(advRc.getTotalAmount().add(taxSplit.gettGST()).add(adviseDue));
							}
						} else {
							advRc = getChargeRcd(adviseDue, tdsAmount, taxSplit, ++chargeSeq, type, desc,
									advise.getFeeTypeCode(), false);
						}

						advMap.put(advise.getFeeTypeCode(), advRc);
					}
				}
			}

			// Adding Manual Advises to List
			if (!advMap.isEmpty()) {
				charges.addAll(advMap.values());
			}

			// Bounce Due Charges
			if (bounceDue.compareTo(BigDecimal.ZERO) > 0) {
				charges.add(getChargeRcd(bounceDue, bounceTds, bounceTax, ++chargeSeq, Allocation.BOUNCE,
						bounceFeeType.getFeeTypeDesc(), bounceFeeType.getFeeTypeCode(), false));
			}
		}

		return charges;

	}

	private RestructureCharge getChargeRcd(BigDecimal actualAmount, BigDecimal tdsAmount, TaxAmountSplit taxAmountSplit,
			int Seq, String alocType, String alocTypeDesc, String feeTypeCode, boolean isCpz) {
		RestructureCharge rc = new RestructureCharge();
		rc.setChargeSeq(Seq);
		rc.setAlocType(alocType);
		rc.setAlocTypeDesc(alocTypeDesc);
		rc.setActualAmount(actualAmount);
		rc.setFeeCode(feeTypeCode);
		rc.setCapitalized(isCpz);

		BigDecimal totalAmt = actualAmount;
		if (taxAmountSplit != null) {
			rc.setTaxType(taxAmountSplit.getTaxType());
			rc.setCgst(taxAmountSplit.getcGST());
			rc.setSgst(taxAmountSplit.getsGST());
			rc.setUgst(taxAmountSplit.getuGST());
			rc.setIgst(taxAmountSplit.getiGST());
			rc.setCess(taxAmountSplit.getCess());

			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxAmountSplit.getTaxType())) {
				totalAmt = totalAmt.add(taxAmountSplit.gettGST());
			}
		}
		rc.setTdsAmount(tdsAmount);
		rc.setTotalAmount(totalAmt.subtract(tdsAmount));

		return rc;
	}

	public BigDecimal getTDSAmount(FinanceMain finMain, BigDecimal amount) {
		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		BigDecimal netAmount = amount.multiply(tdsPerc.divide(new BigDecimal(100)));
		netAmount = CalculationUtil.roundAmount(netAmount, tdsRoundMode, tdsRoundingTarget);

		return netAmount;
	}

	@Override
	public AuditDetail doValidations(FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();

		AuditDetail auditDetail = new AuditDetail();

		String lang = "EN";

		// validate Instruction details
		boolean isWIF = fsi.isWif();
		long finID = fsi.getFinID();
		Date fromDate = fsi.getFromDate();

		if (fromDate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "FromDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// It shouldn't be past date when compare to appdate
		if (DateUtil.compare(fromDate, appDate) < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "From date";
			valueParm[1] = "application date:" + DateUtil.formatToLongDate(appDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm), lang));
			return auditDetail;
		}

		boolean isValidFromDate = false;
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", isWIF);
		if (schedules != null) {
			for (FinanceScheduleDetail schDetail : schedules) {
				if (DateUtil.compare(fromDate, schDetail.getSchDate()) == 0) {
					isValidFromDate = true;
					if (checkIsValidRepayDate(auditDetail, schDetail, "FromDate") != null) {
						return auditDetail;
					}
				}
			}

			if (!isValidFromDate) {
				String[] valueParm = new String[1];
				valueParm[0] = "FromDate:" + DateUtil.formatToShortDate(fsi.getFromDate());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91111", "", valueParm), lang));
				return auditDetail;
			}
		}
		if (fsi.getNextRepayDate() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "NextRepayDate";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
			return auditDetail;
		}
		// validate Next payment date with finStart date and maturity date
		if (fsi.getNextRepayDate().compareTo(fromDate) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "Next RepayDate";
			valueParm[1] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm), lang));
			return auditDetail;
		}

		// FromDate should be Unpaid Date
		FinanceScheduleDetail schedule = financeScheduleDetailDAO.getFinanceScheduleDetailById(finID, fromDate, "",
				isWIF);

		BigDecimal paidAmount = schedule.getSchdPriPaid().add(schedule.getSchdFeePaid().add(schedule.getSchdPftPaid()));
		if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "From Date:" + DateUtil.formatToShortDate(fromDate);
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91116", "", valueParm), lang));
		}

		// validate repay frequency code
		ErrorDetail errorDetail = FrequencyUtil.validateFrequency(fsi.getRepayFrq());
		if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = fsi.getRepayFrq();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90207", "", valueParm), lang));
		}

		// terms
		if (fsi.getTerms() <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Terms";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), lang));
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditDetail checkIsValidRepayDate(AuditDetail auditDetail, FinanceScheduleDetail curSchd, String label) {
		if (!((curSchd.isRepayOnSchDate()
				|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
				&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && curSchd.isRepayOnSchDate()
						&& !curSchd.isSchPftPaid())
						|| (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0
								&& curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid())))) {
			String[] valueParm = new String[1];
			valueParm[0] = label;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90261", "", valueParm)));
			return auditDetail;
		}
		return null;
	}

	@Override
	public AuditDetail deleteRestructureDetail(RestructureDetail rd, String type, String transType) {
		logger.debug(Literal.ENTERING);

		restructureDAO.deleteChargeList(rd.getId(), type);
		restructureDAO.delete(rd.getId(), type);
		String[] fields = PennantJavaUtil.getFieldDetails(rd, rd.getExcludeFields());
		AuditDetail auditDetail = new AuditDetail(transType, 1, fields[0], fields[1], rd.getBefImage(), rd);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public List<AuditDetail> doApproveRestructureDetail(FinanceDetail fd, String type, String transType) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		RestructureDetail rd = schdData.getRestructureDetail();
		FinanceProfitDetail fpd = schdData.getFinPftDeatil();

		long finID = fm.getFinID();

		if (InstrumentType.isSI(fm.getFinRepayMethod())) {
			Mandate mandate = new Mandate();
			String accNum = mandateDAO.getMandateNumber(fm.getMandateID());
			mandate.setAccNumber(accNum);
			fd.setMandate(mandate);
		}

		if (fpd == null) {
			fpd = financeProfitDetailDAO.getFinProfitDetailsById(finID);
		}

		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();

		if (PennantConstants.RECORD_TYPE_DEL.equals(fm.getRecordType())) {
			transType = PennantConstants.TRAN_DEL;
			restructureDAO.delete(rd.getId(), "");
		} else {
			rd.setRoleCode("");
			rd.setNextRoleCode("");
			rd.setTaskId("");
			rd.setNextTaskId("");
			rd.setWorkflowId(0);
			rd.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			rd.setLastMntBy(fm.getLastMntBy());
			rd.setLastMntOn(fm.getLastMntOn());
			rd.setVersion(fm.getVersion());
			rd.setNewTenure(fpd.getNOInst());
			rd.setNewBalTenure(fpd.getFutureInst());
			rd.setNewMaturity(fm.getMaturityDate());
			BigDecimal totalPftSchd = fpd.getTotalPftSchd();
			rd.setNewInterest(totalPftSchd);
			rd.setNewCpzInterest(fpd.getTotalPftCpz());

			FinanceScheduleDetail lastSchd = fsdList.get(fsdList.size() - 2);
			rd.setNewFinalEmi(lastSchd.getRepayAmount());
			rd.setRepayProfitRate(lastSchd.getCalculatedRate());
			rd.setNewBucket(financeMainDAO.getBucketByFinStatus(finID));
			rd.setNewDpd(fpd.getCurODDays());
			BigDecimal totalPriSchd = fpd.getTotalpriSchd();
			BigDecimal tdSchdPri = fpd.getTdSchdPri();
			BigDecimal tdSchdPft = fpd.getTdSchdPft();
			BigDecimal odPrincipal = fpd.getODPrincipal();
			BigDecimal totalPriPaid = fpd.getTotalPriPaid();
			BigDecimal odProfit = fpd.getODProfit();
			int extODDays = fpd.getExtODDays();

			BigDecimal otherCharge = getReceivableAmt(finID, false);
			BigDecimal bounceCharge = getReceivableAmt(finID, true);
			BigDecimal penaltyAmount = getTotalPenaltyBal(finID, null);

			rd.setNewEmiOs((totalPriSchd.add(totalPftSchd)).subtract(tdSchdPri.add(tdSchdPft)));
			rd.setNewMaxUnplannedEmi(fm.getMaxUnplannedEmi());
			rd.setNewAvailedUnplanEmi(fm.getAvailedUnPlanEmi());
			rd.setNewPOsAmount(totalPriSchd.subtract(tdSchdPri));
			rd.setNewEmiOverdue(tdSchdPri.subtract(totalPriPaid));
			rd.setBounceCharge(bounceCharge);
			rd.setOtherCharge(otherCharge);
			rd.setNewPenaltyAmount(penaltyAmount);
			rd.setRestructureCharge(BigDecimal.ZERO);
			rd.setNewExtOdDays(extODDays);
			rd.setNewEmiOverdue(odProfit.add(odPrincipal));

			if (StringUtils.isEmpty(type)) {
				rd.setRecordType("");
				long id = restructureDAO.save(rd, "");
				List<RestructureCharge> charges = rd.getChargeList();
				for (RestructureCharge rc : charges) {
					if (rc.getRestructureId() == 0) {
						rc.setRestructureId(id);
					}

					setDefaultValuesForRC(rd, rc);
				}

				restructureDAO.saveChargeList(rd.getChargeList(), "");
			} else {
				rd.setRecordType("");
				restructureDAO.update(rd, "");
				restructureDAO.deleteChargeList(rd.getId(), "");
				restructureDAO.saveChargeList(rd.getChargeList(), "");
			}
		}

		financeMainDAO.updateRestructure(finID, true);
		fm.setRestructure(true);

		restructureDAO.deleteChargeList(rd.getId(), "_Temp");
		restructureDAO.delete(rd.getId(), "_Temp");

		List<AuditDetail> auditDetails = new ArrayList<>();

		int i = 0;
		for (RestructureCharge rc : rd.getChargeList()) {
			String[] fields = PennantJavaUtil.getFieldDetails(rc, rc.getExcludeFields());
			auditDetails.add(new AuditDetail(transType, ++i, fields[0], fields[1], rc.getBefImage(), rc));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new RestructureDetail(),
				new RestructureDetail().getExcludeFields());

		i = 0;
		auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, ++i, fields[0], fields[1], rd.getBefImage(), rd));
		auditDetails.add(new AuditDetail(transType, ++i, fields[0], fields[1], rd.getBefImage(), rd));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> saveOrUpdateRestructureDetail(FinanceDetail fd, String type, String transType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		RestructureDetail rd = fd.getFinScheduleData().getRestructureDetail();

		rd.setRecordType(fm.getRecordType());
		rd.setWorkflowId(fm.getWorkflowId());
		rd.setVersion(fm.getVersion());
		rd.setRoleCode(fm.getRoleCode());
		rd.setNextRoleCode(fm.getNextRoleCode());
		rd.setTaskId(fm.getTaskId());
		rd.setNextTaskId(fm.getNextTaskId());
		rd.setRecordStatus(fm.getRecordStatus());
		rd.setLastMntBy(fm.getLastMntBy());
		rd.setLastMntOn(fm.getLastMntOn());

		if (rd.isNewRecord()) {
			long id = restructureDAO.save(rd, type);

			for (RestructureCharge rc : rd.getChargeList()) {
				rc.setRestructureId(id);
				setDefaultValuesForRC(rd, rc);
			}

			restructureDAO.saveChargeList(rd.getChargeList(), type);
		} else {
			restructureDAO.update(rd, type);
			restructureDAO.deleteChargeList(rd.getId(), type);
			restructureDAO.saveChargeList(rd.getChargeList(), type);
		}

		int i = 0;
		for (RestructureCharge rc : rd.getChargeList()) {
			String[] fields = PennantJavaUtil.getFieldDetails(rc, rc.getExcludeFields());
			auditDetails.add(new AuditDetail(transType, ++i, fields[0], fields[1], rc.getBefImage(), rc));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(rd, rd.getExcludeFields());
		auditDetails.add(new AuditDetail(transType, 1, fields[0], fields[1], rd.getBefImage(), rd));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private void setDefaultValuesForRC(RestructureDetail rd, RestructureCharge rc) {
		if (PennantConstants.RCD_STATUS_APPROVED.equals(rd.getRecordStatus())) {
			rc.setRoleCode("");
			rc.setNextRoleCode("");
			rc.setTaskId("");
			rc.setNextTaskId("");
			rc.setWorkflowId(0);
		} else {
			rc.setRoleCode(rd.getRoleCode());
			rc.setNextRoleCode(rd.getNextRoleCode());
			rc.setTaskId(rd.getTaskId());
			rc.setNextTaskId(rd.getNextTaskId());
			rc.setWorkflowId(rd.getWorkflowId());
		}

		rc.setRecordStatus(rd.getRecordStatus());
		rc.setRecordType(rd.getRecordType());
		rc.setLastMntBy(rd.getLastMntBy());
		rc.setLastMntOn(rd.getLastMntOn());
		rc.setVersion(rd.getVersion());
	}

	@Override
	public AuditDetail validationRestructureDetail(FinanceDetail fd, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		RestructureDetail rd = fd.getFinScheduleData().getRestructureDetail();
		String userAction = StringUtils.trimToEmpty(fd.getUserAction());

		Date appDate = SysParamUtil.getAppDate();
		Date rdDate = rd.getAppDate();

		String auditTranType;
		if (fm.isNewRecord()) {
			auditTranType = PennantConstants.TRAN_ADD;
		} else {
			auditTranType = PennantConstants.TRAN_UPD;
		}

		AuditDetail auditDetail = new AuditDetail();
		// It shouldn't be past date when compare to appdate
		if (!(PennantConstants.RCD_STATUS_SAVED.contains(userAction)
				|| PennantConstants.RCD_STATUS_CANCELLED.contains(userAction)
				|| PennantConstants.RCD_STATUS_REJECTED.contains(userAction)
				|| PennantConstants.RCD_STATUS_DECLINED.contains(userAction) || userAction.contains("Resubmit"))) {
			if (DateUtil.compare(rdDate, appDate) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Restructure App Date: " + DateUtil.formatToLongDate(rdDate);
				valueParm[1] = "Application Date: " + DateUtil.formatToLongDate(appDate);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30509", "", valueParm)));
				return auditDetail;
			}
		}

		String[] fields = PennantJavaUtil.getFieldDetails(rd, rd.getExcludeFields());
		auditDetail = new AuditDetail(auditTranType, 1, fields[0], fields[1], rd.getBefImage(), rd);

		return auditDetail;
	}

	@Override
	public void processRestructureAccounting(AEEvent aeEvent, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = aeEvent.getDataMap();
		if (dataMap == null) {
			dataMap = new HashMap<>();
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> currSchedules = schdData.getFinanceScheduleDetails();
		long finID = fm.getFinID();
		List<FinanceScheduleDetail> prvsSchedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		RestructureDetail rsd = schdData.getRestructureDetail();

		BigDecimal prvInst = BigDecimal.ZERO;
		BigDecimal prvPrip = BigDecimal.ZERO;
		BigDecimal prvCpz = BigDecimal.ZERO;
		BigDecimal netCpz = BigDecimal.ZERO;
		BigDecimal curInst = BigDecimal.ZERO;
		BigDecimal curPrip = BigDecimal.ZERO;
		BigDecimal curCpz = BigDecimal.ZERO;

		Date resStartDate = rsd.getRestructureDate();
		Date resEndDate = null;
		if (rsd.getPriHldEndDate() != null) {
			resEndDate = rsd.getPriHldEndDate();
		} else {
			resEndDate = rsd.getEmiHldEndDate();
		}

		Date appDate = SysParamUtil.getAppDate();
		if (DateUtil.compare(resEndDate, appDate) > 0) {
			resEndDate = appDate;
		}

		// Previous
		for (FinanceScheduleDetail prvsSchd : prvsSchedules) {
			if (DateUtil.compare(prvsSchd.getSchDate(), resEndDate) > 0) {
				break;
			}
			if (DateUtil.compare(prvsSchd.getSchDate(), resStartDate) >= 0) {
				prvInst = prvInst.add(prvsSchd.getProfitSchd());
				prvPrip = prvPrip.add(prvsSchd.getPrincipalSchd());
				prvCpz = prvCpz.add(prvsSchd.getCpzAmount());
			}
		}

		// Current
		for (FinanceScheduleDetail cursSchd : currSchedules) {
			if (DateUtil.compare(cursSchd.getSchDate(), resEndDate) > 0) {
				break;
			}
			if (DateUtil.compare(cursSchd.getSchDate(), resStartDate) >= 0) {
				curInst = curInst.add(cursSchd.getProfitSchd());
				curPrip = curPrip.add(cursSchd.getPrincipalSchd());
				curCpz = curCpz.add(cursSchd.getCpzAmount());
			}
		}

		// accrue
		BigDecimal totProfitCalc = BigDecimal.ZERO;
		for (FinanceScheduleDetail prvsSchd : prvsSchedules) {
			if (DateUtil.compare(prvsSchd.getSchDate(), appDate) > 0) {
				break;
			}
			totProfitCalc = totProfitCalc.add(prvsSchd.getProfitCalc());
		}

		dataMap.put("ae_PrvInst", prvInst);
		dataMap.put("ae_PrvPrip", prvPrip);
		dataMap.put("ae_CurInst", curInst);
		dataMap.put("ae_CurPrip", curPrip);

		netCpz = curCpz.subtract(prvCpz);
		dataMap.put("ae_NetCpz", netCpz);
		dataMap.put("ae_PrvCpz", prvCpz);
		dataMap.put("ae_CurCpz", prvCpz);

		FinanceProfitDetail finPftDetails = financeProfitDetailDAO.getFinProfitDetailsById(finID);
		BigDecimal mnthIncome = BigDecimal.ZERO;

		if (finPftDetails != null && finPftDetails.getAmzTillLBD() != null) {
			mnthIncome = finPftDetails.getAmzTillLBD().subtract(totProfitCalc);
		}

		if (mnthIncome.compareTo(BigDecimal.ZERO) > 0) {
			dataMap.put("ae_mthIncome", mnthIncome);
		} else {
			dataMap.put("ae_mthIncome", BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void computeLPPandUpdateOD(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();

		Date appDate = SysParamUtil.getAppDate();

		List<FinODDetails> fodList = finODDetailsDAO.getFinODDByFinRef(finID, null);
		if (CollectionUtils.isEmpty(fodList)) {
			logger.debug(Literal.LEAVING);
			return;
		}
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
		RestructureDetail rsd = schdData.getRestructureDetail();
		List<FinODDetails> updatedODList = new ArrayList<>();

		for (int iFod = 0; iFod < fodList.size(); iFod++) {
			FinODDetails fod = fodList.get(iFod);

			Date odSchdDate = fod.getFinODSchdDate();
			if (odSchdDate.compareTo(rsd.getRestructureDate()) < 0) {
				fodList.remove(iFod);
				iFod = iFod - 1;
				continue;
			}

			List<FinanceRepayments> rpdList = null;

			for (FinanceScheduleDetail fsd : fsdList) {
				Date schDate = fsd.getSchDate();

				if (schDate.compareTo(appDate) > 0) {
					break;
				}

				if (schDate.compareTo(odSchdDate) == 0) {

					rpdList = financeRepaymentsDAO.getByFinRefAndSchdDate(finID, odSchdDate);
					latePayMarkingService.resetMaxODAmount(rpdList, fod, fsd);
					latePayMarkingService.latePayMarking(fm, fod, fsdList, rpdList, fsd, appDate, appDate, false);
					updatedODList.add(fod);
					break;
				}
			}
		}

		if (CollectionUtils.isNotEmpty(updatedODList)) {
			finODDetailsDAO.updateTotals(updatedODList);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public BigDecimal getReceivableAmt(long finID, boolean isBounce) {
		return manualAdviseDAO.getReceivableAmt(finID, isBounce);
	}

	@Override
	public BigDecimal getTotalPenaltyBal(long finID, List<Date> presentmentDates) {
		return finODDetailsDAO.getTotalPenaltyBal(finID, presentmentDates);
	}

	@Override
	public RestructureDetail getRestructureDetailByRef(long finID, String type) {
		RestructureDetail rd = restructureDAO.getRestructureDetailByFinReference(finID, type);

		if (rd == null) {
			return null;
		}

		rd.setChargeList(restructureDAO.getRestructureCharges(rd.getId(), "_View".equals(type) ? "_Temp" : ""));

		return rd;
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(long finID) {
		return financeProfitDetailDAO.getFinProfitDetailsById(finID);
	}

	@Override
	public Date getMaxValueDateOfRcv(long finID) {
		return manualAdviseDAO.getMaxValueDateOfRcv(finID);
	}

	@Override
	public List<ErrorDetail> doValidations(RestructureDetail rd) {
		logger.debug(Literal.ENTERING);

		long finID = rd.getFinID();
		String finReference = rd.getFinReference();
		FinanceDetail fd = getFinanceDetailById(finID, "_View", false);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType finType = schdData.getFinanceType();

		String restructureType = rd.getRestructureType();
		String recalType = rd.getRecalculationType();
		String rstReason = rd.getRestructureReason();

		List<ErrorDetail> errors = new ArrayList<>();
		// mandatory validations
		if (StringUtils.isBlank(finReference)) {
			errors.add(getErrorDetail("90502", "finReference"));
		} else {
			// validating finreference if exists or not
			validateFinReference(rd, errors);
		}

		if (StringUtils.isBlank(restructureType)) {
			errors.add(getErrorDetail("90502", "restructureType"));
		} else {
			// validating RestructureType if exists or not
			boolean exists = restructureDAO.isExistRestructureType(Long.parseLong(restructureType),
					schdData.getFinanceMain().isStepFinance());
			if (!exists) {
				errors.add(getErrorDetail("90224", "RestructureType", restructureType));
			}
			// no of emi holiday , no of principal holiday, no of terms
			RestructureType rstType = restructureDAO.getRestructureTypeById(restructureType);
			if (rstType != null) {
				validateRestructureType(restructureType, rd, rstType, errors);
			}
		}

		if ("9".equals(restructureType) || "10".equals(restructureType) || "11".equals(restructureType)) {
			stepLoanValidations(fd, rd, errors);
		}

		if (StringUtils.isBlank(rstReason)) {
			errors.add(getErrorDetail("90502", "RestructureReason"));
		} else {
			// validating RestructureReason if exists or not
			boolean exists = restructureDAO.isExistRestructureReason(rstReason);
			if (!exists) {
				errors.add(getErrorDetail("90224", "RestructureReason", rstReason));
			}
		}

		if (rd.getRestructureDate() == null) {
			errors.add(getErrorDetail("90502", "RestructureDate"));
		} else {
			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				List<FinanceScheduleDetail> schdules = schdData.getFinanceScheduleDetails();
				setAlwdRestructureDates(schdules, finID, rd.getRestructureDate(), errors);
			}
		}

		BigDecimal actRate = rd.getRepayProfitRate();
		String baseRate = rd.getBaseRate();
		BigDecimal margin = rd.getMargin();

		// repay rates validation
		if (actRate.compareTo(BigDecimal.ZERO) == 0 && StringUtils.isEmpty(baseRate)) {
			errors.add(getErrorDetail("90502", "Actual Rate or Base Rate"));
		} else {
			// Check if Actual Rate is negative
			if (actRate.compareTo(BigDecimal.ZERO) < 0) {
				errors.add(getErrorDetail("92021", "Actual Rate cannot be Negative"));
			}
			if (actRate.compareTo(BigDecimal.ZERO) > 0 && (StringUtils.isNotEmpty(baseRate))) {
				errors.add(getErrorDetail("92021", "Either Base Rate or Actual Rate Should be Given"));
			}

			if (actRate.compareTo(new BigDecimal(9999)) > 0) {
				errors.add(getErrorDetail("92021", "Actual Rate should be lesser/Equal to 9999 and greater than 0"));
			}

			// Allow Base Rate?
			if (StringUtils.isBlank(finType.getFinBaseRate()) && StringUtils.isNotEmpty(baseRate)) {
				errors.add(getErrorDetail("92021", "Base Rate not configured for loan type " + finType.getFinType()));
			} else {
				// Base Rate code found?
				if (StringUtils.isNotEmpty(baseRate)) {
					int rcdCount = baseRateDAO.getBaseRateCountById(baseRate, finType.getFinCcy(), "");
					if (rcdCount <= 0) {
						errors.add(getErrorDetail("92021", "Base Rate code Not found"));
					}
				}
				if (StringUtils.isEmpty(baseRate) && margin.compareTo(BigDecimal.ZERO) != 0) {
					errors.add(getErrorDetail("92021", "Margin Not Allowed With Out base Rate"));
				}
				if (StringUtils.isNotEmpty(baseRate) && margin.compareTo(BigDecimal.ZERO) != 0) {
					if (margin.compareTo(new BigDecimal(9999)) > 0 || margin.compareTo(new BigDecimal(-9999)) < 0) {
						errors.add(getErrorDetail("92021",
								"Margin should be greater/Equal to -9999 and lesser/Equal to 9999"));
					}
				}
			}
		}

		if (StringUtils.isEmpty(recalType)) {
			errors.add(getErrorDetail("90502", "RecalculationType"));
		} else {
			int match = 0;
			List<ValueLabel> recalTypeList = PennantStaticListUtil.getRecalTypeList();
			for (ValueLabel valueLabel : recalTypeList) {
				if (StringUtils.equals(recalType, valueLabel.getValue())) {
					match++;
					break;
				}
			}

			if (match == 0) {
				errors.add(getErrorDetail("90224", "RecalculationType", recalType));
			}
			if ((restructureType.equals("7") || restructureType.equals("8"))
					&& !CalculationConstants.RST_RECAL_ADJUSTTENURE.equals(recalType)) {
				errors.add(getErrorDetail("12724", "RecalculationType", CalculationConstants.RST_RECAL_ADJUSTTENURE));
			} else if (!(restructureType.equals("7") || restructureType.equals("8"))
					&& CalculationConstants.RST_RECAL_ADJUSTTENURE.equals(recalType)) {
				errors.add(getErrorDetail("92021", "RecalculationType " + CalculationConstants.RST_RECAL_ADJUSTTENURE
						+ " is not allowed for RestructureType " + restructureType));
			}
		}

		List<RestructureCharge> charges = rd.getChargeList();
		for (RestructureCharge rc : charges) {
			BigDecimal actualAmount = rc.getActualAmount();
			String alocType = rc.getAlocType();

			if (!Allocation.FEE.equals(alocType) && actualAmount.compareTo(BigDecimal.ZERO) > 0) {
				errors.add(getErrorDetail("12725", rc.getFeeCode()));
			}
		}

		return errors;
	}

	private void validateRestructureType(String restructureType, RestructureDetail restDtl, RestructureType rstType,
			List<ErrorDetail> errorDetails) {
		logger.debug(Literal.ENTERING);

		int noOfEmiHld = restDtl.getEmiHldPeriod();
		int noOfpriHld = restDtl.getPriHldPeriod();
		int noOfEmiTerms = restDtl.getEmiPeriods();
		int totNoOfRst = 0;

		switch (restructureType) {
		case "1":
			if (noOfEmiHld == 0 || noOfpriHld == 0) {
				errorDetails.add(getErrorDetail("90502", "EMI Holiday & Principal Holiday"));
			} else if (noOfEmiTerms != 0) {
				errorDetails.add(getErrorDetail("92021", getErrorMessage("Emi Terms", restructureType)));
			}

			totNoOfRst = noOfEmiHld + noOfpriHld;
			break;
		case "2":
		case "11":
			if (noOfEmiTerms == 0) {
				errorDetails.add(getErrorDetail("90502", "EMI Terms"));
			} else if (noOfEmiHld != 0 || noOfpriHld != 0) {
				errorDetails.add(
						getErrorDetail("92021", getErrorMessage("EMI Holiday & Principal Holiday", restructureType)));
			}

			totNoOfRst = noOfEmiTerms;
			break;
		case "3":
		case "7":
		case "9":
			if (noOfEmiHld == 0 || noOfEmiTerms == 0) {
				errorDetails.add(getErrorDetail("90502", "EMI Holiday & EMI Terms"));
			} else if (noOfpriHld != 0) {
				errorDetails.add(getErrorDetail("92021", getErrorMessage("Principal Holiday", restructureType)));
			}

			totNoOfRst = noOfEmiHld + noOfEmiTerms;
			break;
		case "4":
			if (noOfEmiHld == 0) {
				errorDetails.add(getErrorDetail("90502", "EMI Holiday"));
			} else if (noOfpriHld != 0 || noOfEmiTerms != 0) {
				errorDetails.add(
						getErrorDetail("92021", getErrorMessage("Principal Holiday & EMI Terms", restructureType)));
			}

			totNoOfRst = noOfEmiHld;
			break;
		case "5":
			if (noOfpriHld == 0) {
				errorDetails.add(getErrorDetail("90502", "Principal Holiday"));
			} else if (noOfEmiHld != 0 || noOfEmiTerms != 0) {
				errorDetails.add(getErrorDetail("92021", getErrorMessage("EMI Holiday & EMI Terms ", restructureType)));
			}

			totNoOfRst = noOfpriHld;
			break;
		case "6":
		case "8":
		case "10":
			if (noOfpriHld == 0 || noOfEmiTerms == 0) {
				errorDetails.add(getErrorDetail("90502", "Principal Holiday & EMI Terms"));
			} else if (noOfEmiHld != 0) {
				errorDetails.add(getErrorDetail("92021", getErrorMessage("EMI Holiday ", restructureType)));
			}

			totNoOfRst = noOfpriHld + noOfEmiTerms;
			break;
		default:
			break;
		}

		if (totNoOfRst > rstType.getMaxTotTerm()) {
			errorDetails.add(getErrorDetail("12723", "Either EMI Holiday or Tenor or Principal Holiday",
					String.valueOf(rstType.getMaxTotTerm())));
		}

		restDtl.setTotNoOfRestructure(totNoOfRst);
		logger.debug(Literal.LEAVING);
	}

	private String getErrorMessage(String msg, String rstType) {
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append(msg);
		errorMsg.append(" is not applicable for given Restructure Type ");
		errorMsg.append(rstType);
		return errorMsg.toString();
	}

	private FinanceDetail getFinanceDetailById(long finID, String type, boolean isWif) {
		logger.debug(Literal.ENTERING);
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinID(finID);
		schdData.setFinanceMain(financeMainDAO.getFinanceMainById(finID, type, isWif));

		FinanceMain fm = schdData.getFinanceMain();

		schdData.setFinReference(fm.getFinReference());

		if (fm != null) {
			FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(fm.getFinType(), "_ORGView");
			schdData.setFinanceType(financeType);
		}

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, isWif));

		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "_View", false));
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private void setAlwdRestructureDates(List<FinanceScheduleDetail> schedules, long finID, Date rstDate,
			List<ErrorDetail> errors) {
		logger.debug(Literal.ENTERING);
		Date fullyPaidDate = null;

		if (CollectionUtils.isEmpty(schedules)) {
			return;
		}

		for (FinanceScheduleDetail curSchd : schedules) {
			if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
				fullyPaidDate = curSchd.getSchDate();
				continue;
			}

			if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
				fullyPaidDate = curSchd.getSchDate();
				continue;
			}

			if (curSchd.isDisbOnSchDate() || curSchd.getPresentmentId() > 0) {
				fullyPaidDate = curSchd.getSchDate();
				continue;
			}
		}

		// Checking Manual Advise Last max Value Date before Application/Restructuring Date
		Date maxValueDate = getMaxValueDateOfRcv(finID);
		if (maxValueDate != null && DateUtil.compare(maxValueDate, fullyPaidDate) > 0) {
			fullyPaidDate = maxValueDate;
		}

		Date appDate = SysParamUtil.getAppDate();
		if (rstDate.compareTo(appDate) > 0 || rstDate.compareTo(fullyPaidDate) < 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "Restructure Date";
			valueParm[1] = DateUtil.formatToShortDate(fullyPaidDate);
			valueParm[2] = DateUtil.formatToShortDate(appDate);
			errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("12721", valueParm)));
		}

		logger.debug(Literal.LEAVING);
	}

	private void validateFinReference(RestructureDetail rd, List<ErrorDetail> errors) {
		long finID = rd.getFinID();
		String finReference = rd.getFinReference();

		if (restructureDAO.checkLoanProduct(finID)) {
			errors.add(getErrorDetail("12722", finReference));
		}

		FinanceMain fm = financeMainDAO.getRcdMaintenanceByRef(finID, "_View");
		Date maturityDate = fm.getMaturityDate();
		Date appDate = SysParamUtil.getAppDate();

		if (DateUtil.compare(maturityDate, appDate) < 0) {
			errors.add(getErrorDetail("RU0000", "Restructure"));
		}
	}

	private ErrorDetail getErrorDetail(String errorCode, String param) {
		String[] valueParm = new String[1];
		valueParm[0] = param;

		return ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, valueParm));
	}

	private ErrorDetail getErrorDetail(String errorCode, String paramOne, String paramTwo) {
		String[] valueParm = new String[2];
		valueParm[0] = paramOne;
		valueParm[1] = paramTwo;

		return ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, valueParm));
	}

	@Override
	public boolean checkLoanDues(List<RestructureCharge> charges) {
		logger.debug(Literal.ENTERING);

		boolean isDue = false;
		String alocType = null;

		for (RestructureCharge rc : charges) {
			alocType = rc.getAlocType();
			if ("BPI".equals(alocType) || Allocation.FEE.equals(alocType)) {
				continue;
			}

			if (Allocation.PRI.equals(alocType) || Allocation.PFT.equals(alocType)) {
				isDue = true;
				break;
			}

			if ((Allocation.MANADV.equals(alocType) || Allocation.BOUNCE.equals(alocType)
					|| Allocation.ODC.equals(alocType)) && !rc.isCapitalized()) {
				isDue = false;
			} else {
				isDue = true;
				break;
			}
		}

		logger.debug(Literal.LEAVING);
		return isDue;
	}

	private void stepLoanValidations(FinanceDetail fd, RestructureDetail rd, List<ErrorDetail> errors) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();

		// Finance Type allow Step?
		if (fm.isStepFinance() && !financeType.isStepFinance()) {
			errors.add(getErrorDetail("91129", fm.getFinType()));
		} else if (!fm.isStepFinance() && !financeType.isStepFinance()) {
			errors.add(getErrorDetail("91129", fm.getFinType()));
		}

		if (!fm.isStepFinance() && financeType.isSteppingMandatory()) {
			errors.add(getErrorDetail("91128", fm.getFinType()));
		}

		if (fm.isStepFinance() && StringUtils.isNotBlank(fm.getStepsAppliedFor())) {
			// Check if stepsAppliedFor is given same as FinanceMain in request
			if (StringUtils.isBlank(rd.getStepsAppliedFor())) {
				errors.add(getErrorDetail("92021", "stepsAppliedFor cannot be blank for step loan"));
			} else if (!StringUtils.equals(rd.getStepsAppliedFor(), fm.getStepsAppliedFor())) {
				errors.add(getErrorDetail("STP0012", "stepsAppliedFor", rd.getStepsAppliedFor()));
			}
		} else {
			// check for Non step loans
			if (StringUtils.isNotBlank(rd.getStepsAppliedFor())) {
				List<ValueLabel> stepsApplForList = PennantStaticListUtil.getStepsAppliedFor();
				boolean stepsApplForSts = false;
				for (ValueLabel value : stepsApplForList) {
					if (StringUtils.equals(value.getValue(), rd.getStepsAppliedFor())) {
						stepsApplForSts = true;
						break;
					}
				}
				if (!stepsApplForSts) {
					errors.add(getErrorDetail("STP0012", "stepsAppliedFor", rd.getStepsAppliedFor()));
				}
			} else {
				errors.add(getErrorDetail("92021", "Steps applied for cannot be blank"));
			}
		}

		if (fm.isStepFinance() && StringUtils.isNotBlank(fm.getCalcOfSteps())) {
			// check if calcOfSteps is same as FinanceMain in request
			if (StringUtils.isBlank(rd.getCalcOfSteps())) {
				errors.add(getErrorDetail("92021", "calcOfSteps cannot be blank for step loan"));
			} else if (!StringUtils.equals(rd.getCalcOfSteps(), fm.getCalcOfSteps())) {
				errors.add(getErrorDetail("STP0012", "calcOfSteps", rd.getCalcOfSteps()));
			}
		} else {
			// check calcOfSteps for Non step loans
			if (StringUtils.isNotBlank(rd.getCalcOfSteps())) {
				List<ValueLabel> calcOfStepsList = PennantStaticListUtil.getCalcOfStepsList();
				boolean calcOfStepsSts = false;
				for (ValueLabel value : calcOfStepsList) {
					if (StringUtils.equals(value.getValue(), rd.getCalcOfSteps())) {
						calcOfStepsSts = true;
						break;
					}
				}
				if (!calcOfStepsSts) {
					errors.add(getErrorDetail("STP0012", "calcOfSteps", rd.getCalcOfSteps()));
				}
			} else {
				errors.add(getErrorDetail("90502", rd.getCalcOfSteps()));
			}
		}

		if (PennantConstants.STEPPING_CALC_AMT.equals(rd.getCalcOfSteps())
				&& (StringUtils.isNotEmpty(rd.getStepPolicy()) || StringUtils.isNotEmpty(rd.getStepType()))) {
			errors.add(getErrorDetail("92021",
					"Step Policy & Step Type are not allowed for calcOfSteps " + rd.getCalcOfSteps()));
		}

		if (PennantConstants.STEPPING_CALC_AMT.equals(rd.getCalcOfSteps()) && !rd.isAlwManualSteps()) {
			errors.add(getErrorDetail("92021", "AlwManualSteps should be true for " + rd.getCalcOfSteps()));
		}

		if (rd.isAlwManualSteps() && rd.getNoOfSteps() <= 0) {
			errors.add(getErrorDetail("92021", "No of Steps cannot be less than or equal to 0"));
		}

		if (fm.isStepFinance() && rd.isAlwManualSteps() && CollectionUtils.isEmpty(rd.getStepPolicyDetails())
				&& rd.getNoOfSteps() == 0) {
			errors.add(getErrorDetail("92021", "Step Details must be provided "));
		}

		// check if steps details are equal to no of steps
		if (rd.getNoOfSteps() != rd.getStepPolicyDetails().size()) {
			errors.add(getErrorDetail("92021", "Step Details should be equal to Repay steps " + rd.getNoOfSteps()));
		}

		// check step loan tenor
		validateStepTenor(rd, schdData, errors);

		// For Step Detail that has overdue & future installments , no of installments cannot be less than total
		// no of overdue installments
		if (fm.isStepFinance()) {
			List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
			for (FinanceStepPolicyDetail spd : rd.getStepPolicyDetails()) {
				int instCount = 0;
				if (spd.getStepEnd() != null && spd.getStepEnd().compareTo(rd.getRestructureDate()) > 0) {
					for (FinanceScheduleDetail fsd : schedules) {
						if (fsd.getSchDate().compareTo(spd.getStepStart()) >= 0
								&& fsd.getSchDate().compareTo(rd.getRestructureDate()) < 0) {
							instCount++;
						}
					}
					if (spd.getInstallments() < instCount) {
						errors.add(getErrorDetail("92021", "No of Installments cannot be less than " + instCount
								+ " for Step No " + spd.getStepNo()));
						break;
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void validateStepTenor(RestructureDetail rd, FinScheduleData schdData, List<ErrorDetail> errors) {
		// validate Tenor for Step Loans
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepDtls = rd.getStepPolicyDetails();
		int noOfStepTerms = 0;
		boolean override = false;
		int noOfEMITerms = fm.getNumberOfTerms() + rd.getEmiPeriods();

		for (FinanceStepPolicyDetail rstStepDetail : stepDtls) {
			noOfStepTerms = noOfStepTerms + rstStepDetail.getInstallments();

			if (rstStepDetail.getStepNo() > rd.getNoOfSteps()) {
				errors.add(getErrorDetail("92021", "Step number " + rstStepDetail.getStepNo()
						+ " should be less than/equal to Repay Steps " + rd.getNoOfSteps()));
			}

			if (rstStepDetail.getStepNo() == rd.getNoOfSteps()
					&& rstStepDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) > 0) {
				errors.add(getErrorDetail("92021", "SteppedEMI Amount is not allowed for last Step Detail"));
			} else if (rstStepDetail.getStepNo() != rd.getNoOfSteps()
					&& rstStepDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) == 0) {
				errors.add(getErrorDetail("92021", "SteppedEMI Amount cannot be 0 for " + rstStepDetail.getStepNo()));
			}

			if (fm.isStepFinance()) {
				override = validateStepPolicyDetail(rstStepDetail, schdData, rd.getRestructureDate());
				if (override) {
					errors.add(getErrorDetail("92021", "Not allowed to Edit Step No " + rstStepDetail.getStepNo()));
				}
				for (FinanceStepPolicyDetail stepPolicyDtl : schdData.getStepPolicyDetails()) {
					if (rstStepDetail.getStepNo() == stepPolicyDtl.getStepNo()) {
						rstStepDetail.setAutoCal(stepPolicyDtl.isAutoCal());
						rstStepDetail.setFinReference(stepPolicyDtl.getFinReference());
						rstStepDetail.setNewRecord(stepPolicyDtl.isNewRecord());
						rstStepDetail.setStepStart(stepPolicyDtl.getStepStart());
						rstStepDetail.setStepEnd(stepPolicyDtl.getStepEnd());
						rstStepDetail.setStepSpecifier(stepPolicyDtl.getStepSpecifier());
					} else {
						rstStepDetail.setAutoCal(false);
						rstStepDetail.setFinReference(stepPolicyDtl.getFinReference());
						rstStepDetail.setStepSpecifier(stepPolicyDtl.getStepSpecifier());
					}
				}
			} else {
				// for non step loans while sorting of step details at the time of schedule generation
				// step specifier is required otherwise getting unhandled exception
				rstStepDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);

				// validation to check if all overdue installments are given in first step and with same amount
				if (rstStepDetail.getStepNo() == 1 && (StringUtils.equals(rd.getRestructureType(), "9")
						|| StringUtils.equals(rd.getRestructureType(), "10")
						|| StringUtils.equals(rd.getRestructureType(), "11"))) {
					Map<String, Object> map = getNoOfInstAndAmt(schdData, rd.getRestructureDate());
					BigDecimal amt = BigDecimal.ZERO;
					int inst = 0;
					if (map.get("Amount") != null && map.get("NoOfInstallments") != null) {
						amt = (BigDecimal) map.get("Amount");
						inst = (int) map.get("NoOfInstallments");
					}
					if (rstStepDetail.getInstallments() != inst) {
						errors.add(getErrorDetail("92021",
								"No of Step Installments should be equal to No of Overdue Installments " + inst));
						break;
					}
					if (rstStepDetail.getStepNo() == 1 && rstStepDetail.getSteppedEMI().compareTo(amt) != 0) {
						errors.add(
								getErrorDetail("92021", "Amount should be same as Overdue installment amount " + amt));
						break;
					}
				}
			}
		}
		if (noOfEMITerms != noOfStepTerms) {
			errors.add(getErrorDetail("92021", Labels.getLabel("label_RestructureDialog_NumberOfSteps.value")));
		}
	}

	private Boolean validateStepPolicyDetail(FinanceStepPolicyDetail stepPolicy, FinScheduleData schdData,
			Date rstDate) {
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		int idxStart = 0;
		Date stepEndDate = null;
		FinanceStepPolicyDetail finStpDtl = null;
		int noOfFinSteps = schdData.getFinanceMain().getNoOfSteps();

		if (stepPolicy.isNewRecord()) {
			return false;
		}

		if (stepPolicy.getStepNo() > noOfFinSteps) {
			return false;
		}

		for (int i = 0; i < stepPolicy.getStepNo(); i++) {
			if (i > stepPolicy.getStepNo()) {
				break;
			}

			finStpDtl = schdData.getStepPolicyDetails().get(i);

			int instCount = 0;
			for (int iFsd = idxStart; iFsd < schedules.size(); iFsd++) {
				FinanceScheduleDetail fsd = schedules.get(iFsd);
				String specifier = fsd.getSpecifier();
				if (fsd.isRepayOnSchDate() && fsd.isFrqDate()) {
					instCount = instCount + 1;
				} else if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(stepPolicy.getStepSpecifier())
						&& !(FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday()))
						&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
								|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
						&& !fsd.isDisbOnSchDate() && fsd.isFrqDate()) {
					instCount = instCount + 1;
				}

				if (finStpDtl.getInstallments() == instCount) {
					stepEndDate = fsd.getSchDate();
					idxStart = iFsd + 1;
					break;
				}
			}
		}

		if (stepEndDate != null && stepEndDate.compareTo(rstDate) < 0) {
			if (finStpDtl != null && finStpDtl.getStepNo() != stepPolicy.getStepNo()
					|| finStpDtl.getInstallments() != stepPolicy.getInstallments()
					|| finStpDtl.getSteppedEMI().compareTo(stepPolicy.getSteppedEMI()) != 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Object> getNoOfInstAndAmt(FinScheduleData schdData, Date rstDate) {
		logger.debug(Literal.ENTERING);
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		Map<String, Object> map = new HashMap<>();
		int installments = 0;
		BigDecimal amount = BigDecimal.ZERO;

		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail fsd = schedules.get(i);
			if (fsd.isDisbOnSchDate() || FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday())) {
				continue;
			}
			if (fsd.getSchDate().compareTo(rstDate) < 0) {
				installments++;
				if (fsd.isRepayOnSchDate()) {
					amount = fsd.getRepayAmount();
				}
			}
			if (fsd.getSchDate().compareTo(rstDate) > 0) {
				break;
			}
		}

		map.put("Amount", amount);
		map.put("NoOfInstallments", installments);

		logger.debug(Literal.LEAVING);
		return map;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setRestructureDAO(RestructureDAO restructureDAO) {
		this.restructureDAO = restructureDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setLatePayMarkingService(LatePayMarkingService latePayMarkingService) {
		this.latePayMarkingService = latePayMarkingService;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		this.baseRateDAO = baseRateDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinanceStepDetailDAO(FinanceStepDetailDAO financeStepDetailDAO) {
		this.financeStepDetailDAO = financeStepDetailDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

}
