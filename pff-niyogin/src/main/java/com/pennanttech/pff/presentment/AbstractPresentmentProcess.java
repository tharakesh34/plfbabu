package com.pennanttech.pff.presentment;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.PresentmentRequest;

public class AbstractPresentmentProcess extends AbstractInterface implements PresentmentRequest {
	private static final Logger	logger	= Logger.getLogger(AbstractPresentmentProcess.class);

	@Override
	public void sendReqest(List<Long> idList, long headerId, boolean isError) throws Exception {
		PresentmentRequestProcess process = new PresentmentRequestProcess(dataSource,new Long(1000),getValueDate(), idList, headerId,isError);
		process.processData();
		
	}
}
