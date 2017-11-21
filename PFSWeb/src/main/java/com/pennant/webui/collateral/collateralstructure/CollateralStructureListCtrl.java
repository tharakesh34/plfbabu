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

package com.pennant.webui.collateral.collateralstructure;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.extendedfields.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.collateral.collateralstructure.model.CollateralStructureListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/CollateralstructureList.zul file.
 */
public class CollateralStructureListCtrl extends GFCBaseListCtrl<CollateralStructure> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CollateralStructureListCtrl.class);

	protected Window window_CollateralStructureList;
	protected Borderlayout borderLayout_CollateralStructureList;
	protected Listbox listBoxCollateralStructure;
	protected Paging pagingCollateralStructureList;

	protected Listheader listheader_CollateralType;
	protected Listheader listheader_CollateralDesc;
	protected Listheader listheader_LtvType;
	protected Listheader listheader_MarketableSecurities;
	protected Listheader listheader_PreValidationReq;
	protected Listheader listheader_PostValidationReq;
	protected Listheader listheader_CollateralLocReq;
	protected Listheader listheader_Active;

	protected Button button_CollateralStructureList_NewCollateralStructure;
	protected Button button_CollateralStructureList_CollateralStructureSearch;

	protected Textbox collateralDesc;
	protected Uppercasebox collateralType;
	protected Combobox ltvType;
	protected Checkbox marketableSecurities;
	protected Checkbox preValidationReq;
	protected Checkbox postValidationReq;
	protected Checkbox active;

	protected Listbox sortOperator_CollateralType;
	protected Listbox sortOperator_CollateralDescription;
	protected Listbox sortOperator_LtvType;
	protected Listbox sortOperator_MarketableSecurities;
	protected Listbox sortOperator_PreValidationReq;
	protected Listbox sortOperator_PostValidationReq;
	protected Listbox sortOperator_Active;

	private transient CollateralStructureService collateralStructureService;
	
	List<ValueLabel> listLtvType = PennantStaticListUtil.getListLtvTypes();

	/**
	 * default constructor.<br>
	 */
	public CollateralStructureListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CollateralStructure";
		super.pageRightName = "CollateralStructureList";
		super.tableName = "CollateralStructure_AView";
		super.queueTableName = "CollateralStructure_View";
		super.enquiryTableName = "CollateralStructure_View";
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CollateralStructureList(Event event)
			throws Exception {
		// Set the page level components.
		setPageComponents(window_CollateralStructureList, borderLayout_CollateralStructureList, listBoxCollateralStructure, pagingCollateralStructureList);
		setItemRender(new CollateralStructureListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CollateralStructureList_NewCollateralStructure, "button_CollateralStructureList_NewCollateralStructure", true);
		registerButton(button_CollateralStructureList_CollateralStructureSearch);

		registerField("collateralType", listheader_CollateralType, SortOrder.ASC, collateralType, sortOperator_CollateralType,Operators.STRING);
		registerField("collateralDesc", listheader_CollateralDesc, SortOrder.ASC, collateralDesc, sortOperator_CollateralDescription,Operators.STRING);
		registerField("ltvType", listheader_LtvType, SortOrder.ASC, ltvType, sortOperator_LtvType, Operators.STRING);
		registerField("marketableSecurities", listheader_MarketableSecurities, SortOrder.ASC, marketableSecurities, sortOperator_MarketableSecurities, Operators.BOOLEAN);
		registerField("preValidationReq", listheader_PreValidationReq, SortOrder.ASC, preValidationReq, sortOperator_PreValidationReq, Operators.BOOLEAN);
		registerField("postValidationReq", listheader_PostValidationReq, SortOrder.ASC, postValidationReq, sortOperator_PostValidationReq, Operators.BOOLEAN);
		registerField("active", listheader_Active, SortOrder.ASC, active, sortOperator_Active, Operators.BOOLEAN);

		fillComboBox(this.ltvType, "", listLtvType, "");
		
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
	public void onClick$button_CollateralStructureList_CollateralStructureSearch(Event event) {
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
	public void onClick$button_CollateralStructureList_NewCollateralStructure(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Create a new entity.
		CollateralStructure aCollateralStructure = new CollateralStructure();
		aCollateralStructure.setNewRecord(true);
		aCollateralStructure.setWorkflowId(getWorkFlowId());
		
		// Copy Button Process
		boolean isCopyProcess = false;
		if (event.getData() != null) {
			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
			ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
			DateConverter dateConverter = new DateConverter(null);
			ConvertUtils.register(dateConverter, Date.class);
			CollateralStructure sourceCol = (CollateralStructure) event.getData();
			BeanUtils.copyProperties(aCollateralStructure, sourceCol);
			aCollateralStructure.setCollateralType("");
			aCollateralStructure.setCollateralDesc("");
			aCollateralStructure.setNewRecord(true);
			aCollateralStructure.setRecordStatus("");
			isCopyProcess = true;

			//Extended Field Details
			ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
			extendedFieldHeader.setTabHeading(sourceCol.getExtendedFieldHeader().getTabHeading());
			extendedFieldHeader.setNumberOfColumns(sourceCol.getExtendedFieldHeader().getNumberOfColumns());
			extendedFieldHeader.setExtendedFieldDetails(new ArrayList<ExtendedFieldDetail>());
			List<ExtendedFieldDetail> fieldDetails = sourceCol.getExtendedFieldHeader().getExtendedFieldDetails();
			if (fieldDetails != null && !fieldDetails.isEmpty()) {
				aCollateralStructure.setExtendedFieldHeader(extendedFieldHeader);
				for (ExtendedFieldDetail oldDetail : fieldDetails) {
					ExtendedFieldDetail newDetail = new ExtendedFieldDetail();
					newDetail.setFieldName(oldDetail.getFieldName());
					newDetail.setFieldLabel(oldDetail.getFieldLabel());
					newDetail.setFieldSeqOrder(oldDetail.getFieldSeqOrder());
					newDetail.setFieldType(oldDetail.getFieldType());
					newDetail.setFieldConstraint(oldDetail.getFieldConstraint());
					newDetail.setFieldPrec(oldDetail.getFieldPrec());
					newDetail.setFieldList(oldDetail.getFieldList());
					newDetail.setFieldDefaultValue(oldDetail.getFieldDefaultValue());
					newDetail.setFieldMinValue(oldDetail.getFieldMinValue());
					newDetail.setFieldMaxValue(oldDetail.getFieldMaxValue());
					newDetail.setFieldLength(oldDetail.getFieldLength());
					newDetail.setFieldMandatory(oldDetail.isFieldMandatory());
					newDetail.setFieldUnique(oldDetail.isFieldUnique());
					newDetail.setVersion(1);
					newDetail.setRecordType(PennantConstants.RCD_ADD);
					extendedFieldHeader.getExtendedFieldDetails().add(newDetail);
				}
			}
		}

		// Display the dialog page.
		doShowDialogPage(aCollateralStructure , isCopyProcess);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCollateralStructureItemDoubleClicked(ForwardEvent event) {
		logger.debug("Entering");

		// Get the selected record.
		//Listitem selectedItem = this.listBoxCollateralStructure.getSelectedItem();
		Listitem selectedItem = (Listitem) event.getOrigin().getTarget();

		// Get the selected entity.
		String collateralType = (String) selectedItem.getAttribute("CollateralType");
		CollateralStructure collateralStructure = collateralStructureService.getCollateralStructureByType(collateralType);

		if (collateralStructure == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CollateralType='" + collateralStructure.getCollateralType() + "' AND version="
				+ collateralStructure.getVersion() + " ";

		if (doCheckAuthority(collateralStructure, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && collateralStructure.getWorkflowId() == 0) {
				collateralStructure.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(collateralStructure, false);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CollateralStructure collateralStructure, boolean isCopyProcess) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("collateralStructure", collateralStructure);
		arg.put("collateralStructureListCtrl", this);
		arg.put("isCopyProcess", isCopyProcess);
		arg.put("alwCopyOption", this.button_CollateralStructureList_NewCollateralStructure.isVisible());

		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralStructure/CollateralStructureDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public void setCollateralStructureService(CollateralStructureService collateralStructureService) {
		this.collateralStructureService = collateralStructureService;
	}
	
}