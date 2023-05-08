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
 * * FileName : BranchListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.branch.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class BranchListModelItemRenderer implements ListitemRenderer<Branch>, Serializable {

	private static final long serialVersionUID = -4870847307910889683L;

	public BranchListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Branch branch, int count) {

		Listcell lc;
		lc = new Listcell(branch.getBranchCode());
		lc.setParent(item);
		lc = new Listcell(branch.getBranchDesc());
		lc.setParent(item);
		lc = new Listcell(branch.getBranchCountry() + "-" + branch.getLovDescBranchCountryName());
		lc.setParent(item);
		lc = new Listcell(branch.getBranchProvince() + "-" + branch.getLovDescBranchProvinceName());
		lc.setParent(item);
		lc = new Listcell(StringUtils.trimToEmpty(branch.getBranchCity()) + "-"
				+ StringUtils.trimToEmpty(branch.getLovDescBranchCityName()));
		lc.setParent(item);
		lc = new Listcell(branch.getBranchSwiftBrnCde());
		lc.setParent(item);
		lc = new Listcell(branch.getPinAreaDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbBranchIsActive = new Checkbox();
		cbBranchIsActive.setDisabled(true);
		cbBranchIsActive.setChecked(branch.isBranchIsActive());
		lc.appendChild(cbBranchIsActive);
		lc.setParent(item);
		lc = new Listcell(branch.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(branch.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", branch.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBranchItemDoubleClicked");
	}
}