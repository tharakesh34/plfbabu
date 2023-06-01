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
 * * FileName : MailTemplateDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2012 * *
 * Modified Date : 04-10-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.mail.mailtemplate;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.TemplateFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.PTCKeditor;
import com.pennant.pff.template.TemplateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * This is the controller class for the /WEB-INF/pages/Mail/MailTemplate/mailTemplateDialog.zul file.
 */
public class MailTemplateDialogCtrl extends GFCBaseCtrl<MailTemplate> {
	private static final long serialVersionUID = 4140622258920094017L;
	private static final Logger logger = LogManager.getLogger(MailTemplateDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_MailTemplateDialog; // autowired

	protected Tab basicDetailsTab; // autowired
	protected Tab emailDetailsTab; // autowired

	protected Textbox templateCode; // autowired
	// protected Textbox templateDesc; // autowired

	protected Textbox templateDesc;
	protected Intbox turnAroundTime; // autowired
	protected Checkbox templateRepeat; // autowired
	protected Checkbox templateForSMS; // autowired
	protected Textbox smsContent; // autowired
	protected Checkbox templateForEmail; // autowired
	protected Checkbox active; // autowired
	protected Combobox templateFor; // autowired
	protected Combobox templateModule; // autowired
	protected Combobox templateEvent;

	protected Combobox emailFormat; // autowired
	protected Textbox userIds; // autowired
	protected Codemirror emailSubject; // autowired
	protected Div divHtmlArtifact; // autowired
	protected PTCKeditor htmlArtifact; // autowired
	protected Textbox plainText; // autowired
	protected Listbox templateData; // autowired
	protected Listbox templateData1; // autowired
	protected Space Space_htmlArtifact;
	protected Space Space_emailSubject;
	protected Row row_turnAroundTime; // autowired
	protected Row row_templateRepeat; // autowired
	protected Row row_SMSContent; // autowired
	protected Row row_EmailFormat; // autowired
	protected Row row_EmailSendTo; // autowired
	protected Row row_TemplateEvent; // autowired
	protected MailTemplate aMailTemplate;
	protected Textbox lovDescUserNames; // autowired
	protected Button btnUserIds; // autowired
	private boolean enqModule = false;

	// not auto wired vars
	private MailTemplate mailTemplate; // overhanded per param
	private transient MailTemplateListCtrl mailTemplateListCtrl; // overhanded
																	// per param

	private transient boolean validationOn;

	protected Button btnSimulate;

	// ServiceDAOs / Domain Classes
	private transient MailTemplateService mailTemplateService;
	private transient PagedListService pagedListService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<>();
	private List<ValueLabel> listEmailFormat = TemplateUtil.getFormats(); // autowired
	private List<ValueLabel> listTemplateFor = TemplateUtil.getTemplatesFor();
	private List<ValueLabel> mailTeplateModulesList = TemplateUtil.getModules();
	private List<ValueLabel> templateEvents = TemplateUtil.getEvents();
	private Map<String, String> filedValues = new HashMap<>();
	private Map<String, String> filedDesc = new HashMap<>();

	/**
	 * default constructor.<br>
	 */
	public MailTemplateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MailTemplateDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected MailTemplate object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_MailTemplateDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_MailTemplateDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("mailTemplate")) {
				this.mailTemplate = (MailTemplate) arguments.get("mailTemplate");
				MailTemplate befImage = new MailTemplate();
				BeanUtils.copyProperties(this.mailTemplate, befImage);
				this.mailTemplate.setBefImage(befImage);
				setMailTemplate(this.mailTemplate);
			} else {
				setMailTemplate(null);
			}

			// READ OVERHANDED params !
			// we get the mailTemplateListWindow controller. So we have access to it and can synchronize the shown data
			// when we do insert, edit or delete mailTemplate here.
			if (arguments.containsKey("mailTemplateListCtrl")) {
				setMailTemplateListCtrl((MailTemplateListCtrl) arguments.get("mailTemplateListCtrl"));
			} else {
				setMailTemplateListCtrl(null);
			}

