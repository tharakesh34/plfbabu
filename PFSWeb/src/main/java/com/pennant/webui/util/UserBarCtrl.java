/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : USerBarCtl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.AutomationUtil;
import com.pennant.util.PennantAppUtil;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.ComponentUtil;

public class UserBarCtrl extends GFCBaseCtrl<AbstractWorkflowEntity> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(UserBarCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
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

	protected Label label_currentDate;
	protected Label label_currentTime;
	protected Window outerIndexWindow;
	protected Menuitem menuitem_logout;
	protected Menuitem menuitem_changePasssword;
	protected Menuitem menuitem_global_logout;
	LoggedInUser user = null;

	private boolean recordXpath = false;
	private boolean generateXpath = false;

	protected Button recordXpathComp;
	// protected Button generateXpathFile;
	protected A xpathDownload;

	@Value("${xpath.storge.path}")
	private String xpathGenPath;

	@Value("${xpath.storage.filename}")
	private String xpathFileName;

	@Value("${authentication.sso:false}")
	private boolean sso;

	@Autowired
	AutomationUtil automationUtil;

	/**
	 * Default constructor.
	 */
	public UserBarCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug("Entering");

		super.doAfterCompose(comp);
		user = getUserWorkspace().getLoggedInUser();

		String appDate = SysParamUtil.getAppDate(DateFormat.SHORT_DATE);
		set_LoginTimeText(PennantAppUtil.getTime(user.getLogonTime()).toString());
		set_LoginDateText(appDate);
		set_UserText(user.getUserName());
		set_BranchCodeText(user.getBranchCode());
		set_DepartmentCodeText(user.getDepartmentCode());

		// Desktop Time Setting
		label_currentDate.setValue(appDate);
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm");
		label_currentTime.setValue(dateFormat.format(DateUtility.getSysDate()));
		recordXpathComp.setLabel("Start Rec Xpath");
		xpathDownload.setDisabled(true);

		doShowLabel();

		// Listener for Last Login
		EventQueues.lookup("lastLoginEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener<Event>() {
			public void onEvent(Event event) {
				doShowLastLogin();
			}
		});

		switch (user.getAuthType()) {
		case "SSO":
			menuitem_logout.setVisible(false);
			menuitem_changePasssword.setVisible(false);
			break;
		case "LDAP":
			menuitem_logout.setVisible(true);
			menuitem_changePasssword.setVisible(false);
			break;
		default:
			menuitem_logout.setVisible(true);
			menuitem_changePasssword.setVisible(true);
			break;
		}

		if (sso) {
			menuitem_global_logout.setVisible(true);
			menuitem_changePasssword.setVisible(false);
		} else {
			menuitem_global_logout.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 */
	public void onCreate$winUserBar(Event event) {
		this.winUserBar.setBorder("none");
	}

	/**
	 * Shows the labels with values.<br>
	 */
	private void doShowLabel() {
		this.menu_user.setLabel(get_UserText());
		this.label_branch.setValue(get_BranchCodeText() + "/" + get_DepartmentCodeText());
	}

	private void doShowLastLogin() {
		String loginInfo = getLastLoginInfo();
		if (loginInfo != null) {
			Clients.showNotification(loginInfo, "info", this.menu_user, "start_before", -1);
		}
	}

	/**
	 * When the 'Logout' button is clicked.<br>
	 * 
	 * @throws IOException
	 */
	public void onClick$menuitem_logout(Event event) throws IOException {
		getUserWorkspace().doLogout();

		if (sso) {
			Executions.sendRedirect("/saml/logout?local=true");
		} else {
			Executions.sendRedirect("/csrfLogout.zul");
		}

	}

	/**
	 * When the 'Logout' button is clicked.<br>
	 * 
	 * @throws IOException
	 */
	public void onClick$menuitem_global_logout(Event event) throws IOException {
		getUserWorkspace().doLogout();

		if (sso) {
			Executions.sendRedirect("/saml/logout");
		} else {
			Executions.sendRedirect("/csrfLogout.zul");
		}

	}

	public void onClick$menuitem_changePasssword() throws URISyntaxException {
		ComponentUtil.openMenuItem("menu_Item_ChgPwd", "/WEB-INF/pages/PasswordReset/changePwd.zul", true, null);
	}

	private String getLastLoginInfo() {
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		StringBuilder builder = new StringBuilder("<table>");

		if ((loggedInUser.getPrevPassLogonTime() == null && loggedInUser.getPrevFailLogonTime() == null)) {
			builder.append("<tr>");
			builder.append("<td colspan='2' nowrap>");
			builder.append("Welcome ");
			builder.append(loggedInUser.getUserName());
			builder.append("!");
			builder.append("</td>");
			builder.append("</tr>");
		} else {
			if (loggedInUser.getPrevPassLogonTime() != null) {
				builder.append("<tr>");
				builder.append("<td nowrap>");
				builder.append(Labels.getLabel("label_last_logged"));
				builder.append("</td>");
				builder.append("<td nowrap>");
				builder.append(DateUtility.format(loggedInUser.getPrevPassLogonTime(), DateFormat.LONG_DATE_TIME));
				builder.append("</td>");
				builder.append("</tr>");
			}

			if (loggedInUser.getPrevFailLogonTime() != null) {
				builder.append("<tr>");
				builder.append("<td nowrap>");
				builder.append(Labels.getLabel("label_last_unsuccessfull_login"));
				builder.append("</td>");
				builder.append("<td nowrap>");
				builder.append(DateUtility.format(loggedInUser.getPrevFailLogonTime(), DateFormat.LONG_DATE_TIME));
				builder.append("</td>");
				builder.append("</tr>");

				builder.append("<tr>");
				builder.append("<td nowrap>");
				builder.append(Labels.getLabel("label_last_no_tries"));
				builder.append("</td>");
				builder.append("<td nowrap>");
				builder.append(loggedInUser.getFailAttempts());
				builder.append("</td>");
				builder.append("</tr>");
			}
		}

		builder.append("</table>");

		return builder.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public boolean isRecordXpath() {
		return recordXpath;
	}

	public void setRecordXpath(boolean recordXpath) {
		this.recordXpath = recordXpath;
	}

	public boolean isGenerateXpath() {
		return generateXpath;
	}

	public void setGenerateXpath(boolean generateXpath) {
		this.generateXpath = generateXpath;
	}

	public void onClick$recordXpathComp(Event event) {
		this.recordXpath = !this.recordXpath;

		AutomationUtil.getSingleton().setRecordXpath(recordXpath);

		if (!recordXpath) {
			recordXpathComp.setLabel("Start Rec Xpath");
			xpathDownload.setDisabled(false);
		} else {
			recordXpathComp.setLabel("Stop Rec Xpath");
			xpathDownload.setDisabled(true);
		}
	}

	public void onClick$xpathDownload(Event event) {
		System.out.println("on click download event");

		try {

			String filePath = xpathGenPath;
			// String fileName = "xpath.csv";

			if (filePath != null && xpathFileName != null) {
				filePath = filePath.concat("/").concat(xpathFileName);
			}

			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			InputStream inputStream = new FileInputStream(filePath);
			int data;
			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}

			inputStream.close();

			inputStream = null;
			String contentType = "text/plain";
			if (StringUtils.endsWith(xpathFileName.toUpperCase(), PennantConstants.DOC_TYPE_CSV_EXT)) {
				contentType = "text/csv";
			}
			Filedownload.save(stream.toByteArray(), contentType,
					new Timestamp(System.currentTimeMillis()) + "_" + xpathFileName);

			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
