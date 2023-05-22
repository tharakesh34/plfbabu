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
 * * FileName : MandateCheckDigitListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-12-2017 * *
 * Modified Date : 11-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.mandatecheckdigit;

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

import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.service.applicationmaster.MandateCheckDigitService;
import com.pennant.webui.applicationmaster.mandatecheckdigit.model.MandateCheckDigitListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/MandateCheckDigit/MandateCheckDigitList.zul
 * file.
 * 
 */
public class MandateCheckDigitListCtrl extends GFCBaseListCtrl<MandateCheckDigit> {
	private static final long serialVersionUID = 1L;

	protected Window window_MandateCheckDigitList;
	protected Borderlayout borderLayout_MandateCheckDigitList;
	protected Paging pagingMandateCheckDigitList;
	protected Listbox listBoxMandateCheckDigit;

	// List headers
	protected Listheader listheader_CheckDigitValue;
	protected Listheader listheader_LookUpValue;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_MandateCheckDigitList_NewMandateCheckDigit;
	protected Button button_MandateCheckDigitList_MandateCheckDigitSearch;

	// Search Fields
	protected Intbox checkDigitValue; // autowired
	protected Textbox lookUpValue; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_CheckDigitValue;
	protected Listbox sortOperator_LookUpValue;
	protected Listbox sortOperator_Active;

	private transient MandateCheckDigitService mandateCheckDigitService;

	/**
	 * default constructor.<br>
	 */
	public MandateCheckDigitListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MandateCheckDigit";
		super.pageRightName = "MandateCheckDigitList";
		super.tableName = "MandateCheckDigits_AView";
		super.queueTableName = "MandateCheckDigits_View";
		super.enquiryTableName = "MandateCheckDigits_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateCheckDigitList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_MandateCheckDigitList, borderLayout_MandateCheckDigitList, listBoxMandateCheckDigit,
				pagingMandateCheckDigitList);
		setItemRender(new MandateCheckDigitListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MandateCheckDigitList_MandateCheckDigitSearch);
		registerButton(button_MandateCheckDigitList_NewMandateCheckDigit,
				"button_MandateCheckDigitList_NewMandateCheckDigit", true);

		registerField("checkDigitValue", listheader_CheckDigitValue, SortOrder.NONE, checkDigitValue,
				sortOperator_CheckDigitValue, Operators.NUMERIC);
		registerField("lookUpValue", listheader_LookUpValue, SortOrder.NONE, lookUpValue, sortOperator_LookUpValue,
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
	public void onClick$button_MandateCheckDigitList_MandateCheckDigitSearch(Event event) {
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
	public void onClick$button_MandateCheckDigitList_NewMandateCheckDigit(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		MandateCheckDigit mandatecheckdigit = new MandateCheckDigit();
		mandatecheckdigit.setNewRecord(true);
		mandatecheckdigit.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(mandatecheckdigit);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onMandateCheckDigitItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxMandateCheckDigit.getSelectedItem();
		final int checkDigitValue = (int) selectedItem.getAttribute("checkDigitValue");
		MandateCheckDigit mandatecheckdigit = mandateCheckDigitService.getMandateCheckDigit(checkDigitValue);

		if (mandatecheckdigit == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  CheckDigitValue = ?");

		if (doCheckAuthority(mandatecheckdigit, whereCond.toString(),
				new Object[] { mandatecheckdigit.getCheckDigitValue() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && mandatecheckdigit.getWorkflowId() == 0) {
				mandatecheckdigit.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(mandatecheckdigit);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param mandatecheckdigit The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(MandateCheckDigit mandatecheckdigit) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("mandateCheckDigit", mandatecheckdigit);
		arg.put("mandateCheckDigitListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/MandateCheckDigit/MandateCheckDigitDialog.zul", null, arg);
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

	public void setMandateCheckDigitService(MandateCheckDigitService mandateCheckDigitService) {
		this.mandateCheckDigitService = mandateCheckDigitService;
	}
}