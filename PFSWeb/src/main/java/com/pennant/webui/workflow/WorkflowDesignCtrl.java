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
 * FileName    		:  WorkFlowDialogCtl.java												*                           
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
package com.pennant.webui.workflow;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class WorkflowDesignCtrl extends GFCBaseCtrl<WorkFlowDetails> {
	private static final long serialVersionUID = -1351367303946249042L;
	private static Logger logger = Logger.getLogger(WorkflowDesignCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_workflowDesign; 
	// not auto wired vars
	private WorkFlowDetails workFlowDetails; // overhanded per param
	private transient WorkFlowListCtrl workFlowListCtrl; // overhanded per param
	protected Iframe iframe;

	private transient WorkFlowDetailsService workFlowDetailsService;
	protected JdbcSearchObject<WorkFlowDetails> searchObj;
	private String								designerUrl;

	public WorkflowDesignCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "WorkFlowDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected WorkFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_workflowDesign(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_workflowDesign);

		try {
			    if (arguments.containsKey("workFlowDetails")) {
				this.workFlowDetails = (WorkFlowDetails) arguments
						.get("workFlowDetails");
				WorkFlowDetails flowDetails = new WorkFlowDetails();
				BeanUtils.copyProperties(this.workFlowDetails, flowDetails);
				this.workFlowDetails.setBefImage(flowDetails);

			} else {
				setWorkFlowDetails(null);
			}

			if (arguments.containsKey("workFlowListCtrl")) {
				setWorkFlowListCtrl((WorkFlowListCtrl) arguments
						.get("workFlowListCtrl"));
			} else {
				setWorkFlowListCtrl(null);
			}

			doShowDialog(getWorkFlowDetails());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_workflowDesign.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aWorkFlowDetails
	 *            WorkFlowDetails
	 * @throws Exception
	 */
	public void doWriteBeanToComponents(WorkFlowDetails aWorkFlowDetails) {
		logger.debug("Entering ");
		
		if (aWorkFlowDetails.isNew()) {
			this.iframe.setSrc(designerUrl + "editor/#/processes");
		} else {
			this.iframe.setSrc(designerUrl + "editor/#/editor/" + aWorkFlowDetails.getId());
		}
		
		this.iframe.setAttribute("MYNAME", "SAI");

		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aWorkFlowDetails
	 * @throws Exception
	 */
	public void doShowDialog(WorkFlowDetails aWorkFlowDetails) throws Exception {
		logger.debug("Entering");
		try {
			doWriteBeanToComponents(aWorkFlowDetails);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_workflowDesign.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		refreshList();
		doClose(false);
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getWorkFlowListCtrl().doReset();
		getWorkFlowListCtrl().search();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public WorkFlowDetails getWorkFlowDetails() {
		return workFlowDetails;
	}

	public void setWorkFlowDetails(WorkFlowDetails workFlowDetails) {
		this.workFlowDetails = workFlowDetails;
	}

	public WorkFlowDetailsService getWorkFlowDetailsService() {
		return workFlowDetailsService;
	}

	public void setWorkFlowDetailsService(
			WorkFlowDetailsService workFlowDetailsService) {
		this.workFlowDetailsService = workFlowDetailsService;
	}

	public WorkFlowListCtrl getWorkFlowListCtrl() {
		return workFlowListCtrl;
	}

	public void setWorkFlowListCtrl(WorkFlowListCtrl workFlowListCtrl) {
		this.workFlowListCtrl = workFlowListCtrl;
	}

	public void setDesignerUrl(String designerUrl) {
		this.designerUrl = designerUrl;
	}
}
