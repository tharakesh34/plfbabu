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
 * * FileName : ClusterListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * * Modified Date
 * : 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.cluster;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.webui.applicationmaster.cluster.model.ClusterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Cluster/ClusterList.zul file.
 * 
 */
public class ClusterListCtrl extends GFCBaseListCtrl<Cluster> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ClusterListCtrl.class);

	protected Window window_ClusterList;
	protected Borderlayout borderLayout_ClusterList;
	protected Paging pagingClusterList;
	protected Listbox listBoxCluster;

	// List headers
	protected Listheader listheader_Entity;
	protected Listheader listheader_Code;
	protected Listheader listheader_ClusterType;
	protected Listheader listheader_Parent;
	protected Listheader listheader_Name;
	protected Listheader listheader_ParentType;

	// checkRights
	protected Button button_ClusterList_NewCluster;
	protected Button button_ClusterList_ClusterSearch;

	// Search Fields
	protected Textbox entity;
	protected Textbox code;
	protected Textbox clusterType;
	protected Textbox name;
	protected Intbox parent;
	protected Intbox parentType;

	protected Listbox sortOperator_Entity;
	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_ClusterType;
	protected Listbox sortOperator_Parent;
	protected Listbox sortOperator_ParentType;
	protected Listbox sortOperator_Name;

	private transient ClusterService clusterService;

	/**
	 * default constructor.<br>
	 */
	public ClusterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Cluster";
		super.pageRightName = "ClusterList";
		super.tableName = "Clusters_AView";
		super.queueTableName = "Clusters_View";
		super.enquiryTableName = "Clusters_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ClusterList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ClusterList, borderLayout_ClusterList, listBoxCluster, pagingClusterList);
		setItemRender(new ClusterListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ClusterList_ClusterSearch);
		registerButton(button_ClusterList_NewCluster, "button_ClusterList_NewCluster", true);

		registerField("Id");
		registerField("EntityDesc");
		registerField("ParentCode");
		registerField("ParentName");
		registerField("entity", listheader_Entity, SortOrder.NONE, entity, sortOperator_Entity, Operators.STRING);
		registerField("clusterType", listheader_ClusterType, SortOrder.NONE, clusterType, sortOperator_ClusterType,
				Operators.NUMERIC);
		registerField("code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("name", listheader_Name, SortOrder.NONE, name, sortOperator_Name, Operators.STRING);
		registerField("parent", listheader_Parent, SortOrder.NONE, parent, sortOperator_Parent, Operators.NUMERIC);
		registerField("parentType");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ClusterList_ClusterSearch(Event event) {
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
	public void onClick$button_ClusterList_NewCluster(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Cluster cluster = new Cluster();
		cluster.setNewRecord(true);
		cluster.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(cluster);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onClusterItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCluster.getSelectedItem();
		final long clusterId = (long) selectedItem.getAttribute("clusterId");
		Cluster cluster = clusterService.getCluster(clusterId);

		if (cluster == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  ClusterId =? ");

		if (doCheckAuthority(cluster, whereCond.toString(), new Object[] { cluster.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && cluster.getWorkflowId() == 0) {
				cluster.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(cluster);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param cluster The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Cluster cluster) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("cluster", cluster);
		arg.put("clusterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Cluster/ClusterDialog.zul", null, arg);
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

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}
}