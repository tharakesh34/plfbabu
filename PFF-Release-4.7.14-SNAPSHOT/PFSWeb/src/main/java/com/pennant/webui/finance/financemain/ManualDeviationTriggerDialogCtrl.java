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
 * FileName    		:  FinAdvancePaymentsDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.ProductDeviation;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/ManualDeviationTriggerDialog.zul file.
 */
public class ManualDeviationTriggerDialogCtrl extends GFCBaseCtrl<FinanceDeviations> {
	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(ManualDeviationTriggerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_ManualDeviationTrigger;

	protected Groupbox					gb_statusDetails;
	private boolean						enqModule			= false;

	private ExtendedCombobox			deviationCode;
	private Combobox					delegationRole;

	// not auto wired vars
	private FinanceDeviations			financeDeviations;

	private transient boolean			newFinance;

	// ServiceDAOs / Domain Classes
	private transient PagedListService	pagedListService;

	private Object						financeMainDialogCtrl;
	private DeviationDetailDialogCtrl	deviationDetailDialogCtrl;
	private boolean						newRecord			= false;
	private boolean						newCustomer			= false;
	private Textbox						remarks;
	private Combobox					status;
	private Row							row_ApprovelStatus;

	private List<FinanceDeviations>		financeDeviationsList;
	private List<ValueLabel>			delegator			= null;
	private FinanceMain					financeMain;
	@Autowired
	private DeviationHelper				deviationHelper;
	private String						prodCode;
	ArrayList<ValueLabel>				approveStatus	= PennantStaticListUtil.getApproveStatus();
	/**
	 * default constructor.<br>
	 */
	public ManualDeviationTriggerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinAdvancePaymentsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinAdvancePaymentsDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ManualDeviationTrigger(Event event) throws Exception {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_ManualDeviationTrigger);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
				delegator = deviationHelper.getRoleAndDesc(financeMain.getWorkflowId());
				prodCode = financeMain.getFinCategory();
			} else {
				financeMain = null;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("financeDeviations")) {
				this.financeDeviations = (FinanceDeviations) arguments.get("financeDeviations");
				FinanceDeviations befImage = new FinanceDeviations();
				BeanUtils.copyProperties(this.financeDeviations, befImage);
				this.financeDeviations.setBefImage(befImage);

				setFinanceDeviations(this.financeDeviations);
			} else {
				setFinanceDeviations(null);
			}

