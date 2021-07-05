package com.pennant.pff.core.engine.accounting;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountingEvent;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.pff.core.engine.accounting.event.PostingEvent;
import com.pennanttech.pennapps.core.AppException;

public class AccountingEngine {
	private static final Logger logger = LogManager.getLogger(AccountingEngine.class);
	private static PostingEventFactory factory;
	private static PostingsDAO postingsDAO;
	private static AccountEngineExecution engineExecution;

	public static List<ReturnDataSet> execute(AccountingEvent event, FinanceDetail fd, String userBranch) {
		List<ReturnDataSet> transactions = new ArrayList<>();

		List<AEEvent> events = executeEvent(event, fd, userBranch);

		events.stream().forEach(aeEvent -> transactions.addAll(aeEvent.getReturnDataSet()));

		return transactions;
	}

	public static List<ReturnDataSet> post(AccountingEvent event, FinanceDetail fd, String userBranch) {
		String eventName = event.name();
		logger.info("Posting accounting tranactions for {} event .", eventName);
		List<AEEvent> events = executeEvent(event, fd, userBranch);

		List<ReturnDataSet> transactions = new ArrayList<>();

		events.stream().forEach(aeEvent -> {
			long linkedTranId = postingsDAO.getLinkedTransId();
			String acEvent = aeEvent.getAccountingEvent();
			logger.info("Posting accounting tranactions for {} event with LinkedTranId {}.", acEvent, linkedTranId);
			aeEvent.setLinkedTranId(linkedTranId);
			aeEvent.getReturnDataSet().stream().forEach(rds -> rds.setLinkedTranId(linkedTranId));
			transactions.addAll(aeEvent.getReturnDataSet());
		});

		PostingEvent postingEvent = getPostingEvent(event);
		postingEvent.setEventDetails(events, fd);

		postingsDAO.saveBatch(transactions);

		logger.info("Posting accounting tranactions for {} event completed.", eventName);
		return transactions;
	}

	public static List<ReturnDataSet> getReversalsByLinkedTranID(long linkedTranId) {
		long newLinkedTranID = postingsDAO.getLinkedTransId();

		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByLinkTransId(linkedTranId);

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID);

		return returnDataSets;
	}

	private static List<AEEvent> executeEvent(AccountingEvent event, FinanceDetail fd, String userBranch) {
		String eventName = event.name();
		logger.info("Executing accounting tranactions for {} event started.", eventName);
		List<AEEvent> events = getPostingEvent(event).prepareAEEvents(fd, userBranch);

		events.stream().forEach(aeEvent -> {
			engineExecution.getAccEngineExecResults(aeEvent);

			if (!aeEvent.isPostingSucess()) {
				logger.warn("Executing accounting tranactions for {} event failed.", aeEvent.getErrorMessage());
				throw new AppException("Executing accounting tranactions for {} event failed.");
			}
		});

		logger.info("Executing accounting tranactions for {} event completed.", eventName);
		return events;
	}

	private static PostingEvent getPostingEvent(AccountingEvent event) {
		return factory.getAccountingEventEvent(event);
	}

	public static void setFactory(PostingEventFactory factory) {
		AccountingEngine.factory = factory;
	}

	public static void setPostingsDAO(PostingsDAO postingsDAO) {
		AccountingEngine.postingsDAO = postingsDAO;
	}

	public static void setEngineExecution(AccountEngineExecution engineExecution) {
		AccountingEngine.engineExecution = engineExecution;
	}

}
