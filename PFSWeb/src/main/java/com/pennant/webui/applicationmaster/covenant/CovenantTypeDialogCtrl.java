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
 * * FileName : CovenantTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-02-2019 * *
 * Modified Date : 06-02-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-02-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.covenant;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.finance.covenant.CovenantTypeService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.staticlist.AppStaticList;

/**
 * This is the controller class for the /WEB-INF/pages/Finance.Covenant/CovenantType/covenantTypeDialog.zul file. <br>
 */
public class CovenantTypeDialogCtrl extends GFCBaseCtrl<CovenantType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CovenantTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CovenantTypeDialog;

	protected Uppercasebox code;
	protected Textbox description;
	protected Combobox category;
	protected ExtendedCombobox docType;
	protected Checkbox allowPostPonement;
	protected Intbox maxAllowedDays;
	protected Uppercasebox allowedPaymentModes;
	protected Checkbox alertsRequired;
	protected Combobox cbFrequency;
	protected Intbox graceDays;
	protected Intbox alertDays;
	protected Combobox alertType;
	protected ExtendedCombobox alertToRoles;
	protected ExtendedCombobox userTemplate;
	protected ExtendedCombobox customerTemplate;
	private CovenantType covenantType;
	protected Combobox cmbCovenantType;

	private transient CovenantTypeListCtrl covenantTypeListCtrl;
	private transient CovenantTypeService covenantTypeService;

	private transient List<Property> covenantCategories = AppStaticList.getCovenantCategories();
	private transient List<Property> listAlertType = AppStaticList.getAlertsFor();
	private transient List<Property> listFrequency = AppStaticList.getFrequencies();
	private transient List<Property> listCovenantType = AppStaticList.getCovenantTypes();

	/**
	 * default constructor.<br>
	 */
	public CovenantTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CovenantTypeDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.covenantType.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CovenantTypeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_CovenantTypeDialog);

		try {
			// Get the required arguments.
			this.covenantType = (CovenantType) arguments.get("covenantType");
			this.covenantTypeListCtrl = (CovenantTypeListCtrl) arguments.get("covenantTypeListCtrl");

			if (this.covenantType == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			CovenantType acovenantType = new CovenantType();
			BeanUtils.copyProperties(this.covenantType, acovenantType);
			this.covenantType.setBefImage(acovenantType);

			// Render the page and display the data.
			doLoadWorkFlow(this.covenantType.isWorkflow(), this.covenantType.getWorkflowId(),
					this.covenantType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.covenantType);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.code.setMaxlength(20);
		this.description.setMaxlength(100);
		this.description.setWidth("850px");

		this.docType.setModuleName("DocumentType");
		this.docType.setValueColumn("DocTypeCode");
		this.docType.setDescColumn("DocTypeDesc");
		this.docType.setValidateColumns(new String[] { "DocTypeCode" });
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("CategoryCode", DocumentCategories.COVENANT.getKey(), Filter.OP_EQUAL);
		this.docType.setFilters(filter);

		this.graceDays.setMaxlength(3);
		this.alertDays.setMaxlength(3);

		this.alertToRoles.setModuleName("OperationRoles");
		this.alertToRoles.setValueColumn("RoleCd");
		this.alertToRoles.setDescColumn("RoleDesc");
		this.alertToRoles.setValidateColumns(new String[] { "RoleCd" });
		this.alertToRoles.setMultySelection(true);
		this.alertToRoles.setInputAllowed(false);
		this.alertToRoles.setWidth("150px");

		this.userTemplate.setModuleName("MailTemplate");
		this.userTemplate.setValueColumn("TemplateCode");
		this.userTemplate.setDescColumn("TemplateDesc");
		this.userTemplate.setValidateColumns(new String[] { "TemplateCode", "TemplateDesc" });
		Filter[] userTemplateFilter = new Filter[2];
		userTemplateFilter[0] = new Filter("TemplateFor", NotificationConstants.TEMPLATE_FOR_AE);
		userTemplateFilter[1] = new Filter("TemplateCode", "COVENANT%", Filter.OP_LIKE);
		userTemplate.setFilters(userTemplateFilter);

		this.customerTemplate.setModuleName("MailTemplate");
		this.customerTemplate.setValueColumn("TemplateCode");
		this.customerTemplate.setDescColumn("TemplateDesc");
		this.customerTemplate.setValidateColumns(new String[] { "TemplateCode", "TemplateDesc" });
		Filter[] custTemplateFilter = new Filter[2];
		custTemplateFilter[0] = new Filter("TemplateFor", NotificationConstants.TEMPLATE_FOR_CN);
		custTemplateFilter[1] = new Filter("TemplateCode", "COVENANT%", Filter.OP_LIKE);
		customerTemplate.setFilters(custTemplateFilter);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CovenantTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CovenantTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CovenantTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CovenantTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
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
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.covenantType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		covenantTypeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.covenantType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param covenantType
	 * 
	 */
	public void doWriteBeanToComponents(CovenantType aCovenantType) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(aCovenantType.getCode());
		this.description.setValue(aCovenantType.getDescription());

		fillList(this.category, covenantCategories, aCovenantType.getCategory());
		fillList(this.cmbCovenantType, listCovenantType, StringUtils.trim(aCovenantType.getCovenantType()));
		fillList(this.alertType, listAlertType, aCovenantType.getAlertType());
		fillList(this.cbFrequency, listFrequency, aCovenantType.getFrequency());

		onSelectCovenantType();

		if (aCovenantType.getDocType() != null) {
			this.docType.setValue(aCovenantType.getDocType());
			this.docType.setDescription(aCovenantType.getDocTypeName());
			DocumentType documentType = new DocumentType();
			documentType.setId(aCovenantType.getDocType());
			this.docType.setObject(documentType);
			onFulfillDocType();
		}

		this.allowPostPonement.setChecked(aCovenantType.isAllowPostPonement());
		this.maxAllowedDays.setValue(aCovenantType.getMaxAllowedDays());
		this.allowedPaymentModes.setValue(aCovenantType.getAllowedPaymentModes());
		this.alertsRequired.setChecked(aCovenantType.isAlertsRequired());
		this.graceDays.setValue(aCovenantType.getGraceDays());
		this.alertDays.setValue(aCovenantType.getAlertDays());

		onCheckAlertsRequired();

		onSelectAlertType();

		if (aCovenantType.getAlertToRoles() != null) {
			Map<String, Object> map = new HashMap<>();
			StringBuilder roles = new StringBuilder();
			for (String role : aCovenantType.getAlertToRoles().split(",")) {
				role = StringUtils.trim(role);

				if (roles.length() > 0) {
					roles.append(",");
				}
				roles.append(role);

				SecurityRole securityRole = new SecurityRole();
				securityRole.setRoleCd(role);
				securityRole.setRoleCode(role);

				map.put(role, securityRole);
			}

			this.alertToRoles.setValue(roles.toString());
			this.alertToRoles.setAttribute("data", map);
			this.alertToRoles.setTooltiptext(roles.toString());
			this.alertToRoles.setInputAllowed(false);
			this.alertToRoles.setSelectedValues(map);
		}

		if (aCovenantType.getUserTemplate() != null) {
			this.userTemplate.setValue(String.valueOf(aCovenantType.getUserTemplateCode()));
			this.userTemplate.setDescription(aCovenantType.getUserTemplateName());
			MailTemplate template = new MailTemplate();
			template.setId(aCovenantType.getUserTemplate());
			template.setTemplateCode(String.valueOf(aCovenantType.getUserTemplateCode()));
			template.setTemplateDesc(aCovenantType.getUserTemplateName());
			this.userTemplate.setObject(template);
			onFulfillUserTemplates();
		}

		if (aCovenantType.getCustomerTemplate() != null) {
			this.customerTemplate.setValue(String.valueOf(aCovenantType.getCustomerTemplateCode()));
			this.customerTemplate.setDescription(aCovenantType.getCustomerTemplateName());
			MailTemplate template = new MailTemplate();
			template.setId(aCovenantType.getCustomerTemplate());
			template.setTemplateCode(String.valueOf(aCovenantType.getCustomerTemplateCode()));
			template.setTemplateDesc(aCovenantType.getCustomerTemplateName());
			this.customerTemplate.setObject(template);
			onFulfillCustomerTemplates();
		}

		if (aCovenantType.isNewRecord()) {
			this.cmbCovenantType.setSelectedIndex(1);
			this.cbFrequency.setSelectedIndex(5);
		}
		onSelectCbFrequency();
		this.recordStatus.setValue(aCovenantType.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(CovenantType aCovenantType) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		List<WrongValueException> wve = new ArrayList<>();

		try {
			aCovenantType.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantType.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strCategory = null;
			if (this.category.getSelectedItem() != null) {
				strCategory = this.category.getSelectedItem().getValue().toString();
			}
			if (strCategory != null && !PennantConstants.List_Select.equals(strCategory)) {
				aCovenantType.setCategory(strCategory);

			} else {
				aCovenantType.setCategory(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.docType.getValidatedValue();
			Object obj = this.docType.getObject();
			if (obj != null) {
				aCovenantType.setDocType(((DocumentType) obj).getDocTypeCode());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strCovenantType = null;
			if (this.cmbCovenantType.getSelectedItem() != null) {
				strCovenantType = this.cmbCovenantType.getSelectedItem().getValue().toString();
			}
			if (strCovenantType != null && !PennantConstants.List_Select.equals(strCovenantType)) {
				aCovenantType.setCovenantType(strCovenantType);

			} else {
				aCovenantType.setCovenantType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantType.setAllowPostPonement(this.allowPostPonement.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantType.setMaxAllowedDays(this.maxAllowedDays.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantType.setAllowedPaymentModes(this.allowedPaymentModes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantType.setAlertsRequired(this.alertsRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strCategory = null;
			if (this.cbFrequency.getSelectedItem() != null) {
				strCategory = this.cbFrequency.getSelectedItem().getValue().toString();
			}
			if (strCategory != null && !PennantConstants.List_Select.equals(strCategory)) {
				aCovenantType.setFrequency(strCategory);

			} else {
				aCovenantType.setFrequency(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.graceDays.getValue() != null) {
				aCovenantType.setGraceDays(this.graceDays.getValue());
			} else {
				aCovenantType.setGraceDays(0);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.alertDays.getValue() != null) {
				aCovenantType.setAlertDays(this.alertDays.getValue());
			} else {
				aCovenantType.setAlertDays(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strAlertType = null;
			if (this.alertType.getSelectedItem() != null) {
				strAlertType = this.alertType.getSelectedItem().getValue().toString();
			}
			if (strAlertType != null && !PennantConstants.List_Select.equals(strAlertType)) {
				aCovenantType.setAlertType(strAlertType);

			} else {
				aCovenantType.setAlertType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.alertToRoles.getValue();
			Map<String, Object> roles = getSelectedValues(this.alertToRoles);
			if (roles != null) {
				if (!this.alertToRoles.getButton().isDisabled() && roles.size() > 5) {
					throw new WrongValueException(this.alertToRoles.getButton(),
							"The number of roles should not exceed more than 5.");
				}

				StringBuilder data = new StringBuilder();
				for (String role : roles.keySet()) {
					if (data.length() > 0) {
						data.append(",");
					}
					data.append(role);
				}

				aCovenantType.setAlertToRoles(data.toString());
			} else {
				aCovenantType.setAlertToRoles(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.userTemplate.getValidatedValue();
			Object obj = this.userTemplate.getObject();
			if (obj != null) {
				aCovenantType.setUserTemplate(((MailTemplate) obj).getTemplateId());
				aCovenantType.setUserTemplateCode(((MailTemplate) obj).getTemplateCode());
			} else {
				aCovenantType.setUserTemplate(null);
				aCovenantType.setUserTemplateCode(null);
				aCovenantType.setUserTemplateName(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.customerTemplate.getValidatedValue();
			Object obj = this.customerTemplate.getObject();
			if (obj != null) {
				aCovenantType.setCustomerTemplate(((MailTemplate) obj).getTemplateId());
				aCovenantType.setCustomerTemplateCode(((MailTemplate) obj).getTemplateCode());
			} else {
				aCovenantType.setCustomerTemplate(null);
				aCovenantType.setCustomerTemplateCode(null);
				aCovenantType.setCustomerTemplateName(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			setAllowedPaymentModes(aCovenantType);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setAllowedPaymentModes(CovenantType aCovenantType) {

		if (StringUtils.isEmpty(this.allowedPaymentModes.getValue())) {
			return;
		}

		if (aCovenantType.getCovenantType().equals("OTC") && !this.allowedPaymentModes.isReadonly()) {
			String[] strAllowPaymentModes = this.allowedPaymentModes.getValue().split(",");

			for (String paymentType : strAllowPaymentModes) {
				paymentType = StringUtils.trim(paymentType);

				if (!paymentType.equals(DisbursementConstants.PAYMENT_TYPE_RTGS)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_NEFT)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_IMPS)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_DD)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_CASH)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_ESCROW)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_NACH)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_IFT)
						&& !paymentType.equals(DisbursementConstants.PAYMENT_TYPE_IST)) {
					throw new WrongValueException(this.allowedPaymentModes,
							Labels.getLabel("label_CovenantTypeDialog_InavlidPaymentMethod.value"));
				}
			}

			String strAllowPayment = this.allowedPaymentModes.getValue();
			String strAllowPayments = "";
			if (strAllowPayment.endsWith(","))
				strAllowPayments = strAllowPayment.substring(0, strAllowPayment.length() - 1);
			else {
				strAllowPayments = strAllowPayment;
			}
			aCovenantType.setAllowedPaymentModes(strAllowPayments);

		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getSelectedValues(ExtendedCombobox extendedCombobox) {
		Object object = extendedCombobox.getAttribute("data");
		return (Map<String, Object>) object;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param covenantType The entity that need to be render.
	 */
	public void doShowDialog(CovenantType covenantType) {
		logger.debug(Literal.LEAVING);

		doWriteBeanToComponents(covenantType);

		if (covenantType.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.code.setFocus(true);
		} else {
			this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(covenantType.getRecordType())) {
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
			this.btnNotes.setVisible(false);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_Covenant_Code.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_Covenant_Description.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.category.isDisabled()) {
			this.category.setConstraint(new StaticListValidator(covenantCategories,
					Labels.getLabel("label_CovenantTypeDialog_Category.value")));
		}

		if (!this.cmbCovenantType.isDisabled()) {
			this.cmbCovenantType.setConstraint(new StaticListValidator(covenantCategories,
					Labels.getLabel("label_CovenantTypeDialog_Category.value")));
			this.cmbCovenantType.setConstraint(new PTListValidator<Property>(
					Labels.getLabel("label_CovenantsDialog_CovenantType.value.value"), listCovenantType, true));
		}

		if (this.alertsRequired.isChecked()) {
			if (!this.cbFrequency.isDisabled()) {
				this.cbFrequency.setConstraint(new StaticListValidator(listFrequency,
						Labels.getLabel("label_CovenantTypeDialog_Frequency.value")));
			}
			if (!this.graceDays.isReadonly() && this.graceDays.getValue() != null && this.graceDays.getValue() > 0) {
				this.graceDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_CovenantTypeDialog_GraceDays.value"), true, false, 0, 30));
			}

			if (!this.alertDays.isReadonly() && this.alertDays.getValue() != null && this.alertDays.getValue() > 0) {
				int days = 0;
				if (this.cbFrequency.getSelectedItem().getValue().toString().equals("M"))
					days = 30;
				else if (this.cbFrequency.getSelectedItem().getValue().toString().equals("Q"))
					days = 90;
				else if (this.cbFrequency.getSelectedItem().getValue().toString().equals("H"))
					days = 180;
				else if (this.cbFrequency.getSelectedItem().getValue().toString().equals("A"))
					days = 365;
				this.alertDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_CovenantTypeDialog_AlertDays.value"), true, false, 0, days));
			}
			if (!this.alertType.isDisabled() && this.alertDays.getValue() != null) {
				this.alertType.setConstraint(new StaticListValidator(listAlertType,
						Labels.getLabel("label_CovenantTypeDialog_AlertType.value")));
			}

			if (!this.alertToRoles.getButton().isDisabled()) {
				this.alertToRoles.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CovenantTypeDialog_AlertToRoles.value"), null, false));
			}

			if (!this.userTemplate.isReadonly()
					&& ("User".equals(alertType.getValue()) || "Both".equals(alertType.getValue()))) {
				this.userTemplate.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CovenantTypeDialog_UserTemplate.value"), null, true, true));
			}

			if (!this.alertToRoles.getButton().isDisabled()
					&& ("User".equals(alertType.getValue()) || "Both".equals(alertType.getValue()))) {
				this.alertToRoles.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CovenantTypeDialog_AlertToRoles.value"), null, true, true));
			}

			if (!this.customerTemplate.isReadonly()
					&& ("Customer".equals(alertType.getValue()) || "Both".equals(alertType.getValue()))) {
				this.customerTemplate.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CovenantTypeDialog_CustomerTemplate.value"), null, true, false));
			}
		}

		if (!this.maxAllowedDays.isDisabled()) {
			this.maxAllowedDays.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_CovenantTypeDialog_AllowPostPonement.value"), true, false, 0));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.category.setConstraint("");
		this.docType.setConstraint("");
		this.cbFrequency.setConstraint("");
		this.graceDays.setConstraint("");
		this.alertDays.setConstraint("");
		this.alertType.setConstraint("");
		this.alertToRoles.setConstraint("");
		this.userTemplate.setConstraint("");
		this.customerTemplate.setConstraint("");
		this.allowedPaymentModes.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CovenantType aCovenantType = new CovenantType();
		BeanUtils.copyProperties(this.covenantType, aCovenantType);

		doDelete(String.valueOf(aCovenantType.getId()), aCovenantType);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.covenantType.isNewRecord()) {
			this.code.setDisabled(false);
			this.category.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.code.setDisabled(true);
			this.category.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("CovenantTypeDialog_CovenantType"), this.cmbCovenantType);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_DocType"), this.docType);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_AllowPostPonement"), this.allowPostPonement);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_MaxAllowedDays"), this.maxAllowedDays);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_AllowedPaymentModes"), this.allowedPaymentModes);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_AlertsRequired"), this.alertsRequired);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_Frequency"), this.cbFrequency);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_GraceDays"), this.graceDays);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_AlertDays"), this.alertDays);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_AlertType"), this.alertType);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_AlertToRoles"), this.alertToRoles.getButton());
		readOnlyComponent(isReadOnly("CovenantTypeDialog_UserTemplate"), this.userTemplate);
		readOnlyComponent(isReadOnly("CovenantTypeDialog_CustomerTemplate"), this.customerTemplate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.covenantType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.category);
		readOnlyComponent(true, this.docType);
		readOnlyComponent(true, this.maxAllowedDays);
		readOnlyComponent(true, this.allowedPaymentModes);
		readOnlyComponent(true, this.allowPostPonement);
		readOnlyComponent(true, this.alertsRequired);
		readOnlyComponent(true, this.cbFrequency);
		readOnlyComponent(true, this.graceDays);
		readOnlyComponent(true, this.alertDays);
		readOnlyComponent(true, this.alertType);
		readOnlyComponent(true, this.alertToRoles.getButton());
		readOnlyComponent(true, this.userTemplate);
		readOnlyComponent(true, this.customerTemplate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.code.setValue("");
		this.description.setValue("");
		this.category.setSelectedIndex(0);
		this.docType.setValue("");
		this.docType.setDescription("");
		this.maxAllowedDays.setText("");
		this.allowedPaymentModes.setText("");
		this.allowPostPonement.setChecked(false);
		this.alertsRequired.setChecked(false);
		this.cbFrequency.setSelectedIndex(0);
		this.graceDays.setValue(0);
		this.alertDays.setValue(0);
		this.alertType.setSelectedIndex(0);
		this.alertToRoles.setValue("");
		this.alertToRoles.setDescription("");
		this.userTemplate.setValue("");
		this.userTemplate.setDescription("");
		this.customerTemplate.setValue("");
		this.customerTemplate.setDescription("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final CovenantType aCovenantType = new CovenantType();
		BeanUtils.copyProperties(this.covenantType, aCovenantType);

		doSetValidation();
		doWriteComponentsToBean(aCovenantType);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCovenantType.getRecordType())) {
				aCovenantType.setVersion(aCovenantType.getVersion() + 1);
				if (aCovenantType.isNewRecord()) {
					aCovenantType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCovenantType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCovenantType.setNewRecord(true);
				}
			}
		} else {
			aCovenantType.setVersion(aCovenantType.getVersion() + 1);
			if (aCovenantType.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aCovenantType, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final Exception e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
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
	protected boolean doProcess(CovenantType aCovenantType, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCovenantType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCovenantType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCovenantType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCovenantType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCovenantType);
				}

				if (isNotesMandatory(taskId, aCovenantType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aCovenantType.setTaskId(taskId);
			aCovenantType.setNextTaskId(nextTaskId);
			aCovenantType.setRoleCode(getRole());
			aCovenantType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCovenantType, tranType);
			String operationRefs = getServiceOperations(taskId, aCovenantType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCovenantType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCovenantType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CovenantType aCovenantType = (CovenantType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = covenantTypeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = covenantTypeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = covenantTypeService.doApprove(auditHeader);

					if (aCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = covenantTypeService.doReject(auditHeader);
					if (aCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CovenantTypeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_CovenantTypeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.covenantType), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CovenantType aCovenantType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCovenantType.getBefImage(), aCovenantType);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aCovenantType.getUserDetails(),
				getOverideMap());
	}

	public void setCovenantTypeService(CovenantTypeService covenantTypeService) {
		this.covenantTypeService = covenantTypeService;
	}

	public void onCheck$alertsRequired(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckAlertsRequired();
		logger.debug(Literal.LEAVING);
	}

	private void onCheckAlertsRequired() {

		if (this.alertsRequired.isChecked()) {
			this.graceDays.setDisabled(false);
			this.alertDays.setDisabled(false);
			this.alertType.setDisabled(false);
		} else {
			this.graceDays.setDisabled(true);
			this.graceDays.setValue(0);
			this.alertDays.setDisabled(true);
			this.alertDays.setValue(0);
			this.alertType.setDisabled(true);
			this.alertType.setSelectedIndex(0);
			this.alertToRoles.setButtonDisabled(true);
			this.alertToRoles.setValue("");
			this.alertToRoles.setDescription("");
			this.userTemplate.setButtonDisabled(true);
			this.customerTemplate.setButtonDisabled(true);
			this.userTemplate.setValue("");
			this.userTemplate.setDescription("");
			this.customerTemplate.setValue("");
			this.customerTemplate.setDescription("");
		}
	}

	public void onSelect$alertType(Event event) {
		logger.debug(Literal.ENTERING);
		onSelectAlertType();
		logger.debug(Literal.LEAVING);
	}

	private void onSelectAlertType() {

		if (alertType.isDisabled()) {
			return;
		}

		if (this.alertType.getSelectedIndex() == 1) {
			this.userTemplate.setButtonDisabled(false);
			this.customerTemplate.setButtonDisabled(true);
			this.alertToRoles.setButtonDisabled(false);
			this.customerTemplate.setValue(null);
			this.customerTemplate.setDescription(null);
			this.customerTemplate.setObject(null);
		} else if (this.alertType.getSelectedIndex() == 2) {
			this.userTemplate.setButtonDisabled(true);
			this.customerTemplate.setButtonDisabled(false);
			this.alertToRoles.setButtonDisabled(true);
			this.alertToRoles.setValue(null);
			this.alertToRoles.setDescription(null);
			this.userTemplate.setValue(null);
			this.userTemplate.setDescription(null);
			this.userTemplate.setObject(null);
		} else if (this.alertType.getSelectedIndex() == 3) {
			this.userTemplate.setButtonDisabled(false);
			this.customerTemplate.setButtonDisabled(false);
			this.alertToRoles.setButtonDisabled(false);
		} else {
			this.userTemplate.setButtonDisabled(true);
			this.customerTemplate.setButtonDisabled(true);
			this.alertToRoles.setButtonDisabled(true);
			this.alertToRoles.setValue(null);
			this.alertToRoles.setDescription(null);
			this.userTemplate.setValue("");
			this.userTemplate.setDescription("");
			this.customerTemplate.setValue("");
			this.customerTemplate.setDescription("");
			this.customerTemplate.setId("");
			this.userTemplate.setId("");
			this.customerTemplate.setObject(null);
			this.userTemplate.setObject(null);
		}
	}

	public void onFulfill$docType(Event event) {
		logger.debug(Literal.ENTERING);
		onFulfillDocType();
		logger.debug(Literal.LEAVING);
	}

	private void onFulfillDocType() {
		Object dataObject = this.docType.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.docType.setValue("");
			this.docType.setDescription("");
		} else {
			DocumentType docType = (DocumentType) dataObject;
			this.docType.setValue(docType.getDocTypeCode());
			this.docType.setDescription(docType.getDocTypeDesc());
		}
	}

	public void onFulfill$userTemplate(Event event) {
		logger.debug(Literal.ENTERING);
		onFulfillUserTemplates();
		logger.debug(Literal.LEAVING);
	}

	private void onFulfillUserTemplates() {
		Object dataObject = this.userTemplate.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.userTemplate.setValue("");
			this.userTemplate.setDescription("");
		} else {
			MailTemplate template = (MailTemplate) dataObject;
			this.userTemplate.setValue(String.valueOf(template.getTemplateCode()));
			this.userTemplate.setDescription(template.getTemplateDesc());
		}
	}

	public void onFulfill$customerTemplate(Event event) {
		logger.debug(Literal.ENTERING);
		onFulfillCustomerTemplates();
		logger.debug(Literal.LEAVING);
	}

	private void onFulfillCustomerTemplates() {
		Object dataObject = this.customerTemplate.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.customerTemplate.setValue("");
			this.customerTemplate.setDescription("");
		} else {
			MailTemplate template = (MailTemplate) dataObject;
			this.customerTemplate.setValue(String.valueOf(template.getTemplateCode()));
			this.customerTemplate.setDescription(template.getTemplateDesc());
		}
	}

	public void onSelect$cmbCovenantType(Event event) {
		logger.debug(Literal.ENTERING);
		onSelectCovenantType();
		logger.debug(Literal.LEAVING);
	}

	public void onSelectCovenantType() {
		String selectedCovenantType = this.cmbCovenantType.getSelectedItem().getValue();

		if ("OTC".equals(selectedCovenantType) || PennantConstants.List_Select.equals(selectedCovenantType)) {
			this.allowPostPonement.setDisabled(true);
			this.allowPostPonement.setChecked(false);

			this.maxAllowedDays.setDisabled(true);
			this.maxAllowedDays.setValue(0);

			this.allowedPaymentModes.setDisabled(true);
			this.allowedPaymentModes.setValue("");
		}

		if ("LOS".equals(selectedCovenantType)) {
			this.allowPostPonement.setDisabled(false);
			this.maxAllowedDays.setDisabled(true);
		}

		if ("OTC".equals(selectedCovenantType)) {
			this.allowedPaymentModes.setDisabled(false);
		}

		if ("PDD".equals(selectedCovenantType)) {
			this.allowPostPonement.setDisabled(false);

			this.maxAllowedDays.setDisabled(false);

			this.allowedPaymentModes.setDisabled(true);
			this.allowedPaymentModes.setValue("");
		}

	}

	public void onSelect$cbFrequency(Event event) {
		logger.debug(Literal.ENTERING);
		onSelectCbFrequency();
		logger.debug(Literal.LEAVING);
	}

	public void onSelectCbFrequency() {
		if (cbFrequency.isDisabled()) {
			return;
		}

		if (this.cbFrequency.getSelectedIndex() != 0) {
			this.alertsRequired.setDisabled(false);
		} else {
			this.alertsRequired.setDisabled(true);
			this.alertsRequired.setChecked(false);
		}
		onCheckAlertsRequired();
	}
}
