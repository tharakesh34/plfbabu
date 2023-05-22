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
 * * FileName : FinanceMainListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011
 * * * Modified Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class FinTaxUploadDetailItemRenderer implements ListitemRenderer<FinTaxUploadHeader>, Serializable {

	private static final long serialVersionUID = -4562142056572229437L;

	@Override
	public void render(Listitem item, FinTaxUploadHeader finTaxUploadHeader, int count) {

		Listcell lc;
		lc = new Listcell(String.valueOf(finTaxUploadHeader.getBatchReference()));
		lc.setParent(item);
		lc = new Listcell(finTaxUploadHeader.getFileName());
		lc.setParent(item);
		lc = new Listcell(
				DateUtil.format(finTaxUploadHeader.getBatchCreatedDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(finTaxUploadHeader.getNumberofRecords() + "");
		lc.setParent(item);
		lc = new Listcell(finTaxUploadHeader.getStatus());
		lc.setParent(item);
		lc = new Listcell(finTaxUploadHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finTaxUploadHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", finTaxUploadHeader.getBatchReference());
		if (!"Cancelled".equals(finTaxUploadHeader.getRecordStatus())
				&& !"Rejected".equals(finTaxUploadHeader.getRecordStatus())) {
			ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTaxUploadDetailItemDoubleClicked");
		}
	}
}