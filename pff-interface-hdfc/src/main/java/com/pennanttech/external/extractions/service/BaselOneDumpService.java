package com.pennanttech.external.extractions.service;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennanttech.external.dao.ExtStagingDao;
import com.pennanttech.external.extractions.model.BaselOne;

public class BaselOneDumpService {

	private ExtStagingDao extStageDao;

	public void processBaselOne(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEods = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEOD : finEods) {
			FinanceProfitDetail fpd = finEOD.getFinProfitDetail();
			BaselOne baselOne = new BaselOne();
			baselOne.setAgreementId(fpd.getFinReference());
			baselOne.setCustomerId(fpd.getCustCIF());
			baselOne.setRegEmi(fpd.getTdSchdPftBal().add(fpd.getTdSchdPriBal()));

			baselOne.setExcessmoney(fpd.getExcessAmtBal());

			baselOne.setAccruedInterest(fpd.getAmzTillLBD());// fpd.getPftAccrued());
			baselOne.setUnEarnedInterest(fpd.getTotalPftSchd().subtract(fpd.getAmzTillLBD()));// fpd.getUnearned());
			baselOne.setGrossReceivable(fpd.getTotalpriSchd().subtract(fpd.getTdSchdPri()));
			baselOne.setSuspenseInterest(fpd.getPftAmzSusp());// Check
			baselOne.setAssetClassficationId("");// FIXME finEOD.getFinType().getFinTypeClassification();
			baselOne.setSecuritized("M");
			baselOne.setExactNpaDate(null);// FIXME
			baselOne.setAnnualTurnover(new BigDecimal("0"));
			baselOne.setExptype("R");
			baselOne.setTenure(fpd.getTotalTenor());
			extStageDao.saveBaselOneExtractionDataToTable(baselOne);
		}
	}

	public void setExtStageDao(ExtStagingDao extStageDao) {
		this.extStageDao = extStageDao;
	}

}
