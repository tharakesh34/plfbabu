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
 * FileName    		:  PresentmentHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.presentmentheader;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentHeaderService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.financemanagement/PresentmentHeader/PresentmentHeaderList.zul file.
 * 
 */
public class PresentmentHeaderListCtrl extends GFCBaseListCtrl<PresentmentHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PresentmentHeaderListCtrl.class);

	protected Window window_PresentmentHeaderList;
	protected Borderlayout borderLayout_PresentmentHeaderList;
	protected Paging pagingPresentmentHeaderList;
	protected Listbox listBoxPresentmentHeader;

	// List headers
	protected Listheader listheader_MandateType;
	protected Listheader listheader_PartnerBankID;
	protected Listheader listheader_Status;

	// checkRights
	protected Button button_PresentmentHeaderList_NewPresentmentHeader;
	protected Button button_PresentmentHeaderList_PresentmentHeaderSearch;

	// Search Fields
    protected Combobox mandateType; // autowired
	protected Textbox partnerBankID; // autowired
    protected Combobox status; // autowired
	
	protected Listbox sortOperator_MandateType;
	protected Listbox sortOperator_PartnerBankID;
	protected Listbox sortOperator_Status;
	
	private transient PresentmentHeaderService presentmentHeaderService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentHeader";
		super.pageRightName = "PresentmentHeaderList";
		super.tableName = "PresentmentHeader_AView";
		super.queueTableName = "PresentmentHeader_View";
		super.enquiryTableName = "PresentmentHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_PresentmentHeaderList, borderLayout_PresentmentHeaderList, listBoxPresentmentHeader,
				pagingPresentmentHeaderList);
		setItemRender(new PresentmentHeaderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_PresentmentHeaderList_PresentmentHeaderSearch);
		registerButton(button_PresentmentHeaderList_NewPresentmentHeader, "button_PresentmentHeaderList_NewPresentmentHeader", true);

		registerField("presentmentID");
		registerField("mandateType", listheader_MandateType, SortOrder.NONE, mandateType, sortOperator_MandateType, Operators.STRING);
		registerField("mandateTypeName");
		registerField("partnerBankID", listheader_PartnerBankID, SortOrder.NONE, partnerBankID, sortOperator_PartnerBankID, Operators.STRING);
		registerField("partnerBankIDName");
		registerField("presentmentDate");		
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status, Operators.STRING);
		registerField("statusName");

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
	public void onClick$button_PresentmentHeaderList_PresentmentHeaderSearch(Event event) {
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
	public void onClick$button_PresentmentHeaderList_NewPresentmentHeader(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		PresentmentHeader presentmentheader = new PresentmentHeader();
		presentmentheader.setNewRecord(true);
		presentmentheader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(presentmentheader);

		logger.debug(Literal.LEAVING);
	}


	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onPresentmentHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");
		
		// Get the selected record.
		Listitem selectedItem = this.listBoxPresentmentHeader.getSelectedItem();
		final long presentmentID = (long) selectedItem.getAttribute("presentmentID");
		PresentmentHeader presentmentheader = presentmentHeaderService.getPresentmentHeader(presentmentID);

		if (presentmentheader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		
		StringBuffer whereCond= new StringBuffer();
		whereCond.append("  AND  PresentmentID = ");
		whereCond.append( presentmentheader.getPresentmentID());
		whereCond.append(" AND  version=");
		whereCond.append(presentmentheader.getVersion());
	
		if (doCheckAuthority(presentmentheader, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && presentmentheader.getWorkflowId() == 0) {
				presentmentheader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(presentmentheader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param presentmentheader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(PresentmentHeader presentmentheader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("presentmentheader", presentmentheader);
		arg.put("presentmentheaderListCtrl", this);
		
		try {
			Executions.createComponents("/WEB-INF/pages/com.pennant.financemanagement/PresentmentHeader/PresentmentHeaderDialog.zul", null, arg);
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
	
	/**
	 * Item renderer for list items in the list box.
	 * 
	 */
	public class PresentmentHeaderListModelItemRenderer implements ListitemRenderer<PresentmentHeader>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, PresentmentHeader presentmentDetail, int count) throws Exception {
		}
	}
	
	public void setPresentmentHeaderService(PresentmentHeaderService presentmentHeaderService) {
		this.presentmentHeaderService = presentmentHeaderService;
	}
}