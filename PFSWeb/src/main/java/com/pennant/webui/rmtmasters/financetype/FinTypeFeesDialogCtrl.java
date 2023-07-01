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
 * * FileName : FinTypeFeesDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-12-2013 * *
 * Modified Date : 04-12-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-12-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceType/FinTypeFeesDialog.zul file.
 */
public class FinTypeFeesDialogCtrl extends GFCBaseCtrl<FinTypeFees> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinTypeFeesDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinTypeFeesDialog;

	protected ExtendedCombobox feeType;
	protected ExtendedCombobox finEvent;
	protected Combobox calculationType;
	protected ExtendedCombobox ruleCode;
	protected CurrencyBox amount;
	protected Space space_percentage;
	protected Decimalbox percentage;
	protected Intbox feeOrder;
	protected Row row_CalculationOn;
	protected Combobox calculationOn;
	protected Row row_FeeScheduleMethod;
	protected Combobox feeScheduleMethod;
	protected Checkbox alwDeviation;
	protected Decimalbox maxWaiver;
	protected Checkbox alwModifyFee;
	protected Checkbox alwModifyFeeSchdMthd;
	protected Checkbox active;
	protected Checkbox alwPreIncomization;
	protected Label label_Window_Title;
	protected Label label_FinTypeFeesDialog_AlwModifyFeeSchdMthd;

	protected Row row_PercentageType;
	protected Combobox percType;
	protected ExtendedCombobox percRule;
	protected Label label_FinTypeFeesDialog_PercRule;

	// ### START SFA_20210405 -->
	protected Label label_FinTypeFeesDialog_InclForAssignment;
	protected Checkbox inclForAssignment;
	// ### END SFA_20210405 <--

	// not auto wired vars
	private FinTypeFees finTypeFees; // overhanded per param
	private transient FinTypeFeesDialogCtrl finTypeFeesDialogCtrl; // overhanded per

	private transient boolean validationOn;

	private String userRole = "";
	// private FinanceTypeDialogCtrl financeTypeDialogCtrl;
	private FinTypeFeesListCtrl finTypeFeesListCtrl;
	private List<FinTypeFees> finTypeFeesList;
	private int ccyFormat = 0;
	boolean isOriginationFee = false;
	boolean isOverdraft = false;
	private String allowFeeZero = "";
	private boolean excludeAppFeeCodes = false;

	/**
	 * default constructor.<br>
	 */
	public FinTypeFeesDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeFeesDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected finTypeFees object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinTypeFeesDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeFeesDialog);

		try {
			if (arguments.containsKey("finTypeFees")) {
				this.finTypeFees = (FinTypeFees) arguments.get("finTypeFees");
				FinTypeFees befImage = new FinTypeFees();
				BeanUtils.copyProperties(this.finTypeFees, befImage);
				this.finTypeFees.setBefImage(befImage);
				setFinTypeFees(this.finTypeFees);
				this.isOriginationFee = this.finTypeFees.isOriginationFee();
			} else {
				setFinTypeFees(null);
			}

			if (arguments.containsKey("ccyFormat")) {
				ccyFormat = (Integer) arguments.get("ccyFormat");
			}

			if (arguments.containsKey("isOverdraft")) {
				isOverdraft = (boolean) arguments.get("isOverdraft");
			}

			if (arguments.containsKey("finTypeFeesListCtrl")) {
				setFinTypeFeesListCtrl((FinTypeFeesListCtrl) arguments.get("finTypeFeesListCtrl"));
			} else {
				setFinTypeFeesListCtrl(null);
			}

			if (arguments.containsKey("role")) {
				userRole = arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, super.pageRightName);
			}

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = true;
			}

			this.finTypeFees.setWorkflowId(0);
			doLoadWorkFlow(this.finTypeFees.isWorkflow(), this.finTypeFees.getWorkflowId(),
					this.finTypeFees.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			}

			if (arguments.containsKey("excludeAppFeeCodes")) {
				this.excludeAppFeeCodes = (Boolean) arguments.get("excludeAppFeeCodes");
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(getFinTypeFees());
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FinTypeFeesDialog.onClose();
		}
		logger.debug("Leaving");
	}

	public FinTypeFees getFinTypeFees() {
		return finTypeFees;
	}

	public void setFinTypeFees(FinTypeFees finTypeFees) {
		this.finTypeFees = finTypeFees;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.feeType.setMaxlength(8);
		this.feeType.getTextbox().setWidth("150px");
		this.feeType.setMandatoryStyle(true);
		this.feeType.setModuleName("FeeType");
		this.feeType.setValueColumn("FeeTypeCode");
		this.feeType.setDescColumn("FeeTypeDesc");
		this.feeType.setValidateColumns(new String[] { "FeeTypeCode" });

		if (this.excludeAppFeeCodes) {
			Filter[] filters = new Filter[3];
			filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("FeeTypeCode", Allocation.BOUNCE, Filter.OP_NOT_EQUAL);
			filters[2] = new Filter("FeeTypeCode", Allocation.ODC, Filter.OP_NOT_EQUAL);
			this.feeType.setFilters(filters);
		} else {
			this.feeType.setFilters(new Filter[] { new Filter("Active", 1, Filter.OP_EQUAL) });
		}

		this.finEvent.setMaxlength(8);
		this.finEvent.setMandatoryStyle(true);
		this.finEvent.setModuleName("AccountEngineEvent");
		this.finEvent.setValueColumn("AEEventCode");
		this.finEvent.setDescColumn("AEEventCodeDesc");
		this.finEvent.setValidateColumns(new String[] { "AEEventCode" });

		this.ruleCode.setMaxlength(8);
		this.ruleCode.setMandatoryStyle(true);
		this.ruleCode.setModuleName("Rule");
		this.ruleCode.setValueColumn("RuleCode");
		this.ruleCode.setDescColumn("RuleCodeDesc");
		this.ruleCode.setValidateColumns(new String[] { "RuleCode" });

		this.percRule.setMaxlength(8);
		this.percRule.setModuleName("Rule");
		this.percRule.setValueColumn("RuleCode");
		this.percRule.setDescColumn("RuleCodeDesc");
		this.percRule.setValidateColumns(new String[] { "RuleCode" });
		this.percRule.setMandatoryStyle(true);

		this.amount.setMandatory(false);
		this.amount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.amount.setScale(ccyFormat);

		this.percentage.setMaxlength(6);
		this.maxWaiver.setMaxlength(6);
		this.feeOrder.setMaxlength(4);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		if (FeeExtension.ALLOW_SINGLE_FEE_CONFIG) {
			this.label_FinTypeFeesDialog_InclForAssignment.setVisible(true);
			this.inclForAssignment.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("FinTypeFeesDialog", userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypeFeesDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypeFeesDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeFeesDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeFeesDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_FinTypeFeesDialog);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDelete(Event event) {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.finTypeFees.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinTypeFees FinTypeFees
	 */
	public void doWriteBeanToComponents(FinTypeFees aFinTypeFees) {
		logger.debug("Entering");

		String excluedeFields = "";
		this.feeType.setObject(setObjectAsLong(aFinTypeFees.getFeeTypeID()));
		this.feeType.setValue(aFinTypeFees.getFeeTypeCode());
		this.feeType.setDescription(StringUtils.trimToEmpty(aFinTypeFees.getFeeTypeDesc()));
		this.finEvent.setValue(aFinTypeFees.getFinEvent());

		if (StringUtils.isEmpty(aFinTypeFees.getFinEventDesc())) {
			aFinTypeFees.setFinEventDesc(this.finEvent.getDescription());
		} else {
			this.finEvent.setDescription(aFinTypeFees.getFinEventDesc());
		}

		this.ruleCode.setValue(aFinTypeFees.getRuleCode());
		this.ruleCode.setDescription(StringUtils.trimToEmpty(aFinTypeFees.getRuleDesc()));
		this.amount.setValue(CurrencyUtil.parse(aFinTypeFees.getAmount(), ccyFormat));
		this.percentage.setValue(aFinTypeFees.getPercentage());
		this.feeOrder.setValue(aFinTypeFees.getFeeOrder());
		this.maxWaiver.setValue(aFinTypeFees.getMaxWaiverPerc());

		String calOnExcludeFields = "," + PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE + ",";

		fillComboBox(this.calculationType, aFinTypeFees.getCalculationType(),
				PennantStaticListUtil.getFeeCalculationTypes(), "");

		if (isOriginationFee) {
			calOnExcludeFields = calOnExcludeFields + PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL + ",";
			if (isOverdraft) {
				this.finEvent.setList(AccountingEngine.getOverDraftEvents());
			} else {
				this.finEvent.setList(AccountingEngine.getOrginationEvents());
			}
		} else {
			if (StringUtils.equals(aFinTypeFees.getFinEvent(), AccountingEvent.REPAY)
					|| StringUtils.equals(aFinTypeFees.getFinEvent(), AccountingEvent.EARLYPAY)
					|| StringUtils.equals(aFinTypeFees.getFinEvent(), AccountingEvent.EARLYSTL)) {
				calOnExcludeFields = "";
			}
			this.finEvent.setList(AccountingEngine.getServicingEvents());
		}

		if (!StringUtils.equals(aFinTypeFees.getFinEvent(), AccountingEvent.EARLYSTL)) {
			calOnExcludeFields = "," + PennantConstants.FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE + ",";
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_FEE_CALC_ADJU_PRINCIPAL)) {
			calOnExcludeFields = calOnExcludeFields + PennantConstants.FEE_CALCULATEDON_ADJUSTEDPRINCIPAL + ",";
		}

		fillComboBox(this.calculationOn, aFinTypeFees.getCalculateOn(), PennantStaticListUtil.getFeeCalculatedOnList(),
				calOnExcludeFields);

		if (StringUtils.equals(aFinTypeFees.getFinEvent(), AccountingEvent.CMTDISB)) {
			excluedeFields = getExcludeFields();
		} else if (AccountingEvent.RESTRUCTURE.equals(aFinTypeFees.getFinEvent())) {
			excluedeFields = getRestructureExcludeFields();
		}

		if (!FeeExtension.ALLOW_PAID_FEE_SCHEDULE_METHOD) { // Paid by customer and waived by bank has been
															// excluded
			excluedeFields = excluedeFields + "," + CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ","
					+ CalculationConstants.REMFEE_WAIVED_BY_BANK + ",";
		}

		fillComboBox(this.feeScheduleMethod, aFinTypeFees.getFeeScheduleMethod(),
				PennantStaticListUtil.getRemFeeSchdMethods(), excluedeFields);
		this.alwDeviation.setChecked(aFinTypeFees.isAlwDeviation());
		this.alwModifyFee.setChecked(aFinTypeFees.isAlwModifyFee());
		this.alwModifyFeeSchdMthd.setChecked(aFinTypeFees.isAlwModifyFeeSchdMthd());

		if (aFinTypeFees.isNewRecord()) {
			this.alwModifyFeeSchdMthd.setChecked(true);
		}

		this.percRule.setValue(aFinTypeFees.getPercRule());
		fillComboBox(this.percType, aFinTypeFees.getPercType(), PennantStaticListUtil.getPercType(), "");

		this.active.setChecked(aFinTypeFees.isActive());
		this.alwPreIncomization.setChecked(aFinTypeFees.isAlwPreIncomization());
		this.recordStatus.setValue(aFinTypeFees.getRecordStatus());

		// ### START SFA_20210405 -->
		if (FeeExtension.ALLOW_SINGLE_FEE_CONFIG) {
			this.inclForAssignment.setChecked(aFinTypeFees.isInclForAssignment());
		}
		// ### END SFA_20210405 <--

		doSetRuleFilters(this.ruleCode, "FEES");
		doSetRuleFilters(this.percRule, "FEEPERC");
		doSetConditionalProp();
		doSetCalculationTypeProp();
		doSetFeeSchdMethodProp();
		// CR : Deduct fee at the time of additional disbursal
		doSetFeeSchdMethod(aFinTypeFees.getFinEvent());
		doSetPercTypeProp();
		if (AccountingEvent.RESTRUCTURE.equals(aFinTypeFees.getFinEvent())) {
			this.alwModifyFee.setChecked(false);
			this.alwDeviation.setDisabled(true);
			this.maxWaiver.setValue(BigDecimal.ZERO);
			readOnlyComponent(true, this.maxWaiver);
		}

		logger.debug("Leaving");
	}

	private String getExcludeFields() {

		return "," + CalculationConstants.REMFEE_PART_OF_DISBURSE + "," + CalculationConstants.REMFEE_PART_OF_SALE_PRICE
				+ "," + CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + ","
				+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + ","
				+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ",";
	}

	private String getRestructureExcludeFields() {

		return "," + CalculationConstants.REMFEE_PART_OF_DISBURSE + ","
				+ CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT + ","
				+ CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR + ","
				+ CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS + ",";
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinTypeFees
	 */
	public void doWriteComponentsToBean(FinTypeFees aFinTypeFees) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			if (readIDValueFromExtCombobox(this.feeType) == null) {
				this.feeType.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinTypeFeesDialog_FeeType.value"), null, true, true));
			}
			aFinTypeFees.setFeeTypeID(readIDValueFromExtCombobox(this.feeType));
			aFinTypeFees.setFeeTypeCode(this.feeType.getValue());
			aFinTypeFees.setFeeTypeDesc(this.feeType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypeFees.setFinEvent(this.finEvent.getValue());
			aFinTypeFees.setFinEventDesc(this.finEvent.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.calculationType))) {
				throw new WrongValueException(this.calculationType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeFeesDialog_CalculationType.value") }));
			}
			aFinTypeFees.setCalculationType(getComboboxValue(this.calculationType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_CalculationOn.isVisible() && "#".equals(getComboboxValue(this.calculationOn))) {
				throw new WrongValueException(this.calculationOn, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinTypeFeesDialog_CalculationOn.value") }));
			}
			aFinTypeFees.setCalculateOn(getComboboxValue(this.calculationOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			/*
			 * if (this.row_FeeScheduleMethod.isVisible() && "#".equals(getComboboxValue(this.feeScheduleMethod))) {
			 * throw new WrongValueException(this.feeScheduleMethod, Labels.getLabel("STATIC_INVALID", new String[] {
			 * Labels.getLabel("label_FinTypeFeesDialog_FeeScheduleMethod.value") })); }
			 */
			aFinTypeFees.setFeeScheduleMethod(getComboboxValue(this.feeScheduleMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setRuleCode(this.ruleCode.getValue());
			aFinTypeFees.setRuleDesc(this.ruleCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setAmount(CurrencyUtil.unFormat(
					this.amount.isReadonly() ? this.amount.getActualValue() : this.amount.getValidateValue(),
					ccyFormat));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.percentage.isVisible() && !this.percentage.isDisabled()) {
				BigDecimal percentageValue = this.percentage.getValue();

				if (percentageValue == null || percentageValue.compareTo(BigDecimal.ZERO) == 0) {
					throw new WrongValueException(this.percentage, Labels.getLabel("NUMBER_MINVALUE",
							new String[] { Labels.getLabel("label_FinTypeFeesDialog_RuleAmtPerc.value"), "0" }));
				} else if (percentageValue.compareTo(BigDecimal.ZERO) != 1) {
					throw new WrongValueException(this.percentage, Labels.getLabel("FIELD_NO_NEGATIVE",
							new String[] { Labels.getLabel("label_FinTypeFeesDialog_RuleAmtPerc.value") }));
				} else if (percentageValue.compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(this.percentage, Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] { Labels.getLabel("label_FinTypeFeesDialog_RuleAmtPerc.value"), "100" }));
				}
			}
			aFinTypeFees.setPercentage(
					new BigDecimal(PennantApplicationUtil.formatRate(this.percentage.getValue().doubleValue(), 2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.maxWaiver.isReadonly() && this.maxWaiver.getValue() != null) {
				if (this.maxWaiver.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(this.maxWaiver, Labels.getLabel("PERCENT_NOTNEGATIVE_LABEL"));
				} else if ((this.maxWaiver.getValue()).compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(this.maxWaiver, Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] { Labels.getLabel("label_FinTypeFeesDialog_MaxWaiver.value"), "100" }));
				}
			}
			aFinTypeFees.setMaxWaiverPerc(
					new BigDecimal(PennantApplicationUtil.formatRate(this.maxWaiver.doubleValue(), 2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.feeOrder.intValue() == 0) {
				throw new WrongValueException(this.feeOrder, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_FinTypeFeesDialog_Order.value") }));
			}
			if (this.feeOrder.getValue() < 0) {
				throw new WrongValueException(this.feeOrder, Labels.getLabel("FIELD_NO_NEGATIVE",
						new String[] { Labels.getLabel("label_FinTypeFeesDialog_Order.value") }));
			}
			aFinTypeFees.setFeeOrder(this.feeOrder.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setAlwDeviation(this.alwDeviation.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setAlwModifyFee(this.alwModifyFee.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setAlwModifyFeeSchdMthd(this.alwModifyFeeSchdMthd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setAlwPreIncomization(this.alwPreIncomization.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.percRule.getValidatedValue();
			aFinTypeFees.setPercRule(this.percRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinTypeFees.setPercType(this.percType.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// ### START SFA_20210405 -->
		if (FeeExtension.ALLOW_SINGLE_FEE_CONFIG) {
			try {
				aFinTypeFees.setInclForAssignment(this.inclForAssignment.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// ### END SFA_20210405 <--

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinTypeFees.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinTypeFees
	 */
	public void doShowDialog(FinTypeFees aFinTypeFees) {
		logger.debug("Entering");
		// set Readonly mode accordingly if the object is new or not.
		if (aFinTypeFees.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.feeType.focus();
		} else {
			this.calculationType.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypeFeesDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinTypeFees);
			allowFeeZero = SysParamUtil.getValueAsString(SMTParameterConstants.FEESALLOWZERO);
			if (StringUtils.equals(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT,
					this.calculationType.getSelectedItem().getValue().toString())
					&& StringUtils.equals("Y", allowFeeZero)) {
				this.amount.setMandatory(false);
			}
			this.window_FinTypeFeesDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.feeType.isReadonly()) {
			this.feeType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinTypeFeesDialog_FeeType.value"), null, true, true));
		}
		if (!this.finEvent.isReadonly()) {
			this.finEvent.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinTypeFeesDialog_FinEvent.value"), null, true, true));
		}
		if (this.row_FeeScheduleMethod.isVisible() && !this.feeScheduleMethod.isReadonly()) {
			this.feeScheduleMethod.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinTypeFeesDialog_FeeScheduleMethod.value"), null, true));
		}
		if (this.ruleCode.isVisible() && !this.ruleCode.isReadonly()) {
			this.ruleCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinTypeFeesDialog_RuleAmtPerc.value"), null, true, true));
		}
		if (!this.amount.isDisabled()) {
			this.amount
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinTypeFeesDialog_RuleAmtPerc.value"),
							ccyFormat, this.amount.isVisible(), false));
		}
		if (this.row_PercentageType.isVisible()) {
			if (!this.percType.isDisabled()) {
				this.percType.setConstraint(new StaticListValidator(PennantStaticListUtil.getPercType(),
						Labels.getLabel("label_FinTypeFeesDialog_PercentageTyp.value")));
			}
			if (!this.percRule.isReadonly()) {
				if (PennantConstants.PERC_TYPE_VARIABLE.equals(percType.getSelectedItem().getValue())) {
					this.percRule.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinTypeFeesDialog_PercRule.value"), null, true, true));
				} else {
					this.percRule.setConstraint("");
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.feeType.setConstraint("");
		this.finEvent.setConstraint("");
		this.calculationType.setConstraint("");
		this.calculationOn.setConstraint("");
		this.feeScheduleMethod.setConstraint("");
		this.ruleCode.setConstraint("");
		this.amount.setConstraint("");
		this.percentage.setConstraint("");
		this.feeOrder.setConstraint("");
		this.maxWaiver.setConstraint("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final FinTypeFees aFinTypeFees, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = newFinTypeFeesProcess(aFinTypeFees, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeFeesDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			if (this.isOriginationFee) {
				getFinTypeFeesListCtrl().doFillFinTypeFeesOrigination(this.finTypeFeesList);
			} else {
				getFinTypeFeesListCtrl().doFillFinTypeFeesServicing(this.finTypeFeesList);
			}
			return true;
		}
		return false;
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);
		final FinTypeFees aFinTypeFees = new FinTypeFees();
		BeanUtils.copyProperties(getFinTypeFees(), aFinTypeFees);

		final String keyReference = Labels.getLabel("label_FinTypeFeesDialog_FeeType.value") + " : "
				+ aFinTypeFees.getFeeTypeCode() + "," + Labels.getLabel("label_FinTypeFeesDialog_FinEvent.value")
				+ " : " + aFinTypeFees.getFinEvent();

		doDelete(keyReference, aFinTypeFees);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getFinTypeFees().isNewRecord()) {
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_feeType"), this.feeType);
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_finEvent"), this.finEvent);
		} else {
			this.feeType.setReadonly(true);
			this.finEvent.setReadonly(true);
		}
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_calculationType"), this.calculationType);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_ruleAmtPerc"), this.ruleCode);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_ruleAmtPerc"), this.amount);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_ruleAmtPerc"), this.percentage);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_calculationOn"), this.calculationOn);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_order"), this.feeOrder);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_feeScheduleMethod"), this.feeScheduleMethod);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_alwDeviation"), this.alwDeviation);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_maxWaiver"), this.maxWaiver);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_alwModifyFee"), this.alwModifyFee);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_alwModifyFeeSchdMthd"), this.alwModifyFeeSchdMthd);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_active"), this.active);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_percType"), this.percType);
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_percRule"), this.percRule);

		if (enqiryModule) {
			this.feeType.setReadonly(true);
			this.finEvent.setReadonly(true);
			this.calculationType.setDisabled(true);
			this.ruleCode.setReadonly(true);
			this.amount.setReadonly(true);
			this.percentage.setReadonly(true);
			this.calculationOn.setReadonly(true);
			this.feeOrder.setReadonly(true);
			this.feeScheduleMethod.setDisabled(true);
			this.alwDeviation.setDisabled(true);
			this.maxWaiver.setDisabled(true);
			this.alwModifyFee.setDisabled(true);
			this.alwModifyFeeSchdMthd.setDisabled(true);
			this.active.setDisabled(true);
			this.btnSave.setDisabled(true);
			this.btnDelete.setDisabled(true);
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_percType"), this.percType);
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_percRule"), this.percRule);

		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypeFees.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		readOnlyComponent(true, this.feeType);
		readOnlyComponent(true, this.finEvent);
		readOnlyComponent(true, this.calculationType);
		readOnlyComponent(true, this.ruleCode);
		readOnlyComponent(true, this.amount);
		readOnlyComponent(true, this.alwDeviation);
		readOnlyComponent(true, this.alwModifyFee);
		readOnlyComponent(true, this.alwModifyFeeSchdMthd);
		readOnlyComponent(true, this.percentage);
		readOnlyComponent(true, this.calculationOn);
		readOnlyComponent(true, this.feeOrder);
		readOnlyComponent(true, this.feeScheduleMethod);
		readOnlyComponent(true, this.maxWaiver);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.alwPreIncomization);
		readOnlyComponent(true, this.inclForAssignment);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
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
		// remove validation, if there are a save before
		this.feeType.setValue("");
		this.feeType.setDescription("");
		this.finEvent.setValue("");
		this.finEvent.setDescription("");
		this.ruleCode.setValue("");
		this.ruleCode.setDescription("");
		this.amount.setValue("");
		this.percentage.setValue("");
		this.maxWaiver.setValue(BigDecimal.ZERO);
		this.calculationType.setValue("");
		this.calculationOn.setValue("");
		this.feeScheduleMethod.setValue("");
		this.alwDeviation.setChecked(false);
		this.alwModifyFee.setChecked(false);
		this.alwModifyFeeSchdMthd.setChecked(false);
		this.active.setChecked(false);
		this.alwPreIncomization.setChecked(false);
		this.feeOrder.setValue(0);
		this.inclForAssignment.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinTypeFees aFinTypeFees = new FinTypeFees();
		BeanUtils.copyProperties(getFinTypeFees(), aFinTypeFees);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinTypeFees object with the components data
		doWriteComponentsToBean(aFinTypeFees);
		if (validateFeeOrder(aFinTypeFees)) {
			return;
		}

		/*
		 * if(!finTypeFeesListCtrl.validateFeeAccounting(aFinTypeFees,true)){ return; }
		 */

		// Write the additional validations as per below example
		// get the selected FinTypeFees object from the listbox
		// Do data level validations here
		isNew = aFinTypeFees.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinTypeFees.getRecordType())) {
				aFinTypeFees.setVersion(aFinTypeFees.getVersion() + 1);
				if (isNew) {
					aFinTypeFees.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinTypeFees.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinTypeFees.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aFinTypeFees.setVersion(1);
				aFinTypeFees.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aFinTypeFees.getRecordType())) {
				aFinTypeFees.setVersion(aFinTypeFees.getVersion() + 1);
				aFinTypeFees.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aFinTypeFees.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			AuditHeader auditHeader = newFinTypeFeesProcess(aFinTypeFees, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinTypeFeesDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (this.isOriginationFee) {
					getFinTypeFeesListCtrl().doFillFinTypeFeesOrigination(this.finTypeFeesList);
				} else {
					getFinTypeFeesListCtrl().doFillFinTypeFeesServicing(this.finTypeFeesList);
				}
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method validates FinTypeFees details <br>
	 * and will return AuditHeader
	 *
	 */
	private AuditHeader newFinTypeFeesProcess(FinTypeFees aFinTypeFees, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFinTypeFees, tranType);
		finTypeFeesList = new ArrayList<FinTypeFees>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aFinTypeFees.getFeeTypeCode();
		valueParm[1] = aFinTypeFees.getFinEvent();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypeFeesDialog_FeeType.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinTypeFeesDialog_FinEvent.value") + ":" + valueParm[1];
		List<FinTypeFees> finTypeExistingList = null;
		if (this.isOriginationFee) {
			finTypeExistingList = getFinTypeFeesListCtrl().getFinTypeFeesOriginationList();
		} else {
			finTypeExistingList = getFinTypeFeesListCtrl().getFinTypeFeesServicingList();
		}

		if (finTypeExistingList != null && finTypeExistingList.size() > 0) {
			for (int i = 0; i < finTypeExistingList.size(); i++) {
				FinTypeFees finTypeFees = finTypeExistingList.get(i);
				if (finTypeFees.getFinEvent().equals(aFinTypeFees.getFinEvent())
						&& finTypeFees.getFeeTypeID().equals(aFinTypeFees.getFeeTypeID())
						&& finTypeFees.isOriginationFee() == aFinTypeFees.isOriginationFee()) {
					// Both Current and Existing list rating same
					if (aFinTypeFees.isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aFinTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aFinTypeFees.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finTypeFeesList.add(aFinTypeFees);
						} else if (aFinTypeFees.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aFinTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aFinTypeFees.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finTypeFeesList.add(aFinTypeFees);
						} else if (aFinTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							// List<FinTypeFees> savedList =
							// getFinTypeFeesListCtrl().getFinanceType().getFinTypeFeesList();
							List<FinTypeFees> savedList = getFinTypeFeesListCtrl().getFinTypeFeesList();
							for (int j = 0; j < savedList.size(); j++) {
								FinTypeFees accType = savedList.get(j);
								if (accType.getFinType().equals(aFinTypeFees.getFinType())) {
									finTypeFeesList.add(accType);
								}
							}
						} else if (aFinTypeFees.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aFinTypeFees.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							finTypeFeesList.add(finTypeFees);
						}
					}
				} else {
					finTypeFeesList.add(finTypeFees);
				}
			}
		}
		if (!recordAdded) {
			finTypeFeesList.add(aFinTypeFees);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	private Object setObjectAsLong(Long value) {
		if (value == null || value == Long.MIN_VALUE) {
			return null;
		}
		return value;
	}

	private Long readIDValueFromExtCombobox(ExtendedCombobox extendedCombobox) {
		Object obj = extendedCombobox.getObject();
		if (obj != null) {
			if (obj instanceof Long) {
				return ((Long) obj).longValue();
			} else if (obj instanceof FeeType) {
				FeeType feeType = (FeeType) obj;
				return feeType.getFeeTypeID();
			}
		}
		return null;
	}

	public void onCheck$alwDeviation(Event event) {
		logger.debug("Entering");
		if (this.alwDeviation.isChecked()) {
			readOnlyComponent(true, this.maxWaiver);
			this.maxWaiver.setValue(BigDecimal.valueOf(100));
		} else {
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_maxWaiver"), this.maxWaiver);
		}
		logger.debug("Leaving");
	}

	public void onSelect$percType(Event event) {
		logger.debug(Literal.ENTERING);
		doSetPercTypeProp();
		logger.debug(Literal.LEAVING);

	}

	private void doSetPercTypeProp() {
		if (finTypeFees.isOriginationFee()) {
			this.percRule.setVisible(false);
			this.label_FinTypeFeesDialog_PercRule.setVisible(false);
			this.percRule.setValue("");
			this.percRule.setConstraint("");
			this.percRule.clearErrorMessage();
			this.percentage.setValue(this.percentage.getValue());
			this.percentage.setDisabled(false);
			return;
		}

		String calType = this.calculationType.getSelectedItem().getValue();
		String perType = this.percType.getSelectedItem().getValue();
		if (PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(calType)
				&& PennantConstants.PERC_TYPE_VARIABLE.equals(perType)) {
			this.percRule.setVisible(true);
			this.percRule.setReadonly(false);
			this.percRule.clearErrorMessage();
			this.feeOrder.setErrorMessage("");
			this.label_FinTypeFeesDialog_PercRule.setVisible(true);
			this.percentage.setValue(BigDecimal.ZERO);
			this.percentage.setDisabled(true);
		} else {
			this.percentage.setDisabled(false);
			this.percRule.setReadonly(true);
			this.percRule.setErrorMessage("");
			this.feeOrder.setErrorMessage("");

		}
	}

	public void onSelect$calculationType(Event event) {
		logger.debug("Entering");
		this.ruleCode.setValue("");
		this.ruleCode.setDescription("");
		this.amount.setValue(BigDecimal.ZERO);
		this.percentage.setValue(BigDecimal.ZERO);
		this.calculationOn.setVisible(true);
		doSetCalculationTypeProp();
		// Fees allowed zero when calculation type is Fixed Amount
		if (StringUtils.equals(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT,
				this.calculationType.getSelectedItem().getValue().toString())
				&& StringUtils.equals("Y", allowFeeZero)) {
			this.amount.setMandatory(false);
		}
		logger.debug("Leaving");
	}

	public void onChange$feeScheduleMethod(Event event) {
		logger.debug("Entering");
		doSetFeeSchdMethodProp();
		logger.debug("Leaving");
	}

	public void onFulfill$finEvent(Event event) {
		logger.debug(Literal.ENTERING);

		String excluedeFields = "";
		this.ruleCode.setObject("");
		this.ruleCode.setValue("", "");

		doSetRuleFilters(this.ruleCode, "FEES");
		doSetRuleFilters(this.percRule, "FEEPERC");
		String finEventValue = this.finEvent.getValue();
		String calOnExcludeFields = "," + PennantConstants.FEE_CALCULATEDON_PAYAMOUNT + ",";

		if (isOriginationFee) {
			calOnExcludeFields = calOnExcludeFields + PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL + ",";
		} else {
			if (StringUtils.equals(finEventValue, AccountingEvent.REPAY)
					|| StringUtils.equals(finEventValue, AccountingEvent.EARLYPAY)
					|| StringUtils.equals(finEventValue, AccountingEvent.EARLYSTL)) {
				calOnExcludeFields = ",";
			}
		}

		this.alwModifyFee.setDisabled(isReadOnly("FinTypeFeesDialog_alwModifyFee"));
		readOnlyComponent(isReadOnly("FinTypeFeesDialog_maxWaiver"), this.maxWaiver);

		if (AccountingEvent.CMTDISB.equals(finEventValue)) {
			excluedeFields = getExcludeFields();
		} else if (AccountingEvent.RESTRUCTURE.equals(finEventValue)) {
			excluedeFields = getRestructureExcludeFields();
			this.alwModifyFee.setChecked(false);
			this.alwDeviation.setChecked(false);
			this.alwDeviation.setDisabled(true);
			this.maxWaiver.setValue(BigDecimal.ZERO);
			readOnlyComponent(true, this.maxWaiver);
		}

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_FEE_CALC_ADJU_PRINCIPAL)) {
			calOnExcludeFields = calOnExcludeFields + PennantConstants.FEE_CALCULATEDON_ADJUSTEDPRINCIPAL + ",";
		}

		fillComboBox(this.feeScheduleMethod, "", PennantStaticListUtil.getRemFeeSchdMethods(), excluedeFields);
		if (StringUtils.equals(finEventValue, AccountingEvent.EARLYSTL)) {
			fillComboBox(this.calculationOn, "", PennantStaticListUtil.getFeeCalculatedOnList(), calOnExcludeFields);
		} else {
			calOnExcludeFields = calOnExcludeFields + PennantConstants.FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE + ",";
			fillComboBox(this.calculationOn, "", PennantStaticListUtil.getFeeCalculatedOnList(), calOnExcludeFields);
		}

		doSetFeeSchdMethod(finEventValue);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$feeType(Event event) {
		this.ruleCode.setObject("");
		this.ruleCode.setValue("", "");
		doSetRuleFilters(this.ruleCode, "FEES");
		doSetRuleFilters(this.percRule, "FEEPERC");

		doSetFeeSchdMethod(this.finEvent.getValue());
	}

	private void doSetRuleFilters(ExtendedCombobox rule, String type) {
		Filter[] filters = new Filter[4];
		filters[0] = new Filter("RuleModule", type, Filter.OP_EQUAL);
		filters[1] = new Filter("RuleEvent", this.finEvent.getValue(), Filter.OP_EQUAL);
		filters[2] = new Filter("FeeTypeID", readIDValueFromExtCombobox(this.feeType), Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		rule.setFilters(filters);
	}

	private void doSetConditionalProp() {
		if (this.isOriginationFee) {
			this.row_FeeScheduleMethod.setVisible(true);
			this.label_Window_Title.setValue(Labels.getLabel("label_FeeDetails_Origination"));
		} else {
			this.row_FeeScheduleMethod.setVisible(false);
			this.label_Window_Title.setValue(Labels.getLabel("label_FeeDetails_Servicing"));
		}
		if (this.alwDeviation.isChecked()) {
			readOnlyComponent(true, this.maxWaiver);
			this.maxWaiver.setValue(BigDecimal.ZERO);
		} else {
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_maxWaiver"), this.maxWaiver);
		}
	}

	private void doSetCalculationTypeProp() {
		if (StringUtils.equals("Y", allowFeeZero)) {
			this.amount.setMandatory(false);
		} else {
			this.amount.setMandatory(true);
		}

		String calType = this.calculationType.getSelectedItem().getValue();

		switch (calType) {
		case PennantConstants.FEE_CALCULATION_TYPE_RULE:
			this.ruleCode.setVisible(true);
			this.amount.setVisible(false);
			this.amount.setMandatory(false);
			this.percentage.setVisible(false);
			this.row_CalculationOn.setVisible(false);
			this.space_percentage.setVisible(false);
			break;
		case PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT:
			this.amount.setVisible(true);
			this.amount.setMandatory(true);
			this.ruleCode.setVisible(false);
			this.percentage.setVisible(false);
			this.row_CalculationOn.setVisible(false);
			this.space_percentage.setVisible(false);
			break;
		case PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE:
			this.percentage.setVisible(true);
			this.ruleCode.setVisible(false);
			this.amount.setVisible(false);
			this.amount.setMandatory(false);
			this.row_CalculationOn.setVisible(true);
			this.space_percentage.setVisible(true);
			break;
		default:
			break;
		}

		if (finTypeFees.isOriginationFee()) {
			this.row_PercentageType.setVisible(false);
			this.percType.setSelectedIndex(0);
			this.percRule.setValue("");
			this.percRule.setConstraint("");
			this.percType.setConstraint("");
			return;
		}

		if (PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(calType)) {
			this.row_PercentageType.setVisible(true);
		} else {
			this.row_PercentageType.setVisible(false);
			this.percType.setSelectedIndex(0);
			this.percRule.setValue("");
			this.percRule.setConstraint("");
			this.percType.setConstraint("");
			this.percType.clearErrorMessage();
		}
	}

	private void doSetFeeSchdMethodProp() {
		String feeScheduleMethodValue = this.feeScheduleMethod.getSelectedItem().getValue();
		switch (feeScheduleMethodValue) {
		case CalculationConstants.REMFEE_PAID_BY_CUSTOMER:
			readOnlyComponent(true, this.maxWaiver);
			this.maxWaiver.setValue(BigDecimal.ZERO);
			this.alwDeviation.setDisabled(true);
			this.alwDeviation.setChecked(false);
			this.alwModifyFeeSchdMthd.setDisabled(true);
			this.alwModifyFeeSchdMthd.setChecked(true);
			break;
		case CalculationConstants.REMFEE_WAIVED_BY_BANK:
			readOnlyComponent(true, this.maxWaiver);
			this.maxWaiver.setValue(BigDecimal.valueOf(100));
			this.alwDeviation.setDisabled(true);
			this.alwDeviation.setChecked(false);
			this.alwModifyFeeSchdMthd.setDisabled(true);
			this.alwModifyFeeSchdMthd.setChecked(true);
			break;
		case CalculationConstants.FEE_SUBVENTION:
			this.alwPreIncomization.setDisabled(true);
			this.calculationType.setDisabled(false);
			this.amount.setReadonly(false);
			this.ruleCode.setReadonly(false);
			this.percentage.setReadonly(false);
			this.feeScheduleMethod.setDisabled(false);
			this.alwModifyFee.setDisabled(isReadOnly("FinTypeFeesDialog_alwModifyFee"));
			this.alwDeviation.setChecked(false);
			this.btnSave.setDisabled(false);

			break;
		default:
			readOnlyComponent(isReadOnly("FinTypeFeesDialog_maxWaiver"), this.maxWaiver);
			this.maxWaiver.setValue(getFinTypeFees().getMaxWaiverPerc());
			this.alwModifyFee.setDisabled(isReadOnly("FinTypeFeesDialog_alwModifyFee"));
			this.alwModifyFeeSchdMthd.setDisabled(isReadOnly("FinTypeFeesDialog_alwModifyFeeSchdMthd"));
			this.alwDeviation.setDisabled(isReadOnly("FinTypeFeesDialog_alwDeviation"));
			break;
		}
	}

	private boolean validateFeeOrder(FinTypeFees finTypeFeesTemp) {
		List<FinTypeFees> finTypeExistingList = null;
		if (this.isOriginationFee) {
			finTypeExistingList = getFinTypeFeesListCtrl().getFinTypeFeesOriginationList();
		} else {
			finTypeExistingList = getFinTypeFeesListCtrl().getFinTypeFeesServicingList();
		}
		if (finTypeExistingList != null && !finTypeExistingList.isEmpty()) {
			for (FinTypeFees finTypeFees : finTypeExistingList) {
				if (StringUtils.equals(finTypeFees.getFinEvent(), finTypeFeesTemp.getFinEvent())
						&& !StringUtils.equals(finTypeFees.getFeeTypeCode(), finTypeFeesTemp.getFeeTypeCode())
						&& finTypeFees.getFeeOrder() == finTypeFeesTemp.getFeeOrder()
						&& finTypeFees.isOriginationFee() == this.isOriginationFee) {
					MessageUtil.showError(Labels.getLabel("FeeOrder_Duplication_NotAllowed",
							new String[] { Integer.toString(finTypeFeesTemp.getFeeOrder()) }));
					return true;
				}
			}
		}
		return false;
	}

	public static String getEventDesc(String value, List<AccountEngineEvent> list) {
		for (int i = 0; i < list.size(); i++) {
			if (StringUtils.equals(list.get(i).getAEEventCode(), value)) {
				return list.get(i).getAEEventCodeDesc();
			}
		}
		return "";
	}

	/**
	 * Fee Schedule Method Defaulted with 'Deduct from disbursement' For Add Disbursement Event. other event all the Fee
	 * Schedule Method for selection.
	 * 
	 */
	private void doSetFeeSchdMethod(String finEventValue) {
		String excluedeFields = "";
		this.alwModifyFeeSchdMthd.setVisible(false);
		this.label_FinTypeFeesDialog_AlwModifyFeeSchdMthd.setVisible(false);
		this.feeScheduleMethod.setReadonly(isReadOnly("FinTypeFeesDialog_feeScheduleMethod"));
		if (AccountingEvent.CMTDISB.equals(finEventValue)) {
			excluedeFields = getExcludeFields();
		}
		fillComboBox(this.feeScheduleMethod, getFinTypeFees().getFeeScheduleMethod(),
				PennantStaticListUtil.getRemFeeSchdMethods(), excluedeFields);
		if (CalculationConstants.FEE_SUBVENTION.equals(this.feeType.getValue())) {
			this.feeScheduleMethod.setValue(Labels.getLabel("label_Fee_Subvention"));
			this.feeScheduleMethod.setReadonly(true);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	private AuditHeader getAuditHeader(FinTypeFees aFinTypeFees, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypeFees.getBefImage(), aFinTypeFees);
		return new AuditHeader(aFinTypeFees.getFinType(), null, null, null, auditDetail, aFinTypeFees.getUserDetails(),
				getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypeFeesDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.finTypeFees);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finTypeFees.getFinEvent());
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.feeType.setErrorMessage("");
		this.finEvent.setErrorMessage("");
		this.ruleCode.setErrorMessage("");
		this.amount.setErrorMessage("");
		this.percentage.setErrorMessage("");
		this.feeOrder.setErrorMessage("");
		this.calculationType.setErrorMessage("");
		this.calculationOn.setErrorMessage("");
		this.feeScheduleMethod.setErrorMessage("");
		this.maxWaiver.setErrorMessage("");
		logger.debug("Leaving");
	}

	public FinTypeFeesDialogCtrl getFinTypeFeesDialogCtrl() {
		return finTypeFeesDialogCtrl;
	}

	public void setFinTypeFeesDialogCtrl(FinTypeFeesDialogCtrl finTypeFeesDialogCtrl) {
		this.finTypeFeesDialogCtrl = finTypeFeesDialogCtrl;
	}

	public FinTypeFeesListCtrl getFinTypeFeesListCtrl() {
		return finTypeFeesListCtrl;
	}

	public void setFinTypeFeesListCtrl(FinTypeFeesListCtrl finTypeFeesListCtrl) {
		this.finTypeFeesListCtrl = finTypeFeesListCtrl;
	}
}
