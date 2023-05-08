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
 * * FileName : ExpenseTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-09-2011
 * * * Modified Date : 29-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.amtmasters.expensetype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ExpenseTypeListModelItemRenderer implements ListitemRenderer<ExpenseType>, Serializable {

	private static final long serialVersionUID = 1L;

	public ExpenseTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ExpenseType expenseType, int count) {

		Listcell lc;
		lc = new Listcell(expenseType.getExpenseTypeCode());
		lc.setParent(item);
		lc = new Listcell(expenseType.getExpenseTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		Checkbox check = new Checkbox();
		check.setDisabled(true);
		check.setChecked(expenseType.isActive());
		lc.appendChild(check);
		lc.setParent(item);
		lc = new Listcell(expenseType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(expenseType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", expenseType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onExpenseTypeItemDoubleClicked");
	}
}