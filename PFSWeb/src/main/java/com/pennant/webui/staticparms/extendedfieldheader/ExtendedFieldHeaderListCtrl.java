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
 * FileName    		:  ExtendedFieldHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.extendedfieldheader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.extendedfields.ExtendedFieldHeader;
import com.pennant.backend.service.staticparms.ExtendedFieldHeaderService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.staticparms.extendedfieldheader.model.ExtendedFieldHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/ExtendedFieldHeader/ExtendedFieldHeaderList.zul
 * file.
 */
public class ExtendedFieldHeaderListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> {
	private static final long serialVersionUID = -1751614637216289000L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldHeaderListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExtendedFieldHeaderList;
	protected Borderlayout borderLayout_ExtendedFieldHeaderList;
	protected Paging pagingExtendedFieldHeaderList;
	protected Listbox listBoxExtendedFieldHeader;

	protected Listheader listheader_ModuleName;
	protected Listheader listheader_SubModuleName;
	protected Listheader listheader_TabHeading;
	protected Listheader listheader_NumberOfColumns;

	protected Button button_ExtendedFieldHeaderList_NewExtendedFieldHeader;
	protected Button button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog;

	protected Combobox moduleName;
	protected Combobox subModuleName;
	protected Textbox tabHeading;
	protected Intbox numberOfColumns;

	protected Listbox sortOperator_moduleName;
	protected Listbox sortOperator_subModuleName;
	protected Listbox sortOperator_tabHeading;
	protected Listbox sortOperator_numberOfColumns;

	private transient ExtendedFieldHeaderService extendedFieldHeaderService;
	private final HashMap<String, HashMap<String, String>> moduleMap = PennantStaticListUtil.getModuleName();
	private List<ValueLabel> modulesList = null;

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ExtendedFieldHeader";
		super.pageRightName = "ExtendedFieldHeaderList";
		super.tableName = "ExtendedFieldHeader_AView";
		super.queueTableName = "ExtendedFieldHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExtendedFieldHeaderList(Event event) {
		// Set the page level components.
		setPageComponents(window_ExtendedFieldHeaderList, borderLayout_ExtendedFieldHeaderList,
				listBoxExtendedFieldHeader, pagingExtendedFieldHeaderList);

		// Filling Module Map
		if (modulesList == null) {
			ValueLabel valuLable = null;
			modulesList = new ArrayList<ValueLabel>(moduleMap.size());
			Set<String> moduleKeys = moduleMap.keySet();
			for (String key : moduleKeys) {
				valuLable = new ValueLabel(key, Labels.getLabel("label_ExtendedField_" + key));
				modulesList.add(valuLable);
			}
		}

		// Register buttons and fields.
		registerButton(button_ExtendedFieldHeaderList_NewExtendedFieldHeader,
				"button_ExtendedFieldHeaderList_NewExtendedFieldHeader", true);
		registerButton(button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog);
		
		registerField("moduleId");
		fillComboBox(moduleName, null, modulesList, "");
		registerField("moduleName", listheader_ModuleName, SortOrder.ASC, moduleName, sortOperator_moduleName,
				Operators.STRING);
		fillsubModule(subModuleName, "", "");
		registerField("subModuleName", listheader_SubModuleName, SortOrder.ASC, subModuleName,
				sortOperator_subModuleName, Operators.STRING);
		registerField("tabHeading", listheader_TabHeading, SortOrder.NONE, tabHeading, sortOperator_tabHeading,
				Operators.STRING);
		registerField("numberOfColumns", listheader_NumberOfColumns, SortOrder.NONE, numberOfColumns,
				sortOperator_numberOfColumns, Operators.NUMERIC);
		
		setItemRender(new ExtendedFieldHeaderListModelItemRenderer(modulesList));

		// Render the page and display the data.
		doRenderPage();
		search();
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		
		if(modulesList != null && !modulesList.isEmpty()){
			List<String> moduleFiletrs = new ArrayList<>();
			for (ValueLabel filter : modulesList) {
				moduleFiletrs.add(filter.getValue());
			}
			this.searchObject.addFilterIn("ModuleName", moduleFiletrs);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog(Event event) {
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
	public void onClick$button_ExtendedFieldHeaderList_NewExtendedFieldHeader(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
		extendedFieldHeader.setNewRecord(true);
		extendedFieldHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(extendedFieldHeader);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onExtendedFieldHeaderItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxExtendedFieldHeader.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderService.getExtendedFieldHeaderById(id);

		if (extendedFieldHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ModuleId=" + extendedFieldHeader.getModuleId() + " AND version="
				+ extendedFieldHeader.getVersion() + " ";

		if (doCheckAuthority(extendedFieldHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && extendedFieldHeader.getWorkflowId() == 0) {
				extendedFieldHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(extendedFieldHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param extendedFieldHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ExtendedFieldHeader extendedFieldHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("extendedFieldHeader", extendedFieldHeader);
		arg.put("extendedFieldHeaderListCtrl", this);
		arg.put("modulesList", modulesList);

		try {
			Executions.createComponents("/WEB-INF/pages/StaticParms/ExtendedFieldHeader/ExtendedFieldHeaderDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/*
	 * Method For filling submodules
	 */
	private void fillsubModule(Combobox combobox, String moduleName, String value) {
		if (this.moduleName.getSelectedItem() != null) {
			HashMap<String, String> hashMap = PennantStaticListUtil.getModuleName().get(moduleName);

			if (hashMap == null) {
				hashMap = new HashMap<String, String>();
			}

			ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
			subModuleName.getItems().clear();
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("----Select-----");
			comboitem.setValue("#");
			subModuleName.appendChild(comboitem);
			subModuleName.setSelectedItem(comboitem);

			for (String key : arrayList) {
				comboitem = new Comboitem();
				comboitem.setLabel(Labels.getLabel("label_ExtendedField_" + key));
				comboitem.setValue(key);
				subModuleName.appendChild(comboitem);
				if (StringUtils.trimToEmpty(value).equals(key)) {
					subModuleName.setSelectedItem(comboitem);
				}
			}

		} else {
			subModuleName.getItems().clear();
		}
	}

	/**
	 * onchange Module Name
	 * 
	 * filling the submodule
	 * 
	 * @param event
	 */
	public void onChange$moduleName(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.moduleName.getSelectedItem() != null) {
			String module = this.moduleName.getSelectedItem().getValue().toString();
			fillsubModule(this.subModuleName, module, "");
		}
		logger.debug("Leaving" + event.toString());
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

	public void setExtendedFieldHeaderService(ExtendedFieldHeaderService extendedFieldHeaderService) {
		this.extendedFieldHeaderService = extendedFieldHeaderService;
	}

}