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
 * FileName    		:  VASConfigurationListCtrl.java                                                   * 	  
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

package com.pennant.webui.configuration.vasconfiguration;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.configuration.vasconfiguration.model.VASConfigurationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Configuration/VASConfiguration/VASConfigurationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class VASConfigurationListCtrl extends GFCBaseListCtrl<VASConfiguration> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VASConfigurationListCtrl.class);

	protected Window window_VASConfigurationList;
	protected Borderlayout borderLayout_VASConfigurationList;
	protected Paging pagingVASConfigurationList;
	protected Listbox listBoxVASConfiguration;

	// List headers
	protected Listheader listheader_ProductCode;
	protected Listheader listheader_ProductDesc;
	protected Listheader listheader_VASCode;
	protected Listheader listheader_VASCategory;
	protected Listheader listheader_Manufacturer;
	
	protected Listheader listheader_RecAgainst;
	protected Listheader listheader_FeeAccrued;
	protected Listheader listheader_RecurringType;
	protected Listheader listheader_PreValidationReq;
	protected Listheader listheader_PostValidationReq;

	// checkRights
	protected Button btnHelp;
	protected Button button_VASConfigurationList_NewVASConfiguration;
	protected Button button_VASConfigurationList_VASConfigurationSearch;
	protected Button button_VASConfigurationList_PrintList;

	protected Textbox productCode;
	protected Listbox sortOperator_ProductCode;
	protected Textbox productDesc;
	protected Listbox sortOperator_ProductDesc;
	
	protected Textbox vasType;
	protected Listbox sortOperator_VASType;
	
	protected Textbox vasCategory;
	protected Listbox sortOperator_VASCategory;
	
	protected Textbox manufacturer;
	protected Listbox sortOperator_manufacturer;
	
	protected Combobox recAgainst;
	protected Listbox sortOperator_RecAgainst;
	protected Checkbox feeAccrued;
	protected Listbox sortOperator_FeeAccrued;
	protected Checkbox recurringType;
	protected Listbox sortOperator_RecurringType;
	protected Checkbox preValidationReq;
	protected Listbox sortOperator_PreValidationReq;
	protected Checkbox postValidationReq;
	protected Listbox sortOperator_PostValidationReq;

	private transient VASConfigurationService vasConfigurationService;

	/**
	 * default constructor.<br>
	 */
	public VASConfigurationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "VASConfiguration";
		super.pageRightName = "VASConfigurationList";
		super.tableName = "VasStructure_AView";
		super.queueTableName = "VasStructure_View";
	}

	/**
	 * Method for Creating window for VAS Configuration details list
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VASConfigurationList(Event event) throws Exception {
		logger.debug("Entering :"+event.toString());

		// Set the page level components.
		setPageComponents(window_VASConfigurationList, borderLayout_VASConfigurationList, listBoxVASConfiguration, pagingVASConfigurationList);
		setItemRender(new VASConfigurationListModelItemRenderer());
		fillComboBox(this.recAgainst, null, PennantStaticListUtil.getRecAgainstTypes(),"");
		// Register buttons and fields.
		registerButton(button_VASConfigurationList_NewVASConfiguration, "button_VASConfigurationList_NewVASConfiguration", true);
		registerButton(button_VASConfigurationList_VASConfigurationSearch);

		registerField("productCode", listheader_ProductCode, SortOrder.ASC, productCode, sortOperator_ProductCode,
				Operators.STRING);
		registerField("productDesc", listheader_ProductDesc, SortOrder.NONE, productDesc, sortOperator_ProductDesc,
				Operators.STRING);
		
		registerField("productType", listheader_VASCode, SortOrder.ASC, vasType, sortOperator_VASType,
				Operators.STRING);
		registerField("productCategory", listheader_VASCategory, SortOrder.NONE, vasCategory, sortOperator_VASCategory,
				Operators.STRING);
		
		registerField("recAgainst", listheader_RecAgainst, SortOrder.NONE, recAgainst, sortOperator_RecAgainst,
				Operators.SIMPLE_NUMARIC);
		registerField("manufacturerName", listheader_Manufacturer, SortOrder.NONE, manufacturer, sortOperator_manufacturer,
				Operators.STRING);
		/*
		registerField("feeAccrued", listheader_FeeAccrued, SortOrder.NONE, feeAccrued, sortOperator_FeeAccrued,
				Operators.BOOLEAN);
		registerField("recurringType", listheader_RecurringType, SortOrder.NONE, recurringType,
				sortOperator_RecurringType, Operators.BOOLEAN);
		registerField("preValidationReq", listheader_PreValidationReq, SortOrder.NONE, preValidationReq,
				sortOperator_PreValidationReq, Operators.BOOLEAN);
		registerField("postValidationReq", listheader_PostValidationReq, SortOrder.NONE, postValidationReq,
				sortOperator_PostValidationReq, Operators.BOOLEAN);*/

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving :"+event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_VASConfigurationList_VASConfigurationSearch(Event event) {
		logger.debug("Entering :"+event.toString());
		search();
		logger.debug("Leaving :"+event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug("Entering :"+event.toString());
		doReset();
		search();
		logger.debug("Leaving :"+event.toString());
	}
	
	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$button_VASConfigurationList_NewVASConfiguration(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering :"+event.toString());

		// Create a new entity.
		VASConfiguration vASConfiguration = new VASConfiguration();
		vASConfiguration.setNewRecord(true);
		vASConfiguration.setWorkflowId(getWorkFlowId());
		
		// Copy Button Process
		boolean isCopyProcess = false;
		if (event.getData() != null) {
			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
			ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
			DateConverter dateConverter = new DateConverter(null);
			ConvertUtils.register(dateConverter, Date.class);
			VASConfiguration sourceVas = (VASConfiguration) event.getData();
			BeanUtils.copyProperties(vASConfiguration, sourceVas);
			vASConfiguration.setProductCode("");
			vASConfiguration.setProductDesc("");
			vASConfiguration.setNewRecord(true);
			isCopyProcess = true;

			//Extended Field Details
			ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
			extendedFieldHeader.setTabHeading(sourceVas.getExtendedFieldHeader().getTabHeading());
			extendedFieldHeader.setNumberOfColumns(sourceVas.getExtendedFieldHeader().getNumberOfColumns());
			extendedFieldHeader.setExtendedFieldDetails(new ArrayList<ExtendedFieldDetail>());
			List<ExtendedFieldDetail> fieldDetails = sourceVas.getExtendedFieldHeader().getExtendedFieldDetails();
			if (fieldDetails != null && !fieldDetails.isEmpty()) {
				vASConfiguration.setExtendedFieldHeader(extendedFieldHeader);
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
		doShowDialogPage(vASConfiguration, isCopyProcess);

		logger.debug("Leaving :"+event.toString());
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onVASConfigurationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering :"+event.toString());

		// Get the selected record.
		Listitem selectedItem = this.listBoxVASConfiguration.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		// Get the selected entity.
		String productCode = ((String) selectedItem.getAttribute("productCode"));
		VASConfiguration vASConfiguration = vasConfigurationService.getVASConfigurationByCode(productCode);

		if (vASConfiguration == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ProductCode='" + vASConfiguration.getProductCode() + "' AND version="
				+ vASConfiguration.getVersion() + " ";

		if (doCheckAuthority(vASConfiguration, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && vASConfiguration.getWorkflowId() == 0) {
				vASConfiguration.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(vASConfiguration, false);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving :"+event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param country
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(VASConfiguration vASConfiguration, boolean isCopyProcess) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("vASConfiguration", vASConfiguration);
		arg.put("vASConfigurationListCtrl", this);
		arg.put("isCopyProcess", isCopyProcess);
		arg.put("alwCopyOption", this.button_VASConfigurationList_NewVASConfiguration.isVisible());

		try {
			Executions.createComponents("/WEB-INF/pages/VASConfiguration/VASConfigurationDialog.zul", null, arg);
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
		logger.debug("Entering :"+event.toString());
		doPrintResults();
		logger.debug("Leaving :"+event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		logger.debug("Entering :"+event.toString());
		doShowHelp(event);
		logger.debug("Leaving :"+event.toString());
	}
	
	public VASConfigurationService getVasConfigurationService() {
		return vasConfigurationService;
	}
	public void setVasConfigurationService(VASConfigurationService vASConfigurationService) {
		this.vasConfigurationService = vASConfigurationService;
	}

}