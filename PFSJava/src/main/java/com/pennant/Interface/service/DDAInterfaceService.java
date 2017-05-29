package com.pennant.Interface.service;

import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennant.exception.PFFInterfaceException;

public interface DDAInterfaceService {

	DDAProcessData sendDDARegistrationReq(DDAProcessData ddaProcessRequest) throws PFFInterfaceException;
	
	DDAAmendment sendDDAAmendmentReq(DDAAmendment ddaAmendment) throws PFFInterfaceException;
	
	DDAUpdate sendDDAUpdateReq(DDAUpdate ddaUpdate) throws PFFInterfaceException;

	DDAProcessData cancelDDARegistration(DDAProcessData ddaCancelReq) throws PFFInterfaceException;

}
