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
 * FileName    		:  InsurancePolicyListCtrl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2017    														*
 *                                                                  						*
 * Modified Date    :  06-02-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.InsurancePolicy;

import java.io.Serializable;
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

import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennant.backend.service.applicationmaster.InsurancePolicyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.applicationmaster.InsurancePolicy.model.InsurancePolicyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/InsurancePolicy/InsurancePolicyList.zul file.<br>
 * 
 */
public class InsurancePolicyListCtrl extends GFCBaseListCtrl<InsurancePolicy> implements Serializable {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger.getLogger(InsurancePolicyListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_InsurancePolicyList;											// autowired
	protected Borderlayout						borderLayout_InsurancePolicyList;										// autowired
	protected Paging							pagingInsurancePolicyList;												// autowired
	protected Listbox							listBoxInsurancePolicy;												// autowired

	// List headers
	protected Listheader						listheader_PolicyCode;													// autowired
	protected Listheader						listheader_InsuranceType;												// autowired
	protected Listheader						listheader_InsuranceProvider;											// autowired
	protected Listheader						listheader_Active;														// autowired

	// checkRights
	protected Button							button_InsurancePolicyList_NewInsurancePolicy;							// autowired
	protected Button							button_InsurancePolicyList_InsurancePolicySearch;						// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<InsurancePolicy>	searchObj;

	private transient InsurancePolicyService	insurancePolicyService;
	protected Textbox							policyCode;															// autowired
	protected Listbox							sortOperator_PolicyCode;												// autowired

	protected Textbox							insuranceType;															// autowired
	protected Listbox							sortOperator_InsuranceType;											// autowired

	protected Textbox							insuranceProvider;														// autowired
	protected Listbox							sortOperator_InsuranceProvider;										// autowired

	protected Checkbox							active;																// autowired
	protected Listbox							sortOperator_Active;													// autowired



	/**
	 * default constructor.<br>
	 */
	public InsurancePolicyListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InsurancePolicy";
		super.pageRightName = "InsuranceTypeList";
		super.tableName = "InsurancePolicy_AView";
		super.queueTableName = "InsurancePolicy_View";
		super.enquiryTableName = "InsurancePolicy_View";
	}

	public void onCreate$window_InsurancePolicyList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InsurancePolicyList, borderLayout_InsurancePolicyList, listBoxInsurancePolicy,
				pagingInsurancePolicyList);
		setItemRender(new InsurancePolicyListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InsurancePolicyList_NewInsurancePolicy, "button_InsurancePolicyList_NewInsurancePolicy",
				true);
		registerButton(button_InsurancePolicyList_InsurancePolicySearch);

		registerField("PolicyCode", listheader_PolicyCode, SortOrder.ASC, policyCode, sortOperator_PolicyCode,
				Operators.STRING);
		registerField("Policydesc");
		registerField("InsuranceType", listheader_InsuranceType, SortOrder.ASC, insuranceType,
				sortOperator_InsuranceType, Operators.STRING);
		registerField("InsuranceProvider", listheader_InsuranceProvider, SortOrder.ASC, insuranceProvider,
				sortOperator_InsuranceProvider, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.ASC, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/*
	 * Invoke Search
	 */

	public void onClick$button_InsurancePolicyList_InsurancePolicySearch(Event event) throws Exception {
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
	public void onClick$button_InsurancePolicyList_NewInsurancePolicy(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		InsurancePolicy insurancePolicy = new InsurancePolicy();
		insurancePolicy.setNewRecord(true);
		insurancePolicy.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(insurancePolicy);

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.applicationmaster.insurancepolicy.model.InsurancePolicyListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onInsurancePolicyItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxInsurancePolicy.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		InsurancePolicy insurancePolicy = insurancePolicyService.getInsurancePolicyById(id);

		if (insurancePolicy == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND PolicyCode='" + insurancePolicy.getPolicyCode() + "' AND version="
				+ insurancePolicy.getVersion() + " ";

		if (doCheckAuthority(insurancePolicy, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && insurancePolicy.getWorkflowId() == 0) {
				insurancePolicy.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(insurancePolicy);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(InsurancePolicy insurancePolicy) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("insurancePolicy", insurancePolicy);
		arg.put("insurancePolicyListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/InsurancePolicy/InsurancePolicyDialog.zul",
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

	public void setInsurancePolicyService(InsurancePolicyService insurancePolicyService) {
		this.insurancePolicyService = insurancePolicyService;
	}

}