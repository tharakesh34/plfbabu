package com.pennant.mq.process.impl;

import com.pennant.coreinterface.model.customer.InterfaceNorkamCheck;
import com.pennant.coreinterface.process.NorkamCheckProcess;
import com.pennant.exception.InterfaceException;
import com.pennant.mq.processutil.NorkamCheckUtilProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;

public class NorkamCheckServiceImpl implements NorkamCheckProcess {
	
	private NorkamCheckUtilProcess norkamCheckUtilProcess;
	
	public NorkamCheckServiceImpl() {
		
	}

	@Override
	public InterfaceNorkamCheck doNorkamProcess(InterfaceNorkamCheck interfaceNorkamCheck)
			throws InterfaceException {
		return getNorkamCheckUtilProcess().doNorkamCheck(interfaceNorkamCheck, InterfaceMasterConfigUtil.BLACKLIST_CHECK);
	}
	
	public NorkamCheckUtilProcess getNorkamCheckUtilProcess() {
		return norkamCheckUtilProcess;
	}
	public void setNorkamCheckUtilProcess(
			NorkamCheckUtilProcess norkamCheckUtilProcess) {
		this.norkamCheckUtilProcess = norkamCheckUtilProcess;
	}
	
}
