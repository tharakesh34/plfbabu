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
 * * FileName : HoldDisbursementListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 20-11-2019 * * Modified Date : 20-11-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-11-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.finchangecustomer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinChangeCustomerListModelItemRenderer implements ListitemRenderer<FinChangeCustomer>, Serializable {

	private static final long serialVersionUID = 1L;

	public FinChangeCustomerListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FinChangeCustomer finChangeCustomer, int count) {

		Listcell lc;
		lc = new Listcell(finChangeCustomer.getFinReference());
		lc.setParent(item);

		lc = new Listcell(String.valueOf(finChangeCustomer.getCustCif()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(finChangeCustomer.getJcustCif()));
		lc.setParent(item);
		lc = new Listcell(finChangeCustomer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finChangeCustomer.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", finChangeCustomer.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinChangeCustomerItemDoubleClicked");
	}
}