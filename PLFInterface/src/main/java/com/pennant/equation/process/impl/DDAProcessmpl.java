package com.pennant.equation.process.impl;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennant.coreinterface.process.DDAProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDAProcessmpl implements DDAProcess {

	private static final Logger logger = Logger.getLogger(DDAProcessmpl.class);
	
	public DDAProcessmpl() {
		
	}
	
	@Override
	public DDARegistration sendDDARequest(DDARegistration ddsRequest) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}

	@Override
	public DDAAmendment sendDDAAmendment(DDAAmendment ddaAmendmentReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}

	@Override
	public DDAUpdate sendDDAUpdate(DDAUpdate ddaUpdateReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}
	
	@Override
	public DDACancellation cancelDDARegistration(DDACancellation ddaCancellationReq) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}

}
