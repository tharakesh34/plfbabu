package com.pennanttech.framework.security.core;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.pennanttech.pennapps.core.App;

@Configuration
@Conditional(SAMLSecurity.Condition.class)
@ImportResource({ "classpath:securityContext-saml.xml" })
public class SAMLSecurity {

	public SAMLSecurity() {
		super();
	}

	static class Condition implements ConfigurationCondition {
		@Override
		public ConfigurationPhase getConfigurationPhase() {
			return ConfigurationPhase.PARSE_CONFIGURATION;
		}

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return App.getBooleanProperty("authentication.sso") && App.getBooleanProperty("authentication.sso.adfs");
		}
	}

}
