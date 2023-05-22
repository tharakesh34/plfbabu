/**
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
 * * FileName : PSLDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-06-2018 * * Modified
 * Date : 20-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.psldetails;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/AMTMasters/PSLDetail/PSLDetailList.zul file.
 * 
 */
public class PSLDetailListCtrl extends GFCBaseListCtrl<PSLDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_PSLDetailList;
	protected Borderlayout borderLayout_PSLDetailList;
	protected Paging pagingPSLDetailList;
	protected Listbox listBoxPSLDetail;

	// List headers

	// checkRights
	protected Button button_PSLDetailList_NewPSLDetail;
	protected Button button_PSLDetailList_PSLDetailSearch;

	// Search Fields

	private transient PSLDetailService pSLDetailService;

	/**
	 * default constructor.<br>
	 */
	public PSLDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PSLDetail";
		super.pageRightName = "PSLDetailList";
		super.tableName = "PSLDetails_AView";
		super.queueTableName = "PSLDetails_View";
		super.enquiryTableName = "PSLDetails_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PSLDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_PSLDetailList, borderLayout_PSLDetailList, listBoxPSLDetail, pagingPSLDetailList);
		// setItemRender(new PSLDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PSLDetailList_PSLDetailSearch);
		registerButton(button_PSLDetailList_NewPSLDetail, "button_PSLDetailList_NewPSLDetail", true);

		registerField("FinID");
		registerField("finReference");
		registerField("categoryCode");
		registerField("categoryCodeName");
		registerField("weakerSection");
		registerField("weakerSectionName");
		registerField("landHolding");
		registerField("landHoldingName");
		registerField("landArea");
		registerField("landAreaName");
		registerField("sector");
		registerField("sectorName");
		registerField("amount");
		registerField("subCategory");
		registerField("subCategoryName");
		registerField("purpose");
		registerField("purposeName");
		registerField("endUse");
		registerField("endUseName");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PSLDetailList_PSLDetailSearch(Event event) {
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
	public void onClick$button_PSLDetailList_NewPSLDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		PSLDetail psldetail = new PSLDetail();
		psldetail.setNewRecord(true);
		psldetail.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(psldetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onPSLDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPSLDetail.getSelectedItem();
		final long finID = (Long) selectedItem.getAttribute("finID");

		PSLDetail psldetail = pSLDetailService.getPSLDetail(finID);

		if (psldetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  FinReference =?");

		if (doCheckAuthority(psldetail, whereCond.toString(), new Object[] { psldetail.getFinReference() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && psldetail.getWorkflowId() == 0) {
				psldetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(psldetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param psldetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PSLDetail psldetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("pSLDetail", psldetail);
		arg.put("pSLDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/PSLDetail/PSLDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

	public void setPSLDetailService(PSLDetailService pSLDetailService) {
		this.pSLDetailService = pSLDetailService;
	}
}