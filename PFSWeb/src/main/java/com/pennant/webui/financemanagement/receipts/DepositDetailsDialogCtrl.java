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
 * * FileName : DepositDetailsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-07-2018 * *
 * Modified Date : 10-07-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-07-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkmax.zul.Tablechildren;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CashDenomination;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.service.finance.DepositDetailsService;
import com.pennant.backend.util.CashManagementConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CashManagement/BranchCashToBankRequest/DepositDetailsDialog.zul
 * file. <br>
 */
public class DepositDetailsDialogCtrl extends GFCBaseCtrl<DepositDetails> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DepositDetailsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DepositDetailsDialog;
	protected Combobox depositType;
	protected ExtendedCombobox branchCode;
	protected CurrencyBox availableAmount;
	protected CurrencyBox reservedAmount;
	protected ExtendedCombobox partnerBankId;
	protected Datebox transactionDate;
	protected Uppercasebox depositSlipNumber;
	protected Space Space_depositSlipNumber;
	protected Listbox listBox_DenominationsList;
	protected Tablechildren tablechildren_Cash;
	protected Listbox listBoxChequeOrDD;
	protected Groupbox groupBox_Cheque;

	private DepositDetails depositDetails;

	private transient DepositDetailsListCtrl depositDetailsListCtrl;
	private transient DepositDetailsService depositDetailsService;

	private List<ValueLabel> listRequestType = PennantStaticListUtil.getDepositTypesListList();
	private List<CashDenomination> cashDenominations = new ArrayList<CashDenomination>();

	/**
	 * default constructor.<br>
	 */
	public DepositDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DepositDetailsDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.depositDetails.getDepositId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DepositDetailsDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DepositDetailsDialog);

		try {
			// Get the required arguments.
			this.depositDetails = (DepositDetails) arguments.get("depositDetails");
			this.depositDetailsListCtrl = (DepositDetailsListCtrl) arguments.get("depositDetailsListCtrl");

			if (this.depositDetails == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			DepositDetails depositDetails = new DepositDetails();
			BeanUtils.copyProperties(this.depositDetails, depositDetails);
			this.depositDetails.setBefImage(depositDetails);

			// Render the page and display the data.
			doLoadWorkFlow(this.depositDetails.isWorkflow(), this.depositDetails.getWorkflowId(),
					this.depositDetails.getNextTaskId());

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
			doShowDialog(this.depositDetails);
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

		// Deposit Date
		this.transactionDate.setFormat(PennantConstants.dateFormat);

		// Available Amount
		this.availableAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.availableAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.availableAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.availableAmount.setScale(PennantConstants.defaultCCYDecPos);

		// Reserved Amount
		if (CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH.equals(this.depositDetails.getDepositType())) {
			this.reservedAmount.setProperties(true, PennantConstants.defaultCCYDecPos);
		} else {
			this.reservedAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		}
		this.reservedAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.reservedAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.reservedAmount.setScale(PennantConstants.defaultCCYDecPos);

		// Deposit Slip Number
		this.depositSlipNumber.setMaxlength(20);

		// Branch Code
		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });
		this.branchCode.setMandatoryStyle(true);

		// Partner Bank
		this.partnerBankId.setModuleName("PartnerBank");
		this.partnerBankId.setValueColumn("PartnerBankId");
		this.partnerBankId.setDescColumn("PartnerBankName");
		this.partnerBankId.setValidateColumns(new String[] { "PartnerBankId", "PartnerBankCode", "PartnerBankName" });
		this.partnerBankId.setMandatoryStyle(true);
		this.partnerBankId.setValueType(DataType.LONG);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DepositDetailsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DepositDetailsDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DepositDetailsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DepositDetailsDialog_btnDelete"));

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

		doShowNotes(this.depositDetails);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		this.depositDetailsListCtrl.search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.depositDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doFillDenominationsList(List<CashDenomination> denominations) {
		logger.debug(Literal.ENTERING);

		BigDecimal totalAmount = BigDecimal.ZERO;
		setCashDenominations(denominations);
		boolean isReadOnly = false;

		if (enqiryModule) {
			isReadOnly = true;
		} else {
			isReadOnly = isReadOnly("DepositDetailsDialog_Count");
		}

		this.listBox_DenominationsList.getItems().clear();

		for (int i = 0; i < denominations.size(); i++) {
			CashDenomination cashDenomination = denominations.get(i);
			boolean coins = false;

			Listitem item = new Listitem();
			Listcell lc;

			// denomination
			lc = new Listcell(cashDenomination.getDenomination());

			if (i == denominations.size() - 1) {
				coins = true;
			}

			if (coins) {
				lc.setStyle("text-align:left;");
			} else {
				lc.setStyle("text-align:right;");
			}
			lc.setParent(item);

			// Count
			Intbox countBox = new Intbox();
			countBox.setMaxlength(18);
			countBox.setDisabled(isReadOnly);

			if (coins) {
				countBox.setVisible(false);
			} else {
				countBox.setVisible(true);
			}

			countBox.setId("Count" + i);
			countBox.setValue(cashDenomination.getCount());
			countBox.setWidth("100px");
			countBox.setMaxlength(15);
			countBox.setFormat("#,##0");
			countBox.setStyle("text-align:right");
			lc = new Listcell();
			lc.setStyle("text-align:right");
			lc.appendChild(countBox);
			lc.setParent(item);
			countBox.addForward("onChange", window_DepositDetailsDialog, "onChangeCount", cashDenomination);

			// Total Amount
			BigDecimal amount = CurrencyUtil.parse(cashDenomination.getAmount(), PennantConstants.defaultCCYDecPos);
			Decimalbox amountBox = new Decimalbox();
			amountBox.setMaxlength(18);
			amountBox.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
			amountBox.setId("Amount" + i);
			amountBox.setValue(amount);
			amountBox.setWidth("150px");
			lc = new Listcell();
			lc.setStyle("text-align:right;");

			if (coins) {
				amountBox.setDisabled(isReadOnly);
				amountBox.addForward("onChange", window_DepositDetailsDialog, "onChangeAmount", cashDenomination);
			} else {
				amountBox.setDisabled(true);
			}
			lc.appendChild(amountBox);
			lc.setParent(item);
			totalAmount = totalAmount.add(amount);
			this.listBox_DenominationsList.appendChild(item);
		}

		Listitem item = new Listitem();
		Listcell lc;

		item = new Listitem();
		// denomination
		lc = new Listcell("Total Amount");
		lc.setParent(item);

		Intbox intbox = new Intbox();
		intbox.setMaxlength(18);
		intbox.setDisabled(false);
		intbox.setVisible(false);
		lc = new Listcell();
		lc.setStyle("text-align:right");
		lc.appendChild(intbox);
		lc.setParent(item);

		Decimalbox totalAmountBox = new Decimalbox();
		totalAmountBox.setMaxlength(18);
		totalAmountBox.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		totalAmountBox.setDisabled(true);
		totalAmountBox.setId("TotalValue");
		totalAmountBox.setValue(totalAmount);
		totalAmountBox.setWidth("150px");
		lc = new Listcell();
		lc.setStyle("text-align:right;");
		lc.appendChild(totalAmountBox);
		lc.setParent(item);

		this.listBox_DenominationsList.appendChild(item);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Change the Denomination count
	 * 
	 * @param event
	 */
	public void onChangeCount(ForwardEvent event) {
		logger.debug("Entering");

		Intbox count = (Intbox) event.getOrigin().getTarget();

		CashDenomination cashDenomination = (CashDenomination) event.getData();

		if (count.getValue() == null) {
			cashDenomination.setCount(0);
			cashDenomination.setAmount(BigDecimal.ZERO);
		} else {
			cashDenomination.setCount(count.getValue());
			BigDecimal denomination = new BigDecimal(cashDenomination.getDenomination());
			cashDenomination.setAmount(CurrencyUtil.unFormat(denomination.multiply(new BigDecimal(count.getValue())),
					PennantConstants.defaultCCYDecPos));
		}

		doFillDenominationsList(getCashDenominations());

		logger.debug("Leaving");
	}

	/**
	 * Change the coins amount
	 * 
	 * @param event
	 */
	public void onChangeAmount(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox amount = (Decimalbox) event.getOrigin().getTarget();

		CashDenomination cashDenomination = (CashDenomination) event.getData();

		if (amount.getValue() == null) {
			cashDenomination.setAmount(CurrencyUtil.unFormat(BigDecimal.ZERO, PennantConstants.defaultCCYDecPos));
		} else {
			cashDenomination.setAmount(CurrencyUtil.unFormat(amount.getValue(), PennantConstants.defaultCCYDecPos));
		}

		doFillDenominationsList(getCashDenominations());

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param branchCashToBankRequest
	 * 
	 */
	public void doWriteBeanToComponents(DepositDetails aDepositDetails) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.depositType, aDepositDetails.getDepositType(), listRequestType, "");
		this.branchCode.setValue(aDepositDetails.getBranchCode(), aDepositDetails.getBranchDesc());

		DepositMovements depositMovements = aDepositDetails.getDepositMovements();
		BigDecimal availableAmount = aDepositDetails.getActualAmount();
		this.availableAmount
				.setValue(PennantApplicationUtil.formateAmount(availableAmount, PennantConstants.defaultCCYDecPos));

		if (depositMovements == null) {
			this.transactionDate.setValue(SysParamUtil.getAppDate());
			depositMovements = new DepositMovements();
			depositMovements.setRecordType(PennantConstants.RCD_ADD);
			depositMovements.setTransactionType(CashManagementConstants.DEPOSIT_MOVEMENT_DEBIT);
			depositMovements.setVersion(1);
			depositMovements.setNewRecord(true);
			aDepositDetails.setDepositMovements(depositMovements);
			if (CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH.equals(aDepositDetails.getDepositType())) {
				this.reservedAmount.setValue(
						PennantApplicationUtil.formateAmount(availableAmount, PennantConstants.defaultCCYDecPos));
			} else {
				this.reservedAmount.setValue(
						PennantApplicationUtil.formateAmount(BigDecimal.ZERO, PennantConstants.defaultCCYDecPos));
			}
		} else {
			this.reservedAmount.setValue(PennantApplicationUtil.formateAmount(aDepositDetails.getReservedAmount(),
					PennantConstants.defaultCCYDecPos));
			this.partnerBankId.setValue(String.valueOf(depositMovements.getPartnerBankId()),
					depositMovements.getPartnerBankName());
			this.transactionDate.setValue(depositMovements.getTransactionDate());
			this.depositSlipNumber.setValue(depositMovements.getDepositSlipNumber());
		}

		if (CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH.equals(aDepositDetails.getDepositType())) {
			this.groupBox_Cheque.setVisible(false);
			if (CollectionUtils.isNotEmpty(depositMovements.getDenominationList())) {
				doFillDenominationsList(depositMovements.getDenominationList());
			} else {
				doFillDenominationsList(prepareCashDenominations(CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH));
			}
		} else {
			this.tablechildren_Cash.setVisible(false);
			List<DepositCheques> depositChequesList = this.depositDetailsService
					.getDepositChequesList(aDepositDetails.getBranchCode());
			if (CollectionUtils.isNotEmpty(depositChequesList)) {
				for (DepositCheques depositCheque : depositChequesList) {
					depositCheque.setStatus(CashManagementConstants.DEPOSIT_CHEQUE_STATUS_APPROVE);
					depositCheque.setRecordType(PennantConstants.RCD_ADD);
					depositCheque.setVersion(1);
					depositCheque.setNewRecord(true);
				}
			}

			if (CollectionUtils.isNotEmpty(depositMovements.getDepositChequesList())) {
				for (DepositCheques depositCheque : depositMovements.getDepositChequesList()) {
					depositCheque.setVisible(true);
				}
				depositMovements.getDepositChequesList().addAll(depositChequesList);
			} else {
				depositMovements.setDepositChequesList(depositChequesList);
			}

			doFillChequeDetails(depositMovements.getDepositChequesList());
		}

		this.recordStatus.setValue(depositDetails.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Showing Posting Details which are going to be reversed
	 * 
	 * @param linkedTranId
	 */
	private void doFillChequeDetails(List<DepositCheques> depositChequesList) {
		logger.debug("Entering");

		if (CollectionUtils.isNotEmpty(depositChequesList)) {
			Listitem item;
			boolean readOnly = isReadOnly("DepositDetailsDialog_TransactionAmount");

			for (DepositCheques depositCheque : depositChequesList) {
				item = new Listitem();
				Listcell lc = null;

				// Check box
				Checkbox active = new Checkbox();
				active.setChecked(depositCheque.isVisible());
				active.setDisabled(readOnly);
				active.addForward("onCheck", window_DepositDetailsDialog, "onCheckListItem", depositCheque);
				lc = new Listcell();
				lc.appendChild(active);
				lc.setParent(item);

				// FinReference
				lc = new Listcell(depositCheque.getFinReference());
				lc.setParent(item);

				// Customer Name
				lc = new Listcell(depositCheque.getCustShrtName());
				lc.setParent(item);

				// Cheque/DD No
				lc = new Listcell(depositCheque.getFavourNumber());
				lc.setParent(item);

				// Cheque/DD Date
				lc = new Listcell(DateUtility.formatToLongDate(depositCheque.getReceivedDate()));
				lc.setParent(item);

				// Bank Name
				// lc = new Listcell(depositCheque.getPartnerBankCode());
				// lc.setParent(item);

				// Amount
				lc = new Listcell(CurrencyUtil.format(depositCheque.getAmount(), PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Receipt Purpose
				lc = new Listcell(PennantApplicationUtil.getLabelDesc(depositCheque.getReceiptpurpose(),
						PennantStaticListUtil.getReceiptPurpose()));
				lc.setParent(item);
				lc = new Listcell(depositCheque.getRemarks());
				lc.setParent(item);

				this.listBoxChequeOrDD.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * onSelect event for list item
	 * 
	 * @param event
	 */
	public void onCheckListItem(ForwardEvent event) {
		logger.debug("Entering");

		DepositCheques depositCheques = (DepositCheques) event.getData();
		if (depositCheques == null) {
			return;
		}
		Checkbox active = (Checkbox) event.getOrigin().getTarget();
		BigDecimal reservedAmount = CurrencyUtil.unFormat(this.reservedAmount.getValidateValue(),
				PennantConstants.defaultCCYDecPos);
		if (active.isChecked()) {
			reservedAmount = reservedAmount.add(depositCheques.getAmount());
			depositCheques.setVisible(true);
		} else {
			reservedAmount = reservedAmount.subtract(depositCheques.getAmount());
			depositCheques.setVisible(false);
		}

		this.reservedAmount
				.setValue(PennantApplicationUtil.formateAmount(reservedAmount, PennantConstants.defaultCCYDecPos));

		logger.debug("Leaving");
	}

	/**
	 * preparing cash denominations
	 * 
	 * @param moduleCode
	 * @return
	 */
	private List<CashDenomination> prepareCashDenominations(String moduleCode) {
		logger.debug(Literal.ENTERING);

		List<CashDenomination> cashDenominations = new ArrayList<CashDenomination>();
		List<String> denominations = PennantStaticListUtil.getDenominations();
		CashDenomination cashDenomination = null;
		int count = 0;

		for (String denomination : denominations) {
			count = count + 1;
			cashDenomination = new CashDenomination(denomination);
			cashDenomination.setRecordType(PennantConstants.RCD_ADD);
			cashDenomination.setVersion(1);
			cashDenomination.setNewRecord(true);
			cashDenomination.setSeqNo(count);
			cashDenominations.add(cashDenomination);
		}

		logger.debug(Literal.LEAVING);

		return cashDenominations;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDepositDetails
	 */
	public void doWriteComponentsToBean(DepositDetails aDepositDetails) {
		logger.debug(Literal.LEAVING);

		DepositMovements depositMovements = aDepositDetails.getDepositMovements();
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Deposit Type
		try {
			isValidComboValue(this.depositType, Labels.getLabel("label_DepositDetailsDialog_DepositType.value"));
			aDepositDetails.setDepositType(getComboboxValue(this.depositType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Branch Code
		try {
			aDepositDetails.setBranchCode(this.branchCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Reserved Amount
		try {
			aDepositDetails.setReservedAmount(
					CurrencyUtil.unFormat(this.reservedAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
			depositMovements.setReservedAmount(
					CurrencyUtil.unFormat(this.reservedAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Transaction Amount
		try {
			double reservedAmount = this.reservedAmount.getValidateValue().doubleValue();
			double availableAmount = this.availableAmount.getValidateValue().doubleValue();

			String recordStatus = this.userAction.getSelectedItem().getLabel();
			if (!"Reject".equalsIgnoreCase(recordStatus) && !"Cancel".equalsIgnoreCase(recordStatus)
					&& !"Resubmit".equalsIgnoreCase(recordStatus)) {
				if (reservedAmount != 0 && availableAmount != 0 && availableAmount < reservedAmount) {
					throw new WrongValueException(this.reservedAmount,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_DepositDetailsDialog_ReservedAmount.value"),
											Labels.getLabel("label_DepositDetailsDialog_AvailableAmount.value") }));
				} else if (reservedAmount == 0) {
					throw new WrongValueException(this.reservedAmount,
							Labels.getLabel("label_DepositDetailsDialog_ReservedAmount.value")
									+ " must be greater than 0.");
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Transaction Date
		try {
			depositMovements.setTransactionDate(this.transactionDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Deposit Slip Number
		try {
			depositMovements.setDepositSlipNumber(this.depositSlipNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Partner Bank Id
		try {
			String partnerBankId = this.partnerBankId.getValue();
			if (StringUtils.isNotBlank(partnerBankId)) {
				PartnerBank partnerBank = (PartnerBank) this.partnerBankId.getObject();
				if (partnerBank != null) {
					depositMovements.setPartnerBankId(partnerBank.getPartnerBankId());
				} else {
					depositMovements.setPartnerBankId(Long.valueOf(partnerBankId));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() == 0) {
			if (this.tablechildren_Cash.isVisible()) { // Cash Denominations
				if (this.userAction.getSelectedItem() != null
						&& !PennantConstants.RECORD_TYPE_DEL.equals(aDepositDetails.getRecordType())) {
					String recordStatus = this.userAction.getSelectedItem().getLabel();
					if (!"Reject".equalsIgnoreCase(recordStatus) && !"Cancel".equalsIgnoreCase(recordStatus)
							&& !"Resubmit".equalsIgnoreCase(recordStatus)) {
						wve = validateCashDenomination(wve);
					}
				}
				if (wve.isEmpty()) {
					depositMovements.setDenominationList(getCashDenominations());
				}
			} else if (this.groupBox_Cheque.isVisible()
					&& CollectionUtils.isNotEmpty(depositMovements.getDepositChequesList())) { // Deposit Cheques
				List<DepositCheques> finalChequesList = new ArrayList<DepositCheques>();

				boolean active = false;
				for (DepositCheques depositCheques : depositMovements.getDepositChequesList()) {
					if (depositCheques.isVisible()) {
						if (depositCheques.isNewRecord()) {
							depositCheques.setRecordType(PennantConstants.RCD_ADD);
						} else {
							depositCheques.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						}
						finalChequesList.add(depositCheques);
						active = true;
					} else if (PennantConstants.RECORD_TYPE_NEW.equals(depositCheques.getRecordType())
							|| PennantConstants.RECORD_TYPE_UPD.equals(depositCheques.getRecordType())) {
						depositCheques.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						finalChequesList.add(depositCheques);
					}
				}

				if (!active) {
					try {
						throw new WrongValueException(this.listBoxChequeOrDD, "Please select at least once record.");
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}

				depositMovements.setDepositChequesList(finalChequesList);
			}
		}

		aDepositDetails.setDepositMovements(depositMovements);
		List<DepositMovements> movementsList = new ArrayList<DepositMovements>();
		movementsList.add(depositMovements);
		aDepositDetails.setDepositMovementsList(movementsList);

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
	 * Validate the Cash Denominations
	 * 
	 * @param wve
	 * @return
	 */
	private ArrayList<WrongValueException> validateCashDenomination(ArrayList<WrongValueException> wve) {

		List<Listitem> listItems = listBox_DenominationsList.getItems();
		Intbox count = null;
		Decimalbox amount = null;

		for (int i = 0; i < listItems.size(); i++) {

			Listitem listItem = listItems.get(i);
			count = (Intbox) listItem.getChildren().get(1).getFirstChild();

			if (listItems.size() - 2 == i) {
				amount = (Decimalbox) listItem.getChildren().get(2).getFirstChild();
				amount.setConstraint("");
				amount.setErrorMessage("");
				BigDecimal amountValue = amount.getValue();
				try {
					if (amountValue.compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(amount, Labels.getLabel("NUMBER_NOT_NEGATIVE",
								new String[] { Labels.getLabel("listheader_DenominationsList_Amount.label") }));
					}
				} catch (WrongValueException wv) {
					wve.add(wv);
				}
			}

			if (count.isVisible()) {
				count.setConstraint("");
				count.setErrorMessage("");
				int countValue = count.getValue();
				try {
					if (countValue < 0) {
						throw new WrongValueException(count, Labels.getLabel("NUMBER_NOT_NEGATIVE",
								new String[] { Labels.getLabel("listheader_DenominationsList_Count.label") }));
					}
				} catch (WrongValueException wv) {
					wve.add(wv);
				}
			}
		}

		if (wve.isEmpty()) {
			try {
				Decimalbox totalAmountBox = (Decimalbox) this.listBox_DenominationsList.getFellow("TotalValue");
				totalAmountBox.setConstraint("");
				totalAmountBox.setErrorMessage("");
				BigDecimal calBoxValue = totalAmountBox.getValue();
				if (calBoxValue == null || calBoxValue.compareTo(BigDecimal.ZERO) <= 0) {
					throw new WrongValueException(totalAmountBox, Labels.getLabel("NUMBER_MINVALUE",
							new String[] { Labels.getLabel("listheader_DenominationsList_Amount.label"), "0" }));
				}

				BigDecimal totalAmountValue = BigDecimal.ZERO;
				if (!StringUtils.isBlank(this.reservedAmount.getCcyTextBox().getValue())) {
					totalAmountValue = this.reservedAmount.getValidateValue();
				}

				if (calBoxValue.compareTo(totalAmountValue) != 0) {
					throw new WrongValueException(totalAmountBox, "Total Amount should be Equal to "
							+ Labels.getLabel("label_DepositDetailsDialog_ReservedAmount.value"));
				}
			} catch (WrongValueException wv) {
				wve.add(wv);
			}
		}

		return wve;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param depositDetails The entity that need to be render.
	 */
	public void doShowDialog(DepositDetails depositDetails) {
		logger.debug(Literal.LEAVING);

		if (depositDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.availableAmount.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(depositDetails.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				this.availableAmount.focus();
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

			if (south != null) {
				south.setVisible(false);
			}
		}

		doWriteBeanToComponents(depositDetails);
		this.btnDelete.setVisible(false); // delete button visibility as false for this requirement
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.branchCode.isReadonly()) {
			this.branchCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_DepositDetailsDialog_BranchCode.value"), null, true, true));
		}

		if (!this.depositSlipNumber.isReadonly()) {
			this.depositSlipNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DepositDetailsDialog_DepositSlipNumber.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.depositType.isReadonly()) {
			this.depositType.setConstraint(new StaticListValidator(listRequestType,
					Labels.getLabel("label_DepositDetailsDialog_DepositType.value")));
		}

		if (!this.availableAmount.isReadonly()) {
			this.availableAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_DepositDetailsDialog_AvailableAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}

		if (!this.reservedAmount.isReadonly()) {
			this.reservedAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_DepositDetailsDialog_ReservedAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}

		if (!this.partnerBankId.isReadonly()) {
			this.partnerBankId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_DepositDetailsDialog_PartnerBankId.value"), null, true, true));
		}

		if (!this.transactionDate.isReadonly()) {
			this.transactionDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_DepositDetailsDialog_TransactionDate.value"), true,
							SysParamUtil.getAppDate(), SysParamUtil.getAppDate(), true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.branchCode.setConstraint("");
		this.depositType.setConstraint("");
		this.transactionDate.setConstraint("");
		this.availableAmount.setConstraint("");
		this.reservedAmount.setConstraint("");
		this.depositSlipNumber.setConstraint("");
		this.partnerBankId.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		this.branchCode.setErrorMessage("");
		this.depositType.setErrorMessage("");
		this.transactionDate.setErrorMessage("");
		this.availableAmount.setErrorMessage("");
		this.reservedAmount.setErrorMessage("");
		this.depositSlipNumber.setErrorMessage("");
		this.partnerBankId.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final DepositDetails aDepositDetails = new DepositDetails();
		BeanUtils.copyProperties(this.depositDetails, aDepositDetails);

		doDelete(String.valueOf(aDepositDetails.getDepositId()), aDepositDetails);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.depositDetails.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("DepositDetailsDialog_BranchCode"), this.branchCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.branchCode);
			branchCode.setMandatoryStyle(false);
		}

		// readOnlyComponent(isReadOnly("DepositDetailsDialog_RequestAmount"), this.reservedAmount);
		// readOnlyComponent(isReadOnly("DepositDetailsDialog_RequestType"), this.depositType);
		// readOnlyComponent(isReadOnly("DepositDetailsDialog_RequestDate"), this.transactionDate);
		readOnlyComponent(isReadOnly("DepositDetailsDialog_DepositSlipNumber"), this.depositSlipNumber);
		readOnlyComponent(isReadOnly("DepositDetailsDialog_PartnerBankId"), this.partnerBankId);
		readOnlyComponent(true, this.transactionDate);
		readOnlyComponent(true, this.availableAmount);
		readOnlyComponent(true, this.depositType);

		String isDepositAmtEdit = SysParamUtil.getValueAsString("DEPOSIT_AMOUNT_EDIT");

		if (StringUtils.equals(isDepositAmtEdit, PennantConstants.YES)
				&& CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH.equals(this.depositDetails.getDepositType())) {
			readOnlyComponent(isReadOnly("DepositDetailsDialog_TransactionAmount"), this.reservedAmount);
		} else {
			readOnlyComponent(true, this.reservedAmount);
		}

		if (partnerBankId.isReadonly()) {
			partnerBankId.setMandatoryStyle(false);
		}
		if (depositSlipNumber.isReadonly()) {
			Space_depositSlipNumber.setSclass("");
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.depositDetails.isNewRecord()) {
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

		readOnlyComponent(true, this.branchCode);
		readOnlyComponent(true, this.depositType);
		readOnlyComponent(true, this.transactionDate);
		readOnlyComponent(true, this.availableAmount);
		readOnlyComponent(true, this.reservedAmount);
		readOnlyComponent(true, this.depositSlipNumber);
		readOnlyComponent(true, this.partnerBankId);

		this.branchCode.setMandatoryStyle(false);
		this.partnerBankId.setMandatoryStyle(false);
		this.Space_depositSlipNumber.setSclass("");

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
		logger.debug("Entering");

		this.branchCode.setValue("", "");
		this.depositType.setSelectedIndex(0);
		this.transactionDate.setText("");
		this.availableAmount.setValue("");
		this.reservedAmount.setValue("");
		this.depositSlipNumber.setValue("");
		this.partnerBankId.setValue("", "");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");

		final DepositDetails aDepositDetails = new DepositDetails();
		BeanUtils.copyProperties(this.depositDetails, aDepositDetails);
		boolean isNew = false;

		// Validation for components
		if (this.userAction.getSelectedItem() != null
				&& !PennantConstants.RECORD_TYPE_DEL.equals(aDepositDetails.getRecordType())) {
			String recordStatus = this.userAction.getSelectedItem().getLabel();
			if (!"Reject".equalsIgnoreCase(recordStatus) && !"Cancel".equalsIgnoreCase(recordStatus)
					&& !"Resubmit".equalsIgnoreCase(recordStatus)) {
				doSetValidation();
			}
		}

		doWriteComponentsToBean(aDepositDetails);

		isNew = aDepositDetails.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDepositDetails.getRecordType())) {
				aDepositDetails.setVersion(aDepositDetails.getVersion() + 1);
				if (isNew) {
					aDepositDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDepositDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDepositDetails.setNewRecord(true);
				}
			}
		} else {
			aDepositDetails.setVersion(aDepositDetails.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aDepositDetails, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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
	protected boolean doProcess(DepositDetails depositDetails, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		depositDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		depositDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		depositDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			depositDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(depositDetails.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, depositDetails);
				}

				if (isNotesMandatory(taskId, depositDetails)) {
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

			depositDetails.setTaskId(taskId);
			depositDetails.setNextTaskId(nextTaskId);
			depositDetails.setRoleCode(getRole());
			depositDetails.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(depositDetails, tranType);
			String operationRefs = getServiceOperations(taskId, depositDetails);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(depositDetails, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(depositDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
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
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DepositDetails aDepositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = depositDetailsService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = depositDetailsService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = depositDetailsService.doApprove(auditHeader);

					if (aDepositDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = depositDetailsService.doReject(auditHeader);
					if (aDepositDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DepositDetailsDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_DepositDetailsDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.depositDetails), true);
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

	/**
	 * @param depositDetails
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(DepositDetails depositDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, depositDetails.getBefImage(), depositDetails);
		return new AuditHeader(getReference(), null, null, null, auditDetail, depositDetails.getUserDetails(),
				getOverideMap());
	}

	public void setDepositDetailsService(DepositDetailsService depositDetailsService) {
		this.depositDetailsService = depositDetailsService;
	}

	public List<CashDenomination> getCashDenominations() {
		return cashDenominations;
	}

	public void setCashDenominations(List<CashDenomination> cashDenominations) {
		this.cashDenominations = cashDenominations;
	}
}
