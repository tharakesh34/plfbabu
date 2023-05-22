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
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class DepositMovementsListModelItemRenderer implements ListitemRenderer<DepositMovements>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public DepositMovementsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DepositMovements depositMovements, int count) {

		Listcell lc;

		// Deposit Type
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(depositMovements.getDepositType(),
				PennantStaticListUtil.getDepositTypesListList()));
		lc.setParent(item);

		// Deposit Branch Code
		lc = new Listcell(depositMovements.getBranchCode() + " - " + depositMovements.getBranchDesc());
		lc.setParent(item);

		// Deposit Slip Number
		lc = new Listcell(depositMovements.getDepositSlipNumber());
		lc.setParent(item);

		// Deposit Date
		lc = new Listcell(DateUtil.formatToLongDate(depositMovements.getTransactionDate()));
		lc.setParent(item);

		// Partner Bank
		lc = new Listcell(depositMovements.getPartnerBankName());
		lc.setParent(item);

		// Amount
		lc = new Listcell(CurrencyUtil.format(depositMovements.getReservedAmount(), PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// Record Status
		lc = new Listcell(depositMovements.getRecordStatus());
		lc.setParent(item);

		// Record Type
		lc = new Listcell(PennantJavaUtil.getLabel(depositMovements.getRecordType()));
		lc.setParent(item);

		item.setAttribute("movementId", depositMovements.getMovementId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDepositMovementsItemDoubleClicked");
	}
}