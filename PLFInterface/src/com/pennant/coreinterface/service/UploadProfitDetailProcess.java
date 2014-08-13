package com.pennant.coreinterface.service;

import java.util.List;

import com.pennant.coreinterface.model.EodFinProfitDetail;

public interface UploadProfitDetailProcess {

	void doUploadPftDetails(List<EodFinProfitDetail> profitDetails,	boolean isItFirstCall) 
			throws Exception;

}
