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
 *																							*
 * FileName    		:  MessageBarCtrl.java                                                  * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-09-2012    														*
 *                                                                  						*
 * Modified Date    :  14-09-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-09-2012       Pennant	                 0.1                                            * 
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

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Style;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.service.messages.MessagesService;
import com.pennant.core.EventManager;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class MessageBarCtrl extends GFCBaseCtrl<LoggedInUser> {
	private static final long serialVersionUID = 5633232048842356789L;
	private static final Logger logger = Logger.getLogger(MessageBarCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window statusBar;
	protected Row statusBarSections;
	private Toolbarbutton btnOpenMsg;
	private Window msgWindow = null;
	private String msg = "";
	private String userName;
	List<SecurityRole> listSecRoles = null;
	private MessagesService messagesService;

	public MessageBarCtrl() {
		super();
	}

	/**
	 * This method Automatically calls by ZK
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug("Entering");

		super.doAfterCompose(comp);
		try {
			LoggedInUser user = getUserWorkspace().getLoggedInUser();			
			userName = user.getUserName();			
			listSecRoles = getUserWorkspace().getSecurityRoles();
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// Subscribe to the Notification Queue
		EventQueues.lookup(EventManager.QUEUE_NAME, EventQueues.APPLICATION,
				true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Object[] data = (Object[]) event.getData();

				if (!EventManager.isRecipient(data, userName, listSecRoles)) {
					return;
				}

				setMsg((String) data[0]);

				if (msgWindow == null) {
					openMessageWindow();
				} else {
					((Textbox) getMsgWindow().getFellow("tb"))
							.setValue(getMsg());
				}
			}
		});

		logger.debug("Leaving");
	}

	private void openMessageWindow() {
		getMsgWindow();
		((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg());
	}

	/**
	 * Build the status bar on window create
	 */
	public void onCreate$statusBar(Event event) {
		logger.debug("Entering");

		// Message section
		Div div = new Div();
		div.setStyle("padding: 1px;");
		div.appendChild(new Label(getMsg()));
		statusBarSections.appendChild(div);

		// Message section - Message tool bar button
		this.btnOpenMsg = new Toolbarbutton();
		this.btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
		this.btnOpenMsg.setTooltiptext(Labels.getLabel("common.Message.Open"));
		this.btnOpenMsg.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// 1. Reset to normal image
				btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
				// 2. open the message window
				Window win = getMsgWindow();
				Textbox t = (Textbox) win.getFellow("tb");
				t.setText(getMsg());
				Clients.scrollIntoView(t);

			}
		});
		this.btnOpenMsg.setParent(div);

		// Version section
		statusBarSections.appendChild(new Label(getAppVersion()));
		statusBarSections.appendChild(new Label(""));
		
		// Show off-line messages at the time of login
		StringBuilder builder = new StringBuilder();
		List<OfflineUsersMessagesBackup> offlineMessages = messagesService
				.getOfflineUsersMessagesBackupByUsrId(userName);

		if (offlineMessages != null && offlineMessages.size() > 0) {
			for (OfflineUsersMessagesBackup offlineMessage : offlineMessages) {
				builder.append(offlineMessage.getMessage());
				builder.append("\n");
			}

			this.msg = builder.toString();

			((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg());
			// Clear Off line messages from OfflineMessagesBackup table
			messagesService.deleteOfflineUsersMessages(userName);
		}


		logger.debug("Leaving");
	}

	/**
	 * This Method returns new messageWindow
	 * 
	 * @return
	 */
	public Window getMsgWindow() {
		logger.debug("Entering");
		if (msgWindow == null) {
			msgWindow = new Window();
			msgWindow.setDraggable("false");
			msgWindow.setId("msgWindow");
			Style contentStyle = new Style();
			contentStyle
					.setContent(".messageWindow .z-window-header {font-size: 13px;font-weight: bold;"
							+ "font-style: normal;color: white;}");
			contentStyle.setParent(msgWindow);
			msgWindow.setTitle("Messages");
			msgWindow
					.setStyle("padding: 2px;background: #5A87B5;overflow: hidden; border:1px solid #5A87B5;");
			msgWindow.setSizable(true);
			msgWindow.setClosable(true);
			msgWindow.setWidth("400px");
			msgWindow.setHeight("250px");
			msgWindow.setParent(statusBar);
			msgWindow.addEventListener("onClose", new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					if(msgWindow!=null){
					msgWindow.detach();
					msgWindow=null;
					}
					
				}
			});
			msgWindow.setPosition("bottom, left");
			Textbox tb = new Textbox();
			tb.setId("tb");
			tb.setMultiline(true);
			tb.setRows(10);
			tb.setReadonly(true);
			tb.setHeight("100%");
			tb.setWidth("100%");
			tb.setStyle("border-color: lightpink;background: lightyellow;");
			tb.setParent(msgWindow);
			msgWindow.doOverlapped();
		}
		return msgWindow;
	}

	// Setter/Getter

	public void setMsg(String msg) {
		this.msg = this.msg + "\n" + msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsgWindow(Window msgWindow) {
		this.msgWindow = msgWindow;
	}

	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	private String getAppVersion() {
		String appVersion = App.NAME;

		try {
			Session sess = Sessions.getCurrent();
			HttpSession hses = (HttpSession) sess.getNativeSession();
			ServletContext sCon = hses.getServletContext();
			Properties prop = new Properties();
			prop.load(sCon.getResourceAsStream("/META-INF/MANIFEST.MF"));

			appVersion = appVersion + " "
					+ prop.getProperty("Implementation-Version");

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		return appVersion;
	}
}
