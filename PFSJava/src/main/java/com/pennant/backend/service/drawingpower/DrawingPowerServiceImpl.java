package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.external.DrawingPower;

public class DrawingPowerServiceImpl implements DrawingPowerService {

	private static final Logger logger = LogManager.getLogger(DrawingPowerServiceImpl.class);

	@Autowired(required = false)
	private DrawingPower drawingPower;
	private ManualAdviseDAO manualAdviseDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;

	/**
	 * Validating the customer drawing power, revolving limit based on configuration.
	 */
	@Override
	public AuditDetail validate(AuditDetail auditDetail, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		String userAction = fd.getUserAction();
		String moduleDefiner = fd.getModuleDefiner();
		FinanceMain financeMain = fd.getFinScheduleData().getFinanceMain();
		FinanceType financeType = fd.getFinScheduleData().getFinanceType();

		// Add disbursement validations.
		if (moduleDefiner.equals(FinServiceEvent.ADDDISB)) {

			if (!"Cancel".equalsIgnoreCase(userAction) && !"Resubmit".equalsIgnoreCase(userAction)
					&& !"Reject".equalsIgnoreCase(userAction)) {

				// Revolving limit checking
				if (financeMain.isAllowRevolving()) {
					ErrorDetail errorDetail = doRevolvingValidations(fd);
					if (errorDetail != null) {
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			}

			// If revolving limit exists then check customer drawing power
			// limit.
			ErrorDetail errorDetail = doDrawingPowerCheck(fd, moduleDefiner);
			if (errorDetail != null) {
				auditDetail.setErrorDetail(errorDetail);
				return auditDetail;
			}
		}

		// LOS validations.
		if (financeType.isAllowDrawingPower()) {
			if (StringUtils.isEmpty(moduleDefiner) || StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {

				if (!"Cancel".equalsIgnoreCase(userAction) && !"Resubmit".equalsIgnoreCase(userAction)
						&& !"Reject".equalsIgnoreCase(userAction) && "Submit".equalsIgnoreCase(userAction)) {

					ErrorDetail errorDetail = doDrawingPowerCheck(fd, moduleDefiner);
					if (errorDetail != null) {
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			}
		}
		return auditDetail;
	}

	public ErrorDetail doRevolvingValidations(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		FinanceType financeType = schdData.getFinanceType();
		int ccyFormat = CurrencyUtil.getFormat(fm.getFinCcy());

		List<FinServiceInstruction> instructions = schdData.getFinServiceInstructions();

		BigDecimal disbAmt = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(instructions)) {
			for (FinServiceInstruction instruction : instructions) {
				if (FinServiceEvent.ADDDISB.equals(instruction.getFinEvent())) {
					disbAmt = disbAmt.add(instruction.getAmount());
				}
			}
		}
		logger.debug("Disbursemet Amt " + disbAmt);

		BigDecimal availableLimit = BigDecimal.ZERO;
		if (financeType.isAllowRevolving()) {

			FinanceMain main = financeMainDAO.getFinanceMainById(fm.getFinReference(), "", false);

			BigDecimal currAssetVal = BigDecimal.ZERO;
			if (main != null) {
				currAssetVal = main.getFinCurrAssetValue();
			} else {
				currAssetVal = fm.getFinCurrAssetValue();
			}
			availableLimit = fm.getFinAssetValue().subtract(currAssetVal).add(fm.getFinRepaymentAmount());

			logger.debug("Available Amt " + disbAmt);
			if (disbAmt.compareTo(availableLimit) > 0) {

				String[] errParm = new String[2];
				errParm[0] = PennantApplicationUtil.amountFormate(disbAmt, ccyFormat);
				errParm[1] = PennantApplicationUtil.amountFormate(availableLimit, ccyFormat);

				return new ErrorDetail(PennantConstants.KEY_FIELD, "REV01", errParm, null);
			}
		}
		logger.debug(Literal.ENTERING);
		return null;
	}

	public ErrorDetail doDrawingPowerCheck(FinanceDetail financeDetail, String moduleDefiner) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		int ccyFormat = CurrencyUtil.getFormat(financeMain.getFinCcy());

		if (!financeType.isAlwSanctionAmt()) {
			logger.debug("AlwSanctionAmt " + 0);
			return null;
		}

		BigDecimal checkingAmt = financeMain.getFinAssetValue();

		logger.debug("checkingAmt " + checkingAmt);
		BigDecimal totOutStanding = BigDecimal.ZERO;

		FinanceProfitDetail profitDetail = financeDetail.getFinScheduleData().getFinPftDeatil();

		// Setting drawing power amount to extended fields
		if (financeDetail.getExtendedFieldRender() != null
				&& financeDetail.getExtendedFieldRender().getMapValues() != null) {
			if (financeDetail.getExtendedFieldRender().getMapValues().containsKey("DRAWINGPOWAMT")) {
				BigDecimal drawingPowerAmt = drawingPower.getDrawingPower(financeMain.getFinReference());
				financeDetail.getExtendedFieldRender().getMapValues().put("DRAWINGPOWAMT", drawingPowerAmt);
			}
		}

		if (!StringUtils.isEmpty(moduleDefiner) && (!StringUtils.equals(moduleDefiner, FinServiceEvent.ORG))) {
			if (profitDetail == null) {
				profitDetail = this.financeProfitDetailDAO.getFinProfitDetailsById(financeMain.getFinReference());
			}
			if (profitDetail != null) {
				totOutStanding = totOutStanding.add(profitDetail.getTotalPriBal());// Principal
																					// outstanding
				totOutStanding = totOutStanding.add(profitDetail.getPenaltyDue().subtract(profitDetail.getPenaltyPaid())
						.subtract(profitDetail.getPenaltyWaived()));// Penal
				// receivable
				totOutStanding = totOutStanding.add(profitDetail.getPftAccrued());// Accured
																					// interest

				String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
				int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
				BigDecimal tdsPerc = new BigDecimal(
						SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
				BigDecimal tdsMultiplier = new BigDecimal(100).divide(new BigDecimal(100).subtract(tdsPerc), 20,
						RoundingMode.HALF_DOWN);

				BigDecimal pftReceivable = profitDetail.getPftAccrued().add(profitDetail.getTdSchdPftBal());
				BigDecimal tdsReceivable = pftReceivable.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
				tdsReceivable = CalculationUtil.roundAmount(tdsReceivable, tdsRoundMode, tdsRoundingTarget);

				totOutStanding = totOutStanding.add(tdsReceivable);// TDS
																	// Receivable
				List<ManualAdvise> advises = manualAdviseDAO.getManualAdviseByRef(financeMain.getFinReference(),
						FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "");// Any
																		// Charges
				if (CollectionUtils.isNotEmpty(advises)) {
					for (ManualAdvise manualAdvise : advises) {
						totOutStanding = totOutStanding.add(manualAdvise.getAdviseAmount()
								.subtract(manualAdvise.getPaidAmount().subtract(manualAdvise.getWaivedAmount())));
					}
				}
			}
			logger.debug("totOutStanding " + totOutStanding);
		}
		if (financeMain.isAllowDrawingPower()) {
			if (drawingPower != null) {
				BigDecimal drawingPowerAmt = drawingPower.getDrawingPower(financeMain.getFinReference());
				if (StringUtils.isEmpty(moduleDefiner) || StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {
					checkingAmt = financeMain.getFinCurrAssetValue();
					if (checkingAmt.compareTo(drawingPowerAmt) > 0) {

						String[] errParm = new String[2];
						errParm[0] = PennantApplicationUtil.amountFormate(checkingAmt, ccyFormat);
						errParm[1] = PennantApplicationUtil.amountFormate(drawingPowerAmt, ccyFormat);

						if (financeType.isAlwSanctionAmtOverride()) {
							return new ErrorDetail(PennantConstants.KEY_FIELD, "DP002", errParm, null);
						} else {
							return new ErrorDetail(PennantConstants.KEY_FIELD, "DP001", errParm, null);
						}
					}
				} else {
					if ((drawingPowerAmt.compareTo(BigDecimal.ZERO) > 0)
							&& (checkingAmt.compareTo(drawingPowerAmt)) > 0) {
						checkingAmt = drawingPowerAmt;
					}

					BigDecimal totDisbAmt = BigDecimal.ZERO;
					List<FinanceDisbursement> disbursements = financeDetail.getFinScheduleData()
							.getDisbursementDetails();

					if (CollectionUtils.isNotEmpty(disbursements)) {

						for (FinanceDisbursement disbursement : disbursements) {

							if (financeDisbursementDAO
									.getFinDsbursmntInstrctnIds(disbursement.getInstructionUID()) <= 0) {
								totDisbAmt = totDisbAmt.add(disbursement.getDisbAmount());
							}
						}
					}

					logger.debug("Totak Disb amount " + totDisbAmt);

					totOutStanding = totOutStanding.add(totDisbAmt);
					if (totOutStanding.compareTo(checkingAmt) > 0) {

						String[] errParm = new String[2];
						errParm[0] = PennantApplicationUtil.amountFormate(totOutStanding, ccyFormat);
						errParm[1] = PennantApplicationUtil.amountFormate(drawingPowerAmt, ccyFormat);

						if (financeType.isAlwSanctionAmtOverride()) {
							return new ErrorDetail(PennantConstants.KEY_FIELD, "DP002", errParm, null);
						} else {
							return new ErrorDetail(PennantConstants.KEY_FIELD, "DP001", errParm, null);
						}
					}
				}
				logger.debug("drawingPowerAmt " + drawingPowerAmt);
			}
		}

		logger.debug("totOutStanding " + totOutStanding);
		logger.debug("checkingAmt " + checkingAmt);

		logger.debug(Literal.LEAVING);
		return null;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Override
	public BigDecimal getDrawingPower(String finreference) {
		if (drawingPower == null) {
			return BigDecimal.ZERO;
		}
		return drawingPower.getDrawingPower(finreference);
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

}