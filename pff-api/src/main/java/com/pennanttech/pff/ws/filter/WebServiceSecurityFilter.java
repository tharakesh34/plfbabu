package com.pennanttech.pff.ws.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class WebServiceSecurityFilter implements Filter {

	public WebServiceSecurityFilter() {
		super();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String queryString = ((HttpServletRequest) request).getQueryString();
		queryString = StringUtils.trimToEmpty(queryString);
		queryString = queryString.toLowerCase();

		String requestURI = ((HttpServletRequest) request).getRequestURI();
		requestURI = StringUtils.trimToEmpty(requestURI);
		requestURI = requestURI.toLowerCase();

		if (queryString.endsWith("wsdl") || queryString.endsWith("wadl") || requestURI.endsWith("services")) {
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
			out.println("<title>PFF - API's</title>");
			out.println("</head>");
			out.println("<body style='background-color:#d3d3d3;'>");
			out.print("<center> <br><br><br><br><br><br><br><br><br><br><br><br>");
			out.println("<h1 style='color:#FA7325'>" + "PLF Services are deployed successfully." + "</h1>");
			out.print("</center>");
			out.println("</body>");
			out.println("</html>");
			return;
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void destroy() {
		//

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		//
	}

}
