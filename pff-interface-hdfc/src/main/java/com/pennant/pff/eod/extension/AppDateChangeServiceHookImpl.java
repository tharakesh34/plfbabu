package com.pennant.pff.eod.extension;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.external.dao.ExtGenericDao;

public class AppDateChangeServiceHookImpl implements AppDateChangeServiceHook {

	private ExtGenericDao extInterfaceDao;

	public AppDateChangeServiceHookImpl() {
		super();
	}
	
	@Override
	public void postAppDateChange() {
		extInterfaceDao.resetAllSequences();
	}

	@Autowired
	public void setExtInterfaceDao(ExtGenericDao extInterfaceDao) {
		this.extInterfaceDao = extInterfaceDao;
	}

}
