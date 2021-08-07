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
 * * FileName : CustomerPhoneNumberDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financialsummary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.service.finance.financialsummary.DealRecommendationMeritsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DealRecommendationMeritsDialogCtrl extends GFCBaseCtrl<DealRecommendationMerits> {
	private static final long serialVersionUID = -3093280086658721485L;
	private static final Logger logger = LogManager.getLogger(DealRecommendationMeritsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_dealRecommendationMeritsDialog; // autowired

	protected Textbox dealMerits; // autowired

	private DealRecommendationMerits dealRecommendationMerits; // overhanded per param

	// per

	protected Button btnSearchPRCustid; // autowire

	// ServiceDAOs / Domain Classes
	private transient DealRecommendationMeritsService dealRecommendationMeritsService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newDealRecommendationMerits = false;

	private List<DealRecommendationMerits> dealRecommendationMeritsList;
	private FinancialSummaryDialogCtrl financialSummaryDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	private boolean workflow = false;

	/**
	 * default constructor.<br>
	 */
	public DealRecommendationMeritsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DealRecommendationMeritsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerPhoneNumber object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_dealRecommendationMeritsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_dealRecommendationMeritsDialog);

		try {

			if (arguments.containsKey("dealRecommendationMerits")) {
				this.dealRecommendationMerits = (DealRecommendationMerits) arguments.get("dealRecommendationMerits");
				DealRecommendationMerits befImage = new DealRecommendationMerits();
				BeanUtils.copyProperties(this.dealRecommendationMerits, befImage);
				this.dealRecommendationMerits.setBefImage(befImage);
				setDealRecommendationMerits(this.dealRecommendationMerits);
			} else {
				getDealRecommendationMerits();
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getDealRecommendationMerits().isNewRecord()) {
				setNewRecord(true);

			}

			if (arguments.containsKey("financialSummaryDialogCtrl")) {
				setFinancialSummaryDialogCtrl((FinancialSummaryDialogCtrl) arguments.get("financialSummaryDialogCtrl"));
				setNewDealRecommendationMerits(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.dealRecommendationMerits.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "DealRecommendationMeritsDialog");
				}

			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (getFinancialSummaryDialogCtrl() != null && !isFinanceProcess) {
				workflow = this.dealRecommendationMerits.isWorkflow();
			}

			doLoadWorkFlow(this.dealRecommendationMerits.isWorkflow(), this.dealRecommendationMerits.getWorkflowId(),
					this.dealRecommendationMerits.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "DealRecommendationMeritsDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDealRecommendationMerits());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_dealRecommendationMeritsDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
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
		getUserWorkspace().allocateAuthorities("DealRecommendationMeritsDialog", userRole);

		this.btnNew.setVisible(true);
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DealRecommendationMeritsDialog_btnEdit"));
		// this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DealRecommendationMeritsDialog_btnDelete"));
		this.btnDelete.setVisible(true);
		this.btnSave.setVisible(true);
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
		MessageUtil.showHelpWindow(event, window_dealRecommendationMeritsDialog);
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
		doWriteBeanToComponents(this.dealRecommendationMerits.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerPhoneNumber CustomerPhoneNumber
	 */
	public void doWriteBeanToComponents(DealRecommendationMerits dealRecommendationMerits) {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.dealMerits.setValue("");
		} else {
			this.dealMerits.setValue(dealRecommendationMerits.getDealMerits());
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 */
	public void doWriteComponentsToBean(DealRecommendationMerits dealRecommendationMerits) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			dealRecommendationMerits.setDealMerits(this.dealMerits.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		setDealRecommendationMerits(dealRecommendationMerits);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerPhoneNumber
	 * @throws Exception
	 */
	public void doShowDialog(DealRecommendationMerits dealRecommendationMerits) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isNewDealRecommendationMerits()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(dealRecommendationMerits);

			doCheckEnquiry();
			if (isNewDealRecommendationMerits()) {
				this.window_dealRecommendationMeritsDialog.setHeight("30%");
				this.window_dealRecommendationMeritsDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_dealRecommendationMeritsDialog.doModal();
			} else {
				this.window_dealRecommendationMeritsDialog.setWidth("100%");
				this.window_dealRecommendationMeritsDialog.setHeight("100%");
				setDialog(DialogType.MODAL);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_dealRecommendationMeritsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType)) {
			this.dealMerits.setReadonly(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	/*
	 * protected void refreshList() { getFinancialSummaryDialogCtrl().search(); }
	 */

	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final DealRecommendationMerits adealRecommendationMerits = new DealRecommendationMerits();
		BeanUtils.copyProperties(getDealRecommendationMerits(), adealRecommendationMerits);

		final String keyReference = Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value") + " : "
				+ adealRecommendationMerits.getId();

		doDelete(keyReference, adealRecommendationMerits);
		logger.debug("Leaving");
	}

	protected void onDoDelete(final DealRecommendationMerits adealRecommendationMerits) {
		String tranType = PennantConstants.TRAN_WF;
		if (StringUtils.isBlank(adealRecommendationMerits.getRecordType())) {
			adealRecommendationMerits.setVersion(adealRecommendationMerits.getVersion() + 1);
			adealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getDealRecommendationMerits() != null
					&& getDealRecommendationMerits().isWorkflow()) {
				adealRecommendationMerits.setNewRecord(true);
			}
			if (isWorkFlowEnabled()) {
				adealRecommendationMerits.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		} else if (StringUtils.equals(adealRecommendationMerits.getRecordType(), PennantConstants.RCD_UPD)) {
			adealRecommendationMerits.setNewRecord(true);
		}

		try {

			if (isNewDealRecommendationMerits()) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newDealRecommendationMeritsProcess(dealRecommendationMerits, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_dealRecommendationMeritsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinancialSummaryDialogCtrl()
							.doFillDealRecommendationMeritsDetails(this.dealRecommendationMeritsList);
					// true;
					// send the data back to customer
					closeDialog();
				}

			} else if (doProcess(adealRecommendationMerits, tranType)) {
				/* refreshList(); */
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		this.dealMerits.setDisabled(isReadOnly("DealRecommendationMeritsDialog_dealMerits"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.dealRecommendationMerits.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newDealRecommendationMerits) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newDealRecommendationMerits);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/*
	 * public boolean isReadOnly(String componentName) { boolean isCustomerWorkflow = false; if
	 * (getFinancialSummaryDialogCtrl() != null) { isCustomerWorkflow = getris; } if (isWorkFlowEnabled() ||
	 * isCustomerWorkflow) { return getUserWorkspace().isReadOnly(componentName); } return false; }
	 */

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.dealMerits.setReadonly(true);

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
		this.dealMerits.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DealRecommendationMerits dealRecommendationMerits = new DealRecommendationMerits();
		BeanUtils.copyProperties(getDealRecommendationMerits(), dealRecommendationMerits);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		// fill the CustomerPhoneNumber object with the components data
		doWriteComponentsToBean(dealRecommendationMerits);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = dealRecommendationMerits.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(dealRecommendationMerits.getRecordType())) {
				dealRecommendationMerits.setVersion(dealRecommendationMerits.getVersion() + 1);
				if (isNew) {
					dealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					dealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					dealRecommendationMerits.setNewRecord(true);
				}
			}
		} else {

			if (isNewDealRecommendationMerits()) {
				if (isNewRecord()) {
					dealRecommendationMerits.setVersion(1);
					dealRecommendationMerits.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (workflow && !isFinanceProcess
							&& StringUtils.isBlank(dealRecommendationMerits.getRecordType())) {
						dealRecommendationMerits.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(dealRecommendationMerits.getRecordType())) {
					dealRecommendationMerits.setVersion(dealRecommendationMerits.getVersion() + 1);
					dealRecommendationMerits.setRecordType(PennantConstants.RCD_UPD);
				}

				if (dealRecommendationMerits.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (dealRecommendationMerits.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				dealRecommendationMerits.setVersion(dealRecommendationMerits.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewDealRecommendationMerits()) {
				AuditHeader auditHeader = newDealRecommendationMeritsProcess(dealRecommendationMerits, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_dealRecommendationMeritsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinancialSummaryDialogCtrl()
							.doFillDealRecommendationMeritsDetails(this.dealRecommendationMeritsList);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(dealRecommendationMerits, tranType)) {
				/* refreshList(); */
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Creating list of Details
	 */
	private AuditHeader newDealRecommendationMeritsProcess(DealRecommendationMerits adealRecommendationMerits,
			String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(dealRecommendationMerits, tranType);
		dealRecommendationMeritsList = new ArrayList<DealRecommendationMerits>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		if (getFinancialSummaryDialogCtrl().getDealRecommendationMeritsDetailList() != null
				&& getFinancialSummaryDialogCtrl().getDealRecommendationMeritsDetailList().size() > 0) {
			for (int i = 0; i < getFinancialSummaryDialogCtrl().getDealRecommendationMeritsDetailList().size(); i++) {
				DealRecommendationMerits dealRecommendationMerits = getFinancialSummaryDialogCtrl()
						.getDealRecommendationMeritsDetailList().get(i);

				if (adealRecommendationMerits.getSeqNo() == dealRecommendationMerits.getSeqNo()) {

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (adealRecommendationMerits.getRecordType().equals(PennantConstants.RCD_UPD)) {
							adealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							dealRecommendationMeritsList.add(adealRecommendationMerits);
						} else if (adealRecommendationMerits.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (adealRecommendationMerits.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							adealRecommendationMerits.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							dealRecommendationMeritsList.add(adealRecommendationMerits);
						} else if (adealRecommendationMerits.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getFinancialSummaryDialogCtrl().getDealRecommendationMeritsDetailList()
									.size(); j++) {
								DealRecommendationMerits dealRecommendationMeritsDetails = getFinancialSummaryDialogCtrl()
										.getDealRecommendationMeritsDetailList().get(j);
								if (dealRecommendationMeritsDetails.getDealMerits() == adealRecommendationMerits
										.getDealMerits()
										&& dealRecommendationMeritsDetails.getDealMerits()
												.equals(adealRecommendationMerits.getDealMerits())) {
									dealRecommendationMeritsList.add(dealRecommendationMeritsDetails);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							dealRecommendationMeritsList.add(dealRecommendationMerits);
						}
					}
				} else {
					dealRecommendationMeritsList.add(dealRecommendationMerits);
				}
			}
		}

		if (!recordAdded) {
			dealRecommendationMeritsList.add(dealRecommendationMerits);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerPhoneNumber (CustomerPhoneNumber)
	 * 
	 * @param tranType             (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(DealRecommendationMerits dealRecommendationMerits, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		dealRecommendationMerits.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		dealRecommendationMerits.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		dealRecommendationMerits.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			dealRecommendationMerits.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(dealRecommendationMerits.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, dealRecommendationMerits);
				}

				if (isNotesMandatory(taskId, dealRecommendationMerits)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			dealRecommendationMerits.setTaskId(taskId);
			dealRecommendationMerits.setNextTaskId(nextTaskId);
			dealRecommendationMerits.setRoleCode(getRole());
			dealRecommendationMerits.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(dealRecommendationMerits, tranType);

			String operationRefs = getServiceOperations(taskId, dealRecommendationMerits);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(dealRecommendationMerits, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(dealRecommendationMerits, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DealRecommendationMerits adealRecommendationMerits = (DealRecommendationMerits) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						deleteNotes = true;
					} else {
						auditHeader = getDealRecommendationMeritsService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						if (adealRecommendationMerits.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getDealRecommendationMeritsService().doReject(auditHeader);
						if (adealRecommendationMerits.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_dealRecommendationMeritsDialog,
								auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_dealRecommendationMeritsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.dealRecommendationMerits), true);
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
		} catch (AppException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerIdentity
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DealRecommendationMerits dealRecommendationMerits, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, dealRecommendationMerits.getBefImage(),
				dealRecommendationMerits);

		return new AuditHeader(getReference(), String.valueOf(dealRecommendationMerits.getId()), null, null,
				auditDetail, dealRecommendationMerits.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_dealRecommendationMeritsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.dealRecommendationMerits);
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}

	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public FinancialSummaryDialogCtrl getFinancialSummaryDialogCtrl() {
		return financialSummaryDialogCtrl;
	}

	public void setFinancialSummaryDialogCtrl(FinancialSummaryDialogCtrl financialSummaryDialogCtrl) {
		this.financialSummaryDialogCtrl = financialSummaryDialogCtrl;
	}

	public DealRecommendationMerits getDealRecommendationMerits() {
		return dealRecommendationMerits;
	}

	public void setDealRecommendationMerits(DealRecommendationMerits dealRecommendationMerits) {
		this.dealRecommendationMerits = dealRecommendationMerits;
	}

	public List<DealRecommendationMerits> getDealRecommendationMeritsList() {
		return dealRecommendationMeritsList;
	}

	public void setDealRecommendationMeritsList(List<DealRecommendationMerits> dealRecommendationMeritsList) {
		this.dealRecommendationMeritsList = dealRecommendationMeritsList;
	}

	public boolean isNewDealRecommendationMerits() {
		return newDealRecommendationMerits;
	}

	public void setNewDealRecommendationMerits(boolean newDealRecommendationMerits) {
		this.newDealRecommendationMerits = newDealRecommendationMerits;
	}

	public DealRecommendationMeritsService getDealRecommendationMeritsService() {
		return dealRecommendationMeritsService;
	}

	public void setDealRecommendationMeritsService(DealRecommendationMeritsService dealRecommendationMeritsService) {
		this.dealRecommendationMeritsService = dealRecommendationMeritsService;
	}

}
