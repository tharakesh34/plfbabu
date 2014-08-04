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
 * FileName    		:  OverdueChargeRecoveryListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.overduechargerecovery.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class OverdueChargeRecoveryListModelItemRenderer implements ListitemRenderer<OverdueChargeRecovery>, Serializable {

	private static final long serialVersionUID = 3995133144435008423L;

	@Override
	public void render(Listitem item, OverdueChargeRecovery overdueChargeRecovery, int count) throws Exception {

		if (item instanceof Listgroup) { 
			item.appendChild(new Listcell("Overdue Term : "+
					DateUtility.formatUtilDate(overdueChargeRecovery.getFinODSchdDate(),
							PennantConstants.dateFormate)+"-"+overdueChargeRecovery.getFinODFor())); 
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(10);
			item.appendChild(cell);
		} else {
			
			Listcell lc;
			lc = new Listcell(DateUtility.formatUtilDate(overdueChargeRecovery.getMovementDate(),
					PennantConstants.dateFormate));
			lc.setParent(item);
			
			lc = new Listcell(String.valueOf(overdueChargeRecovery.getODDays()));
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinCurODPri(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinCurODPft(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinCurODAmt(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getPenalty(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getWaivedAmt(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getPenaltyPaid(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getPenaltyBal(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			
			lc = new Listcell(overdueChargeRecovery.isRcdCanDel() ? "Recovery" : "Collected");
			if(overdueChargeRecovery.isRcdCanDel()){
				lc.setStyle("font-weight:bold;color:red;");
			}else{
				lc.setStyle("font-weight:bold;color:green;");
			}
			lc.setParent(item);
			
			/*item.setAttribute("data", overdueChargeRecovery);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onOverdueChargeRecoveryItemDoubleClicked");*/
		}
	}
}