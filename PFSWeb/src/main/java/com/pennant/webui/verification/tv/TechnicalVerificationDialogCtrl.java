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
package com.pennant.webui.verification.tv;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Verification/TechnicalVerification/technicalVerificationDialog.zul
 * file. <br>
 */
public class TechnicalVerificationDialogCtrl extends GFCBaseCtrl<TechnicalVerification> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TechnicalVerificationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TechnicalVerificationDialog;

	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox custName;
	protected Textbox collateralType;
	protected Textbox contactNumber1;
	protected Textbox contactNumber2;
	protected Textbox collateralReference;

	protected Tabpanel extendedFieldTabPanel;

	protected Groupbox gb_summary;
	protected Textbox agentCode;
	protected Textbox agentName;
	protected Combobox recommendations;
	protected ExtendedCombobox reason;
	protected Textbox summaryRemarks;
	protected Space space_Reason;
	protected CurrencyBox valuationAmount;
	
	protected Tab documentDetails;

	private TechnicalVerification technicalVerification;

	private transient TechnicalVerificationListCtrl technicalVerificationListCtrl;

	@Autowired
	private transient TechnicalVerificationService technicalVerificationService;
	private transient CollateralSetupService	collateralSetupService;

	//Extended fields
	private ExtendedFieldCtrl					extendedFieldCtrl				= null;
	/**
	 * default constructor.<br>
	 */
	public TechnicalVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TechnicalVerificationDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_TechnicalVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TechnicalVerificationDialog);

		try {
			// Get the required arguments.
			this.technicalVerification = (TechnicalVerification) arguments.get("technicalVerification");
			this.technicalVerificationListCtrl = (TechnicalVerificationListCtrl) arguments.get("technicalVerificationListCtrl");

			if (this.technicalVerification == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			TechnicalVerification technicalVerification = new TechnicalVerification();
			BeanUtils.copyProperties(this.technicalVerification, technicalVerification);
			this.technicalVerification.setBefImage(technicalVerification);

			// Render the page and display the data.
			doLoadWorkFlow(this.technicalVerification.isWorkflow(), this.technicalVerification.getWorkflowId(),
					this.technicalVerification.getNextTaskId());

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
			doShowDialog(this.technicalVerification);
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
		logger.debug("Entering");

		this.reason.setMaxlength(8);
		this.reason.setMandatoryStyle(false);
		this.reason.setModuleName("FIStatusReason");
		this.reason.setValueColumn("Code");
		this.reason.setDescColumn("Description");
		this.reason.setValidateColumns(new String[] { "Code" });
		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(20);
		this.summaryRemarks.setMaxlength(50);

		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TechnicalVerificationDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws ParseException 
	 */
	public void onClick$btnSave(Event event) throws ParseException {
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
	 * The framework calls this event handler when user clicks the delete
	 * button.
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
	 * The framework calls this event handler when user clicks the cancel
	 * button.
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
		doShowNotes(this.technicalVerification);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		technicalVerificationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.technicalVerification.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reason(Event event) {
		logger.debug("Entering");
		Object dataObject = reason.getObject();
		if (dataObject instanceof String) {
			this.reason.setValue(dataObject.toString());
			this.reason.setDescription("");
			this.reason.setAttribute("ReasonId", null);
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			if (details != null) {
				this.reason.setAttribute("ReasonId", details.getId());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param fi
	 * 
	 */
	public void doWriteBeanToComponents(TechnicalVerification technicalVerification) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue(technicalVerification.getCustCif());
		this.finReference.setValue(technicalVerification.getKeyReference());
		this.custName.setValue(technicalVerification.getName());
		this.collateralType.setValue(technicalVerification.getCollateralType());
		this.contactNumber1.setValue(technicalVerification.getContactNumber1());
		this.contactNumber2.setValue(technicalVerification.getContactNumber2());
		this.collateralReference.setValue(technicalVerification.getCollateralRef());
		this.agentCode.setValue(technicalVerification.getAgentCode());
		this.agentName.setValue(technicalVerification.getAgentName());
		this.recommendations.setValue(String.valueOf(technicalVerification.getStatus()));
		if (!technicalVerification.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty((technicalVerification.getReasonCode())),
					StringUtils.trimToEmpty(technicalVerification.getReasonDesc()));
			if (technicalVerification.getReason() != null) {
				this.reason.setAttribute("ReasonId", technicalVerification.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}

		if (!technicalVerification.isNewRecord()) {
			visibleComponent(technicalVerification.getStatus());
		}

		this.summaryRemarks.setValue(technicalVerification.getSummaryRemarks());
		fillComboBox(this.recommendations, technicalVerification.getStatus(), FIStatus.getList());

		// Extended Field Details
		appendExtendedFieldDetails(technicalVerification);
		
		this.recordStatus.setValue(technicalVerification.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails(TechnicalVerification technicalVerification) {
		logger.debug("Entering");

		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.MODULE_NAME, technicalVerification.getCollateralType(), ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION);

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append( CollateralConstants.VERIFICATION_MODULE);
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_TV");
			
			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(technicalVerification.getCollateralRef(), tableName.toString());
			this.extendedFieldTabPanel.setHeight((borderLayoutHeight-280)+"px");
			extendedFieldCtrl.setTabpanel(extendedFieldTabPanel);
			technicalVerification.setExtendedFieldHeader(extendedFieldHeader);
			technicalVerification.setExtendedFieldRender(extendedFieldRender);

			if (technicalVerification.getBefImage() != null) {
				technicalVerification.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				technicalVerification.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(/*isReadOnly("CustomerDialog_custFirstName")*/ false);
			extendedFieldCtrl.setWindow(this.window_TechnicalVerificationDialog);
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		logger.debug("Leaving");
	}
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @throws ParseException 
	 */
	public void doWriteComponentsToBean(TechnicalVerification technicalVerification) throws ParseException {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		technicalVerification.setCustCif(this.custCIF.getValue());
		technicalVerification.setKeyReference(this.finReference.getValue());
		technicalVerification.setName(this.custName.getValue());
		technicalVerification.setContactNumber1(this.contactNumber1.getValue());
		technicalVerification.setContactNumber2(this.contactNumber2.getValue());

		try {
			technicalVerification.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			technicalVerification.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("0".equals(getComboboxValue(this.recommendations))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_TechnicalVerificationDialog_Recommendations.value") }));
			} else {
				technicalVerification.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			technicalVerification.setReasonDesc(this.reason.getDescription());
			technicalVerification.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				technicalVerification.setReason((Long.parseLong(object.toString())));
			} else {
				technicalVerification.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			technicalVerification.setSummaryRemarks(this.summaryRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Extended Field validations
		if (technicalVerification.getExtendedFieldHeader() != null) {
			technicalVerification.setExtendedFieldRender(extendedFieldCtrl.save());
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$recommendations(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.reason.setErrorMessage("");
		String type = this.recommendations.getSelectedItem().getValue();
		visibleComponent(Integer.parseInt(type));
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void visibleComponent(Integer type) {
		if (type == FIStatus.NOT_COMPLETED.getKey()) {
			this.reason.setMandatoryStyle(true);
		} else {
			this.reason.setMandatoryStyle(false);
		}
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param technicalVerification
	 *            The entity that need to be render.
	 */
	public void doShowDialog(TechnicalVerification technicalVerification) {
		logger.debug(Literal.LEAVING);

		if (technicalVerification.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(technicalVerification.getRecordType())) {
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

		doWriteBeanToComponents(technicalVerification);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "Collateral Reference" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCollateralRef(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		CollateralSetup collateralSetup = getCollateralSetupService().getCollateralSetupByRef(this.collateralReference.getValue(), "", true);
		if (collateralSetup != null) {
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null, map);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}
	
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_AgentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(new PTListValidator(
					Labels.getLabel("label_TechnicalVerificationDialog_Status.value"), FIStatus.getList(), true));
		}
		if (!this.reason.isReadonly() && this.reason.isMandatory()) {
			this.reason.setConstraint(new PTStringValidator(
					Labels.getLabel("label_TechnicalVerificationDialog_Reason.value"), null, this.reason.isMandatory()));
		}

		if (!this.summaryRemarks.isReadonly()) {
			this.summaryRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TechnicalVerificationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.agentCode.setConstraint("");
		this.agentName.setConstraint("");
		this.recommendations.setConstraint("");
		this.reason.setConstraint("");
		this.summaryRemarks.setConstraint("");
		this.summaryRemarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog
	 * controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a TechnicalVerification object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final TechnicalVerification entity = new TechnicalVerification();
		BeanUtils.copyProperties(this.technicalVerification, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ entity.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(entity.getRecordType()).equals("")) {
				entity.setVersion(entity.getVersion() + 1);
				entity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					entity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					entity.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), entity.getNextTaskId(), entity);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(entity, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.technicalVerification.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_Remarks"), this.summaryRemarks);
		readOnlyComponent(isReadOnly("TechnicalVerificationDialog_ValuationAmount"), this.valuationAmount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.technicalVerification.isNewRecord()) {
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

		this.custCIF.setReadonly(true);
		this.finReference.setReadonly(true);
		this.custName.setReadonly(true);
		this.contactNumber1.setReadonly(true);
		this.contactNumber2.setReadonly(true);

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
	 * Saves the components to table. <br>
	 * @throws ParseException 
	 */
	public void doSave() throws ParseException {
		logger.debug("Entering");
		final TechnicalVerification fi = new TechnicalVerification();
		BeanUtils.copyProperties(this.technicalVerification, fi);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(fi);

		isNew = fi.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(fi.getRecordType())) {
				fi.setVersion(fi.getVersion() + 1);
				if (isNew) {
					fi.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					fi.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					fi.setNewRecord(true);
				}
			}
		} else {
			fi.setVersion(fi.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(fi, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
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
	private boolean doProcess(TechnicalVerification technicalVerification, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		technicalVerification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		technicalVerification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		technicalVerification.setUserDetails(getUserWorkspace().getLoggedInUser());

		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			technicalVerification.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(technicalVerification.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, technicalVerification);
				}

				if (isNotesMandatory(taskId, technicalVerification)) {
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

			technicalVerification.setTaskId(taskId);
			technicalVerification.setNextTaskId(nextTaskId);
			technicalVerification.setRoleCode(getRole());
			technicalVerification.setNextRoleCode(nextRoleCode);


			// Extended Field details
			if (technicalVerification.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = technicalVerification.getExtendedFieldRender();
				details.setReference(technicalVerification.getCollateralRef());
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(technicalVerification.getRecordStatus());
				details.setRecordType(technicalVerification.getRecordType());
				details.setVersion(technicalVerification.getVersion());
				details.setWorkflowId(technicalVerification.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(technicalVerification.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(technicalVerification.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(technicalVerification.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			
			auditHeader = getAuditHeader(technicalVerification, tranType);
			String operationRefs = getServiceOperations(taskId, technicalVerification);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(technicalVerification, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(technicalVerification, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

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
		TechnicalVerification technicalVerification = (TechnicalVerification) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = technicalVerificationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = technicalVerificationService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = technicalVerificationService.doApprove(auditHeader);

						if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = technicalVerificationService.doReject(auditHeader);
						if (technicalVerification.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_TechnicalVerificationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_TechnicalVerificationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.technicalVerification), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TechnicalVerification technicalVerification, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, technicalVerification.getBefImage(), technicalVerification);
		return new AuditHeader(getReference(), null, null, null, auditDetail, technicalVerification.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.technicalVerification.getId());
	}

	public void setTechnicalVerificationService(TechnicalVerificationService technicalVerificationService) {
		this.technicalVerificationService = technicalVerificationService;
	}
	
	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	private void fillComboBox(Combobox combobox, int value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (Integer.parseInt(valueLabel.getValue()) == value) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}
}
