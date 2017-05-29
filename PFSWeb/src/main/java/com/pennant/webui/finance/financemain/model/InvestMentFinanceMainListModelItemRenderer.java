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
 * FileName    		:  FinanceMainListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for list items in the list box.
 * 
 */
public class InvestMentFinanceMainListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = -4562142056572229437L;
	
	public InvestMentFinanceMainListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) throws Exception {
		int format=CurrencyUtil.getFormat(financeMain.getFinCcy());
		Listcell lc;
		lc = new Listcell();
		
		String custCIF =financeMain.getLovDescCustCIF();
		if(financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE &&
				StringUtils.isBlank(financeMain.getLovDescCustCIF())){
			custCIF = "In Process";
			lc.setStyle("font-weight:bold;color:green;");
		}
		lc.setLabel(custCIF);
		lc.setParent(item);		
		lc = new Listcell(financeMain.getLovDescCustShrtName());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getInvestmentRef());
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinReference());
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescProductCodeName());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getFinType());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getFinCcy());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getScheduleMethod() == null ? "" : financeMain.getScheduleMethod());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(financeMain.getFinAmount(), format));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(financeMain.getFinAmount().subtract(financeMain.getDownPayment())
				.add(financeMain.getFeeChargeAmt() == null ?BigDecimal.ZERO : financeMain.getFeeChargeAmt())
				.add(financeMain.getInsuranceAmt() == null ?BigDecimal.ZERO : financeMain.getInsuranceAmt()), format));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		item.setAttribute("data", financeMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceMainItemDoubleClicked");
	}
}