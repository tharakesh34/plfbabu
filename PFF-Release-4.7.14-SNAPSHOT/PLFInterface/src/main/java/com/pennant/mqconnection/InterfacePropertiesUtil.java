package com.pennant.mqconnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.pennanttech.pennapps.core.InterfaceException;

public class InterfacePropertiesUtil extends PropertyPlaceholderConfigurer {

	private static Map<String, String> propertiesMap;
	
	// Default as in PropertyPlaceholderConfigurer
	private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	@Override
	public void setSystemPropertiesMode(int systemPropertiesMode) {
		super.setSystemPropertiesMode(systemPropertiesMode);
		springSystemPropertiesMode = systemPropertiesMode;
	}

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties  props) throws BeansException {
        super.processProperties(beanFactory, props);

		propertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String valueStr = resolvePlaceholder(keyStr, props,	springSystemPropertiesMode);
			propertiesMap.put(keyStr, valueStr);
		}
	}

    public static String getProperty(String name) throws InterfaceException {
		if (propertiesMap.get(name) == null) {
			throw new InterfaceException("PTI2001", "Configuration Not Found for " + name);
    	}
    	
        return propertiesMap.get(name);
    }

    public static int getIntProperty(String name) throws InterfaceException {
		if (propertiesMap.get(name) == null) {
			throw new InterfaceException("PTI2001", "Configuration Not Found for " + name);
    	}
    	
        return Integer.parseInt(propertiesMap.get(name));
    }

}