package com.pennant.corebanking.process.impl;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.coreinterface.process.NorkamCheckProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class NorkamCheckProcessImpl implements NorkamCheckProcess {

	@Override
	public InterfaceNorkamCheck doNorkamProcess(InterfaceNorkamCheck interfaceNorkamCheck) throws InterfaceException {
		interfaceNorkamCheck.setReturnCode("0000");
		return interfaceNorkamCheck;
	}

}
