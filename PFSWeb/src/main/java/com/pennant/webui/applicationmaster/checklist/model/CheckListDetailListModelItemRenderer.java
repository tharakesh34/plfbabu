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
 * * FileName : CheckListDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-12-2011 * * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.checklist.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CheckListDetailListModelItemRenderer implements ListitemRenderer<CheckListDetail>, Serializable {

	private static final long serialVersionUID = -7502706317125873983L;

	public CheckListDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CheckListDetail checkListDetail, int count) {

		Listcell lc;

		lc = new Listcell(checkListDetail.getAnsDesc());
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbMandatory = new Checkbox();
		cbMandatory.setDisabled(true);
		cbMandatory.setChecked(checkListDetail.isRemarksAllow());
		lc.appendChild(cbMandatory);
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox docMandatory = new Checkbox();
		docMandatory.setDisabled(true);
		docMandatory.setChecked(checkListDetail.isDocRequired());
		lc.appendChild(docMandatory);
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(checkListDetail.isRemarksMand());
		lc.appendChild(cbActive);
		lc.setParent(item);

		lc = new Listcell(checkListDetail.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(checkListDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", checkListDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCheckListDetailItemDoubleClicked");
	}
}