/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PaymentOrderIssues. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. RepaymentOrderIssueion or retransmission of
 * the materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : PaymentOrderIssueListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-08-2011 * * Modified Date : 12-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.payorderissue.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class PayOrderIssueListModelItemRenderer implements ListitemRenderer<PayOrderIssueHeader>, Serializable {

	private static final long serialVersionUID = -3304155174434504951L;

	public PayOrderIssueListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, PayOrderIssueHeader payOrderIssueHeader, int count) {

		Listcell lc;
		lc = new Listcell(payOrderIssueHeader.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(payOrderIssueHeader.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(payOrderIssueHeader.getFinReference());
		lc.setParent(item);
		lc = new Listcell(payOrderIssueHeader.getFinType());
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(payOrderIssueHeader.getTotalPOAmount(),
				CurrencyUtil.getFormat(payOrderIssueHeader.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(payOrderIssueHeader.getTotalPOCount()));
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(payOrderIssueHeader.getIssuedPOAmount(),
				CurrencyUtil.getFormat(payOrderIssueHeader.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(payOrderIssueHeader.getIssuedPOCount()));
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(payOrderIssueHeader.getpODueAmount(),
				CurrencyUtil.getFormat(payOrderIssueHeader.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(payOrderIssueHeader.getpODueCount()));
		lc.setParent(item);
		lc = new Listcell(payOrderIssueHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(payOrderIssueHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("finID", payOrderIssueHeader.getFinID());
		item.setAttribute("finRef", payOrderIssueHeader.getFinReference());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPayOrderIssueItemDoubleClicked");
	}
}