			if (this.financeDeviations.isNewRecord()) {
				setNewRecord(true);
			}
			if (arguments.containsKey("DeviationDetailDialogCtrl")) {
				setDeviationDetailDialogCtrl((DeviationDetailDialogCtrl) arguments.get("DeviationDetailDialogCtrl"));
				setNewCustomer(true);
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {

				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.financeDeviations.setWorkflowId(0);
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			doLoadWorkFlow(this.financeDeviations.isWorkflow(), this.financeDeviations.getWorkflowId(),
					this.financeDeviations.getNextTaskId());

			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			//getUserWorkspace().allocateAuthorities("FinAdvancePaymentsDialog", getRole());

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.financeDeviations);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ManualDeviationTrigger);
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
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeDeviations);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeDeviations.getFinReference());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinAdvancePaymentsDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDeviations aFinanceDeviations) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceDeviations.isNew()) {

			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			//			this.paymentDetail.focus();
		} else {
			//			this.paymentDetail.focus();
			if (isNewFinance()) {
				if (enqModule) {
					doReadOnly();
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			//### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations

			if((getUserWorkspace().getUserRoles().contains(aFinanceDeviations.getUserRole()))) {
				btnDelete.setVisible(true);
			}
			// ### 01-05-2018 - End
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceDeviations);

			this.window_ManualDeviationTrigger.setHeight("70%");
			this.window_ManualDeviationTrigger.setWidth("85%");
			this.gb_statusDetails.setVisible(false);
			this.window_ManualDeviationTrigger.doModal();

		} catch (Exception e) {
			this.window_ManualDeviationTrigger.onClose();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (this.financeDeviations.isNewRecord()) {
			readOnlyComponent(false, this.deviationCode);
		} else {
			readOnlyComponent(true, this.deviationCode);
		}

		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

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

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
//			this.btnNew.setVisible(true);
//			this.btnEdit.setVisible(true);
//			this.btnDelete.setVisible(true);
			this.btnSave.setVisible(true);
			//### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations
			readOnlyComponent(true, delegationRole);
			readOnlyComponent(true, status);
			// ### 01-05-2018 - End
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.deviationCode.setProperties("ProductDeviation", "DeviationCode", "DeviationDesc", true, 1, 155);
		//		this.deviationCode.setModuleName("ProductDeviation");
		//		this.deviationCode.setMandatoryStyle(true);
		//		this.deviationCode.setValueColumn("DeviationCode");
		//		this.deviationCode.setDescColumn("DeviationDesc");
		//		this.deviationCode.setDisplayStyle(1);
		//	this.deviationCode.setValidateColumns(new String[] { "DeviationCode" });
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("ProductCode", prodCode, Filter.OP_EQUAL);
		this.deviationCode.setFilters(filters);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
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
		doWriteBeanToComponents(this.financeDeviations.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinAdvancePayments
	 *            FinAdvancePaymentsDetail
	 */
	public void doWriteBeanToComponents(FinanceDeviations aFinanceDeviations) {
		logger.debug("Entering");

		aFinanceDeviations.setModule(DeviationConstants.TY_LOAN);
		fillComboBox(delegationRole, aFinanceDeviations.getDelegationRole(), delegator, "");
		//### 01-05-2018 - story #361(tuleap server) Manual Deviations
		fillComboBox(status, aFinanceDeviations.getApprovalStatus(), approveStatus, "");

		String deviationCode = aFinanceDeviations.getDeviationCode();
		//### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations

		if(aFinanceDeviations.isNewRecord()||StringUtils.equals(aFinanceDeviations.getRecordType(), PennantConstants.RCD_ADD)||(getUserWorkspace().getUserRoles().contains(aFinanceDeviations.getUserRole()))) {
			readOnlyComponent(false, delegationRole);
		}
		
		if (DeviationConstants.MULTIPLE_APPROVAL) {
			if (getUserWorkspace().getUserRoles().contains(aFinanceDeviations.getDelegationRole())) {
				readOnlyComponent(false, status);
				readOnlyComponent(false, delegationRole);
			}
		}
		// ### 01-05-2018 - End
		if (StringUtils.isEmpty(deviationCode)) {
			deviationCode="0";
		}
		this.deviationCode.setAttribute("deviationCode", Long.parseLong(deviationCode));
		this.deviationCode.setAttribute("severity", aFinanceDeviations.getSeverity());
		this.deviationCode.setAttribute("severityName", aFinanceDeviations.getSeverityName());
		this.deviationCode.setValue(aFinanceDeviations.getDeviationCodeName(),
				aFinanceDeviations.getDeviationCodeDesc());
		this.remarks.setValue(aFinanceDeviations.getRemarks());

		//		this.recordStatus.setValue(aFinanceDeviations.getRecordStatus());
		//		this.recordType.setValue(PennantJavaUtil.getLabel(aFinanceDeviations.getRecordType()));

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceDeviations
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinanceDeviations aFinanceDeviations) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			Object deviatCode = this.deviationCode.getAttribute("deviationCode");
			if (deviatCode != null) {
				aFinanceDeviations.setDeviationCode(String.valueOf((long) deviatCode));
				aFinanceDeviations.setDeviationCodeName(this.deviationCode.getValue());
				aFinanceDeviations.setDeviationCodeDesc(this.deviationCode.getDescription());
				Object severity = this.deviationCode.getAttribute("severity");
				if (severity != null) {
					String severity2 = severity.toString();
					if (StringUtils.isNotEmpty(severity2)) {
						aFinanceDeviations.setSeverity(Long.parseLong(severity2));

					}

				}
				//Set severity Name to Product Manual Deviation bean
				Object severityName = this.deviationCode.getAttribute("severityName");
				if (severityName != null) {
					if (StringUtils.isNotEmpty(severityName.toString())) {
						aFinanceDeviations.setSeverityName(severityName.toString());
					}
				}
			} else {
				aFinanceDeviations.setDeviationCode("");
			}

		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			aFinanceDeviations.setDelegationRole(this.delegationRole.getSelectedItem().getValue());

		} catch (WrongValueException e) {
			wve.add(e);
		}
		//### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations
		if (this.status.isVisible()) {
			try {
				aFinanceDeviations.setApprovalStatus(this.status.getSelectedItem().getValue());
				long userId = getUserWorkspace().getLoggedInUser().getUserId();
				if (StringUtils.isBlank(aFinanceDeviations.getDelegatedUserId())) {
					aFinanceDeviations.setDelegatedUserId(String.valueOf(userId));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}
		}
		// ### 01-05-2018 - End
		aFinanceDeviations.setDeviationDate(new Timestamp(DateUtility.getAppDate().getTime()));
		aFinanceDeviations.setRemarks(this.remarks.getValue());

		doRemoveValidation();
		doClearMessage();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinanceDeviations.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a FinAdvancePaymentsDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinanceDeviations aFinAdvancePayments = new FinanceDeviations();
		BeanUtils.copyProperties(this.financeDeviations, aFinAdvancePayments);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n"
				+ Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentSequence.value") + " : "
				+ aFinAdvancePayments.getDeviationId();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinAdvancePayments.getRecordType())) {
				aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
				aFinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinAdvancePayments.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aFinAdvancePayments.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinAdvancePayments.getNextTaskId(),
							aFinAdvancePayments);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (isNewCustomer()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newFinAdvancePaymentsProcess(aFinAdvancePayments, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_ManualDeviationTrigger, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getDeviationDetailDialogCtrl().doFillManualDeviations(this.financeDeviationsList);
						closeDialog();
					}
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
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
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinanceDeviations aFinAdvancePayments = new FinanceDeviations();
		BeanUtils.copyProperties(this.financeDeviations, aFinAdvancePayments);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aFinAdvancePayments.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinAdvancePayments.getNextTaskId(),
					aFinAdvancePayments);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFinAdvancePayments.getRecordType()) && isValidation()) {
			doClearMessage();
			doSetValidation();
			// fill the FinAdvancePaymentsDetail object with the components data
			doWriteComponentsToBean(aFinAdvancePayments);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFinAdvancePayments.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinAdvancePayments.getRecordType())) {
				aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
				if (isNew) {
					aFinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinAdvancePayments.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aFinAdvancePayments.setVersion(1);
					aFinAdvancePayments.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aFinAdvancePayments.getRecordType())) {
					aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
					aFinAdvancePayments.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aFinAdvancePayments.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aFinAdvancePayments.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aFinAdvancePayments.setVersion(aFinAdvancePayments.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewCustomer()) {
				AuditHeader auditHeader = newFinAdvancePaymentsProcess(aFinAdvancePayments, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ManualDeviationTrigger, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getDeviationDetailDialogCtrl().doFillManualDeviations(this.financeDeviationsList);
					closeDialog();
				}
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFinAdvancePaymentsProcess(FinanceDeviations aFinanceDeviations, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aFinanceDeviations, tranType);
		financeDeviationsList = new ArrayList<FinanceDeviations>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(aFinanceDeviations.getFinReference());
		valueParm[1] = String.valueOf(aFinanceDeviations.getDeviationId());

		errParm[0] = PennantJavaUtil.getLabel("FinAdvancePayments_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("FinAdvancePayments_PaymentSeq") + ":" + valueParm[1];

		List<FinanceDeviations> listAdvance = null;
		listAdvance = getDeviationDetailDialogCtrl().getManualDeviationList();

		if (listAdvance != null && listAdvance.size() > 0) {
			for (int i = 0; i < listAdvance.size(); i++) {
				FinanceDeviations loanFinDev = listAdvance.get(i);
				if (StringUtils.equals(aFinanceDeviations.getDeviationCode(), loanFinDev.getDeviationCode())) { // Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aFinanceDeviations.getRecordType())) {
							aFinanceDeviations.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							financeDeviationsList.add(aFinanceDeviations);
						} else if (PennantConstants.RCD_ADD.equals(aFinanceDeviations.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFinanceDeviations.getRecordType())) {
							aFinanceDeviations.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							financeDeviationsList.add(aFinanceDeviations);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aFinanceDeviations.getRecordType())) {
							recordAdded = true;
							List<FinanceDeviations> listAdvanceApproved = null;
							listAdvanceApproved = getDeviationDetailDialogCtrl().getFinanceDetail()
									.getManualDeviations();
							for (int j = 0; j < listAdvanceApproved.size(); j++) {
								FinanceDeviations detail = listAdvanceApproved.get(j);
								if (detail.getFinReference() == aFinanceDeviations.getFinReference()
										&& detail.getDeviationCode().equals(aFinanceDeviations.getDeviationCode())) {
									financeDeviationsList.add(detail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							financeDeviationsList.add(loanFinDev);
						}
					}
				} else {
					financeDeviationsList.add(loanFinDev);
				}
			}
		}

		if (!recordAdded) {
			financeDeviationsList.add(aFinanceDeviations);
		}
		return auditHeader;
	}

	public void onFulfill$deviationCode(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());

		Object dataObject = deviationCode.getObject();

		if (dataObject instanceof String) {
			this.deviationCode.setValue(dataObject.toString());
			this.deviationCode.setAttribute("severity", 0);
			this.deviationCode.setAttribute("deviationCode", Long.MIN_VALUE);
		} else {
			ProductDeviation details = (ProductDeviation) dataObject;
			if (details != null) {
				this.deviationCode.setAttribute("deviationCode", details.getProductDevID());
				this.deviationCode.setAttribute("severity", details.getSeverity());
				this.deviationCode.setAttribute("severityName", details.getSeverityName());
			} else {
				this.deviationCode.setValue("", "");
				this.deviationCode.setAttribute("severity", 0);
				this.deviationCode.setAttribute("deviationCode", Long.MIN_VALUE);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinanceDeviations aFinAdvancePayments, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinAdvancePayments.getBefImage(), aFinAdvancePayments);
		return new AuditHeader(aFinAdvancePayments.getFinReference(), null, null, null, auditDetail,
				aFinAdvancePayments.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDeviations(FinanceDeviations financeDeviations) {
		this.financeDeviations = financeDeviations;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public DeviationDetailDialogCtrl getDeviationDetailDialogCtrl() {
		return deviationDetailDialogCtrl;
	}

	public void setDeviationDetailDialogCtrl(DeviationDetailDialogCtrl deviationDetailDialogCtrl) {
		this.deviationDetailDialogCtrl = deviationDetailDialogCtrl;
	}

	public void setFinanceDeviationsList(List<FinanceDeviations> financeDeviations) {
		this.financeDeviationsList = financeDeviations;
	}

	public List<FinanceDeviations> getFinanceDeviationsList() {
		return financeDeviationsList;
	}

}
