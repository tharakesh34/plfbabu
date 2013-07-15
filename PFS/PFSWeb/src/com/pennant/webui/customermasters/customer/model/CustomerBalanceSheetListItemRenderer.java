/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Zksample2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerBalanceSheetListItemRenderer implements ListitemRenderer<CustomerBalanceSheet>, Serializable {

	private static final long serialVersionUID = 2926498156293698787L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CustomerBalanceSheet balanceSheet, int count) throws Exception {

		//final CustomerBalanceSheet balanceSheet = (CustomerBalanceSheet) data;
		Listcell lc;
		lc = new Listcell(balanceSheet.getFinancialYear());
		lc.setParent(item);
		lc = new Listcell(balanceSheet.getTotalAssets().toString());
		lc.setParent(item);
		lc = new Listcell(balanceSheet.getTotalLiabilities().toString());
		lc.setParent(item);
		lc = new Listcell(balanceSheet.getNetProfit().toString());
		lc.setParent(item);
		lc = new Listcell(balanceSheet.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(balanceSheet.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", balanceSheet);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerBalanceSheetItemDoubleClicked");
	}	
}