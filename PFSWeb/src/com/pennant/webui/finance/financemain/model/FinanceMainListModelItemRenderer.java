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

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for list items in the list box.
 * 
 */
public class FinanceMainListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = -4562142056572229437L;
	
	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) throws Exception {

		Listcell lc;
		lc = new Listcell();
		
		String custCIF =financeMain.getLovDescCustCIF();
		if(financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE &&
				StringUtils.trimToEmpty(financeMain.getLovDescCustCIF()).equals("")){
			custCIF = "In Process";
			lc.setStyle("font-weight:bold;color:green;");
		}
		lc.setLabel(custCIF);
		lc.setParent(item);		
		lc = new Listcell(financeMain.getLovDescCustShrtName());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getFinReference());
		lc.setParent(item);
		
		if(StringUtils.trimToEmpty(financeMain.getLovDescFinProduct()).equals("")){
			lc = new Listcell(financeMain.getFinType());
			lc.setParent(item);
			lc = new Listcell("");
			lc.setParent(item);
		}else{
			lc = new Listcell("");
			lc.setParent(item);
			lc = new Listcell(financeMain.getFinType());
			lc.setParent(item);
		}
	  	
	  	lc = new Listcell(financeMain.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(financeMain.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(financeMain.getFinAmount(), financeMain.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(financeMain.getFinAmount().subtract(financeMain.getDownPayment()).add(financeMain.getFeeChargeAmt() == null ?BigDecimal.ZERO : financeMain.getFeeChargeAmt()), financeMain.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescRequestStage());
		lc.setParent(item);
		
		lc = new Listcell(PennantAppUtil.getlabelDesc(String.valueOf(financeMain.getPriority()), PennantStaticListUtil.getQueuePriority()));
		switch (financeMain.getPriority()) {
        case 0:  lc.setStyle("font-weight:bold;color:blue;");
                 break;
        case 1:  lc.setStyle("font-weight:bold;color:green;");
                 break;
        case 2:  lc.setStyle("font-weight:bold;color:yellow;");
                 break;
        case 3: lc.setStyle("font-weight:bold;color:red;");
                 break;
		}
		lc.setParent(item);
		
	  	lc = new Listcell(financeMain.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeMain.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", financeMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceMainItemDoubleClicked");
	}
}