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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : RuleDialogCtrl.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 03-06-2011
 * 
 * Modified Date : 03-06-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rulefactory.rule;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.JSRuleReturnType;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/RuleFactorry/Rule/ruleDialog.zul file.
 */
public class RuleDialogCtrl extends GFCBaseCtrl<Rule> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(RuleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RuleDialog;

	protected Textbox ruleCode;
	protected Textbox ruleCodeDesc;

	protected ExtendedCombobox ruleEvent;
	protected ExtendedCombobox feeType;

	protected Combobox returnType;
	protected Combobox deviationType;

	protected Checkbox allowDeviation;
	protected Checkbox revolving;
	protected Checkbox active;

	protected Intbox seqOrder;

	protected Space space_DeviationType;

	protected Label label_RuleTitle;
	protected Label label_RuleDialog_ruleCode;
	protected Label label_RuleDialog_ruleCodeDesc;
	protected Label label_seqOrder;
	protected Label label_DeviationType;
	protected Label notesValue;

	protected Hbox hbox_DeviationType;

	protected Row row_FeeType;
	protected Row row_SeqOrder;
	protected Row row_DeviationType;
	protected Row row_Revolving;
	protected Row row_AllowDeviation;
	protected Row row_Notes;

	protected Button btnReadValues;
	protected Button btnSimulation;

	protected Grid grid_basicDetail;
	protected JavaScriptBuilder javaScriptSqlRule;

	// not auto wired vars
	private Rule rule; // overhanded per param
	private transient RuleListCtrl ruleListCtrl; // overhanded per param

	// ServiceDAOs / Domain Classes
	private transient RuleService ruleService;
	private transient PagedListService pagedListService;

	private transient boolean validationOn;
	private String ruleModuleName; // FIXME This field is being maintained only for the Rights and Labels in
									// i3-label.properties

	/**
	 * default constructor.<br>
	 */
	public RuleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RuleDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_RuleDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RuleDialog);

		try {
			if (this.arguments.containsKey("ruleModuleName")) {
				this.ruleModuleName = (String) this.arguments.get("ruleModuleName");
			}

			// READ OVERHANDED params !
			if (this.arguments.containsKey("rule")) {

				this.rule = (Rule) arguments.get("rule");

				Rule befImage = new Rule();
				BeanUtils.copyProperties(this.rule, befImage);

				this.rule.setBefImage(befImage);
				setRule(this.rule);
			} else {
				setRule(null);
			}

			// READ OVERHANDED params !
			// we get the ruleListWindow controller. So we have access to it and can synchronize the shown data when we
			// do insert, edit or delete rule here.

			if (this.arguments.containsKey("ruleListCtrl")) {
				setRuleListCtrl((RuleListCtrl) this.arguments.get("ruleListCtrl"));
			} else {
				setRuleListCtrl(null);
			}

			doLoadWorkFlow(this.rule.isWorkflow(), this.rule.getWorkflowId(), this.rule.getNextTaskId());

			if (isWorkFlowEnabled() && !this.enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "RuleDialog");
			}

			// set components visible dependent of the users rights
			doCheckRights();

			getBorderLayoutHeight();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.rule);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RuleDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());

		doSave();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());

		doEdit();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		MessageUtil.showHelpWindow(event, window_RuleDialog);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDelete(Event event) {
		logger.debug("Entering" + event.toString());

		if (StringUtils.equals(RuleConstants.MODULE_LMTLINE, this.rule.getRuleModule())) {
			if (!active.isDisabled())
				doDelete();
			else {
				MessageUtil.showError(Labels.getLabel("LIMIT_FIELD_DELETE", new String[] { this.rule.getRuleCode() }));
			}
		} else {
			doDelete();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());

		doCancel();

		logger.debug("Leaving" + event.toString());
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
	 * build a rule for values and generate in textBox
	 * 
	 * @param event
	 */
	public void onClick$btnReadValues(Event event) {
		logger.debug("Entering" + event.toString());

		validate();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Simulation of builded code
	 * 
	 * @param event
	 */
	public void onClick$btnSimulation(Event event) {
		logger.debug("Entering" + event.toString());

		doSetValidation();
		doWriteComponentsToBean(this.rule);
		this.javaScriptSqlRule.simulateQuery();
		this.javaScriptSqlRule.setSelectedTab(RuleConstants.TAB_SCRIPT);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the Rule Return Type
	 * 
	 * @param event
	 */
	public void onChange$returnType(Event event) {
		logger.debug("Entering" + event.toString());

		doSetRuleBuilder();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the Deviation Return Type
	 * 
	 * @param event
	 */
	public void onChange$deviationType(Event event) {
		logger.debug("Entering" + event.toString());

		doSetRuleBuilder();

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$ruleEvent(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ruleEvent.getObject();
		if (dataObject instanceof String) {
			this.ruleEvent.setValue(dataObject.toString());
			this.ruleEvent.setDescription("");
		} else {
			AccountEngineEvent details = (AccountEngineEvent) dataObject;

			if (details != null) {
				this.ruleEvent.setValue(details.getAEEventCode());
				this.ruleEvent.setDescription(details.getAEEventCodeDesc());
				doSetRuleBuilder();
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.feeType.setMaxlength(8);
		this.feeType.setMandatoryStyle(true);
		this.feeType.setModuleName("FeeType");
		this.feeType.setValueColumn("FeeTypeCode");
		this.feeType.setDescColumn("FeeTypeDesc");
		this.feeType.setValidateColumns(new String[] { "FeeTypeCode", "FeeTypeDesc" });
		this.feeType.getTextbox().setDisabled(true);
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.feeType.setFilters(filters);

		this.ruleEvent.setMaxlength(8);
		this.ruleEvent.setMandatoryStyle(true);
		this.ruleEvent.setModuleName("AccountEngineEvent");
		this.ruleEvent.setValueColumn("AEEventCode");
		this.ruleEvent.setDescColumn("AEEventCodeDesc");
		this.ruleEvent.setValidateColumns(new String[] { "AEEventCode" });
		this.ruleEvent.setList(AccountingEngine.getEvents());
		this.ruleEvent.getTextbox().setDisabled(true);

		String module = this.rule.getRuleModule();
		String event = this.rule.getRuleEvent();

		this.ruleCode.setMaxlength(8);
		this.ruleCodeDesc.setMaxlength(50);
		this.seqOrder.setMaxlength(4);

		String notes = Labels.getLabel("label_RuleDialog_" + this.ruleModuleName + "_NotesValue.value");

		if (StringUtils.isBlank(notes)) {
			this.row_Notes.setVisible(false);
		} else {
			this.notesValue.setValue(notes);
		}

		switch (module) {

		case RuleConstants.MODULE_AGRRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_BOOLEAN);
			break;
		case RuleConstants.MODULE_VERRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_BOOLEAN);
			break;
		case RuleConstants.MODULE_CLRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_BOOLEAN);
			break;

		case RuleConstants.MODULE_DOWNPAYRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_ELGRULE:
			this.row_DeviationType.setVisible(true);
			this.label_DeviationType.setVisible(true);
			this.hbox_DeviationType.setVisible(true);
			break;

		case RuleConstants.MODULE_FEES:
			this.row_FeeType.setVisible(true);
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_RATERULE:
			this.row_AllowDeviation.setVisible(true);
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_PROVSN:
			this.row_DeviationType.setVisible(true);
			this.label_DeviationType.setValue(Labels.getLabel("label_RuleDialog_provisionType.value"));
			this.rule.setReturnType(RuleConstants.RETURNTYPE_OBJECT);
			this.rule.setDeviationType(DeviationConstants.DT_PERCENTAGE);
			this.deviationType.setReadonly(true);
			this.returnType.setReadonly(true);
			break;

		case RuleConstants.MODULE_REFUND:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_SCORES:
			this.label_seqOrder.setValue(Labels.getLabel("label_RuleDialog_metricSeqOrder.value"));
			// this.row_SeqOrder.setVisible(true);
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_SUBHEAD:
			this.row_DeviationType.setVisible(true);
			this.label_DeviationType.setVisible(false);
			this.hbox_DeviationType.setVisible(false);
			// this.rule.setReturnType(RuleConstants.RETURNTYPE_STRING);
			break;

		case RuleConstants.MODULE_LMTLINE:
			this.label_RuleDialog_ruleCode.setValue(Labels.getLabel("label_LimitLineDialog_Code.value"));
			this.label_RuleDialog_ruleCodeDesc.setValue(Labels.getLabel("label_LimitLineDialog_Desc.value"));
			this.rule.setReturnType(RuleConstants.RETURNTYPE_BOOLEAN);

			if (StringUtils.equals(LimitConstants.LIMIT_CATEGORY_CUST, event)) {
				this.row_Revolving.setVisible(true);
			}
			break;

		case RuleConstants.MODULE_BOUNCE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);

		case RuleConstants.MODULE_LPPRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_GSTRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;

		case RuleConstants.MODULE_AMORTIZATIONMETHOD:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_STRING);
			break;

		case RuleConstants.MODULE_STGACRULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_INTEGER);
			break;
		case RuleConstants.MODULE_FEEPERC:
			this.row_FeeType.setVisible(true);
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;
		case RuleConstants.MODULE_BRERULE:
			this.row_DeviationType.setVisible(true);
			this.label_DeviationType.setVisible(false);
			this.hbox_DeviationType.setVisible(false);
			// this.rule.setReturnType(RuleConstants.RETURNTYPE_STRING);
			break;
		case RuleConstants.MODULE_DUEDATERULE:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_INTEGER);
			break;
		case RuleConstants.MODULE_AUTOREFUND:
			this.rule.setReturnType(RuleConstants.RETURNTYPE_DECIMAL);
			break;
		}

		// Window Title
		if (StringUtils.equals(RuleConstants.EVENT_BANK, event)) {
			this.label_RuleTitle
					.setValue("Institutional " + Labels.getLabel("window_RuleDialog_" + ruleModuleName + ".title"));
		} else if (StringUtils.equals(RuleConstants.EVENT_CUSTOMER, event)) {
			this.label_RuleTitle
					.setValue("Customer " + Labels.getLabel("window_RuleDialog_" + ruleModuleName + ".title"));
		} else if (RuleConstants.EVENT_AUTOTREFUND.equals(event)) {
			this.label_RuleTitle.setValue("Auto Refund Reserve " + Labels.getLabel("window_RuleDialog.title"));
		} else {
			this.label_RuleTitle.setValue(Labels.getLabel("window_RuleDialog_" + ruleModuleName + ".title"));
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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

		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities("RuleDialog", getRole());

			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnEdit"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnSave"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btnDelete"));
		} else {
			btnNotes.setVisible(false);
		}

		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original isItemSibling.<br>
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.rule.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aRule
	 */
	public void doShowDialog(Rule aRule) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRule.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ruleCode.focus();
		} else {
			this.ruleCodeDesc.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aRule.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aRule);

			if (RuleConstants.MODULE_GSTRULE.equals(this.rule.getRuleModule())) {
				this.btnDelete.setVisible(false);
			}

			// FIXME Temporary Not Visible
			this.btnSimulation.setVisible(false);
			this.btnReadValues.setVisible(false);
			if (RuleConstants.MODULE_AMORTIZATIONMETHOD.equals(this.rule.getRuleModule())) {
				this.btnDelete.setVisible(false);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RuleDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRule (Rule)
	 */
	public void doWriteBeanToComponents(Rule aRule) {
		logger.debug("Entering");

		String module = aRule.getRuleModule();
		String event = aRule.getRuleEvent();
		String excludeFields = "";

		if (StringUtils.equals(module, RuleConstants.MODULE_ELGRULE)) {
			excludeFields = "," + RuleConstants.RETURNTYPE_STRING + "," + RuleConstants.RETURNTYPE_CALCSTRING + ",";
		} else if (StringUtils.equals(module, RuleConstants.MODULE_SUBHEAD)
				|| StringUtils.equals(module, RuleConstants.MODULE_BRERULE)) {
			excludeFields = "," + RuleConstants.RETURNTYPE_DECIMAL + "," + RuleConstants.RETURNTYPE_BOOLEAN + ","
					+ RuleConstants.RETURNTYPE_INTEGER + "," + RuleConstants.RETURNTYPE_OBJECT + ",";
		}

		fillComboBox(this.returnType, aRule.getReturnType(), PennantStaticListUtil.getRuleReturnType(), excludeFields);
		fillComboBox(this.deviationType, aRule.getDeviationType(), PennantStaticListUtil.getDeviationDataTypes(), "");

		this.ruleCode.setValue(aRule.getRuleCode());
		this.ruleCodeDesc.setValue(aRule.getRuleCodeDesc());

		if (StringUtils.equalsIgnoreCase(module, RuleConstants.MODULE_FEES)) {
			this.ruleEvent.setValue(event);
			this.feeType.setValue(aRule.getFeeTypeCode());
			this.feeType.setDescription(aRule.getFeeTypeDesc());

			if (aRule.getFeeTypeID() != null) {
				this.feeType.setObject(new FeeType(aRule.getFeeTypeID()));
			}
		} else if (StringUtils.equalsIgnoreCase(module, RuleConstants.MODULE_FEEPERC)) {
			this.ruleEvent.setValue(event);
			this.feeType.setValue(aRule.getFeeTypeCode());
			this.feeType.setDescription(aRule.getFeeTypeDesc());

			if (aRule.getFeeTypeID() != null) {
				this.feeType.setObject(new FeeType(aRule.getFeeTypeID()));
			}
		}

		this.seqOrder.setValue(aRule.getSeqOrder());
		this.allowDeviation.setChecked(aRule.isAllowDeviation());
		this.active.setChecked(aRule.isActive());
		this.revolving.setChecked(aRule.isRevolving());
		this.recordStatus.setValue(aRule.getRecordStatus());

		if (!(aRule.isNewRecord() && !StringUtils.equalsIgnoreCase(RuleConstants.MODULE_LMTLINE, module))) {
			this.btnSimulation.setVisible(false);
			if (getUserWorkspace().isAllowed("button_RuleDialog_btn" + this.ruleModuleName + "Maintain")
					&& getUserWorkspace().isAllowed("button_RuleDialog_btn" + this.ruleModuleName + "Save")
					&& !PennantConstants.RECORD_TYPE_DEL.equals(this.rule.getRecordType())) {
				this.btnSimulation.setVisible(true);
			}
		}

		doSetRuleBuilder(); // Rule Building

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRule
	 */
	public void doWriteComponentsToBean(Rule aRule) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.ruleCode.isReadonly()) {
				JdbcSearchObject<BMTRBFldDetails> searchObj = new JdbcSearchObject<BMTRBFldDetails>(
						BMTRBFldDetails.class, getListRows());
				searchObj.addTabelName("RBFieldDetails");
				searchObj.addFilter(new Filter("RBFldName", this.ruleCode.getValue(), Filter.OP_EQUAL));

				if (this.pagedListService.getSRBySearchObject(searchObj).getTotalCount() > 0) {
					throw new WrongValueException(this.ruleCode, Labels.getLabel("label_RuleCodeExcept"));
				}

				if (StringUtils.equals(RuleConstants.MODULE_LMTLINE, aRule.getRuleModule())) {
					Map<String, Rule> ruleCodesMap = PennantAppUtil.getLimitLineCodes(RuleConstants.MODULE_LMTLINE,
							false, "_View");
					if (ruleCodesMap.containsKey(ruleCode.getValue())) {
						wve.add(new WrongValueException(ruleCode,
								Labels.getLabel("LIMILINE_DATA_ALREADY_EXISTS", new String[] { ruleCode.getValue() })));
					}
				}
			}
			aRule.setRuleCode(this.ruleCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aRule.setRuleCodeDesc(this.ruleCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aRule.setSeqOrder(this.seqOrder.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		String returnType_Value = "";
		try {
			if (this.returnType.getSelectedIndex() == 0) {
				throw new WrongValueException(returnType, Labels.getLabel("Label_RuleDialog_select_list"));
			}
			returnType_Value = this.returnType.getSelectedItem().getValue();
			aRule.setReturnType(returnType_Value);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Deviation Return type
		if (this.hbox_DeviationType.isVisible()) {
			try {
				if (StringUtils.equals(returnType_Value, RuleConstants.RETURNTYPE_OBJECT)
						&& this.deviationType.getSelectedIndex() == 0) {
					throw new WrongValueException(this.deviationType, Labels.getLabel("Label_RuleDialog_select_list"));
				}
				aRule.setDeviationType(this.deviationType.getSelectedItem().getValue().toString());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.row_FeeType.isVisible()) {
			try {
				aRule.setRuleEvent(this.ruleEvent.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				FeeType feeTypeObj = (FeeType) feeType.getObject();
				aRule.setFeeTypeID(feeTypeObj.getFeeTypeID());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.row_AllowDeviation.isVisible()) {
			try {
				aRule.setAllowDeviation(this.allowDeviation.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aRule.setAllowDeviation(false);
		}

		if (this.row_Revolving.isVisible()) {
			try {
				aRule.setRevolving(this.revolving.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// Active
		try {
			aRule.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// SQLRule, ActualBlock, Fields
		try {
			if (wve.isEmpty() && !this.javaScriptSqlRule.isReadOnly()) {
				String recordStatus = userAction.getSelectedItem().getValue();

				if (!StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_CANCELLED)
						&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_REJECTED)) {
					validate();

					aRule.setSQLRule(this.javaScriptSqlRule.getActualQuery());
					aRule.setActualBlock(this.javaScriptSqlRule.getActualBlock());
					aRule.setFields(this.javaScriptSqlRule.getFields());
					aRule.setSPLRule(this.javaScriptSqlRule.getSplQuery());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		// Record Status
		aRule.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.ruleCode.setErrorMessage("");
		this.ruleCodeDesc.setErrorMessage("");
		this.returnType.setErrorMessage("");
		this.deviationType.setErrorMessage("");
		this.feeType.setErrorMessage("");
		this.seqOrder.setErrorMessage("");
		this.ruleEvent.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		String ruleLabel = "label_RuleDialog_ruleCode.value";
		String ruleDescLabel = "label_RuleDialog_ruleCodeDesc.value";
		setValidationOn(true);

		if (StringUtils.equals(RuleConstants.MODULE_LMTLINE, this.rule.getRuleModule())) {
			ruleLabel = "label_LimitLineDialog_Code.value";
			ruleDescLabel = "label_LimitLineDialog_Desc.value";
		}

		if (!this.ruleCode.isReadonly()) {
			this.ruleCode.setConstraint(new PTStringValidator(Labels.getLabel(ruleLabel),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.ruleCodeDesc.isReadonly()) {
			this.ruleCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel(ruleDescLabel),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (this.row_FeeType.isVisible()) {
			this.feeType.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleDialog_FeeType.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
			this.ruleEvent.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleDialog_ruleEvent.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.ruleCode.setConstraint("");
		this.ruleCodeDesc.setConstraint("");
		this.returnType.setConstraint("");
		this.deviationType.setConstraint("");
		this.feeType.setConstraint("");
		this.seqOrder.setConstraint("");
		this.ruleEvent.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Validate the SQL Query and then gives SQL Query and Actual Block.
	 */
	private void validate() {
		logger.debug("Entering");

		this.javaScriptSqlRule.getSqlQuery();
		this.javaScriptSqlRule.setSelectedTab(RuleConstants.TAB_SCRIPT);

		logger.debug("Leaving");
	}

	/**
	 * set the Java Script Builder Component
	 */
	private void doSetRuleBuilder() {
		String ruleModule = this.rule.getRuleModule();

		// Rule Building
		this.javaScriptSqlRule.setModule(ruleModule);
		this.javaScriptSqlRule.setMode(RuleConstants.RULEMODE_SELECTFIELDLIST);
		this.javaScriptSqlRule.setNoOfRowsVisible(this.grid_basicDetail.getRows().getVisibleItemCount());

		String returnType = this.returnType.getSelectedItem().getValue().toString();
		List<JSRuleReturnType> jsRuleReturnTypeList = new ArrayList<JSRuleReturnType>();

		this.space_DeviationType.setSclass("");
		this.deviationType.setErrorMessage("");

		RuleReturnType ruleReturnType = null;
		if (!PennantConstants.List_Select.equals(returnType)) {
			if (RuleReturnType.BOOLEAN.value().equals(returnType)) {
				ruleReturnType = RuleReturnType.BOOLEAN;
			} else if (RuleReturnType.DECIMAL.value().equals(returnType)) {
				ruleReturnType = RuleReturnType.DECIMAL;
			} else if (RuleReturnType.STRING.value().equals(returnType)) {
				ruleReturnType = RuleReturnType.STRING;
			} else if (RuleReturnType.CALCSTRING.value().equals(returnType)) {
				ruleReturnType = RuleReturnType.CALCSTRING;
			} else if (RuleReturnType.INTEGER.value().equals(returnType)) {
				ruleReturnType = RuleReturnType.INTEGER;
			} else if (RuleReturnType.OBJECT.value().equals(returnType)) {
				ruleReturnType = RuleReturnType.OBJECT;
			}
		}

		if (RuleReturnType.OBJECT.value().equals(returnType)) {
			if (this.hbox_DeviationType.isVisible()) {
				if (PennantConstants.List_Select.equals(this.deviationType.getSelectedItem().getValue())) {
					throw new WrongValueException(deviationType, Labels.getLabel("Label_RuleDialog_select_list"));
				}

				this.space_DeviationType.setSclass(PennantConstants.mandateSclass);
			}

			List<ValueLabel> valueLabelList = new ArrayList<ValueLabel>();
			JSRuleReturnType jsRuleReturnType = null;

			if (RuleConstants.MODULE_ELGRULE.equals(ruleModule)) {
				jsRuleReturnType = new JSRuleReturnType();
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_COMBOBOX);
				valueLabelList.add(new ValueLabel("1", "TRUE"));
				valueLabelList.add(new ValueLabel("0", "FALSE"));
				jsRuleReturnType.setListOfData(valueLabelList);

				jsRuleReturnType.setResultLabel("result.value = ");

				jsRuleReturnTypeList.add(jsRuleReturnType);

				String deviationType_Label = this.deviationType.getSelectedItem().getLabel();
				jsRuleReturnType = new JSRuleReturnType();

				if (StringUtils.equals(deviationType_Label, Labels.getLabel("label_Boolean"))) {
					jsRuleReturnType.setListOfData(valueLabelList);
					deviationType_Label = RuleConstants.COMPONENTTYPE_COMBOBOX;
				}

				jsRuleReturnType.setResultLabel(" result.deviation = ");
				jsRuleReturnType.setComponentType(deviationType_Label);

				jsRuleReturnTypeList.add(jsRuleReturnType);
			}

			if (RuleConstants.MODULE_PROVSN.equals(ruleModule)) {
				jsRuleReturnType = new JSRuleReturnType();
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_PERCENTAGE);
				jsRuleReturnType.setResultLabel(" result.provPercentage = ");
				jsRuleReturnTypeList.add(jsRuleReturnType);

				jsRuleReturnType = new JSRuleReturnType();
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_DECIMAL);
				jsRuleReturnType.setResultLabel(" result.provAmount = ");
				jsRuleReturnTypeList.add(jsRuleReturnType);

				jsRuleReturnType = new JSRuleReturnType();
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_PERCENTAGE);
				jsRuleReturnType.setResultLabel(" result.vasProvPercentage = ");
				jsRuleReturnTypeList.add(jsRuleReturnType);

				jsRuleReturnType = new JSRuleReturnType();
				jsRuleReturnType.setComponentType(RuleConstants.COMPONENTTYPE_DECIMAL);
				jsRuleReturnType.setResultLabel(" result.vasProvAmount = ");
				jsRuleReturnTypeList.add(jsRuleReturnType);
			}
		}

		this.javaScriptSqlRule.setJsRuleReturnTypeList(jsRuleReturnTypeList);
		this.javaScriptSqlRule.setRuleType(ruleReturnType);

		if (this.rule.isNewRecord()) {

			if (StringUtils.equals(returnType, PennantConstants.List_Select)) {
				this.javaScriptSqlRule.setTreeTabVisible(false);
			} else {

				this.javaScriptSqlRule.setTreeTabVisible(true);

				if (StringUtils.equalsIgnoreCase(this.rule.getRuleModule(), RuleConstants.MODULE_FEES)) {
					this.javaScriptSqlRule.setEvent(this.ruleEvent.getValue());
				} else if (StringUtils.equalsIgnoreCase(this.rule.getRuleModule(), RuleConstants.MODULE_FEEPERC)) {
					this.javaScriptSqlRule.setEvent(this.ruleEvent.getValue());
				} else {
					this.javaScriptSqlRule.setEvent(this.rule.getRuleEvent());
				}

				this.javaScriptSqlRule.setSelectedTab(RuleConstants.TAB_DESIGN);
				this.javaScriptSqlRule.setActualBlock("");
				this.javaScriptSqlRule.setEditable(true);
			}

		} else {

			this.javaScriptSqlRule.setEvent(this.rule.getRuleEvent());
			this.javaScriptSqlRule.setFields(this.rule.getFields());
			this.javaScriptSqlRule.setSqlQuery(this.rule.getSQLRule());
			this.javaScriptSqlRule.setSplQuery(this.rule.getSPLRule());
			this.javaScriptSqlRule.setActualBlock(this.rule.getActualBlock());
			this.javaScriptSqlRule.buildQuery(this.rule.getActualBlock());

		}
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final Rule aRule = new Rule();
		BeanUtils.copyProperties(this.rule, aRule);

		String keyReference = Labels.getLabel("label_RuleDialog_ruleCode.value") + " : " + aRule.getRuleCode();

		doDelete(keyReference, aRule);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.rule.isNewRecord()) {
			this.ruleCode.setReadonly(false);
			this.returnType.setDisabled(false);
			this.deviationType.setDisabled(false);
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.ruleEvent);
			readOnlyComponent(false, this.feeType);
			this.javaScriptSqlRule.setTreeTabVisible(true);
			this.btnReadValues.setVisible(true);
			this.active.setDisabled(true);
			// readOnlyComponent(isReadOnly("RuleDialog_deviationType"), this.deviationType);
		} else {
			this.ruleCode.setReadonly(true);
			this.returnType.setDisabled(true);
			this.deviationType.setDisabled(true);
			this.btnCancel.setVisible(true);

			if (enqiryModule) {
				this.btnReadValues.setVisible(false);
				this.javaScriptSqlRule.setTreeTabVisible(false);
				this.btnDelete.setVisible(false);
				this.btnSave.setVisible(false);
			} else {
				this.btnReadValues.setVisible(getUserWorkspace().isAllowed("RuleDialog_sqlRule"));
				this.javaScriptSqlRule.setTreeTabVisible(getUserWorkspace().isAllowed("RuleDialog_sqlRule"));
			}

			if (PennantConstants.RECORD_TYPE_DEL.equals(this.rule.getRecordType())) {
				this.javaScriptSqlRule.setTreeTabVisible(false);
				this.btnReadValues.setVisible(false);
			}

			this.active.setDisabled(enqiryModule || isReadOnly("RuleDialog_active"));

			if (StringUtils.equals(RuleConstants.MODULE_LMTLINE, this.rule.getRuleModule())) {
				this.active.setDisabled(isReadOnly("RuleDialog_active")
						|| !this.ruleService.validationCheck(this.rule.getRuleCode(), this.rule.getRuleEvent()));
			}
		}

		readOnlyComponent(enqiryModule || isReadOnly("RuleDialog_ruleCodeDesc"), this.ruleCodeDesc);
		readOnlyComponent(enqiryModule || isReadOnly("RuleDialog_revolving"), this.revolving);

		if (RuleConstants.MODULE_PROVSN.equals(this.rule.getRuleModule())) {
			this.returnType.setDisabled(true);
			this.hbox_DeviationType.setVisible(false);
			this.label_DeviationType.setVisible(false);
		}

		this.seqOrder.setReadonly(isReadOnly("RuleDialog_seqOrder"));
		this.allowDeviation.setDisabled(isReadOnly("RuleDialog_allowDeviation"));

		if (this.javaScriptSqlRule.isTreeTabVisible()) {
			this.javaScriptSqlRule.setSelectedTab(RuleConstants.TAB_DESIGN);
		} else {
			this.javaScriptSqlRule.setSelectedTab(RuleConstants.TAB_SCRIPT);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.rule.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				if (enqiryModule) {
					btnSave.setVisible(false);
					btnNotes.setVisible(false);
				}
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.ruleCode.setReadonly(true);
		this.ruleCodeDesc.setReadonly(true);
		this.ruleEvent.setReadonly(true);
		this.feeType.setReadonly(true);
		this.active.setDisabled(true);

		this.javaScriptSqlRule.setTreeTabVisible(false);
		this.javaScriptSqlRule.setSelectedTab(RuleConstants.TAB_DESIGN);

		this.allowDeviation.setDisabled(true);
		this.revolving.setDisabled(true);

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

		// remove validation, if there are a save before
		doRemoveValidation();

		this.ruleCode.setValue("");
		this.ruleCodeDesc.setValue("");
		this.returnType.setSelectedIndex(0);
		this.deviationType.setSelectedIndex(0);
		this.feeType.setValue("");
		this.seqOrder.setText("");
		this.ruleEvent.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		final Rule aRule = new Rule();
		BeanUtils.copyProperties(this.rule, aRule);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Rule object with the components data
		doWriteComponentsToBean(aRule);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aRule.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aRule.getRecordType())) {
				aRule.setVersion(aRule.getVersion() + 1);
				if (isNew) {
					aRule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRule.setNewRecord(true);
				}
			}
		} else {
			aRule.setVersion(aRule.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aRule, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This Method used for setting all workFlow details from userWorkSpace and setting audit details to auditHeader
	 * 
	 * @param aRule    (Rule)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 */
	protected boolean doProcess(Rule aRule, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRule.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aRule.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRule.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aRule.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRule.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");

				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aRule);
				}

				if (isNotesMandatory(taskId, aRule)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aRule.setTaskId(taskId);
			aRule.setNextTaskId(nextTaskId);
			aRule.setRoleCode(getRole());
			aRule.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRule, tranType);
			String operationRefs = getServiceOperations(taskId, aRule);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRule, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aRule, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");

		return processCompleted;
	}

	/**
	 * This Method used for calling the all Database operations from the service by passing the auditHeader and
	 * operationRefs(Method) as String
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		Rule aRule = (Rule) auditHeader.getAuditDetail().getModelData();
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = this.ruleService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = this.ruleService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = this.ruleService.doApprove(auditHeader);

					if (aRule.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = this.ruleService.doReject(auditHeader);

					if (aRule.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_RuleDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_RuleDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.rule), true);
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

		logger.debug("Leaving");

		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aRule    (Rule)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Rule aRule, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aRule.getBefImage(), aRule);
		return new AuditHeader(String.valueOf(aRule.getId()), null, null, null, auditDetail, aRule.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());

		doShowNotes(this.rule);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		this.ruleListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.rule.getId());
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

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setRuleListCtrl(RuleListCtrl ruleListCtrl) {
		this.ruleListCtrl = ruleListCtrl;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}
