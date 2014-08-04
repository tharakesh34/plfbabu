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
 * FileName    		:  GoodsLoanDetailListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.goodsloandetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class GoodsLoanDetailListModelItemRenderer implements ListitemRenderer<GoodsLoanDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, GoodsLoanDetail goodsLoanDetail, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(goodsLoanDetail.getLoanRefNumber());
		lc.setParent(item);
	  	lc = new Listcell(goodsLoanDetail.getItemNumber());
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.amountFormate(goodsLoanDetail.getUnitPrice(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateInt(goodsLoanDetail.getQuantity()));
	  	lc.setParent(item);
	  	lc = new Listcell(goodsLoanDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(goodsLoanDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", goodsLoanDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onGoodsLoanDetailItemDoubleClicked");
	}
}