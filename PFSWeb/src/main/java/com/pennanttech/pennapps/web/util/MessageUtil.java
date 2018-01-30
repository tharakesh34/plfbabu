/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  MessageUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennanttech.pennapps.web.util;

import java.text.MessageFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * A suite of utilities surrounding the use of the {@link org.zkoss.zul.Messagebox Messagebox} object.
 */
public final class MessageUtil {
	private static final Logger	logger		= Logger.getLogger(MessageUtil.class);

	private static final String	TEMPLATE	= "/WEB-INF/pages/util/messagebox.zul";
	/**
	 * A symbol consisting of an exclamation point in a triangle with a yellow background.
	 */
	private static final String	EXCLAMATION	= Messagebox.EXCLAMATION;
	/**
	 * A symbol of a lower case letter i in a circle.
	 */
	private static final String	INFORMATION	= Messagebox.INFORMATION;
	/**
	 * A symbol consisting of a white X in a circle with a red background.
	 */
	private static final String	ERROR		= Messagebox.ERROR;

	/**
	 * A OK button.
	 */
	public static final int		OK			= Messagebox.OK;
	/**
	 * A Yes button.
	 */
	public static final int		YES			= Messagebox.YES;
	/**
	 * A No button.
	 */
	public static final int		NO			= Messagebox.NO;
	/**
	 * A Cancel button.
	 */
	public static final int		CANCEL		= Messagebox.CANCEL;
	/**
	 * A Abort button.
	 */
	public static final int		ABORT		= Messagebox.ABORT;
	/**
	 * A Abort button.
	 */
	public static final int		OVERIDE		= Messagebox.IGNORE;

	private static final String	SUFFIX		= "\n\n";

	private MessageUtil() {
		super();
	}

	/**
	 * Shows a confirmation message box and returns the button that has been chosen.
	 * 
	 * @param message
	 *            The detail message.
	 * @return The button being pressed.
	 */
	public static int confirm(String message) {
		Messagebox.setTemplate(TEMPLATE);
		return Messagebox.show(message.concat(SUFFIX), App.NAME, YES | NO, EXCLAMATION, NO);
	}

	/**
	 * Shows a confirmation message box and returns the button that has been chosen.
	 * 
	 * @param message
	 *            The detail message.
	 * @param buttons
	 *            A combination of buttons.
	 * @return The button being pressed.
	 */
	public static int confirm(String message, int buttons) {
		Messagebox.setTemplate(TEMPLATE);
		return Messagebox.show(message.concat(SUFFIX), App.NAME, buttons, EXCLAMATION);
	}

	/**
	 * Shows a confirmation message box with the specified buttons and returns the button that has been chosen.
	 * 
	 * @param error
	 *            The {@link ErrorDetail error} object.
	 * @param buttons
	 *            A combination of buttons.
	 * @return The button being pressed.
	 */
	public static int confirm(ErrorDetail error, int buttons) {
		String message = error.getCode().concat(": ").concat(error.getError());

		Messagebox.setTemplate(TEMPLATE);
		return Messagebox.show(message.concat(SUFFIX), App.NAME, buttons, EXCLAMATION);
	}

	/**
	 * Shows an information message box and logs the message.
	 * 
	 * @param message
	 *            The detail message.
	 */
	public static void showMessage(String message) {
		logger.info(message);

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(message.concat(SUFFIX), App.NAME, OK, INFORMATION);
	}

	public static void showInfo(String labelKey, Object... args) {
		String message = Labels.getLabel(labelKey);
		
		if (message == null) {
			message = AppException.getDefaultMessage();
		}

		if (args.length > 0) {
			message = MessageFormat.format(message, args);
		}

		logger.info(message);

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(message.concat(SUFFIX), App.NAME, OK, INFORMATION);
	}

