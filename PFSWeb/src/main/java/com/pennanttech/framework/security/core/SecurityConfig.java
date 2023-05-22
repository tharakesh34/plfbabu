package com.pennanttech.framework.security.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.web.security.filter.AuthenticationFormFilter;

@Configuration
@EnableWebSecurity
@Import({ DefaultSecurity.class, SAMLSecurity.class })
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationFormFilter authenticationFilter;
	@Autowired
	private AuthenticationManager authenticationProvider;
	@Autowired
	private LogoutFilter logoutFilter;
	@Autowired
	private ConcurrentSessionFilter concurrencyFilter;
	@Autowired
	private RequestMatcher csrfRequestMatcher;
	@Autowired
	@Qualifier("sas")
	private CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy;
	@Autowired
	private LoginUrlAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired(required = false)
	private FilterChainProxy samlFilter;
	@Autowired(required = false)
	private SAMLEntryPoint samlEntryPoint;
	@Autowired(required = false)
	private MetadataGeneratorFilter metadataGeneratorFilter;

	@Value("${authentication.sso:false}")
	private boolean sso;
	@Value("${authentication.sso.adfs:false}")
	private boolean adfs;
	@Value("${authentication.sso.adfs.protocol:#{null}}")
	private String adfsProtocol;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (sso && adfs && "SAML".equals(adfsProtocol)) {
			configureSAML(http);
		} else {
			configureDefault(http);
		}
	}

	private void configureDefault(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		http.addFilter(authenticationFilter);
		http.addFilterBefore(logoutFilter, LogoutFilter.class);
		http.addFilter(concurrencyFilter);
		http.antMatcher("/**").authorizeRequests().antMatchers("/", "/pages/**", "/WEB-INF/pages/**").authenticated()
				.and().httpBasic();

		http.csrf().requireCsrfProtectionMatcher(csrfRequestMatcher);

		http.headers().cacheControl();
		http.headers().contentTypeOptions();
		http.headers().frameOptions().sameOrigin();
		http.headers().httpStrictTransportSecurity();

		http.headers().addHeaderWriter(new StaticHeadersWriter("Server", App.getProperty("server.server-header")));

		http.headers().xssProtection();

		http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy);
	}

	private void configureSAML(HttpSecurity http) throws Exception {
		http.httpBasic().authenticationEntryPoint(samlEntryPoint);
		http.addFilterBefore(metadataGeneratorFilter, ChannelProcessingFilter.class);
		http.addFilterAfter(samlFilter, BasicAuthenticationFilter.class);

		http.authorizeRequests().antMatchers("/").permitAll().antMatchers("/error").permitAll().antMatchers("/saml/**")
				.permitAll().anyRequest().authenticated();
		http.logout().logoutSuccessUrl("/");

		http.csrf().requireCsrfProtectionMatcher(csrfRequestMatcher);

		http.headers().cacheControl();
		http.headers().contentTypeOptions();
		http.headers().frameOptions().sameOrigin();
		http.headers().httpStrictTransportSecurity();

		http.headers().addHeaderWriter(new StaticHeadersWriter("Server", App.getProperty("server.server-header")));

		http.headers().xssProtection();

		http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy);
	}
}
