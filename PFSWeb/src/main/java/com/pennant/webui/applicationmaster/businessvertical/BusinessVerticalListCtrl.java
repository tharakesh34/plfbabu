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
 * * FileName : BusinessVerticalListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2018 * *
 * Modified Date : 14-12-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.businessvertical;

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

import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.service.applicationmaster.BusinessVerticalService;
import com.pennant.webui.applicationmaster.businessvertical.model.BusinessVerticalListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BusinessVertical/BusinessVerticalList.zul file.
 * 
 */
public class BusinessVerticalListCtrl extends GFCBaseListCtrl<BusinessVertical> {
	private static final long serialVersionUID = 1L;

	protected Window window_BusinessVerticalList;
	protected Borderlayout borderLayout_BusinessVerticalList;
	protected Paging pagingBusinessVerticalList;
	protected Listbox listBoxBusinessVertical;

	// List headers
	protected Listheader listheader_code;
	protected Listheader listheader_description;
	protected Listheader listheader_active;

	// checkRights
	protected Button button_BusinessVerticalList_NewBusinessVertical;
	protected Button button_BusinessVerticalList_BusinessVerticalSearch;

	// Search Fields
	protected Textbox code; // autowired
	protected Textbox description; // autowired
	protected Checkbox active;

	protected Listbox sortOperator_code;
	protected Listbox sortOperator_description;
	protected Listbox sortOperator_active;

	private transient BusinessVerticalService businessVerticalService;

	/**
	 * default constructor.<br>
	 */
	public BusinessVerticalListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BusinessVertical";
		super.pageRightName = "BusinessVerticalList";
		super.tableName = "business_vertical_AView";
		super.queueTableName = "business_vertical_View";
		super.enquiryTableName = "business_vertical_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BusinessVerticalList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_BusinessVerticalList, borderLayout_BusinessVerticalList, listBoxBusinessVertical,
				pagingBusinessVerticalList);
		setItemRender(new BusinessVerticalListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BusinessVerticalList_BusinessVerticalSearch);
		registerButton(button_BusinessVerticalList_NewBusinessVertical,
				"button_BusinessVerticalList_NewBusinessVertical", true);

		registerField("id");
		registerField("code", listheader_code, SortOrder.NONE, code, sortOperator_code, Operators.STRING);
		registerField("description", listheader_description, SortOrder.NONE, description, sortOperator_description,
				Operators.STRING);
		registerField("active", listheader_active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_BusinessVerticalList_BusinessVerticalSearch(Event event) {
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
	public void onClick$button_BusinessVerticalList_NewBusinessVertical(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		BusinessVertical businessvertical = new BusinessVertical();
		businessvertical.setNewRecord(true);
		businessvertical.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(businessvertical);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBusinessVerticalItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBusinessVertical.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		BusinessVertical businessvertical = businessVerticalService.getBusinessVertical(id);

		if (businessvertical == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =?");

		if (doCheckAuthority(businessvertical, whereCond.toString(), new Object[] { businessvertical.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && businessvertical.getWorkflowId() == 0) {
				businessvertical.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(businessvertical);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param businessvertical The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BusinessVertical businessvertical) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("businessVertical", businessvertical);
		arg.put("businessVerticalListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/BusinessVertical/BusinessVerticalDialog.zul",
					null, arg);
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

	public void setBusinessVerticalService(BusinessVerticalService businessVerticalService) {
		this.businessVerticalService = businessVerticalService;
	}
}