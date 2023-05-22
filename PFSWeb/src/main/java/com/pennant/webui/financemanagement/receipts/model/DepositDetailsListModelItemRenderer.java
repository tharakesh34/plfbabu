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
 * * FileName : AcademicListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-05-2011 * *
 * Modified Date : 23-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.receipts.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class DepositDetailsListModelItemRenderer implements ListitemRenderer<DepositDetails>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public DepositDetailsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DepositDetails depositDetails, int count) {

		Listcell lc;

		// Deposit Type
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(depositDetails.getDepositType(),
				PennantStaticListUtil.getDepositTypesListList()));
		lc.setParent(item);
		// Branch Code
		lc = new Listcell(depositDetails.getBranchCode() + " - " + depositDetails.getBranchDesc());
		lc.setParent(item);
		// Available Amount
		lc = new Listcell(CurrencyUtil.format(depositDetails.getActualAmount(), PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		// Record Status
		lc = new Listcell(depositDetails.getRecordStatus());
		lc.setParent(item);
		// Record Type
		lc = new Listcell(PennantJavaUtil.getLabel(depositDetails.getRecordType()));
		lc.setParent(item);

		item.setAttribute("depositId", depositDetails.getDepositId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDepositDetailsItemDoubleClicked");
	}
}