package com.pennanttech.external.extractions.service;

import java.util.List;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennanttech.external.dao.ExtStagingDao;
import com.pennanttech.external.extractions.model.BaselTwoExtract;

public class BaselTwoDumpService {

	private ExtStagingDao extStageDao;

	public void setExtStageDao(ExtStagingDao extStageDao) {
		this.extStageDao = extStageDao;
	}

	public void processBaselTwoDump(CustEODEvent custEODEvent) {

		List<FinEODEvent> finEods = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEOD : finEods) {
			FinanceProfitDetail fpd = finEOD.getFinProfitDetail();
			FinanceMain fm = finEOD.getFinanceMain();
			BaselTwoExtract baselTwo = new BaselTwoExtract();
			baselTwo.setAgreementId(Long.parseLong(fpd.getFinReference()));
			baselTwo.setCustomerId(Long.parseLong(fpd.getCustCIF()));
			baselTwo.setLoanApplDate(fm.getFinStartDate());
			baselTwo.setTotalEmis(fm.getNumberOfTerms());
			baselTwo.setEmiType("E");
			baselTwo.setTotAmtPaidMnthChq(null);
			baselTwo.setTotAmtPaidMnthCash(null);
			baselTwo.setPrinLossClosure(null);
			baselTwo.setIbpcStart(null);
			baselTwo.setIbpcEnd(null);
			baselTwo.setIcnStatus(null);
			baselTwo.setIcnAcquired(null);
			baselTwo.setDnd(null);
			baselTwo.setAppliedBefore(null);
			baselTwo.setOldAgmtNo(null);
			baselTwo.setMicrLocation(null);
			baselTwo.setFees(null);
			baselTwo.setFixedFloat(null);
			baselTwo.setDealerComm(null);
			baselTwo.setManfDisc(null);
			baselTwo.setPromotionCode(null);
			baselTwo.setRcAcquired(null);
			baselTwo.setRcAvailStatus(null);
			baselTwo.setRest(null);
			baselTwo.setCustAccount(null);
			baselTwo.setNoBounces(0);
			baselTwo.setChargesPaid(null);
			baselTwo.setPrevNPA(null);
			baselTwo.setSecurtDt(null);
			baselTwo.setSecurtFlag(null);
			baselTwo.setDpdString(null);
			baselTwo.setRescheduled(null);
			baselTwo.setRescheduleEffDt(null);
			baselTwo.setClosureType(null);
			baselTwo.setMonthsInPrevJob(0);
			baselTwo.setMonthsIncurrJob(0);
			baselTwo.setMonthsInCurrResidence(0);
			baselTwo.setMonthsInCity(0);
			baselTwo.setRentPM(null);
			baselTwo.setDeviation(null);
			baselTwo.setVehicleAge(0);
			baselTwo.setAppScore(0);
			baselTwo.setRicScore(0);
			baselTwo.setIncomeProofReceived(null);
			baselTwo.setTotalExperience(null);
			baselTwo.setEmployerCategory(null);
			baselTwo.setEligibility(null);
			baselTwo.setObligations(null);
			baselTwo.setObligationsn(null);
			extStageDao.saveBaselTwoExtractionDataToTable(baselTwo);
		}

	}

}
