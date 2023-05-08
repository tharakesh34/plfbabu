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
 * * FileName : WIFFinanceMainListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-11-2011 * * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.wiffinancemain.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class WIFFinanceMainListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = 1L;

	public WIFFinanceMainListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinanceMain wIFFinanceMain, int count) {

		int format = CurrencyUtil.getFormat(wIFFinanceMain.getFinCcy());

		Listcell lc;
		lc = new Listcell(wIFFinanceMain.getFinReference());
		lc.setParent(item);
		if (wIFFinanceMain.getLovDescCustCIF() != null) {
			lc = new Listcell(wIFFinanceMain.getLovDescCustCIF());
		} else {
			lc = new Listcell();
		}
		lc.setParent(item);

		if (StringUtils.isBlank(wIFFinanceMain.getLovDescFinProduct())) {
			lc = new Listcell(wIFFinanceMain.getFinType());
			lc.setParent(item);
			lc = new Listcell("");
			lc.setParent(item);
		} else {
			lc = new Listcell(wIFFinanceMain.getLovDescFinProduct());
			lc.setParent(item);
			lc = new Listcell(wIFFinanceMain.getFinType());
			lc.setParent(item);
		}

		BigDecimal finAmount = wIFFinanceMain.getFinCurrAssetValue().add(wIFFinanceMain.getFeeChargeAmt());
		lc = new Listcell(PennantApplicationUtil.amountFormate(finAmount, format));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		lc = new Listcell(wIFFinanceMain.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(wIFFinanceMain.getScheduleMethod());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(wIFFinanceMain.getCalTerms() + wIFFinanceMain.getAdvTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(wIFFinanceMain.getFinStartDate()));
		lc.setParent(item);
		if (wIFFinanceMain.getGrcPeriodEndDate() != null) {
			lc = new Listcell(DateUtil.formatToLongDate(wIFFinanceMain.getGrcPeriodEndDate()));
		} else {
			lc = new Listcell();
		}
		lc.setParent(item);
		if (wIFFinanceMain.getMaturityDate() != null) {
			lc = new Listcell(DateUtil.formatToLongDate(wIFFinanceMain.getMaturityDate()));
		} else {
			lc = new Listcell();
		}
		lc.setParent(item);
		if (StringUtils.isNotBlank(wIFFinanceMain.getRecordStatus())) {
			lc = new Listcell(wIFFinanceMain.getRecordStatus());
		} else {
			lc = new Listcell();
		}
		lc.setParent(item);

		item.setAttribute("data", wIFFinanceMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onWIFFinanceMainItemDoubleClicked");
	}
}