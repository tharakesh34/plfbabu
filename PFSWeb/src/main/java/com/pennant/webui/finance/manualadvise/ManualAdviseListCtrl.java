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
 * * FileName : ManualAdviseListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified
 * Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.manualadvise;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.webui.finance.manualadvise.model.ManualAdviseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.finance/ManualAdvise/ManualAdviseList.zul file.
 * 
 */
public class ManualAdviseListCtrl extends GFCBaseListCtrl<ManualAdvise> {
	private static final long serialVersionUID = 1L;

	protected Window window_ManualAdviseList;
	protected Borderlayout borderLayout_ManualAdviseList;
	protected Paging pagingManualAdviseList;
	protected Listbox listBoxManualAdvise;

	// List headers
	protected Listheader listheader_AdviseType;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_FeeTypeID;

	// checkRights
	protected Button button_ManualAdviseList_NewManualAdvise;
	protected Button button_ManualAdviseList_ManualAdviseSearch;

	// Search Fields
	protected Combobox adviseType; // autowired
	protected Textbox finReference; // autowired
	protected Textbox feeTypeID; // autowired

	protected Listbox sortOperator_AdviseType;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_FeeTypeID;
	protected Listheader listheader_AdviseStatus;

	private transient ManualAdviseService manualAdviseService;
	private List<ValueLabel> listAdviseType = AdviseType.getList();
	private FinanceMain financeMain = null;
	private String module = null;
	private String menuItemName = null;

	/**
	 * default constructor.<br>
	 */
	public ManualAdviseListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		if (arguments.containsKey("module")) {
			this.module = (String) arguments.get("module");
		}

		if (PennantConstants.MANUALADVISE_CREATE_MODULE.equals(this.module)
				|| PennantConstants.MANUALADVISE_ENQUIRY_MODULE.equals(this.module)) {
			super.moduleCode = "ManualAdvise";
			super.pageRightName = "ManualAdviseList";
			super.tableName = "ManualAdvise";
			super.queueTableName = "ManualAdvise_TView";
			super.enquiryTableName = "MANUALADVISE_LVIEW";
		} else if (PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module)) {
			super.moduleCode = "ManualAdvise";
			super.pageRightName = "ManualAdviseList";
			super.tableName = "ManualAdvise";
			super.queueTableName = "ManualAdvise_CView";
		} else if (PennantConstants.MANUALADVISE_MAINTAIN_MODULE.equals(this.module)) {
			super.moduleCode = "ManualAdvise";
			super.pageRightName = "ManualAdviseList";
			super.tableName = "ManualAdvise";
			super.queueTableName = "ManualAdvise_CView";
		}
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		String whereClause = "";
		if (!enqiryModule) {
			this.searchObject.addFilter(new Filter("BounceID", 0, Filter.OP_EQUAL));
			whereClause = "(Status is NULL) OR (Status in ('" + PennantConstants.MANUALADVISE_MAINTAIN + "'))";

			if (PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module)) {
				whereClause = whereClause + " OR (RecordStatus not in ('" + PennantConstants.RCD_STATUS_APPROVED
						+ "'))";

				this.searchObject.addWhereClause(whereClause);
				this.searchObject.addFilter(new Filter("ValueDate", SysParamUtil.getAppDate(), Filter.OP_GREATER_THAN));
			}
			if (PennantConstants.MANUALADVISE_MAINTAIN_MODULE.equals(this.module)) {
				this.searchObject.addWhereClause(whereClause);
				this.searchObject.addFilter(new Filter("ValueDate", SysParamUtil.getAppDate(), Filter.OP_GREATER_THAN));
			}
		} else {
			this.searchObject.addFilter(new Filter("FeeTypeId", 0, Filter.OP_NOT_EQUAL));
			this.help.setVisible(false);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ManualAdviseList(Event event) {
		logger.debug(Literal.ENTERING);

		menuItemName = getMenuItemName(event, menuItemName);

		// Set the page level components.
		setPageComponents(window_ManualAdviseList, borderLayout_ManualAdviseList, listBoxManualAdvise,
				pagingManualAdviseList);
		setItemRender(new ManualAdviseListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ManualAdviseList_ManualAdviseSearch);
		registerButton(button_ManualAdviseList_NewManualAdvise, "button_ManualAdviseList_NewManualAdvise", true);

		registerField("adviseID");
		registerField("adviseType", listheader_AdviseType, SortOrder.NONE, adviseType, sortOperator_AdviseType,
				Operators.SIMPLE_NUMARIC);
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("feeTypeDesc", listheader_FeeTypeID, SortOrder.NONE, feeTypeID, sortOperator_FeeTypeID,
				Operators.STRING);

		if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			listheader_AdviseStatus.setVisible(true);
			registerField("status", listheader_AdviseStatus);
		}
		// comboBox list
		fillComboBox(adviseType, null, listAdviseType, "");
		// Render the page and display the data.
		doRenderPage();

		doCheckRights();

		search();
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.button_ManualAdviseList_NewManualAdvise
				.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseList_NewManualAdvise"));

		if (PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module)
				|| PennantConstants.MANUALADVISE_MAINTAIN_MODULE.equals(this.module)
				|| (PennantConstants.MANUALADVISE_ENQUIRY_MODULE.equals(this.module))) {
			this.button_ManualAdviseList_NewManualAdvise.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ManualAdviseList_ManualAdviseSearch(Event event) {
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
	public void onClick$button_ManualAdviseList_NewManualAdvise(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ManualAdvise manualadvise = new ManualAdvise();
		manualadvise.setNewRecord(true);
		manualadvise.setWorkflowId(getWorkFlowId());
		manualadvise.setValueDate(SysParamUtil.getAppDate());
		// Display the dialog page.
		doShowDialogPage(manualadvise);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onManualAdviseItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxManualAdvise.getSelectedItem();
		final long adviseID = (long) selectedItem.getAttribute("adviseID");
		ManualAdvise manualadvise = manualAdviseService.getManualAdviseById(adviseID);

		if (manualadvise == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		financeMain = manualAdviseService.getFinanceDetails(manualadvise.getFinID());

		StringBuilder whereCond = new StringBuilder();
		whereCond.append(" where  AdviseID =?");

		if (doCheckAuthority(manualadvise, whereCond.toString(), new Object[] { manualadvise.getAdviseID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && manualadvise.getWorkflowId() == 0) {
				manualadvise.setWorkflowId(getWorkFlowId());
			}

			logUserAccess(menuItemName, manualadvise.getFinReference(), moduleCode);
			doShowDialogPage(manualadvise);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param manualadvise The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ManualAdvise manualadvise) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("manualAdvise", manualadvise);
		arg.put("manualAdviseListCtrl", this);
		arg.put("module", this.module);

		try {

			if (manualadvise.isNewRecord()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/ManualAdvise/SelectManualAdviseFinReferenceDialog.zul", null,
						arg);
			} else {
				arg.put("financeMain", financeMain);
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManualAdvise/ManualAdviseDialog.zul",
						null, arg);
			}
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

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}
}