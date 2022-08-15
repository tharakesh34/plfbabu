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
 * * FileName : CustomerPaymentTxnsListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 19-12-2017 * * Modified Date : 19-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.customerPaymentTransactions.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.PaymentTransaction;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CustomerPaymentTxnsListModelItemRenderer implements ListitemRenderer<PaymentTransaction>, Serializable {

	private static final long serialVersionUID = 1L;

	public CustomerPaymentTxnsListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, PaymentTransaction transaction, int count) {

		Listcell lc;

		lc = new Listcell(transaction.getFinReference());
		lc.setParent(item);

		String tranModule = "";
		if ("DISB".equals(transaction.getTranModule())) {
			tranModule = "Disbursement";
		} else if ("PYMT".equals(transaction.getTranModule())) {
			tranModule = "Payments";
		}

		lc = new Listcell(tranModule);
		lc.setParent(item);

		lc = new Listcell(String.valueOf(transaction.getPaymentId()));
		lc.setParent(item);

		/*
		 * lc = new Listcell(transaction.getTranStatus()); lc.setParent(item);
		 */
		item.setAttribute("paymentTransaction", transaction);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPaymentTxnsItemDoubleClicked");
	}
}