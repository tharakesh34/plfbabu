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
 * * FileName : EmpStsCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 *
 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.empstscode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class EmpStsCodeListModelItemRenderer implements ListitemRenderer<EmpStsCode>, Serializable {

	private static final long serialVersionUID = 6423359151100839127L;

	public EmpStsCodeListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, EmpStsCode empStsCode, int count) {

		Listcell lc;
		lc = new Listcell(empStsCode.getEmpStsCode());
		lc.setParent(item);
		lc = new Listcell(empStsCode.getEmpStsDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbEmpStsIsActive = new Checkbox();
		cbEmpStsIsActive.setDisabled(true);
		cbEmpStsIsActive.setChecked(empStsCode.isEmpStsIsActive());
		lc.appendChild(cbEmpStsIsActive);
		lc.setParent(item);
		lc = new Listcell(empStsCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(empStsCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", empStsCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onEmpStsCodeItemDoubleClicked");
	}
}