package com.pennant.Interface.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.Interface.service.NorkamCheckService;
import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.coreinterface.process.NorkamCheckProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class NorkamCheckServiceImpl implements NorkamCheckService {

	private NorkamCheckProcess norkamCheckProcess;

	public NorkamCheckServiceImpl() {
		super();
	}

	@Override
	public InterfaceNorkamCheck doNorkamCheck(InterfaceNorkamCheck interfaceNorkamCheck) throws InterfaceException {
		if (norkamCheckProcess != null) {
			return norkamCheckProcess.doNorkamProcess(interfaceNorkamCheck);
		}
		return null;
	}

	/*
	 * public NorkamCheckProcess getNorkamCheckProcess() { return norkamCheckProcess; }
	 */
	@Autowired(required = false)
	public void setNorkamCheckProcess(NorkamCheckProcess norkamCheckProcess) {
		this.norkamCheckProcess = norkamCheckProcess;
	}

}
