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
 * FileName    		:  TreasuaryFinanceListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.treasuaryfinance.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TreasuaryFinHeaderListModelItemRenderer implements ListitemRenderer<InvestmentFinHeader>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public TreasuaryFinHeaderListModelItemRenderer() {
		
	}
	
   @Override
	public void render(Listitem item, InvestmentFinHeader data, int count) throws Exception {

		final InvestmentFinHeader treasuaryFinance = (InvestmentFinHeader) data;
		Listcell lc;
		lc = new Listcell(treasuaryFinance.getInvestmentRef());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(treasuaryFinance.getTotPrincipalAmt(), 
				CurrencyUtil.getFormat(treasuaryFinance.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(treasuaryFinance.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(treasuaryFinance.getStartDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(treasuaryFinance.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(treasuaryFinance.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(treasuaryFinance.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onTreasuaryFinanceItemDoubleClicked");
	}


}