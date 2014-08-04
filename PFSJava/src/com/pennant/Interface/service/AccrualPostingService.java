package com.pennant.Interface.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface AccrualPostingService {
	
	public List<ReturnDataSet> doAccrualPosting(List<ReturnDataSet> setDetails, Date valueDate, String postBranch, 
			long linkTransId, String createNow, String isDummy) throws Exception; 
	
}
