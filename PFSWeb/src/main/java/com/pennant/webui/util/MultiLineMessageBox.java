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
 * FileName    		:  MultiLineMessageBox.java												*                           
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

import java.io.Serializable;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

/**
 * Extended messagebox that can show multilined messages. <br>
 * Lines can be breaked with the \n . <br>
 * <br>
 */
public class MultiLineMessageBox extends Messagebox implements Serializable {
	private static final long		serialVersionUID	= 1L;

	// path of the messagebox zul-template
	private transient static String	_templ				= "/WEB-INF/pages/util/multiLineMessageBox.zul";
	private transient static String	_temp2				= "/WEB-INF/pages/util/multiLineErrorMessageBox.zul";

	public MultiLineMessageBox() {
	}

	public static void doSetTemplate() {
		setTemplate(_templ);
	}

	public static void doErrorTemplate() {
		setTemplate(_temp2);
	}

	/**
	 * Shows a message box and returns what button is pressed. A shortcut to show(message, null, OK, INFORMATION). <br>
	 * <br>
	 * Simple MessageBox with customizable message and title. <br>
	 * 
	 * @param message
	 *            The message to display.
	 * @param title
	 *            The title to display.
	 * @param icon
	 *            The icon to display. <br>
	 *            QUESTION = "z-msgbox z-msgbox-question"; <br>
	 *            EXCLAMATION = "z-msgbox z-msgbox-exclamation"; <br>
	 *            INFORMATION = "z-msgbox z-msgbox-imformation"; <br>
	 *            ERROR = "z-msgbox z-msgbox-error"; <br>
	 * @param buttons
	 *            MultiLineMessageBox.CANCEL<br>
	 *            MultiLineMessageBox.YES<br>
	 *            MultiLineMessageBox.NO<br>
	 *            MultiLineMessageBox.ABORT<br>
	 *            MultiLineMessageBox.RETRY<br>
	 *            MultiLineMessageBox.IGNORE<br>
	 * @param padding
	 *            true = Added an empty line before and after the message.<br>
	 * 
	 * 
	 * @return
	 */
	public static final int show(String message, String title, int buttons, String icon, boolean padding) {

		String msg = message;

		if (padding) {
			msg = "\n" + message + "\n\n";
		}

		if ("QUESTION".equals(icon)) {
			icon = "z-msgbox z-msgbox-question";
		} else if ("EXCLAMATION".equals(icon)) {
			icon = "z-msgbox z-msgbox-exclamation";
		} else if ("INFORMATION".equals(icon)) {
			icon = "z-msgbox z-msgbox-imformation";
		} else if ("ERROR".equals(icon)) {
			icon = "z-msgbox z-msgbox-error";
		}

		return show(msg, title, buttons, icon, 0, null);
	}

	/**
	 * Shows a message box and returns what button is pressed. A shortcut to show(message, null, OK, INFORMATION). <br>
	 * <br>
	 * Simple MessageBox with customizable message and title. <br>
	 * 
	 * @param message
	 *            The message to display.
	 * @param title
	 *            The title to display.
	 * @param icon
	 *            The icon to display. <br>
	 *            QUESTION = "z-msgbox z-msgbox-question"; <br>
	 *            EXCLAMATION = "z-msgbox z-msgbox-exclamation"; <br>
	 *            INFORMATION = "z-msgbox z-msgbox-imformation"; <br>
	 *            ERROR = "z-msgbox z-msgbox-error"; <br>
	 * @param buttons
	 *            MultiLineMessageBox.CANCEL<br>
	 *            MultiLineMessageBox.YES<br>
	 *            MultiLineMessageBox.NO<br>
	 *            MultiLineMessageBox.ABORT<br>
	 *            MultiLineMessageBox.RETRY<br>
	 *            MultiLineMessageBox.IGNORE<br>
	 * @param padding
	 *            true = Added an empty line before and after the message.<br>
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final int show(String message, String title, int buttons, String icon, boolean padding,
			EventListener listener) {

		String msg = message;

		if (padding) {
			msg = "\n" + message + "\n\n";
		}

		if ("QUESTION".equals(icon)) {
			icon = "z-msgbox z-msgbox-question";
		} else if ("EXCLAMATION".equals(icon)) {
			icon = "z-msgbox z-msgbox-exclamation";
		} else if ("INFORMATION".equals(icon)) {
			icon = "z-msgbox z-msgbox-imformation";
		} else if ("ERROR".equals(icon)) {
			icon = "z-msgbox z-msgbox-error";
		}

		return show(msg, title, buttons, icon, 0, listener);
	}

}