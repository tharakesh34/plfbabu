package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennanttech.pennapps.core.InterfaceException;

public interface FinanceMaintenanceProcess {

	HandlingInstruction sendHandlingInstruction(HandlingInstruction handlingInstruction) throws InterfaceException;
	
}
