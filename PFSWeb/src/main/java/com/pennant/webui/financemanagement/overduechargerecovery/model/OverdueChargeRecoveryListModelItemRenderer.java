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
 * * FileName : OverdueChargeRecoveryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 11-05-2012 * * Modified Date : 11-05-2012 * * Description : * *
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
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class OverdueChargeRecoveryListModelItemRenderer
		implements ListitemRenderer<OverdueChargeRecovery>, Serializable {

	private static final long serialVersionUID = 3995133144435008423L;
	private BigDecimal cummulativePenalityAmt;

	public OverdueChargeRecoveryListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, OverdueChargeRecovery overdueChargeRecovery, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(
					new Listcell("Overdue Term : " + DateUtil.formatToLongDate(overdueChargeRecovery.getFinODSchdDate())
							+ "-" + overdueChargeRecovery.getFinODFor()));
			cummulativePenalityAmt = BigDecimal.ZERO;
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(10);
			item.appendChild(cell);
		} else {

			int format = CurrencyUtil.getFormat(overdueChargeRecovery.getFinCcy());

			Listcell lc;
			lc = new Listcell(DateUtil.formatToLongDate(overdueChargeRecovery.getMovementDate()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(overdueChargeRecovery.getODDays()));
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getFinCurODPri(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getFinCurODPft(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getFinCurODAmt(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			cummulativePenalityAmt = cummulativePenalityAmt.add(overdueChargeRecovery.getPenalty());

			lc = new Listcell(CurrencyUtil.format(cummulativePenalityAmt, format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getPenalty(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getWaivedAmt(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getPenaltyPaid(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(overdueChargeRecovery.getPenaltyBal(), format));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			boolean isRecovered = false;
			if (overdueChargeRecovery.getPenaltyBal().compareTo(BigDecimal.ZERO) == 0) {
				isRecovered = true;
			}

			if (isRecovered) {
				lc = new Listcell("Collected");
				lc.setStyle("font-weight:bold;color:green;");
			} else {
				lc = new Listcell("Recovery");
				lc.setStyle("font-weight:bold;color:red;");
			}
			lc.setParent(item);

			item.setAttribute("data", overdueChargeRecovery);
			// ComponentsCtrl.applyForward(item, "onDoubleClick=onOverdueChargeRecoveryItemDoubleClicked");
		}
	}
}