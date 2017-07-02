package com.pennanttech.bajaj.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.bajaj.process.ALMRequestProcess;
import com.pennanttech.pff.core.process.ProjectedAccrualProcess;
import com.pennanttech.pff.core.services.ALMRequestService;

public class ALMRequestServiceImpl extends BajajService implements ALMRequestService {
	
	
	@Autowired
	private ProjectedAccrualProcess projectedAccrualProcess;
	
	@Override
	public void sendReqest(Object... params) throws Exception {
		ALMRequestProcess service = new ALMRequestProcess(dataSource, (Long) params[0], (Date)params[1], (Date)params[2], projectedAccrualProcess);
		service.process("ALM_REQUEST");
	}
}
