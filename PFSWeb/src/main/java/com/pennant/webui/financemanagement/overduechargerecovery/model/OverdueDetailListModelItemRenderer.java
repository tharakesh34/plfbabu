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
 * * FileName : finODDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-05-2012
 * * * Modified Date : 11-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.overduechargerecovery.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class OverdueDetailListModelItemRenderer implements ListitemRenderer<FinODDetails>, Serializable {

	private static final long serialVersionUID = 3995133144435008423L;

	private int ccyFormatter = 0;

	public OverdueDetailListModelItemRenderer() {
	    super();
	}

	public OverdueDetailListModelItemRenderer(int ccyFormatter) {
		this.ccyFormatter = ccyFormatter;
	}

	@Override
	public void render(Listitem item, FinODDetails finODDetail, int count) {

		Listcell lc;
		lc = new Listcell(DateUtil.formatToLongDate(finODDetail.getFinODSchdDate()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(finODDetail.getFinCurODDays()));
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getFinCurODPri(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getFinCurODPft(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getFinCurODAmt(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getTotPenaltyAmt(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getTotWaived(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getTotPenaltyPaid(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(finODDetail.getTotPenaltyBal(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(finODDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0 ? "Recovery" : "Collected");
		if (finODDetail.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
			lc.setStyle("font-weight:bold;color:red;");
		} else {
			lc.setStyle("font-weight:bold;color:green;");
		}
		lc.setParent(item);

	}
}