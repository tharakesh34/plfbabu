package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennanttech.pennapps.core.InterfaceException;

public interface DDAProcess {
	
	DDARegistration sendDDARequest(DDARegistration ddsRequest) throws InterfaceException;

	DDAAmendment sendDDAAmendment(DDAAmendment ddaAmendmentReq) throws InterfaceException;

	DDAUpdate sendDDAUpdate(DDAUpdate ddaUpdateReq) throws InterfaceException;

	DDACancellation cancelDDARegistration(DDACancellation ddaCancellationReq) throws InterfaceException;

}
