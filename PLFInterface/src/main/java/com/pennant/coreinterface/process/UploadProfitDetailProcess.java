package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface UploadProfitDetailProcess {

	void doUploadPftDetails(List<EodFinProfitDetail> profitDetails,	boolean isItFirstCall) 
			throws InterfaceException;

}
