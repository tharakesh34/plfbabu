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

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Style;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.service.messages.MessagesService;
import com.pennant.core.EventManager;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.lic.License;

public class MessageBarCtrl extends GFCBaseCtrl<LoggedInUser> {
	private static final long serialVersionUID = 5633232048842356789L;
	private static final Logger logger = Logger.getLogger(MessageBarCtrl.class);

	protected Window statusBar;
	//protected Row statusBarSections;
	protected A messageBox;
	protected Label copyRight;
	protected A copyRightInfo;
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
		super.doAfterCompose(comp);
		try {
			LoggedInUser user = getUserWorkspace().getLoggedInUser();			
			userName = user.getUserName();			
			listSecRoles = getUserWorkspace().getSecurityRoles();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Subscribe to the Notification Queue
		EventQueues.lookup(EventManager.QUEUE_NAME, EventQueues.APPLICATION, true)
				.subscribe(new EventListener<Event>() {
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
							((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg());
						}
					}
				});

		logger.debug("Leaving");
	}

	private void openMessageWindow() {
		getMsgWindow();
		((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg());
	}
	
	
	public void onClick$messageBox(Event event) {
		// 1. Reset to normal image
		messageBox.setImage("/images/icons/message2_16x16.gif");
		// 2. open the message window
		Window win = getMsgWindow();
		Textbox t = (Textbox) win.getFellow("tb");
		t.setText(getMsg());
		Clients.scrollIntoView(t);
	}

	/**
	 * Build the status bar on window create
	 */
	public void onCreate$statusBar(Event event) {
		this.messageBox.setImage("/images/icons/message2_16x16.gif");
		this.messageBox.setTooltiptext(Labels.getLabel("common.Message.Open"));
		this.messageBox.setStyle("text-decoration:none;color:#385D8A;");
		this.messageBox.appendChild(new Label(getMsg()));
			
		if (License.getCopyRight() != null) {
			copyRight.setValue(License.getCopyRight());
		} else {
			copyRight.setValue(App.getVersion());
		}

		
		StringBuilder builder = new StringBuilder();
		List<OfflineUsersMessagesBackup> offlineMessages = messagesService.getOfflineUsersMessagesBackupByUsrId(userName);
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
	
	/**
	 * when clicks on "copyRightInfo" hyper link
	 * 
	 * @param event
	 */
	public void onClick$copyRightInfo(Event event) {
		Executions.createComponents("/WEB-INF/pages/License/CopyRight.zul", null, null);
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
}
