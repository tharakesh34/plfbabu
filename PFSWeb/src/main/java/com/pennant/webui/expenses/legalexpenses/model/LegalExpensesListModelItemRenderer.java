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
 * FileName    		:  LegalExpensesListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.expenses.legalexpenses.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LegalExpensesListModelItemRenderer implements ListitemRenderer<LegalExpenses>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, LegalExpenses legalExpenses, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(legalExpenses.getExpReference());
		lc.setParent(item);
		lc = new Listcell(legalExpenses.getFinReference());
		lc.setParent(item);
		lc = new Listcell(legalExpenses.getCustomerId());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(legalExpenses.getAmount(), 2));
		lc.setParent(item);
		lc = new Listcell(legalExpenses.getTransactionType());
		lc.setParent(item);
		lc = new Listcell(legalExpenses.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(legalExpenses.getRecordType()));
		lc.setParent(item);

		item.setAttribute("expReference", legalExpenses.getExpReference());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalExpensesItemDoubleClicked");
	}
}