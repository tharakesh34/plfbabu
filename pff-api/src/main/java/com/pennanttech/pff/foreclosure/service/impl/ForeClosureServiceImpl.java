package com.pennanttech.pff.foreclosure.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.GSTCalculator;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.ForeClosureLetter;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.controller.FinStatementController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.foreclosure.service.ForeClosureService;
import com.pennanttech.pff.receipt.constants.Allocation;
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
	public ForeClosureLetter getForeClosureAmt(FinStatementRequest request) {
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

		for (ForeClosure foreClosure : foreClosureDetails) {
			fcl.setAccuredIntTillDate(foreClosure.getAccuredIntTillDate());
			fcl.setValueDate(foreClosure.getValueDate());
			fcl.setChargeAmount(foreClosure.getChargeAmount());
			fcl.setForeCloseAmount(foreClosure.getForeCloseAmount());
			fcl.setBounceCharge(foreClosure.getBounceCharge().add(totalBCgstFee));
			fcl.setTotalLPIAmount(foreClosure.getLPIAmount());
			fcl.setReceivableAdviceAmt(foreClosure.getReceivableADFee().add(totalADgstAmt));
		}

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

			if (StringUtils.contains(RepayConstants.EXAMOUNTTYPE_EXCESS, amountType)) {
				letter.setExcessAmount(balanceAmt);
			} else if (StringUtils.contains(RepayConstants.EXAMOUNTTYPE_EMIINADV, amountType)) {
				letter.setEmiInAdvance(balanceAmt);
			}
		}

		return letter;
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
