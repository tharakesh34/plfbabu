/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Zksample2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Zksample2. If not, see
 * <http://www.gnu.org/licenses/gpl.html>.
 */
package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerIncomeListItemRenderer implements ListitemRenderer<CustomerIncome>, Serializable {

	private static final long serialVersionUID = 6321996138703133595L;

	public CustomerIncomeListItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerIncome income, int count) {

		int format = CurrencyUtil.getFormat(income.getToCcy());
		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(income.getIncomeExpense(),
				PennantStaticListUtil.getIncomeExpense()));
		lc.setParent(item);
		lc = new Listcell(
				PennantApplicationUtil.getLabelDesc(income.getCategory(), PennantAppUtil.getIncomeExpenseCategory()));
		lc.setParent(item);
		if (income.getRecordType().equals(PennantConstants.RCD_ADD)
				|| income.getRecordType().equals(PennantConstants.RCD_UPD)) {

			lc = new Listcell(income.getIncomeTypeDesc());
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(income.getIncome(), format));
			lc.setParent(item);
		} else {
			lc = new Listcell(income.getIncomeType());
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(income.getIncome(), format));
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