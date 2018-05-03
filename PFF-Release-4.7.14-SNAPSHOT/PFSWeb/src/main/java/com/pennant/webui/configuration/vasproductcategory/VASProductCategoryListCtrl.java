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
 * FileName    		:  VASProductCategoryListCtrl.java                                                   * 	  
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

package com.pennant.webui.configuration.vasproductcategory;

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

import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.service.vasproduct.VASProductCategoryService;
import com.pennant.webui.configuration.vasproductcategory.model.VASProductCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/vasproduct/VASProductCategory/VASProductCategoryList.zul file.
 * 
 */
public class VASProductCategoryListCtrl extends GFCBaseListCtrl<VASProductCategory> {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger.getLogger(VASProductCategoryListCtrl.class);

	protected Window							window_VASProductCategoryList;
	protected Borderlayout						borderLayout_VASProductCategoryList;
	protected Paging							pagingVASProductCategoryList;
	protected Listbox							listBoxVASProductCategory;

	protected Listheader						listheader_ProductCtg;
	protected Listheader						listheader_ProductCtgDesc;
	protected Listheader 						listheader_ProductCtgActive;

	protected Button							button_VASProductCategoryList_NewVASProductCategory;
	protected Button							button_VASProductCategoryList_VASProductCategorySearch;

	protected Textbox							productCtg;
	protected Textbox							productCtgDesc;
	protected Checkbox							active;

	protected Listbox							sortOperator_ProductCtg;
	protected Listbox							sortOperator_ProductCtgDesc;
	protected Listbox							sortOperator_Active;

	private transient VASProductCategoryService	vASProductCategoryService;

	/**
	 * default constructor.<br>
	 */
	public VASProductCategoryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VASProductCategory";
		super.pageRightName = "VASProductCategoryList";
		super.tableName = "VasProductCategory_AView";
		super.queueTableName = "VasProductCategory_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_VASProductCategoryList(Event event) {
		// Set the page level components.
		setPageComponents(window_VASProductCategoryList, borderLayout_VASProductCategoryList,
				listBoxVASProductCategory, pagingVASProductCategoryList);
		setItemRender(new VASProductCategoryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_VASProductCategoryList_NewVASProductCategory,
				"button_VASProductCategoryList_NewVASProductCategory", true);
		registerButton(button_VASProductCategoryList_VASProductCategorySearch);

		registerField("productCtg", listheader_ProductCtg, SortOrder.ASC, productCtg, sortOperator_ProductCtg,
				Operators.STRING);
		registerField("productCtgDesc", listheader_ProductCtgDesc, SortOrder.NONE, productCtgDesc,
				sortOperator_ProductCtgDesc, Operators.STRING);
		registerField("active", listheader_ProductCtgActive, SortOrder.NONE, active,
				sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Entering");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_VASProductCategoryList_VASProductCategorySearch(Event event) {
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
	public void onClick$button_VASProductCategoryList_NewVASProductCategory(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		VASProductCategory vASProductCategory = new VASProductCategory();
		vASProductCategory.setNewRecord(true);
		vASProductCategory.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(vASProductCategory);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVASProductCategoryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxVASProductCategory.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		VASProductCategory vASProductCategory = vASProductCategoryService.getVASProductCategoryById(id);

		if (vASProductCategory == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProductCtg='" + vASProductCategory.getProductCtg() + "' AND version="
				+ vASProductCategory.getVersion() + " ";

		if (doCheckAuthority(vASProductCategory, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vASProductCategory.getWorkflowId() == 0) {
				vASProductCategory.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vASProductCategory);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aVASProductCategory
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VASProductCategory aVASProductCategory) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASProductCategory", aVASProductCategory);
		arg.put("vASProductCategoryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/VASProductCategory/VASProductCategoryDialog.zul",
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

	public void setVASProductCategoryService(VASProductCategoryService vASProductCategoryService) {
		this.vASProductCategoryService = vASProductCategoryService;
	}

}