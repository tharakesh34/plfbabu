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
 * * FileName : IncomeTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 *
 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.interfacemapping.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class InterfaceMappingListModelItemRenderer implements ListitemRenderer<InterfaceMapping>, Serializable {

	private static final long serialVersionUID = -385475759409018331L;

	public InterfaceMappingListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, InterfaceMapping interfaceMapping, int count) {

		Listcell lc;
		lc = new Listcell(interfaceMapping.getInterfaceName());
		lc.setParent(item);
		lc = new Listcell(interfaceMapping.getInterfaceField());
		lc.setParent(item);
		lc = new Listcell(interfaceMapping.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(interfaceMapping.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", interfaceMapping.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInterfaceMappingItemDoubleClicked");
	}
}