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
 * FileName    		:  ExpenseTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.expensetype;

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

import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.service.amtmasters.ExpenseTypeService;
import com.pennant.webui.amtmasters.expensetype.model.ExpenseTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/ExpenseType/ExpenseTypeList.zul file.
 */
public class ExpenseTypeListCtrl extends GFCBaseListCtrl<ExpenseType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ExpenseTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExpenseTypeList;
	protected Borderlayout borderLayout_ExpenseTypeList;
	protected Paging pagingExpenseTypeList;
	protected Listbox listBoxExpenseType;

	protected Listheader listheader_ExpenceTypeCode;
	protected Listheader listheader_ExpenceTypeDesc;
	protected Listheader listheader_Active;

	protected Button button_ExpenseTypeList_NewExpenseType;
	protected Button button_ExpenseTypeList_ExpenseTypeSearchDialog;

	protected Textbox expenceTypeCode;
	protected Textbox expenceTypeDesc;
	protected Checkbox active;

	protected Listbox sortOperator_expenceTypeCode;
	protected Listbox sortOperator_expenceTypeDesc;
	protected Listbox sortOperator_Active;

	private transient ExpenseTypeService expenseTypeService;

	/**
	 * default constructor.<br>
	 */
	public ExpenseTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ExpenseType";
		super.pageRightName = "ExpenseTypeList";
		super.tableName = "ExpenseTypes_AView";
		super.queueTableName = "ExpenseTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExpenseTypeList(Event event) {
		// Set the page level components.
		setPageComponents(window_ExpenseTypeList, borderLayout_ExpenseTypeList, listBoxExpenseType,
				pagingExpenseTypeList);
		setItemRender(new ExpenseTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ExpenseTypeList_NewExpenseType, "button_ExpenseTypeList_NewExpenseType", true);
		registerButton(button_ExpenseTypeList_ExpenseTypeSearchDialog);

		registerField("expenseTypeId");
		registerField("expenseTypeCode", listheader_ExpenceTypeCode, SortOrder.ASC, expenceTypeCode,
				sortOperator_expenceTypeCode, Operators.STRING);
		registerField("expenseTypeDesc", listheader_ExpenceTypeDesc, SortOrder.NONE, expenceTypeDesc,
				sortOperator_expenceTypeDesc, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

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
	public void onClick$button_ExpenseTypeList_ExpenseTypeSearchDialog(Event event) {
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
	public void onClick$button_ExpenseTypeList_NewExpenseType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ExpenseType expenseType = new ExpenseType();
		expenseType.setNewRecord(true);
		expenseType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(expenseType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onExpenseTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxExpenseType.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		ExpenseType expenseType = expenseTypeService.getExpenseTypeById(id);

		if (expenseType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where ExpenceTypeId=?";

		if (doCheckAuthority(expenseType, whereCond, new Object[] { expenseType.getExpenseTypeId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && expenseType.getWorkflowId() == 0) {
				expenseType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(expenseType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param expenseType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ExpenseType expenseType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("expenseType", expenseType);
		arg.put("expenseTypeListCtrl", this);
		arg.put("newRecord", expenseType.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/ExpenseType/ExpenseTypeDialog.zul", null, arg);
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

	public void setExpenseTypeService(ExpenseTypeService expenseTypeService) {
		this.expenseTypeService = expenseTypeService;
	}

}