package com.pennant.web.security.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.csrf.CsrfToken;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.GenericInitiator;

public class CsrfMetaAppender extends GenericInitiator {
	private static final String TOKEN_SET_FOR_DESKTOP = CsrfMetaAppender.class.getName() + ".tokenSetForDesktop";

	public CsrfMetaAppender() {
		super();
	}
	
	@Override
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		Execution execution = Executions.getCurrent();
		if(Boolean.TRUE.equals(execution.getAttribute(TOKEN_SET_FOR_DESKTOP))) {
			return; // avoid setting the token more than once per page (e.g. includes also count)
		}
		execution.setAttribute(TOKEN_SET_FOR_DESKTOP, true);

		HttpServletRequest nativeRequest = (HttpServletRequest)Executions.getCurrent().getNativeRequest();

		CsrfToken csrf = (CsrfToken) nativeRequest.getAttribute("_csrf");
		addMeta(page, "_csrf_parameter", csrf.getParameterName());
		addMeta(page, "_csrf_header", csrf.getHeaderName());
		addMeta(page, "_csrf", csrf.getToken());
	}

	private void addMeta(Page page, String name, String content) {
		String meta = "<meta name=\"" + name + "\" content=\"" + content + "\"/>";
		((PageCtrl)page).addBeforeHeadTags(meta);
	}



}