			doLoadWorkFlow(this.mailTemplate.isWorkflow(), this.mailTemplate.getWorkflowId(),
					this.mailTemplate.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "MailTemplateDialog");
			}

			getBorderLayoutHeight();
			this.htmlArtifact.setHeight(borderLayoutHeight - 170 + "px");
			// this.emailSubject.setHeight(borderLayoutHeight-270+"px");
			this.templateData.setHeight(borderLayoutHeight - 230 + "px");

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMailTemplate());
			this.btnDelete.setVisible(false);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_MailTemplateDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnUserIds(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		String userIds = "";
		String userNames = "";
		@SuppressWarnings("unchecked")
		List<Object> selectedValues = (List<Object>) MultiSelectionSearchListBox.show(this.window_MailTemplateDialog,
				"SecurityUser", String.valueOf(this.userIds.getValue()), new Filter[] {});
		if (selectedValues != null) {
			for (int i = 0; i < selectedValues.size(); i++) {
				SecurityUser selectedValue = (SecurityUser) selectedValues.get(i);
				userIds = userIds + selectedValue.getUsrID() + ",";
				userNames = userNames + selectedValue.getUsrLogin() + ",";
				if (i == selectedValues.size() - 1) {
					userIds = userIds.substring(0, userIds.lastIndexOf(','));
					userNames = userNames.substring(0, userNames.lastIndexOf(','));
				}
			}
			this.userIds.setValue(userIds);
			this.lovDescUserNames.setValue(userNames);
			this.lovDescUserNames.setTooltiptext(userNames);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		// this.emailSubject.setMaxlength(100);
		this.templateCode.setMaxlength(100);
		this.smsContent.setMaxlength(SysParamUtil.getValueAsInt("SMS_LEN"));

		if (isWorkFlowEnabled()) {
			if (enqModule) {
				groupboxWf.setVisible(false);
			}
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
		getUserWorkspace().allocateAuthorities("MailTemplateDialog", getRole());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnSave"));
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws UnsupportedEncodingException
	 */
	public void onClick$btnSave(Event event) throws UnsupportedEncodingException {
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
		MessageUtil.showHelpWindow(event, window_MailTemplateDialog);
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.mailTemplate.getBefImage());
		doReadOnly(true);
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMailTemplate MailTemplate
	 */
	public void doWriteBeanToComponents(MailTemplate aMailTemplate) {
		logger.debug("Entering");

		this.templateCode.setValue(aMailTemplate.getTemplateCode());
		this.templateDesc.setValue(aMailTemplate.getTemplateDesc());

		if (NotificationConstants.TEMPLATE_FOR_AE.equals(getMailTemplate().getTemplateFor())) {
			this.row_turnAroundTime.setVisible(false);
			this.turnAroundTime.setValue(aMailTemplate.getTurnAroundTime());
			this.row_templateRepeat.setVisible(false);
			this.templateRepeat.setChecked(aMailTemplate.isRepeat());
		}

		this.templateForSMS.setChecked(aMailTemplate.isSmsTemplate());
		this.smsContent.setValue(aMailTemplate.getSmsContent());
		loadSMSFields();

		this.templateForEmail.setChecked(aMailTemplate.isEmailTemplate());
		aMailTemplate.setEmailFormat("H");
		fillComboBox(this.emailFormat, aMailTemplate.getEmailFormat(), listEmailFormat, "");

		fillComboBox(this.templateModule, aMailTemplate.getModule(), mailTeplateModulesList, "");

		if (StringUtils.equals(aMailTemplate.getModule(), NotificationConstants.MAIL_MODULE_FIN)) {
			this.row_TemplateEvent.setVisible(true);
		}
		fillComboBox(this.templateEvent, aMailTemplate.getEvent(), templateEvents, "");

		if (!aMailTemplate.isNewRecord()
				&& StringUtils.equals(aMailTemplate.getModule(), NotificationConstants.MAIL_MODULE_FIN)
				|| StringUtils.equals(aMailTemplate.getModule(), NotificationConstants.MAIL_MODULE_PROVIDER)) {
			if (StringUtils.equals(aMailTemplate.getModule(), NotificationConstants.MAIL_MODULE_FIN)) {

				fillComboBox(this.templateFor, aMailTemplate.getTemplateFor(), listTemplateFor, ",PN,");

			} else if (StringUtils.equals(aMailTemplate.getModule(), NotificationConstants.MAIL_MODULE_PROVIDER)) {

				fillComboBox(this.templateFor, aMailTemplate.getTemplateFor(), listTemplateFor, ",SP,DN,AE,CN,");
			}
		} else {
			fillComboBox(this.templateFor, StringUtils.trimToEmpty(aMailTemplate.getTemplateFor()), listTemplateFor,
					"");
		}

		if (aMailTemplate.isEmailTemplate()) {
			this.emailDetailsTab.setDisabled(false);
			if (NotificationConstants.TEMPLATE_FOR_AE.equals(getMailTemplate().getTemplateFor())) {
				this.row_EmailSendTo.setVisible(false);
				this.userIds.setValue("");
				this.lovDescUserNames.setValue("");
			}
			this.emailSubject.setValue(aMailTemplate.getEmailSubject());

			doSetArtifact(aMailTemplate.getEmailFormat());
			String type = aMailTemplate.getEmailFormat();
			if (type != null && aMailTemplate.getEmailContent() != null) {
				try {
					if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(type)) {
						this.htmlArtifact.setValue(
								new String(aMailTemplate.getEmailContent(), NotificationConstants.DEFAULT_CHARSET));
						this.divHtmlArtifact.appendChild(new Html(
								new String(aMailTemplate.getEmailContent(), NotificationConstants.DEFAULT_CHARSET)));
						doFillTemplateFields(aMailTemplate.getModule(), this.templateData, aMailTemplate.getEvent());
						doFillTemplateFields(aMailTemplate.getModule(), this.templateData1, aMailTemplate.getEvent());

					} else if (NotificationConstants.TEMPLATE_FORMAT_PLAIN.equals(type)) {
						this.plainText.setValue(
								new String(aMailTemplate.getEmailContent(), NotificationConstants.DEFAULT_CHARSET));
					}
				} catch (Exception e) {
					logger.error("Exception:", e);
				}
			}
		}

		this.active.setChecked(aMailTemplate.isActive());
		if (aMailTemplate.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(aMailTemplate.getRecordType())) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		this.recordStatus.setValue(aMailTemplate.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMailTemplate
	 * @throws UnsupportedEncodingException
	 */
	public void doWriteComponentsToBean(MailTemplate aMailTemplate) throws UnsupportedEncodingException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Basic Details Tab
		// Template Code
		try {
			aMailTemplate.setTemplateCode(this.templateCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template Desc
		try {
			aMailTemplate.setTemplateDesc(this.templateDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template For
		try {
			if (this.templateFor.isVisible()) {
				if ("#".equals(this.templateFor.getSelectedItem().getValue().toString())) {
					throw new WrongValueException(this.templateFor, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("label_MailTemplateDialog_templateFor.value") }));
				} else {
					aMailTemplate.setTemplateFor(this.templateFor.getSelectedItem().getValue().toString());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template Module
		try {
			if (this.templateModule.isVisible()) {
				if ("#".equals(this.templateModule.getSelectedItem().getValue().toString())) {
					throw new WrongValueException(this.templateModule, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("label_MailTemplateDialog_templateModule.value") }));
				} else {
					aMailTemplate.setModule(this.templateModule.getSelectedItem().getValue().toString());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template Event
		try {
			if (this.row_TemplateEvent.isVisible()) {
				if (!this.templateEvent.isDisabled()
						&& "#".equals(this.templateEvent.getSelectedItem().getValue().toString())) {
					throw new WrongValueException(this.templateModule, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("label_MailTemplateDialog_templateEvent.value") }));
				} else {
					aMailTemplate.setEvent(this.templateEvent.getSelectedItem().getValue().toString());
				}
			} else {
				aMailTemplate.setEvent(aMailTemplate.getModule());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template TAT
		try {
			if (this.row_turnAroundTime.isVisible()) {
				aMailTemplate.setTurnAroundTime(this.turnAroundTime.intValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template Repeat
		try {
			if (this.row_templateRepeat.isVisible()) {
				aMailTemplate.setRepeat(this.templateRepeat.isChecked());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Template For SMS
		try {
			aMailTemplate.setSmsTemplate(this.templateForSMS.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.templateForSMS.isChecked() && !this.templateForEmail.isChecked()) {
				throw new WrongValueException(this.templateForSMS,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_MailTemplateDialog_TemplateForSMS.value"),
										Labels.getLabel("label_MailTemplateDialog_TemplateForEmail.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// SMS Content
		if (this.templateForSMS.isChecked()) {
			try {
				if (StringUtils.isBlank(this.smsContent.getValue())) {
					throw new WrongValueException(this.smsContent, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_MailTemplateDialog_SMS.value") }));
				} else {
					aMailTemplate.setSmsContent(this.smsContent.getValue());
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		showErrorDetails(wve, this.basicDetailsTab);

		// Email Template Tab
		// Template For EMail
		try {
			aMailTemplate.setEmailTemplate(this.templateForEmail.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.templateForEmail.isChecked()) {
			// Email Send to
			if (this.row_EmailSendTo.isVisible()) {
				try {
					if (NotificationConstants.TEMPLATE_FOR_AE.equals(getMailTemplate().getTemplateFor())) {
						aMailTemplate.setEmailSendTo(this.userIds.getValue());
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

			// Email Content
			try {
				String emailFormat = "H";
				if (this.emailFormat.getSelectedItem() != null) {
					emailFormat = this.emailFormat.getSelectedItem().getValue().toString();

					if (emailFormat == PennantConstants.List_Select) {
						emailFormat = null;
					}
				}
				aMailTemplate.setEmailFormat(emailFormat);
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// Email Subject
			try {
				if (StringUtils.trimToEmpty(this.emailSubject.getValue()).trim().length() <= 0) {
					throw new WrongValueException(this.Space_emailSubject, Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_MailTemplateDialog_EmailSubject.value") }));
				} else {
					aMailTemplate.setEmailSubject(this.emailSubject.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// EMail Template Content
			try {

				if (NotificationConstants.TEMPLATE_FORMAT_HTML.equals(aMailTemplate.getEmailFormat())) {
					if (StringUtils.isBlank(this.htmlArtifact.getValue())) {
						throw new WrongValueException(this.Space_htmlArtifact, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_MailTemplateDialog_EMailContent.value") }));
					} else {
						aMailTemplate.setEmailContent(
								this.htmlArtifact.getValue().getBytes(NotificationConstants.DEFAULT_CHARSET));
					}
				} else if (NotificationConstants.TEMPLATE_FORMAT_PLAIN.equals(aMailTemplate.getEmailFormat())) {
					if (StringUtils.isBlank(this.plainText.getValue())) {
						throw new WrongValueException(this.plainText, Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_MailTemplateDialog_EMailContent.value") }));
					} else {
						aMailTemplate.setEmailContent(
								this.plainText.getValue().getBytes(NotificationConstants.DEFAULT_CHARSET));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		// Active
		try {
			aMailTemplate.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();
		showErrorDetails(wve, this.emailDetailsTab);

		aMailTemplate.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		if (!wve.isEmpty() && wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aMailTemplate
	 */
	public void doShowDialog(MailTemplate aMailTemplate) {
		logger.debug("Entering");

		if (enqModule) {
			doReadOnly(true);
		} else if (aMailTemplate.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			// this.templateModule.focus();
		} else {
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
			doWriteBeanToComponents(aMailTemplate);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_MailTemplateDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.templateCode.isReadonly()) {
			this.templateCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MailTemplateDialog_TemplateCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.templateDesc.isReadonly()) {
			this.templateDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MailTemplateDialog_TemplateDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		// TAT
		if (this.row_turnAroundTime.isVisible()) {
			this.turnAroundTime.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_MailTemplateDialog_turnAroundTime.value"), true));
		}

		// Email Subject
		/*
		 * if (!this.emailSubject.isReadonly()){ this.emailSubject.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_MailTemplateDialog_EmailSubject.value"),
		 * PennantRegularExpressions.REGEX_NAME, true)); }
		 */
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.turnAroundTime.setConstraint("");
		this.templateCode.setConstraint("");
		this.templateDesc.setConstraint("");
		this.emailFormat.setConstraint("");
		this.templateFor.setConstraint("");
		this.templateEvent.setConstraint("");
		this.templateModule.setConstraint("");
		// this.emailSubject.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.templateCode.setErrorMessage("");
		this.templateDesc.setErrorMessage("");
		this.emailFormat.setErrorMessage("");
		this.templateFor.setErrorMessage("");
		this.templateModule.setErrorMessage("");
		this.templateEvent.setErrorMessage("");
		Clients.clearWrongValue(this.Space_emailSubject);
		Clients.clearWrongValue(this.Space_htmlArtifact);

		this.turnAroundTime.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getMailTemplateListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final MailTemplate aMailTemplate = new MailTemplate();
		BeanUtils.copyProperties(getMailTemplate(), aMailTemplate);

		String keyReference = Labels.getLabel("label_MailTemplateDialog_TemplateCode.value") + " : "
				+ aMailTemplate.getTemplateCode();

		doDelete(keyReference, aMailTemplate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getMailTemplate().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		doReadOnly(false);
		if (isWorkFlowEnabled()) {

			if (this.mailTemplate.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		if (isReadOnly("MailTemplateDialog_templateContent")) {
			this.btnSimulate.setVisible(false);
			this.divHtmlArtifact.setVisible(true);
			this.htmlArtifact.setVisible(false);
			this.templateData.setVisible(false);
			this.templateData1.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		if (PennantConstants.RECORD_TYPE_DEL.equals(this.mailTemplate.getRecordType())) {
			readOnly = true;
		}

		if (!getMailTemplate().isNewRecord()) {
			this.templateCode.setReadonly(true);
		}

		this.templateDesc.setReadonly(isReadOnly("MailTemplateDialog_templateDesc"));
		this.turnAroundTime.setReadonly(isReadOnly("MailTemplateDialog_turnAroundTime"));
		this.templateRepeat.setDisabled(isReadOnly("MailTemplateDialog_templateRepeat"));

		// SMS
		this.templateForSMS.setDisabled(isReadOnly("MailTemplateDialog_templateForSMS"));
		this.smsContent.setDisabled(isReadOnly("MailTemplateDialog_templateContent"));

		// EMAIL
		this.templateForEmail.setDisabled(isReadOnly("MailTemplateDialog_templateForEmail"));

		this.emailFormat.setDisabled(isReadOnly("MailTemplateDialog_emailFormat"));
		this.templateFor.setDisabled(isReadOnly("MailTemplateDialog_templateFor"));
		this.templateModule.setDisabled(isReadOnly("MailTemplateDialog_templateModule"));
		this.templateEvent.setDisabled(isReadOnly("MailTemplateDialog_templateModule"));

		this.emailSubject.setReadonly(isReadOnly("MailTemplateDialog_emailSubject"));
		if (this.emailSubject.isReadonly()) {
			this.templateData1.setVisible(false);
		}
		this.active.setDisabled(isReadOnly("MailTemplateDialog_active"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.templateCode.setValue("");
		this.templateDesc.setValue("");
		this.templateRepeat.setChecked(false);
		this.templateForSMS.setChecked(false);
		this.templateForEmail.setChecked(false);
		this.emailFormat.setSelectedIndex(0);
		this.templateFor.setSelectedIndex(0);
		this.templateModule.setSelectedIndex(0);
		this.emailSubject.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void doSave() throws UnsupportedEncodingException {
		logger.debug("Entering");

		final MailTemplate aMailTemplate = new MailTemplate();
		BeanUtils.copyProperties(getMailTemplate(), aMailTemplate);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aMailTemplate.getRecordType())) {
			doSetValidation();
			// fill the MailTemplate object with the components data
			doWriteComponentsToBean(aMailTemplate);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aMailTemplate.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aMailTemplate.getRecordType())) {
				aMailTemplate.setVersion(aMailTemplate.getVersion() + 1);
				if (isNew) {
					aMailTemplate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMailTemplate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMailTemplate.setNewRecord(true);
				}
			}
		} else {
			aMailTemplate.setVersion(aMailTemplate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aMailTemplate, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	protected boolean doProcess(MailTemplate aMailTemplate, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		String nextRoleCode = "";

		aMailTemplate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aMailTemplate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMailTemplate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aMailTemplate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aMailTemplate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aMailTemplate);
				}

				if (isNotesMandatory(taskId, aMailTemplate)) {
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

			aMailTemplate.setTaskId(taskId);
			aMailTemplate.setNextTaskId(nextTaskId);
			aMailTemplate.setRoleCode(getRole());
			aMailTemplate.setNextRoleCode(nextRoleCode);

			String operationRefs = getServiceOperations(taskId, aMailTemplate);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(aMailTemplate, tranType, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(aMailTemplate, PennantConstants.TRAN_WF, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(aMailTemplate, tranType, null);
		}

		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(MailTemplate aMailTemplate, String tranType, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		AuditHeader auditHeader = getAuditHeader(aMailTemplate, tranType);

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getMailTemplateService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getMailTemplateService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getMailTemplateService().doApprove(auditHeader);

					if (tranType.equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getMailTemplateService().doReject(auditHeader);
					if (tranType.equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_MailTemplateDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_MailTemplateDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.mailTemplate), true);
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

	public void onChange$templateModule(Event event) {
		logger.debug("Entering " + event);
		this.row_TemplateEvent.setVisible(false);

		String eventValue = templateModule.getSelectedItem().getValue().toString();
		if (eventValue.equals(NotificationConstants.MAIL_MODULE_FIN)) {
			this.row_TemplateEvent.setVisible(true);
		} else {
			this.templateEvent.setSelectedIndex(0);
		}
		if (StringUtils.equals(this.templateModule.getSelectedItem().getValue().toString(),
				NotificationConstants.MAIL_MODULE_FIN)) {
			fillComboBox(this.templateFor, this.templateModule.getSelectedItem().getValue().toString(), listTemplateFor,
					",PN,");

		} else if (StringUtils.equals(this.templateModule.getSelectedItem().getValue().toString(),
				NotificationConstants.MAIL_MODULE_PROVIDER)) {
			fillComboBox(this.templateFor, this.templateModule.getSelectedItem().getValue().toString(), listTemplateFor,
					",SP,DN,AE,CN,");
		}

		if (this.templateModule.getSelectedItem().getValue() != null
				&& (StringUtils.equals(this.templateModule.getSelectedItem().getValue().toString(),
						NotificationConstants.MAIL_MODULE_CAF)
						|| StringUtils.equals(this.templateModule.getSelectedItem().getValue().toString(),
								NotificationConstants.MAIL_MODULE_FIN)
						|| StringUtils.equals(this.templateModule.getSelectedItem().getValue().toString(),
								NotificationConstants.MAIL_MODULE_CREDIT)
						|| StringUtils.equals(this.templateModule.getSelectedItem().getValue().toString(),
								NotificationConstants.MAIL_MODULE_PROVIDER)
						|| NotificationConstants.TEMPLATE_FOR_OTP
								.equals(this.templateModule.getSelectedItem().getValue().toString()))) {
			doFillTemplateFields(this.templateModule.getSelectedItem().getValue().toString(), this.templateData,
					this.templateEvent.getSelectedItem().getValue());
			doFillTemplateFields(this.templateModule.getSelectedItem().getValue().toString(), this.templateData1,
					this.templateEvent.getSelectedItem().getValue().toString());
		}

		this.emailSubject.setValue("");
		this.htmlArtifact.setValue("");

		logger.debug("Leaving " + event);
	}

	// Template Event
	public void onChange$templateEvent(Event event) {
		logger.debug("Entering " + event);

		this.templateData.getItems().clear();
		this.templateData1.getItems().clear();

		String templateEvent = this.templateEvent.getSelectedItem().getValue();
		String templateModule = this.templateModule.getSelectedItem().getValue();

		if (!StringUtils.equals(templateEvent, PennantConstants.List_Select)) {

			// Subject Line Parameters
			doFillTemplateFields(templateModule, templateData, templateEvent);

			// Mail Body Parameters
			doFillTemplateFields(templateModule, templateData1, templateEvent);

			this.emailSubject.setValue("");
			this.htmlArtifact.setValue("");
			logger.debug("Leaving " + event);
		}
	}

	// WorkFlow Components
	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(MailTemplate aMailTemplate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMailTemplate.getBefImage(), aMailTemplate);
		return new AuditHeader(aMailTemplate.getTemplateCode(), null, null, null, auditDetail,
				aMailTemplate.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.mailTemplate);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.mailTemplate.getTemplateCode());
	}

	/*
	 * onselect Event for Content Type
	 */
	public void onChange$emailFormat(Event event) {
		logger.debug("Entering" + event.toString());
		String emailTypeVal = (String) this.emailFormat.getSelectedItem().getValue();
		this.row_EmailSendTo.setVisible(true);
		doSetArtifact(emailTypeVal);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * OnCheck event for template For SMS
	 * 
	 * @param event
	 */
	public void onCheck$templateForSMS(Event event) {
		loadSMSFields();
	}

	private void loadSMSFields() {
		this.row_SMSContent.setVisible(false);
		if (this.templateForSMS.isChecked()) {
			Clients.clearWrongValue(this.templateForSMS);
			this.row_SMSContent.setVisible(true);
		}
	}

	/**
	 * OnCheck event for template For Email
	 * 
	 * @param event
	 */
	public void onCheck$templateForEmail(Event event) {
		loadEmailFields();
	}

	private void loadEmailFields() {
		logger.debug("Entering");

		this.emailDetailsTab.setDisabled(true);
		this.btnSimulate.setVisible(false);
		this.templateData.setVisible(false);
		this.templateData1.setVisible(false);
		if (this.templateForEmail.isChecked()) {
			Clients.clearWrongValue(this.templateForSMS);
			this.emailDetailsTab.setDisabled(false);
			this.emailDetailsTab.setSelected(true);
			this.btnSimulate.setVisible(true);
			if (NotificationConstants.TEMPLATE_FOR_AE.equals(getMailTemplate().getTemplateFor())) {
				this.row_EmailSendTo.setVisible(true);
			}

			if (!this.templateForEmail.isDisabled()) {
				this.templateData.setVisible(true);
			}

			if (this.emailSubject.isReadonly()) {
				this.templateData1.setVisible(false);
			}

			doFillTemplateFields(getMailTemplate().getModule(), this.templateData, getMailTemplate().getEvent());
			doFillTemplateFields(getMailTemplate().getModule(), this.templateData1, getMailTemplate().getEvent());

		}
		logger.debug("Leaving");
	}

	/*
	 * changing the artifact based on selection of Content Type
	 * 
	 * @ param content type
	 */
	private void doSetArtifact(String type) {
		logger.debug("Entering");

		doClearArtifact();

		if (type.equals(NotificationConstants.TEMPLATE_FORMAT_HTML)) {
			if (isReadOnly("MailTemplateDialog_templateContent")) {
				this.btnSimulate.setVisible(false);
				this.divHtmlArtifact.setVisible(true);
				this.htmlArtifact.setVisible(false);
				this.templateData.setVisible(false);
				this.templateData1.setVisible(false);
			} else {
				this.btnSimulate.setVisible(true);
				this.htmlArtifact.setVisible(true);
				this.templateData.setVisible(true);
				this.plainText.setVisible(false);
			}
		} else if (type.equals(NotificationConstants.TEMPLATE_FORMAT_PLAIN)) {
			this.plainText.setVisible(true);
			this.htmlArtifact.setVisible(false);
			this.templateData.setVisible(false);
		}

		logger.debug("Leaving ");
	}

	private void doClearArtifact() {
		this.htmlArtifact.setVisible(false);
		this.templateData.setVisible(false);
		this.htmlArtifact.setValue("");
		this.plainText.setVisible(false);
		this.plainText.setValue("");
	}

	private void doFillTemplateFields(String module, Listbox templateData, String event) {
		logger.debug("Entering");
		templateData.getItems().clear();
		List<TemplateFields> templateFieldsList = new ArrayList<TemplateFields>();
		JdbcSearchObject<TemplateFields> searchObj = new JdbcSearchObject<TemplateFields>(TemplateFields.class);
		searchObj.addTabelName("TemplateFields");

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("Module", module, Filter.OP_EQUAL);

		if (StringUtils.equals(NotificationConstants.MAIL_MODULE_FIN, module)) {
			filters[1] = new Filter("Event", event, Filter.OP_EQUAL);
		} else {
			filters[1] = new Filter("Event", module, Filter.OP_EQUAL);
		}

		searchObj.addFilters(filters);
		templateFieldsList = getPagedListService().getBySearchObject(searchObj);

		String lcLabel = "";
		if (templateFieldsList.size() == 0) {
			templateData.setVisible(false);
			return;
		} else {
			templateData.setVisible(true);
		}

		for (int i = 0; i < templateFieldsList.size(); i++) {
			Listitem item = new Listitem();
			String value = templateFieldsList.get(i).getField().trim();
			lcLabel = "${vo." + value + "}";
			if ("D".equals(templateFieldsList.get(i).getFieldFormat())) {
				lcLabel = "${vo." + value + "?date}";
			} else if ("AM2".equals(templateFieldsList.get(i).getFieldFormat())
					|| "AM3".equals(templateFieldsList.get(i).getFieldFormat())) {
				lcLabel = "${vo." + value + "?string.currency}";
			} else if ("T".equals(templateFieldsList.get(i).getFieldFormat())) {
				lcLabel = "${vo." + value + "?datetime}";
			}

			Listcell lc = new Listcell(lcLabel);
			filedValues.put(lcLabel, templateFieldsList.get(i).getField().trim());
			filedDesc.put(templateFieldsList.get(i).getField().trim(),
					templateFieldsList.get(i).getFieldDesc() + ":" + templateFieldsList.get(i).getFieldFormat());
			lc.setParent(item);
			lc.setVisible(false);
			lc = new Listcell(templateFieldsList.get(i).getFieldDesc());
			lc.setParent(item);
			templateData.appendChild(item);
		}

		logger.debug("Leaving");

	}

	public void onClick$btnSimulate(Event event) {
		logger.debug(event.toString());
		Clients.clearWrongValue(this.Space_htmlArtifact);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			StringTemplateLoader loader = new StringTemplateLoader();

			String content = this.htmlArtifact.getValue();
			if (StringUtils.isBlank(content)) {
				throw new WrongValueException(this.Space_htmlArtifact, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_MailTemplateDialog_EMailContent.value") }));
			}

			loader.putTemplate("Template", content);

			Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			configuration.setTemplateLoader(loader);
			Template template = configuration.getTemplate("Template");

			Map<String, Object> model = new HashMap<String, Object>();
			FinanceMain fm = new FinanceMain();
			fm.setMaturityDate(SysParamUtil.getAppValueDate());
			fm.setFinStartDate(SysParamUtil.getAppValueDate());
			model.put("vo", fm);

			FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

		} catch (WrongValueException we) {
			wve.add(we);
			showErrorDetails(wve, this.emailDetailsTab);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		createSimulator(this.htmlArtifact.getValue());
		logger.debug("Leaving");
	}

	private void createSimulator(String mailContent) {
		logger.debug("Entering");

		final Map<String, String> fieldsMap = new HashMap<String, String>();
		for (String field : filedValues.keySet()) {
			if (mailContent.contains(field)) {
				fieldsMap.put(filedValues.get(field), filedDesc.get(filedValues.get(field)));
			}
		}

		final Map<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("mailTemplateDialogCtrl", this);
		argsMap.put("fieldsMap", fieldsMap);
		argsMap.put("mailContent", mailContent);
		argsMap.put("module", getMailTemplate().getModule());
		argsMap.put("event", getMailTemplate().getEvent());

		try {
			Executions.createComponents("/WEB-INF/pages/Mail/MailTemplate/TemplatePreview.zul", null, argsMap);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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

	public MailTemplate getMailTemplate() {
		return this.mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public MailTemplateService getMailTemplateService() {
		return this.mailTemplateService;
	}

	public void setMailTemplateListCtrl(MailTemplateListCtrl mailTemplateListCtrl) {
		this.mailTemplateListCtrl = mailTemplateListCtrl;
	}

	public MailTemplateListCtrl getMailTemplateListCtrl() {
		return this.mailTemplateListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

}
