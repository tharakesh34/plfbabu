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
 * FileName    		:  CustomerPhoneNumberDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.financialsummary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.service.finance.financialsummary.RisksAndMitigantsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerViewDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerPhoneNumber /RisksAndMitigantsDialog.zul
 * file.
 */
public class RisksAndMitigantsDialogCtrl extends GFCBaseCtrl<RisksAndMitigants> {
	private static final long serialVersionUID = -3093280086658721485L;
	private static final Logger logger = Logger.getLogger(RisksAndMitigantsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RisksAndMitigantsDialog; // autowired

	protected Textbox risk; // autowired
	protected Textbox mitigants; // autowiredc

	private RisksAndMitigants risksAndMitigants; // overhanded per param

	// per

	protected Button btnSearchPRCustid; // autowire

	// ServiceDAOs / Domain Classes
	private transient RisksAndMitigantsService risksAndMitigantsService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newRiskAndMitigaints = false;

	private List<RisksAndMitigants> risksAndMitigant;
	private FinancialSummaryDialogCtrl financialSummaryDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	private boolean workflow = false;

	/**
	 * default constructor.<br>
	 */
	public RisksAndMitigantsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RisksAndMitigantsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerPhoneNumber object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RisksAndMitigantsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RisksAndMitigantsDialog);

		try {

			if (arguments.containsKey("risksAndMitigants")) {
				this.risksAndMitigants = (RisksAndMitigants) arguments.get("risksAndMitigants");
				RisksAndMitigants befImage = new RisksAndMitigants();
				BeanUtils.copyProperties(this.risksAndMitigants, befImage);
				this.risksAndMitigants.setBefImage(befImage);
				setRisksAndMitigants(this.risksAndMitigants);
			} else {
				getRisksAndMitigants();
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getRisksAndMitigants().isNewRecord()) {
				setNewRecord(true);

			}

			if (arguments.containsKey("financialSummaryDialogCtrl")) {
				setFinancialSummaryDialogCtrl((FinancialSummaryDialogCtrl) arguments.get("financialSummaryDialogCtrl"));
				setNewRiskAndMitigaints(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.risksAndMitigants.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "RisksAndMitigantsDialog");
				}

			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (getFinancialSummaryDialogCtrl() != null && !isFinanceProcess) {
				workflow = this.risksAndMitigants.isWorkflow();
			}

			doLoadWorkFlow(this.risksAndMitigants.isWorkflow(), this.risksAndMitigants.getWorkflowId(),
					this.risksAndMitigants.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "RisksAndMitigantsDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getRisksAndMitigants());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RisksAndMitigantsDialog.onClose();
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
		getUserWorkspace().allocateAuthorities("CustomerPhoneNumberDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnEdit"));
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
		MessageUtil.showHelpWindow(event, window_RisksAndMitigantsDialog);
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
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.risksAndMitigants.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 *            CustomerPhoneNumber
	 */
	public void doWriteBeanToComponents(RisksAndMitigants risksAndMitigants) {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.risk.setValue("");
			this.mitigants.setValue("");
		} else {
			this.risk.setValue(risksAndMitigants.getRisk());
			this.mitigants.setValue(risksAndMitigants.getMitigants());
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 */
	public void doWriteComponentsToBean(RisksAndMitigants risksAndMitigants) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			risksAndMitigants.setRisk(this.risk.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			risksAndMitigants.setMitigants(this.mitigants.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		setRisksAndMitigants(risksAndMitigants);
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
	public void doShowDialog(RisksAndMitigants risksAndMitigants) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isNewRiskAndMitigaints()) {
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
			doWriteBeanToComponents(risksAndMitigants);

			doCheckEnquiry();
			if (isNewRiskAndMitigaints()) {
				this.window_RisksAndMitigantsDialog.setHeight("50%");
				this.window_RisksAndMitigantsDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_RisksAndMitigantsDialog.doModal();
			} else {
				this.window_RisksAndMitigantsDialog.setWidth("100%");
				this.window_RisksAndMitigantsDialog.setHeight("100%");
				setDialog(DialogType.MODAL);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RisksAndMitigantsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType)) {
			this.risk.setReadonly(true);
			this.mitigants.setDisabled(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	/*
	 * private void refreshList() { getFinancialSummaryDialogCtrl().search(); }
	 */

	// CRUD operations

	/**
	 * Deletes a CustomerPhoneNumber object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final RisksAndMitigants arisksAndMitigants = new RisksAndMitigants();
		BeanUtils.copyProperties(getRisksAndMitigants(), arisksAndMitigants);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value") + " : "
				+ arisksAndMitigants.getId();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(arisksAndMitigants.getRecordType())) {
				arisksAndMitigants.setVersion(arisksAndMitigants.getVersion() + 1);
				arisksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (!isFinanceProcess && getRisksAndMitigants() != null && getRisksAndMitigants().isWorkflow()) {
					arisksAndMitigants.setNewRecord(true);
				}
				if (isWorkFlowEnabled()) {
					arisksAndMitigants.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.equals(arisksAndMitigants.getRecordType(), PennantConstants.RCD_UPD)) {
				arisksAndMitigants.setNewRecord(true);
			}

			try {

				if (isNewRiskAndMitigaints()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newRiskAndMitigantsProcess(risksAndMitigants, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_RisksAndMitigantsDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getFinancialSummaryDialogCtrl().doFillRisksAndMitigants(this.risksAndMitigant);
						// true;
						// send the data back to customer
						closeDialog();
					}

				} else if (doProcess(arisksAndMitigants, tranType)) {
					/* refreshList(); */
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		this.risk.setDisabled(isReadOnly("CustomerPhoneNumberDialog_phonePriority"));
		this.mitigants.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneCustID"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.risksAndMitigants.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newRiskAndMitigaints) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newRiskAndMitigaints);
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
		this.risk.setReadonly(true);
		this.mitigants.setReadonly(true);

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
		this.risk.setValue("");
		this.mitigants.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final RisksAndMitigants risksAndMitigants = new RisksAndMitigants();
		BeanUtils.copyProperties(getRisksAndMitigants(), risksAndMitigants);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		// fill the CustomerPhoneNumber object with the components data
		doWriteComponentsToBean(risksAndMitigants);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = risksAndMitigants.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(risksAndMitigants.getRecordType())) {
				risksAndMitigants.setVersion(risksAndMitigants.getVersion() + 1);
				if (isNew) {
					risksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					risksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					risksAndMitigants.setNewRecord(true);
				}
			}
		} else {

			if (isNewRiskAndMitigaints()) {
				if (isNewRecord()) {
					risksAndMitigants.setVersion(1);
					risksAndMitigants.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (workflow && !isFinanceProcess && StringUtils.isBlank(risksAndMitigants.getRecordType())) {
						risksAndMitigants.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(risksAndMitigants.getRecordType())) {
					risksAndMitigants.setVersion(risksAndMitigants.getVersion() + 1);
					risksAndMitigants.setRecordType(PennantConstants.RCD_UPD);
				}

				if (risksAndMitigants.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (risksAndMitigants.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				risksAndMitigants.setVersion(risksAndMitigants.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewRiskAndMitigaints()) {
				AuditHeader auditHeader = newRiskAndMitigantsProcess(risksAndMitigants, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_RisksAndMitigantsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinancialSummaryDialogCtrl().doFillRisksAndMitigants(this.risksAndMitigant);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(risksAndMitigants, tranType)) {
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
	private AuditHeader newRiskAndMitigantsProcess(RisksAndMitigants arisksAndMitigants, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(risksAndMitigants, tranType);
		risksAndMitigant = new ArrayList<RisksAndMitigants>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		if (getFinancialSummaryDialogCtrl().getRisksAndMitigantsDetailList() != null
				&& getFinancialSummaryDialogCtrl().getRisksAndMitigantsDetailList().size() > 0) {
			for (int i = 0; i < getFinancialSummaryDialogCtrl().getRisksAndMitigantsDetailList().size(); i++) {
				RisksAndMitigants risksAndMitigants = getFinancialSummaryDialogCtrl().getRisksAndMitigantsDetailList()
						.get(i);

				if (arisksAndMitigants.getSeqNo() == risksAndMitigants.getSeqNo()) {

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (arisksAndMitigants.getRecordType().equals(PennantConstants.RCD_UPD)) {
							arisksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							risksAndMitigant.add(arisksAndMitigants);
						} else if (arisksAndMitigants.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (arisksAndMitigants.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							arisksAndMitigants.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							risksAndMitigant.add(arisksAndMitigants);
						} else if (arisksAndMitigants.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getFinancialSummaryDialogCtrl().getRisksAndMitigantsDetailList()
									.size(); j++) {
								RisksAndMitigants risksAndMitigantDetails = getFinancialSummaryDialogCtrl()
										.getRisksAndMitigantsDetailList().get(j);
								if (risksAndMitigantDetails.getRisk() == arisksAndMitigants.getRisk()
										&& risksAndMitigantDetails.getRisk().equals(arisksAndMitigants.getRisk())) {
									risksAndMitigant.add(risksAndMitigantDetails);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							risksAndMitigant.add(risksAndMitigants);
						}
					}
				} else {
					risksAndMitigant.add(risksAndMitigants);
				}
			}
		}

		if (!recordAdded) {
			risksAndMitigant.add(risksAndMitigants);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerPhoneNumber
	 *            (CustomerPhoneNumber)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RisksAndMitigants risksAndMitigants, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		risksAndMitigants.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		risksAndMitigants.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		risksAndMitigants.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			risksAndMitigants.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(risksAndMitigants.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, risksAndMitigants);
				}

				if (isNotesMandatory(taskId, risksAndMitigants)) {
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

			risksAndMitigants.setTaskId(taskId);
			risksAndMitigants.setNextTaskId(nextTaskId);
			risksAndMitigants.setRoleCode(getRole());
			risksAndMitigants.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(risksAndMitigants, tranType);

			String operationRefs = getServiceOperations(taskId, risksAndMitigants);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(risksAndMitigants, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(risksAndMitigants, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		RisksAndMitigants arisksAndMitigants = (RisksAndMitigants) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						//auditHeader = getRisksAndMitigantsService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRisksAndMitigantsService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						//auditHeader = getRisksAndMitigantsService().doApprove(auditHeader);
						if (arisksAndMitigants.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRisksAndMitigantsService().doReject(auditHeader);
						if (arisksAndMitigants.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RisksAndMitigantsDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RisksAndMitigantsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.risksAndMitigants), true);
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
		} catch (InterruptedException e) {
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
	private AuditHeader getAuditHeader(RisksAndMitigants risksAndMitigants, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, risksAndMitigants.getBefImage(), risksAndMitigants);

		return new AuditHeader(getReference(), String.valueOf(risksAndMitigants.getId()), null, null, auditDetail,
				risksAndMitigants.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RisksAndMitigantsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.risksAndMitigants);
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

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

	public RisksAndMitigantsService getRisksAndMitigantsService() {
		return risksAndMitigantsService;
	}

	public void setRisksAndMitigantsService(RisksAndMitigantsService risksAndMitigantsService) {
		this.risksAndMitigantsService = risksAndMitigantsService;
	}

	public void setRisksAndMitigants(RisksAndMitigants risksAndMitigants) {
		this.risksAndMitigants = risksAndMitigants;
	}

	public boolean isNewRiskAndMitigaints() {
		return newRiskAndMitigaints;
	}

	public void setNewRiskAndMitigaints(boolean newRiskAndMitigaints) {
		this.newRiskAndMitigaints = newRiskAndMitigaints;
	}

	public RisksAndMitigants getRisksAndMitigants() {
		return risksAndMitigants;
	}

	public FinancialSummaryDialogCtrl getFinancialSummaryDialogCtrl() {
		return financialSummaryDialogCtrl;
	}

	public void setFinancialSummaryDialogCtrl(FinancialSummaryDialogCtrl financialSummaryDialogCtrl) {
		this.financialSummaryDialogCtrl = financialSummaryDialogCtrl;
	}

}
