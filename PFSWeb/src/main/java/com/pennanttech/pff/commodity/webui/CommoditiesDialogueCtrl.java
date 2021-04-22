package com.pennanttech.pff.commodity.webui;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.commodity.service.CommoditiesService;
import com.pennanttech.pff.staticlist.AppStaticList;

public class CommoditiesDialogueCtrl extends GFCBaseCtrl<Commodity> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CommodityTypeDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CommoditiesDialogue;

	protected ExtendedCombobox type;
	protected Uppercasebox code;
	protected Textbox description;
	protected CurrencyBox currentValue;
	protected Uppercasebox hsnCode;
	protected Checkbox active;
	protected Commodity commodity;
	protected Checkbox alertsRequired;
	protected Combobox alertType;
	protected ExtendedCombobox alertToRoles;
	protected ExtendedCombobox userTemplate;
	protected ExtendedCombobox customerTemplate;

	private transient CommoditiesListCtrl commoditiesListCtrl;
	private transient CommoditiesService commoditiesService;

	private transient List<Property> listAlertType = AppStaticList.getAlertsFor();

	private CommodityType commodityTypeObject;

	/**
	 * default constructor.<br>
	 */
	public CommoditiesDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommoditiesDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.commodity.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_CommoditiesDialogue(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CommoditiesDialogue);

		try {
			this.commodity = (Commodity) arguments.get("commodity");
			this.commoditiesListCtrl = (CommoditiesListCtrl) arguments.get("commoditiesListCtrl");

			if (this.commodity == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			Commodity acommodities = new Commodity();
			BeanUtils.copyProperties(this.commodity, acommodities);
			this.commodity.setBefImage(acommodities);

			doLoadWorkFlow(this.commodity.isWorkflow(), this.commodity.getWorkflowId(), this.commodity.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CommoditiesDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.commodity);
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

		setStatusDetails();

		this.code.setMaxlength(8);
		this.hsnCode.setMaxlength(20);

		this.type.setMandatoryStyle(true);
		this.type.setModuleName("CommodityType");
		this.type.setValueColumn("Id");
		this.type.setDescColumn("Description");
		this.type.setValueType(DataType.LONG);
		this.type.setWidth("300dpx");
		this.type.setValidateColumns(new String[] { "Id", "Code", "Description" });

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
		userTemplateFilter[1] = new Filter("TemplateCode", "LAS%", Filter.OP_LIKE);
		userTemplate.setFilters(userTemplateFilter);

		this.customerTemplate.setModuleName("MailTemplate");
		this.customerTemplate.setValueColumn("TemplateCode");
		this.customerTemplate.setDescColumn("TemplateDesc");
		this.customerTemplate.setValidateColumns(new String[] { "TemplateCode", "TemplateDesc" });
		Filter[] custTemplateFilter = new Filter[2];
		custTemplateFilter[0] = new Filter("TemplateFor", NotificationConstants.TEMPLATE_FOR_CN);
		custTemplateFilter[1] = new Filter("TemplateCode", "LAS%", Filter.OP_LIKE);
		customerTemplate.setFilters(custTemplateFilter);

		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		this.currentValue.setProperties(true, formatter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.commodity);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);

		commoditiesListCtrl.search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.commodity);
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
	public void doWriteBeanToComponents(Commodity acommodity) {
		logger.debug(Literal.ENTERING);

		this.type.setValue(acommodity.getCommodityTypeCode());
		this.type.setDescription(acommodity.getDescription());
		CommodityType commodityType = new CommodityType();
		commodityType.setCode(acommodity.getCode());
		commodityType.setDescription(acommodity.getDescription());
		commodityType.setId(acommodity.getCommodityType());
		this.type.setObject(commodityType);
		onChangeCommodityType();

		this.code.setText(acommodity.getCode());
		this.hsnCode.setText(acommodity.getHSNCode());
		this.currentValue.setValue(PennantApplicationUtil.formateAmount(acommodity.getCurrentValue(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));

		this.description.setText(acommodity.getDescription());
		this.active.setChecked(acommodity.isActive());
		if (acommodity.isNew() || (acommodity.getRecordType() != null ? acommodity.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		fillList(this.alertType, listAlertType, acommodity.getAlertType());
		this.alertsRequired.setChecked(acommodity.isAlertsRequired());

		onCheckAlertsRequired();

		onSelectAlertType();

		if (acommodity.getAlertToRoles() != null) {
			Map<String, Object> map = new HashMap<>();
			StringBuilder roles = new StringBuilder();
			for (String role : acommodity.getAlertToRoles().split(",")) {
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

		if (acommodity.getUserTemplate() != null) {
			this.userTemplate.setValue(String.valueOf(acommodity.getUserTemplateCode()));
			this.userTemplate.setDescription(acommodity.getUserTemplateName());
			MailTemplate template = new MailTemplate();
			template.setId(acommodity.getUserTemplate());
			template.setTemplateCode(String.valueOf(acommodity.getUserTemplateCode()));
			template.setTemplateDesc(acommodity.getUserTemplateName());
			this.userTemplate.setObject(template);
			onFulfillUserTemplates();
		}

		if (acommodity.getCustomerTemplate() != null) {
			this.customerTemplate.setValue(String.valueOf(acommodity.getCustomerTemplateCode()));
			this.customerTemplate.setDescription(acommodity.getCustomerTemplateName());
			MailTemplate template = new MailTemplate();
			template.setId(acommodity.getCustomerTemplate());
			template.setTemplateCode(String.valueOf(acommodity.getCustomerTemplateCode()));
			template.setTemplateDesc(acommodity.getCustomerTemplateName());
			this.customerTemplate.setObject(template);
			onFulfillCustomerTemplates();
		}

		this.recordStatus.setValue(acommodity.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	private void onCheckAlertsRequired() {

		if (this.alertsRequired.isChecked()) {
			this.alertType.setSelectedIndex(1);
			this.alertType.setDisabled(true);
			this.alertToRoles.setButtonDisabled(false);
			this.userTemplate.setButtonDisabled(false);
		} else {
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

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(Commodity acommodity) {
		logger.debug(Literal.LEAVING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			this.type.getValidatedValue();
			Object obj = this.type.getObject();
			if (obj != null) {
				acommodity.setCommodityType(((CommodityType) obj).getId());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodity.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodity.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.currentValue.isReadonly()) {
				if (this.currentValue.getActualValue().compareTo(BigDecimal.ZERO) == 0
						|| this.currentValue.getActualValue().compareTo(new BigDecimal("0.00")) == 0) {
					throw new WrongValueException(this.currentValue,
							Labels.getLabel("label_CommoditiesDialogue_CurrentValue.value")
									+ " should be greater than 0");
				}
			}

			acommodity.setCurrentValue(PennantApplicationUtil.unFormateAmount(this.currentValue.getActualValue(),
					CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodity.setHSNCode(this.hsnCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodity.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodity.setAlertsRequired(this.alertsRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String strAlertType = null;
			if (this.alertType.getSelectedItem() != null) {
				strAlertType = this.alertType.getSelectedItem().getValue().toString();
			}
			if (strAlertType != null && !PennantConstants.List_Select.equals(strAlertType)) {
				acommodity.setAlertType(strAlertType);

			} else {
				acommodity.setAlertType(null);
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

				StringBuffer data = new StringBuffer();
				for (String role : roles.keySet()) {
					if (data.length() > 0) {
						data.append(",");
					}
					data.append(role);
				}

				acommodity.setAlertToRoles(data.toString());
			} else {
				acommodity.setAlertToRoles(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.userTemplate.getValidatedValue();
			Object obj = this.userTemplate.getObject();
			if (obj != null) {
				acommodity.setUserTemplate(((MailTemplate) obj).getTemplateId());
				acommodity.setUserTemplateCode(((MailTemplate) obj).getTemplateCode());
			} else {
				acommodity.setUserTemplate(null);
				acommodity.setUserTemplateCode(null);
				acommodity.setUserTemplateName(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.customerTemplate.getValidatedValue();
			Object obj = this.customerTemplate.getObject();
			if (obj != null) {
				acommodity.setCustomerTemplate(((MailTemplate) obj).getTemplateId());
				acommodity.setCustomerTemplateCode(((MailTemplate) obj).getTemplateCode());
			} else {
				acommodity.setCustomerTemplate(null);
				acommodity.setCustomerTemplateCode(null);
				acommodity.setCustomerTemplateName(null);
			}
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

	@SuppressWarnings("unchecked")
	private Map<String, Object> getSelectedValues(ExtendedCombobox extendedCombobox) {
		Object object = extendedCombobox.getAttribute("data");
		return (Map<String, Object>) object;
	}

	public void onCheck$alertsRequired(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckAlertsRequired();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param covenantType
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Commodity commodity) {
		logger.debug(Literal.LEAVING);

		if (commodity.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.code.setFocus(true);
		} else {
			this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(commodity.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(commodity);

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

		BigDecimal bdcurrentValue = this.currentValue.getActualValue();

		if (!this.type.getButton().isDisabled()) {
			this.type.setConstraint(
					new PTStringValidator(Labels.getLabel("label_StockCompanyDialogue_CompanyCode.value"), null, true));
		}

		if (!this.code.isDisabled()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommoditiesDialogue_CommodityCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		if (!this.alertToRoles.getButton().isDisabled()) {
			this.alertToRoles.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommoditiesDialog_AlertToRoles.value"), null, true));
		}

		if (!this.userTemplate.isReadonly()
				&& ("User".equals(alertType.getValue()) || "Both".equals(alertType.getValue()))) {
			this.userTemplate.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CommoditiesDialog_UserTemplate.value"), null, true, true));
		}

		if (!this.alertToRoles.getButton().isDisabled()
				&& ("User".equals(alertType.getValue()) || "Both".equals(alertType.getValue()))) {
			this.alertToRoles.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CommoditiesDialog_AlertToRoles.value"), null, true, true));
		}

		if (!this.customerTemplate.isReadonly()
				&& ("Customer".equals(alertType.getValue()) || "Both".equals(alertType.getValue()))) {
			this.customerTemplate.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CommoditiesDialog_CustomerTemplate.value"), null, true, false));
		}

		if (!this.hsnCode.isReadonly()) {
			this.hsnCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommoditiesDialogue_HSNCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.type.setConstraint("");
		this.description.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

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

	/**
	 * Deletes a CovenantType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final Commodity acommodity = new Commodity();
		BeanUtils.copyProperties(this.commodity, acommodity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ acommodity.getCommodityType();
		if (MessageUtil.confirm(msg) != MessageUtil.YES) {
			return;
		}

		if (StringUtils.trimToEmpty(acommodity.getRecordType()).equals("")) {
			acommodity.setVersion(acommodity.getVersion() + 1);
			acommodity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (isWorkFlowEnabled()) {
				acommodity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				acommodity.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), acommodity.getNextTaskId(), acommodity);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (doProcess(acommodity, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.commodity.isNewRecord()) {
			this.type.setButtonDisabled(false);
			this.code.setDisabled(false);
			this.alertType.setDisabled(true);
			this.customerTemplate.setReadonly(true);
		} else {
			this.type.setButtonDisabled(true);
			this.code.setDisabled(true);
			this.alertType.setDisabled(true);
			this.customerTemplate.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("Commodities_Description"), this.description);
		readOnlyComponent(isReadOnly("Commodities_CurrentValue"), this.currentValue);
		readOnlyComponent(isReadOnly("Commodities_HSNCode"), this.hsnCode);
		readOnlyComponent(isReadOnly("Commodities_Active"), this.active);
		readOnlyComponent(isReadOnly("Commodities_AlertsRequired"), this.alertsRequired);
		readOnlyComponent(isReadOnly("Commodities_AlertToRoles"), this.alertToRoles);
		readOnlyComponent(isReadOnly("Commodities_UserTemplate"), this.userTemplate);

		if (this.commodity != null
				&& StringUtils.equals(this.commodity.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
			readOnlyComponent(true, this.description);
			readOnlyComponent(true, this.currentValue);
			readOnlyComponent(true, this.hsnCode);
			readOnlyComponent(true, this.active);
			readOnlyComponent(true, this.alertsRequired);
			readOnlyComponent(true, this.alertToRoles);
			readOnlyComponent(true, this.userTemplate);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.commodity.isNewRecord()) {
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

		readOnlyComponent(true, this.type);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.active);

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

		this.type.setValue("");
		this.description.setValue("");
		this.active.setChecked(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final Commodity acommodity = new Commodity();
		BeanUtils.copyProperties(this.commodity, acommodity);

		doSetValidation();
		doWriteComponentsToBean(acommodity);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(acommodity.getRecordType())) {
				acommodity.setVersion(acommodity.getVersion() + 1);
				if (acommodity.isNew()) {
					acommodity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					acommodity.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					acommodity.setNewRecord(true);
				}
			}
		} else {
			acommodity.setVersion(acommodity.getVersion() + 1);
			if (acommodity.isNew()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(acommodity, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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
	private boolean doProcess(Commodity acommodity, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		acommodity.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		acommodity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		acommodity.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			acommodity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(acommodity.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, acommodity);
				}
				if (isNotesMandatory(taskId, acommodity)) {
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
			acommodity.setTaskId(taskId);
			acommodity.setNextTaskId(nextTaskId);
			acommodity.setRoleCode(getRole());
			acommodity.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(acommodity, tranType);
			String operationRefs = getServiceOperations(taskId, acommodity);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(acommodity, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(acommodity, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);

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
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Commodity aCovenantType = (Commodity) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = commoditiesService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = commoditiesService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = commoditiesService.doApprove(auditHeader);
						if (aCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = commoditiesService.doReject(auditHeader);
						if (aCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommoditiesDialogue, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_CommoditiesDialogue, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.commodity), true);
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

		logger.debug(Literal.LEAVING);

		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CommodityType aStockCompany, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStockCompany.getBefImage(), aStockCompany);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aStockCompany.getUserDetails(),
				getOverideMap());
	}

	public void onFulfill$type(Event event) {
		logger.debug(Literal.ENTERING);

		onChangeCommodityType();

		logger.debug(Literal.LEAVING);
	}

	public void onChangeCommodityType() {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(this.type.getValue())) {
			clearOnChangeCommodityData();
			return;
		}

		commodityTypeObject = (CommodityType) this.type.getObject();
		if (commodityTypeObject == null) {
			return;
		}

		Search search = new Search(CommodityType.class);
		search.addFilterEqual("Id", commodityTypeObject.getId());

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		clearOnChangeCommodityData();
		if (searchProcessor.getResults(search).size() > 0) {
			commodityTypeObject = (CommodityType) searchProcessor.getResults(search).get(0);
			commodityTypeDetails(commodityTypeObject);
		}

		logger.debug(Literal.LEAVING);
	}

	public void clearOnChangeCommodityData() {
		logger.debug(Literal.ENTERING);

		this.type.setValue("");

		logger.debug(Literal.LEAVING);
	}

	public void commodityTypeDetails(CommodityType commodityType) {
		logger.debug(Literal.ENTERING);

		this.type.setValue(commodityType.getCode());
		this.type.setDescription(commodityType.getDescription());

		logger.debug(Literal.LEAVING);
	}

	public void setCommoditiesService(CommoditiesService commoditiesService) {
		this.commoditiesService = commoditiesService;
	}

	public void onFulfill$alertToRoles(Event event) {
		logger.debug(Literal.ENTERING);
		this.alertToRoles.setTooltiptext(this.alertToRoles.getValue());
		logger.debug(Literal.LEAVING);
	}

}
