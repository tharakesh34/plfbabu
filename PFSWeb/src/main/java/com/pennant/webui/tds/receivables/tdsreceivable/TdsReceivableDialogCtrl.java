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
 * * FileName : TdsReceivableDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * *
 * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.tds.receivables.tdsreceivable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.service.tds.receivables.TdsReceivableService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/tds.receivables/TdsReceivable/tdsReceivableDialog.zul file. <br>
 */
public class TdsReceivableDialogCtrl extends GFCBaseCtrl<TdsReceivable> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TdsReceivableDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TdsReceivableDialog;
	protected ExtendedCombobox tanNumber;
	protected Long tanID;
	protected Button btnBrowse;
	protected Grid grid_basicDetails;
	protected Div docDiv;
	protected Space space_CertificateNumber;
	protected Space space_UploadCertificate;
	protected Textbox certificateNumber;
	protected Datebox certificateDate;
	protected CurrencyBox certificateAmount;
	protected Combobox assessmentYear;
	protected Datebox dateOfReceipt;
	protected Combobox certificateQuarter;
	protected Textbox uploadCertificate;
	private TdsReceivable tdsReceivable; // overhanded per param
	private List<ValueLabel> financialYear;
	protected Button btnView;

	private transient TdsReceivableListCtrl tdsReceivableListCtrl; // overhanded
																	// per param
	private transient TdsReceivableService tdsReceivableService;

	/**
	 * default constructor.<br>
	 */
	public TdsReceivableDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TdsReceivableDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.tdsReceivable.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_TdsReceivableDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TdsReceivableDialog);

		try {
			// Get the required arguments.
			this.tdsReceivable = (TdsReceivable) arguments.get("tdsReceivable");
			this.tdsReceivableListCtrl = (TdsReceivableListCtrl) arguments.get("tdsReceivableListCtrl");

			if (this.tdsReceivable == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			TdsReceivable tdsReceivable = new TdsReceivable();
			BeanUtils.copyProperties(this.tdsReceivable, tdsReceivable);
			this.tdsReceivable.setBefImage(tdsReceivable);

			// Documents Details
			if (tdsReceivable.getDocumentDetails() != null) {
				this.btnView.addForward("onClick", this.window_TdsReceivableDialog, "onClickViewButton",
						tdsReceivable.getDocumentDetails());
			} else {
				this.btnView.setDisabled(true);
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.tdsReceivable.isWorkflow(), this.tdsReceivable.getWorkflowId(),
					this.tdsReceivable.getNextTaskId());

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
			doShowDialog(this.tdsReceivable);
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

		this.tanNumber.setMaxlength(19);
		this.certificateDate.setFormat(PennantConstants.dateFormat);
		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		this.certificateAmount.setProperties(false, formatter);
		this.certificateAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.certificateAmount.setMandatory(true);
		this.assessmentYear.setMaxlength(50);
		this.dateOfReceipt.setFormat(PennantConstants.dateFormat);

		setStatusDetails();

		this.tanNumber.setButtonDisabled(false);
		this.tanNumber.setTextBoxWidth(140);
		this.tanNumber.setMandatoryStyle(true);
		this.tanNumber.setModuleName("TanDetail");
		this.tanNumber.setValueColumn("TanNumber");
		this.tanNumber.setValidateColumns(new String[] { "TanNumber" });

		logger.debug(Literal.LEAVING);
	}

	public static int getYearFromDate(Date date) {
		int result = -1;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			result = cal.get(Calendar.YEAR);
		}
		return result;
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableDialog_btnSave"));
		this.btnView.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableDialog_btnView"));
		this.btnBrowse.setVisible(getUserWorkspace().isAllowed("button_TdsReceivableDialog_btnBrowse"));
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
		doShowNotes(this.tdsReceivable);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		tdsReceivableListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.tdsReceivable.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param tdsReceivable
	 * 
	 */
	public void doWriteBeanToComponents(TdsReceivable aTdsReceivable) {
		logger.debug(Literal.ENTERING);

		this.tanNumber.setValue(aTdsReceivable.getTanNumber());
		this.certificateNumber.setValue(aTdsReceivable.getCertificateNumber());
		this.certificateDate.setValue(aTdsReceivable.getCertificateDate());
		this.certificateQuarter.setValue(aTdsReceivable.getCertificateQuarter());
		this.certificateAmount.setValue(PennantApplicationUtil.formateAmount(aTdsReceivable.getCertificateAmount(),
				PennantConstants.defaultCCYDecPos));
		this.assessmentYear.setValue(aTdsReceivable.getAssessmentYear());
		if (aTdsReceivable.getDateOfReceipt() == null) {
			this.dateOfReceipt.setValue(SysParamUtil.getAppDate());
		} else {
			this.dateOfReceipt.setValue(aTdsReceivable.getDateOfReceipt());
		}
		this.uploadCertificate.setValue(aTdsReceivable.getDocName());
		this.uploadCertificate.setAttribute("data", aTdsReceivable);

		fillComboBox(certificateQuarter, aTdsReceivable.getCertificateQuarter(),
				PennantStaticListUtil.getCertificateQuarter(), "");
		fillComboBox(assessmentYear, aTdsReceivable.getAssessmentYear(), tdsReceivableListCtrl.getAssesmentYearList(),
				"");

		AMedia amedia = null;

		if (aTdsReceivable.getDocImage() != null) {
			if (aTdsReceivable.getDocType().equals(PennantConstants.DOC_TYPE_WORD)
					|| aTdsReceivable.getDocType().equals(PennantConstants.DOC_TYPE_MSG)
					|| aTdsReceivable.getDocType().equals(PennantConstants.DOC_TYPE_ZIP)
					|| aTdsReceivable.getDocType().equals(PennantConstants.DOC_TYPE_7Z)
					|| aTdsReceivable.getDocType().equals(PennantConstants.DOC_TYPE_RAR)
					|| aTdsReceivable.getDocType().equals(PennantConstants.DOC_TYPE_EXCEL)) {
				this.docDiv.getChildren().clear();
				this.docDiv.appendChild(getDocumentLink(aTdsReceivable.getDocName(), aTdsReceivable.getDocType(),
						this.uploadCertificate.getValue(), aTdsReceivable.getDocImage()));
			} else {
				amedia = new AMedia(aTdsReceivable.getDocName(), null, null, aTdsReceivable.getDocImage());
			}
		}
		this.recordStatus.setValue(aTdsReceivable.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTdsReceivable
	 */
	public void doWriteComponentsToBean(TdsReceivable aTdsReceivable) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();
		Date appStartDate = SysParamUtil.getAppDate();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (aTdsReceivable.getTanID() == 0) {

				TanDetail tanDetails = (TanDetail) this.tanNumber.getObject();
				aTdsReceivable.setTanID(tanDetails.getId());
			} else
				aTdsReceivable.setTanID(aTdsReceivable.getTanID());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTdsReceivable.setTanNumber(this.tanNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Number
		try {
			aTdsReceivable.setCertificateNumber(this.certificateNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Upload On
		try {
			aTdsReceivable.setCertificateDate(this.certificateDate.getValue());
			if (DateUtil.compare(this.certificateDate.getValue(), appStartDate) > 0) {
				throw new WrongValueException(this.certificateDate,
						Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
								new String[] { DateUtil.formatToLongDate(this.certificateDate.getValue()),
										DateUtil.formatToLongDate(appStartDate) }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Amount
		try {
			if (this.certificateAmount.getActualValue() != null) {
				aTdsReceivable.setCertificateAmount(PennantApplicationUtil
						.unFormateAmount(this.certificateAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
			} else {
				aTdsReceivable.setCertificateAmount(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Assessment Year
		try {
			aTdsReceivable.setAssessmentYear(this.assessmentYear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Date Of Receipt
		try {
			aTdsReceivable.setDateOfReceipt(this.dateOfReceipt.getValue());
			Date receiptDate = (Date) this.dateOfReceipt.getValue();

			if (DateUtil.compare(receiptDate, this.certificateDate.getValue()) < 0
					|| DateUtil.compare(receiptDate, appStartDate) > 0) {
				throw new WrongValueException(this.dateOfReceipt,
						Labels.getLabel("DATE_ALLOWED_RANGE",
								new String[] { Labels.getLabel("label_TdsReceivableDialog_DateOfReceipt.value"),
										DateUtil.formatToLongDate(this.certificateDate.getValue()),
										DateUtil.formatToLongDate(appStartDate) }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Certificate Quarter
		try {
			aTdsReceivable.setCertificateQuarter(this.certificateQuarter.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Doc I D
		try {
			aTdsReceivable.setUploadCertificate(this.uploadCertificate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTdsReceivable.setBalanceAmount(PennantApplicationUtil
					.unFormateAmount(this.certificateAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aTdsReceivable.setDocName(this.uploadCertificate.getValue());
			TdsReceivable details = (TdsReceivable) this.uploadCertificate.getAttribute("data");
			aTdsReceivable.setDocImage(details.getDocImage());
			aTdsReceivable.setDocType(details.getDocType());
			aTdsReceivable.setDocRefId(Long.MIN_VALUE);
			setTdsReceivable(aTdsReceivable);

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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param tdsReceivable The entity that need to be render.
	 */
	public void doShowDialog(TdsReceivable tdsReceivable) {
		logger.debug(Literal.LEAVING);

		if (tdsReceivable.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.certificateNumber.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(tdsReceivable.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.certificateNumber.focus();
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

		doWriteBeanToComponents(tdsReceivable);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.tanNumber.isReadonly()) {
			this.tanNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TdsReceivableDialog_TanNumber.value"),
							PennantRegularExpressions.REGEX_TAN_NUMBER, true));
		}
		if (!this.certificateNumber.isReadonly()) {
			this.certificateNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TdsReceivableDialog_CertificateNumber.value"),
							PennantRegularExpressions.REGEX_CERTIFICATE_NUMBER, true));
		}
		if (!this.certificateDate.isReadonly()) {
			this.certificateDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_TdsReceivableDialog_CertificateDate.value"), true));
		}
		if (!this.certificateAmount.isReadonly()) {
			this.certificateAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_TdsReceivableDialog_CertificateAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}
		if (!this.assessmentYear.isDisabled()) {
			this.assessmentYear.setConstraint(new StaticListValidator(tdsReceivableListCtrl.getAssesmentYearList(),
					Labels.getLabel("label_TdsReceivableDialog_AssessmentYear.value")));
		}
		if (!this.dateOfReceipt.isReadonly()) {
			this.dateOfReceipt.setConstraint(
					new PTDateValidator(Labels.getLabel("label_TdsReceivableDialog_DateOfReceipt.value"), true));
		}
		if (!this.uploadCertificate.isDisabled()) {
			this.uploadCertificate.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TdsReceivableDialog_UploadCertificate.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}

		if (!this.certificateQuarter.isDisabled()) {
			this.certificateQuarter.setConstraint(new StaticListValidator(PennantStaticListUtil.getCertificateQuarter(),
					Labels.getLabel("label_TdsReceivableDialog_CertificateQuarter.value")));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.tanNumber.setConstraint("");
		this.certificateNumber.setConstraint("");
		this.certificateDate.setConstraint("");
		this.certificateAmount.setConstraint("");
		this.assessmentYear.setConstraint("");
		this.dateOfReceipt.setConstraint("");
		this.certificateQuarter.setConstraint("");
		this.uploadCertificate.setConstraint("");

		logger.debug(Literal.LEAVING);
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
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.tanNumber.setErrorMessage("");
		this.certificateNumber.setErrorMessage("");
		this.certificateDate.setErrorMessage("");
		this.certificateAmount.setErrorMessage("");
		this.assessmentYear.setErrorMessage("");
		this.dateOfReceipt.setErrorMessage("");
		this.certificateQuarter.setErrorMessage("");
		this.uploadCertificate.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a TdsReceivable object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final TdsReceivable aTdsReceivable = new TdsReceivable();
		BeanUtils.copyProperties(this.tdsReceivable, aTdsReceivable);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aTdsReceivable.getCertificateNumber();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aTdsReceivable.getRecordType()).equals("")) {
				aTdsReceivable.setVersion(aTdsReceivable.getVersion() + 1);
				aTdsReceivable.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTdsReceivable.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aTdsReceivable.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTdsReceivable.getNextTaskId(),
							aTdsReceivable);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aTdsReceivable, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnView(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tdsReceivable", this.tdsReceivable);
		Executions.createComponents("/WEB-INF/pages/Finance/TdsReceivableCancel/TdsReceivableCancelView.zul", null,
				map);

		logger.debug(Literal.LEAVING);
	}

	public void onUpload$btnBrowse(UploadEvent event) throws InterruptedException {
		Media media = event.getMedia();
		browseDoc(media, this.uploadCertificate);
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			List<DocType> allowed = new ArrayList<>();
			allowed.add(DocType.PDF);
			allowed.add(DocType.JPEG);
			allowed.add(DocType.JPG);
			allowed.add(DocType.PNG);
			allowed.add(DocType.DOC);
			allowed.add(DocType.DOCX);
			allowed.add(DocType.ZIP);
			allowed.add(DocType.Z7);
			allowed.add(DocType.RAR);
			allowed.add(DocType.TXT);
			allowed.add(DocType.MSG);

			if (!MediaUtil.isValid(media, allowed)) {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
				return;
			}

			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else if ("application/x-zip-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_ZIP;
			} else if ("application/octet-stream".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_7Z;
			} else if ("application/x-rar-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_RAR;
			} else if (media.getName().endsWith(".xls") || media.getName().endsWith(".xlsx")) {
				docType = PennantConstants.DOC_TYPE_EXCEL;
			} else if ("text/plain".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_TXT;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = null;
			if (docType.equals(PennantConstants.DOC_TYPE_TXT)) {
				String data = media.getStringData();
				ddaImageData = data.getBytes();
			} else {
				ddaImageData = IOUtils.toByteArray(media.getStreamData());
			}

			textbox.setValue(fileName);
			if (textbox.getAttribute("data") == null) {
				TdsReceivable documentDetails = new TdsReceivable("", docType, fileName, ddaImageData);
				textbox.setAttribute("data", documentDetails);
			} else {
				TdsReceivable documentDetails = (TdsReceivable) textbox.getAttribute("data");
				documentDetails.setDocType(docType);
				documentDetails.setDocImage(ddaImageData);
				textbox.setAttribute("data", documentDetails);
			}
		} catch (Exception ex) {
			logger.error(Literal.EXCEPTION, ex);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.tdsReceivable.isNewRecord()) {
			this.tanNumber.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.tanNumber.setReadonly(true);
		}
		this.certificateNumber.setReadonly(isReadOnly("TdsReceivableDialog_CertificateNumber"));
		this.certificateDate.setDisabled(isReadOnly("TdsReceivableDialog_CertificateDate"));
		this.dateOfReceipt.setDisabled(isReadOnly("TdsReceivableDialog_DateOfReceipt"));
		this.certificateAmount.setReadonly(isReadOnly("TdsReceivableDialog_CertificateAmount"));
		this.assessmentYear.setDisabled(isReadOnly("TdsReceivableDialog_AssessmentYear"));
		this.certificateQuarter.setDisabled(isReadOnly("TdsReceivableDialog_CertificateQuarter"));
		this.uploadCertificate.setDisabled(isReadOnly("TdsReceivableDialog_UploadCertificate"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.tdsReceivable.isNewRecord()) {
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

		this.tanNumber.setReadonly(true);
		this.certificateNumber.setReadonly(true);
		this.certificateDate.setReadonly(true);
		this.dateOfReceipt.setReadonly(true);
		this.certificateAmount.setReadonly(true);
		this.assessmentYear.setDisabled(true);
		this.certificateQuarter.setDisabled(true);
		this.uploadCertificate.setDisabled(true);

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

		this.tanNumber.setValue("");
		this.certificateNumber.setValue("");
		this.certificateDate.setText("");
		this.certificateAmount.setValue("");
		this.assessmentYear.setValue("");
		this.dateOfReceipt.setText("");
		this.certificateQuarter.setSelectedIndex(0);
		this.uploadCertificate.setText("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final TdsReceivable aTdsReceivable = new TdsReceivable();
		BeanUtils.copyProperties(this.tdsReceivable, aTdsReceivable);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aTdsReceivable);

		isNew = aTdsReceivable.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTdsReceivable.getRecordType())) {
				aTdsReceivable.setVersion(aTdsReceivable.getVersion() + 1);
				if (isNew) {
					aTdsReceivable.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTdsReceivable.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTdsReceivable.setNewRecord(true);
				}
			}
		} else {
			aTdsReceivable.setVersion(aTdsReceivable.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aTdsReceivable, tranType)) {
				refreshList();
				// Confirmation message
				String msg = PennantApplicationUtil.getstatus(aTdsReceivable.getRoleCode(),
						aTdsReceivable.getNextRoleCode(), aTdsReceivable.getCertificateNumber(), " TDS Certificate:",
						aTdsReceivable.getRecordStatus());
				if (StringUtils.equals(aTdsReceivable.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " TDS Certificate:" + aTdsReceivable.getCertificateNumber() + " Approved Successfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		} catch (Exception e) {
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
	 * @throws Exception
	 * 
	 */
	protected boolean doProcess(TdsReceivable aTdsReceivable, String tranType) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aTdsReceivable.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTdsReceivable.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTdsReceivable.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aTdsReceivable.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aTdsReceivable.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aTdsReceivable);
				}

				if (isNotesMandatory(taskId, aTdsReceivable)) {
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

			aTdsReceivable.setTaskId(taskId);
			aTdsReceivable.setNextTaskId(nextTaskId);
			aTdsReceivable.setRoleCode(getRole());
			aTdsReceivable.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aTdsReceivable, tranType);
			String operationRefs = getServiceOperations(taskId, aTdsReceivable);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aTdsReceivable, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aTdsReceivable, tranType);
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
	 * @throws Exception
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TdsReceivable aTdsReceivable = (TdsReceivable) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = tdsReceivableService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = tdsReceivableService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = tdsReceivableService.doApprove(auditHeader);

					if (aTdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = tdsReceivableService.doReject(auditHeader);
					if (aTdsReceivable.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_TdsReceivableDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_TdsReceivableDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.tdsReceivable), true);
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

	private AuditHeader getAuditHeader(TdsReceivable aTdsReceivable, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTdsReceivable.getBefImage(), aTdsReceivable);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aTdsReceivable.getUserDetails(),
				getOverideMap());
	}

	public void setTdsReceivableService(TdsReceivableService tdsReceivableService) {
		this.tdsReceivableService = tdsReceivableService;
	}

	public void setTdsReceivable(TdsReceivable tdsReceivable) {
		this.tdsReceivable = tdsReceivable;
	}

}