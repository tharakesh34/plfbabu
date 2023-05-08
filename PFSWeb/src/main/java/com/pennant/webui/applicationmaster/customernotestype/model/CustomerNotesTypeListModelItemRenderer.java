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
 * * FileName : CustomerNotesTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 05-05-2011 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.customernotestype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class CustomerNotesTypeListModelItemRenderer implements ListitemRenderer<CustomerNotesType>, Serializable {

	private static final long serialVersionUID = -228283421898600121L;

	public CustomerNotesTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerNotesType customerNotesType, int count) {

		Listcell lc;
		lc = new Listcell(customerNotesType.getCustNotesTypeCode());
		lc.setParent(item);
		lc = new Listcell(customerNotesType.getCustNotesTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbCustNotesTypeIsPerminent = new Checkbox();
		cbCustNotesTypeIsPerminent.setDisabled(true);
		cbCustNotesTypeIsPerminent.setChecked(customerNotesType.isCustNotesTypeIsPerminent());
		lc.appendChild(cbCustNotesTypeIsPerminent);
		lc.setParent(item);
		lc = new Listcell(customerNotesType.getCustNotesTypeArchiveFrq());
		lc.setParent(item);
		lc = new Listcell(customerNotesType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerNotesType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerNotesType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerNotesTypeItemDoubleClicked");
	}
}