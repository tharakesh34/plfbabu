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
 * * FileName : FinChangeCustomerListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-11-2019 * *
 * Modified Date : 20-11-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-11-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.finchangecustomer;

import java.util.List;
import java.util.Map;

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

import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.finance.FinChangeCustomerService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.webui.finance.finchangecustomer.model.FinChangeCustomerListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinChangeCustomer/FinChangeCustomerList.zul file.
 * 
 */
public class FinChangeCustomerListCtrl extends GFCBaseListCtrl<FinChangeCustomer> {
	private static final long serialVersionUID = 1L;

	protected Window window_FinChangeCustomerList;
	protected Borderlayout borderLayout_FinChangeCustomerList;
	protected Paging pagingFinChangeCustomerList;
	protected Listbox listBoxFinChangeCustomer;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_OldCustId;
	protected Listheader listheader_CoApplicantId;
	// checkRights
	protected Button button_FinChangeCustomerList_NewFinChangeCustomer;
	protected Button button_FinChangeCustomerList_FinChangeCustomerSearch;

	// Search Fields
	protected Textbox finReference; // autowired
	protected Textbox oldCustId;
	protected Textbox coApplicantId;

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_OldCustId;
	protected Listbox sortOperator_CoApplicantId;
	protected JointAccountDetailService jointAccountDetailService;

	private transient FinChangeCustomerService finChangeCustomerService;

	/**
	 * default constructor.<br>
	 */
	public FinChangeCustomerListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinChangeCustomer";
		super.pageRightName = "FinChangeCustomerList";
		super.tableName = "FinChangeCustomer_AView";
		super.queueTableName = "FINCHANGECUSTOMER_lVIEW";
		super.enquiryTableName = "FINCHANGECUSTOMER_lVIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinChangeCustomerList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinChangeCustomerList, borderLayout_FinChangeCustomerList, listBoxFinChangeCustomer,
				pagingFinChangeCustomerList);
		setItemRender(new FinChangeCustomerListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinChangeCustomerList_FinChangeCustomerSearch);
		registerButton(button_FinChangeCustomerList_NewFinChangeCustomer,
				"button_FinChangeCustomerList_NewFinChangeCustomer", true);
		registerField("id");
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("CustCif", listheader_OldCustId, SortOrder.NONE, oldCustId, sortOperator_OldCustId,
				Operators.STRING);

		registerField("jcustCif", listheader_CoApplicantId, SortOrder.NONE, coApplicantId, sortOperator_CoApplicantId,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FinChangeCustomerList_FinChangeCustomerSearch(Event event) {
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
	public void onClick$button_FinChangeCustomerList_NewFinChangeCustomer(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		FinChangeCustomer finChangeCustomer = new FinChangeCustomer();
		finChangeCustomer.setNewRecord(true);
		finChangeCustomer.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(finChangeCustomer);
		// call the ZUL-file with the parameters packed in a map

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onFinChangeCustomerItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinChangeCustomer.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		FinChangeCustomer finChangeCustomer = finChangeCustomerService.getFinChangeCustomerById(id);

		if (finChangeCustomer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =?");

		if (doCheckAuthority(finChangeCustomer, whereCond.toString(), new Object[] { finChangeCustomer.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && finChangeCustomer.getWorkflowId() == 0) {
				finChangeCustomer.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(finChangeCustomer);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param finChangeCustomer The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinChangeCustomer finChangeCustomer) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("finChangeCustomer", finChangeCustomer);
		arg.put("finChangeCustomerListCtrl", this);
		arg.put("moduleDefiner", "FinChangeCustomer");
		try {
			if (finChangeCustomer.isNewRecord()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/ManualAdvise/SelectManualAdviseFinReferenceDialog.zul", null,
						arg);
			} else {
				// arg.put("financeMain",
				List<JointAccountDetail> joinAccountDetail = getJointAccountDetailService()
						.getJoinAccountDetail(finChangeCustomer.getFinID(), "_View");
				arg.put("jointAccountDetails", joinAccountDetail);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ChangeCustomerDialog.zul", null, arg);
			}
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

	public void setFinChangeCustomerService(FinChangeCustomerService FinChangeCustomerService) {
		this.finChangeCustomerService = FinChangeCustomerService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

}