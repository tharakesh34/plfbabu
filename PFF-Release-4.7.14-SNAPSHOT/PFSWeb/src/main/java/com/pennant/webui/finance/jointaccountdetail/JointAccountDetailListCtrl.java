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
 * FileName    		:  JointAccountDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.jointaccountdetail;

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
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.webui.finance.jountaccountdetail.model.JountAccountDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/JointAccountDetail/JointAccountDetailList.zul file.
 */
public class JointAccountDetailListCtrl extends GFCBaseListCtrl<JointAccountDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JointAccountDetailListCtrl.class);

	protected Window window_JointAccountDetailList;
	protected Borderlayout borderLayout_JointAccountDetailList;
	protected Paging pagingJointAccountDetailList;
	protected Listbox listBoxJointAccountDetail;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_RepayAccountId;

	protected Button button_JountAccountDetailList_NewJountAccountDetail;
	protected Button button_JountAccountDetailList_JountAccountDetailSearch;


	private transient JointAccountDetailService jointAccountDetailService;

	/**
	 * default constructor.<br>
	 */
	public JointAccountDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "JointAccountDetail";
		super.pageRightName = "JointAccountDetailList";
		super.tableName = "FinJointAccountDetails_AView";
		super.queueTableName = "FinJointAccountDetails_View";
		super.enquiryTableName = "FinJointAccountDetails_TView";
	}

	public void onCreate$window_JointAccountDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_JointAccountDetailList, borderLayout_JointAccountDetailList,
				listBoxJointAccountDetail, pagingJointAccountDetailList);
		setItemRender(new JountAccountDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_JountAccountDetailList_NewJountAccountDetail,
				"button_JointAccountDetailList_NewJointAccountDetail", true);
		registerButton(button_JountAccountDetailList_JountAccountDetailSearch);

		registerField("custCIF", listheader_CustCIF);
		registerField("repayAccountId", listheader_RepayAccountId);

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

	public void onClick$button_JointAccountDetailList_NewJointAccountDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		JointAccountDetail aJointAccountDetail = new JointAccountDetail();
		aJointAccountDetail.setNewRecord(true);
		aJointAccountDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aJointAccountDetail);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onJointAccountDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxJointAccountDetail.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		JointAccountDetail aJointAccountDetail = jointAccountDetailService.getJountAccountDetailById(id);

		if (aJointAccountDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND JointAccountId='" + aJointAccountDetail.getJointAccountId() + "' AND version="
				+ aJointAccountDetail.getVersion() + " ";

		if (doCheckAuthority(aJointAccountDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aJointAccountDetail.getWorkflowId() == 0) {
				aJointAccountDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aJointAccountDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aJointAccountDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(JointAccountDetail aJointAccountDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("jountAccountDetail", aJointAccountDetail);
		arg.put("jountAccountDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/JointAccountDetail/JointAccountDetailDialog.zul", null,
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
	public void onCheck$fromApproved(Event event) throws Exception {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) throws Exception {
		search();
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

}