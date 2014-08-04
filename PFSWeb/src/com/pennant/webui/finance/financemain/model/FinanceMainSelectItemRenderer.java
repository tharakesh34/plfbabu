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
 * FileName    		:  CustomerListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FinanceMainSelectItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = 1552059797117039294L;

	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) throws Exception {

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
		lc = new Listcell(DateUtility.formatUtilDate(financeMain.getFinStartDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(financeMain.getGraceTerms()+financeMain.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(financeMain.getMaturityDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinCcy());
		lc.setParent(item);
		BigDecimal finAmount = financeMain.getFinAmount();
		if(financeMain.getFeeChargeAmt() != null && financeMain.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0){
			finAmount = finAmount.add(financeMain.getFeeChargeAmt());
		}
		lc = new Listcell(PennantAppUtil.amountFormate(finAmount,financeMain.getLovDescFinFormatter()));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if(financeMain.getFinRepaymentAmount()!=null){
			lc = new Listcell(PennantAppUtil.amountFormate(finAmount
					.subtract(financeMain.getFinRepaymentAmount()),financeMain.getLovDescFinFormatter()));
			lc.setStyle("text-align:right");
		}else{
			lc = new Listcell("");
			
		}
		lc.setParent(item);
		lc = new Listcell(financeMain.getRecordStatus());
		lc.setParent(item);
		item.setAttribute("data", financeMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceItemDoubleClicked");
	}
}