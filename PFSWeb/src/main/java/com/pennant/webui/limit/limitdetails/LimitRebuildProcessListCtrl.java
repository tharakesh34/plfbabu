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
 * * FileName : LimitDetailsListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified
 * Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.limit.limitdetails;

import javax.xml.datatype.DatatypeConfigurationException;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.service.limitservice.impl.InstitutionLimitRebuild;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * This is the controller class for the /WEB-INF/pages/Limit/LimitDetails/LimitDetailsList.zul file.<br>
 * ************************************************************<br>
 */
public class LimitRebuildProcessListCtrl extends GFCBaseListCtrl<LimitHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_limitRebuildProcessList;
	protected Borderlayout borderLayout_LimitRebuildProcessList;
	protected Label label_status;

	private transient InstitutionLimitRebuild institutionLimitRebuild;

	/**
	 * default constructor.<br>
	 */
	public LimitRebuildProcessListCtrl() {
		super();
	}

	// Component Events

	public void onCreate$window_limitRebuildProcessList(Event event) {
		logger.debug("Entering" + event.toString());

		setPageComponents(window_limitRebuildProcessList, borderLayout_LimitRebuildProcessList, null, null);

		doRenderPage();

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnStart(Event event) throws DatatypeConfigurationException {
		logger.debug("Entering");

		// label_status.setValue("Processing...");

		institutionLimitRebuild.executeLimitRebuildProcess();

		// label_status.setValue("Completed");

		Clients.showNotification("Limit Rebuild Process Completed.");

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	// setters / getters

	public InstitutionLimitRebuild getInstitutionLimitRebuild() {
		return institutionLimitRebuild;
	}

	public void setInstitutionLimitRebuild(InstitutionLimitRebuild institutionLimitRebuild) {
		this.institutionLimitRebuild = institutionLimitRebuild;
	}
}