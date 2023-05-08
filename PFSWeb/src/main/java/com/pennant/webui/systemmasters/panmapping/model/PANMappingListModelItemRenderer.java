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
 * * FileName : PANMappingListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-05-2011 *
 * * Modified Date : 23-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.panmapping.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class PANMappingListModelItemRenderer implements ListitemRenderer<CustTypePANMapping>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public PANMappingListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustTypePANMapping custTypePANMapping, int count) {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(custTypePANMapping.getCustCategory(),
				PennantAppUtil.getcustCtgCodeList()));
		lc.setParent(item);
		lc = new Listcell(custTypePANMapping.getCustTypeDesc());
		lc.setParent(item);
		lc = new Listcell(custTypePANMapping.getPanLetter());
		lc.setParent(item);
		lc = new Listcell();
		// Active
		Checkbox active = new Checkbox();
		active.setDisabled(true);
		active.setChecked(custTypePANMapping.isActive());
		lc.appendChild(active);
		lc.setParent(item);

		lc = new Listcell(custTypePANMapping.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(custTypePANMapping.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", custTypePANMapping.getMappingID());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPANMappingItemDoubleClicked");
	}
}