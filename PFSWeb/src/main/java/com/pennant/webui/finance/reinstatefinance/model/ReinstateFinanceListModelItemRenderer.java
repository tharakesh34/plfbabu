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
 * FileName    		:  ReinstateFinanceListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2011    														*
 *                                                                  						*
 * Modified Date    :  23-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.reinstatefinance.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class ReinstateFinanceListModelItemRenderer implements ListitemRenderer<ReinstateFinance>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public ReinstateFinanceListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, ReinstateFinance reinstateFinance, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(reinstateFinance.getFinType());
		lc.setParent(item);
		lc = new Listcell(reinstateFinance.getFinCategory());
		lc.setParent(item);
		lc = new Listcell(reinstateFinance.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(reinstateFinance.getFinReference());
		lc.setParent(item);
		lc = new Listcell(reinstateFinance.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(reinstateFinance.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(reinstateFinance.getGraceTerms() + reinstateFinance.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(reinstateFinance.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(reinstateFinance.getFinCcy());
		lc.setParent(item);
		BigDecimal finAmount = reinstateFinance.getFinAmount();
		if (reinstateFinance.getFeeChargeAmt() != null
				&& reinstateFinance.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0) {
			finAmount = finAmount.add(reinstateFinance.getFeeChargeAmt());
		}
		lc = new Listcell(
				PennantAppUtil.amountFormate(finAmount, CurrencyUtil.getFormat(reinstateFinance.getFinCcy())));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if (reinstateFinance.getFinRepaymentAmount() != null) {
			lc = new Listcell(PennantAppUtil.amountFormate(finAmount.subtract(reinstateFinance.getFinRepaymentAmount()),
					CurrencyUtil.getFormat(reinstateFinance.getFinCcy())));
			lc.setStyle("text-align:right");
		} else {
			lc = new Listcell("");

		}
		lc.setParent(item);
		lc = new Listcell(reinstateFinance.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(reinstateFinance.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", reinstateFinance.getFinReference());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onReinstateFinanceItemDoubleClicked");
	}
}