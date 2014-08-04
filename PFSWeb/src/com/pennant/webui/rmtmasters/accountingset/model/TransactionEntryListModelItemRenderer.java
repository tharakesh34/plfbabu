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
 * FileName    		:  TransactionEntryListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountingset.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TransactionEntryListModelItemRenderer implements ListitemRenderer<TransactionEntry>, Serializable {

	private static final long serialVersionUID = 6906998807263283546L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, TransactionEntry transactionEntry, int count) throws Exception {

		//final TransactionEntry transactionEntry = (TransactionEntry) data;
		Listcell lc;
	  	lc = new Listcell(PennantAppUtil.formateInt(transactionEntry.getTransOrder()));
	  	lc.setParent(item);
	  	lc = new Listcell(transactionEntry.getTransDesc());
		lc.setParent(item);
		lc = new Listcell();
		Checkbox checkbox = new Checkbox();
		checkbox.setDisabled(true);
		checkbox.setChecked(transactionEntry.isEntryByInvestment());
		lc.appendChild(checkbox);
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(transactionEntry.getDebitcredit(),PennantStaticListUtil.getTranType()));
	  	lc.setParent(item);
		lc = new Listcell(transactionEntry.getAccount());
		lc.setParent(item);
	  	lc = new Listcell(transactionEntry.getTranscationCode());
		lc.setParent(item);
	  /*	lc = new Listcell(transactionEntry.getRuleDecider());
		lc.setParent(item);*/
	  	lc = new Listcell(transactionEntry.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(transactionEntry.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", transactionEntry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onTransactionEntryItemDoubleClicked");
	}
}