package com.pennant.coreinterface.process;

import java.util.Date;
import java.util.List;

import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennanttech.pennapps.core.InterfaceException;

public interface AccountPostingProcess {

	List<CoreBankAccountPosting> doPostings(List<CoreBankAccountPosting> accountPostings,String postBranch,
			String createNow) throws InterfaceException ;

	List<CoreBankAccountPosting> doUploadAccruals(List<CoreBankAccountPosting> postings, Date valueDate,
			String postBranch, String isDummy) throws InterfaceException;

	List<CoreBankAccountPosting> doReversalPostings(
			List<CoreBankAccountPosting> accountPostings, String postBranch, String createNow) throws InterfaceException;
}
