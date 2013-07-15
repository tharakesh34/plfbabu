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
 * FileName    		:  WIFFinanceScheduleDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancescheduledetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class WIFFinanceScheduleDetailListModelItemRenderer implements ListitemRenderer<FinanceScheduleDetail>, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceScheduleDetail wIFFinanceScheduleDetail, int count) throws Exception {

		//final FinanceScheduleDetail wIFFinanceScheduleDetail = (FinanceScheduleDetail) data;
		Listcell lc;
	  	lc = new Listcell(wIFFinanceScheduleDetail.getFinReference());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(wIFFinanceScheduleDetail.getSchDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateInt(wIFFinanceScheduleDetail.getSchSeq()));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getBalanceForPftCal(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getDefProfitSchd(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getDefPrincipalSchd(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getClosingBalance(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getPrvRepayAmount(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getDefProfitBal(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getDefPrincipalBal(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceScheduleDetail.getSchdPriPaid(),0));
	  	lc.setParent(item);
	  	lc = new Listcell(wIFFinanceScheduleDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(wIFFinanceScheduleDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", wIFFinanceScheduleDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onWIFFinanceScheduleDetailItemDoubleClicked");
	}
}