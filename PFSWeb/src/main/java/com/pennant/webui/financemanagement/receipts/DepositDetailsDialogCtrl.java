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
 * FileName    		:  DepositDetailsDialogCtrl.java                               			* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-07-2018    														*
 *                                                                  						*
 * Modified Date    :  10-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-07-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.CashDenomination;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.service.finance.DepositDetailsService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/CashManagement/BranchCashToBankRequest/DepositDetailsDialog.zul file. <br>
 */
public class DepositDetailsDialogCtrl extends GFCBaseCtrl<DepositDetails> {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger.getLogger(DepositDetailsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_DepositDetailsDialog;
	protected Combobox							depositType;
	protected ExtendedCombobox					branchCode;
	protected CurrencyBox						reservedAmount;
	protected CurrencyBox						transactionAmount;
	protected ExtendedCombobox					partnerBankId;
	protected Datebox							transactionDate;
	protected Uppercasebox						depositSlipNumber;
	protected Space								Space_depositSlipNumber;
	protected Listbox							listBox_DenominationsList;

	private DepositDetails						depositDetails;

	private transient DepositDetailsListCtrl	depositDetailsListCtrl;
	private transient DepositDetailsService		depositDetailsService;

	private List<ValueLabel>					listRequestType		= PennantStaticListUtil.getDepositTypesListList();
	private List<CashDenomination>				cashDenominations	= new ArrayList<CashDenomination>();

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
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.depositDetails.getDepositId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_DepositDetailsDialog(Event event) throws Exception {
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
			doLoadWorkFlow(this.depositDetails.isWorkflow(), this.depositDetails.getWorkflowId(), this.depositDetails.getNextTaskId());

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

		this.transactionDate.setFormat(PennantConstants.dateFormat);

		this.reservedAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.reservedAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.reservedAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.reservedAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.transactionAmount.setProperties(true, PennantConstants.defaultCCYDecPos);
		this.transactionAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.transactionAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.transactionAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.depositSlipNumber.setMaxlength(20);

		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });
		this.branchCode.setMandatoryStyle(true);

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
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
	 * The framework calls this event handler when user clicks the delete button.
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
	 * The framework calls this event handler when user clicks the cancel button.
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

		doShowNotes(this.depositDetails);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
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
			Decimalbox amountBox = new Decimalbox();
			amountBox.setMaxlength(18);
			amountBox.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
			amountBox.setId("Amount" + i);
			amountBox.setValue(cashDenomination.getAmount());
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
			totalAmount = totalAmount.add(cashDenomination.getAmount());
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

	public void onChangeCount(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Intbox count = (Intbox) event.getOrigin().getTarget();

		CashDenomination cashDenomination = (CashDenomination) event.getData();

		if (count.getValue() == null) {
			cashDenomination.setCount(0);
			cashDenomination.setAmount(BigDecimal.ZERO);
		} else {
			cashDenomination.setCount(count.getValue());
			BigDecimal denomination = new BigDecimal(cashDenomination.getDenomination());
			cashDenomination.setAmount(denomination.multiply(new BigDecimal(count.getValue())));
		}

		doFillDenominationsList(getCashDenominations());

		logger.debug("Leaving" + event.toString());
	}

