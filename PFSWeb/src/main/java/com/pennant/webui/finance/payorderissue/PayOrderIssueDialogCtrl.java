/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PayOrderIssueHeaders. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * RepayOrderIssueHeaderion or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PayOrderIssueDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.payorderissue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.payorderissue.PayOrderIssueService;
import com.pennant.backend.service.payorderissue.impl.DisbursementPostings;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/PayOrderIssueHeader/PayOrderIssueDialog.zul file.
 */
public class PayOrderIssueDialogCtrl extends GFCBaseCtrl<FinAdvancePayments> {
	private static final long							serialVersionUID		= -8421583705358772016L;
	private static final Logger							logger					= Logger.getLogger(PayOrderIssueDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window									window_PayOrderIssueDialog;

	protected Textbox									finReference;
	protected Grid										grid_Basicdetails;

	// not auto wired variables
	private PayOrderIssueHeader							payOrderIssueHeader;
	private transient PayOrderIssueListCtrl				payOrderIssueListCtrl;

	private transient boolean							validationOn;

	// ServiceDAOs / Domain Classes
	private transient PayOrderIssueService				payOrderIssueService;
	private HashMap<String, ArrayList<ErrorDetails>>	overideMap				= new HashMap<String, ArrayList<ErrorDetails>>();

	// NEEDED for the ReUse in the SearchWindow

	int													listRows;

	protected Grid										grid_payOrderIssue;
	protected Label										payOrderIssue_finReference;
	protected Label										payOrderIssue_finType;
	protected Label										payOrderIssue_custCIF;
	protected Checkbox									payOrderIssue_quickDisb;
	protected Label										payOrderIssue_finCcy;
	protected Label										payOrderIssue_startDate;
	protected Label										payOrderIssue_maturityDate;

	protected Label										label_PayOrderIssueDialog_FinReference;
	protected Button									button_PayOrderIssueDialog_NewDisbursement;

	protected Listbox									listboxPayOrderIssue;
	protected Label										label_AdvancePayments_Title;

	private List<FinAdvancePayments>					finAdvancePaymentsList	= new ArrayList<FinAdvancePayments>();
	private String										ModuleType_POISSUE		= "POISSUE";
	private DisbursementInstCtrl						disbursementInstCtrl;
	private FinanceMain									financeMain;
	private int											ccyformat;

	protected Tab										tabPosting;
	protected Listbox									listBoxFinAccountings;
	private PostingsPreparationUtil						postingsPreparationUtil;
	protected Decimalbox								payOrderIssue_FinAssetValue;
	protected Decimalbox								payOrderIssue_FinCurrAssetValue;
	private DisbursementPostings						disbursementPostings;

	/**
	 * default constructor.<br>
	 */
	public PayOrderIssueDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PayOrderIssueDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected PayOrderIssueHeader object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PayOrderIssueDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PayOrderIssueDialog);

		try {
			
			// READ OVERHANDED parameters !
			if (arguments.containsKey("payOrderIssueHeader")) {
				this.payOrderIssueHeader = (PayOrderIssueHeader) arguments.get("payOrderIssueHeader");
				PayOrderIssueHeader befImage = new PayOrderIssueHeader();
				BeanUtils.copyProperties(this.payOrderIssueHeader, befImage);
				this.payOrderIssueHeader.setBefImage(befImage);
				setPayOrderIssueHeader(this.payOrderIssueHeader);
				financeMain = payOrderIssueHeader.getFinanceMain();
				ccyformat = CurrencyUtil.getFormat(financeMain.getFinCcy());
			} else {
				setPayOrderIssueHeader(null);
			}

			doLoadWorkFlow(this.payOrderIssueHeader.isWorkflow(), this.payOrderIssueHeader.getWorkflowId(),
					this.payOrderIssueHeader.getNextTaskId());
			
			if (!enqiryModule) {
				/* set components visible dependent of the users rights */
				doCheckRights();
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "PayOrderIssueDialog");
			}

			// READ OVERHANDED parameters !
			// we get the payOrderIssueHeaderListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete payOrderIssueHeader here.
			if (arguments.containsKey("payOrderIssueListCtrl")) {
				setPayOrderIssueListCtrl((PayOrderIssueListCtrl) arguments.get("payOrderIssueListCtrl"));
			} else {
				setPayOrderIssueListCtrl(null);
			}

