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
 * FileName    		:SecurityGroupRightsListCtrl .java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-07-2011															*
 *                                                                  
 * Modified Date    :  2-08-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *2-08-2011	      Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securitygrouprights;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.service.administration.SecurityGroupService;
import com.pennant.webui.administration.securitygroup.model.SecurityGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityGroupRights/SecurityGroupRightsList.zul
 * file.
 */
public class SecurityGroupRightsListCtrl extends GFCBaseListCtrl<SecurityGroup> {
	private static final long serialVersionUID = -577256448245687404L;
	private static final Logger logger = Logger.getLogger(SecurityGroupRightsListCtrl.class);

	protected Window window_SecurityGroupRightsList;
	protected Borderlayout borderLayout_SecurityGroupList;
	private Paging pagingSecurityGroupList;
	private Listbox listBoxSecurityGroup;

	protected Listheader listheader_GrpCode;
	protected Listheader listheader_GrpDesc;
	protected Panel securityGroupSeekPanel;
	protected Panel securityGroupListPanel;

	protected Intbox grpID;
	protected Textbox grpCode;
	protected Textbox grpDesc;

	protected Listbox sortOperator_grpID;
	protected Listbox sortOperator_grpCode;
	protected Listbox sortOperator_grpDesc;

	protected Button button_SecurityGroupList_SecurityGroupSearchDialog;
	protected Button button_SecurityGroupList_PrintList;

	private transient SecurityGroupService securityGroupService;

	/**
	 * default constructor.<br>
	 */
	public SecurityGroupRightsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SecurityGroup";
		super.pageRightName = "SecurityGroupList";
		super.tableName = "SecGroups_View";
		super.queueTableName = "SecGroups_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onCreate$window_SecurityGroupRightsList(Event event) {
		// Set the page level components.
		setPageComponents(window_SecurityGroupRightsList, borderLayout_SecurityGroupList, listBoxSecurityGroup,
				pagingSecurityGroupList);
		setItemRender(new SecurityGroupListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SecurityGroupList_SecurityGroupSearchDialog);

		registerField("grpID");
		registerField("grpCode", listheader_GrpCode, SortOrder.ASC, grpCode, sortOperator_grpCode, Operators.STRING);
		registerField("grpDesc", listheader_GrpDesc, SortOrder.NONE, grpDesc, sortOperator_grpDesc, Operators.STRING);

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
	public void onClick$button_SecurityGroupList_SecurityGroupSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSecurityGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSecurityGroup.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		SecurityGroup aSecurityGroup = securityGroupService.getSecurityGroupById(id);

		if (aSecurityGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GrpID=" + aSecurityGroup.getGrpID() + " AND version=" + aSecurityGroup.getVersion()
				+ " ";

		if (doCheckAuthority(aSecurityGroup, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aSecurityGroup.getWorkflowId() == 0) {
				aSecurityGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aSecurityGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSecurityGroup
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SecurityGroup aSecurityGroup) {
		logger.debug("Entering");

		Map<String, Object> aruments = getDefaultArguments();
		aruments.put("securityGroup", aSecurityGroup);
		aruments.put("securityGroupListCtrl", this);
		aruments.put("newRecord", aSecurityGroup.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecurityGroupRights"
					+ "/SecurityGroupRightsDialog.zul", null, aruments);
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

	public void setSecurityGroupService(SecurityGroupService securityGroupService) {
		this.securityGroupService = securityGroupService;
	}
}