	public void onChangeAmount(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Decimalbox amount = (Decimalbox) event.getOrigin().getTarget();

		CashDenomination cashDenomination = (CashDenomination) event.getData();

		if (amount.getValue() == null) {
			cashDenomination.setAmount(BigDecimal.ZERO);
		} else {
			cashDenomination.setAmount(amount.getValue());
		}

		doFillDenominationsList(getCashDenominations());

		logger.debug("Leaving" + event.toString());
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

		if (depositMovements == null) {
			this.transactionDate.setValue(DateUtility.getSysDate());
			depositMovements = new DepositMovements();
			depositMovements.setRecordType(PennantConstants.RCD_ADD);
			depositMovements.setTransactionType(PennantConstants.DEPOSIT_MOVEMENT_DEBIT);
			depositMovements.setVersion(1);
			depositMovements.setNewRecord(true);
			aDepositDetails.setDepositMovements(depositMovements);
			BigDecimal availableAmount = aDepositDetails.getActualAmount().subtract(aDepositDetails.getTransactionAmount());
			this.transactionAmount.setValue(PennantApplicationUtil.formateAmount(availableAmount, PennantConstants.defaultCCYDecPos));
			this.reservedAmount.setValue(PennantApplicationUtil.formateAmount(availableAmount, PennantConstants.defaultCCYDecPos));
		} else {
			this.reservedAmount.setValue(PennantApplicationUtil.formateAmount(aDepositDetails.getReservedAmount(), PennantConstants.defaultCCYDecPos));
			this.transactionAmount.setValue(PennantApplicationUtil.formateAmount(aDepositDetails.getTransactionAmount(), PennantConstants.defaultCCYDecPos));
			this.partnerBankId.setValue(String.valueOf(depositMovements.getPartnerBankId()), depositMovements.getPartnerBankName());
			this.transactionDate.setValue(depositMovements.getTransactionDate());
			this.depositSlipNumber.setValue(depositMovements.getDepositSlipNumber());
		}
		
		if (CollectionUtils.isNotEmpty(depositMovements.getDenominationList())) {
			doFillDenominationsList(depositMovements.getDenominationList());
		} else {
			doFillDenominationsList(prepareCashDenominations(AccountEventConstants.ACCEVENT_DEPOSIT_TYPE_CASH));
		}

		logger.debug(Literal.LEAVING);
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

		//Deposit Type
		try {
			isValidComboValue(this.depositType, Labels.getLabel("label_DepositDetailsDialog_DepositType.value"));
			aDepositDetails.setDepositType(getComboboxValue(this.depositType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch Code
		try {
			aDepositDetails.setBranchCode(this.branchCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Reserved Amount
		try {
			aDepositDetails.setReservedAmount(PennantAppUtil.unFormateAmount(this.reservedAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Transaction Amount
		try {
			double tranAmount = this.transactionAmount.getValidateValue().doubleValue();
			double resvAmount = this.reservedAmount.getValidateValue().doubleValue();
			
			if (tranAmount == 0 && resvAmount == 0) {
			} else if (resvAmount < tranAmount) {
				throw new WrongValueException(this.transactionAmount,
						Labels.getLabel("label_DepositDetailsDialog_TransactionAmount.value")
						+ " should be less than or equal to " + Labels.getLabel("label_DepositDetailsDialog_ReservedAmount.value"));
			}
			
			aDepositDetails.setTransactionAmount(PennantAppUtil.unFormateAmount(this.transactionAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
			depositMovements.setReservedAmount(PennantAppUtil.unFormateAmount(this.transactionAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Transaction Date
		try {
			depositMovements.setTransactionDate(this.transactionDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Deposit Slip Number
		try {
			depositMovements.setDepositSlipNumber(this.depositSlipNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Partner Bank Id
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
			wve = validateCashDenomination(wve);
			if (wve.isEmpty()) {
				depositMovements.setDenominationList(getCashDenominations());
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
				if (!StringUtils.isBlank(this.transactionAmount.getCcyTextBox().getValue())) {
					totalAmountValue = this.transactionAmount.getValidateValue();
				}

				if (calBoxValue.compareTo(totalAmountValue) != 0) {
					throw new WrongValueException(totalAmountBox, "Total Amount should be Equal to "
							+ Labels.getLabel("label_DepositDetailsDialog_TransactionAmount.value"));
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
	 * @param depositDetails
	 *            The entity that need to be render.
	 */
	public void doShowDialog(DepositDetails depositDetails) {
		logger.debug(Literal.LEAVING);

		if (depositDetails.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.reservedAmount.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(depositDetails.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				this.reservedAmount.focus();
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
			this.depositSlipNumber.setConstraint(new PTStringValidator(
					Labels.getLabel("label_DepositDetailsDialog_DepositSlipNumber.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.depositType.isReadonly()) {
			this.depositType.setConstraint(new StaticListValidator(listRequestType,
					Labels.getLabel("label_DepositDetailsDialog_DepositType.value")));
		}

		if (!this.reservedAmount.isReadonly()) {
			this.reservedAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_DepositDetailsDialog_ReservedAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}

		if (!this.transactionAmount.isReadonly()) {
			this.transactionAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_DepositDetailsDialog_TransactionAmount.value"),
							PennantConstants.defaultCCYDecPos, false, false, 0));
		}

		if (!this.partnerBankId.isReadonly()) {
			this.partnerBankId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_DepositDetailsDialog_PartnerBankId.value"), null, true, true));
		}
		
		if (!this.transactionDate.isDisabled()) {
			this.transactionDate.setConstraint(new PTDateValidator(Labels.getLabel("label_DepositDetailsDialog_TransactionDate.value"), true, true, null, true));
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
		this.reservedAmount.setConstraint("");
		this.transactionAmount.setConstraint("");
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
		this.reservedAmount.setErrorMessage("");
		this.transactionAmount.setErrorMessage("");
		this.depositSlipNumber.setErrorMessage("");
		this.partnerBankId.setErrorMessage("");
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a BranchCashToBankRequest object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final DepositDetails aDepositDetails = new DepositDetails();
		BeanUtils.copyProperties(this.depositDetails, aDepositDetails);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aDepositDetails.getDepositId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aDepositDetails.getRecordType()).equals("")) {
				aDepositDetails.setVersion(aDepositDetails.getVersion() + 1);
				aDepositDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDepositDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aDepositDetails.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aDepositDetails.getNextTaskId(),
							aDepositDetails);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDepositDetails, tranType)) {
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

		if (this.depositDetails.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("DepositDetailsDialog_BranchCode"), this.branchCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.branchCode);
			branchCode.setMandatoryStyle(false);
		}

		readOnlyComponent(isReadOnly("DepositDetailsDialog_RequestDate"), this.transactionDate);
		//readOnlyComponent(isReadOnly("DepositDetailsDialog_RequestAmount"), this.reservedAmount);
		//readOnlyComponent(isReadOnly("DepositDetailsDialog_RequestType"), this.depositType);
		readOnlyComponent(true, this.reservedAmount);
		readOnlyComponent(true, this.depositType);

		String editable = SysParamUtil.getValueAsString("DEPOSIT_AMOUNT_EDIT");
		if ("Y".equals(editable)) {
			readOnlyComponent(isReadOnly("DepositDetailsDialog_TransactionAmount"), this.transactionAmount);
		} else {
			readOnlyComponent(true, this.transactionAmount);
		}
		readOnlyComponent(isReadOnly("DepositDetailsDialog_DepositSlipNumber"), this.depositSlipNumber);
		if (depositSlipNumber.isReadonly()) {
			Space_depositSlipNumber.setSclass("");
		}
		readOnlyComponent(isReadOnly("DepositDetailsDialog_PartnerBankId"), this.partnerBankId);
		if (partnerBankId.isReadonly()) {
			partnerBankId.setMandatoryStyle(false);
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
		readOnlyComponent(true, this.reservedAmount);
		readOnlyComponent(true, this.transactionAmount);
		readOnlyComponent(true, this.depositSlipNumber);
		readOnlyComponent(true, this.partnerBankId);

		branchCode.setMandatoryStyle(false);
		Space_depositSlipNumber.setSclass("");
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

		this.branchCode.setValue("", "");
		this.depositType.setSelectedIndex(0);
		this.transactionDate.setText("");
		this.reservedAmount.setValue("");
		this.transactionAmount.setValue("");
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

		doSetValidation();
		doWriteComponentsToBean(aDepositDetails);

		isNew = aDepositDetails.isNew();
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
	private boolean doProcess(DepositDetails aDepositDetails, String tranType) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDepositDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDepositDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDepositDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDepositDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDepositDetails.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDepositDetails);
				}

				if (isNotesMandatory(taskId, aDepositDetails)) {
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

			aDepositDetails.setTaskId(taskId);
			aDepositDetails.setNextTaskId(nextTaskId);
			aDepositDetails.setRoleCode(getRole());
			aDepositDetails.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDepositDetails, tranType);
			String operationRefs = getServiceOperations(taskId, aDepositDetails);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDepositDetails, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDepositDetails, tranType);
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
		DepositDetails aDepositDetails = (DepositDetails) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
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
	private AuditHeader getAuditHeader(DepositDetails aDepositDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDepositDetails.getBefImage(), aDepositDetails);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aDepositDetails.getUserDetails(), getOverideMap());
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
