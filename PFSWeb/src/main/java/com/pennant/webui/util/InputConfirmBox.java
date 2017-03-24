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
 * FileName    		:  InputConfirmBox.java													*                          
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

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can input something. By onClosing this InputConfirmBox can return a value. <br>
 * In this useCase the returnValue is the same as the inputValue.<br>
 * 
 */
public class InputConfirmBox extends Window {

	private static final long serialVersionUID = 8109634704496621100L;
	private static final Logger logger = Logger.getLogger(InputConfirmBox.class);

	private final String question;
	private final Textbox textbox;

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @param anQuestion
	 *            The question that's to be confirmed.
	 * @return String from the input textbox.
	 */
	public static String show(Component parent, String anQuestion) {
		return new InputConfirmBox(parent, anQuestion).textbox.getText();
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method
	 * 
	 * @param parent
	 * @param anQuestion
	 */
	private InputConfirmBox(Component parent, String anQuestion) {
		super();
		question = anQuestion;
		textbox = new Textbox();

		setParent(parent);

		createBox();
	}

	private void createBox() {

		setWidth("350px");
		setHeight("110px");
		setTitle(Labels.getLabel("message.Information"));
		setId("confBox");
		setVisible(true);
		setClosable(true);
		addEventListener("onOK", new OnCloseListener());

		Vbox vbox = new Vbox();
		vbox.setParent(this);

		Label label = new Label();
		label.setValue(question);
		label.setParent(vbox);

		Separator sp = new Separator();
		sp.setBar(true);
		sp.setParent(vbox);

		Hbox hbox = new Hbox();
		hbox.setParent(vbox);

		Separator sep = new Separator();
		sep.setParent(hbox);

		textbox.setType("password");
		textbox.setWidth("100px");
		textbox.setParent(hbox);

		try {
			doModal();
		} catch (SuspendNotAllowedException e) {
			logger.fatal("", e);
		}
	}

	final class OnCloseListener implements EventListener<Event> {
		
		public OnCloseListener() {
			
		}
		@Override
		public void onEvent(Event event) throws Exception {
			onClose();
		}
	}

}
