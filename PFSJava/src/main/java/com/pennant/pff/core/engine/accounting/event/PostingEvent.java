package com.pennant.pff.core.engine.accounting.event;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.AppException;

public abstract class PostingEvent {
	protected Logger logger = LogManager.getLogger(this.getClass());

	private static final String LITERAL1 = "{} is the AccountingSet-ID for [{}] FinType/Promotion and [{}] Accounting Event and [{}] Module";
	private static final String LITERAL2 = "AccountingSet-ID not found for the specified [%s] FinType/Promotion and [%s] Accounting Event and [%d] Module.";
	protected static final String LITERAL3 = "Preparing AEEvents for the accounting evnet {}";
	protected static final String LITERAL4 = "AEEvents preparing completed for the accounting evnet {}";

	public abstract List<AEEvent> prepareAEEvents(FinanceDetail fd, String userBranch);

	public void setEventDetails(List<AEEvent> aeEvents, FinanceDetail fd) {

	}

	protected Long getAccountingSetId(String finType, String accEvent, int module) {
		Long accountSetId = AccountingConfigCache.getAccountSetID(finType, accEvent, module);

		if (accountSetId == null || accountSetId == Long.MIN_VALUE) {
			throw new AppException(String.format(LITERAL2, finType, accEvent, module));
		}

		logger.info(LITERAL1, accountSetId, finType, accEvent, module);

		return accountSetId;
	}
}
