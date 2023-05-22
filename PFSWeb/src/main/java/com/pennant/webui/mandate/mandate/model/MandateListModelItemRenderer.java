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
 * * FileName : MandateListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * *
 * Modified Date : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.mandate.mandate.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.mandate.MandateUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class MandateListModelItemRenderer implements ListitemRenderer<Mandate>, Serializable {

	private static final long serialVersionUID = 1L;
	boolean multiselect = false;
	// String customMandateStatus = SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS);

	public MandateListModelItemRenderer(boolean multiselect) {
		super();
		this.multiselect = multiselect;
	}

	@Override
	public void render(Listitem item, Mandate mandate, int count) {
		if (multiselect) {
			Listbox listbox = (Listbox) item.getParent();
			listbox.setMultiple(true);
			listbox.setCheckmark(true);
		}
		Listcell lc;

		lc = new Listcell(String.valueOf(mandate.getMandateID()));
		lc.setParent(item);
		lc = new Listcell(mandate.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(mandate.getMandateType());
		lc.setParent(item);
		lc = new Listcell(mandate.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(mandate.getBankName());
		lc.setParent(item);
		lc = new Listcell(mandate.getAccNumber());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(mandate.getAccType(), MandateUtil.getAccountTypes()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(mandate.getMaxLimit(),
				CurrencyUtil.getFormat(mandate.getMandateCcy())));
		lc.setParent(item);

		lc = new Listcell();
		Checkbox ckActive = new Checkbox();
		ckActive.setChecked(mandate.isSecurityMandate());
		ckActive.setDisabled(true);
		ckActive.setParent(lc);
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(mandate.getExpiryDate()));
		lc.setParent(item);

		String status = PennantApplicationUtil.getLabelDesc(mandate.getStatus(), MandateUtil.getMandateStatus());

		// FIXME: Showing Custom Mandate status when Status is not there. Have To check with respective module Owner.
		/*
		 * if (StringUtils.isEmpty(status)) { if (StringUtils.isEmpty(mandate.getStatus()) ||
		 * StringUtils.equals(mandate.getStatus(), PennantConstants.List_Select)) { status =
		 * Labels.getLabel("label_Mandate_" + customMandateStatus); } }
		 */
		lc = new Listcell(status);
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(mandate.getInputDate()));
		lc.setParent(item);
		lc = new Listcell(mandate.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(mandate.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", mandate.getMandateID());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onMandateItemDoubleClicked");
	}
}