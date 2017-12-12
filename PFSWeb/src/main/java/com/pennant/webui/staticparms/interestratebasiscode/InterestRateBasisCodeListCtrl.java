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
 * FileName    		:  InterestRateBasisCodeListCtrl.java                                                   * 	  
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
package com.pennant.webui.staticparms.interestratebasiscode;

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

import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.service.staticparms.InterestRateBasisCodeService;
import com.pennant.webui.staticparms.interestratebasiscode.model.InterestRateBasisCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/StaticParms/InterestRateBasisCode /InterestRateBasisCodeList.zul
 * file.
 */
public class InterestRateBasisCodeListCtrl extends GFCBaseListCtrl<InterestRateBasisCode> {
	private static final long serialVersionUID = -2097643737104268398L;
	private static final Logger logger = Logger.getLogger(InterestRateBasisCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_InterestRateBasisCodeList;
	protected Borderlayout borderLayout_InterestRateBasisCodeList;
	protected Paging pagingInterestRateBasisCodeList;
	protected Listbox listBoxInterestRateBasisCode;

	protected Listheader listheader_IntRateBasisCode;
	protected Listheader listheader_IntRateBasisDesc;
	protected Listheader listheader_IntRateBasisIsActive;

	protected Button button_InterestRateBasisCodeList_NewInterestRateBasisCode;
	protected Button button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog;

	protected Textbox intRateBasisCode;
	protected Textbox intRateBasisDesc;
	protected Checkbox intRateBasisIsActive;

	protected Listbox sortOperator_intRateBasisCode;
	protected Listbox sortOperator_intRateBasisDesc;
	protected Listbox sortOperator_intRateBasisIsActive;

	private transient InterestRateBasisCodeService interestRateBasisCodeService;

	/**
	 * default constructor.<br>
	 */
	public InterestRateBasisCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InterestRateBasisCode";
		super.pageRightName = "InterestRateBasisCodeList";
		super.tableName = "BMTIntRateBasisCodes_AView";
		super.queueTableName = "BMTIntRateBasisCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_InterestRateBasisCodeList(Event event) {
		// Set the page level components.
		setPageComponents(window_InterestRateBasisCodeList, borderLayout_InterestRateBasisCodeList,
				listBoxInterestRateBasisCode, pagingInterestRateBasisCodeList);
		setItemRender(new InterestRateBasisCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InterestRateBasisCodeList_NewInterestRateBasisCode, RIGHT_NOT_ACCESSIBLE, true);
		registerButton(button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog);

		registerField("intRateBasisCode", listheader_IntRateBasisCode, SortOrder.ASC, intRateBasisCode,
				sortOperator_intRateBasisCode, Operators.STRING);
		registerField("intRateBasisDesc", listheader_IntRateBasisDesc, SortOrder.NONE, intRateBasisDesc,
				sortOperator_intRateBasisDesc, Operators.STRING);
		registerField("intRateBasisIsActive", listheader_IntRateBasisIsActive, SortOrder.NONE, intRateBasisIsActive,
				sortOperator_intRateBasisIsActive, Operators.BOOLEAN);

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
	public void onClick$button_InterestRateBasisCodeList_InterestRateBasisCodeSearchDialog(Event event) {
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
	public void onClick$button_InterestRateBasisCodeList_NewInterestRateBasisCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		InterestRateBasisCode interestRateBasisCode = new InterestRateBasisCode();
		interestRateBasisCode.setNewRecord(true);
		interestRateBasisCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(interestRateBasisCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onInterestRateBasisCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxInterestRateBasisCode.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		InterestRateBasisCode interestRateBasisCode = interestRateBasisCodeService.getInterestRateBasisCodeById(id);

		if (interestRateBasisCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND IntRateBasisCode='" + interestRateBasisCode.getIntRateBasisCode() + "' AND version="
				+ interestRateBasisCode.getVersion() + " ";

		if (doCheckAuthority(interestRateBasisCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && interestRateBasisCode.getWorkflowId() == 0) {
				interestRateBasisCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(interestRateBasisCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aInterestRateBasisCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(InterestRateBasisCode aInterestRateBasisCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("interestRateBasisCode", aInterestRateBasisCode);
		arg.put("interestRateBasisCodeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/InterestRateBasisCode/InterestRateBasisCodeDialog.zul", null, arg);
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

	public void setInterestRateBasisCodeService(InterestRateBasisCodeService interestRateBasisCodeService) {
		this.interestRateBasisCodeService = interestRateBasisCodeService;
	}
}