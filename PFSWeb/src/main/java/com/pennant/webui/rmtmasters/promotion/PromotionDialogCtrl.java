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
 * * FileName : PromotionDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-03-2017 * * Modified
 * Date : 21-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-03-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.promotion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
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
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.rmtmasters.financetype.FinTypeAccountingListCtrl;
import com.pennant.webui.rmtmasters.financetype.FinTypeFeesListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/Promotion/PromotionDialog.zul file. <br>
 */
public class PromotionDialogCtrl extends GFCBaseCtrl<Promotion> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PromotionDialogCtrl.class);

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
	protected Checkbox specialScheme;
	protected Textbox remarks;
	protected Intbox cashBackFromTheManufacturer;
	protected Intbox manufacturerCashbackToTheCustomer;
	protected Intbox dealerCashBackToTheCustomer;
	protected Combobox cashBackPayoutOptions;
	protected Checkbox dbd;
	protected Checkbox mbd;
	protected Decimalbox dbdPercentage;
	protected Combobox dbdPercentageCalculationOn;
	protected Checkbox dbdRetained;
	protected Checkbox mbdRetained;
	protected Checkbox knockOffOverDueAmountWithCashBackAmount;
	protected ExtendedCombobox dbdFeetype;
	protected ExtendedCombobox mbdFeetype;
	protected ExtendedCombobox dbdAndmbdFeetype;
	protected Space space_DBDPercentage;
	protected Space space_DBDPercentageCalculationOn;
	protected Label label_dealerCashBackToTheCustomer;

	protected Label label_StartDate;
	protected Label label_EndDate;

	protected Textbox finTypeName;
	protected Label schemeId;
	protected Intbox tenor;
	protected Intbox advEMITerms;
	protected Combobox pftDaysBasis;
	protected Decimalbox subventionRate;
	protected Checkbox taxApplicable;
	protected Checkbox openBalOnPV;
	protected Intbox cashBackFromDealer;
	protected Intbox cashBackToCustomer;
	protected Row row_cashbackFromDelaer;

	protected Button btnCopy;
	protected Button btnNewSchemeId;

	protected boolean alwCopyOption = false;
	protected boolean isCopy = false;

	protected Tab tab_fees;
	protected Tabpanel tabpanel_Fees;

	protected Div basicDetailDiv;
	protected Row row_ApplyRpyPricing;
	protected Row row_CashBackManufacturer;

	private Promotion promotion;
	private transient PromotionListCtrl promotionListCtrl;
	private transient PromotionService promotionService;
	private transient boolean validationOn;

	private int format;
	private String finCcy = "";

	protected Tabs tabsIndexCenter;
	protected Tab basicDetails;
	protected Tabpanels tabpanelsBoxIndexCenter;

	protected String selectMethodName = "onSelectTab";
	protected Component feeDetailWindow;
	protected Component insuranceDetailWindow;
	protected Component accountingDetailWindow;

	protected FinTypeFeesListCtrl finTypeFeesListCtrl;
	protected FinTypeAccountingListCtrl finTypeAccountingListCtrl;

	private boolean isCompReadonly = false;
	private boolean consumerDurable = false;

	private final List<ValueLabel> cashBackPayoutOptionsList = PennantStaticListUtil.getCashBackPayoutOptionsList();
	private final List<ValueLabel> DBDPercentageList = PennantStaticListUtil.getDBDPercentageList();
	private String postEvent;

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
	 */
	public void onCreate$window_PromotionDialog(Event event) {
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

			if (arguments.containsKey("consumerDurable")) {
				this.consumerDurable = (boolean) arguments.get("consumerDurable");
			}

			if (arguments.containsKey("alwCopyOption")) {
				this.alwCopyOption = (Boolean) arguments.get("alwCopyOption");
			}

			if (arguments.containsKey("isCopy")) {
				this.isCopy = (Boolean) arguments.get("isCopy");
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
			if (this.promotion.isNewRecord() && consumerDurable) {
				doEnable();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.finCcy = this.promotion.getFinCcy();
		this.format = CurrencyUtil.getFormat(this.finCcy);

		this.promotionCode.setMaxlength(8);
		this.promotionDesc.setMaxlength(50);
		this.actualInterestRate.setMaxlength(13);
		this.actualInterestRate.setFormat(PennantConstants.rateFormate9);
		this.actualInterestRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.actualInterestRate.setScale(9);
		this.finMinTerm.setMaxlength(3);
		this.finMaxTerm.setMaxlength(3);

		this.finMinRate.setMaxlength(13);
		this.finMinRate.setFormat(PennantConstants.rateFormate9);
		this.finMinRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finMinRate.setScale(9);

		this.finMaxRate.setMaxlength(13);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finMaxRate.setScale(9);

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinCategory");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType", "FinCategory", "FinTypeDesc" });
		this.finType.setMandatoryStyle(true);

		this.startDate.setFormat(PennantConstants.dateFormat);
		this.endDate.setFormat(PennantConstants.dateFormat);

		this.downPayRule.setInputAllowed(false);
		this.downPayRule.setModuleName("Rule");
		this.downPayRule.setValueColumn("RuleCode");
		this.downPayRule.setDescColumn("RuleCodeDesc");
		this.downPayRule.setFilters(
				new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_DOWNPAYRULE, Filter.OP_EQUAL) });
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
		this.rpyPricingMethod
				.setFilters(new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_RATERULE, Filter.OP_EQUAL) });
		this.rpyPricingMethod.setMandatoryStyle(this.promotion.isApplyRpyPricing());

		this.row_ApplyRpyPricing.setVisible(ImplementationConstants.ALLOW_PRICINGPOLICY);

		this.finMinAmount.setMandatory(false);
		this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finMinAmount.setScale(format);

		this.finMaxAmount.setMandatory(false);
		this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finMaxAmount.setScale(format);

		if (consumerDurable) {

			this.tenor.setMaxlength(2);
			this.advEMITerms.setMaxlength(2);
			this.cashBackFromDealer.setMaxlength(1);
			this.cashBackToCustomer.setMaxlength(1);

			this.pftDaysBasis.setReadonly(true);

			this.subventionRate.setMaxlength(13);
			this.subventionRate.setFormat(PennantConstants.rateFormate9);
			this.subventionRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.subventionRate.setScale(9);

			this.dbdFeetype.setModuleName("FeeType");
			this.dbdFeetype.setValueColumn("FeeTypeCode");
			this.dbdFeetype.setDescColumn("FeeTypeDesc");
			this.dbdFeetype.setValidateColumns(new String[] { "FeeTypeCode" });

			this.mbdFeetype.setModuleName("FeeType");
			this.mbdFeetype.setValueColumn("FeeTypeCode");
			this.mbdFeetype.setDescColumn("FeeTypeDesc");
			this.mbdFeetype.setValidateColumns(new String[] { "FeeTypeCode" });

			this.dbdAndmbdFeetype.setModuleName("FeeType");
			this.dbdAndmbdFeetype.setValueColumn("FeeTypeCode");
			this.dbdAndmbdFeetype.setDescColumn("FeeTypeDesc");
			this.dbdAndmbdFeetype.setValidateColumns(new String[] { "FeeTypeCode" });

			readOnlyComponent(true, this.cashBackFromTheManufacturer);
			readOnlyComponent(true, this.manufacturerCashbackToTheCustomer);
			readOnlyComponent(true, this.dealerCashBackToTheCustomer);
			this.dbdPercentage.setDisabled(true);

			this.knockOffOverDueAmountWithCashBackAmount.setChecked(true);

			this.row_cashbackFromDelaer.setVisible(false);

		}

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

	private boolean isMaintainable() {
		// If workflow enabled and not first task owner then cannot maintain
		// Else can maintain
		if (enqiryModule) {
			return false;
		}

		if (isWorkFlowEnabled()) {
			if (!StringUtils.equals(getRole(), getWorkFlow().firstTaskOwner())) {
				return false;
			}
		}

		boolean isMaintenance = true;
		if (promotion.isNewRecord()
				|| StringUtils.equals(promotion.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isMaintenance = false;
		}

		if (isMaintenance) {
			return false;
		}

		return true;
	}

	private boolean allowChildMaintenance() {
		// If workflow enabled and not first task owner then cannot maintain
		// Else can maintain
		if (enqiryModule) {
			return false;
		}

		if (isWorkFlowEnabled()) {
			if (!StringUtils.equals(getRole(), getWorkFlow().firstTaskOwner())) {
				return false;
			}
		}

		return true;
	}

	private void doEnable() {
		// this.cashBackPayoutOptions.setSelectedIndex(1);
		this.dbdPercentageCalculationOn.setDisabled(true);
		this.knockOffOverDueAmountWithCashBackAmount.setChecked(true);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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
					RateDetail rateDetail = RateUtil.rates(this.finBaseRate.getBaseValue(), this.finCcy,
							this.finBaseRate.getSpecialValue(),
							this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO
									: this.finBaseRate.getMarginValue(),
							this.finMinRate.getValue(), this.finMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.finBaseRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
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
					RateDetail rateDetail = RateUtil.rates(this.finBaseRate.getBaseValue(), this.finCcy,
							this.finBaseRate.getSpecialValue(),
							this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO
									: this.finBaseRate.getMarginValue(),
							this.finMinRate.getValue(), this.finMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.finBaseRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
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

	private void setEffectiveRate() {
		logger.debug("Entering");

		if (StringUtils.isBlank(this.finBaseRate.getBaseValue())) {
			this.finBaseRate.setEffRateText(PennantApplicationUtil.formatRate(
					(this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate.getMarginValue())
							.doubleValue(),
					2));
			return;
		}

		RateDetail rateDetail = RateUtil.rates(this.finBaseRate.getBaseValue(), this.finCcy,
				this.finBaseRate.getSpecialValue(),
				this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate.getMarginValue(),
				this.finMinRate.getValue(), this.finMaxRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			this.finBaseRate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
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
	 * @param aPromotion Promotion
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
		this.finType.setObject(new FinanceType(aPromotion.getFinType()));

		if (consumerDurable) {
			this.finTypeName.setValue(aPromotion.getFinTypeDesc());
			this.schemeId.setValue(String.valueOf(aPromotion.getReferenceID()));
			this.tenor.setValue(aPromotion.getTenor());
			this.advEMITerms.setValue(aPromotion.getAdvEMITerms());
			this.cashBackFromDealer.setValue(aPromotion.getCashBackFromDealer());
			this.cashBackToCustomer.setValue(aPromotion.getCashBackToCustomer());

			fillComboBox(this.pftDaysBasis, aPromotion.getPftDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(),
					"");

			this.subventionRate.setValue(aPromotion.getSubventionRate());
			this.taxApplicable.setChecked(aPromotion.isTaxApplicable());
			this.openBalOnPV.setChecked(aPromotion.isOpenBalOnPV());
			this.specialScheme.setChecked(aPromotion.isSpecialScheme());
			this.remarks.setValue(aPromotion.getRemarks());
			this.cashBackFromTheManufacturer.setValue(aPromotion.getCbFrmMnf());
			this.manufacturerCashbackToTheCustomer.setValue(aPromotion.getMnfCbToCust());
			this.dealerCashBackToTheCustomer.setValue(aPromotion.getDlrCbToCust());
			fillComboBox(this.cashBackPayoutOptions, aPromotion.getCbPyt(), cashBackPayoutOptionsList, "");
			this.dbd.setChecked(aPromotion.isDbd());
			this.mbd.setChecked(aPromotion.isMbd());
			this.dbdPercentage.setValue(aPromotion.getDbdPerc());
			fillComboBox(this.dbdPercentageCalculationOn, aPromotion.getDbdPercCal(), DBDPercentageList, "");
			this.dbdRetained.setChecked(aPromotion.isDbdRtnd());
			this.mbdRetained.setChecked(aPromotion.isMbdRtnd());
			this.knockOffOverDueAmountWithCashBackAmount.setChecked(aPromotion.isKnckOffDueAmt());
			onCheckdbd();
			onCheckmbd();
			FeeType dbdFeeType = setFeeTypeData(aPromotion.getDbdFeeTypId());
			FeeType mbdFeeType = setFeeTypeData(aPromotion.getMbdFeeTypId());
			FeeType dbdAndMbdFeeType = setFeeTypeData(aPromotion.getDbdAndMbdFeeTypId());
			if (dbdFeeType != null) {
				this.dbdFeetype.setValue(dbdFeeType.getFeeTypeCode());
				this.dbdFeetype.setDescription(dbdFeeType.getFeeTypeDesc());
				this.dbdFeetype.setAttribute("data", dbdFeeType);
			}
			if (mbdFeeType != null) {
				this.mbdFeetype.setValue(mbdFeeType.getFeeTypeCode());
				this.mbdFeetype.setDescription(mbdFeeType.getFeeTypeDesc());
				this.mbdFeetype.setAttribute("data", mbdFeeType);
			}
			if (dbdAndMbdFeeType != null) {
				this.dbdAndmbdFeetype.setValue(dbdAndMbdFeeType.getFeeTypeCode());
				this.dbdAndmbdFeetype.setDescription(dbdAndMbdFeeType.getFeeTypeDesc());
				this.dbdAndmbdFeetype.setAttribute("data", dbdAndMbdFeeType);
			}

		}

		this.finMaxAmount.setMandatory(false);
		this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(this.format));
		this.finMaxAmount.setScale(this.format);
		this.finMinAmount.setMandatory(false);
		this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(this.format));
		this.finMinAmount.setScale(this.format);

		this.finMinAmount.setValue(PennantApplicationUtil.formateAmount(aPromotion.getFinMinAmount(), this.format));
		this.finMaxAmount.setValue(PennantApplicationUtil.formateAmount(aPromotion.getFinMaxAmount(), this.format));

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

		setEffectiveRate();

		this.recordStatus.setValue(aPromotion.getRecordStatus());

		if (StringUtils.isNotBlank(aPromotion.getFinType())) {

			appendFeeDetailTab();

			if (!consumerDurable) {
				appendAccountingDetailsTab();
				// appendInsuranceDetailsTab(); //commented as per Bajaj requirement
			}
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
			boolean isMaintenance = true;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", consumerDurable ? tab_fees : getTab(AssetConstants.UNIQUE_ID_FEES));
			map.put("roleCode", getRole());
			map.put("finType", this.promotionCode.getValue());
			map.put("finCcy", this.finCcy);
			map.put("moduleId", FinanceConstants.MODULEID_PROMOTION);
			map.put("mainController", this);
			map.put("isCompReadonly", !allowChildMaintenance());
			map.put("excludeAppFeeCodes", true);

			if (promotion.isNewRecord()
					|| StringUtils.equals(promotion.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				isMaintenance = false;
			}

			map.put("enqiryModule", this.enqiryModule || isMaintenance);
			map.put("consumerDurable", consumerDurable);
			map.put("finTypeFeesList", this.promotion.getFinTypeFeesList());

			feeDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeFeesList.zul",
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
	protected void appendAccountingDetailsTab() {
		logger.debug("Entering");

		try {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ACCOUNTING));
			map.put("roleCode", getRole());
			map.put("finType", this.promotionCode.getValue());
			map.put("moduleId", FinanceConstants.MODULEID_PROMOTION);
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("finTypeAccountingList", this.promotion.getFinTypeAccountingList());

			accountingDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountingList.zul",
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
						&& this.finBaseRate.getMarginValue().compareTo(BigDecimal.ZERO) > 0) {
					throw new WrongValueException(this.actualInterestRate,
							Labels.getLabel("EITHER_OR",
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
			aPromotion.setFinBaseRate(
					StringUtils.isEmpty(this.finBaseRate.getBaseValue()) ? null : this.finBaseRate.getBaseValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Special Rate
		try {
			aPromotion.setFinSplRate(StringUtils.isEmpty(this.finBaseRate.getSpecialValue()) ? null
					: this.finBaseRate.getSpecialValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Margin
		try {
			aPromotion.setFinMargin(
					this.finBaseRate.getMarginValue() == null ? BigDecimal.ZERO : this.finBaseRate.getMarginValue());
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
					throw new WrongValueException(this.finMaxTerm,
							Labels.getLabel("label_PromotionDialog_FinMaxTerm.value")
									+ " should be greater than or equal to "
									+ Labels.getLabel("label_PromotionDialog_FinMinTerm.value"));
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
					throw new WrongValueException(this.finMaxAmount,
							Labels.getLabel("label_PromotionDialog_FinMaxAmount.value")
									+ " should be greater than or equal to "
									+ Labels.getLabel("label_PromotionDialog_FinMinAmount.value"));
				}
			}
			aPromotion.setFinMinAmount(
					PennantApplicationUtil.unFormateAmount(this.finMinAmount.getValidateValue(), format));
			aPromotion.setFinMaxAmount(
					PennantApplicationUtil.unFormateAmount(this.finMaxAmount.getValidateValue(), format));
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
					throw new WrongValueException(this.finMaxRate,
							Labels.getLabel("label_PromotionDialog_FinMaxRate.value")
									+ " should be greater than or equal to "
									+ Labels.getLabel("label_PromotionDialog_FinMinRate.value"));
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

		if (consumerDurable) {
			try {
				aPromotion.setTenor(this.tenor.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setAdvEMITerms(this.advEMITerms.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setCashBackFromDealer(this.cashBackFromDealer.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setPftDaysBasis(getComboboxValue(this.pftDaysBasis));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setSubventionRate(this.subventionRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				/*
				 * if (this.cashBackFromDealer.intValue() == 0 && this.cashBackToCustomer.intValue() > 0) { throw new
				 * WrongValueException(cashBackToCustomer, Labels.getLabel("NUMBER_EQ", new String[] {
				 * Labels.getLabel("label_CDSchemeDialog_CashbackToCustomer.value"), "0" })); }
				 */
				aPromotion.setCashBackToCustomer(this.cashBackToCustomer.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setTaxApplicable(this.taxApplicable.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setOpenBalOnPV(this.openBalOnPV.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			// Description
			try {
				aPromotion.setRemarks(this.remarks.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setSpecialScheme(this.specialScheme.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.cashBackFromTheManufacturer.getValue() != null) {
					aPromotion.setCbFrmMnf(this.cashBackFromTheManufacturer.getValue());
				} else {
					aPromotion.setCbFrmMnf(0);
					this.cashBackFromTheManufacturer.setValue(0);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setDbdRtnd(this.dbdRetained.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setMbdRtnd(this.mbdRetained.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!aPromotion.isMbdRtnd()) {
					if (this.manufacturerCashbackToTheCustomer.getValue() != null) {
						if (this.manufacturerCashbackToTheCustomer.getValue() < this.cashBackFromTheManufacturer
								.getValue()) {
							throw new WrongValueException(this.manufacturerCashbackToTheCustomer,
									Labels.getLabel("label_CDSchemeDialog_ManufacturerCashbackToTheCustomer.value")
											+ " should be greater than or equal to " + Labels.getLabel(
													"label_CDSchemeDialog_CashbackFromTheManufacturer.value"));
						} else {
							aPromotion.setMnfCbToCust(this.manufacturerCashbackToTheCustomer.getValue());
						}
					}
				} else {
					aPromotion.setMnfCbToCust(0);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.dealerCashBackToTheCustomer.getValue() != null) {
					aPromotion.setDlrCbToCust(this.dealerCashBackToTheCustomer.getValue());
				} else {
					aPromotion.setDlrCbToCust(0);
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setCbPyt(getComboboxValue(this.cashBackPayoutOptions));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setDbd(this.dbd.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setMbd(this.mbd.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setDbdPerc(this.dbdPercentage.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setDbdPercCal(getComboboxValue(this.dbdPercentageCalculationOn));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aPromotion.setKnckOffDueAmt(this.knockOffOverDueAmountWithCashBackAmount.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.dbdFeetype.getValidatedValue();
				Object obj = this.dbdFeetype.getAttribute("data");
				if (obj != null) {
					aPromotion.setDbdFeeTypId(((FeeType) obj).getFeeTypeID());
				} else {
					aPromotion.setDbdFeeTypId(0);
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.mbdFeetype.getValidatedValue();
				Object obj = this.mbdFeetype.getAttribute("data");
				if (obj != null) {
					aPromotion.setMbdFeeTypId(((FeeType) obj).getFeeTypeID());
				} else {
					aPromotion.setMbdFeeTypId(0);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.dbdAndmbdFeetype.getValidatedValue();
				Object obj = this.dbdAndmbdFeetype.getAttribute("data");
				if (obj != null) {
					aPromotion.setDbdAndMbdFeeTypId(((FeeType) obj).getFeeTypeID());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.finTypeFeesListCtrl != null) {
			aPromotion.setFinTypeFeesList(this.finTypeFeesListCtrl.onSave());
		}

		if (wve.isEmpty() && this.finTypeAccountingListCtrl != null) {
			if (this.finTypeAccountingListCtrl != null) {
				aPromotion.setFinTypeAccountingList(this.finTypeAccountingListCtrl.onSave());
			}
		} else {
			this.basicDetails.setSelected(true);
		}

		doRemoveValidation();
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
	 * @param promotion The entity that need to be render.
	 */
	public void doShowDialog(Promotion promotion) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (promotion.isNewRecord()) {
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
		if (!promotion.isNewRecord()) {
			boolean isFinExists = promotionService.isFinExistsByPromotionSeqID(promotion.getReferenceID());
			if (isFinExists) {
				this.btnDelete.setVisible(false);
			}
		}

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
			this.promotionCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PromotionDialog_PromotionCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		// Description
		if (!this.promotionDesc.isReadonly()) {
			this.promotionDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PromotionDialog_PromotionDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// Finance Type
		this.finType.setConstraint(
				new PTStringValidator(Labels.getLabel("label_PromotionDialog_FinType.value"), null, true, true));

		Date appStartDate = SysParamUtil.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		// Start Date
		if (!this.startDate.isDisabled()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceTypeDialog_StartDate.value"),
					true, appStartDate, appEndDate, true));
		}
		// end Date
		if (!this.endDate.isDisabled()) {
			try {
				this.startDate.getValue();
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceTypeDialog_EndDate.value"),
						true, this.startDate.getValue(), appEndDate, false));
			} catch (WrongValueException we) {
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceTypeDialog_EndDate.value"),
						true, true, null, false));
			}
		}
		// Down Payment Rule
		if (this.finIsDwPayRequired.isChecked() && this.downPayRule.isButtonVisible()) {
			this.downPayRule
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PromotionDialog_DownPayRule.value"),
							null, this.finIsDwPayRequired.isChecked(), true));
		}

		// Actual Interest Rate
		if (!this.actualInterestRate.isDisabled() && !consumerDurable
				&& this.finBaseRate.getMarginValue().compareTo(BigDecimal.ZERO) == 0) {
			this.actualInterestRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PromotionDialog_ActualInterestRate.value"), 9, true, false, 9999));
		}

		// Scheme IRR
		if (this.actualInterestRate.getValue() != null) {
			if (this.actualInterestRate.getValue().compareTo(BigDecimal.ZERO) < 0
					&& !this.actualInterestRate.isReadonly()) {
				this.actualInterestRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_CDSchemeDialog_SchemeIRR.value"), 9, true, false, 9999));

			}
		}

		// Base Rate
		if (!this.finBaseRate.getMarginComp().isDisabled()) {
			this.finBaseRate.getMarginComp().setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PromotionDialog_FinMargin.value"), 9, false, true, -9999, 9999));
			this.finBaseRate.getBaseComp().setConstraint(new PTStringValidator(
					Labels.getLabel("label_PromotionDialog_FinBaseRate.value"), null, false, true));
		}
		// Repay Pricing Method
		if (this.applyRpyPricing.isChecked() && this.rpyPricingMethod.isButtonVisible()) {
			this.rpyPricingMethod.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PromotionDialog_RpyPricingMethod.value"), null,
							this.applyRpyPricing.isChecked(), true));
		}
		// Minimum Term
		if (!this.finMinTerm.isReadonly()) {
			this.finMinTerm.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_PromotionDialog_FinMinTerm.value"), false, false, 0));
		}
		// Maximum Term
		if (!this.finMaxTerm.isReadonly()) {
			this.finMaxTerm.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_PromotionDialog_FinMaxTerm.value"), false, false, 0));
		}
		// Minimum Amount
		if (!this.finMinAmount.isReadonly() && this.finMinAmount.getValidateValue().compareTo(BigDecimal.ZERO) != 0) {
			this.finMinAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PromotionDialog_FinMinAmount.value"), format, false, false));
		}
		// Maximum Amount
		if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getActualValue().compareTo(BigDecimal.ZERO) != 0) {
			this.finMaxAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PromotionDialog_FinMaxAmount.value"), format, false, false));
		}
		// Minimum Rate
		if (!this.finMinRate.isDisabled()) {
			this.finMinRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PromotionDialog_FinMinRate.value"), 9, false, false, 9999));
		}
		// Maximum Rate
		if (!this.finMaxRate.isDisabled()) {
			this.finMaxRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_PromotionDialog_FinMaxRate.value"), 9, false, false, 9999));
		}

		if (consumerDurable) {

			// tenor
			if (!this.tenor.isReadonly()) {
				this.tenor.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_CDSchemeDialog_Tenor.value"), true));
			}
			// Advance EMI Terms
			if (!this.advEMITerms.isReadonly()) {
				this.advEMITerms.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_CDSchemeDialog_AdvEMITerms.value"), false));
			}
			// Cash Back From Dealer
			if (!this.cashBackFromDealer.isReadonly()) {
				this.cashBackFromDealer.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_CDSchemeDialog_CashbackFromDealer.value"), false));
			}
			// Cash Back To Customer
			if (!this.cashBackToCustomer.isReadonly()) {
				this.cashBackToCustomer.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_CDSchemeDialog_CashbackToCustomer.value"), false));
			}
			// Subvention Rate
			if (!this.subventionRate.isDisabled()) {
				this.subventionRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_CDSchemeDialog_SubventionRate.value"), 9, false, false, 9999));
			}
			// Interest Days Basis
			if (!this.pftDaysBasis.isDisabled()) {
				this.pftDaysBasis.setConstraint(new StaticListValidator(PennantStaticListUtil.getProfitDaysBasis(),
						Labels.getLabel("label_CDSchemeDialog_ProfitDaysBasis.value")));
			}
		}

		if (!this.dbdPercentage.isDisabled()) {
			this.dbdPercentage.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CDSchemeDialog_DBDPercentage.value"), 9, true, false));
		}

		if (!this.dbdFeetype.isReadonly() && this.cashBackPayoutOptions.getSelectedIndex() != 2) {
			this.dbdFeetype.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CDSchemeDialog_DBDFeeTypeId.value"), null, true, true));
		}

		if (!this.mbdFeetype.isReadonly() && this.cashBackPayoutOptions.getSelectedIndex() != 2) {
			this.mbdFeetype.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CDSchemeDialog_MBDFeeTypeId.value"), null, true, true));
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
		this.finMinRate.setConstraint("");
		this.finMaxRate.setConstraint("");
		this.finMinAmount.setConstraint("");
		this.finMaxAmount.setConstraint("");

		if (consumerDurable) {
			this.tenor.setConstraint("");
			this.advEMITerms.setConstraint("");
			this.cashBackFromDealer.setConstraint("");
			this.cashBackToCustomer.setConstraint("");
			this.pftDaysBasis.setConstraint("");
			this.subventionRate.setConstraint("");
			this.dbdPercentage.setConstraint("");
		}

		logger.debug("Leaving");
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
		this.finMinRate.setErrorMessage("");
		this.finMaxRate.setErrorMessage("");
		this.finMinAmount.setErrorMessage("");
		this.finMaxAmount.setErrorMessage("");

		if (consumerDurable) {
			this.tenor.setErrorMessage("");
			this.advEMITerms.setErrorMessage("");
			this.cashBackFromDealer.setErrorMessage("");
			this.cashBackToCustomer.setErrorMessage("");
			this.pftDaysBasis.setErrorMessage("");
			this.subventionRate.setErrorMessage("");
		}

		logger.debug("Leaving");
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final Promotion aPromotion = new Promotion();
		BeanUtils.copyProperties(this.promotion, aPromotion);

		doDelete(aPromotion.getPromotionCode(), aPromotion);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		readOnlyComponent(true, this.finType);

		if (this.promotion.isNewRecord()) {
			this.btnCancel.setVisible(false);
			if (consumerDurable) {
				this.btnCopy.setVisible(false);
				this.btnNewSchemeId.setVisible(false);
			}
			this.active.setDisabled(true);
		} else {
			if (consumerDurable) {
				this.btnCopy.setVisible((allowChildMaintenance() || enqiryModule) && alwCopyOption);
				this.btnNewSchemeId.setVisible((allowChildMaintenance() || enqiryModule) && alwCopyOption
						&& PennantConstants.RCD_STATUS_APPROVED.equals(this.promotion.getRecordStatus()));
			}
			readOnlyComponent(isReadOnly("PromotionDialog_Active"), this.active);
		}

		if (isCopy) {
			readOnlyComponent(isReadOnly("PromotionDialog_PromotionCode"), this.promotionCode);
		} else {
			readOnlyComponent(true, this.promotionCode);
		}

		readOnlyComponent(isReadOnly("PromotionDialog_DownPayRule"), this.downPayRule);
		readOnlyComponent(isReadOnly("PromotionDialog_RpyPricingMethod"), this.rpyPricingMethod);
		readOnlyComponent(isReadOnly("PromotionDialog_PromotionDesc"), this.promotionDesc);
		readOnlyComponent(isReadOnly("PromotionDialog_StartDate", true), this.startDate);
		readOnlyComponent(isReadOnly("PromotionDialog_EndDate", true), this.endDate);
		readOnlyComponent(isReadOnly("PromotionDialog_FinIsDwPayRequired"), this.finIsDwPayRequired);
		readOnlyComponent(isReadOnly("PromotionDialog_ActualInterestRate", true), this.actualInterestRate);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMinAmount", true), this.finMinAmount);
		readOnlyComponent(isReadOnly("PromotionDialog_FinMaxAmount", true), this.finMaxAmount);

		if (consumerDurable) {
			readOnlyComponent(isReadOnly("PromotionDialog_Tenor", true), this.tenor);
			readOnlyComponent(isReadOnly("PromotionDialog_AdvEMITerms", true), this.advEMITerms);
			readOnlyComponent(isReadOnly("PromotionDialog_CashbackFromDealer", true), this.cashBackFromDealer);
			readOnlyComponent(isReadOnly("PromotionDialog_CashbackToCustomer", true), this.cashBackToCustomer);
			readOnlyComponent(isReadOnly("PromotionDialog_PftDaysBasis", true), this.pftDaysBasis);
			readOnlyComponent(isReadOnly("PromotionDialog_SubventionRate", true), this.subventionRate);
			readOnlyComponent(isReadOnly("PromotionDialog_TaxApplicable"), this.taxApplicable);
			readOnlyComponent(isReadOnly("PromotionDialog_OpenBalOnPV", true), this.openBalOnPV);
			readOnlyComponent(isReadOnly("PromotionDialog_OpenBalOnPV"), this.remarks);
			readOnlyComponent(isReadOnly("PromotionDialog_OpenBalOnPV", true), this.specialScheme);
			readOnlyComponent(isReadOnly("PromotionDialog_CashBackFromTheManufacturer", true),
					this.cashBackFromTheManufacturer);
			readOnlyComponent(isReadOnly("PromotionDialog_ManufacturerCashbackToTheCustomer", true),
					this.manufacturerCashbackToTheCustomer);
			readOnlyComponent(isReadOnly("PromotionDialog_DealerCashBackToTheCustomer", true),
					this.dealerCashBackToTheCustomer);
			readOnlyComponent(isReadOnly("PromotionDialog_DBDPercentage", true), this.dbdPercentage);
			readOnlyComponent(isReadOnly("PromotionDialog_DBD", true), this.dbd);
			readOnlyComponent(isReadOnly("PromotionDialog_DBDRetained", true), this.dbdRetained);
			readOnlyComponent(isReadOnly("PromotionDialog_MBD", true), this.mbd);
			readOnlyComponent(isReadOnly("PromotionDialog_MBDRetained", true), this.mbdRetained);
			readOnlyComponent(isReadOnly("PromotionDialog_DBDPercentageCalculationOn", true),
					this.dbdPercentageCalculationOn);
			readOnlyComponent(isReadOnly("PromotionDialog_CashBackPayoutOptions", true), this.cashBackPayoutOptions);
			readOnlyComponent(isReadOnly("PromotionDialog_KnockoffOverDueAmountWithCashbackAmount", true),
					this.knockOffOverDueAmountWithCashBackAmount);
			readOnlyComponent(isReadOnly("PromotionDialog_DBDFeeType"), this.dbdFeetype);
			readOnlyComponent(isReadOnly("PromotionDialog_MBDFeeType"), this.mbdFeetype);
			readOnlyComponent(isReadOnly("PromotionDialog_DBDAndMBDFeeType"), this.dbdAndmbdFeetype);

		} else {
			readOnlyComponent(isReadOnly("PromotionDialog_ApplyRpyPricing"), this.applyRpyPricing);
			readOnlyComponent(isReadOnly("PromotionDialog_FinMinTerm"), this.finMinTerm);
			readOnlyComponent(isReadOnly("PromotionDialog_FinMaxTerm"), this.finMaxTerm);
			readOnlyComponent(isReadOnly("PromotionDialog_FinMinRate"), this.finMinRate);
			readOnlyComponent(isReadOnly("PromotionDialog_FinMaxRate"), this.finMaxRate);

			this.finBaseRate.getBaseComp().setReadonly(isReadOnly("PromotionDialog_FinBaseRate"));
			this.finBaseRate.getSpecialComp().setReadonly(isReadOnly("PromotionDialog_FinBaseRate"));
			this.finBaseRate.setReadonly(isReadOnly("PromotionDialog_FinBaseRate"));
		}

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

	private boolean isReadOnly(String componentName, boolean maintenanceChkReq) {

		boolean isReadOnly = false;
		if (isWorkFlowEnabled()) {
			isReadOnly = getUserWorkspace().isReadOnly(componentName);
		}

		// Some of the Fields cannot be modified for the Maintenance level
		if (maintenanceChkReq) {
			boolean isMaintenance = true;
			if (promotion.isNewRecord()
					|| StringUtils.equals(promotion.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				isMaintenance = false;
			}

			if (isMaintenance) {
				isReadOnly = true;
			}
		}

		return isReadOnly;
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

		if (consumerDurable) {
			readOnlyComponent(true, this.tenor);
			readOnlyComponent(true, this.advEMITerms);
			readOnlyComponent(true, this.cashBackFromDealer);
			readOnlyComponent(true, this.cashBackToCustomer);
			readOnlyComponent(true, this.pftDaysBasis);
			readOnlyComponent(true, this.subventionRate);
			readOnlyComponent(true, this.taxApplicable);
			readOnlyComponent(true, this.specialScheme);
			readOnlyComponent(true, this.remarks);
			readOnlyComponent(true, this.cashBackFromTheManufacturer);
			readOnlyComponent(true, this.manufacturerCashbackToTheCustomer);
			readOnlyComponent(true, this.dealerCashBackToTheCustomer);
			readOnlyComponent(true, this.dbdPercentage);
			readOnlyComponent(true, this.dbd);
			readOnlyComponent(true, this.dbdRetained);
			readOnlyComponent(true, this.mbd);
			readOnlyComponent(true, this.mbdRetained);
			readOnlyComponent(true, this.dbdPercentageCalculationOn);
			readOnlyComponent(true, this.cashBackPayoutOptions);
			readOnlyComponent(true, this.knockOffOverDueAmountWithCashBackAmount);
			readOnlyComponent(true, this.dbdFeetype);
			readOnlyComponent(true, this.mbdFeetype);
			readOnlyComponent(true, this.dbdAndmbdFeetype);

		}

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

		if (consumerDurable) {
			this.tenor.setValue(0);
			this.advEMITerms.setValue(0);
			this.cashBackFromDealer.setValue(0);
			this.cashBackToCustomer.setValue(0);
			this.pftDaysBasis.setSelectedIndex(0);
			this.subventionRate.setValue(BigDecimal.ZERO);
			this.taxApplicable.setChecked(false);
		}

		logger.debug("Leaving");
	}

	public void onClick$btnCopy(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		postEvent = "onClick$CopychemeIdCreation";
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNewSchemeId(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		postEvent = "onClick$NewSchemeIdCreation";
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	protected void doPostClose() {
		if (StringUtils.isEmpty(postEvent)) {
			return;
		}
		Events.postEvent(postEvent, promotionListCtrl.window_PromotionList, promotion);
		postEvent = null;
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

		if (isWorkFlowEnabled() && "Submit".equalsIgnoreCase(userAction.getSelectedItem().getLabel())) {
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
			if (!this.cashBackPayoutOptions.isDisabled() && this.cashBackPayoutOptions.getSelectedIndex() == 2) {
				if (this.manufacturerCashbackToTheCustomer.getValue() != this.dealerCashBackToTheCustomer.getValue()) {
					MessageUtil.showError(
							"Manufacturer cash back to the customer should be equal to Dealer cash back to the customer for the selected criteria.");
					return;
				}

			}

		}

		// Write the additional validations as per below example get the selected branch object from the listbox Do data
		// level validations here

		isNew = aPromotion.isNewRecord();
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

	public void onSelect$cashBackPayoutOptions() {
		onSelectCashBackPayoutOptions();
	}

	public void onSelectCashBackPayoutOptions() {
		if (this.cashBackPayoutOptions.getSelectedIndex() == 0) {
			this.dbd.setChecked(false);
			this.dbdRetained.setChecked(false);
			this.mbd.setChecked(false);
			this.mbdRetained.setChecked(false);
			this.dbd.setDisabled(true);
			this.dbdRetained.setDisabled(true);
			this.mbd.setDisabled(true);
			this.mbdRetained.setDisabled(true);
			this.cashBackFromTheManufacturer.setValue(0);
			this.manufacturerCashbackToTheCustomer.setValue(0);
			readOnlyComponent(true, this.cashBackFromTheManufacturer);
			readOnlyComponent(true, this.manufacturerCashbackToTheCustomer);
			this.dbdPercentage.setDisabled(true);
			this.dbdPercentageCalculationOn.setDisabled(true);
			readOnlyComponent(true, this.dbdAndmbdFeetype);
			this.dbdAndmbdFeetype.setValue("", "");
			readOnlyComponent(true, this.dealerCashBackToTheCustomer);
			this.dealerCashBackToTheCustomer.setValue(0);
			readOnlyComponent(true, this.dbdFeetype);
			this.dbdFeetype.setValue("", "");
			this.mbdFeetype.setReadonly(true);
			readOnlyComponent(true, this.mbdFeetype);
			this.mbdFeetype.setValue("", "");
		} else if (this.cashBackPayoutOptions.getSelectedIndex() == 2) {
			this.dbd.setDisabled(false);
			this.mbd.setDisabled(false);
			this.dbd.setChecked(false);
			this.mbd.setChecked(false);

			readOnlyComponent(isReadOnly("PromotionDialog_DBDFeeType"), this.dbdFeetype);
			readOnlyComponent(isReadOnly("PromotionDialog_MBDFeeType"), this.mbdFeetype);
			readOnlyComponent(isReadOnly("PromotionDialog_DBDAndMBDFeeType"), this.dbdAndmbdFeetype);

			onCheckdbd();
			onCheckmbd();
		} else {
			this.dbd.setChecked(false);
			this.mbd.setChecked(false);
			this.dbd.setDisabled(false);
			readOnlyComponent(true, dbdFeetype);
			this.dbdFeetype.setValue("", "");
			onCheckdbd();
			readOnlyComponent(true, this.cashBackFromTheManufacturer);
			readOnlyComponent(true, this.manufacturerCashbackToTheCustomer);
			this.cashBackFromTheManufacturer.setValue(0);
			this.manufacturerCashbackToTheCustomer.setValue(0);
			this.dbdRetained.setDisabled(true);
			this.mbdRetained.setDisabled(true);
			this.dbdAndmbdFeetype.setValue("", "");
			readOnlyComponent(true, dbdAndmbdFeetype);
			this.mbd.setDisabled(false);
			this.mbdFeetype.setValue("", "");
			readOnlyComponent(true, mbdFeetype);
			onCheckmbd();

		}
	}

	public void onCheck$dbd() {
		onCheckdbd();
	}

	public void onCheck$mbd() {
		onCheckmbd();
	}

	public void onCheckdbd() {
		if (this.dbd.isChecked()) {
			this.dbdRetained.setDisabled(false);
			if (this.dbdRetained.isChecked()) {
				this.dealerCashBackToTheCustomer.setDisabled(true);
			}
			readOnlyComponent(isReadOnly("PromotionDialog_DealerCashBackToTheCustomer", true),
					this.dealerCashBackToTheCustomer);
			this.dbdPercentage.setDisabled(false);
			this.space_DBDPercentage.setSclass(PennantConstants.mandateSclass);
			if (this.cashBackPayoutOptions.getSelectedIndex() == 1) {
				readOnlyComponent(isReadOnly("PromotionDialog_DBDFeeType"), this.dbdFeetype);
			}

		} else {
			this.dbdRetained.setChecked(false);
			this.dbdRetained.setDisabled(true);
			this.dealerCashBackToTheCustomer.setValue(0);
			readOnlyComponent(true, this.dealerCashBackToTheCustomer);
			this.dbdPercentage.setDisabled(false);
			this.dbdPercentage.setValue(BigDecimal.ZERO);
			this.dbdPercentage.setDisabled(true);
			this.dbdPercentageCalculationOn.setDisabled(true);
			this.dbdPercentageCalculationOn.setSelectedIndex(0);
			this.space_DBDPercentage.setSclass("");
			readOnlyComponent(true, this.dbdFeetype);
			this.dbdFeetype.setValue("", "");
		}
	}

	public void onCheckmbd() {
		if (this.mbd.isChecked()) {
			this.mbdRetained.setDisabled(false);
			if (this.mbdRetained.isChecked()) {
				this.manufacturerCashbackToTheCustomer.setDisabled(true);
			}
			readOnlyComponent(isReadOnly("PromotionDialog_CashBackFromTheManufacturer", true),
					this.cashBackFromTheManufacturer);
			readOnlyComponent(isReadOnly("PromotionDialog_ManufacturerCashbackToTheCustomer", true),
					this.manufacturerCashbackToTheCustomer);
			if (this.cashBackPayoutOptions.getSelectedIndex() == 1) {
				readOnlyComponent(isReadOnly("PromotionDialog_MBDFeeType"), this.mbdFeetype);
			}
		} else {
			this.mbdRetained.setChecked(false);
			this.mbdRetained.setDisabled(true);
			this.cashBackFromTheManufacturer.setValue(0);
			this.manufacturerCashbackToTheCustomer.setValue(0);
			readOnlyComponent(true, this.cashBackFromTheManufacturer);
			readOnlyComponent(true, this.manufacturerCashbackToTheCustomer);
			this.mbdRetained.setDisabled(true);
			readOnlyComponent(true, this.mbdFeetype);
			this.mbdFeetype.setValue("", "");
		}
	}

	public void onChange$dbdPercentage() {
		if (this.dbdPercentage.getValue() != null && this.dbdPercentage.getValue().compareTo(BigDecimal.ZERO) > 0) {
			this.space_DBDPercentageCalculationOn.setSclass(PennantConstants.mandateSclass);
			this.dbdPercentageCalculationOn.setDisabled(false);
			this.dbdPercentageCalculationOn.setSelectedIndex(1);
		} else {
			this.space_DBDPercentageCalculationOn.setSclass("");
			this.dbdPercentageCalculationOn.setSelectedIndex(0);
		}
	}

	public void onSelect$dbdPercentageCalculationOn() {
		if (this.dbdPercentageCalculationOn.getSelectedIndex() == 0) {
			this.dbdPercentage.setValue(BigDecimal.ZERO);
			this.space_DBDPercentageCalculationOn.setSclass("");
		}
	}

	public void onCheck$dbdRetained() {
		if (this.dbdRetained.isChecked()) {
			this.dealerCashBackToTheCustomer.setValue(0);
			this.dealerCashBackToTheCustomer.setDisabled(true);
		} else {
			this.dealerCashBackToTheCustomer.setDisabled(false);
			onCheck$dbd();
		}
	}

	public void onCheck$mbdRetained() {
		if (this.mbdRetained.isChecked()) {
			this.manufacturerCashbackToTheCustomer.setValue(0);
			this.manufacturerCashbackToTheCustomer.setDisabled(true);
			readOnlyComponent(true, this.manufacturerCashbackToTheCustomer);
		} else {
			this.manufacturerCashbackToTheCustomer.setDisabled(false);
			onCheck$mbd();
		}
	}

	public void onFulfill$dbdFeetype(Event event) {
		logger.debug(Literal.ENTERING);
		onFulfilldbdFeetype();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfilldbdFeetype() {
		Object dataObject = this.dbdFeetype.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.dbdFeetype.setValue("");
			this.dbdFeetype.setDescription("");
		} else {
			FeeType docType = (FeeType) dataObject;
			this.dbdFeetype.setValue(docType.getFeeTypeCode());
			this.dbdFeetype.setDescription(docType.getFeeTypeDesc());
		}
	}

	public void onFulfill$mbdFeetype(Event event) {
		logger.debug(Literal.ENTERING);
		onFulfillmbdFeetype();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfillmbdFeetype() {
		Object dataObject = this.mbdFeetype.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.mbdFeetype.setValue("");
			this.mbdFeetype.setDescription("");
		} else {
			FeeType docType = (FeeType) dataObject;
			this.mbdFeetype.setValue(docType.getFeeTypeCode());
			this.mbdFeetype.setDescription(docType.getFeeTypeDesc());
		}
	}

	public void onFulfill$dbdAndmbdFeetype(Event event) {
		logger.debug(Literal.ENTERING);
		onFulfilldbdAndmbdFeetype();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfilldbdAndmbdFeetype() {
		Object dataObject = this.dbdAndmbdFeetype.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.dbdAndmbdFeetype.setValue("");
			this.dbdAndmbdFeetype.setDescription("");
		} else {
			FeeType docType = (FeeType) dataObject;
			this.dbdAndmbdFeetype.setValue(docType.getFeeTypeCode());
			this.dbdAndmbdFeetype.setDescription(docType.getFeeTypeDesc());
		}
	}

	public FeeType setFeeTypeData(long feeTypeId) {

		if (feeTypeId == 0) {
			return null;
		}

		FeeType feeType;

		Search search = new Search(FeeType.class);
		search.addFilterEqual("FeeTypeId", feeTypeId);

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		feeType = (FeeType) searchProcessor.getResults(search).get(0);

		return feeType;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */

	protected boolean doProcess(Promotion aPromotion, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aPromotion.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
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
			aPromotion.setCDLoan(consumerDurable);
			// FinTypeFees
			List<FinTypeFees> finTypeFees = aPromotion.getFinTypeFeesList();
			if (finTypeFees != null && !finTypeFees.isEmpty()) {
				for (FinTypeFees item : finTypeFees) {
					item.setReferenceId(aPromotion.getReferenceID());
					item.setFinType(aPromotion.getPromotionCode());
				}
			}

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
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Promotion aPromotion = (Promotion) auditHeader.getAuditDetail().getModelData();

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
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.promotion);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
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

	public void setFinTypeAccountingListCtrl(FinTypeAccountingListCtrl finTypeAccountingListCtrl) {
		this.finTypeAccountingListCtrl = finTypeAccountingListCtrl;
	}
}
