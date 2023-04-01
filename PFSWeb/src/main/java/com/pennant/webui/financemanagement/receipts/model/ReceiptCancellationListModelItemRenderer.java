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

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * Item renderer for listItems in the listBox.
 */
public class ReceiptCancellationListModelItemRenderer implements ListitemRenderer<FinReceiptHeader>, Serializable {

	private static final long serialVersionUID = 3736186724610414895L;

	public ReceiptCancellationListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FinReceiptHeader header, int count) {

		Listcell lc;

		String extReference = header.getExtReference();
		if (StringUtils.isNotEmpty(extReference)) {
			lc = new Listcell(extReference);
		} else {
			lc = new Listcell(header.getReference());
		}

		lc.setParent(item);
		lc = new Listcell(header.getPromotionCode());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(header.getReceiptPurpose(),
				PennantStaticListUtil.getReceiptPurpose()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(header.getReceiptMode(),
				PennantStaticListUtil.getReceiptModesByFeePayment()));
		lc.setParent(item);
		lc = new Listcell(header.getTransactionRef());
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(header.getReceiptDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(header.getReceiptAmount(),
				CurrencyUtil.getFormat(header.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(header.getAllocationType(),
				PennantStaticListUtil.getAllocationMethods()));
		lc.setParent(item);
		lc = new Listcell(header.getFinType());
		lc.setParent(item);
		lc = new Listcell(header.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(header.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(header.getReceiptDate()));
		lc.setParent(item);
		lc = new Listcell(header.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(StringUtils.trimToEmpty(header.getExtReference()));
		lc.setParent(item);
		lc = new Listcell(header.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(header.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", header);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onReceiptCancellationItemDoubleClicked");
	}
}