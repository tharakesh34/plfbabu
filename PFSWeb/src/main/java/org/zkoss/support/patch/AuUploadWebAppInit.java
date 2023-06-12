package org.zkoss.support.patch;

import java.io.IOException;

import org.zkoss.zk.au.http.AuUploader;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

public class AuUploadWebAppInit extends AuUploader implements WebAppInit {
	public void init(WebApp wapp) throws Exception {
		DHtmlUpdateServlet.addAuExtension(wapp, "/upload", this);
	}

	public void service(HttpServletRequest request, HttpServletResponse response, String pathInfo)
			throws ServletException, IOException {
		super.service(new BugFix(request), response, pathInfo);
	}

	private static class BugFix extends HttpServletRequestWrapper {
		public BugFix(HttpServletRequest request) {
			super(request);
		}

		public String getParameter(String name) {
			if ("nextURI".equals(name)) {
				return null;
			}
			return super.getParameter(name);
		}
	}
}