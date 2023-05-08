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
 * * FileName : SalesOfficerListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2011
 * * * Modified Date : 12-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.salesofficer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SalesOfficerListModelItemRenderer implements ListitemRenderer<SalesOfficer>, Serializable {

	private static final long serialVersionUID = 601020221986538839L;

	public SalesOfficerListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SalesOfficer salesOfficer, int count) {

		Listcell lc;
		lc = new Listcell(salesOfficer.getSalesOffCode());
		lc.setParent(item);
		lc = new Listcell(salesOfficer.getSalesOffFName());
		lc.setParent(item);
		lc = new Listcell(salesOfficer.getSalesOffDept() + "-" + salesOfficer.getLovDescSalesOffDeptName());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbSalesOffIsActive = new Checkbox();
		cbSalesOffIsActive.setDisabled(true);
		cbSalesOffIsActive.setChecked(salesOfficer.isSalesOffIsActive());
		lc.appendChild(cbSalesOffIsActive);
		lc.setParent(item);
		lc = new Listcell(salesOfficer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(salesOfficer.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", salesOfficer.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSalesOfficerItemDoubleClicked");
	}
}