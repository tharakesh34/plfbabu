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
 * * FileName : ReceiptDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 14-07-2018 * * Modified Date : 14-07-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.receiptupload;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.receipt.constants.AllocationType;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ReceiptDetailHeaderListModelItemRenderer implements ListitemRenderer<ReceiptUploadDetail>, Serializable {

	private static final long serialVersionUID = 6906998807263283546L;

	public ReceiptDetailHeaderListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, ReceiptUploadDetail rcptDtl, int count) {

		Listcell lc;

		lc = new Listcell(rcptDtl.getRootId());
		lc.setParent(item);

		String reference = rcptDtl.getReference();

		if (StringUtils.isNotBlank(reference)) {
			reference = reference.toUpperCase();
		}

		lc = new Listcell(reference);
		lc.setParent(item);

		String receiptPurpose = rcptDtl.getReceiptPurpose();
		lc = new Listcell();
		if (StringUtils.equalsIgnoreCase(rcptDtl.getReceiptPurpose(), "SP")) {
			receiptPurpose = Labels.getLabel("label_ReceiptPurpose_SchedulePayment");
		} else if (StringUtils.equalsIgnoreCase(rcptDtl.getReceiptPurpose(), "EP")) {
			receiptPurpose = Labels.getLabel("label_ReceiptPurpose_PartialSettlement");
		} else if (StringUtils.equalsIgnoreCase(rcptDtl.getReceiptPurpose(), "ES")) {
			receiptPurpose = Labels.getLabel("label_ReceiptPurpose_EarlySettlement");
			lc.setStyle("font-weight:bold;");
		}
		lc.setLabel(receiptPurpose);
		lc.setParent(item);

		lc = new Listcell(CurrencyUtil.format(rcptDtl.getReceiptAmount(), PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		String alocType = rcptDtl.getAllocationType();
		if (StringUtils.equalsIgnoreCase(AllocationType.AUTO, rcptDtl.getAllocationType())) {
			alocType = Labels.getLabel("label_AllocationMethod_Auto");
		} else if (StringUtils.equalsIgnoreCase(AllocationType.MANUAL, rcptDtl.getAllocationType())) {
			alocType = Labels.getLabel("label_AllocationMethod_Manual");
		}
		lc = new Listcell(alocType);
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(rcptDtl.getReceivedDate()));
		lc.setParent(item);

		lc = new Listcell(rcptDtl.getProcessingStatus() == 2 ? "SUCCESS" : "FAILED");
		lc.setParent(item);

		lc = new Listcell(rcptDtl.getReason());
		lc.setParent(item);

	}
}