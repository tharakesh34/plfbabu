package com.pennant.Interface.service.impl;

import com.pennant.Interface.service.NorkamCheckService;
import com.pennant.backend.dao.impl.NextIdViewSQLServerDaoImpl;
import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.coreinterface.process.NorkamCheckProcess;
import com.pennant.exception.PFFInterfaceException;

public class NorkamCheckServiceImpl extends NextIdViewSQLServerDaoImpl implements NorkamCheckService {
	
	private NorkamCheckProcess norkamCheckProcess;
	
	public NorkamCheckServiceImpl(){
		super();
	}

	@Override
    public InterfaceNorkamCheck doNorkamCheck(InterfaceNorkamCheck interfaceNorkamCheck) throws PFFInterfaceException {
	    return getNorkamCheckProcess().doNorkamProcess(interfaceNorkamCheck);
    }

	public NorkamCheckProcess getNorkamCheckProcess() {
		return norkamCheckProcess;
	}
	public void setNorkamCheckProcess(NorkamCheckProcess norkamCheckProcess) {
		this.norkamCheckProcess = norkamCheckProcess;
	}

}
