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
 * * FileName : IRRCodeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2017 * * Modified Date
 * : 21-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.irrcode;

import java.util.Map;

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

import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennant.backend.service.applicationmaster.IRRCodeService;
import com.pennant.webui.applicationmaster.irrcode.model.IRRCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.applicationmaster/IRRCode/IRRCodeList.zul file.
 * 
 */
public class IRRCodeListCtrl extends GFCBaseListCtrl<IRRCode> {
	private static final long serialVersionUID = 1L;

	protected Window window_IRRCodeList;
	protected Borderlayout borderLayout_IRRCodeList;
	protected Paging pagingIRRCodeList;
	protected Listbox listBoxIRRCode;

	// List headers
	protected Listheader listheader_IRRCode;
	protected Listheader listheader_IRRCodeDesc;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_IRRCodeList_NewIRRCode;
	protected Button button_IRRCodeList_IRRCodeSearch;

	// Search Fields
	protected Textbox iRRCode; // autowired
	protected Textbox iRRCodeDesc; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_IRRCode;
	protected Listbox sortOperator_IRRCodeDesc;
	protected Listbox sortOperator_Active;

	private transient IRRCodeService iRRCodeService;

	/**
	 * default constructor.<br>
	 */
	public IRRCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "IRRCode";
		super.pageRightName = "IRRCodeList";
		super.tableName = "IRRCodes_AView";
		super.queueTableName = "IRRCodes_View";
		super.enquiryTableName = "IRRCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_IRRCodeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_IRRCodeList, borderLayout_IRRCodeList, listBoxIRRCode, pagingIRRCodeList);
		setItemRender(new IRRCodeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_IRRCodeList_IRRCodeSearch);
		registerButton(button_IRRCodeList_NewIRRCode, "button_IRRCodeList_NewIRRCode", true);

		registerField("iRRID");
		registerField("iRRCode", listheader_IRRCode, SortOrder.NONE, iRRCode, sortOperator_IRRCode, Operators.STRING);
		registerField("iRRCodeDesc", listheader_IRRCodeDesc, SortOrder.NONE, iRRCodeDesc, sortOperator_IRRCodeDesc,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_IRRCodeList_IRRCodeSearch(Event event) {
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
	public void onClick$button_IRRCodeList_NewIRRCode(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		IRRCode irrcode = new IRRCode();
		irrcode.setNewRecord(true);
		// irrcode.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(irrcode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onIRRCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxIRRCode.getSelectedItem();
		final long iRRID = (long) selectedItem.getAttribute("iRRID");
		IRRCode irrcode = iRRCodeService.getIRRCode(iRRID);

		if (irrcode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  IRRID =? ");

		if (doCheckAuthority(irrcode, whereCond.toString(), new Object[] { irrcode.getIRRID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && irrcode.getWorkflowId() == 0) {
				irrcode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(irrcode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param irrcode The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(IRRCode irrcode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		irrcode.setWorkflowId(getWorkFlowId());
		arg.put("irrcode", irrcode);
		arg.put("irrcodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/IRRCode/IRRCodeDialog.zul", null, arg);
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

	public void setIRRCodeService(IRRCodeService iRRCodeService) {
		this.iRRCodeService = iRRCodeService;
	}
}