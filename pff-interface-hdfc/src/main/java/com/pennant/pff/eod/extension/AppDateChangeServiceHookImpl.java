package com.pennant.pff.eod.extension;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.external.config.dao.ExtGenericDao;

public class AppDateChangeServiceHookImpl implements AppDateChangeServiceHook {

	private ExtGenericDao extGenericDao;

	public AppDateChangeServiceHookImpl() {
		super();
	}

	@Override
	public void postAppDateChange() {
		extGenericDao.resetAllSequences();
	}

	@Autowired
	public void setExtGenericDao(ExtGenericDao extGenericDao) {
		this.extGenericDao = extGenericDao;
	}

}
