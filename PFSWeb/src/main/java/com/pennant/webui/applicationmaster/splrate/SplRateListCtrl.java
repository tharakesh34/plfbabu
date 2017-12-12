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
 * FileName    		:  SplRateListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.splrate;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.service.applicationmaster.SplRateService;
import com.pennant.webui.applicationmaster.splrate.model.SplRateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SplRate/SplRateList.zul file.
 */
public class SplRateListCtrl extends GFCBaseListCtrl<SplRate> {
	private static final long serialVersionUID = -2685575893028486510L;
	private static final Logger logger = Logger.getLogger(SplRateListCtrl.class);

	protected Window window_SplRateList;
	protected Borderlayout borderLayout_SplRateList;
	protected Paging pagingSplRateList;
	protected Listbox listBoxSplRate;

	// List headers
	protected Listheader listheader_SRType;
	protected Listheader listheader_SREffDate;
	protected Listheader listheader_SRRate;

	// checkRights
	protected Button button_SplRateList_NewSplRate;
	protected Button button_SplRateList_SplRateSearchDialog;

	protected Textbox sRType;
	protected Datebox sREffDate;
	protected Decimalbox sRRate;

	protected Listbox sortOperator_sRType;
	protected Listbox sortOperator_sREffDate;
	protected Listbox sortOperator_sRRate;

	private transient SplRateService splRateService;

	/**
	 * The default constructor.
	 */
	public SplRateListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SplRate";
		super.pageRightName = "SplRateList";
		super.tableName = "RMTSplRates_AView";
		super.queueTableName = "RMTSplRates_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SplRateList(Event event) {
		// Set the page level components.
		setPageComponents(window_SplRateList, borderLayout_SplRateList, listBoxSplRate, pagingSplRateList);
		setItemRender(new SplRateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SplRateList_NewSplRate, "button_SplRateList_NewSplRate", true);
		registerButton(button_SplRateList_SplRateSearchDialog);

		registerField("sRType", listheader_SRType, SortOrder.ASC, sRType, sortOperator_sRType, Operators.STRING);
		registerField("sRRate", listheader_SRRate, SortOrder.NONE, sRRate, sortOperator_sRRate, Operators.STRING);
		registerField("lovDescSRTypeName");
		registerField("sREffDate", listheader_SREffDate, SortOrder.ASC, sREffDate, sortOperator_sREffDate,
				Operators.DATE);

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
	public void onClick$button_SplRateList_SplRateSearchDialog(Event event) {
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
	 * @throws Exception
	 */
	public void onClick$button_SplRateList_NewSplRate(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		SplRate aSplRate = new SplRate();
		aSplRate.setNewRecord(true);
		aSplRate.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aSplRate);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSplRateItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSplRate.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Date sREffDate = (Date) selectedItem.getAttribute("sREffDate");
		SplRate splRate = splRateService.getSplRateById(id, sREffDate);

		if (splRate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND SRType='" + splRate.getSRType() + "' AND SREffDate='" + splRate.getSREffDate()
				+ "' AND version=" + splRate.getVersion() + " ";

		if (doCheckAuthority(splRate, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && splRate.getWorkflowId() == 0) {
				splRate.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(splRate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param splRate
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SplRate splRate) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("splRate", splRate);
		arg.put("splRateListCtrl", this);
		arg.put("newRecord", splRate.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/SplRate/SplRateDialog.zul", null, arg);
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

	public void setSplRateService(SplRateService splRateService) {
		this.splRateService = splRateService;
	}
}