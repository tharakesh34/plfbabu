package com.pennanttech.pff.backend.service;

import com.pennant.app.util.MasterDefUtil;
import com.pennanttech.pennapps.service.ConfigurationService;
import com.pennanttech.pff.backend.dao.MasterDefDAO;

public class ConfigurationServiceImpl implements ConfigurationService {

	private MasterDefDAO masterDefDAO;

	@Override
	public void loadConfigurations() {
		MasterDefUtil.setMasterDefList(masterDefDAO.getMasterDefList());
	}

	@Override
	public void reloadConfigurations(ConfigType configType) {
		switch (configType) {
		case MASTER_DEF:
			MasterDefUtil.setMasterDefList(masterDefDAO.getMasterDefList());
			break;
		default:
			break;
		}

	}

	public void init() {
		loadConfigurations();
	}

	public void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		this.masterDefDAO = masterDefDAO;
	}
}
