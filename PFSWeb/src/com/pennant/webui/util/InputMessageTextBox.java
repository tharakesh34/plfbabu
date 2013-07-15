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
 * FileName    		:  InputMessageTextBox.java												*                           
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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.policy.model.UserImpl;


/**
 * This class creates a modal window as a dialog in which the user <br>
 * can input some text. By onClosing with <RETURN> or Button <send> this
 * InputConfirmBox can return the message as a String value if not empty. <br>
 * In this case the returnValue is the same as the inputValue.<br>
 * 
 */
public class InputMessageTextBox extends Window {

	private static final long serialVersionUID = 8109634704496621100L;
	private static final Logger logger = Logger.getLogger(InputMessageTextBox.class);

	private final Textbox textbox;
	private String msg = "";
	private String userName;

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @param anQuestion
	 *            The question that's to be confirmed.
	 * @return String from the input textbox.
	 */
	public static String show(Component parent) {
		return new InputMessageTextBox(parent).getMsg();
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 * 
	 * @param parent
	 * @param anQuestion
	 */
	private InputMessageTextBox(Component parent) {
		super();

		textbox = new Textbox();

		setParent(parent);

		try {
			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		} catch (Exception e) {
			e.printStackTrace();
		}

		createBox();
	}

	private void createBox() {

		setWidth("350px");
		setHeight("150px");
		setTitle(Labels.getLabel("message.Information.PleaseInsertText"));
		setId("confBox");
		setVisible(true);
		setClosable(true);
		addEventListener("onOK", new OnCloseListener());

		// Hbox hbox = new Hbox();
		// hbox.setWidth("100%");
		// hbox.setParent(this);
		// Checkbox cb = new Checkbox();
		// cb.setLabel(Labels.getLabel("common.All"));
		// cb.setChecked(true);

		Separator sp = new Separator();
		sp.setParent(this);

		textbox.setWidth("98%");
		textbox.setHeight("80px");
		textbox.setMultiline(true);
		textbox.setRows(5);
		textbox.setParent(this);

		Separator sp2 = new Separator();
		sp2.setBar(true);
		sp2.setParent(this);

		Button btnSend = new Button();
		btnSend.setLabel(Labels.getLabel("common.Send"));
		btnSend.setParent(this);
		btnSend.addEventListener("onClick", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {

				// Check if empty, than do not send
				if (StringUtils.isEmpty(StringUtils.trim(textbox.getText()))) {
					onClose();
					return;
				}

				msg = msg + PTDateFormat.getDateTimeLongFormater().format(new Date()) + " / " + Labels.getLabel("common.Message.From") + " " + userName + ":" + "\n";
				msg = msg + textbox.getText();
				msg = msg + "\n" + "_____________________________________________________" + "\n";

				onClose();
			}
		});

		try {
			doModal();
		} catch (SuspendNotAllowedException e) {
			logger.fatal("", e);
		} //Upgraded to ZK-6.5.1.1 Removed interrupted exception
	}

	final class OnCloseListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
