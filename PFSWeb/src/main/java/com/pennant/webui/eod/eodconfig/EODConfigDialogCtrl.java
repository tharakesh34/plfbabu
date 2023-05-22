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
 * * FileName : EODConfigDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-05-2017 * * Modified
 * Date : 24-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.eod.eodconfig;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.service.eod.EODConfigService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/eod/EODConfig/eODConfigDialog.zul file. <br>
 */
public class EODConfigDialogCtrl extends GFCBaseCtrl<EODConfig> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(EODConfigDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EODConfigDialog;
	protected Checkbox extMnthRequired;
	protected Datebox mnthExtTo;
	protected Checkbox active;
	protected Checkbox autoEodRequired;
	protected Timebox eodStartJobFrequency;
	protected Label label_cronexp;
	protected Checkbox enableAutoEOD;
	protected Checkbox eodAutoDisable;
	protected Checkbox sendEmailRequired;
	protected Textbox sMTPHost;
	protected Textbox sMTPPort;
	protected Combobox encryptionType;
	protected Checkbox sMTPAuthenticationRequired;
	protected Textbox sMTPUserName;
	protected Textbox sMTPPassword;
	protected Textbox fromEmailAddress;
	protected Textbox fromName;
	protected Textbox toEmailAddress;
	protected Textbox cCEmailAddress;
	protected Checkbox eMailNotificationsRequired;
	protected Checkbox publishNotificationsRequired;
	protected Combobox reminderFrequencyHour;
	protected Combobox reminderFrequencyMin;
	protected Checkbox delayRequired;
	protected Combobox delayFrequencyHour;
	protected Combobox delayFrequencyMin;

	protected Space space_EODStartJobFrequency;
	protected Space space_SMTPUserName;
	protected Space space_SMTPPassword;
	protected Space space_FromName;
	protected Space space_FromEmailAddress;
	protected Space space_ToEmailAddress;
	protected Space space_CCEmailAddress;
	protected Space space_SMTPHost;
	protected Space space_SMTPPort;
	protected Space space_eMailNotificationsRequired;
	protected Space space_publishNotificationsRequired;
	protected Space space_reminderFrequency;
	protected Space space_delayRequired;
	protected Space space_delayFrequency;
	protected Space space_EncryptionType;

	protected Groupbox gb_autoEOD_Details;
	protected Groupbox gb_authReq_Details;
	protected Groupbox gb_EOD_Notifications;

	private EODConfig eODConfig; // overhanded per param
	private EODConfig appRovedeodConfig;

	private transient EODConfigListCtrl eODConfigListCtrl; // overhanded per param
	private transient EODConfigService eODConfigService;

	private List<ValueLabel> encryptionTypeList = PennantStaticListUtil.getEncryptionTypeList();
	private List<ValueLabel> hourList = PennantStaticListUtil.getHourList();
	private List<ValueLabel> minList = PennantStaticListUtil.getMinuteList();

	/**
	 * default constructor.<br>
	 */
	public EODConfigDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EODConfigDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.eODConfig.getEodConfigId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_EODConfigDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_EODConfigDialog);

		try {
			// Get the required arguments.
			this.eODConfig = (EODConfig) arguments.get("eodconfig");
			this.eODConfigListCtrl = (EODConfigListCtrl) arguments.get("eodconfigListCtrl");

			if (this.eODConfig == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			EODConfig eODConfig = new EODConfig();
			BeanUtils.copyProperties(this.eODConfig, eODConfig);
			this.eODConfig.setBefImage(eODConfig);

			// Render the page and display the data.
			doLoadWorkFlow(this.eODConfig.isWorkflow(), this.eODConfig.getWorkflowId(), this.eODConfig.getNextTaskId());

			if (ImplementationConstants.AUTO_EOD_REQUIRED) {
				gb_autoEOD_Details.setVisible(true);
				gb_authReq_Details.setVisible(true);
				gb_EOD_Notifications.setVisible(true);
			}

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			appRovedeodConfig = eODConfigService.getApprovedEODConfig(eODConfig.getId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.eODConfig);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$eodStartJobFrequency(Event event) {
		if (this.eodStartJobFrequency.getValue() != null) {
			this.label_cronexp.setVisible(true);
			this.label_cronexp.setValue(toCron(this.eodStartJobFrequency.getValue()));
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.mnthExtTo.setFormat(PennantConstants.dateFormat);
		this.space_EODStartJobFrequency.setSclass("mandatory");
		this.space_SMTPUserName.setSclass("mandatory");
		this.space_SMTPPassword.setSclass("mandatory");
		this.space_FromName.setSclass("mandatory");
		this.space_FromEmailAddress.setSclass("mandatory");
		this.space_ToEmailAddress.setSclass("mandatory");
		// this.space_CCEmailAddress.setSclass("mandatory");
		this.space_SMTPHost.setSclass("mandatory");
		this.space_SMTPPort.setSclass("mandatory");
		this.space_reminderFrequency.setSclass("mandatory");
		this.space_delayFrequency.setSclass("mandatory");
		this.space_EncryptionType.setSclass("mandatory");
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EODConfigDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EODConfigDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EODConfigDialog_btnSave"));
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
		doShowNotes(this.eODConfig);
		logger.debug(Literal.LEAVING);
	}

	/*
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		eODConfigListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.eODConfig.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param eODConfig
	 * 
	 */
	public void doWriteBeanToComponents(EODConfig aEODConfig) {
		logger.debug(Literal.ENTERING);

		String password = "";

		this.extMnthRequired.setChecked(aEODConfig.isExtMnthRequired());
		this.mnthExtTo.setValue(aEODConfig.getMnthExtTo());
		this.active.setChecked(aEODConfig.isActive());

		if (ImplementationConstants.AUTO_EOD_REQUIRED) {
			this.autoEodRequired.setChecked(aEODConfig.isAutoEodRequired());
		} else {
			this.autoEodRequired.setChecked(false);
			this.autoEodRequired.setTooltiptext("Job is not Enabled");
		}

		Date eodStartTime = cronToDate(aEODConfig.getEODStartJobFrequency());
		if (aEODConfig.getEODStartJobFrequency() != null) {
			this.eodStartJobFrequency.setRawValue(eodStartTime);
			this.label_cronexp.setValue(aEODConfig.getEODStartJobFrequency());
		} else {
			this.label_cronexp.setVisible(false);
		}

		this.enableAutoEOD.setChecked(aEODConfig.isEnableAutoEod());
		this.eodAutoDisable.setChecked(aEODConfig.isEODAutoDisable());
		this.sendEmailRequired.setChecked(aEODConfig.isSendEmailRequired());
		this.sMTPHost.setValue(aEODConfig.getSMTPHost());
		this.sMTPPort.setValue(aEODConfig.getSMTPPort());
		fillComboBox(this.encryptionType, aEODConfig.getEncryptionType(), encryptionTypeList, "");
		this.sMTPAuthenticationRequired.setChecked(aEODConfig.isSMTPAutenticationRequired());
		this.sMTPUserName.setValue(aEODConfig.getSMTPUserName());

		if (aEODConfig.getSMTPPwd() != null && !StringUtils.isEmpty(aEODConfig.getSMTPPwd())) {
			password = EncryptionUtil.decrypt("ENC(" + aEODConfig.getSMTPPwd() + ")");
		}

		this.sMTPPassword.setValue(password);
		this.fromEmailAddress.setValue(aEODConfig.getFromEmailAddress());
		this.fromName.setValue(aEODConfig.getFromName());
		this.toEmailAddress.setValue(aEODConfig.getToEmailAddress());
		this.cCEmailAddress.setValue(aEODConfig.getCCEmailAddress());

		this.eMailNotificationsRequired.setChecked(aEODConfig.isEmailNotifReqrd());
		this.publishNotificationsRequired.setChecked(aEODConfig.isPublishNotifReqrd());
		this.recordStatus.setValue(aEODConfig.getRecordStatus());

		Date cronToDate = cronToDate(aEODConfig.getReminderFrequency());

		if (cronToDate != null) {
			String[] remFrq = DateUtil.timeBetween(eodStartTime, cronToDate).split(":");
			int remFrqhr = Integer.parseInt(remFrq[0]);
			String remhr = "";
			remFrqhr = (remFrqhr > 12 ? remFrqhr - 12 : remFrqhr);
			if (remFrqhr < 10) {
				remhr = "0" + (String.valueOf(remFrqhr));
			} else {
				remhr = String.valueOf(remFrqhr);
			}
			fillComboBox(this.reminderFrequencyHour, remhr, hourList, "");
			fillComboBox(this.reminderFrequencyMin, remFrq[1], minList, "");
		} else {
			fillComboBox(this.reminderFrequencyHour, "", hourList, "");
			fillComboBox(this.reminderFrequencyMin, "", minList, "");
		}

		this.delayRequired.setChecked(aEODConfig.isDelayNotifyReq());

		cronToDate = cronToDate(aEODConfig.getDelayFrequency());

		if (cronToDate != null) {
			String[] delayFrq = DateUtil.timeBetween(cronToDate, eodStartTime).split(":");
			int delayfrqhr = Integer.parseInt(delayFrq[0]);
			String delayhr = "";
			delayfrqhr = (delayfrqhr > 12 ? delayfrqhr - 12 : delayfrqhr);
			if (delayfrqhr < 10) {
				delayhr = "0" + (String.valueOf(delayfrqhr));
			} else {
				delayhr = String.valueOf(delayfrqhr);
			}
			fillComboBox(this.delayFrequencyHour, delayhr, hourList, "");
			fillComboBox(this.delayFrequencyMin, delayFrq[1], minList, "");
		} else {
			fillComboBox(this.delayFrequencyHour, "", hourList, "");
			fillComboBox(this.delayFrequencyMin, "", minList, "");
		}

		doCheckMonthEnd();

		if (!"Submitted".equals(eODConfig.getRecordStatus())) {
			checkVisibility(aEODConfig);
		}

		logger.debug(Literal.LEAVING);
	}

	private void checkVisibility(EODConfig aEODConfig) {
		if (this.extMnthRequired.isChecked()) {
			this.mnthExtTo.setDisabled(false);
		} else {
			this.mnthExtTo.setDisabled(true);
		}

		if (aEODConfig.isAutoEodRequired()) {
			this.eodStartJobFrequency.setDisabled(false);
			this.enableAutoEOD.setDisabled(false);
			this.eodAutoDisable.setDisabled(false);

		} else {
			this.eodStartJobFrequency.setValue(cronToDate(null));
			this.eodStartJobFrequency.setDisabled(true);
			this.enableAutoEOD.setChecked(false);
			this.enableAutoEOD.setDisabled(true);
			this.eodAutoDisable.setChecked(false);
			this.eodAutoDisable.setDisabled(true);
			this.eodStartJobFrequency.setValue(cronToDate(null));
			this.eodStartJobFrequency.setDisabled(true);
			this.enableAutoEOD.setChecked(false);
			this.enableAutoEOD.setDisabled(true);
			this.eodAutoDisable.setChecked(false);
			checkGbNotifVisibility();
		}

		if (aEODConfig.isSendEmailRequired()) {
			this.sMTPUserName.setDisabled(false);
			this.fromName.setDisabled(false);
			this.fromEmailAddress.setDisabled(false);
			this.toEmailAddress.setDisabled(false);
			this.cCEmailAddress.setDisabled(false);
			this.sMTPHost.setDisabled(false);
			this.sMTPPort.setDisabled(false);
			this.sMTPAuthenticationRequired.setDisabled(false);
			if (aEODConfig.isSMTPAutenticationRequired()) {
				this.sMTPPassword.setDisabled(false);
			} else {
				this.sMTPPassword.setValue("");
				this.sMTPPassword.setDisabled(true);

			}
			this.encryptionType.setDisabled(false);

		} else {
			setDefaultValues();
		}

		checkGbNotifVisibility();

	}

	private void setDefaultValues() {
		this.sMTPUserName.setValue("");
		this.sMTPUserName.setDisabled(true);
		this.fromName.setValue("");
		this.fromName.setDisabled(true);
		this.fromEmailAddress.setValue("");
		this.fromEmailAddress.setDisabled(true);
		this.toEmailAddress.setValue("");
		this.toEmailAddress.setDisabled(true);
		this.cCEmailAddress.setValue("");
		this.cCEmailAddress.setDisabled(true);
		this.sMTPHost.setValue("");
		this.sMTPHost.setDisabled(true);
		this.sMTPPort.setValue("");
		this.sMTPPort.setDisabled(true);
		this.sMTPAuthenticationRequired.setChecked(false);
		this.sMTPAuthenticationRequired.setDisabled(true);
		this.sMTPPassword.setValue("");
		this.sMTPPassword.setDisabled(true);
		this.encryptionType.setValue("");
		this.encryptionType.setDisabled(true);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEODConfig
	 */
	public void doWriteComponentsToBean(EODConfig aEODConfig) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Month Extended To
		if (this.extMnthRequired.isChecked()) {
			try {
				aEODConfig.setMnthExtTo(this.mnthExtTo.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// Extended month required
		if (this.extMnthRequired.isChecked()) {
			try {
				aEODConfig.setExtMnthRequired(this.extMnthRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// Active
		try {
			aEODConfig.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Auto EOD required
		try {
			aEODConfig.setAutoEodRequired(this.autoEodRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.autoEodRequired.isChecked()) {
			try {
				if (this.eodStartJobFrequency.getValue() == null) {
					wve.add(new WrongValueException(this.eodStartJobFrequency, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_EODStartJobFrequency.value") })));
				} else {
					String cronExpression = toCron(this.eodStartJobFrequency.getValue());
					try {
						CronExpression.validateExpression(cronExpression);
					} catch (ParseException e) {
						wve.add(new WrongValueException(this.eodStartJobFrequency, Labels.getLabel("FIELD_NOT_VALID",
								new String[] { Labels.getLabel("label_EODConfigDialog_EODStartJobFrequency.value") })));
					}
					aEODConfig.setEODStartJobFrequency(cronExpression);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aEODConfig.setEnableAutoEod(this.enableAutoEOD.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aEODConfig.setEODAutoDisable(this.eodAutoDisable.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aEODConfig.setEnableAutoEod(false);
			aEODConfig.setEODAutoDisable(false);
		}

		try {
			aEODConfig.setSendEmailRequired(this.sendEmailRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.sendEmailRequired.isChecked()) {
			try {
				aEODConfig.setSMTPAutenticationRequired(this.sMTPAuthenticationRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (this.sMTPAuthenticationRequired.isChecked()) {
				try {
					if (StringUtils.isEmpty(this.sMTPPassword.getValue())) {
						wve.add(new WrongValueException(this.sMTPUserName, Labels.getLabel("MUST_BE_ENTERED",
								new String[] { Labels.getLabel("label_EODConfigDialog_SMTPPassword.value") })));
					} else {
						String password = EncryptionUtil.encrypt(this.sMTPPassword.getValue());
						aEODConfig.setSMTPPwd(password);
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

			try {
				if (this.encryptionType.isVisible() && !this.encryptionType.isDisabled()) {
					if ("#".equals(this.encryptionType.getSelectedItem().getValue().toString())) {
						throw new WrongValueException(this.encryptionType, Labels.getLabel("CHECK_NO_EMPTY",
								new String[] { Labels.getLabel("label_EODConfigDialog_EncryptionType.value") }));
					} else {
						aEODConfig.setEncryptionType(this.encryptionType.getSelectedItem().getValue().toString());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (StringUtils.isEmpty(this.sMTPHost.getValue())) {
					wve.add(new WrongValueException(this.sMTPHost, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_SMTPHost.value") })));
				} else {
					aEODConfig.setSMTPHost(this.sMTPHost.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (StringUtils.isEmpty(this.sMTPPort.getValue())) {
					wve.add(new WrongValueException(this.sMTPPort, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_SMTPPort.value") })));
				} else {
					aEODConfig.setSMTPPort(this.sMTPPort.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (StringUtils.isEmpty(this.sMTPUserName.getValue())) {
					wve.add(new WrongValueException(this.sMTPUserName, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_SMTPUserName.value") })));
				} else {
					aEODConfig.setSMTPUserName(this.sMTPUserName.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (StringUtils.isEmpty(this.fromEmailAddress.getValue())) {
					wve.add(new WrongValueException(this.fromEmailAddress, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_FromEmailAddress.value") })));
				} else {
					aEODConfig.setFromEmailAddress(this.fromEmailAddress.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (StringUtils.isEmpty(this.fromName.getValue())) {
					wve.add(new WrongValueException(this.fromName, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_FromName.value") })));
				} else {
					aEODConfig.setFromName(this.fromName.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (StringUtils.isEmpty(this.toEmailAddress.getValue())) {
					wve.add(new WrongValueException(this.toEmailAddress, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_EODConfigDialog_ToEmailAddress.value") })));
				} else {
					aEODConfig.setToEmailAddress(this.toEmailAddress.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aEODConfig.setCCEmailAddress(this.cCEmailAddress.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {

			aEODConfig.setSMTPAutenticationRequired(false);
		}

		if (this.autoEodRequired.isChecked() && this.sendEmailRequired.isChecked()) {

			try {
				aEODConfig.setEmailNotifReqrd(this.eMailNotificationsRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aEODConfig.setPublishNotifReqrd(this.publishNotificationsRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			if (this.eMailNotificationsRequired.isChecked() || this.publishNotificationsRequired.isChecked()) {
				try {
					if ("#".equals(this.reminderFrequencyHour.getSelectedItem().getValue().toString())) {
						throw new WrongValueException(this.reminderFrequencyHour, Labels.getLabel("CHECK_NO_EMPTY",
								new String[] { Labels.getLabel("label_reminderFrequency.value") }));
					}
					if ("#".equals(this.reminderFrequencyMin.getSelectedItem().getValue().toString())) {
						throw new WrongValueException(this.reminderFrequencyMin, Labels.getLabel("CHECK_NO_EMPTY",
								new String[] { Labels.getLabel("label_reminderFrequency.value") }));
					}

					String hours = getComboboxValue(this.reminderFrequencyHour);
					String min = getComboboxValue(this.reminderFrequencyMin);

					Date eodFreq = this.eodStartJobFrequency.getValue();
					Date reminderFreq = DateUtil.parse(hours + ":" + min, DateFormat.SHORT_TIME);

					String setTimeToCron = DateUtil.timeBetween(eodFreq, reminderFreq, "ss mm HH");

					if (setTimeToCron == "") {
						MessageUtil.showError("Please select valid time for Reminder Frequency");
						throw new WrongValueException();
					}

					setTimeToCron = String.format("%s * * ?", setTimeToCron);
					aEODConfig.setReminderFrequency(setTimeToCron);
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
			try {
				aEODConfig.setDelayNotifyReq(this.delayRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			if (this.delayRequired.isChecked()) {
				try {
					if ("#".equals(this.delayFrequencyHour.getSelectedItem().getValue().toString())) {
						throw new WrongValueException(this.delayFrequencyHour, Labels.getLabel("CHECK_NO_EMPTY",
								new String[] { Labels.getLabel("label_delayFrequency.value") }));
					}
					if ("#".equals(this.delayFrequencyMin.getSelectedItem().getValue().toString())) {
						throw new WrongValueException(this.delayFrequencyMin, Labels.getLabel("CHECK_NO_EMPTY",
								new String[] { Labels.getLabel("label_delayFrequency.value") }));
					}

					int hours = Integer.valueOf(getComboboxValue(this.delayFrequencyHour));
					int min = Integer.valueOf(getComboboxValue(this.delayFrequencyMin));
					Date eodFreq = this.eodStartJobFrequency.getValue();
					String startTime = DateUtil.format(eodFreq, DateFormat.SHORT_TIME);
					String[] delayFreq = startTime.split(":");

					hours = hours + Integer.valueOf(delayFreq[0]);
					min = min + Integer.valueOf(delayFreq[1]);

					Date delayFreqT = DateUtil.parse(hours + ":" + min, DateFormat.SHORT_TIME);
					String setTimeToCron = DateUtil.format(delayFreqT, "ss mm HH");

					if (setTimeToCron == "") {
						MessageUtil.showError("Please select valid time for Delay Frequency");
						throw new WrongValueException();
					}

					setTimeToCron = String.format("%s * * ?", setTimeToCron);
					aEODConfig.setDelayFrequency(setTimeToCron);
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		} else {
			aEODConfig.setEmailNotifReqrd(false);
			aEODConfig.setPublishNotifReqrd(false);
			aEODConfig.setDelayNotifyReq(false);
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param eODConfig The entity that need to be render.
	 */
	public void doShowDialog(EODConfig eODConfig) {
		logger.debug(Literal.ENTERING);

		if (eODConfig.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.extMnthRequired.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(eODConfig.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.extMnthRequired.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnDelete.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(eODConfig);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.mnthExtTo.isReadonly()) {

			String lable = Labels.getLabel("label_EODConfigDialog_MnthExtTo.value");
			if (appRovedeodConfig != null && appRovedeodConfig.isInExtMnth()) {
				// greater than today and less than current month
				this.mnthExtTo.setConstraint(new PTDateValidator(lable, true, SysParamUtil.getAppDate(),
						DateUtil.getMonthEnd(SysParamUtil.getAppDate()), true));
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(SysParamUtil.getAppDate());
				calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
				// greater than current month end and less than next month end;
				this.mnthExtTo.setConstraint(
						new PTDateValidator(lable, true, DateUtil.getMonthEnd(SysParamUtil.getAppDate()),
								DateUtil.getMonthEnd(calendar.getTime()), false));
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.mnthExtTo.setConstraint("");
		this.eodStartJobFrequency.setConstraint("");
		this.sMTPHost.setConstraint("");
		this.sMTPPort.setConstraint("");
		this.encryptionType.setConstraint("");
		this.sMTPUserName.setConstraint("");
		this.sMTPPassword.setConstraint("");
		this.fromEmailAddress.setConstraint("");
		this.fromName.setConstraint("");
		this.toEmailAddress.setConstraint("");
		this.cCEmailAddress.setConstraint("");
		this.reminderFrequencyHour.setConstraint("");
		this.reminderFrequencyMin.setConstraint("");
		this.delayFrequencyHour.setConstraint("");
		this.delayFrequencyMin.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		// Config Id
		// Extended month required
		// Month Extended To
		// Active

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final EODConfig aEODConfig = new EODConfig();
		BeanUtils.copyProperties(this.eODConfig, aEODConfig);

		doDelete(String.valueOf(aEODConfig.getEodConfigId()), aEODConfig);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.eODConfig.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);

		}

		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.extMnthRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_MnthExtTo"), this.mnthExtTo);
		readOnlyComponent(isReadOnly("EODConfigDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.autoEodRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.eodStartJobFrequency);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.enableAutoEOD);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.eodAutoDisable);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.sendEmailRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.sMTPHost);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.sMTPPort);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.encryptionType);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.sMTPAuthenticationRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.sMTPUserName);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.sMTPPassword);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.fromEmailAddress);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.fromName);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.toEmailAddress);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.cCEmailAddress);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.eMailNotificationsRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.publishNotificationsRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.reminderFrequencyHour);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.reminderFrequencyMin);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.delayRequired);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.delayFrequencyHour);
		readOnlyComponent(isReadOnly("EODConfigDialog_ExtMnthRequired"), this.delayFrequencyMin);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.eODConfig.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				this.btnDelete.setVisible(false);
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
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.extMnthRequired);
		readOnlyComponent(true, this.mnthExtTo);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.autoEodRequired);
		readOnlyComponent(true, this.eodStartJobFrequency);
		readOnlyComponent(true, this.enableAutoEOD);
		readOnlyComponent(true, this.eodAutoDisable);
		readOnlyComponent(true, this.sendEmailRequired);
		readOnlyComponent(true, this.sMTPHost);
		readOnlyComponent(true, this.sMTPPort);
		readOnlyComponent(true, this.encryptionType);
		readOnlyComponent(true, this.sMTPAuthenticationRequired);
		readOnlyComponent(true, this.sMTPUserName);
		readOnlyComponent(true, this.sMTPPassword);
		readOnlyComponent(true, this.fromEmailAddress);
		readOnlyComponent(true, this.fromName);
		readOnlyComponent(true, this.toEmailAddress);
		readOnlyComponent(true, this.cCEmailAddress);
		readOnlyComponent(true, this.eMailNotificationsRequired);
		readOnlyComponent(true, this.publishNotificationsRequired);
		readOnlyComponent(true, this.reminderFrequencyHour);
		readOnlyComponent(true, this.reminderFrequencyMin);
		readOnlyComponent(true, this.delayRequired);
		readOnlyComponent(true, this.delayFrequencyHour);
		readOnlyComponent(true, this.delayFrequencyMin);

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

		this.extMnthRequired.setChecked(false);
		this.mnthExtTo.setText("");
		this.active.setChecked(false);

		if (!ImplementationConstants.AUTO_EOD_REQUIRED) {
			this.autoEodRequired.setChecked(false);
		}

		this.enableAutoEOD.setChecked(false);
		this.eodAutoDisable.setChecked(false);
		this.sendEmailRequired.setChecked(false);
		this.sMTPHost.setValue("");
		this.sMTPPort.setValue("");
		this.encryptionType.setValue("");
		this.sMTPAuthenticationRequired.setChecked(false);
		this.sMTPUserName.setValue("");
		this.sMTPPassword.setValue("");
		this.fromEmailAddress.setValue("");
		this.fromName.setValue("");
		this.toEmailAddress.setValue("");
		this.cCEmailAddress.setValue("");
		this.eMailNotificationsRequired.setChecked(false);
		this.publishNotificationsRequired.setChecked(false);
		this.reminderFrequencyHour.setValue("");
		this.reminderFrequencyMin.setValue("");
		this.delayRequired.setChecked(false);
		this.delayFrequencyHour.setValue("");
		this.delayFrequencyMin.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final EODConfig aEODConfig = new EODConfig();
		BeanUtils.copyProperties(this.eODConfig, aEODConfig);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aEODConfig);

		isNew = aEODConfig.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEODConfig.getRecordType())) {
				aEODConfig.setVersion(aEODConfig.getVersion() + 1);
				if (isNew) {
					aEODConfig.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aEODConfig.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEODConfig.setNewRecord(true);
				}
			}
		} else {
			aEODConfig.setVersion(aEODConfig.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aEODConfig, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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
	protected boolean doProcess(EODConfig aEODConfig, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aEODConfig.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aEODConfig.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEODConfig.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aEODConfig.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEODConfig.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aEODConfig);
				}

				if (isNotesMandatory(taskId, aEODConfig)) {
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

			aEODConfig.setTaskId(taskId);
			aEODConfig.setNextTaskId(nextTaskId);
			aEODConfig.setRoleCode(getRole());
			aEODConfig.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aEODConfig, tranType);
			String operationRefs = getServiceOperations(taskId, aEODConfig);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aEODConfig, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aEODConfig, tranType);
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
		EODConfig aEODConfig = (EODConfig) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = eODConfigService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = eODConfigService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = eODConfigService.doApprove(auditHeader);

					if (aEODConfig.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = eODConfigService.doReject(auditHeader);
					if (aEODConfig.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_EODConfigDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_EODConfigDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.eODConfig), true);
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

	private void doCheckMonthEnd() {

		if (appRovedeodConfig != null && appRovedeodConfig.isInExtMnth()) {
			readOnlyComponent(true, this.extMnthRequired);
		}
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(EODConfig aEODConfig, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEODConfig.getBefImage(), aEODConfig);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aEODConfig.getUserDetails(),
				getOverideMap());
	}

	public void setEODConfigService(EODConfigService eODConfigService) {
		this.eODConfigService = eODConfigService;
	}

	public void onCheck$autoEodRequired(Event event) {
		if (this.autoEodRequired.isChecked()) {
			this.eodStartJobFrequency.setDisabled(false);
			this.enableAutoEOD.setDisabled(false);
			this.eodAutoDisable.setDisabled(false);
		} else {
			this.eodStartJobFrequency.setValue(cronToDate(null));
			this.label_cronexp.setValue("");
			this.eodStartJobFrequency.setDisabled(true);
			this.enableAutoEOD.setChecked(false);
			this.enableAutoEOD.setDisabled(true);
			this.eodAutoDisable.setChecked(false);
			this.eodAutoDisable.setDisabled(true);
		}
		checkGbNotifVisibility();
	}

	public void onCheck$sendEmailRequired(Event event) {
		if (this.sendEmailRequired.isChecked()) {
			this.sMTPUserName.setDisabled(false);
			this.sMTPUserName.setValue(App.getProperty("notification.email.out.user"));
			this.fromName.setDisabled(false);
			this.fromName.setValue(App.getProperty("notification.email.out.personal"));
			this.fromEmailAddress.setDisabled(false);
			this.fromEmailAddress.setValue(App.getProperty("notification.email.out.from"));
			this.toEmailAddress.setDisabled(false);
			this.toEmailAddress.setValue("");
			this.cCEmailAddress.setDisabled(false);
			this.cCEmailAddress.setValue("");
			this.sMTPHost.setDisabled(false);
			this.sMTPHost.setValue(App.getProperty("notification.email.out.host"));
			this.sMTPPort.setDisabled(false);
			this.sMTPPort.setValue(App.getProperty("notification.email.out.port"));
			this.sMTPAuthenticationRequired.setDisabled(false);
			this.sMTPAuthenticationRequired.setChecked(false);
			this.encryptionType.setDisabled(false);
			this.encryptionType.setValue(App.getProperty("notification.email.out.encryptionType"));
			if (this.sMTPAuthenticationRequired.isChecked() || App.getBooleanProperty("notification.email.out.auth")) {
				this.sMTPAuthenticationRequired.setChecked(true);
				this.sMTPPassword.setDisabled(false);
				this.sMTPPassword.setValue(App.getProperty("notification.email.out.password"));
			} else {
				this.sMTPPassword.setValue("");
				this.sMTPPassword.setDisabled(true);
			}

		} else {
			setDefaultValues();
		}

		checkGbNotifVisibility();
	}

	public void onCheck$sMTPAuthenticationRequired(Event event) {
		if (this.sMTPAuthenticationRequired.isChecked()) {
			this.sMTPPassword.setDisabled(false);
			this.sMTPPassword.setValue(App.getProperty("notification.email.out.password"));
		} else {
			this.sMTPPassword.setValue("");
			this.sMTPPassword.setDisabled(true);
		}
	}

	public void onCheck$eMailNotificationsRequired(Event event) {
		checkGbNotifVisibility();
	}

	public void onCheck$publishNotificationsRequired(Event event) {
		checkGbNotifVisibility();
	}

	public void checkGbNotifVisibility() {
		if (!this.autoEodRequired.isChecked()) {
			this.sendEmailRequired.setChecked(false);
			this.sendEmailRequired.setDisabled(true);
			setDefaultValues();
		} else {
			this.sendEmailRequired.setDisabled(false);
		}

		if (this.autoEodRequired.isChecked() && this.sendEmailRequired.isChecked()) {
			this.eMailNotificationsRequired.setDisabled(false);
			this.publishNotificationsRequired.setDisabled(false);
			if (this.eMailNotificationsRequired.isChecked() || this.publishNotificationsRequired.isChecked()) {
				this.reminderFrequencyHour.setDisabled(false);
				this.reminderFrequencyMin.setDisabled(false);
			} else {
				this.reminderFrequencyHour.setValue("");
				this.reminderFrequencyHour.setDisabled(true);
				this.reminderFrequencyMin.setValue("");
				this.reminderFrequencyMin.setDisabled(true);
			}
			this.delayRequired.setDisabled(false);
			if (this.delayRequired.isChecked() && !this.delayRequired.isDisabled()) {
				this.delayFrequencyHour.setDisabled(false);
				this.delayFrequencyMin.setDisabled(false);
			} else {
				this.delayFrequencyHour.setValue("");
				this.delayFrequencyHour.setDisabled(true);
				this.delayFrequencyMin.setValue("");
				this.delayFrequencyMin.setDisabled(true);
			}
		} else {
			this.eMailNotificationsRequired.setChecked(false);
			this.eMailNotificationsRequired.setDisabled(true);
			this.publishNotificationsRequired.setChecked(false);
			this.publishNotificationsRequired.setDisabled(true);
			this.reminderFrequencyHour.setValue("");
			this.reminderFrequencyHour.setDisabled(true);
			this.reminderFrequencyMin.setValue("");
			this.reminderFrequencyMin.setDisabled(true);
			this.delayRequired.setDisabled(true);
			this.delayFrequencyHour.setValue("");
			this.delayFrequencyHour.setDisabled(true);
			this.delayFrequencyMin.setValue("");
			this.delayFrequencyMin.setDisabled(true);
		}

	}

	public void onCheck$delayRequired(Event event) {
		CheckDelayNotifVisibility();
	}

	private void CheckDelayNotifVisibility() {
		this.delayRequired.setDisabled(false);
		if (this.delayRequired.isChecked()) {
			this.delayFrequencyHour.setDisabled(false);
			this.delayFrequencyMin.setDisabled(false);
		} else {
			this.delayFrequencyHour.setValue("");
			this.delayFrequencyHour.setDisabled(true);
			this.delayFrequencyMin.setValue("");
			this.delayFrequencyMin.setDisabled(true);
		}
	}

	public void onCheck$extMnthRequired(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.extMnthRequired.isChecked()) {
			this.mnthExtTo.setDisabled(false);
		} else {
			this.mnthExtTo.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	private String toCron(Date cronTime) {
		String format = DateUtil.format(cronTime, "ss:mm:HH");
		format = format.replace(":", " ");

		return String.format("%s * * ?", format);
	}

	private Date cronToDate(String cronExp) {
		if (cronExp == null) {
			return null;
		}
		final CronSequenceGenerator generator = new CronSequenceGenerator(cronExp);

		return generator.next(DateUtil.addDays(DateUtil.getSysDate(), -1));
	}

}
