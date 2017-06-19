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
 * FileName    		:  AddSecondaryAccountDialogCtrl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2016    														*
 *                                                                  						*
 * Modified Date    :  27-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2016       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.rits.cloning.Cloner;

public class AddSecondaryAccountDialogCtrl extends GFCBaseCtrl<SecondaryAccount> {
	private static final long serialVersionUID = 6004939933729664896L;
	private static final Logger logger = Logger.getLogger(AddSecondaryAccountDialogCtrl.class);

	protected Window window_SecondaryAccountDialog; // autoWired
	protected AccountSelectionBox secondaryAccountId;
	protected Intbox priority; // autoWired
	protected Row row_SecondaryAccount; // aotowired
	protected Row row_Priority;
	protected Row row_btnAdd;
	protected Button btnAdd;
	protected Row row_save;
	private FinanceDetail financeDetail = null; // over handed per parameters
	List<SecondaryAccount> secondarayAccount;
	private Listbox accountReceivableList;
	private FinanceMainBaseCtrl finaceMainBaseCtrl;
	private boolean isViewAllowed;
	private String repayAccountId;
	List<SecondaryAccount> accounList = new ArrayList<>();
	boolean isListModified = false;
	
	
	/**
	 * default constructor.<br>
	 */
	public AddSecondaryAccountDialogCtrl(){
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	public void onCreate$window_SecondaryAccountDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecondaryAccountDialog);

		logger.debug("Entering");
		FinanceType financetype;
		int finFormatter;

		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			secondarayAccount = getFinanceMain().getSecondaryAccount();
			if (secondarayAccount != null) {
				accountReceivableList.setCheckmark(true);
				accountReceivableList.setMultiple(true);
				doFillSecondaryAccountDetails(secondarayAccount);
			}
			finFormatter = CurrencyUtil.getFormat(getFinanceMain().getFinCcy());
			financetype = getFinanceDetail().getFinScheduleData().getFinanceType();

