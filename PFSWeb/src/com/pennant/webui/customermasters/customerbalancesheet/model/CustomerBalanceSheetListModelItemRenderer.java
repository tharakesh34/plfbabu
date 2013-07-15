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
 * FileName    		:  CustomerBalanceSheetListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerbalancesheet.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerBalanceSheetListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = -4954735333466148555L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Object data, int count) throws Exception {

		if (item instanceof Listgroup) { 
			Object groupData = (Object) data; 
			final CustomerBalanceSheet customerBalanceSheet= (CustomerBalanceSheet)groupData;
			item.appendChild(new Listcell(String.valueOf(customerBalanceSheet.getLovDescCustCIF()))); 
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell); 
		} else { 
			final CustomerBalanceSheet customerBalanceSheet = (CustomerBalanceSheet) data;
			Listcell lc;
			lc = new Listcell(PennantAppUtil.getlabelDesc(customerBalanceSheet.getFinancialYear(),
					PennantAppUtil.getFinancialYears()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(customerBalanceSheet.getTotalAssets(),0));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(customerBalanceSheet.getTotalLiabilities(),0));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(customerBalanceSheet.getNetProfit(),0));
			lc.setParent(item);
			lc = new Listcell(customerBalanceSheet.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerBalanceSheet.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", data);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerBalanceSheetItemDoubleClicked");
		}
	}
}