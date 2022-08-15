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
 * * FileName : ClusterHierarcheyListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * *
 * Modified Date : 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.clusterhierarchey;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.service.applicationmaster.ClusterHierarchyService;
import com.pennant.webui.applicationmaster.clusterhierarchey.model.ClusterHierarcheyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ClusterHierarchey/ClusterHierarcheyList.zul
 * file.
 * 
 */
public class ClusterHierarcheyListCtrl extends GFCBaseListCtrl<ClusterHierarchy> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ClusterHierarcheyListCtrl.class);

	protected Window window_ClusterHierarcheyList;
	protected Borderlayout borderLayout_ClusterHierarcheyList;
	protected Paging pagingClusterHierarcheyList;
	protected Listbox listBoxClusterHierarchey;

	// List headers
	protected Listheader listheader_Entity;
	// checkRights
	protected Button button_ClusterHierarcheyList_NewClusterHierarchey;
	protected Button button_ClusterHierarcheyList_ClusterHierarcheySearch;

	// Search Fields
	protected Textbox entity; // autowired
	protected Listbox sortOperator_Entity;

	private transient ClusterHierarchyService clusterHierarchyService;

	/**
	 * default constructor.<br>
	 */
	public ClusterHierarcheyListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ClusterHierarchy";
		super.pageRightName = "ClusterHierarcheyList";
		super.tableName = "cluster_hierarchy_lview";
		super.queueTableName = "cluster_hierarchy_lview";
		super.enquiryTableName = "cluster_hierarchy_lview";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ClusterHierarcheyList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ClusterHierarcheyList, borderLayout_ClusterHierarcheyList, listBoxClusterHierarchey,
				pagingClusterHierarcheyList);
		setItemRender(new ClusterHierarcheyListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ClusterHierarcheyList_ClusterHierarcheySearch);
		registerButton(button_ClusterHierarcheyList_NewClusterHierarchey,
				"button_ClusterHierarcheyList_NewClusterHierarchey", true);

		registerField("entity", listheader_Entity, SortOrder.ASC, entity, sortOperator_Entity, Operators.STRING);
		// registerField("clusterType");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ClusterHierarcheyList_ClusterHierarcheySearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ClusterHierarcheyList_NewClusterHierarchey(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ClusterHierarchy item = new ClusterHierarchy();
		item.setNewRecord(true);
		item.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(item);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.1
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onClusterHierarcheyItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxClusterHierarchey.getSelectedItem();
		final String entity = (String) selectedItem.getAttribute("entity");
		ClusterHierarchy hierarchey = new ClusterHierarchy();

		hierarchey.setEntity(entity);

		List<ClusterHierarchy> hierarcheyList = clusterHierarchyService
				.getClusterHierarcheyList(hierarchey.getEntity());
		hierarchey.setClusterTypes(hierarcheyList);
		if (!hierarcheyList.isEmpty()) {
			hierarchey.setVersion(hierarcheyList.get(0).getVersion());
			hierarchey.setLastMntBy(hierarcheyList.get(0).getLastMntBy());
			hierarchey.setLastMntOn(hierarcheyList.get(0).getLastMntOn());
			hierarchey.setRecordStatus(hierarcheyList.get(0).getRecordStatus());
			hierarchey.setRoleCode(hierarcheyList.get(0).getRoleCode());
			hierarchey.setNextRoleCode(hierarcheyList.get(0).getNextRoleCode());
			hierarchey.setTaskId(hierarcheyList.get(0).getTaskId());
			hierarchey.setNextTaskId(hierarcheyList.get(0).getNextTaskId());
			hierarchey.setRecordType(hierarcheyList.get(0).getRecordType());
			hierarchey.setWorkflowId(hierarcheyList.get(0).getWorkflowId());
		}

		if (CollectionUtils.isEmpty(hierarcheyList)) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		// Check whether the user has authority to change/view the record.
		String whereCond = " where  Entity = ?";

		if (doCheckAuthority(hierarchey, whereCond, new Object[] { hierarchey.getEntity() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && hierarchey.getWorkflowId() == 0) {
				hierarchey.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(hierarchey);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param clusterhierarchey The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ClusterHierarchy clusterhierarchey) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("clusterHierarchey", clusterhierarchey);
		arg.put("clusterHierarcheyListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/ClusterHierarchey/ClusterHierarcheyDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
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

	public void setClusterHierarchyService(ClusterHierarchyService clusterHierarchyService) {
		this.clusterHierarchyService = clusterHierarchyService;
	}

}