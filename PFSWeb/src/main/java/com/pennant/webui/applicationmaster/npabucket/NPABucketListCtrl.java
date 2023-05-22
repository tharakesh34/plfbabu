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
 * * FileName : NPABucketListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified
 * Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.npabucket;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.service.applicationmaster.NPABucketService;
import com.pennant.webui.applicationmaster.npabucket.model.NPABucketListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/NPABucket/NPABucketList.zul file.
 * 
 */
public class NPABucketListCtrl extends GFCBaseListCtrl<NPABucket> {
	private static final long serialVersionUID = 1L;

	protected Window window_NPABucketList;
	protected Borderlayout borderLayout_NPABucketList;
	protected Paging pagingNPABucketList;
	protected Listbox listBoxNPABucket;

	// List headers
	protected Listheader listheader_BucketCode;
	protected Listheader listheader_BucketDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_NPABucketList_NewNPABucket;
	protected Button button_NPABucketList_NPABucketSearch;

	// Search Fields
	protected Textbox bucketCode; // autowired
	protected Textbox bucketDesc; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_BucketCode;
	protected Listbox sortOperator_BucketDesc;
	protected Listbox sortOperator_Active;

	private transient NPABucketService nPABucketService;

	/**
	 * default constructor.<br>
	 */
	public NPABucketListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "NPABucket";
		super.pageRightName = "NPABucketList";
		super.tableName = "NPABUCKETS";
		super.queueTableName = "NPABUCKETS_View";
		super.enquiryTableName = "NPABUCKETS_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NPABucketList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_NPABucketList, borderLayout_NPABucketList, listBoxNPABucket, pagingNPABucketList);
		setItemRender(new NPABucketListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_NPABucketList_NPABucketSearch);
		registerButton(button_NPABucketList_NewNPABucket, "button_NPABucketList_NewNPABucket", true);

		registerField("bucketID");
		registerField("bucketCode", listheader_BucketCode, SortOrder.NONE, bucketCode, sortOperator_BucketCode,
				Operators.STRING);
		registerField("bucketDesc", listheader_BucketDesc, SortOrder.NONE, bucketDesc, sortOperator_BucketDesc,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_NPABucketList_NPABucketSearch(Event event) {
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
	public void onClick$button_NPABucketList_NewNPABucket(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		NPABucket npabucket = new NPABucket();
		npabucket.setNewRecord(true);
		npabucket.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(npabucket);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onNPABucketItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxNPABucket.getSelectedItem();
		final long bucketID = (long) selectedItem.getAttribute("bucketID");
		NPABucket npabucket = nPABucketService.getNPABucket(bucketID);

		if (npabucket == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  BucketID =? ");

		if (doCheckAuthority(npabucket, whereCond.toString(), new Object[] { npabucket.getBucketID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && npabucket.getWorkflowId() == 0) {
				npabucket.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(npabucket);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param npabucket The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(NPABucket npabucket) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("npabucket", npabucket);
		arg.put("npabucketListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/NPABucket/NPABucketDialog.zul", null, arg);
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

	public void setNPABucketService(NPABucketService npabucketService) {
		this.nPABucketService = npabucketService;
	}
}