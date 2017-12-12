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
 * FileName    		:  ReportFilterFieldsDialogCtrl.java                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.reports.reportconfiguration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.feature.ModuleUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ReportFilterFields/reportFilterFieldsDialog.zul
 * file.
 */
public class ReportFilterFieldsDialogCtrl extends GFCBaseCtrl<ReportFilterFields> {
	private static final long serialVersionUID = -2843265056714842214L;
	private static final Logger logger = Logger.getLogger(ReportFilterFieldsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReportFilterFieldsDialog; // autoWired

	protected Textbox fieldName; // autoWired
	protected Combobox fieldType; // autoWired
	protected Textbox fieldLabel; // autoWired
	protected Textbox fieldDBName; // autoWired
	protected Combobox appUtilMethodName; // autoWired
	protected Combobox moduleName; // autoWired
	protected Combobox lovHiddenFieldMethod; // autoWired
	protected Combobox lovTextFieldMethod; // autoWired
	protected Checkbox multiSelectSearch; // autoWired
	protected Intbox fieldLength; // autoWired
	protected Intbox fieldMaxValue; // autoWired
	protected Intbox fieldMinValue; // autoWired
	protected Intbox seqOrder; // autoWired
	protected Checkbox mandatory; // autoWired
	protected Textbox fieldConstraint; // autoWired
	protected Textbox fieldErrorMessage; // autoWired
	protected Textbox whereCondition; // autoWired
	protected Textbox staticValue; // autoWired
	protected Intbox fieldWidth; // autoWired
	protected Checkbox filterRequired; // autoWired
	protected Combobox defaultFilter; // autoWired

	protected Label label_ReportFilterFieldsDialog_FieldName;
	protected Label label_ReportFilterFieldsDialog_FieldType;
	protected Label label_ReportFilterFieldsDialog_FieldLabel;
	protected Label label_ReportFilterFieldsDialog_FieldDBName;
	protected Label label_ReportFilterFieldsDialog_AppUtilMethodName;
	protected Label label_ReportFilterFieldsDialog_ModuleName;
	protected Label label_ReportFilterFieldsDialog_LovHiddenFieldMethod;
	protected Label label_ReportFilterFieldsDialog_LovTextFieldMethod;
	protected Label label_ReportFilterFieldsDialog_MultiSelectSearch;
	protected Label label_ReportFilterFieldsDialog_FieldLength;
	protected Label label_ReportFilterFieldsDialog_FieldMaxValue;
	protected Label label_ReportFilterFieldsDialog_FieldMinValue;
	protected Label label_ReportFilterFieldsDialog_SeqOrder;
	protected Label label_ReportFilterFieldsDialog_Mandatory;
	protected Label label_ReportFilterFieldsDialog_FieldConstraint;
	protected Label label_ReportFilterFieldsDialog_FieldErrorMessage;
	protected Label label_ReportFilterFieldsDialog_WhereCondition;
	protected Label label_ReportFilterFieldsDialog_StaticValue;
	protected Label label_ReportFilterFieldsDialog_FieldWidth;
	protected Label label_ReportFilterFieldsDialog_FilterRequired;
	protected Label label_ReportFilterFieldsDialog_DefaultFilter;
	protected Html instructions;

	protected Row row_Zero;
	protected Row row_One;
	protected Row row_Two; // AppUtilMethodName and ModuleName
	protected Row row_Three;// Get Value method and Get Label Methods
	protected Row row_Four;
	protected Row row_Five;// FieldMaxValue and FieldMinValue
	protected Row row_Six;
	protected Row row_Seven;// FieldConstraint and FieldError Message Row
	protected Row row_Eight;// WhereCondition
	protected Row row_Nine;
	protected Row row_Ten;
	protected Row row_Eleven;

	protected Hlayout hlayout_FieldName;
	protected Hlayout hlayout_FieldType;
	protected Hlayout hlayout_FieldLabel;
	protected Hlayout hlayout_FieldDBName;
	protected Hlayout hlayout_AppUtilMethodName;
	protected Hlayout hlayout_ModuleName;
	protected Hlayout hlayout_LovHiddenFieldMethod;
	protected Hlayout hlayout_LovTextFieldMethod;
	protected Hlayout hlayout_MultiSelectSearch;
	protected Hlayout hlayout_FieldLength;
	protected Hlayout hlayout_FieldMaxValue;
	protected Hlayout hlayout_FieldMinValue;
	protected Hlayout hlayout_SeqOrder;
	protected Hlayout hlayout_Mandatory;
	protected Hlayout hlayout_FieldConstraint;
	protected Hlayout hlayout_FieldErrorMessage;
	protected Hlayout hlayout_WhereCondition;
	protected Hlayout hlayout_StaticValue;
	protected Hlayout hlayout_FieldWidth;
	protected Hlayout hlayout_FilterRequired;
	protected Hlayout hlayout_DefaultFilter;

	protected Space space_FieldName; // autoWired
	protected Space space_FieldType; // autoWired
	protected Space space_FieldLabel; // autoWired
	protected Space space_FieldDBName; // autoWired
	protected Space space_AppUtilMethodName; // autoWired
	protected Space space_ModuleName; // autoWired
	protected Space space_LovHiddenFieldMethod; // autoWired
	protected Space space_LovTextFieldMethod; // autoWired
	protected Space space_MultiSelectSearch; // autoWired
	protected Space space_FieldLength; // autoWired
	protected Space space_FieldMaxValue; // autoWired
	protected Space space_FieldMinValue; // autoWired
	protected Space space_SeqOrder; // autoWired
	protected Space space_Mandatory; // autoWired
	protected Space space_FieldConstraint; // autoWired
	protected Space space_FieldErrorMessage; // autoWired
	protected Space space_WhereCondition; // autoWired
	protected Space space_StaticValue; // autoWired
	protected Space space_FieldWidth; // autoWired
	protected Space space_FilterRequired; // autoWired
	protected Space space_DefaultFilter; // autoWired
	protected Groupbox gb_statusDetails; // autoWired
	protected Listbox listBox;
	protected Textbox textBox;
	// not auto wired Var's
	private ReportFilterFields reportFilterFields; // overHanded per parameter
	private ReportConfigurationDialogCtrl reportConfigurationDialogCtrl;

	private transient boolean validationOn;

	private boolean enqModule = false;

	// ServiceDAOs / Domain Classes
	// private transient ReportFilterFieldsService reportFilterFieldsService;
	private transient PagedListService pagedListService;
	private List<ValueLabel> fieldTypeList = PennantStaticListUtil.getReportFieldTypes();
	private List<ValueLabel> modulesList = PennantAppUtil.getModuleNamesList();
	private List<ValueLabel> defaultFilterList = PennantStaticListUtil.getDefaultFilters();
	private List<ReportFilterFields> reportFilterFieldsList;
	private Map<String, Object> fieldTypes = new HashMap<String, Object>();
	private Map<String, Object> fieldDateTypesMap = new HashMap<String, Object>();
	private Map<String, String> filterMap = new HashMap<>();

	public enum FIELDTYPE {
		TXT, DATE, TIME, DATETIME, STATICLIST, DYNAMICLIST, LOVSEARCH, DECIMAL, INTRANGE, DECIMALRANGE, NUMBER, CHECKBOX, MULTISELANDLIST, MULTISELINLIST, DATERANGE, DATETIMERANGE, TIMERANGE, STATICVALUE
	};

	/**
	 * default constructor.<br>
	 */
	public ReportFilterFieldsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReportFilterFieldDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected ReportFilterFields object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReportFilterFieldsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReportFilterFieldsDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("reportFilterFields")) {
				this.reportFilterFields = (ReportFilterFields) arguments.get("reportFilterFields");
				ReportFilterFields befImage = new ReportFilterFields();
				BeanUtils.copyProperties(this.reportFilterFields, befImage);
				this.reportFilterFields.setBefImage(befImage);

				setReportFilterFields(this.reportFilterFields);
			} else {
				setReportFilterFields(null);
			}
			differentiateFileds();

