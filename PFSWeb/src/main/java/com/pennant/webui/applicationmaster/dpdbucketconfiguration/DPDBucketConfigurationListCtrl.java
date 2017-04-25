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
 * FileName    		:  DPDBucketConfigurationListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.dpdbucketconfiguration;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.service.applicationmaster.DPDBucketConfigurationService;
import com.pennant.webui.applicationmaster.dpdbucketconfiguration.model.DPDBucketConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/DPDBucketConfiguration/DPDBucketConfigurationList.zul file.
 * 
 */
public class DPDBucketConfigurationListCtrl extends GFCBaseListCtrl<DPDBucketConfiguration> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DPDBucketConfigurationListCtrl.class);

	protected Window window_DPDBucketConfigurationList;
	protected Borderlayout borderLayout_DPDBucketConfigurationList;
	protected Paging pagingDPDBucketConfigurationList;
	protected Listbox listBoxDPDBucketConfiguration;

	// List headers
	protected Listheader listheader_ProductCode;
	protected Listheader listheader_BucketID;
	protected Listheader listheader_DueDays;
	protected Listheader listheader_SuspendProfit;

	// checkRights
	protected Button button_DPDBucketConfigurationList_NewDPDBucketConfiguration;
	protected Button button_DPDBucketConfigurationList_DPDBucketConfigurationSearch;

	// Search Fields
	protected Textbox productCode; // autowired
	protected Textbox bucketID; // autowired
  	protected Intbox dueDays; // autowired
	protected Checkbox suspendProfit; // autowired
	
	protected Listbox sortOperator_ProductCode;
	protected Listbox sortOperator_BucketID;
	protected Listbox sortOperator_DueDays;
	protected Listbox sortOperator_SuspendProfit;
	
	private transient DPDBucketConfigurationService dPDBucketConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public DPDBucketConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DPDBucketConfiguration";
		super.pageRightName = "DPDBucketConfigurationList";
		super.tableName = "DPDBUCKETSCONFIG_AView";
		super.queueTableName = "DPDBUCKETSCONFIG_View";
		super.enquiryTableName = "DPDBUCKETSCONFIG_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DPDBucketConfigurationList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_DPDBucketConfigurationList, borderLayout_DPDBucketConfigurationList, listBoxDPDBucketConfiguration,
				pagingDPDBucketConfigurationList);
		setItemRender(new DPDBucketConfigurationListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DPDBucketConfigurationList_DPDBucketConfigurationSearch);
		registerButton(button_DPDBucketConfigurationList_NewDPDBucketConfiguration, "button_DPDBucketConfigurationList_NewDPDBucketConfiguration", true);

		registerField("configID");
		registerField("productCode", listheader_ProductCode, SortOrder.NONE, productCode, sortOperator_ProductCode, Operators.STRING);
		registerField("bucketCode", listheader_BucketID, SortOrder.NONE, bucketID, sortOperator_BucketID, Operators.NUMERIC);
		registerField("dueDays", listheader_DueDays, SortOrder.NONE, dueDays, sortOperator_DueDays, Operators.NUMERIC);
		registerField("suspendProfit", listheader_SuspendProfit, SortOrder.NONE, suspendProfit, sortOperator_SuspendProfit, Operators.BOOLEAN);

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
	public void onClick$button_DPDBucketConfigurationList_DPDBucketConfigurationSearch(Event event) {
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
	public void onClick$button_DPDBucketConfigurationList_NewDPDBucketConfiguration(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		DPDBucketConfiguration dpdbucketconfiguration = new DPDBucketConfiguration();
		dpdbucketconfiguration.setNewRecord(true);
		dpdbucketconfiguration.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(dpdbucketconfiguration);

		logger.debug(Literal.LEAVING);
	}


	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onDPDBucketConfigurationItemDoubleClicked(Event event) {
		logger.debug("Entering");
		
		// Get the selected record.
		Listitem selectedItem = this.listBoxDPDBucketConfiguration.getSelectedItem();
		final long configID = (long) selectedItem.getAttribute("configID");
		DPDBucketConfiguration dpdbucketconfiguration = dPDBucketConfigurationService.getDPDBucketConfiguration(configID);

		if (dpdbucketconfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		
		StringBuffer whereCond= new StringBuffer();
		whereCond.append("  AND  ConfigID = ");
		whereCond.append( dpdbucketconfiguration.getConfigID());
		whereCond.append(" AND  version=");
		whereCond.append(dpdbucketconfiguration.getVersion());
	
		if (doCheckAuthority(dpdbucketconfiguration, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dpdbucketconfiguration.getWorkflowId() == 0) {
				dpdbucketconfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dpdbucketconfiguration);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param dpdbucketconfiguration
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DPDBucketConfiguration dpdbucketconfiguration) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("dpdbucketconfiguration", dpdbucketconfiguration);
		arg.put("dpdbucketconfigurationListCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/DPDBucketConfiguration/DPDBucketConfigurationDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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

	public void setDPDBucketConfigurationService(DPDBucketConfigurationService dpdbucketconfigurationService) {
		this.dPDBucketConfigurationService = dpdbucketconfigurationService;
	}
}