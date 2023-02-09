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
 * * FileName : ExtInterfaceConfDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-08-2019 * *
 * Modified Date : 10-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.externalinterface.InterfaceConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.model.externalinterface.InterfaceServiceLog;
import com.pennant.backend.service.externalinterface.InterfaceConfigurationService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ExternalInterface/InterfaceConfiguration/externalInterfaceConfigurationDialog.zul file. <br>
 */
public class InterfaceServiceDialogCtrl extends GFCBaseCtrl<InterfaceConfiguration> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InterfaceServiceDialogCtrl.class);
	private Textbox requestData;
	private Textbox responseData;

	protected Window window_InterfaceServiceDialog;

	private transient InterfaceServiceListCtrl interfaceServiceListCtrl; // overhanded
	private InterfaceServiceLog interfaceServiceLog;
	// per
	// param
	private transient InterfaceConfigurationService interfaceConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public InterfaceServiceDialogCtrl() {
		super();
	}

	@Override
	protected String getReference() {
		return interfaceServiceLog.getReference();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_InterfaceServiceDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_InterfaceServiceDialog);

		try {

			/* set components visible dependent of the users rights */

			if (arguments.containsKey("interfaceServiceList")) {
				this.interfaceServiceLog = (InterfaceServiceLog) arguments.get("interfaceServiceList");
				InterfaceServiceLog befImage = new InterfaceServiceLog();
				BeanUtils.copyProperties(this.interfaceServiceLog, befImage);
				this.interfaceServiceLog.setBefImage(befImage);

				setInterfaceServiceLog(this.interfaceServiceLog);
			} else {
				setInterfaceServiceLog(null);
			}

			if (arguments.containsKey("interfaceServiceListCtrl")) {
				setInterfaceServiceListCtrl((InterfaceServiceListCtrl) arguments.get("countryListCtrl"));
			} else {
				setInterfaceServiceListCtrl(null);
			}

			doShowDialog(getInterfaceServiceLog());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_InterfaceServiceDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	private void doShowDialog(InterfaceServiceLog interfaceServiceLog) {
		this.requestData.setReadonly(true);
		this.responseData.setReadonly(true);
		try {
			// fill the components with the data
			doWriteBeanToComponents(interfaceServiceLog);

			setDialog(DialogType.OVERLAPPED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_InterfaceServiceDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(false);
		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(InterfaceServiceLog interfaceServiceLog) {
		logger.debug("Entering");

		this.requestData.setValue(interfaceServiceLog.getRequest());
		this.responseData.setValue(interfaceServiceLog.getResponse());

		logger.debug("Leaving");
	}

	public InterfaceServiceListCtrl getInterfaceServiceListCtrl() {
		return interfaceServiceListCtrl;
	}

	public void setInterfaceServiceListCtrl(InterfaceServiceListCtrl interfaceServiceListCtrl) {
		this.interfaceServiceListCtrl = interfaceServiceListCtrl;
	}

	public InterfaceServiceLog getInterfaceServiceLog() {
		return interfaceServiceLog;
	}

	public void setInterfaceServiceLog(InterfaceServiceLog interfaceServiceLog) {
		this.interfaceServiceLog = interfaceServiceLog;
	}

	public InterfaceConfigurationService getInterfaceConfigurationService() {
		return interfaceConfigurationService;
	}

	public void setInterfaceConfigurationService(InterfaceConfigurationService interfaceConfigurationService) {
		this.interfaceConfigurationService = interfaceConfigurationService;
	}

}
