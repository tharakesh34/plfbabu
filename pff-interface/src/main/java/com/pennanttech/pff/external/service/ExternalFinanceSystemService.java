package com.pennanttech.pff.external.service;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;

public interface ExternalFinanceSystemService {

	public void processLMSEvents();

	public void saveDisursementEvent(long respBatchId);

	public void createLoan(FinanceMain afinanceMain, String operation);

	public int loanClosureDetails();

	public void getDocumentStatusList(List<DocumentStatusDetail> dsList, String finReference);

}