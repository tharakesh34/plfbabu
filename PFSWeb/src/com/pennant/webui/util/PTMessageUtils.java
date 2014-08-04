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
 * FileName    		:  PTMessageUtils.java													*                           
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
import java.util.HashMap;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;

/**
 * 
 * 1. doShowNotImplementedMessage / Shows a messagebox.<br>
 * 2. doShowNotAllowedInDemoModeMessage / Shows a messagebox.<br>
 * 3. doShowNotAllowedForDemoRecords / Shows a messagebox.<br>
 * 4. doShowOutOfOrderMessage / Shows a messagebox.<br>
 * 6. showErrorMessage / shows a multiline errormessage.<br>
 * 7. showHelpWindow / shows a help window.<br>
 * 
 */
public class PTMessageUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	public PTMessageUtils() {
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Methods +++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Shows a messagebox with text: 'Not implemented yet'.<br>
	 * 
	 * @throws InterruptedException
	 */
	public static void doShowNotImplementedMessage() throws InterruptedException {

		final String message = Labels.getLabel("message.Not_Implemented_Yet");
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Shows a messagebox with text: 'Not allowed in demo mode'.<br>
	 * 
	 * @throws InterruptedException
	 */
	public static void doShowNotAllowedInDemoModeMessage() throws InterruptedException {

		final String message = Labels.getLabel("message.Not_Allowed_In_Demo_Mode");
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Shows a messagebox with text: 'Not allowed in demo mode'.<br>
	 * 
	 * @throws InterruptedException
	 */
	public static void doShowNotAllowedForDemoRecords() throws InterruptedException {

		final String message = Labels.getLabel("message.Not_Allowed_On_System_Objects");
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Shows a messagebox with text: 'temporarely out of order'.<br>
	 * 
	 * @throws InterruptedException
	 */
	public static void doShowOutOfOrderMessage() throws InterruptedException {

		final String message = Labels.getLabel("message.Information.OutOfOrder");
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
	}

	/**
	 * Shows a multiline ErrorMessage.<br>
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	public static void showErrorMessage(String e) throws InterruptedException {
		final String title = Labels.getLabel("message.Error");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(e, title, MultiLineMessageBox.OK, "ERROR", true);
	}
	
	/**
	 * Shows a multiline ErrorMessage.<br>
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	public static void showErrorMessage(ErrorDetails errorDetail) throws InterruptedException {
		final String title = Labels.getLabel("message.Error");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(errorDetail.getErrorCode() +PennantConstants.KEY_SEPERATOR+errorDetail.getErrorMessage(), 
				title, MultiLineMessageBox.OK, "ERROR", true);
	}
	
	public static void showHelpWindow(Event event, Window parentWindow) {
		
		Component comp = (Button) ((ForwardEvent) event).getOrigin().getTarget();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("parentComponent", comp);
		Executions.createComponents("/WEB-INF/pages/util/helpWindow.zul", parentWindow, map);

		// we stop the propagation of the event, because zk will call ALL events
		// with the same name in the namespace and 'btnHelp' is a standard
		// button in this application and can often appears.
		Events.getRealOrigin((ForwardEvent) event).stopPropagation();
		event.stopPropagation();
	}
}