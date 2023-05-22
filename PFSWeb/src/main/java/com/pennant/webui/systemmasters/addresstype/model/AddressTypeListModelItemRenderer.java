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
 * * FileName : AddressTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011
 * * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.addresstype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class AddressTypeListModelItemRenderer implements ListitemRenderer<AddressType>, Serializable {

	private static final long serialVersionUID = 6352065299727172054L;

	public AddressTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, AddressType addressType, int count) {

		Listcell lc;
		lc = new Listcell(addressType.getAddrTypeCode());
		lc.setParent(item);
		lc = new Listcell(addressType.getAddrTypeDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(addressType.getAddrTypePriority()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox fiRequired = new Checkbox();
		fiRequired.setDisabled(true);
		fiRequired.setChecked(addressType.isAddrTypeFIRequired());
		lc.appendChild(fiRequired);
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbAddrTypeIsActive = new Checkbox();
		cbAddrTypeIsActive.setDisabled(true);
		cbAddrTypeIsActive.setChecked(addressType.isAddrTypeIsActive());
		lc.appendChild(cbAddrTypeIsActive);
		lc.setParent(item);

		lc = new Listcell(addressType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(addressType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", addressType.getAddrTypeCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAddressTypeItemDoubleClicked");
	}
}