	/**
	 * Shows an information message box, logs the message and returns the button that has been chosen.
	 * 
	 * @param error
	 *            The {@link ErrorDetail error} object.
	 * @return The button being pressed.
	 */
	public static int showMessage(ErrorDetail error) {
		String message = error.getCode().concat(": ").concat(error.getError());
		logger.info(message);

		Messagebox.setTemplate(TEMPLATE);
		return Messagebox.show(message.concat(SUFFIX), App.NAME, OK, INFORMATION);
	}

	/**
	 * Shows an error message box and logs the message.
	 * 
	 * @param message
	 *            The detail message.
	 */
	public static void showError(String message) {
		logger.info(message);

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(message.concat(SUFFIX), App.NAME, OK, ERROR);
	}

	/**
	 * Shows an error message box and logs the message.
	 * 
	 * @param error
	 *            The {@link ErrorDetail error} object.
	 */
	public static void showError(ErrorDetail error) {
		String message = error.getCode().concat(": ").concat(error.getError());
		logger.info(message);

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(message.concat(SUFFIX), App.NAME, OK, ERROR);
	}

	/**
	 * Shows an error message box and logs the message. Returns the button that has been chosen.
	 * 
	 * @param error
	 *            The {@link ErrorDetail error} object.
	 * @param buttons
	 *            A combination of buttons.
	 * @return The button being pressed.
	 */
	public static int showError(ErrorDetail error, int buttons) {
		String message = error.getCode().concat(": ").concat(error.getError());
		logger.info(message);

		Messagebox.setTemplate(TEMPLATE);
		return Messagebox.show(message.concat(SUFFIX), App.NAME, buttons, ERROR);
	}

	/**
	 * Shows an error message box and logs the message and cause. Displays<br/>
	 * - detail message of the exception for application exception.<br/>
	 * - generic message for unhandled exception.
	 * 
	 * @param e
	 *            The exception.
	 */
	public static void showError(Exception e) {
		if (e instanceof InterfaceException) {
			show((InterfaceException) e);
		} else if (e instanceof AppException) {
			show((AppException) e);
		} else {
			show(e);
		}
	}

	/**
	 * Shows an error message box for interface exception and logs the message and cause.
	 * 
	 * @param e
	 *            The exception.
	 */
	private static void show(InterfaceException e) {
		logger.warn(Literal.EXCEPTION, e);

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(e.getMessage().concat(SUFFIX), App.NAME, OK, ERROR);
	}

	/**
	 * Shows an error message box for application exception and logs the message and cause.
	 * 
	 * @param e
	 *            The exception.
	 */
	private static void show(AppException e) {
		if (e.getCause() != null) {
			logger.warn(Literal.EXCEPTION, e);
		} else {
			logger.info(e.getMessage());
		}

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(e.getMessage().concat(SUFFIX), App.NAME, OK, ERROR);
	}
	

	/**
	 * Shows an error message box for unhandled exception and logs the message and cause.
	 * 
	 * @param e
	 *            The exception.
	 */
	private static void show(Exception e) {
		// To address multiple double-clicks of the list items.
		if (e instanceof UiException && e.getMessage() != null
				&& e.getMessage().startsWith("Not unique in the ID space")) {
			logger.warn(e.getMessage());
			return;
		}

		logger.error("Exception: ", e);

		Messagebox.setTemplate(TEMPLATE);
		Messagebox.show(AppException.getDefaultMessage().concat(SUFFIX), App.NAME, OK, ERROR);
	}

	/**
	 * Shows help window.
	 * 
	 * @param event
	 *            The event.
	 * @param parent
	 *            The parent component.
	 */
	public static void showHelpWindow(Event event, Window parent) {
		HashMap<String, Object> arg = new HashMap<>();
		arg.put("parentComponent", ((ForwardEvent) event).getOrigin().getTarget());

		Executions.createComponents("/WEB-INF/pages/util/helpWindow.zul", parent, arg);

		// Stop the propagation for this event as 'btnHelp' is a standard button and appears in different modules.
		Events.getRealOrigin((ForwardEvent) event).stopPropagation();
		event.stopPropagation();
	}
}
