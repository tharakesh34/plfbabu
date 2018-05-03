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
 * FileName    		:  SecurityOperationRolesListCtrl .java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  10-03-2014															*
 *                                                                  
 * Modified Date    :  10-03-2014															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *10-03-2014	      Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.SecurityOperationRoles;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.service.administration.SecurityOperationService;
import com.pennant.webui.administration.securityoperation.model.SecurityOperationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityOperationRoles/OperationRolesList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SecurityOperationRolesListCtrl extends GFCBaseListCtrl<SecurityOperation>  {

	 private static final long serialVersionUID = -577256448245687404L;
	 private static final Logger logger = Logger.getLogger(SecurityOperationRolesListCtrl.class);

	 protected Window        window_OperationRolesList;                      
	 protected Borderlayout  borderLayout_OperationRolesList;  
	 private   Paging        pagingOperationRolesList;  
	 private   Listbox       listBoxOperationRoles;
	 
	 protected Listheader    listheader_OprCode;                                  
	 protected Listheader    listheader_OprDesc;   
	 
	 protected Button        button_OperationRolesList_OperationRolesSearchDialog;
	 protected Button        button_OperationRolesList_PrintList;                 
	
	 protected Textbox oprCode;
	 protected Textbox oprDesc;
	 
	 protected Listbox sortOperator_oprDesc;
	 protected Listbox sortOperator_oprCode;
	 
	 private transient SecurityOperationService    securityOperationService;

	 /**
	  * default constructor.<br>
	  */
	 public SecurityOperationRolesListCtrl (){
		 super();
	 }
	 
	@Override
	protected void doSetProperties() {
		super.moduleCode = "SecurityOperation";
		super.pageRightName = "SecurityOperationList";
		super.tableName = "SecOperations_View";
		super.queueTableName = "SecOperations_RView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_OperationRolesList(Event event) {
		setPageComponents(window_OperationRolesList, borderLayout_OperationRolesList, listBoxOperationRoles,
				pagingOperationRolesList);
		setItemRender(new SecurityOperationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_OperationRolesList_OperationRolesSearchDialog);
		registerField("oprID",SortOrder.ASC);
		registerField("oprCode", listheader_OprCode, SortOrder.NONE, oprCode, sortOperator_oprCode, Operators.STRING);
		registerField("oprDesc", listheader_OprDesc, SortOrder.NONE, oprDesc, sortOperator_oprDesc, Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_OperationRolesList_OperationRolesSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}
	 
	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.administration.securityoperation.model.SecurityOperationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSecurityOperationItemDoubleClicked(Event event) {

		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxOperationRoles.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		SecurityOperation aSecurityOperation = new SecurityOperation();

		if (enqiryModule) {
			aSecurityOperation = securityOperationService.getApprovedSecurityOperationById(id);
		} else {
			aSecurityOperation = securityOperationService.getSecurityOperationRoleById(id, "_RView", true);
		}

		if (aSecurityOperation == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND OprId=" + aSecurityOperation.getOprID() + " AND version="
				+ aSecurityOperation.getVersion() + " ";

		if (doCheckAuthority(aSecurityOperation, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aSecurityOperation.getWorkflowId() == 0) {
				aSecurityOperation.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aSecurityOperation);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSecurityOperation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SecurityOperation aSecurityOperation) {
		logger.debug("Entering");

		Map<String, Object> aruments = getDefaultArguments();
		aruments.put("securityOperation", aSecurityOperation);
		aruments.put("SecurityOperationRoles", this);
		aruments.put("newRecord", aSecurityOperation.isNew());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Administration/SecurityOperationRoles/SecurityOperationRolesDialog.zul", null, aruments);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setSecurityOperationService(SecurityOperationService securityOperationService) {
		this.securityOperationService = securityOperationService;
	}
}