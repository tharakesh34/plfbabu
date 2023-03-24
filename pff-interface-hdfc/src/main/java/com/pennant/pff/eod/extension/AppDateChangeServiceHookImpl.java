package com.pennant.pff.eod.extension;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.external.dao.ExtInterfaceDao;

public class AppDateChangeServiceHookImpl implements AppDateChangeServiceHook {

	private ExtInterfaceDao extInterfaceDao;

	public AppDateChangeServiceHookImpl() {
		super();
	}
	
	@Override
	public void postAppDateChange() {
		extInterfaceDao.resetAllSequences();
	}

	@Autowired
	public void setExtInterfaceDao(ExtInterfaceDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
