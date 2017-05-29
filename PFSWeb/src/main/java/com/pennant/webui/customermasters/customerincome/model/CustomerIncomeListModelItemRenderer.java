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
 * FileName    		:  CustomerIncomeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerincome.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerIncomeListModelItemRenderer implements ListitemRenderer<CustomerIncome>,Serializable {

	private static final long serialVersionUID = 816239347392992946L;

	public CustomerIncomeListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, CustomerIncome customerIncome, int count) throws Exception {

		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(String.valueOf(customerIncome.getLovDescCustCIF())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(8);
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(customerIncome.getLovDescCustCIF() );
			lc.setParent(item);
			lc = new Listcell(customerIncome.getCustIncomeType() + "-"+ customerIncome.getLovDescCustIncomeTypeName());
			lc.setParent(item);
			lc = new Listcell();
			Checkbox cb = new Checkbox();
			cb.setChecked(customerIncome.isJointCust());
			cb.setParent(lc);
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), CurrencyUtil.getFormat(customerIncome.getToCcy())));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(customerIncome.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerIncome.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", customerIncome);
			ComponentsCtrl.applyForward(item,"onDoubleClick=onCustomerIncomeItemDoubleClicked");
		}
	}
}