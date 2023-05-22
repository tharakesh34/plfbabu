package com.pennanttech.framework.security.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@ImportResource({ "classpath:securityContext.xml" })
public class DefaultSecurity {

	public DefaultSecurity() {
		super();
	}

}
