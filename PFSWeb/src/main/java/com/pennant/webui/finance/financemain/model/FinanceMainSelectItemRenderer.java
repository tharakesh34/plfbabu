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
 * * FileName : FinanceMainSelectItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FinanceMainSelectItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = 1552059797117039294L;

	public FinanceMainSelectItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) {

		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		Listcell lc;
		lc = new Listcell(financeMain.getFinType());
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescProductCodeName());
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinReference());
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(financeMain.getNOInst()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinCcy());
		lc.setParent(item);
		BigDecimal finAmount = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt());
		lc = new Listcell(PennantApplicationUtil.amountFormate(finAmount, format));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if (financeMain.getFinRepaymentAmount() != null) {
			// KMILLMS-854: Loan basic details-loan O/S amount is not getting 0.
			BigDecimal curFinAmountValue = null;

			if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(financeMain.getClosingStatus())) {
				curFinAmountValue = BigDecimal.ZERO;
			} else {
				curFinAmountValue = finAmount.add(financeMain.getTotalCpz())
						.subtract(financeMain.getFinRepaymentAmount()).subtract(financeMain.getDownPayment())
						.subtract(financeMain.getSvAmount());
			}

			lc = new Listcell(PennantApplicationUtil.amountFormate(curFinAmountValue, format));
			lc.setStyle("text-align:right");
		} else {
			lc = new Listcell("");
		}
		lc.setParent(item);
		lc = new Listcell(
				StringUtils.isNotBlank(financeMain.getLovDescRequestStage())
						? (financeMain.getLovDescRequestStage().endsWith(",")
								? financeMain.getLovDescRequestStage().substring(0,
										financeMain.getLovDescRequestStage().length() - 1)
								: financeMain.getLovDescRequestStage())
						: "");
		lc.setParent(item);
		lc = new Listcell(financeMain.getRecordStatus());
		lc.setParent(item);
		item.setAttribute("data", financeMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceItemDoubleClicked");
	}
}