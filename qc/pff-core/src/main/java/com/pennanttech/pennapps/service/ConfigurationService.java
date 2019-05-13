package com.pennanttech.pennapps.service;

public interface ConfigurationService {

	public enum ConfigType {
		MASTER_DEF, SYS_PARAM
	}

	public void loadConfigurations();

	public void reloadConfigurations(ConfigType configType);

}
