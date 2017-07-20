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
 * FileName    		:  FinFeeReceiptDialogCtrl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-06-2017    														*
 *                                                                  						*
 * Modified Date    :  01-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-06-2017       Pennant	                 0.1                                            * 
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
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinFeeReceiptDialog.zul file.
 */
public class FinFeeReceiptDialogCtrl extends GFCBaseCtrl<FinFeeReceipt> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = Logger.getLogger(FinFeeReceiptDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinFeeReceiptDialog;

	protected Listbox listBoxReciptFeeDetail;
	protected Label feeType;
	protected Label feeAmount;
	protected Label paidAmount;
	protected Label waiverAmount;

	// For Dynamically calling of this Controller
	private String roleCode = "";
	private String finFeeType = "";
	private long feeTypeId;
	private BigDecimal feeAmountValue = BigDecimal.ZERO;
	private BigDecimal paidAmountValue = BigDecimal.ZERO;
	private BigDecimal waiverAmountValue = BigDecimal.ZERO;
	private FinFeeDetailListCtrl finFeeDetailListCtrl;
	private FinanceDetail financeDetail;

	private LinkedHashMap<Long, List<FinFeeReceipt>> finFeeReceiptMap = new LinkedHashMap<Long, List<FinFeeReceipt>>();

	/**
	 * default constructor.<br>
	 */
	public FinFeeReceiptDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinFeeReceiptDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinFeeReceipt object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinFeeReceiptDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(this.window_FinFeeReceiptDialog);

		try {
			if (arguments.containsKey("role")) {
				this.roleCode = (String) arguments.get("role");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("role"), "FinFeeReceiptDialog");
			}

			if (arguments.containsKey("finFeeReceiptMap")) {
				this.finFeeReceiptMap = (LinkedHashMap<Long, List<FinFeeReceipt>>) arguments.get("finFeeReceiptMap");
			}

			if (arguments.containsKey("feeType")) {
				this.finFeeType = (String) arguments.get("feeType");
			}

			if (arguments.containsKey("feeTypeId")) {
				this.feeTypeId = (long) arguments.get("feeTypeId");
			}

			if (arguments.containsKey("FeeAmount")) {
				this.feeAmountValue = (BigDecimal) arguments.get("FeeAmount");
			}

			if (arguments.containsKey("PaidAmount")) {
				this.paidAmountValue = (BigDecimal) arguments.get("PaidAmount");
			}

			if (arguments.containsKey("WaiverAmount")) {
				this.waiverAmountValue = (BigDecimal) arguments.get("WaiverAmount");
			}

			if (arguments.containsKey("finFeeDetailListCtrl")) {
				setFinFeeDetailListCtrl((FinFeeDetailListCtrl) arguments.get("finFeeDetailListCtrl"));
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(this.financeDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		BigDecimal paidAmount;
		BigDecimal remReceiptAmount;
		BigDecimal totalPaidAmount = BigDecimal.ZERO;
		Decimalbox paidBox;
		Decimalbox remReceiptFeeBox;
		List<Listitem> listItems = listBoxReciptFeeDetail.getItems();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		int formatter = CurrencyUtil.getFormat(this.financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());

		for (Listitem listItem : listItems) {
			paidBox = (Decimalbox) listItem.getChildren().get(4).getFirstChild();
			remReceiptFeeBox = (Decimalbox) listItem.getChildren().get(5).getFirstChild();
			paidAmount = PennantAppUtil.unFormateAmount(BigDecimal.valueOf(paidBox.doubleValue()), formatter);
			remReceiptAmount = PennantAppUtil.unFormateAmount(BigDecimal.valueOf(remReceiptFeeBox.doubleValue()), formatter);
			totalPaidAmount = totalPaidAmount.add(BigDecimal.valueOf(paidBox.doubleValue()));

			try {
				if (remReceiptAmount.compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(paidBox, Labels.getLabel("label_FinFeeReceiptDialog_PaiBox_Error.value"));
				}
			} catch (WrongValueException wv) {
				wve.add(wv);
			}
		}

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		} else if (this.paidAmountValue.compareTo(totalPaidAmount) != 0) {
			MessageUtil.showError(Labels.getLabel("label_FinFeeReceiptDialog_TotalPaidAmount_Error.value"));
			return;
		} else {
			for (Listitem listItem : listItems) {
				paidBox = (Decimalbox) listItem.getChildren().get(4).getFirstChild();
				paidAmount = PennantAppUtil.unFormateAmount(BigDecimal.valueOf(paidBox.doubleValue()), formatter);

				FinFeeReceipt finFeeReceipt = (FinFeeReceipt) listItem.getAttribute("finFeeReceipt");
				List<FinFeeReceipt> finFeeReceiptsList = this.finFeeReceiptMap.get(finFeeReceipt.getReceiptID());

				if (!finFeeReceipt.isExist()) {
					if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					} else {
						finFeeReceiptsList.add(finFeeReceipt);
					}
				} else {
					if (BigDecimal.ZERO.compareTo(paidAmount) == 0) {
						for (int i = 0; i < finFeeReceiptsList.size(); i++) {
							FinFeeReceipt finFeeReceipt2 = finFeeReceiptsList.get(i);
							if (finFeeReceipt2.getFeeType().equals(this.finFeeType)) {
								if (finFeeReceiptsList.size() > 1) {
									finFeeReceiptsList.remove(i);
									break;
								} else {
									finFeeReceipt2.setFeeTypeId(0);
									finFeeReceipt2.setFeeType("");
									finFeeReceipt2.setPaidAmount(BigDecimal.ZERO);
								}
							}
						}
					}
				}

				finFeeReceipt.setPaidAmount(paidAmount);
			}

			this.finFeeDetailListCtrl.doFillFinFeeReceipts(this.finFeeReceiptMap);
			closeDialog();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.listBoxReciptFeeDetail
				.setHeight(this.borderLayoutHeight - (this.listBoxReciptFeeDetail.getItemCount() * 20) + "px");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("FinFeeReceiptDialog", roleCode);

		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinFeeReceiptDialog_btnSave"));
		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnNotes.setVisible(false);
		this.btnCancel.setVisible(false);

		logger.debug("leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail financeDetail) throws InterruptedException {
		logger.debug("Entering");

		try {
			doWriteBeanToComponents(financeDetail);
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param financeDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinanceDetail financeDetail) {
		logger.debug("Entering ");

		this.feeType.setValue(this.finFeeType);
		this.feeAmount.setValue(String.valueOf(this.feeAmountValue));
		this.paidAmount.setValue(String.valueOf(this.paidAmountValue));
		this.waiverAmount.setValue(String.valueOf(this.waiverAmountValue));
		long workFlowId = this.financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId();

		List<FinFeeReceipt> finFeeReceipts = new ArrayList<FinFeeReceipt>();

		for (long receiptId : this.finFeeReceiptMap.keySet()) {
			FinFeeReceipt finFeeReceipt = null;
			FinFeeReceipt finFeeReceiptTemp = null;
			BigDecimal availableAmt = BigDecimal.ZERO;
			BigDecimal receiptAmt = BigDecimal.ZERO;
			List<FinFeeReceipt> finFeeReceiptList = this.finFeeReceiptMap.get(receiptId);

			for (int i = 0; i < finFeeReceiptList.size(); i++) {
				finFeeReceipt = finFeeReceiptList.get(i);

				if (receiptAmt.equals(BigDecimal.ZERO)) {
					receiptAmt = finFeeReceipt.getReceiptAmount();
				}

				if (i == 0) {
					availableAmt = receiptAmt;
				}

				if (finFeeReceipt.getFeeTypeId() == this.feeTypeId) {
					finFeeReceiptTemp = finFeeReceipt;
					finFeeReceiptTemp.setExist(true);
				} else {
					availableAmt = availableAmt.subtract(finFeeReceipt.getPaidAmount());
				}

				if (finFeeReceiptList.size() == 1 && finFeeReceipt.getFeeTypeId() <= 0) {
					finFeeReceiptTemp = finFeeReceipt;
					finFeeReceiptTemp.setNewRecord(true);
					finFeeReceiptTemp.setFeeType(this.finFeeType);
					finFeeReceiptTemp.setFeeTypeId(this.feeTypeId);
					finFeeReceiptTemp.setWorkflowId(workFlowId);
					finFeeReceiptTemp.setRecordType(PennantConstants.RCD_ADD);
					finFeeReceiptTemp.setExist(true);
				}

			}

			if (finFeeReceipt != null && finFeeReceiptTemp == null) {
				finFeeReceiptTemp = new FinFeeReceipt();
				finFeeReceiptTemp.setNewRecord(true);
				finFeeReceiptTemp.setReceiptAmount(finFeeReceipt.getReceiptAmount());
				finFeeReceiptTemp.setReceiptReference(finFeeReceipt.getReceiptReference());
				finFeeReceiptTemp.setReceiptType(finFeeReceipt.getReceiptType());
				finFeeReceiptTemp.setFeeType(this.finFeeType);
				finFeeReceiptTemp.setFeeTypeId(this.feeTypeId);
				finFeeReceiptTemp.setReceiptID(finFeeReceipt.getReceiptID());
				finFeeReceiptTemp.setWorkflowId(workFlowId);
				finFeeReceiptTemp.setRecordType(PennantConstants.RCD_ADD);
				finFeeReceiptTemp.setExist(false);
			}

			finFeeReceiptTemp.setAvailableAmount(availableAmt);
			finFeeReceipts.add(finFeeReceiptTemp);
		}

		doFillFinFeeReceipts(finFeeReceipts);

		logger.debug("Leaving ");
	}

	private void doFillFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts) {
		logger.debug("Entering");

		this.listBoxReciptFeeDetail.getItems().clear();
		FinanceMain finMain = this.financeDetail.getFinScheduleData().getFinanceMain();
		int formatter = CurrencyUtil.getFormat(finMain.getFinCcy());

		boolean readOnly = isReadOnly("FinFeeReceiptDialog_paidAmount");

		if (finFeeReceipts != null && !finFeeReceipts.isEmpty()) {
			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				Listitem item = new Listitem();
				Listcell lc;

				// Receipt Type
				lc = new Listcell(finFeeReceipt.getReceiptType());
				lc.setParent(item);

				// Receipt Reference
				lc = new Listcell(finFeeReceipt.getReceiptReference());
				lc.setParent(item);

				// Receipt Amount
				Decimalbox receiptAmountBox = new Decimalbox();
				receiptAmountBox.setMaxlength(18);
				receiptAmountBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				receiptAmountBox.setDisabled(true);
				receiptAmountBox.setValue(PennantAppUtil.formateAmount(finFeeReceipt.getReceiptAmount(), formatter));
				lc = new Listcell();
				lc.appendChild(receiptAmountBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Available Amount
				Decimalbox availableAmountBox = new Decimalbox();
				availableAmountBox.setMaxlength(18);
				availableAmountBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				availableAmountBox.setDisabled(true);
				availableAmountBox
						.setValue(PennantAppUtil.formateAmount(finFeeReceipt.getAvailableAmount(), formatter));
				lc = new Listcell();
				lc.appendChild(availableAmountBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Paid Amount
				Decimalbox paidBox = new Decimalbox();
				paidBox.setMaxlength(18);
				paidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				paidBox.setDisabled(readOnly);
				paidBox.setValue(PennantAppUtil.formateAmount(finFeeReceipt.getPaidAmount(), formatter));
				lc = new Listcell();
				lc.appendChild(paidBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Remaining Receipt Amount
				Decimalbox remReceiptAmountBox = new Decimalbox();
				remReceiptAmountBox.setMaxlength(18);
				remReceiptAmountBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				remReceiptAmountBox.setDisabled(true);
				remReceiptAmountBox.setValue(PennantAppUtil.formateAmount(
						finFeeReceipt.getAvailableAmount().subtract(finFeeReceipt.getPaidAmount()), formatter));
				lc = new Listcell();
				lc.appendChild(remReceiptAmountBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				item.setAttribute("finFeeReceipt", finFeeReceipt);

				this.listBoxReciptFeeDetail.appendChild(item);

				List<Object> amountBoxlist = new ArrayList<Object>(8);
				amountBoxlist.add(receiptAmountBox);
				amountBoxlist.add(availableAmountBox);
				amountBoxlist.add(paidBox);
				amountBoxlist.add(remReceiptAmountBox);
				amountBoxlist.add(finFeeReceipt);
				paidBox.addForward("onChange", window_FinFeeReceiptDialog, "onChangePaidAmount", amountBoxlist);
			}
		}

		logger.debug("Leaving");
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
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangePaidAmount(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		List<Object> list = (List<Object>) event.getData();
		Decimalbox availableAmountBox = (Decimalbox) list.get(1);
		Decimalbox paidBox = (Decimalbox) list.get(2);
		Decimalbox remReceiptFeeBox = (Decimalbox) list.get(3);

		availableAmountBox.setErrorMessage("");
		paidBox.setErrorMessage("");
		remReceiptFeeBox.setErrorMessage("");

		remReceiptFeeBox.setValue(BigDecimal.valueOf(availableAmountBox.doubleValue())
				.subtract(BigDecimal.valueOf(paidBox.doubleValue())));

		if (remReceiptFeeBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
			paidBox.setErrorMessage("Paid amount should be less than or equals to available amount.");
		}

		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}
}
