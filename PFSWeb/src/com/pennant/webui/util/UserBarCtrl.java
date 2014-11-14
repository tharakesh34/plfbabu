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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.service.MenuDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

 
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
	private Label label_branch;
	private Menu menu_user;
	
	// Localized labels for the columns

	private String _LoginTimeText = "";
	private String _LoginDateText = "";
	private String _UserText = "";
	private String _BranchCodeText = "";
	private String _DepartmentCodeText = "";

	protected Label label_currentDate; // autowired
	protected Label label_currentTime; // autowired
    protected Timer hostStatusTimer; // autowired
    protected Window outerIndexWindow; // autowired
    
	private transient MenuDetailsService menuDetailsService;

	/**
	 * Default constructor.
	 */
	public UserBarCtrl() {
		super();
	}

	@SuppressWarnings("unchecked")
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
		
		// Listener for Last Login
		EventQueues.lookup("lastLoginEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				doShowLastLogin();
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
		this.hostStatusTimer.setDelay(1000);
		this.hostStatusTimer.start();
	}
 
	/**
	 * Shows the labels with values.<br>
	 */
	private void doShowLabel() {
		this.menu_user.setLabel(get_UserText())	;
		this.label_branch.setValue(get_BranchCodeText() + "/" + get_DepartmentCodeText());
	}
	
	private void doShowLastLogin() {
		String loginInfo = getLastLoginInfo(this.menu_user.getLabel());
		if(loginInfo != null) {
			Clients.showNotification(loginInfo, "info", this.menu_user, "start_before", -1);
		}
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
	
	private String getLastLoginInfo(String usrLogin) {

		StringBuilder builder = new StringBuilder("<table>");
		Map<String, Object> inputParamMap = new LinkedHashMap<String, Object>();
		Map<String, Object> outputParamMap = new LinkedHashMap<String, Object>();
		inputParamMap.put("LOGIN_USER", Types.VARCHAR);
		outputParamMap.put("@LAST_SUCC_LOGINTIME", Types.VARCHAR);
		outputParamMap.put("@FAILED_COUNT", Types.INTEGER);
		outputParamMap.put("@LAST_UNSUCC_LOGINTIME", Types.VARCHAR);	
		outputParamMap = this.menuDetailsService.getLastLoginInfo("LOGIN_DETAILS", usrLogin, inputParamMap, outputParamMap);

		String sucLogin = "", failLogin = "", failCount = "0";
		for (Entry<String, Object> entry : outputParamMap.entrySet()) {
			if(entry.getValue() == null) {
				return null;
			}
			if("@LAST_SUCC_LOGINTIME".equals(entry.getKey())){	
				sucLogin = PennantAppUtil.formateDate(new Date((Timestamp.valueOf(entry.getValue().toString())).getTime()), PennantConstants.dateTimeAMPMFormat);
			}

			if("@LAST_UNSUCC_LOGINTIME".equals(entry.getKey()) && entry.getValue() != null){
				failLogin = PennantAppUtil.formateDate(new Date((Timestamp.valueOf(entry.getValue().toString())).getTime()), PennantConstants.dateTimeAMPMFormat);
			}

			if("@FAILED_COUNT".equals(entry.getKey()) && entry.getValue() != null && Integer.parseInt(entry.getValue().toString()) > 0){	       
				failCount = entry.getValue().toString();
					
			}
		}	
		
		if (StringUtils.trimToNull(sucLogin) != null) {
			builder.append("<tr>");
			builder.append("<td nowrap>").append(Labels.getLabel("label_last_logged")).append("</td>");
			builder.append("<td nowrap>").append(sucLogin).append("</td>");
			builder.append("</tr>");
		}
		if (StringUtils.trimToNull(failLogin) != null) {
			builder.append("<tr>");
			builder.append("<td nowrap>").append(Labels.getLabel("label_last_unsuccessfull_login")).append("</td>");
			builder.append("<td nowrap>").append(failLogin).append("</td>");
			builder.append("</tr>");
			
			builder.append("<tr>");
			builder.append("<td nowrap>").append(Labels.getLabel("label_last_no_tries")).append("</td>");
			builder.append("<td nowrap>").append(failCount).append("</td>");
			builder.append("</tr>");
		}
		builder.append("</table>");
		return builder.toString();
	}

	/**
	 * this event will raise for every n seconds .
	 * 
	 * @param event
	 */
	public void onTimer$hostStatusTimer(Event event) {
		
		Date date  = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MMM/yyyy");
		label_currentDate.setValue(dateFormat.format(date));
		dateFormat = new java.text.SimpleDateFormat("HH:mm:ss");
		label_currentTime.setValue(dateFormat.format(DateUtility.getUtilDate()));
		String hostStatusReq = com.pennant.app.util.SystemParameterDetails
				.getSystemParameterValue("HOSTSTATUS_REQUIRED").toString();
		
		//winUserBar
		Window winMessageBar =  (Window) this.outerIndexWindow.getFellowIfAny("winMessageBar");
		
		if (winMessageBar.getFellowIfAny("hostStatus") != null) {
			Image image = (Image) winMessageBar.getFellowIfAny("hostStatus");
			if (hostStatusReq.equals("Y")) {
				image.setVisible(true);
				if (com.pennant.app.util.HostStatusUtil.getHostStatus("PFF")) {
					image.setSrc("//images//Pennant//HostUp.png");
				} else {
					image.setSrc("//images//Pennant//HostDown.png");
				}
			} 
		}
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void set_LoginTimeText(String loginTimeText) {
		this._LoginTimeText = loginTimeText;
	}

	public String get_LoginTimeText() {
		return this._LoginTimeText;
	}
	
	public void set_LoginDateText(String loginDateText) {
		this._LoginDateText = loginDateText;
	}
	
	public String get_LoginDateText() {
		return this._LoginDateText;
	}
	public void set_UserText(String userText) {
		this._UserText = userText;
	}

	public String get_UserText() {
		return this._UserText;
	}

	public void set_BranchCodeText(String branchCodeText) {
		this._BranchCodeText = branchCodeText;
	}

	public String get_BranchCodeText() {
		return this._BranchCodeText;
	}

	public void set_DepartmentCodeText(String departmentCodeText) {
		this._DepartmentCodeText = departmentCodeText;
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
