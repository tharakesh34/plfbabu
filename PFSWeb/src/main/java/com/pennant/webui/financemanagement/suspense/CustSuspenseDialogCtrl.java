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
 * * FileName : SuspenseDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified
 * Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.suspense;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

/**
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/Suspense/SusoenseDialog.zul file.
 */
public class CustSuspenseDialogCtrl extends GFCBaseCtrl<Customer> {
	private static final long serialVersionUID = 7798200490595650451L;
	private static final Logger logger = LogManager.getLogger(CustSuspenseDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustSuspenseDialog; // autowired
	protected Textbox custCIF; // autowired
	protected Longbox custID; // autowired
	protected Label custShrtName; // autowired
	protected Textbox custBranch; // autowired
	protected Checkbox custSuspSts; // autowired
	protected Datebox custSuspDate; // autowired
	protected Textbox custSuspRemarks; // autowired
	protected Button btnSearchCustCIF; // autowired
	protected Space space_custSuspDate; // autowired
	protected Space space_custCIF; // autowired
	protected Label lovDescCustDftBranch; // autowired

	// not auto wired vars
	private Customer customer; // overhanded per param
	private transient CustSuspenseListCtrl custSuspenseListCtrl; // overhanded per param

	private transient boolean validationOn;

	private String menuItemRightName = null;

	// ServiceDAOs / Domain Classes
	private CustomerService customerService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private String suspTrigger = "";

	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private NotificationService notificationService;

	/**
	 * default constructor.<br>
	 */
	public CustSuspenseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustSuspenseDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Suspense object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustSuspenseDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustSuspenseDialog);

