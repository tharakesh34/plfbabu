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
 * * FileName : BaseRateCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011
 * * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.costoffundcode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.CostOfFundCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CostOfFundCodeListModelItemRenderer implements ListitemRenderer<CostOfFundCode>, Serializable {

	private static final long serialVersionUID = -8176701674841176336L;

	public CostOfFundCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CostOfFundCode costOfFundCode, int count) {

		Listcell lc;
		lc = new Listcell(costOfFundCode.getCofCode());
		lc.setParent(item);
		lc = new Listcell(costOfFundCode.getCofDesc());
		lc.setParent(item);
		lc = new Listcell(costOfFundCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(costOfFundCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", costOfFundCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCostOfFundCodeItemDoubleClicked");
	}
}