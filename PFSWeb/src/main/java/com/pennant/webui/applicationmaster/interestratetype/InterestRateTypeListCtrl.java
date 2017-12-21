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
 * FileName    		:  InterestRateTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.interestratetype;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.service.applicationmaster.InterestRateTypeService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.applicationmaster.interestratetype.model.InterestRateTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/InterestRateType/InterestRateTypeList.zul file.
 */
public class InterestRateTypeListCtrl extends GFCBaseListCtrl<InterestRateType> {
	private static final long serialVersionUID = 4676258087775088404L;
	private static final Logger logger = Logger.getLogger(InterestRateTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_InterestRateTypeList;
	protected Borderlayout borderLayout_InterestRateTypeList;
	protected Paging pagingInterestRateTypeList;
	protected Listbox listBoxInterestRateType;

	protected Combobox intRateTypeCode;
	protected Textbox intRateTypeDesc;
	protected Checkbox intRateTypeIsActive;

	protected Listbox sortOperator_intRateTypeCode;
	protected Listbox sortOperator_intRateTypeDesc;
	protected Listbox sortOperator_intRateTypeIsActive;

	// List headers
	protected Listheader listheader_IntRateTypeCode;
	protected Listheader listheader_IntRateTypeDesc;
	protected Listheader listheader_IntRateTypeIsActive;

	// checkRights
	protected Button button_InterestRateTypeList_NewInterestRateType;
	protected Button button_InterestRateTypeList_InterestRateTypeSearchDialog;

	private transient InterestRateTypeService interestRateTypeService;

	/**
	 * default constructor.<br>
	 */
	public InterestRateTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InterestRateType";
		super.pageRightName = "InterestRateTypeList";
		super.tableName = "BMTInterestRateTypes_AView";
		super.queueTableName = "BMTInterestRateTypes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_InterestRateTypeList(Event event) {

		// Set the page level components.
		setPageComponents(window_InterestRateTypeList, borderLayout_InterestRateTypeList, listBoxInterestRateType,
				pagingInterestRateTypeList);
		setItemRender(new InterestRateTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InterestRateTypeList_NewInterestRateType, RIGHT_NOT_ACCESSIBLE, true);
		registerButton(button_InterestRateTypeList_InterestRateTypeSearchDialog);

		fillComboBox(this.intRateTypeCode, "", PennantStaticListUtil.getInterestRateType(true), "");

		registerField("intRateTypeCode", listheader_IntRateTypeCode, SortOrder.ASC, intRateTypeCode,
				sortOperator_intRateTypeCode, Operators.STRING);
		registerField("intRateTypeDesc", listheader_IntRateTypeDesc, SortOrder.NONE, intRateTypeDesc,
				sortOperator_intRateTypeDesc, Operators.STRING);
		registerField("intRateTypeIsActive", listheader_IntRateTypeIsActive, SortOrder.NONE, intRateTypeIsActive,
				sortOperator_intRateTypeIsActive, Operators.BOOLEAN);

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
	public void onClick$button_InterestRateTypeList_InterestRateTypeSearchDialog(Event event) {
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
	public void onClick$button_InterestRateTypeList_NewInterestRateType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		InterestRateType interestRateType = new InterestRateType();
		interestRateType.setNewRecord(true);
		interestRateType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(interestRateType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onInterestRateTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxInterestRateType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		InterestRateType interestRateType = interestRateTypeService.getInterestRateTypeById(id);

		if (interestRateType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND IntRateTypeCode='" + interestRateType.getIntRateTypeCode() + "' AND version="
				+ interestRateType.getVersion() + " ";

		if (doCheckAuthority(interestRateType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && interestRateType.getWorkflowId() == 0) {
				interestRateType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(interestRateType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aInterestRateType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(InterestRateType aInterestRateType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("interestRateType", aInterestRateType);
		arg.put("interestRateTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/InterestRateType/InterestRateTypeDialog.zul",
					null, arg);
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

	public void setInterestRateTypeService(InterestRateTypeService interestRateTypeService) {
		this.interestRateTypeService = interestRateTypeService;
	}
}