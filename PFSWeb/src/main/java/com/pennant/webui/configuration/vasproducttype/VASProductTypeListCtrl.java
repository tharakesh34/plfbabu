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
 * FileName    		:  VASProductTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-01-2017    														*
 *                                                                  						*
 * Modified Date    :  09-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-01-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.configuration.vasproducttype;

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

import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennant.backend.service.vasproducttype.VASProductTypeService;
import com.pennant.webui.configuration.vasproducttype.model.VASProductTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/VASProductType/VASProductType/VASProductTypeList.zul file
 * 
 */
public class VASProductTypeListCtrl extends GFCBaseListCtrl<VASProductType> {

	private static final long				serialVersionUID	= 1L;
	private static final Logger				logger				= Logger.getLogger(VASProductTypeListCtrl.class);

	protected Window						window_VASProductTypeList;
	protected Borderlayout					borderLayout_VASProductTypeList;
	protected Paging						pagingVASProductTypeList;
	protected Listbox						listBoxVASProductType;

	protected Listheader					listheader_ProductType;
	protected Listheader					listheader_ProductTypeDesc;
	protected Listheader					listheader_ProductCtg;
	protected Listheader					listheader_ProductTypeActive;

	protected Button						button_VASProductTypeList_NewVASProductType;
	protected Button						button_VASProductTypeList_VASProductTypeSearch;

	protected Textbox						productType;
	protected Textbox						productTypeDesc;
	protected Textbox						productCtg;
	protected Checkbox						active;

	protected Listbox						sortOperator_ProductType;
	protected Listbox						sortOperator_ProductTypeDesc;
	protected Listbox						sortOperator_ProductCtg;
	protected Listbox						sortOperator_Active;

	private transient VASProductTypeService	vASProductTypeService;

	/**
	 * default constructor.<br>
	 */
	public VASProductTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VASProductType";
		super.pageRightName = "VASProductTypeList";
		super.tableName = "VasProductType_AView";
		super.queueTableName = "VasProductType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_VASProductTypeList(Event event) {
		logger.debug("Entering");
		
		// Set the page level components.
		setPageComponents(window_VASProductTypeList, borderLayout_VASProductTypeList, listBoxVASProductType,
				pagingVASProductTypeList);
		setItemRender(new VASProductTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VASProductTypeList_NewVASProductType, "button_VASProductTypeList_NewVASProductType", true);
		registerButton(button_VASProductTypeList_VASProductTypeSearch);

		registerField("productType", listheader_ProductType, SortOrder.ASC, productType, sortOperator_ProductType,
				Operators.STRING);
		registerField("productTypeDesc", listheader_ProductTypeDesc, SortOrder.NONE, productTypeDesc,
				sortOperator_ProductTypeDesc, Operators.STRING);
		registerField("productCtg", listheader_ProductCtg, SortOrder.NONE, productCtg, sortOperator_ProductCtg,
				Operators.STRING);
		registerField("active", listheader_ProductTypeActive, SortOrder.NONE, active,
				sortOperator_Active, Operators.BOOLEAN);

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
	public void onClick$button_VASProductTypeList_VASProductTypeSearch(Event event) {
		logger.debug("Entering");
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug("Entering");
		doReset();
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_VASProductTypeList_NewVASProductType(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		VASProductType vASProductType = new VASProductType();
		vASProductType.setNewRecord(true);
		vASProductType.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(vASProductType);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVASProductTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVASProductType.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		VASProductType vASProductType = vASProductTypeService.getVASProductTypeById(id);

		if (vASProductType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProductType='" + vASProductType.getProductType() + "' AND version="
				+ vASProductType.getVersion() + " ";

		if (doCheckAuthority(vASProductType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vASProductType.getWorkflowId() == 0) {
				vASProductType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vASProductType);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aVASProductType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VASProductType aVASProductType) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASProductType", aVASProductType);
		arg.put("vASProductTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/VASProductType/VASProductTypeDialog.zul", null,
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
		logger.debug("Entering");
		doPrintResults();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		logger.debug("Entering");
		doShowHelp(event);
		logger.debug("Leaving");
	}

	public void setVASProductTypeService(VASProductTypeService vASProductTypeService) {
		this.vASProductTypeService = vASProductTypeService;
	}

}