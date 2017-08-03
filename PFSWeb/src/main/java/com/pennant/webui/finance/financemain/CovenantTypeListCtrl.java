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
package com.pennant.webui.finance.financemain;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.webui.systemmasters.covenant.model.CovenantListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/CovenantType.zul file.
 */
public class CovenantTypeListCtrl extends GFCBaseListCtrl<FinCovenantType> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(CovenantTypeListCtrl.class);

	protected Window window_CovenantTypeList;
	protected Borderlayout borderLayout_CovenantList;
	protected Listbox listBoxCovenant;
	protected Paging pagingCovenantList;

	protected Listheader listheader_CovenantReference;
	protected Listheader listheader_CovenantType;
	protected Listheader listheader_ALwWaiver;
	protected Listheader listheader_PostDoc;

	protected Button button_CovenantList_NewCovenant;
	protected Button button_CovenantList_CovenantSearchDialog;

	protected Textbox covenantReference;
	protected Textbox covenantType;
	protected Checkbox alwWaiver;
	protected Checkbox postDoc;

	protected Listbox sortOperator_CovenantReference;
	protected Listbox sortOperator_CovenantType;
	protected Listbox sortOperator_alwaiver;
	protected Listbox sortOperator_postDoc;

	private transient FinCovenantTypeService finCovenantTypeService;

	/**
	 * The default constructor.
	 */
	public CovenantTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinCovenantTypes";
		super.pageRightName = "FinCovenantTypeList";
		super.tableName = "FinCovenantType_AView";
		super.queueTableName = "FinCovenantType_View";
		super.enquiryTableName = "FinCovenantType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CovenantTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_CovenantTypeList, borderLayout_CovenantList, listBoxCovenant, pagingCovenantList);
		setItemRender(new CovenantListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CovenantList_NewCovenant, "button_CovenantList_NewCovenant", true);
		registerButton(button_CovenantList_CovenantSearchDialog);

		//registerField("finReference");
		registerField("finReference", listheader_CovenantReference, SortOrder.ASC, covenantReference,
				sortOperator_CovenantReference, Operators.STRING);
		registerField("covenantType", listheader_CovenantType, SortOrder.ASC, covenantType,
				sortOperator_CovenantType, Operators.STRING);
		registerField("alwWaiver", listheader_ALwWaiver, SortOrder.NONE, alwWaiver, sortOperator_alwaiver,
				Operators.BOOLEAN);
		registerField("alwPostpone", listheader_PostDoc, SortOrder.NONE, postDoc, sortOperator_postDoc,
				Operators.BOOLEAN);
		registerField("covenantTypeDesc");

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
	public void onClick$button_CovenantList_CovenantSearchDialog(Event event) {
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
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_CovenantList_NewCovenant(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinCovenantType finCovenant = new FinCovenantType();
		finCovenant.setNewRecord(true);
		finCovenant.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(finCovenant);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCovenantItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCovenant.getSelectedItem();

		// Get the selected entity.
		FinCovenantType finCovenantType = (FinCovenantType) selectedItem.getAttribute("data");
		finCovenantType=getFinCovenantTypeService().getFinCovenantTypeById(finCovenantType.getFinReference(),finCovenantType.getCovenantType(),"_View");
		

		if (finCovenantType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinReference='" + finCovenantType.getFinReference()+ "' AND version=" + finCovenantType.getVersion()
				+ " ";

		if (doCheckAuthority(finCovenantType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && finCovenantType.getWorkflowId() == 0) {
				finCovenantType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finCovenantType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFinCovenant
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinCovenantType aFinCovenant) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("fincovenant", aFinCovenant);
		arg.put("covenantListCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CovenantTypeDialog.zul", null, arg);
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

	public FinCovenantTypeService getFinCovenantTypeService() {
		return finCovenantTypeService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

}