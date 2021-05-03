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
 * FileName    		:  CollectionManagerDialogCtrl.java                                                   * 	  
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.messages.UserContactsList;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.service.messages.MessagesService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Messages/SendMessageDialog.zul file.
 */
public class SendMessageDialogCtrl extends GFCBaseCtrl<ReportConfiguration> {
	private static final long serialVersionUID = -7028973478971693678L;
	private static final Logger logger = LogManager.getLogger(SendMessageDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by 'GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_UserDialog;
	protected Panel contactsPanel;
	protected Panel rolesePanel;
	protected Listbox contacts;
	protected Listbox roles;
	protected Timer refreshTimer;
	protected Radiogroup notificationType;
	protected Textbox messageBox;

	private MessagesService messagesService;
	private String username;

	/**
	 * default constructor.
	 */
	public SendMessageDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Initializes the screen
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_UserDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_UserDialog);

		logger.debug("Entering");

		try {
			username = getUserWorkspace().getLoggedInUser().getUserName();

			// Set the component heights
			getBorderLayoutHeight();
			this.roles.setHeight(borderLayoutHeight - 275 + "px");
			this.contacts.setHeight(borderLayoutHeight - 275 + "px");

			// Fill the lists
			doFillRoles();
			doFillContacts();

			// Start the timer
			this.refreshTimer.setDelay(1 * 60 * 1000);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			window_UserDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Triggers when roles panel is opened
	 * 
	 * @param event
	 */
	public void onOpen$rolesePanel(Event event) {
		logger.debug("Entering");

		this.contacts.clearSelection();
		this.contactsPanel.setOpen(false);
		this.notificationType.setSelectedIndex(1);

		logger.debug("Leaving");
	}

	/**
	 * Triggers when contacts panel is opened
	 * 
	 * @param event
	 */
	public void onOpen$contactsPanel(Event event) {
		logger.debug("Entering");

		this.roles.clearSelection();
		this.rolesePanel.setOpen(false);
		this.notificationType.setSelectedIndex(0);

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values.
	 */
	public void doClear() {
		logger.debug("Entering");

		this.messageBox.setValue("");
		this.contacts.clearSelection();
		this.roles.clearSelection();

		logger.debug("Leaving");
	}

	/**
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$addContacts(Event event) {
		logger.debug("Entering");

		Map<String, Object> dataMap = new HashMap<String, Object>();
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("UsrID", getUserWorkspace().getLoggedInUser().getUserId(), Filter.OP_NOT_EQUAL);

		dataMap = (Map<String, Object>) ExtendedMultipleSearchListBox.show(this.window_UserDialog, "SecurityUsers",
				dataMap, filter);

		if (dataMap != null && dataMap.size() > 0) {
			for (String contact : dataMap.keySet()) {
				if (!username.equals(contact) && this.contacts.getFellowIfAny(contact) == null) {
					Listitem listitem = new Listitem();
					listitem.setId(contact);

					Listcell listcell = new Listcell();
					listcell.setLabel(contact);

					listitem.appendChild(listcell);
					this.contacts.appendChild(listitem);
				}
			}

			doSaveContacts();
		}

		doShowOnlineUsers();

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on delete button
	 * 
	 * @param event
	 */
	public void onClick$deleteContacts(Event event) {
		logger.debug("Entering");

		while (this.contacts.getSelectedCount() > 0) {
			int index = this.contacts.getSelectedIndex();

			this.contacts.removeItemAt(index);
		}

		doSaveContacts();
		doShowOnlineUsers();

		logger.debug("Leaving");
	}

	/**
	 * This method saves contact list of user
	 * 
	 * @param contactsList
	 */
	private void doSaveContacts() {
		logger.debug("Entering");

		StringBuilder contactsList = new StringBuilder();

		for (int i = 0; i < this.contacts.getItems().size(); i++) {
			Listitem item = (Listitem) this.contacts.getItems().get(i);
			contactsList.append(item.getId());

			if (i != this.contacts.getItems().size() - 1) {
				contactsList.append(",");
			}
		}

		UserContactsList userContact = new UserContactsList();
		userContact.setUsrID(username);
		userContact.setType("USERS");
		userContact.setGroupName("");
		userContact.setContactsList(contactsList.toString());

		getMessagesService().deleteUserContactsList(userContact.getUsrID(), userContact.getType());
		getMessagesService().saveUserContactsList(userContact);

		logger.debug("Leaving");
	}

	/**
	 * When user select radio group
	 * 
	 * @param event
	 */
	public void onCheck$notificationType(Event event) {
		logger.debug("Entering");

		Notify notify = Notify.valueOf((String) this.notificationType.getSelectedItem().getValue());

		if (notify == Notify.USER) {
			this.roles.clearSelection();
			this.rolesePanel.setOpen(false);
			this.contactsPanel.setOpen(true);
		} else {
			this.contacts.clearSelection();
			this.contactsPanel.setOpen(false);
			this.rolesePanel.setOpen(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * When user Click on send button.Publish the message and capture off line users and save message in
	 * "OfflineMessagebackup" table for show message when they login
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$sendMessage(Event event) throws Exception {
		logger.debug("Entering");

		if (StringUtils.isEmpty(this.messageBox.getValue())) {
			MessageUtil.showError("Please enter the Message.");

			logger.debug("Leaving");
			return;
		}

		Notify notify = Notify.valueOf((String) this.notificationType.getSelectedItem().getValue());

		// Get the recipients
		Listbox listbox = null;

		if (notify == Notify.USER) {
			listbox = this.contacts;
		} else {
			listbox = this.roles;
		}

		Set<Listitem> items = listbox.getSelectedItems();

		if (items.isEmpty()) {
			MessageUtil.showError("Please select the Recipients.");

			logger.debug("Leaving");
			return;
		}

		String[] to = new String[items.size()];
		int i = 0;

		for (Listitem item : items) {
			to[i++] = notify == Notify.USER ? item.getId() : item.getId().substring(5);
		}

		// Publish the message
		if (SysParamUtil.isAllowed(SMTParameterConstants.USER_NOTIFICATION_PUBLISH)) {
			eventManager.publish(this.messageBox.getValue(), username, notify, to);
		}

		doClear();

		logger.debug("Leaving");
	}

	/**
	 * This method fills all the contacts of the user.
	 */
	private void doFillContacts() {
		logger.trace(Literal.ENTERING);

		// Clear the existing list box.
		contacts.getItems().clear();

		// Get the list of user contacts.
		UserContactsList list = getMessagesService().getUserContactsList(username, "USERS");

		if (list == null || StringUtils.isEmpty(list.getContactsList())) {
			return;
		}

		// Display the user contacts.
		Listitem listitem;

		for (String contact : list.getContactsList().split(",")) {
			listitem = new Listitem();
			listitem.setId(contact);
			contacts.appendChild(listitem);

			listitem.appendChild(new Listcell(contact));
		}

		// Show online users.
		doShowOnlineUsers();

		logger.trace(Literal.LEAVING);
	}

	/**
	 * This method fills all the roles.
	 */
	private void doFillRoles() {
		logger.trace(Literal.ENTERING);

		// Clear the existing list box.
		roles.getItems().clear();

		// Get the list of system roles.
		JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<>(SecurityRole.class);
		searchObject.addTabelName("SecRoles");
		searchObject.addSort("RoleCd", false);

		SearchResult<SecurityRole> result = getPagedListWrapper().getPagedListService()
				.getSRBySearchObject(searchObject);

		if (result == null) {
			return;
		}

		// Display the system roles.
		Listitem listitem;

		for (SecurityRole role : result.getResult()) {
			listitem = new Listitem();
			// Prefix with "#RL#_" to avoid conflicts with user names.
			listitem.setId("#RL#_" + role.getRoleCd());
			roles.appendChild(listitem);

			listitem.appendChild(new Listcell(role.getRoleCd()));
		}

		logger.trace(Literal.LEAVING);
	}

	/**
	 * This method checks users is in online if yes sets name in green color
	 */
	private void doShowOnlineUsers() {
		logger.debug("Entering");

		int count = 0;
		Map<String, Boolean> activeUsers = EventManager.getActiveUsers();

		for (Listitem item : this.contacts.getItems()) {
			Listcell listcell = (Listcell) item.getChildren().get(0);

			if (activeUsers.containsKey(item.getId()) && activeUsers.get(item.getId())) {
				listcell.setStyle("font-style:normal; color:#00B050");

				count = count + 1;
			} else {
				listcell.setStyle("font-style:italic; color:Gray");
			}
		}

		contactsPanel.setTitle("Contacts [Online: " + count + "]");

		logger.debug("Leaving");
	}

	/**
	 * This event will raise for every n seconds and shows online users.
	 * 
	 * @param event
	 */
	public void onTimer$refreshTimer(Event event) {
		logger.debug("Entering");

		doShowOnlineUsers();

		logger.debug("Leaving");

	}

	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	public MessagesService getMessagesService() {
		return messagesService;
	}

}
