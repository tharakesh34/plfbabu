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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.finance.payorderissue.PayOrderIssueDialogCtrl;
import com.pennant.webui.finance.payorderissue.PayOrderIssueListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsDialog.zul file.
 */
public class FinAdvancePaymentsDialogCtrl extends GFCBaseCtrl<FinAdvancePayments> {
	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger
			.getLogger(FinAdvancePaymentsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_FinAdvancePaymentsDialog;

	protected Combobox							disbDate;
	protected Decimalbox						disbDateAmount;
	protected Intbox							disbSeq;
	protected Intbox							paymentSequence;
	protected Combobox							paymentDetail;
	protected CurrencyBox						amtToBeReleased;
	protected Textbox							liabilityHoldName;
	protected Textbox							beneficiaryName;
	protected Label								label_FinAdvancePaymentsDialog_BeneficiaryName;
	protected Textbox							beneficiaryAccNo;
	protected Label								label_FinAdvancePaymentsDialog_BeneficiaryAccNo;
	protected Textbox							description;
	protected Combobox							paymentType;
	protected Textbox							llReferenceNo;
	protected Datebox							llDate;
	protected CurrencyBox						custContribution;
	protected CurrencyBox						sellerContribution;
	protected Textbox							remarks;
	protected Textbox							transactionRef;
	protected ExtendedCombobox					bankCode;
	protected Textbox							payableLoc;
	protected Textbox							printingLoc;
	protected Datebox							valueDate;
	protected ExtendedCombobox					bankBranchID;
	protected ExtendedCombobox					partnerBankID;
	protected Textbox							bank;
	protected Textbox							branch;
	protected Textbox							city;
	protected Space								contactNumber;
	//protected Textbox							phoneCountryCode;
	//protected Textbox							phoneAreaCode;
	protected Textbox							phoneNumber;

	protected Label								label_liabilityHoldName;
	protected Hbox								hbox_liabilityHoldName;
	protected Label								label_llReferenceNo;
	protected Hbox								hbox_llReferenceNo;
	protected Label								label_llDate;
	protected Hbox								hbox_llDate;
	protected Label								label_custContribution;
	protected Hbox								hbox_custContribution;
	protected Label								label_sellerContribution;
	protected Hbox								hbox_sellerContribution;

	protected Label								recordType;
	protected Groupbox							gb_statusDetails;
	protected Groupbox							gb_ChequeDetails;
	protected Groupbox							gb_NeftDetails;
	private boolean								enqModule			= false;
	protected Button							btnGetCustBeneficiary;
	protected Row								rowGetCust;
	protected Caption							caption_FinAdvancePaymentsDialog_NeftDetails;
	protected Caption							caption_FinAdvancePaymentsDialog_ChequeDetails;

	// not auto wired vars
	private FinAdvancePayments					finAdvancePayments;								// over handed per param

	private transient boolean					newFinance;

	// ServiceDAOs / Domain Classes
	private transient PagedListService			pagedListService;

	private Object								financeMainDialogCtrl;
	private FinAdvancePaymentsListCtrl			finAdvancePaymentsListCtrl;
	private transient PayOrderIssueListCtrl		payOrderIssueListCtrl;
	private transient PayOrderIssueDialogCtrl	payOrderIssueDialogCtrl;
	private boolean								newRecord			= false;
	private boolean								newCustomer			= false;
	private int									ccyFormatter		= 0;
	private long								custID;
	private String								finCcy;

	private String								moduleType			= "";

	private List<FinAdvancePayments>			finAdvancePaymentsDetails;

	private boolean								poIssued			= false;
	private boolean								allowMultyparty		= false;
	private List<FinanceDisbursement>			financeDisbursement	= null;
	private List<FinanceDisbursement>			approvedDisbursments;

	protected int								accNoLength;
	private transient BankDetailService			bankDetailService;
	private FinanceMain							financeMain;

	/**
	 * default constructor.<br>
	 */
	public FinAdvancePaymentsDialogCtrl() {
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
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinAdvancePaymentsDialog(Event event) throws Exception {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_FinAdvancePaymentsDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("multiParty")) {
				allowMultyparty = (Boolean) arguments.get("multiParty");
			} else {
				allowMultyparty = false;
			}

			if (arguments.containsKey("financeDisbursement")) {
				financeDisbursement = (List<FinanceDisbursement>) arguments.get("financeDisbursement");
			} else {
				financeDisbursement = null;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("finAdvancePayments")) {
				this.finAdvancePayments = (FinAdvancePayments) arguments.get("finAdvancePayments");
				FinAdvancePayments befImage = new FinAdvancePayments();
				BeanUtils.copyProperties(this.finAdvancePayments, befImage);
				this.finAdvancePayments.setBefImage(befImage);

				setFinAdvancePayments(this.finAdvancePayments);
				poIssued = this.finAdvancePayments.ispOIssued();
			} else {
				setFinAdvancePayments(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("approvedDisbursments")) {
				approvedDisbursments = (List<FinanceDisbursement>) arguments.get("approvedDisbursments");
			}
			if (arguments.containsKey("financeMain")) {
				financeMain = (FinanceMain) arguments.get("financeMain");
			}

			if (this.finAdvancePayments.isNewRecord()) {
				setNewRecord(true);
			}
			if (arguments.containsKey("finAdvancePaymentsListCtrl")) {
				setFinAdvancePaymentsListCtrl((FinAdvancePaymentsListCtrl) arguments.get("finAdvancePaymentsListCtrl"));
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {

				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("ccyFormatter")) {
					ccyFormatter = (Integer) arguments.get("ccyFormatter");
				}

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.finAdvancePayments.setWorkflowId(0);
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			if (arguments.containsKey("payOrderIssueListCtrl")) {
				setPayOrderIssueListCtrl((PayOrderIssueListCtrl) arguments.get("payOrderIssueListCtrl"));
			}

			if (arguments.containsKey("payOrderIssueDialogCtrl")) {
				setPayOrderIssueDialogCtrl((PayOrderIssueDialogCtrl) arguments.get("payOrderIssueDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("ccyFormatter")) {
					ccyFormatter = (Integer) arguments.get("ccyFormatter");
				}

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				setNewFinance(true);
				this.finAdvancePayments.setWorkflowId(0);
			}
			if (arguments.containsKey("custID")) {
				custID = (long) arguments.get("custID");
			}
			if (arguments.containsKey("finCcy")) {
				finCcy = (String) arguments.get("finCcy");
			}
			doLoadWorkFlow(this.finAdvancePayments.isWorkflow(), this.finAdvancePayments.getWorkflowId(),
					this.finAdvancePayments.getNextTaskId());

			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			getUserWorkspace().allocateAuthorities("FinAdvancePaymentsDialog", getRole());

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.finAdvancePayments);
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
		MessageUtil.showHelpWindow(event, window_FinAdvancePaymentsDialog);
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
		doShowNotes(this.finAdvancePayments);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finAdvancePayments.getFinReference());
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
	public void doShowDialog(FinAdvancePayments aFinAdvancePayments) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinAdvancePayments.isNew()) {

			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.paymentDetail.focus();
		} else {
			this.paymentDetail.focus();
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
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinAdvancePayments);
			if (poIssued) {
				doReadOnly();
				this.btnSave.setVisible(false);
				this.btnGetCustBeneficiary.setVisible(false);
			}

			this.window_FinAdvancePaymentsDialog.setHeight("65%");
			this.window_FinAdvancePaymentsDialog.setWidth("85%");
			this.gb_statusDetails.setVisible(false);
			this.window_FinAdvancePaymentsDialog.doModal();

		} catch (Exception e) {
			this.window_FinAdvancePaymentsDialog.onClose();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (this.finAdvancePayments.isNewRecord()) {
			this.paymentDetail.setDisabled(isReadOnly("FinAdvancePaymentsDialog_paymentDetail"));
		} else {
			this.paymentDetail.setDisabled(true);
		}
		this.disbDate.setDisabled(isReadOnly("FinAdvancePaymentsDialog_llDate"));
		this.amtToBeReleased.setDisabled(isReadOnly("FinAdvancePaymentsDialog_amtToBeReleased"));
		this.llDate.setDisabled(isReadOnly("FinAdvancePaymentsDialog_llDate"));
		this.paymentType.setDisabled(isReadOnly("FinAdvancePaymentsDialog_paymentType"));
		this.remarks.setReadonly(isReadOnly("FinAdvancePaymentsDialog_remarks"));
		this.transactionRef.setReadonly(true);
		//2
		this.bankBranchID.setReadonly(isReadOnly("FinAdvancePaymentsDialog_bankBranchID"));
		this.beneficiaryAccNo.setReadonly(isReadOnly("FinAdvancePaymentsDialog_beneficiaryAccNo"));
		this.beneficiaryName.setReadonly(isReadOnly("FinAdvancePaymentsDialog_beneficiaryName"));
		this.phoneNumber.setReadonly(isReadOnly("FinAdvancePaymentsDialog_contactNumber"));
		//3
		this.bankCode.setReadonly(isReadOnly("FinAdvancePaymentsDialog_bankCode"));
		this.liabilityHoldName.setReadonly(isReadOnly("FinAdvancePaymentsDialog_liabilityHoldName"));
		this.llReferenceNo.setReadonly(isReadOnly("FinAdvancePaymentsDialog_llReferenceNo"));
		this.payableLoc.setReadonly(isReadOnly("FinAdvancePaymentsDialog_payableLoc"));
		this.printingLoc.setReadonly(isReadOnly("FinAdvancePaymentsDialog_printingLoc"));
		this.valueDate.setReadonly(isReadOnly("FinAdvancePaymentsDialog_valueDate"));
		this.description.setReadonly(isReadOnly("FinAdvancePaymentsDialog_description"));
		this.custContribution.setDisabled(isReadOnly("FinAdvancePaymentsDialog_custContribution"));
		this.sellerContribution.setDisabled(isReadOnly("FinAdvancePaymentsDialog_sellerContribution"));
		this.partnerBankID.setReadonly(isReadOnly("FinAdvancePaymentsDialog_partnerBankID"));

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

		this.paymentSequence.setReadonly(true);
		this.paymentDetail.setDisabled(true);
		this.disbDate.setDisabled(true);
		this.liabilityHoldName.setReadonly(true);
		this.description.setReadonly(true);
		this.amtToBeReleased.setDisabled(true);
		this.beneficiaryName.setReadonly(true);
		this.beneficiaryAccNo.setReadonly(true);
		this.paymentType.setDisabled(true);
		this.llReferenceNo.setReadonly(true);
		this.llDate.setDisabled(true);
		this.custContribution.setDisabled(true);
		this.sellerContribution.setDisabled(true);
		this.remarks.setReadonly(true);
		this.bankCode.setReadonly(true);
		this.bankBranchID.setReadonly(true);
		this.payableLoc.setDisabled(true);
		this.printingLoc.setDisabled(true);
		this.valueDate.setDisabled(true);
		this.phoneNumber.setReadonly(true);
		this.partnerBankID.setReadonly(true);
		this.transactionRef.setReadonly(true);

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
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinAdvancePaymentsDialog_btnSave"));
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
		this.paymentSequence.setReadonly(true);
		this.liabilityHoldName.setMaxlength(100);
		this.beneficiaryName.setMaxlength(100);
		this.description.setMaxlength(500);
		this.llReferenceNo.setMaxlength(6);
		this.remarks.setMaxlength(500);
		this.transactionRef.setReadonly(true);

		this.amtToBeReleased.setMandatory(true);
		this.amtToBeReleased.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.amtToBeReleased.setScale(ccyFormatter);
		this.amtToBeReleased.setTextBoxWidth(150);

		this.disbDateAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.disbDateAmount.setScale(ccyFormatter);

		this.beneficiaryAccNo.setMaxlength(50);

		this.llDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.custContribution.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.custContribution.setScale(ccyFormatter);
		this.custContribution.setTextBoxWidth(150);

		this.sellerContribution.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.sellerContribution.setScale(ccyFormatter);
		this.sellerContribution.setTextBoxWidth(150);

		this.liabilityHoldName.setWidth("150px");
		this.beneficiaryName.setWidth("150px");
		this.llDate.setWidth("150px");
		this.valueDate.setWidth("150px");
		this.llReferenceNo.setWidth("150px");
		this.description.setWidth("150px");
		this.remarks.setWidth("150px");
		this.printingLoc.setWidth("150px");

		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "BankCode" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("IFSC");
		this.bankBranchID.setDescColumn("");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "IFSC" });