			if (isWorkFlowEnabled()) {
				if (!enqModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ReportFilterFieldDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			fillComboBox(fieldType, "", fieldTypeList, "");
			fillComboBox(moduleName, "", modulesList, "");
			setListAppUtilMethodName();
			fillComboBox(defaultFilter, "", defaultFilterList, "");

			// this.defaultFilter.getChildren().get(0).detach();
			this.defaultFilter.setValue("=");
			if (arguments.containsKey("reportConfigurationDialogCtrl")) {
				this.reportConfigurationDialogCtrl = (ReportConfigurationDialogCtrl) arguments
						.get("reportConfigurationDialogCtrl");
				setReportConfigurationDialogCtrl(this.reportConfigurationDialogCtrl);
			} else {
				setReportConfigurationDialogCtrl(null);
			}
			this.btnSave.setVisible(true);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getReportFilterFields());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReportFilterFieldsDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 */
	private void differentiateFileds() {
		fieldTypes.put("TXT", true);
		fieldTypes.put("DATE", true);
		fieldTypes.put("TIME", true);
		fieldTypes.put("DATETIME", true);
		fieldTypes.put("NUMBER", true);
		fieldTypes.put("DECIMAL", true);
		// FiledWidth Contain Types
		fieldDateTypesMap.put("TXT", true);
		fieldDateTypesMap.put("STATICLIST", true);
		fieldDateTypesMap.put("DYNAMICLIST", true);
		fieldDateTypesMap.put("LOVSEARCH", true);
		fieldDateTypesMap.put("DECIMAL", true);
		fieldDateTypesMap.put("NUMBER", true);
		fieldDateTypesMap.put("DECIMAL", true);
		fieldDateTypesMap.put("MULTISELINLIST", true);
		fieldDateTypesMap.put("MULTISELANDLIST", true);
		this.seqOrder.setAttribute(getNextRoleCode(), moduleName);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.fieldName.setMaxlength(100);
		this.fieldType.setMaxlength(50);
		this.fieldLabel.setMaxlength(100);
		this.fieldDBName.setMaxlength(100);
		this.appUtilMethodName.setMaxlength(100);
		this.moduleName.setMaxlength(50);
		this.seqOrder.setMaxlength(2);
		this.fieldWidth.setMaxlength(3);
		this.fieldWidth.setValue(150);
		this.lovHiddenFieldMethod.setMaxlength(100);
		this.lovTextFieldMethod.setMaxlength(100);
		this.fieldConstraint.setMaxlength(500);
		this.fieldErrorMessage.setMaxlength(500);
		this.whereCondition.setMaxlength(500);
		this.staticValue.setMaxlength(150);
		this.fieldLength.setMaxlength(3);
		this.fieldLength.setValue(20);

		if (isWorkFlowEnabled() && !enqModule) {
			this.gb_statusDetails.setVisible(true);
		} else {
			if (enqModule) {
				groupboxWf.setVisible(false);
				south.setHeight("60px");
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		if (!enqModule) {
			this.btnNew.setVisible(false);
			/*
			 * this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReportFilterFieldDialog_btnEdit"));
			 * this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReportFilterFieldDialog_btnDelete"));
			 * this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReportFilterFieldDialog_btnSave"));
			 */

			this.btnEdit.setVisible(true);
			this.btnDelete.setVisible(true);
			this.btnSave.setVisible(true);
		}

		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnEdit(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doEdit();
		doWriteBeanToComponents(getReportFilterFields());
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
		MessageUtil.showHelpWindow(event, window_ReportFilterFieldsDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnCancel(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
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

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 * @throws Exception
	 * 
	 */
	private void doCancel() throws Exception {
		logger.debug("Entering ");

		doWriteBeanToComponents(this.reportFilterFields.getBefImage());
		doReadOnly(true);
		doWriteBeanToComponents(getReportFilterFields());
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);
		setDisabledListBox_OnCancel();
		renderFilterFields();

		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReportFilterFields
	 *            reportFilterFields
	 * @throws Exception
	 */
	public void doWriteBeanToComponents(ReportFilterFields aReportFilterFields) throws Exception {
		logger.debug("Entering ");
		this.fieldName.setValue(aReportFilterFields.getFieldName());
		this.fieldLabel.setValue(aReportFilterFields.getFieldLabel());
		this.fieldDBName.setValue(aReportFilterFields.getFieldDBName());
		this.multiSelectSearch.setChecked(aReportFilterFields.isMultiSelectSearch());
		this.fieldLength.setValue(aReportFilterFields.getFieldLength());
		this.fieldMaxValue.setValue(aReportFilterFields.getFieldMaxValue());
		this.fieldMinValue.setValue(aReportFilterFields.getFieldMinValue());
		if (aReportFilterFields.isNewRecord()) {
			this.seqOrder.setText("");
		} else {
			this.seqOrder.setValue(aReportFilterFields.getSeqOrder());
		}
		this.mandatory.setChecked(aReportFilterFields.isMandatory());
		this.fieldConstraint.setValue(aReportFilterFields.getFieldConstraint());
		this.fieldErrorMessage.setValue(aReportFilterFields.getFieldErrorMessage());
		this.whereCondition.setValue(aReportFilterFields.getWhereCondition());
		this.staticValue.setValue(aReportFilterFields.getStaticValue());
		this.fieldWidth.setValue(aReportFilterFields.getFieldWidth());
		this.filterRequired.setChecked(aReportFilterFields.isFilterRequired());
		if (!aReportFilterFields.isNewRecord()) {
			setFieldType(aReportFilterFields.getFieldType());
			this.fieldType.setValue(PennantAppUtil.getlabelDesc(aReportFilterFields.getFieldType(), fieldTypeList));
			this.moduleName.setValue(aReportFilterFields.getModuleName());
			this.defaultFilter.setValue(aReportFilterFields.getDefaultFilter());
			this.appUtilMethodName.setValue(aReportFilterFields.getAppUtilMethodName());
			doFillInstructions(aReportFilterFields.getFieldType());
			if (this.moduleName.getSelectedItem() != null && !("").equals(this.moduleName.getSelectedItem().getValue())
					&& !this.moduleName.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
				getAllMethods(this.moduleName.getSelectedItem().getValue().toString(), this.lovHiddenFieldMethod);
				getAllMethods(this.moduleName.getSelectedItem().getValue().toString(), this.lovTextFieldMethod);
			}
			this.lovHiddenFieldMethod.setValue(aReportFilterFields.getLovHiddenFieldMethod());
			this.lovTextFieldMethod.setValue(aReportFilterFields.getLovTextFieldMethod());
		}
		this.recordStatus.setValue(aReportFilterFields.getRecordStatus());

		if (StringUtils.trimToNull(aReportFilterFields.getFilterFileds()) != null) {
			String[] filterFields = StringUtils.split(aReportFilterFields.getFilterFileds(), "|");
			for (String filterField : filterFields) {
				String[] fieldStr = StringUtils.split(filterField, "@");
				filterMap.put(fieldStr[0], fieldStr[1]);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReportFilterFields
	 */
	public void doWriteComponentsToBean(ReportFilterFields aReportFilterFields) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String filedType = this.fieldType.getSelectedItem().getValue().toString();
		try {
			aReportFilterFields.setFieldName(this.fieldName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (filedType.equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.fieldType, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReportFilterFieldsDialog_FieldType.value") }));
			}
			aReportFilterFields.setFieldType(filedType);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("STATICVALUE".equals(filedType)) {
				aReportFilterFields.setFieldLabel("");
			} else {
				aReportFilterFields.setFieldLabel(this.fieldLabel.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("STATICVALUE".equals(filedType) || "MULTISELANDLIST".equals(filedType)) {
				aReportFilterFields.setFieldDBName("");
			} else {
				aReportFilterFields.setFieldDBName(this.fieldDBName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("STATICLIST".equals(filedType) || "MULTISELANDLIST".equals(filedType)
					|| "MULTISELINLIST".equals(filedType)) {
				if (this.appUtilMethodName.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.appUtilMethodName,
							Labels.getLabel("FIELD_IS_MAND", new String[] {
									Labels.getLabel("label_ReportFilterFieldsDialog_AppUtilMethodName.value") }));

				}
				aReportFilterFields.setAppUtilMethodName(this.appUtilMethodName.getValue());
			} else {
				aReportFilterFields.setAppUtilMethodName("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aReportFilterFields.setModuleName(this.moduleName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if ("DYNAMICLIST".equals(filedType) || "LOVSEARCH".equals(filedType)) {
			try {
				if (this.lovTextFieldMethod.getSelectedItem() == null
						|| (this.lovTextFieldMethod.getSelectedItem() != null && this.lovTextFieldMethod
								.getSelectedItem().getValue().equals(PennantConstants.List_Select))) {
					throw new WrongValueException(this.lovTextFieldMethod,
							Labels.getLabel("FIELD_IS_MAND", new String[] {
									Labels.getLabel("label_ReportFilterFieldsDialog_LovTextFieldMethod.value") }));

				}
				aReportFilterFields
						.setLovTextFieldMethod(this.lovTextFieldMethod.getSelectedItem().getValue().toString());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.lovHiddenFieldMethod.getSelectedItem() == null
						|| (this.lovHiddenFieldMethod.getSelectedItem() != null && this.lovHiddenFieldMethod
								.getSelectedItem().getValue().equals(PennantConstants.List_Select))) {
					throw new WrongValueException(this.lovHiddenFieldMethod,
							Labels.getLabel("FIELD_IS_MAND", new String[] {
									Labels.getLabel("label_ReportFilterFieldsDialog_LovHiddenFieldMethod.value") }));

				}
				aReportFilterFields
						.setLovHiddenFieldMethod(this.lovHiddenFieldMethod.getSelectedItem().getValue().toString());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aReportFilterFields.setLovTextFieldMethod("");
			aReportFilterFields.setLovHiddenFieldMethod("");
		}
		try {
			aReportFilterFields.setMultiSelectSearch(this.multiSelectSearch.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (("TXT".equals(filedType) || "DECIMAL".equals(filedType) || "NUMBER".equals(filedType))
					&& this.fieldLength.intValue() == 0) {
				throw new WrongValueException(this.fieldLength, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_ReportFilterFieldsDialog_FieldLength.value"), "0" }));

			}
			aReportFilterFields.setFieldLength(this.fieldLength.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (("TXT".equals(filedType) || "DECIMAL".equals(filedType) || "NUMBER".equals(filedType))
					&& this.fieldLength.intValue() == 0) {
				throw new WrongValueException(this.fieldWidth, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_ReportFilterFieldsDialog_FieldWidth.value"), "0" }));

			}
			aReportFilterFields.setFieldWidth(this.fieldWidth.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aReportFilterFields.setFieldMaxValue(this.fieldMaxValue.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.seqOrder.getValue() == null) {
				throw new WrongValueException(this.seqOrder,
						Labels.getLabel("FIELD_IS_MAND", new String[] {
								Labels.getLabel("label_ReportFilterFieldsDialog_SeqOrder.value") }));
			}if (this.seqOrder.getValue() == 0) {
					throw new WrongValueException(this.seqOrder, Labels.getLabel("FIELD_NO_NEGATIVE",
							new String[] { Labels.getLabel("label_ReportFilterFieldsDialog_SeqOrder.value") }));
				}
			
			aReportFilterFields.setSeqOrder(this.seqOrder.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportFilterFields.setFieldMinValue(this.fieldMinValue.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportFilterFields.setMandatory(this.mandatory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if ("TXT".equals(filedType)) {
			try {
				if (("").equals(this.fieldConstraint.getValue().trim())) {
					throw new WrongValueException(this.fieldConstraint, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_ReportFilterFieldsDialog_FieldConstraint.value") }));

				}
				aReportFilterFields.setFieldConstraint(this.fieldConstraint.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (("").equals(this.fieldErrorMessage.getValue().trim())) {
					throw new WrongValueException(this.fieldErrorMessage, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_ReportFilterFieldsDialog_FieldConstraint.value") }));

				}
				aReportFilterFields.setFieldErrorMessage(this.fieldErrorMessage.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			aReportFilterFields.setWhereCondition(this.whereCondition.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportFilterFields.setStaticValue(this.staticValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportFilterFields.setFilterRequired(this.filterRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aReportFilterFields.setDefaultFilter(this.defaultFilter.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aReportFilterFields.setFilterFileds(getFilterString());

		try {
			aReportFilterFields.setDefaultFilter(this.defaultFilter.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aReportFilterFields.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aReportFilterFields
	 * @throws Exception
	 */
	public void doShowDialog(ReportFilterFields aReportFilterFields) throws Exception {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (enqModule) {
			doReadOnly(true);
		} else if (aReportFilterFields.isNew()) {
			this.btnCtrl.setInitNew();
			// setFocus
			this.fieldName.focus();
		} else {
			this.fieldName.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aReportFilterFields);
			renderFilterFields();
			if (reportFilterFields.isNewRecord()) {
				setEnableListBox_OnEdit();
			}
			this.window_ReportFilterFieldsDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReportFilterFieldsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * doCancel Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.fieldName.isReadonly()) {
			this.fieldName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportFilterFieldsDialog_FieldName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.fieldLabel.isReadonly()) {
			this.fieldLabel.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportFilterFieldsDialog_FieldLabel.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.fieldDBName.isReadonly()) {
			this.fieldDBName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportFilterFieldsDialog_FieldDBName.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		/*
		 * if (!this.seqOrder.isReadonly()) { this.seqOrder.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_ReportFilterFieldsDialog_SeqOrder.value"),
		 * PennantRegularExpressions.REGEX_ALPHANUM, true)); }
		 */
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.fieldName.setConstraint("");
		this.fieldType.setConstraint("");
		this.fieldLabel.setConstraint("");
		this.fieldDBName.setConstraint("");
		this.appUtilMethodName.setConstraint("");
		this.moduleName.setConstraint("");
		this.lovHiddenFieldMethod.setConstraint("");
		this.lovTextFieldMethod.setConstraint("");
		this.fieldLength.setConstraint("");
		this.fieldMaxValue.setConstraint("");
		this.fieldMinValue.setConstraint("");
		this.seqOrder.setConstraint("");
		this.fieldConstraint.setConstraint("");
		this.fieldErrorMessage.setConstraint("");
		this.whereCondition.setConstraint("");
		this.staticValue.setConstraint("");
		this.fieldWidth.setConstraint("");
		this.defaultFilter.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.fieldName.setErrorMessage("");
		this.fieldType.setErrorMessage("");
		this.fieldLabel.setErrorMessage("");
		this.fieldDBName.setErrorMessage("");
		this.appUtilMethodName.setErrorMessage("");
		this.moduleName.setErrorMessage("");
		this.lovHiddenFieldMethod.setErrorMessage("");
		this.lovTextFieldMethod.setErrorMessage("");
		this.fieldLength.setErrorMessage("");
		this.fieldMaxValue.setErrorMessage("");
		this.fieldMinValue.setErrorMessage("");
		this.seqOrder.setErrorMessage("");
		this.fieldConstraint.setErrorMessage("");
		this.fieldErrorMessage.setErrorMessage("");
		this.whereCondition.setErrorMessage("");
		this.staticValue.setErrorMessage("");
		this.fieldWidth.setErrorMessage("");
		this.defaultFilter.setErrorMessage("");

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ReportFilterFields object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final ReportFilterFields aReportFilterFields = new ReportFilterFields();
		BeanUtils.copyProperties(getReportFilterFields(), aReportFilterFields);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aReportFilterFields.getFieldName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aReportFilterFields.getRecordType())) {
				aReportFilterFields.setVersion(aReportFilterFields.getVersion() + 1);
				aReportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aReportFilterFields.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newReportFilterFieldsListProcess(aReportFilterFields, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ReportFilterFieldsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					this.reportConfigurationDialogCtrl.doFillReportFilterFieldsList(reportFilterFieldsList);
					this.window_ReportFilterFieldsDialog.onClose();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	boolean enable;

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		doReadOnly(false);
		if (getReportFilterFields().isNewRecord()) {
			setComponentAccessType("ReportFilterFieldDialog_fieldName", false, this.fieldName, this.space_FieldName,
					this.label_ReportFilterFieldsDialog_FieldName, this.hlayout_FieldName, this.row_Zero);
			this.btnCancel.setVisible(false);
		} else {
			// this.fieldName.setReadonly(true);
			this.btnSave.setVisible(true);
			this.btnCancel.setVisible(true);
			this.seqOrder.setReadonly(false);
			this.fieldWidth.setReadonly(false);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.reportFilterFields.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (this.reportFilterFields.isNewRecord()) {
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				setEnableListBox_OnEdit();

			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering ");

		boolean tempReadOnly = readOnly;

		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.reportFilterFields.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("ReportFilterFieldDialog_fieldName", tempReadOnly, this.fieldName, this.space_FieldName,
				this.label_ReportFilterFieldsDialog_FieldName, this.hlayout_FieldName, null);
		setComponentAccessType("ReportFilterFieldDialog_fieldType", tempReadOnly, this.fieldType, this.space_FieldType,
				this.label_ReportFilterFieldsDialog_FieldType, this.hlayout_FieldType, null);
		setRowInvisible(this.row_Zero, this.hlayout_FieldName, this.hlayout_FieldType);

		setComponentAccessType("ReportFilterFieldDialog_fieldLabel", tempReadOnly, this.fieldLabel,
				this.space_FieldLabel, this.label_ReportFilterFieldsDialog_FieldLabel, this.hlayout_FieldLabel, null);
		setComponentAccessType("ReportFilterFieldDialog_fieldDBName", tempReadOnly, this.fieldDBName,
				this.space_FieldDBName, this.label_ReportFilterFieldsDialog_FieldDBName, this.hlayout_FieldDBName,
				null);
		setRowInvisible(this.row_One, this.hlayout_FieldLabel, this.hlayout_FieldDBName);

		setComponentAccessType("ReportFilterFieldDialog_appUtilMethodName", tempReadOnly, this.appUtilMethodName,
				this.space_AppUtilMethodName, this.label_ReportFilterFieldsDialog_AppUtilMethodName,
				this.hlayout_AppUtilMethodName, null);
		setComponentAccessType("ReportFilterFieldDialog_moduleName", tempReadOnly, this.moduleName,
				this.space_ModuleName, this.label_ReportFilterFieldsDialog_ModuleName, this.hlayout_ModuleName, null);
		setRowInvisible(this.row_Two, this.hlayout_AppUtilMethodName, this.hlayout_ModuleName);

		setComponentAccessType("ReportFilterFieldDialog_lovHiddenFieldMethod", tempReadOnly, this.lovHiddenFieldMethod,
				null, this.label_ReportFilterFieldsDialog_LovHiddenFieldMethod, this.hlayout_LovHiddenFieldMethod,
				null);
		setComponentAccessType("ReportFilterFieldDialog_lovTextFieldMethod", tempReadOnly, this.lovTextFieldMethod,
				null, this.label_ReportFilterFieldsDialog_LovTextFieldMethod, this.hlayout_LovTextFieldMethod, null);
		setRowInvisible(this.row_Three, this.hlayout_LovHiddenFieldMethod, this.hlayout_LovTextFieldMethod);

		setComponentAccessType("ReportFilterFieldDialog_multiSelectSearch", tempReadOnly, this.multiSelectSearch, null,
				this.label_ReportFilterFieldsDialog_MultiSelectSearch, this.hlayout_MultiSelectSearch, null);
		setComponentAccessType("ReportFilterFieldDialog_fieldLength", tempReadOnly, this.fieldLength,
				this.space_FieldLength, this.label_ReportFilterFieldsDialog_FieldLength, this.hlayout_FieldLength,
				null);
		setRowInvisible(this.row_Four, this.hlayout_MultiSelectSearch, this.hlayout_FieldLength);

		setComponentAccessType("ReportFilterFieldDialog_fieldMaxValue", tempReadOnly, this.fieldMaxValue,
				this.space_FieldMaxValue, this.label_ReportFilterFieldsDialog_FieldMaxValue, this.hlayout_FieldMaxValue,
				null);
		setComponentAccessType("ReportFilterFieldDialog_fieldMinValue", tempReadOnly, this.fieldMinValue,
				this.space_FieldMinValue, this.label_ReportFilterFieldsDialog_FieldMinValue, this.hlayout_FieldMinValue,
				null);
		setRowInvisible(this.row_Five, this.hlayout_FieldMaxValue, this.hlayout_FieldMinValue);

		setComponentAccessType("ReportFilterFieldDialog_seqOrder", tempReadOnly, this.seqOrder, this.space_SeqOrder,
				this.label_ReportFilterFieldsDialog_SeqOrder, this.hlayout_SeqOrder, null);
		setComponentAccessType("ReportFilterFieldDialog_mandatory", tempReadOnly, this.mandatory, null,
				this.label_ReportFilterFieldsDialog_Mandatory, this.hlayout_Mandatory, null);
		setRowInvisible(this.row_Six, this.hlayout_SeqOrder, this.hlayout_Mandatory);

		setComponentAccessType("ReportFilterFieldDialog_fieldConstraint", tempReadOnly, this.fieldConstraint,
				this.space_FieldConstraint, this.label_ReportFilterFieldsDialog_FieldConstraint,
				this.hlayout_FieldConstraint, null);
		setComponentAccessType("ReportFilterFieldDialog_fieldErrorMessage", tempReadOnly, this.fieldErrorMessage,
				this.space_FieldErrorMessage, this.label_ReportFilterFieldsDialog_FieldErrorMessage,
				this.hlayout_FieldErrorMessage, null);
		setRowInvisible(this.row_Seven, this.hlayout_FieldConstraint, this.hlayout_FieldErrorMessage);

		setComponentAccessType("ReportFilterFieldDialog_whereCondition", tempReadOnly, this.whereCondition, null,
				this.label_ReportFilterFieldsDialog_WhereCondition, this.hlayout_WhereCondition, null);
		setComponentAccessType("ReportFilterFieldDialog_staticValue", tempReadOnly, this.staticValue, null,
				this.label_ReportFilterFieldsDialog_StaticValue, this.hlayout_StaticValue, null);
		setRowInvisible(this.row_Eight, this.hlayout_WhereCondition, this.hlayout_StaticValue);

		setComponentAccessType("ReportFilterFieldDialog_fieldWidth", tempReadOnly, this.fieldWidth,
				this.space_FieldWidth, this.label_ReportFilterFieldsDialog_FieldWidth, this.hlayout_FieldWidth, null);
		setComponentAccessType("ReportFilterFieldDialog_filterRequired", tempReadOnly, this.filterRequired,
				this.space_FilterRequired, this.label_ReportFilterFieldsDialog_FilterRequired,
				this.hlayout_FilterRequired, null);
		setRowInvisible(this.row_Nine, this.hlayout_FieldWidth, this.hlayout_FilterRequired);

		setComponentAccessType("ReportFilterFieldDialog_defaultFilter", tempReadOnly, this.defaultFilter,
				this.space_DefaultFilter, this.label_ReportFilterFieldsDialog_DefaultFilter, this.hlayout_DefaultFilter,
				this.row_Ten);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear(boolean isAll) {
		logger.debug("Entering ");

		// remove validation, if there are a save before
		if (isAll || "STATICVALUE".equals(this.fieldType.getSelectedItem().getValue())) {
			if (isAll) {
				this.fieldName.setValue("");
				this.fieldType.setValue("");
			}
			this.fieldLabel.setValue("");
			this.fieldDBName.setValue("");
			this.seqOrder.setText("");
			// this.seqOrder.setValue(0);
		}
		this.appUtilMethodName.setValue(Labels.getLabel("Combo.Select"));
		this.moduleName.setValue(Labels.getLabel("Combo.Select"));
		this.lovHiddenFieldMethod.setValue(Labels.getLabel("Combo.Select"));
		this.lovTextFieldMethod.setValue(Labels.getLabel("Combo.Select"));
		this.fieldLength.setText("20");
		this.fieldMaxValue.setText("");
		this.fieldMinValue.setText("");
		this.fieldConstraint.setValue("");
		this.fieldErrorMessage.setValue("");
		this.whereCondition.setValue("");
		this.staticValue.setValue("");
		this.fieldWidth.setText("150");
		this.defaultFilter.setValue("=");
		this.mandatory.setChecked(false);
		this.multiSelectSearch.setValue("");
		this.filterRequired.setChecked(true);

		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final ReportFilterFields aReportFilterFields = new ReportFilterFields();
		BeanUtils.copyProperties(getReportFilterFields(), aReportFilterFields);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(reportFilterFields.getRecordType())) {
			doSetValidation();
			// fill the reportFilterFields object with the components data
			doWriteComponentsToBean(aReportFilterFields);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aReportFilterFields.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReportFilterFields.getRecordType())) {
				aReportFilterFields.setVersion(aReportFilterFields.getVersion() + 1);
				if (isNew) {
					aReportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReportFilterFields.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aReportFilterFields.setVersion(1);
				aReportFilterFields.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aReportFilterFields.getRecordType())) {
				tranType = PennantConstants.TRAN_UPD;
				aReportFilterFields.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aReportFilterFields.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aReportFilterFields.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newReportFilterFieldsListProcess(aReportFilterFields, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ReportFilterFieldsDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				this.reportConfigurationDialogCtrl.doFillReportFilterFieldsList(reportFilterFieldsList);
				this.window_ReportFilterFieldsDialog.onClose();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method adds the ReportFilterFields object into reportFilterFieldsList by setting RecordType according to
	 * tranType
	 * <p>
	 * example: if(tranType==PennantConstants.TRAN_DEL){
	 * aReportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_DEL); }
	 * </p>
	 * 
	 * @param aReportFilterFields
	 *            (ReportFilterFields)
	 * @param tranType
	 *            (String)doSave
	 * @return auditHeader (AuditHeader)
	 */
	private AuditHeader newReportFilterFieldsListProcess(ReportFilterFields aReportFilterFields, String tranType) {
		logger.debug("Entering ");

		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aReportFilterFields, tranType);
		reportFilterFieldsList = new ArrayList<ReportFilterFields>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = String.valueOf(aReportFilterFields.getFieldName());
		errParm[0] = PennantJavaUtil.getLabel("label_FieldName") + ":" + valueParm[0];
		if (this.reportConfigurationDialogCtrl.getReportFilterFieldsList() != null
				&& this.reportConfigurationDialogCtrl.getReportFilterFieldsList().size() > 0) {
			for (int i = 0; i < this.reportConfigurationDialogCtrl.getReportFilterFieldsList().size(); i++) {
				ReportFilterFields reportFilterFields = this.reportConfigurationDialogCtrl.getReportFilterFieldsList()
						.get(i);

				if (aReportFilterFields.getFieldName().trim()
						.equalsIgnoreCase(reportFilterFields.getFieldName().trim())) {
					// Both Current and Existing list expense same
					// if same educational expenses added twice set error detail
					if (getReportFilterFields().isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aReportFilterFields.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aReportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							reportFilterFieldsList.add(aReportFilterFields);
						} else if (aReportFilterFields.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aReportFilterFields.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aReportFilterFields.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							reportFilterFieldsList.add(aReportFilterFields);
						} else if (aReportFilterFields.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < this.reportConfigurationDialogCtrl.getReportFilterFieldsList()
									.size(); j++) {
								ReportFilterFields rFilterFields = this.reportConfigurationDialogCtrl
										.getReportFilterFieldsList().get(j);
								if (aReportFilterFields.getFieldName().trim()
										.equalsIgnoreCase(reportFilterFields.getFieldName().trim())) {
									reportFilterFieldsList.add(rFilterFields);
								}
							}
						} else if (aReportFilterFields.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aReportFilterFields.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							reportFilterFieldsList.add(reportFilterFields);
						}
					}
				} else {
					reportFilterFieldsList.add(reportFilterFields);
				}
			}
		}
		if (!recordAdded) {
			reportFilterFieldsList.add(aReportFilterFields);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	// OnChange Events

	/**
	 * When user clicks on "appUtilMethodName"
	 * @param event
	 * @throws Exception
	 */
	public void onChange$fieldType(Event event) throws Exception {
		String selectedFieldType = this.fieldType.getSelectedItem().getValue().toString();
		doClear(false);

		doFillInstructions(selectedFieldType);
		setFieldType(selectedFieldType);
	}

	/**
	 * When user checks on "filterRequired"
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$filterRequired(Event event) throws Exception {
		if (this.filterRequired.isChecked()) {
			this.defaultFilter.setValue("=");
			this.defaultFilter.setDisabled(true);
		} else {
			this.defaultFilter.setDisabled(false);
		}
	}

	/**
	 * @param selectedFieldType
	 */

	public void setFieldType(String selectedFieldType) {
		// Show FieldConstraint and FieldError Message Row only select Text box
		if (!selectedFieldType.equals(PennantConstants.List_Select) && "TXT".equals(selectedFieldType)) {
			this.row_Seven.setVisible(true);
		} else {
			this.row_Seven.setVisible(false);
		}
		/*
		 * Show AppUtilMethodName, ModuleName,Get Value method and Get Label Methods Rows only select LOVSEARCH and
		 * DYNAMICLIST But AppUtilMethodName is disabled for these two Components
		 */
		this.row_Two.setVisible(false);
		if (!selectedFieldType.equals(PennantConstants.List_Select) && "LOVSEARCH".equals(selectedFieldType)
				|| "DYNAMICLIST".equals(selectedFieldType)) {
			this.row_Two.setVisible(true);
			this.row_Three.setVisible(true);
			this.row_Four.setVisible(true);
			this.appUtilMethodName.setDisabled(true);
			this.space_ModuleName.setVisible(true);
			this.appUtilMethodName.setValue(Labels.getLabel("Combo.Select"));
			if ("DYNAMICLIST".equals(selectedFieldType)) {
				this.row_Eight.setVisible(true); // WhereClause Row is only for Dynamic List
			}
		} else {
			this.row_Three.setVisible(false);
			this.row_Four.setVisible(false);
			this.row_Eight.setVisible(false);
			this.appUtilMethodName.setDisabled(false);
			this.appUtilMethodName.setValue(Labels.getLabel("Combo.Select"));
		}
		/* FieldMaxValue and FieldMinValue row is only for Number and decimal boxes */
		if (!selectedFieldType.equals(PennantConstants.List_Select)
				&& ("NUMBER".equals(selectedFieldType) || "DECIMAL".equals(selectedFieldType))) {
			this.row_Five.setVisible(true);
		} else {
			this.row_Five.setVisible(false);
		}
		/* for MULTISELANDLIST ,MULTISELINLIST and STATICLIST Allow to select PeannatAppUtil method name */
		if (!selectedFieldType.equals(PennantConstants.List_Select)
				&& ("MULTISELANDLIST".equals(selectedFieldType) || "MULTISELINLIST".equals(selectedFieldType))
				|| "STATICLIST".equals(selectedFieldType)) {
			this.row_Two.setVisible(true);
			this.moduleName.setDisabled(true);
			this.space_AppUtilMethodName.setVisible(true);
		} else {
			this.moduleName.setDisabled(false);
			this.space_AppUtilMethodName.setVisible(false);
		}
		if ("MULTISELANDLIST".equals(selectedFieldType)) {
			this.fieldDBName.setDisabled(true);
			this.space_FieldDBName.setVisible(false);
		} else {
			this.fieldDBName.setDisabled(false);
			this.space_FieldDBName.setVisible(true);
		}
		/* Allow Filter Required for only input types */

		if (fieldTypes.containsKey(selectedFieldType)) {
			this.filterRequired.setChecked(true);
			this.filterRequired.setDisabled(false);
			this.row_Ten.setVisible(true);
			this.row_Eleven.setVisible(true);
		} else {
			this.filterRequired.setChecked(false);
			this.filterRequired.setDisabled(true);
			this.row_Eleven.setVisible(false);
		}

		/* if Type is STATICVALUE then show Show only STATICVALUE */
		if (!selectedFieldType.equals(PennantConstants.List_Select) && ("STATICVALUE".equals(selectedFieldType))) {
			this.row_Nine.setVisible(true);
			this.row_One.setVisible(false);
			this.row_Six.setVisible(false);
		} else {
			this.row_Nine.setVisible(false);
			this.row_One.setVisible(true);
			this.row_Six.setVisible(true);
		}
		/* if filter not required then only allow to choose default filter */
		if (this.filterRequired.isChecked()) {
			this.defaultFilter.setValue("=");
			this.defaultFilter.setDisabled(true);
		} else {
			this.defaultFilter.setDisabled(false);
		}
		// Field Length setting
		if ("TXT".equals(selectedFieldType) || "DECIMAL".equals(selectedFieldType)
				|| "NUMBER".equals(selectedFieldType)) {
			this.fieldLength.setDisabled(false);
		} else {
			this.fieldLength.setValue(0);
			this.fieldLength.setDisabled(true);
		}
		if (!fieldDateTypesMap.containsKey(selectedFieldType)) {
			this.fieldWidth.setDisabled(true);
		} else {
			this.fieldWidth.setDisabled(false);
		}
		if (this.fieldWidth.isDisabled() && this.filterRequired.isDisabled()) {
			this.row_Ten.setVisible(false);
		} else {
			this.row_Ten.setVisible(true);
		}
	}

	/**
	 * When user clicks on "appUtilMethodName"
	 * @param event
	 * @throws Exception
	 */
	public void onChange$appUtilMethodName(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (!("").equals(this.appUtilMethodName.getSelectedItem().getValue())
				&& !this.appUtilMethodName.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			this.moduleName.setSelectedIndex(0);
			this.space_LovHiddenFieldMethod.setVisible(true);
			this.space_LovTextFieldMethod.setVisible(true);

		} else {
			this.space_LovHiddenFieldMethod.setVisible(false);
			this.space_LovTextFieldMethod.setVisible(false);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "moduleName"
	 * @param event
	 * @throws Exception
	 */
	public void onChange$moduleName(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (!("").equals(this.moduleName.getSelectedItem().getValue())
				&& !this.moduleName.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			this.appUtilMethodName.setSelectedIndex(0);
			this.space_LovHiddenFieldMethod.setVisible(true);
			this.space_LovTextFieldMethod.setVisible(true);
			getAllMethods(this.moduleName.getSelectedItem().getValue().toString(), this.lovHiddenFieldMethod);
			getAllMethods(this.moduleName.getSelectedItem().getValue().toString(), this.lovTextFieldMethod);
		} else {

			this.space_LovHiddenFieldMethod.setVisible(false);
			this.space_LovTextFieldMethod.setVisible(false);
			this.lovTextFieldMethod.getChildren().clear();
			this.lovTextFieldMethod.setValue(Labels.getLabel("Combo.Select"));
			this.lovHiddenFieldMethod.getChildren().clear();
			this.lovHiddenFieldMethod.setValue(Labels.getLabel("Combo.Select"));
		}
		logger.debug("Leaving " + event.toString());
	}

	/***
	 * Fills the Combo box "AppUtilMethodName"
	 */
	@SuppressWarnings("rawtypes")
	private void setListAppUtilMethodName() {
		logger.debug("Entering");
		List<ValueLabel> appUtilMethodsList = new ArrayList<ValueLabel>();
		Class pennantAppUtil = PennantStaticListUtil.class;

		// Get the methods
		Method[] methods = pennantAppUtil.getDeclaredMethods();
		sortMethodDetails(Arrays.asList(methods));

		// Loop through the methods and add to list
		this.appUtilMethodName.getChildren().clear();

		for (Method method : methods) {
			appUtilMethodsList.add(new ValueLabel(method.getName(), method.getName()));
		}
		Comboitem comboitem = new Comboitem();
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		comboitem.setValue(PennantConstants.List_Select);
		this.appUtilMethodName.appendChild(comboitem);
		this.appUtilMethodName.setSelectedItem(comboitem);

		for (int i = 0; i < appUtilMethodsList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(appUtilMethodsList.get(i).getLabel());
			comboitem.setValue(appUtilMethodsList.get(i).getValue());
			this.appUtilMethodName.appendChild(comboitem);
		}

		logger.debug("Leaving");
	}

	private List<Method> sortMethodDetails(List<Method> methods) {

		if (methods != null && methods.size() > 0) {
			Collections.sort(methods, new Comparator<Method>() {
				public int compare(Method detail1, Method detail2) {

					if (detail1.getName().compareTo(detail2.getName()) > 0) {
						return 1;
					} else if (detail1.getName().compareTo(detail2.getName()) < 0) {
						return -1;
					}
					return 0;
				}
			});

		}

		return methods;
	}

	/**
	 * @param className
	 * @param component
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void getAllMethods(String className, Combobox component) throws Exception {
		List<ValueLabel> methodsList = new ArrayList<ValueLabel>();
		Class value = ModuleUtil.getModuleClass(className);
		
		// Get the methods
		Method[] methods = value.getDeclaredMethods();
		sortMethodDetails(Arrays.asList(methods));
		
		// Loop through the methods and add to list
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				methodsList.add(new ValueLabel(method.getName(), method.getName()));
			}
		}
		component.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		comboitem.setValue(PennantConstants.List_Select);
		component.appendChild(comboitem);
		component.setSelectedItem(comboitem);

		for (int i = 0; i < methodsList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(methodsList.get(i).getLabel());
			comboitem.setValue(methodsList.get(i).getValue());
			component.appendChild(comboitem);
		}
	}

	/**
	 * Get Audit Header Details
	 * @param aReportFilterFields
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ReportFilterFields aReportFilterFields, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReportFilterFields.getBefImage(), aReportFilterFields);
		return new AuditHeader(String.valueOf(aReportFilterFields.getId()), null, null, null, auditDetail,
				aReportFilterFields.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ReportFilterFieldsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.reportFilterFields);
	}

	private void doFillInstructions(String filedType) {

		if (filedType.equals(PennantConstants.List_Select)) {
			this.instructions.setContent("");
		} else {
			FIELDTYPE fieldValueType = FIELDTYPE.valueOf(filedType);
			switch (fieldValueType) {
			case TXT:
				this.instructions.setContent(getInstructions("TXT"));
				break;
			case STATICLIST:
				this.instructions.setContent(getInstructions("STATICLIST"));
				break;
			case DYNAMICLIST:
				this.instructions.setContent(getInstructions("DYNAMICLIST"));
				break;
			case LOVSEARCH:
				this.instructions.setContent(getInstructions("LOVSEARCH"));
				break;
			case MULTISELANDLIST:
				this.instructions.setContent(getInstructions("MULTISELANDLIST"));
				break;
			case MULTISELINLIST:
				this.instructions.setContent(getInstructions("MULTISELINLIST"));
				break;
			case STATICVALUE:
				this.instructions.setContent(getInstructions("STATICVALUE"));
				break;
			default:
				this.instructions.setContent("");
				break;
			}
		}
	}

	// Show FilterFields
	private void renderFilterFields() {
		this.listBox.getItems().clear();
		List<ReportFilterFields> filterFields = this.reportConfigurationDialogCtrl.getReportFilterFieldsList();

		if (filterFields != null && !filterFields.isEmpty()) {
			for (ReportFilterFields reportFilterField : filterFields) {
				if (!StringUtils.equals(getReportFilterFields().getFieldDBName(), reportFilterField.getFieldDBName())) {
					listBox.appendChild(doFillDetails(reportFilterField));
				}
			}
		}
	}

	public Listitem doFillDetails(ReportFilterFields reportFilterField) {

		Listitem listitem = new Listitem();
		Listcell listcell = new Listcell();
		Checkbox ch = new Checkbox();
		ComponentsCtrl.applyForward(ch, "onCheck=onChecklistItemSelect");
		String strTableField = filterMap.get(reportFilterField.getFieldDBName());
		if (StringUtils.trimToNull(strTableField) != null) {
			ch.setChecked(true);
		} else {
			strTableField = reportFilterField.getFieldDBName();
		}

		listcell.appendChild(ch);
		listcell.setParent(listitem);
		ch.setDisabled(true);

		Listcell listcell1 = new Listcell();
		Textbox dbField = new Textbox();
		dbField.setText(reportFilterField.getFieldDBName());
		dbField.setReadonly(true);
		listcell1.appendChild(dbField);
		listcell1.setParent(listitem);

		Listcell listcell2 = new Listcell();
		Textbox tableField = new Textbox();
		if (ch.isChecked()) {
			tableField.setText(strTableField);
		} else {
			tableField.setText("");
		}
		tableField.setReadonly(true);
		listcell2.appendChild(tableField);
		listcell2.setParent(listitem);
		return listitem;
	}

	public void onChecklistItemSelect(Event event) {
		logger.debug("Entering");

		List<Listitem> items = this.listBox.getItems();
		for (Listitem listitem : items) {
			textBox = ((Textbox) listitem.getLastChild().getFirstChild());

			if (((Checkbox) listitem.getFirstChild().getFirstChild()).isChecked()) {
				textBox.setReadonly(false);
				dosetValidation(textBox);
			} else {
				textBox.setConstraint("");
				textBox.setErrorMessage("");
				textBox.setText("");
				textBox.setReadonly(true);
			}
		}
		logger.debug("Leaving");
	}

	private void dosetValidation(Textbox textBox) {
		logger.debug("Entering");
		if (!textBox.isReadonly()) {
			textBox.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReportFilterFieldsDialog_TableFields.value"), null, true));
		}
		logger.debug("Leaving");
	}

	private String getFilterString() {
		StringBuilder stringBuilder = new StringBuilder();
		List<Component> items = listBox.getChildren();

		for (Component component : items) {
			if (component instanceof Listitem) {

				List<Listcell> listcells = component.getChildren();

				for (int i = 0; i < listcells.size(); i++) {
					Listcell listcell = listcells.get(i);
					Component component1 = listcell.getFirstChild();

					if (listcell.getFirstChild() instanceof Checkbox) {
						Checkbox ch = (Checkbox) component1;
						if (!ch.isChecked()) {
							break;
						}
					}

					if (component1 instanceof Textbox) {
						Textbox textbox = (Textbox) component1;
						if (i == 1 && stringBuilder.length() > 0) {
							stringBuilder.append("|");
						} else if (i == 2) {
							stringBuilder.append("@");
						}
						stringBuilder.append(textbox.getText());
					}
				}
			}
		}
		return stringBuilder.toString();
	}

	private void setEnableListBox_OnEdit() {
		List<Component> items = listBox.getChildren();

		for (Component component : items) {
			if (component instanceof Listitem) {

				List<Listcell> listcells = component.getChildren();
				Component checkBoxComponent = listcells.get(0).getFirstChild();
				Component textBoxComponent = listcells.get(2).getFirstChild();
				if (checkBoxComponent instanceof Checkbox) {
					Checkbox ch = (Checkbox) checkBoxComponent;
					ch.setDisabled(false);
				}

				if (textBoxComponent instanceof Textbox) {
					Textbox textbox = (Textbox) textBoxComponent;
					if(((Checkbox) component.getFirstChild().getFirstChild()).isChecked()){
					textbox.setReadonly(false);
					}
				}
			}
		}
	}

	private void setDisabledListBox_OnCancel() {
		List<Component> items = listBox.getChildren();

		for (Component component : items) {
			if (component instanceof Listitem) {

				List<Listcell> listcells = component.getChildren();
				Component checkBoxComponent = listcells.get(0).getFirstChild();
				Component textBoxComponent = listcells.get(2).getFirstChild();
				if (checkBoxComponent instanceof Checkbox) {
					((Checkbox) checkBoxComponent).setDisabled(true);
				}

				if (textBoxComponent instanceof Textbox) {
					((Textbox) textBoxComponent).setReadonly(true);
				}
			}
		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.reportFilterFields.getFieldName());
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

	public ReportFilterFields getReportFilterFields() {
		return this.reportFilterFields;
	}

	public void setReportFilterFields(ReportFilterFields reportFilterFields) {
		this.reportFilterFields = reportFilterFields;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public ReportConfigurationDialogCtrl getReportConfigurationDialogCtrl() {
		return reportConfigurationDialogCtrl;
	}

	public void setReportConfigurationDialogCtrl(ReportConfigurationDialogCtrl reportConfigurationDialogCtrl) {
		this.reportConfigurationDialogCtrl = reportConfigurationDialogCtrl;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	
	private String getInstructions(String component) {
		StringBuilder builder = new StringBuilder();
		
		if(component.equals("TXT")) {
			builder.append("* 'Field Constraint' is regix .It is must to avoid injection problems .<br/>");
			builder.append("* 'Field ErrorMessage' is error message to show when regix fails.<br/>");
			builder.append("*  Regix must follow single \\ pattern .Eg:\\+^[0-9\\s]+");
			return builder.toString();
		}
		else if (component.equals("STATICLIST")) {
			builder.append("* This StaticList Type renders a ComboBox with value labels.<br/>");
			builder.append("* 'AppUtil Method Name' is PennantAppUtilMethod,java method name which returns value,Labels to render ComboBox.");
			return builder.toString();
		}
		else if (component.equals("DYNAMICLIST")) {
			builder.append("* This DynamicList Type renders a ComboBox with value labels witch comes from table.<br/>");
			builder.append("* 'Module Name'  is PennaJavaUtil Class module names from which table we have to show List of values.<br/>");
			builder.append("* 'Value Get Method' is getMethod for ComboBox Value.<br/>");
			builder.append("* 'Value Label Method' is getMethod for ComboBox Label.");
			return builder.toString();
		}
		
		else if (component.equals("LOVSEARCH")) {
			builder.append("* 'Module Name'  is PennaJavaUtil Class module names from which table we have to show List of values.<br/>");
			builder.append("* 'Value Get Method' is getMethod for hidden TextBox value.<br/>");
			builder.append( "* 'Value Label Method' is getMethod for Label TextBox value.");
			return builder.toString();
		}
		else if (component.equals("MULTISELANDLIST")) {
			builder.append("* This is useful in use single component for different status.<br/>");
			builder.append("* This Multi Select(With And Condition) Type renders a BandBox with multi select check boxes values.<br/>");
			builder.append("* 'AppUtil Method Name' is PennantAppUtilMethod.java method name which returns dbFieldName,value,Labels to render BandBox.<br/>");
			builder.append("Eg :To Select differnt User Status from one component.PennantAppUtil Method must follw below.<br/>");
			builder.append("public static ArrayList<ValueLabel> getUserStatusList() { <br/>");
			builder.append("reportNames.add(new ValueLabel(\"usrEnabled\",\"1\",\"User Enabled\" <br/>");
			builder.append("reportNames.add(new ValueLabel(\"usrAcExp\",\"1\",\"User Expired\" <br/>");
			builder.append("reportNames.add(new ValueLabel(\"usrIsMultiBranch\",\"1\",\"Multi Branch\" <br/>");
			builder.append("reportNames.add(new ValueLabel(\"usrCanOverrideLimits\",\"1\",\"Over Ride Limits\" <br/>");
			builder.append("return reportNames;<br/>");
			builder.append("}");
			
			return builder.toString();
		}
		else if (component.equals("MULTISELINLIST")) {
			builder.append("* This Multi Select(With In Condition) Type renders a BandBox with multi select check boxes values .<br/>");
			builder.append("* This will form a In condition for all Selected values. Eg . RecordType in ('NEW','Edit','Delete')<br/>");
			builder.append("* 'AppUtil Method Name' is PennantAppUtilMethod.java method name which returns value,Labels to render BandBox.");
			return builder.toString();
		}
		else if (component.equals("STATICVALUE")) {
			builder.append("* This is for hidden values with no filter to display .like our report always select Active records only.<br/>");
			builder.append("* As it appends in query must follow sql rules");
			return builder.toString();
		}
		return builder.toString();
	}

}
