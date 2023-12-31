/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerBalanceSheetListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 07-12-2011 * * Modified Date : 07-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-12-2011 Pennant 0.1 * * * * * * * * *
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

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerBalanceSheetListModelItemRenderer implements ListitemRenderer<CustomerBalanceSheet>, Serializable {

	private static final long serialVersionUID = -4954735333466148555L;

	public CustomerBalanceSheetListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerBalanceSheet data, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(String.valueOf(data.getLovDescCustCIF())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell);
		} else {
			Listcell lc;
			lc = new Listcell("");
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(data.getTotalAssets(), 0));
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(data.getTotalLiabilities(), 0));
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(data.getNetProfit(), 0));
			lc.setParent(item);
			lc = new Listcell(data.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(data.getRecordType()));
			lc.setParent(item);

			item.setAttribute("id", data.getId());
			item.setAttribute("custId", data.getCustId());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerBalanceSheetItemDoubleClicked");
		}
	}
}