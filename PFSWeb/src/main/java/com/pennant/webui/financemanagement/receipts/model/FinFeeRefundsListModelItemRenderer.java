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
 * * FileName : FinFeeRefundsListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 18-12-2019 * * Modified Date : 18-12-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-12-2019 Ganesh.P 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.receipts.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 */
public class FinFeeRefundsListModelItemRenderer implements ListitemRenderer<FinFeeRefundHeader>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public FinFeeRefundsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinFeeRefundHeader finFeeRefund, int count) {

		Listcell lc;

		lc = new Listcell(finFeeRefund.getFinReference());
		lc.setParent(item);
		lc = new Listcell(finFeeRefund.getFinType());
		lc.setParent(item);
		lc = new Listcell(finFeeRefund.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(finFeeRefund.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(finFeeRefund.getLovDescCustShrtName());
		lc.setParent(item);
		lc = new Listcell(finFeeRefund.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finFeeRefund.getRecordType()));
		lc.setParent(item);

		item.setAttribute("headerID", finFeeRefund.getHeaderId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinFeeRefundItemDoubleClicked");

	}
}