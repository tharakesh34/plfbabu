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
 * FileName    		:  DefaultTreecell.java													*                           
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
package com.pennant.common.menu.tree;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Treecell;

import com.pennant.UserWorkspace;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.endofday.main.BatchMonitor;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.common.menu.util.ILabelElement;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * 
 */
class DefaultTreecell extends Treecell implements EventListener<Event>, Serializable, ILabelElement {
	private static final long serialVersionUID = 5221385297281381652L;
	private static final Logger logger = Logger.getLogger(DefaultTreecell.class);

	private String zulNavigation;
	private UserWorkspace workspace;

	public DefaultTreecell() {
		super();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		setUserWorkspace();

		LoggedInUser user = workspace.getLoggedInUser();

		/*
		 * This condition checks whether current system time is between user signOnFrom to signOnTo time .if not show
		 * message box to prevent operations
		 */
		Component comp = event.getTarget();

		if (BatchMonitor.isEodRunning() && (!comp.getId().contains("menu_Item_BatchAdmin") && !comp.getId().contains("menu_Item_CustomerEOD")) ) {
			Clients.showNotification(Labels.getLabel("EOD_RUNNING"), "info", null, null, -1);
		} else if (!"Y".equalsIgnoreCase(SysParamUtil.getValueAsString(PennantConstants.ALLOW_ACCESS_TO_APP))) {
			Clients.showNotification(Labels.getLabel("ALLOW_ACCESS_RESTRICTION"), "info", null, null, -1);
		} else if (SysParamUtil.getValueAsString(PennantConstants.APP_PHASE).equals(PennantConstants.APP_PHASE_EOD)
				&& (!comp.getId().contains("menu_Item_BatchAdmin") && !comp.getId().contains("menu_Item_CustomerEOD"))) {
			Clients.showNotification(Labels.getLabel("CHANGE_EOD_PHASE"), "info", null, null, -1);
		} else {
			if ((user.getLogonFromTime() != null) && (user.getLogonToTime() != null)) {
				if ((DateUtility.compareTime(new Date(System.currentTimeMillis()), user.getLogonFromTime(), false) == -1)
						|| DateUtility.compareTime(new Date(System.currentTimeMillis()), user.getLogonToTime(), false) == 1) {
					MultiLineMessageBox.show(
							Labels.getLabel("OPERATIONS_TIME",
									new String[] { PennantAppUtil.getTime(user.getLogonFromTime()).toString(),
											PennantAppUtil.getTime(user.getLogonToTime()).toString() }), "",
							MultiLineMessageBox.OK, MultiLineMessageBox.INFORMATION, true);
				} else {
					openPage();
				}
			} else {
				openPage();
			}
		}
	}

	private void openPage() throws InterruptedException {
		String tabId = this.getId().replace("menu_Item_", "tab_");

		// Get the container component to add the page as tab.
		Tabs tabs = (Tabs) Path.getComponent("/outerIndexWindow/tabsIndexCenter");

		try {
			// Open the tab if already exists.
			if (tabs.hasFellow(tabId)) {
				((Tab) tabs.getFellow(tabId)).setSelected(true);
				return;
			}

			// Create and append the tab
			Tab tab = new Tab();
			tab.setId(tabId);
			tab.setLabel(Labels.getLabel(this.getId()));
			tab.setClosable(true);
			tab.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
				public void onEvent(Event event) throws UiException {
					String page = event.getTarget().getId().replace("tab_", "");
					workspace.deAllocateAuthorities(page);
				}
			});
			tab.setParent(tabs);

			Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setHeight("100%");
			tabpanel.setStyle("padding: 0px;");
			tabpanel.setParent(tabpanels);

			String[] requestPage = getZulNavigation().split("\\?");
			String zulPage = requestPage[0];

			logger.debug(getId() + " Calling zul file: " + zulPage);

			Map<String, String> mapParams = null;
			if (requestPage.length > 1) {
				String params = requestPage[1];

				if (params != null) {
					mapParams = new HashMap<String, String>();

					String[] param = params.split("&");

					for (String item : param) {
						if (item.split("=").length == 2) {
							mapParams.put(item.split("=")[0], item.split("=")[1]);
						} else if (item.split("=").length == 1) {
							mapParams.put(item.split("=")[0], null);
						}
					}
				}
			}

			Executions.createComponents(zulPage, tabpanel, mapParams);
			tab.setSelected(true);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
	}

	private String getZulNavigation() {
		return this.zulNavigation;
	}

	@Override
	public void setZulNavigation(String zulNavigation) {
		this.zulNavigation = zulNavigation;
		if (!StringUtils.isEmpty(zulNavigation)) {
			addEventListener("onClick", this);
		}
	}

	private void setUserWorkspace() {
		if (workspace == null) {
			workspace = (UserWorkspace) SpringUtil.getBean("userWorkspace");
		}
	}
}
