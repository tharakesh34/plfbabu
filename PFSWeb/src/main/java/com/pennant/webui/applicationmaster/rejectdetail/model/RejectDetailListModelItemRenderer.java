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
 * * FileName : RejectDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011
 * * * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.rejectdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class RejectDetailListModelItemRenderer implements ListitemRenderer<RejectDetail>, Serializable {

	private static final long serialVersionUID = 9099171990501035267L;

	public RejectDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, RejectDetail rejectDetail, int count) {

		Listcell lc;
		lc = new Listcell(rejectDetail.getRejectCode());
		lc.setParent(item);
		lc = new Listcell(rejectDetail.getRejectDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbRejectIsActive = new Checkbox();
		cbRejectIsActive.setDisabled(true);
		cbRejectIsActive.setChecked(rejectDetail.isRejectIsActive());
		lc.appendChild(cbRejectIsActive);
		lc.setParent(item);
		lc = new Listcell(rejectDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(rejectDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", rejectDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onRejectDetailItemDoubleClicked");
	}
}