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
 * * FileName : ProvisionListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 *
 * * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.suspense.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CustSuspenseListModelItemRenderer implements ListitemRenderer<Customer>, Serializable {

	private static final long serialVersionUID = -4554647022945989420L;

	public CustSuspenseListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, Customer customer, int count) {

		// final FinanceSuspHead suspHead = (FinanceSuspHead) data;
		Listcell lc;
		lc = new Listcell(String.valueOf(customer.getCustCIF()));
		lc.setParent(item);
		lc = new Listcell(customer.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(customer.getCustDftBranch());
		lc.setParent(item);
		lc = new Listcell(customer.getCustSts() + "-" + customer.getLovDescCustStsName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(customer.getCustStsChgDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(customer.getCustSuspDate()));
		lc.setParent(item);
		lc = new Listcell(customer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customer.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", customer.getCustID());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustSuspenseItemDoubleClicked");
	}
}