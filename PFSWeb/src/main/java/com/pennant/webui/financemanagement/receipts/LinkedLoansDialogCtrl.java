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
 * * FileName : LinkedLoansDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-09-2018 * *
 * Modified Date : 17-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-09-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.receipts;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Receipts/LinkedLoansDialog.zul
 */
public class LinkedLoansDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(LinkedLoansDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LinkedLoansDialog;
	protected Borderlayout borderlayout_LinkedLoans;

	protected Listbox listBox_LinkedLoans;
	// Buttons
	protected Button btn_LinkedLoan;

	private List<FinanceMain> financeMains;
	private List<FinanceProfitDetail> finpftDetails;

	/**
	 * default constructor.<br>
	 */
	public LinkedLoansDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReceiptDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_LinkedLoansDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LinkedLoansDialog);

		try {
			if (arguments.containsKey("financeMains")) {
				financeMains = (List<FinanceMain>) arguments.get("financeMains");
			}

			if (arguments.containsKey("finpftDetails")) {
				finpftDetails = (List<FinanceProfitDetail>) arguments.get("finpftDetails");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			doWriteBeanToComponents();
			this.window_LinkedLoansDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LinkedLoansDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(financeMains)) {
			Listitem item;

			for (FinanceMain finMain : financeMains) {
				for (FinanceProfitDetail finPftDetail : finpftDetails) {
					if (StringUtils.equals(finPftDetail.getFinReference(), finMain.getFinReference())) {

						int format = CurrencyUtil.getFormat(finMain.getFinCcy());
						item = new Listitem();
						Listcell lc = null;

						// Loan Date
						lc = new Listcell(DateUtil.formatToLongDate(finMain.getFinStartDate()));
						lc.setParent(item);

						// Loan Type
						lc = new Listcell(finMain.getFinType());
						lc.setParent(item);

						// FinReference
						lc = new Listcell(finMain.getFinReference());
						lc.setParent(item);

						// Original Amount
						BigDecimal finAmount = finMain.getFinCurrAssetValue().add(finMain.getFeeChargeAmt());
						lc = new Listcell(CurrencyUtil.format(finAmount, format));
						lc.setStyle("text-align:right");
						lc.setParent(item);

						// Installment Amount
						BigDecimal instllmentAmount = finPftDetail.getNSchdPft().add(finPftDetail.getNSchdPri());
						lc = new Listcell(CurrencyUtil.format(instllmentAmount, format));
						lc.setStyle("text-align:right");
						lc.setParent(item);

						// Outstanding Balance
						if (finMain.getFinRepaymentAmount() != null) {
							lc = new Listcell(
									CurrencyUtil.format(finAmount.subtract(finMain.getFinRepaymentAmount()), format));
							lc.setStyle("text-align:right");
						} else {
							lc = new Listcell("");
						}
						lc.setStyle("text-align:right");
						lc.setParent(item);

						// DPD
						lc = new Listcell(String.valueOf(finPftDetail.getCurODDays()));
						lc.setParent(item);

						// Loan Status
						lc = new Listcell(finMain.getFinStatus());
						lc.setParent(item);

						this.listBox_LinkedLoans.appendChild(item);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
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

		this.btnSave.setVisible(false);

		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}
}