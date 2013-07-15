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
 * FileName    		:  USerBarCtl.java														*                           
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

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.backend.service.MenuDetailsService;

 
public class UserBarCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(UserBarCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window winUserBar; // autowired

	// Used Labels
	private org.zkoss.zul.Timer timer;
	private Label label_branch;
	private Menu menu_user;
	
	// Localized labels for the columns

	private String _LoginTimeText = "";
	private String _LoginDateText = "";
	private String _UserText = "";
	private String _BranchCodeText = "";
	private String _DepartmentCodeText = "";

	private transient MenuDetailsService menuDetailsService;

	/**
	 * Default constructor.
	 */
	public UserBarCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		logger.debug("Entering ");
		super.doAfterCompose(window);

		// Listener for loginTime
		EventQueues.lookup("loginTimeEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				set_LoginTimeText(msg);
				doShowLabel();
			}
		});
		// Listener for loginDate
		EventQueues.lookup("loginDateEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				set_LoginDateText(msg);
				doShowLabel();
			}
		});
		
		// Listener for user
		EventQueues.lookup("userNameEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				set_UserText(msg);
				doShowLabel();
			}
		});

		// Listener for BranchCode
		EventQueues.lookup("branchCodeEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				set_BranchCodeText(msg);
				doShowLabel();
			}
		});

		// Listener for DepartmentCode
		EventQueues.lookup("departmentCodeEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				set_DepartmentCodeText(msg);
				doShowLabel();
			}
		});
		logger.debug("Leaving ");
	}

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 */
	public void onCreate$winUserBar(Event event) {
		this.winUserBar.setBorder("none");
		Events.postEvent("onTimer", this.timer, event);
	}
 
	/**
	 * Shows the labels with values.<br>
	 */
	private void doShowLabel() {
		this.menu_user.setLabel(get_UserText())	;
		this.label_branch.setValue(get_BranchCodeText() + "/" + get_DepartmentCodeText());
	}
	
	

	/**
	 * When the 'Logout' button is clicked.<br>
	 * 
	 * @throws IOException
	 */
	public void onClick$menuitem_logout(Event event) throws IOException {
 		getUserWorkspace().doLogout(); // logout.
	}
	
	public void onClick$menuitem_changePasssword(Event event) throws IOException {
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		/* get an instance of the searched CENTER layout area */
		final West west = bl.getWest();
		
		Button button = (Button) west.getChildren().get(0).getChildren().get(0).getFellowIfAny("btnGo");
		Textbox textbox = (Textbox) west.getChildren().get(0).getChildren().get(0).getFellowIfAny("menuName1");
		textbox.setValue("changePassword");
		Events.postEvent("onClick", button, event);
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void set_LoginTimeText(String _LoginTimeText) {
		this._LoginTimeText = _LoginTimeText;
	}

	public String get_LoginTimeText() {
		return this._LoginTimeText;
	}
	
	public void set_LoginDateText(String _LoginDateText) {
		this._LoginDateText = _LoginDateText;
	}
	
	public String get_LoginDateText() {
		return this._LoginDateText;
	}
	public void set_UserText(String _UserText) {
		this._UserText = _UserText;
	}

	public String get_UserText() {
		return this._UserText;
	}

	public void set_BranchCodeText(String _BranchCodeText) {
		this._BranchCodeText = _BranchCodeText;
	}

	public String get_BranchCodeText() {
		return this._BranchCodeText;
	}

	public void set_DepartmentCodeText(String _DepartmentCodeText) {
		this._DepartmentCodeText = _DepartmentCodeText;
	}

	public String get_DepartmentCodeText() {
		return this._DepartmentCodeText;
	}

	/**
	 * @return the menuDetailsService
	 */
	public MenuDetailsService getMenuDetailsService() {
		return menuDetailsService;
	}

	/**
	 * @param menuDetailsService the menuDetailsService to set
	 */
	public void setMenuDetailsService(MenuDetailsService menuDetailsService) {
		this.menuDetailsService = menuDetailsService;
	}

}
