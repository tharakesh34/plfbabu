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
 * FileName    		:  ProcessViewCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-11-2017    														*
 *                                                                  						*
 * Modified Date    :  05-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-11-2017       Pennant	                 0.1                                            * 
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

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/util/ProcessView.zul file.
 */
public class ProcessViewCtrl extends GFCBaseCtrl<WorkFlowDetails> implements Serializable {
	private static final long serialVersionUID = 7494345200993379495L;
	private static final Logger logger = Logger.getLogger(ProcessViewCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	private Window processViewWindow; // autoWired
	private Iframe workflowContentIframe; // autoWired

	private String designerUrl;
	protected WorkFlowDetails workflow;

	public ProcessViewCtrl() {
		super();
	}

	public String getDesignerUrl() {
		return designerUrl;
	}

	public void setDesignerUrl(String designerUrl) {
		this.designerUrl = designerUrl;
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Record AuditData List in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$processViewWindow(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		long workFlowId = (long) arguments.get("workFlowId");
		String moduleCode = (String) arguments.get("moduleCode");
		Object keyValue = arguments.get("keyValue");
		long fromAuditId = (long) arguments.get("fromAuditId");
		long toAuditId = (long) arguments.get("toAuditId");

		if (workFlowId != 0L) {
			workflow = WorkFlowUtil.getWorkflow(workFlowId);
			//doWriteBeanToComponents();
			if (fromAuditId != 0L && toAuditId != 0L) {
				loadWorkflow(workFlowId, moduleCode, keyValue, fromAuditId, toAuditId);
			}
			doSetFieldProperties();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like width and height.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		processViewWindow.setHeight("100%");
		processViewWindow.setWidth("100%");
		this.processViewWindow.doModal();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To load Process View as per respective below data.
	 * 
	 * @param workflowId
	 *            (long)
	 * @param moduleCode
	 *            (String)
	 * @param keyValue
	 *            (Object)
	 * @param fromAuditId
	 *            (long)
	 * @param toAuditId
	 *            (long)
	 */
	private void loadWorkflow(long workflowId, String moduleCode, Object keyValue, long fromAuditId,
			long toAuditId) {
		logger.debug(Literal.ENTERING);

		String args = moduleCode + "," + keyValue + "," + fromAuditId + "," + toAuditId;
		try {
			workflowContentIframe.setSrc(designerUrl + "editor/#/process/" + workflowId + "/" + args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		this.processViewWindow.onClose();
		logger.debug(Literal.LEAVING);
	}
}