			// Set the DialogController Height for listBox
			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 100 + 75;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listboxPayOrderIssue.setHeight(listboxHeight + "px");
			this.listBoxFinAccountings.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getPayOrderIssueHeader());
			this.btnDelete.setVisible(false);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PayOrderIssueDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.payOrderIssue_FinAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyformat));
		this.payOrderIssue_FinCurrAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyformat));

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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

		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
			this.button_PayOrderIssueDialog_NewDisbursement.setVisible(getUserWorkspace().isAllowed(
					"button_PayOrderIssueDialog_btnNew"));
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PayOrderIssueDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

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
		MessageUtil.showHelpWindow(event, window_PayOrderIssueDialog);
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
		doWriteBeanToComponents(this.payOrderIssueHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param payIHeader
	 *            PayOrderIssueHeader
	 */
	public void doWriteBeanToComponents(PayOrderIssueHeader payIHeader) {
		logger.debug("Entering");

		this.payOrderIssue_finType.setValue(payIHeader.getFinType() + " - " + payIHeader.getFinTypeDesc());
		this.payOrderIssue_finCcy.setValue(payIHeader.getFinCcy() + " - "
				+ CurrencyUtil.getCcyDesc(payIHeader.getFinCcy()));

		this.payOrderIssue_startDate.setValue(DateUtility.formatToLongDate(financeMain.getFinStartDate()));
		this.payOrderIssue_maturityDate.setValue(DateUtility.formatToLongDate(financeMain.getMaturityDate()));

		this.finReference.setValue(financeMain.getFinReference());
		this.payOrderIssue_quickDisb.setChecked(financeMain.isQuickDisb());
		this.payOrderIssue_custCIF.setValue(payIHeader.getCustCIF());

		this.payOrderIssue_FinAssetValue.setValue(formateAmount(financeMain.getFinAssetValue()));
		this.payOrderIssue_FinCurrAssetValue.setValue(formateAmount(financeMain.getFinCurrAssetValue()));
		doFillFinAdvancePaymentsDetails(payIHeader.getFinAdvancePaymentsList());
		this.recordStatus.setValue(payIHeader.getRecordStatus());

		if (!enqiryModule) {
			//Accounting Details Tab Addition
			showAccounting(payIHeader, false);
		}
		if (enqiryModule) {
			showAccounting(payIHeader, true);
		}

		logger.debug("Leaving");
	}

	private BigDecimal formateAmount(BigDecimal decimal) {
		return PennantAppUtil.formateAmount(decimal, ccyformat);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPayOrderIssueHeader
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(PayOrderIssueHeader aPayOrderIssueHeader) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPayOrderIssueHeader.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		disbursementInstCtrl.setFinanceDisbursement(aPayOrderIssueHeader.getFinanceDisbursements());
		List<ErrorDetails> valid = disbursementInstCtrl.validateFinAdvancePayment(getFinAdvancePaymentsList(),
				aPayOrderIssueHeader.isLoanApproved());
		valid = ErrorUtil.getErrorDetails(valid, getUserWorkspace().getUserLanguage());
		if (valid != null && !valid.isEmpty()) {
			for (ErrorDetails errorDetails : valid) {
				wve.add(new WrongValueException(this.label_AdvancePayments_Title, errorDetails.getError()));
			}

		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aPayOrderIssueHeader.setRecordStatus(this.recordStatus.getValue());
		aPayOrderIssueHeader.setFinAdvancePaymentsList(getFinAdvancePaymentsList());
		setPayOrderIssueHeader(aPayOrderIssueHeader);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param header
	 * @throws Exception
	 */
	public void doShowDialog(PayOrderIssueHeader header) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (header.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnSave.setVisible(false);
			this.btnNotes.setVisible(false);
			this.groupboxWf.setVisible(false);
		}

		try {
			disbursementInstCtrl.init(this.listboxPayOrderIssue, financeMain.getFinCcy(),
					header.isAlwMultiPartyDisb(), getRole());
			disbursementInstCtrl.setFinanceDisbursement(header.getFinanceDisbursements());
			disbursementInstCtrl.setFinanceMain(financeMain);
			// fill the components with the data
			doWriteBeanToComponents(header);
			if (!header.isLoanApproved()) {
				this.button_PayOrderIssueDialog_NewDisbursement.setVisible(false);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PayOrderIssueDialog.onClose();
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
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PayOrderIssueDialog_PayOrderIssueHeaderCode.value"), null, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getPayOrderIssueListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a PayOrderIssueHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final PayOrderIssueHeader aPayOrderIssueHeader = new PayOrderIssueHeader();
		BeanUtils.copyProperties(getPayOrderIssueHeader(), aPayOrderIssueHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_PayOrderIssueDialog_FinReference.value") + " : "
				+ aPayOrderIssueHeader.getFinReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doWriteBeanToComponents(aPayOrderIssueHeader);

			if (StringUtils.isBlank(aPayOrderIssueHeader.getRecordType())) {
				aPayOrderIssueHeader.setVersion(aPayOrderIssueHeader.getVersion() + 1);
				aPayOrderIssueHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPayOrderIssueHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aPayOrderIssueHeader, tranType)) {
					refreshList();
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

		if (getPayOrderIssueHeader().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.payOrderIssueHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
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
		this.finReference.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final PayOrderIssueHeader aPayOrderIssueHeader = new PayOrderIssueHeader();
		BeanUtils.copyProperties(getPayOrderIssueHeader(), aPayOrderIssueHeader);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the PayOrderIssueHeader object with the components data
		doWriteComponentsToBean(aPayOrderIssueHeader);

		isNew = aPayOrderIssueHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPayOrderIssueHeader.getRecordType())) {
				aPayOrderIssueHeader.setVersion(aPayOrderIssueHeader.getVersion() + 1);
				if (isNew) {
					aPayOrderIssueHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPayOrderIssueHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPayOrderIssueHeader.setNewRecord(true);
				}
			}
		} else {
			aPayOrderIssueHeader.setVersion(aPayOrderIssueHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (aPayOrderIssueHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
					|| !(this.finAdvancePaymentsList == null || this.finAdvancePaymentsList.size() == 0)) {
				if (doProcess(aPayOrderIssueHeader, tranType)) {
					refreshList();
					closeDialog();
				}
			} else {
				MessageUtil.showError(Labels.getLabel("List_Error",
						new String[] { Labels.getLabel("window_PayOrderIssueDialog.title"),
								Labels.getLabel("window_PayOrderIssueDialog.title") }));
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aPayOrderIssueHeader
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(PayOrderIssueHeader aPayOrderIssueHeader, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPayOrderIssueHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aPayOrderIssueHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPayOrderIssueHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPayOrderIssueHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPayOrderIssueHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPayOrderIssueHeader);
				}

				if (isNotesMandatory(taskId, aPayOrderIssueHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();

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

			aPayOrderIssueHeader.setTaskId(taskId);
			aPayOrderIssueHeader.setNextTaskId(nextTaskId);
			aPayOrderIssueHeader.setRoleCode(getRole());
			aPayOrderIssueHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPayOrderIssueHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aPayOrderIssueHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPayOrderIssueHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPayOrderIssueHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		PayOrderIssueHeader payOrderIssueHeader = (PayOrderIssueHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getPayOrderIssueService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getPayOrderIssueService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getPayOrderIssueService().doApprove(auditHeader);

						if (payOrderIssueHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPayOrderIssueService().doReject(auditHeader);

						if (payOrderIssueHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PayOrderIssueDialog, auditHeader);
						return processCompleted;

					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PayOrderIssueDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
				}

				if (deleteNotes) {
					deleteNotes(getNotes(this.payOrderIssueHeader), true);
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

	public void doFillFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePayDetails) {
		logger.debug("Entering");
		disbursementInstCtrl.doFillFinAdvancePaymentsDetails(finAdvancePayDetails);
		setFinAdvancePaymentsList(finAdvancePayDetails);
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillPOStatus(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = null;
		int firstRecord = 1;
		for (ValueLabel valueLabel : list) {
			if (!excludeFields.contains("," + valueLabel.getValue() + ",")) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);
			}
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			} else {
				if (firstRecord == 1) {
					combobox.setSelectedItem(comboitem);
				}
			}
			firstRecord++;
		}
		logger.debug("Leaving fillComboBox()");
	}

	public void onClick$button_PayOrderIssueDialog_NewDisbursement(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		disbursementInstCtrl.onClickNew(this.payOrderIssueListCtrl, this, ModuleType_POISSUE,
				getFinAdvancePaymentsList());

		logger.debug("Leaving" + event.toString());
	}

	public void onFinAdvancePaymentsItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		disbursementInstCtrl.onDoubleClick(this.payOrderIssueListCtrl, this, ModuleType_POISSUE, enqiryModule);
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aGender
	 *            (Gender)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(PayOrderIssueHeader aPayOrderIssueHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPayOrderIssueHeader.getBefImage(), aPayOrderIssueHeader);
		return new AuditHeader(String.valueOf(aPayOrderIssueHeader.getFinReference()), null, null, null, auditDetail,
				aPayOrderIssueHeader.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_PayOrderIssueDialog, auditHeader);
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
		doShowNotes(this.payOrderIssueHeader);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.payOrderIssueHeader.getFinReference());
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 * 
	 * @param accountingSetEntries
	 *            (List)
	 * 
	 */
	public void doFillAccounting(List<?> accountingSetEntries) {
		logger.debug("Entering");

		//		setDisbCrSum(BigDecimal.ZERO);
		//		setDisbDrSum(BigDecimal.ZERO);

		int formatter = ccyformat;

		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
			 if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);
					
					if(entry.getPostAmount().compareTo(BigDecimal.ZERO)!=0){

					//Highlighting Failed Posting Details 
					String sClassStyle = "";
					if (StringUtils.isNotBlank(entry.getErrorId())
							&& !"0000".equals(StringUtils.trimToEmpty(entry.getErrorId()))) {
						sClassStyle = "color:#FF0000;";
					}

					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(entry.getDrOrCr(),
							PennantStaticListUtil.getTranType()));
					label.setStyle(sClassStyle);
					hbox.appendChild(label);
					if (StringUtils.isNotBlank(entry.getPostStatus())) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.setStyle(sClassStyle);
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					if (entry.isShadowPosting()) {
						lc = new Listcell("Shadow");
						lc.setStyle(sClassStyle);
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setStyle(sClassStyle);
						lc.setParent(item);
					} else {
						lc = new Listcell(entry.getTranCode());
						lc.setStyle(sClassStyle);
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setStyle(sClassStyle);
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
					lc.setStyle(sClassStyle);
					lc.setParent(item);

					lc = new Listcell(entry.getAcCcy());
					lc.setParent(item);

					BigDecimal amt = entry.getPostAmount() != null ? entry.getPostAmount() : BigDecimal.ZERO;
					lc = new Listcell(PennantApplicationUtil.amountFormate(amt, formatter));

					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setStyle(sClassStyle + "font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell("0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? ""
							: StringUtils.trimToEmpty(entry.getErrorId()));
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				
			  }
			 
			 }
				this.listBoxFinAccountings.appendChild(item);
			}

		}
		logger.debug("Leaving");
	}

	private void showAccounting(PayOrderIssueHeader issueHeader, boolean enquiry) {
		try {
			if (enquiry) {
				List<ReturnDataSet> returnDataSetList = getPostings(issueHeader);
				doFillAccounting(returnDataSetList);
			} else {
				List<ReturnDataSet> datasetList = getDisbursementPostings().getDisbPosting(issueHeader.getFinAdvancePaymentsList(), financeMain);
				doFillAccounting(datasetList);
			}

		} catch (Exception e) {
			logger.debug(e);
		}

	}

	private List<ReturnDataSet> getPostings(PayOrderIssueHeader issueHeader) {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<ReturnDataSet> postingAccount = new ArrayList<ReturnDataSet>();
		JdbcSearchObject<ReturnDataSet> searchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addFilterEqual("FinEvent", AccountEventConstants.ACCEVENT_DISBINS);
		searchObject.addTabelName("Postings_view");
		searchObject.addFilterEqual("finreference", issueHeader.getFinReference());
		List<ReturnDataSet> postings = pagedListService.getBySearchObject(searchObject);
		if (postings != null && !postings.isEmpty()) {
			return postings;
		}

		logger.debug("Leaving");
		return postingAccount;
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

	public PayOrderIssueHeader getPayOrderIssueHeader() {
		return this.payOrderIssueHeader;
	}

	public void setPayOrderIssueHeader(PayOrderIssueHeader payOrderIssueHeader) {
		this.payOrderIssueHeader = payOrderIssueHeader;
	}

	public PayOrderIssueService getPayOrderIssueService() {
		return payOrderIssueService;
	}

	public void setPayOrderIssueService(PayOrderIssueService payOrderIssueService) {
		this.payOrderIssueService = payOrderIssueService;
	}

	public PayOrderIssueListCtrl getPayOrderIssueListCtrl() {
		return payOrderIssueListCtrl;
	}

	public void setPayOrderIssueListCtrl(PayOrderIssueListCtrl payOrderIssueListCtrl) {
		this.payOrderIssueListCtrl = payOrderIssueListCtrl;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsList() {
		return finAdvancePaymentsList;
	}

	public void setFinAdvancePaymentsList(List<FinAdvancePayments> list) {
		this.finAdvancePaymentsList = list;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public DisbursementPostings getDisbursementPostings() {
		return disbursementPostings;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}
	
	public void setDisbursementInstCtrl(DisbursementInstCtrl disbursementInstCtrl) {
		this.disbursementInstCtrl = disbursementInstCtrl;
	}

}