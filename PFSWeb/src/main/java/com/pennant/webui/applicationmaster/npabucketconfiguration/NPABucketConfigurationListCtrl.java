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
 * * FileName : NPABucketConfigurationListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 *
 * * Modified Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.npabucketconfiguration;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.service.applicationmaster.NPABucketConfigurationService;
import com.pennant.webui.applicationmaster.npabucketconfiguration.model.NPABucketConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.applicationmaster/NPABucketConfiguration/NPABucketConfigurationList.zul file.
 * 
 */
public class NPABucketConfigurationListCtrl extends GFCBaseListCtrl<NPABucketConfiguration> {
	private static final long serialVersionUID = 1L;

	protected Window window_NPABucketConfigurationList;
	protected Borderlayout borderLayout_NPABucketConfigurationList;
	protected Paging pagingNPABucketConfigurationList;
	protected Listbox listBoxNPABucketConfiguration;

	// List headers
	protected Listheader listheader_ProductCode;
	protected Listheader listheader_BucketID;
	protected Listheader listheader_DueDays;

	// checkRights
	protected Button button_NPABucketConfigurationList_NewNPABucketConfiguration;
	protected Button button_NPABucketConfigurationList_NPABucketConfigurationSearch;

	// Search Fields
	protected Textbox productCode; // autowired
	protected Textbox bucketID; // autowired
	protected Intbox dueDays; // autowired
	protected Checkbox suspendProfit; // autowired

	protected Listbox sortOperator_ProductCode;
	protected Listbox sortOperator_BucketID;
	protected Listbox sortOperator_DueDays;
	protected Listbox sortOperator_SuspendProfit;

	private transient NPABucketConfigurationService nPABucketConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public NPABucketConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "NPABucketConfiguration";
		super.pageRightName = "NPABucketConfigurationList";
		super.tableName = "NPABUCKETSCONFIG_AView";
		super.queueTableName = "NPABUCKETSCONFIG_View";
		super.enquiryTableName = "NPABUCKETSCONFIG_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NPABucketConfigurationList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_NPABucketConfigurationList, borderLayout_NPABucketConfigurationList,
				listBoxNPABucketConfiguration, pagingNPABucketConfigurationList);
		setItemRender(new NPABucketConfigurationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_NPABucketConfigurationList_NPABucketConfigurationSearch);
		registerButton(button_NPABucketConfigurationList_NewNPABucketConfiguration,
				"button_NPABucketConfigurationList_NewNPABucketConfiguration", true);

		registerField("configID");
		registerField("productCode", listheader_ProductCode, SortOrder.NONE, productCode, sortOperator_ProductCode,
				Operators.STRING);
		registerField("bucketCode", listheader_BucketID, SortOrder.NONE, bucketID, sortOperator_BucketID,
				Operators.NUMERIC);
		registerField("dueDays", listheader_DueDays, SortOrder.NONE, dueDays, sortOperator_DueDays, Operators.NUMERIC);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_NPABucketConfigurationList_NPABucketConfigurationSearch(Event event) {
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
	public void onClick$button_NPABucketConfigurationList_NewNPABucketConfiguration(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		NPABucketConfiguration npabucketconfiguration = new NPABucketConfiguration();
		npabucketconfiguration.setNewRecord(true);
		npabucketconfiguration.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(npabucketconfiguration);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onNPABucketConfigurationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxNPABucketConfiguration.getSelectedItem();
		final long configID = (long) selectedItem.getAttribute("configID");
		NPABucketConfiguration npabucketconfiguration = nPABucketConfigurationService
				.getNPABucketConfiguration(configID);

		if (npabucketconfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  ConfigID =? ");

		if (doCheckAuthority(npabucketconfiguration, whereCond.toString(),
				new Object[] { npabucketconfiguration.getConfigID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && npabucketconfiguration.getWorkflowId() == 0) {
				npabucketconfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(npabucketconfiguration);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param npabucketconfiguration The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(NPABucketConfiguration npabucketconfiguration) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("npabucketconfiguration", npabucketconfiguration);
		arg.put("npabucketconfigurationListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/NPABucketConfiguration/NPABucketConfigurationDialog.zul", null,
					arg);
		} catch (Exception e) {
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

	public void setNPABucketConfigurationService(NPABucketConfigurationService npabucketconfigurationService) {
		this.nPABucketConfigurationService = npabucketconfigurationService;
	}
}