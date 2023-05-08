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
 * * FileName : PartnerBankListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-03-2017
 * * * Modified Date : 09-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-03-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.partnerbank.partnerbank.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class PartnerBankListModelItemRenderer implements ListitemRenderer<PartnerBank>, Serializable {

	private static final long serialVersionUID = 1L;

	public PartnerBankListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, PartnerBank partnerBank, int count) {

		Listcell lc;
		lc = new Listcell(String.valueOf(partnerBank.getPartnerBankId()));
		lc.setParent(item);
		lc = new Listcell(partnerBank.getPartnerBankCode());
		lc.setParent(item);
		lc = new Listcell(partnerBank.getPartnerBankName());
		lc.setParent(item);
		lc = new Listcell(partnerBank.getBankCode());
		lc.setParent(item);
		lc = new Listcell(partnerBank.getBankBranchCode());
		lc.setParent(item);
		lc = new Listcell(partnerBank.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(partnerBank.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", partnerBank.getPartnerBankId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPartnerBankItemDoubleClicked");
	}
}