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

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinanceMain;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FinanceMainSelectItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = 1552059797117039294L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) throws Exception {

		//final FinanceMain financeMain = (FinanceMain) data;
		Listcell lc;
		lc = new Listcell(financeMain.getFinReference());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getFinType());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getFinCcy());
		lc.setParent(item);
	  	lc = new Listcell(financeMain.getScheduleMethod()==null?"":financeMain.getScheduleMethod() );
		lc.setParent(item);
		lc = new Listcell(financeMain.getProfitDaysBasis());
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinBranch());
		lc.setParent(item);
		item.setAttribute("data", financeMain);
		ComponentsCtrl.applyForward(item,
		"onDoubleClick=onFinanceItemDoubleClicked");
	}
}