		try {
			if (arguments.containsKey("custSuspense")) {
				this.customer = (Customer) arguments.get("custSuspense");
				Customer befImage = new Customer();
				BeanUtils.copyProperties(this.customer, befImage);
				this.customer.setBefImage(befImage);
				setCustomer(this.customer);
			} else {
				setCustomer(null);
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(), customer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "SuspenseDialog", menuItemRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the SuspenseListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete Suspense here.
			if (arguments.containsKey("custSuspenseListCtrl")) {
				setCustSuspenseListCtrl((CustSuspenseListCtrl) arguments.get("custSuspenseListCtrl"));
			} else {
				setCustSuspenseListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustSuspenseDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		if (!getCustomer().isNewRecord()) {
			this.custCIF.setSclass("");
			this.btnSearchCustCIF.setVisible(false);
			this.custSuspDate.setSclass("");
		}

		logger.debug("Leaving");
	}

	public void onCheck$custSuspSts(Event event) {
		logger.debug("Entering" + event.toString());

		/*
		 * if(isDataChanged()) { this.btnSave.setVisible(true); } else { this.btnSave.setVisible(false); }
		 */
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_CustSuspenseDialog);
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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSuspense Suspense
	 */
	public void doWriteBeanToComponents(Customer aCustomer) {
		logger.debug("Entering");

		suspTrigger = aCustomer.getCustSuspTrigger();
		if (!aCustomer.isNewRecord()) {
			this.custSuspDate.setDisabled(true);
			this.space_custCIF.setSclass("");
			this.space_custSuspDate.setSclass("");

			// this.custSuspSts.setDisabled(true);
			// this.custSuspRemarks.setDisabled(true);
		}
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.custBranch.setValue(aCustomer.getCustDftBranch());
		this.lovDescCustDftBranch.setValue(aCustomer.getLovDescCustDftBranchName());
		this.custSuspSts.setChecked(aCustomer.isCustSuspSts());
		this.custSuspDate.setValue(aCustomer.getCustSuspDate());
		if (aCustomer.getCustSuspDate() == null) {
			Date appDate = SysParamUtil.getAppDate();
			this.custSuspDate.setValue(appDate);
			this.custSuspSts.setChecked(true);
		}

		String remarks = getCustomerService().getCustSuspRemarks(aCustomer.getCustID());
		if (!StringUtils.isBlank(remarks)) {
			this.custSuspRemarks.setValue(remarks);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSuspHead
	 */
	public void doWriteComponentsToBean(Customer aCustomer) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomer.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustDftBranch(this.custBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustSuspSts(this.custSuspSts.isChecked());
			if (this.custSuspSts.isChecked()) {
				aCustomer.setCustSuspTrigger("M");
			} else {
				aCustomer.setCustSuspTrigger("");
				if (StringUtils.isBlank(suspTrigger)) {
					throw new WrongValueException(this.custSuspSts, Labels.getLabel("CUSTOMER_SUSPENSE"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustSuspDate(this.custSuspDate.getValue());
			Date appStartDate = SysParamUtil.getAppDate();
			if (DateUtil.compare(this.custSuspDate.getValue(), appStartDate) > 0) {
				throw new WrongValueException(this.custSuspDate,
						Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
								new String[] { DateUtil.formatToLongDate(this.custSuspDate.getValue()),
										DateUtil.formatToLongDate(appStartDate) }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustSuspRemarks(this.custSuspRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
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
	 * @param aSuspHead
	 */
	public void doShowDialog(Customer aCustomer) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aCustomer.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				// doReadOnly();
				// doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomer);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustSuspenseDialog.onClose();
		} catch (Exception e) {
			throw e;
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

		getUserWorkspace().allocateAuthorities("CustSuspenseDialog", getRole(), menuItemRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustSuspenseDialog_btnNew"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustSuspenseDialog_btnSave"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustSuspenseDialog_btnEdit"));
		this.btnNotes.setVisible(false);
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustSuspenseDialog_btnDelete"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCustomer().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.custCIF.setReadonly(false);
			this.custSuspSts.setDisabled(false);
		} else {
			this.custCIF.setReadonly(true);
		}

		this.btnSearchCustCIF.setDisabled(isReadOnly("CustSuspenseDialog_btnSearchCustCIF"));
		this.custCIF.setDisabled(isReadOnly("CustSuspenseDialog_custCIF"));
		this.custID.setReadonly(isReadOnly("CustSuspenseDialog_custID"));
		this.custSuspSts.setDisabled(isReadOnly("CustSuspenseDialog_custSuspSts"));
		this.custSuspRemarks.setReadonly(isReadOnly("CustSuspenseDialog_custSuspRemarks"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customer.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());

		this.btnEdit.setVisible(false);
		this.btnSave.setVisible(false);
		this.btnCancel.setVisible(true);

		this.custSuspSts.setDisabled(false);
		this.custSuspRemarks.setDisabled(false);

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		this.custSuspSts.setDisabled(true);
		this.custSuspRemarks.setDisabled(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnDelete(Event event)
			throws InterruptedException, IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.customer.getBefImage());
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		this.btnEdit.setVisible(true);
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custBranch.setConstraint("");
		this.custSuspDate.setConstraint("");
		this.custID.setConstraint("");
		this.custCIF.setConstraint("");
		logger.debug("Leaving");
	}

	private void doDelete()
			throws InterruptedException, IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug(Literal.ENTERING);

		final Customer aCustomer = new Customer();
		BeanUtils.copyProperties(getCustomer(), aCustomer);

		String keyReference = Labels.getLabel("label_TargetDetailDialog_TargetCode.value") + " : "
				+ aCustomer.getCustID();

		doDelete(keyReference, aCustomer);

		logger.debug(Literal.LEAVING);
	}

	// CRUD operations

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		final Customer aCustomer = new Customer();
		BeanUtils.copyProperties(getCustomer(), aCustomer);

		// force validation, if on, than execute by component.getValue()
		doSetValidation();

		// fill the Suspense object with the components data
		doWriteComponentsToBean(aCustomer);

		// Write the additional validations as per below example
		String tranType = "";
		boolean isNew = aCustomer.isNewRecord();
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomer.getRecordType())) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomer.setNewRecord(true);
				}
			}

		} else {
			aCustomer.setVersion(aCustomer.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aCustomer, tranType)) {

				// Save the Customer Suspense details into Log Table
				getCustomerService().saveCustSuspMovements(prepareSuspMovementDetail(aCustomer));

				refreshList();

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aCustomer.getNextTaskId())) {
					aCustomer.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aCustomer.getRoleCode(),
						aCustomer.getNextRoleCode(), aCustomer.getCustCIF(), " Customer Suspense ",
						aCustomer.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for User
				if (StringUtils.isNotBlank(aCustomer.getNextTaskId())
						&& !StringUtils.trimToEmpty(aCustomer.getNextRoleCode()).equals(aCustomer.getRoleCode())) {
					notificationService.sendNotifications(NotificationConstants.MAIL_MODULE_MANUALSUSPENSE, aCustomer);
				}

				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		// Update the Customer table
		// getCustomerService().updateCustSuspenseDetails(aCustomer);

		// Save the Customer Suspense details into Log Table
		// getCustomerService().saveCustSuspMovements(prepareSuspMovementDetail(aCustomer));

		// refreshList();
		// Close the Existing Dialog
		// closeDialog();

		logger.debug("Leaving");
	}

	private Customer prepareSuspMovementDetail(Customer aCustomer) {
		logger.debug("Entering");

		if (aCustomer.isCustSuspSts()) {
			aCustomer.setCustSuspMvtType("S");
		} else {
			aCustomer.setCustSuspMvtType("N");
		}
		aCustomer.setCustSuspAprDate(new Timestamp(System.currentTimeMillis()));
		aCustomer.setCustSuspEffDate(new Timestamp(System.currentTimeMillis()));

		logger.debug("Leaving");

		return aCustomer;
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	protected boolean doProcess(Customer aCustomer, String tranType)
			throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomer);
				}

				if (isNotesMandatory(taskId, aCustomer)) {
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

			aCustomer.setTaskId(taskId);
			aCustomer.setNextTaskId(nextTaskId);
			aCustomer.setRoleCode(getRole());
			aCustomer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomer, tranType);
			String operationRefs = getServiceOperations(taskId, aCustomer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomer, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * 
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.custCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
			this.custBranch.setValue(customer.getCustDftBranch());
			this.lovDescCustDftBranch.setValue(customer.getLovDescCustDftBranchName());
			validateSuspendCustomer();
		} else {
			this.custCIF.setValue("");
			this.custShrtName.setValue("");
		}

		logger.debug("Leaving ");
	}

	private void validateSuspendCustomer() {

		Customer suspendCustomer = getCustomerService().getSuspendCustomer(this.custID.getValue());
		if (suspendCustomer != null) {
			// this.btnSave.setVisible(false);
			// this.btnEdit.setVisible(true);
			doEdit();
			suspTrigger = suspendCustomer.getCustSuspTrigger();
			doWriteBeanToComponents(suspendCustomer);
		} else {
			doEdit();
			this.custSuspDate.setDisabled(false);
			this.space_custCIF.setSclass("");
			this.space_custSuspDate.setSclass("");

			this.custSuspSts.setDisabled(false);
			this.custSuspRemarks.setDisabled(false);
			this.custSuspSts.setChecked(true);
			Date appDate = SysParamUtil.getAppDate();
			this.custSuspDate.setValue(appDate);
		}
	}

	/**
	 * Method for Processing Workflow Method
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Customer aCustomer = (Customer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					// auditHeader = getCustomerService().delete(auditHeader);
					deleteNotes = true;
				} else {
					getCustomerService().updateCustSuspenseDetails(aCustomer, "");
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

					if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {

						// Remove customer from Suspense mode
						aCustomer.setCustSuspSts(false);
						aCustomer.setCustSuspDate(null);
						aCustomer.setCustSuspTrigger("");

						// Save Customer Suspense movements
						aCustomer.setCustSuspMvtType("N");
						aCustomer.setCustSuspAprDate(DateUtil.getTimestamp(new Date()));
						aCustomer.setCustSuspEffDate(DateUtil.getTimestamp(new Date()));
						getCustomerService().saveCustSuspMovements(aCustomer);
						deleteNotes = true;
					}
					// while Record Approve
					aCustomer.setTaskId("");
					aCustomer.setNextTaskId("");
					aCustomer.setRoleCode("");
					aCustomer.setNextRoleCode("");
					aCustomer.setRecordType("");
					aCustomer.setWorkflowId(0);

					getCustomerService().updateCustSuspenseDetails(aCustomer, "");

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aCustomer.setCustSuspSts(false);
					aCustomer.setCustSuspDate(null);
					aCustomer.setCustSuspTrigger("");
					getCustomerService().updateCustSuspenseDetails(aCustomer, "");

					if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CustSuspenseDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_CustSuspenseDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(), true);
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

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustSuspenseDialog_CustCIF.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		logger.debug("Leaving");
	}

	private AuditHeader getAuditHeader(Customer aCustomer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomer.getBefImage(), aCustomer);
		return new AuditHeader(aCustomer.getCustCIF(), null, null, null, auditDetail, aCustomer.getUserDetails(),
				getOverideMap());
	}

	public void onClick$btnNotes(Event event) {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("Suspense");
		notes.setVersion(getCustomer().getVersion());
		return notes;
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustSuspenseListCtrl().search();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustSuspenseListCtrl getCustSuspenseListCtrl() {
		return custSuspenseListCtrl;
	}

	public void setCustSuspenseListCtrl(CustSuspenseListCtrl custSuspenseListCtrl) {
		this.custSuspenseListCtrl = custSuspenseListCtrl;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
