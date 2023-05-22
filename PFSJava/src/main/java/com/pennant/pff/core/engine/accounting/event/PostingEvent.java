package com.pennant.pff.core.engine.accounting.event;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.AppException;

public abstract class PostingEvent {
	protected Logger logger = LogManager.getLogger(this.getClass());

	private static final String LITERAL1 = "{} is the AccountingSet-ID for [{}] FinType/Promotion and [{}] Accounting Event and [{}] Module";
	private static final String LITERAL2 = "AccountingSet-ID not found for the specified [%s] FinType/Promotion and [%s] Accounting Event and [%d] Module.";
	protected static final String LITERAL3 = "Preparing AEEvents for the accounting evnet {}";
	protected static final String LITERAL4 = "AEEvents preparing completed for the accounting evnet {}";

	public abstract List<AEEvent> prepareAEEvents(PostingDTO postingDTO);

	public void setEventDetails(List<AEEvent> aeEvents, PostingDTO postingDTO) {

	}

	public boolean prvStageAccountingCheck(List<ReturnDataSet> returnDataSet, PostingDTO postingDTO) {
		return false;
	}

	protected Long getAccountingSetId(FinanceMain fm, String accEvent, int module) {
		Long accountSetId = AccountingEngine.getAccountSetID(fm, accEvent);

		if (accountSetId == null || accountSetId <= 0) {
			throw new AppException(String.format(LITERAL2, fm.getFinType(), accEvent, module));
		}

		logger.info(LITERAL1, accountSetId, fm.getFinType(), accEvent, module);

		return accountSetId;
	}

}
