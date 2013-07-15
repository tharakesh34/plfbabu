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

import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerIncomeListItemRenderer implements ListitemRenderer<CustomerIncome>, Serializable {

	private static final long serialVersionUID = 6321996138703133595L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CustomerIncome income, int count) throws Exception {

		//final CustomerIncome income = (CustomerIncome) data;
		Listcell lc;
		if(income.getRecordType().equals(PennantConstants.RCD_ADD) 
				|| income.getRecordType().equals(PennantConstants.RCD_UPD)){
			
			lc = new Listcell(income.getLovDescCustIncomeTypeName());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(income.getCustIncome(),income.getLovDescCcyEditField()));
			lc.setParent(item);
			lc = new Listcell(income.getLovDescCustIncomeCountryName());
			lc.setParent(item);
		}else{
			lc = new Listcell(income.getCustIncomeType()+"-"+income.getLovDescCustIncomeTypeName());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(income.getCustIncome(),income.getLovDescCcyEditField()));
			lc.setParent(item);
			lc = new Listcell(income.getCustIncomeCountry()+"-"+income.getLovDescCustIncomeCountryName());
			lc.setParent(item);
		}
		lc = new Listcell(income.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(income.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", income);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
	}	
}