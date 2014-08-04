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
 * FileName    		:  DefaultDropDownMenuItem.java											*                           
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


package com.pennant.common.menu.dropdown;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

import com.pennant.common.menu.util.ILabelElement;
import com.pennant.webui.util.PTMessageUtils;


class DefaultDropDownMenuItem extends Menuitem implements EventListener<Event>, Serializable, ILabelElement {

	private static final long serialVersionUID = -2813840859147955432L;
	private static final Logger logger = Logger.getLogger(DefaultDropDownMenuItem.class);

	private String zulNavigation;

	@Override
	public void onEvent(Event event) throws Exception {

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
					checkTab = (Tab) tabs.getFellow("tab_" + this.getLabel().trim());
					checkTab.setSelected(true);
				} catch (final ComponentNotFoundException ex) {
					// Ignore if can not get tab.
				}

				if (checkTab == null) {
					final Tab tab = new Tab();
					tab.setId("tab_" + this.getLabel().trim());
					tab.setLabel(this.getLabel().trim());
					tab.setClosable(true);

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
		/* else {
				 get an instance of the borderlayout defined in the zul-file 
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				 get an instance of the searched CENTER layout area 
				final Center center = bl.getCenter();
				 clear the center child comps 
				center.getChildren().clear();
				
				 * create the page and put it in the center layout area
				 
				Executions.createComponents(getZulNavigation(), center, null);
			}
*/
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
