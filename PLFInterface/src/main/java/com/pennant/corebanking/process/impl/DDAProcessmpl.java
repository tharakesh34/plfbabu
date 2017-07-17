package com.pennant.corebanking.process.impl;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennant.coreinterface.process.DDAProcess;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDAProcessmpl implements DDAProcess {

	private static final Logger logger = Logger.getLogger(DDAProcessmpl.class);
	
	public DDAProcessmpl() {
		
	}
	
	@Override
	public DDARegistration sendDDARequest(DDARegistration ddsRequest) throws InterfaceException {
		logger.debug("Entering");
		
		DDARegistration ddaRegistration = new DDARegistration();
		ddaRegistration.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		ddaRegistration.setReturnCode("0000");
		ddaRegistration.setReturnText("SUCESS");
		ddaRegistration.setTimeStamp(System.currentTimeMillis());
		
		logger.debug("Leaving");
		
		return ddaRegistration;
	}

	@Override
	public DDAAmendment sendDDAAmendment(DDAAmendment ddaAmendmentReq) throws InterfaceException {
		logger.debug("Entering");
		
		DDAAmendment ddaAmendment = new DDAAmendment();
		ddaAmendment.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		ddaAmendment.setReturnCode("0000");
		ddaAmendment.setReturnText("SUCESS");
		ddaAmendment.setTimeStamp(System.currentTimeMillis());
		
		logger.debug("Leaving");
		
		return ddaAmendment;
	}

	@Override
	public DDAUpdate sendDDAUpdate(DDAUpdate ddaUpdateReq) throws InterfaceException {
		logger.debug("Entering");
		
		DDAUpdate ddaUpdate = new DDAUpdate();
		ddaUpdate.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		ddaUpdate.setReturnCode("0000");
		ddaUpdate.setReturnText("SUCESS");
		ddaUpdate.setTimeStamp(System.currentTimeMillis());
		
		logger.debug("Leaving");
		
		return ddaUpdate;
	}

	@Override
	public DDACancellation cancelDDARegistration(DDACancellation ddaCancellationReq) throws InterfaceException {
		logger.debug("Entering");
		
		DDACancellation ddaCancelReply = new DDACancellation();
		ddaCancelReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		ddaCancelReply.setReturnCode("0000");
		ddaCancelReply.setReturnText("SUCESS");
		ddaCancelReply.setTimeStamp(System.currentTimeMillis());
		
		logger.debug("Leaving");
		
		return ddaCancelReply;
	}

}
