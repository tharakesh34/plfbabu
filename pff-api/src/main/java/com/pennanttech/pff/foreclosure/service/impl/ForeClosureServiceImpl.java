package com.pennanttech.pff.foreclosure.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ForeClosureLetter;
import com.pennant.backend.model.finance.ForeClosureResponse;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.controller.FinStatementController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.foreclosure.service.ForeClosureService;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.ExcessType;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementRequest;
import com.pennanttech.ws.model.statement.FinStatementResponse;

public class ForeClosureServiceImpl implements ForeClosureService {
	private static final Logger logger = LogManager.getLogger(ForeClosureServiceImpl.class);

	private FinStatementController finStatementController;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FeeTypeDAO feeTypeDAO;

	@Override
	public ForeClosureLetter getForeClosureAmt(FinStatementRequest request, ForeClosureResponse clsrresponse) {
		logger.debug(Literal.ENTERING);

		FinStatementResponse response = finStatementController.getStatement(request, APIConstants.STMT_FORECLOSURE);

		FinanceDetail fd = response.getFinance().get(0);
		FinScheduleData schdData = fd.getFinScheduleData();
		List<FinFeeDetail> fees = schdData.getFeeDues();

		List<ManualAdvise> advises = manualAdviseDAO.getAdvises(request.getFinID(), "_View");
		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(request.getFinID());

		BigDecimal totalADgstAmt = BigDecimal.ZERO;
		BigDecimal totalBCgstFee = BigDecimal.ZERO;

		for (FinFeeDetail fee : fees) {
			String feeTypeCode = StringUtils.trimToEmpty(fee.getFeeTypeCode());
			for (ManualAdvise advise : advises) {
				if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advise.getTaxComponent())) {
					BigDecimal actualOriginal = fee.getActualAmount();
					TaxAmountSplit taxSplit = GSTCalculator.getExclusiveGST(actualOriginal, taxPercentages);
					BigDecimal totalGst = taxSplit.gettGST();

					if (feeTypeCode.equals(advise.getFeeTypeCode())) {
						fee.setActualAmount(actualOriginal.add(totalGst));
						totalADgstAmt = totalADgstAmt.add(totalGst);
					}

					if (fee.getFeeID() == advise.getAdviseID()) {
						totalBCgstFee = totalBCgstFee.add(totalGst);
					}
				}
			}
		}

		List<FinExcessAmount> excessAmounts = finExcessAmountDAO.getAllExcessAmountsByRef(request.getFinID(), "");

		List<ForeClosure> foreClosureDetails = fd.getForeClosureDetails();

		ForeClosureLetter fcl = getForeClosureLetter(excessAmounts);
		fcl.setOutStandPrincipal(schdData.getFinanceSummary().getOutStandPrincipal());

		FinanceSummary summary = schdData.getFinanceSummary();
		fcl.setOutStandPrincipal(summary.getOutStandPrincipal());
		fcl.setPricipleAmount(summary.getPrincipal());
		fcl.setFuturePricipleAmount(summary.getFuturePrincipal());
		fcl.setInterestAmount(summary.getInterest());
		clsrresponse.setForeClosureFees(schdData.getForeClosureFees());
		clsrresponse.setFeeDues(schdData.getFeeDues());

		for (ForeClosure foreClosure : foreClosureDetails) {
			fcl.setAccuredIntTillDate(foreClosure.getAccuredIntTillDate());
			fcl.setValueDate(foreClosure.getValueDate());
			fcl.setChargeAmount(foreClosure.getChargeAmount());
			fcl.setForeCloseAmount(foreClosure.getForeCloseAmount());
			fcl.setBounceCharge(foreClosure.getBounceCharge().add(totalBCgstFee));
			fcl.setTotalLPIAmount(foreClosure.getLPIAmount());
			fcl.setReceivableAdviceAmt(foreClosure.getReceivableADFee().add(totalADgstAmt));
			fcl.setFutureInterestAmount(foreClosure.getAccuredIntTillDate().subtract(summary.getInterest()));
		}

		int sdSize = schdData.getFinanceScheduleDetails().size();

		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;

		for (int i = 1; i < sdSize; i++) {

			curSchd = schdData.getFinanceScheduleDetails().get(i);
			prvSchd = schdData.getFinanceScheduleDetails().get(i - 1);

			if (curSchd.getSchDate().compareTo(fcl.getValueDate()) > 0) {
				break;
			}
		}

		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);
		BigDecimal futureInt = BigDecimal.ZERO;
		if (curSchd != null && prvSchd != null) {
			futureInt = getIntDueTillNBD(schdData.getFinanceMain(), prvSchd, curSchd, fcl.getValueDate(), roundAdjMth);
		}

		fcl.setFutureInterestAmount(futureInt.add(prvSchd.getProfitBalance()));

		FeeType taxDetail = feeTypeDAO.getApprovedFeeTypeByFeeCode(Allocation.ODC);
		BigDecimal totPenaltyGstAmt = getTotalPenaltyGstAmt(taxPercentages, fcl, taxDetail.getTaxComponent());

		fcl.setChargeAmount(fcl.getChargeAmount().add(totPenaltyGstAmt));
		fcl.setForeCloseAmount(fcl.getForeCloseAmount().add(totalBCgstFee).add(totalADgstAmt)
				.add(fcl.getTotalLPIAmount().add(totPenaltyGstAmt)));

		logger.debug(Literal.LEAVING);
		return fcl;
	}

	private BigDecimal getTotalPenaltyGstAmt(Map<String, BigDecimal> taxes, ForeClosureLetter fcl,
			String taxComponent) {

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxComponent)) {
			return GSTCalculator.getExclusiveGST(fcl.getChargeAmount(), taxes).gettGST();
		}

		return BigDecimal.ZERO;
	}

	private ForeClosureLetter getForeClosureLetter(List<FinExcessAmount> excessAmounts) {
		ForeClosureLetter letter = new ForeClosureLetter();

		for (FinExcessAmount fea : excessAmounts) {
			String amountType = fea.getAmountType();
			BigDecimal balanceAmt = fea.getBalanceAmt();

			if (StringUtils.contains(ExcessType.EXCESS, amountType)) {
				letter.setExcessAmount(balanceAmt);
			} else if (StringUtils.contains(ExcessType.EMIINADV, amountType)) {
				letter.setEmiInAdvance(balanceAmt);
			}
		}

		return letter;
	}

	private static BigDecimal getIntDueTillNBD(FinanceMain fm, FinanceScheduleDetail prvSchd,
			FinanceScheduleDetail curSchd, Date valueDate, String roundAdjMth) {

		BigDecimal intDueTillNBD = BigDecimal.ZERO;
		BigDecimal calIntFraction = prvSchd.getProfitFraction();

		// Calculate Interest
		if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {

			if ("NO_ADJ".equals(roundAdjMth) || ("ADJ_LAST_INST".equals(roundAdjMth)
					&& curSchd.getSchDate().compareTo(fm.getMaturityDate()) != 0)) {
				calIntFraction = BigDecimal.ZERO;
			}

			intDueTillNBD = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate, curSchd.getBalanceForPftCal(),
					prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

			intDueTillNBD = intDueTillNBD.add(calIntFraction);
			BigDecimal calIntRounded = BigDecimal.ZERO;
			if (intDueTillNBD.compareTo(BigDecimal.ZERO) > 0) {
				calIntRounded = CalculationUtil.roundAmount(intDueTillNBD, fm.getCalRoundingMode(),
						fm.getRoundingTarget());
			}

			calIntFraction = intDueTillNBD.subtract(calIntRounded);
			intDueTillNBD = calIntRounded;

		} else {
			intDueTillNBD = BigDecimal.ZERO;
		}

		return intDueTillNBD;
	}

	@Autowired
	public void setFinStatementController(FinStatementController finStatementController) {
		this.finStatementController = finStatementController;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}
