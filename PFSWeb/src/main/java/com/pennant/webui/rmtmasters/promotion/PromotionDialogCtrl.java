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
 * FileName    		:  PromotionDialogCtrl.java                                             * 	  
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.RateBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RuleConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.rmtmasters.financetype.FinTypeAccountingListCtrl;
import com.pennant.webui.rmtmasters.financetype.FinTypeFeesListCtrl;
import com.pennant.webui.rmtmasters.financetype.FinTypeInsuranceListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/Promotion/PromotionDialog.zul file. <br>
 */
public class PromotionDialogCtrl extends GFCBaseCtrl<Promotion> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PromotionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PromotionDialog;

	protected Textbox promotionCode;
	protected Textbox promotionDesc;
	protected ExtendedCombobox finType;
	protected Datebox startDate;
	protected Datebox endDate;
	protected Checkbox finIsDwPayRequired;
	protected ExtendedCombobox downPayRule;
	protected Decimalbox actualInterestRate;
	protected RateBox finBaseRate;
	protected Checkbox applyRpyPricing;
	protected ExtendedCombobox rpyPricingMethod;
	protected Intbox finMinTerm;
	protected Intbox finMaxTerm;
	protected CurrencyBox finMinAmount;
	protected CurrencyBox finMaxAmount;
	protected Decimalbox finMinRate;
	protected Decimalbox finMaxRate;
	protected Checkbox active;

	protected Div basicDetailDiv;
	
	protected Row row_ApplyRpyPricing;

	private Promotion promotion;
	private transient PromotionListCtrl promotionListCtrl;
	private transient PromotionService promotionService;
	private transient boolean validationOn;

	private int format;
	private String finCcy = "";

	protected Tabs tabsIndexCenter;
	protected Tab  basicDetails;
	protected Tabpanels tabpanelsBoxIndexCenter;

	protected String selectMethodName = "onSelectTab";
	protected Component feeDetailWindow;
	protected Component insuranceDetailWindow;
	protected Component accountingDetailWindow;

	protected FinTypeFeesListCtrl finTypeFeesListCtrl;
	protected FinTypeInsuranceListCtrl finTypeInsuranceListCtrl;
	protected FinTypeAccountingListCtrl finTypeAccountingListCtrl;
	
	private boolean isCompReadonly = false;
	
	/**
	 * default constructor.<br>
	 */
	public PromotionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PromotionDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Promotion object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PromotionDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PromotionDialog);

		try {
			// Get the required arguments.
			this.promotion = (Promotion) arguments.get("promotion");
			this.promotionListCtrl = (PromotionListCtrl) arguments.get("promotionListCtrl");

			if (this.promotion == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			Promotion befImage = new Promotion();
			BeanUtils.copyProperties(this.promotion, befImage);
			this.promotion.setBefImage(befImage);

			doLoadWorkFlow(this.promotion.isWorkflow(), this.promotion.getWorkflowId(), this.promotion.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			this.basicDetailDiv.setHeight(this.borderLayoutHeight - 90 + "px");
			this.isCompReadonly = !isMaintainable();

			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.promotion);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}
	
	private boolean isMaintainable() {
		// If workflow enabled and not first task owner then cannot maintain. Else can maintain
		if (isWorkFlowEnabled()) {
			if (!StringUtils.equals(getRole(), getWorkFlow().firstTaskOwner())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.promotionCode.setMaxlength(8);
		this.promotionDesc.setMaxlength(50);
		this.actualInterestRate.setMaxlength(13);
		this.finMinTerm.setMaxlength(3);
		this.finMaxTerm.setMaxlength(3);

		this.finMinRate.setMaxlength(13);
		this.finMinRate.setFormat(PennantConstants.rateFormate9);
		this.finMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finMinRate.setScale(9);

		this.finMaxRate.setMaxlength(13);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finMaxRate.setScale(9);

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinCategory");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType", "FinCategory", "FinTypeDesc" });
		this.finType.setMandatoryStyle(true);

		this.startDate.setFormat(PennantConstants.dateFormat);
		this.endDate.setFormat(PennantConstants.dateFormat);

		this.downPayRule.setInputAllowed(false);
		this.downPayRule.setDisplayStyle(3);
		this.downPayRule.setMaxlength(8);
		this.downPayRule.setModuleName("Rule");
		this.downPayRule.setValueColumn("RuleCode");
		this.downPayRule.setDescColumn("RuleCodeDesc");
		this.downPayRule.setValidateColumns(new String[] { "RuleId", "RuleCode", "RuleCodeDesc" });
		this.downPayRule.setFilters(new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_DOWNPAYRULE, Filter.OP_EQUAL) });
		this.downPayRule.setMandatoryStyle(this.promotion.isFinIsDwPayRequired());

		this.finBaseRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.finBaseRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.finBaseRate.setMandatoryStyle(false);

		this.rpyPricingMethod.setInputAllowed(false);
		this.rpyPricingMethod.setDisplayStyle(3);
		this.rpyPricingMethod.setMaxlength(8);
		this.rpyPricingMethod.setModuleName("Rule");
		this.rpyPricingMethod.setValueColumn("RuleCode");
		this.rpyPricingMethod.setDescColumn("RuleCodeDesc");
		this.rpyPricingMethod.setValidateColumns(new String[] { "RuleId", "RuleCode", "RuleCodeDesc" });
		this.rpyPricingMethod.setFilters(new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_RATERULE, Filter.OP_EQUAL) });
		this.rpyPricingMethod.setMandatoryStyle(this.promotion.isApplyRpyPricing());

		this.row_ApplyRpyPricing.setVisible(ImplementationConstants.ALLOW_PRICINGPOLICY);

		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PromotionDialog_btnNew"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PromotionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PromotionDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onCheck$applyRpyPricing(Event event) {
		logger.debug("Entering" + event.toString());

		this.rpyPricingMethod.setErrorMessage("");
		this.rpyPricingMethod.setConstraint("");
		this.rpyPricingMethod.setValue("0");
		this.rpyPricingMethod.setDescription("");
		
		if (this.applyRpyPricing.isChecked()) {
			this.rpyPricingMethod.setReadonly(isReadOnly("PromotionDialog_RpyPricingMethod"));
			this.rpyPricingMethod.setButtonDisabled(isReadOnly("PromotionDialog_RpyPricingMethod"));
			this.rpyPricingMethod.setMandatoryStyle(!isReadOnly("PromotionDialog_RpyPricingMethod"));
		} else {
			this.rpyPricingMethod.setReadonly(true);
			this.rpyPricingMethod.setButtonDisabled(true);
			this.rpyPricingMethod.setMandatoryStyle(false);
			this.rpyPricingMethod.setObject(new Rule(0));
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$finIsDwPayRequired(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.finIsDwPayRequired.isChecked()) {
			this.downPayRule.setReadonly(isReadOnly("PromotionDialog_DownPayRule"));
			this.downPayRule.setMandatoryStyle(true);
		} else {
			this.downPayRule.setErrorMessage("");
			this.downPayRule.setConstraint("");
			this.downPayRule.setValue("0");
			this.downPayRule.setDescription("");
			this.downPayRule.setReadonly(true);
			this.downPayRule.setMandatoryStyle(false);
			this.downPayRule.setObject(new Rule(0));
		}

		logger.debug("Leaving" + event.toString());
	}


	public void onFulfill$finBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		Clients.clearWrongValue(this.finBaseRate.getBaseComp());
		Clients.clearWrongValue(this.finBaseRate.getSpecialComp());
		this.finBaseRate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();

		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = finBaseRate.getBaseObject();

			if (dataObject instanceof String) {
				this.finBaseRate.setBaseValue(dataObject.toString());
				this.finBaseRate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;

				if (details != null) {
					this.finBaseRate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(
							this.finBaseRate.getBaseValue(),
							this.finCcy,
							this.finBaseRate.getSpecialValue(),
							this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate
									.getMarginValue(), this.finMinRate.getValue(), this.finMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.finBaseRate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail
								.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.finBaseRate.setBaseValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = finBaseRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.finBaseRate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.finBaseRate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(
							this.finBaseRate.getBaseValue(),
							this.finCcy,
							this.finBaseRate.getSpecialValue(),
							this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate
									.getMarginValue(), this.finMinRate.getValue(), this.finMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.finBaseRate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail
								.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.finBaseRate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setEffectiveRate();
		}

		logger.debug("Leaving " + event.toString());
	}

	private void setEffectiveRate() throws InterruptedException {
		logger.debug("Entering");

		if (StringUtils.isBlank(this.finBaseRate.getBaseValue())) {
			this.finBaseRate.setEffRateText(PennantApplicationUtil.formatRate(
					(this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate.getMarginValue())
							.doubleValue(), 2));
			return;
		}

		RateDetail rateDetail = RateUtil.rates(this.finBaseRate.getBaseValue(), this.finCcy, this.finBaseRate.getSpecialValue(),
				this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate.getMarginValue(),
				this.finMinRate.getValue(), this.finMaxRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			this.finBaseRate.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan()
					.doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.finBaseRate.setSpecialValue("");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.promotion.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPromotion
	 *            Promotion
	 */
	public void doWriteBeanToComponents(Promotion aPromotion) {
		logger.debug("Entering");

		this.promotionCode.setValue(aPromotion.getPromotionCode());
		this.promotionDesc.setValue(aPromotion.getPromotionDesc());
		this.startDate.setValue(aPromotion.getStartDate());
		this.endDate.setValue(aPromotion.getEndDate());
		this.finIsDwPayRequired.setChecked(aPromotion.isFinIsDwPayRequired());
		this.actualInterestRate.setValue(aPromotion.getActualInterestRate());

		this.finBaseRate.setEffectiveRateVisible(true);
		this.finBaseRate.setBaseValue(aPromotion.getFinBaseRate());
		this.finBaseRate.setSpecialValue(aPromotion.getFinSplRate());
		this.finBaseRate.setMarginValue(aPromotion.getFinMargin());

		this.applyRpyPricing.setChecked(aPromotion.isApplyRpyPricing());
		this.finMinTerm.setValue(aPromotion.getFinMinTerm());
		this.finMaxTerm.setValue(aPromotion.getFinMaxTerm());
		this.active.setChecked(aPromotion.isActive());

		this.finType.setValue(aPromotion.getFinType());
		this.finType.setDescription(aPromotion.getFinTypeDesc());
		this.finType.setObject(new FinanceType(aPromotion.getFinType()));

		this.finCcy = aPromotion.getFinCcy();
		this.format = CurrencyUtil.getFormat(this.finCcy);

		this.finMaxAmount.setMandatory(false);
		this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(this.format));
		this.finMaxAmount.setScale(this.format);
		this.finMinAmount.setMandatory(false);
		this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(this.format));
		this.finMinAmount.setScale(this.format);

		this.finMinAmount.setValue(PennantAppUtil.formateAmount(aPromotion.getFinMinAmount(), this.format));
		this.finMaxAmount.setValue(PennantAppUtil.formateAmount(aPromotion.getFinMaxAmount(), this.format));

		if (!applyRpyPricing.isChecked()) {
			readOnlyComponent(true, this.rpyPricingMethod);
		}

		if (!finIsDwPayRequired.isChecked()) {
			readOnlyComponent(true, this.downPayRule);
		}

		this.downPayRule.setValue(aPromotion.getDownPayRuleCode());
		this.downPayRule.setDescription(aPromotion.getDownPayRuleDesc());
		this.downPayRule.setObject(new Rule(aPromotion.getDownPayRule()));

		this.rpyPricingMethod.setValue(aPromotion.getRpyPricingCode());
		this.rpyPricingMethod.setDescription(aPromotion.getRpyPricingDesc());
		this.rpyPricingMethod.setObject(new Rule(aPromotion.getRpyPricingMethod()));

		this.finMinRate.setValue(aPromotion.getFinMinRate());
		this.finMaxRate.setValue(aPromotion.getFinMaxRate());

		try {
			setEffectiveRate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.recordStatus.setValue(aPromotion.getRecordStatus());

		if (StringUtils.isNotBlank(aPromotion.getFinType())) {
			appendFeeDetailTab();
			appendAccountingDetailsTab();
			//appendInsuranceDetailsTab();	//commented as per Bajaj requirement
		}

		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendFeeDetailTab() {
		logger.debug("Entering");

		try {
			createTab(AssetConstants.UNIQUE_ID_FEES, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEES));
			map.put("roleCode", getRole());
			map.put("finType", this.promotionCode.getValue());
			map.put("finCcy", this.finCcy);
			map.put("moduleId", FinanceConstants.MODULEID_PROMOTION);
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("finTypeFeesList", this.promotion.getFinTypeFeesList());

			feeDetailWindow = Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeFeesList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FEES), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendInsuranceDetailsTab() {
		logger.debug("Entering");

		try {
			createTab(AssetConstants.UNIQUE_ID_INSURANCES, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_INSURANCES));
			map.put("roleCode", getRole());
			map.put("finType", this.promotionCode.getValue());
			map.put("moduleId", FinanceConstants.MODULEID_PROMOTION);
			map.put("finTypeDesc", this.promotionDesc.getValue());
			map.put("finCcy", this.finCcy);
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("finTypeInsuranceList", this.promotion.getFinTypeInsurancesList());

			insuranceDetailWindow = Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeInsuranceList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_INSURANCES), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendAccountingDetailsTab() {
		logger.debug("Entering");
		
		try {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ACCOUNTING));
			map.put("roleCode", getRole());
			map.put("finType", this.promotionCode.getValue());
			map.put("moduleId", FinanceConstants.MODULEID_PROMOTION);
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("finTypeAccountingList", this.promotion.getFinTypeAccountingList());
			
			accountingDetailWindow = Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountingList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}
	
	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");

		String tabName = Labels.getLabel("tab_label_" + moduleID);

		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));

		logger.debug("Leaving");
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPromotion
	 */
	public void doWriteComponentsToBean(Promotion aPromotion) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			aPromotion.setPromotionCode(this.promotionCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aPromotion.setPromotionDesc(this.promotionDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Start Date
		try {
			aPromotion.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// End Date
		try {
			aPromotion.setEndDate(this.endDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Down Payment Required
		try {
			aPromotion.setFinIsDwPayRequired(this.finIsDwPayRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Down Payment Rule
		try {
			Rule downPayRuleObj = (Rule) this.downPayRule.getObject();
			aPromotion.setDownPayRule(downPayRuleObj.getId());
			aPromotion.setDownPayRuleDesc(this.downPayRule.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Actual Interest Rate
		try {
			/*
			 * to check mutually exclusive values i.e Base rate code and Actual Int rate
			 */
			if (this.actualInterestRate.getValue() != null) {
				if (this.actualInterestRate.getValue().compareTo(BigDecimal.ZERO) > 0
						&& StringUtils.isNotEmpty(this.finBaseRate.getBaseValue())) {
					throw new WrongValueException(this.actualInterestRate, Labels.getLabel("EITHER_OR",
							new String[] { Labels.getLabel("label_PromotionDialog_FinBaseRate.value"),
									Labels.getLabel("label_PromotionDialog_ActualInterestRate.value") }));
				}
				aPromotion.setActualInterestRate(this.actualInterestRate.getValue());
			} else {
				aPromotion.setActualInterestRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}	
		// Base Rate
		try {
			aPromotion.setFinBaseRate(StringUtils.isEmpty(this.finBaseRate.getBaseValue()) ? null : this.finBaseRate.getBaseValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Special Rate
		try {
			aPromotion.setFinSplRate(StringUtils.isEmpty(this.finBaseRate.getSpecialValue()) ? null : this.finBaseRate.getSpecialValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Margin
		try {
			aPromotion.setFinMargin(this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Apply Rpy Pricing
		try {
			aPromotion.setApplyRpyPricing(this.applyRpyPricing.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Repay Pricing Method
		try {
			Rule rpyPricingRuleObj = (Rule) this.rpyPricingMethod.getObject();
			aPromotion.setRpyPricingMethod(rpyPricingRuleObj.getId());
			aPromotion.setRpyPricingDesc(this.rpyPricingMethod.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			int minTerms = this.finMinTerm.intValue();
			int maxTerms = this.finMaxTerm.intValue();
			boolean validationRequired = true;
			
			if (minTerms == 0 && maxTerms == 0) {
				validationRequired = false;
			}

			if (validationRequired) {
				if (maxTerms < minTerms) {
					throw new WrongValueException(this.finMaxTerm, Labels.getLabel("label_PromotionDialog_FinMaxTerm.value") + " should be greater than or equal to " + Labels.getLabel("label_PromotionDialog_FinMinTerm.value"));
				}
			}
			aPromotion.setFinMinTerm(this.finMinTerm.getValue());
			aPromotion.setFinMaxTerm(this.finMaxTerm.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			double finMinAmountValue = this.finMinAmount.getValidateValue().doubleValue();
			double finMaxAmountValue = this.finMaxAmount.getValidateValue().doubleValue();
			boolean validationRequired = true;
			
			if (finMinAmountValue == 0 && finMaxAmountValue == 0) {
				validationRequired = false;
			}

			if (validationRequired) {
				if (finMaxAmountValue < finMinAmountValue) {
					throw new WrongValueException(this.finMaxAmount, Labels.getLabel("label_PromotionDialog_FinMaxAmount.value") + " should be greater than or equal to " + Labels.getLabel("label_PromotionDialog_FinMinAmount.value"));
				}
			}
			aPromotion.setFinMinAmount(PennantAppUtil.unFormateAmount(this.finMinAmount.getValidateValue(), format));
			aPromotion.setFinMaxAmount(PennantAppUtil.unFormateAmount(this.finMaxAmount.getValidateValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Minimum Rate
		try {
			long finMinRateValue = 0;
			long finMaxRateValue = 0;
			boolean validationRequired = true;
			
			if (this.finMinRate.getValue() != null) {
				finMinRateValue = this.finMinRate.getValue().longValue();
			}
			
			if (this.finMaxRate.getValue() != null) {
				finMaxRateValue = this.finMaxRate.getValue().longValue();
			}
			
			
			if (finMinRateValue == 0 && finMaxRateValue == 0) {
				validationRequired = false;
			}

			if (validationRequired) {
				if (finMaxRateValue < finMinRateValue) {
					throw new WrongValueException(this.finMaxRate, Labels.getLabel("label_PromotionDialog_FinMaxRate.value") + " should be greater than or equal to " + Labels.getLabel("label_PromotionDialog_FinMinRate.value"));
				}
			}
			
			aPromotion.setFinMinRate(this.finMinRate.getValue() == null ? BigDecimal.ZERO : this.finMinRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Maximum Rate
		try {
			aPromotion.setFinMaxRate(this.finMaxRate.getValue() == null ? BigDecimal.ZERO : this.finMaxRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aPromotion.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(this.finTypeFeesListCtrl != null) {
			aPromotion.setFinTypeFeesList(this.finTypeFeesListCtrl.doSave());
		}

		if(this.finTypeInsuranceListCtrl != null) {
			aPromotion.setFinTypeInsurancesList(this.finTypeInsuranceListCtrl.getFinTypeInsuranceList());
		}
		
		if (wve.isEmpty() && this.finTypeAccountingListCtrl != null) {
			if (this.finTypeAccountingListCtrl != null) {
				aPromotion.setFinTypeAccountingList(this.finTypeAccountingListCtrl.doSave());
			}
		} else {
			this.basicDetails.setSelected(true);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Displays the dialog page.
	 * 
	 * @param promotion
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Promotion promotion) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (promotion.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.promotionCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.promotionCode.focus();
				if (StringUtils.isNotBlank(promotion.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents(promotion);
		
		this.btnDelete.setVisible(false);
		
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		// Code
		if (!this.promotionCode.isReadonly()) {
			this.promotionCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PromotionDialog_PromotionCode.value"), PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_FL3, true));
		}
		// Description
		if (!this.promotionDesc.isReadonly()) {
			this.promotionDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PromotionDialog_PromotionDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// Finance Type
		this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_PromotionDialog_FinType.value"), null, true, true));

		Date appStartDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		// Start Date
		if (!this.startDate.isDisabled()) {
			this.startDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_FinanceTypeDialog_StartDate.value"), true, appStartDate, appEndDate, true));
		}
		// end Date
		if (!this.endDate.isDisabled()) {
			try {
				this.startDate.getValue();
				this.endDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_FinanceTypeDialog_EndDate.value"), true, this.startDate.getValue(), appEndDate, false));
			} catch (WrongValueException we) {
				this.endDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_FinanceTypeDialog_EndDate.value"), true, true, null, false));
			}
		}
		// Down Payment Rule
		if (this.finIsDwPayRequired.isChecked() && this.downPayRule.isButtonVisible()) {
			this.downPayRule.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PromotionDialog_DownPayRule.value"), null, this.finIsDwPayRequired.isChecked(), true));
		}

		// Actual Interest Rate
		if (!this.actualInterestRate.isDisabled()) {
			this.actualInterestRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_PromotionDialog_ActualInterestRate.value"), 9, false, false, 9999));
		}
		// Base Rate
		if (!this.finBaseRate.getMarginComp().isDisabled()) {
			this.finBaseRate.getMarginComp().setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_PromotionDialog_FinMargin.value"), 9, false, true, -9999, 9999));
			this.finBaseRate.getBaseComp().setConstraint(
							new PTStringValidator(Labels.getLabel("label_PromotionDialog_FinBaseRate.value"), null, false, true));
		}
		// Repay Pricing Method
		if (this.applyRpyPricing.isChecked() && this.rpyPricingMethod.isButtonVisible()) {
			this.rpyPricingMethod.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PromotionDialog_RpyPricingMethod.value"), null, this.applyRpyPricing.isChecked(), true));
		}
		// Minimum Term
		if (!this.finMinTerm.isReadonly()) {
			this.finMinTerm.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_PromotionDialog_FinMinTerm.value"), false, false, 0));
		}
		// Maximum Term
		if (!this.finMaxTerm.isReadonly()) {
			this.finMaxTerm.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_PromotionDialog_FinMaxTerm.value"), false, false, 0));
		}
		// Minimum Amount
		if (!this.finMinAmount.isReadonly() && this.finMinAmount.getValidateValue().compareTo(BigDecimal.ZERO) != 0) {
			this.finMinAmount.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_PromotionDialog_FinMinAmount.value"), format, false, false));
		}
		// Maximum Amount
		if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getActualValue().compareTo(BigDecimal.ZERO) != 0) {
			this.finMaxAmount.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_PromotionDialog_FinMaxAmount.value"), format, false, false));
		}
		// Minimum Rate
		if (!this.finMinRate.isDisabled()) {
			this.finMinRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_PromotionDialog_FinMinRate.value"), 9, false, false, 9999));
		}
		// Maximum Rate
		if (!this.finMaxRate.isDisabled()) {
			this.finMaxRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_PromotionDialog_FinMaxRate.value"), 9, false, false, 9999));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.promotionCode.setConstraint("");
		this.promotionDesc.setConstraint("");
		this.finType.setConstraint("");
		this.startDate.setConstraint("");
		this.endDate.setConstraint("");
		this.downPayRule.setConstraint("");
		this.actualInterestRate.setConstraint("");
		this.finBaseRate.getBaseComp().setConstraint("");
		this.finBaseRate.getSpecialComp().setConstraint("");
		this.rpyPricingMethod.setConstraint("");
		this.finMinTerm.setConstraint("");
		this.finMaxTerm.setConstraint("");
		this.finMinAmount.setConstraint("");
		this.finMaxAmount.setConstraint("");
		this.finMinRate.setConstraint("");
		this.finMaxRate.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.promotionCode.setErrorMessage("");
		this.promotionDesc.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.endDate.setErrorMessage("");
		this.downPayRule.setErrorMessage("");
		this.actualInterestRate.setErrorMessage("");
		this.finBaseRate.getBaseComp().setErrorMessage("");
		this.finBaseRate.getSpecialComp().setErrorMessage("");
		this.finBaseRate.getMarginComp().setErrorMessage("");
		this.rpyPricingMethod.setErrorMessage("");
		this.finMinTerm.setErrorMessage("");
		this.finMaxTerm.setErrorMessage("");
		this.finMinAmount.setErrorMessage("");
		this.finMaxAmount.setErrorMessage("");
		this.finMinRate.setErrorMessage("");
		this.finMaxRate.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Deletes a Promotion object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");

		final Promotion aPromotion = new Promotion();
		BeanUtils.copyProperties(this.promotion, aPromotion);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aPromotion.getPromotionCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aPromotion.getRecordType()).equals("")) {
				aPromotion.setVersion(aPromotion.getVersion() + 1);
				aPromotion.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPromotion.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aPromotion.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aPromotion.getNextTaskId(), aPromotion);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aPromotion, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		this.promotionCode.setReadonly(true);
		readOnlyComponent(true, this.finType);

		if (this.promotion.isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.active.setDisabled(true);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(isReadOnly("PromotionDialog_Active"), this.active);
		}

		readOnlyComponent(isReadOnly("PromotionDialog_DownPayRule"), this.downPayRule);
		readOnlyComponent(isReadOnly("PromotionDialog_RpyPricingMethod"), this.rpyPricingMethod);
		readOnlyComponent(isReadOnly("PromotionDialog_PromotionDesc"), this.promotionDesc);
		readOnlyComponent(isReadOnly("PromotionDialog_StartDate"), this.startDate);
		readOnlyComponent(isReadOnly("PromotionDialog_EndDate"), this.endDate);
		readOnlyComponent(isReadOnly("PromotionDialog_FinIsDwPayRequired"), this.finIsDwPayRequired);
		readOnlyComponent(isReadOnly("PromotionDialog_ActualInterestRate"), this.actualInterestRate);
		readOnlyComponent(isReadOnly("PromotionDialog_ApplyRpyPricing"), this.applyRpyPricing);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMinTerm"), this.finMinTerm);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMaxTerm"), this.finMaxTerm);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMinAmount"), this.finMinAmount);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMaxAmount"), this.finMaxAmount);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMinRate"), this.finMinRate);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMaxRate"), this.finMaxRate);

		this.finBaseRate.getBaseComp().setReadonly(isReadOnly("PromotionDialog_FinBaseRate"));
		this.finBaseRate.getSpecialComp().setReadonly(isReadOnly("PromotionDialog_FinBaseRate"));
		this.finBaseRate.setReadonly(isReadOnly("PromotionDialog_FinBaseRate"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.promotion.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.promotionCode);
		readOnlyComponent(true, this.promotionDesc);
		readOnlyComponent(true, this.finType);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.endDate);
		readOnlyComponent(true, this.finIsDwPayRequired);
		readOnlyComponent(true, this.downPayRule);
		readOnlyComponent(true, this.actualInterestRate);
		readOnlyComponent(true, this.applyRpyPricing);
		readOnlyComponent(true, this.rpyPricingMethod);
		readOnlyComponent(true, this.finMinTerm);
		readOnlyComponent(true, this.finMaxTerm);
		readOnlyComponent(true, this.finMinAmount);
		readOnlyComponent(true, this.finMaxAmount);
		readOnlyComponent(true, this.finMinRate);
		readOnlyComponent(true, this.finMaxRate);
		readOnlyComponent(true, this.active);

		this.finBaseRate.getBaseComp().setReadonly(true);
		this.finBaseRate.getSpecialComp().setReadonly(true);
		this.finBaseRate.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.promotionCode.setValue("");
		this.promotionDesc.setValue("");
		this.finType.setValue("");
		this.finType.setDescription("");
		this.startDate.setText("");
		this.endDate.setText("");
		this.finIsDwPayRequired.setChecked(false);
		this.downPayRule.setValue("");
		this.downPayRule.setDescription("");
		this.actualInterestRate.setValue("");
		this.finBaseRate.getBaseComp().setValue("", "");
		this.finBaseRate.getSpecialComp().setValue("");
		this.finBaseRate.setBaseValue("");
		this.finBaseRate.setSpecialValue("");
		this.finBaseRate.setMarginValue(BigDecimal.ZERO);
		this.applyRpyPricing.setChecked(false);
		this.rpyPricingMethod.setValue("");
		this.rpyPricingMethod.setDescription("");
		this.finMinTerm.setText("");
		this.finMaxTerm.setText("");
		this.finMinAmount.setValue("");
		this.finMaxAmount.setValue("");
		this.finMinRate.setValue("");
		this.finMaxRate.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() {
		logger.debug("Entering");
		final Promotion aPromotion = new Promotion();
		BeanUtils.copyProperties(this.promotion, aPromotion);
		boolean isNew = false;
		boolean validate = false;
		
		if (isWorkFlowEnabled() &&"Submit".equalsIgnoreCase(userAction.getSelectedItem().getLabel())) {
			validate = true;// Stop validations in save mode
		} else {
			validate = false;// Stop validations in save mode
		}
		
		if (this.finTypeAccountingListCtrl != null) {
			this.finTypeAccountingListCtrl.setValidate(validate);
		}

		if (isWorkFlowEnabled()) {
			aPromotion.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aPromotion.getNextTaskId(), aPromotion);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(aPromotion.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Promotion object with the components data
			doWriteComponentsToBean(aPromotion);
		}
		
		// Write the additional validations as per below example get the selected branch object from the listbox Do data
		// level validations here

		isNew = aPromotion.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aPromotion.getRecordType()).equals("")) {
				aPromotion.setVersion(aPromotion.getVersion() + 1);
				if (isNew) {
					aPromotion.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPromotion.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPromotion.setNewRecord(true);
				}
			}
		} else {
			aPromotion.setVersion(aPromotion.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aPromotion, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(Promotion aPromotion, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aPromotion.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aPromotion.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPromotion.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPromotion.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPromotion.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPromotion);
				}

				if (isNotesMandatory(taskId, aPromotion)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aPromotion.setTaskId(taskId);
			aPromotion.setNextTaskId(nextTaskId);
			aPromotion.setRoleCode(getRole());
			aPromotion.setNextRoleCode(nextRoleCode);

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aPromotion, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aPromotion, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aPromotion, tranType), null);
		}
		
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Promotion aPromotion = (Promotion) auditHeader.getAuditDetail().getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = this.promotionService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = this.promotionService.saveOrUpdate(auditHeader);
					}
				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = this.promotionService.doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aPromotion.getRecordType())) {
							deleteNotes = true;
						}
					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = this.promotionService.doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aPromotion.getRecordType())) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PromotionDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PromotionDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes("Promotion", aPromotion.getPromotionCode(), aPromotion.getVersion()), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}

		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");

		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Promotion aPromotion, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPromotion.getBefImage(), aPromotion);
		return new AuditHeader(aPromotion.getPromotionCode(), null, null, null, auditDetail,
				aPromotion.getUserDetails(), getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.promotion);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		promotionListCtrl.search();
	}

	@Override
	protected String getReference() {
		return this.promotion.getPromotionCode();
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	public void setFinTypeFeesListCtrl(FinTypeFeesListCtrl finTypeFeesListCtrl) {
		this.finTypeFeesListCtrl = finTypeFeesListCtrl;
	}

	public void setFinTypeInsuranceListCtrl(FinTypeInsuranceListCtrl finTypeInsuranceListCtrl) {
		this.finTypeInsuranceListCtrl = finTypeInsuranceListCtrl;
	}

	public void setFinTypeAccountingListCtrl(FinTypeAccountingListCtrl finTypeAccountingListCtrl) {
		this.finTypeAccountingListCtrl = finTypeAccountingListCtrl;
	}
}
