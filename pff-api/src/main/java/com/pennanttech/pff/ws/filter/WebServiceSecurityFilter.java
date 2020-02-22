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

import org.apache.commons.lang.StringUtils;

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

		if (queryString.endsWith("wsdl") || queryString.endsWith("wadl") || requestURI.endsWith("services")
				|| requestURI.endsWith("services/")) {
			String userAgent = ((HttpServletRequest) request).getHeader("User-Agent");
			if (StringUtils.contains(userAgent, "Chrome") || StringUtils.contains(userAgent, "Firefox")) {
				PrintWriter out = response.getWriter();
				out.println("<html>");
				out.println("<head>");
				out.println("<title>PLF - API's</title>");
				out.println("</head>");
				out.println("<body style='background-color:#d3d3d3;'>");
				out.print("<center> <br><br><br><br><br><br><br><br>");
				out.println("<img src='PLF_API_Icon.svg' height='280' width='400' alt='API image' />");
				out.println("<p style='font-family:calibri;font-size:300%'> Services are deployed successfully. </p>");
				out.print("</center>");
				out.println("</body>");
				out.println("</html>");
				return;
			} else {
				response.setContentType("application/json");
				String message = " \"Invalid Service.\" ";
				PrintWriter out = response.getWriter();
				out.println("{");
				out.println(message);
				out.println("}");
				return;
			}
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
