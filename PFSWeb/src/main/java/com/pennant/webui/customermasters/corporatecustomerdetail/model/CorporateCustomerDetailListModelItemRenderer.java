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
 * * FileName : CorporateCustomerDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 01-12-2011 * * Modified Date : 01-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.corporatecustomerdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CorporateCustomerDetailListModelItemRenderer
		implements ListitemRenderer<CorporateCustomerDetail>, Serializable {

	private static final long serialVersionUID = 2495491623811019430L;

	public CorporateCustomerDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CorporateCustomerDetail corporateCustomerDetail, int count) {

		Listcell lc;
		lc = new Listcell(corporateCustomerDetail.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(corporateCustomerDetail.getName());
		lc.setParent(item);
		lc = new Listcell(corporateCustomerDetail.getPhoneNumber());
		lc.setParent(item);
		lc = new Listcell(corporateCustomerDetail.getEmailId());
		lc.setParent(item);
		lc = new Listcell(corporateCustomerDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(corporateCustomerDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", corporateCustomerDetail.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCorporateCustomerDetailItemDoubleClicked");
	}
}