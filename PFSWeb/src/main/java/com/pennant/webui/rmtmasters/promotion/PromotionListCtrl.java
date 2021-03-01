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
 * FileName    		:  PromotionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.promotion;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.rmtmasters.promotion.model.CDSchemeListModelItemRenderer;
import com.pennant.webui.rmtmasters.promotion.model.PromotionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Promotion/Promotion/PromotionList.zul file.<br>
 * 
 */
public class PromotionListCtrl extends GFCBaseListCtrl<Promotion> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PromotionListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PromotionList;
	protected Borderlayout borderLayout_PromotionList;
	protected Paging pagingPromotionList;
	protected Listbox listBoxPromotion;

	// List headers
	protected Listheader listheader_PromotionCode;
	protected Listheader listheader_PromotionDesc;
	protected Listheader listheader_FinType;
	protected Listheader listheader_PromotionStartDate;
	protected Listheader listheader_PromotionEndDate;
	protected Listheader listheader_SchemeID;
	protected Listheader listheader_Tenor;
	protected Listheader listheader_PromotionActive;

	// checkRights
	protected Button button_PromotionList_NewPromotion;
	protected Button button_PromotionList_PromotionSearch;

	protected Textbox promotionCode;
	protected Textbox promotionDesc;
	protected Textbox finCategory;
	protected Longbox schemeID;
	protected Checkbox active;

	protected Listbox sortOperator_PromotionCode;
	protected Listbox sortOperator_PromotionDesc;
	protected Listbox sortOperator_SchemeID;
	protected Listbox sortOperator_active;

	private transient PromotionService promotionService;
	private transient boolean consumerDurable = false;

	/**
	 * default constructor.<br>
	 */
	public PromotionListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		if (PennantConstants.WORFLOW_MODULE_CD.equals(StringUtils.trimToEmpty(finCategory.getValue()))) {
			super.moduleCode = "CDScheme";
		} else {
			super.moduleCode = "Promotion";
		}

		super.pageRightName = "PromotionList";
		super.tableName = "Promotions_AView";
		super.queueTableName = "Promotions_View";
		super.enquiryTableName = "Promotions_TView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PromotionList(Event event) throws Exception {
		logger.debug("Entering");

		if (StringUtils.trimToEmpty(finCategory.getValue()).equals(PennantConstants.WORFLOW_MODULE_CD)) {
			consumerDurable = true;
		}

		// Set the page level components.
		setPageComponents(window_PromotionList, borderLayout_PromotionList, listBoxPromotion, pagingPromotionList);
		if (consumerDurable) {
			setItemRender(new CDSchemeListModelItemRenderer());
		} else {
			setItemRender(new PromotionListModelItemRenderer());
		}

		// Register buttons and fields.
		registerButton(button_PromotionList_NewPromotion, "button_PromotionList_NewPromotion", true);
		registerButton(button_PromotionList_PromotionSearch);

		registerField("PromotionCode", listheader_PromotionCode, SortOrder.ASC, promotionCode,
				sortOperator_PromotionCode, Operators.STRING);
		registerField("PromotionDesc", listheader_PromotionDesc, SortOrder.ASC, promotionDesc,
				sortOperator_PromotionDesc, Operators.STRING);
		registerField("FinType", listheader_FinType, SortOrder.NONE);
		registerField("StartDate", listheader_PromotionStartDate, SortOrder.NONE);
		registerField("EndDate", listheader_PromotionEndDate, SortOrder.NONE);

		if (consumerDurable) {
			registerField("ReferenceId", listheader_SchemeID, SortOrder.ASC, schemeID, sortOperator_SchemeID,
					Operators.NUMERIC);
			registerField("Active", listheader_PromotionActive, SortOrder.NONE, active, sortOperator_active,
					Operators.BOOLEAN);
			registerField("Tenor", listheader_Tenor, SortOrder.NONE);
		} else {
			registerField("Active", listheader_PromotionActive, SortOrder.NONE);
		}

		registerField("PromotionId");

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	protected void doAddFilters() {
		super.doAddFilters();
		if (consumerDurable) {
			this.searchObject.addFilterEqual("ProductCategory", FinanceConstants.PRODUCT_CD);
		} else {
			this.searchObject.addFilterNotEqual("ProductCategory", FinanceConstants.PRODUCT_CD);
		}
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_PromotionList_PromotionSearch(Event event) {
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
	public void onClick$button_PromotionList_NewPromotion(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Promotion promotion = new Promotion();
		promotion.setNewRecord(true);
		promotion.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("promotion", promotion);
		arg.put("promotionListCtrl", this);
		arg.put("role", getRole());
		arg.put("consumerDurable", consumerDurable);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/Promotion/SelectPromotionDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnSearchScheme" button o
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSchemeCode(Event event) {
		logger.debug(Literal.ENTERING);

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("ReferenceId", 0, Filter.OP_GREATER_THAN);

		Object dataObject = ExtendedSearchListBox.show(this.window_PromotionList, "Promotion",
				this.promotionCode.getValue(), filters, StringUtils.trimToNull(this.searchObject.getWhereClause()));
		if (dataObject instanceof String) {
			this.promotionCode.setValue("");
		} else {
			Promotion details = (Promotion) dataObject;
			if (details != null) {
				this.promotionCode.setValue(details.getPromotionCode());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$NewSchemeIdCreation(Event event) throws Exception {
		logger.debug("Entering");
		createSchemeDialog(event, true);
		logger.debug("Leaving");
	}

	public void onClick$CopychemeIdCreation(Event event) throws Exception {
		logger.debug("Entering");
		createSchemeDialog(event, false);
		logger.debug("Leaving");
	}

	private void createSchemeDialog(Event event, boolean processType)
			throws IllegalAccessException, InvocationTargetException {
		// Create a new entity.
		Promotion promotion = new Promotion();
		promotion.setNewRecord(true);
		promotion.setWorkflowId(getWorkFlowId());

		// aFinanceType.setFinScheduleOn("");
		if (event.getData() != null) {
			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
			ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
			DateConverter dateConverter = new DateConverter(null);
			ConvertUtils.register(dateConverter, Date.class);
			Promotion sourcePromotion = (Promotion) event.getData();
			BeanUtils.copyProperties(promotion, sourcePromotion);
			promotion.setNewRecord(true);
			promotion.setRecordType("");
			promotion.setRecordStatus("");
			promotion.setPromotionId(Long.MIN_VALUE);
			long schemeId = promotionService.getPromotionalReferenceId();
			promotion.setReferenceID(schemeId);
			promotion.setActive(true);

			if (!processType) {
				promotion.setPromotionCode(null);
				promotion.setPromotionDesc(null);
			}

			List<FinTypeFees> finTypeFeesList = sourcePromotion.getFinTypeFeesList();
			if (finTypeFeesList != null && !finTypeFeesList.isEmpty()) {
				promotion.setFinTypeFeesList(new ArrayList<FinTypeFees>());
				for (FinTypeFees finTypeFees : finTypeFeesList) {
					finTypeFees.setVersion(1);
					finTypeFees.setRecordStatus("");
					finTypeFees.setRecordType(PennantConstants.RCD_ADD);
					promotion.getFinTypeFeesList().add(finTypeFees);
				}
			}
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("promotion", promotion);
		arg.put("isCopy", !processType);
		arg.put("alwCopyOption", this.button_PromotionList_NewPromotion.isVisible());
		arg.put("promotionListCtrl", this);
		arg.put("role", getRole());
		arg.put("consumerDurable", consumerDurable);
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/CDScheme/CDSchemeDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onPromotionItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxPromotion.getSelectedItem();

		// Get the selected entity.
		Promotion promotionObj = (Promotion) selectedItem.getAttribute("promotion");
		Promotion promotion = null;
		if (consumerDurable) {
			promotion = promotionService.getPromotionByPromotionId(promotionObj.getPromotionId(),
					FinanceConstants.MODULEID_PROMOTION);
		} else {
			promotion = promotionService.getPromotionById(promotionObj.getPromotionCode(),
					FinanceConstants.MODULEID_PROMOTION);
		}

		if (promotion == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where PromotionCode=?";

		if (doCheckAuthority(promotion, whereCond, new Object[] { promotion.getPromotionCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && promotion.getWorkflowId() == 0) {
				promotion.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(promotion);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param promotion
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Promotion promotion) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("promotion", promotion);
		arg.put("promotionListCtrl", this);
		arg.put("consumerDurable", consumerDurable);
		arg.put("alwCopyOption", this.button_PromotionList_NewPromotion.isVisible());

		if (consumerDurable) {
			try {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/CDScheme/CDSchemeDialog.zul", null, arg);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} else {
			try {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/Promotion/PromotionDialog.zul", null, arg);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
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

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}
}