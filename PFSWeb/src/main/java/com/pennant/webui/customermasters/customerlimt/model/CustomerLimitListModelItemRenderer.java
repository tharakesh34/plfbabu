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

package com.pennant.webui.customermasters.customerlimt.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerLimit;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CustomerLimitListModelItemRenderer implements ListitemRenderer<CustomerLimit>, Serializable {

	private static final long serialVersionUID = 2274326782681085785L;

	public CustomerLimitListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerLimit customerLimit, int count) {

		Listcell lc;
		lc = new Listcell(customerLimit.getCustCIF().trim());
		lc.setParent(item);
		lc = new Listcell(customerLimit.getCustShortName());
		lc.setParent(item);
		lc = new Listcell(customerLimit.getLimitCategory());
		lc.setParent(item);
		lc = new Listcell(customerLimit.getBranch());
		lc.setParent(item);
		lc = new Listcell(customerLimit.getCurrency());
		lc.setParent(item);
		item.setAttribute("data", customerLimit);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerItemDoubleClicked");
	}
}