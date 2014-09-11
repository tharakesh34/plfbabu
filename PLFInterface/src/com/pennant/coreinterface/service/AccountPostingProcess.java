package com.pennant.coreinterface.service;

import java.util.Date;
import java.util.List;

import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.model.CoreBankAccountPosting;

public interface AccountPostingProcess {

	List<CoreBankAccountPosting> doFillPostingDetails(List<CoreBankAccountPosting> accountPostings,String postBranch,
			String createNow) throws AccountNotFoundException ;

	List<CoreBankAccountPosting> doUploadAccruals(List<CoreBankAccountPosting> postings, Date valueDate,
			String postBranch, String isDummy) throws AccountNotFoundException;
}
