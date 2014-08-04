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
 * FileName    		:  CommidityLoanDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.commidityloandetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CommidityLoanDetailListModelItemRenderer implements ListitemRenderer<CommidityLoanDetail>, Serializable {

	private static final long serialVersionUID = -2004592795687229837L;

	@Override
	public void render(Listitem item, CommidityLoanDetail commidityLoanDetail, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(commidityLoanDetail.getLoanRefNumber());
		lc.setParent(item);
	  	lc = new Listcell(commidityLoanDetail.getLovDescItemDescription());
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateLong(commidityLoanDetail.getQuantity()));
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(commidityLoanDetail.getUnitBuyPrice(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(commidityLoanDetail.getBuyAmount(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(commidityLoanDetail.getUnitSellPrice(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(commidityLoanDetail.getSellAmount(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(commidityLoanDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(commidityLoanDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", commidityLoanDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCommidityLoanDetailItemDoubleClicked");
	}
}