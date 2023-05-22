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
 * * FileName : FinanceApplicationCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 05-05-2011 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.financeapplicationcode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class FinanceApplicationCodeListModelItemRenderer
		implements ListitemRenderer<FinanceApplicationCode>, Serializable {

	private static final long serialVersionUID = -6336194516320385692L;

	public FinanceApplicationCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinanceApplicationCode financeApplicationCode, int count) {

		Listcell lc;
		lc = new Listcell(financeApplicationCode.getFinAppType());
		lc.setParent(item);
		lc = new Listcell(financeApplicationCode.getFinAppDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbFinAppIsActive = new Checkbox();
		cbFinAppIsActive.setDisabled(true);
		cbFinAppIsActive.setChecked(financeApplicationCode.isFinAppIsActive());
		lc.appendChild(cbFinAppIsActive);
		lc.setParent(item);
		lc = new Listcell(financeApplicationCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeApplicationCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", financeApplicationCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceApplicationCodeItemDoubleClicked");
	}
}