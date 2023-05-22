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
 * * FileName : EmploymentTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 03-05-2011 * * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.employmenttype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class EmploymentTypeListModelItemRenderer implements ListitemRenderer<EmploymentType>, Serializable {

	private static final long serialVersionUID = -900657208463120770L;

	public EmploymentTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, EmploymentType employmentType, int count) {

		Listcell lc;
		lc = new Listcell(employmentType.getEmpType());
		lc.setParent(item);
		lc = new Listcell(employmentType.getEmpTypeDesc());
		lc.setParent(item);
		lc = new Listcell(employmentType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(employmentType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", employmentType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onEmploymentTypeItemDoubleClicked");
	}
}