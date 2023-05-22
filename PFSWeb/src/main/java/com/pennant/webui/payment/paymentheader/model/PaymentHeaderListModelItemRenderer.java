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
 * * FileName : PaymentHeaderListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 27-05-2017 * * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.payment.paymentheader.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class PaymentHeaderListModelItemRenderer implements ListitemRenderer<PaymentHeader>, Serializable {

	private static final long serialVersionUID = 1L;

	public PaymentHeaderListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, PaymentHeader paymentHeader, int count) {

		Listcell lc;
		lc = new Listcell(paymentHeader.getFinReference());
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.getLabelDesc(paymentHeader.getPaymentInstrType(),
				PennantStaticListUtil.getPaymentTypesWithIST()));
		lc.setParent(item);

		lc = new Listcell(DateUtil.format(paymentHeader.getApprovedOn(), PennantConstants.dateFormat));
		lc.setParent(item);

		lc = new Listcell(paymentHeader.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(paymentHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("paymentId", paymentHeader.getPaymentId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPaymentHeaderItemDoubleClicked");
	}
}