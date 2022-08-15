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

package com.pennant.webui.cashmanagement.branchcashlimit.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BranchCashLimitListModelItemRenderer implements ListitemRenderer<BranchCashLimit>, Serializable {

	private static final long serialVersionUID = 1L;

	public BranchCashLimitListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, BranchCashLimit branchCashLimit, int count) {

		Listcell lc;
		lc = new Listcell(branchCashLimit.getBranchCode());

		lc.setParent(item);
		lc = new Listcell(branchCashLimit.getBranchCodeName());

		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(branchCashLimit.getReOrderLimit(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		lc.setStyle("text-align:right;");

		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(branchCashLimit.getCashLimit(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		lc.setStyle("text-align:right;");

		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getCashPosition(branchCashLimit.getReOrderLimit(),
				branchCashLimit.getBranchCash(), branchCashLimit.getCashLimit()));
		lc.setStyle("text-align:Left;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(branchCashLimit.getAutoTransitAmount(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		lc.setStyle("text-align:right;");

		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(branchCashLimit.getAdHocCashLimit(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		lc.setStyle("text-align:right;");

		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(branchCashLimit.getAdhocTransitAmount(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		lc.setStyle("text-align:right;");

		lc.setParent(item);
		lc = new Listcell(branchCashLimit.getRecordStatus());

		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(branchCashLimit.getRecordType()));

		lc.setParent(item);
		item.setAttribute("branchCode", branchCashLimit.getBranchCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBranchCashLimitItemDoubleClicked");
	}
}