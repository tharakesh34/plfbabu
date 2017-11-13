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
 * FileName    		:  ExtendedFieldDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.solutionfactory.extendedfielddetail;

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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.solutionfactory.extendedfielddetail.model.ExtendedFieldDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/ExtendedFieldDetail/ExtendedFieldDetailList.zul
 * file.
 */
public class ExtendedFieldDetailListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> {
	private static final long serialVersionUID = 7866684540841299572L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldDetailListCtrl.class);

	protected Window window_ExtendedFieldDetailList;
	protected Borderlayout borderLayout_ExtendedFieldDetailList;
	protected Paging pagingExtendedFieldDetailList;
	protected Listbox listBoxExtendedFieldDetail;

	protected Listheader listheader_FieldName;
	protected Listheader listheader_FieldType;

	protected Button button_ExtendedFieldDetailList_NewExtendedFieldDetail;
	protected Button button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog;

	protected Combobox moduleName;
	protected Combobox subModuleName;

	protected Listbox sortOperator_moduleName;
	protected Listbox sortOperator_subModuleName;

	private transient ExtendedFieldDetailService extendedFieldDetailService;

	private HashMap<String, HashMap<String, String>> moduleMap = PennantStaticListUtil.getModuleName();
	private List<ValueLabel> modulesList = null;

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ExtendedFieldHeader";
		super.pageRightName = "ExtendedFieldDetailList";
		super.tableName = "ExtendedFieldHeader_AView";
		super.queueTableName = "ExtendedFieldHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExtendedFieldDetailList(Event event) {
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

		// Set the page level components.
		setPageComponents(window_ExtendedFieldDetailList, borderLayout_ExtendedFieldDetailList,
				listBoxExtendedFieldDetail, pagingExtendedFieldDetailList);
		setItemRender(new ExtendedFieldDetailListModelItemRenderer(modulesList));

		// Register buttons and fields.
		registerButton(button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog);

		fillComboBox(moduleName, null, modulesList, "");
		fillsubModule(subModuleName, "", "");

		registerField("moduleName", listheader_FieldName, SortOrder.ASC, moduleName, sortOperator_moduleName,
				Operators.STRING);
		registerField("subModuleName", listheader_FieldType, SortOrder.ASC, subModuleName, sortOperator_subModuleName,
				Operators.STRING);
		registerField("moduleId");
		registerField("tabHeading");
		registerField("numberOfColumns");

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
	public void onClick$button_ExtendedFieldDetailList_ExtendedFieldDetailSearchDialog(Event event) {
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onExtendedFieldDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxExtendedFieldDetail.getSelectedItem();

		// Get the selected entity.
		ExtendedFieldHeader aExtendedFieldHeader = (ExtendedFieldHeader) selectedItem.getAttribute("data");
		ExtendedFieldHeader extendedFieldHeader = extendedFieldDetailService
				.getExtendedFieldHeaderById((ExtendedFieldHeader) aExtendedFieldHeader);

		if (extendedFieldHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ModuleName='" + extendedFieldHeader.getModuleName() + "' AND version="
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
	 * @param aExtendedFieldHeader
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("extendedFieldHeader", aExtendedFieldHeader);
		arg.put("extendedFieldDetailListCtrl", this);
		arg.put("moduleid", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * onChange Event For combobox moduleName
	 */
	public void onChange$moduleName(Event event) {
		logger.debug("Entering  :" + event.toString());
		if (!"#".equals(this.moduleName.getSelectedItem().getValue().toString())) {
			fillsubModule(subModuleName, this.moduleName.getSelectedItem().getValue().toString(), "");
		} else {
			fillsubModule(subModuleName, "", "");
		}
		logger.debug("Leaving  :" + event.toString());
	}

	/**
	 * method For filling submodules list
	 */
	private void fillsubModule(Combobox combobox, String moduleName, String value) {
		if (this.moduleName.getSelectedItem() != null) {
			HashMap<String, String> hashMap = PennantStaticListUtil.getModuleName().get(moduleName) == null ? new HashMap<String, String>()
					: PennantStaticListUtil.getModuleName().get(moduleName);
			ArrayList<String> arrayList = new ArrayList<String>(hashMap.keySet());
			subModuleName.getItems().clear();
			Comboitem comboitem = new Comboitem();
			comboitem.setLabel("----Select-----");
			comboitem.setValue("#");
			subModuleName.appendChild(comboitem);
			subModuleName.setSelectedItem(comboitem);
			if (arrayList != null) {
				for (int i = 0; i < arrayList.size(); i++) {
					comboitem = new Comboitem();
					comboitem.setLabel(Labels.getLabel("label_ExtendedField_" + arrayList.get(i)));
					comboitem.setValue(arrayList.get(i));
					subModuleName.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(arrayList.get(i))) {
						subModuleName.setSelectedItem(comboitem);
					}
				}
			}
		} else {
			subModuleName.getItems().clear();
		}
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

	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}
}