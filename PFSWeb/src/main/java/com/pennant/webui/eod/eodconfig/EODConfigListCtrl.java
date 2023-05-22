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
 * * FileName : EODConfigListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-05-2017 * * Modified
 * Date : 24-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.eod.eodconfig;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.service.eod.EODConfigService;
import com.pennant.webui.eod.eodconfig.model.EODConfigListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.eod/EODConfig/EODConfigList.zul file.
 * 
 */
public class EODConfigListCtrl extends GFCBaseListCtrl<EODConfig> {
	private static final long serialVersionUID = 1L;

	protected Window window_EODConfigList;
	protected Borderlayout borderLayout_EODConfigList;
	protected Paging pagingEODConfigList;
	protected Listbox listBoxEODConfig;

	// List headers
	protected Listheader listheader_ExtMnthRequired;
	protected Listheader listheader_MnthExtTo;

	// checkRights
	protected Button button_EODConfigList_NewEODConfig;
	protected Button button_EODConfigList_EODConfigSearch;

	// Search Fields
	protected Checkbox extMnthRequired; // autowired
	protected Datebox mnthExtTo; // autowired

	protected Listbox sortOperator_ExtMnthRequired;
	protected Listbox sortOperator_MnthExtTo;

	private transient EODConfigService eODConfigService;

	/**
	 * default constructor.<br>
	 */
	public EODConfigListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "EODConfig";
		super.pageRightName = "EODConfigList";
		super.tableName = "EodConfig_AView";
		super.queueTableName = "EodConfig_View";
		super.enquiryTableName = "EodConfig_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_EODConfigList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_EODConfigList, borderLayout_EODConfigList, listBoxEODConfig, pagingEODConfigList);
		setItemRender(new EODConfigListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_EODConfigList_EODConfigSearch);
		registerButton(button_EODConfigList_NewEODConfig, "button_EODConfigList_NewEODConfig", true);

		registerField("eodConfigId");
		registerField("extMnthRequired", listheader_ExtMnthRequired, SortOrder.NONE, extMnthRequired,
				sortOperator_ExtMnthRequired, Operators.BOOLEAN);
		registerField("mnthExtTo", listheader_MnthExtTo, SortOrder.NONE, mnthExtTo, sortOperator_MnthExtTo,
				Operators.DATE);
		registerField("active");

		// Render the page and display the data.
		doRenderPage();
		search();

		List<EODConfig> list = eODConfigService.getEODConfig();

		if (list.size() > 0) {
			this.button_EODConfigList_NewEODConfig.setVisible(false);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_EODConfigList_EODConfigSearch(Event event) {
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
	public void onClick$button_EODConfigList_NewEODConfig(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		EODConfig eodconfig = new EODConfig();
		eodconfig.setNewRecord(true);
		eodconfig.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(eodconfig);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onEODConfigItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxEODConfig.getSelectedItem();
		final long eodConfigId = (long) selectedItem.getAttribute("eodConfigId");
		EODConfig eodconfig = eODConfigService.getEODConfig(eodConfigId);

		if (eodconfig == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append(" Where EodConfigId = ?");

		if (doCheckAuthority(eodconfig, whereCond.toString(), new Object[] { eodconfig.getEodConfigId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && eodconfig.getWorkflowId() == 0) {
				eodconfig.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(eodconfig);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param eodconfig The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(EODConfig eodconfig) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("eodconfig", eodconfig);
		arg.put("eodconfigListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/EOD/EODConfig/EODConfigDialog.zul", null, arg);
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

	public void setEODConfigService(EODConfigService eODConfigService) {
		this.eODConfigService = eODConfigService;
	}
}