package com.pennant.Interface.service;

import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennanttech.pennapps.core.InterfaceException;

public interface DDAInterfaceService {

	DDAProcessData sendDDARegistrationReq(DDAProcessData ddaProcessRequest) throws InterfaceException;
	
	DDAAmendment sendDDAAmendmentReq(DDAAmendment ddaAmendment) throws InterfaceException;
	
	DDAUpdate sendDDAUpdateReq(DDAUpdate ddaUpdate) throws InterfaceException;

	DDAProcessData cancelDDARegistration(DDAProcessData ddaCancelReq) throws InterfaceException;

}
