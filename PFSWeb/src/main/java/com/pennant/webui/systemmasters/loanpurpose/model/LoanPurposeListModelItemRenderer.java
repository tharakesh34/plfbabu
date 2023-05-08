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
 * * FileName : AddressTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011
 * * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.loanpurpose.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class LoanPurposeListModelItemRenderer implements ListitemRenderer<LoanPurpose>, Serializable {

	private static final long serialVersionUID = 6352065299727172054L;

	public LoanPurposeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, LoanPurpose loanPurpose, int count) {

		Listcell lc;
		lc = new Listcell(loanPurpose.getLoanPurposeCode());
		lc.setParent(item);
		lc = new Listcell(loanPurpose.getLoanPurposeDesc());
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbAddrTypeIsActive = new Checkbox();
		cbAddrTypeIsActive.setDisabled(true);
		cbAddrTypeIsActive.setChecked(loanPurpose.isLoanPurposeIsActive());
		lc.appendChild(cbAddrTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(loanPurpose.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(loanPurpose.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", loanPurpose.getLoanPurposeCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanPurposeItemDoubleClicked");
	}
}