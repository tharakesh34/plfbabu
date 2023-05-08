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
 * * FileName : OtherBankFinanceTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 03-04-2015 * * Modified Date : 03-04-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-04-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.otherbankfinancetype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class OtherBankFinanceTypeListModelItemRenderer implements ListitemRenderer<OtherBankFinanceType>, Serializable {

	private static final long serialVersionUID = 1L;

	public OtherBankFinanceTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, OtherBankFinanceType data, int index) {

		final OtherBankFinanceType otherBankFinanceType = (OtherBankFinanceType) data;
		Listcell lc;
		lc = new Listcell(otherBankFinanceType.getFinType());
		lc.setParent(item);
		lc = new Listcell(otherBankFinanceType.getFinTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(otherBankFinanceType.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
		lc = new Listcell(otherBankFinanceType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(otherBankFinanceType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", data.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onOtherBankFinanceTypeItemDoubleClicked");
	}
}