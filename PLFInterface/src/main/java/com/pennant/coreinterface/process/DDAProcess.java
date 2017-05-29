package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennant.exception.PFFInterfaceException;

public interface DDAProcess {
	
	DDARegistration sendDDARequest(DDARegistration ddsRequest) throws PFFInterfaceException;

	DDAAmendment sendDDAAmendment(DDAAmendment ddaAmendmentReq) throws PFFInterfaceException;

	DDAUpdate sendDDAUpdate(DDAUpdate ddaUpdateReq) throws PFFInterfaceException;

	DDACancellation cancelDDARegistration(DDACancellation ddaCancellationReq) throws PFFInterfaceException;

}
