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
 * * FileName : FinSuspHoldListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011
 * * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.finsusphold.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class FinSuspHoldListModelItemRenderer implements ListitemRenderer<FinSuspHold>, Serializable {

	private static final long serialVersionUID = -6336194516320385692L;

	public FinSuspHoldListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinSuspHold finSuspHold, int count) {

		Listcell lc;
		lc = new Listcell(finSuspHold.getProduct());
		lc.setParent(item);
		lc = new Listcell(finSuspHold.getFinType());
		lc.setParent(item);
		lc = new Listcell(finSuspHold.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(finSuspHold.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(finSuspHold.getFinReference());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbFinAppIsActive = new Checkbox();
		cbFinAppIsActive.setDisabled(true);
		cbFinAppIsActive.setChecked(finSuspHold.isActive());
		lc.appendChild(cbFinAppIsActive);
		lc.setParent(item);
		lc = new Listcell(finSuspHold.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finSuspHold.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", finSuspHold.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinSuspHoldItemDoubleClicked");
	}
}