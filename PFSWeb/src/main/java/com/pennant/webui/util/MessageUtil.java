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
package com.pennant.webui.util;

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.AppException;
import com.pennanttech.pff.core.ErrorCode;
import com.pennanttech.pff.core.Literal;

/**
 * A suite of utilities surrounding the use of the {@link org.zkoss.zul.Messagebox} object.
 */
public final class MessageUtil {
	private static final Logger	logger	= Logger.getLogger(MessageUtil.class);

	/**
	 * A symbol consisting of a white X in a circle with a red background.
	 */
	public static final String	ERROR	= Messagebox.ERROR;

	/**
	 * A OK button.
	 */
	public static final int		OK		= Messagebox.OK;

	public static final String	SUFFIX	= "\n\n";

	private MessageUtil() {
		super();
	}

	/**
	 * @deprecated Instead use showError();
	 * @param message
	 * @throws InterruptedException
	 */
	@Deprecated
	public static void showErrorMessage(String message) {
		logger.info(message);

		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message.concat(SUFFIX), App.NAME, OK, ERROR);
	}

	/**
	 * Shows an error message box and logs the message.
	 * 
	 * @param message
	 *            The detail message.
	 */
	public static void showError(String message) {
		logger.info(message);

		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message.concat(SUFFIX), App.NAME, OK, ERROR);
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
		if (e instanceof AppException) {
			show((AppException) e);
		} else {
			show(e);
		}
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

		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(e.getMessage().concat(SUFFIX), App.NAME, OK, ERROR);
	}

	/**
	 * Shows an error message box for unhandled exception and logs the message and cause.
	 * 
	 * @param e
	 *            The exception.
	 */
	private static void show(Exception e) {
		logger.error("Exception: ", e);

		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(ErrorCode.PPS_900.getMessage().concat(SUFFIX), App.NAME, OK, ERROR);
	}

	// TODO: Re-factor below code.

	/** A symbol consisting of an exclamation point in a triangle with a yellow background. */
	public static final String	EXCLAMATION	= Messagebox.EXCLAMATION;
	/** A symbol of a lower case letter i in a circle. */
	public static final String	INFORMATION	= Messagebox.INFORMATION;
	/** A Cancel button. */
	public static final int		CANCEL		= Messagebox.CANCEL;
	/** A Yes button. */
	public static final int		YES			= Messagebox.YES;
	/** A No button. */
	public static final int		NO			= Messagebox.NO;
	/** A Abort button. */
	public static final int		ABORT		= Messagebox.ABORT;
	/** A Retry button. */
	public static final int		RETRY		= Messagebox.RETRY;
	/** A IGNORE button. */
	public static final int		IGNORE		= Messagebox.IGNORE;

	/**
	 * Shows a message box and returns what button is pressed. The message box will be displayed with the following
	 * parameters:<br/>
	 * <b>title</b> - {@link App#NAME} is used.<br/>
	 * <b>buttons</b> - a combination of {@link #YES} and {@link #NO}.<br/>
	 * <b>icon</b> - {@link #EXCLAMATION} to show an image.<br/>
	 * <b>focus</b> - {@link #NO} button with focus.
	 * 
	 * @param message
	 *            The message that need to be displayed.
	 * @return the button being pressed.
	 */
	public static int confirm(String message) {
		MultiLineMessageBox.doSetTemplate();

		return MultiLineMessageBox.show(message, App.NAME, YES | NO, EXCLAMATION, NO);
	}

	/**
	 * Shows a message box. The message box will be displayed with the following parameters:<br/>
	 * <b>title</b> - {@link App#NAME} is used.<br/>
	 * <b>buttons</b> - {@link #OK}.<br/>
	 * <b>icon</b> - {@link #INFORMATION} to show an image.<br/>
	 * <b>focus</b> - {@link #OK} button with focus.
	 * 
	 * @param message
	 *            The message that need to be displayed.
	 */
	public static void showMessage(String message) {
		MultiLineMessageBox.doSetTemplate();

		MultiLineMessageBox.show(message, App.NAME, OK, INFORMATION);
	}

	/**
	 * Shows a multiline ErrorMessage.<br>
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	public static void showErrorMessage(ErrorDetails errorDetail) throws InterruptedException {
		final String title = Labels.getLabel("message.Error");
		logger.info("Message : " + errorDetail.getErrorCode() + PennantConstants.KEY_SEPERATOR
				+ errorDetail.getErrorMessage() + ", title : " + title);
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(
				errorDetail.getErrorCode() + PennantConstants.KEY_SEPERATOR + errorDetail.getErrorMessage(), title,
				MultiLineMessageBox.OK, "ERROR", true);
	}

	public static void showHelpWindow(Event event, Window parentWindow) {
		Component comp = ((ForwardEvent) event).getOrigin().getTarget();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("parentComponent", comp);
		Executions.createComponents("/WEB-INF/pages/util/helpWindow.zul", parentWindow, map);

		// we stop the propagation of the event, because zk will call ALL events
		// with the same name in the namespace and 'btnHelp' is a standard
		// button in this application and can often appears.
		Events.getRealOrigin((ForwardEvent) event).stopPropagation();
		event.stopPropagation();
	}

	public static void showErrorMessage(Exception e) throws InterruptedException {
		final String title = Labels.getLabel("message.Error");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(Labels.getLabel("message.SystemError"), title, MultiLineMessageBox.OK, "ERROR", true,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					public void onEvent(Event evt) throws InterruptedException {
						Collection<Component> list = Executions.getCurrent().getDesktop().getComponents();

						for (Component component : list) {
							if (component instanceof Tab) {
								Tab tab = (Tab) component;
								if (tab.isSelected()) {
									tab.close();
									break;
								}
							}
						}
					}
				});
	}

	/**
	 * Shows a multiline ErrorMessage.<br>
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	public static void showInfoMessage(String e) throws InterruptedException {
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(e, title, MultiLineMessageBox.OK, "Info", true);
	}
}
