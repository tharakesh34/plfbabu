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
 * * FileName : CovenantTypeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-02-2019 * * Modified
 * Date : 06-02-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-02-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.covenant;

import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.finance.covenant.CovenantTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.covenant.covenanttype.model.CovenantTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
// import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
// import com.pennanttech.pfs.core.Literal;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance.Covenant/CovenantType/CovenantTypeList.zul file.
 * 
 */
public class CovenantTypeListCtrl extends GFCBaseListCtrl<CovenantType> {
	private static final long serialVersionUID = 1L;

	protected Window window_CovenantTypeList;
	protected Borderlayout borderLayout_CovenantTypeList;
	protected Paging pagingCovenantTypeList;
	protected Listbox listBoxCovenantType;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Category;
	protected Listheader listheader_DocType;

	// checkRights
	protected Button button_CovenantTypeList_NewCovenantType;
	protected Button button_CovenantTypeList_CovenantTypeSearch;

	// Search Fields
	protected Uppercasebox code;
	protected Textbox description;
	protected Combobox category;
	protected ExtendedCombobox docType;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_Category;
	protected Listbox sortOperator_DocType;

	private transient CovenantTypeService covenantTypeService;
	private transient List<Property> listCategory = PennantStaticListUtil.getCovenantCategories();

	/**
	 * default constructor.<br>
	 */
	public CovenantTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CovenantType";
		super.pageRightName = "CovenantTypeList";
		super.tableName = "COVENANT_TYPES_AView";
		super.queueTableName = "COVENANT_TYPES_View";
		super.enquiryTableName = "COVENANT_TYPES_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CovenantTypeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_CovenantTypeList, borderLayout_CovenantTypeList, listBoxCovenantType,
				pagingCovenantTypeList);
		setItemRender(new CovenantTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CovenantTypeList_CovenantTypeSearch);
		registerButton(button_CovenantTypeList_NewCovenantType, "button_CovenantTypeList_NewCovenantType", true);
		registerField("Id");
		registerField("Category", listheader_Category, SortOrder.NONE, category, sortOperator_Category,
				Operators.STRING);
		registerField("Code", listheader_Code, SortOrder.ASC, code, sortOperator_Code, Operators.STRING);
		registerField("Description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("DocType", listheader_DocType, SortOrder.ASC, docType, sortOperator_DocType, Operators.DEFAULT);
		registerField("DocTypeName");
		// registerField("Los");
		// registerField("Otc");
		registerField("CovenantType");
		registerField("AllowPostPonement");
		doSetFieldProperties();
		fillList(this.category, listCategory, null);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_CovenantTypeList_CovenantTypeSearch(Event event) {
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
	public void onClick$button_CovenantTypeList_NewCovenantType(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		CovenantType covenanttype = new CovenantType();
		covenanttype.setNewRecord(true);
		covenanttype.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(covenanttype);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.docType.setModuleName("DocumentType");
		this.docType.setValueColumn("DocTypeCode");
		this.docType.setDescColumn("DocTypeDesc");
		this.docType.setValidateColumns(new String[] { "DocTypeCode" });

		logger.debug(Literal.LEAVING);
	}

	public void onCovenantTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCovenantType.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		CovenantType covenanttype = covenantTypeService.getCovenantType(id);

		if (covenanttype == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(covenanttype, whereCond.toString(), new Object[] { covenanttype.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && covenanttype.getWorkflowId() == 0) {
				covenanttype.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(covenanttype);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param covenanttype The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CovenantType covenanttype) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("covenantType", covenanttype);
		arg.put("covenantTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Covenant/CovenantTypeDialog.zul", null, arg);
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

	public void setCovenantTypeService(CovenantTypeService covenantTypeService) {
		this.covenantTypeService = covenantTypeService;
	}

	public void onFulfill$docType(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = this.docType.getObject();

		if (dataObject instanceof String) {
			this.docType.setValue(dataObject.toString());
			this.docType.setDescription("");
			this.docType.setAttribute("docType", null);
		} else {
			DocumentType docType = (DocumentType) dataObject;
			if (docType != null) {
				this.docType.setAttribute("docType", docType.getDocTypeCode());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void fillList(Combobox component, List<Property> properties, Object selectedKey) {
		logger.trace(Literal.ENTERING);

		component.setReadonly(true);

		// Clear the existing items.
		component.getChildren().clear();

		// Add the default item.
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));

		component.appendChild(comboitem);
		component.setSelectedItem(comboitem);

		// Add the list of items.
		for (Property property : properties) {
			comboitem = new Comboitem();
			comboitem.setValue(property.getKey());
			comboitem.setLabel(property.getValue());

			component.appendChild(comboitem);

			if (property.getKey().equals(selectedKey)) {
				component.setSelectedItem(comboitem);
			}
		}

		logger.trace(Literal.LEAVING);
	}
}