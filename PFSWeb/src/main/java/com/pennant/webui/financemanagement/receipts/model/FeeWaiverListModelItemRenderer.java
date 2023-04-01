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

package com.pennant.webui.financemanagement.receipts.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class FeeWaiverListModelItemRenderer implements ListitemRenderer<FeeWaiverHeader>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public FeeWaiverListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FeeWaiverHeader feeWaiverHeader, int count) {

		Listcell lc;

		lc = new Listcell(feeWaiverHeader.getFinReference());
		lc.setParent(item);

		lc = new Listcell(feeWaiverHeader.getEvent());
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(feeWaiverHeader.getPostingDate()));
		lc.setParent(item);

		// Record Status
		lc = new Listcell(feeWaiverHeader.getRecordStatus());
		lc.setParent(item);

		// Record Type
		lc = new Listcell(PennantJavaUtil.getLabel(feeWaiverHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("waiverId", feeWaiverHeader.getWaiverId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFeeWaiverItemDoubleClicked");
	}
}