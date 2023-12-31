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
 * * FileName : HoldDisbursementListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 09-10-2018 * * Modified Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.holddisbursement.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class HoldDisbursementListModelItemRenderer implements ListitemRenderer<HoldDisbursement>, Serializable {

	private static final long serialVersionUID = 1L;

	public HoldDisbursementListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, HoldDisbursement holdDisbursement, int count) {

		Listcell lc;
		lc = new Listcell(holdDisbursement.getFinReference());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbHold = new Checkbox();
		cbHold.setDisabled(true);
		cbHold.setChecked(holdDisbursement.isHold());
		lc.appendChild(cbHold);
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(holdDisbursement.getTotalLoanAmt(), PennantConstants.defaultCCYDecPos));
		lc.setParent(item);
		lc = new Listcell(
				CurrencyUtil.format(holdDisbursement.getDisbursedAmount(), PennantConstants.defaultCCYDecPos));
		lc.setParent(item);
		lc = new Listcell(
				CurrencyUtil.format(holdDisbursement.getHoldLimitAmount(), PennantConstants.defaultCCYDecPos));
		lc.setParent(item);
		lc = new Listcell(holdDisbursement.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(holdDisbursement.getRecordType()));
		lc.setParent(item);

		item.setAttribute("finID", holdDisbursement.getFinID());
		item.setAttribute("finReference", holdDisbursement.getFinReference());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onHoldDisbursementItemDoubleClicked");
	}
}