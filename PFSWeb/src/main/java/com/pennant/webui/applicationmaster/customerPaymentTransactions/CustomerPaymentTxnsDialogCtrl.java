/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PayOrderIssueHeaders. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. RepayOrderIssueHeaderion or retransmission of
 * the materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerPaymentTxnsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-08-2019 * *
 * Modified Date : 26-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-08-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.customerPaymentTransactions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class CustomerPaymentTxnsDialogCtrl extends GFCBaseCtrl<PaymentTransaction> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CustomerPaymentTxnsDialogCtrl.class);

	protected Window window_CustomerPaymentTxnsDialog;

	protected Textbox finReference;
	protected Grid grid_Basicdetails;

	protected Button btnPaymentSave;
	protected Row row_tranStatus;

	protected Label customerPaymentTxn_finType;
	protected Label customerPaymentTxn_custCIF;
	protected Checkbox customerPaymentTxn_quickDisb;
	protected Label customerPaymentTxn_finCcy;
	protected Label customerPaymentTxn_startDate;
	protected Label customerPaymentTxn_maturityDate;

	protected Label label_PayOrderIssueDialog_FinReference;

	protected Listbox listboxCustomerPaymentTxns;
	protected Label label_AdvancePayments_Title;

	protected Tab tabPosting;
	protected Listbox listBoxFinAccountings;
	protected Decimalbox customerPaymentTxn_FinAssetValue;
	protected Decimalbox customerPaymentTxn_FinCurrAssetValue;

	protected Textbox tranModule;
	protected Textbox tranReference;
	protected Textbox tranBatch;
	protected Longbox paymentId;
	protected Textbox statusCode;
	protected Textbox statusDesc;
	protected Combobox tranStatus;

	private transient CustomerPaymentTxnsListCtrl customerPaymentTxnsListCtrl;
	private transient FinAdvancePaymentsService finAdvancePaymentsService;

	private transient boolean validationOn;
	int listRows;

	private PaymentTransaction paymentTransaction;
	private FinanceDisbursement financeDisbursement;

	private List<FinAdvancePayments> finAdvancePaymentsList = new ArrayList<FinAdvancePayments>();
	private List<FinanceDisbursement> financeDisbursementList = new ArrayList<FinanceDisbursement>();
	private String ModuleType_CUSTPMTTXN = "CUSTPMTTXN";
	private DisbursementInstCtrl disbursementInstCtrl;
	private FinanceMain financeMain;
	private int ccyformat;
	private FinanceDisbursementDAO financeDisbursementDAO;

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

	public void onCreate$window_CustomerPaymentTxnsDialog(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_CustomerPaymentTxnsDialog);

		try {
			if (arguments.containsKey("paymentTransaction")) {
				this.paymentTransaction = (PaymentTransaction) arguments.get("paymentTransaction");
				PaymentTransaction befImage = new PaymentTransaction();
				BeanUtils.copyProperties(this.paymentTransaction, befImage);
				this.paymentTransaction.setBefImage(befImage);
				setPaymentTransaction(this.paymentTransaction);
			} else {
				setPaymentTransaction(null);
			}

			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (arguments.containsKey("financeMain")) {
				this.financeMain = (FinanceMain) arguments.get("financeMain");
				this.ccyformat = CurrencyUtil.getFormat(this.financeMain.getFinCcy());
			}
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
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 400;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listboxCustomerPaymentTxns.setHeight(listboxHeight + "px");
			this.listBoxFinAccountings.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getPaymentTransaction());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerPaymentTxnsDialog.onClose();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.customerPaymentTxn_FinAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyformat));
		this.customerPaymentTxn_FinCurrAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyformat));

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		this.btnPaymentSave.setVisible(!this.enqiryModule);
		this.row_tranStatus.setVisible(!this.enqiryModule);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPaymentSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		MessageUtil.showHelpWindow(event, window_CustomerPaymentTxnsDialog);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnPaymentSave.isVisible());
	}

	public void doWriteBeanToComponents(PaymentTransaction paymentTransaction,
			FinanceDisbursement financeDisbursement) {
		logger.debug(Literal.ENTERING);

		FinAdvancePayments finAdvancePayments = paymentTransaction.getFinAdvancePayments();

		String tranModule = "";
		if ("DISB".equals(paymentTransaction.getTranModule())) {
			tranModule = "Disbursement";
		}
		this.tranModule.setValue(tranModule);

		this.tranReference.setValue(paymentTransaction.getTranReference());
		this.tranBatch.setValue(paymentTransaction.getTranBatch());
		this.paymentId.setValue(paymentTransaction.getPaymentId());
		this.statusCode.setValue(paymentTransaction.getStatusCode());
		this.statusDesc.setValue(paymentTransaction.getStatusDesc());
		fillComboBox(this.tranStatus, "", PennantStaticListUtil.getDisbursementStatus(), "");

		this.customerPaymentTxn_finType.setValue(financeMain.getFinType() + " - " + financeMain.getFinType());
		this.customerPaymentTxn_finCcy
				.setValue(financeMain.getFinCcy() + " - " + CurrencyUtil.getCcyDesc(financeMain.getFinCcy()));

		this.customerPaymentTxn_startDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		this.customerPaymentTxn_maturityDate.setValue(DateUtil.formatToLongDate(financeMain.getMaturityDate()));

		this.finReference.setValue(financeMain.getFinReference());
		this.customerPaymentTxn_quickDisb.setChecked(financeMain.isQuickDisb());
		this.customerPaymentTxn_custCIF.setValue(financeMain.getCustCIF());

		this.customerPaymentTxn_FinAssetValue.setValue(formateAmount(financeMain.getFinAssetValue()));
		this.customerPaymentTxn_FinCurrAssetValue.setValue(formateAmount(financeMain.getFinCurrAssetValue()));
		doFillFinAdvancePaymentsDetails(finAdvancePayments, financeDisbursement);
		this.recordStatus.setValue(finAdvancePayments.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	private BigDecimal formateAmount(BigDecimal decimal) {
		return PennantApplicationUtil.formateAmount(decimal, ccyformat);
	}

	public void doWriteComponentsToBean(PaymentTransaction paymentTransaction) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (PennantConstants.List_Select.equals(this.tranStatus.getSelectedItem().getValue())) {
			throw new WrongValueException(this.tranStatus, "Status Update is mandatory.");
		}
		paymentTransaction.setTranStatus(this.tranStatus.getSelectedItem().getValue());
		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param header
	 */
	public void doShowDialog(PaymentTransaction paymentTransaction) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		try {
			disbursementInstCtrl.init(this.listboxCustomerPaymentTxns, financeMain.getFinCcy(), false, getRole());
			List<FinanceDisbursement> disbDataList = financeDisbursementDAO
					.getFinanceDisbursementDetails(financeMain.getFinID(), TableType.MAIN_TAB.getSuffix(), false);
			disbursementInstCtrl.setFinanceDisbursement(disbDataList);
			disbursementInstCtrl.setFinanceMain(financeMain);

			doWriteBeanToComponents(paymentTransaction, financeDisbursement);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_CustomerPaymentTxnsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.finReference.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.finReference.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerPaymentTxnsListCtrl().search();
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PaymentTransaction paymentTransaction = new PaymentTransaction();
		BeanUtils.copyProperties(getPaymentTransaction(), paymentTransaction);

		doWriteComponentsToBean(paymentTransaction);
		try {
			this.finAdvancePaymentsService.processPayments(paymentTransaction);
			refreshList();
			closeDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doFillFinAdvancePaymentsDetails(FinAdvancePayments finAdvancePayDetail,
			FinanceDisbursement financeDisbursement) {
		logger.debug(Literal.ENTERING);
		List<FinAdvancePayments> finAdvancePayDetails = new ArrayList<FinAdvancePayments>();
		List<FinanceDisbursement> financeDisbursements = new ArrayList<FinanceDisbursement>();
		finAdvancePayDetails.add(finAdvancePayDetail);
		disbursementInstCtrl.doFillFinAdvancePaymentsDetailss(finAdvancePayDetails, false);
		setFinAdvancePaymentsList(finAdvancePayDetails);
		setFinanceDisbursementList(financeDisbursements);
		logger.debug(Literal.LEAVING);
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

	public void onClick$button_PayOrderIssueDialog_NewDisbursement(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		disbursementInstCtrl.onClickNew(this.customerPaymentTxnsListCtrl, this, ModuleType_CUSTPMTTXN,
				getFinAdvancePaymentsList(), null, null);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFinAdvancePaymentsItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		disbursementInstCtrl.onDoubleClick(this.customerPaymentTxnsListCtrl, this, ModuleType_CUSTPMTTXN, true, null);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 * 
	 * @param accountingSetEntries (List)
	 * 
	 */
	public void doFillAccounting(List<?> accountingSetEntries) {
		logger.debug(Literal.ENTERING);

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
						Label label = new Label(PennantApplicationUtil.getLabelDesc(entry.getDrOrCr(),
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
		logger.debug(Literal.LEAVING);
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

	public void setDisbursementInstCtrl(DisbursementInstCtrl disbursementInstCtrl) {
		this.disbursementInstCtrl = disbursementInstCtrl;
	}

	public PaymentTransaction getPaymentTransaction() {
		return paymentTransaction;
	}

	public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
	}

	public FinAdvancePaymentsService getFinAdvancePaymentsService() {
		return finAdvancePaymentsService;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

}