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
 * FileName    		:  CustomerPaymentTxnsDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.customerPaymentTransactions;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDisbursement;
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
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerPaymentTransactions/CustomerPaymentTxnsDialog.zul file.
 */
public class CustomerPaymentTxnsDialogCtrl extends GFCBaseCtrl<FinAdvancePayments> {
	private static final long serialVersionUID = -8421583705358772016L;
	private static final Logger logger = Logger.getLogger(CustomerPaymentTxnsDialogCtrl.class);

	protected Window window_CustomerPaymentTxnsDialog;

	protected Textbox finReference;
	protected Grid grid_Basicdetails;

	private FinAdvancePayments finAdvancePayments;
	private FinanceDisbursement financeDisbursement;
	private transient CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl;
	private transient PayOrderIssueService payOrderIssueService;
	private transient boolean validationOn;

	int listRows;
	protected Label customerPaymentTxn_finType;
	protected Label customerPaymentTxn_custCIF;
	protected Checkbox customerPaymentTxn_quickDisb;
	protected Label customerPaymentTxn_finCcy;
	protected Label customerPaymentTxn_startDate;
	protected Label customerPaymentTxn_maturityDate;

	protected Label label_PayOrderIssueDialog_FinReference;
	protected Button button_PayOrderIssueDialog_NewDisbursement;
	protected Button btnTest;

	protected Listbox listboxCustomerPaymentTxns;
	protected Label label_AdvancePayments_Title;

	private List<FinAdvancePayments> finAdvancePaymentsList = new ArrayList<FinAdvancePayments>();
	private List<FinanceDisbursement> financeDisbursementList = new ArrayList<FinanceDisbursement>();
	private String ModuleType_CUSTPMTTXN = "CUSTPMTTXN";
	private DisbursementInstCtrl disbursementInstCtrl;
	private FinanceMain financeMain;
	private int ccyformat;

	protected Tab tabPosting;
	protected Listbox listBoxFinAccountings;
	private PostingsPreparationUtil postingsPreparationUtil;
	protected Decimalbox customerPaymentTxn_FinAssetValue;
	protected Decimalbox customerPaymentTxn_FinCurrAssetValue;
	private DisbursementPostings disbursementPostings;

