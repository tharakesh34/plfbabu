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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkmax.zul.Tablechildren;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.CashDenomination;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.DepositDetailsService;
import com.pennant.backend.util.CashManagementConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CashManagement/BranchCashToBankRequest/DepositDetailsDialog.zul
 * file. <br>
 */
public class DepositMovementsDialogCtrl extends GFCBaseCtrl<DepositDetails> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DepositMovementsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DepositMovementsDialog;
	protected CurrencyBox transactionAmount;
	protected ExtendedCombobox partnerBankId;
	protected Datebox transactionDate;
	protected Uppercasebox depositSlipNumber;
	protected Listbox listBox_DenominationsList;
	protected Listbox listBoxPosting;
	protected Tablechildren tablechildren_CashDenominations;
	protected Groupbox groupBox_Cheque;
	protected Listbox listBoxChequeOrDD;
	protected Tabpanel tabpanel_BasicDetails;
	protected Tabpanel tabpanel_Postings;
	protected Tab receiptDetailsTab;
	protected Tab postingDetailsTab;

	private DepositMovements depositMovements;

	private transient DepositMovementsListCtrl depositMovementsListCtrl;
	private transient DepositDetailsService depositDetailsService;
	private List<CashDenomination> cashDenominations = new ArrayList<CashDenomination>();

	/**
	 * default constructor.<br>
	 */
	public DepositMovementsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DepositDetailsDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.depositMovements.getMovementId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DepositMovementsDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DepositMovementsDialog);

		try {
			// Get the required arguments.
			this.depositMovements = (DepositMovements) arguments.get("depositMovements");
			this.depositMovementsListCtrl = (DepositMovementsListCtrl) arguments.get("depositMovementsListCtrl");

			if (this.depositMovements == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			DepositMovements depositMovements = new DepositMovements();
			BeanUtils.copyProperties(this.depositMovements, depositMovements);
			this.depositMovements.setBefImage(depositMovements);

			// Render the page and display the data.
			doLoadWorkFlow(this.depositMovements.isWorkflow(), this.depositMovements.getWorkflowId(),
					this.depositMovements.getNextTaskId());

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
			doShowDialog(this.depositMovements);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onSelect$receiptDetailsTab(Event event) {
		logger.debug(Literal.ENTERING);

		doShowDialog(this.depositMovements);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.transactionDate.setFormat(PennantConstants.dateFormat);

		this.transactionAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.transactionAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.transactionAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.transactionAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.depositSlipNumber.setMaxlength(20);

		this.partnerBankId.setModuleName("PartnerBank");
		this.partnerBankId.setValueColumn("PartnerBankId");
		this.partnerBankId.setDescColumn("PartnerBankName");
		this.partnerBankId.setValidateColumns(new String[] { "PartnerBankId", "PartnerBankCode", "PartnerBankName" });
		this.partnerBankId.setMandatoryStyle(false);
		this.partnerBankId.setValueType(DataType.LONG);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnSave.setVisible(false);
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);

		// doSave();

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

		// doDelete();

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

		doShowNotes(this.depositMovements);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.depositMovements.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	private void doFillDenominationsList(List<CashDenomination> denominations) {
		logger.debug(Literal.ENTERING);

		BigDecimal totalAmount = BigDecimal.ZERO;
		setCashDenominations(denominations);
		boolean isReadOnly = true;
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

			// Total Amount
			BigDecimal amount = CurrencyUtil.parse(cashDenomination.getAmount(), PennantConstants.defaultCCYDecPos);
			Decimalbox amountBox = new Decimalbox();
			amountBox.setMaxlength(18);
			amountBox.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
			amountBox.setId("Amount" + i);
			amountBox.setValue(amount);
			amountBox.setWidth("150px");
			amountBox.setDisabled(isReadOnly);
			lc = new Listcell();
			lc.setStyle("text-align:right;");

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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param branchCashToBankRequest
	 * 
	 */
	public void doWriteBeanToComponents(DepositMovements depositMovements) {
		logger.debug(Literal.ENTERING);

		this.transactionAmount.setValue(PennantApplicationUtil.formateAmount(depositMovements.getReservedAmount(),
				PennantConstants.defaultCCYDecPos));
		this.partnerBankId.setValue(String.valueOf(depositMovements.getPartnerBankId()),
				depositMovements.getPartnerBankName());
		this.transactionDate.setValue(depositMovements.getTransactionDate());
		this.depositSlipNumber.setValue(depositMovements.getDepositSlipNumber());

		// Posting Details Rendering
		if (depositMovements.getLinkedTranId() > 0) {
			List<ReturnDataSet> postings = this.depositDetailsService
					.getPostingsByLinkTransId(depositMovements.getLinkedTranId());
			doFillPostings(postings);
			this.listBoxPosting.setHeight(getListBoxHeight(6));
		}

		if (CollectionUtils.isNotEmpty(depositMovements.getDenominationList())) {
			doFillDenominationsList(depositMovements.getDenominationList());
			this.groupBox_Cheque.setVisible(false);
			this.tablechildren_CashDenominations.setVisible(true);
		} else {
			this.tablechildren_CashDenominations.setVisible(false);
			this.groupBox_Cheque.setVisible(true);

			doFillChequeDetails(depositMovements.getDepositChequesList());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Showing Posting Details which are going to be reversed
	 * 
	 * @param linkedTranId
	 */
	private void doFillChequeDetails(List<DepositCheques> depositChequesList) {
		logger.debug("Entering");

		this.listBoxChequeOrDD.getItems().clear();

		if (CollectionUtils.isNotEmpty(depositChequesList)) {
			Listitem item;
			for (DepositCheques depositCheque : depositChequesList) {
				item = new Listitem();
				Listcell lc = null;

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
				lc = new Listcell(DateUtil.formatToLongDate(depositCheque.getReceivedDate()));
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

				// Postings
				Button postings = new Button("Postings");
				lc = new Listcell();
				lc.appendChild(postings);
				if (CashManagementConstants.DEPOSIT_CHEQUE_STATUS_REVERSE.equals(depositCheque.getStatus())) {
					readOnlyComponent(true, postings);
				} else {
					readOnlyComponent(false, postings);
				}
				postings.addForward("onClick", window_DepositMovementsDialog, "onClickPostings", depositCheque);
				lc.setParent(item);

				this.listBoxChequeOrDD.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	public void onClickPostings(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		DepositCheques depositCheque = (DepositCheques) event.getData();

		if (depositCheque.getLinkedTranId() <= 0) {
			MessageUtil.showError("Postings not available.");
			return;
		}

		List<ReturnDataSet> postings = this.depositDetailsService
				.getPostingsByLinkTransId(depositCheque.getLinkedTranId());
		doFillPostings(postings);
		this.postingDetailsTab.setSelected(true);
		this.listBoxPosting.setHeight(getListBoxHeight(6));

		logger.debug("Leaving");
	}

	/**
	 * Method for Showing Posting Details which are going to be reversed
	 * 
	 * @param linkedTranId
	 */
	private void doFillPostings(List<ReturnDataSet> postingList) {
		logger.debug("Entering");

		if (CollectionUtils.isNotEmpty(postingList)) {
			this.listBoxPosting.getItems().clear();

			Listitem item;
			for (ReturnDataSet returnDataSet : postingList) {
				item = new Listitem();
				Listcell lc = new Listcell();
				if (AccountConstants.TRANTYPE_CREDIT.equals(returnDataSet.getDrOrCr())) {
					lc = new Listcell(Labels.getLabel("common.Credit"));
				} else if (AccountConstants.TRANTYPE_DEBIT.equals(returnDataSet.getDrOrCr())) {
					lc = new Listcell(Labels.getLabel("common.Debit"));
				}
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranDesc());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getRevTranCode());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranCode());
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getAcCcy());
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(returnDataSet.getPostAmount(),
						CurrencyUtil.getFormat(returnDataSet.getAcCcy())));
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);

				this.listBoxPosting.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param depositMovements The entity that need to be render.
	 */
	public void doShowDialog(DepositMovements depositMovements) {
		logger.debug(Literal.LEAVING);

		if (depositMovements.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.transactionAmount.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(depositMovements.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				this.transactionAmount.focus();
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

		doWriteBeanToComponents(depositMovements);
		this.btnDelete.setVisible(false); // delete button visibility as false for this requirement
		this.btnSave.setVisible(false); // Save button visibility as false for this requirement
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		this.transactionDate.setErrorMessage("");
		this.transactionAmount.setErrorMessage("");
		this.depositSlipNumber.setErrorMessage("");
		this.partnerBankId.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.depositMovements.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(true, this.transactionDate);
		readOnlyComponent(true, this.transactionAmount);
		readOnlyComponent(true, this.depositSlipNumber);
		readOnlyComponent(true, this.partnerBankId);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.depositMovements.isNewRecord()) {
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

		readOnlyComponent(true, this.transactionDate);
		readOnlyComponent(true, this.transactionAmount);
		readOnlyComponent(true, this.depositSlipNumber);
		readOnlyComponent(true, this.partnerBankId);
		partnerBankId.setMandatoryStyle(false);

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

		this.transactionDate.setText("");
		this.transactionAmount.setValue("");
		this.depositSlipNumber.setValue("");
		this.partnerBankId.setValue("", "");

		logger.debug("Leaving");
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

	public DepositMovementsListCtrl getDepositMovementsListCtrl() {
		return depositMovementsListCtrl;
	}

	public void setDepositMovementsListCtrl(DepositMovementsListCtrl depositMovementsListCtrl) {
		this.depositMovementsListCtrl = depositMovementsListCtrl;
	}

	public DepositMovements getDepositMovements() {
		return depositMovements;
	}

	public void setDepositMovements(DepositMovements depositMovements) {
		this.depositMovements = depositMovements;
	}
}
