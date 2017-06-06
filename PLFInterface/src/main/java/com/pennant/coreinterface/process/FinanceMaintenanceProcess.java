package com.pennant.coreinterface.process;

import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennant.exception.InterfaceException;

public interface FinanceMaintenanceProcess {

	HandlingInstruction sendHandlingInstruction(HandlingInstruction handlingInstruction) throws InterfaceException;
	
}
