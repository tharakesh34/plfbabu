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
 * * FileName : CheckListListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-12-2011 *
 * * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.vasMovement;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.VasMovement;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VasMovementListModelItemRenderer implements ListitemRenderer<VasMovement>, Serializable {

	private static final long serialVersionUID = 1L;

	public VasMovementListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, VasMovement vasMoment, int count) {

		Listcell lc;
		lc = new Listcell(vasMoment.getFinType());
		lc.setParent(item);

		lc = new Listcell(vasMoment.getCustCif());
		lc.setParent(item);
		lc = new Listcell(vasMoment.getFinReference());
		lc.setParent(item);
		lc = new Listcell(vasMoment.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(vasMoment.getFinStartdate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(vasMoment.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(vasMoment.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(vasMoment.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(vasMoment.getFinAmount(), CurrencyUtil.getFormat(vasMoment.getFinCcy())));

		lc.setParent(item);
		lc = new Listcell(vasMoment.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vasMoment.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", vasMoment.getFinReference());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onVasMovementItemDoubleClicked");
	}
}