package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.exception.PFFInterfaceException;

public interface UploadProfitDetailProcess {

	void doUploadPftDetails(List<EodFinProfitDetail> profitDetails,	boolean isItFirstCall) 
			throws PFFInterfaceException;

}
