package com.pennant.Interface.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface PostingsInterfaceService {
	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<ReturnDataSet> doFillPostingDetails(List<ReturnDataSet> setDetails,String finBranch,
			long linkTransId, String createNow) throws AccountNotFoundException;

	public List<ReturnDataSet> doAccrualPosting(List<ReturnDataSet> list, Date valueDate,
            String postBranch, long linkedTranId, String createNow, String isDummy) throws AccountNotFoundException; 
	
}
