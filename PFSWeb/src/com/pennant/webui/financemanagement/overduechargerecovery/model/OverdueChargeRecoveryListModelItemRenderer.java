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

import org.zkoss.zk.ui.sys.ComponentsCtrl;
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
public class OverdueChargeRecoveryListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Object data, int count) throws Exception {

		if (item instanceof Listgroup) { 
			Object groupData = (Object) data; 
			final OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) groupData;
			item.appendChild(new Listcell(String.valueOf(overdueChargeRecovery.getFinReference()))); 
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(7);
			item.appendChild(cell);
		} else {
			final OverdueChargeRecovery overdueChargeRecovery = (OverdueChargeRecovery) data;
			Listcell lc;
			lc = new Listcell(DateUtility.formatUtilDate(overdueChargeRecovery.getFinSchdDate(),
							PennantConstants.dateFormate));
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatUtilDate(overdueChargeRecovery.getFinODDate(),
					PennantConstants.dateFormate));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODPri(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODPft(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODTot(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODCPenalty(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODCWaived(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODCPLPenalty(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(overdueChargeRecovery.getFinODCCPenalty(),
					overdueChargeRecovery.getLovDescFinFormatter()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(overdueChargeRecovery.getFinODCRecoverySts());
			lc.setParent(item);
			/*lc = new Listcell(overdueChargeRecovery.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(overdueChargeRecovery.getRecordType()));
			lc.setParent(item);*/
			item.setAttribute("data", data);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onOverdueChargeRecoveryItemDoubleClicked");
		}
	}
}