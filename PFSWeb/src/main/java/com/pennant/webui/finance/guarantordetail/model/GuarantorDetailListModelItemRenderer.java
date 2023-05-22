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
 * * FileName : GuarantorDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 10-09-2013 * * Modified Date : 10-09-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.guarantordetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class GuarantorDetailListModelItemRenderer implements ListitemRenderer<GuarantorDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public GuarantorDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, GuarantorDetail guarantorDetail, int count) {

		Listcell lc;
		lc = new Listcell();
		final Checkbox cbBankCustomer = new Checkbox();
		cbBankCustomer.setDisabled(true);
		cbBankCustomer.setChecked(guarantorDetail.isBankCustomer());
		lc.appendChild(cbBankCustomer);
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getGuarantorCIF() + "-" + guarantorDetail.getGuarantorCIFName());
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getGuarantorIDTypeName());
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getGuarantorIDNumber());
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getName());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatRate(guarantorDetail.getGuranteePercentage().doubleValue(), 2));
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getMobileNo());
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getEmailId());
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getGuarantorProofName());
		lc.setParent(item);
		lc = new Listcell(guarantorDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(guarantorDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", guarantorDetail.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onGuarantorDetailItemDoubleClicked");
	}
}