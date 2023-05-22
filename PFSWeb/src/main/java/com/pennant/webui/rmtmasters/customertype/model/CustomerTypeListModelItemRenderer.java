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
 * * FileName : CustomerTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011
 * * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.customertype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CustomerTypeListModelItemRenderer implements ListitemRenderer<CustomerType>, Serializable {

	private static final long serialVersionUID = 4481477716896911264L;

	public CustomerTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerType customerType, int count) {

		Listcell lc;
		lc = new Listcell(customerType.getCustTypeCode());
		lc.setParent(item);
		lc = new Listcell(customerType.getCustTypeDesc());
		lc.setParent(item);
		/*
		 * if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, customerType.getCustTypeCtg())) { lc = new
		 * Listcell(Labels.getLabel("label_Corporate")); } else if
		 * (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, customerType.getCustTypeCtg())) { lc = new
		 * Listcell(Labels.getLabel("label_Individual")); } else if
		 * (StringUtils.equals(PennantConstants.PFF_CUSTCTG_SME, customerType.getCustTypeCtg())) { lc = new
		 * Listcell(Labels.getLabel("label_Financial")); } else { lc = new Listcell(""); }
		 */
		lc = new Listcell(customerType.getCustctgdesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCustTypeIsActive = new Checkbox();
		cbCustTypeIsActive.setDisabled(true);
		cbCustTypeIsActive.setChecked(customerType.isCustTypeIsActive());
		lc.appendChild(cbCustTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(customerType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerTypeItemDoubleClicked");
	}
}