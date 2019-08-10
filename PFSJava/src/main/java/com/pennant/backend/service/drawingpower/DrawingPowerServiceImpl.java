package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DrawingPower;

public class DrawingPowerServiceImpl implements DrawingPowerService {

	private static final Logger logger = Logger.getLogger(DrawingPowerServiceImpl.class);

	@Autowired(required = false)
	private DrawingPower drawingPower;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;

	@Override
	public String doRevolvingValidations(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		List<FinServiceInstruction> instructions = financeDetail.getFinScheduleData().getFinServiceInstructions();

		BigDecimal disbAmt = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(instructions)) {
			for (FinServiceInstruction instruction : instructions) {
				if (FinanceConstants.FINSER_EVENT_ADDDISB.equals(instruction.getFinEvent())) {
					disbAmt = disbAmt.add(instruction.getAmount());
				}
			}
		}
		logger.debug("Disbursemet Amt " + disbAmt);

		BigDecimal availableLimit = BigDecimal.ZERO;
		if (financeType.isAllowRevolving()) {
			availableLimit = financeMain.getFinAssetValue()
					.subtract(financeMain.getFinCurrAssetValue()).add(financeMain.getFinRepaymentAmount());

			logger.debug("Available Amt " + disbAmt);
			if (disbAmt.compareTo(availableLimit) > 0) {
				return "Disbursement amount should less than or equal to available amount.";
			}
		}
		logger.debug(Literal.ENTERING);
		return null;
	}

	@Override
	public String doDrawingPowerCheck(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		if (!financeType.isAlwSanctionAmt()) {
			
			logger.debug("AlwSanctionAmt " + 0);
			return null;
		}

		BigDecimal checkingAmt = financeMain.getFinAssetValue();
		
		logger.debug("checkingAmt " + checkingAmt);
		BigDecimal totOutStanding = BigDecimal.ZERO;

		FinanceProfitDetail profitDetail = financeDetail.getFinScheduleData().getFinPftDeatil();

		if (profitDetail == null) {
			profitDetail = this.financeProfitDetailDAO.getFinProfitDetailsById(financeMain.getFinReference());
		}

		totOutStanding = totOutStanding.add(profitDetail.getTotalPriBal());// Principal
																			// outstanding
		totOutStanding = totOutStanding.add(profitDetail.getTdSchdPftBal());// Interest
																			// receivable
		totOutStanding = totOutStanding.add(profitDetail.getPenaltyDue().subtract(profitDetail.getPenaltyPaid())
				.subtract(profitDetail.getPenaltyWaived()));// Penal
		// receivable
		totOutStanding = totOutStanding.add(profitDetail.getPftAccrued());// Accured
																			// interest

		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		BigDecimal tdsMultiplier = new BigDecimal(100).divide(new BigDecimal(100).subtract(tdsPerc), 20,
				RoundingMode.HALF_DOWN);

		BigDecimal pftReceivable = profitDetail.getPftAccrued().add(profitDetail.getTdSchdPftBal());
		BigDecimal tdsReceivable = pftReceivable.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
		tdsReceivable = CalculationUtil.roundAmount(tdsReceivable, tdsRoundMode, tdsRoundingTarget);

		totOutStanding = totOutStanding.add(tdsReceivable);// TDS Receivable
		List<ManualAdvise> advises = manualAdviseDAO.getManualAdviseByRef(financeMain.getFinReference(),
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "");// Any Charges
		if (CollectionUtils.isNotEmpty(advises)) {
			for (ManualAdvise manualAdvise : advises) {
				totOutStanding = totOutStanding.add(manualAdvise.getAdviseAmount()
						.subtract(manualAdvise.getPaidAmount().subtract(manualAdvise.getWaivedAmount())));
			}
		}

		logger.debug("totOutStanding " + totOutStanding);
		
		if (financeMain.isAllowDrawingPower()) {
			if (drawingPower != null) {
				BigDecimal drawingPowerAmt = drawingPower.getDrawingPower(financeMain.getFinReference());
				if ((drawingPowerAmt.compareTo(BigDecimal.ZERO) > 0) && (checkingAmt.compareTo(drawingPowerAmt)) > 0) {
					checkingAmt = drawingPowerAmt;
				}
				
				logger.debug("drawingPowerAmt " + drawingPowerAmt);
			}
		}

		logger.debug("totOutStanding " + totOutStanding);
		logger.debug("checkingAmt " + checkingAmt);
		if (totOutStanding.compareTo(checkingAmt) > 0) {
			String msg = "Sanction Amount less than sum of total outstanding.";
			if (financeType.isAlwSanctionAmtOverride()) {
				msg = msg.concat(" Do you want to proceed.?");
			}
			return msg;
		}
		
		logger.debug(Literal.LEAVING);
		return null;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

}