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
 * FileName    		:  AssetTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.configuration.assettype;

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
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.configuration.AssetTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.configuration.assettype.model.AssetTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
	
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/configuration/AssetType/AssetTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AssetTypeListCtrl extends GFCBaseListCtrl<AssetType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AssetTypeListCtrl.class);

	protected Window 		window_AssetTypeList; 
	protected Borderlayout 	borderLayout_AssetTypeList; 
	protected Paging 		pagingAssetTypeList; 
	protected Listbox 		listBoxAssetType; 

	// List headers
	protected Listheader 	listheader_AssetType; 
	protected Listheader 	listheader_AssetDescription;
	protected Listheader	listheader_AssetActive;
	
	// checkRights
	protected Button 		btnHelp; 
	protected Button 		button_AssetTypeList_NewAssetType; 
	protected Button 		button_AssetTypeList_AssetTypeSearch; 
	
	protected Textbox 		assetType; 
	protected Listbox 		sortOperator_AssetType; 

	protected Textbox 		assetDesc; 
	protected Listbox 		sortOperator_AssetDescription; 
	
	protected Checkbox      active;
	protected Listbox       sortOperator_Active;
						
	protected Textbox 		moduleType; 
	
	private transient AssetTypeService 	assetTypeService;

	/**
	 * default constructor.<br>
	 */
	public AssetTypeListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetType";
		super.pageRightName = "AssetTypeList";
		super.tableName = "AssetTypes_AView";
		super.queueTableName = "AssetTypes_View";
	}

	
	public void onCreate$window_AssetTypeList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AssetTypeList, borderLayout_AssetTypeList, listBoxAssetType, pagingAssetTypeList);
		setItemRender(new AssetTypeListModelItemRenderer());
		// Register buttons and fields.
		registerButton(button_AssetTypeList_NewAssetType, "button_AssetTypeList_NewAssetType", true);
		registerButton(button_AssetTypeList_AssetTypeSearch);

		registerField("assetType", listheader_AssetType, SortOrder.ASC, assetType, sortOperator_AssetType,
				Operators.STRING);
		registerField("assetDesc", listheader_AssetDescription, SortOrder.NONE, assetDesc,
				sortOperator_AssetDescription, Operators.STRING);
		registerField("active", listheader_AssetActive, SortOrder.NONE, active,
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
	public void onClick$button_AssetTypeList_AssetTypeSearch(Event event) {
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$button_AssetTypeList_NewAssetType(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Create a new entity.
		AssetType assetType = new AssetType();
		assetType.setNewRecord(true);
		assetType.setWorkflowId(getWorkFlowId());
		assetType.getExtendedFieldHeader().setNewRecord(true);
		assetType.getExtendedFieldHeader().setWorkflowId(getWorkFlowId());
		
		// Copy Button Process
		boolean isCopyProcess = false;
		if (event.getData() != null) {
			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
			ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
			DateConverter dateConverter = new DateConverter(null);
			ConvertUtils.register(dateConverter, Date.class);
			AssetType sourceAsset = (AssetType) event.getData();
			BeanUtils.copyProperties(assetType, sourceAsset);
			assetType.setAssetType("");
			assetType.setAssetDesc("");
			assetType.setNewRecord(true);
			assetType.setRecordStatus("");
			isCopyProcess = true;

			//Extended Field Details
			ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
			extendedFieldHeader.setTabHeading(sourceAsset.getExtendedFieldHeader().getTabHeading());
			extendedFieldHeader.setNumberOfColumns(sourceAsset.getExtendedFieldHeader().getNumberOfColumns());
			extendedFieldHeader.setExtendedFieldDetails(new ArrayList<ExtendedFieldDetail>());
			List<ExtendedFieldDetail> fieldDetails = sourceAsset.getExtendedFieldHeader().getExtendedFieldDetails();
			if (fieldDetails != null && !fieldDetails.isEmpty()) {
				assetType.setExtendedFieldHeader(extendedFieldHeader);
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
		doShowDialogPage(assetType, isCopyProcess);

		logger.debug("Leaving");
	}
	
	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onAssetTypeItemDoubleClicked(Event event) throws Exception {
		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetType.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String id = ((String) selectedItem.getAttribute("id"));
		AssetType assetType = assetTypeService.getAssetTypeById(id);

		if (assetType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND AssetType='" + assetType.getAssetType() + "' AND version=" + assetType.getVersion()
				+ " ";

		if (doCheckAuthority(assetType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assetType.getWorkflowId() == 0) {
				assetType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assetType , false);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}
	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param country
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AssetType assetConfigurationType, boolean isCopyProcess) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetConfigurationType", assetConfigurationType);
		arg.put("assetTypeListCtrl", this);
		arg.put("isCopyProcess", isCopyProcess);
		arg.put("alwCopyOption", this.button_AssetTypeList_NewAssetType.isVisible());

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/AssetType/AssetTypeDialog.zul", null, arg);
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
	
	public void setAssetTypeService(AssetTypeService assetTypeService) {
		this.assetTypeService = assetTypeService;
	}

	public AssetTypeService getAssetTypeService() {
		return this.assetTypeService;
	}
}