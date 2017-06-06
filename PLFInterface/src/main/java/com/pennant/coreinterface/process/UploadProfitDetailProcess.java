package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.exception.InterfaceException;

public interface UploadProfitDetailProcess {

	void doUploadPftDetails(List<EodFinProfitDetail> profitDetails,	boolean isItFirstCall) 
			throws InterfaceException;

}
