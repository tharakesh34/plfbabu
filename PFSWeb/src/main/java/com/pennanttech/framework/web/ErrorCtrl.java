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
package com.pennanttech.framework.web;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /error.zul file.
 */
public class ErrorCtrl extends GenericForwardComposer<Component> {
	private static final long serialVersionUID = 4590439096983686575L;
	private static final Logger logger = Logger.getLogger(ErrorCtrl.class);
	protected Window window_ErrorDialog;
	protected Panel panel;
	protected Label message;

	/**
	 * default constructor.<br>
	 */
	public ErrorCtrl() {
		super();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_ErrorDialog(Event event) throws Exception {
		panel.setTitle(App.NAME);
		message.setValue(AppException.getDefaultMessage().concat("\n\n"));

		logger.error(Literal.EXCEPTION, (Throwable) requestScope.get("javax.servlet.error.exception"));
	}
}