		this.partnerBankID.setButtonDisabled(true);
		this.partnerBankID.setReadonly(true);
		this.partnerBankID.setModuleName("FinTypePartner");
		this.partnerBankID.setMandatoryStyle(true);
		this.partnerBankID.setValueColumn("PartnerBankCode");
		this.partnerBankID.setDescColumn("PartnerBankName");
		this.partnerBankID.setMaxlength(8);
		this.partnerBankID.setValidateColumns(new String[] { "PartnerBankCode" });

		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");

		if (StringUtils.isNotBlank(this.finAdvancePayments.getBranchBankCode())) {
			accNoLength = bankDetailService.getAccNoLengthByCode(this.finAdvancePayments.getBranchBankCode());
		}

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
		doWriteBeanToComponents(this.finAdvancePayments.getBefImage());
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
	public void doWriteBeanToComponents(FinAdvancePayments aFinAdvnancePayments) {
		logger.debug("Entering");

		if (isNewRecord()) {
			aFinAdvnancePayments.setStatus(DisbursementConstants.STATUS_NEW);
		}

		if (aFinAdvnancePayments.isNewRecord() && StringUtils.isEmpty(aFinAdvnancePayments.getPaymentDetail())) {
			aFinAdvnancePayments.setPaymentDetail(DisbursementConstants.PAYMENT_DETAIL_CUSTOMER);
		}
		List<String> list = new ArrayList<>(2);
		if (!allowMultyparty) {
			list.add(DisbursementConstants.PAYMENT_DETAIL_THIRDPARTY);
			list.add(DisbursementConstants.PAYMENT_DETAIL_VENDOR);
		}

		this.paymentSequence.setValue(aFinAdvnancePayments.getPaymentSeq());
		this.disbSeq.setValue(aFinAdvnancePayments.getDisbSeq());

		fillComboBox(this.paymentDetail, aFinAdvnancePayments.getPaymentDetail(),
				PennantStaticListUtil.getPaymentDetails(), list);
		if (financeDisbursement != null) {
			int seq = aFinAdvnancePayments.getDisbSeq();
			if (financeDisbursement.size() == 1 && aFinAdvnancePayments.isNewRecord()) {
				seq = financeDisbursement.get(0).getDisbSeq();
			}
			fillComboBox(this.disbDate, seq, financeDisbursement, isNewRecord());
		}
		setDisbursmentAmount();

		this.amtToBeReleased.setValue(formate(aFinAdvnancePayments.getAmtToBeReleased()));
		if (aFinAdvnancePayments.isNewRecord() && aFinAdvnancePayments.getLlDate() == null) {
			this.llDate.setValue(DateUtility.getAppDate());
		} else {
			this.llDate.setValue(aFinAdvnancePayments.getLlDate());
		}
		fillComboBox(this.paymentType, aFinAdvnancePayments.getPaymentType(),
				PennantStaticListUtil.getPaymentTypes(false), "");
		this.remarks.setValue(aFinAdvnancePayments.getRemarks());
		//banking
		if (aFinAdvnancePayments.getBankBranchID() != Long.MIN_VALUE && aFinAdvnancePayments.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aFinAdvnancePayments.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getiFSC()));
		}
		if (aFinAdvnancePayments.getPartnerBankID() != Long.MIN_VALUE && aFinAdvnancePayments.getPartnerBankID() != 0) {
			this.partnerBankID.getButton().setDisabled(isReadOnly("FinAdvancePaymentsDialog_partnerBankID"));
			this.partnerBankID.setAttribute("partnerBankId", aFinAdvnancePayments.getPartnerBankID());
			this.partnerBankID.setValue(aFinAdvnancePayments.getPartnerbankCode(),
					aFinAdvnancePayments.getPartnerBankName());
		}

		this.bank.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getBranchBankName()));
		this.branch.setValue(aFinAdvnancePayments.getBranchDesc());
		this.city.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getCity()));
		this.beneficiaryAccNo.setValue(aFinAdvnancePayments.getBeneficiaryAccNo());
		this.beneficiaryName.setValue(aFinAdvnancePayments.getBeneficiaryName());
		this.transactionRef.setValue(aFinAdvnancePayments.getTransactionRef());

		this.phoneNumber.setValue(aFinAdvnancePayments.getPhoneNumber());
		//other 
		this.bankCode.setAttribute("bankCode", aFinAdvnancePayments.getBankCode());
		this.bankCode.setValue(StringUtils.trimToEmpty(aFinAdvnancePayments.getBankCode()),
				StringUtils.trimToEmpty(aFinAdvnancePayments.getBankName()));
		this.liabilityHoldName.setValue(aFinAdvnancePayments.getLiabilityHoldName());
		this.llReferenceNo.setValue(aFinAdvnancePayments.getLlReferenceNo());
		this.payableLoc.setValue(aFinAdvnancePayments.getPayableLoc());
		this.printingLoc.setValue(aFinAdvnancePayments.getPrintingLoc());
		this.valueDate.setValue(aFinAdvnancePayments.getValueDate());
		//unused
		this.custContribution.setValue(formate(aFinAdvnancePayments.getCustContribution()));
		this.sellerContribution.setValue(formate(aFinAdvnancePayments.getSellerContribution()));

		this.description.setValue(aFinAdvnancePayments.getDescription());
		checkPaymentType(aFinAdvnancePayments.getPaymentType());
		this.recordStatus.setValue(aFinAdvnancePayments.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aFinAdvnancePayments.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the combobox with given list of values and will exclude the the values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBox(Combobox combobox, int seq, List<FinanceDisbursement> list, boolean execuledApprove) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceDisbursement disbursement : list) {
			if (execuledApprove && isContainsInAppList(disbursement)) {
				continue;
			}
			//cancelled disbursement should not be allowed to process
			if (StringUtils.trimToEmpty(disbursement.getDisbStatus()).equals(FinanceConstants.DISB_STATUS_CANCEL)) {
				continue;
			}

			comboitem = new Comboitem();
			String label = DateUtility.formatToLongDate(disbursement.getDisbDate());
			label = label.concat(" , ") + disbursement.getDisbSeq();
			comboitem.setLabel(label);
			comboitem.setValue(disbursement.getDisbDate());
			comboitem.setAttribute("data", disbursement);
			combobox.appendChild(comboitem);
			if (seq == disbursement.getDisbSeq()) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	private boolean isContainsInAppList(FinanceDisbursement disbursement) {
		if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : approvedDisbursments) {
				if (disbursement.getDisbDate().getTime() == financeDisbursement.getDisbDate().getTime()
						&& disbursement.getDisbSeq() == financeDisbursement.getDisbSeq()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * To avoid length of the line
	 * 
	 * @param amt
	 * @return
	 */
	private BigDecimal formate(BigDecimal amt) {
		return PennantApplicationUtil.formateAmount(amt, ccyFormatter);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinAdvancePayments
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinAdvancePayments aFinAdvancePayments) throws InterruptedException {
		logger.debug("Entering");
		String paymentType = "";

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinAdvancePayments.setPaymentSeq(this.paymentSequence.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if ("#".equals(getComboboxValue(this.disbDate))) {
				throw new WrongValueException(this.disbDate, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_DisbDate.value") }));

			} else {
				Comboitem select = this.disbDate.getSelectedItem();
				FinanceDisbursement disbursement = (FinanceDisbursement) select.getAttribute("data");
				aFinAdvancePayments.setDisbSeq(disbursement.getDisbSeq());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.paymentDetail))) {
				if (this.paymentDetail.isVisible() && !this.paymentDetail.isDisabled()) {
					throw new WrongValueException(this.paymentDetail, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value") }));
				} else {
					aFinAdvancePayments.setPaymentDetail(null);
				}
			} else {
				aFinAdvancePayments.setPaymentDetail(getComboboxValue(this.paymentDetail));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.paymentDetail))) {
				if (this.paymentDetail.isVisible() && !this.paymentDetail.isDisabled()) {
					throw new WrongValueException(this.paymentDetail, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value") }));
				} else {
					aFinAdvancePayments.setPaymentDetail(null);
				}
			} else {
				aFinAdvancePayments.setPaymentDetail(getComboboxValue(this.paymentDetail));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setLiabilityHoldName(this.liabilityHoldName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setAmtToBeReleased(PennantAppUtil.unFormateAmount(this.amtToBeReleased.isReadonly()
					? this.amtToBeReleased.getActualValue() : this.amtToBeReleased.getValidateValue(), ccyFormatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setCustContribution(
					PennantApplicationUtil.unFormateAmount(this.custContribution.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setSellerContribution(
					PennantApplicationUtil.unFormateAmount(this.sellerContribution.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setBeneficiaryAccNo(this.beneficiaryAccNo.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			paymentType = getComboboxValue(this.paymentType);
			if ("#".equals(paymentType)) {
				if (this.paymentType.isVisible() && !this.paymentType.isDisabled()) {
					throw new WrongValueException(this.paymentType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentType.value") }));
				} else {
					aFinAdvancePayments.setPaymentType(null);
				}
			} else {
				aFinAdvancePayments.setPaymentType(paymentType);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Comboitem item = this.disbDate.getSelectedItem();
			if (item != null) {
				Object data = item.getAttribute("data");
				if (data != null) {
					FinanceDisbursement disbursement = (FinanceDisbursement) data;
					if (this.llDate.getValue() != null) {
						if (financeMain != null && financeMain.getMaturityDate() != null) {
							if (this.llDate.getValue().before(disbursement.getDisbDate())
									|| this.llDate.getValue().after(financeMain.getMaturityDate())) {

								String maturityDate = DateUtility.formatToLongDate(financeMain.getMaturityDate());
								String disbDate = DateUtility.formatToLongDate(disbursement.getDisbDate());

								throw new WrongValueException(this.llDate,
										Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
												new String[] {
														Labels.getLabel("label_FinAdvancePaymentsDialog_LLDate.value"),
														disbDate, maturityDate }));
							}
						}
						aFinAdvancePayments.setLLDate(this.llDate.getValue());
					}

					BigDecimal disAmt = DisbursementInstCtrl.getTotalByDisbursment(disbursement, financeMain);
					BigDecimal insAmt = getAdjustedAmount(disbursement);
					insAmt=insAmt.add(aFinAdvancePayments.getAmtToBeReleased());
					if (insAmt.compareTo(disAmt) > 0) {
						throw new WrongValueException(this.amtToBeReleased,
								Labels.getLabel("NUMBER_MAXVALUE_EQ",
										new String[] {
												Labels.getLabel("label_FinAdvancePaymentsDialog_AmtToBeReleased.value"),
												Labels.getLabel("label_FinAdvancePaymentsDialog_DisbAmount.value") }));
					}

				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setBeneficiaryName(this.beneficiaryName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setLLReferenceNo(this.llReferenceNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankCode.getValidatedValue();
			Object obj = this.bankCode.getAttribute("bankCode");
			if (obj != null) {
				aFinAdvancePayments.setBankCode(String.valueOf(obj));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAdvancePayments.setPayableLoc(this.payableLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setPrintingLoc(this.printingLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.valueDate.getValue() != null) {
				aFinAdvancePayments.setValueDate(this.valueDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				aFinAdvancePayments.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			boolean mandatory = false;
			if (DisbursementConstants.PAYMENT_TYPE_IMPS.equals(paymentType)) {
				mandatory = true;
			}

			this.phoneNumber.clearErrorMessage();

			this.phoneNumber.setErrorMessage("");

			if (!this.phoneNumber.isReadonly()) {
				this.phoneNumber.setConstraint(new PTMobileNumberValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_PhoneNumber.value"), mandatory));
			}

			aFinAdvancePayments.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinAdvancePayments.setDisbCCy(finCcy);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.partnerBankID.getValidatedValue();
			Object obj = this.partnerBankID.getAttribute("partnerBankId");
			if (obj != null) {
				aFinAdvancePayments.setPartnerBankID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinAdvancePayments.setTransactionRef(this.transactionRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinAdvancePayments.setLinkedTranId(0);
		doRemoveValidation();
		doClearMessage();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFinAdvancePayments.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Change the partnerBankID for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$partnerBankID(Event event) {
		logger.debug("Entering");
		Object dataObject = partnerBankID.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.partnerBankID.setValue(dataObject.toString());
				this.partnerBankID.setDescription("");
			}

		} else {
			FinTypePartnerBank partnerBank = (FinTypePartnerBank) dataObject;
			if (partnerBank != null) {
				this.partnerBankID.setAttribute("partnerBankId", partnerBank.getPartnerBankID());
				this.finAdvancePayments.setPartnerbankCode(partnerBank.getPartnerBankCode());
				this.finAdvancePayments.setPartnerBankName(partnerBank.getPartnerBankName());

			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.paymentDetail.isDisabled()) {
			this.paymentDetail.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentDetail.value"), null, true));
		}
		if (!this.amtToBeReleased.isDisabled()) {
			this.amtToBeReleased.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_AmtToBeReleased.value"),
							ccyFormatter, true, false));
		}
		if (!this.paymentType.isDisabled()) {
			this.paymentType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentType.value"), null, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_Description.value"), null, false));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_Remarks.value"), null, false));
		}
		if (this.hbox_llReferenceNo.isVisible() && !this.llReferenceNo.isReadonly()) {
			this.llReferenceNo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_LLReferenceNo.value"), null, false));
		}
		if (this.hbox_llDate.isVisible() && !this.llDate.isDisabled()) {
			this.llDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LLDate.value"), true));
		}
		if (this.hbox_custContribution.isVisible() && !this.custContribution.isDisabled()) {
			this.custContribution.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_CustContribution.value"),
							ccyFormatter, false, false));
		}
		if (this.hbox_sellerContribution.isVisible() && !this.sellerContribution.isDisabled()) {
			this.sellerContribution.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_SellerContribution.value"),
							ccyFormatter, false, false));
		}

		if (!this.partnerBankID.isReadonly()) {
			this.partnerBankID.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinAdvancePaymentsDialog_PartnerbankId.value"), null, true));
		}

		if (gb_ChequeDetails.isVisible()) {
			if (this.hbox_liabilityHoldName.isVisible() && !this.liabilityHoldName.isReadonly()) {
				this.liabilityHoldName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_LiabilityHoldName.value"),
								PennantRegularExpressions.REGEX_FAVOURING_NAME, true));
			}
			if (!this.bankCode.isReadonly()) {
				this.bankCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_BankCode.value"), null, true));
			}
			if (!this.payableLoc.isReadonly()) {
				this.payableLoc.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_PayableLoc.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
			if (!this.printingLoc.isReadonly()) {
				this.printingLoc.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_PrintingLoc.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}
			if (!this.valueDate.isReadonly()) {
				Date todate = DateUtility.addMonths(DateUtility.getAppDate(), 6);
				this.valueDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_ValueDate.value"), true,
								DateUtility.getAppDate(), todate, true));
			}
		} else {
			if (!this.bankBranchID.isReadonly()) {
				this.bankBranchID.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinAdvancePaymentsDialog_BankBranchID.value"), null, true));
			}
			if (!this.beneficiaryName.isReadonly()) {
				this.beneficiaryName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_BeneficiaryName.value"),
								PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
			}
			if (!this.beneficiaryAccNo.isReadonly()) {
				this.beneficiaryAccNo.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinAdvancePaymentsDialog_BeneficiaryAccNo.value"),
								PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.paymentDetail.setConstraint("");
		this.liabilityHoldName.setConstraint("");
		this.amtToBeReleased.setConstraint("");
		this.beneficiaryAccNo.setConstraint("");
		this.paymentType.setConstraint("");
		this.beneficiaryName.setConstraint("");
		this.description.setConstraint("");
		this.llReferenceNo.setConstraint("");
		this.llDate.setConstraint("");
		this.custContribution.setConstraint("");
		this.sellerContribution.setConstraint("");
		this.remarks.setConstraint("");
		this.bankCode.setConstraint("");
		this.payableLoc.setConstraint("");
		this.printingLoc.setConstraint("");
		this.valueDate.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.transactionRef.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.paymentDetail.setErrorMessage("");
		this.liabilityHoldName.setErrorMessage("");
		this.amtToBeReleased.setErrorMessage("");
		this.beneficiaryAccNo.setErrorMessage("");
		this.paymentType.setErrorMessage("");
		this.beneficiaryName.setErrorMessage("");
		this.description.setErrorMessage("");
		this.llReferenceNo.setErrorMessage("");
		this.llDate.setErrorMessage("");
		this.custContribution.setErrorMessage("");
		this.sellerContribution.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.payableLoc.setErrorMessage("");
		this.printingLoc.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.transactionRef.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a FinAdvancePaymentsDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(this.finAdvancePayments, aFinAdvancePayments);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n"
				+ Labels.getLabel("label_FinAdvancePaymentsDialog_PaymentSequence.value") + " : "
				+ aFinAdvancePayments.getPaymentSeq();

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
					auditHeader = ErrorControl.showErrorDetails(this.window_FinAdvancePaymentsDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (this.moduleType.equals("LOAN")) {
							getFinAdvancePaymentsListCtrl()
									.doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails);
						} else {
							getPayOrderIssueDialogCtrl()
									.doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails);
						}
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
		this.paymentDetail.setValue("");
		this.liabilityHoldName.setValue("");
		this.beneficiaryName.setValue("");
		this.description.setValue("");
		this.amtToBeReleased.setValue("");
		this.paymentType.setValue("");
		this.llReferenceNo.setValue("");
		this.llDate.setText("");
		this.custContribution.setValue("");
		this.sellerContribution.setValue("");
		this.remarks.setValue("");
		this.bankCode.setValue("");
		this.bankBranchID.setValue("");
		this.payableLoc.setValue("");
		this.printingLoc.setValue("");
		this.bank.setValue("");
		this.branch.setValue("");
		this.city.setValue("");
		this.transactionRef.setValue("");
		this.valueDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		BeanUtils.copyProperties(this.finAdvancePayments, aFinAdvancePayments);
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
				auditHeader = ErrorControl.showErrorDetails(this.window_FinAdvancePaymentsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (this.moduleType.equals("LOAN")) {
						getFinAdvancePaymentsListCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails);
					} else {
						getPayOrderIssueDialogCtrl().doFillFinAdvancePaymentsDetails(this.finAdvancePaymentsDetails);
					}
					closeDialog();
				}
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private ArrayList<ErrorDetails> validate(List<FinAdvancePayments> list, FinAdvancePayments advancePayments) {

		ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();
		if (list != null && !list.isEmpty()) {
			String validateAcNumber = StringUtils.trimToEmpty(advancePayments.getBeneficiaryAccNo());
			if (StringUtils.isEmpty(validateAcNumber)) {
				return errors;
			}
		}
		return errors;
	}

	private AuditHeader newFinAdvancePaymentsProcess(FinAdvancePayments afinAdvancePayments, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(afinAdvancePayments, tranType);
		finAdvancePaymentsDetails = new ArrayList<FinAdvancePayments>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(afinAdvancePayments.getFinReference());
		valueParm[1] = Integer.toString(afinAdvancePayments.getPaymentSeq());

		errParm[0] = PennantJavaUtil.getLabel("FinAdvancePayments_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("FinAdvancePayments_PaymentSeq") + ":" + valueParm[1];

		List<FinAdvancePayments> listAdvance = null;
		if (this.moduleType.equals("LOAN")) {
			listAdvance = getFinAdvancePaymentsListCtrl().getFinAdvancePaymentsList();
		} else {
			listAdvance = getPayOrderIssueDialogCtrl().getFinAdvancePaymentsList();
		}
		ArrayList<ErrorDetails> erroe = validate(listAdvance, afinAdvancePayments);
		if (!erroe.isEmpty()) {
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(erroe, getUserWorkspace().getUserLanguage()));
			return auditHeader;
		}

		if (listAdvance != null && listAdvance.size() > 0) {
			for (int i = 0; i < listAdvance.size(); i++) {
				FinAdvancePayments loanDetail = listAdvance.get(i);

				if (afinAdvancePayments.getPaymentSeq() == loanDetail.getPaymentSeq()) { // Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(afinAdvancePayments.getRecordType())) {
							afinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finAdvancePaymentsDetails.add(afinAdvancePayments);
						} else if (PennantConstants.RCD_ADD.equals(afinAdvancePayments.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(afinAdvancePayments.getRecordType())) {
							afinAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finAdvancePaymentsDetails.add(afinAdvancePayments);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(afinAdvancePayments.getRecordType())) {
							recordAdded = true;
							List<FinAdvancePayments> listAdvanceApproved = null;
							if ("LOAN".equals(this.moduleType)) {
								listAdvanceApproved = getFinAdvancePaymentsListCtrl().getFinancedetail()
										.getAdvancePaymentsList();
							} else {
								listAdvanceApproved = getPayOrderIssueDialogCtrl().getPayOrderIssueHeader()
										.getFinAdvancePaymentsList();
							}
							for (int j = 0; j < listAdvanceApproved.size(); j++) {
								FinAdvancePayments detail = listAdvanceApproved.get(j);
								if (detail.getFinReference() == afinAdvancePayments.getFinReference()
										&& detail.getPaymentSeq() == afinAdvancePayments.getPaymentSeq()) {
									finAdvancePaymentsDetails.add(detail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							finAdvancePaymentsDetails.add(loanDetail);
						}
					}
				} else {
					finAdvancePaymentsDetails.add(loanDetail);
				}
			}
		}

		if (!recordAdded) {
			finAdvancePaymentsDetails.add(afinAdvancePayments);
		}
		return auditHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinAdvancePayments aFinAdvancePayments, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinAdvancePayments.getBefImage(), aFinAdvancePayments);
		return new AuditHeader(aFinAdvancePayments.getFinReference(), null, null, null, auditDetail,
				aFinAdvancePayments.getUserDetails(), getOverideMap());
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bankBranchID.getObject();

		if (dataObject instanceof String) {
			this.bankBranchID.setValue(dataObject.toString());
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
			this.finAdvancePayments.setBranchBankCode("");
		} else {
			BankBranch details = (BankBranch) dataObject;

			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.finAdvancePayments.setCity(details.getCity());
				this.finAdvancePayments.setBranchBankCode(details.getBankCode());
				this.finAdvancePayments.setBranchBankName(details.getBankName());
				this.finAdvancePayments.setBranchDesc(details.getBranchDesc());
				this.finAdvancePayments.setiFSC(details.getIFSC());
				this.city.setValue(details.getCity());
				this.branch.setValue(details.getBranchDesc());
				this.bankBranchID.setValue(details.getIFSC());
				if (StringUtils.isNotBlank(details.getBankCode())) {
					accNoLength = bankDetailService.getAccNoLengthByCode(details.getBankCode());
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = bankCode.getObject();

		if (dataObject instanceof String) {
			this.bankCode.setValue(dataObject.toString());
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setAttribute("bankCode", details.getBankCode());
				this.finAdvancePayments.setBankName(details.getBankName());
			}
		}
	}

	public void onChange$paymentType(Event event) {
		String dType = this.paymentType.getSelectedItem().getValue().toString();
		this.partnerBankID.setButtonDisabled(false);
		this.partnerBankID.setReadonly(false);
		Filter[] filters = new Filter[4];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", "D", Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", dType, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.partnerBankID.setFilters(filters);
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");

		checkPaymentType(dType);
	}

	public void checkPaymentType(String str) {
		if (StringUtils.isEmpty(str) || StringUtils.equals(str, PennantConstants.List_Select)) {
			gb_ChequeDetails.setVisible(false);
			gb_NeftDetails.setVisible(false);
			this.partnerBankID.setReadonly(true);
			this.partnerBankID.setValue("");
			this.partnerBankID.setDescription("");
			return;
		} else if (str.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
				|| str.equals(DisbursementConstants.PAYMENT_TYPE_DD)) {
			doaddFilter(str);
			caption_FinAdvancePaymentsDialog_ChequeDetails.setLabel(this.paymentType.getSelectedItem().getLabel());
			gb_ChequeDetails.setVisible(true);
			gb_NeftDetails.setVisible(false);
			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
			this.beneficiaryAccNo.setValue("");
			this.beneficiaryName.setValue("");
			this.phoneNumber.setValue("");
			if (str.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)) {
				readOnlyComponent(isReadOnly("FinAdvancePaymentsDialog_printingLoc"), this.printingLoc);
			} else {
				this.printingLoc.setValue("");
				readOnlyComponent(true, this.printingLoc);
			}

			this.btnGetCustBeneficiary.setVisible(false);
		} else {
			doaddFilter(str);
			caption_FinAdvancePaymentsDialog_NeftDetails.setLabel(this.paymentType.getSelectedItem().getLabel());
			gb_NeftDetails.setVisible(true);
			gb_ChequeDetails.setVisible(false);
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
			this.liabilityHoldName.setValue("");
			this.payableLoc.setValue("");
			this.printingLoc.setValue("");
			this.valueDate.setText("");
			this.llReferenceNo.setValue("");
			if (str.equals(DisbursementConstants.PAYMENT_TYPE_IMPS)) {
				this.contactNumber.setSclass("mandatory");
			} else {
				this.contactNumber.setSclass("");
			}
			this.btnGetCustBeneficiary.setVisible(!isReadOnly("FinAdvancePaymentsDialog_bankBranchID"));
		}
		//FIXME Fields moved to branches
		//		Filter filter[] = new Filter[1];
		//		switch (str) {
		//		case DisbursementConstants.PAYMENT_TYPE_CHEQUE:
		//			filter[0] = new Filter("Cheque", "1", Filter.OP_EQUAL);
		//			this.bankCode.setFilters(filter);
		//			break;
		//		case DisbursementConstants.PAYMENT_TYPE_DD:
		//			filter[0] = new Filter("DD", "1", Filter.OP_EQUAL);
		//			this.bankCode.setFilters(filter);
		//			break;
		//		default:
		//			break;
		//		}

	}

	public void doaddFilter(String payMode) {
		Filter[] filters = new Filter[4];
		filters[0] = new Filter("FinType", financeMain.getFinType(), Filter.OP_EQUAL);
		filters[1] = new Filter("Purpose", "D", Filter.OP_EQUAL);
		filters[2] = new Filter("PaymentMode", payMode, Filter.OP_EQUAL);
		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.partnerBankID.setFilters(filters);

	}

	public void onClick$btnGetCustBeneficiary(Event event) {
		logger.debug("Entering");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("CustId", custID, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FinAdvancePaymentsDialog, "BeneficiaryEnquiry",
				filter, "");
		if (dataObject instanceof Beneficiary) {
			Beneficiary details = (Beneficiary) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bankBranchID.setValue(details.getiFSC());
				this.finAdvancePayments.setCity(details.getCity());
				this.finAdvancePayments.setBranchBankName(details.getBankName());
				this.finAdvancePayments.setBranchBankCode(details.getBankCode());
				this.finAdvancePayments.setBranchDesc(details.getBranchDesc());
				this.finAdvancePayments.setiFSC(details.getiFSC());
				this.bank.setValue(details.getBankName());
				this.branch.setValue(details.getBranchDesc());
				this.beneficiaryAccNo.setValue(details.getAccNumber());
				this.beneficiaryName.setValue(details.getAccHolderName());
				this.city.setValue(details.getCity());
				this.phoneNumber.setValue(details.getPhoneNumber());
				if (StringUtils.isNotBlank(details.getBankCode())) {
					accNoLength = bankDetailService.getAccNoLengthByCode(details.getBankCode());
				}
			}
		}
	}

	public void onSelect$disbDate(ForwardEvent event) {
		setDisbursmentAmount();
	}

	private void setDisbursmentAmount() {
		Comboitem item = this.disbDate.getSelectedItem();
		if (item != null && item.getValue() != null) {
			FinanceDisbursement disbursement = (FinanceDisbursement) item.getAttribute("data");
			if (disbursement != null) {
				BigDecimal disAmt = DisbursementInstCtrl.getTotalByDisbursment(disbursement, financeMain);
				disAmt = disAmt.subtract(getAdjustedAmount(disbursement));

				this.disbDateAmount.setValue(PennantAppUtil.formateAmount(disAmt, ccyFormatter));
			}
		} else {
			this.disbDateAmount.setValue(BigDecimal.ZERO);
		}
	}

	public BigDecimal getAdjustedAmount(FinanceDisbursement disbursement) {
		BigDecimal adjustedAmount = BigDecimal.ZERO;

		List<FinAdvancePayments> list = null;
		if (this.moduleType.equals("LOAN")) {
			list = getFinAdvancePaymentsListCtrl().getFinAdvancePaymentsList();
		} else {
			list = getPayOrderIssueDialogCtrl().getFinAdvancePaymentsList();
		}
		if (list == null || list.isEmpty()) {
			return adjustedAmount;
		}

		for (FinAdvancePayments finAdvPayments : list) {
			if (finAdvPayments.getDisbSeq() == disbursement.getDisbSeq()) {
				if (this.paymentSequence.intValue() == finAdvPayments.getPaymentSeq()) {
					continue;
				}
				if (DisbursementInstCtrl.isDeleteRecord(finAdvPayments)) {
					continue;
				}
				adjustedAmount = adjustedAmount.add(finAdvPayments.getAmtToBeReleased());
			}

		}
		return adjustedAmount;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinAdvancePayments(FinAdvancePayments finAdvancePayments) {
		this.finAdvancePayments = finAdvancePayments;
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

	public FinAdvancePaymentsListCtrl getFinAdvancePaymentsListCtrl() {
		return finAdvancePaymentsListCtrl;
	}

	public void setFinAdvancePaymentsListCtrl(FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl) {
		this.finAdvancePaymentsListCtrl = finAdvancePaymentsListCtrl;
	}

	public void setFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePaymentsDetails) {
		this.finAdvancePaymentsDetails = finAdvancePaymentsDetails;
	}

	public List<FinAdvancePayments> getFinAdvancePaymentsDetails() {
		return finAdvancePaymentsDetails;
	}

	public PayOrderIssueListCtrl getPayOrderIssueListCtrl() {
		return payOrderIssueListCtrl;
	}

	public void setPayOrderIssueListCtrl(PayOrderIssueListCtrl payOrderIssueListCtrl) {
		this.payOrderIssueListCtrl = payOrderIssueListCtrl;
	}

	public PayOrderIssueDialogCtrl getPayOrderIssueDialogCtrl() {
		return payOrderIssueDialogCtrl;
	}

	public void setPayOrderIssueDialogCtrl(PayOrderIssueDialogCtrl payOrderIssueDialogCtrl) {
		this.payOrderIssueDialogCtrl = payOrderIssueDialogCtrl;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
