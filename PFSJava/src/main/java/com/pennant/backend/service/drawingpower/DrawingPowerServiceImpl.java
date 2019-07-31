package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pff.external.DrawingPower;

public class DrawingPowerServiceImpl implements DrawingPowerService {
	@Autowired(required = false)
	private DrawingPower drawingPower;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;

	@Override
	public String doDrawingPowerCheck(FinanceDetail financeDetail) {

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		if (!financeType.isAlwSanctionAmt()) {
			return null;
		}

		BigDecimal checkingAmt = financeMain.getFinAssetValue();
		BigDecimal totOutStanding = BigDecimal.ZERO;

		FinanceProfitDetail profitDetail = financeDetail.getFinScheduleData().getFinPftDeatil();

		if (profitDetail == null) {
			profitDetail = this.financeProfitDetailDAO.getFinProfitDetailsById(financeMain.getFinReference());
		}

		totOutStanding = totOutStanding.add(profitDetail.getTotalPriBal());//Principal outstanding
		totOutStanding = totOutStanding.add(profitDetail.getTotalPftPaid());//Interest receivable
		totOutStanding = totOutStanding.add(profitDetail.getPenaltyPaid());//Penal receivable
		totOutStanding = totOutStanding.add(profitDetail.getPftAccrued());//Accured interest

		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
		BigDecimal tdsMultiplier = new BigDecimal(100).divide(new BigDecimal(100).subtract(tdsPerc), 20,
				RoundingMode.HALF_DOWN);

		BigDecimal accrueDue = profitDetail.getPftAccrued().subtract(profitDetail.getTdSchdPftBal());
		BigDecimal accrueTDS = accrueDue.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
		accrueTDS = CalculationUtil.roundAmount(accrueTDS, tdsRoundMode, tdsRoundingTarget);

		totOutStanding = totOutStanding.add(profitDetail.getTdsAccrued());//TDS Receivable//FIXME
		List<ManualAdvise> advises = manualAdviseDAO.getManualAdvise(financeMain.getFinReference());//Any Charges
		if (CollectionUtils.isNotEmpty(advises)) {
			for (ManualAdvise manualAdvise : advises) {
				totOutStanding = totOutStanding.add(manualAdvise.getAdviseAmount()
						.subtract(manualAdvise.getPaidAmount().subtract(manualAdvise.getWaivedAmount())));
			}
		}

		if (financeMain.isAllowDrawingPower()) {
			if (drawingPower != null) {

				BigDecimal drawingPowerAmt = drawingPower.getDrawingPower(financeMain.getFinReference());
				BigDecimal disbursementAmt = drawingPower.getDrawingPower(financeMain.getFinReference());

				if (checkingAmt.compareTo(drawingPowerAmt) > 0) {
					checkingAmt = drawingPowerAmt;
				}

				StringBuilder sb = new StringBuilder();
				String drawingPowerAmte = PennantApplicationUtil.amountFormate(drawingPowerAmt, 2);
				String disbursementAmte = PennantApplicationUtil.amountFormate(disbursementAmt, 2);
				sb.append("Drawing power amount : ");
				sb.append(drawingPowerAmte);
				sb.append(", Disbursement amount :");
				sb.append(disbursementAmte);

				return sb.toString();
			}
		}

		if (totOutStanding.compareTo(checkingAmt) > 0) {
			String msg = "Sanction AMount less than sum of totak outstanding.";
			if (financeType.isAlwSanctionAmtOverride()) {
				msg = msg.concat(" Do you want to proceed.?");
			}
			return msg;
		}
		return null;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

}