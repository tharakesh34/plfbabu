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
 * FileName    		:  BaseRateListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.baserate;

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

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.applicationmaster.baserate.model.BaseRateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BaseRate/BaseRateList.zul file.
 */
public class BaseRateListCtrl extends GFCBaseListCtrl<BaseRate> {
	private static final long serialVersionUID = 8263433171238545613L;
	private static final Logger logger = Logger.getLogger(BaseRateListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BaseRateList;
	protected Borderlayout borderLayout_BaseRateList;
	protected Paging pagingBaseRateList;
	protected Listbox listBoxBaseRate;

	protected Textbox bRType;
	protected Textbox bRTypeDesc;
	protected Datebox bREffDate;
	protected Decimalbox bRRate;

	protected Listbox sortOperator_bRType;
	protected Listbox sortOperator_bRTypeDesc;
	protected Listbox sortOperator_bREffDate;
	protected Listbox sortOperator_bRRate;

	// List headers
	protected Listheader listheader_BRType;
	protected Listheader listheader_BRTypeDesc;
	protected Listheader listheader_BREffDate;
	protected Listheader listheader_BRRate;

	// checkRights
	protected Button button_BaseRateList_NewBaseRate;
	protected Button button_BaseRateList_BaseRateSearchDialog;

	private transient BaseRateService baseRateService;

	/**
	 * default constructor.<br>
	 */
	public BaseRateListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BaseRate";
		super.pageRightName = "BaseRateList";
		super.tableName = "RMTBaseRates_AView";
		super.queueTableName = "RMTBaseRates_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_BaseRateList(Event event) {
		// Set the page level components.
		setPageComponents(window_BaseRateList, borderLayout_BaseRateList, listBoxBaseRate, pagingBaseRateList);
		setItemRender(new BaseRateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_BaseRateList_NewBaseRate, "button_BaseRateList_NewBaseRate", true);
		registerButton(button_BaseRateList_BaseRateSearchDialog);

		registerField("bRType", listheader_BRType, SortOrder.ASC, bRType, sortOperator_bRType, Operators.STRING);
		registerField("lovDescBRTypeName", listheader_BRTypeDesc, SortOrder.NONE, bRTypeDesc, sortOperator_bRTypeDesc,
				Operators.STRING);
		registerField("bREffDate", listheader_BREffDate, SortOrder.NONE, bREffDate, sortOperator_bREffDate,
				Operators.DATE);
		registerField("currency");
		registerField("bRRate", listheader_BRRate, SortOrder.NONE, bRRate, sortOperator_bRRate, Operators.NUMERIC);

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
	public void onClick$button_BaseRateList_BaseRateSearchDialog(Event event) {
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
	public void onClick$button_BaseRateList_NewBaseRate(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		BaseRate baseRate = new BaseRate();
		baseRate.setNewRecord(true);
		baseRate.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(baseRate);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onBaseRateItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxBaseRate.getSelectedItem();

		// Get the selected entity.
		String bRType = (String) selectedItem.getAttribute("bRType");
		String currency = (String) selectedItem.getAttribute("currency");
		Date bREffDate = (Date) selectedItem.getAttribute("bREffDate");

		BaseRate baseRate = baseRateService.getBaseRateById(bRType, currency, bREffDate);

		if (baseRate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND BRType='" + baseRate.getBRType() + "' AND BREffDate='"
				+ DateUtility.formatDate(baseRate.getBREffDate(), PennantConstants.DBDateFormat) + "' AND version="
				+ baseRate.getVersion() + " ";

		if (doCheckAuthority(baseRate, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && baseRate.getWorkflowId() == 0) {
				baseRate.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(baseRate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aBaseRate
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(BaseRate aBaseRate) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("baseRate", aBaseRate);
		arg.put("baseRateListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/BaseRate/BaseRateDialog.zul", null, arg);
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

	public void setBaseRateService(BaseRateService baseRateService) {
		this.baseRateService = baseRateService;
	}

}