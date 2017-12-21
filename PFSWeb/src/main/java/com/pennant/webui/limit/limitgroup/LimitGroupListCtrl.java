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
 * FileName    		:  LimitGroupListCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-03-2016    														*
 *                                                                  						*
 * Modified Date    :  31-03-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-03-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.limit.limitgroup;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.service.limit.LimitGroupService;
import com.pennant.webui.limit.limitgroup.model.LimitGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Limit/LimitGroup/LimitGroupList.zul
 * file.<br>
 * ************************************************************<br>
 * 
 */
public class LimitGroupListCtrl extends GFCBaseListCtrl<LimitGroup> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LimitGroupListCtrl.class);

	/*
	 * ************************************************************************	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ************************************************************************
	 */
	protected Window 							window_LimitGroupList; 
	protected Borderlayout 						borderLayout_LimitGroupList; 
	protected Paging 							pagingLimitGroupList; 
	protected Listbox 							listBoxLimitGroup; 

	// List headers
	protected Listheader 						listheader_GroupCode; 
	protected Listheader 						listheader_GroupName; 
	protected Listheader						listheader_Active;
	protected Listheader						listheader_GroupCategory;

	// checkRights
	protected Button 							button_LimitGroupList_NewLimitGroup; 
	protected Button 							button_LimitGroupList_LimitGroupSearch; 
	protected Button 							button_LimitGroupList_PrintList; 
	protected Label  							label_LimitGroupList_RecordStatus; 							
	protected Label  							label_LimitGroupList_RecordType; 							

	// NEEDED for the ReUse in the SearchWindow
	private transient LimitGroupService 		limitGroupService;

	protected Textbox 							groupCode; 
	protected Listbox 							sortOperator_GroupCode; 

	protected Textbox 							groupName; 
	protected Listbox 							sortOperator_GroupName; 
	
	protected Textbox 							groupCategory; 
	protected Listbox 							sortOperator_GroupCategory; 
	
	protected Listbox 							sortOperator_active;
	protected Checkbox 							active;


	/**
	 * default constructor.<br>
	 */
	public LimitGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LimitGroup";
		super.pageRightName = "LimitGroupList";
		super.tableName = "LimitGroup_AView";
		super.queueTableName = "LimitGroup_View";
		super.enquiryTableName = "LimitGroup_AView";
	}

	// ***************************************************//
	// *************** Component Events ******************//
	// ***************************************************//

	public void onCreate$window_LimitGroupList(Event event) throws Exception {
		logger.debug("Entering");


		// Set the page level components.
		setPageComponents(window_LimitGroupList, borderLayout_LimitGroupList, listBoxLimitGroup, pagingLimitGroupList);
		setItemRender(new LimitGroupListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LimitGroupList_NewLimitGroup, "button_LimitGroupList_NewLimitGroup", true);
		registerButton(button_LimitGroupList_LimitGroupSearch);


		registerField("groupCode", listheader_GroupCode, SortOrder.ASC, groupCode,
				sortOperator_GroupCode, Operators.STRING);
		registerField("groupName", listheader_GroupName, SortOrder.ASC, groupName,
				sortOperator_GroupName, Operators.STRING);
		registerField("limitCategory", listheader_GroupCategory, SortOrder.ASC, groupCategory,
				sortOperator_GroupCategory, Operators.STRING);
		
		registerField("active", listheader_Active, SortOrder.NONE, active,
				sortOperator_active, Operators.BOOLEAN);


		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * Invoke Search
	 */
	public void onClick$button_LimitGroupList_LimitGroupSearch(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		search();
		logger.debug("Leaving" +event.toString());
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
	 * Call the LimitGroup dialog with a new empty entry. <br>
	 */
	public void onClick$button_LimitGroupList_NewLimitGroup(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new LimitGroup object, We GET it from the backend.

		LimitGroup limitGroup = new LimitGroup();
		limitGroup.setNewRecord(true);
		limitGroup.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(limitGroup);
		logger.debug("Leaving" +event.toString());


	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.limit.limitgroup.model.LimitGroupListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onLimitGroupItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// Get the selected record.
		Listitem selectedItem = this.listBoxLimitGroup.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		LimitGroup limitGroup = limitGroupService.getLimitGroupById(id);

		if (limitGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GroupCode='" + limitGroup.getGroupCode()+ "'  AND version="+ limitGroup.getVersion() + " ";

		if (doCheckAuthority(limitGroup, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && limitGroup.getWorkflowId() == 0) {
				limitGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(limitGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}


		logger.debug("Leaving" +event.toString());
	}


	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param limitGroup
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LimitGroup limitGroup) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("limitGroup", limitGroup);
		arg.put("limitGroupListCtrl", this);
		arg.put("enqiryModule", super.enqiryModule);


		try {
			Executions.createComponents("/WEB-INF/pages/Limit/LimitGroup/LimitGroupDialog.zul", null, arg);
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
	public void onClick$btnHelp(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}



	// ******************************************************//
	// ****************** getter / setter  ******************//
	// ******************************************************//

	public void setLimitGroupService(LimitGroupService limitGroupService) {
		this.limitGroupService = limitGroupService;
	}

}