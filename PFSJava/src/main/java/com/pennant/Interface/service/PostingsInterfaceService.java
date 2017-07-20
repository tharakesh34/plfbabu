package com.pennant.Interface.service;

import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.InterfaceException;

public interface PostingsInterfaceService {
	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	List<ReturnDataSet> doFillPostingDetails(List<ReturnDataSet> setDetails,String finBranch,
			long linkTransId, boolean isCreateNow) throws InterfaceException;

	List<ReturnDataSet> doAccrualPosting(List<ReturnDataSet> list, Date valueDate,
            String postBranch, long linkedTranId, boolean isCreateNow, String isDummy) throws InterfaceException;

}