			this.secondaryAccountId.setAccountDetails(financetype.getFinType(),
					AccountConstants.FinanceAccount_SECONDARYACCT, financetype.getFinCcy());
			this.secondaryAccountId.setFormatter(finFormatter);
			this.secondaryAccountId.setBranchCode(StringUtils.trimToEmpty(getFinanceMain().getFinBranch()));
			this.secondaryAccountId.setCustCIF(String.valueOf(getFinanceMain().getCustID()));
			this.secondaryAccountId.setMandatoryStyle(true);

		}

		if (arguments.containsKey("financemainBaseCtrl")) {
			this.finaceMainBaseCtrl = (FinanceMainBaseCtrl) arguments.get("financemainBaseCtrl");

		}
		if (arguments.containsKey("repayAccountId")) {
			this.repayAccountId = (String) arguments.get("repayAccountId");
		}

		if (arguments.containsKey("isViewAllowed")) {
			isViewAllowed = (boolean) arguments.get("isViewAllowed");
			if (!isViewAllowed) {
				this.window_SecondaryAccountDialog.setTitle(Labels.getLabel("window_SecondaryAccountDialog.title"));
				this.window_SecondaryAccountDialog.setClosable(true);
				this.accountReceivableList.setMultiple(false);
				this.accountReceivableList.setCheckmark(false);
				this.accountReceivableList.setWidth("550px");
				this.accountReceivableList.setHeight("310px");
				this.row_SecondaryAccount.setVisible(false);
				this.row_Priority.setVisible(false);
				this.row_save.setVisible(false);
				this.row_btnAdd.setVisible(false);
			}

		}
		this.btnDelete.setDisabled(true);
		doSetFieldProperties();
		logger.debug("Leaving");
	}

	private FinanceMain getFinanceMain() {
		return getFinanceDetail().getFinScheduleData().getFinanceMain();
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		this.priority.setMaxlength(3);
	}

	public void onClick$btnAdd(Event event) throws Exception {
		logger.debug("Entering");
		SecondaryAccount acc;

		this.secondaryAccountId.setErrorMessage("");
		Clients.clearWrongValue(this.secondaryAccountId);

		if (StringUtils.isBlank(this.secondaryAccountId.getValue())) {

			throw new WrongValueException(this.secondaryAccountId, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_AddSecondaryAccountDialog_SecondaryAccount.value") }));
		}

		if (StringUtils.trimToEmpty(this.secondaryAccountId.getValue()).equals(repayAccountId)) {

			throw new WrongValueException(this.secondaryAccountId, Labels.getLabel("SECONDARYACC_EXISTS",
					new String[] { Labels.getLabel("label_AddSecondaryAccountDialog_SecondaryAccount.value") }));
		}

		if (this.priority.getValue() == null) {

			throw new WrongValueException(this.priority, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_AddSecondaryAccountDialog_Priority.value") }));
		}

		if (this.priority.getConstraint() != null) {
			this.priority.getValue();
		}
		this.priority.setErrorMessage("");
		Clients.clearWrongValue(this.priority);

		String accno = this.secondaryAccountId.getValue();
		int priority = this.priority.getValue();

		if (secondarayAccount.size() > 0) {
			accounList.addAll(secondarayAccount);
			secondarayAccount = new ArrayList<>();

		}

		if (!isAccAlreadyInList(accno)) {
			if (!isPriorityAlreadyInList(priority)) {
				isListModified = true;
				acc = new SecondaryAccount();
				acc.setAccountNumber(accno);
				acc.setPriority(priority);
				accounList.add(acc);
				this.secondaryAccountId.setValue("");
				this.priority.setText("");
				accountReceivableList.setCheckmark(true);
				accountReceivableList.setMultiple(true);
				this.secondaryAccountId.setConstraint("");
				this.priority.setConstraint("");
				this.secondaryAccountId.setErrorMessage("");
				this.priority.setErrorMessage("");
				doFillSecondaryAccountDetails(accounList);
				if (!this.btnDelete.isDisabled()) {
					this.btnDelete.setDisabled(false);
				}
			} else {
				throw new WrongValueException(this.priority,
						Labels.getLabel("label_AddSecondaryAccountDialog_Priority.value") + ":"
								+ this.priority.getValue() + " Already Added");
			}

		} else {
			throw new WrongValueException(this.secondaryAccountId,
					Labels.getLabel("label_AddSecondaryAccountDialog_SecondaryAccount.value") + ":"
							+ this.secondaryAccountId.getValue() + " Already Added");
		}

		logger.debug("Leaving");
	}

	private boolean isAccAlreadyInList(String accno) {
		for (SecondaryAccount detail : accounList) {
			if (detail.getAccountNumber().equals(accno)) {
				return true;
			}
		}
		return false;
	}

	private boolean isPriorityAlreadyInList(int priority) {
		for (SecondaryAccount detail : accounList) {
			if (detail.getPriority() == priority) {
				return true;
			}
		}
		return false;
	}

	private void doFillSecondaryAccountDetails(List<SecondaryAccount> secondarayAccount) {
		logger.debug("Entering ");
		this.accountReceivableList.getItems().clear();
		for (SecondaryAccount accountDetail : secondarayAccount) {
			Listitem listitem = new Listitem();
			Listcell listcell;

			listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(accountDetail.getAccountNumber()));
			listcell.setParent(listitem);

			listcell = new Listcell(String.valueOf(accountDetail.getPriority()));
			listcell.setParent(listitem);
			ComponentsCtrl.applyForward(accountReceivableList, "onSelect=onCheckListItem");
			listitem.setAttribute("DATA", accountDetail);
			accountReceivableList.appendChild(listitem);
			logger.debug("Leaving ");
		}

	}

	public void onCheckListItem(Event event) throws Exception {
		logger.debug("Entering");
		this.btnDelete.setDisabled(false);
		logger.debug("Leaving");
	}

	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering");

		doSetValidation();
		if (!isListModified) {
			accounList = secondarayAccount;
		}

		getFinaceMainBaseCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain()
				.setSecondaryAccount(accounList);
		this.window_SecondaryAccountDialog.onClose();

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.priority.isReadonly()) {
			this.priority.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_AddSecondaryAccountDialog_Priority.value"), true, false));
		}

	}

	public void onClick$btnDelete(Event event) throws Exception {
		logger.debug("Entering");
		if (this.accountReceivableList.getItems().size() > 0
				&& this.accountReceivableList.getSelectedItems().size() == 0) {
			throw new WrongValueException(this.accountReceivableList,
					Labels.getLabel("label_Delete_SecondaryAccount.value"));
		}

		Cloner cloner = new Cloner();
		accounList = cloner.deepClone(secondarayAccount);

		if (this.accountReceivableList.getSelectedItems().size() > 1) {

			List<SecondaryAccount> secondryList = new ArrayList<SecondaryAccount>();
			secondryList.addAll(accounList);
			for (Listitem listitem : accountReceivableList.getSelectedItems()) {
				SecondaryAccount accont = (SecondaryAccount) listitem.getAttribute("DATA");
				secondryList.remove(accont);
				isListModified = true;
			}
			accounList = secondryList;
		} else {
			if (this.accountReceivableList.getSelectedItem() != null && !accounList.isEmpty()) {
				accounList.remove(this.accountReceivableList.getSelectedItem().getIndex());
				isListModified = true;
			}
		}
		Clients.clearWrongValue(this.accountReceivableList);
		doFillSecondaryAccountDetails(accounList);

		if (this.accountReceivableList.getItems().isEmpty()) {
			this.btnDelete.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			this.window_SecondaryAccountDialog.onClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	public FinanceMainBaseCtrl getFinaceMainBaseCtrl() {
		return finaceMainBaseCtrl;
	}

	public void setFinaceMainBaseCtrl(FinanceMainBaseCtrl finaceMainBaseCtrl) {
		this.finaceMainBaseCtrl = finaceMainBaseCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
