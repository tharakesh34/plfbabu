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
 * * FileName : CustomerAddresListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 26-05-2011 * * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customeraddres.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerAddresListModelItemRenderer implements ListitemRenderer<CustomerAddres>, Serializable {

	private static final long serialVersionUID = 1411092588536155341L;

	public CustomerAddresListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerAddres customerAddres, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(String.valueOf(customerAddres.getLovDescCustCIF())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(7);
			item.appendChild(cell);
		} else {
			Listcell lc;
			lc = new Listcell(customerAddres.getLovDescCustCIF());
			lc.setParent(item);
			lc = new Listcell(customerAddres.getCustAddrType() + "-" + customerAddres.getLovDescCustAddrTypeName());
			lc.setParent(item);
			lc = new Listcell(customerAddres.getCustAddrHNbr());
			lc.setParent(item);
			lc = new Listcell(customerAddres.getCustFlatNbr());
			lc.setParent(item);
			lc = new Listcell(customerAddres.getCustAddrStreet());
			lc.setParent(item);
			lc = new Listcell(customerAddres.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(customerAddres.getRecordType()));
			lc.setParent(item);

			item.setAttribute("id", customerAddres.getCustID());
			item.setAttribute("type", customerAddres.getCustAddrType());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddresItemDoubleClicked");
		}
	}
}