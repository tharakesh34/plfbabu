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
 * FileName    		:  WIFFinanceMainListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.finance.wiffinancemain.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class WIFFinanceMainListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = 1L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceMain wIFFinanceMain, int count) throws Exception {

		//final FinanceMain wIFFinanceMain = (FinanceMain) data;
		Listcell lc;
	  	lc = new Listcell(wIFFinanceMain.getFinReference());
		lc.setParent(item);
		if(wIFFinanceMain.getLovDescCustCIF()!=null){
			lc = new Listcell(wIFFinanceMain.getLovDescCustCIF());
		}else{
			lc = new Listcell();
		}
		lc.setParent(item);
	  	lc = new Listcell(wIFFinanceMain.getFinType());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(wIFFinanceMain.getFinAmount(),
				wIFFinanceMain.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
	  	lc = new Listcell(wIFFinanceMain.getFinCcy());
		lc.setParent(item);
	  	lc = new Listcell(wIFFinanceMain.getScheduleMethod());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(wIFFinanceMain.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(wIFFinanceMain.getFinStartDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		if(wIFFinanceMain.getGrcPeriodEndDate()!=null){
			lc = new Listcell(DateUtility.formatUtilDate(wIFFinanceMain.getGrcPeriodEndDate(),
					PennantConstants.dateFormate));
		}else {
			lc = new Listcell();
		}
		lc.setParent(item);
		if(wIFFinanceMain.getMaturityDate()!=null){
			lc = new Listcell(DateUtility.formatUtilDate(wIFFinanceMain.getMaturityDate(),
					PennantConstants.dateFormate));
		}else {
			lc = new Listcell();
		}
		lc.setParent(item);
		if(!StringUtils.trimToEmpty(wIFFinanceMain.getRecordStatus()).equals("")){
			lc = new Listcell(wIFFinanceMain.getRecordStatus());
		}else{
			lc = new Listcell();
		}
		lc.setParent(item);
		
		item.setAttribute("data", wIFFinanceMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onWIFFinanceMainItemDoubleClicked");
	}
}