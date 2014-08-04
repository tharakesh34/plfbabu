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
 * FileName    		:  LoginDialogCtl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    : 05-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.webui.util.PTDateFormat;
import com.pennant.webui.util.WindowBaseCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/loginDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class LoginDialogCtrl extends WindowBaseCtrl implements Serializable {

	private final static Logger logger = Logger.getLogger(LoginDialogCtrl.class);
	private static final long serialVersionUID = -71422545405325060L;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends WindowBaseCtrl'.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window     loginwin;              // autowired
	protected Label      lbl_ServerTime;        // autowired
	protected Textbox    txtbox_Username;       // autowired
	protected Textbox    txtbox_Password;       // autowired
	protected Button     btnReset;
	protected Image      loginImage;
	protected Label      label_error;
	/**
	 * default constructor. <br>
	 */
	public LoginDialogCtrl() {
		super();
	}

	public void onCreate$loginwin(Event event) throws Exception {
		logger.debug("Entering ");
		doOnCreateCommon(this.loginwin); // do the autowire
		this.txtbox_Username.setMaxlength(50);
		this.txtbox_Password.setMaxlength(15);
		this.txtbox_Username.focus(); // set the focus on UserName
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when clicks on "reset" button
	 * @param event
	 */
	public void onClick$btnReset(Event event) {
		logger.debug("Entering " + event.toString());

		if (label_error!=null){
			this.label_error.setValue("");
		}
		this.txtbox_Username.setValue("");
		this.txtbox_Password.setValue("");
		this.txtbox_Username.focus(); // set the focus on UserName
		Executions.sendRedirect("loginDialog.zul");
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @throws IOException
	 */
	public void onClick$button_LoginDialog_Close() throws IOException {
		logger.debug("Entering ");
		Executions.sendRedirect("/j_spring_logout");
		logger.debug("Leaving ");
	}

	/**
	 * when the "getServerTime" button is clicked. <br>
	 * 
	 * @throws IOException
	 */
	public void onClick$button_LoginDialog_ServerTime() throws IOException {
		logger.debug("Entering ");
		logger.debug("get the server date/time");

		//  get the tomcat servers time, if the TimeServer doesn't answers.
		final long l = getCurrentHttpTokenTime();
		final String dateStr = PTDateFormat.getDateTimeLongFormater().format(l);
		this.lbl_ServerTime.setMultiline(true);
		this.lbl_ServerTime.setValue("time on synchronization-server:\n" + dateStr);
		logger.debug("Leaving ");
	}

	/**
	 * Get a date/time from a web server for the one-time-password
	 * synchronizing.<br>
	 * <br>
	 * We became our time with calling a PHP Function on a webserver.<br>
	 * This time-Url and time is used only for synchronizing the tokenizer <br>
	 * application on the users PC and the server method for calculate the <br>
	 * user token. So the running user-application must have an internet access. <br>
	 * In the case of non Internet connection of the users pc, the tokenizer<br>
	 * takes the time from the users pc clock. So the user can set the pc clock
	 * to the servers time manually.<br>
	 * 
	 * <pre>
	 * File: time.php
	 * --------------
	 * 1. &lt;?php
	 * 2. echo mktime();
	 * 3. ?&gt;
	 * --------------
	 * End-File. = 3 lines
	 * </pre>
	 * 
	 * @return
	 */
	private long getCurrentHttpTokenTime() {
		logger.debug("Entering ");

		final String urlString = "http://unixtime.forsthaus.de/time.php";

		try {
			final URL url = new URL(urlString);
			final URLConnection conn = url.openConnection();
			final InputStream istream = conn.getInputStream();
			try {
				final StringBuilder sb = new StringBuilder();
				int ch = -1;
				while ((ch = istream.read()) != -1) {
					sb.append((char) ch);
				}
				final long l1 = Long.parseLong(sb.toString());

				return l1 * 1000;
			} catch (final NumberFormatException e) {
				throw new RuntimeException(e);
			} finally {
				istream.close();
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
