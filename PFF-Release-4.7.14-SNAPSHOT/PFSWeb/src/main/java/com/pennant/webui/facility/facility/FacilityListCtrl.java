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
 * FileName    		:  FacilityListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-11-2013    														*
 *                                                                  						*
 * Modified Date    :  25-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-11-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.facility.facility;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.webui.facility.facility.model.FacilityListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Facility/Facility/FacilityList.zul file.
 */
public class FacilityListCtrl extends GFCBaseListCtrl<Facility> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FacilityListCtrl.class);

	protected Window window_FacilityList;
	protected Borderlayout borderLayout_FacilityList;
	protected Paging pagingFacilityList;
	protected Listbox listBoxFacility;

	protected Listheader listheader_CAFReference;
	protected Listheader listheader_CustID;
	protected Listheader listheader_StartDate;
	protected Listheader listheader_NextReviewDate;
	protected Listheader listheader_PresentingUnit;
	protected Listheader listheader_CountryOfDomicile;

	protected Button button_FacilityList_NewFacility;
	protected Button button_FacilityList_FacilitySearch;

	private transient FacilityService facilityService;

	protected Textbox cAFReference;
	protected Textbox custID;
	protected Datebox startDate;
	protected Textbox presentingUnit;
	protected Textbox countryOfDomicile;
	protected Datebox deadLine;
	protected Textbox countryOfRisk;
	protected Datebox establishedDate;
	protected Textbox natureOfBusiness;
	protected Textbox sICCode;
	protected Textbox countryManager;
	protected Textbox customerRiskType;
	protected Textbox relationshipManager;
	protected Textbox customerGroup;
	private Textbox cafType;

	protected Datebox nextReviewDate;
	protected Listbox sortOperator_NextReviewDate;
	protected Listbox sortOperator_CAFReference;
	protected Listbox sortOperator_CustID;
	protected Listbox sortOperator_StartDate;
	protected Listbox sortOperator_PresentingUnit;
	protected Listbox sortOperator_CountryOfDomicile;
	protected Listbox sortOperator_DeadLine;
	protected Listbox sortOperator_CountryOfRisk;
	protected Listbox sortOperator_EstablishedDate;
	protected Listbox sortOperator_NatureOfBusiness;
	protected Listbox sortOperator_SICCode;
	protected Listbox sortOperator_CountryManager;
	protected Listbox sortOperator_CustomerRiskType;
	protected Listbox sortOperator_RelationshipManager;
	protected Listbox sortOperator_CustomerGroup;

	/**
	 * default constructor.<br>
	 */
	public FacilityListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Facility";
		super.pageRightName = "FacilityList";
		super.tableName = "FacilityHeader_AView";
		super.queueTableName = "FacilityHeader_View";
		super.enquiryTableName = "FacilityHeader_TView";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (this.cafType != null) {
			this.searchObject.addFilterEqual("FacilityType", this.cafType.getValue());
		}
	}

	@Override
	protected void doPrintResults() {
		super.doPrintResults();
		if (enqiryModule) {
			try {
				new PTListReportUtils("ENQFAC", super.searchObject, this.pagingFacilityList.getTotalSize() + 1);
			} catch (InterruptedException e) {
				logger.error("Exception:", e);
			}
		} else {
			try {
				new PTListReportUtils(this.cafType.getValue(), super.searchObject,
						this.pagingFacilityList.getTotalSize() + 1);
			} catch (InterruptedException e) {
				logger.error("Exception:", e);
			}
		}
	}
	
	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FacilityList(Event event) {
		// Set the page level components.
		setPageComponents(window_FacilityList, borderLayout_FacilityList, listBoxFacility, pagingFacilityList);
		setItemRender(new FacilityListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FacilityList_NewFacility, "button_FacilityList_NewFacility", true);
		registerButton(button_FacilityList_FacilitySearch);

		registerField("CAFReference", listheader_CAFReference, SortOrder.ASC, cAFReference, sortOperator_CAFReference,
				Operators.STRING);
		registerField("CustCIF", listheader_CustID, SortOrder.NONE, custID, sortOperator_CustID, Operators.STRING);
		registerField("startDate", listheader_StartDate, SortOrder.NONE, startDate, sortOperator_StartDate,
				Operators.DATE);
		registerField("NextReviewDate", listheader_NextReviewDate, SortOrder.NONE, nextReviewDate,
				sortOperator_NextReviewDate, Operators.DATE);
		registerField("presentingUnit", listheader_PresentingUnit, SortOrder.NONE);
		registerField("countryOfDomicile", listheader_CountryOfDomicile, SortOrder.NONE);
		registerField("custID");
		registerField("deadLine");
		registerField("countryOfRisk");
		registerField("establishedDate");
		registerField("natureOfBusiness");
		registerField("sICCode");
		registerField("countryManager");
		registerField("customerRiskType");
		registerField("customerGroup");

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
	public void onClick$button_FacilityList_FacilitySearch(Event event) {
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
	public void onClick$button_FacilityList_NewFacility(Event event) throws Exception {
		logger.debug("Entering");

		// Create a new entity.
		Facility aFacility = new Facility();
		aFacility.setNewRecord(true);
		aFacility.setWorkflowId(getWorkFlowId());

		Map<String, Object> map = getDefaultArguments();
		map.put("facilityListCtrl", this);
		map.put("facility", aFacility);
		map.put("role", getUserWorkspace().getUserRoles());
		if (this.cafType != null) {
			map.put("cafType", this.cafType.getValue());
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/SelectFacilityTypeDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFacilityItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFacility.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Facility aFacility = facilityService.getFacilityById(id);

		if (aFacility == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		} else {
			aFacility.setUserRole(getRole());
			aFacility = getFacilityService().setFacilityScoringDetails(aFacility);
			aFacility.setCustomerEligibilityCheck(getFacilityService().getCustomerEligibility(null,
					aFacility.getCustID()));
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CAFReference='" + aFacility.getCAFReference() + "' AND version="
				+ aFacility.getVersion() + " ";

		if (doCheckAuthority(aFacility, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aFacility.getWorkflowId() == 0) {
				aFacility.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aFacility);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_FacilityList);
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromApproved(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		search();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		search();
		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFacility
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Facility aFacility) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("facility", aFacility);
		arg.put("facilityListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	
	public void onClick$button_FacilityList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering "+event.toString());
		doPrintResults();
		logger.debug("Leaving "+event.toString());
	}
	
	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FacilityService getFacilityService() {
		return this.facilityService;
	}

}