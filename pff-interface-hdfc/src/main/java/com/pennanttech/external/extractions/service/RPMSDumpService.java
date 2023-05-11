package com.pennanttech.external.extractions.service;

import java.util.List;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennanttech.external.dao.ExtStagingDao;
import com.pennanttech.external.extractions.model.RPMSExtract;

public class RPMSDumpService {
	private ExtStagingDao extStageDao;

	public void setExtStageDao(ExtStagingDao extStageDao) {
		this.extStageDao = extStageDao;
	}

	public void processRPMSDump(CustEODEvent custEODEvent) {

		List<FinEODEvent> finEods = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEOD : finEods) {
			FinanceProfitDetail fpd = finEOD.getFinProfitDetail();
			FinanceMain fm = finEOD.getFinanceMain();
			RPMSExtract rpmsExtract = new RPMSExtract();
			rpmsExtract.setAgreementId(Long.parseLong(fpd.getFinReference()));
			rpmsExtract.setCustomerId(Long.parseLong(fpd.getCustCIF()));
			rpmsExtract.setStatus(fm.getClosingStatus());

			rpmsExtract.setMultiLinkLoanFlag(null); // FIXME
			rpmsExtract.setParentLoanNo(0); // FIXME
			rpmsExtract.setCustomerSegment(null);// FIXME
			rpmsExtract.setGroupId(0);// FIXME
			rpmsExtract.setGroupCode(null);// FIXME
			rpmsExtract.getGroupDesc();// FIXME
			rpmsExtract.setRepoSettledFlag(null);// FIXME
			rpmsExtract.setClosureDate(null);// FIXME
			rpmsExtract.setClosureReason(null);// FIXME
			rpmsExtract.setPosOnClosure(null);// FIXME
			rpmsExtract.setPdcFlag(null);// FIXME
			rpmsExtract.setTotPrinWaiveOff(null);// FIXME
			rpmsExtract.setTotIntWaiveOff(null);// FIXME
			rpmsExtract.setWoffOverDueCharge(null);// FIXME
			rpmsExtract.setWoffChqBounceCharges(null);// FIXME
			rpmsExtract.setWoffOthers(null);// FIXME
			rpmsExtract.setRamId(null);// FIXME
			rpmsExtract.setTurnOverInYearOne(null);// FIXME
			rpmsExtract.setTurnOverAmtYearOne(null);// FIXME
			rpmsExtract.setTurnOverInYearTwo(null);// FIXME
			rpmsExtract.setTurnOverAmtYearTwo(null);// FIXME
			rpmsExtract.setTurnOverInYearThree(null);// FIXME
			rpmsExtract.setTurnOverAmtYearThree(null);// FIXME
			extStageDao.saveRPMSExtractExtractionDataToTable(rpmsExtract);
		}

	}

}
