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
 * * FileName : AcademicListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-05-2011 * *
 * Modified Date : 23-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.academic.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class AcademicListModelItemRenderer implements ListitemRenderer<Academic>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public AcademicListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Academic academic, int count) {

		Listcell lc;
		lc = new Listcell(academic.getAcademicLevel());
		lc.setParent(item);
		lc = new Listcell(academic.getAcademicDecipline());
		lc.setParent(item);
		lc = new Listcell(academic.getAcademicDesc());
		lc.setParent(item);
		lc = new Listcell(academic.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(academic.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", academic.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAcademicItemDoubleClicked");
	}
}