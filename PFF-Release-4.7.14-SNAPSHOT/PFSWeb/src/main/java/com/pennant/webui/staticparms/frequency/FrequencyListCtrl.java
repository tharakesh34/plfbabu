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
 * FileName    		:  FrequencyListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.frequency;

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

import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.service.staticparms.FrequencyService;
import com.pennant.webui.staticparms.frequency.model.FrequencyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/StaticParms/Frequency/FrequencyList.zul file.
 */
public class FrequencyListCtrl extends GFCBaseListCtrl<Frequency> {
	private static final long serialVersionUID = -2254447125626598370L;
	private static final Logger logger = Logger.getLogger(FrequencyListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FrequencyList;
	protected Borderlayout borderLayout_FrequencyList;
	protected Paging pagingFrequencyList;
	protected Listbox listBoxFrequency;

	protected Listheader listheader_FrqCode;
	protected Listheader listheader_FrqDesc;
	protected Listheader listheader_FrqIsActive;

	protected Button button_FrequencyList_NewFrequency;
	protected Button button_FrequencyList_FrequencySearchDialog;

	protected Textbox frqCode;
	protected Textbox frqDesc;
	protected Checkbox frqIsActive;

	protected Listbox sortOperator_frqCode;
	protected Listbox sortOperator_frqDesc;
	protected Listbox sortOperator_frqIsActive;

	private transient FrequencyService frequencyService;

	/**
	 * default constructor.<br>
	 */
	public FrequencyListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Frequency";
		super.pageRightName = "FrequencyList";
		super.tableName = "BMTFrequencies_AView";
		super.queueTableName = "BMTFrequencies_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FrequencyList(Event event) {
		// Set the page level components.
		setPageComponents(window_FrequencyList, borderLayout_FrequencyList, listBoxFrequency, pagingFrequencyList);
		setItemRender(new FrequencyListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FrequencyList_NewFrequency, "button_FrequencyList_NewFrequency", true);
		registerButton(button_FrequencyList_FrequencySearchDialog);

		registerField("frqCode", listheader_FrqCode, SortOrder.ASC, frqCode, sortOperator_frqCode, Operators.STRING);
		registerField("frqDesc", listheader_FrqDesc, SortOrder.NONE, frqDesc, sortOperator_frqDesc, Operators.STRING);
		registerField("frqIsActive", listheader_FrqIsActive, SortOrder.NONE, frqIsActive, sortOperator_frqIsActive,
				Operators.BOOLEAN);

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
	public void onClick$button_FrequencyList_FrequencySearchDialog(Event event) {
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
	public void onClick$button_FrequencyList_NewFrequency(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Frequency frequency = new Frequency();
		frequency.setNewRecord(true);
		frequency.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(frequency);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFrequencyItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFrequency.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Frequency frequency = frequencyService.getFrequencyById(id);

		if (frequency == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FrqCode='" + frequency.getFrqCode() + "' AND version=" + frequency.getVersion() + " ";

		if (doCheckAuthority(frequency, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && frequency.getWorkflowId() == 0) {
				frequency.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(frequency);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFrequency
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Frequency aFrequency) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("frequency", aFrequency);
		arg.put("frequencyListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/Frequency/FrequencyDialog.zul", null, arg);
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

	public void setFrequencyService(FrequencyService frequencyService) {
		this.frequencyService = frequencyService;
	}
}