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
 * FileName    		:  EmployerDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.employerdetail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.systemmasters.EmployerDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.systemmasters.employerdetail.model.EmployerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailList.zul file.
 */
public class EmployerDetailListCtrl extends GFCBaseListCtrl<EmployerDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EmployerDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EmployerDetailList;
	protected Borderlayout borderLayout_EmployerDetailList;
	protected Paging pagingEmployerDetailList;
	protected Listbox listBoxEmployerDetail;

	protected Intbox empID;
	protected Textbox empIndustry;
	protected Textbox empName;
	protected Textbox empPOBox;
	protected Textbox empCity;
	protected Combobox empAlocationType;

	protected Listbox sortOperator_EmpIndustry;
	protected Listbox sortOperator_EmpID;
	protected Listbox sortOperator_EmpName;
	protected Listbox sortOperator_EmpPOBox;
	protected Listbox sortOperator_EmpCity;
	protected Listbox sortOperator_EmpAlocationType;

	// List headers
	protected Listheader listheader_EmployerId;
	protected Listheader listheader_EmpIndustry;
	protected Listheader listheader_EmpName;
	protected Listheader listheader_EstablishDate;
	protected Listheader listheader_EmpPOBox;
	protected Listheader listheader_EmpCity;

	// checkRights
	protected Button button_EmployerDetailList_NewEmployerDetail;
	protected Button button_EmployerDetailList_EmployerDetailSearch;

	private transient EmployerDetailService employerDetailService;

	/**
	 * default constructor.<br>
	 */
	public EmployerDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "EmployerDetail";
		super.pageRightName = "EmployerDetailList";
		super.tableName = "EmployerDetail_AView";
		super.queueTableName = "EmployerDetail_View";
		super.enquiryTableName = "EmployerDetail_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_EmployerDetailList(Event event) {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_EmployerDetailList, borderLayout_EmployerDetailList, listBoxEmployerDetail,
				pagingEmployerDetailList);
		setItemRender(new EmployerDetailListModelItemRenderer());

		fillComboBox(this.empAlocationType, "", PennantStaticListUtil.getEmpAlocList(), "");

		// Register buttons and fields.
		registerButton(button_EmployerDetailList_NewEmployerDetail, "button_EmployerDetailList_NewEmployerDetail", true);
		registerButton(button_EmployerDetailList_EmployerDetailSearch);

		registerField("EmployerId", listheader_EmployerId, SortOrder.ASC, empID, sortOperator_EmpID, Operators.NUMERIC);
		registerField("empName", listheader_EmpName, SortOrder.NONE, empName, sortOperator_EmpName, Operators.STRING);
		registerField("lovDescIndustryDesc", listheader_EmpIndustry, SortOrder.NONE, empIndustry, sortOperator_EmpIndustry,
				Operators.STRING);
		registerField("empPOBox", listheader_EmpPOBox, SortOrder.NONE, empPOBox, sortOperator_EmpPOBox,
				Operators.STRING);
		registerField("lovDescCityName", listheader_EmpCity, SortOrder.NONE, empCity, sortOperator_EmpCity, Operators.STRING);
		registerField("empAlocationType", empAlocationType, SortOrder.NONE, sortOperator_EmpAlocationType,
				Operators.SIMPLE_NUMARIC);
		registerField("establishDate", listheader_EstablishDate, SortOrder.NONE);

		// Render the page and display the data.
		doRenderPage();
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_EmployerDetailList_EmployerDetailSearch(Event event) {
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
	public void onClick$button_EmployerDetailList_NewEmployerDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		EmployerDetail employerDetail = new EmployerDetail();
		employerDetail.setNewRecord(true);
		employerDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(employerDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onEmployerDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxEmployerDetail.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		EmployerDetail employerDetail = employerDetailService.getEmployerDetailById(id);

		if (employerDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND EmployerId='" + employerDetail.getEmployerId() + "' AND version="
				+ employerDetail.getVersion() + " ";

		if (doCheckAuthority(employerDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && employerDetail.getWorkflowId() == 0) {
				employerDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(employerDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param employerDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(EmployerDetail employerDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("employerDetail", employerDetail);
		arg.put("employerDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailDialog.zul", null,
					arg);
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
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setEmployerDetailService(EmployerDetailService employerDetailService) {
		this.employerDetailService = employerDetailService;
	}
}