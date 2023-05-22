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
 * * FileName : ExcessEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-08-2019 * *
 * Modified Date : 08-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-08-2019 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.pff.fee.AdviseType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/ExcessEnquiryDialog.zul file.
 */
public class ExcessEnquiryDialogCtrl extends GFCBaseCtrl<FinExcessAmount> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(ExcessEnquiryDialogCtrl.class);

	protected Window window_ExcessEnquiryDialog;
	protected Listbox listBoxExcess;
	protected Borderlayout borderlayoutExcessEnquiry;
	private Tabpanel tabPanel_dialogWindow;
	private Listheader listheaderExcessHeaderDialogButton;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl;
	private List<FinExcessAmount> paymentDetailList = new ArrayList<>();

	private List<ManualAdvise> payables;
	private int ccyFormatter = 0;

	public ExcessEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_ExcessEnquiryDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_ExcessEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("excessDetails")) {
			this.paymentDetailList = (List<FinExcessAmount>) arguments.get("excessDetails");
		} else {
			this.paymentDetailList = new ArrayList<>();
		}
		if (arguments.containsKey("payables")) {
			this.payables = (List<ManualAdvise>) arguments.get("payables");
		} else {
			this.payables = null;
		}
		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (int) arguments.get("ccyFormatter");
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}
		doShowDialog();
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(payables)) {
			for (ManualAdvise manualAdvise : payables) {
				FinExcessAmount finExcess = new FinExcessAmount();
				finExcess.setAmountType(String.valueOf(manualAdvise.getAdviseType()));
				finExcess.setFeeTypeDesc(manualAdvise.getFeeTypeDesc());
				finExcess.setTaxComponent(manualAdvise.getTaxComponent());
				finExcess.setAmount(manualAdvise.getAdviseAmount());
				finExcess.setUtilisedAmt(manualAdvise.getPaidAmount());
				finExcess.setReservedAmt(manualAdvise.getReservedAmt());
				finExcess.setBalanceAmt(manualAdvise.getBalanceAmt());
				finExcess.setValueDate(manualAdvise.getValueDate());
				this.paymentDetailList.add(finExcess);
			}
		}

		try {
			doFillHeaderList(this.paymentDetailList);

			if (tabPanel_dialogWindow != null) {

				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
				this.listBoxExcess.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.window_ExcessEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight + "px");
				tabPanel_dialogWindow.appendChild(this.window_ExcessEnquiryDialog);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onExpand(ForwardEvent event) {
		Button button = (Button) event.getOrigin().getTarget();
		FinExcessAmount pd = (FinExcessAmount) button.getAttribute("pd");

		pd.setExpand(false);
		pd.setCollapse(true);

		doFillHeaderList(paymentDetailList);
	}

	public void onCollapse(ForwardEvent event) {
		Button button = (Button) event.getOrigin().getTarget();
		FinExcessAmount pd = (FinExcessAmount) button.getAttribute("pd");

		pd.setExpand(true);
		pd.setCollapse(false);

		doFillHeaderList(paymentDetailList);
	}

	private void doFillHeaderList(List<FinExcessAmount> excesslist) {
		this.listBoxExcess.getItems().clear();

		this.listheaderExcessHeaderDialogButton.setVisible(true);

		Map<String, List<FinExcessAmount>> map = new HashMap<>();

		for (FinExcessAmount fa : excesslist) {
			String excessType = fa.getAmountType();

			if (AdviseType.isPayable(fa.getAmountType())) {
				excessType = String.valueOf(AdviseType.PAYABLE.id());
			}

			List<FinExcessAmount> feaList = map.get(excessType);

			if (feaList == null) {
				feaList = new ArrayList<>();
				map.put(excessType, feaList);
			}

			feaList.add(fa);
		}

		for (Entry<String, List<FinExcessAmount>> fea : map.entrySet()) {
			doFillHeaderList(fea.getKey(), fea.getValue());
		}
	}

	private void doFillHeaderList(String excessType, List<FinExcessAmount> feList) {
		logger.debug(Literal.ENTERING);

		BigDecimal totalPayAmt = BigDecimal.ZERO;
		BigDecimal avaAmount = BigDecimal.ZERO;
		BigDecimal utilizedAmount = BigDecimal.ZERO;
		BigDecimal reservedAmount = BigDecimal.ZERO;
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal payAmount = BigDecimal.ZERO;

		FinExcessAmount temp = null;

		for (FinExcessAmount pd : feList) {
			if (temp == null) {
				temp = pd;
			}

			avaAmount = pd.getAmount().add(avaAmount);
			utilizedAmount = pd.getUtilisedAmt().add(utilizedAmount);
			balanceAmount = pd.getBalanceAmt().add(balanceAmount);
			reservedAmount = pd.getReservedAmt().add(reservedAmount);

			payAmount = pd.getAmount().add(payAmount);
			totalPayAmt = totalPayAmt.add(payAmount);
		}

		Listitem item = new Listitem();

		// Button
		Listcell lc = new Listcell();
		lc.appendChild(getButton(temp));
		lc.setParent(item);

		// Amount Type
		String amountType = null;
		if (excessType.equals("S")) {
			amountType = Labels.getLabel("label_Settlement_" + excessType);
		} else {
			amountType = Labels.getLabel("label_Excess_Type_" + excessType);

			if (String.valueOf(AdviseType.PAYABLE.id()).equals(excessType)) {
				amountType = Labels.getLabel("label_PaymentHeaderDialog_ManualAdvisePayable.value");
			}
		}
		lc = new Listcell(amountType);
		lc.setSpan(3);
		lc.setParent(item);

		// Amount
		lc = new Listcell();
		lc.appendChild(getDecimalbox(avaAmount));
		lc.setParent(item);

		// Utilized Amount
		lc = new Listcell();
		lc.appendChild(getDecimalbox(utilizedAmount));
		lc.setParent(item);

		// Reserved Amount
		lc = new Listcell();
		lc.appendChild(getDecimalbox(reservedAmount));
		lc.setParent(item);

		// Balanced Amount
		lc = new Listcell();
		lc.appendChild(getDecimalbox(balanceAmount));
		lc.setParent(item);

		this.listBoxExcess.appendChild(item);

		if (temp.isExpand() && !temp.isCollapse()) {
			doFillChildDetail(feList);
		}

		lc = new Listcell();
		lc.appendChild(getDecimalbox(totalPayAmt));
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);
	}

	private void doFillChildDetail(List<FinExcessAmount> feDetail) {
		logger.debug(Literal.ENTERING);

		for (FinExcessAmount pd : feDetail) {
			BigDecimal availAmount = pd.getAmount();
			BigDecimal paidAmount = pd.getUtilisedAmt();
			BigDecimal reserveAmt = pd.getReservedAmt();
			BigDecimal balAmt = pd.getBalanceAmt();
			String desc = pd.getFeeTypeDesc();
			if (StringUtils.equals(pd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)) {
				desc = desc.concat(" (Inclusive)");
			} else if (StringUtils.equals(pd.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) {
				desc = desc.concat(" (Exclusive)");
			}
			Listitem item = new Listitem();

			Listcell lc = new Listcell();
			lc.setParent(item);

			// Amount Type
			lc = new Listcell(desc);
			lc.setParent(item);
			item.appendChild(new Listcell(pd.getReceiptID() == null ? "" : String.valueOf(pd.getReceiptID())));
			item.appendChild(new Listcell(DateUtil.formatToLongDate(pd.getValueDate())));

			// Available Amount
			lc = new Listcell();
			lc.appendChild(getDecimalbox(availAmount));
			lc.setParent(item);

			// utilized amount
			lc = new Listcell();
			lc.appendChild(getDecimalbox(paidAmount));
			lc.setParent(item);

			// reserve amount
			lc = new Listcell();
			lc.appendChild(getDecimalbox(reserveAmt));
			lc.setParent(item);

			// balance amount
			lc = new Listcell();
			lc.appendChild(getDecimalbox(balAmt));
			lc.setParent(item);

			this.listBoxExcess.appendChild(item);
		}
		logger.debug(Literal.LEAVING);
	}

	private Button getButton(FinExcessAmount temp) {
		Button button = new Button();

		if (temp.isExpand()) {
			button.setImage("/images/icons/delete.png");
			button.setStyle("background:white;border:0px;");
			button.addForward("onClick", self, "onExpand");
		} else {
			button.setImage("/images/icons/add.png");
			button.setStyle("background:#FFFFFF;border:0px;onMouseOver ");
			button.addForward("onClick", self, "onCollapse");
		}

		button.setAttribute("pd", temp);
		return button;
	}

	private Decimalbox getDecimalbox(BigDecimal amount) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		decimalbox.setStyle("text-align:right; ");
		decimalbox.setReadonly(true);
		decimalbox.setValue(PennantApplicationUtil.formateAmount(amount, ccyFormatter));

		return decimalbox;
	}
}
