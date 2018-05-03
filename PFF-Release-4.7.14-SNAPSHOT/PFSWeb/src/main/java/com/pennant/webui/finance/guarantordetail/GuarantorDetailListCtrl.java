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
 * FileName    		:  GuarantorDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.guarantordetail;

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

import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.webui.finance.guarantordetail.model.GuarantorDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailList.zul file.
 */
public class GuarantorDetailListCtrl extends GFCBaseListCtrl<GuarantorDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GuarantorDetailListCtrl.class);

	protected Window window_GuarantorDetailList;
	protected Borderlayout borderLayout_GuarantorDetailList;
	protected Paging pagingGuarantorDetailList;
	protected Listbox listBoxGuarantorDetail;

	protected Listheader listheader_BankCustomer;
	protected Listheader listheader_GuarantorCIF;
	protected Listheader listheader_GuarantorIDType;
	protected Listheader listheader_GuarantorIDNumber;
	protected Listheader listheader_Name;
	protected Listheader listheader_GuranteePercentage;
	protected Listheader listheader_MobileNo;
	protected Listheader listheader_EmailId;
	protected Listheader listheader_GuarantorProof;
	protected Listheader listheader_GuarantorProofName;

	protected Button button_GuarantorDetailList_NewGuarantorDetail;
	protected Button button_GuarantorDetailList_GuarantorDetailSearch;

	private transient GuarantorDetailService guarantorDetailService;


	/**
	 * default constructor.<br>
	 */
	public GuarantorDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GuarantorDetail";
		super.pageRightName = "GuarantorDetailList";
		super.tableName = "FinGuarantorsDetails_AView";
		super.queueTableName = "FinGuarantorsDetails_View";
		super.enquiryTableName = "FinGuarantorsDetails_TView";
	}

	public void onCreate$window_GuarantorDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_GuarantorDetailList, borderLayout_GuarantorDetailList, listBoxGuarantorDetail,
				pagingGuarantorDetailList);
		setItemRender(new GuarantorDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_GuarantorDetailList_NewGuarantorDetail, "button_GuarantorDetailList_NewGuarantorDetail",
				true);
		registerButton(button_GuarantorDetailList_GuarantorDetailSearch);

		registerField("GuarantorId", SortOrder.ASC);
		registerField("guarantorCIF", listheader_GuarantorCIF);
		registerField("guarantorIDType", listheader_GuarantorIDType);
		registerField("guarantorIDNumber", listheader_GuarantorIDNumber);
		registerField("guarantorIDTypeName", listheader_Name);

		registerField("guranteePercentage", listheader_GuranteePercentage);
		registerField("mobileNo", listheader_MobileNo);
		registerField("emailId", listheader_EmailId);
		registerField("guarantorProof", listheader_GuarantorProof);
		registerField("guarantorProofName", listheader_GuarantorProofName);

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
	public void onClick$button_GuarantorDetailList_GuarantorDetailSearch(Event event) {
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
	public void onClick$button_GuarantorDetailList_NewGuarantorDetail(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		GuarantorDetail aGuarantorDetail = new GuarantorDetail();
		aGuarantorDetail.setNewRecord(true);
		aGuarantorDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aGuarantorDetail);

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onGuarantorDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxGuarantorDetail.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		GuarantorDetail guarantorDetail = guarantorDetailService.getGuarantorDetailById(id);

		if (guarantorDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GuarantorID='" + guarantorDetail.getGuarantorId() + "' AND version="
				+ guarantorDetail.getVersion() + " ";

		if (doCheckAuthority(guarantorDetail, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && guarantorDetail.getWorkflowId() == 0) {
				guarantorDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(guarantorDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aGuarantorDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(GuarantorDetail aGuarantorDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("guarantorDetail", aGuarantorDetail);
		arg.put("guarantorDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul", null, arg);
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

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

}