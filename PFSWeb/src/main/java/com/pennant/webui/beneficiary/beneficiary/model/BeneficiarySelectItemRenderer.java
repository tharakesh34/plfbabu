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
 * * FileName : CustomerListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * *
 * Modified Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.beneficiary.beneficiary.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.beneficiary.Beneficiary;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class BeneficiarySelectItemRenderer implements ListitemRenderer<Beneficiary>, Serializable {
	private static final long serialVersionUID = 1552059797117039294L;

	public BeneficiarySelectItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, Beneficiary beneficiary, int index) {
		Listcell lc;
		lc = new Listcell(beneficiary.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getBankName());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getBranchDesc());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getCity());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getAccNumber());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getAccHolderName());
		lc.setParent(item);
		item.setAttribute("data", beneficiary);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBeneficiaryItemDoubleClicked");
	}
}