	/**
	 * default constructor.<br>
	 */
	public CustomerPaymentTxnsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PayOrderIssueDialog";
	}

	public void onCreate$window_CustomerPaymentTxnsDialog(Event event) throws Exception {
		logger.debug("Entering");
		setPageComponents(window_CustomerPaymentTxnsDialog);

		try {
			if (arguments.containsKey("finAdvancePayments")) {
				this.finAdvancePayments = (FinAdvancePayments) arguments.get("finAdvancePayments");
				FinAdvancePayments befImage = new FinAdvancePayments();
				BeanUtils.copyProperties(this.finAdvancePayments, befImage);
				this.finAdvancePayments.setBefImage(befImage);
				setFinAdvancePayments(this.finAdvancePayments);
			} else {
				setFinAdvancePayments(null);
			}

			if (arguments.containsKey("financeMain")) {
				this.financeMain = (FinanceMain) arguments.get("financeMain");
				this.ccyformat = CurrencyUtil.getFormat(this.financeMain.getFinCcy());
			}

			doLoadWorkFlow(this.finAdvancePayments.isWorkflow(), this.finAdvancePayments.getWorkflowId(),
					this.finAdvancePayments.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "PayOrderIssueDialog");
			}
			if (arguments.containsKey("customerPaymentTxnsListCtrl")) {
				setCustomerPaymentTxnsListCtrl(
						(CustomerPaymentTxnsListCtrl) arguments.get("customerPaymentTxnsListCtrl"));
			} else {
				setCustomerPaymentTxnsListCtrl(null);
			}

			// Set the DialogController Height for listBox
			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 100 + 125;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listboxCustomerPaymentTxns.setHeight(listboxHeight + "px");
			this.listBoxFinAccountings.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinAdvancePayments());
			this.btnDelete.setVisible(false);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerPaymentTxnsDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.customerPaymentTxn_FinAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyformat));
		this.customerPaymentTxn_FinCurrAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyformat));

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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

	public void onClick$btnTest(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doTest();
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
		MessageUtil.showHelpWindow(event, window_CustomerPaymentTxnsDialog);
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
		doWriteBeanToComponents(this.finAdvancePayments.getBefImage(), this.financeDisbursement.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	public void doWriteBeanToComponents(FinAdvancePayments finAdvancePayments,
			FinanceDisbursement financeDisbursement) {
		logger.debug("Entering");

		this.customerPaymentTxn_finType
				.setValue(financeMain.getFinType() + " - " + financeMain.getFinType());
		this.customerPaymentTxn_finCcy
				.setValue(financeMain.getFinCcy() + " - " + CurrencyUtil.getCcyDesc(financeMain.getFinCcy()));

		this.customerPaymentTxn_startDate.setValue(DateUtility.formatToLongDate(financeMain.getFinStartDate()));
		this.customerPaymentTxn_maturityDate.setValue(DateUtility.formatToLongDate(financeMain.getMaturityDate()));

		this.finReference.setValue(financeMain.getFinReference());
		this.customerPaymentTxn_quickDisb.setChecked(financeMain.isQuickDisb());
		this.customerPaymentTxn_custCIF.setValue(financeMain.getCustCIF());

		this.customerPaymentTxn_FinAssetValue.setValue(formateAmount(financeMain.getFinAssetValue()));
		this.customerPaymentTxn_FinCurrAssetValue.setValue(formateAmount(financeMain.getFinCurrAssetValue()));
		doFillFinAdvancePaymentsDetails(finAdvancePayments, financeDisbursement);
		this.recordStatus.setValue(finAdvancePayments.getRecordStatus());

		if (!enqiryModule) {
			//Accounting Details Tab Addition
			showAccounting(finAdvancePayments, false);
		}
		if (enqiryModule) {
			showAccounting(finAdvancePayments, true);
		}

		logger.debug("Leaving");
	}

	private BigDecimal formateAmount(BigDecimal decimal) {
		return PennantApplicationUtil.formateAmount(decimal, ccyformat);
	}

	public void doWriteComponentsToBean(FinAdvancePayments finAdvancePayments) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			finAdvancePayments.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		disbursementInstCtrl.setFinanceDisbursement(finAdvancePayments.getFinanceDisbursements());
		//disbursementInstCtrl.setDocumentDetails(finAdvancePayments.getDocumentDetails());
		//List<ErrorDetail> valid = disbursementInstCtrl.validateFinAdvancePayment(getFinAdvancePaymentsList(), finAdvancePayments.isLoanApproved());
		/*
		 * valid = ErrorUtil.getErrorDetails(valid, getUserWorkspace().getUserLanguage()); if (valid != null &&
		 * !valid.isEmpty()) { for (ErrorDetail errorDetails : valid) { wve.add(new
		 * WrongValueException(this.label_AdvancePayments_Title, errorDetails.getError())); }
		 * 
		 * }
		 */

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		finAdvancePayments.setRecordStatus(this.recordStatus.getValue());
		setFinAdvancePayments(finAdvancePayments);
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
	public void doShowDialog(FinAdvancePayments finAdvancePayments) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (finAdvancePayments.isNew()) {
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
			disbursementInstCtrl.init(this.listboxCustomerPaymentTxns, financeMain.getFinCcy(), false, getRole());
			disbursementInstCtrl.setFinanceDisbursement(finAdvancePayments.getFinanceDisbursements());
			disbursementInstCtrl.setFinanceMain(financeMain);

			doWriteBeanToComponents(finAdvancePayments, financeDisbursement);
			this.button_PayOrderIssueDialog_NewDisbursement.setVisible(false);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_CustomerPaymentTxnsDialog.onClose();
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
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_PayOrderIssueDialog_PayOrderIssueHeaderCode.value"), null, true));
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
		getCustomerPaymentTxnsListCtrl().search();
	}

	/**
	 * Deletes a PayOrderIssueHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(getFinAdvancePayments(), finAdvancePayments);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CustomerPaymentTxnsDialog_finReference.value") + " : "
				+ finAdvancePayments.getFinReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doWriteBeanToComponents(finAdvancePayments, financeDisbursement);

			if (StringUtils.isBlank(finAdvancePayments.getRecordType())) {
				finAdvancePayments.setVersion(finAdvancePayments.getVersion() + 1);
				finAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					finAdvancePayments.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(finAdvancePayments, tranType)) {
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

		if (getFinAdvancePayments().isNewRecord()) {
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

			if (this.finAdvancePayments.isNewRecord()) {
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
		final FinAdvancePayments finAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(getFinAdvancePayments(), finAdvancePayments);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(finAdvancePayments);

		isNew = finAdvancePayments.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(finAdvancePayments.getRecordType())) {
				finAdvancePayments.setVersion(finAdvancePayments.getVersion() + 1);
				if (isNew) {
					finAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					finAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					finAdvancePayments.setNewRecord(true);
				}
			}
		} else {
			finAdvancePayments.setVersion(finAdvancePayments.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (finAdvancePayments.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)
					|| !(this.finAdvancePaymentsList == null || this.finAdvancePaymentsList.size() == 0)) {
				if (doProcess(finAdvancePayments, tranType)) {
					refreshList();
					closeDialog();
				}
			} else {
				MessageUtil.showError(Labels.getLabel("List_Error",
						new String[] { Labels.getLabel("window_CustomerPaymentTxnsDialog.title"),
								Labels.getLabel("window_CustomerPaymentTxnsDialog.title") }));
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void doTest() throws InterruptedException {
		logger.debug("Entering");
		try {
			List<FinAdvancePayments> finAdvancePaymentsLists = new ArrayList<FinAdvancePayments>();
			FinAdvancePayments finAdvancePayments = new FinAdvancePayments();

			Calendar calendar = Calendar.getInstance();
			String time = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR))
					+ String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + String.valueOf(calendar.get(Calendar.MINUTE))
					+ String.valueOf(calendar.get(Calendar.SECOND));

			finAdvancePayments.setPaymentId(Integer.parseInt(time));
			finAdvancePayments.setAmtToBeReleased(new BigDecimal("99.5"));
			finAdvancePayments.setPartnerBankAc("09582650000173");
			finAdvancePayments.setLLDate(new Date());
			finAdvancePayments.setiFSC("BOFA0BG3978");
			finAdvancePayments.setBeneficiaryAccNo("1234569874");
			finAdvancePayments.setBeneficiaryName("INDIA TEST TEST");
			finAdvancePayments.setPhoneNumber("8970987873");
			finAdvancePayments.setClearingDate(new Date());
			finAdvancePayments.setCity("IND");
			finAdvancePayments.setPaymentType("IFSC");
			finAdvancePayments.setRemarks("cms tranction details");
			finAdvancePayments.setPayableLoc("103");
			finAdvancePayments.setPrintingLoc("234");

			finAdvancePaymentsLists.add(finAdvancePayments);

			//CustomerPaymentServiceImpl impl =new CustomerPaymentServiceImpl();

			//impl.processOnlinePayment(finAdvancePaymentsLists);

			//call To Service
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showMessage(e.getMessage());
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param finAdvancePayments
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(FinAdvancePayments finAdvancePayments, String tranType) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		finAdvancePayments.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		finAdvancePayments.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		finAdvancePayments.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			finAdvancePayments.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(finAdvancePayments.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, finAdvancePayments);
				}

				if (isNotesMandatory(taskId, finAdvancePayments)) {
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

			finAdvancePayments.setTaskId(taskId);
			finAdvancePayments.setNextTaskId(nextTaskId);
			finAdvancePayments.setRoleCode(getRole());
			finAdvancePayments.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(finAdvancePayments, tranType);
			String operationRefs = getServiceOperations(taskId, finAdvancePayments);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(finAdvancePayments, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(finAdvancePayments, tranType);
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerPaymentTxnsDialog, auditHeader);
						return processCompleted;

					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPaymentTxnsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
				}

				if (deleteNotes) {
					deleteNotes(getNotes(this.finAdvancePayments), true);
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

	public void doFillFinAdvancePaymentsDetails(FinAdvancePayments finAdvancePayDetail,
			FinanceDisbursement financeDisbursement) {
		logger.debug("Entering");
		List<FinAdvancePayments> finAdvancePayDetails = new ArrayList<FinAdvancePayments>();
		List<FinanceDisbursement> financeDisbursements = new ArrayList<FinanceDisbursement>();
		finAdvancePayDetails.add(finAdvancePayDetail);
		disbursementInstCtrl.doFillFinAdvancePaymentsDetailss(finAdvancePayDetails, false);
		setFinAdvancePaymentsList(finAdvancePayDetails);
		setFinanceDisbursementList(financeDisbursements);
		logger.debug("Leaving");
	}

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
		disbursementInstCtrl.onClickNew(this.customerPaymentTxnsListCtrl, this, ModuleType_CUSTPMTTXN,
				getFinAdvancePaymentsList());

		logger.debug("Leaving" + event.toString());
	}

	public void onFinAdvancePaymentsItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		disbursementInstCtrl.onDoubleClick(this.customerPaymentTxnsListCtrl, this, ModuleType_CUSTPMTTXN, true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aGender
	 *            (Gender)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(FinAdvancePayments finAdvancePayments, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finAdvancePayments.getBefImage(), finAdvancePayments);
		return new AuditHeader(String.valueOf(finAdvancePayments.getFinReference()), null, null, null, auditDetail,
				finAdvancePayments.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerPaymentTxnsDialog, auditHeader);
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
		doShowNotes(this.finAdvancePayments);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finAdvancePayments.getFinReference());
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

		int formatter = ccyformat;

		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
				if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);

					if (entry.getPostAmount().compareTo(BigDecimal.ZERO) != 0) {

						String sClassStyle = "";
						if (StringUtils.isNotBlank(entry.getErrorId())
								&& !"0000".equals(StringUtils.trimToEmpty(entry.getErrorId()))) {
							sClassStyle = "color:#FF0000;";
						}

						Hbox hbox = new Hbox();
						Label label = new Label(
								PennantAppUtil.getlabelDesc(entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
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

	private void showAccounting(FinAdvancePayments issueHeader, boolean enquiry) {
		try {
			if (enquiry) {
				List<ReturnDataSet> returnDataSetList = getPostings(issueHeader);
				doFillAccounting(returnDataSetList);
			} else {
				/*
				 * List<ReturnDataSet> datasetList = getDisbursementPostings()
				 * .getDisbPosting(issueHeader.getFinAdvancePaymentsList(), financeMain);
				 */
				//doFillAccounting(datasetList);
			}

		} catch (Exception e) {
			logger.debug(e);
		}

	}

	private List<ReturnDataSet> getPostings(FinAdvancePayments issueHeader) {
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

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CustomerPaymentTxnsListCtrl getCustomerPaymentTxnsListCtrl() {
		return customerPaymentTxnsListCtrl;
	}

	public void setCustomerPaymentTxnsListCtrl(CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl) {
		this.customerPaymentTxnsListCtrl = customerPaymentTxnsListCtrl;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsList() {
		return finAdvancePaymentsList;
	}

	public void setFinAdvancePaymentsList(List<FinAdvancePayments> list) {
		this.finAdvancePaymentsList = list;
	}

	public List<FinanceDisbursement> getFinanceDisbursementList() {
		return financeDisbursementList;
	}

	public void setFinanceDisbursementList(List<FinanceDisbursement> list) {
		this.financeDisbursementList = list;
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

	public FinAdvancePayments getFinAdvancePayments() {
		return finAdvancePayments;
	}

	public void setFinAdvancePayments(FinAdvancePayments finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
	}

	public PayOrderIssueService getPayOrderIssueService() {
		return payOrderIssueService;
	}

	public void setPayOrderIssueService(PayOrderIssueService payOrderIssueService) {
		this.payOrderIssueService = payOrderIssueService;
	}

}