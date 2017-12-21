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
 * FileName    		:  CorpRelationCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.corprelationcode;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.service.applicationmaster.CorpRelationCodeService;
import com.pennant.webui.applicationmaster.corprelationcode.model.CorpRelationCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CorpRelationCode/CorpRelationCodeList.zul file.
 */
public class CorpRelationCodeListCtrl extends GFCBaseListCtrl<CorpRelationCode> {
	private static final long serialVersionUID = -2566872901248774242L;
	private static final Logger logger = Logger.getLogger(CorpRelationCodeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CorpRelationCodeList;
	protected Borderlayout borderLayout_CorpRelationCodeList;
	protected Paging pagingCorpRelationCodeList;
	protected Listbox listBoxCorpRelationCode;

	protected Textbox corpRelationCode;
	protected Textbox corpRelationDesc;
	protected Checkbox corpRelationIsActive;

	protected Listbox sortOperator_corpRelationCode;
	protected Listbox sortOperator_corpRelationDesc;
	protected Listbox sortOperator_corpRelationIsActive;

	// List headers
	protected Listheader listheader_CorpRelationCode;
	protected Listheader listheader_CorpRelationDesc;
	protected Listheader listheader_CorpRelationIsActive;

	// checkRights
	protected Button button_CorpRelationCodeList_NewCorpRelationCode;
	protected Button button_CorpRelationCodeList_CorpRelationCodeSearchDialog;

	private transient CorpRelationCodeService corpRelationCodeService;

	/**
	 * default constructor.<br>
	 */
	public CorpRelationCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CorpRelationCode";
		super.pageRightName = "CorpRelationCodeList";
		super.tableName = "BMTCorpRelationCodes_AView";
		super.queueTableName = "BMTCorpRelationCodes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CorpRelationCodeList(Event event) {

		// Set the page level components.
		setPageComponents(window_CorpRelationCodeList, borderLayout_CorpRelationCodeList, listBoxCorpRelationCode,
				pagingCorpRelationCodeList);
		setItemRender(new CorpRelationCodeListModelItemRenderer());
		// Register buttons and fields.
		registerButton(button_CorpRelationCodeList_NewCorpRelationCode,
				"button_CorpRelationCodeList_NewCorpRelationCode", true);
		registerButton(button_CorpRelationCodeList_CorpRelationCodeSearchDialog);

		registerField("corpRelationCode", listheader_CorpRelationCode, SortOrder.ASC, corpRelationCode,
				sortOperator_corpRelationCode, Operators.STRING);
		registerField("corpRelationDesc", listheader_CorpRelationDesc, SortOrder.NONE, corpRelationDesc,
				sortOperator_corpRelationDesc, Operators.STRING);
		registerField("corpRelationIsActive", listheader_CorpRelationIsActive, SortOrder.NONE, corpRelationIsActive,
				sortOperator_corpRelationIsActive, Operators.BOOLEAN);

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
	public void onClick$button_CorpRelationCodeList_CorpRelationCodeSearchDialog(Event event) {
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
	public void onClick$button_CorpRelationCodeList_NewCorpRelationCode(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		CorpRelationCode corpRelationCode = new CorpRelationCode();
		corpRelationCode.setNewRecord(true);
		corpRelationCode.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(corpRelationCode);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCorpRelationCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCorpRelationCode.getSelectedItem();
		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		CorpRelationCode corpRelationCode = corpRelationCodeService.getCorpRelationCodeById(id);

		if (corpRelationCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CorpRelationCode='" + corpRelationCode.getCorpRelationCode() + "' AND version="
				+ corpRelationCode.getVersion() + " ";

		if (doCheckAuthority(corpRelationCode, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && corpRelationCode.getWorkflowId() == 0) {
				corpRelationCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(corpRelationCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCorpRelationCode
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CorpRelationCode aCorpRelationCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("corpRelationCode", aCorpRelationCode);
		arg.put("corpRelationCodeListCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CorpRelationCode/CorpRelationCodeDialog.zul",
					null, arg);
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

	public void setCorpRelationCodeService(CorpRelationCodeService corpRelationCodeService) {
		this.corpRelationCodeService = corpRelationCodeService;
	}

}