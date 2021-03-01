package com.pennanttech.external.control.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.backend.model.external.control.PushPullControl;
import com.pennanttech.external.control.service.PushPullControlService;
import com.pennanttech.external.dao.PushPullControlDAO;
import com.pennanttech.pennapps.core.resource.Literal;

public class PushPullControlServiceImpl implements PushPullControlService {
	private static final Logger logger = LogManager.getLogger(PushPullControlServiceImpl.class);

	private PushPullControlDAO pushPullControlDAO;

	@Override
	public long save(PushPullControl pushPullControl) {
		logger.debug(Literal.ENTERING);

		return pushPullControlDAO.save(pushPullControl);
	}

	@Override
	public void update(PushPullControl pushPullControl) {
		logger.debug(Literal.ENTERING);

		pushPullControlDAO.update(pushPullControl);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public PushPullControl getValueByName(String name, String type) {
		logger.debug(Literal.ENTERING);

		return pushPullControlDAO.getValueByName(name, type);
	}

	public PushPullControlDAO getPushPullControlDAO() {
		return pushPullControlDAO;
	}

	public void setPushPullControlDAO(PushPullControlDAO pushPullControlDAO) {
		this.pushPullControlDAO = pushPullControlDAO;
	}

}
