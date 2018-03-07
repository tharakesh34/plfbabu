/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.web.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.csrf.CsrfToken;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.GenericInitiator;

public class CsrfTokenAppender extends GenericInitiator {
	private static final String	TOKEN_SET_FOR_DESKTOP	= CsrfTokenAppender.class.getName() + ".tokenSetForDesktop";

	public CsrfTokenAppender() {
		super();
	}

	@Override
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		Execution execution = Executions.getCurrent();

		// Avoid setting the token more than once per page (e.g. includes also count)
		if (Boolean.TRUE.equals(execution.getAttribute(TOKEN_SET_FOR_DESKTOP))) {
			return;
		}

		execution.setAttribute(TOKEN_SET_FOR_DESKTOP, true);

		HttpServletRequest nativeRequest = (HttpServletRequest) execution.getNativeRequest();

		CsrfToken csrf = (CsrfToken) nativeRequest.getAttribute("_csrf");
		addMeta(page, "_csrf_parameter", csrf.getParameterName());
		addMeta(page, "_csrf_header", csrf.getHeaderName());
		addMeta(page, "_csrf", csrf.getToken());
	}

	private void addMeta(Page page, String name, String content) {
		String meta = "<meta name=\"" + name + "\" content=\"" + content + "\"/>";
		((PageCtrl) page).addBeforeHeadTags(meta);
	}
}
