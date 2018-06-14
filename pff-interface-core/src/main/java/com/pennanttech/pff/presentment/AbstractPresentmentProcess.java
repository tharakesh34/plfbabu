package com.pennanttech.pff.presentment;

import java.util.List;

import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.PresentmentRequest;

public class AbstractPresentmentProcess extends AbstractInterface implements PresentmentRequest {

	@Override
	public void sendReqest(List<Long> idList, long headerId, boolean isError, boolean isPDC) throws Exception {
		PresentmentRequestProcess process = new PresentmentRequestProcess(dataSource,new Long(1000),getValueDate(), idList, headerId,isError);
		process.processData();
	}
}
