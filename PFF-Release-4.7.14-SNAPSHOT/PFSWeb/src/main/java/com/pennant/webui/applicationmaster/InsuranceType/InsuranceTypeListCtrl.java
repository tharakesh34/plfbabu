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
 * FileName    		:  InsuranceTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2016    														*
 *                                                                  						*
 * Modified Date    :  19-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.InsuranceType;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.service.applicationmaster.InsuranceTypeService;
import com.pennant.webui.applicationmaster.InsuranceType.model.InsuranceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/applicationmasters/InsuranceType/InsuranceTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class InsuranceTypeListCtrl extends GFCBaseListCtrl<InsuranceType>
		implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(InsuranceTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_InsuranceTypeList; // autowired
	protected Borderlayout borderLayout_InsuranceTypeList; // autowired
	protected Listbox listBoxInsuranceType; // autowired
	protected Paging pagingInsuranceTypeList; // autowired

	// List headers
	protected Listheader listheader_InsuranceType; // autowired
	protected Listheader listheader_InsuranceTypeDesc; // autowired

	protected Button button_InsuranceTypeList_NewInsuranceType; // autowired
	protected Button button_InsuranceTypeList_InsuranceTypeSearch; // autowired

	protected Textbox insuranceType; // autowired
	protected Textbox insuranceTypeDesc; // autowired

	protected Listbox sortOperator_InsuranceType; // autowired
	protected Listbox sortOperator_InsuranceTypeDesc; // autowired

	private transient InsuranceTypeService insuranceTypeService;

	/**
	 * default constructor.<br>
	 */
	public InsuranceTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InsuranceType";
		super.pageRightName = "InsuranceTypeList";
		super.tableName = "InsuranceType_AView";
		super.queueTableName = "InsuranceType_View";
		super.enquiryTableName = "InsuranceType_View";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_InsuranceTypeList(Event event) throws Exception {
		// Set the page level components.
		setPageComponents(window_InsuranceTypeList,
				borderLayout_InsuranceTypeList, listBoxInsuranceType,
				pagingInsuranceTypeList);
		setItemRender(new InsuranceTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InsuranceTypeList_NewInsuranceType,
				"button_InsuranceTypeList_NewInsuranceType", true);
		registerButton(button_InsuranceTypeList_InsuranceTypeSearch);

		registerField("insuranceType", listheader_InsuranceType, SortOrder.ASC,
				insuranceType, sortOperator_InsuranceType, Operators.STRING);
		registerField("insuranceTypeDesc", listheader_InsuranceTypeDesc,
				SortOrder.ASC, insuranceTypeDesc,
				sortOperator_InsuranceTypeDesc, Operators.STRING);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_InsuranceTypeList_InsuranceTypeSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button.
	 * Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_InsuranceTypeList_NewInsuranceType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		InsuranceType inusranceType = new InsuranceType();
		inusranceType.setNewRecord(true);
		inusranceType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(inusranceType);

		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.applicationmasters.insurancetype.model.
	 * InsuranceTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onInsuranceTypeItemDoubleClicked(Event event) throws Exception {

		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxInsuranceType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String tableType = StringUtils.isEmpty((String) selectedItem.getAttribute("recordType")) ? "_View" : "_TView";
		InsuranceType insuranceType = insuranceTypeService.getInsuranceTypeById(id,tableType);

		if (insuranceType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND InsuranceType='"
				+ insuranceType.getInsuranceType() + "' AND version="
				+ insuranceType.getVersion() + " ";

		if (doCheckAuthority(insuranceType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && insuranceType.getWorkflowId() == 0) {
				insuranceType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(insuranceType);
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
	private void doShowDialogPage(InsuranceType insuranceType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("insuranceType", insuranceType);
		arg.put("insuranceTypeListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/InsuranceType/InsuranceTypeDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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

	public void setInsuranceTypeService(
			InsuranceTypeService insuranceTypeService) {
		this.insuranceTypeService = insuranceTypeService;
	}

}