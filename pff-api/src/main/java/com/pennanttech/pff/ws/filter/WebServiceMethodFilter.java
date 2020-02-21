package com.pennanttech.pff.ws.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class WebServiceMethodFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getMethod().equals("OPTIONS") || request.getMethod().equals("TRACE")) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} else {
			try {
				filterChain.doFilter(request, response);
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		}
	}
}
