package com.pennant.corebanking.process.impl;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.coreinterface.process.NorkamCheckProcess;
import com.pennant.exception.PFFInterfaceException;

public class NorkamCheckProcessImpl implements NorkamCheckProcess {

	@Override
	public InterfaceNorkamCheck doNorkamProcess(InterfaceNorkamCheck interfaceNorkamCheck) throws PFFInterfaceException {
		interfaceNorkamCheck.setReturnCode("0000");
		return interfaceNorkamCheck;
	}

}
