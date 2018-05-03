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
 * FileName    		:  TakafulProviderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.takafulprovider;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.service.applicationmaster.TakafulProviderService;
import com.pennant.webui.applicationmasters.takafulprovider.model.TakafulProviderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/TakafulProvider/TakafulProviderList.zul file.
 */
public class TakafulProviderListCtrl extends GFCBaseListCtrl<TakafulProvider> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TakafulProviderListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TakafulProviderList;
	protected Borderlayout borderLayout_TakafulProviderList;
	protected Paging pagingTakafulProviderList;
	protected Listbox listBoxTakafulProvider;

	protected Listheader listheader_TakafulCode;
	protected Listheader listheader_TakafulName;
	protected Listheader listheader_TakafulType;
	protected Listheader listheader_TakafulRate;

	protected Button button_TakafulProviderList_NewTakafulProvider;
	protected Button button_TakafulProviderList_TakafulProviderSearch;

	protected Textbox takafulCode;
	protected Textbox takafulName;
	protected Combobox takafulType;
	protected Textbox takafulRate;

	protected Listbox sortOperator_TakafulCode;
	protected Listbox sortOperator_TakafulName;
	protected Listbox sortOperator_TakafulType;
	protected Listbox sortOperator_TakafulRate;

	private transient TakafulProviderService takafulProviderService;

	/**
	 * default constructor.<br>
	 */
	public TakafulProviderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "TakafulProvider";
		super.pageRightName = "TakafulProviderList";
		super.tableName = "TakafulProvider_AView";
		super.queueTableName = "TakafulProvider_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_TakafulProviderList(Event event) {
		// Set the page level components.
		setPageComponents(window_TakafulProviderList, borderLayout_TakafulProviderList, listBoxTakafulProvider,
				pagingTakafulProviderList);
		setItemRender(new TakafulProviderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_TakafulProviderList_NewTakafulProvider, "button_TakafulProviderList_NewTakafulProvider",
				true);
		registerButton(button_TakafulProviderList_TakafulProviderSearch);

		registerField("TakafulCode", listheader_TakafulCode, SortOrder.ASC, takafulCode, sortOperator_TakafulCode,
				Operators.STRING);
		registerField("TakafulName", listheader_TakafulName, SortOrder.NONE, takafulName, sortOperator_TakafulName,
				Operators.STRING);
	
		registerField("TakafulRate", listheader_TakafulRate, SortOrder.NONE, takafulRate, sortOperator_TakafulRate,
				Operators.STRING);

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
	public void onClick$button_TakafulProviderList_TakafulProviderSearch(Event event) {
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
	public void onClick$button_TakafulProviderList_NewTakafulProvider(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		TakafulProvider takafulProvider = new TakafulProvider();
		takafulProvider.setNewRecord(true);
		takafulProvider.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(takafulProvider);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onTakafulProviderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxTakafulProvider.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		TakafulProvider takafulProvider = takafulProviderService.getTakafulProviderById(id);

		if (takafulProvider == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND TakafulCode='" + takafulProvider.getTakafulCode() + "' AND version="
				+ takafulProvider.getVersion() + " ";

		if (doCheckAuthority(takafulProvider, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && takafulProvider.getWorkflowId() == 0) {
				takafulProvider.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(takafulProvider);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aTakafulProvider
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TakafulProvider aTakafulProvider) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("takafulProvider", aTakafulProvider);
		arg.put("takafulProviderListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/TakafulProvider/TakafulProviderDialog.zul",
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

	public void setTakafulProviderService(TakafulProviderService takafulProviderService) {
		this.takafulProviderService = takafulProviderService;
	}
}