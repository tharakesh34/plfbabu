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
 * * FileName : BeneficiaryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2016
 * * * Modified Date : 01-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.beneficiary.beneficiary.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BeneficiaryListModelItemRenderer implements ListitemRenderer<Beneficiary>, Serializable {
	private static final long serialVersionUID = 1L;

	public BeneficiaryListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Beneficiary beneficiary, int count) {

		Listcell lc;
		lc = new Listcell(StringUtils.trimToEmpty(String.valueOf(beneficiary.getCustCIF())));
		lc.setParent(item);
		lc = new Listcell(beneficiary.getAccNumber());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getAccHolderName());
		lc.setParent(item);
		lc = new Listcell(beneficiary.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(beneficiary.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", beneficiary.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBeneficiaryItemDoubleClicked");
	}
}