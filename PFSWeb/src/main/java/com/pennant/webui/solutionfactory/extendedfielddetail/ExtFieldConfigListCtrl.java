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
 * FileName    		:  CollateralStructureListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.FinServicingEvent;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtFieldConfigListCtrl extends GFCBaseListCtrl<ExtendedFieldHeader> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ExtFieldConfigListCtrl.class);

	protected Window window_ExtFieldConfigList;
	protected Borderlayout borderLayout_ExtFieldConfigList;
	protected Listbox listBoxExtFieldConfig;
	protected Paging pagingExtFieldConfigList;

	protected Listheader listheader_ModuleName;
	protected Listheader listheader_SubModuleName;
	protected Listheader listheader_Event;
	protected Listheader listheader_TabHeading;
	protected Listheader listheader_PreValidationReq;
	protected Listheader listheader_PostValidationReq;
	protected Listheader listheader_Active;

	protected Button button_ExtendedFieldHeaderList_NewExtendedFieldHeader;
	protected Button button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog;

	protected Uppercasebox moduleName;
	protected Textbox subModuleName;
	protected Combobox finEvent;
	protected Textbox tabHeading;
	protected Checkbox preValidationReq;
	protected Checkbox postValidationReq;

	protected Listbox sortOperator_ModuleName;
	protected Listbox sortOperator_SubModuleName;
	protected Listbox sortOperator_event;
	protected Listbox sortOperator_TabHeading;
	protected Listbox sortOperator_PreValidationReq;
	protected Listbox sortOperator_PostValidationReq;
	private transient ExtFieldConfigService extFieldConfigService;
	
	List<ValueLabel> listLtvType = PennantStaticListUtil.getListLtvTypes();

	/**
	 * default constructor.<br>
	 */
	public ExtFieldConfigListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ExtendedFieldHeader";
		super.pageRightName = "ExtendedFieldConfigList";
		super.tableName = "ExtendedFieldHeader_AView";
		super.queueTableName = "ExtendedFieldHeader_View";
		super.enquiryTableName = "ExtendedFieldHeader_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		List<String> moduleNames = new ArrayList<String>();
		moduleNames.add(ExtendedFieldConstants.MODULE_CUSTOMER);
		moduleNames.add(ExtendedFieldConstants.MODULE_LOAN);
		moduleNames.add(ExtendedFieldConstants.MODULE_VERIFICATION);
		this.searchObject.addFilterIn("MODULENAME", moduleNames);
	}
	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExtFieldConfigList(Event event) throws Exception {
		// Set the page level components.
		setPageComponents(window_ExtFieldConfigList, borderLayout_ExtFieldConfigList, listBoxExtFieldConfig, pagingExtFieldConfigList);
		setItemRender(new ExtFieldConfigListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ExtendedFieldHeaderList_NewExtendedFieldHeader, "button_ExtendedFieldConfigList_NewExtendedFieldConfig", true);
		registerButton(button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog);

		registerField("ModuleName", listheader_ModuleName, SortOrder.ASC, moduleName, sortOperator_ModuleName,Operators.STRING);
		registerField("SubModuleName", listheader_SubModuleName, SortOrder.ASC, subModuleName, sortOperator_SubModuleName,Operators.STRING);
		registerField("TabHeading", listheader_TabHeading, SortOrder.NONE, tabHeading, sortOperator_TabHeading, Operators.STRING);
		registerField("preValidationReq", listheader_PreValidationReq, SortOrder.ASC, preValidationReq, sortOperator_PreValidationReq, Operators.BOOLEAN);
		registerField("postValidationReq", listheader_PostValidationReq, SortOrder.ASC, postValidationReq, sortOperator_PostValidationReq, Operators.BOOLEAN);
		registerField("event", listheader_Event, SortOrder.NONE, finEvent, sortOperator_event,
				Operators.STRING);
		
		List<FinServicingEvent> events = PennantStaticListUtil.getFinEvents(true);
		fillComboBox(finEvent, null, PennantStaticListUtil.getValueLabels(events), "");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ExtendedFieldHeaderList_ExtendedFieldHeaderSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button.
	 * Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$button_ExtendedFieldHeaderList_NewExtendedFieldHeader(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
		extendedFieldHeader.setNewRecord(true);
		extendedFieldHeader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(extendedFieldHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCustomerExteItemDoubleClicked(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		//Listitem selectedItem = this.listBoxCollateralStructure.getSelectedItem();
		Listitem selectedItem = (Listitem) event.getOrigin().getTarget();

		// Get the selected entity.
		ExtendedFieldHeader object = (ExtendedFieldHeader) selectedItem.getAttribute("Object");
	
		ExtendedFieldHeader extendedFieldHeader = getExtFieldConfigService()
				.getExtendedFieldHeaderByModule(object.getModuleName(), object.getSubModuleName(), object.getEvent());
		if (extendedFieldHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND Modulename='" + extendedFieldHeader.getModuleName()+ "' AND version=" + extendedFieldHeader.getVersion() + " ";

		if (doCheckAuthority(extendedFieldHeader, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && extendedFieldHeader.getWorkflowId() == 0) {
				extendedFieldHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(extendedFieldHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ExtendedFieldHeader extendedFieldHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("ExtendedFieldHeader", extendedFieldHeader);
		arg.put("ConfigListCtrl", this);
		try { 
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtFieldConfigDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	
	public class ExtFieldConfigListModelItemRenderer implements ListitemRenderer<ExtendedFieldHeader>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, ExtendedFieldHeader ext, int count) throws Exception {

			Listcell lc;

			lc = new Listcell(ext.getModuleName());
			lc.setParent(item);
			
			lc = new Listcell(ext.getSubModuleName());
			lc.setParent(item);
			
			lc = new Listcell(ext.getEvent());
			lc.setParent(item);

			lc = new Listcell(ext.getTabHeading());
			lc.setParent(item);
			
			lc = new Listcell();
			final Checkbox cbPreValidationReq = new Checkbox();
			cbPreValidationReq.setDisabled(true);
			cbPreValidationReq.setChecked(ext.isPreValidationReq());
			lc.appendChild(cbPreValidationReq);
			lc.setParent(item);

			lc = new Listcell();
			final Checkbox cbPostValidationReq = new Checkbox();
			cbPostValidationReq.setDisabled(true);
			cbPostValidationReq.setChecked(ext.isPostValidationReq());
			lc.appendChild(cbPostValidationReq);
			lc.setParent(item);


			lc = new Listcell(ext.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(ext.getRecordType()));
			lc.setParent(item);

			item.setAttribute("Object", ext);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerExteItemDoubleClicked");
		}
	}
	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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
	 * @return the extFieldConfigService
	 */
	public ExtFieldConfigService getExtFieldConfigService() {
		return extFieldConfigService;
	}

	/**
	 * @param extFieldConfigService the extFieldConfigService to set
	 */
	public void setExtFieldConfigService(ExtFieldConfigService extFieldConfigService) {
		this.extFieldConfigService = extFieldConfigService;
	}

	 
}