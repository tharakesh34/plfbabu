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
 * * FileName : DepartmentListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 *
 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.department.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class DepartmentListModelItemRenderer implements ListitemRenderer<Department>, Serializable {

	private static final long serialVersionUID = -6009133907143378134L;

	public DepartmentListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Department department, int count) {

		Listcell lc;
		lc = new Listcell(department.getDeptCode());
		lc.setParent(item);
		lc = new Listcell(department.getDeptDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbDeptIsActive = new Checkbox();
		cbDeptIsActive.setDisabled(true);
		cbDeptIsActive.setChecked(department.isDeptIsActive());
		lc.appendChild(cbDeptIsActive);
		lc.setParent(item);
		lc = new Listcell(department.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(department.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", department.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDepartmentItemDoubleClicked");
	}
}