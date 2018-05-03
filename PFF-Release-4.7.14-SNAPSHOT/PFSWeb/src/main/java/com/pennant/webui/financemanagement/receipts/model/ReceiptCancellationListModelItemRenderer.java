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
 * FileName    		:  AcademicListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.financemanagement.receipts.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class ReceiptCancellationListModelItemRenderer implements ListitemRenderer<FinReceiptHeader>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;
	
	public ReceiptCancellationListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, FinReceiptHeader header, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(header.getReference());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(header.getReceiptPurpose(), PennantStaticListUtil.getReceiptPurpose()));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(header.getReceiptMode(), PennantStaticListUtil.getReceiptModes()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(header.getReceiptAmount(), CurrencyUtil.getFormat(header.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(header.getAllocationType(), PennantStaticListUtil.getAllocationMethods()));
		lc.setParent(item);
		lc = new Listcell(header.getFinType());
		lc.setParent(item);
		lc = new Listcell(header.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(header.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(header.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(header.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(header.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("data", header);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onReceiptCancellationItemDoubleClicked");
	}
}