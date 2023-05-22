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
 * * FileName : FinanceRepayPriorityListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 16-03-2012 * * Modified Date : 16-03-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-03-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financerepaypriority.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinanceRepayPriorityListModelItemRenderer implements ListitemRenderer<FinanceRepayPriority>, Serializable {

	private static final long serialVersionUID = 1L;

	public FinanceRepayPriorityListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinanceRepayPriority financeRepayPriority, int count) {

		Listcell lc;
		lc = new Listcell(financeRepayPriority.getFinType() + "-" + financeRepayPriority.getLovDescFinTypeName());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(financeRepayPriority.getFinPriority()));
		lc.setParent(item);
		lc = new Listcell(financeRepayPriority.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeRepayPriority.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", financeRepayPriority.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceRepayPriorityItemDoubleClicked");
	}
}