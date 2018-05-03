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
package com.pennant.webui.systemmasters.academic;

import java.util.Map;

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

import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.service.systemmasters.AcademicService;
import com.pennant.webui.systemmasters.academic.model.AcademicListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Academic/AcademicList.zul file.
 */
public class AcademicListCtrl extends GFCBaseListCtrl<Academic> {
	private static final long serialVersionUID = 5327118548986437717L;
	private static final Logger logger = Logger.getLogger(AcademicListCtrl.class);

	protected Window window_AcademicList;
	protected Borderlayout borderLayout_AcademicList;
	protected Listbox listBoxAcademic;
	protected Paging pagingAcademicList;

	protected Listheader listheader_AcademicLevel;
	protected Listheader listheader_AcademicDecipline;
	protected Listheader listheader_AcademicDesc;

	protected Button button_AcademicList_NewAcademic;
	protected Button button_AcademicList_AcademicSearchDialog;

	protected Textbox academicLevel;
	protected Textbox academicDecipline;
	protected Textbox academicDesc;

	protected Listbox sortOperator_academicDecipline;
	protected Listbox sortOperator_academicLevel;
	protected Listbox sortOperator_academicDesc;

	private transient AcademicService academicService;

	/**
	 * The default constructor.
	 */
	public AcademicListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Academic";
		super.pageRightName = "AcademicList";
		super.tableName = "BMTAcademics_AView";
		super.queueTableName = "BMTAcademics_View";
		super.enquiryTableName = "BMTAcademics_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_AcademicList(Event event) {
		// Set the page level components.
		setPageComponents(window_AcademicList, borderLayout_AcademicList, listBoxAcademic, pagingAcademicList);
		setItemRender(new AcademicListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AcademicList_NewAcademic, "button_AcademicList_NewAcademic", true);
		registerButton(button_AcademicList_AcademicSearchDialog);

		registerField("academicID");
		registerField("academicLevel", listheader_AcademicLevel, SortOrder.ASC, academicLevel,
				sortOperator_academicLevel, Operators.STRING);
		registerField("academicDecipline", listheader_AcademicDecipline, SortOrder.ASC, academicDecipline,
				sortOperator_academicDecipline, Operators.STRING);
		registerField("academicDesc", listheader_AcademicDesc, SortOrder.NONE, academicDesc, sortOperator_academicDesc,
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
	public void onClick$button_AcademicList_AcademicSearchDialog(Event event) {
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
	public void onClick$button_AcademicList_NewAcademic(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Academic academic = new Academic();
		academic.setNewRecord(true);
		academic.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(academic);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onAcademicItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAcademic.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		Academic academic = academicService.getAcademicById(id);

		if (academic == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND AcademicID='" + academic.getAcademicID() + "' AND version=" + academic.getVersion()
				+ " ";

		if (doCheckAuthority(academic, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && academic.getWorkflowId() == 0) {
				academic.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(academic);
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
	private void doShowDialogPage(Academic academic) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("academic", academic);
		arg.put("academicListCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Academic/AcademicDialog.zul", null, arg);
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

	public void setAcademicService(AcademicService academicService) {
		this.academicService = academicService;
	}
}