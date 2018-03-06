package com.pennanttech.framework.security.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter used to protected against session fixation attacks while still keeping the same session id on both HTTP and
 * HTTPS protocols. Uses a secondary, HTTPS cookie that must be present on every HTTPS request for a given session after
 * the first request. If it's not present and equal to what we expect, we will redirect the user to "/" and remove his
 * session cookie.
 */
public class SessionFixationProtectionFilter extends GenericFilterBean {
	@Override
	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) sRequest;

		String host;
		if (request.getHeader("X-FORWARDED-FOR") != null) {
			host = request.getHeader("X-FORWARDED-FOR");
		} else {
			host = request.getRemoteHost();
		}

		HttpServletResponse response = (HttpServletResponse) sResponse;
		HttpSession session = request.getSession(false);

		String activeHost = (session == null) ? null : (String) session.getAttribute("SATTR_REMOTE_HOST");

		if (SecurityContextHolder.getContext() == null || session == null || activeHost == null) {
			chain.doFilter(request, response);
			return;
		}

		if (!StringUtils.equals(activeHost, host)) {
			logger.warn(String.format("Invalid host logout the user. actual %s expected %s", host, activeHost));
			abortUser(request);
		}

		chain.doFilter(request, response);
	}

	private void abortUser(HttpServletRequest request) {
		SecurityContextHolder.clearContext();
		request.getSession().invalidate();
	}

	/**
	 * @param attributes
	 *            the attributes which were extracted from the original session by {@code extractAttributes}
	 * @param session
	 *            the newly created session
	 */
	protected void transferAttributes(Map<String, Object> attributes, HttpSession session) {
		if (attributes != null) {
			for (Map.Entry<String, Object> entry : attributes.entrySet()) {
				session.setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Called to extract the existing attributes from the session, prior to invalidating it. If
	 * {@code migrateAttributes} is set to {@code false}, only Spring Security attributes will be retained. All
	 * application attributes will be discarded.
	 * <p>
	 * You can override this method to control exactly what is transferred to the new session.
	 * 
	 * @param session
	 *            the session from which the attributes should be extracted
	 * @return the map of session attributes which should be transferred to the new session
	 */
	protected Map<String, Object> extractAttributes(HttpSession session) {
		return createMigratedAttributeMap(session);
	}

	private HashMap<String, Object> createMigratedAttributeMap(HttpSession session) {
		HashMap<String, Object> attributesToMigrate = null;

		attributesToMigrate = new HashMap<>();

		Enumeration<String> enumer = session.getAttributeNames();

		while (enumer.hasMoreElements()) {
			String key = enumer.nextElement();
			if (key.startsWith("SPRING_SECURITY_")) {
				continue;
			}
			attributesToMigrate.put(key, session.getAttribute(key));
		}

		return attributesToMigrate;
	}
}