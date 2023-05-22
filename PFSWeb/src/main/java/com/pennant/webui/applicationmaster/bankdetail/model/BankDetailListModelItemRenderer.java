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
 * * FileName : BankDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 *
 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.bankdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class BankDetailListModelItemRenderer implements ListitemRenderer<BankDetail>, Serializable {

	private static final long serialVersionUID = -6336194516320385692L;

	public BankDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, BankDetail bankDetail, int count) {

		Listcell lc;
		lc = new Listcell(bankDetail.getBankCode());
		lc.setParent(item);
		lc = new Listcell(bankDetail.getBankName());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbFinAppIsActive = new Checkbox();
		cbFinAppIsActive.setDisabled(true);
		cbFinAppIsActive.setChecked(bankDetail.isActive());
		lc.appendChild(cbFinAppIsActive);
		lc.setParent(item);
		lc = new Listcell(bankDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(bankDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", bankDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBankDetailItemDoubleClicked");
	}
}