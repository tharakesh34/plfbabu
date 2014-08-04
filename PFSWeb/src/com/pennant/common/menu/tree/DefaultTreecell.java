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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Treecell;

import com.pennant.UserWorkspace;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.endofday.main.BatchMonitor;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.common.menu.util.ILabelElement;
import com.pennant.policy.model.UserImpl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;


/**
 * 
 */
class DefaultTreecell extends Treecell implements EventListener<Event>, Serializable, ILabelElement {

	private static final long serialVersionUID = 5221385297281381652L;
	private static final Logger logger = Logger.getLogger(DefaultTreecell.class);
	private SecurityUser securityUser;
	private String zulNavigation;

	@Override
	public void onEvent(Event event) throws Exception {

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		UserImpl userDetails = (UserImpl) currentUser.getPrincipal();		
		this.securityUser = userDetails.getSecurityUser();
		/* This condition checks  whether current system time is between user signOnFrom to signOnTo time 
		 * .if not show message box to prevent operations */
 		Component comp = event.getTarget();
		if(BatchMonitor.isEodRunning() && !comp.getId().contains("menu_Item_BatchAdmin")){
			Clients.showNotification(Labels.getLabel("EOD_RUNNING"),  "info", null, null, -1);
		}else{
		if(((securityUser.getUsrCanSignonFrom()!=null) && (securityUser.getUsrCanSignonTo()!=null))){

			if((DateUtility.compareTime(new Date(System.currentTimeMillis()),securityUser.getUsrCanSignonFrom(), false)==-1)
					|| DateUtility.compareTime( new Date(System.currentTimeMillis()),securityUser.getUsrCanSignonTo(), false)==1){

				MultiLineMessageBox.show(Labels.getLabel("OPERATIONS_TIME"
						,new String[]{PennantAppUtil.getTime(securityUser.getUsrCanSignonFrom()).toString()
								,PennantAppUtil.getTime(securityUser.getUsrCanSignonTo()).toString()}),"",
								MultiLineMessageBox.OK,MultiLineMessageBox.INFORMATION, true);		
			}else{

				openPage();
			}
		}else{
			openPage();
		}
	}
	}

	private void openPage() throws InterruptedException{
		try {
			// get the parameter for working with tabs from the application
			// params
			//final int workWithTabs = 1;

			//if (workWithTabs == 1) {

			/* get an instance of the borderlayout defined in the zul-file */
			final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
			/* get an instance of the searched CENTER layout area */
			final Center center = bl.getCenter();

			final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
			.getFellow("tabsIndexCenter");

			// Check if the tab is already open, if not than create them
			Tab checkTab = null;
			try {
				checkTab = (Tab) tabs.getFellow(this.getId().trim().replace("menu_Item_", "tab_"));
				checkTab.setSelected(true);
			} catch (final ComponentNotFoundException ex) {
				// Ignore if can not get tab.
			}

			if (checkTab == null) {

				final Tab tab = new Tab();
				tab.setId(this.getId().trim().replace("menu_Item_", "tab_"));
				tab.setLabel(Labels.getLabel(this.getId().trim()));
				tab.setClosable(true);
				tab.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
					@SuppressWarnings("deprecation")
					public void onEvent(Event event) throws UiException {
						String pageName = event.getTarget().getId().replace("tab_", "");
						UserWorkspace workspace= UserWorkspace.getInstance();
						workspace.deAlocateAuthorities(pageName);
					}
				});
				tab.setParent(tabs);

				final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter")
				.getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter")
				.getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);

				Executions.createComponents(getZulNavigation(), tabpanel, null);
				tab.setSelected(true);
			}
			//} 

			if (logger.isDebugEnabled()) {
				logger.debug("-->[" + getId() + "] calling zul-file: " + getZulNavigation());
			}
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
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
}