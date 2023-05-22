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
 * * FileName : CustomerListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CustomerListModelItemRenderer implements ListitemRenderer<Customer>, Serializable {

	private static final long serialVersionUID = 2274326782681085785L;

	public CustomerListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Customer customer, int count) {

		Listcell lc;
		lc = new Listcell(customer.getCustCIF().trim());
		lc.setParent(item);
		lc = new Listcell(customer.getCustCoreBank());
		lc.setParent(item);
		lc = new Listcell(customer.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(customer.getCustDftBranch());
		lc.setParent(item);
		lc = new Listcell(customer.getLovDescCustCtgCodeName());
		lc.setParent(item);
		lc = new Listcell(customer.getLovDescCustTypeCodeName());
		lc.setParent(item);
		lc = new Listcell(
				StringUtils.equals(customer.getLovDescRequestStage(), ",") ? "" : customer.getLovDescRequestStage());
		lc.setParent(item);
		lc = new Listcell(customer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customer.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customer.getCustID());
		item.setAttribute("data", customer);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerItemDoubleClicked");
	}
}